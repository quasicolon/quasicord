package dev.qixils.quasicolon.test;

import dev.qixils.quasicolon.test.actions.DummyRestAction;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final OkHttpClient client = new OkHttpClient();
    private final DirectAudioController audioController = new DirectAudioController() {
        @NotNull
        @Override
        public JDA getJDA() {
            return DummyJDA.this;
        }

        @Override
        public void connect(@NotNull VoiceChannel channel) {}

        @Override
        public void disconnect(@NotNull Guild guild) {}

        @Override
        public void reconnect(@NotNull VoiceChannel channel) {}
    };
    private final List<Object> eventListeners = new ArrayList<>();

    // IDK why I chose to make a real impl for this
    private final Presence presence = new Presence() {
        private OnlineStatus onlineStatus = OnlineStatus.INVISIBLE;
        private Activity activity = null;
        private boolean idle = false;

        @NotNull
        @Override
        public JDA getJDA() {
            return DummyJDA.this;
        }

        @NotNull
        @Override
        public OnlineStatus getStatus() {
            return onlineStatus;
        }

        @Nullable
        @Override
        public Activity getActivity() {
            return activity;
        }

        @Override
        public boolean isIdle() {
            return idle;
        }

        @Override
        public void setStatus(@Nullable OnlineStatus status) {
            this.onlineStatus = status;
        }

        @Override
        public void setActivity(@Nullable Activity activity) {
            this.activity = activity;
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

    private IEventManager eventManager = new AnnotatedEventManager();
    private boolean autoReconnect = true;

    private final SelfUser user = new DummySelfUser(this, "qixils", "0493", 140564059417346049L);
    private final ApplicationInfo applicationInfo = new DummyApplicationInfo(this, user, user);
    private String scopes = "bot";

    @NotNull
    @Override
    public Status getStatus() {
        return Status.CONNECTED;
    }

    @NotNull
    @Override
    public EnumSet<GatewayIntent> getGatewayIntents() {
        return EnumSet.noneOf(GatewayIntent.class);
    }

    @NotNull
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

    @NotNull
    @Override
    public JDA awaitStatus(@NotNull JDA.Status status, Status @NotNull... failOn) {
        return this;
    }

    @Override
    public int cancelRequests() {
        return 0;
    }

    @NotNull
    @Override
    public ScheduledExecutorService getRateLimitPool() {
        return executor;
    }

    @NotNull
    @Override
    public ScheduledExecutorService getGatewayPool() {
        return executor;
    }

    @NotNull
    @Override
    public ExecutorService getCallbackPool() {
        return executor;
    }

    @NotNull
    @Override
    public OkHttpClient getHttpClient() {
        return client;
    }

    @NotNull
    @Override
    public DirectAudioController getDirectAudioController() {
        return audioController;
    }

    @Override
    public void setEventManager(@Nullable IEventManager manager) {
        if (manager != null)
            this.eventManager = manager;
    }

    @Override
    public void addEventListener(Object @NotNull... listeners) {
        this.eventListeners.addAll(Arrays.asList(listeners));
    }

    @Override
    public void removeEventListener(Object @NotNull... listeners) {
        this.eventListeners.removeAll(Arrays.asList(listeners));
    }

    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return Collections.unmodifiableList(this.eventListeners);
    }

    @NotNull
    @Override
    public RestAction<List<Command>> retrieveCommands() {
        return new DummyRestAction<>(this, Collections.emptyList());
    }

    @NotNull
    @Override
    public RestAction<Command> retrieveCommandById(@NotNull String id) {
        return new DummyRestAction<>(this);
    }

    @NotNull
    @Override
    public CommandCreateAction upsertCommand(@NotNull CommandData command) {
        return null;
    }

    @NotNull
    @Override
    public CommandListUpdateAction updateCommands() {
        return null;
    }

    @NotNull
    @Override
    public CommandEditAction editCommandById(@NotNull String id) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> deleteCommandById(@NotNull String commandId) {
        return new DummyRestAction<>(this);
    }

    @NotNull
    @Override
    public GuildAction createGuild(@NotNull String name) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> createGuildFromTemplate(@NotNull String code, @NotNull String name, @Nullable Icon icon) {
        return new DummyRestAction<>(this);
    }

    @NotNull
    @Override
    public CacheView<AudioManager> getAudioManagerCache() {
        return EmptyCacheView.emptyCacheView();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<User> getUserCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public List<Guild> getMutualGuilds(User @NotNull... users) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<Guild> getMutualGuilds(@NotNull Collection<User> users) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public RestAction<User> retrieveUserById(long id, boolean update) {
        return new DummyRestAction<>(this);
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Guild> getGuildCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public Set<String> getUnavailableGuilds() {
        return Collections.emptySet();
    }

    @Override
    public boolean isUnavailable(long guildId) {
        return false;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Role> getRoleCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Category> getCategoryCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<StoreChannel> getStoreChannelCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<TextChannel> getTextChannelCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<PrivateChannel> getPrivateChannelCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public RestAction<PrivateChannel> openPrivateChannelById(long userId) {
        return new DummyRestAction<>(this);
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Emote> getEmoteCache() {
        return EmptySnowflakeCacheView.emptySnowflakeCacheView();
    }

    @NotNull
    @Override
    public IEventManager getEventManager() {
        return eventManager;
    }

    @NotNull
    @Override
    public SelfUser getSelfUser() {
        return user;
    }

    @NotNull
    @Override
    public Presence getPresence() {
        return presence;
    }

    @NotNull
    @Override
    public ShardInfo getShardInfo() {
        return ShardInfo.SINGLE;
    }

    @NotNull
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
    public void setAutoReconnect(boolean reconnect) {
        this.autoReconnect = reconnect;
    }

    @Override
    public void setRequestTimeoutRetry(boolean retryOnTimeout) {

    }

    @Override
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    @Override
    public boolean isBulkDeleteSplittingEnabled() {
        return true;
    }

    @Override
    public void shutdown() {}

    @Override
    public void shutdownNow() {
        cancelRequests();
    }

    @NotNull
    @Override
    public AccountType getAccountType() {
        return AccountType.BOT;
    }

    @NotNull
    @Override
    public RestAction<ApplicationInfo> retrieveApplicationInfo() {
        return new DummyRestAction<>(this, applicationInfo);
    }

    @NotNull
    @Override
    public JDA setRequiredScopes(@NotNull Collection<String> scopes) {
        Set<String> scopeSet = new HashSet<>();
        scopeSet.add("bot");
        for (String scope : scopes)
            scopeSet.add(scope.toLowerCase(Locale.ENGLISH));
        this.scopes = String.join("%20", scopeSet);
        return this;
    }

    @NotNull
    @Override
    public String getInviteUrl(@Nullable Permission... permissions) {
        return "https://discord.com/oauth2/authorize?scope=" + scopes
                + "&client_id=" + getSelfUser().getId()
                + "&permissions=" + Permission.getRaw(permissions);
    }

    @NotNull
    @Override
    public String getInviteUrl(@Nullable Collection<Permission> permissions) {
        return getInviteUrl(permissions == null ? null : permissions.toArray(new Permission[0]));
    }

    @Nullable
    @Override
    public ShardManager getShardManager() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Webhook> retrieveWebhookById(@NotNull String webhookId) {
        return new DummyRestAction<>(this);
    }
}
