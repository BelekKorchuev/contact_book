plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.student_list"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.student_list"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // круглое изображение
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    //передача  фото, картинки и работа с ней:
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //Возможность преобразовать любой объект в необходимый формат:
    implementation ("androidx.multidex:multidex:2.0.1")
    // выбор фото из галереи и ипользование:
    implementation ("com.karumi:dexter:6.2.3")
    //Room
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
//    //расширение возможностей фрагмента:
//    implementation ("androidx.fragment:fragment:1.8.5")
}