package dev.qixils.quasicord.extensions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@OptIn(ExperimentalCoroutinesApi::class)
private class DistinctFlow<T>(private val upstream: Flow<T>) : AbstractFlow<T>() {
	override suspend fun collectSafely(collector: FlowCollector<T>) {
		val emitted = mutableSetOf<T>()
		return upstream.collect {
			if (!emitted.add(it)) return@collect
			collector.emit(it)
		}
	}
}

fun <T> Flow<T>.distinct(): Flow<T> = DistinctFlow(this)
