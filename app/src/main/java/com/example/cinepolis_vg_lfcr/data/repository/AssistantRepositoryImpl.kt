package com.example.cinepolis_vg_lfcr.data.repository

import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressApi
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressConfig
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressSseClient
import com.example.cinepolis_vg_lfcr.data.remote.botpress.CreateUserRequest
import com.example.cinepolis_vg_lfcr.data.remote.botpress.SendMessageRequest
import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class AssistantRepositoryImpl @Inject constructor(
    private val api: BotpressApi,
    private val sseClient: BotpressSseClient
) : AssistantRepository {

    private var userKey: String? = null

    override suspend fun createConversation(): Result<String> = runCatching {
        val key = userKey ?: run {
            val userResponse = api.createUser(
                CreateUserRequest(id = "app-${UUID.randomUUID()}", name = "App User")
            )
            if (!userResponse.isSuccessful) {
                throw Exception("Create user failed: ${userResponse.code()} ${userResponse.message()}")
            }
            val userBody = userResponse.body() ?: throw Exception("Empty create user response")
            userBody.resolveKey() ?: BotpressConfig.API_KEY
        }.also { userKey = it }
        val response = api.createConversation(userKey!!)
        if (!response.isSuccessful) {
            throw Exception("Create conversation failed: ${response.code()} ${response.message()}")
        }
        val body = response.body() ?: throw Exception("Empty conversation response")
        body.resolveId() ?: "conversation-${UUID.randomUUID()}"
    }

    override suspend fun sendMessage(text: String): Result<Unit> = runCatching {
        val key = userKey ?: throw IllegalStateException("No user key; call createConversation first")
        val response = api.sendMessage(key, SendMessageRequest(text = text))
        if (!response.isSuccessful) throw Exception("Send message failed: ${response.code()}")
    }

    override fun botMessageEvents(): Flow<String> = sseClient
        .events(BotpressConfig.WEBHOOK_ID, userKey ?: "")
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
