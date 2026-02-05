package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetGamesUseCaseTest {

    private lateinit var repository: GameRepository
    private lateinit var useCase: GetGamesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetGamesUseCase(repository)
    }

    @Test
    fun invoke_returnsFlowFromRepository() = runTest {
        val games = listOf(createGame(1), createGame(2))
        every { repository.getGames() } returns flowOf(games)

        val list = mutableListOf<List<Game>>()
        useCase().collect { list.add(it) }

        assertEquals(1, list.size)
        assertEquals(games, list[0])
    }

    @Test
    fun invoke_delegatesToRepository() = runTest {
        every { repository.getGames() } returns flowOf(emptyList())

        useCase().collect { }

        io.mockk.verify { repository.getGames() }
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
