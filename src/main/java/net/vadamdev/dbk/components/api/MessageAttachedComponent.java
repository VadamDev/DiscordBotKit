package net.vadamdev.dbk.components.api;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author VadamDev
 * @since 20/03/2026
 */
public interface MessageAttachedComponent<T extends GenericInteractionCreateEvent> extends SmartComponent<T> {
    String getMessageId();
    String getChannelId();

    @Nullable
    String getGuildId();

    @Override
    default boolean shouldInvalidateOnCatch() {
        return false;
    }
}
