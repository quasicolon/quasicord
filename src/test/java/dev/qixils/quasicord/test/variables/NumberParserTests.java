/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.qixils.quasicord.test.variables;

import dev.qixils.quasicord.test.MockBot;
import dev.qixils.quasicord.variables.parsers.numbers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigInteger;

@SuppressWarnings("ResultOfMethodCallIgnored")
class NumberParserTests {
    private static final ByteParser BYTE_PARSER = new ByteParser(MockBot.INSTANCE);
    private static final ShortParser SHORT_PARSER = new ShortParser(MockBot.INSTANCE);
    private static final IntegerParser INTEGER_PARSER = new IntegerParser(MockBot.INSTANCE);
    private static final LongParser LONG_PARSER = new LongParser(MockBot.INSTANCE);
    private static final BigIntegerParser BIG_INT_PARSER = new BigIntegerParser(MockBot.INSTANCE);
    private static final FloatParser FLOAT_PARSER = new FloatParser(MockBot.INSTANCE);
    private static final DoubleParser DOUBLE_PARSER = new DoubleParser(MockBot.INSTANCE);

    @ParameterizedTest(name="ByteParser#parseText(\"{1}\") -> {0}")
    @CsvSource({
            "1, +1",
            "-115, -115",
            ", this is not a number", //  first value is a null value
            ", 128"
    })
    void byteParseTest(Byte value, String text) {
        BYTE_PARSER.parseText(null, text).thenAccept(aByte -> Assertions.assertEquals(aByte, value)).join();
        Assertions.assertEquals(BYTE_PARSER.parseTextAsMono(null, text).block(), value);
    }

    // exception tests are slightly redundant but good to have just in case
    @ParameterizedTest(name="ByteParser#decode(\"{0}\") throws")
    @CsvSource({"this is not a number", "128"})
    void byteExceptionTest(String text) {
        Assertions.assertThrows(NumberFormatException.class, () -> BYTE_PARSER.decode(text));
    }

    @ParameterizedTest(name="ByteParser#encode({0}) -> \"{1}\"")
    @CsvSource({"127, 127", "-128, -128"})
    void byteEncodeTest(Byte value, String text) {
        Assertions.assertEquals(BYTE_PARSER.encode(value), text);
    }

    @ParameterizedTest(name="ShortParser#parseText(\"{1}\") -> {0}")
    @CsvSource({
            "1, +1",
            "-115, -115",
            ", this is not a number",
            ", 32768"
    })
    void shortParseTest(Short value, String text) {
        SHORT_PARSER.parseText(null, text).thenAccept(aByte -> Assertions.assertEquals(aByte, value)).join();
        Assertions.assertEquals(SHORT_PARSER.parseTextAsMono(null, text).block(), value);
    }

    @ParameterizedTest(name="ShortParser#decode(\"{0}\") throws")
    @CsvSource({"this is not a number", "32768"})
    void shortExceptionTest(String text) {
        Assertions.assertThrows(NumberFormatException.class, () -> SHORT_PARSER.decode(text));
    }

    @ParameterizedTest(name="ShortParser#encode({0}) -> \"{1}\"")
    @CsvSource({"32767, 32767", "-32768, -32768"})
    void shortEncodeTest(Short value, String text) {
        Assertions.assertEquals(SHORT_PARSER.encode(value), text);
    }

    @ParameterizedTest(name="IntegerParser#parseText(\"{1}\") -> {0}")
    @CsvSource({
            "1, +1",
            "-115, -115",
            ", this is not a number",
            ", 2147483648"
    })
    void integerParseTest(Integer value, String text) {
        INTEGER_PARSER.parseText(null, text).thenAccept(aByte -> Assertions.assertEquals(aByte, value)).join();
        Assertions.assertEquals(INTEGER_PARSER.parseTextAsMono(null, text).block(), value);
    }

    @ParameterizedTest(name="IntegerParser#decode(\"{0}\") throws")
    @CsvSource({"this is not a number", "2147483648"})
    void integerExceptionTest(String text) {
        Assertions.assertThrows(NumberFormatException.class, () -> INTEGER_PARSER.decode(text));
    }

    @ParameterizedTest(name="IntegerParser#encode({0}) -> \"{1}\"")
    @CsvSource({"2147483647, 2147483647", "-2147483648, -2147483648"})
    void integerEncodeTest(Integer value, String text) {
        Assertions.assertEquals(INTEGER_PARSER.encode(value), text);
    }

