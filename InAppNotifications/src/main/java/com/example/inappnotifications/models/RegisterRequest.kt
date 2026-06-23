package com.example.inappnotifications.models

data class RegisterDeviceRequest(
    val device_model: String,
    val device_id: String,
    val user_id: String
)