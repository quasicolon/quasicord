/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class DummyApplicationInfo implements ApplicationInfo {
	private final JDA jda;
	private final User self;
	private final User owner;

	public DummyApplicationInfo(JDA jda, User self, User owner) {
		this.jda = jda;
		this.self = self;
		this.owner = owner;
	}

	@Override
	public boolean doesBotRequireCodeGrant() {
		return false;
	}

	@NotNull
	@Override
	public String getDescription() {
		return "This is a bot";
	}

	@Nullable
	@Override
	public String getTermsOfServiceUrl() {
		return null;
	}

	@Nullable
	@Override
	public String getPrivacyPolicyUrl() {
		return null;
	}

	@Nullable
	@Override
	public String getIconId() {
		return null;
	}

	@Nullable
	@Override
	public String getIconUrl() {
		return null;
	}

	@Nullable
	@Override
	public ApplicationTeam getTeam() {
		return null;
	}

	@NotNull
	@Override
	public ApplicationInfo setRequiredScopes(@NotNull Collection<String> scopes) {
		jda.setRequiredScopes(scopes);
		return this;
	}

	@NotNull
	@Override
	public String getInviteUrl(@Nullable String guildId, @Nullable Collection<Permission> permissions) {
		return jda.getInviteUrl(permissions) + "&guild_id=" + guildId;
	}

	@NotNull
	@Override
	public JDA getJDA() {
		return jda;
	}

	@NotNull
	@Override
	public String getName() {
		return self.getName();
	}

	@NotNull
	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public boolean isBotPublic() {
		return false;
	}

	@NotNull
	@Override
	public List<String> getTags() {
		return Collections.emptyList();
	}

	@NotNull
	@Override
	public List<String> getRedirectUris() {
		return Collections.emptyList();
	}

	@Nullable
	@Override
	public String getInteractionsEndpointUrl() {
		return null;
	}

	@Nullable
	@Override
	public String getRoleConnectionsVerificationUrl() {
		return null;
	}

	@Override
	public String getCustomAuthorizationUrl() {
		return null;
	}

	@NotNull
	@Override
	public List<String> getScopes() {
		return Collections.emptyList();
	}

	@NotNull
	@Override
	public EnumSet<Permission> getPermissions() {
		return EnumSet.noneOf(Permission.class);
	}

	@Override
	public long getPermissionsRaw() {
		return 0;
	}

	@Override
	public long getFlagsRaw() {
		return 0;
	}

	@Override
	public long getIdLong() {
		return self.getIdLong();
	}
}
