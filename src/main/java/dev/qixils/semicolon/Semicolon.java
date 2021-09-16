package dev.qixils.semicolon;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import dev.qixils.semicolon.db.DatabaseManager;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.AllowedMentions;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.Locale;

@Getter
public class Semicolon {
	public static void main(String[] args) throws Exception {
		new Semicolon(args);
	}

	private final JDA jda;
	private final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().build(); // TODO: configure?
	private final ConfigurationNode rootNode;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Environment environment;
	private final DatabaseManager database;
	private final Localizer localizer = new Localizer();

	public Semicolon(String[] args) throws ConfigurateException, LoginException {
		rootNode = loader.load();
		environment = Environment.valueOf(rootNode.node("environment").getString("PRODUCTION").toLowerCase(Locale.ENGLISH));
		database = new DatabaseManager(environment);

		final String token;
		if (args.length == 0)
			token = rootNode.node("token").getString();
		else
			token = args[0];

		JDABuilder builder = JDABuilder.createDefault(args[0])
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
				.disableCache(CacheFlag.ACTIVITY);
		AllowedMentions.setDefaultMentions(Collections.emptySet());
		// TODO: register event listeners
		jda = builder.build();
	}

	public void shutdown() {
		jda.shutdown();
	}
}
