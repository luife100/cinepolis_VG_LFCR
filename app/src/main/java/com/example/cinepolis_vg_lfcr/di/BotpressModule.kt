package com.example.cinepolis_vg_lfcr.di

import android.util.Log
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressApi
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressConfig
import com.example.cinepolis_vg_lfcr.data.remote.botpress.BotpressSseClient
import com.example.cinepolis_vg_lfcr.data.repository.AssistantRepositoryImpl
import com.example.cinepolis_vg_lfcr.domain.repository.AssistantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.google.gson.Gson

@Module
@InstallIn(SingletonComponent::class)
object BotpressModule {

    private const val BOTPRESS_LOG_TAG = "Botpress"

    @Provides
    @Singleton
    @BotpressClient
    fun provideBotpressOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader(BotpressConfig.HEADER_AUTHORIZATION, "Bearer ${BotpressConfig.API_KEY}")
                    .addHeader(BotpressConfig.HEADER_BOT_ID, BotpressConfig.BOT_ID)
                    .addHeader("Content-Type", "application/json")
                    .build()
            )
        }
        .addInterceptor { chain ->
            val response = chain.proceed(chain.request())
            val path = response.request.url.encodedPath
            if (!path.contains("listen") && response.body != null && (path.contains("users") || path.contains("conversations") || path.contains("messages"))) {
                val rawBody = response.peekBody(Long.MAX_VALUE).string()
                Log.d(BOTPRESS_LOG_TAG, "API raw response [$path]: $rawBody")
            }
            response
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideBotpressApi(@BotpressClient client: OkHttpClient): BotpressApi {
        val baseUrl = "${BotpressConfig.BASE_URL}${BotpressConfig.WEBHOOK_ID}/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BotpressApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideBotpressSseClient(
        @BotpressClient client: OkHttpClient,
        gson: Gson
    ): BotpressSseClient = BotpressSseClient(client, gson)

    @Provides
    @Singleton
    fun provideAssistantRepository(
        api: BotpressApi,
        sseClient: BotpressSseClient
    ): AssistantRepository = AssistantRepositoryImpl(api, sseClient)
}
