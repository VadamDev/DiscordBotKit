package net.vadamdev.dbk.framework.interactive;

import net.dv8tion.jda.api.JDA;
import net.vadamdev.dbk.framework.interactive.api.components.InteractiveComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Utility class to manage {@link InteractiveComponent}.
 * Useful if you make custom Components or want to manage them without the {@link net.vadamdev.dbk.framework.interactive.api.Invalidatable Invalidatable} interface
 *
 * @author VadamDev
 * @since 11/11/2024
 */
public final class InteractiveComponents {
    private InteractiveComponents() {}

    private static final Map<String, InteractiveComponentManager> managers = new HashMap<>();

    static final ScheduledExecutorService MONO_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

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

    public static void shutdown() {
        MONO_EXECUTOR.shutdown();
    }

    private static Optional<InteractiveComponentManager> findComponentManager(JDA jda) {
        return Optional.ofNullable(managers.get(jda.getSelfUser().getId()));
    }
}
