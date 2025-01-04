package org.huho.libs.domain.aggregate.mongo.exceptions

import org.huho.kotlin.libs.identity.AbstractIdentity

class AggregateNotFoundException(
    aggregateName: String,
    id: AbstractIdentity,
) : RuntimeException(
        "Aggregate [%s] with id [%s] not found."
            .format(aggregateName, id),
    )
