package net.vadamdev.dbk.framework.interactive.api.registry;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

/**
 * @author VadamDev
 * @since 11/03/2025
 */
public interface MessageRegistry<T> extends DeferredRegister<T, Message> {
    default void register(InteractionHook hook) {
        hook.retrieveOriginal().queue(this::register);
    }
}
