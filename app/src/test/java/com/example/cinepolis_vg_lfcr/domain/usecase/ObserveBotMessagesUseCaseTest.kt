package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveBotMessagesUseCaseTest {

    private lateinit var repository: AssistantRepository
    private lateinit var useCase: ObserveBotMessagesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObserveBotMessagesUseCase(repository)
    }

    @Test
    fun invoke_returnsFlowFromRepository() = runTest {
        val messages = flowOf("Hi", "Bye")
        every { repository.botMessageEvents() } returns messages

        val flow = useCase()
        val list = mutableListOf<String>()
        flow.collect { list.add(it) }

        assertEquals(listOf("Hi", "Bye"), list)
    }

    @Test
    fun invoke_delegatesToRepository() = runTest {
        every { repository.botMessageEvents() } returns flowOf()

        useCase().collect { }

        io.mockk.verify { repository.botMessageEvents() }
    }
}
