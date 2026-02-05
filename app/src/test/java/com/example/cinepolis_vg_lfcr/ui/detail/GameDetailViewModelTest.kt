package com.example.cinepolis_vg_lfcr.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.usecase.GetGameByIdUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGameDeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.UpdateGameUseCase
import com.example.cinepolis_vg_lfcr.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameDetailViewModelTest {

    @get:Rule
    val mainRule = MainCoroutineRule()

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

    @Test
    fun init_loadsGameAndSetsState() = runTest {
        val game = createGame(1)
        val getById = mockk<GetGameByIdUseCase>()
        coEvery { getById(1) } returns game
        val updateGame = mockk<UpdateGameUseCase>(relaxed = true)
        val markDeleted = mockk<MarkGameDeletedUseCase>(relaxed = true)
        val savedState = SavedStateHandle(mapOf("gameId" to 1))

        val viewModel = GameDetailViewModel(savedState, getById, updateGame, markDeleted)

        assertEquals(game, viewModel.state.value.game)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(null, viewModel.state.value.error)
    }

    @Test
    fun init_whenGameNotFound_setsError() = runTest {
        val getById = mockk<GetGameByIdUseCase>()
        coEvery { getById(99) } returns null
        val updateGame = mockk<UpdateGameUseCase>(relaxed = true)
        val markDeleted = mockk<MarkGameDeletedUseCase>(relaxed = true)
        val savedState = SavedStateHandle(mapOf("gameId" to 99))

        val viewModel = GameDetailViewModel(savedState, getById, updateGame, markDeleted)

        assertEquals("Game not found", viewModel.state.value.error)
    }

    @Test
    fun deleteGame_success_setsNavigateBack() = runTest {
        val game = createGame(1)
        val getById = mockk<GetGameByIdUseCase>()
        coEvery { getById(1) } returns game
        val updateGame = mockk<UpdateGameUseCase>(relaxed = true)
        val markDeleted = mockk<MarkGameDeletedUseCase>()
        coEvery { markDeleted(1) } returns Unit
        val savedState = SavedStateHandle(mapOf("gameId" to 1))

        val viewModel = GameDetailViewModel(savedState, getById, updateGame, markDeleted)
        viewModel.deleteGame()

        assertTrue(viewModel.state.value.navigateBack)
    }

    @Test
    fun updateGame_failure_setsError() = runTest {
        val game = createGame(1)
        val getById = mockk<GetGameByIdUseCase>()
        coEvery { getById(1) } returns game
        val updateGame = mockk<UpdateGameUseCase>()
        coEvery { updateGame(any()) } throws Exception("Update failed")
        val markDeleted = mockk<MarkGameDeletedUseCase>(relaxed = true)
        val savedState = SavedStateHandle(mapOf("gameId" to 1))

        val viewModel = GameDetailViewModel(savedState, getById, updateGame, markDeleted)
        viewModel.updateGame(game)

        assertEquals("Update failed", viewModel.state.value.error)
    }

    @Test
    fun clearNavigateBack_clearsFlag() = runTest {
        val game = createGame(1)
        val getById = mockk<GetGameByIdUseCase>()
        coEvery { getById(1) } returns game
        val updateGame = mockk<UpdateGameUseCase>(relaxed = true)
        val markDeleted = mockk<MarkGameDeletedUseCase>()
        coEvery { markDeleted(1) } returns Unit
        val savedState = SavedStateHandle(mapOf("gameId" to 1))

        val viewModel = GameDetailViewModel(savedState, getById, updateGame, markDeleted)
        viewModel.deleteGame()
        assertTrue(viewModel.state.value.navigateBack)
        viewModel.clearNavigateBack()
        assertFalse(viewModel.state.value.navigateBack)
    }
}
