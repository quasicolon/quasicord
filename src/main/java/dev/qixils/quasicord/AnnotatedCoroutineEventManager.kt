package dev.qixils.quasicord

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.CoroutineEventManager
import dev.minn.jda.ktx.events.getDefaultScope
import kotlinx.coroutines.CoroutineScope
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.time.Duration

class AnnotatedCoroutineEventManager(
	scope: CoroutineScope = getDefaultScope(),
	/** Timeout [Duration] each event listener is allowed to run. Set to [Duration.INFINITE] for no timeout. Default: [Duration.INFINITE] */
	timeout: Duration = Duration.INFINITE,
) : CoroutineEventManager(scope, timeout) {
	override fun register(listener: Any) {
		when (listener) {
			is EventListener, is CoroutineEventListener -> listeners.add(listener)
			else -> listeners.addAll(discover(listener))
		}
	}

	fun discover(listener: Any): List<CoroutineEventListener> = listener::class
		.declaredFunctions
		.filter {
			it.hasAnnotation<SubscribeEvent>()
				&& it.parameters.size == 1
				&& GenericEvent::class.java.isAssignableFrom(it.parameters[0].javaClass)
		}
		.map {
			val inputClass = it.parameters[0].javaClass
			CoroutineEventListener { event ->
				if (inputClass.isAssignableFrom(event.javaClass))
					it.callSuspend(event)
			}
		}
}
