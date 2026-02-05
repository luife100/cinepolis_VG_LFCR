package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateConversationUseCaseTest {

    private lateinit var repository: AssistantRepository
    private lateinit var useCase: CreateConversationUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = CreateConversationUseCase(repository)
    }

    @Test
    fun invoke_delegatesToRepository_andReturnsSuccess() = runTest {
        val convId = "conv_123"
        coEvery { repository.createConversation() } returns Result.success(convId)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(convId, result.getOrNull())
    }

    @Test
    fun invoke_whenRepositoryFails_returnsFailure() = runTest {
        val error = Exception("Network error")
        coEvery { repository.createConversation() } returns Result.failure(error)

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
