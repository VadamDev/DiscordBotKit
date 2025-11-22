package net.vadamdev.dbk.interactive.entities;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.vadamdev.dbk.interactive.api.registry.MessageRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author VadamDev
 * @since 03/03/2025
 */
public class SmartActionRow {
    protected final List<ComponentStorage> components;
    private int readIndex;

    public SmartActionRow() {
        this.components = new ArrayList<>();
        this.readIndex = 0;
    }

    public ActionComponent offer(MessageRegistry<? extends ActionComponent> registry) {
        final ActionComponent component = registry.get();
        components.add(new ComponentStorage(registry, component));

        return component;
    }

    public ActionComponent offer(ActionComponent component) {
        components.add(new ComponentStorage(null, component));
        return component;
    }

    public final SmartActionRow offer(MessageRegistry<? extends ActionComponent>... registries) {
        for(MessageRegistry<? extends ActionComponent> component : registries)
            offer(component);

        return this;
    }

    public final SmartActionRow offer(ActionComponent... components) {
        for(ActionComponent component : components)
            offer(component);

        return this;
    }

    public void registerAllAndClear(InteractionHook hook) {
        hook.retrieveOriginal().queue(this::registerAllAndClear);
    }

    public void registerAllAndClear(Message message) {
        components.stream()
                .map(ComponentStorage::registry)
                .filter(Objects::nonNull)
                .forEach(registry -> registry.register(message));
        components.clear();

        flip();
    }

    @Nullable
    public ActionComponent read() {
        return get(readIndex++);
    }

    public SmartActionRow flip() {
        readIndex = 0;
        return this;
    }

    @Nullable
    public ActionComponent get(int index) {
        if(index >= components.size())
            return null;

        return components.get(index).component();
    }

    @Nullable
    public ActionRow getAsActionRow(int index) {
        return ActionRow.of(get(index));
    }

    public ActionRow asActionRow() {
        final List<ActionComponent> result = components.stream()
                .map(ComponentStorage::component)
                .toList();

        return ActionRow.of(result);
    }

    protected record ComponentStorage(@Nullable MessageRegistry<? extends ActionComponent> registry, ActionComponent component) {}
}
