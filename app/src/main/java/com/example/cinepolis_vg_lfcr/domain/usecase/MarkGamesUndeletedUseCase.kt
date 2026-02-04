package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import javax.inject.Inject

class MarkGamesUndeletedUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(ids: List<Int>) = repository.markGamesUndeleted(ids)
}
