package net.vadamdev.dbk.framework.interactive.entities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.vadamdev.dbk.framework.interactive.api.components.PermissibleInteractiveComponent;
import net.vadamdev.dbk.framework.interactive.api.components.PersistentComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 06/03/2025
 */
public abstract class PermissiblePersistentComponent<T extends GenericInteractionCreateEvent> implements PersistentComponent<T>, PermissibleInteractiveComponent<T> {
    protected final Map<Permission, Consumer<T>> permissions;

    public PermissiblePersistentComponent(@Nullable Map<Permission, Consumer<T>> permissions) {
        this.permissions = permissions;
    }

    @Nullable
    @Override
    public Map<Permission, Consumer<T>> getRequiredPermissions() {
        return permissions;
    }
}
