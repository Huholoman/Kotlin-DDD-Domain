package org.huho.domain.identity.generator

import org.gradle.api.file.FileCollection
import org.huho.domain.identity.generator.serializer.IdentitySerializersProcessor
import org.huho.domain.identity.generator.serializer.generator.Generator
import java.io.File

class Main {
    fun run(
        runtimeClasspath: FileCollection,
        packageName: String,
        packageToScan: String,
        outputDir: String,
    ) {
        val serializersGenerator = Generator(File(outputDir), packageName)
        val processor = IdentitySerializersProcessor(serializersGenerator)

        ClassScanner(runtimeClasspath, packageToScan, processor)
            .scan()

        try {
            processor.process()
        } catch (e: Throwable) {
            println("Error processing classes: ${e.message}")
            e.printStackTrace(System.out)
        }
    }
}
