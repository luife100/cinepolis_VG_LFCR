package com.example.cinepolis_vg_lfcr.data.mapper

import com.example.cinepolis_vg_lfcr.data.local.GameEntity
import com.example.cinepolis_vg_lfcr.data.mapper.GameMapper.toDomain
import com.example.cinepolis_vg_lfcr.data.mapper.GameMapper.toEntity
import com.example.cinepolis_vg_lfcr.data.remote.GameDto
import com.example.cinepolis_vg_lfcr.domain.model.Game
import org.junit.Assert.assertEquals
import org.junit.Test

class GameMapperTest {

    private val sampleEntity = GameEntity(
        id = 1,
        title = "Game A",
        thumbnail = "https://thumb",
        shortDescription = "Short",
        gameUrl = "https://game",
        genre = "RPG",
        platform = "PC",
        publisher = "Pub",
        developer = "Dev",
        releaseDate = "2024-01-01",
        freetogameProfileUrl = "https://profile",
        isDeleted = false,
        isFavorite = true
    )

    private val sampleDto = GameDto(
        id = 2,
        title = "Game B",
        thumbnail = "https://thumb2",
        shortDescription = "Short2",
        gameUrl = "https://game2",
        genre = "Shooter",
        platform = "Web",
        publisher = "Pub2",
        developer = "Dev2",
        releaseDate = "2024-06-01",
        freetogameProfileUrl = "https://profile2"
    )

    @Test
    fun entity_toDomain_mapsAllFields() {
        val game = sampleEntity.toDomain()
        assertEquals(sampleEntity.id, game.id)
        assertEquals(sampleEntity.title, game.title)
        assertEquals(sampleEntity.thumbnail, game.thumbnail)
        assertEquals(sampleEntity.shortDescription, game.shortDescription)
        assertEquals(sampleEntity.gameUrl, game.gameUrl)
        assertEquals(sampleEntity.genre, game.genre)
        assertEquals(sampleEntity.platform, game.platform)
        assertEquals(sampleEntity.publisher, game.publisher)
        assertEquals(sampleEntity.developer, game.developer)
        assertEquals(sampleEntity.releaseDate, game.releaseDate)
        assertEquals(sampleEntity.freetogameProfileUrl, game.freetogameProfileUrl)
        assertEquals(sampleEntity.isDeleted, game.isDeleted)
        assertEquals(sampleEntity.isFavorite, game.isFavorite)
    }

    @Test
    fun dto_toEntity_defaultsDeletedAndFavoriteFalse() {
        val entity = sampleDto.toEntity()
        assertEquals(sampleDto.id, entity.id)
        assertEquals(sampleDto.title, entity.title)
        assertEquals(false, entity.isDeleted)
        assertEquals(false, entity.isFavorite)
    }

    @Test
    fun dto_toEntity_withDeletedAndFavorite_preservesFlags() {
        val entity = sampleDto.toEntity(isDeleted = true, isFavorite = true)
        assertEquals(true, entity.isDeleted)
        assertEquals(true, entity.isFavorite)
    }

    @Test
    fun game_toEntity_roundTripsWithEntity() {
        val game = sampleEntity.toDomain()
        val back = game.toEntity()
        assertEquals(sampleEntity.id, back.id)
        assertEquals(sampleEntity.title, back.title)
        assertEquals(sampleEntity.isDeleted, back.isDeleted)
        assertEquals(sampleEntity.isFavorite, back.isFavorite)
    }
}
