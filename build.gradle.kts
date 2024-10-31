plugins {
    alias(libs.plugins.android.application) apply false

}

buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.google.services)
    }
}

allprojects {
    // No agregar repositorios aquí, ya están configurados en settings.gradle.kts
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}