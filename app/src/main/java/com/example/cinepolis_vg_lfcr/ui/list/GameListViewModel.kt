package com.example.cinepolis_vg_lfcr.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.usecase.GetDeletedGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.GetFavoriteGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.GetGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGameDeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGamesDeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGamesFavoriteUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGamesUndeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGamesUnfavoriteUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SearchDeletedGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SearchFavoriteGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SearchGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SyncGamesUseCase
import com.example.cinepolis_vg_lfcr.data.preferences.ViewModePreferences
import com.example.cinepolis_vg_lfcr.domain.usecase.UpdateGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ViewMode { List, Grid }

enum class ListType { Main, Favorites, Deleted, Assistant }

data class GameListUiState(
    val games: List<Game> = emptyList(),
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
    val refreshError: String? = null,
    val selectedGame: Game? = null,
    val viewMode: ViewMode = ViewMode.List,
    val selectionMode: Boolean = false,
    val selectedGameIds: Set<Int> = emptySet(),
    val showBulkDeleteConfirmation: Boolean = false,
    val listType: ListType = ListType.Main
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameListViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase,
    private val searchGamesUseCase: SearchGamesUseCase,
    private val getFavoriteGamesUseCase: GetFavoriteGamesUseCase,
    private val searchFavoriteGamesUseCase: SearchFavoriteGamesUseCase,
    private val getDeletedGamesUseCase: GetDeletedGamesUseCase,
    private val searchDeletedGamesUseCase: SearchDeletedGamesUseCase,
    private val syncGamesUseCase: SyncGamesUseCase,
    private val updateGameUseCase: UpdateGameUseCase,
    private val markGameDeletedUseCase: MarkGameDeletedUseCase,
    private val markGamesDeletedUseCase: MarkGamesDeletedUseCase,
    private val markGamesUndeletedUseCase: MarkGamesUndeletedUseCase,
    private val markGamesFavoriteUseCase: MarkGamesFavoriteUseCase,
    private val markGamesUnfavoriteUseCase: MarkGamesUnfavoriteUseCase,
    private val viewModePreferences: ViewModePreferences
) : ViewModel() {

    private val _state = MutableStateFlow(GameListUiState())
    val state: StateFlow<GameListUiState> = _state.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")
    private val listTypeFlow = MutableStateFlow(ListType.Main)

    init {
        combine(listTypeFlow, searchQueryFlow) { listType, query -> listType to query }
            .flatMapLatest { (listType, query) ->
                when (listType) {
                    ListType.Main ->
                        if (query.isBlank()) getGamesUseCase() else searchGamesUseCase(query)
                    ListType.Favorites ->
                        if (query.isBlank()) getFavoriteGamesUseCase() else searchFavoriteGamesUseCase(query)
                    ListType.Deleted ->
                        if (query.isBlank()) getDeletedGamesUseCase() else searchDeletedGamesUseCase(query)
                    ListType.Assistant ->
                        flowOf(emptyList())
                }
            }
            .onEach { games ->
                _state.update { it.copy(games = games) }
            }
            .launchIn(viewModelScope)

        listTypeFlow
            .onEach { type ->
                _state.update { it.copy(listType = type) }
            }
            .launchIn(viewModelScope)

        viewModePreferences.viewModeValue
            .onEach { value ->
                val mode = if (value == "Grid") ViewMode.Grid else ViewMode.List
                _state.update { it.copy(viewMode = mode) }
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
        _state.update { it.copy(searchQuery = query) }
    }

    fun onPullToRefresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, refreshError = null) }
            syncGamesUseCase(forceRefresh = true)
                .onSuccess {
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            refreshError = e.message ?: "Refresh failed"
                        )
                    }
                }
        }
    }

    fun setSelectedGame(game: Game?) {
        _state.update { it.copy(selectedGame = game) }
    }

    fun setListType(type: ListType) {
        listTypeFlow.value = type
    }

    fun setViewMode(mode: ViewMode) {
        _state.update { it.copy(viewMode = mode) }
        viewModelScope.launch {
            viewModePreferences.setViewModeValue(
                when (mode) { ViewMode.List -> "List"; ViewMode.Grid -> "Grid" }
            )
        }
    }

    fun clearSelection() {
        _state.update { it.copy(selectedGame = null) }
    }

    fun updateGame(updated: Game) {
        viewModelScope.launch {
            runCatching { updateGameUseCase(updated) }
                .onSuccess { _state.update { it.copy(selectedGame = updated) } }
        }
    }

    fun deleteGame(id: Int) {
        viewModelScope.launch {
            runCatching { markGameDeletedUseCase(id) }
                .onSuccess { clearSelection() }
        }
    }

    fun enterSelectionMode(initialId: Int? = null) {
        _state.update {
            it.copy(
                selectionMode = true,
                selectedGameIds = if (initialId != null) setOf(initialId) else emptySet()
            )
        }
    }

    fun exitSelectionMode() {
        _state.update {
            it.copy(selectionMode = false, selectedGameIds = emptySet())
        }
    }

    fun toggleGameSelection(id: Int) {
        _state.update { state ->
            val newSet = if (state.selectedGameIds.contains(id)) {
                state.selectedGameIds - id
            } else {
                state.selectedGameIds + id
            }
            state.copy(selectedGameIds = newSet)
        }
    }

    fun showBulkDeleteConfirmation() {
        if (_state.value.selectedGameIds.isNotEmpty()) {
            _state.update { it.copy(showBulkDeleteConfirmation = true) }
        }
    }

    fun dismissBulkDeleteConfirmation() {
        _state.update { it.copy(showBulkDeleteConfirmation = false) }
    }

    fun bulkDeleteSelected() {
        val ids = _state.value.selectedGameIds.toList()
        if (ids.isEmpty()) return
        _state.update { it.copy(showBulkDeleteConfirmation = false) }
        viewModelScope.launch {
            runCatching { markGamesDeletedUseCase(ids) }
                .onSuccess { exitSelectionMode() }
        }
    }

    fun toggleGameFavorite(game: Game) {
        viewModelScope.launch {
            val ids = listOf(game.id)
            if (game.isFavorite) {
                runCatching { markGamesUnfavoriteUseCase(ids) }
            } else {
                runCatching { markGamesFavoriteUseCase(ids) }
            }
        }
    }

    fun bulkMarkFavoriteSelected() {
        val ids = _state.value.selectedGameIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            runCatching { markGamesFavoriteUseCase(ids) }
                .onSuccess { exitSelectionMode() }
        }
    }

    fun bulkUnfavoriteSelected() {
        val ids = _state.value.selectedGameIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            runCatching { markGamesUnfavoriteUseCase(ids) }
                .onSuccess { exitSelectionMode() }
        }
    }

    fun bulkRestoreSelected() {
        val ids = _state.value.selectedGameIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            runCatching { markGamesUndeletedUseCase(ids) }
                .onSuccess { exitSelectionMode() }
        }
    }
}
