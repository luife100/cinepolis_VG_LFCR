package com.example.cinepolis_vg_lfcr.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    /** All games that are not logically deleted. */
    @Query("SELECT * FROM games WHERE isDeleted = 0 ORDER BY title ASC")
    fun getAllNotDeleted(): Flow<List<GameEntity>>

    /** Search by title or genre (suggestions / filter). Only non-deleted. */
    @Query("""
        SELECT * FROM games 
        WHERE isDeleted = 0 
        AND (title LIKE '%' || :query || '%' OR genre LIKE '%' || :query || '%')
        ORDER BY title ASC
    """)
    fun searchNotDeleted(query: String): Flow<List<GameEntity>>

    /** Get single game by id (may be deleted). */
    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getById(id: Int): GameEntity?

    /** Count of rows (to know if DB is empty). */
    @Query("SELECT COUNT(*) FROM games")
    suspend fun getCount(): Int

    /** Insert or replace on conflict (id). Used when syncing from API. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<GameEntity>)

    /** Update existing row. When syncing from API we do NOT overwrite isDeleted. */
    @Update
    suspend fun update(entity: GameEntity)

    /** Logical delete: set isDeleted = 1 for the given id. */
    @Query("UPDATE games SET isDeleted = 1 WHERE id = :id")
    suspend fun markDeleted(id: Int)

    /** Bulk logical delete. */
    @Query("UPDATE games SET isDeleted = 1 WHERE id IN (:ids)")
    suspend fun markDeleted(ids: List<Int>)

    /** Mark games as favorite. */
    @Query("UPDATE games SET isFavorite = 1 WHERE id IN (:ids)")
    suspend fun markFavorite(ids: List<Int>)

    /** Used on sync: get current isDeleted and isFavorite per id so we don't overwrite user data. */
    @Query("SELECT id, isDeleted, isFavorite FROM games")
    suspend fun getAllIdDeletedFavorite(): List<IdDeletedFavorite>
}

data class IdAndDeleted(val id: Int, val isDeleted: Boolean)
data class IdDeletedFavorite(val id: Int, val isDeleted: Boolean, val isFavorite: Boolean)
