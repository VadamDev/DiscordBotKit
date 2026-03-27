package net.vadamdev.dbk.components.entities.button;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.vadamdev.dbk.components.api.Invalidatable;
import net.vadamdev.dbk.components.api.PermissibleSmartComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 25/03/2026
 */
public abstract class SmartButtonBase implements PermissibleSmartComponent<ButtonInteractionEvent> {
    @Nullable private final Map<Permission, Consumer<ButtonInteractionEvent>> permissions;

    private final String buttonId;
    private final BiConsumer<ButtonInteractionEvent, Invalidatable> action;

    protected SmartButtonBase(String buttonId, BiConsumer<ButtonInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<ButtonInteractionEvent>> permissions) {
        this.permissions = permissions;

        this.buttonId = buttonId;
        this.action = action;
    }

    @Override
    public void executeWithPermission(ButtonInteractionEvent event) {
        action.accept(event, this);
    }

    @Nullable
    @Override
    public Map<Permission, Consumer<ButtonInteractionEvent>> getRequiredPermissions() {
        return permissions;
    }

    @Override
    public boolean isValidFor(ButtonInteractionEvent event) {
        return event.getComponentId().equals(buttonId);
    }

    @Override
    public Class<ButtonInteractionEvent> getClassType() {
        return ButtonInteractionEvent.class;
    }
}
