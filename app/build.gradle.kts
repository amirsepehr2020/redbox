import java.net.URL

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val downloadVazirmatn = tasks.register("downloadVazirmatn") {
    doLast {
        val fontDir = file("$projectDir/src/main/res/font")
        fontDir.mkdirs()
        val fonts = mapOf(
            "vazirmatn_regular.ttf" to "https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Regular.ttf",
            "vazirmatn_bold.ttf" to "https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Bold.ttf"
        )
        fonts.forEach { (name, url) ->
            val target = file("$fontDir/$name")
            if (!target.exists() || target.length() < 1000) URL(url).openStream().use { input -> target.outputStream().use { output -> input.copyTo(output) } }
        }
    }
}
tasks.named("preBuild") { dependsOn(downloadVazirmatn) }

android {
    namespace = "ir.redlighte.redbox"
    compileSdk = 35
    defaultConfig { applicationId = "ir.redlighte.redbox"; minSdk = 26; targetSdk = 35; versionCode = 11; versionName = "0.11.0" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    sourceSets["main"].java.exclude("**/MainActivity.kt")
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
