/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.test;

import dev.qixils.quasicord.Quasicord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;

public class MockBot extends Quasicord {
    public MockBot() throws LoginException, IOException, InterruptedException {
        super("mockbot", Locale.ENGLISH, Paths.get(".").toAbsolutePath(), null, null);
    }

    @Override
    protected @NotNull JDA initJDA(Activity activity) {
        return new DummyJDA();
    }

    public static final @NonNull MockBot INSTANCE;
    static {
        try {
            INSTANCE = new MockBot();
        } catch (Throwable exc) {
            throw new RuntimeException(exc);
        }
    }
}
