package org.huho.kotlin.libs.domain.identity.generator.serializer

import org.huho.kotlin.libs.domain.identity.generator.serializer.generator.Generator
import org.huho.kotlin.libs.messenger.cqrs.generator.processor.ClassProcessor
import org.huho.libs.domain.identity.AbstractIdentity
import uy.klutter.core.collections.toImmutable
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class IdentitySerializersProcessor(
    val generator: Generator,
) : ClassProcessor {
    private var identities: MutableSet<SerializerInfo> = mutableSetOf()

    override fun handle(clazz: KClass<*>) {
        if (!clazz.isSubclassOf(AbstractIdentity::class)) {
            return
        }

        val classInfo = createClassInfo(clazz)
        if (classInfo == null) {
            return
        }

        identities.add(classInfo)
    }

    override fun process() {
        generator.generate(identities.toImmutable())
    }
}

private fun createClassInfo(clazz: KClass<*>): SerializerInfo? {
    val qualifiedName = clazz.qualifiedName
    val packageName = qualifiedName?.substringBeforeLast('.', missingDelimiterValue = "")
    val className = clazz.simpleName

    if (className === null || packageName === null) {
        return null
    }

    return SerializerInfo(
        ClassInfo(packageName, "${className}Serializer"),
        ClassInfo(packageName, className),
    )
}
