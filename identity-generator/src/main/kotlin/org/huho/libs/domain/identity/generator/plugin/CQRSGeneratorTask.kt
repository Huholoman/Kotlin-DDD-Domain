package org.huho.libs.domain.identity.generator.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.huho.libs.domain.identity.generator.Main

open class CQRSGeneratorTask : DefaultTask() {
    init {
        group = "generators"
        description = "Generates Serializers for all identities.."
    }

    @Input
    lateinit var configuration: CQRSGeneratorConfiguration

    @InputFiles
    lateinit var runtimeClasspath: FileCollection

    @Input
    lateinit var outputDir: String

    @TaskAction
    fun generate() {
        println("Generate CQRS handlers and koin registration.")
        println("scanning \"${configuration.packageToScan}\"")

        Main().run(
            runtimeClasspath,
            configuration.packageToScan!!,
            configuration.packageToScan!!,
//            configuration.packageName,
//            configuration.packageToScan ?: configuration.packageName,
            outputDir,
        )
    }
}
