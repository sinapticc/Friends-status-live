// Compiles the app's shared Kotlin (everything except Android-only files) against
// Compose for Desktop, so screens can be type-checked and screenshotted without the
// Android SDK. Old Compose (1.5) on purpose: it only needs Maven Central.
plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.compose") version "1.5.12"
    kotlin("plugin.serialization") version "1.9.22"
}

sourceSets {
    main {
        kotlin.srcDir("../../app/src/main/java")
        // Android-only code; DesktopAssets.kt stands in for platform/Assets.kt.
        kotlin.exclude("**/platform/Assets.kt", "**/MainActivity.kt", "**/android/**")
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

kotlin { jvmToolchain(21) }

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs += listOf("-Dout=${layout.buildDirectory.dir("shots").get().asFile}") +
            listOfNotNull(System.getProperty("api")?.let { "-Dapi=$it" }, System.getProperty("only")?.let { "-Donly=$it" })
    }
}
