# InAppNotifications

[![](https://jitpack.io/v/NereyaHillel/InAppNotificationLibrary.svg)](https://jitpack.io/#NereyaHillel/InAppNotificationLibrary)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)

A lightweight, modern Android SDK for seamlessly registering devices, fetching in-app notifications, tracking interactions, and reporting crashes. Built entirely in Kotlin utilizing modern Android architecture (Coroutines, Retrofit, and Gson).

## 🚀 Installation

### 1. Add the JitPack repository
In your **client app's** `settings.gradle.kts` (or root `build.gradle.kts`), add the JitPack repository:

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

### 2. Add the dependency
In your app-level `build.gradle.kts` (`Module :app`), add the SDK:

```kotlin
dependencies {
    implementation("com.github.NereyaHillel:InAppNotificationLibrary:1.0.1")
}
```

---

## 🛠️ Requirements
* **Min SDK:** 24 | **Compile SDK:** 34
* **Java 17:** Ensure `sourceCompatibility` and `targetCompatibility` are set to `JavaVersion.VERSION_17` in your `app/build.gradle.kts`.

---

## 💻 Usage

### 1. Initialization
Initialize the SDK once, ideally in your `Application` class or main `Activity`. The SDK automatically securely fetches the device ID for you.

```kotlin
import com.example.inappnotifications.InAppNotifier

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        InAppNotifier.initialize(
            context = this,
            userId = "EXAMPLE_USER_ID" // The logged-in user's ID
        )
    }
}
```

### 2. Registering the Device
Once initialized, register the device to your backend so it can start receiving targeted campaigns. All network calls are `suspend` functions and must be run inside a Coroutine.

```kotlin
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

lifecycleScope.launch {
    val success = InAppNotifier.registerDevice()
    if (success) {
        println("Device successfully registered!")
    }
}
```

### 3. Add Required Permissions
Add the Internet permission to your client app's AndroidManifest.xml:
```kotlin
<uses-permission android:name="android.permission.INTERNET" />
```

### 4. Fetching and Displaying Notifications
You can fetch pending notifications and easily display them using the built-in UI popup builder.

```kotlin
import com.example.inappnotifications.NotificationPosition

lifecycleScope.launch {
    // 1. Fetch notifications from the backend
    val notifications = InAppNotifier.getNotifications()
    
    if (!notifications.isNullOrEmpty()) {
        val firstNotification = notifications.first()
        
        // 2. Display a built-in alert dialog
        InAppNotifier.showNotificationPopup(
            context = this@MainActivity,
            notification = firstNotification,
            position = NotificationPosition.TOP, // TOP, CENTER, or BOTTOM
            positiveButtonText = "Got it!",
            onPositiveClick = {
                println("User dismissed the notification.")
            }
        )
    }
}
```
*Note: The `showNotificationPopup` automatically tracks the interaction (sends a tracking ping to the backend) when the user clicks the positive button!*

### 5. Manual Interaction Tracking
If you choose to build your own custom UI instead of using `showNotificationPopup`, you can manually track interactions when a user clicks your custom notification.

```kotlin
lifecycleScope.launch {
    val tracked = InAppNotifier.trackInteraction(notificationId = "notif_456")
}
```

### 6. Crash Reporting
The SDK includes a simple endpoint for logging crash details or caught exceptions to your backend.

```kotlin
try {
    // Some risky operation
} catch (e: Exception) {
    lifecycleScope.launch {
        InAppNotifier.reportCrash("Exception caught in MainActivity: ${e.message}")
    }
}
```

---

## 🏗️ Architecture & Libraries Used
* **Kotlin Coroutines:** For non-blocking, asynchronous network calls.
* **Retrofit 2:** For type-safe HTTP API requests.
* **Gson:** For JSON serialization and deserialization.
