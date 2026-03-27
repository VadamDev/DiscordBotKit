package net.vadamdev.dbk.components.entities;

import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.vadamdev.dbk.components.api.registry.MessageRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author VadamDev
 * @since 24/03/2026
 */
public class SmartComponentDrawer implements Iterable<ActionComponent> {
    protected final List<ComponentEntry> components;

    public SmartComponentDrawer() {
        this.components = new ArrayList<>();
    }

    public <T extends ActionComponent> T push(MessageRegistry<T> registry) {
        final T component = registry.get();
        components.add(new ComponentEntry(component, registry));

        return component;
    }

    /*
       Push
     */

    public <T extends ActionComponent> T push(T component) {
        components.add(new ComponentEntry(component, null));
        return component;
    }

    /*
       Register all and dispose
     */

    public void registerAllAndClear(InteractionHook hook) {
        hook.retrieveOriginal().queue(this::registerAllAndClear);
    }

    public void registerAllAndClear(Message message) {
        components.stream()
                .map(ComponentEntry::registry)
                .filter(Objects::nonNull)
                .forEach(registry -> registry.register(message));

        components.clear();
    }

    /*
       Getters
     */

    public ActionComponent get(int index) {
        return components.get(index).component();
    }

    public int size() {
        return components.size();
    }

    @NotNull
    @Override
    public Iterator<ActionComponent> iterator() {
        return components.stream().map(ComponentEntry::component).iterator();
    }

    /*
       Storage Struct
     */

    protected record ComponentEntry(ActionComponent component, @Nullable MessageRegistry<? extends ActionComponent> registry) {}
}
