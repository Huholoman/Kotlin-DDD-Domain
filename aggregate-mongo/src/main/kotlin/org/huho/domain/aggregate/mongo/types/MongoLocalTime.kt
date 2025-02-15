package org.huho.domain.aggregate.mongo.types

import kotlinx.datetime.LocalTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.BsonInt64
import org.bson.codecs.kotlinx.BsonDecoder
import org.bson.codecs.kotlinx.BsonEncoder

@Serializable(with = MongoTimeSerializer::class)
data class MongoLocalTime(
    val value: LocalTime,
)

@OptIn(ExperimentalSerializationApi::class)
object MongoTimeSerializer : KSerializer<MongoLocalTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.LONG)

    override fun serialize(
        encoder: Encoder,
        value: MongoLocalTime,
    ) {
        if (encoder is BsonEncoder) {
            val millis = value.value.toNanosecondOfDay() / 1_000_000
            encoder.encodeBsonValue(BsonInt64(millis))
        } else {
            encoder.encodeString(value.toString())
        }
    }

    override fun deserialize(decoder: Decoder): MongoLocalTime =
        MongoLocalTime(
            when (decoder) {
                is BsonDecoder -> {
                    val millis = (decoder.decodeBsonValue() as BsonInt64).value
                    LocalTime.fromNanosecondOfDay(millis * 1_000_000)
                }
                else -> LocalTime.parse(decoder.decodeString())
            },
        )
}
