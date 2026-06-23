# InAppNotifications

[![](https://jitpack.io/v/NereyaHillel/InAppNotificationLibrary.svg)](https://jitpack.io/#NereyaHillel/InAppNotificationLibrary)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](https://github.com/NereyaHillel/InAppNotifications)

A professional, production-ready Android library for seamlessly registering devices, fetching and displaying in-app notifications, tracking user interactions, and reporting crashes. Built entirely in Kotlin utilizing modern Android architecture (Coroutines, Retrofit, Gson, and OkHttp).

## ✨ Key Features

- 🎯 **Beautiful UI Components** - Customizable notification dialogs with multiple positioning options (TOP, CENTER, BOTTOM)
- ⚡ **Non-blocking Operations** - Full Kotlin Coroutines support for async operations
- 📊 **Interaction Tracking** - Automatic and manual tracking of user interactions
- 💥 **Crash Reporting** - Global crash handler with automatic server reporting
- 🔄 **Device Registration** - Seamless device registration with the notification server
- 🎨 **Rich Customization** - Highly customizable buttons, images, and layouts
- 📸 **Image Support** - Built-in Glide integration with fallback handling
- 🔐 **Type-Safe API** - Retrofit-based API with proper error handling
- 📱 **Wide Device Support** - Min SDK 24 to latest Android versions
- 🛡️ **Robust Error Handling** - Comprehensive error handling and logging

## 📋 Table of Contents

- [Installation](#-installation)
- [Requirements](#-requirements)
- [Quick Start](#-quick-start)
- [API Reference](#-api-reference)
- [Advanced Usage](#-advanced-usage)
- [Best Practices](#-best-practices)
- [Troubleshooting](#-troubleshooting)
- [License](#-license)

## 🚀 Installation

### Step 1: Add JitPack Repository

In your **settings.gradle.kts** (root level), add the JitPack repository:

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

In your app-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.NereyaHillel:InAppNotificationLibrary:1.1.0")
}
```

### Step 3: Sync Gradle

Click "Sync Now" in Android Studio.

---

## ⚙️ Requirements

| Requirement | Version |
|-------------|---------|
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |
| **Compile SDK** | 34 |
| **Java Version** | 17+ |
| **Kotlin Version** | 1.9.0+ |

Ensure your app's `build.gradle.kts` has Java 17:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

---

## 🏃 Quick Start

### 1️⃣ Initialize the SDK

Initialize in your `Application` class:

```kotlin
import com.example.inappnotifications.InAppNotifier

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        InAppNotifier.init(this, "user_123")
    }
}
```

### 2️⃣ Register Device

```kotlin
lifecycleScope.launch {
    val success = InAppNotifier.registerDevice()
    if (success) Log.d("Notifications", "Device registered")
}
```

### 3️⃣ Add Permissions

The following permissions are automatically declared in the library manifest:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 4️⃣ Fetch and Display Notifications

```kotlin
import com.example.inappnotifications.NotificationPosition

lifecycleScope.launch {
    // Fetch notifications
    val notifications = InAppNotifier.getNotifications()
    
    notifications?.forEach { notification ->
        // Display notification
        InAppNotifier.showNotificationPopup(
            context = this@MainActivity,
            notification = notification,
            position = NotificationPosition.CENTER,
            positiveButtonText = "Accept",
            onPositiveClick = {
                Log.d("Notifications", "User accepted")
            }
        )
    }
}
```

---

## 📚 API Reference

### InAppNotifier Methods

#### `initialize(context: Context, userId: String)`

Initializes SDK. **Must be called first.**

#### `registerDevice(): Boolean`

Registers device with server.

#### `getNotifications(): List<InAppNotification>?`

Fetches pending notifications.

#### `trackInteraction(notificationId: String, action: String): Boolean`

Records user interaction ("clicked", "dismissed", etc).

#### `showNotificationPopup(...): Unit`

Displays notification dialog with customizable buttons and positioning.

#### `reportCrash(crashDetails: String): Boolean`

Reports crashes (called automatically for unhandled exceptions).

For detailed documentation, see [complete API reference](https://nereyahillel.github.io/InAppNotificationLibrary/).

---

## 💡 Advanced Usage

### Custom Dialog Positioning

```kotlin
// Top position
InAppNotifier.showNotificationPopup(
    context, notification,
    position = NotificationPosition.TOP
)

// Center (default)
InAppNotifier.showNotificationPopup(
    context, notification,
    position = NotificationPosition.CENTER
)

// Bottom position
InAppNotifier.showNotificationPopup(
    context, notification,
    position = NotificationPosition.BOTTOM
)
```

### Complete Example with All Options

```kotlin
InAppNotifier.showNotificationPopup(
    context = this@MainActivity,
    notification = notification,
    position = NotificationPosition.CENTER,
    imageUrl = notification.imageUrl,
    fallbackImageRes = R.drawable.ic_default,
    positiveButtonText = "Accept",
    onPositiveClick = { handleAccept() },
    negativeButtonText = "Decline",
    onNegativeClick = { handleDecline() },
    neutralButtonText = "Learn More",
    onNeutralClick = { openWebsite() },
    onDismiss = { logDismissal() }
)
```

---

## 🎯 Best Practices

1. **Initialize in Application class**
   ```kotlin
   class MyApplication : Application() {
       override fun onCreate() {
           super.onCreate()
           InAppNotifier.initialize(this, userId)
       }
   }
   ```

2. **Use lifecycleScope for coroutines**
   ```kotlin
   lifecycleScope.launch {
       InAppNotifier.registerDevice()
   }
   ```

3. **Check return values for errors**
   ```kotlin
   val success = InAppNotifier.registerDevice()
   if (!success) Log.w("Notifications", "Failed")
   ```

4. **Handle null notifications**
   ```kotlin
   val notifications = InAppNotifier.getNotifications()
   notifications?.forEach { /* Process */ }
   ```

---

## 🔧 Troubleshooting

**"SDK not initialized" warning**
→ Call `InAppNotifier.initialize()` before using APIs

**Images not loading**
→ Check URL is valid and provide fallback drawable

**Dialog not showing**
→ Ensure notification status is not "read"

**Crashes not reported**
→ Crashes report automatically; check Logcat

**For debugging:**
```bash
adb logcat | grep "InAppNotifier"
```


---

## 📄 License

MIT License - see [LICENSE](LICENSE)

---

## 🏗️ Architecture & Libraries

* **Kotlin Coroutines** - Async operations
* **Retrofit 2** - HTTP API client
* **Gson** - JSON serialization
* **OkHttp** - Network layer
* **Glide** - Image loading

---

**For complete documentation and API reference, visit the [full documentation site](https://nereyahillel.github.io/InAppNotificationLibrary/)**
