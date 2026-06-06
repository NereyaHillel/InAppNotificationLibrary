package com.example.inappnotifications.models

data class InAppNotification(
    val _id: String,
    val campaign_id: String,
    val message: String,
    val status: String
)

data class NotificationResponse(
    val message: String,
    val notifications: List<InAppNotification>
)