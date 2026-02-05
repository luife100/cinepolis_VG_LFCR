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

class MarkGamesFavoriteUseCaseTest {

    private lateinit var repository: GameRepository
    private lateinit var useCase: MarkGamesFavoriteUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = MarkGamesFavoriteUseCase(repository)
    }

    @Test
    fun invoke_callsRepositoryWithIds() = runTest {
        coEvery { repository.markGamesFavorite(any()) } just Runs
        useCase(listOf(1, 2, 3))
        coVerify { repository.markGamesFavorite(listOf(1, 2, 3)) }
    }
}
