/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.test;

import dev.qixils.quasicord.test.actions.DummyRestAction;
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

    public DummySelfUser(@NotNull JDA jda, @NotNull String username, @NotNull String globalName, long id) {
        super(jda, username, globalName, id, true, false);
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
        public DummyAccountManager setCheck(BooleanSupplier checks) {
            return (DummyAccountManager) super.setCheck(checks);
        }

        @NotNull
        @Override
        public DummyAccountManager timeout(long timeout, @NotNull TimeUnit unit) {
            return (DummyAccountManager) super.timeout(timeout, unit);
        }

		@NotNull
        @Override
        public DummyAccountManager deadline(long timestamp) {
            return (DummyAccountManager) super.deadline(timestamp);
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
