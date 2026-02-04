package com.example.cinepolis_vg_lfcr.data.remote.botpress

import com.google.gson.annotations.SerializedName

/** Response from POST .../conversations */
data class ConversationResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("conversationId") val conversationId: String? = null
) {
    fun resolveId(): String? = id ?: conversationId
}

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
