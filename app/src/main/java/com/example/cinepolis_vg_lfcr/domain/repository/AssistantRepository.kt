package com.example.cinepolis_vg_lfcr.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Domain contract for the assistant/chat. Implemented in the data layer.
 */
interface AssistantRepository {

    /** Create a conversation; returns conversation id on success. */
    suspend fun createConversation(): Result<String>

    /** Send a text message. */
    suspend fun sendMessage(text: String): Result<Unit>

    /** Stream of bot message texts from SSE events. */
    fun botMessageEvents(): Flow<String>
}
