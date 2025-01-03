package org.huho.libs.domain.identity

import java.util.Objects
import java.util.UUID

abstract class AbstractIdentity (
    val uuid: UUID
) {
    constructor(uuid: String) : this(UUID.fromString(uuid))
    constructor() : this(UUID.randomUUID())
    constructor(otherIdentity: AbstractIdentity): this(otherIdentity.uuid)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractIdentity
        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(javaClass, uuid)
    }

    override fun toString(): String {
        return uuid.toString()
    }
}
