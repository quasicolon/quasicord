/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import dev.qixils.quasicolon.Environment;
import dev.qixils.quasicolon.Quasicord;
import net.dv8tion.jda.api.entities.ISnowflake;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Manages the {@link Quasicord Bot}'s database.
 */
public class DatabaseManager implements Closeable {
	private final @NotNull CodecRegistry pojoRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
			CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	private final @NotNull MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
			.applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
			.codecRegistry(pojoRegistry)
			.build();
	private final @NotNull MongoClient mongoClient = MongoClients.create(mongoClientSettings);
	protected final @NotNull MongoDatabase database;

	public DatabaseManager(String dbPrefix, Environment environment) {
		this(dbPrefix, Objects.requireNonNull(environment, "environment cannot be null").name().toLowerCase(Locale.ROOT));
	}

	public DatabaseManager(@NotNull String dbPrefix, @NotNull String dbSuffix) {
		Objects.requireNonNull(dbPrefix, "dbPrefix cannot be null");
		Objects.requireNonNull(dbSuffix, "dbSuffix cannot be null");
		this.database = mongoClient.getDatabase(dbPrefix + "-" + dbSuffix);
	}

	// collectionNameOf

	private static String collectionNameOf(Object object) {
		return collectionNameOf(object.getClass());
	}

	private static String collectionNameOf(Class<?> clazz) {
		String collectionName;
		if (clazz.isAnnotationPresent(CollectionName.class))
			collectionName = clazz.getAnnotation(CollectionName.class).name();
		else
			collectionName = clazz.getSimpleName();
		return collectionName.substring(0, 127);
	}

	// collection

	public <T> @NotNull MongoCollection<T> collection(Class<T> tClass) {
		return collection(collectionNameOf(tClass), tClass);
	}

	public <T> @NotNull MongoCollection<T> collection(String collectionName, Class<T> tClass) {
		return database.getCollection(collectionName, tClass);
	}

	// getAll

	public <T> @NotNull Flux<T> getAll(Class<T> tClass) {
		return Flux.from(collection(tClass).find());
	}

	// getAllBy

	public <T> @NotNull Flux<T> getAllBy(Bson filters, Class<T> tClass) {
		return Flux.from(collection(tClass).find(filters));
	}

	public <T> @NotNull Flux<T> getAllByEquals(Map<String, ?> keyValueMap, Class<T> tClass) {
		Bson filter = Filters.empty();
		for (Entry<String, ?> entry : keyValueMap.entrySet())
			filter = Filters.and(filter, Filters.eq(entry.getKey(), entry.getValue()));
		return getAllBy(filter, tClass);
	}

	// getBySnowflake

	public <T extends ISnowflake> @NotNull Mono<T> getBySnowflake(String snowflake, Class<T> tClass) {
		return Mono.from(collection(tClass).find(Filters.eq("snowflake", snowflake)).first());
	}

	public <T extends ISnowflake> @NotNull Mono<T> getBySnowflake(long snowflake, Class<T> tClass) {
		return getBySnowflake(String.valueOf(snowflake), tClass);
	}

	public <T extends ISnowflake> @NotNull Mono<T> getBySnowflake(ISnowflake snowflake, Class<T> tClass) {
		return getBySnowflake(snowflake.getId(), tClass);
	}

	// misc

	public void close() {
		mongoClient.close();
	}
}
