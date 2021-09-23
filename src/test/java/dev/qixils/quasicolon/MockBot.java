package dev.qixils.quasicolon;

import dev.qixils.quasicolon.variables.AbstractVariables;
import net.dv8tion.jda.api.JDA;
import org.spongepowered.configurate.ConfigurateException;

import javax.security.auth.login.LoginException;

public class MockBot extends QuasicolonBot {
    public MockBot() throws LoginException, ConfigurateException {
        super(new AbstractVariables(){});
    }

    @SuppressWarnings({"ConstantConditions", "NullableProblems"})
    @Override
    protected JDA initJDA() {
        return null;
    }

    public static MockBot INSTANCE;
    static {
        try {
            INSTANCE = new MockBot();
        } catch (Throwable exc) {
            throw new RuntimeException(exc);
        }
    }
}
