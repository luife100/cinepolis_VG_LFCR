package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SendChatMessageUseCaseTest {

    private lateinit var repository: AssistantRepository
    private lateinit var useCase: SendChatMessageUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SendChatMessageUseCase(repository)
    }

    @Test
    fun invoke_delegatesToRepository_andReturnsSuccess() = runTest {
        coEvery { repository.sendMessage(any()) } returns Result.success(Unit)

        val result = useCase("hello")

        assertTrue(result.isSuccess)
    }

    @Test
    fun invoke_passesTextToRepository() = runTest {
        coEvery { repository.sendMessage(any()) } returns Result.success(Unit)

        useCase("hi there")

        coVerify { repository.sendMessage("hi there") }
    }

    @Test
    fun invoke_whenRepositoryFails_returnsFailure() = runTest {
        val error = Exception("Send failed")
        coEvery { repository.sendMessage(any()) } returns Result.failure(error)

        val result = useCase("hello")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() == error)
    }
}
