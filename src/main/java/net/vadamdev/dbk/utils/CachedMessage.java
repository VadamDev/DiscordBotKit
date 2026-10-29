package net.vadamdev.dbk.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 17/03/2025
 */
public record CachedMessage(String messageId, String channelId, String authorId, OffsetDateTime creationDate, JDA jda) {
    public CachedMessage(Message message) {
        this(message.getId(), message.getChannelId(), message.getAuthor().getId(), message.getTimeCreated(), message.getJDA());
    }

    /**
     * Retrieves the message from discord
     *
     * @throws IllegalArgumentException if the channel no longer exists
     * @return a RestAction that will retrieve the message from discord
     */
    public RestAction<Message> retrieveMessage() {
        final MessageChannel channel = getChannel();
        Checks.notNull(channel, "Channel no longer exists!");

        return channel.retrieveMessageById(messageId);
    }

    /**
     * Deletes the message if it exists, otherwise does nothing
     *
     * @return a RestAction that will delete the message if it exists, otherwise does nothing
     */
    public RestAction<Void> delete() {
        final MessageChannel channel = getChannel();
        if(channel == null)
            return new CompletedRestAction<>(jda, null);

        return channel.deleteMessageById(messageId);
    }

    /**
     * Gets the channel of the message, or null if the channel no longer exists
     *
     * @return the channel of the message, or null if the channel no longer exists
     */
    @Nullable
    public MessageChannel getChannel() {
        return jda.getChannelById(MessageChannel.class, channelId);
    }

    /**
     * Checks if the message still exists in the channel, or if it has been deleted
     *
     * @return a CompletableFuture that will complete with true if the message exists, or false if it has been deleted
     */
    public CompletableFuture<Boolean> exists() {
        final MessageChannel channel = getChannel();
        if(channel == null)
            return CompletableFuture.completedFuture(false);

        return channel.retrieveMessageById(messageId).submit()
                .thenApply(message -> true)
                .exceptionally(throwable -> false);
    }

    /**
     * Runs the specified action if the message exists, otherwise runs the error action
     *
     * @param onSuccess the action to run if the message exists
     * @param onError the action to run if the message does not exist
     */
    public void runIfExists(Consumer<Message> onSuccess, Runnable onError) {
        final MessageChannel channel = getChannel();
        if(channel == null)
            return;

        channel.retrieveMessageById(messageId).submit()
                .exceptionally(throwable -> {
                    onError.run();
                    return null;
                })
                .thenAccept(message -> {
                    if(message == null)
                        return;

                    onSuccess.accept(message);
                });
    }

    /**
     * Runs the specified action if the message exists
     *
     * @param onSuccess the action to run if the message exists
     */
    public void runIfExists(Consumer<Message> onSuccess) {
        runIfExists(onSuccess, () -> {});
    }
}