    @ParameterizedTest(name="LongParser#parseText(\"{1}\") -> {0}")
    @CsvSource({
            "1, +1",
            "-115, -115",
            ", this is not a number",
            ", 9223372036854775808"
    })
    void longParseTest(Long value, String text) {
        LONG_PARSER.parseText(null, text).thenAccept(aByte -> Assertions.assertEquals(aByte, value)).join();
        Assertions.assertEquals(LONG_PARSER.parseTextAsMono(null, text).block(), value);
    }

    @ParameterizedTest(name="LongParser#decode(\"{0}\") throws")
    @CsvSource({"this is not a number", "9223372036854775808"})
    void longExceptionTest(String text) {
        Assertions.assertThrows(NumberFormatException.class, () -> LONG_PARSER.decode(text));
    }

    @ParameterizedTest(name="LongParser#encode({0}) -> \"{1}\"")
    @CsvSource({"9223372036854775807, 9223372036854775807", "-9223372036854775808, -9223372036854775808"})
    void longEncodeTest(Long value, String text) {
        Assertions.assertEquals(LONG_PARSER.encode(value), text);
    }

    @ParameterizedTest(name="BigIntegerParser#parseText(\"{1}\") -> {0}")
    @CsvSource({
            "1, +1",
            "-115, -115",
            ", this is not a number",
            "9223372036854775808, 9223372036854775808"
    })
    void bigIntParseTest(BigInteger value, String text) {
        BIG_INT_PARSER.parseText(null, text).thenAccept(aByte -> Assertions.assertEquals(aByte, value)).join();
        Assertions.assertEquals(BIG_INT_PARSER.parseTextAsMono(null, text).block(), value);
    }

    @ParameterizedTest(name="BigIntegerParser#decode(\"{0}\") throws")
    @CsvSource({"this is not a number"})
    void bigIntExceptionTest(String text) {
        Assertions.assertThrows(NumberFormatException.class, () -> BIG_INT_PARSER.decode(text));
    }

    @ParameterizedTest(name="BigIntegerParser#encode({0}) -> \"{1}\"")
    @CsvSource({"9223372036854775808, 9223372036854775808", "-9223372036854775809, -9223372036854775809"})
    void bigIntEncodeTest(BigInteger value, String text) {
        Assertions.assertEquals(BIG_INT_PARSER.encode(value), text);
    }

    @ParameterizedTest(name="FloatParser#parseText(\"{1}\") -> {0}")
    @CsvSource({
            "1.0, +1",
            "-115.0, -115",
            "-0.0, -0",
            "-0.1, -.1",
            ", this is not a number"
    })
    void floatParseTest(Float value, String text) {
        FLOAT_PARSER.parseText(null, text).thenAccept(aByte -> Assertions.assertEquals(aByte, value)).join();
        Assertions.assertEquals(FLOAT_PARSER.parseTextAsMono(null, text).block(), value);
    }

    @ParameterizedTest(name="FloatParser#decode(\"{0}\") throws")
    @CsvSource({"this is not a number"})
    void floatExceptionTest(String text) {
        Assertions.assertThrows(NumberFormatException.class, () -> FLOAT_PARSER.decode(text));
    }

    @ParameterizedTest(name="FloatParser#encode({0}) -> \"{1}\"")
    @CsvSource({"500, 500.0", "-.4, -0.4"})
    void floatEncodeTest(Float value, String text) {
        Assertions.assertEquals(FLOAT_PARSER.encode(value), text);
    }

    @ParameterizedTest(name="DoubleParser#parseText(\"{1}\") -> {0}")
    @CsvSource({
            "1.0, +1",
            "-115.0, -115",
            "-0.0, -0",
            "-0.1, -.1",
            ", this is not a number"
    })
    void doubleParseTest(Double value, String text) {
        DOUBLE_PARSER.parseText(null, text).thenAccept(aByte -> Assertions.assertEquals(aByte, value)).join();
        Assertions.assertEquals(DOUBLE_PARSER.parseTextAsMono(null, text).block(), value);
    }

    @ParameterizedTest(name="DoubleParser#decode(\"{0}\") throws")
    @CsvSource({"this is not a number"})
    void doubleExceptionTest(String text) {
        Assertions.assertThrows(NumberFormatException.class, () -> DOUBLE_PARSER.decode(text));
    }

    @ParameterizedTest(name="DoubletParser#encode({0}) -> \"{1}\"")
    @CsvSource({"500, 500.0", "-.4, -0.4"})
    void doubleEncodeTest(Double value, String text) {
        Assertions.assertEquals(DOUBLE_PARSER.encode(value), text);
    }
}
