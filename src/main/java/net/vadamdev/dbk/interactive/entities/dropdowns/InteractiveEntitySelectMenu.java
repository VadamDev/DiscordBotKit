package net.vadamdev.dbk.interactive.entities.dropdowns;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.vadamdev.dbk.interactive.InteractiveComponents;
import net.vadamdev.dbk.interactive.api.Invalidatable;
import net.vadamdev.dbk.interactive.api.registry.MessageRegistry;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 06/03/2025
 */
public class InteractiveEntitySelectMenu extends AbstractSelectMenu<EntitySelectInteractionEvent> {
    public static MessageRegistry<EntitySelectMenu> of(EntitySelectMenu selectMenu, BiConsumer<EntitySelectInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<EntitySelectInteractionEvent>> permissions) {
        return new Registry(selectMenu, action, permissions);
    }

    public static MessageRegistry<EntitySelectMenu> of(EntitySelectMenu selectMenu, BiConsumer<EntitySelectInteractionEvent, Invalidatable> action) {
        return of(selectMenu, action, null);
    }

    @CheckReturnValue
    public static Builder of(EntitySelectMenu selectMenu) {
        return new Builder(selectMenu);
    }

    protected InteractiveEntitySelectMenu(String selectMenuId, BiConsumer<EntitySelectInteractionEvent, Invalidatable> action,
                                          Map<Permission, Consumer<EntitySelectInteractionEvent>> permissions, long messageId, long channelId, long guildId) {

        super(selectMenuId, action, permissions, messageId, channelId, guildId);
    }

    @Override
    public Class<EntitySelectInteractionEvent> getClassType() {
        return EntitySelectInteractionEvent.class;
    }

    /*
       Builder
     */

    public static final class Builder extends AbstractBuilder<EntitySelectMenu, EntitySelectInteractionEvent, Builder> {
        public Builder(EntitySelectMenu selectMenu) {
            super(selectMenu);
        }

        @NotNull
        @Override
        public MessageRegistry<EntitySelectMenu> build() {
            checkValid();
            return of(selectMenu, action, requiredPermissions);
        }

        public LightweightEntitySelectMenu lightweight(JDA jda) {
            final LightweightEntitySelectMenu lightweightMenu = new LightweightEntitySelectMenu(selectMenu.toData(), action, requiredPermissions);
            InteractiveComponents.registerComponent(jda, lightweightMenu);

            return lightweightMenu;
        }
    }

    /*
       Registry
     */

    private static final class Registry extends AbstractSelectMenuRegistry<EntitySelectMenu, EntitySelectInteractionEvent> {
        public Registry(EntitySelectMenu selectMenu, BiConsumer<EntitySelectInteractionEvent, Invalidatable> action,
                        @Nullable Map<Permission, Consumer<EntitySelectInteractionEvent>> requiredPermissions) {
            super(selectMenu, action, requiredPermissions);
        }

        @Override
        public void register(Message message) {
            if(registered)
                return;

            final InteractiveEntitySelectMenu result = new InteractiveEntitySelectMenu(
                    selectMenu.getId(),
                    action,
                    requiredPermissions,

                    message.getIdLong(),
                    message.getGuildIdLong(),
                    message.getChannelType() != ChannelType.PRIVATE ? message.getGuildIdLong() : 0
            );

            InteractiveComponents.registerComponent(message.getJDA(), result);

            registered = true;
        }
    }
}
