package com.example.cinepolis_vg_lfcr.data.remote.botpress

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BotpressApi {

    /** No auth; response contains key to use as x-user-key. */
    @POST("users")
    suspend fun createUser(@Body body: CreateUserRequest = CreateUserRequest()): Response<CreateUserResponse>

    /** Requires x-user-key header (from createUser response). */
    @POST("conversations")
    suspend fun createConversation(
        @Header("x-user-key") userKey: String,
        @Body body: CreateConversationRequest = CreateConversationRequest()
    ): Response<ConversationResponse>

    /** Requires x-user-key header. */
    @POST("messages")
    suspend fun sendMessage(
        @Header("x-user-key") userKey: String,
        @Body body: SendMessageRequest
    ): Response<Unit>
}
