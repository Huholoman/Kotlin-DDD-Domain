package org.huho.domain.aggregate.mongo.types

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
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

@Serializable(with = MongoDateTimeSerializer::class)
data class MongoLocalDateTime(
    val value: LocalDateTime,
)

@OptIn(ExperimentalSerializationApi::class)
object MongoDateTimeSerializer : KSerializer<MongoLocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MongoLocalDateTime,
    ) {
        if (encoder is BsonEncoder) {
            val millis = value.value.toInstant(TimeZone.UTC).toEpochMilliseconds()
            encoder.encodeBsonValue(BsonDateTime(millis))
        } else {
            encoder.encodeString(value.toString())
        }
    }

    override fun deserialize(decoder: Decoder): MongoLocalDateTime =
        MongoLocalDateTime(
            when (decoder) {
                is BsonDecoder -> {
                    val millis = (decoder.decodeBsonValue() as BsonDateTime).value
                    Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                }
                else -> LocalDateTime.parse(decoder.decodeString())
            },
        )
}
