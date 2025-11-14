package net.vadamdev.dbk.framework.menu;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.RestAction;
import net.vadamdev.dbk.framework.interactive.api.Invalidatable;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author VadamDev
 * @since 17/03/2025
 */
public abstract class AbstractMenu implements Invalidatable {
    private ScheduledFuture<?> timeoutTask;
    protected JDA jda;

    protected AbstractMenu(long timeout, @Nullable TimeUnit unit, @Nullable ScheduledExecutorService scheduler) {
        if(unit != null && timeout > 0 && scheduler != null)
            timeoutTask = scheduler.schedule(() -> invalidate(jda), timeout, unit);
    }

    public abstract RestAction<Message> display(Message message);
    public abstract RestAction<Message> display(MessageChannel channel, @Nullable String messageId);
    public RestAction<Message> display(MessageChannel channel) { return display(channel, null); }

    public abstract RestAction<Message> display(IReplyCallback callback, boolean ephemeral);
    public RestAction<Message> display(IReplyCallback callback) { return display(callback, false); }

    public abstract RestAction<Message> display(InteractionHook hook, boolean edit);
    public RestAction<Message> display(InteractionHook hook) { return display(hook, false); }

    public void cancelTimeout() {
        if(timeoutTask == null)
            return;

        if(!timeoutTask.isCancelled() && !timeoutTask.isDone())
            timeoutTask.cancel(false);

        timeoutTask = null;
    }

    @Override
    public void invalidate(JDA jda) {
        cancelTimeout();
    }

    public abstract static class Builder<T extends AbstractMenu, B extends Builder<T, B>> {
        protected long timeout;
        protected TimeUnit unit;
        protected ScheduledExecutorService scheduler;

        protected Builder() {
            this.timeout = -1;
            this.unit = null;
        }

        public B setTimeout(long timeout, TimeUnit unit, ScheduledExecutorService scheduler) {
            this.timeout = timeout;
            this.unit = unit;
            this.scheduler = scheduler;

            return (B) this;
        }

        public B noTimeout() {
            this.timeout = -1;
            this.unit = null;
            this.scheduler = null;

            return (B) this;
        }

        public abstract T build();
    }
}
