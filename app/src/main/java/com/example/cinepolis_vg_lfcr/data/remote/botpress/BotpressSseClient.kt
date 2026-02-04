package com.example.cinepolis_vg_lfcr.data.remote.botpress

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
class BotpressSseClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    fun events(webhookId: String, apiKey: String): Flow<BotpressEvent> = callbackFlow {
        val url = "${BotpressConfig.BASE_URL}$webhookId/events"
        val request = Request.Builder()
            .url(url)
            .addHeader(BotpressConfig.HEADER_USER_KEY, apiKey)
            .get()
            .build()

        val call = okHttpClient.newCall(request)
        @Suppress("BlockingMethodInNonBlockingContext")
        val response = withContext(Dispatchers.IO) { call.execute() }
        if (!response.isSuccessful) {
            close(Throwable("SSE failed: ${response.code} ${response.message}"))
            awaitClose { }
            return@callbackFlow
        }
        val body = response.body
        if (body == null) {
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
                            if (json.isEmpty() || json == "[DONE]") continue
                            try {
                                val event = gson.fromJson(json, BotpressEvent::class.java)
                                trySend(event)
                            } catch (_: Exception) { /* skip unparseable */ }
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
