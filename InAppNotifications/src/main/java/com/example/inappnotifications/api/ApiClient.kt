package com.example.inappnotifications.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object ApiClient {

    fun create(baseUrl: String): NotificationApiService {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val retrofit = Retrofit.Builder()
            .baseUrl(safeBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(NotificationApiService::class.java)
    }
}