# InAppNotifications

[![Release](https://jitpack.io/v/NereyaHillel/InAppNotifications.svg)](https://jitpack.io/#NereyaHillel/InAppNotifications)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)

A lightweight, modern Android SDK for seamlessly registering users and handling in-app notifications. Built entirely in Kotlin utilizing modern Android architecture (Coroutines, Retrofit, and Gson).

## 🚀 Installation

### 1. Add the JitPack repository
In your **client app's** `settings.gradle.kts` (or root `build.gradle.kts`), add the JitPack repository:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("[https://jitpack.io](https://jitpack.io)") }
    }
}
```

### 2. Add the dependency
In your app-level `build.gradle.kts` (`Module :app`), add the SDK:

```kotlin
dependencies {
    implementation("com.github.NereyaHillel:InAppNotifications:v1.0.1")
}
```

---

## 🛠️ Requirements
* **Min SDK:** 24 | **Compile SDK:** 34
* **Java 17:** Ensure `sourceCompatibility` and `targetCompatibility` are set to `JavaVersion.VERSION_17` in your `app/build.gradle.kts`.

---

## 💻 Usage

### Initialization
Initialize the SDK in your `Application` class or main `Activity`.

```kotlin
import com.example.inappnotifications.InAppNotifier

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        InAppNotifier.initialize(context = this, apiKey = "YOUR_API_KEY")
    }
}
```

### Registering a User
```kotlin
import com.example.inappnotifications.models.RegisterRequest
import kotlinx.coroutines.launch

launch {
    val request = RegisterRequest(userId = "user_123", deviceToken = "device_fcm_token")
    if (InAppNotifier.registerUser(request)) {
        println("User registered!")
    }
}
```

### Handling Interactions
```kotlin
import com.example.inappnotifications.models.InteractionRequest

val interaction = InteractionRequest(notificationId = "notif_456", actionTaken = "CLICKED")
InAppNotifier.logInteraction(interaction)
```

---

## 🏗️ Architecture & Libraries Used
* **Kotlin Coroutines:** For non-blocking, asynchronous network calls.
* **Retrofit 2:** For type-safe HTTP API requests.
* **Gson:** For JSON serialization and deserialization.