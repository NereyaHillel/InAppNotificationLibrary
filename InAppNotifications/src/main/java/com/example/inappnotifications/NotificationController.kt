package com.example.inappnotifications

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationController {
    private var BASE_URL = "https://in-app-notifications-api.vercel.app/"

    fun getAPI(): NotificationAPI {
        val gson = Gson()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(NotificationAPI::class.java)
    }

    fun getNotifications(Callback_getNotifications: Callback_getNotifications): MutableList<Notification> {
        val call = getAPI().getNotifications()
        call.enqueue(object : retrofit2.Callback<MutableList<Notification>> {
            override fun onResponse(
                call: retrofit2.Call<MutableList<Notification>>,
                response: retrofit2.Response<MutableList<Notification>>
            ) {
                if (response.isSuccessful) {
                    val notifications = response.body()
                    if (notifications != null) {
                        Callback_getNotifications.onSuccess(notifications)
                    }
                } else {
                    Callback_getNotifications.onFailure(Throwable("Response not successful"))
                }
            }

            override fun onFailure(call: retrofit2.Call<MutableList<Notification>>, t: Throwable) {
                Callback_getNotifications.onFailure(t)
            }
        })

        return mutableListOf()
    }

}