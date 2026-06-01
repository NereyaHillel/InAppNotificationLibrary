package com.example.inappnotifications

interface Callback_getNotifications {
    fun onSuccess(notifications: MutableList<Notification>)
    fun onFailure(t: Throwable)

}
