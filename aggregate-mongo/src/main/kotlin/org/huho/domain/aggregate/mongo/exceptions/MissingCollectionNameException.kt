package org.huho.domain.aggregate.mongo.exceptions

class MissingCollectionNameException(
    aggregateClass: Class<*>,
) : Exception(
        "Missing CollectionName annotation for %s."
            .format(aggregateClass.getName()),
    )
