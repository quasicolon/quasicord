/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.db

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dev.qixils.quasicord.Environment
import dev.qixils.quasicord.db.codecs.LocaleCodec
import dev.qixils.quasicord.db.codecs.ZoneIdCodec
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import java.io.Closeable

/**
 * Manages the [Bot][Quasicord]'s database.
 */
class DatabaseManager(dbPrefix: String, dbSuffix: String) : Closeable {
    private val pojoRegistry: CodecRegistry = CodecRegistries.fromRegistries(
		CodecRegistries.fromCodecs(ZoneIdCodec, LocaleCodec),
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )
    private val mongoClientSettings: MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString("mongodb://localhost:27017")) // TODO: env
        .codecRegistry(pojoRegistry)
        .build()
    private val mongoClient: MongoClient = MongoClient.create(mongoClientSettings)
    protected val database: MongoDatabase = mongoClient.getDatabase("$dbPrefix-$dbSuffix")
	private val caches = mutableMapOf<Class<*>, CachedQueries<*>>()

	constructor(dbPrefix: String, environment: Environment) : this(
        dbPrefix,
        environment.name.lowercase(),
    )

	// collection
    fun <T : Any> collection(tClass: Class<T>): MongoCollection<T> {
        return collection(collectionNameOf(tClass), tClass)
    }

	inline fun <reified T : Any> collection(): MongoCollection<T> {
		return collection(T::class.java)
	}

    fun <T : Any> collection(collectionName: String, tClass: Class<T>): MongoCollection<T> {
		// TODO: indices
        return database.getCollection(collectionName, tClass)
    }

	inline fun <reified T : Any> collection(collectionName: String): MongoCollection<T> {
		return collection(collectionName, T::class.java)
	}

	fun <T : Any> cache(tClass: Class<T>): CachedQueries<T> {
		return caches.computeIfAbsent(tClass) { CachedQueries(collection(tClass)) } as CachedQueries<T>
    }

	inline fun <reified T : Any> cache(): CachedQueries<T> {
		return cache(T::class.java)
	}

    // misc
    override fun close() {
        mongoClient.close()
    }

    companion object {
        // collectionNameOf
        private fun collectionNameOf(obj: Any): String {
            return collectionNameOf(obj.javaClass)
        }

        private fun collectionNameOf(clazz: Class<*>): String {
			val collectionName =
				if (clazz.isAnnotationPresent(CollectionName::class.java))
					clazz.getAnnotation(CollectionName::class.java).value
				else
					clazz.getSimpleName()
            return collectionName.substring(0, collectionName.length.coerceAtMost(127))
        }
    }
}
