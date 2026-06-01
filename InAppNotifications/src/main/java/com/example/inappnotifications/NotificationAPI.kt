package com.example.inappnotifications

import android.util.Log
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationAPI {
    @GET("/getNotifications")
    fun getNotifications(): Call<MutableList<Notification>>{


        Log.d("NotificationAPI", "getNotifications")
        return null!!
    }
}