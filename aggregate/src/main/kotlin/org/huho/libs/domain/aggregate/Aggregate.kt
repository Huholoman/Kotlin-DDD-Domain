package org.huho.libs.domain.aggregate

import org.huho.libs.domain.identity.AbstractIdentity

abstract class Aggregate<T : AbstractIdentity> {
    abstract val id: T

    private val events: MutableList<Any> = mutableListOf()

    protected fun record(vararg newEvents: Any) {
        newEvents.forEach { event ->
            events.add(event)
            apply(event)
        }
    }

    protected abstract fun apply(event: Any)

    fun pullEvents(): List<Any> =
        events
            .toList()
            .also { events.clear() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other !is Aggregate<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
