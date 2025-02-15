plugins {
    id("java")
    kotlin("jvm")
    `maven-publish`
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

group = "org.huho.domain"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":identity"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
