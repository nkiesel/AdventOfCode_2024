import org.jetbrains.kotlin.gradle.internal.config.LanguageFeature

plugins {
    kotlin("jvm") version "2.1.10"
    alias(libs.plugins.versions)
    alias(libs.plugins.versions.filter)
    alias(libs.plugins.versions.update)
}

version = "2024"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotest.assertions.core)
    testImplementation(platform(libs.junit.bom))
    implementation(libs.junit.jupiter)
    implementation(libs.kotlin.serialization)
}

tasks.test {
    useJUnitPlatform()
    minHeapSize = "1g"
    maxHeapSize = "30g"
    testLogging.showStandardStreams = true
    filter {
        setIncludePatterns("*Test", "Day*")
    }
}

kotlin {
    jvmToolchain(21)
    @Suppress("UnsafeCompilerArguments")
    sourceSets.all {
        languageSettings {
            enableLanguageFeature(LanguageFeature.WhenGuards.name)
            enableLanguageFeature(LanguageFeature.MultiDollarInterpolation.name)
            enableLanguageFeature(LanguageFeature.BreakContinueInInlineLambdas.name)
        }
    }
}
