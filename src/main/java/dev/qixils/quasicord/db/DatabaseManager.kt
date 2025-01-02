/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.db

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dev.qixils.quasicord.Environment
import dev.qixils.quasicord.db.codecs.LocaleCodec
import dev.qixils.quasicord.db.codecs.ZoneIdCodec
import kotlinx.coroutines.flow.singleOrNull
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.conversions.Bson
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

	constructor(dbPrefix: String, environment: Environment) : this(
        dbPrefix,
        environment.name.lowercase(),
    )

	// collection
    fun <T : Any> collection(tClass: Class<T>): MongoCollection<T> {
        return collection(Companion.collectionNameOf(tClass), tClass)
    }

	inline fun <reified T : Any> collection(): MongoCollection<T> {
		return collection(T::class.java)
	}

    fun <T : Any> collection(collectionName: String, tClass: Class<T>): MongoCollection<T> {
        return database.getCollection(collectionName, tClass)
    }

	inline fun <reified T : Any> collection(collectionName: String): MongoCollection<T> {
		return collection(collectionName, T::class.java)
	}

    // getAll
    fun <T : Any> getAll(tClass: Class<T>): FindFlow<T> {
        return collection(tClass).find()
    }

	inline fun <reified T : Any> getAll(): FindFlow<T> {
		return getAll(T::class.java)
	}

    // getAllBy
    fun <T : Any> getAllBy(filters: Bson, tClass: Class<T>): FindFlow<T> {
        return collection(tClass).find(filters)
    }

	inline fun <reified T : Any> getAllBy(filters: Bson): FindFlow<T> {
		return getAllBy(filters, T::class.java)
	}

    fun <T : Any> getAllByEquals(keyValueMap: Map<String, *>, tClass: Class<T>): FindFlow<T> {
		return getAllBy(Filters.and(keyValueMap.entries.map { Filters.eq(it.key, it.value) }), tClass)
    }

	inline fun <reified T : Any> getAllByEquals(keyValueMap: Map<String, *>): FindFlow<T> {
		return getAllByEquals(keyValueMap, T::class.java)
	}

    // getById
	suspend fun <T : Any> getById(field: String, id: Any?, tClass: Class<T>): T? {
		return collection(tClass).find(Filters.eq(field, id)).singleOrNull()
	}

	suspend inline fun <reified T : Any> getById(field: String, id: Any?): T? {
		return getById(field, id, T::class.java)
	}

    suspend fun <T : Any> getById(id: Any?, tClass: Class<T>): T? {
        return getById("_id", id, tClass)
    }

	suspend inline fun <reified T : Any> getById(id: Any?): T? {
		return getById(id, T::class.java)
	}

    // misc
    override fun close() {
        mongoClient.close()
    }

    companion object {
        // collectionNameOf
        private fun collectionNameOf(obj: Any): String {
            return Companion.collectionNameOf(obj.javaClass)
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
