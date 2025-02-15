plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.20-Beta1"
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.3.1"
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

sourceSets {
    main {
        kotlin.srcDir("src/main/kotlin")
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

    implementation("io.insert-koin:koin-annotations:2.0.0-Beta2")
    implementation("io.insert-koin:koin-core:4.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

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
            id = "org.huho.domain.identity-generator"
            implementationClass = "org.huho.domain.identity.generator.plugin.CQRSGeneratorPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Huholoman/Kotlin-DDD-Domain")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
