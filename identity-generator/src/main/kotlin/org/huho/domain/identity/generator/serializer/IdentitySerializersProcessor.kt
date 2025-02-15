package org.huho.domain.identity.generator.serializer

import org.huho.domain.identity.AbstractIdentity
import org.huho.domain.identity.generator.serializer.generator.Generator
import org.huho.messenger.cqrs.generator.processor.ClassProcessor
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
