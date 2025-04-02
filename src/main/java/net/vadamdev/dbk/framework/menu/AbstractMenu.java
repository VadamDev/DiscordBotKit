package net.vadamdev.dbk.framework.menu;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.RestAction;
import net.vadamdev.dbk.framework.DBKFramework;
import net.vadamdev.dbk.framework.interactive.api.Invalidatable;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author VadamDev
 * @since 17/03/2025
 */
public abstract class AbstractMenu implements Invalidatable {
    private ScheduledFuture<?> timeoutTask;
    protected JDA jda;

    protected AbstractMenu(long timeout, @Nullable TimeUnit unit) {
        if(unit != null && timeout > 0)
            timeoutTask = DBKFramework.getScheduledExecutorMonoThread().schedule(() -> invalidate(jda), timeout, unit);
    }

    public abstract RestAction<Message> display(MessageChannel channel);
    public abstract RestAction<Message> display(Message message);
    public RestAction<Message> display(IReplyCallback callback) { return display(callback, false); }
    public abstract RestAction<Message> display(IReplyCallback callback, boolean ephemeral);

    @Override
    public void invalidate(JDA jda) {
        if(timeoutTask != null && !timeoutTask.isCancelled())
            timeoutTask.cancel(false);
    }

    public abstract static class Builder<T extends AbstractMenu, B extends Builder<T, B>> {
        protected long timeout;
        protected TimeUnit unit;

        protected Builder() {
            this.timeout = -1;
            this.unit = null;
        }

        public B setTimeout(long timeout, TimeUnit unit) {
            this.timeout = timeout;
            this.unit = unit;

            return (B) this;
        }

        public B noTimeout() {
            this.timeout = -1;
            this.unit = null;

            return (B) this;
        }

        public abstract T build();
    }
}
