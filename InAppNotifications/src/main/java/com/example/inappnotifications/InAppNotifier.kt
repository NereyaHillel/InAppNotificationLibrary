package com.example.inappnotifications

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

/**
 * Main singleton object for managing in-app notifications.
 *
 * This object handles initialization, notification retrieval, display, and interaction tracking
 * with the remote notification API. It also manages global crash reporting.
 *
 * Usage:
 * ```
 * InAppNotifier.initialize(context, "user123")
 * InAppNotifier.registerDevice()
 * val notifications = InAppNotifier.getNotifications()
 * InAppNotifier.showNotificationPopup(context, notification)
 * ```
 */
@Suppress("unused")
object InAppNotifier {
    // API Configuration
    private const val BASE_URL = "https://in-app-notifications-api.vercel.app/"
    private const val TAG = "InAppNotifier"

    // Dialog UI Constants
    private const val WINDOW_PADDING_DP = 48
    private const val POSITION_OFFSET_DP = 80

    // State Management
    private var isInitialized = false
    private var currentUserId = ""
    private var currentDeviceModel = ""
    private var currentDeviceId = ""

    private lateinit var apiService: NotificationApiService

    /**
     * Initializes the InAppNotifier SDK with the provided context and user ID.
     *
     * This method must be called before using any other functionality.
     * It retrieves device information, instantiates the API service, and sets up
     * a global crash handler.
     *
     * @param context The Android application context.
     * @param userId The unique identifier for the current user.
     * @throws IllegalStateException if called multiple times (idempotent).
     */
    fun init(context: Context, userId: String) {
        if (isInitialized) {
            Log.d(TAG, "InAppNotifier already initialized")
            return
        }

        currentUserId = userId
        currentDeviceModel = android.os.Build.MODEL ?: "unknown_model"
        @Suppress("HardwareIds")
        currentDeviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown_device"
        apiService = ApiClient.create(BASE_URL)

        isInitialized = true
        Log.d(TAG, "SDK Initialized: User $currentUserId, Device $currentDeviceId")

        setupGlobalCrashHandler()
    }

    /**
     * Sets up a global uncaught exception handler for crash reporting.
     *
     * This handler intercepts all unhandled exceptions, reports them to the server,
     * and then delegates to the default exception handler.
     */
    private fun setupGlobalCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            val crashDetails = Log.getStackTraceString(exception)
            Log.e(TAG, "Uncaught exception in thread ${thread.name}", exception)

            runBlocking(Dispatchers.IO) {
                reportCrash(crashDetails)
            }

