package org.huho.domain.aggregate.mongo.types

import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.BsonDateTime
import org.bson.codecs.kotlinx.BsonDecoder
import org.bson.codecs.kotlinx.BsonEncoder

@Serializable(with = MongoInstantSerializer::class)
data class MongoInstant(
    val value: Instant,
)

@OptIn(ExperimentalSerializationApi::class)
object MongoInstantSerializer : KSerializer<MongoInstant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MongoInstant,
    ) {
        if (encoder is BsonEncoder) {
            val millis = value.value.toEpochMilliseconds()
            encoder.encodeBsonValue(BsonDateTime(millis))
        } else {
            encoder.encodeString(value.toString())
        }
    }

    override fun deserialize(decoder: Decoder): MongoInstant =
        MongoInstant(
            when (decoder) {
                is BsonDecoder -> {
                    val millis = (decoder.decodeBsonValue() as BsonDateTime).value
                    Instant.fromEpochMilliseconds(millis)
                }
                else -> Instant.parse(decoder.decodeString())
            },
        )
}
