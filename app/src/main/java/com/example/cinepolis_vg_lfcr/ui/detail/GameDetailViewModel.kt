package com.example.cinepolis_vg_lfcr.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.usecase.GetGameByIdUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGameDeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.UpdateGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameDetailUiState(
    val game: Game? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val navigateBack: Boolean = false
)

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGameByIdUseCase: GetGameByIdUseCase,
    private val updateGameUseCase: UpdateGameUseCase,
    private val markGameDeletedUseCase: MarkGameDeletedUseCase
) : ViewModel() {

    private val gameId: Int = savedStateHandle["gameId"] ?: 0
    private val _state = MutableStateFlow(GameDetailUiState())
    val state: StateFlow<GameDetailUiState> = _state.asStateFlow()

    init {
        loadGame()
    }

    private fun loadGame() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val game = getGameByIdUseCase(gameId)
            _state.update {
                it.copy(
                    game = game,
                    isLoading = false,
                    error = if (game == null) "Game not found" else null
                )
            }
        }
    }

    fun updateGame(game: Game) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching { updateGameUseCase(game) }
                .onSuccess { _state.update { it.copy(isLoading = false, game = game) } }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Update failed"
                        )
                    }
                }
        }
    }

    fun deleteGame() {
        viewModelScope.launch {
            runCatching { markGameDeletedUseCase(gameId) }
                .onSuccess { _state.update { it.copy(navigateBack = true) } }
                .onFailure { e ->
                    _state.update {
                        it.copy(error = e.message ?: "Delete failed")
                    }
                }
        }
    }

    fun clearNavigateBack() {
        _state.update { it.copy(navigateBack = false) }
    }
}
