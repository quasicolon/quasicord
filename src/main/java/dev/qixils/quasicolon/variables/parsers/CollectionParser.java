package dev.qixils.quasicolon.variables.parsers;

import dev.qixils.quasicolon.QuasicolonBot;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * A parser which can collect any (positive) number of variables using a sub-parser and a character
 * to split entries.
 *
 * @param <C> type of collection to store parsed objects in
 * @param <R> type of objects to parse
 */
public class CollectionParser<C extends Collection<R>, R> extends VariableParser<C> {
	protected static final char JOINER = '\u2603'; // snowman! :)
	protected static final String JOINER_STR = String.valueOf(JOINER);
	protected static final String ESC_JOINER = "\\" + JOINER;
	protected static final Pattern SPLITTER = Pattern.compile("(?<!\\\\)" + JOINER);

	protected final VariableParser<R> parser;
	protected final String separator;
	protected final Supplier<C> constructor;

	/**
	 * Creates a new collection parser.
	 *
	 * @param bot         the owning bot
	 * @param parser      the parser used to parse the collected items
	 * @param separator   the character that a user's input should use to distinguish between entries
	 * @param constructor the function used to create a new mutable collection of the specified type
	 */
	public CollectionParser(@NotNull QuasicolonBot bot, @NotNull VariableParser<R> parser, char separator, @NotNull Supplier<@NotNull C> constructor) {
		super(bot);
		this.parser = Objects.requireNonNull(parser, "parser");
		this.separator = String.valueOf(separator);
		this.constructor = constructor;
	}

	@Override
	public @NotNull C decode(@NotNull String value) {
		C items = constructor.get();
		for (String element : SPLITTER.split(value))
			items.add(parser.decode(element.replace(ESC_JOINER, JOINER_STR)));
		return items;
	}

	@Override
	public @NotNull String encode(@NotNull C collection) {
		StringBuilder output = new StringBuilder();
		Iterator<R> items = collection.iterator();
		while (items.hasNext()) {
			output.append(parser.encode(items.next()).replace(JOINER_STR, ESC_JOINER));
			if (items.hasNext())
				output.append(JOINER);
		}
		return output.toString();
	}

	@Override
	public @NotNull CompletableFuture<C> parseText(@Nullable Message context, @NotNull String humanText) {
		C items = constructor.get();
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
