package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchFavoriteGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    operator fun invoke(query: String): Flow<List<Game>> = repository.searchFavoriteGames(query)
}
