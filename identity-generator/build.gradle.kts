plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    `java-gradle-plugin`
    // Pokud chco publikovat na Gradle Plugin Portal...
    // Potřebuju to furt? podle mě jsem to přidal jako pokus,
    // když mi nefungovalo pusblishování...
    id("com.gradle.plugin-publish") version "1.2.0"
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

group = "org.huho.libs.domain"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":identity"))

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
            id = "org.huho.libs.domain.identity-generator"
            implementationClass = "org.huho.libs.domain.identity.generator.plugin.CQRSGeneratorPlugin"
        }
    }
}
