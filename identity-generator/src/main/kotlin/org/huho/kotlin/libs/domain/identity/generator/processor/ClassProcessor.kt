package org.huho.kotlin.libs.messenger.cqrs.generator.processor

import kotlin.reflect.KClass

interface ClassProcessor {
    fun handle(clazz: KClass<*>)

    fun process()
}
