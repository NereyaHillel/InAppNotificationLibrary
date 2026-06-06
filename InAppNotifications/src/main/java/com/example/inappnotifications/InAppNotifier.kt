package com.example.inappnotifications

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.example.inappnotifications.api.ApiClient
import com.example.inappnotifications.api.NotificationApiService
import com.example.inappnotifications.models.CrashReportRequest
import com.example.inappnotifications.models.InAppNotification
import com.example.inappnotifications.models.RegisterDeviceRequest
import android.app.AlertDialog
import android.view.Gravity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class NotificationPosition {
    TOP, CENTER, BOTTOM
}

object InAppNotifier {
    private const val TAG = "InAppNotifier"

    private var isInitialized = false
    private var currentUserId = ""
    private var currentDeviceId = ""
    private lateinit var apiService: NotificationApiService

    fun initialize(context: Context, userId: String, backendBaseUrl: String) {
        if (isInitialized) return

        currentUserId = userId
        currentDeviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
        apiService = ApiClient.create(backendBaseUrl)

        isInitialized = true
        Log.d(TAG, "SDK Initialized: User $currentUserId")
    }

    suspend fun registerDevice(): Boolean {
        if (!isInitialized) return false

        return try {
            val request = RegisterDeviceRequest(currentDeviceId, currentUserId)
            val response = apiService.registerDevice(request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            false
        }
    }

    suspend fun getNotifications(): List<InAppNotification>? {
        if (!isInitialized) return null

        return try {
            val response = apiService.getNotifications(currentUserId)
            if (response.isSuccessful) {
                response.body()?.notifications
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get notifications", e)
            null
        }
    }

    suspend fun trackInteraction(notificationId: String): Boolean {
        if (!isInitialized) return false

        return try {
            val response = apiService.trackInteraction(notificationId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Interaction tracking failed", e)
            false
        }
    }

    suspend fun reportCrash(crashDetails: String): Boolean {
        if (!isInitialized) return false

        return try {
            val request = CrashReportRequest(currentUserId, crashDetails)
            val response = apiService.reportCrash(request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Crash report failed", e)
            false
        }
    }

    fun showNotificationPopup(
        context: Context,
        notification: InAppNotification,
        position: NotificationPosition = NotificationPosition.CENTER,
        positiveButtonText: String = "OK",
        onPositiveClick: (() -> Unit)? = null,
        negativeButtonText: String = "Cancel",
        onNegativeClick: (() -> Unit)? = null,
        neutralButtonText: String? = null,
        onNeutralClick: (() -> Unit)? = null
    ) {
        if (!isInitialized) return

        val builder = AlertDialog.Builder(context)
            .setTitle("New Message")
            .setMessage(notification.message)
            .setCancelable(false)

        builder.setPositiveButton(positiveButtonText) { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                trackInteraction(notification._id)
            }
            onPositiveClick?.invoke()
            dialog.dismiss()
        }

        builder.setNegativeButton(negativeButtonText) { dialog, _ ->
            onNegativeClick?.invoke()
            dialog.dismiss()
        }

        if (neutralButtonText != null) {
            builder.setNeutralButton(neutralButtonText) { dialog, _ ->
                onNeutralClick?.invoke()
                dialog.dismiss()
            }
        }

        val dialog = builder.create()

        dialog.window?.let { window ->
            val layoutParams = window.attributes
            when (position) {
                NotificationPosition.TOP -> {
                    layoutParams.gravity = Gravity.TOP
                    layoutParams.y = 150
                }
                NotificationPosition.BOTTOM -> {
                    layoutParams.gravity = Gravity.BOTTOM
                    layoutParams.y = 150
                }
                NotificationPosition.CENTER -> {
                    layoutParams.gravity = Gravity.CENTER
                }
            }
            window.attributes = layoutParams
        }

        dialog.show()
    }
}