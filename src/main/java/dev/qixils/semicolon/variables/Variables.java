package dev.qixils.semicolon.variables;

import dev.qixils.semicolon.Semicolon;
import dev.qixils.semicolon.variables.parsers.ListParser;
import dev.qixils.semicolon.variables.parsers.StringParser;
import dev.qixils.semicolon.variables.parsers.VariableParser;
import dev.qixils.semicolon.variables.parsers.numbers.ByteParser;
import dev.qixils.semicolon.variables.parsers.numbers.NumberParser;
import dev.qixils.semicolon.variables.parsers.snowflakes.ChannelParser;
import dev.qixils.semicolon.variables.parsers.snowflakes.EmoteParser;
import dev.qixils.semicolon.variables.parsers.snowflakes.RoleParser;
import dev.qixils.semicolon.variables.parsers.snowflakes.UserParser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Variables {
	private final Map<String, VariableParser<?>> REGISTRY = new HashMap<>();

	@NotNull
	private <T extends VariableParser<?>> T register(@NotNull String variable, @NotNull T parser) {
		REGISTRY.put(Objects.requireNonNull(variable, "variable"),
					 Objects.requireNonNull(parser, "parser"));
		return parser;
	}

	@Nullable
	public VariableParser<?> get(@NotNull String variable) {
		return REGISTRY.get(Objects.requireNonNull(variable, "variable cannot be null"));
	}

	public final ListParser<String> PREFIX;
	public final ChannelParser<TextChannel> STARBOARD_CHANNEL;
	public final ByteParser STARBOARD_THRESHOLD;
	public final StringParser STARBOARD_EMOJI;
	public final RoleParser MUTED_ROLE;
	public final RoleParser JOIN_ROLE;
	public final EmoteParser REPORT_EMOTE;
	public final ByteParser PING_BAN_THRESHOLD;
	public final ByteParser PING_BAN_JOIN_BYPASS;
	public final ByteParser PING_BAN_CREATION_BYPASS;
	public final ByteParser MISBEHAVIOR_THRESHOLD;
	public final ChannelParser<TextChannel> JOINS_CHANNEL;
	public final ChannelParser<TextChannel> MESSAGES_CHANNEL;
	public final ChannelParser<TextChannel> NAME_CHANGE_CHANNEL;
	public final ChannelParser<TextChannel> MODMAIL_CHANNEL;
	public final ChannelParser<TextChannel> ROLES_CHANNEL;
	//public final ChannelParser<TextChannel> MOD_LOG_CHANNEL;
	public final ListParser<User> IGNORED_MESSAGES_LOG_USERS; // TODO: rename

	public Variables(@NotNull Semicolon bot) {
		Objects.requireNonNull(bot, "bot cannot be null");
		PREFIX = new ListParser<>(bot, new StringParser(bot), ' ');
		STARBOARD_CHANNEL = new ChannelParser<>(bot, TextChannel.class);
		STARBOARD_THRESHOLD = new ByteParser(bot, NumberParser.ParserFilter.ONLY_POSITIVE);
		STARBOARD_EMOJI = new StringParser(bot);
		MUTED_ROLE = new RoleParser(bot);
		JOIN_ROLE = new RoleParser(bot);
		REPORT_EMOTE = new EmoteParser(bot);
		PING_BAN_THRESHOLD = new ByteParser(bot, NumberParser.ParserFilter.NON_NEGATIVE);
		PING_BAN_JOIN_BYPASS = new ByteParser(bot, NumberParser.ParserFilter.NON_NEGATIVE);
		PING_BAN_CREATION_BYPASS = new ByteParser(bot, NumberParser.ParserFilter.NON_NEGATIVE);
		MISBEHAVIOR_THRESHOLD = new ByteParser(bot, NumberParser.ParserFilter.NON_NEGATIVE);
		JOINS_CHANNEL = new ChannelParser<>(bot, TextChannel.class);
		MESSAGES_CHANNEL = new ChannelParser<>(bot, TextChannel.class);
		NAME_CHANGE_CHANNEL = new ChannelParser<>(bot, TextChannel.class);
		MODMAIL_CHANNEL = new ChannelParser<>(bot, TextChannel.class);
		ROLES_CHANNEL = new ChannelParser<>(bot, TextChannel.class);
		IGNORED_MESSAGES_LOG_USERS = new ListParser<>(bot, new UserParser(bot), ',');
	}
}
