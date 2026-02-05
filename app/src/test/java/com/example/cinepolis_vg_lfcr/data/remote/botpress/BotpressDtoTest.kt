package com.example.cinepolis_vg_lfcr.data.remote.botpress

import org.junit.Assert.assertEquals
import org.junit.Test

class BotpressDtoTest {

    @Test
    fun CreateUserResponse_resolveKey_returnsKeyWhenPresent() {
        val r = CreateUserResponse(key = "jwt-123")
        assertEquals("jwt-123", r.resolveKey())
    }

    @Test
    fun CreateUserResponse_resolveKey_returnsUserKeyWhenKeyNull() {
        val r = CreateUserResponse(userKey = "uk-456")
        assertEquals("uk-456", r.resolveKey())
    }

    @Test
    fun CreateUserResponse_resolveKey_returnsAccessKeyWhenOthersNull() {
        val r = CreateUserResponse(accessKey = "ak-789")
        assertEquals("ak-789", r.resolveKey())
    }

    @Test
    fun CreateUserResponse_resolveKey_returnsUserIdWhenOthersNull() {
        val r = CreateUserResponse(user = UserDto(id = "user-1"))
        assertEquals("user-1", r.resolveKey())
    }

    @Test
    fun CreateUserResponse_resolveKey_returnsNullWhenAllNull() {
        val r = CreateUserResponse()
        assertEquals(null, r.resolveKey())
    }

    @Test
    fun ConversationResponse_resolveId_returnsConversationIdFirst() {
        val r = ConversationResponse(conversation = ConversationDto(id = "conv-1"))
        assertEquals("conv-1", r.resolveId())
    }

    @Test
    fun ConversationResponse_resolveId_returnsRootIdWhenConversationNull() {
        val r = ConversationResponse(id = "root-id")
        assertEquals("root-id", r.resolveId())
    }

    @Test
    fun ConversationResponse_resolveId_returnsConversationIdFieldWhenOthersNull() {
        val r = ConversationResponse(conversationId = "cid-1")
        assertEquals("cid-1", r.resolveId())
    }

    @Test
    fun ConversationResponse_resolveId_returnsNullWhenAllNull() {
        val r = ConversationResponse()
        assertEquals(null, r.resolveId())
    }
}
