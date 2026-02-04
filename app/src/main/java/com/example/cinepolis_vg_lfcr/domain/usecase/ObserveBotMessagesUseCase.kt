package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBotMessagesUseCase @Inject constructor(
    private val repository: AssistantRepository
) {
    operator fun invoke(): Flow<String> = repository.botMessageEvents()
}
