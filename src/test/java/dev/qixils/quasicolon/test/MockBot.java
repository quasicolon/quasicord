package dev.qixils.quasicolon.test;

import dev.qixils.quasicolon.QuasicolonBot;
import dev.qixils.quasicolon.variables.AbstractVariables;
import net.dv8tion.jda.api.JDA;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import javax.security.auth.login.LoginException;

public class MockBot extends QuasicolonBot {
    public MockBot() throws LoginException, ConfigurateException, InterruptedException {
        super(MockBot.class, new AbstractVariables(){});
    }

    @Override
    protected @NotNull JDA initJDA() {
        return new DummyJDA();
    }

    public static @NonNull MockBot INSTANCE;
    static {
        try {
            INSTANCE = new MockBot();
        } catch (Throwable exc) {
            throw new RuntimeException(exc);
        }
    }
}
