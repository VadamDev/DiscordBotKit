package net.vadamdev.dbk.interactive.api.components;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

/**
 * @author VadamDev
 * @since 18/11/2024
 */
public interface PersistentComponent<T extends GenericInteractionCreateEvent> extends InteractiveComponent<T> {
    long getMessageId();
    long getChannelId();
    long getGuildId();

    default boolean isFromGuild() {
        return getGuildId() != 0;
    }

    @Override
    default boolean shouldInvalidateOnCatch() {
        return false;
    }
}
