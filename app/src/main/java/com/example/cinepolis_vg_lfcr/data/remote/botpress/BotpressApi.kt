package com.example.cinepolis_vg_lfcr.data.remote.botpress

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BotpressApi {

    @POST("conversations")
    suspend fun createConversation(): Response<ConversationResponse>

    @POST("messages")
    suspend fun sendMessage(@Body body: SendMessageRequest): Response<Unit>
}
