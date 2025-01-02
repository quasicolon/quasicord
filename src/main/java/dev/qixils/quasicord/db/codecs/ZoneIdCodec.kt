package dev.qixils.quasicord.db.codecs

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import java.time.ZoneId

object ZoneIdCodec : Codec<ZoneId> {

	override fun getEncoderClass(): Class<ZoneId> {
		return ZoneId::class.java
	}

	override fun encode(
		writer: BsonWriter,
		value: ZoneId?,
		encoderContext: EncoderContext,
	) {
		if (value == null) return
		writer.writeString(value.id)
	}

	override fun decode(
		reader: BsonReader,
		decoderContext: DecoderContext,
	): ZoneId? {
		return reader.readString()?.let { ZoneId.of(it) }
	}
}
