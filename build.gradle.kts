plugins {
    kotlin("jvm") version "2.1.20-Beta1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

group = "org.huho.libs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
