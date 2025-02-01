package org.huho.libs.aggregate.mongo

import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.kotlinx.KotlinSerializerCodecProvider
import org.huho.libs.domain.aggregate.Aggregate
import org.huho.libs.domain.aggregate.mongo.CollectionName
import org.huho.libs.domain.aggregate.mongo.CollectionNameResolver
import org.huho.libs.domain.aggregate.mongo.MongoAggregateRepository
import org.huho.libs.domain.aggregate.mongo.MongoApplicationDatabase
import org.huho.libs.domain.identity.AbstractIdentity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.UUID

abstract class AbstractMongoIntegrationTest {
    protected lateinit var mongoClient: MongoClient
    protected lateinit var database: MongoDatabase
    protected lateinit var repository: MongoAggregateRepository
    protected val collectionNameResolver = CollectionNameResolver()
    protected val eventProcessor = SpyAggregateEventProcessor()

    /**
     * This is probably overkill.
     * For regular tests will be fine to set one repository for all tests.
     */
    @BeforeEach
    fun setUp() =
        runBlocking {
            eventProcessor.pullEvents()

            // Use GlobalJson.serializersModule for MongoDB serialization
            val codecRegistry: CodecRegistry =
                CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(KotlinSerializerCodecProvider()),
                )

            mongoClient = MongoClient.create(getConnectionString())

            val dbName = "test-db-${UUID.randomUUID().toString().replace("-", "")}"
            database = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry)

            repository =
                MongoAggregateRepository(
                    MongoApplicationDatabase(database),
                    collectionNameResolver,
                    eventProcessor,
                )
        }

    @AfterEach
    fun tearDown() =
        runBlocking {
            database.drop()
            mongoClient.close()
        }

    protected fun getConnectionString(): String =
        System.getenv("MONGO_CONNECTION_STRING")
            ?: "mongodb://localhost:27017"
}

@CollectionName("test")
@Serializable
class TestAggregate : Aggregate<TestId>() {
    @SerialName("_id")
    override lateinit var id: TestId
    private var note: String? = null

    private var generic: Generic? = null

    fun create(
        testId: TestId,
        note: String? = null,
    ) {
        record(TestCreated(testId))
        if (note != null) {
            record(TestNoteChanged(testId, note))
        }
    }

    fun changeGeneric(newValue: Generic) {
        record(TestGenericChanged(id, newValue))
    }

    fun changeNote(newNote: String) {
        record(TestNoteChanged(id, newNote))
    }

    fun getNote() = note

    override fun apply(event: Any) {
        when (event) {
            is TestCreated -> id = event.id
            is TestNoteChanged -> note = event.note
            is TestGenericChanged -> generic = event.generic
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    @Serializable
    data class TestCreated(
        val id: TestId,
    )

    @Serializable
    data class TestNoteChanged(
        val id: TestId,
        val note: String?,
    )

    @Serializable
    data class TestGenericChanged(
        val id: TestId,
        val generic: Generic,
    )

    @Serializable
    data class UnknownTestEvent(
        val id: TestId,
    )
}

@Serializable
sealed interface Generic {
    @Serializable
    @SerialName("generic_a")
    data class GenericA(
        val a: Int,
    ) : Generic

    @Serializable
    @SerialName("generic_b")
    data class GenericB(
        val b: String,
    ) : Generic
}

@Serializable(with = TestIdSerializer::class)
class TestId : AbstractIdentity {
    constructor() : super()
    constructor(uuid: UUID) : super(uuid)
    constructor(entityId: AbstractIdentity) : super(entityId)
    constructor(value: String) : super(value)
}

class TestIdSerializer : KSerializer<TestId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TestId", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TestId = TestId(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: TestId,
    ) {
        encoder.encodeString(value.toString())
    }
}
