package net.vadamdev.dbk.components.entities.dropdowns;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.vadamdev.dbk.components.SmartComponents;
import net.vadamdev.dbk.components.api.Invalidatable;
import net.vadamdev.dbk.components.api.MessageAttachedComponent;
import net.vadamdev.dbk.components.api.SmartComponent;
import net.vadamdev.dbk.components.api.registry.MessageRegistry;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 26/03/2026
 */
public final class SmartEntitySelectMenu {
    private SmartEntitySelectMenu() {}

    @CheckReturnValue
    public static Builder builder(Collection<EntitySelectMenu.SelectTarget> types) {
        return new Builder(types);
    }

    @CheckReturnValue
    public static Builder builder(EntitySelectMenu.SelectTarget type, EntitySelectMenu.SelectTarget... types) {
        return new Builder(type, types);
    }

    /*
       Select Menu Types
     */

    public static class MessageAttachedEntitySelectMenu extends SmartSelectMenuBase<EntitySelectInteractionEvent> implements MessageAttachedComponent<EntitySelectInteractionEvent> {
        private final String messageId, channelId;
        @Nullable private final String guildId;

        protected MessageAttachedEntitySelectMenu(String selectMenuId, BiConsumer<EntitySelectInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<EntitySelectInteractionEvent>> permissions, String messageId, String channelId, @Nullable String guildId) {
            super(selectMenuId, action, permissions);

            this.messageId = messageId;
            this.channelId = channelId;
            this.guildId = guildId;
        }

        @Override
        public Class<EntitySelectInteractionEvent> getClassType() {
            return EntitySelectInteractionEvent.class;
        }

        @Override
        public String getMessageId() {
            return messageId;
        }

        @Override
        public String getChannelId() {
            return channelId;
        }

        @Nullable
        @Override
        public String getGuildId() {
            return guildId;
        }
    }

    public static class StaticEntitySelectMenu extends SmartSelectMenuBase<EntitySelectInteractionEvent> {
        protected StaticEntitySelectMenu(String selectMenuId, BiConsumer<EntitySelectInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<EntitySelectInteractionEvent>> permissions) {
            super(selectMenuId, action, permissions);
        }

        @Override
        public Class<EntitySelectInteractionEvent> getClassType() {
            return EntitySelectInteractionEvent.class;
        }

        @Override
        public boolean shouldInvalidateOnCatch() {
            return false;
        }
    }

    /*
       Registry
     */

    public static class Registry extends SmartSelectMenuRegistryBase<EntitySelectMenu, EntitySelectInteractionEvent> {
        protected Registry(EntitySelectMenu selectMenu, BiConsumer<EntitySelectInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<EntitySelectInteractionEvent>> permissions) {
            super(selectMenu, action, permissions);
        }

        @Override
        public void register(Message message) {
            if(registered)
                return;

            final SmartEntitySelectMenu.MessageAttachedEntitySelectMenu smartSelectMenu = new SmartEntitySelectMenu.MessageAttachedEntitySelectMenu(
                    selectMenu.getCustomId(), action, permissions,
                    message.getId(), message.getChannelId(), message.getGuildId()
            );

            SmartComponents.registerComponent(message.getJDA(), smartSelectMenu);
            registered = true;
        }
    }

    /*
       Builder
     */

    public static class Builder extends SmartSelectMenuBase.BuilderBase<EntitySelectMenu, EntitySelectMenu.Builder, EntitySelectInteractionEvent, Builder> {
        protected Builder(Collection<EntitySelectMenu.SelectTarget> types) {
            super(EntitySelectMenu.create(SmartComponent.newComponentUID(), types));
        }

        public Builder(EntitySelectMenu.SelectTarget type, EntitySelectMenu.SelectTarget... types) {
            super(EntitySelectMenu.create(SmartComponent.newComponentUID(), type, types));
        }

        public Builder setEntityTypes(@NotNull Collection<EntitySelectMenu.SelectTarget> types) {
            builder.setEntityTypes(types);
            return this;
        }

        public Builder setEntityTypes(@NotNull EntitySelectMenu.SelectTarget type, @NotNull EntitySelectMenu.SelectTarget... types) {
            builder.setEntityTypes(type, types);
            return this;
        }

        public Builder setChannelTypes(@NotNull Collection<ChannelType> types) {
            builder.setChannelTypes(types);
            return this;
        }

        public Builder setChannelTypes(@NotNull ChannelType... types) {
            builder.setChannelTypes(types);
            return this;
        }

        public Builder setDefaultValues(@NotNull EntitySelectMenu.DefaultValue... values) {
            builder.setDefaultValues(values);
            return this;
        }

        public Builder setDefaultValues(@NotNull Collection<? extends EntitySelectMenu.DefaultValue> values) {
            builder.setDefaultValues(values);
            return this;
        }

        @Override
        public MessageRegistry<EntitySelectMenu> build() {
            checkValid();
            return new Registry(makeJDASelectMenu(), action, permissions);
        }

        @Override
        public EntitySelectMenu buildStatic(JDA jda) {
            checkValid();

            final EntitySelectMenu selectMenu = makeJDASelectMenu();
            SmartComponents.registerComponent(jda, new SmartEntitySelectMenu.StaticEntitySelectMenu(selectMenu.getCustomId(), action, permissions));

            return selectMenu;
        }

        private EntitySelectMenu makeJDASelectMenu() {
            return builder.build();
        }
    }
}
