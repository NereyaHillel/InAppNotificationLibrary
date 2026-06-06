package com.example.inappnotifications.api

import com.example.inappnotifications.models.CrashReportRequest
import com.example.inappnotifications.models.RegisterDeviceRequest
import com.example.inappnotifications.models.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface NotificationApiService {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/v1/sdk/device/register")
    suspend fun registerDevice(@Body request: RegisterDeviceRequest): Response<Unit>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("api/v1/sdk/notifications")
    suspend fun getNotifications(@Query("user_id") userId: String): Response<NotificationResponse>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/v1/sdk/notifications/{id}/interact")
    suspend fun trackInteraction(@Path("id") notificationId: String): Response<Unit>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/v1/sdk/crash-report")
    suspend fun reportCrash(@Body request: CrashReportRequest): Response<Unit>
}