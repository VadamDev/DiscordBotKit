package net.vadamdev.dbk.components.entities.dropdowns;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.components.api.Invalidatable;
import net.vadamdev.dbk.components.api.PermissibleSmartComponent;
import net.vadamdev.dbk.components.api.registry.MessageRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 24/03/2026
 */
public abstract class SmartSelectMenuBase<T extends GenericSelectMenuInteractionEvent<?, ? extends SelectMenu>> implements PermissibleSmartComponent<T> {
    @Nullable private final Map<Permission, Consumer<T>> permissions;

    private final String selectMenuId;
    private final BiConsumer<T, Invalidatable> action;

    protected SmartSelectMenuBase(String selectMenuId, BiConsumer<T, Invalidatable> action, @Nullable Map<Permission, Consumer<T>> permissions) {
        this.permissions = permissions;

        this.selectMenuId = selectMenuId;
        this.action = action;
    }

    @Override
    public void executeWithPermission(T event) {
        action.accept(event, this);
    }

    @Nullable
    @Override
    public Map<Permission, Consumer<T>> getRequiredPermissions() {
        return permissions;
    }

    @Override
    public boolean isValidFor(T event) {
        return event.getComponentId().equals(selectMenuId);
    }

    public abstract static class BuilderBase<T extends SelectMenu, C extends SelectMenu.Builder<T, ?>, E extends GenericSelectMenuInteractionEvent<?, T>, B extends BuilderBase<T, C, E, B>> {
        protected final C builder;

        protected BiConsumer<E, Invalidatable> action;
        @Nullable protected Map<Permission, Consumer<E>> permissions;

        protected BuilderBase(C builder) {
            this.builder = builder;
        }

        public B id(String selectMenuId) {
            builder.setCustomId(selectMenuId);
            return (B) this;
        }

        public B setPlaceholder(@Nullable String placeholder) {
            builder.setPlaceholder(placeholder);
            return (B) this;
        }

        public B setMinValues(int minValues) {
            builder.setMinValues(minValues);
            return (B) this;
        }

        public B setMaxValues(int maxValues) {
            builder.setMaxValues(maxValues);
            return (B) this;
        }

        public B setRequiredRange(int min, int max) {
            builder.setRequiredRange(min, max);
            return (B) this;
        }

        public B disabled() {
            builder.setDisabled(true);
            return (B) this;
        }

        public B action(BiConsumer<E, Invalidatable> action) {
            if(this.action != null)
                this.action = this.action.andThen(action);
            else
                this.action = action;

            return (B) this;
        }

        public B requiredPermission(@NotNull Consumer<E> missingPermissionAction, Permission permission) {
            if(permissions == null)
                permissions = new EnumMap<>(Permission.class);

            permissions.put(permission, missingPermissionAction);

            return (B) this;
        }

        public B requiredPermission(@NotNull Consumer<E> missingPermissionAction, Permission... permission) {
            if(permissions == null)
                permissions = new EnumMap<>(Permission.class);

            for(Permission p : permission)
                permissions.put(p, missingPermissionAction);

            return (B) this;
        }

        protected B checkValid() {
            Checks.notNull(action, "Action");
            return (B) this;
        }

        public abstract MessageRegistry<T> build();
        public abstract T buildStatic(JDA jda);
    }
}
