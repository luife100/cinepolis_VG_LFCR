package com.example.cinepolis_vg_lfcr.data.remote.botpress

import com.google.gson.annotations.SerializedName

/** Request body for POST .../users (createUser). */
data class CreateUserRequest(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("pictureUrl") val pictureUrl: String? = null,
    @SerializedName("profile") val profile: String? = null
)

/** Response from POST .../users; the key is used as x-user-key for subsequent calls. */
data class CreateUserResponse(
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("key") val key: String? = null
)

data class UserDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null
)

/** Request body for POST .../conversations (optional id). */
data class CreateConversationRequest(
    @SerializedName("id") val id: String? = null
)

/** Response from POST .../conversations: { "conversation": { "id": "..." } } */
data class ConversationResponse(
    @SerializedName("conversation") val conversation: ConversationDto? = null
)

data class ConversationDto(
    @SerializedName("id") val id: String? = null
)

/** Request body for POST .../messages */
data class SendMessageRequest(
    @SerializedName("type") val type: String = "text",
    @SerializedName("text") val text: String
)

/** SSE event payload - adapt fields based on actual Botpress event shape */
data class BotpressEvent(
    @SerializedName("type") val type: String? = null,
    @SerializedName("payload") val payload: EventPayload? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("text") val text: String? = null
)

data class EventPayload(
    @SerializedName("type") val type: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("payload") val payload: TextPayload? = null
)

data class TextPayload(
    @SerializedName("text") val text: String? = null
)
