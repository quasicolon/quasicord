package dev.qixils.quasicord.db

/**
 * The order in which to sort the values of a key.
 */
enum class IndexKeyOrder {
	/**
	 * Sorts in ascending order.
	 */
	ASCENDING,
	/**
	 * Sorts in descending order.
	 */
	DESCENDING,
}

/**
 * A key by which to index.
 */
annotation class IndexKey(
	/**
	 * The field to index. For a parameter `snowflake`, this would be `snowflake`.
	 * For a parameter `id` within a parameter `where`, this would be `where.id`.
	 */
	val value: String,
	/**
	 * The order in which to sort the values.
	 */
	val order: IndexKeyOrder = IndexKeyOrder.ASCENDING,
)
