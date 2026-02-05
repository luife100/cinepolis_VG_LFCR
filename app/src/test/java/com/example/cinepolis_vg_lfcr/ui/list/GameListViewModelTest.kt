package com.example.cinepolis_vg_lfcr.ui.list

import com.example.cinepolis_vg_lfcr.data.preferences.ViewModePreferences
import com.example.cinepolis_vg_lfcr.domain.usecase.GetDeletedGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.GetFavoriteGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.GetGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGamesDeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGamesFavoriteUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGamesUndeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGameDeletedUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.MarkGamesUnfavoriteUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SearchDeletedGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SearchFavoriteGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SearchGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.SyncGamesUseCase
import com.example.cinepolis_vg_lfcr.domain.usecase.UpdateGameUseCase
import com.example.cinepolis_vg_lfcr.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.Runs
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameListViewModelTest {

    @get:Rule
    val mainRule = MainCoroutineRule()

    private fun createMocks(): GameListViewModel {
        val getGames = mockk<GetGamesUseCase>()
        every { getGames() } returns flowOf(emptyList())
        val searchGames = mockk<SearchGamesUseCase>()
        every { searchGames(any()) } returns flowOf(emptyList())
        val getFavorites = mockk<GetFavoriteGamesUseCase>()
        every { getFavorites() } returns flowOf(emptyList())
        val searchFavorites = mockk<SearchFavoriteGamesUseCase>()
        every { searchFavorites(any()) } returns flowOf(emptyList())
        val getDeleted = mockk<GetDeletedGamesUseCase>()
        every { getDeleted() } returns flowOf(emptyList())
        val searchDeleted = mockk<SearchDeletedGamesUseCase>()
        every { searchDeleted(any()) } returns flowOf(emptyList())
        val syncGames = mockk<SyncGamesUseCase>()
        coEvery { syncGames(any()) } returns Result.success(Unit)
        val updateGame = mockk<UpdateGameUseCase>(relaxed = true)
        val markGameDeleted = mockk<MarkGameDeletedUseCase>(relaxed = true)
        val markGamesDeleted = mockk<MarkGamesDeletedUseCase>(relaxed = true)
        val markGamesUndeleted = mockk<MarkGamesUndeletedUseCase>(relaxed = true)
        val markGamesFavorite = mockk<MarkGamesFavoriteUseCase>(relaxed = true)
        val markGamesUnfavorite = mockk<MarkGamesUnfavoriteUseCase>(relaxed = true)
        val viewModePrefs = mockk<ViewModePreferences>()
        every { viewModePrefs.viewModeValue } returns flowOf("List")
        coEvery { viewModePrefs.setViewModeValue(any()) } just Runs

        return GameListViewModel(
            getGames,
            searchGames,
            getFavorites,
            searchFavorites,
            getDeleted,
            searchDeleted,
            syncGames,
            updateGame,
            markGameDeleted,
            markGamesDeleted,
            markGamesUndeleted,
            markGamesFavorite,
            markGamesUnfavorite,
            viewModePrefs
        )
    }

    @Test
    fun updateSearchQuery_updatesState() = runTest {
        val viewModel = createMocks()

        viewModel.updateSearchQuery("rpg")

        assertEquals("rpg", viewModel.state.value.searchQuery)
    }

    @Test
    fun setListType_updatesState() = runTest {
        val viewModel = createMocks()

        viewModel.setListType(ListType.Favorites)

        assertEquals(ListType.Favorites, viewModel.state.value.listType)
    }

    @Test
    fun setViewMode_updatesState() = runTest {
        val viewModel = createMocks()

        viewModel.setViewMode(ViewMode.Grid)

        assertEquals(ViewMode.Grid, viewModel.state.value.viewMode)
    }

    @Test
    fun enterSelectionMode_setsSelectionModeTrue() = runTest {
        val viewModel = createMocks()

        viewModel.enterSelectionMode(initialId = 1)

        assertTrue(viewModel.state.value.selectionMode)
        assertEquals(setOf(1), viewModel.state.value.selectedGameIds)
    }

    @Test
    fun exitSelectionMode_clearsSelection() = runTest {
        val viewModel = createMocks()
        viewModel.enterSelectionMode(initialId = 1)

        viewModel.exitSelectionMode()

        assertFalse(viewModel.state.value.selectionMode)
        assertEquals(emptySet<Int>(), viewModel.state.value.selectedGameIds)
    }
}
