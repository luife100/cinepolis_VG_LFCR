package com.example.cinepolis_vg_lfcr.data.mapper

import com.example.cinepolis_vg_lfcr.data.local.GameEntity
import com.example.cinepolis_vg_lfcr.data.remote.GameDto
import com.example.cinepolis_vg_lfcr.domain.model.Game

object GameMapper {

    fun GameEntity.toDomain(): Game = Game(
        id = id,
        title = title,
        thumbnail = thumbnail,
        shortDescription = shortDescription,
        gameUrl = gameUrl,
        genre = genre,
        platform = platform,
        publisher = publisher,
        developer = developer,
        releaseDate = releaseDate,
        freetogameProfileUrl = freetogameProfileUrl,
        isDeleted = isDeleted
    )

    fun GameDto.toEntity(isDeleted: Boolean = false): GameEntity = GameEntity(
        id = id,
        title = title,
        thumbnail = thumbnail,
        shortDescription = shortDescription,
        gameUrl = gameUrl,
        genre = genre,
        platform = platform,
        publisher = publisher,
        developer = developer,
        releaseDate = releaseDate,
        freetogameProfileUrl = freetogameProfileUrl,
        isDeleted = isDeleted
    )

    fun Game.toEntity(): GameEntity = GameEntity(
        id = id,
        title = title,
        thumbnail = thumbnail,
        shortDescription = shortDescription,
        gameUrl = gameUrl,
        genre = genre,
        platform = platform,
        publisher = publisher,
        developer = developer,
        releaseDate = releaseDate,
        freetogameProfileUrl = freetogameProfileUrl,
        isDeleted = isDeleted
    )
}
