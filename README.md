# InAppNotifications


[![](https://jitpack.io/v/NereyaHillel/InAppNotificationLibrary.svg)](https://jitpack.io/#NereyaHillel/InAppNotificationLibrary)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)

A professional, production-ready Android library implementing **Server-Driven UI (SDUI)** architecture for in-app notifications. The backend controls all UI aspects (buttons, positioning, images, deep links) while the SDK acts as an autonomous rendering and routing engine. Built entirely in Kotlin with modern Android architecture (Coroutines, Retrofit, Gson, OkHttp, Glide).

<div align="center">
  <video src="https://github.com/user-attachments/assets/4a8af908-11f1-4768-bbb3-f69fb2b55471" width="100%" controls autoplay loop muted></video>
</div>
---

## Key Features

- **Server-Driven UI** - Backend controls all visual layouts, button texts, positions, and deep links
- **Automatic Routing** - Internal deep-link engine with URL normalization and Intent handling
- **Beautiful UI Components** - Customizable notification dialogs with TOP, CENTER, BOTTOM positioning
- **Non-blocking Operations** - Full Kotlin Coroutines support for async operations
- **Interaction Tracking** - Automatic tracking of user interactions (clicked, dismissed)
- **Crash Reporting** - Global crash handler with automatic server reporting
- **Device Registration** - Seamless device model and ID registration
- **Image Support** - Built-in Glide integration with fallback handling
- **Type-Safe API** - Retrofit-based API with comprehensive error handling
- **Wide Device Support** - Min SDK 24 to latest Android versions
- **Minimal Code** - 93% less boilerplate compared to traditional implementations

---
## Portal Link
### 🌟 Portal Capabilities
* **Visual Campaign Builder:** Instantly push new messages, images, and button texts to all registered Android devices.
* **Dynamic Routing Engine:** Define custom deep links (`myapp://`), App Links (`https://`), or Google Play Store links (`market://`) that the SDK will autonomously parse and execute.
* **Layout Control:** Change the visual presentation layer on the fly (TOP, CENTER, or BOTTOM positioning).
* **Live Analytics:** Track real-time Delivery Rates, Open Rates, and Click-Through Rates (CTR) across all active campaigns.

[🚀 **Access the Live Admin Portal**](https://in-app-notifications-api.vercel.app/)

---

## Table of Contents

- [Installation](#installation)
- [Requirements](#requirements)
- [Quick Start](#quick-start)
- [Server-Driven UI (SDUI)](#server-driven-ui-sdui)
- [API Reference](#api-reference)
- [Advanced Usage](#advanced-usage)
- [Backend Integration](#backend-integration)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [License](#license)
- [Links & Resources](#-links--resources)

---

## Installation

### Step 1: Add JitPack Repository

**File:** `settings.gradle.kts` (root level)

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add Dependency

**File:** `build.gradle.kts` (app-level)

```kotlin
dependencies {
    implementation("com.github.NereyaHillel:InAppNotificationLibrary:TAG")
}
```

### Step 3: Sync Project

Click **"Sync Now"** in Android Studio.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |
| **Compile SDK** | 34 |
| **Java Version** | 17+ |
| **Kotlin Version** | 1.9.0+ |

**File:** `build.gradle.kts` (app-level)

```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
```

---

## Quick Start

### 1. Initialize the SDK

**File:** `MyApplication.kt` (Application class)

```kotlin
import android.app.Application
import com.example.inappnotifications.InAppNotifier

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize with user ID
        InAppNotifier.init(this, "user_123")
    }
}
```

**Important:** Register your Application class in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

### 2. Register Device

**File:** `MainActivity.kt` (Activity or Fragment)

```kotlin
import androidx.lifecycle.lifecycleScope
import com.example.inappnotifications.InAppNotifier
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Register device with the server
        lifecycleScope.launch {
            val success = InAppNotifier.registerDevice()
            if (success) {
                Log.d("App", "Device registered successfully")
                fetchNotifications()
            }
        }
    }
}
```

### 3. Fetch and Display Notifications (SDUI)

**File:** `MainActivity.kt` (Activity or Fragment)

```kotlin
private fun fetchNotifications() {
    lifecycleScope.launch {
        // Fetch notifications from server
        val notifications = InAppNotifier.getNotifications()
        
        if (notifications.isNullOrEmpty()) {
            Log.d("App", "No pending notifications")
            return@launch
        }
        
        // Display notifications - SDK handles everything from JSON payload
        notifications.forEach { notification ->
            InAppNotifier.showNotificationPopup(
                context = this@MainActivity,
                notification = notification,
                onDismiss = {
                    Log.d("App", "Notification dismissed")
                }
            )
        }
    }
}
```

**That's it!** The backend now controls all UI aspects through JSON.

---

## Server-Driven UI (SDUI)

### What is SDUI?

The backend JSON payload dictates:
- **Dialog Position** (`position`: "TOP" | "CENTER" | "BOTTOM")
- **Button Text** (`btn_positive`, `btn_negative`, `btn_neutral`)
- **Images** (`image_url`)
- **Deep Links** (`link` - opened automatically on positive button click)

### Backend JSON Structure

```json
{
  "message": "Unread notifications retrieved successfully",
  "notifications": [
    {
      "_id": "notif_123",
      "campaign_id": "camp_456",
      "title": "Special Offer!",
      "status": "delivered",
      
      // SDUI Fields (all optional)
      "message": "Get 50% off your next purchase",
      "position": "CENTER",
      "image_url": "https://example.com/offer.jpg",
      "link": "https://example.com/shop/special-offer",
      "btn_positive": "Shop Now",
      "btn_negative": "Maybe Later",
      "btn_neutral": null
    }
  ]
}
```

### SDUI Benefits

✅ **For Developers:** 93% less code, no UI configuration needed  
✅ **For Product Teams:** Change UI without app deployment  
✅ **For Marketing:** Real-time A/B testing and campaign updates  
✅ **For Engineering:** Professional architecture, minimal technical debt  

---

## API Reference

### InAppNotifier Methods

#### `init(context: Context, userId: String)`

Initializes the SDK. **Must be called first** (typically in Application class).

**Parameters:**
- `context`: Android application context
- `userId`: Unique identifier for the current user

**Example:** See [Quick Start](#quick-start)

---

#### `registerDevice(): Boolean`

Registers the device with the notification server. Sends device model and ID.

**Returns:** `true` if registration successful, `false` otherwise

**File:** Activity/Fragment with coroutine scope

```kotlin
lifecycleScope.launch {
    val success = InAppNotifier.registerDevice()
    if (!success) {
        Log.w("App", "Device registration failed")
    }
}
```

---

#### `getNotifications(): List<InAppNotification>?`

Fetches unread in-app notifications for the current user.

**Returns:** List of notifications, or `null` if failed

**File:** Activity/Fragment with coroutine scope

```kotlin
lifecycleScope.launch {
    val notifications = InAppNotifier.getNotifications()
    notifications?.forEach { notification ->
        // Process notification
    }
}
```

---

#### `showNotificationPopup(...)`

Displays notification dialog using Server-Driven UI pattern. All UI configurations read from the notification object.

**Parameters:**
- `context: Context` - Android context (Activity/Fragment)
- `notification: InAppNotification` - Notification object with SDUI fields
- `fallbackImageRes: Int` - Fallback drawable (default: `R.drawable.ic_notification`)
- `onDismiss: (() -> Unit)?` - Optional callback when dialog dismissed

**File:** Activity/Fragment

```kotlin
InAppNotifier.showNotificationPopup(
    context = this,
    notification = notification,
    fallbackImageRes = R.drawable.my_custom_fallback,
    onDismiss = {
        // Handle dismissal
    }
)
```

**Note:** Position, buttons, images, and links are controlled by the backend JSON.

---

#### `trackInteraction(notificationId: String, action: String): Boolean`

Manually tracks user interaction with a notification.

**Parameters:**
- `notificationId`: The notification's unique ID
- `action`: Action type (e.g., "clicked", "dismissed", "viewed")

**Returns:** `true` if tracking successful

**File:** Activity/Fragment with coroutine scope

```kotlin
lifecycleScope.launch {
    val tracked = InAppNotifier.trackInteraction(notification._id, "viewed")
}
```

**Note:** Positive button clicks are tracked automatically.

---

#### `reportCrash(crashDetails: String): Boolean`

Reports crash details to the server. **Called automatically** for unhandled exceptions.

**Parameters:**
- `crashDetails`: Crash stack trace or details

**Returns:** `true` if report sent successfully

**Note:** Global crash handler is set up automatically during `init()`.

---

## Advanced Usage

### Sequential Notification Display

**File:** `MainActivity.kt`

```kotlin
private fun displayNotificationsSequentially() {
    lifecycleScope.launch {
        val notifications = InAppNotifier.getNotifications() ?: return@launch
        
        var currentIndex = 0
        
        fun showNext() {
            if (currentIndex < notifications.size) {
                val notification = notifications[currentIndex]
                currentIndex++
                
                InAppNotifier.showNotificationPopup(
                    context = this@MainActivity,
                    notification = notification,
                    onDismiss = { 
                        // Show next notification when current one is dismissed
                        showNext()
                    }
                )
            }
        }
        
        showNext()
    }
}
```

### Custom Fallback Image

**File:** `Activity/Fragment`

```kotlin
InAppNotifier.showNotificationPopup(
    context = this,
    notification = notification,
    fallbackImageRes = R.drawable.custom_notification_icon
)
```

### Manual Interaction Tracking

**File:** `Activity/Fragment`

```kotlin
lifecycleScope.launch {
    // Track custom events
    InAppNotifier.trackInteraction(notification._id, "viewed")
    InAppNotifier.trackInteraction(notification._id, "shared")
}
```

---

## ⚙️ Backend Integration

This SDK requires a compatible backend to serve the Server-Driven UI (SDUI) JSON payloads, manage campaigns, and track analytics.

**Don't want to build it from scratch?**
The complete, production-ready Python/Flask backend and Admin Portal used in this project is open-source and available here:
🔗 [**GitHub: NereyaHillel/InAppNotificationsAPI**](https://github.com/NereyaHillel/InAppNotificationsAPI)

### Required API Endpoints
If you are building your own custom backend, your server must implement these exact endpoints:

#### 1. Register Device
```
POST /api/v1/sdk/device/register
Content-Type: application/json

{
  "device_model": "Pixel 6",
  "device_id": "android_device_id",
  "user_id": "user_123"
}
```

#### 2. Get Notifications
```
GET /api/v1/sdk/notifications?user_id=user_123

Response:
{
  "message": "Success",
  "notifications": [ /* SDUI notification objects */ ]
}
```

#### 3. Track Interaction
```
POST /api/v1/sdk/notifications/{id}/interact?action=clicked
```

#### 4. Report Crash
```
POST /api/v1/sdk/crash-report
Content-Type: application/json

{
  "user_id": "user_123",
  "crash_details": "Stack trace..."
}
```

### Campaign Database Structure

Your campaigns collection should include SDUI fields:

```json
{
  "_id": "ObjectId(...)",
  "name": "Summer Sale",
  "message": "Get 50% off all items!",
  "status": "active",
  "position": "BOTTOM",
  "image_url": "https://cdn.example.com/summer-sale.jpg",
  "link": "myapp://shop/summer-sale",
  "btn_positive": "Shop Now",
  "btn_negative": "Remind Me Later",
  "btn_neutral": null
}
```

---

## Best Practices

### 1. Initialize in Application Class

**File:** `MyApplication.kt`

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        InAppNotifier.init(this, getCurrentUserId())
    }
    
    private fun getCurrentUserId(): String {
        // Fetch from SharedPreferences, Firebase Auth, etc.
        return "user_123"
    }
}
```

### 2. Use Lifecycle-Aware Coroutines

**File:** Activity/Fragment

```kotlin
// ✅ Good - respects lifecycle
lifecycleScope.launch {
    InAppNotifier.registerDevice()
}

// ❌ Bad - may leak
GlobalScope.launch {
    InAppNotifier.registerDevice()
}
```

### 3. Handle Null Responses

```kotlin
val notifications = InAppNotifier.getNotifications()
if (notifications.isNullOrEmpty()) {
    // Handle empty state
    showEmptyNotificationsView()
    return@launch
}
```

### 4. Check Return Values

```kotlin
lifecycleScope.launch {
    val registered = InAppNotifier.registerDevice()
    if (!registered) {
        Log.w("App", "Registration failed - check network/server")
    }
}
```

### 5. Test with Different Payloads

Test your app with various SDUI configurations:
- No buttons (`btn_positive`, `btn_negative`, `btn_neutral` all null)
- No image (`image_url` null)
- Different positions ("TOP", "CENTER", "BOTTOM")
- Valid and invalid links

---

## Troubleshooting

### "SDK not initialized" Warning

**Cause:** Forgot to call `InAppNotifier.init()`

**Solution:** Add initialization in Application class:

**File:** `MyApplication.kt`

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        InAppNotifier.init(this, "user_id")
    }
}
```

### Buttons Not Showing

**Cause:** Backend not sending SDUI fields

**Solution:** Check your backend logs. Ensure `btn_positive`, `btn_negative` fields are included in JSON response.

### Notification Position Wrong

**Cause:** Invalid position value or null

**Solution:** Backend must send valid values: "TOP", "CENTER", or "BOTTOM" (case-insensitive). SDK defaults to CENTER if invalid.

### Images Not Loading

**Cause:** Invalid URL or network error

**Solution:**
1. Verify URL is accessible
2. Ensure INTERNET permission is granted (added automatically by library)
3. Provide a fallback image:

```kotlin
InAppNotifier.showNotificationPopup(
    context = this,
    notification = notification,
    fallbackImageRes = R.drawable.fallback_image
)
```

### Deep Links Not Opening

**Cause:** Invalid URL format

**Solution:** SDK automatically normalizes URLs (adds `https://` if missing), but ensure:
- URL is valid
- App has permission to open Intent
- For custom schemes (e.g., `myapp://`), ensure Intent filters are configured

### Crashes Not Reported

**Cause:** Crash happens before initialization

**Solution:** Initialize SDK as early as possible in Application class.



### Key Technologies

- **Kotlin Coroutines** - Asynchronous operations
- **Retrofit 2.9.0** - HTTP client
- **Gson 2.10.1** - JSON serialization
- **OkHttp 4.11.0** - Network layer
- **Glide 5.0.7** - Image loading

---

## License

See the [LICENSE](LICENSE) file for details.

---

## 🔗 Links & Resources

- **Live Admin Portal:** [https://in-app-notifications-api.vercel.app/](https://in-app-notifications-api.vercel.app/)
- **Android SDK Repository:** [https://github.com/NereyaHillel/InAppNotificationLibrary](https://github.com/NereyaHillel/InAppNotificationLibrary)
- **Backend API Repository:** [https://github.com/NereyaHillel/InAppNotificationsAPI](https://github.com/NereyaHillel/InAppNotificationsAPI)
- **Documentation:** [https://nereyahillel.github.io/InAppNotificationLibrary/](https://nereyahillel.github.io/InAppNotificationLibrary/)
- **JitPack:** [https://jitpack.io/#NereyaHillel/InAppNotificationLibrary](https://jitpack.io/#NereyaHillel/InAppNotificationLibrary)
---

**Built with ♥ for Android developers | Server-Driven UI Architecture | Production-Ready**
