package com.example.cinepolis_vg_lfcr.data.repository

import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressApi
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressConfig
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressSseClient
import com.example.cinepolis_vg_lfcr.data.remote.botpress.SendMessageRequest
import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AssistantRepositoryImpl @Inject constructor(
    private val api: BotpressApi,
    private val sseClient: BotpressSseClient
) : AssistantRepository {

    override suspend fun createConversation(): Result<String> = runCatching {
        val response = api.createConversation()
        if (!response.isSuccessful) throw Exception("Create conversation failed: ${response.code()}")
        val body = response.body() ?: throw Exception("Empty conversation response")
        body.resolveId() ?: throw Exception("No conversation id in response")
    }

    override suspend fun sendMessage(text: String): Result<Unit> = runCatching {
        val response = api.sendMessage(SendMessageRequest(text = text))
        if (!response.isSuccessful) throw Exception("Send message failed: ${response.code()}")
    }

    override fun botMessageEvents(): Flow<String> = sseClient
        .events(BotpressConfig.WEBHOOK_ID, BotpressConfig.API_KEY)
        .map { event ->
            event.payload?.text
                ?: event.payload?.payload?.text
                ?: event.text
                ?: event.message
                ?: ""
        }
        .catch { _ -> emit("") }
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}
