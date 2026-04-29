plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}
