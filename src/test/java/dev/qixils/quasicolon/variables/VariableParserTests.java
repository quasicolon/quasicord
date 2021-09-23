package dev.qixils.quasicolon.variables;

import dev.qixils.quasicolon.MockBot;
import dev.qixils.quasicolon.variables.parsers.numbers.ByteParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VariableParserTests {
    @Test
    public void byteParserTest() {
        ByteParser parser = new ByteParser(MockBot.INSTANCE);
        Assertions.assertEquals((byte) -115, parser.fromDatabase("-115"));
        Assertions.assertEquals("-115", parser.toDatabase((byte) -115));
        parser.parseText(null, "NaN").thenAccept(Assertions::assertNull);
    }
}
