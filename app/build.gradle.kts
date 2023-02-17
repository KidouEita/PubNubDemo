import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "tw.com.pubnubdemo"
    compileSdk = 33

    defaultConfig {
        applicationId = "tw.com.pubnubdemo"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val pubKey = properties.getProperty("pubKey")
        val subKey = properties.getProperty("subKey")
        val secKey = properties.getProperty("secKey")

        getByName("debug") {
            buildConfigField("String", "PN_PUB_KEY", pubKey)
            buildConfigField("String", "PN_SUB_KEY", subKey)
            buildConfigField("String", "PN_SEC_KEY", secKey)
        }
        getByName("release") {
            buildConfigField("String", "PN_PUB_KEY", pubKey)
            buildConfigField("String", "PN_SUB_KEY", subKey)
            buildConfigField("String", "PN_SEC_KEY", secKey)
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    val composeVersion = rootProject.extra.get("compose_ui_version").toString()

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material:material:1.3.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // PubNub Dependencies
    implementation("com.pubnub:pubnub-kotlin:7.4.0")
    implementation("com.pubnub.components:chat-android:0.5.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.0.2"))
    implementation("com.google.firebase:firebase-messaging-ktx")

    // DaggerHilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}