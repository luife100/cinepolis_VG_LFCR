package com.example.cinepolis_vg_lfcr.data.remote

import com.google.gson.annotations.SerializedName

/**
 * API response model for GET https://www.freetogame.com/api/games
 * All fields are stored as-is in Room.
 */
data class GameDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("short_description") val shortDescription: String,
    @SerializedName("game_url") val gameUrl: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("platform") val platform: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("developer") val developer: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("freetogame_profile_url") val freetogameProfileUrl: String
)
