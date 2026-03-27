package net.vadamdev.dbk.components;

import net.dv8tion.jda.api.JDA;
import net.vadamdev.dbk.components.api.SmartComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class to manage {@link SmartComponent}.
 *
 * @author VadamDev
 * @since 11/11/2024
 */
public final class SmartComponents {
    private SmartComponents() {}

    private static final Map<String, SmartComponentsManager> managers = new HashMap<>();

    public static void registerManager(JDA jda, SmartComponentsManager componentManager) {
        managers.put(jda.getSelfUser().getId(), componentManager);
    }

    public static void unregisterManager(JDA jda) {
        managers.remove(jda.getSelfUser().getId());
    }

    public static void registerComponent(JDA jda, SmartComponent<?> component) {
        findComponentManager(jda).ifPresent(manager -> manager.register(component));
    }

    public static void invalidateComponent(JDA jda, SmartComponent<?> component) {
        findComponentManager(jda).ifPresent(manager -> manager.invalidate(component));
    }

    public static Optional<SmartComponentsManager> findComponentManager(JDA jda) {
        return Optional.ofNullable(managers.get(jda.getSelfUser().getId()));
    }
}
