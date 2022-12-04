/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.test;

import dev.qixils.quasicolon.test.actions.DummyRestAction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class DummyUser implements User {
	private final JDA jda;
	private final String username;
	private final String discriminator;
	private final short discrimShort;
	private final long id;
	private final boolean bot;
	private final boolean system;

	public DummyUser(@NotNull JDA jda, long id, boolean bot, boolean system) {
		this.jda = Objects.requireNonNull(jda, "jda");
		this.id = id;
		this.username = null;
		this.discriminator = null;
		this.discrimShort = 0;
		this.bot = bot;
		this.system = system;
	}

	public DummyUser(@NotNull JDA jda, @NotNull String username, @NotNull String discriminator, long id, boolean bot, boolean system) {
		this.jda = Objects.requireNonNull(jda, "jda");
		this.username = Objects.requireNonNull(username, "username");
		this.id = id;
		this.bot = bot;
		this.system = system;

		Objects.requireNonNull(discriminator, "discriminator");
		if (discriminator.length() != 4)
			throw new IllegalArgumentException("Illegal length for discriminator");
		this.discriminator = discriminator;
		this.discrimShort = Short.parseShort(discriminator);
	}


	@NotNull
	@Override
	public String getName() {
		if (username == null)
			throw new UnsupportedOperationException("User was created from only an ID");
		return username;
	}

	@NotNull
	@Override
	public String getDiscriminator() {
		if (discriminator == null)
			throw new UnsupportedOperationException("User was created from only an ID");
		return discriminator;
	}

	@Nullable
	@Override
	public String getAvatarId() {
		return null;
	}

	@NotNull
	@Override
	public String getDefaultAvatarId() {
		if (discriminator == null)
			throw new UnsupportedOperationException("User was created from only an ID");
		return String.valueOf(discrimShort % 5);
	}

	@NotNull
	@Override
	public CacheRestAction<Profile> retrieveProfile() {
		return new DummyRestAction<>(jda);
	}

	@NotNull
	@Override
	public String getAsTag() {
		if (username == null)
			throw new UnsupportedOperationException("User was created from only an ID");
		return username + "#" + discriminator;
	}

	@Override
	public boolean hasPrivateChannel() {
		return false;
	}

	@NotNull
	@Override
	public CacheRestAction<PrivateChannel> openPrivateChannel() {
		return new DummyRestAction<>(jda);
	}

	@NotNull
	@Override
	public List<Guild> getMutualGuilds() {
		return Collections.emptyList();
	}

	@Override
	public boolean isBot() {
		return bot;
	}

	@Override
	public boolean isSystem() {
		return system;
	}

	@NotNull
	@Override
	public JDA getJDA() {
		return jda;
	}

	@NotNull
	@Override
	public EnumSet<UserFlag> getFlags() {
		return EnumSet.noneOf(UserFlag.class);
	}

	@Override
	public int getFlagsRaw() {
		return 0;
	}

	@NotNull
	@Override
	public String getAsMention() {
		return "<@" + id + ">";
	}

	@Override
	public long getIdLong() {
		return id;
	}
}
