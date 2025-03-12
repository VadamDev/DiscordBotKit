package net.vadamdev.dbk.framework.interactive.entities.buttons;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
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
public class LightweightButton extends ButtonImpl implements PermissibleInteractiveComponent<ButtonInteractionEvent> {
    private final BiConsumer<ButtonInteractionEvent, Invalidatable> action;
    private final Map<Permission, Consumer<ButtonInteractionEvent>> permissions;

    protected LightweightButton(DataObject data, BiConsumer<ButtonInteractionEvent, Invalidatable> action,
                             @Nullable Map<Permission, Consumer<ButtonInteractionEvent>> permissions) {
        super(data);

        this.action = action;
        this.permissions = permissions;
    }

    @Override
    public void executeWithRequiredPermissions(ButtonInteractionEvent event) {
        action.accept(event, this);
    }

    @Override
    public boolean isValidFor(ButtonInteractionEvent event) {
        return getId().equals(event.getComponentId());
    }

    @Override
    public Class<ButtonInteractionEvent> getClassType() {
        return ButtonInteractionEvent.class;
    }

    @Nullable
    @Override
    public Map<Permission, Consumer<ButtonInteractionEvent>> getRequiredPermissions() {
        return permissions;
    }

    @Override
    public boolean shouldInvalidateOnCatch() {
        return false;
    }
}
