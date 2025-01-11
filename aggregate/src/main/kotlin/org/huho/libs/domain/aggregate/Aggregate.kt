package org.huho.libs.domain.aggregate

import org.huho.libs.domain.identity.AbstractIdentity

abstract class Aggregate<T : AbstractIdentity> {
    abstract fun getId(): T

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other !is Aggregate<*>) return false
        return getId() == other.getId()
    }

    override fun hashCode(): Int = getId().hashCode()
}
