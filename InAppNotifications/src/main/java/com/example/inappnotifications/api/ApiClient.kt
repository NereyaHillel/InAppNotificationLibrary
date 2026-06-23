package com.example.inappnotifications.api

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Factory object for creating and configuring Retrofit instances.
 *
 * This object provides centralized management of HTTP client configuration,
 * including timeouts and request/response interceptors.
 */
internal object ApiClient {
    private const val TAG = "ApiClient"

    // HTTP Client Configuration
    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 30L

    /**
     * Creates and configures a [NotificationApiService] instance with the provided base URL.
     *
     * The method ensures the base URL ends with a trailing slash for Retrofit compatibility,
     * and configures the HTTP client with appropriate timeout values.
     *
     * @param baseUrl The base URL for the API endpoints. Will be normalized to end with '/'.
     * @return A configured [NotificationApiService] instance.
     * @throws IllegalArgumentException if the baseUrl is empty or invalid.
     */
    fun create(baseUrl: String): NotificationApiService {
        if (baseUrl.isBlank()) {
            throw IllegalArgumentException("Base URL cannot be empty")
        }

        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        Log.d(TAG, "Creating API client with base URL: $safeBaseUrl")

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                val responseBody = response.peekBody(Long.MAX_VALUE)
                response
            }
            .build()

        // Configure Gson to be lenient and ignore unknown fields
        val gson = com.google.gson.GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(safeBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(NotificationApiService::class.java)
    }
}