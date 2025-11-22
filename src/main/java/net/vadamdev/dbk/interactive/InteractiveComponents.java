package net.vadamdev.dbk.interactive;

import net.dv8tion.jda.api.JDA;
import net.vadamdev.dbk.interactive.api.Invalidatable;
import net.vadamdev.dbk.interactive.api.components.InteractiveComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class to manage {@link InteractiveComponent}.
 * Useful if you make custom Components or want to manage them without the {@link Invalidatable Invalidatable} interface
 *
 * @author VadamDev
 * @since 11/11/2024
 */
public final class InteractiveComponents {
    private InteractiveComponents() {}

    private static final Map<String, InteractiveComponentManager> managers = new HashMap<>();

    public static void registerManager(JDA jda, InteractiveComponentManager componentManager) {
        managers.put(jda.getSelfUser().getId(), componentManager);
    }

    public static void unregisterManager(JDA jda) {
        managers.remove(jda.getSelfUser().getId());
    }

    public static void registerComponent(JDA jda, InteractiveComponent<?> component) {
        findComponentManager(jda).ifPresent(manager -> manager.register(component));
    }

    public static void invalidateComponent(JDA jda, InteractiveComponent<?> component) {
        findComponentManager(jda).ifPresent(manager -> manager.invalidate(component));
    }

    public static Optional<InteractiveComponentManager> findComponentManager(JDA jda) {
        return Optional.ofNullable(managers.get(jda.getSelfUser().getId()));
    }
}
