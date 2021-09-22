package dev.qixils.semicolon.variables;

import dev.qixils.semicolon.Semicolon;
import dev.qixils.semicolon.variables.parsers.ListParser;
import dev.qixils.semicolon.variables.parsers.StringParser;
import dev.qixils.semicolon.variables.parsers.numbers.ByteParser;
import dev.qixils.semicolon.variables.parsers.numbers.NumberParser;
import dev.qixils.semicolon.variables.parsers.snowflakes.ChannelParser;
import dev.qixils.semicolon.variables.parsers.snowflakes.EmoteParser;
import dev.qixils.semicolon.variables.parsers.snowflakes.RoleParser;
import dev.qixils.semicolon.variables.parsers.snowflakes.UserParser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Variables extends AbstractVariables {
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
	public final ListParser<User> IGNORED_MESSAGE_LOG_USERS;

	public Variables(@NotNull Semicolon bot) {
		Objects.requireNonNull(bot, "bot cannot be null");
		PREFIX = register("prefix", new ListParser<>(bot, new StringParser(bot), ' '));
		STARBOARD_CHANNEL = register("starboard_channel", new ChannelParser<>(bot, TextChannel.class));
		STARBOARD_THRESHOLD = register("starboard_threshold", new ByteParser(bot, NumberParser.ParserFilter.ONLY_POSITIVE));
		STARBOARD_EMOJI = register("starboard_emoji", new StringParser(bot));
		MUTED_ROLE = register("muted_role", new RoleParser(bot));
		JOIN_ROLE = register("join_role", new RoleParser(bot));
		REPORT_EMOTE = register("report_emote", new EmoteParser(bot)); // TODO: handle rename in legacy import
		PING_BAN_THRESHOLD = register("ping_ban_threshold", new ByteParser(bot, NumberParser.ParserFilter.NON_NEGATIVE));
		PING_BAN_JOIN_BYPASS = register("ping_ban_join_bypass", new ByteParser(bot, NumberParser.ParserFilter.NON_NEGATIVE));
		PING_BAN_CREATION_BYPASS = register("ping_ban_creation_bypass", new ByteParser(bot, NumberParser.ParserFilter.NON_NEGATIVE));
		MISBEHAVIOR_THRESHOLD = register("misbehavior_threshold", new ByteParser(bot, NumberParser.ParserFilter.NON_NEGATIVE));
		JOINS_CHANNEL = register("joins_channel", new ChannelParser<>(bot, TextChannel.class));
		MESSAGES_CHANNEL = register("messages_channel", new ChannelParser<>(bot, TextChannel.class));
		NAME_CHANGE_CHANNEL = register("name_change_channel", new ChannelParser<>(bot, TextChannel.class));
		MODMAIL_CHANNEL = register("modmail_channel", new ChannelParser<>(bot, TextChannel.class));
		ROLES_CHANNEL = register("roles_channel", new ChannelParser<>(bot, TextChannel.class));
		IGNORED_MESSAGE_LOG_USERS = register("ignored_message_log_users", new ListParser<>(bot, new UserParser(bot), ','));  // TODO: handle rename in legacy import
	}
}
