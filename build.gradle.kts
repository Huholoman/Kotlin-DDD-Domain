plugins {
    kotlin("jvm") version "2.1.20-Beta1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

group = "org.huho"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
