package net.vadamdev.dbk.components.entities.dropdowns;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
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
 * @since 25/03/2026
 */
public final class SmartStringSelectMenu {
    private SmartStringSelectMenu() {}

    @CheckReturnValue
    public static Builder builder() {
        return new Builder();
    }

    /*
       Select Menu Types
     */

    public static class MessageAttachedStringSelectMenu extends SmartSelectMenuBase<StringSelectInteractionEvent> implements MessageAttachedComponent<StringSelectInteractionEvent> {
        private final String messageId, channelId;
        @Nullable private final String guildId;

        protected MessageAttachedStringSelectMenu(String selectMenuId, BiConsumer<StringSelectInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<StringSelectInteractionEvent>> permissions, String messageId, String channelId, @Nullable String guildId) {
            super(selectMenuId, action, permissions);

            this.messageId = messageId;
            this.channelId = channelId;
            this.guildId = guildId;
        }

        @Override
        public Class<StringSelectInteractionEvent> getClassType() {
            return StringSelectInteractionEvent.class;
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
        public  String getGuildId() {
            return guildId;
        }
    }

    public static class StaticStringSelectMenu extends SmartSelectMenuBase<StringSelectInteractionEvent> {
        protected StaticStringSelectMenu(String selectMenuId, BiConsumer<StringSelectInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<StringSelectInteractionEvent>> permissions) {
            super(selectMenuId, action, permissions);
        }

        @Override
        public Class<StringSelectInteractionEvent> getClassType() {
            return StringSelectInteractionEvent.class;
        }

        @Override
        public boolean shouldInvalidateOnCatch() {
            return false;
        }
    }

    /*
       Registry
     */

    public static class Registry extends SmartSelectMenuRegistryBase<StringSelectMenu, StringSelectInteractionEvent> {
        protected Registry(StringSelectMenu selectMenu, BiConsumer<StringSelectInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<StringSelectInteractionEvent>> permissions) {
            super(selectMenu, action, permissions);
        }

        @Override
        public void register(Message message) {
            if(registered)
                return;

            final SmartStringSelectMenu.MessageAttachedStringSelectMenu smartSelectMenu = new SmartStringSelectMenu.MessageAttachedStringSelectMenu(
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

    public static class Builder extends SmartSelectMenuBase.BuilderBase<StringSelectMenu, StringSelectMenu.Builder, StringSelectInteractionEvent, Builder> {
        protected Builder() {
            super(StringSelectMenu.create(SmartComponent.newComponentUID()));
        }

        public Builder addOptions(@NotNull SelectOption... options) {
            builder.addOptions(options);
            return this;
        }

        public Builder addOptions(@NotNull Collection<? extends SelectOption> options) {
            builder.addOptions(options);
            return this;
        }

        public Builder addOption(@NotNull String label, @NotNull String value) {
            builder.addOption(label, value);
            return this;
        }

        public Builder addOption(@NotNull String label, @NotNull String value, @NotNull Emoji emoji) {
            builder.addOption(label, value, emoji);
            return this;
        }

        public Builder addOption(@NotNull String label, @NotNull String value, @NotNull String description) {
            builder.addOption(label, value, description);
            return this;
        }

        public Builder addOption(@NotNull String label, @NotNull String value, @Nullable String description, @Nullable Emoji emoji) {
            builder.addOption(label, value, description, emoji);
            return this;
        }

        public Builder setDefaultValues(@NotNull Collection<String> values) {
            builder.setDefaultValues(values);
            return this;
        }

        public Builder setDefaultValues(@NotNull String... values) {
            builder.setDefaultValues(values);
            return this;
        }

        public Builder setDefaultOptions(@NotNull Collection<? extends SelectOption> values) {
            builder.setDefaultOptions(values);
            return this;
        }

        public Builder setDefaultOptions(@NotNull SelectOption... values) {
            builder.setDefaultOptions(values);
            return this;
        }

        @Override
        public MessageRegistry<StringSelectMenu> build() {
            checkValid();
            return new Registry(makeJDASelectMenu(), action, permissions);
        }

        @Override
        public StringSelectMenu buildStatic(JDA jda) {
            checkValid();

            final StringSelectMenu selectMenu = makeJDASelectMenu();
            SmartComponents.registerComponent(jda, new StaticStringSelectMenu(selectMenu.getCustomId(), action, permissions));

            return selectMenu;
        }

        private StringSelectMenu makeJDASelectMenu() {
            return builder.build();
        }
    }
}
