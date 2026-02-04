package com.example.cinepolis_vg_lfcr.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.cinepolis_vg_lfcr.data.local.AppDatabase
import com.example.cinepolis_vg_lfcr.data.local.GameDao
import com.example.cinepolis_vg_lfcr.data.preferences.ViewModePreferences
import com.example.cinepolis_vg_lfcr.data.remote.FreeToGameApi
import com.example.cinepolis_vg_lfcr.data.repository.GameRepositoryImpl
import com.example.cinepolis_vg_lfcr.domain.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://www.freetogame.com/api/"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideGameDao(database: AppDatabase): GameDao = database.gameDao()

    @Provides
    @Singleton
    fun provideFreeToGameApi(): FreeToGameApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FreeToGameApi::class.java)

    @Provides
    @Singleton
    fun provideGameRepository(api: FreeToGameApi, dao: GameDao): GameRepository =
        GameRepositoryImpl(api, dao)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("user_preferences") }
        )

    @Provides
    @Singleton
    fun provideViewModePreferences(dataStore: DataStore<Preferences>): ViewModePreferences =
        ViewModePreferences(dataStore)
}
