import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"

}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven{
        url = uri("https://maven.pkg.jetbrains.space/data2viz/p/maven/dev")
    }
    maven{
        url = uri("https://maven.pkg.jetbrains.space/data2viz/p/maven/public")
    }
    google()
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")

            dependencies {
                implementation(compose.html.core)
                implementation(compose.runtime)

                implementation("app.softwork:routing-compose:0.4.0")

                implementation("io.ktor:ktor-client-core:2.3.4") // Para cliente HTTP genérico
                implementation("io.ktor:ktor-client-js:2.3.4") // Para funcionar no navegador
                implementation("io.ktor:ktor-client-content-negotiation:2.3.4") // Para trabalhar com JSON
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4") // Para Kotlin Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0") // Para JSON
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.9.1")

            }
        }
    }
}
