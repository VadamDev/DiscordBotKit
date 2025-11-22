package net.vadamdev.dbk.interactive.entities.dropdowns;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
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
public class InteractiveStringSelectMenu extends AbstractSelectMenu<StringSelectInteractionEvent> {
    public static MessageRegistry<StringSelectMenu> of(StringSelectMenu selectMenu, BiConsumer<StringSelectInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<StringSelectInteractionEvent>> permissions) {
        return new Registry(selectMenu, action, permissions);
    }

    public static MessageRegistry<StringSelectMenu> of(StringSelectMenu selectMenu, BiConsumer<StringSelectInteractionEvent, Invalidatable> action) {
        return of(selectMenu, action, null);
    }

    @CheckReturnValue
    public static Builder of(StringSelectMenu selectMenu) {
        return new Builder(selectMenu);
    }

    protected InteractiveStringSelectMenu(String selectMenuId, BiConsumer<StringSelectInteractionEvent, Invalidatable> action,
                                          Map<Permission, Consumer<StringSelectInteractionEvent>> permissions, long messageId, long channelId, long guildId) {

        super(selectMenuId, action, permissions, messageId, channelId, guildId);
    }

    @Override
    public Class<StringSelectInteractionEvent> getClassType() {
        return StringSelectInteractionEvent.class;
    }

    /*
       Builder
     */

    public static final class Builder extends AbstractSelectMenu.AbstractBuilder<StringSelectMenu, StringSelectInteractionEvent, Builder> {
        public Builder(StringSelectMenu selectMenu) {
            super(selectMenu);
        }

        @NotNull
        @Override
        public MessageRegistry<StringSelectMenu> build() {
            checkValid();
            return of(selectMenu, action, requiredPermissions);
        }

        public LightweightStringSelectMenu lightweight(JDA jda) {
            final LightweightStringSelectMenu lightweightMenu = new LightweightStringSelectMenu(selectMenu.toData(), action, requiredPermissions);
            InteractiveComponents.registerComponent(jda, lightweightMenu);

            return lightweightMenu;
        }
    }

    /*
       Registry
     */

    private static final class Registry extends AbstractSelectMenuRegistry<StringSelectMenu, StringSelectInteractionEvent> {
        public Registry(StringSelectMenu selectMenu, BiConsumer<StringSelectInteractionEvent, Invalidatable> action,
                        @Nullable Map<Permission, Consumer<StringSelectInteractionEvent>> requiredPermissions) {
            super(selectMenu, action, requiredPermissions);
        }

        @Override
        public void register(Message message) {
            if(registered)
                return;

            final InteractiveStringSelectMenu result = new InteractiveStringSelectMenu(
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
