package com.example.cinepolis_vg_lfcr.data.remote.botpress

/**
 * Botpress Chat API configuration.
 * Consider moving secrets to BuildConfig or a secure store for production.
 */
object BotpressConfig {
    // This identifies your bot in the URL/Headers
    const val WEBHOOK_ID = "465dfce3-2da8-4a33-89cd-22bbcc4db9b0"
    const val BOT_ID = "80eee861-aebf-4ebe-9989-39b724cc1288"

    // Your Personal Access Token (Keep this safe!)
    const val API_KEY = "bp_bak_DNOPU8NtqVyjy8Um1F_aCyIS2NUzuzxP42pT"

    const val BASE_URL = "https://chat.botpress.cloud/v1/" // Added v1 for the Chat API

    // Header KEY NAMES (The labels on the envelopes)
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_BOT_ID = "x-bot-id"
    const val HEADER_USER_KEY = "x-user-key"
}
