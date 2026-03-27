package net.vadamdev.dbk.components;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.dbk.DBKApplication;
import net.vadamdev.dbk.components.api.IAutoExpirable;
import net.vadamdev.dbk.components.api.MessageAttachedComponent;
import net.vadamdev.dbk.components.api.SmartComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author VadamDev
 * @since 20/03/2026
 */
public class SmartComponentsManager extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartComponentsManager.class);

    private final List<SmartComponent<?>> allComponents;
    private final List<MessageAttachedComponent<?>> messageAttachedComponents;
    private final List<IAutoExpirable> autoExpirables;

    public SmartComponentsManager(JDA jda, DBKApplication application) {
        this.allComponents = new ArrayList<>();
        this.messageAttachedComponents = new ArrayList<>();
        this.autoExpirables = new ArrayList<>();

        application.getScheduledExecutorMonoThread().scheduleAtFixedRate(this::cleanup, 5, 5, TimeUnit.MINUTES);

        jda.addEventListener(this);
    }

    public void register(SmartComponent<?> component) {
        allComponents.add(component);

        if(component instanceof MessageAttachedComponent<?> comp)
            messageAttachedComponents.add(comp);

        if(component instanceof IAutoExpirable autoExpirable)
            autoExpirables.add(autoExpirable);
    }

    public void invalidate(SmartComponent<?> component) {
        allComponents.remove(component);

        if(component instanceof MessageAttachedComponent<?> comp)
            messageAttachedComponents.remove(comp);

        if(component instanceof IAutoExpirable autoExpirable)
            autoExpirables.remove(autoExpirable);
    }

    /*
       Message Attached
     */

    public void invalidateMessageAttachedComponents(String messageId) {
        final MessageAttachedComponent<?> component = messageAttachedComponents.stream()
                .filter(comp -> comp.getMessageId().equals(messageId))
                .findFirst().orElse(null);

        if(component == null)
            return;

        invalidate(component);
    }

    public void invalidateChannelAttachedComponents(String channelId) {
        final MessageAttachedComponent<?> component = messageAttachedComponents.stream()
                .filter(comp -> comp.getChannelId().equals(channelId))
                .findFirst().orElse(null);

        if(component == null)
            return;

        invalidate(component);
    }

    /*
       AutoExpirable
     */

    private void cleanup() {
        final int oldSize = allComponents.size();

        //TODO: remove this retarded array duplication
        int removed = 0;
        for(IAutoExpirable autoExpirable : new ArrayList<>(autoExpirables)) {
            if(!autoExpirable.canExpire())
                continue;

            if(autoExpirable.getLifetimeMs() <= autoExpirable.getLongevity())
                continue;

            invalidate((SmartComponent<?>) autoExpirable);
            removed++;
        }

        if(removed > 0)
            LOGGER.info("Removed " + removed + " expired components (" + oldSize + " -> " + allComponents.size() + ")");
    }

    /*
       Listener
     */

    @Override
    public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent event) {
        for(SmartComponent<?> component : allComponents) {
            if(!component.executeUnsafely(event))
                continue;

            break;
        }
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        invalidateChannelAttachedComponents(event.getChannel().getId());
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        invalidateMessageAttachedComponents(event.getMessageId());
    }

    @Override
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {
        event.getMessageIds().forEach(this::invalidateMessageAttachedComponents);
    }
}
