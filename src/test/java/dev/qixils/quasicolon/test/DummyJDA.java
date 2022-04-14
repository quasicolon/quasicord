package dev.qixils.quasicolon.test;

import dev.qixils.quasicolon.test.actions.DummyCommandListUpdateAction;
import dev.qixils.quasicolon.test.actions.DummyRestAction;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.NewsChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import okhttp3.OkHttpClient;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
	private final @NonNull SelfUser user = new DummySelfUser(this, "qixils", "0493", 140564059417346049L);
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
	public RestAction<User> retrieveUserById(long id, boolean update) {
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
	public SnowflakeCacheView<PrivateChannel> getPrivateChannelCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
	}

	@NonNull
	@Override
	public RestAction<PrivateChannel> openPrivateChannelById(long userId) {
		return new DummyRestAction<>(this);
	}

	@NonNull
	@Override
	public SnowflakeCacheView<Emote> getEmoteCache() {
		return EmptySnowflakeCacheView.emptySnowflakeCacheView();
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
	public AccountType getAccountType() {
		return AccountType.BOT;
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
