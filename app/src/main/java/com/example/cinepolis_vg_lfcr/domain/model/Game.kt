package com.example.cinepolis_vg_lfcr.domain.model

/**
 * Domain model for a video game. Pure data class with no framework dependencies.
 * Used across domain and presentation layers.
 */
data class Game(
    val id: Int,
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
    val isDeleted: Boolean = false
)
