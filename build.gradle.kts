plugins {
    kotlin("jvm") version "1.6.10"
}

group = "faiz"
version = "0.0.1"

repositories {
    mavenCentral()
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    implementation("io.github.monun:kommand-api:2.6.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}