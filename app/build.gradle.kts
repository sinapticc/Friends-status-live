plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

/** Build settings come from a Gradle property or an environment variable (CI). */
fun setting(name: String): String = (project.findProperty(name) as String?) ?: System.getenv(name) ?: ""

/**
 * Firebase is optional. When app/google-services.json exists (CI writes it from a secret),
 * its values are baked into BuildConfig and push is turned on; otherwise the app falls back
 * to checking every 15 minutes.
 */
val firebase: Map<String, String> = run {
    val f = file("google-services.json")
    if (!f.exists()) return@run emptyMap()
    @Suppress("UNCHECKED_CAST")
    val json = groovy.json.JsonSlurper().parse(f) as Map<String, Any?>
    val project = json["project_info"] as Map<String, Any?>
    val clients = json["client"] as List<Map<String, Any?>>
    val client = clients.firstOrNull {
        ((it["client_info"] as Map<String, Any?>)["android_client_info"] as Map<String, Any?>)["package_name"] == "com.sinapticc.friendsstatus"
    } ?: clients.first()
    mapOf(
        "appId" to ((client["client_info"] as Map<String, Any?>)["mobilesdk_app_id"] as String),
        "apiKey" to (((client["api_key"] as List<Map<String, Any?>>).first())["current_key"] as String),
        "projectId" to (project["project_id"] as String),
        "senderId" to (project["project_number"] as String),
    )
}

android {
    namespace = "com.sinapticc.friendsstatus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sinapticc.friendsstatus"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.2.0"

        buildConfigField("String", "API_URL", "\"${setting("FSL_API_URL")}\"")
        buildConfigField("String", "FIREBASE_APP_ID", "\"${firebase["appId"] ?: ""}\"")
        buildConfigField("String", "FIREBASE_API_KEY", "\"${firebase["apiKey"] ?: ""}\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"${firebase["projectId"] ?: ""}\"")
        buildConfigField("String", "FIREBASE_SENDER_ID", "\"${firebase["senderId"] ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Signed with the debug key so the release APK installs for testing.
            // Replace with a real signing config before publishing.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.zxing.core)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.firebase.messaging)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
