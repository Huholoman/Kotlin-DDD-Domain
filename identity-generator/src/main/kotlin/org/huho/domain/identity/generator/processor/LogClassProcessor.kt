package org.huho.messenger.cqrs.generator.processor

import kotlin.reflect.KClass

class LogClassProcessor : ClassProcessor {
    private var clazzes: MutableSet<KClass<*>> = mutableSetOf()

    override fun handle(clazz: KClass<*>) {
        clazzes.add(clazz)
    }

    override fun process() {
        println("")
        println("# Registered Services:")

        clazzes.forEach {
            println(" - ${it.qualifiedName}")
        }
    }
}
