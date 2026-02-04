package com.example.cinepolis_vg_lfcr.data.repository

import com.example.cinepolis_vg_lfcr.data.local.GameDao
import com.example.cinepolis_vg_lfcr.data.mapper.GameMapper.toDomain
import com.example.cinepolis_vg_lfcr.data.mapper.GameMapper.toEntity
import com.example.cinepolis_vg_lfcr.data.remote.FreeToGameApi
import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Data layer implementation of [GameRepository].
 * Syncs from API only when DB is empty (initial load) or when [syncFromRemote](forceRefresh = true) (pull-to-refresh).
 */
class GameRepositoryImpl @Inject constructor(
    private val api: FreeToGameApi,
    private val dao: GameDao
) : GameRepository {

    override fun getGames(): Flow<List<Game>> =
        dao.getAllNotDeleted().map { entities -> entities.map { it.toDomain() } }

    override fun searchGames(query: String): Flow<List<Game>> =
        dao.searchNotDeleted(query).map { entities -> entities.map { it.toDomain() } }

    override fun getFavoriteGames(): Flow<List<Game>> =
        dao.getFavorites().map { entities -> entities.map { it.toDomain() } }

    override fun searchFavoriteGames(query: String): Flow<List<Game>> =
        dao.searchFavorites(query).map { entities -> entities.map { it.toDomain() } }

    override fun getDeletedGames(): Flow<List<Game>> =
        dao.getDeleted().map { entities -> entities.map { it.toDomain() } }

    override fun searchDeletedGames(query: String): Flow<List<Game>> =
        dao.searchDeleted(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getGameById(id: Int): Game? =
        dao.getById(id)?.toDomain()

    override suspend fun syncFromRemote(forceRefresh: Boolean): Result<Unit> {
        val shouldSync = forceRefresh || dao.getCount() == 0
        if (!shouldSync) return Result.success(Unit)
        return runCatching {
            val dtos = api.getGames()
            val existing = dao.getAllIdDeletedFavorite().associateBy { it.id }
            val entities = dtos.map { dto ->
                val ex = existing[dto.id]
                dto.toEntity(
                    isDeleted = ex?.isDeleted ?: false,
                    isFavorite = ex?.isFavorite ?: false
                )
            }
            dao.insertAll(entities)
        }
    }

    override suspend fun updateGame(game: Game) {
        dao.update(game.toEntity())
    }

    override suspend fun markGameDeleted(id: Int) {
        dao.markDeleted(id)
    }

    override suspend fun markGamesDeleted(ids: List<Int>) {
        if (ids.isNotEmpty()) dao.markDeleted(ids)
    }

    override suspend fun markGamesFavorite(ids: List<Int>) {
        if (ids.isNotEmpty()) dao.markFavorite(ids)
    }

    override suspend fun markGamesUnfavorite(ids: List<Int>) {
        if (ids.isNotEmpty()) dao.markUnfavorite(ids)
    }
}
