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

class MarkGameDeletedUseCaseTest {

    private lateinit var repository: GameRepository
    private lateinit var useCase: MarkGameDeletedUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = MarkGameDeletedUseCase(repository)
    }

    @Test
    fun invoke_callsRepositoryMarkGameDeleted() = runTest {
        coEvery { repository.markGameDeleted(any()) } just Runs

        useCase(42)

        coVerify { repository.markGameDeleted(42) }
    }
}
