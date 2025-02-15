package org.huho.domain.identity

import java.util.UUID

interface Identity {
    fun toUuid(): UUID

    override fun toString(): String
}
