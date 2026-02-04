package com.example.cinepolis_vg_lfcr.domain.repository

import com.example.cinepolis_vg_lfcr.domain.model.Game
import kotlinx.coroutines.flow.Flow

/**
 * Domain contract for game data. Implemented in the data layer.
 * Presentation/domain use this; data layer provides implementation.
 */
interface GameRepository {

    /** All games that are not logically deleted. */
    fun getGames(): Flow<List<Game>>

    /** Search by title or genre; only non-deleted. */
    fun searchGames(query: String): Flow<List<Game>>

    /** Single game by id, or null if not found. */
    suspend fun getGameById(id: Int): Game?

    /** Sync from remote. When [forceRefresh] is true, fetch and merge; otherwise only sync if DB is empty. */
    suspend fun syncFromRemote(forceRefresh: Boolean): Result<Unit>

    /** Update an existing game (e.g. after edit). */
    suspend fun updateGame(game: Game)

    /** Logical delete: mark game as deleted. */
    suspend fun markGameDeleted(id: Int)

    /** Bulk logical delete. */
    suspend fun markGamesDeleted(ids: List<Int>)

    /** Mark multiple games as favorite. */
    suspend fun markGamesFavorite(ids: List<Int>)
}
