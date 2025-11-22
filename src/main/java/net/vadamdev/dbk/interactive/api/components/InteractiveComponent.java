package net.vadamdev.dbk.interactive.api.components;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.vadamdev.dbk.interactive.InteractiveComponents;
import net.vadamdev.dbk.interactive.api.Invalidatable;

import java.util.UUID;

/**
 * @author VadamDev
 * @since 08/11/2024
 */
public interface InteractiveComponent<T extends GenericInteractionCreateEvent> extends Invalidatable {
    void execute(T event);
    boolean isValidFor(T event);

    Class<T> getClassType();

    boolean shouldInvalidateOnCatch();

    @Override
    default void invalidate(JDA jda) {
        InteractiveComponents.invalidateComponent(jda, this);
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
        }catch(Exception e) {
            e.printStackTrace();
        }

        if(shouldInvalidateOnCatch())
            invalidate(event.getJDA());

        return true;
    }

    static String generateComponentUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
