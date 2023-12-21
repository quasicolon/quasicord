/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.test;

import dev.qixils.quasicord.test.actions.DummyCommandListUpdateAction;
import dev.qixils.quasicord.test.actions.DummyRestAction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.StickerPack;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.sticker.StickerUnion;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import okhttp3.OkHttpClient;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DummyJDA implements JDA {
	private final @NonNull ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private final @NonNull OkHttpClient client = new OkHttpClient();
	private final @NonNull DirectAudioController audioController = new DirectAudioController() {
		@NonNull
		@Override
		public JDA getJDA() {
			return DummyJDA.this;
		}

		@Override
		public void connect(@NonNull AudioChannel audioChannel) {
		}

		@Override
		public void disconnect(@NonNull Guild guild) {
		}

		@Override
		public void reconnect(@NonNull AudioChannel audioChannel) {
		}
	};
	private final @NonNull List<Object> eventListeners = new ArrayList<>();

	// IDK why I chose to make a real impl for this
	private final @NonNull Presence presence = new Presence() {
		private @NonNull OnlineStatus onlineStatus = OnlineStatus.INVISIBLE;
		private @Nullable Activity activity = null;
		private boolean idle = false;

		@NonNull
		@Override
		public JDA getJDA() {
			return DummyJDA.this;
		}

		@NonNull
		@Override
		public OnlineStatus getStatus() {
			return onlineStatus;
		}

		@Override
		public void setStatus(@Nullable OnlineStatus status) {
			if (status != null)
				this.onlineStatus = status;
		}

		@Nullable
		@Override
		public Activity getActivity() {
			return activity;
		}

		@Override
		public void setActivity(@Nullable Activity activity) {
			this.activity = activity;
		}

		@Override
		public boolean isIdle() {
			return idle;
		}

		@Override
		public void setIdle(boolean idle) {
			this.idle = idle;
		}

		@Override
		public void setPresence(@Nullable OnlineStatus status, @Nullable Activity activity, boolean idle) {
			if (status != null)
				this.onlineStatus = status;
			this.activity = activity;
			this.idle = idle;
		}

		@Override
		public void setPresence(@Nullable OnlineStatus status, @Nullable Activity activity) {
			if (status != null)
				this.onlineStatus = status;
			this.activity = activity;
		}

		@Override
		public void setPresence(@Nullable OnlineStatus status, boolean idle) {
			if (status != null)
				this.onlineStatus = status;
			this.idle = idle;
		}

		@Override
		public void setPresence(@Nullable Activity activity, boolean idle) {
			this.activity = activity;
			this.idle = idle;
		}
	};
	private final @NonNull SelfUser user = new DummySelfUser(this, "qixils", "Lexi", 140564059417346049L);
	private final @NonNull ApplicationInfo applicationInfo = new DummyApplicationInfo(this, user, user);
	private @NonNull IEventManager eventManager = new AnnotatedEventManager();
	private boolean autoReconnect = true;
	private @NonNull String scopes = "bot";

	@NonNull
	@Override
	public Status getStatus() {
		return Status.CONNECTED;
	}

	@NonNull
	@Override
	public EnumSet<GatewayIntent> getGatewayIntents() {
		return EnumSet.noneOf(GatewayIntent.class);
	}

	@NonNull
	@Override
	public EnumSet<CacheFlag> getCacheFlags() {
		return EnumSet.noneOf(CacheFlag.class);
	}

	@Override
	public boolean unloadUser(long userId) {
		return false;
	}

	@Override
	public long getGatewayPing() {
		return 0;
	}

	@NonNull
	@Override
	public JDA awaitStatus(JDA.@NonNull Status status, Status @NonNull ... failOn) {
		return this;
	}

	@Override
	public boolean awaitShutdown(long duration, @NotNull TimeUnit unit) throws InterruptedException {
		shutdownNow();
		return true;
	}

	@Override
	public int cancelRequests() {
		return 0;
	}

	@NonNull
	@Override
	public ScheduledExecutorService getRateLimitPool() {
		return executor;
	}

	@NonNull
	@Override
	public ScheduledExecutorService getGatewayPool() {
		return executor;
	}

	@NonNull
	@Override
	public ExecutorService getCallbackPool() {
		return executor;
	}

	@NonNull
	@Override
	public OkHttpClient getHttpClient() {
		return client;
	}

	@NonNull
	@Override
	public DirectAudioController getDirectAudioController() {
		return audioController;
	}

	@Override
	public void addEventListener(Object @NonNull ... listeners) {
		this.eventListeners.addAll(Arrays.asList(listeners));
	}

	@Override
	public void removeEventListener(Object @NonNull ... listeners) {
		this.eventListeners.removeAll(Arrays.asList(listeners));
	}

	@NonNull
	@Override
	public List<Object> getRegisteredListeners() {
		return Collections.unmodifiableList(this.eventListeners);
	}

	@NonNull
	@Override
	public RestAction<List<Command>> retrieveCommands() {
		return new DummyRestAction<>(this, Collections.emptyList());
	}

	@NonNull
	@Override
	public RestAction<List<Command>> retrieveCommands(boolean withLocalizations) {
		return new DummyRestAction<>(this, Collections.emptyList());
	}

	@NonNull
	@Override
	public RestAction<Command> retrieveCommandById(@NonNull String id) {
		return new DummyRestAction<>(this);
	}

	@NonNull
	@Override
	public CommandCreateAction upsertCommand(@NonNull CommandData command) {
		return null;
	}

	@NonNull
	@Override
	public CommandListUpdateAction updateCommands() {
		return new DummyCommandListUpdateAction(this);
	}

	@NonNull
	@Override
	public CommandEditAction editCommandById(@NonNull String id) {
		return null;
	}

	@NonNull
	@Override
	public RestAction<Void> deleteCommandById(@NonNull String commandId) {
		return new DummyRestAction<>(this);
	}

	@NotNull
	@Override
	public RestAction<List<RoleConnectionMetadata>> retrieveRoleConnectionMetadata() {
		return new DummyRestAction<>(this, Collections.emptyList());
	}

	@NotNull
	@Override
	public RestAction<List<RoleConnectionMetadata>> updateRoleConnectionMetadata(@NotNull Collection<? extends RoleConnectionMetadata> records) {
		return new DummyRestAction<>(this, new ArrayList<>(records));
	}

	@NonNull
	@Override
	public GuildAction createGuild(@NonNull String name) {
		return null;
	}

	@NonNull
	@Override
	public RestAction<Void> createGuildFromTemplate(@NonNull String code, @NonNull String name, @Nullable Icon icon) {
		return new DummyRestAction<>(this);
	}

	@NonNull
	@Override
	public CacheView<AudioManager> getAudioManagerCache() {
		return EmptyCacheView.emptyCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<User> getUserCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public List<Guild> getMutualGuilds(User @NonNull ... users) {
		return Collections.emptyList();
	}

	@NonNull
	@Override
	public List<Guild> getMutualGuilds(@NonNull Collection<User> users) {
		return Collections.emptyList();
	}

	@NonNull
	@Override
	public CacheRestAction<User> retrieveUserById(long id) {
		return new DummyRestAction<>(this);
	}

	@NonNull
	@Override
	public SnowflakeCacheView<Guild> getGuildCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public Set<String> getUnavailableGuilds() {
		return Collections.emptySet();
	}

	@Override
	public boolean isUnavailable(long guildId) {
		return false;
	}

	@NonNull
	@Override
	public SnowflakeCacheView<Role> getRoleCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<StageChannel> getStageChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<Category> getCategoryCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<TextChannel> getTextChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<NewsChannel> getNewsChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<ForumChannel> getForumChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NotNull
	@Override
	public SnowflakeCacheView<MediaChannel> getMediaChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public SnowflakeCacheView<PrivateChannel> getPrivateChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public CacheRestAction<PrivateChannel> openPrivateChannelById(long userId) {
		return new DummyRestAction<>(this);
	}

	@NonNull
	@Override
	public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public RestAction<StickerUnion> retrieveSticker(@NonNull StickerSnowflake sticker) {
		return new DummyRestAction<>(this);
	}

	@NonNull
	@Override
	public RestAction<List<StickerPack>> retrieveNitroStickerPacks() {
		return new DummyRestAction<>(this, Collections.emptyList());
	}

	@NonNull
	@Override
	public IEventManager getEventManager() {
		return eventManager;
	}

	@Override
	public void setEventManager(@Nullable IEventManager manager) {
		if (manager != null)
			this.eventManager = manager;
	}

	@NonNull
	@Override
	public SelfUser getSelfUser() {
		return user;
	}

	@NonNull
	@Override
	public Presence getPresence() {
		return presence;
	}

	@NonNull
	@Override
	public ShardInfo getShardInfo() {
		return ShardInfo.SINGLE;
	}

	@NonNull
	@Override
	public String getToken() {
		return "";
	}

	@Override
	public long getResponseTotal() {
		return 0;
	}

	@Override
	public int getMaxReconnectDelay() {
		return 0;
	}

	@Override
	public void setRequestTimeoutRetry(boolean retryOnTimeout) {

	}

	@Override
	public boolean isAutoReconnect() {
		return autoReconnect;
	}

	@Override
	public void setAutoReconnect(boolean reconnect) {
		this.autoReconnect = reconnect;
	}

	@Override
	public boolean isBulkDeleteSplittingEnabled() {
		return true;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void shutdownNow() {
		cancelRequests();
	}

	@NonNull
	@Override
	public RestAction<ApplicationInfo> retrieveApplicationInfo() {
		return new DummyRestAction<>(this, applicationInfo);
	}

	@NonNull
	@Override
	public JDA setRequiredScopes(@NonNull Collection<String> scopes) {
		Set<String> scopeSet = new HashSet<>();
		scopeSet.add("bot");
		for (String scope : scopes)
			scopeSet.add(scope.toLowerCase(Locale.ROOT));
		this.scopes = String.join("%20", scopeSet);
		return this;
	}

	@NonNull
	@Override
	public String getInviteUrl(@Nullable Permission... permissions) {
		return "https://discord.com/oauth2/authorize?scope=" + scopes
				+ "&client_id=" + getSelfUser().getId()
				+ "&permissions=" + Permission.getRaw(permissions);
	}

	@NonNull
	@Override
	public String getInviteUrl(@Nullable Collection<Permission> permissions) {
		return getInviteUrl(permissions == null ? null : permissions.toArray(new Permission[0]));
	}

	@Nullable
	@Override
	public ShardManager getShardManager() {
		return null;
	}

	@NonNull
	@Override
	public RestAction<Webhook> retrieveWebhookById(@NonNull String webhookId) {
		return new DummyRestAction<>(this);
	}
}
