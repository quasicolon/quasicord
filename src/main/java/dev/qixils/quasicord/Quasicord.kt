/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord

import dev.qixils.quasicord.commands.GuildConfigCommand
import dev.qixils.quasicord.commands.UserConfigCommand
import dev.qixils.quasicord.db.DatabaseManager
import dev.qixils.quasicord.events.EventDispatcher
import dev.qixils.quasicord.locale.LocaleProvider
import dev.qixils.quasicord.locale.TranslationProvider
import dev.qixils.quasicord.registry.core.RegistryRegistry
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.api.utils.messages.MessageRequest
import org.jetbrains.annotations.Contract
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.security.auth.login.LoginException
import kotlin.Throws

/**
 * Managing class for a [Discord](https://discord.com/) bot which utilizes a
 * [MongoDB](https://mongodb.com/) [database][.getDatabaseManager].
 *
 *
 * See [Builder] for instructions on how to create a new instance.
 */
open class Quasicord(
    /**
     * Gets the bot's namespace which is used for fetching translation strings.
     *
     * @return bot's namespace
     */
    val namespace: String, defaultLocale: Locale, configRoot: Path, activity: Activity?, eventHandler: Any?
) {
    /**
     * Returns the [JDA] API for interacting with Discord.
     *
     * @return the JDA API
     */
    val jda: JDA

    /**
     * Returns the root [QuasicordConfig] representing the options set in `config.yml`.
     *
     * @return root configuration node
     */
    val config: QuasicordConfig

    /**
     * Returns the [Logger] for this bot.
     *
     * @return bot's logger
     */
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Returns the [DatabaseManager] which facilitates communication with the MongoDB database owned by this bot.
     *
     * @return database manager
     */
    val databaseManager: DatabaseManager

    /**
     * Gets the [registry of registries][RegistryRegistry].
     *
     * @return root registry
     */
    val rootRegistry: RegistryRegistry

    /**
     * Returns the [EventDispatcher] which is used to dispatch events to listeners.
     *
     * @return event dispatcher
     */
	@JvmField
	val eventDispatcher: EventDispatcher = EventDispatcher()
    protected val tempListenerExecutor: TemporaryListenerExecutor = TemporaryListenerExecutor()
    protected val ownerId: Long
    protected val botId: Long

    /**
     * Returns the [TranslationProvider] which is used to obtain translated strings.
     *
     * @return locale manager
     */
    val translationProvider: TranslationProvider

    /**
     * Returns the [LocaleProvider] which is used to obtain an object's locale.
     *
     * @return locale provider
     */
	@JvmField
	val localeProvider: LocaleProvider

    /**
     * Gets the command manager.
     *
     * @return command manager
     */
    val commandManager: CommandManager

    /**
     *
     * @param namespace
     * @param defaultLocale The default locale
     * @param configRoot
     * @param activity
     * @param eventHandler
     * @throws LoginException
     * @throws InterruptedException
     * @throws IOException
     */
    init {
        // misc initialization

        // register default event handler
        if (eventHandler != null) eventDispatcher.registerListeners(eventHandler)

        // register translation providers
        translationProvider = TranslationProvider(namespace, defaultLocale)
        TranslationProvider.registerInstance(translationProvider)
        TranslationProvider.registerInstance(TranslationProvider(Key.LIBRARY_NAMESPACE, Locale.ENGLISH))

        // load configuration
        val loader = YamlConfigurationLoader.builder()
            .path(configRoot.resolve("config.yml")) // TODO: default config options
            .build()
        val rootConfigNode = loader.load()
        config = rootConfigNode.get(QuasicordConfig::class.java) ?: throw IllegalStateException("config.yml is missing or invalid")

        // load database and locale provider
        databaseManager = DatabaseManager(namespace, config.environment)
        localeProvider = LocaleProvider(defaultLocale, databaseManager)
        LocaleProvider.instance = localeProvider

        // initialize JDA and relevant data
        jda = initJDA(activity) // should be executed last-ish
        botId = jda.selfUser.idLong
        ownerId = jda.retrieveApplicationInfo().complete().owner.idLong

        // late initialize (depends on JDA)
        rootRegistry = RegistryRegistry(this)
        this.commandManager = CommandManager(this)
        jda.addEventListener(commandManager)
        registerCommands()
        commandManager.upsertCommands(jda)
    }

    protected open fun initJDA(activity: Activity?): JDA {
        val builder = JDABuilder.createDefault(config.token)
            .disableIntents(
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.GUILD_MESSAGE_TYPING,  // GatewayIntent.GUILD_INTEGRATIONS, // unused, apparently
                GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.GUILD_VOICE_STATES
            )
            .enableIntents(GatewayIntent.GUILD_MEMBERS) // TODO:
            //	1. register cogs before initJDA() is called
            //  2. implement getRequiredIntents() in Cog
            //  3. use that result here to compute minimum required intents
            //  4. late-loaded cogs, if any/ever, can decline when their required intents weren't met at startup
            //  5. except wait, cogs need jda to be constructed (and probably should), that's a problem
            .enableIntents(GatewayIntent.GUILD_MESSAGES)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setEventManager(AnnotatedEventManager())
            .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE)
        if (activity != null) builder.setActivity(activity)
        MessageRequest.setDefaultMentions(emptySet())
        val jda = builder.build()
        jda.setRequiredScopes("applications.commands")
        jda.addEventListener(tempListenerExecutor)
        jda.addEventListener(object : Any() {
            @SubscribeEvent
            fun on(event: Event?) {
                eventDispatcher.dispatch(event!!)
            }
        })
        try {
            jda.awaitReady()
        } catch (ignored: InterruptedException) {
        }
        return jda
    }

    /**
     * Registers a temporary listener.
     *
     * @param listener temporary listener to register
     */
    // we don't expose the raw executor in a getter because objects could abuse the #onEvent method
    fun register(listener: TemporaryListener<*>) {
        tempListenerExecutor.register(Objects.requireNonNull(listener, "listener cannot be null"))
    }

    /**
     * Registers commands just before the initial upsert.
     */
    protected fun registerCommands() {
        commandManager.discoverCommands(UserConfigCommand(this))
        commandManager.discoverCommands(GuildConfigCommand(this))
    }

    /**
     * Shuts down the bot as soon as pending tasks have finished execution.
     */
    fun shutdown() {
        jda.shutdown()
    }

    /**
     * Shuts down the bot immediately.
     */
    fun shutdownNow() {
        jda.shutdownNow()
    }

    // boilerplate

    val environment: Environment
        /**
         * Returns the [Environment] that the bot is currently running in.
         *
         * @return execution environment
         */
        get() = config.environment

    /**
     * A builder for [Quasicord] instances.
     */
    class Builder
    /**
     * Creates a new builder.
     */
    {
        protected var namespace: String? = null
        protected var locale: Locale = Locale.ENGLISH
        protected var configRoot: Path = Paths.get(".").toAbsolutePath()
        protected var activity: Activity? = null
        protected var eventHandler: Any? = null

        /**
         * Sets the namespace used for fetching translation strings.
         *
         * @param namespace your software's namespace
         * @return this builder
         */
        @Contract("_ -> this")
        fun namespace(namespace: String): Builder {
            this.namespace = namespace
            return this
        }

        /**
         * Sets the default locale for the bot.
         *
         * @param locale the default locale
         * @return this builder
         */
        @Contract("_ -> this")
        fun defaultLocale(locale: Locale): Builder {
            this.locale = locale
            return this
        }

        /**
         * Sets the root directory for the configuration files.
         *
         * @param configRoot the root directory
         * @return this builder
         */
        @Contract("_ -> this")
        fun configRoot(configRoot: Path): Builder {
            this.configRoot = configRoot
            return this
        }

        /**
         * Sets the activity to be used for the bot.
         *
         * @param activity the activity
         * @return this builder
         */
        @Contract("_ -> this")
        fun activity(activity: Activity?): Builder {
            this.activity = activity
            return this
        }

        /**
         * Sets the default event handler to be used for the bot.
         * Other event handlers can be added later.
         *
         * @param eventHandler the event handler
         * @return this builder
         */
        @Contract("_ -> this")
        fun eventHandler(eventHandler: Any?): Builder {
            this.eventHandler = eventHandler
            return this
        }

        /**
         * Builds a new [Quasicord] instance.
         *
         * @return the new instance
         * @throws IllegalStateException if the namespace is not set
         * @throws LoginException        if the JDA login fails
         * @throws InterruptedException  if the JDA login is interrupted
         * @throws ConfigurateException  if the configuration fails
         */
        @Throws(
            IllegalStateException::class,
            LoginException::class,
            InterruptedException::class,
            IOException::class
        )
        fun build(): Quasicord {
            checkNotNull(namespace) { "namespace must be set" }
            return Quasicord(namespace!!, locale, configRoot, activity, eventHandler)
        }
    }
}
