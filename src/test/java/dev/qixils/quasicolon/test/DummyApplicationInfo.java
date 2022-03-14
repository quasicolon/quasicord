package dev.qixils.quasicolon.test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

	@Override
	public long getIdLong() {
		return self.getIdLong();
	}
}
