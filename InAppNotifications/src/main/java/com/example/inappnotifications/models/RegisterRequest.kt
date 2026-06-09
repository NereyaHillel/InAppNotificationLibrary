package com.example.inappnotifications.models

data class RegisterDeviceRequest(
    val device_name: String,
    val device_id: String,
    val user_id: String
)