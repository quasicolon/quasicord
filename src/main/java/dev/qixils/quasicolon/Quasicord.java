/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon;

import dev.qixils.quasicolon.commands.UserConfigCommand;
import dev.qixils.quasicolon.db.DatabaseManager;
import dev.qixils.quasicolon.events.EventDispatcher;
import dev.qixils.quasicolon.locale.LocaleProvider;
import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.registry.core.RegistryRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.messages.MessageRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Managing class for a <a href="https://discord.com/">Discord</a> bot which utilizes a
 * <a href="https://mongodb.com/">MongoDB</a> {@link #getDatabaseManager() database}.
 * <p>
 * See {@link Builder} for instructions on how to create a new instance.
 */
public class Quasicord {
	protected final @NonNull JDA jda;
	protected final @NotNull QuasicordConfig config;
	protected final @NonNull Logger logger = LoggerFactory.getLogger(getClass());
	protected final @NonNull DatabaseManager database;
	protected final @NonNull RegistryRegistry rootRegistry;
	protected final @NonNull EventDispatcher eventDispatcher = new EventDispatcher();
	protected final @NonNull HashMap<Long, EventDispatcher> guildDispatchers = new HashMap<>();
	protected final @NonNull TemporaryListenerExecutor tempListenerExecutor = new TemporaryListenerExecutor();
	protected final @NonNull String namespace;
	protected final long ownerId;
	protected final long botId;
	protected final @NonNull TranslationProvider translationProvider;
	protected final @NonNull LocaleProvider localeProvider;
	protected final @NonNull CommandManager commandManager;

	/**
	 *
	 * @param namespace
	 * @param locales The list of supported locales, with the first locale being treated as the default.
	 * @param configRoot
	 * @param activity
	 * @param eventHandler
	 * @throws LoginException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected Quasicord(@NonNull String namespace, @NonNull List<Locale> locales, @NonNull Path configRoot, @Nullable Activity activity, @Nullable Object eventHandler) throws LoginException, InterruptedException, IOException {
		if (locales.isEmpty()) {
			throw new IllegalArgumentException("'locales' parameter must have at least one object");
		}

		// misc initialization
		this.namespace = namespace;

		// register default event handler
		if (eventHandler != null)
			eventDispatcher.registerListeners(eventHandler);

		// register translation providers
		translationProvider = new TranslationProvider(namespace, locales);
		TranslationProvider.registerInstance(translationProvider);
		TranslationProvider.registerInstance(new TranslationProvider(Key.LIBRARY_NAMESPACE, Locale.ENGLISH, locales));

		// load configuration
		YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
				.path(configRoot.resolve("config.yml"))
				// TODO: default config options
				.build();
		var rootConfigNode = loader.load();
		config = Objects.requireNonNull(rootConfigNode.get(QuasicordConfig.class), "config.yml is missing or invalid");

		// load database and locale provider
		database = new DatabaseManager(namespace, config.environment());
		localeProvider = new LocaleProvider(locales.get(0), database);

		// initialize JDA and relevant data
		jda = initJDA(activity); // should be executed last-ish
		botId = jda.getSelfUser().getIdLong();
		ownerId = jda.retrieveApplicationInfo().complete().getOwner().getIdLong();

		// late initialize (depends on JDA)
		rootRegistry = new RegistryRegistry(this);
		this.commandManager = new CommandManager(this);
		registerCommands();
		commandManager.upsertCommands(jda);
	}

	@NonNull
	protected JDA initJDA(@Nullable Activity activity) {
		JDABuilder builder = JDABuilder.createDefault(config.token())
				.disableIntents(GatewayIntent.DIRECT_MESSAGE_TYPING,
						GatewayIntent.GUILD_MESSAGE_TYPING,
						// GatewayIntent.GUILD_INTEGRATIONS, // unused, apparently
						GatewayIntent.GUILD_WEBHOOKS,
						GatewayIntent.GUILD_INVITES,
						GatewayIntent.GUILD_VOICE_STATES)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				// TODO:
				//	1. register cogs before initJDA() is called
				//  2. implement getRequiredIntents() in Cog
				//  3. use that result here to compute minimum required intents
				//  4. late-loaded cogs, if any/ever, can decline when their required intents weren't met at startup
				//  5. except wait, cogs need jda to be constructed (and probably should), that's a problem
				.enableIntents(GatewayIntent.GUILD_MESSAGES)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setEventManager(new AnnotatedEventManager())
				.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE);
		if (activity != null)
			builder.setActivity(activity);
		MessageRequest.setDefaultMentions(Collections.emptySet());
		JDA jda = builder.build();
		jda.setRequiredScopes("applications.commands");
		jda.addEventListener(tempListenerExecutor);
		jda.addEventListener(commandManager);
		jda.addEventListener(new Object() {
			@net.dv8tion.jda.api.hooks.SubscribeEvent
			public void on(net.dv8tion.jda.api.events.Event event) {
				eventDispatcher.dispatch(event);
			}
		});
		try {
			jda.awaitReady();
		} catch (InterruptedException ignored) {
		}
		return jda;
	}

	/**
	 * Registers a temporary listener.
	 *
	 * @param listener temporary listener to register
	 */
	// we don't expose the raw executor in a getter because objects could abuse the #onEvent method
	public void register(@NonNull TemporaryListener<?> listener) {
		tempListenerExecutor.register(Objects.requireNonNull(listener, "listener cannot be null"));
	}

	/**
	 * Registers commands just before the initial upsert.
	 */
	protected void registerCommands() {
		commandManager.discoverCommands(new UserConfigCommand(this));
	}

	/**
	 * Shuts down the bot as soon as pending tasks have finished execution.
	 */
	public void shutdown() {
		jda.shutdown();
	}

	/**
	 * Shuts down the bot immediately.
	 */
	public void shutdownNow() {
		jda.shutdownNow();
	}

	/**
	 * Returns the {@link JDA} API for interacting with Discord.
	 *
	 * @return the JDA API
	 */
	public @NonNull JDA getJDA() {
		return jda;
	}

	// boilerplate

	/**
	 * Returns the root {@link QuasicordConfig} representing the options set in {@code config.yml}.
	 *
	 * @return root configuration node
	 */
	public @NonNull QuasicordConfig getConfig() {
		return config;
	}

	/**
	 * Returns the {@link Logger} for this bot.
	 *
	 * @return bot's logger
	 */
	public @NonNull Logger getLogger() {
		return logger;
	}

	/**
	 * Returns the {@link Environment} that the bot is currently running in.
	 *
	 * @return execution environment
	 */
	public @NonNull Environment getEnvironment() {
		return config.environment();
	}

	/**
	 * Returns the {@link DatabaseManager} which facilitates communication with the MongoDB database owned by this bot.
	 *
	 * @return database manager
	 */
	public @NonNull DatabaseManager getDatabaseManager() {
		return database;
	}

	/**
	 * Returns the {@link TranslationProvider} which is used to obtain translated strings.
	 *
	 * @return locale manager
	 */
	public @NonNull TranslationProvider getTranslationProvider() {
		return translationProvider;
	}

	/**
	 * Returns the {@link LocaleProvider} which is used to obtain an object's locale.
	 *
	 * @return locale provider
	 */
	public @NonNull LocaleProvider getLocaleProvider() {
		return localeProvider;
	}

	/**
	 * Returns the {@link EventDispatcher} which is used to dispatch events to listeners.
	 *
	 * @return event dispatcher
	 */
	public @NonNull EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	/**
	 * Gets the command manager.
	 *
	 * @return command manager
	 */
	public @NonNull CommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * Gets the {@link RegistryRegistry registry of registries}.
	 *
	 * @return root registry
	 */
	public @NonNull RegistryRegistry getRootRegistry() {
		return rootRegistry;
	}

	/**
	 * Gets the bot's namespace which is used for fetching translation strings.
	 *
	 * @return bot's namespace
	 */
	public @NonNull String getNamespace() {
		return namespace;
	}

	/**
	 * A builder for {@link Quasicord} instances.
	 */
	public static class Builder {
		protected @Nullable String namespace;
		protected @NonNull List<Locale> locales = new ArrayList<>();
		protected @NonNull Path configRoot = Paths.get(".").toAbsolutePath();
		protected @Nullable Activity activity;
		protected @Nullable Object eventHandler;

		/**
		 * Creates a new builder.
		 */
		public Builder() {

		}

		/**
		 * Sets the namespace used for fetching translation strings.
		 *
		 * @param namespace your software's namespace
		 * @return this builder
		 */
		@Contract("_ -> this")
		public Builder namespace(@NonNull String namespace) {
			this.namespace = namespace;
			return this;
		}

		/**
		 * Sets the default locale for the bot.
		 *
		 * @param locale the default locale
		 * @return this builder
		 */
		@Contract("_ -> this")
		public Builder defaultLocale(@NonNull Locale locale) {
			locales.add(0, locale);
			return this;
		}

		/**
		 * Sets the locales for the bot. The first element is treated as the default.
		 *
		 * @param locales the locales to set
		 * @return this builder
		 */
		@Contract("_ -> this")
		public Builder setLocales(@NonNull Locale... locales) {
			this.locales.clear();
			return addLocales(locales);
		}

		/**
		 * Adds locales to the bot.
		 *
		 * @param locales the locales to add
		 */
		@Contract("_ -> this")
		public Builder addLocales(@NonNull Locale... locales) {
			this.locales.addAll(Arrays.asList(locales));
			return this;
		}

		/**
		 * Sets the root directory for the configuration files.
		 *
		 * @param configRoot the root directory
		 * @return this builder
		 */
		@Contract("_ -> this")
		public Builder configRoot(@NonNull Path configRoot) {
			this.configRoot = configRoot;
			return this;
		}

		/**
		 * Sets the activity to be used for the bot.
		 *
		 * @param activity the activity
		 * @return this builder
		 */
		@Contract("_ -> this")
		public Builder activity(@Nullable Activity activity) {
			this.activity = activity;
			return this;
		}

		/**
		 * Sets the default event handler to be used for the bot.
		 * Other event handlers can be added later.
		 *
		 * @param eventHandler the event handler
		 * @return this builder
		 */
		@Contract("_ -> this")
		public Builder eventHandler(@Nullable Object eventHandler) {
			this.eventHandler = eventHandler;
			return this;
		}

		/**
		 * Builds a new {@link Quasicord} instance.
		 *
		 * @return the new instance
		 * @throws IllegalStateException if the namespace is not set
		 * @throws LoginException        if the JDA login fails
		 * @throws InterruptedException  if the JDA login is interrupted
		 * @throws ConfigurateException  if the configuration fails
		 */
		public @NonNull Quasicord build() throws IllegalStateException, LoginException, InterruptedException, IOException {
			if (namespace == null)
				throw new IllegalStateException("namespace must be set");
			List<Locale> locales = this.locales.isEmpty() ? Collections.singletonList(Locale.ENGLISH) : this.locales;
			return new Quasicord(namespace, locales, configRoot, activity, eventHandler);
		}
	}
}
