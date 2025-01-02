/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.converter

import dev.qixils.quasicord.Quasicord
import dev.qixils.quasicord.converter.ConverterImpl.Companion.identity
import dev.qixils.quasicord.converter.ConverterImpl.ConverterImplStep
import dev.qixils.quasicord.converter.impl.DurationConverter
import dev.qixils.quasicord.converter.impl.LocaleConverter
import dev.qixils.quasicord.converter.impl.ZoneIdConverter
import dev.qixils.quasicord.converter.impl.ZonedDateTimeConverter
import dev.qixils.quasicord.locale.Context
import dev.qixils.quasicord.registry.impl.RegistryImpl
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.*
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.Interaction
import java.time.*
import java.util.*
import java.util.function.Supplier

class ConverterRegistry(library: Quasicord) : RegistryImpl<Converter<*, *>>("converters") {
    val ZONED_DATE_TIME: Converter<String, ZonedDateTime>

    init {
        // contextual converters
        register(VoidConverterImpl(Member::class.java) { it.member!! })
        register(VoidConverterImpl(User::class.java) { it.user })
        register(VoidConverterImpl(Guild::class.java) { it.guild!! })
        register(VoidConverterImpl(Channel::class.java) { it.getChannel()!! })
        register(VoidConverterImpl(ChannelType::class.java) { it.getChannelType() })
        register(VoidConverterImpl(Context::class.java) { Context.fromInteraction(it) })
        register(VoidConverterImpl(DiscordLocale::class.java) { obj: Interaction? -> obj!!.getUserLocale() })
        register(
            ConverterImpl(
                Context::class.java,
                Locale::class.java
            ) { it: Interaction?, ctx: Context? -> ctx!!.locale(library.localeProvider).block() }) // TODO: async
        register(LocaleConverter(library))
        register(
            ConverterImpl(
                Locale::class.java,
                DiscordLocale::class.java
            ) { it: Interaction?, locale: Locale? ->
                DiscordLocale.from(
                    locale!!
                )
            })
        register(
            ConverterImpl(
                DiscordLocale::class.java,
                Locale::class.java
            ) { it: Interaction?, locale: DiscordLocale? -> locale!!.toLocale() })
        register(ZoneIdConverter())
        // channels
        register(ConverterImpl.Companion.channel(TextChannel::class.java))
        register(ConverterImpl.Companion.channel(PrivateChannel::class.java))
        register(ConverterImpl.Companion.channel(VoiceChannel::class.java))
        register(ConverterImpl.Companion.channel(Category::class.java))
        register(ConverterImpl.Companion.channel(NewsChannel::class.java))
        register(ConverterImpl.Companion.channel(StageChannel::class.java))
        register(ConverterImpl.Companion.channel(ThreadChannel::class.java))
        register(ConverterImpl.Companion.channel(ForumChannel::class.java))
        // zoned date time and misc conversions to other java time types
        register(DurationConverter(library))
        ZONED_DATE_TIME = typedRegister(ZonedDateTimeConverter(library))
        register(
            ConverterImpl(
                ZonedDateTime::class.java,
                Instant::class.java
            ) { it: Interaction?, zdt: ZonedDateTime? -> zdt!!.toInstant() })
        register(
            ConverterImpl(
                ZonedDateTime::class.java,
                LocalDateTime::class.java
            ) { it: Interaction?, zdt: ZonedDateTime? -> zdt!!.toLocalDateTime() })
        register(
            ConverterImpl(
                LocalDateTime::class.java,
                LocalDate::class.java
            ) { it: Interaction?, ldt: LocalDateTime? -> ldt!!.toLocalDate() })
        register(
            ConverterImpl(
                LocalDateTime::class.java,
                LocalTime::class.java
            ) { it: Interaction?, ldt: LocalDateTime? -> ldt!!.toLocalTime() })
        // numbers
        register(
            ConverterImpl(
                Number::class.java,
                Int::class.java
            ) { it: Interaction?, l: Number? -> l!!.toInt() })
        register(
            ConverterImpl(
                Number::class.java,
                Long::class.java
            ) { it: Interaction?, d: Number? -> d!!.toLong() })
        register(
            ConverterImpl(
                Number::class.java,
                Float::class.java
            ) { it: Interaction?, f: Number? -> f!!.toFloat() })
        register(
            ConverterImpl(
                Number::class.java,
                Double::class.java
            ) { it: Interaction?, d: Number? -> d!!.toDouble() })
        register(
            ConverterImpl(
                Number::class.java,
                Short::class.java
            ) { it: Interaction?, s: Number? -> s!!.toShort() })
        register(
            ConverterImpl(
                Number::class.java,
                Byte::class.java
            ) { it: Interaction?, b: Number? -> b!!.toByte() })
        // misc
        register(ConverterImpl(User::class.java, Member::class.java) { it: Interaction?, u: User? ->
            Objects.requireNonNull<Member?>(
                Objects.requireNonNull<Guild?>(it!!.guild).getMember(u!!)
            )
        })
        register(
            ConverterImpl(
                Int::class.java,
                Enum::class.java,
                ConverterImplStep { ctx: Interaction?, i: Int?, tc: Class<out Enum<*>?>? -> tc!!.getEnumConstants()[i!!] })
        )
        register(
            ConverterImpl(
                String::class.java,
                Enum::class.java,
                ConverterImplStep { ctx: Interaction?, i: String?, tc: Class<out Enum<*>?>? ->
                    Arrays.stream(
                        tc!!.getEnumConstants()
                    ).filter { e: Enum<*>? -> e!!.name == i }.findFirst().orElseThrow()
                })
        )
    }

