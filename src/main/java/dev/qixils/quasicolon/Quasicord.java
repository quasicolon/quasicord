/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

/**
 * Managing class for a <a href="https://discord.com/">Discord</a> bot which utilizes a
 * <a href="https://mongodb.com/">MongoDB</a> {@link #getDatabaseManager() database}.
 * <p>
 * See {@link Builder} for instructions on how to create a new instance.
 */
public class Quasicord {
	protected final @NonNull JDA jda;
	protected final @NonNull ConfigurationNode rootNode;
	protected final @NonNull Logger logger = LoggerFactory.getLogger(getClass());
	protected final @NonNull Environment environment;
	protected final @NonNull DatabaseManager database;
	protected final @NonNull RegistryRegistry rootRegistry;
	protected final @NonNull EventDispatcher eventDispatcher = new EventDispatcher();
	protected final @NonNull TemporaryListenerExecutor tempListenerExecutor = new TemporaryListenerExecutor();
	protected final @NonNull String namespace;
	protected final long ownerId;
	protected final long botId;
	protected final @NonNull TranslationProvider translationProvider;
	protected final @NonNull LocaleProvider localeProvider;

	protected Quasicord(@NonNull String namespace, @NonNull Locale defaultLocale, @NonNull Path configRoot, @Nullable Activity activity, @Nullable Object eventHandler) throws LoginException, InterruptedException, IOException {
		// misc initialization
		this.namespace = namespace;

		// register default event handler
		if (eventHandler != null)
			eventDispatcher.registerListeners(eventHandler);

		// register translation providers
		translationProvider = new TranslationProvider(namespace, defaultLocale);
		TranslationProvider.registerInstance(translationProvider);
		TranslationProvider.registerInstance(new TranslationProvider(Key.LIBRARY_NAMESPACE, defaultLocale));

		// load configuration
		YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
				.path(configRoot.resolve("config.yml"))
				// TODO: default config options
				.build();
		rootNode = loader.load();
		environment = Environment.valueOf(rootNode.node("environment").getString("TEST").toUpperCase(Locale.ENGLISH));

		// load database and locale provider
		database = new DatabaseManager(namespace, environment);
		localeProvider = new LocaleProvider(defaultLocale, database);

		// initialize JDA and relevant data
		jda = initJDA(activity); // should be executed last-ish
		botId = jda.getSelfUser().getIdLong();
		ownerId = jda.retrieveApplicationInfo().complete().getOwner().getIdLong();

		// initialize registry (depends on JDA)
		rootRegistry = new RegistryRegistry(this);
	}

	@NonNull
	protected JDA initJDA(@Nullable Activity activity) {
		JDABuilder builder = JDABuilder.createDefault(rootNode.node("token").getString())
				.disableIntents(GatewayIntent.DIRECT_MESSAGE_TYPING,
						GatewayIntent.GUILD_MESSAGE_TYPING,
						// GatewayIntent.GUILD_INTEGRATIONS, // unused, apparently
						GatewayIntent.GUILD_WEBHOOKS,
						GatewayIntent.GUILD_INVITES,
						GatewayIntent.GUILD_VOICE_STATES)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setEventManager(new AnnotatedEventManager())
				.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE);
		if (activity != null)
			builder.setActivity(activity);
		MessageRequest.setDefaultMentions(Collections.emptySet());
		JDA jda = builder.build();
		jda.setRequiredScopes("applications.commands");
		jda.addEventListener(tempListenerExecutor);
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
	 * Returns the root {@link ConfigurationNode} representing the options set in {@code config.yml}.
	 *
	 * @return root configuration node
	 */
	public @NonNull ConfigurationNode getRootConfigNode() {
		return rootNode;
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
		return environment;
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
		protected @NonNull Locale defaultLocale = Locale.ENGLISH;
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
			this.defaultLocale = locale;
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
			return new Quasicord(namespace, defaultLocale, configRoot, activity, eventHandler);
		}
	}
}
