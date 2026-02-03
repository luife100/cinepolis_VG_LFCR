package com.example.cinepolis_vg_lfcr.domain.usecase

import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Syncs games from remote. When [forceRefresh] is true (e.g. pull-to-refresh),
 * fetches from API and merges into DB preserving local isDeleted.
 * When false, only fetches if DB is empty (initial load).
 */
class SyncGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean): Result<Unit> =
        repository.syncFromRemote(forceRefresh)
}
