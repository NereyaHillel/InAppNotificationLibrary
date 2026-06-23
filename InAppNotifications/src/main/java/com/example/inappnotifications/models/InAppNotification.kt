package com.example.inappnotifications.models

data class InAppNotification(
    val _id: String,
    val campaign_id: String,
    val message: String,
    val status: String,
    val title: String,
    // SDUI fields - all nullable, server-driven
    val position: String? = null,
    val image_url: String? = null,
    val link: String? = null,
    val btn_positive: String? = null,
    val btn_negative: String? = null,
    val btn_neutral: String? = null
)

data class NotificationResponse(
    val message: String,
    val notifications: List<InAppNotification>
)