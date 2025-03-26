package net.vadamdev.dbk.framework.interactive.entities.dropdowns;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.vadamdev.dbk.framework.interactive.api.Invalidatable;
import net.vadamdev.dbk.framework.interactive.api.registry.MessageRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 08/03/2025
 */
public abstract class AbstractSelectMenuRegistry<T extends SelectMenu, E extends GenericSelectMenuInteractionEvent<?, T>> implements MessageRegistry<T> {
    protected final T selectMenu;

    protected final BiConsumer<E, Invalidatable> action;
    protected final Map<Permission, Consumer<E>> requiredPermissions;

    protected boolean registered;

    public AbstractSelectMenuRegistry(T selectMenu, BiConsumer<E, Invalidatable> action, @Nullable Map<Permission, Consumer<E>> requiredPermissions) {
        this.selectMenu = selectMenu;
        this.action = action;
        this.requiredPermissions = requiredPermissions;
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
