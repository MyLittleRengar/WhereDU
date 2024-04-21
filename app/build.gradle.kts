import java.io.*
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

val properties = Properties()
properties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.project.wheredu"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.wheredu"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SERVER_IP", "${properties["api.serverip"]}")
        buildConfigField("String", "KAKAOAPI", "${properties["api.kakao_api"]}")
        buildConfigField("String", "KAKAOURL", "${properties["api.kakao_url"]}")
        buildConfigField("String", "GOOGLEMAPAPI", "${properties["api.google_map_api"]}")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.0")
    //noinspection KtxExtensionAvailable,GradleDependency
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //Loading Animations
    implementation("com.airbnb.android:lottie:6.3.0")
    //Api Server
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.1")
    //cardView
    implementation("androidx.cardview:cardview:1.0.0")
    //CircleImageview
    implementation("de.hdodenhof:circleimageview:3.1.0")
    //Kakao api
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(files("libs/libDaumMapAndroid.jar"))
    //Custom Calendar
    implementation("com.github.prolificinteractive:material-calendarview:1.6.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")
    //Image Crop
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.1")
    //Google Map
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.1")
    implementation("com.google.maps:google-maps-services:0.15.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //FireBase
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.google.firebase:firebase-analytics:21.6.2")

}