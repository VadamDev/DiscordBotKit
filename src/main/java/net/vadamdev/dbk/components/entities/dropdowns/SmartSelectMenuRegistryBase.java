package net.vadamdev.dbk.components.entities.dropdowns;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.vadamdev.dbk.components.api.Invalidatable;
import net.vadamdev.dbk.components.api.registry.MessageRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 24/03/2026
 */
public abstract class SmartSelectMenuRegistryBase<T extends SelectMenu, E extends GenericSelectMenuInteractionEvent<?, T>> implements MessageRegistry<T> {
    protected final T selectMenu;

    protected final BiConsumer<E, Invalidatable> action;
    protected final Map<Permission, Consumer<E>> permissions;

    protected boolean registered;

    protected SmartSelectMenuRegistryBase(T selectMenu, BiConsumer<E, Invalidatable> action, @Nullable Map<Permission, Consumer<E>> permissions) {
        this.selectMenu = selectMenu;

        this.action = action;
        this.permissions = permissions;
    }

    @Override
    public boolean isRegistered() {
        return registered;
    }

    @Override
    public T get() {
        return selectMenu;
    }
}
