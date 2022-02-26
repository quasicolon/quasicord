package dev.qixils.quasicolon.processors;

import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.jda.JDAGuildSender;
import cloud.commandframework.services.types.ConsumerService;
import dev.qixils.quasicolon.utils.PermissionUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Command post-processor that prevents commands from being executed if the bot cannot
 * write messages in the invoking channel. If possible, the bot will react with the
 * SPEAK-NO-EVIL MONKEY emoji to indicate its inability to speak.
 */
public final class WritePermissionChecker implements CommandPostprocessor<JDACommandSender> {

	@Override
	public void accept(@NonNull CommandPostprocessingContext<JDACommandSender> context) {
		JDACommandSender sender = context.getCommandContext().getSender();
		if (sender.getEvent().isEmpty() || !(sender instanceof JDAGuildSender guildSender))
			return;

		TextChannel channel = guildSender.getTextChannel();
		Member self = channel.getGuild().getSelfMember();

		// if we can write messages then abort this check
		if (PermissionUtil.checkPermission(channel, self, Permission.MESSAGE_SEND))
			return;

		// add SPEAK-NO-EVIL MONKEY to invoking message if possible
		if (PermissionUtil.checkPermission(channel, self, Permission.MESSAGE_ADD_REACTION))
			sender.getEvent().get().getMessage().addReaction("\uD83D\uDE4A").queue();

		// stop processing command
		ConsumerService.interrupt();
	}
}
