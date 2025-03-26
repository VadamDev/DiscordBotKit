package net.vadamdev.dbk.framework.interactive;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.dbk.framework.DBKFramework;
import net.vadamdev.dbk.framework.interactive.api.components.Expirable;
import net.vadamdev.dbk.framework.interactive.api.components.InteractiveComponent;
import net.vadamdev.dbk.framework.interactive.api.components.PersistentComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author VadamDev
 * @since 08/11/2024
 */
public class InteractiveComponentManager extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractiveComponentManager.class);

    private final List<InteractiveComponent<?>> components;

    public InteractiveComponentManager(JDA jda) {
        this.components = new ArrayList<>();

        jda.addEventListener(this);

        DBKFramework.getScheduledExecutorMonoThread().scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.HOURS);
    }

    public void register(InteractiveComponent<?> component) {
        components.add(component);
    }

    public void invalidate(InteractiveComponent<?> component) {
        components.removeIf(c -> c.equals(component));
    }

    public void invalidateAll(PersistentComponent<?> component) {
        invalidateMessageAttachedComponents(component.getMessageId());
    }

    public void invalidateMessageAttachedComponents(long messageId) {
        components.removeIf(c -> c instanceof PersistentComponent<?> c1 && c1.getMessageId() == messageId);
    }

    private void cleanup() {
        final int oldSize = components.size();

        final boolean hasRemoved = components.removeIf(component -> {
            if(!(component instanceof Expirable expirable) || !expirable.canExpire())
                return false;

            return expirable.getLifetimeMs() > expirable.getLongevity();
        });

        if(hasRemoved)
            LOGGER.info("Removed " + (oldSize - components.size()) + " expired components (" + oldSize + " -> " + components.size() + ")");
    }

    @Override
    public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent event) {
        for(InteractiveComponent<?> component : components) {
            if(component.executeUnsafely(event))
                break;
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        invalidateMessageAttachedComponents(event.getMessageIdLong());
    }

    @Override
    public void onMessageBulkDelete(@NotNull MessageBulkDeleteEvent event) {
        event.getMessageIds().forEach(messageId -> invalidateMessageAttachedComponents(Long.parseLong(messageId)));
    }
}
