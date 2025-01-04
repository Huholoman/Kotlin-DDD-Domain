package org.huho.libs.domain.identity.generator.serializer.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.huho.libs.domain.identity.generator.serializer.SerializerInfo
import java.io.File

class SerializerGenerator(
    val outputDir: File,
) {
    fun generate(serializerInfo: SerializerInfo) {
        val serializerClassName = serializerInfo.classInfo.toClassName()
        val targetClassName = serializerInfo.identityInfo.toClassName()

        // Vytvoření třídy serializeru
        val serializerClass =
            TypeSpec.Companion
                .classBuilder(serializerInfo.classInfo.className)
                .addSuperinterface(KSerializer::class.asClassName().parameterizedBy(targetClassName))
                .addFunction(
                    FunSpec.Companion
                        .builder("serialize")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("encoder", Encoder::class)
                        .addParameter("value", targetClassName)
                        .addStatement("encoder.encodeString(value.toString())")
                        .build(),
                ).addFunction(
                    FunSpec.Companion
                        .builder("deserialize")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("decoder", Decoder::class)
                        .returns(targetClassName)
                        .addStatement("val value = decoder.decodeString()")
                        .addStatement("return %T(value)", targetClassName)
                        .build(),
                ).addProperty(
                    PropertySpec
                        .builder(
                            "descriptor",
                            SerialDescriptor::class,
                            KModifier.OVERRIDE,
                        ).initializer(
                            "PrimitiveSerialDescriptor(%S, PrimitiveKind.STRING)",
                            serializerInfo.classInfo.qualifiedName(),
                        ).build(),
                ).build()

        FileSpec
            .builder(serializerInfo.classInfo.packagePath, serializerInfo.classInfo.className)
            .addType(serializerClass)
            .addImport("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor", "PrimitiveKind")
            .build()
            .writeTo(outputDir)
    }
}
