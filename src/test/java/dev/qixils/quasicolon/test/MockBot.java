/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicolon.test;

import dev.qixils.quasicolon.Quasicolon;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import javax.security.auth.login.LoginException;
import java.nio.file.Paths;
import java.util.Locale;

public class MockBot extends Quasicolon {
    public MockBot() throws LoginException, ConfigurateException, InterruptedException {
        super("mockbot", Locale.ROOT, Paths.get(".").toAbsolutePath(), null, null);
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
