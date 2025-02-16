package org.huho.domain.aggregate.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.KSerializer
import org.bson.Document
import org.huho.domain.aggregate.Aggregate
import org.huho.domain.aggregate.AggregateEventProcessor
import org.huho.domain.aggregate.AggregateRepository
import org.huho.domain.aggregate.mongo.exceptions.AggregateNotFoundException
import org.huho.domain.identity.AbstractIdentity

open class MongoAggregateRepository<Event : Any>(
    private val database: MongoApplicationDatabase,
    private val collectionNameResolver: CollectionNameResolver,
    private val eventProcessor: AggregateEventProcessor<Event>,
) : AggregateRepository<Event> {
    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> insert(
        aggregate: T,
        serializer: KSerializer<T>,
    ) {
        aggregate.pullEvents().forEach { eventProcessor.process(it) }

        val collection = resolveCollectionFromClass(aggregate::class.java) as MongoCollection<T>
        collection.insertOne(aggregate)
    }

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> save(
        aggregate: T,
        serializer: KSerializer<T>,
    ) {
        aggregate.pullEvents().forEach { eventProcessor.process(it) }

        val collection = resolveCollectionFromClass(aggregate::class.java) as MongoCollection<T>
//        val document = serialize(aggregate, serializer)
        collection.replaceOne(resolveDocumentId(aggregate.id), aggregate)
    }

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> find(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): T? {
        val collection: MongoCollection<T> = resolveCollectionFromClass(aggregateClass)
        return collection.find(Document("_id", id.toString())).firstOrNull()
//        val document = collection.find(Document("_id", id.toString())).firstOrNull()
//        return document?.let { deserialize(it, serializer) }
    }

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> get(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): T =
        find(id, aggregateClass, serializer)
            ?: throw AggregateNotFoundException(aggregateClass.name, id)

    override suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> exists(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): Boolean = find(id, aggregateClass, serializer) != null

    private fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> resolveCollectionFromClass(
        aggregateClass: Class<T>,
    ): MongoCollection<T> =
        database.database.getCollection(
            collectionNameResolver.resolve(aggregateClass),
            aggregateClass,
        )

//    private fun <ID : AbstractIdentity, T : Aggregate<ID>> serialize(
//        aggregate: T,
//        serializer: KSerializer<T>,
//    ): Document {
//        val jsonStr = json.encodeToString(serializer, aggregate)
//        return Document.parse(jsonStr)
//    }

//    private fun <T> deserialize(
//        document: Document,
//        serializer: KSerializer<T>,
//    ): T {
//        val jsonStr = document.toJson()
//        return json.decodeFromString(serializer, jsonStr)
//    }

    private fun <ID : AbstractIdentity> resolveDocumentId(id: ID): Document = Document("_id", id.toString())
}
