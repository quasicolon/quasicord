package dev.qixils.quasicolon.variables.parsers;

import dev.qixils.quasicolon.QuasicolonBot;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class ListParser<R> extends VariableParser<List<R>> {
    private static final char JOINER = '\u2603'; // snowman! :)
    private static final String JOINER_STR = String.valueOf(JOINER);
    private static final String ESC_JOINER = "\\" + JOINER;
    private static final Pattern SPLITTER = Pattern.compile("(?<!\\\\)" + JOINER);

    private final VariableParser<R> parser;
    private final String separator;

    public ListParser(@NotNull QuasicolonBot bot, @NotNull VariableParser<R> parser, char separator) {
        super(bot);
        this.parser = Objects.requireNonNull(parser, "parser");
        this.separator = String.valueOf(separator);
    }

    @Override
    public @NotNull List<@Nullable R> decode(@NotNull String value) {
        List<R> items = new ArrayList<>();
        for (String element : SPLITTER.split(value))
            items.add(parser.decode(element.replace(ESC_JOINER, JOINER_STR)));
        return items;
    }

    @Override
    public @NotNull String encode(@NotNull List<R> listR) {
        StringBuilder output = new StringBuilder();
        Iterator<R> items = listR.iterator();
        while (items.hasNext()) {
            output.append(parser.encode(items.next()).replace(JOINER_STR, ESC_JOINER));
            if (items.hasNext())
                output.append(JOINER);
        }
        return output.toString();
    }

    @Override
    public @NotNull CompletableFuture<List<@NotNull R>> parseText(@Nullable Message context, @NotNull String humanText) {
        List<R> items = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String text : humanText.split(separator)) {
            futures.add(parser.parseText(context, text).thenAccept(item -> {
                if (item != null)
                    items.add(item);
            }));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenApply($ -> items);
    }
}
