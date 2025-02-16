package org.huho.domain.aggregate

import org.huho.domain.identity.AbstractIdentity

abstract class Aggregate<T : AbstractIdentity, Event : Any> {
    abstract val id: T

    private val events: MutableList<Event> = mutableListOf()

    protected fun record(vararg newEvents: Event) {
        newEvents.forEach { event ->
            events.add(event)
            apply(event)
        }
    }

    protected abstract fun apply(event: Event)

    fun pullEvents(): List<Event> =
        events
            .toList()
            .also { events.clear() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other !is Aggregate<*, *>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
