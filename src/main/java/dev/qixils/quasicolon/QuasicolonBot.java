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
import dev.qixils.quasicolon.locale.LocaleManager;
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
import org.jetbrains.annotations.NotNull;
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
	/**
	 * A command prefix that is impossible to type.
	 * Uses an obscure unicode character to make {@link String#startsWith(String)} calls terminate faster.
	 */
	private static final String UNKNOWN_PREFIX = "\u2603".repeat(4001);

	protected final JDA jda;
	protected final ConfigurationNode rootNode;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final Environment environment;
	protected final DatabaseManager database;
	protected final LocaleManager localeManager = new LocaleManager();
	protected final AbstractVariables variables;
	protected final TemporaryListenerExecutor tempListenerExecutor = new TemporaryListenerExecutor();
	protected final CommandManager<JDACommandSender> commandManager;
	protected final long ownerId;
	protected final long botId;

	protected QuasicolonBot(@NotNull AbstractVariables variables) throws ConfigurateException, LoginException, InterruptedException {
		this(variables, Paths.get(".").toAbsolutePath());
	}

	protected QuasicolonBot(@NotNull AbstractVariables variables, @NotNull Path configRoot) throws ConfigurateException, LoginException, InterruptedException {
		this.variables = Objects.requireNonNull(variables, "variables cannot be null");

		YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
				.path(configRoot.resolve("config.yml"))
				// TODO: default config options
				.build();
		rootNode = loader.load();
		environment = Environment.valueOf(rootNode.node("environment").getString("TEST").toUpperCase(Locale.ENGLISH));
		database = new DatabaseManager("semicolon", environment);
		jda = initJDA(); // should be executed last-ish
		jda.setRequiredScopes("applications.commands");
		botId = jda.getSelfUser().getIdLong();
		ownerId = jda.retrieveApplicationInfo().complete().getOwner().getIdLong();

		commandManager = new JDA4CommandManager<>(jda, this::getPrefix, this::hasPermission,
				AsynchronousCommandExecutionCoordinator.<JDACommandSender>newBuilder().withAsynchronousParsing().build(),
				Function.identity(), Function.identity());
		// TODO: register custom exception handlers
	}

	@NotNull
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
		} catch (InterruptedException ignored) {}
		return jda;
	}

	/**
	 * Whether or not the bot developer has been warned about not having a `prefix` variable.
	 */
	private boolean prefixWarned = false;

	/**
	 * Gets the prefix for a given message event.
	 * @param cmdSender wrapper of a message event
	 * @return command prefix
	 */
	// TODO: briefly cache output (to reduce DB calls)
	public @NotNull String getPrefix(@NotNull JDACommandSender cmdSender) {
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

	protected Set<String> prefixes = null;

	/**
	 * Gets this bot's default prefixes.
	 * <p>
	 * By default, this fetches the {@code prefixes} config option from the bot's {@code config.yml}.
	 * @return default command prefixes
	 */
	public @NotNull Set<@NotNull String> getDefaultPrefixes() {
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
	 * @param messageContent message to check
	 * @param prefixes available prefixes to search for
	 * @return prefix used in the message
	 */
	protected @NotNull String findPrefix(@NotNull String messageContent, @NotNull Set<@NotNull String> prefixes) {
		if (prefixes.isEmpty()) {
			logger.error("No prefixes were supplied to #findPrefix!");
			return UNKNOWN_PREFIX;
		}
		return Objects.requireNonNullElse(CollectionUtil.first(prefixes, messageContent::startsWith), UNKNOWN_PREFIX);
	}

	public boolean hasPermission(@NotNull JDACommandSender sender, @NotNull String permission) {
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

	protected static final Pattern NEWLINE_SPLIT = Pattern.compile("\n");
	protected static final Pattern COLON_SPLIT = Pattern.compile(":");
	protected static final Pattern COMMA_SPLIT = Pattern.compile(",");
	protected void verifyPermissions(@NotNull JDACommandSender sender, @NotNull String permission) throws NoPermissionException {
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
	 * @param listener temporary listener to register
	 */
	// we don't expose the raw executor in a getter because objects could abuse the #onEvent method
	public void register(@NotNull TemporaryListener<?> listener) {
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
	 * @return the JDA API
	 */
	public @NotNull JDA getJDA() {
		return jda;
	}

	/**
	 * Returns the root {@link ConfigurationNode} representing the options set in {@code config.yml}.
	 * @return root configuration node
	 */
	public @NotNull ConfigurationNode getRootConfigNode() {
		return rootNode;
	}

	/**
	 * Returns the {@link Logger} for this bot.
	 * @return bot's logger
	 */
	public @NotNull Logger getLogger() {
		return logger;
	}

	/**
	 * Returns the {@link Environment} that the bot is currently running in.
	 * @return execution environment
	 */
	public @NotNull Environment getEnvironment() {
		return environment;
	}

	/**
	 * Returns the {@link DatabaseManager} which facilitates communication with the MongoDB database owned by this bot.
	 * @return database manager
	 */
	public @NotNull DatabaseManager getDatabaseManager() {
		return database;
	}

	/**
	 * Returns the {@link LocaleManager} which is used to obtain translated strings.
	 * @return locale manager
	 */
	public @NotNull LocaleManager getLocaleManager() {
		return localeManager;
	}

	/**
	 * Returns the variable registry which maps the names of variables to their parsers.
	 * @return variable registry
	 */
	public @NotNull AbstractVariables getVariables() {
		return variables;
	}
}
