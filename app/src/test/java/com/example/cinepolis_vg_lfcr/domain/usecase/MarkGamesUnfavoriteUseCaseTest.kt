package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MarkGamesUnfavoriteUseCaseTest {

    private lateinit var repository: GameRepository
    private lateinit var useCase: MarkGamesUnfavoriteUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = MarkGamesUnfavoriteUseCase(repository)
    }

    @Test
    fun invoke_callsRepositoryWithIds() = runTest {
        coEvery { repository.markGamesUnfavorite(any()) } just Runs
        useCase(listOf(1, 2))
        coVerify { repository.markGamesUnfavorite(listOf(1, 2)) }
    }
}
