package org.huho.domain.aggregate.mongo.exceptions

import org.huho.domain.identity.AbstractIdentity

class AggregateNotFoundException(
    aggregateName: String,
    id: AbstractIdentity,
) : RuntimeException(
        "Aggregate [%s] with id [%s] not found."
            .format(aggregateName, id),
    )
