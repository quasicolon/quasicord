package dev.qixils.quasicord.db

/**
 * Defines an index for a collection.
 * You may define multiple indexes.
 */
@Repeatable
annotation class Index(
	/**
	 * Whether this index is unique, meaning no two objects can share the same key values.
	 */
	val unique: Boolean = false,
	/**
	 * The keys to index against.
	 */
	vararg val value: IndexKey,
)
