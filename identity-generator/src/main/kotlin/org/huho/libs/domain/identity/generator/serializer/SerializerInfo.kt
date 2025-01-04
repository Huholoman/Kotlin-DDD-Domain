package org.huho.libs.domain.identity.generator.serializer

data class SerializerInfo(
    val classInfo: ClassInfo,
    val identityInfo: ClassInfo,
)

data class ClassInfo(
    val packagePath: String,
    val className: String,
) {
    fun qualifiedName(): String = "$packagePath.$className"
}
