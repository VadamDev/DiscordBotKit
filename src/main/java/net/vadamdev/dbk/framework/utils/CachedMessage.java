package net.vadamdev.dbk.framework.utils;

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
public record CachedMessage(long messageId, long channelId, long authorId, OffsetDateTime creationDate, JDA jda) {
    public CachedMessage(Message message) {
        this(message.getIdLong(), message.getChannelIdLong(), message.getAuthor().getIdLong(), message.getTimeCreated(), message.getJDA());
    }

    public RestAction<Message> retrieveMessage() {
        final MessageChannel channel = getChannel();
        Checks.notNull(channel, "Channel no longer exists!");

        return channel.retrieveMessageById(messageId);
    }

    public RestAction<Void> delete() {
        final MessageChannel channel = getChannel();
        if(channel == null)
            return new CompletedRestAction<>(jda, null);

        return channel.deleteMessageById(messageId);
    }

    @Nullable
    public MessageChannel getChannel() {
        return jda.getChannelById(MessageChannel.class, channelId);
    }

    public CompletableFuture<Boolean> exists() {
        final MessageChannel channel = getChannel();
        if(channel == null)
            return CompletableFuture.completedFuture(false);

        return channel.retrieveMessageById(messageId).submit()
                .thenApply(message -> true)
                .exceptionally(throwable -> false);
    }

    public void runIfExists(Consumer<Message> action, Runnable onError) {
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

                    action.accept(message);
                });
    }

    public void runIfExists(Consumer<Message> action) {
        runIfExists(action, () -> {});
    }
}
