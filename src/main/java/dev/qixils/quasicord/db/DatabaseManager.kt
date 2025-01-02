/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.db

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import dev.qixils.quasicord.Environment
import dev.qixils.quasicord.db.codecs.ZoneIdCodec
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.conversions.Bson
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.Closeable
import java.util.*

/**
 * Manages the [Bot][Quasicord]'s database.
 */
class DatabaseManager(dbPrefix: String, dbSuffix: String) : Closeable {
    private val pojoRegistry: CodecRegistry = CodecRegistries.fromRegistries(
		CodecRegistries.fromCodecs(ZoneIdCodec),
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )
    private val mongoClientSettings: MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString("mongodb://localhost:27017"))
        .codecRegistry(pojoRegistry)
        .build()
    private val mongoClient: MongoClient = MongoClients.create(mongoClientSettings)
    protected val database: MongoDatabase

    constructor(dbPrefix: String, environment: Environment) : this(
        dbPrefix,
        environment.name.lowercase(),
    )

    init {
        Objects.requireNonNull<String>(dbPrefix, "dbPrefix cannot be null")
        Objects.requireNonNull<String>(dbSuffix, "dbSuffix cannot be null")
        this.database = mongoClient.getDatabase("$dbPrefix-$dbSuffix")
    }

    // collection
    fun <T> collection(tClass: Class<T>): MongoCollection<T> {
        return collection(Companion.collectionNameOf(tClass), tClass)
    }

    fun <T> collection(collectionName: String, tClass: Class<T>): MongoCollection<T> {
        return database.getCollection(collectionName, tClass)
    }

    // getAll
    fun <T> getAll(tClass: Class<T>): Flux<T> {
        return Flux.from(collection(tClass).find())
    }

    // getAllBy
    fun <T> getAllBy(filters: Bson, tClass: Class<T>): Flux<T> {
        return Flux.from(collection(tClass).find(filters))
    }

    fun <T> getAllByEquals(keyValueMap: Map<String, *>, tClass: Class<T>): Flux<T> {
        var filter = Filters.empty()
        for (entry in keyValueMap.entries) filter = Filters.and(filter, Filters.eq(entry.key, entry.value))
        return getAllBy(filter, tClass)
    }

    // getById
    fun <T> getById(id: Any?, tClass: Class<T>): Mono<T> {
        return Mono.from(collection(tClass).find(Filters.eq("_id", id)))
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
