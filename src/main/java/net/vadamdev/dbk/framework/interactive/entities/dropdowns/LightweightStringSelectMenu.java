package net.vadamdev.dbk.framework.interactive.entities.dropdowns;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;
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
public class LightweightStringSelectMenu extends StringSelectMenuImpl implements PermissibleInteractiveComponent<StringSelectInteractionEvent> {
    private final BiConsumer<StringSelectInteractionEvent, Invalidatable> action;
    private final Map<Permission, Consumer<StringSelectInteractionEvent>> permissions;

    protected LightweightStringSelectMenu(DataObject data, BiConsumer<StringSelectInteractionEvent, Invalidatable> action,
                                       @Nullable Map<Permission, Consumer<StringSelectInteractionEvent>> permissions) {
        super(data);

        this.action = action;
        this.permissions = permissions;
    }

    @Override
    public void executeWithRequiredPermissions(StringSelectInteractionEvent event) {
        action.accept(event, this);
    }

    @Override
    public boolean isValidFor(StringSelectInteractionEvent event) {
        return getId().equals(event.getComponentId());
    }

    @Override
    public Class<StringSelectInteractionEvent> getClassType() {
        return StringSelectInteractionEvent.class;
    }

    @Nullable
    @Override
    public Map<Permission, Consumer<StringSelectInteractionEvent>> getRequiredPermissions() {
        return permissions;
    }

    @Override
    public boolean shouldInvalidateOnCatch() {
        return false;
    }
}
