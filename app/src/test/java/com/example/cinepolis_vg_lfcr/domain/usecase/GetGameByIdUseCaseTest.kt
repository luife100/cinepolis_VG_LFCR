package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetGameByIdUseCaseTest {

    private lateinit var repository: GameRepository
    private lateinit var useCase: GetGameByIdUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetGameByIdUseCase(repository)
    }

    @Test
    fun invoke_returnsGameFromRepository() = runTest {
        val game = createGame(42)
        coEvery { repository.getGameById(42) } returns game

        val result = useCase(42)

        assertEquals(game, result)
    }

    @Test
    fun invoke_whenNotFound_returnsNull() = runTest {
        coEvery { repository.getGameById(999) } returns null

        val result = useCase(999)

        assertNull(result)
    }

    @Test
    fun invoke_passesIdToRepository() = runTest {
        coEvery { repository.getGameById(7) } returns null

        useCase(7)

        coVerify { repository.getGameById(7) }
    }

    private fun createGame(id: Int) = Game(
        id = id,
        title = "Game $id",
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
}
