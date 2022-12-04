/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

/**
 * An expansion of JDA's {@link net.dv8tion.jda.internal.utils.PermissionUtil} utility class with
 * a focus on methods relating to the cloud command framework.
 */
public class PermissionUtil extends net.dv8tion.jda.internal.utils.PermissionUtil {

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
				case VIEW_CHANNEL, MESSAGE_SEND, MESSAGE_EMBED_LINKS,
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
