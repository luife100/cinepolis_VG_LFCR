package com.example.cinepolis_vg_lfcr.ui.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinepolis_vg_lfcr.domain.usecase.SyncGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoadingState {
    data object Idle : LoadingState()
    data object Loading : LoadingState()
    data object Success : LoadingState()
    data class Error(val message: String) : LoadingState()
}

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val syncGamesUseCase: SyncGamesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val state: StateFlow<LoadingState> = _state.asStateFlow()

    init {
        syncCatalog()
    }

    fun syncCatalog() {
        viewModelScope.launch {
            _state.value = LoadingState.Loading
            syncGamesUseCase(forceRefresh = false)
                .onSuccess {
                    _state.value = LoadingState.Success
                }
                .onFailure { e ->
                    _state.value = LoadingState.Error(e.message ?: "Unknown error")
                }
        }
    }
}
