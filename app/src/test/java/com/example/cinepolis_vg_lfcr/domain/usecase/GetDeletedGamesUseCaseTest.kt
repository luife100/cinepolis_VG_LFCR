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

class GetDeletedGamesUseCaseTest {

    private lateinit var repository: GameRepository
    private lateinit var useCase: GetDeletedGamesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetDeletedGamesUseCase(repository)
    }

    @Test
    fun invoke_returnsFlowFromRepository() = runTest {
        val games = listOf(createGame(1))
        every { repository.getDeletedGames() } returns flowOf(games)
        val list = mutableListOf<List<Game>>()
        useCase().collect { list.add(it) }
        assertEquals(1, list.size)
        assertEquals(games, list[0])
    }

    @Test
    fun invoke_delegatesToRepository() = runTest {
        every { repository.getDeletedGames() } returns flowOf(emptyList())
        useCase().collect { }
        io.mockk.verify { repository.getDeletedGames() }
    }

    private fun createGame(id: Int) = Game(
        id = id, title = "Game $id", thumbnail = "", shortDescription = "",
        gameUrl = "", genre = "", platform = "", publisher = "", developer = "",
        releaseDate = "", freetogameProfileUrl = "", isDeleted = true
    )
}
