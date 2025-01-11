package org.huho.libs.aggregate.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
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

    @BeforeEach
    fun setUp() =
        runBlocking {
            mongoClient = MongoClient.create(getConnectionString())

            val dbName = "test-db-${UUID.randomUUID().toString().replace("-", "")}"
            database = mongoClient.getDatabase(dbName)

            repository =
                MongoAggregateRepository(
                    MongoApplicationDatabase(database),
                    GlobalJson.json,
                    collectionNameResolver,
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

object GlobalJson {
    val json =
        Json {
            serializersModule =
                SerializersModule {
                    polymorphic(Generic::class) {
                        subclass(GenericA::class)
                        subclass(GenericB::class)
                    }
                }
        }
}

@CollectionName("test")
@Serializable
class TestAggregate constructor(
    @SerialName("_id")
    private val id: TestId,
) : Aggregate<TestId>() {
    private var note: String? = null

    private var generic: Generic? = null

    constructor(testId: TestId, note: String?) : this(testId) {
        this.note = note
    }

    fun changeGeneric(newValue: Generic) {
        generic = newValue
    }

    fun changeNote(newNote: String) {
        this.note = newNote
    }

    fun getNote() = note

    override fun getId(): TestId = id
}

@Serializable
@Polymorphic
sealed interface Generic

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
