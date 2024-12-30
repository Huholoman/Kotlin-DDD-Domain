plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.0" // Pokud chcete publikovat na Gradle Plugin Portal
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

sourceSets {
    main {
//        java.srcDir("src/main/java")
        kotlin.srcDir("src/main/kotlin")
    }
}

group = "org.huho.kotlin.libs.domain"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.huho.kotlin.libs:identity:0.1")

    implementation("io.insert-koin:koin-annotations:2.0.0-Beta2")
    implementation("io.insert-koin:koin-core:4.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    implementation("com.squareup:kotlinpoet-jvm:2.0.0")
    implementation("uy.kohesive.klutter:klutter-core:3.0.0")

    implementation("io.github.classgraph:classgraph:4.8.179")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("generateIdentitySerializers") {
            id = "org.huho.kotlin.libs.domain.identity-generator"
            implementationClass = "org.huho.kotlin.libs.domain.identity.generator.plugin.CQRSGeneratorPlugin"
        }
    }
}
