package net.vadamdev.dbk.components.api;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.vadamdev.dbk.components.SmartComponents;

import java.util.UUID;

/**
 * @author VadamDev
 * @since 20/03/2026
 */
public interface SmartComponent<T extends GenericInteractionCreateEvent> extends Invalidatable {
    void execute(T event);
    boolean isValidFor(T event);

    Class<T> getClassType();

    boolean shouldInvalidateOnCatch();

    @Override
    default void invalidate(JDA jda) {
        SmartComponents.invalidateComponent(jda, this);
    }

    default boolean executeUnsafely(GenericInteractionCreateEvent event) {
        final Class<T> clazz = getClassType();
        if(!clazz.isAssignableFrom(event.getClass()))
            return false;

        final T typedEvent = clazz.cast(event);
        if(!isValidFor(typedEvent))
            return false;

        try {
            execute(typedEvent);
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(shouldInvalidateOnCatch())
            invalidate(event.getJDA());

        return true;
    }

    static String newComponentUID() {
        return UUID.randomUUID().toString().replace('-', ' ');
    }
}
