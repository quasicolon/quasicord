package dev.qixils.quasicord.db

// TODO: unique

@Repeatable
annotation class Index(vararg val value: IndexKey)