    fun <C : Converter<*, *>?> typedRegister(converter: C): C {
        register(converter!!)
        return converter
    }

    fun <I, O> getConverter(inputClass: Class<I>, outputClass: Class<O>): Converter<I, O>? {
        if (inputClass == outputClass)
            return identity<O>(outputClass) as Converter<I, O>

        return stream().filter { converter: Converter<*, *>? -> converter!!.inputClass == inputClass && converter.outputClass == outputClass }
            .findAny().or(Supplier {
                stream().filter { converter: Converter<*, *>? ->
                    converter!!.inputClass.isAssignableFrom(inputClass) && outputClass.isAssignableFrom(
                        converter.outputClass
                    )
                }.findAny()
            }).orElse(null) as Converter<I, O>?
    }

    fun <I, O> findConverter(inputClass: Class<I>, outputClass: Class<O>): Converter<I, O>? {
        // the goal of this method is basically to find a series of converters that can be chained together
        //   to convert from inputClass to outputClass

        // first, we check if there's a direct converter

        val direct = getConverter<I, O>(inputClass, outputClass)
        if (direct != null) return direct

        // if there's no direct converter, we need to find a chain of converters
        val encounteredConverters: MutableList<Converter<*, *>?> = ArrayList<Converter<*, *>?>()
        var nodes: MutableList<FindNode> = stream()
            .filter { converter: Converter<*, *>? -> converter!!.inputClass.isAssignableFrom(inputClass) }
            .map<FindNode?> { converter: Converter<*, *>? -> FindNode(converter!!, null) }
            .toList()
        while (!nodes.isEmpty()) {
            val newNodes: MutableList<FindNode> = ArrayList<FindNode>()
            for (node in nodes) {
                for (converter in this) {
                    if (!converter.canConvertTo) continue
                    if (encounteredConverters.contains(converter)) continue
                    if (!node.converter.outputClass.isAssignableFrom(converter.inputClass)) continue
                    if (node.isDuplicate(converter.outputClass)) continue

                    val newNode = FindNode(converter, node)
                    if (outputClass.isAssignableFrom(converter.outputClass)) {
                        // we found a chain! | TODO: cache
                        return ChainConverter<I, O>(inputClass, outputClass, newNode)
                    }
                    if (converter.canConvertFrom) newNodes.add(newNode)
                    encounteredConverters.add(converter)
                }
            }
            nodes = newNodes
        }

        // we couldn't find a chain
        return null
    }

    @JvmRecord
    private data class FindNode(
        val converter: Converter<*, *>,
        val parent: FindNode?
    ) {
        /**
         * Determines if the provided type has already been converted to/from in this chain.
         *
         * @return true if the type is a duplicate
         */
        fun isDuplicate(type: Class<*>): Boolean {
            var node: FindNode? = this
            while (node != null) {
                if (node.converter.inputClass == type || node.converter.outputClass == type) return true
                node = node.parent
            }
            return false
        }
    }

    private class ChainConverter<I, O>(inputClass: Class<I>, outputClass: Class<O>, node: FindNode) :
        AbstractConverter<I, O>(inputClass, outputClass) {
        private val converters: MutableList<Converter<*, *>>

        init {
            // technically the input & output class can be found from the chain, but accepting them as args is a little easier
            //  and is better for type safety
            var node: FindNode? = node
            converters = mutableListOf<Converter<*, *>>()
            while (node != null) {
				converters.addFirst(node.converter)
                node = node.parent
            }
        }

        override fun convert(interaction: Interaction, input: I, targetClass: Class<out O?>): O {
            var result: Any? = input
            for (converter in converters) result = (converter as Converter<Any?, Any?>).convert(
                interaction,
                result,
                targetClass
            ) // TODO: does passing targetClass unconditionally make sense here?

            return result as O
        }
    }
}
