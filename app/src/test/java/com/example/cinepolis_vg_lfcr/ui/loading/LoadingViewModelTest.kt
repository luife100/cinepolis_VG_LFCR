package com.example.cinepolis_vg_lfcr.ui.loading

import com.example.cinepolis_vg_lfcr.domain.usecase.SyncGamesUseCase
import com.example.cinepolis_vg_lfcr.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoadingViewModelTest {

    @get:Rule
    val mainRule = MainCoroutineRule()

    @Test
    fun init_syncSuccess_setsStateToSuccess() = runTest {
        val syncUseCase = mockk<SyncGamesUseCase>()
        coEvery { syncUseCase(any()) } returns Result.success(Unit)

        val viewModel = LoadingViewModel(syncUseCase)

        assertEquals(LoadingState.Success, viewModel.state.value)
    }

    @Test
    fun init_syncFails_setsStateToError() = runTest {
        val syncUseCase = mockk<SyncGamesUseCase>()
        coEvery { syncUseCase(any()) } returns Result.failure(Exception("Network error"))

        val viewModel = LoadingViewModel(syncUseCase)

        assertEquals(LoadingState.Error("Network error"), viewModel.state.value)
    }

    @Test
    fun syncCatalog_callsUseCaseAgain() = runTest {
        val syncUseCase = mockk<SyncGamesUseCase>(relaxed = true)
        coEvery { syncUseCase(any()) } returns Result.success(Unit)

        val viewModel = LoadingViewModel(syncUseCase)
        viewModel.syncCatalog()

        coVerify(atLeast = 2) { syncUseCase(any()) }
    }
}
