plugins {
    id("com.android.library")
    alias(libs.plugins.maven.publish)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                // Your GitHub info
                groupId = "com.github.NereyaHillel"
                artifactId = "InAppNotifications"

                pom {
                    withXml {
                        val dependenciesNode = asNode().appendNode("dependencies")

                        configurations.api.get().dependencies.forEach { dependency ->
                            if (dependency.group != null && dependency.name != "unspecified") {
                                // FIXED TYPO: This must be "dependency", not "dependencies"
                                val dependencyNode = dependenciesNode.appendNode("dependency")
                                dependencyNode.appendNode("groupId", dependency.group)
                                dependencyNode.appendNode("artifactId", dependency.name)
                                dependencyNode.appendNode("version", dependency.version)
                                dependencyNode.appendNode("scope", "compile")
                            }
                        }

                        configurations.implementation.get().dependencies.forEach { dependency ->
                            if (dependency.group != null && dependency.name != "unspecified") {
                                val dependencyNode = dependenciesNode.appendNode("dependency")
                                dependencyNode.appendNode("groupId", dependency.group)
                                dependencyNode.appendNode("artifactId", dependency.name)
                                dependencyNode.appendNode("version", dependency.version)
                                dependencyNode.appendNode("scope", "runtime")
                            }
                        }
                    }
                }
            }
        }
    }
}

android {
    namespace = "com.example.inappnotifications"
    compileSdk = 34 // FIXED REGRESSION

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // FIXED REGRESSION
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.appcompat.v7)
    testImplementation(libs.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso.core)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)
}