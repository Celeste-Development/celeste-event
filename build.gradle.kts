import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm") version "2.1.21"
}

group = "com.celeste.event"
version = "0.1"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.slf4j:slf4j-simple:2.0.7")
}

tasks {

    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
}

kotlin {
    jvmToolchain(21)
}

publishing.publications.create<MavenPublication>("maven").from(components["java"])