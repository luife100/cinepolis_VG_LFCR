package com.example.cinepolis_vg_lfcr.data.repository

import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressApi
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressEvent
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressSseClient
import com.example.cinepolis_vg_lfcr.data.remote.botpress.ConversationDto
import com.example.cinepolis_vg_lfcr.data.remote.botpress.ConversationResponse
import com.example.cinepolis_vg_lfcr.data.remote.botpress.CreateUserResponse
import com.example.cinepolis_vg_lfcr.data.remote.botpress.SseMessageData
import com.example.cinepolis_vg_lfcr.data.remote.botpress.SseMessagePayload
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AssistantRepositoryImplTest {

    private lateinit var api: BotpressApi
    private lateinit var sseClient: BotpressSseClient
    private lateinit var repository: AssistantRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        sseClient = mockk()
        repository = AssistantRepositoryImpl(api, sseClient)
    }

    @Test
    fun createConversation_callsCreateUserThenCreateConversation_andReturnsConvId() = runTest {
        val userKey = "user-key-123"
        val convId = "conv_abc"
        coEvery { api.createUser(any()) } returns Response.success(
            CreateUserResponse(key = userKey)
        )
        coEvery { api.createConversation(userKey, any()) } returns Response.success(
            ConversationResponse(conversation = ConversationDto(id = convId))
        )

        val result = repository.createConversation()

        assertTrue(result.isSuccess)
        assertEquals(convId, result.getOrNull())
        coVerify { api.createUser(any()) }
        coVerify { api.createConversation(userKey, any()) }
    }

    @Test
    fun sendMessage_afterCreateConversation_usesStoredKeyAndConvId() = runTest {
        coEvery { api.createUser(any()) } returns Response.success(CreateUserResponse(key = "key"))
        coEvery { api.createConversation(any(), any()) } returns Response.success(
            ConversationResponse(conversation = ConversationDto(id = "conv_1"))
        )
        coEvery { api.sendMessage(any(), any()) } returns Response.success(Unit)

        repository.createConversation()
        val result = repository.sendMessage("hello")

        assertTrue(result.isSuccess)
        coVerify { api.sendMessage("key", match { it.conversationId == "conv_1" && it.payload.text == "hello" }) }
    }

    @Test
    fun sendMessage_beforeCreateConversation_returnsFailure() = runTest {
        val result = repository.sendMessage("hi")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun botMessageEvents_emitsBotMessageTextFromSseData() = runTest {
        coEvery { api.createUser(any()) } returns Response.success(CreateUserResponse(key = "k"))
        coEvery { api.createConversation(any(), any()) } returns Response.success(
            ConversationResponse(conversation = ConversationDto(id = "c"))
        )
        repository.createConversation()

        val botEvent = BotpressEvent(
            type = "message_created",
            data = SseMessageData(
                payload = SseMessagePayload(text = "Hello from bot"),
                isBot = true
            )
        )
        every { sseClient.listenConversation(any(), any(), any()) } returns flowOf(botEvent)

        val list = mutableListOf<String>()
        repository.botMessageEvents().collect { list.add(it) }

        assertEquals(listOf("Hello from bot"), list)
    }

    @Test
    fun botMessageEvents_filtersOutNonBotMessages() = runTest {
        coEvery { api.createUser(any()) } returns Response.success(CreateUserResponse(key = "k"))
        coEvery { api.createConversation(any(), any()) } returns Response.success(
            ConversationResponse(conversation = ConversationDto(id = "c"))
        )
        repository.createConversation()

        val userEvent = BotpressEvent(
            type = "message_created",
            data = SseMessageData(
                payload = SseMessagePayload(text = "User said this"),
                isBot = false
            )
        )
        every { sseClient.listenConversation(any(), any(), any()) } returns flowOf(userEvent)

        val list = mutableListOf<String>()
        repository.botMessageEvents().collect { list.add(it) }

        assertEquals(emptyList<String>(), list)
    }
}
