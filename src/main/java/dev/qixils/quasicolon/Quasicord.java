/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.jda.JDAGuildSender;
import cloud.commandframework.jda.JDAPrivateSender;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.meta.CommandMeta;
import dev.qixils.quasicolon.db.DatabaseManager;
import dev.qixils.quasicolon.error.permissions.BotMissingPermException;
import dev.qixils.quasicolon.error.permissions.DMOnlyException;
import dev.qixils.quasicolon.error.permissions.GuildOnlyException;
import dev.qixils.quasicolon.error.permissions.NoPermissionException;
import dev.qixils.quasicolon.error.permissions.OwnerOnlyException;
import dev.qixils.quasicolon.error.permissions.UserMissingPermException;
import dev.qixils.quasicolon.events.EventDispatcher;
import dev.qixils.quasicolon.locale.LocaleProvider;
import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.processors.GuildCommandProcessor;
import dev.qixils.quasicolon.processors.WritePermissionChecker;
import dev.qixils.quasicolon.registry.core.RegistryRegistry;
import dev.qixils.quasicolon.utils.CollectionUtil;
import dev.qixils.quasicolon.utils.PermissionUtil;
import dev.qixils.quasicolon.variables.parsers.PrefixParser;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.AllowedMentions;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Managing class for a <a href="https://discord.com/">Discord</a> bot which utilizes a
 * <a href="https://mongodb.com/">MongoDB</a> {@link #getDatabaseManager() database}.
 * <p>
 * See {@link Builder} for instructions on how to create a new instance.
 */
public class Quasicord {
	public static final @NonNull CloudKey<Quasicord> BOT_KEY = SimpleCloudKey.of("quasicord.bot", TypeToken.get(Quasicord.class));
	public static final @NonNull CloudKey<String> NAMESPACE_KEY = SimpleCloudKey.of("quasicord.namespace", TypeToken.get(String.class));
	public static final CommandMeta.@NonNull Key<Long> GUILD_KEY = CommandMeta.Key.of(TypeToken.get(Long.class), "quasicord.guild_id");
	protected static final @NonNull Pattern NEWLINE_SPLIT = Pattern.compile("\n");
	protected static final @NonNull Pattern COLON_SPLIT = Pattern.compile(":");
	protected static final @NonNull Pattern COMMA_SPLIT = Pattern.compile(",");
	/**
	 * A command prefix that is impossible to type.
	 * Uses an obscure unicode character to make {@link String#startsWith(String)} calls terminate faster.
	 */
	private static final @NonNull String UNKNOWN_PREFIX = "\u2603".repeat(4001);
	protected final @NonNull JDA jda;
	protected final @NonNull ConfigurationNode rootNode;
	protected final @NonNull Logger logger = LoggerFactory.getLogger(getClass());
	protected final @NonNull Environment environment;
	protected final @NonNull DatabaseManager database;
	protected final @NonNull RegistryRegistry rootRegistry;
	protected final @NonNull EventDispatcher eventDispatcher = new EventDispatcher();
	protected final @NonNull TemporaryListenerExecutor tempListenerExecutor = new TemporaryListenerExecutor();
	protected final @NonNull CommandManager<JDACommandSender> commandManager;
	protected final @NonNull String namespace;
	protected final long ownerId;
	protected final long botId;
	protected final @NonNull TranslationProvider translationProvider;
	protected final @NonNull LocaleProvider localeProvider;
	@SuppressWarnings("NullabilityAnnotations") // ffs intellij
	protected @MonotonicNonNull Set<String> prefixes = null;

	protected Quasicord(@NonNull String namespace, @NonNull Locale defaultLocale, @NonNull Path configRoot, @Nullable Activity activity, @Nullable Object eventHandler) throws ConfigurateException, LoginException, InterruptedException {
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

		// initialize command manager
		commandManager = new JDA4CommandManager<>(jda, this::getPrefix, this::hasPermission,
				AsynchronousCommandExecutionCoordinator.<JDACommandSender>newBuilder().withAsynchronousParsing().build(),
				Function.identity(), Function.identity());
		commandManager.registerCommandPreProcessor(context -> {
			CommandContext<JDACommandSender> ctx = context.getCommandContext();
			ctx.store(BOT_KEY, this);
			ctx.store(NAMESPACE_KEY, namespace);
		});
		commandManager.registerCommandPostProcessor(new WritePermissionChecker());
		commandManager.registerCommandPostProcessor(new GuildCommandProcessor());
		// TODO: register custom exception handlers
		// TODO: support @Confirmation with an ephemeral message and yes/no buttons
	}

	@NonNull
	protected JDA initJDA(@Nullable Activity activity) throws LoginException {
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
		AllowedMentions.setDefaultMentions(Collections.emptySet());
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
	 * Gets the prefix for a given message event.
	 *
	 * @param cmdSender wrapper of a message event
	 * @return command prefix
	 */
	// TODO: briefly cache output (to reduce DB calls)
	public @NonNull String getPrefix(@NonNull JDACommandSender cmdSender) {
		// if this is a custom command event(?) then no prefix is applicable
		if (cmdSender.getEvent().isEmpty())
			return UNKNOWN_PREFIX;

		final String messageContent = cmdSender.getEvent().get().getMessage().getContentRaw();

		// if command was not used in a server, return default prefixes
		if (!(cmdSender instanceof JDAGuildSender sender))
			return findPrefix(messageContent, getDefaultPrefixes());

		// get server's prefix config
		PrefixParser parser = rootRegistry.VARIABLE_REGISTRY.PREFIX;
		return findPrefix(messageContent,
				parser.fromDatabase(sender.getMember().getGuild().getIdLong(), "prefix") // get server's configured prefixes
						.blockOptional().orElse(getDefaultPrefixes())); // or else use default prefixes
	}

	/**
	 * Gets this bot's default prefixes.
	 * <p>
	 * By default, this fetches the {@code prefixes} config option from the bot's {@code config.yml}.
	 *
	 * @return default command prefixes
	 */
	public @NonNull Set<@NonNull String> getDefaultPrefixes() {
		if (this.prefixes == null) {
			Set<String> prefixes;
			try {
				//noinspection unchecked
				prefixes = (Set<String>) getRootConfigNode().node("prefixes").get(TypeFactory.parameterizedClass(Set.class, String.class));
				if (prefixes == null) {
					logger.error("Default prefixes could not be found");
					System.exit(1);
				}
			} catch (ClassCastException | SerializationException exc) {
				logger.error("Default prefixes could not be loaded", exc);
				System.exit(1);
				prefixes = Collections.emptySet();
			}
			this.prefixes = prefixes;
		}

		return this.prefixes;
	}

	/**
	 * Finds the prefix used in a message.
	 *
	 * @param messageContent message to check
	 * @param prefixes       available prefixes to search for
	 * @return prefix used in the message
	 */
	protected @NonNull String findPrefix(@NonNull String messageContent, @NonNull Set<@NonNull String> prefixes) {
		if (prefixes.isEmpty()) {
			logger.error("No prefixes were supplied to #findPrefix!");
			return UNKNOWN_PREFIX;
		}
		return Objects.requireNonNullElse(CollectionUtil.first(prefixes, messageContent::startsWith), UNKNOWN_PREFIX);
	}

	public boolean hasPermission(@NonNull JDACommandSender sender, @NonNull String permission) {
		try {
			verifyPermissions(sender, permission);
			return true;
		} catch (NoPermissionException exc) {
			return false;
		}

		// TODO:  replace this with a command preprocessor so we can reply with an error message
		//  and also so we can create our own permission annotations instead of using
		//  the string-based one from the cloud library

		// original to.do for custom permission parsing:
		//  - separated by spaces or \n
		//  - different types of permissions:
		//   - `guild` or `dm` indicating command must be run in DMs
		//   - `owner` indicating you must be Me to use
		//   - `perms:x,y,z` indicates that the user must have effective permissions x,y,z
		//   - `botperms:x,y,z` indicates that the bot must have effective permissions
		//  - unfortunately cannot reply to messages with failure here, may have to do in a command preprocessor?
	}

	protected void verifyPermissions(@NonNull JDACommandSender sender, @NonNull String permission) throws NoPermissionException {
		if (sender.getEvent().isEmpty()) return;
		String[] nodes = NEWLINE_SPLIT.split(permission);
		for (String node : nodes) {
			if (node.isBlank())
				throw new IllegalArgumentException("Invalid permission node (must not be blank)");

			String[] params = COLON_SPLIT.split(node);
			if (params.length == 0 || params.length > 2)
				throw new IllegalArgumentException("Invalid permission node '" + node + "' (expected 1-2 params, received " + params.length + ")");

			String type = params[0];
			if (type.isBlank())
				throw new IllegalArgumentException("Invalid permission node '" + node + "' (empty 'type' parameter)");

			if (params.length == 1) {
				switch (type) {
					case "guild":
						if (!(sender instanceof JDAGuildSender))
							throw new GuildOnlyException();
						break;
					case "dm":
						if (!(sender instanceof JDAPrivateSender))
							throw new DMOnlyException();
						break;
					case "owner":
						if (sender.getUser().getIdLong() != ownerId)
							throw new OwnerOnlyException();
						break;
					default:
						throw new IllegalStateException("Unexpected key for node '" + type + "'");
				}
			} else {
				JDACommandSender senderToCheck = switch (type) {
					case "perms" -> sender;
					case "botperms" -> {
						// get bot's JDACommandSender
						if (sender instanceof JDAGuildSender guildSender)
							yield new JDAGuildSender(null, guildSender.getMember().getGuild().getSelfMember(), guildSender.getTextChannel());
						else if (sender instanceof JDAPrivateSender privateSender)
							yield new JDAPrivateSender(null, jda.getSelfUser(), privateSender.getPrivateChannel());
						else
							throw new IllegalArgumentException("Unknown command sender type");
					}
					default -> throw new IllegalStateException("Unexpected key for node '" + node + "'");
				};

				boolean isCheckingBot = type.equals("botperms");

				for (String permText : COMMA_SPLIT.split(params[1])) {
					Permission perm = Permission.valueOf(permText.toUpperCase(Locale.ENGLISH));
					if (!PermissionUtil.checkPermission(senderToCheck, perm)) {
						// missing a perm
						if (isCheckingBot) {
							throw new BotMissingPermException(perm);
						} else {
							throw new UserMissingPermException(perm);
						}
					}
				}
			}
		}
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
	 * Gets the bot's manager for legacy text-based commands.
	 * <p>
	 * <b>Note:</b> Command registration should be done through
	 * {@link dev.qixils.quasicolon.cogs.Cog Cogs} instead of using the command manager directly.
	 *
	 * @return legacy command manager
	 */
	public @NonNull CommandManager<JDACommandSender> getCommandManager() {
		return commandManager;
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
		public @NonNull Quasicord build() throws IllegalStateException, LoginException, InterruptedException, ConfigurateException {
			if (namespace == null)
				throw new IllegalStateException("namespace must be set");
			return new Quasicord(namespace, defaultLocale, configRoot, activity, eventHandler);
		}
	}
}
