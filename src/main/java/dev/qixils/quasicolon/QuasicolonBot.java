package dev.qixils.quasicolon;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.jda.JDAGuildSender;
import cloud.commandframework.jda.JDAPrivateSender;
import dev.qixils.quasicolon.db.DatabaseManager;
import dev.qixils.quasicolon.error.permissions.DMOnlyException;
import dev.qixils.quasicolon.error.permissions.GuildOnlyException;
import dev.qixils.quasicolon.error.permissions.NoPermissionException;
import dev.qixils.quasicolon.error.permissions.OwnerOnlyException;
import dev.qixils.quasicolon.locale.LocaleProvider;
import dev.qixils.quasicolon.locale.TranslationProvider;
import dev.qixils.quasicolon.processors.WritePermissionChecker;
import dev.qixils.quasicolon.utils.CollectionUtil;
import dev.qixils.quasicolon.utils.PermissionUtil;
import dev.qixils.quasicolon.variables.AbstractVariables;
import dev.qixils.quasicolon.variables.parsers.PrefixParser;
import dev.qixils.quasicolon.variables.parsers.VariableParser;
import io.leangen.geantyref.TypeFactory;
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
 * Abstract <a href="https://discord.com/">Discord</a> bot which utilizes a <a href="https://mongodb.com/">MongoDB</a>
 * {@link #getDatabaseManager() database}.
 */
public abstract class QuasicolonBot {
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
	protected final @NonNull AbstractVariables variables;
	protected final @NonNull TemporaryListenerExecutor tempListenerExecutor = new TemporaryListenerExecutor();
	protected final @NonNull CommandManager<JDACommandSender> commandManager;
	protected final @NonNull String namespace;
	protected final long ownerId;
	protected final long botId;
	protected final @NonNull TranslationProvider translationProvider;
	protected final @NonNull LocaleProvider localeProvider;
	@SuppressWarnings("NullabilityAnnotations") // ffs intellij
	protected @MonotonicNonNull Set<String> prefixes = null;
	/**
	 * Whether the bot developer has been warned about not having a `prefix` variable.
	 */
	private boolean prefixWarned = false;

	protected QuasicolonBot(@NonNull String namespace, @NonNull Locale defaultLocale, @NonNull AbstractVariables variables) throws ConfigurateException, LoginException, InterruptedException {
		this(namespace, defaultLocale, variables, Paths.get(".").toAbsolutePath());
	}

	protected QuasicolonBot(@NonNull String namespace, @NonNull Locale defaultLocale, @NonNull AbstractVariables variables, @NonNull Path configRoot) throws ConfigurateException, LoginException, InterruptedException {
		// register translation providers
		this.namespace = namespace;
		TranslationProvider internalTranslationProvider = new TranslationProvider(QuasicolonBot.class, defaultLocale);
		TranslationProvider.registerInstance(Key.LIBRARY_NAMESPACE, internalTranslationProvider);
		this.translationProvider = new TranslationProvider(getClass(), defaultLocale);
		TranslationProvider.registerInstance(namespace, this.translationProvider);

		this.variables = Objects.requireNonNull(variables, "variables cannot be null");

		YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
				.path(configRoot.resolve("config.yml"))
				// TODO: default config options
				.build();
		rootNode = loader.load();
		environment = Environment.valueOf(rootNode.node("environment").getString("TEST").toUpperCase(Locale.ENGLISH));
		database = new DatabaseManager("semicolon", environment);
		localeProvider = new LocaleProvider(defaultLocale, database);
		jda = initJDA(); // should be executed last-ish
		jda.setRequiredScopes("applications.commands");
		botId = jda.getSelfUser().getIdLong();
		ownerId = jda.retrieveApplicationInfo().complete().getOwner().getIdLong();

		commandManager = new JDA4CommandManager<>(jda, this::getPrefix, this::hasPermission,
				AsynchronousCommandExecutionCoordinator.<JDACommandSender>newBuilder().withAsynchronousParsing().build(),
				Function.identity(), Function.identity());

		commandManager.registerCommandPostProcessor(new WritePermissionChecker());
		// TODO: register custom exception handlers
	}

	@NonNull
	protected JDA initJDA() throws LoginException {
		JDABuilder builder = JDABuilder.createDefault(rootNode.node("token").getString())
				.disableIntents(GatewayIntent.DIRECT_MESSAGE_TYPING,
						GatewayIntent.GUILD_MESSAGE_TYPING,
						// GatewayIntent.GUILD_INTEGRATIONS, // unused, apparently
						GatewayIntent.GUILD_WEBHOOKS,
						GatewayIntent.GUILD_INVITES,
						GatewayIntent.GUILD_VOICE_STATES)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setActivity(Activity.watching("semicolon.qixils.dev"))
				.setEventManager(new AnnotatedEventManager())
				.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE);
		AllowedMentions.setDefaultMentions(Collections.emptySet());
		JDA jda = builder.build();
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
		VariableParser<?> genericParser = getVariables().get("prefix");
		if (genericParser == null) {
			if (!prefixWarned) {
				prefixWarned = true;
				logger.error("'prefix' variable is unavailable, please implement this!");
			}
			return findPrefix(messageContent, getDefaultPrefixes()); // generic prefix
		}
		if (!(genericParser instanceof PrefixParser parser)) {
			if (!prefixWarned) {
				prefixWarned = true;
				logger.error("'prefix' variable is misconfigured. Expected PrefixParser, received " + genericParser.getClass().getSimpleName());
			}
			return findPrefix(messageContent, getDefaultPrefixes()); // generic prefix
		}

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

		// TODO: custom permission parsing
		// - separated by spaces or \n
		// - different types of permissions:
		//  - `guild` or `dm` indicating command must be run in DMs
		//  - `owner` indicating you must be Me to use
		//  - `perms:x,y,z` indicates that the user must have effective permissions x,y,z
		//  - `botperms:x,y,z` indicates that the bot must have effective permissions
		// - unfortunately cannot reply to messages with failure here, may have to do in a command preprocessor?
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
				if (type.equals("perms") || type.equals("botperms")) {
					for (String permText : COMMA_SPLIT.split(params[1])) {
						Permission perm = Permission.valueOf(permText.toLowerCase(Locale.ENGLISH));
						if (PermissionUtil.checkPermission(sender, perm)) {
							// TODO
						}
					}
				} else {
					throw new IllegalStateException("Unexpected key for node '" + node + "'");
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

	// boilerplate

	/**
	 * Returns the {@link JDA} API for interacting with Discord.
	 *
	 * @return the JDA API
	 */
	public @NonNull JDA getJDA() {
		return jda;
	}

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
	 * Returns the variable registry which maps the names of variables to their parsers.
	 *
	 * @return variable registry
	 */
	public @NonNull AbstractVariables getVariables() {
		return variables;
	}

	/**
	 * Returns the bot's namespace which is used for fetching translation strings.
	 *
	 * @return bot's namespace
	 */
	public @NonNull String getNamespace() {
		return namespace;
	}
}
