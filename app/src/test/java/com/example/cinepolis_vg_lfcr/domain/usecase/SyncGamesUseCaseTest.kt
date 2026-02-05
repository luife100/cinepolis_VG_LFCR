package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SyncGamesUseCaseTest {

    private lateinit var repository: GameRepository
    private lateinit var useCase: SyncGamesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SyncGamesUseCase(repository)
    }

    @Test
    fun invoke_delegatesToRepository_andReturnsSuccess() = runTest {
        coEvery { repository.syncFromRemote(any()) } returns Result.success(Unit)

        val result = useCase(forceRefresh = true)

        assertTrue(result.isSuccess)
        coVerify { repository.syncFromRemote(true) }
    }

    @Test
    fun invoke_withForceRefreshFalse_passesFalse() = runTest {
        coEvery { repository.syncFromRemote(any()) } returns Result.success(Unit)

        useCase(forceRefresh = false)

        coVerify { repository.syncFromRemote(false) }
    }

    @Test
    fun invoke_whenRepositoryFails_returnsFailure() = runTest {
        val error = Exception("Sync failed")
        coEvery { repository.syncFromRemote(any()) } returns Result.failure(error)

        val result = useCase(forceRefresh = true)

        assertTrue(result.isFailure)
        assertFalse(result.isSuccess)
    }
}
