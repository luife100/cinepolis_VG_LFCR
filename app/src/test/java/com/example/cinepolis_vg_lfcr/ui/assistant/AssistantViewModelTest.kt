package com.example.cinepolis_vg_lfcr.ui.assistant

import com.example.cinepolis_vg_lfcr.domain.usecase.CreateConversationUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.ObserveBotMessagesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SendChatMessageUseCase
import com.example.cinepolis_vg_lfcr.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AssistantViewModelTest {

    @get:Rule
    val mainRule = MainCoroutineRule()

    @Test
    fun init_createConversationSuccess_setsConversationReady() = runTest {
        val createConv = mockk<CreateConversationUseCase>()
        coEvery { createConv() } returns Result.success("conv_1")
        val sendMsg = mockk<SendChatMessageUseCase>(relaxed = true)
        val observeBot = mockk<ObserveBotMessagesUseCase>()
        every { observeBot() } returns flowOf()

        val viewModel = AssistantViewModel(createConv, sendMsg, observeBot)

        assertTrue(viewModel.state.value.conversationReady)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(null, viewModel.state.value.error)
    }

    @Test
    fun init_createConversationFails_setsError() = runTest {
        val createConv = mockk<CreateConversationUseCase>()
        coEvery { createConv() } returns Result.failure(Exception("API error"))
        val sendMsg = mockk<SendChatMessageUseCase>(relaxed = true)
        val observeBot = mockk<ObserveBotMessagesUseCase>()
        every { observeBot() } returns flowOf()

        val viewModel = AssistantViewModel(createConv, sendMsg, observeBot)

        assertEquals("API error", viewModel.state.value.error)
        assertFalse(viewModel.state.value.conversationReady)
    }

    @Test
    fun updateInputText_updatesState() = runTest {
        val createConv = mockk<CreateConversationUseCase>()
        coEvery { createConv() } returns Result.success("conv_1")
        val sendMsg = mockk<SendChatMessageUseCase>(relaxed = true)
        val observeBot = mockk<ObserveBotMessagesUseCase>()
        every { observeBot() } returns flowOf()

        val viewModel = AssistantViewModel(createConv, sendMsg, observeBot)
        viewModel.updateInputText("hello")

        assertEquals("hello", viewModel.state.value.inputText)
    }

    @Test
    fun sendMessage_addsUserMessageAndClearsInput() = runTest {
        val createConv = mockk<CreateConversationUseCase>()
        coEvery { createConv() } returns Result.success("conv_1")
        val sendMsg = mockk<SendChatMessageUseCase>()
        coEvery { sendMsg(any()) } returns Result.success(Unit)
        val observeBot = mockk<ObserveBotMessagesUseCase>()
        every { observeBot() } returns flowOf()

        val viewModel = AssistantViewModel(createConv, sendMsg, observeBot)
        viewModel.updateInputText("hi")
        viewModel.sendMessage()

        assertEquals("", viewModel.state.value.inputText)
        assertEquals(1, viewModel.state.value.messages.size)
        assertEquals("hi", viewModel.state.value.messages[0].text)
        assertTrue(viewModel.state.value.messages[0].isFromUser)
    }

    @Test
    fun sendMessage_whenInputEmpty_doesNotAddMessage() = runTest {
        val createConv = mockk<CreateConversationUseCase>()
        coEvery { createConv() } returns Result.success("conv_1")
        val sendMsg = mockk<SendChatMessageUseCase>(relaxed = true)
        val observeBot = mockk<ObserveBotMessagesUseCase>()
        every { observeBot() } returns flowOf()

        val viewModel = AssistantViewModel(createConv, sendMsg, observeBot)
        viewModel.updateInputText("   ")
        viewModel.sendMessage()

        assertEquals(0, viewModel.state.value.messages.size)
    }
}
