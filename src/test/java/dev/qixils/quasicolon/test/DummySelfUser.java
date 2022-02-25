package dev.qixils.quasicolon.test;

import dev.qixils.quasicolon.test.actions.DummyRestAction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class DummySelfUser extends DummyUser implements SelfUser {
    public DummySelfUser(@NotNull JDA jda, long id) {
        super(jda, id, true, false);
    }

    public DummySelfUser(@NotNull JDA jda, @NotNull String username, @NotNull String discriminator, long id) {
        super(jda, username, discriminator, id, true, false);
    }

    @Override
    public long getApplicationIdLong() {
        return 0;
    }

    @Override
    public boolean isVerified() {
        return false;
    }

    @Override
    public boolean isMfaEnabled() {
        return false;
    }

    @Override
    public long getAllowedFileSize() {
        return Message.MAX_FILE_SIZE;
    }

    @NotNull
    @Override
    public AccountManager getManager() {
        return new DummyAccountManager(this);
    }

    static class DummyAccountManager extends DummyRestAction<Void> implements AccountManager {
        private final SelfUser user;
        DummyAccountManager(SelfUser user) {
            super(user.getJDA());
            this.user = user;
        }

        @NotNull
        @Override
        public SelfUser getSelfUser() {
            return user;
        }

        @NotNull
        @Override
        public AuditableRestAction<Void> reason(@Nullable String reason) {
            return this;
        }

        @NotNull
        @Override
        public AccountManager setCheck(BooleanSupplier checks) {
            return this;
        }

        @NotNull
        @Override
        public AccountManager timeout(long timeout, @NotNull TimeUnit unit) {
            return this;
        }

        @NotNull
        @Override
        public AccountManager deadline(long timestamp) {
            return this;
        }

        @NotNull
        @Override
        public AccountManager reset(long fields) {
            return this;
        }

        @NotNull
        @Override
        public AccountManager reset(long... fields) {
            return this;
        }

        @NotNull
        @Override
        public AccountManager reset() {
            return this;
        }

        @NotNull
        @Override
        public AccountManager setName(@NotNull String name) {
            return this;
        }

        @NotNull
        @Override
        public AccountManager setAvatar(@Nullable Icon avatar) {
            return this;
        }
    }
}