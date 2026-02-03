package com.example.cinepolis_vg_lfcr.data.remote

import retrofit2.http.GET

interface FreeToGameApi {

    @GET("games")
    suspend fun getGames(): List<GameDto>
}
