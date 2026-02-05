package com.example.cinepolis_vg_lfcr.data.repository

import com.example.cinepolis_vg_lfcr.data.local.GameDao
import com.example.cinepolis_vg_lfcr.data.local.GameEntity
import com.example.cinepolis_vg_lfcr.data.local.IdDeletedFavorite
import com.example.cinepolis_vg_lfcr.data.mapper.GameMapper.toDomain
import com.example.cinepolis_vg_lfcr.data.remote.FreeToGameApi
import com.example.cinepolis_vg_lfcr.data.remote.GameDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.Runs
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameRepositoryImplTest {

    private lateinit var api: FreeToGameApi
    private lateinit var dao: GameDao
    private lateinit var repository: GameRepositoryImpl

    private val entity1 = GameEntity(
        id = 1,
        title = "Game 1",
        thumbnail = "",
        shortDescription = "",
        gameUrl = "",
        genre = "RPG",
        platform = "PC",
        publisher = "",
        developer = "",
        releaseDate = "",
        freetogameProfileUrl = "",
        isDeleted = false,
        isFavorite = false
    )

    private val dto1 = GameDto(
        id = 1,
        title = "Game 1",
        thumbnail = "",
        shortDescription = "",
        gameUrl = "",
        genre = "RPG",
        platform = "PC",
        publisher = "",
        developer = "",
        releaseDate = "",
        freetogameProfileUrl = ""
    )

    @Before
    fun setUp() {
        api = mockk()
        dao = mockk(relaxed = true)
        repository = GameRepositoryImpl(api, dao)
    }

    @Test
    fun getGames_mapsEntitiesFromDao() = runTest {
        every { dao.getAllNotDeleted() } returns flowOf(listOf(entity1))

        val list = mutableListOf<List<com.example.cinepolis_vg_lfcr.domain.model.Game>>()
        repository.getGames().collect { list.add(it) }

        assertEquals(1, list.size)
        assertEquals(1, list[0].size)
        assertEquals(1, list[0][0].id)
        assertEquals("Game 1", list[0][0].title)
    }

    @Test
    fun getGameById_returnsMappedGame() = runTest {
        coEvery { dao.getById(1) } returns entity1

        val game = repository.getGameById(1)

        assertEquals(1, game?.id)
        assertEquals("Game 1", game?.title)
    }

    @Test
    fun getGameById_whenNotFound_returnsNull() = runTest {
        coEvery { dao.getById(999) } returns null

        val game = repository.getGameById(999)

        assertEquals(null, game)
    }

    @Test
    fun syncFromRemote_whenForceRefresh_callsApiAndInserts() = runTest {
        coEvery { dao.getCount() } returns 0
        coEvery { dao.getAllIdDeletedFavorite() } returns emptyList()
        coEvery { api.getGames() } returns listOf(dto1)
        coEvery { dao.insertAll(any()) } just Runs

        val result = repository.syncFromRemote(forceRefresh = true)

        assertTrue(result.isSuccess)
        coVerify { api.getGames() }
        coVerify { dao.insertAll(match { it.size == 1 && it[0].id == 1 }) }
    }

    @Test
    fun syncFromRemote_whenDbNotEmptyAndNotForceRefresh_doesNotCallApi() = runTest {
        coEvery { dao.getCount() } returns 5

        val result = repository.syncFromRemote(forceRefresh = false)

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { api.getGames() }
    }

    @Test
    fun syncFromRemote_preservesExistingDeletedAndFavorite() = runTest {
        coEvery { dao.getCount() } returns 0
        coEvery { dao.getAllIdDeletedFavorite() } returns listOf(
            IdDeletedFavorite(id = 1, isDeleted = true, isFavorite = true)
        )
        coEvery { api.getGames() } returns listOf(dto1)
        coEvery { dao.insertAll(any()) } just Runs

        repository.syncFromRemote(forceRefresh = true)

        coVerify { dao.insertAll(match { list: List<GameEntity> -> list[0].isDeleted && list[0].isFavorite }) }
    }

    @Test
    fun markGameDeleted_callsDao() = runTest {
        coEvery { dao.markDeleted(any<Int>()) } just Runs

        repository.markGameDeleted(42)

        coVerify { dao.markDeleted(42) }
    }

    @Test
    fun updateGame_callsDaoUpdate() = runTest {
        val game = entity1.toDomain()
        coEvery { dao.update(any()) } just Runs

        repository.updateGame(game)

        coVerify { dao.update(match { entity: GameEntity -> entity.id == 1 }) }
    }
}
