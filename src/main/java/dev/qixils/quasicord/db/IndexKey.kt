package dev.qixils.quasicord.db

enum class IndexKeyOrder{ ASCENDING, DESCENDING }

annotation class IndexKey(val value: String, val order: IndexKeyOrder = IndexKeyOrder.ASCENDING)
