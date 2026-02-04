package com.example.cinepolis_vg_lfcr.data.repository

import android.util.Log
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressApi
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressConfig
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressSseClient
import com.example.cinepolis_vg_lfcr.data.remote.botpress.CreateUserRequest
import com.example.cinepolis_vg_lfcr.data.remote.botpress.MessagePayload
import com.example.cinepolis_vg_lfcr.data.remote.botpress.SendMessageRequest
import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

private const val BOTPRESS_LOG_TAG = "Botpress"

class AssistantRepositoryImpl @Inject constructor(
    private val api: BotpressApi,
    private val sseClient: BotpressSseClient
) : AssistantRepository {

    private var userKey: String? = null
    private var conversationId: String? = null

    override suspend fun createConversation(): Result<String> = runCatching {
        val key = userKey ?: run {
            val userResponse = api.createUser(
                CreateUserRequest(id = "app-${UUID.randomUUID()}", name = "App User")
            )
            Log.d(BOTPRESS_LOG_TAG, "createUser: code=${userResponse.code()} body=${userResponse.body()}")
            if (!userResponse.isSuccessful) {
                throw Exception("Create user failed: ${userResponse.code()} ${userResponse.message()}")
            }
            val userBody = userResponse.body() ?: throw Exception("Empty create user response")
            userBody.resolveKey() ?: BotpressConfig.API_KEY
        }.also { userKey = it }
        val response = api.createConversation(userKey!!)
        Log.d(BOTPRESS_LOG_TAG, "createConversation: code=${response.code()} body=${response.body()}")
        if (!response.isSuccessful) {
            throw Exception("Create conversation failed: ${response.code()} ${response.message()}")
        }
        val body = response.body() ?: throw Exception("Empty conversation response")
        val convId = body.resolveId() ?: "conversation-${UUID.randomUUID()}"
        conversationId = convId
        Log.d(BOTPRESS_LOG_TAG, "conversationId=$convId")
        convId
    }

    override suspend fun sendMessage(text: String): Result<Unit> = runCatching {
        val key = userKey ?: throw IllegalStateException("No user key; call createConversation first")
        val convId = conversationId ?: throw IllegalStateException("No conversation; call createConversation first")
        val response = api.sendMessage(key, SendMessageRequest(conversationId = convId, payload = MessagePayload(text = text)))
        Log.d(BOTPRESS_LOG_TAG, "sendMessage: code=${response.code()} message=${response.message()}" + if (!response.isSuccessful) " errorBody=${response.errorBody()?.string()}" else " ok")
        if (!response.isSuccessful) throw Exception("Send message failed: ${response.code()}")
    }

    override fun botMessageEvents(): Flow<String> = sseClient
        .listenConversation(BotpressConfig.WEBHOOK_ID, conversationId ?: "", userKey ?: "")
        .map { event ->
            // Botpress SSE: type=message_created, data.payload.text; only emit bot messages
            if (event.data?.isBot == true) {
                event.data.payload?.text ?: ""
            } else {
                event.payload?.text
                    ?: event.payload?.payload?.text
                    ?: event.text
                    ?: event.message
                    ?: ""
            }
        }
        .catch { _ -> emit("") }
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}
