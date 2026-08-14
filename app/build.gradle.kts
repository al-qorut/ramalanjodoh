plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    //alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.parcelize)
}

android {
    namespace = "smk.adzikro.ramalanjodoh"
    compileSdk = libs.versions.compileSdk.get().toInt()


    defaultConfig {
        applicationId = "smk.adzikro.ramalanjodoh"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 26
        versionName = "4.2"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true

    }


}
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ads)
    implementation(libs.recyclerview)
    implementation(libs.glide)
    implementation(libs.bundles.navigation)
    implementation(libs.lotte)
    implementation(libs.guava)
    //firebase
    implementation(libs.bundles.firebase)
    implementation(libs.google.play.service.auth)
    implementation(libs.google.play.service.location)
   // implementation(libs.firebase.firestore.ktx)

   //IAP
    implementation(libs.billing.ktx)
    implementation(libs.bundles.appudapte)

    //proguard
    implementation(libs.bundles.proguard)
    //room
    api(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.paging)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.lifecycle)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}