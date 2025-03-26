package net.vadamdev.dbk.framework.interactive.entities.dropdowns;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.framework.interactive.api.Invalidatable;
import net.vadamdev.dbk.framework.interactive.api.registry.MessageRegistry;
import net.vadamdev.dbk.framework.interactive.entities.PermissiblePersistentComponent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 08/03/2025
 */
public abstract class AbstractSelectMenu<T extends GenericSelectMenuInteractionEvent<?, ? extends SelectMenu>> extends PermissiblePersistentComponent<T> {
    private final String selectMenuId;
    private final BiConsumer<T, Invalidatable> action;

    private final long messageId, channelId, guildId;

    public AbstractSelectMenu(String selectMenuId, BiConsumer<T, Invalidatable> action, Map<Permission, Consumer<T>> permissions,
                              long messageId, long channelId, long guildId) {
        super(permissions);

        this.selectMenuId = selectMenuId;
        this.action = action;
        this.messageId = messageId;
        this.channelId = channelId;
        this.guildId = guildId;
    }

    @Override
    public void executeWithRequiredPermissions(T event) {
        action.accept(event, this);
    }

    @Override
    public boolean isValidFor(T event) {
        return selectMenuId.equals(event.getComponentId());
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public long getChannelId() {
        return channelId;
    }

    @Override
    public long getGuildId() {
        return guildId;
    }

    public abstract static class AbstractBuilder<T extends SelectMenu, E extends GenericSelectMenuInteractionEvent<?, T>, B extends AbstractBuilder<T, E, B>> {
        protected final T selectMenu;

        protected BiConsumer<E, Invalidatable> action;
        protected Map<Permission, Consumer<E>> requiredPermissions;

        public AbstractBuilder(T selectMenu) {
            this.selectMenu = selectMenu;

            this.action = null;
            this.requiredPermissions = null;
        }

        public B action(BiConsumer<E, Invalidatable> action) {
            if(this.action != null)
                this.action = this.action.andThen(action);
            else
                this.action = action;

            return (B) this;
        }

        public B addRequiredPermission(@NotNull Consumer<E> missingPermissionAction, Permission permission) {
            if(requiredPermissions == null)
                requiredPermissions = new EnumMap<>(Permission.class);

            requiredPermissions.put(permission, missingPermissionAction);

            return (B) this;
        }

        public B addRequiredPermission(@NotNull Consumer<E> missingPermissionAction, Permission... permission) {
            if(requiredPermissions == null)
                requiredPermissions = new EnumMap<>(Permission.class);

            for(Permission p : permission)
                requiredPermissions.put(p, missingPermissionAction);

            return (B) this;
        }

        protected B checkValid() {
            Checks.notNull(action, "Action");
            return (B) this;
        }

        public abstract MessageRegistry<T> build();
    }
}
