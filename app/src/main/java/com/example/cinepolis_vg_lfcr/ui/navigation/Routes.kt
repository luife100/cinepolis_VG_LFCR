package com.example.cinepolis_vg_lfcr.ui.navigation

object Routes {
    const val Loading = "loading"
    const val List = "list"
    const val Detail = "detail/{gameId}"

    fun detail(gameId: Int) = "detail/$gameId"
}
