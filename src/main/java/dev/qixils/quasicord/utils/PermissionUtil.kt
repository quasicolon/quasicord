/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.qixils.quasicord.utils

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.internal.utils.Checks
import net.dv8tion.jda.internal.utils.PermissionUtil

/**
 * An expansion of JDA's [PermissionUtil] utility class with
 * a focus on methods relating to the cloud command framework.
 */
object PermissionUtil : PermissionUtil() {

    /**
     * Checks to see if the provided [Permissions][Permission] are available in a private channel.
     * @param permissions The [Permissions][Permission] being checked for.
     * @throws IllegalArgumentException if `permissions` is `null`
     * @return True if all the provided [Permissions][Permission] are available in a private channel
     */
    @Throws(IllegalArgumentException::class)
    fun checkPrivateChannelPermission(vararg permissions: Permission?): Boolean {
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
}
