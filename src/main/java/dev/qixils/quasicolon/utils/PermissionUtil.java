package dev.qixils.quasicolon.utils;

import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.jda.JDAGuildSender;
import cloud.commandframework.jda.JDAPrivateSender;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

/**
 * An expansion of JDA's {@link net.dv8tion.jda.internal.utils.PermissionUtil} utility class with
 * a focus on methods relating to the cloud command framework.
 */
public class PermissionUtil extends net.dv8tion.jda.internal.utils.PermissionUtil {
	/**
	 * Checks to see if the {@link JDACommandSender} has the specified {@link Permission Permissions}.
	 * This method properly deals with {@link net.dv8tion.jda.api.entities.PermissionOverride PermissionOverrides} and Owner status.
	 *
	 * <p><b>Note:</b> this is based on effective permissions, not literal permissions. If a member has permissions that would
	 * enable them to do something without the literal permission to do it, this will still return true.
	 * <br>Example: If a member has the {@link net.dv8tion.jda.api.Permission#ADMINISTRATOR} permission, they will be able to
	 * {@link net.dv8tion.jda.api.Permission#MESSAGE_WRITE MESSAGE_WRITE} in every channel.
	 *
	 * @param  sender
	 *         The {@link JDACommandSender} whose permissions are being checked.
	 * @param  permissions
	 *         The {@link net.dv8tion.jda.api.Permission Permissions} being checked for.
	 *
	 * @throws IllegalArgumentException
	 *         if any of the provided parameters is {@code null}
	 *         or the provided {@link JDACommandSender} is neither from a guild nor a private message
	 *
	 * @return True -
	 *         if the {@link JDACommandSender} effectively has the specified {@link net.dv8tion.jda.api.Permission Permissions}.
	 */
	public static boolean checkPermission(@NotNull JDACommandSender sender, @NotNull Permission... permissions) throws IllegalArgumentException {
		Checks.notNull(sender, "sender");

		if (sender instanceof JDAGuildSender guildSender)
			return checkPermission(guildSender.getTextChannel(), guildSender.getMember(), permissions);
		else if (sender instanceof JDAPrivateSender)
			return checkPrivateChannelPermission(permissions);
		else
			throw new IllegalArgumentException("Unknown command sender type");
	}

	/**
	 * Checks to see if the provided {@link Permission Permissions} are available in a private channel.
	 * @param permissions The {@link net.dv8tion.jda.api.Permission Permissions} being checked for.
	 * @throws IllegalArgumentException if {@code permissions} is {@code null}
	 * @return True if all the provided {@link Permission Permissions} are available in a private channel
	 */
	public static boolean checkPrivateChannelPermission(@NotNull Permission... permissions) throws IllegalArgumentException {
		Checks.notNull(permissions, "permissions");

		for (Permission permission : permissions) {
			boolean canUse = switch (permission) {
				case VIEW_CHANNEL, MESSAGE_READ, MESSAGE_WRITE, MESSAGE_EMBED_LINKS,
						MESSAGE_ADD_REACTION, MESSAGE_ATTACH_FILES, MESSAGE_HISTORY,
						MESSAGE_EXT_EMOJI, MESSAGE_EXT_STICKER, VOICE_STREAM,
						VOICE_CONNECT, VOICE_SPEAK, VOICE_USE_VAD -> true;
				default -> false;
			};
			if (!canUse)
				return false;
		}
		return true;
	}
}
