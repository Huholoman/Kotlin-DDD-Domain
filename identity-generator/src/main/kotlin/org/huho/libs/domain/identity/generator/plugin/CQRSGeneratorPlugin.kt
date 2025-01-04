package org.huho.libs.domain.identity.generator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

open class CQRSGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val configuration = target.extensions.create("identitySerializerGenerator", CQRSGeneratorConfiguration::class.java)

        val sourceSets = target.extensions.getByType(SourceSetContainer::class.java)
        val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        val generateIdentitySerializersTask =
            target.tasks.register("generate-identity-serializers", CQRSGeneratorTask::class.java) {
                it.group = "generators"
                it.description = "Generates Identity serializers and its provider for koin."
                it.configuration = configuration
                it.runtimeClasspath = mainSourceSet.runtimeClasspath + mainSourceSet.runtimeClasspath
                it.outputDir =
                    target.layout.buildDirectory
                        .dir(BUILD_PATH)
                        .get()
                        .asFile.absolutePath
            }

        target.afterEvaluate {
            mainSourceSet.java.srcDir(
                target.layout.buildDirectory
                    .dir(BUILD_PATH)
                    .get(),
            )
        }

        target.tasks.named("build").configure {
            it.dependsOn(generateIdentitySerializersTask)
        }
    }

    companion object {
        const val BUILD_PATH = "generated/identities/internalhandlers"
    }
}
