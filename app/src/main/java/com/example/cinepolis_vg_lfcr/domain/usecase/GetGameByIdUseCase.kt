package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.model.Game
import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import javax.inject.Inject

class GetGameByIdUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(id: Int): Game? = repository.getGameById(id)
}
