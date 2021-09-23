package dev.qixils.quasicolon;

import dev.qixils.quasicolon.variables.AbstractVariables;
import lombok.SneakyThrows;

public class MockBot extends QuasicolonBot {
    @SneakyThrows
    public MockBot() {
        super(new AbstractVariables(){});
    }
}
