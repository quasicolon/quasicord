package dev.qixils.quasicord.extensions

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.internal.utils.Checks
import net.dv8tion.jda.internal.utils.PermissionUtil

/**
 * Checks to see if the provided [Permissions][Permission] are available in a private channel.
 * @param permissions The [Permissions][Permission] being checked for.
 * @throws IllegalArgumentException if `permissions` is `null`
 * @return True if all the provided [Permissions][Permission] are available in a private channel
 */
@Throws(IllegalArgumentException::class)
fun PermissionUtil.checkPrivateChannelPermission(vararg permissions: Permission?): Boolean {
	Checks.notNull(permissions, "permissions")

	for (permission in permissions) {
		val canUse = when (permission) {
			Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_HISTORY, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EXT_STICKER, Permission.VOICE_STREAM, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_USE_VAD -> true
			else -> false
		}
		if (!canUse) return false
	}
	return true
}

fun GuildChannel.botHas(vararg permissions: Permission) = PermissionUtil.checkPermission(permissionContainer, this.guild.selfMember, *permissions)

fun GuildChannel.memberHas(member: Member, vararg permissions: Permission) = PermissionUtil.checkPermission(permissionContainer, this.guild.selfMember, *permissions)
