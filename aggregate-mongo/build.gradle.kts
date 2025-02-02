plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.20-Beta1"
    `kotlin-dsl` version "5.2.0"
    `maven-publish`
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

group = "org.huho.libs.domain"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(project(":aggregate"))
    api(project(":identity"))

    implementation("org.reflections:reflections:0.10")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    api("org.mongodb:bson-kotlinx:5.3.1")

    testImplementation(kotlin("test"))
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
//            from(components["kotlin"])
//            artifact(tasks.named("sourcesJar")) {
//                classifier = "sources"
//            }
//            artifact(tasks.named("javadocJar"))
        }
    }
}

tasks.test {
    environment("MONGO_CONNECTION_STRING", "mongodb://test:test@localhost:27017")

    useJUnitPlatform()
}
