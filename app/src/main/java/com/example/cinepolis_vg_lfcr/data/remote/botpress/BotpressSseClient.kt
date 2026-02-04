package com.example.cinepolis_vg_lfcr.data.remote.botpress

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.currentCoroutineContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

/**
 * Listens to Botpress SSE events endpoint and parses "data:" lines as [BotpressEvent].
 * Run the returned flow in a coroutine scope; the connection stays open until cancelled.
 */
private const val TAG = "Botpress"

class BotpressSseClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    /**
     * Listens to conversation SSE: GET .../conversations/{conversationId}/listen
     * Use the conversation id from createConversation so bot replies are received.
     */
    fun listenConversation(webhookId: String, conversationId: String, userKey: String): Flow<BotpressEvent> = callbackFlow {
        if (conversationId.isBlank()) {
            awaitClose { }
            return@callbackFlow
        }
        val url = "${BotpressConfig.BASE_URL}$webhookId/conversations/$conversationId/listen"
        Log.d(TAG, "SSE listen url=$url")
        val request = Request.Builder()
            .url(url)
            .addHeader(BotpressConfig.HEADER_USER_KEY, userKey)
            .get()
            .build()

        val call = okHttpClient.newCall(request)
        @Suppress("BlockingMethodInNonBlockingContext")
        val response = withContext(Dispatchers.IO) { call.execute() }
        Log.d(TAG, "SSE connect: code=${response.code} message=${response.message}")
        if (!response.isSuccessful) {
            val errBody = response.body?.string() ?: ""
            Log.e(TAG, "SSE failed body=$errBody")
            close(Throwable("SSE failed: ${response.code} ${response.message}"))
            awaitClose { }
            return@callbackFlow
        }
        val body = response.body
        if (body == null) {
            Log.e(TAG, "SSE empty body")
            close(Throwable("SSE empty body"))
            awaitClose { }
            return@callbackFlow
        }
        val job = CoroutineScope(currentCoroutineContext()).launch {
            withContext(Dispatchers.IO) {
                body.byteStream().bufferedReader(Charsets.UTF_8).use { reader ->
                    while (currentCoroutineContext().isActive) {
                        val line = reader.readLine() ?: break
                        if (line.startsWith("data:")) {
                            val json = line.removePrefix("data:").trim()
                            Log.d(TAG, "SSE data: $json")
                            if (json.isEmpty() || json == "[DONE]") continue
                            try {
                                val event = gson.fromJson(json, BotpressEvent::class.java)
                                Log.d(TAG, "SSE parsed event: type=${event.type} payload=${event.payload} text=${event.text} message=${event.message}")
                                trySend(event)
                            } catch (e: Exception) {
                                Log.w(TAG, "SSE parse failed: $json", e)
                            }
                        }
                    }
                }
            }
        }
        awaitClose {
            call.cancel()
            job.cancel()
        }
    }
}
