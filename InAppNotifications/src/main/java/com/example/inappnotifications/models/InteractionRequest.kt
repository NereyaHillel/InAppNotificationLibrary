package com.example.inappnotifications.models

data class CrashReportRequest(
    val user_id: String,
    val crash_details: String
)