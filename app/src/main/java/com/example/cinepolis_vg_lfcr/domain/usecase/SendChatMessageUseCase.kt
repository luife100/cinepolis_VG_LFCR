package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val repository: AssistantRepository
) {
    suspend operator fun invoke(text: String): Result<Unit> = repository.sendMessage(text)
}
