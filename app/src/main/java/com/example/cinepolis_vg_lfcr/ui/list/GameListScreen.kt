package com.example.cinepolis_vg_lfcr.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.ui.detail.GameDetailContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    viewModel: GameListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val listTitle = when (state.listType) {
        ListType.Main -> "Game Catalog"
        ListType.Favorites -> "Favorites"
        ListType.Deleted -> "Deleted"
    }
    Scaffold(
        topBar = {
            Text(
                text = listTitle,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ViewList, contentDescription = null) },
                    label = { Text("List") },
                    selected = state.listType == ListType.Main,
                    onClick = { viewModel.setListType(ListType.Main) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Favorites") },
                    selected = state.listType == ListType.Favorites,
                    onClick = { viewModel.setListType(ListType.Favorites) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                    label = { Text("Deleted") },
                    selected = state.listType == ListType.Deleted,
                    onClick = { viewModel.setListType(ListType.Deleted) }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search by name or category") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                    )
                )
                IconButton(
                    onClick = {
                        viewModel.setViewMode(
                            if (state.viewMode == ViewMode.List) ViewMode.Grid else ViewMode.List
                        )
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (state.viewMode == ViewMode.List) Icons.Default.GridView else Icons.Default.ViewList,
                        contentDescription = if (state.viewMode == ViewMode.List) "Switch to grid view" else "Switch to list view"
                    )
                }
            }

            if (state.selectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.selectedGameIds.isEmpty()) "Select items" else "${state.selectedGameIds.size} selected",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (state.selectedGameIds.isNotEmpty()) {
                            if (state.listType == ListType.Favorites) {
                                Button(
                                    onClick = viewModel::bulkUnfavoriteSelected,
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text("Unfavorite")
                                }
                            } else {
                                Button(
                                    onClick = viewModel::bulkMarkFavoriteSelected,
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text("Favorites")
                                }
                            }
                            Button(
                                onClick = viewModel::showBulkDeleteConfirmation,
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Delete")
                            }
                        }
                        TextButton(onClick = viewModel::exitSelectionMode) {
                            Text("Cancel")
                        }
                    }
                }
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = viewModel::onPullToRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp)
            ) {
                state.refreshError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                when (state.viewMode) {
                    ViewMode.List -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.games,
                            key = { it.id }
                        ) { game ->
                            GameItem(
                                game = game,
                                isSelectionMode = state.selectionMode,
                                isSelected = state.selectedGameIds.contains(game.id),
                                onClick = {
                                    if (state.selectionMode) viewModel.toggleGameSelection(game.id)
                                    else viewModel.setSelectedGame(game)
                                },
                                onLongClick = { viewModel.enterSelectionMode(game.id) },
                                onFavoriteClick = { viewModel.toggleGameFavorite(game) }
                            )
                        }
                    }
                    ViewMode.Grid -> LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.games,
                            key = { it.id }
                        ) { game ->
                            GridGameItem(
                                game = game,
                                isSelectionMode = state.selectionMode,
                                isSelected = state.selectedGameIds.contains(game.id),
                                onClick = {
                                    if (state.selectionMode) viewModel.toggleGameSelection(game.id)
                                    else viewModel.setSelectedGame(game)
                                },
                                onLongClick = { viewModel.enterSelectionMode(game.id) },
                                onFavoriteClick = { viewModel.toggleGameFavorite(game) }
                            )
                        }
                    }
                }
            }

            if (state.showBulkDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = viewModel::dismissBulkDeleteConfirmation,
                    title = { Text("Delete games?") },
                    text = {
                        Text(
                            "Delete ${state.selectedGameIds.size} game(s)? They will be hidden from the list."
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = viewModel::bulkDeleteSelected,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::dismissBulkDeleteConfirmation) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (state.selectedGame != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { viewModel.clearSelection() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { /* consume clicks so card doesn't close */ }
                    ) {
                        state.selectedGame?.let{ selectedGame ->
                            GameDetailContent(
                            game = selectedGame,
                            onClose = viewModel::clearSelection,
                            onEdit = { viewModel.updateGame(it) },
                            onDelete = { viewModel.deleteGame(selectedGame.id) },
                            error = null
                        )
                    }
                    }
                }
            }
        }
    }
}

@Composable
private fun GridGameItem(
    game: Game,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (isSelectionMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = if (isSelected) "Selected" else "Not selected",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = game.thumbnail,
                    contentDescription = game.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (game.isFavorite) "Unfavorite" else "Favorite",
                        tint = if (game.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun GameItem(
    game: Game,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelectionMode) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = if (isSelected) "Selected" else "Not selected"
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                AsyncImage(
                model = game.thumbnail,
                contentDescription = game.title,
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = game.genre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = game.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (game.isFavorite) "Unfavorite" else "Favorite",
                    tint = if (game.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        }
    }
}
