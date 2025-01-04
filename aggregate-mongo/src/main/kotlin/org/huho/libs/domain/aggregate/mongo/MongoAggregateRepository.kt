package org.huho.libs.domain.aggregate.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.bson.Document
import org.huho.libs.domain.aggregate.Aggregate
import org.huho.libs.domain.aggregate.AggregateRepository
import org.huho.libs.domain.aggregate.mongo.exceptions.AggregateNotFoundException
import org.huho.libs.domain.aggregate.mongo.exceptions.MissingCollectionNameException
import org.huho.libs.domain.identity.AbstractIdentity

open class MongoAggregateRepository(
    private val database: MongoDatabase,
    private val json: Json,
) : AggregateRepository {
    constructor(
        client: MongoClient,
        json: Json,
    ) : this(
        client.getDatabase("default"),
        json,
    )

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> insert(
        aggregate: T,
        serializer: KSerializer<T>,
    ) {
        val collection = resolveCollectionFromClass(aggregate::class.java)
        val document = serialize(aggregate, serializer)
        document["_id"] = aggregate.getId().toString()
        collection.insertOne(document)
    }

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> save(
        aggregate: T,
        serializer: KSerializer<T>,
    ) {
        val collection = resolveCollectionFromClass(aggregate::class.java)
        val document = serialize(aggregate, serializer)
        collection.replaceOne(resolveDocumentId(aggregate.getId()), document)
    }

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> find(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): T? {
        val collection: MongoCollection<Document> = resolveCollectionFromClass(aggregateClass)
        val document = collection.find(Document("_id", id.toString())).firstOrNull() // MongoDB coroutine version
        return document?.let { deserialize(it, serializer) }
    }

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> get(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): T =
        find(id, aggregateClass, serializer)
            ?: throw AggregateNotFoundException(aggregateClass.name, id)

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> exists(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): Boolean = find(id, aggregateClass, serializer) != null

    private fun <ID : AbstractIdentity, T : Aggregate<ID>> resolveCollectionFromClass(aggregateClass: Class<T>): MongoCollection<Document> {
        val collectionNameAnnotation =
            aggregateClass.getAnnotation(CollectionName::class.java)
                ?: throw MissingCollectionNameException(aggregateClass)
        return database.getCollection(collectionNameAnnotation.value)
    }

    private fun <ID : AbstractIdentity, T : Aggregate<ID>> serialize(
        aggregate: T,
        serializer: KSerializer<T>,
    ): Document {
        val jsonStr = json.encodeToString(serializer, aggregate)
        return Document.parse(jsonStr)
    }

    private fun <T> deserialize(
        document: Document,
        serializer: KSerializer<T>,
    ): T {
        val jsonStr = document.toJson()
        return json.decodeFromString(serializer, jsonStr)
    }

    private fun <ID : AbstractIdentity> resolveDocumentId(id: ID): Document = Document("_id", id.toString())
}