            defaultHandler?.uncaughtException(thread, exception)
        }
    }

    /**
     * Registers the current device with the notification server.
     *
     * @return true if registration was successful, false otherwise.
     * @throws IllegalStateException if SDK is not initialized.
     */
    suspend fun registerDevice(): Boolean {
        if (!isInitialized) {
            Log.w(TAG, "Cannot register device: SDK not initialized")
            return false
        }
        return try {
            val request = RegisterDeviceRequest(currentDeviceModel, currentDeviceId, currentUserId)
            val response = apiService.registerDevice(request)
            if (response.isSuccessful) {
                Log.d(TAG, "Device registered successfully")
                true
            } else {
                Log.w(TAG, "Device registration failed with code ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Device registration exception", e)
            false
        }
    }

    /**
     * Fetches in-app notifications for the current user from the server.
     *
     * @return A list of [InAppNotification] objects if successful, null otherwise.
     * @throws IllegalStateException if SDK is not initialized.
     */
    suspend fun getNotifications(): List<InAppNotification>? {
        if (!isInitialized) {
            Log.w(TAG, "Cannot fetch notifications: SDK not initialized")
            return null
        }
        return try {
            val response = apiService.getNotifications(currentUserId)
            if (response.isSuccessful) {
                val notifications = response.body()?.notifications
                Log.d(TAG, "Retrieved ${notifications?.size ?: 0} notifications")
                notifications
            } else {
                Log.w(TAG, "Failed to fetch notifications: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching notifications", e)
            null
        }
    }

    /**
     * Tracks user interaction with a notification.
     *
     * @param notificationId The ID of the notification that was interacted with.
     * @param action The action type (e.g., "clicked", "dismissed").
     * @return true if tracking was successful, false otherwise.
     * @throws IllegalStateException if SDK is not initialized.
     */
    suspend fun trackInteraction(notificationId: String, action: String): Boolean {
        if (!isInitialized) {
            Log.w(TAG, "Cannot track interaction: SDK not initialized")
            return false
        }
        return try {
            val response = apiService.trackInteraction(notificationId, action)
            if (response.isSuccessful) {
                Log.d(TAG, "Tracked interaction: notification=$notificationId, action=$action")
                true
            } else {
                Log.w(TAG, "Failed to track interaction: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while tracking interaction", e)
            false
        }
    }

    /**
     * Reports a crash to the notification server.
     *
     * @param crashDetails The stack trace and details of the crash.
     * @return true if the report was sent successfully, false otherwise.
     * @throws IllegalStateException if SDK is not initialized.
     */
    suspend fun reportCrash(crashDetails: String): Boolean {
        if (!isInitialized) {
            Log.w(TAG, "Cannot report crash: SDK not initialized")
            return false
        }
        return try {
            val request = CrashReportRequest(currentUserId, crashDetails)
            val response = apiService.reportCrash(request)
            if (response.isSuccessful) {
                Log.d(TAG, "Crash report sent successfully")
                true
            } else {
                Log.w(TAG, "Failed to send crash report: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while reporting crash", e)
            false
        }
    }

    /**
     * Displays an in-app notification popup dialog with customizable content and buttons.
     *
     * This method creates and displays a styled dialog with the notification content.
     * The dialog position and button visibility are determined by the provided parameters.
     *
     * @param context The Android application context.
     * @param notification The [InAppNotification] object to display.
     * @param position The position where the dialog should appear (default: CENTER).
     * @param imageUrl Optional URL of an image to display in the dialog.
     * @param fallbackImageRes The drawable resource ID to use if imageUrl fails to load.
     * @param positiveButtonText Optional text for the positive button.
     * @param onPositiveClick Optional callback invoked when positive button is clicked.
     * @param negativeButtonText Optional text for the negative button.
     * @param onNegativeClick Optional callback invoked when negative button is clicked.
     * @param neutralButtonText Optional text for the neutral button.
     * @param onNeutralClick Optional callback invoked when neutral button is clicked.
     * @param onDismiss Optional callback invoked when dialog is dismissed.
     */
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
        onNeutralClick: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        if (!isInitialized) {
            Log.w(TAG, "Cannot show notification: SDK not initialized")
            return
        }

        if (notification.status == "read") {
            Log.d(TAG, "Skipping already-read notification ${notification._id}")
            return
        }

        try {
            val inflater = LayoutInflater.from(context)
            @Suppress("InflateParams")
            val customView = inflater.inflate(R.layout.dialog_custom_notification, null)

            // Setup title
            val titleView = customView.findViewById<TextView>(R.id.dialogTitle)
            titleView.text = notification.title

            // Setup message
            val messageView = customView.findViewById<TextView>(R.id.dialogMessage)
            if (notification.message.isNotEmpty()) {
                messageView.text = notification.message
                messageView.visibility = View.VISIBLE
            } else {
                messageView.visibility = View.GONE
            }

            // Setup image for CENTER position
            if (position == NotificationPosition.CENTER) {
                val imageView = customView.findViewById<ImageView>(R.id.dialogImage)
                if (!imageUrl.isNullOrEmpty()) {
                    imageView.visibility = View.VISIBLE
                    loadImageWithGlide(context, imageUrl, fallbackImageRes, imageView)
                }
            }

            // Create dialog
            val dialog = android.app.Dialog(context)
            dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
            dialog.setContentView(customView)

            dialog.setOnDismissListener {
                onDismiss?.invoke()
            }

            // Setup buttons
            var buttonCount = 0

            val btnPositive = customView.findViewById<Button>(R.id.btnPositive)
            if (positiveButtonText != null) {
                btnPositive.text = positiveButtonText
                btnPositive.visibility = View.VISIBLE
                btnPositive.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        trackInteraction(notification._id, "clicked")
                    }
                    onPositiveClick?.invoke()
                    dialog.dismiss()
                }
                buttonCount++
            } else {
                btnPositive.visibility = View.GONE
            }

            val btnNegative = customView.findViewById<Button>(R.id.btnNegative)
            if (negativeButtonText != null) {
                btnNegative.text = negativeButtonText
                btnNegative.visibility = View.VISIBLE
                btnNegative.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        trackInteraction(notification._id, "dismissed")
                    }
                    onNegativeClick?.invoke()
                    dialog.dismiss()
                }
                buttonCount++
            } else {
                btnNegative.visibility = View.GONE
            }

            val btnNeutral = customView.findViewById<Button>(R.id.btnNeutral)
            if (neutralButtonText != null) {
                btnNeutral.text = neutralButtonText
                btnNeutral.visibility = View.VISIBLE
                btnNeutral.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        trackInteraction(notification._id, "dismissed")
                    }
                    onNeutralClick?.invoke()
                    dialog.dismiss()
                }
                buttonCount++
            } else {
                btnNeutral.visibility = View.GONE
            }

            dialog.setCancelable(buttonCount == 0)

            // Configure window
            dialog.window?.let { window ->
                window.setBackgroundDrawableResource(android.R.color.transparent)

                window.setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                )
                window.decorView.setPadding(WINDOW_PADDING_DP, WINDOW_PADDING_DP, WINDOW_PADDING_DP, WINDOW_PADDING_DP)

                val layoutParams = window.attributes
                when (position) {
                    NotificationPosition.TOP -> {
                        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                        layoutParams.y = POSITION_OFFSET_DP
                    }
                    NotificationPosition.BOTTOM -> {
                        layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                        layoutParams.y = POSITION_OFFSET_DP
                    }
                    NotificationPosition.CENTER -> {
                        layoutParams.gravity = Gravity.CENTER
                    }
                }
                window.attributes = layoutParams
            }
            dialog.show()
            Log.d(TAG, "Displayed notification dialog for notification ${notification._id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying notification popup", e)
        }
    }

    /**
     * Loads an image using Glide with fallback handling.
     *
     * @param context The Android application context.
     * @param imageUrl The URL of the image to load.
     * @param fallbackImageRes The fallback drawable resource ID.
     * @param imageView The ImageView to load the image into.
     */
    private fun loadImageWithGlide(
        context: Context,
        imageUrl: String,
        fallbackImageRes: Int,
        imageView: ImageView
    ) {
        try {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(fallbackImageRes)
                .error(fallbackImageRes)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image with Glide", e)
            imageView.visibility = View.GONE
        }
    }
}