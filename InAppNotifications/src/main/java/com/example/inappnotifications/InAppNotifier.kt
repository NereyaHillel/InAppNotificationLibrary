package com.example.inappnotifications

import android.app.AlertDialog
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.inappnotifications.api.ApiClient
import com.example.inappnotifications.api.NotificationApiService
import com.example.inappnotifications.models.CrashReportRequest
import com.example.inappnotifications.models.InAppNotification
import com.example.inappnotifications.models.RegisterDeviceRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class NotificationPosition {
    TOP, CENTER, BOTTOM
}

object InAppNotifier {
    private const val BASE_URL = "https://in-app-notifications-api.vercel.app/"
    private const val TAG = "InAppNotifier"

    private var isInitialized = false
    private var currentUserId = ""
    private var currentDeviceName = ""
    private var currentDeviceId = ""

    private lateinit var apiService: NotificationApiService

    fun initialize(context: Context, userId: String) {
        if (isInitialized) return

        currentUserId = userId
        currentDeviceName = Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME) ?: "unknown_device"
        currentDeviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
        apiService = ApiClient.create(BASE_URL)

        isInitialized = true
        Log.d(TAG, "SDK Initialized: User $currentUserId")

        setupGlobalCrashHandler()
    }

    private fun setupGlobalCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            val crashDetails = Log.getStackTraceString(exception)

            runBlocking(Dispatchers.IO) {
                reportCrash(crashDetails)
            }

            defaultHandler?.uncaughtException(thread, exception)
        }
    }

    suspend fun registerDevice(): Boolean {
        if (!isInitialized) return false
        return try {
            val request = RegisterDeviceRequest(currentDeviceName, currentDeviceId, currentUserId)
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
            if (response.isSuccessful) response.body()?.notifications else null
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
        imageUrl: String? = null,
        fallbackImageRes: Int = R.drawable.ic_notification,
        positiveButtonText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        onNegativeClick: (() -> Unit)? = null,
        neutralButtonText: String? = null,
        onNeutralClick: (() -> Unit)? = null
    ) {
        if (!isInitialized) return

        if (notification.status == "read") {
            Log.d(TAG, "Notification ${notification._id} already handled.")
            return
        }

        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.dialog_custom_notification, null)

        val titleView = customView.findViewById<TextView>(R.id.dialogTitle)
        titleView.text = notification.title

        val messageView = customView.findViewById<TextView>(R.id.dialogMessage)
        if (!notification.message.isNullOrEmpty()) {
            messageView.text = notification.message
            messageView.visibility = View.VISIBLE
        }

        if (position == NotificationPosition.TOP || position == NotificationPosition.BOTTOM) {

            val imageView = customView.findViewById<ImageView>(R.id.dialogImage)
            if (!imageUrl.isNullOrEmpty()) {
                imageView.visibility = View.VISIBLE
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(fallbackImageRes)
                    .error(fallbackImageRes)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
            }
        }

        val builder = AlertDialog.Builder(context).setView(customView)
        val dialog = builder.create()
        var buttonCount = 0

        val btnPositive = customView.findViewById<Button>(R.id.btnPositive)
        positiveButtonText?.let { text ->
            btnPositive.text = text
            btnPositive.visibility = View.VISIBLE
            btnPositive.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch { trackInteraction(notification._id) }
                onPositiveClick?.invoke()
                dialog.dismiss()
            }
            buttonCount++
        }

        val btnNegative = customView.findViewById<Button>(R.id.btnNegative)
        negativeButtonText?.let { text ->
            btnNegative.text = text
            btnNegative.visibility = View.VISIBLE
            btnNegative.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch { trackInteraction(notification._id) }
                onNegativeClick?.invoke()
                dialog.dismiss()
            }
            buttonCount++
        }

        val btnNeutral = customView.findViewById<Button>(R.id.btnNeutral)
        neutralButtonText?.let { text ->
            btnNeutral.text = text
            btnNeutral.visibility = View.VISIBLE
            btnNeutral.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch { trackInteraction(notification._id) }
                onNeutralClick?.invoke()
                dialog.dismiss()
            }
            buttonCount++
        }

        dialog.setCancelable(buttonCount == 0)

        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
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