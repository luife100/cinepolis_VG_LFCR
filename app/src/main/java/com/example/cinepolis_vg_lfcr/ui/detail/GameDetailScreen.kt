package com.example.cinepolis_vg_lfcr.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.ui.navigation.Routes

@Composable
fun GameDetailScreen(
    navController: NavController,
    viewModel: GameDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            viewModel.clearNavigateBack()
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading && state.game == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null && state.game == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error!!,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            state.game != null -> {
                GameDetailContent(
                    game = state.game!!,
                    onClose = { navController.popBackStack() },
                    onEdit = { updated -> viewModel.updateGame(updated) },
                    onDelete = { viewModel.deleteGame() },
                    error = state.error
                )
            }
        }
    }
}

@Composable
fun GameDetailContent(
    game: Game,
    onClose: () -> Unit,
    onEdit: (Game) -> Unit,
    onDelete: () -> Unit,
    error: String?
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditGameDialog(
            game = game,
            onDismiss = { showEditDialog = false },
            onConfirm = { updated ->
                onEdit(updated)
                showEditDialog = false
            }
        )
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete game?") },
            text = { Text("This will remove \"${game.title}\" from your catalog. You can refresh the list to get it back from the server.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.widthIn(max = 400.dp),
        shape = androidx.compose.material3.MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
            error?.let {
                Text(
                    text = it,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            AsyncImage(
                model = game.thumbnail,
                contentDescription = game.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = game.title,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.shortDescription,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Genre: ${game.genre}",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Platform: ${game.platform}",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Release: ${game.releaseDate}",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Edit")
                }
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun EditGameDialog(
    game: Game,
    onDismiss: () -> Unit,
    onConfirm: (Game) -> Unit
) {
    var title by remember(game.title) { mutableStateOf(game.title) }
    var shortDescription by remember(game.shortDescription) { mutableStateOf(game.shortDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit game") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = shortDescription,
                    onValueChange = { shortDescription = it },
                    label = { Text("Short description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    game.copy(
                        title = title.trim().ifBlank { game.title },
                        shortDescription = shortDescription.trim().ifBlank { game.shortDescription }
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

