package org.huho.domain.identity.generator.serializer.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import kotlinx.serialization.modules.SerializersModuleBuilder
import org.huho.domain.identity.generator.serializer.SerializerInfo
import java.io.File

class SerializerProviderGenerator(
    val outputDir: File,
    val packageName: String,
) {
    fun generate(infos: Set<SerializerInfo>) {
        val functionBuilder =
            FunSpec
                .builder("registerIdentities")
                .addParameter("moduleBuilder", SerializersModuleBuilder::class)
                .addStatement("moduleBuilder.apply {")

        infos.forEach {
            functionBuilder.addStatement(
                "    contextual(%T::class, %T())",
                it.identityInfo.toClassName(),
                it.classInfo.toClassName(),
            )
        }

        functionBuilder.addStatement("}")

        FileSpec
            .builder("$packageName.config", "RegisterIdentity")
            .addFunction(functionBuilder.build())
            .build()
            .writeTo(outputDir)
    }
}
