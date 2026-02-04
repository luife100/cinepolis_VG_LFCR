package com.example.cinepolis_vg_lfcr.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity storing all API fields plus [isDeleted] for logical deletion.
 * Sync from API only on first load (empty DB) or pull-to-refresh.
 */
@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val thumbnail: String,
    val shortDescription: String,
    val gameUrl: String,
    val genre: String,
    val platform: String,
    val publisher: String,
    val developer: String,
    val releaseDate: String,
    val freetogameProfileUrl: String,
    val isDeleted: Boolean = false,
    val isFavorite: Boolean = false
)
