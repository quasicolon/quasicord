package dev.qixils.quasicord.db.codecs

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import java.util.*

object LocaleCodec : Codec<Locale> {

	override fun getEncoderClass(): Class<Locale> {
		return Locale::class.java
	}

	override fun encode(
		writer: BsonWriter,
		value: Locale?,
		encoderContext: EncoderContext,
	) {
		if (value == null) return
		writer.writeString(value.toLanguageTag())
	}

	override fun decode(
		reader: BsonReader,
		decoderContext: DecoderContext,
	): Locale? {
		return reader.readString()?.let { Locale.forLanguageTag(it) }
	}
}
