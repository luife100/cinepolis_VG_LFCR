package com.example.cinepolis_vg_lfcr.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.usecase.GetGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGameDeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SearchGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SyncGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.UpdateGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameListUiState(
    val games: List<Game> = emptyList(),
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
    val refreshError: String? = null,
    val selectedGame: Game? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameListViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase,
    private val searchGamesUseCase: SearchGamesUseCase,
    private val syncGamesUseCase: SyncGamesUseCase,
    private val updateGameUseCase: UpdateGameUseCase,
    private val markGameDeletedUseCase: MarkGameDeletedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameListUiState())
    val state: StateFlow<GameListUiState> = _state.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        searchQueryFlow
            .flatMapLatest { query ->
                if (query.isBlank()) getGamesUseCase()
                else searchGamesUseCase(query)
            }
            .onEach { games ->
                _state.update { it.copy(games = games) }
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
}
