package org.huho.domain.identity.generator.serializer.generator

import org.huho.domain.identity.generator.serializer.SerializerInfo
import java.io.File

class Generator(
    outputDir: File,
    packageName: String,
) {
    val serializerGenerator = SerializerGenerator(outputDir)
    val serializerProviderGenerator = SerializerProviderGenerator(outputDir, packageName)

    fun generate(serializerInfos: Set<SerializerInfo>) {
        serializerInfos.forEach { serializerGenerator.generate(it) }
        serializerProviderGenerator.generate(serializerInfos)
    }
}
