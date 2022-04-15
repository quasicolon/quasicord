package dev.qixils.quasicolon.cogs.impl.autosend;

import cloud.commandframework.annotations.MethodCommandExecutionHandler;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.jda.JDACommandSender;
import dev.qixils.quasicolon.Quasicolon;
import dev.qixils.quasicolon.locale.Context;
import dev.qixils.quasicolon.locale.LocaleProvider;
import dev.qixils.quasicolon.text.Text;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Handles methods annotated with {@link CloudAutoSend}.
 */
public class CloudAutoSendHandler extends MethodCommandExecutionHandler<JDACommandSender> {

	/**
	 * Determines whether a method should be handled by this handler.
	 */
	public static final Predicate<Method> IS_AUTO_SEND = method -> method.isAnnotationPresent(CloudAutoSend.class);

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudAutoSendHandler.class);
	private final @NonNull LocaleProvider localeProvider;
	private final @NonNull CloudAutoSend autoSend;

	private CloudAutoSendHandler(@NonNull LocaleProvider localeProvider, @NonNull CommandMethodContext<JDACommandSender> context) throws Exception {
		super(context);
		this.localeProvider = localeProvider;
		this.autoSend = context.method().getAnnotation(CloudAutoSend.class);
	}

	/**
	 * Creates an instance of the handler.
	 *
	 * @param localeProvider locale provider
	 * @param context        command context
	 * @return new handler instance
	 */
	@Contract("_, _ -> new")
	public static @NonNull CloudAutoSendHandler of(@NonNull LocaleProvider localeProvider, @NonNull CommandMethodContext<JDACommandSender> context) {
		try {
			return new CloudAutoSendHandler(localeProvider, context);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create CloudAutoSendHandler", e);
		}
	}

	/**
	 * Creates an instance of the handler.
	 *
	 * @param library library instance
	 * @param context command context
	 * @return new handler instance
	 */
	@Contract("_, _ -> new")
	public static @NonNull CloudAutoSendHandler of(@NonNull Quasicolon library, @NonNull CommandMethodContext<JDACommandSender> context) {
		return of(library.getLocaleProvider(), context);
	}

	@Override
	public void execute(@NonNull CommandContext<JDACommandSender> commandContext) {
		try {
			// get execution result
			Object result = methodHandle().invokeWithArguments(
					createParameterValues(
							commandContext,
							commandContext.flags(),
							parameters()
					)
			);
			if (result == null)
				throw new NoResponseException();

			// get the invoking message
			JDACommandSender sender = commandContext.getSender();
			Message message = sender.getEvent().map(MessageReceivedEvent::getMessage).orElse(null);
			if (message == null) {
				LOGGER.debug("Message is null; not sending response to {} ({}) in #{}", sender.getUser().getAsTag(), sender.getUser().getId(), sender.getChannel().getName());
				return;
			}

			// send response
			handleResult(result, message);
		} catch (final Error e) {
			throw e;
		} catch (final Throwable throwable) {
			throw new CommandExecutionException(throwable, commandContext);
		}
	}

	private void handleResult(@NonNull Object result, @NonNull Message message) {
		// TODO: create a custom wrapper object which supports embeds and attachments
		if (result instanceof Text text)
			text.asString(Context.fromMessage(message), localeProvider).subscribe(createMessageSender(message));
		else if (result instanceof Mono<?> mono)
			mono.subscribe(obj -> handleResult(obj, message));
		else if (result instanceof CompletableFuture<?> future)
			future.thenAccept(obj -> handleResult(obj, message));
		else if (result instanceof MessageAction action)
			handleMessageAction(action);
		else
			throw new IllegalArgumentException("Unsupported response type: " + result.getClass().getName());
	}

	@SuppressWarnings("ResultOfMethodCallIgnored") // JDA misuses this annotation
	@NonNull
	private Consumer<String> createMessageSender(@NonNull Message message) {
		return content -> {
			// create action
			MessageAction action = message.getChannel().sendMessage(content);
			// get send type
			CloudSendType sendType = autoSend.value();
			// add reply data if applicable
			if (sendType.isReply())
				action.reference(message);
			// set whether to ping the replied user
			action.mentionRepliedUser(sendType.isPing());
			// send the message
			handleMessageAction(action);
		};
	}

	private void handleMessageAction(@NonNull MessageAction action) {
		action.queue(null, throwable -> LOGGER.warn("Failed to send message", throwable));
	}
}
