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
    @SerializedName("key") val key: String? = null,
    @SerializedName("userKey") val userKey: String? = null,
    @SerializedName("accessKey") val accessKey: String? = null
) {
    /** Resolve key from any known field (API may use different names). */
    fun resolveKey(): String? = key ?: userKey ?: accessKey ?: user?.id
}

data class UserDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null
)

/** Request body for POST .../conversations (optional id). */
data class CreateConversationRequest(
    @SerializedName("id") val id: String? = null
)

/** Response from POST .../conversations; API may nest id or put at root. */
data class ConversationResponse(
    @SerializedName("conversation") val conversation: ConversationDto? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("conversationId") val conversationId: String? = null
) {
    /** Resolve conversation id from any known field. */
    fun resolveId(): String? =
        conversation?.id ?: id ?: conversationId
}

data class ConversationDto(
    @SerializedName("id") val id: String? = null
)

/** Request body for POST .../messages: requires conversationId and payload. */
data class SendMessageRequest(
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("payload") val payload: MessagePayload
)

data class MessagePayload(
    @SerializedName("type") val type: String = "text",
    @SerializedName("text") val text: String
)

/** SSE event: type + data (Botpress sends message_created with data.payload.text). */
data class BotpressEvent(
    @SerializedName("type") val type: String? = null,
    @SerializedName("data") val data: SseMessageData? = null,
    @SerializedName("payload") val payload: EventPayload? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("text") val text: String? = null
)

/** Nested under event.data for message_created. */
data class SseMessageData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("conversationId") val conversationId: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("payload") val payload: SseMessagePayload? = null,
    @SerializedName("isBot") val isBot: Boolean? = null
)

data class SseMessagePayload(
    @SerializedName("type") val type: String? = null,
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
