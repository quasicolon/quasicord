package dev.qixils.quasicord.db

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.singleOrNull
import org.bson.conversions.Bson
import java.time.Duration
import java.time.Instant

private data class CachedQuery<T : Any>(val flow: FindFlow<T>, val cachedAt: Instant = Instant.now())

class CachedQueries<T : Any>(private val collection: MongoCollection<T>, private val duration: Duration = Duration.ofSeconds(60)) {
	private val cache = mutableMapOf<Bson, CachedQuery<T>>()

	fun getAllBy(filters: Bson): FindFlow<T> {
		val query = cache[filters]
		if (query != null) {
			if (query.cachedAt.plus(duration).isBefore(Instant.now())) cache.remove(filters) // TODO: need to have an alt way to clear cache so it doesn't balloon
			else return query.flow
		}

		val result = collection.find(filters)
		cache[filters] = CachedQuery(result)
		return result
	}

	fun getAll(): FindFlow<T> {
		return getAllBy(Filters.empty())
	}

	fun getAllByEquals(keyValueMap: Map<String, *>): FindFlow<T> {
		return getAllBy(Filters.and(keyValueMap.entries.map { Filters.eq(it.key, it.value) }))
	}

	suspend fun getById(field: String, id: Any?): T? {
		return getAllBy(Filters.eq(field, id)).singleOrNull()
	}

	suspend fun getById(id: Any?): T? {
		return getById("_id", id)
	}
}
