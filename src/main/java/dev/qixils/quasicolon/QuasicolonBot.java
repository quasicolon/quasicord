package dev.qixils.quasicolon;

import dev.qixils.quasicolon.db.DatabaseManager;
import dev.qixils.quasicolon.locale.LocaleManager;
import dev.qixils.quasicolon.variables.AbstractVariables;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

/**
 * Abstract <a href="https://discord.com/">Discord</a> bot which utilizes a <a href="https://mongodb.com/">MongoDB</a>
 * {@link #getDatabaseManager() database}.
 */
public class QuasicolonBot {
	private final JDA jda;
	private final YamlConfigurationLoader loader;
	private final ConfigurationNode rootNode;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Environment environment;
	private final DatabaseManager database;
	private final LocaleManager localeManager = new LocaleManager();
	private final AbstractVariables variables;
	private final TemporaryListenerExecutor tempListenerExecutor = new TemporaryListenerExecutor();

	protected QuasicolonBot(@NotNull AbstractVariables variables) throws ConfigurateException, LoginException {
		this(variables, Paths.get(".").toAbsolutePath());
	}

	protected QuasicolonBot(@NotNull AbstractVariables variables, @NotNull Path configRoot) throws ConfigurateException, LoginException {
		this.variables = Objects.requireNonNull(variables, "variables cannot be null");

		loader = YamlConfigurationLoader.builder()
				.path(configRoot.resolve("config.yml"))
				// TODO: default config options
				.build();
		rootNode = loader.load();
		environment = Environment.valueOf(rootNode.node("environment").getString("TEST").toUpperCase(Locale.ENGLISH));
		database = new DatabaseManager("semicolon", environment);
		jda = initJDA(); // should be executed last
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
		return jda;
	}

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

	/**
	 * Registers a temporary listener.
	 * @param listener temporary listener to register
	 */
	// we don't expose the raw executor in a getter because of the #onEvent method
	public void register(@NotNull TemporaryListener<?> listener) {
		tempListenerExecutor.register(Objects.requireNonNull(listener, "listener cannot be null"));
	}

	/**
	 * Shuts down the bot as soon as pending tasks have finished execution.
	 */
	public void shutdown() {
		jda.shutdown();
	}
}
