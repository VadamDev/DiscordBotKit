package net.vadamdev.dbk.framework.interactive.entities.dropdowns;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.component.EntitySelectMenuImpl;
import net.vadamdev.dbk.framework.interactive.api.Invalidatable;
import net.vadamdev.dbk.framework.interactive.api.components.PermissibleInteractiveComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 11/03/2025
 */
public class LightweightEntitySelectMenu extends EntitySelectMenuImpl implements PermissibleInteractiveComponent<EntitySelectInteractionEvent> {
    private final BiConsumer<EntitySelectInteractionEvent, Invalidatable> action;
    private final Map<Permission, Consumer<EntitySelectInteractionEvent>> permissions;

    protected LightweightEntitySelectMenu(DataObject data, BiConsumer<EntitySelectInteractionEvent, Invalidatable> action,
                                          @Nullable Map<Permission, Consumer<EntitySelectInteractionEvent>> permissions) {
        super(data);

        this.action = action;
        this.permissions = permissions;
    }

    @Override
    public void executeWithRequiredPermissions(EntitySelectInteractionEvent event) {
        action.accept(event, this);
    }

    @Override
    public boolean isValidFor(EntitySelectInteractionEvent event) {
        return getId().equals(event.getComponentId());
    }

    @Override
    public Class<EntitySelectInteractionEvent> getClassType() {
        return EntitySelectInteractionEvent.class;
    }

    @Nullable
    @Override
    public Map<Permission, Consumer<EntitySelectInteractionEvent>> getRequiredPermissions() {
        return permissions;
    }

    @Override
    public boolean shouldInvalidateOnCatch() {
        return false;
    }
}
