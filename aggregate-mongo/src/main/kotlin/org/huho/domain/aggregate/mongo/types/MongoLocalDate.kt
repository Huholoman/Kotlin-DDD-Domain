package org.huho.domain.aggregate.mongo.types

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
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

@Serializable(with = MongoDateSerializer::class)
data class MongoLocalDate(
    val value: LocalDate,
)

@OptIn(ExperimentalSerializationApi::class)
object MongoDateSerializer : KSerializer<MongoLocalDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MongoLocalDate,
    ) {
        if (encoder is BsonEncoder) {
            val millis = value.value.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
            encoder.encodeBsonValue(BsonDateTime(millis))
        } else {
            encoder.encodeString(value.toString())
        }
    }

    override fun deserialize(decoder: Decoder): MongoLocalDate =
        MongoLocalDate(
            when (decoder) {
                is BsonDecoder -> {
                    val millis = (decoder.decodeBsonValue() as BsonDateTime).value
                    Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                }
                else -> LocalDate.parse(decoder.decodeString())
            },
        )
}
