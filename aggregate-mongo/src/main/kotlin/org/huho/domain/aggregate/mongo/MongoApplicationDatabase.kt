package org.huho.domain.aggregate.mongo

import com.mongodb.kotlin.client.coroutine.MongoDatabase

open class MongoApplicationDatabase(
    val database: MongoDatabase,
)
