package com.example.cinepolis_vg_lfcr.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinepolis_vg_lfcr.domain.usecase.CreateConversationUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.ObserveBotMessagesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean
)

data class AssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val conversationReady: Boolean = false
)

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val createConversationUseCase: CreateConversationUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val observeBotMessagesUseCase: ObserveBotMessagesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AssistantUiState())
    val state: StateFlow<AssistantUiState> = _state.asStateFlow()

    private var messageIdCounter = 0
    private fun nextId() = "msg_${messageIdCounter++}"

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            createConversationUseCase()
                .onSuccess {
                    _state.update {
                        it.copy(
                            conversationReady = true,
                            isLoading = false,
                            error = null
                        )
                    }
                    startListeningToBot()
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to start conversation"
                        )
                    }
                }
        }
    }

    private fun startListeningToBot() {
        observeBotMessagesUseCase()
            .onEach { text ->
                _state.update { state ->
                    state.copy(
                        messages = state.messages + ChatMessage(
                            id = nextId(),
                            text = text,
                            isFromUser = false
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateInputText(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isEmpty() || !_state.value.conversationReady) return
        _state.update {
            it.copy(
                inputText = "",
                messages = it.messages + ChatMessage(
                    id = nextId(),
                    text = text,
                    isFromUser = true
                )
            )
        }
        viewModelScope.launch {
            sendChatMessageUseCase(text)
                .onFailure { e ->
                    _state.update {
                        it.copy(error = e.message ?: "Failed to send")
                    }
                }
        }
    }
}
