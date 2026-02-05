package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateGameUseCaseTest {

    private lateinit var repository: GameRepository
    private lateinit var useCase: UpdateGameUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = UpdateGameUseCase(repository)
    }

    @Test
    fun invoke_callsRepositoryUpdateGame() = runTest {
        val game = Game(
            id = 1,
            title = "Updated",
            thumbnail = "",
            shortDescription = "",
            gameUrl = "",
            genre = "",
            platform = "",
            publisher = "",
            developer = "",
            releaseDate = "",
            freetogameProfileUrl = ""
        )
        coEvery { repository.updateGame(any()) } just Runs

        useCase(game)

        coVerify { repository.updateGame(game) }
    }
}
