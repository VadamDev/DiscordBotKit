package net.vadamdev.dbk.components.entities.button;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.internal.components.buttons.ButtonImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.components.SmartComponents;
import net.vadamdev.dbk.components.api.Invalidatable;
import net.vadamdev.dbk.components.api.MessageAttachedComponent;
import net.vadamdev.dbk.components.api.SmartComponent;
import net.vadamdev.dbk.components.api.registry.MessageRegistry;
import org.jetbrains.annotations.CheckReturnValue;
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
public final class SmartButton {
    private SmartButton() {}

    @CheckReturnValue
    public static Builder builder(ButtonStyle style) {
        assertButtonStyle(style);
        return new Builder(style);
    }

    public static void assertButtonStyle(ButtonStyle style) {
        final boolean validStyle = switch(style) {
            case PRIMARY, SECONDARY, SUCCESS, DANGER -> true;
            default -> false;
        };

        Checks.check(validStyle, "Style " + style + " cannot be used in a Smart Button");
    }

    /*
       Button Types
     */

    public static class MessageAttachedButton extends SmartButtonBase implements MessageAttachedComponent<ButtonInteractionEvent> {
        private final String messageId, channelId;
        @Nullable private final String guildId;

        protected MessageAttachedButton(String buttonId, BiConsumer<ButtonInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<ButtonInteractionEvent>> permissions, String messageId, String channelId, @Nullable String guildId) {
            super(buttonId, action, permissions);

            this.messageId = messageId;
            this.channelId = channelId;
            this.guildId = guildId;
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

    public static class StaticButton extends SmartButtonBase {
        protected StaticButton(String buttonId, BiConsumer<ButtonInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<ButtonInteractionEvent>> permissions) {
            super(buttonId, action, permissions);
        }

        @Override
        public boolean shouldInvalidateOnCatch() {
            return false;
        }
    }

    /*
       Registry
     */

    public static class Registry implements MessageRegistry<Button> {
        private final Button button;
        private final BiConsumer<ButtonInteractionEvent, Invalidatable> action;
        @Nullable private final Map<Permission, Consumer<ButtonInteractionEvent>> permissions;

        private boolean registered;

        protected Registry(Button button, BiConsumer<ButtonInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<ButtonInteractionEvent>> permissions) {
            this.button = button;
            this.action = action;
            this.permissions = permissions;

            this.registered = false;
        }

        @Override
        public void register(Message message) {
            if(registered)
                return;

            final SmartButton.MessageAttachedButton smartButton = new SmartButton.MessageAttachedButton(
                    button.getCustomId(), action, permissions,
                    message.getId(), message.getChannelId(), message.getGuildId()
            );

            SmartComponents.registerComponent(message.getJDA(), smartButton);
            registered = true;
        }

        @Override
        public boolean isRegistered() {
            return registered;
        }

        @Override
        public Button get() {
            return button;
        }
    }

    /*
       Builder
     */

    public static class Builder {
        private final ButtonStyle style;

        private String buttonId, label;
        private Emoji emoji;
        private boolean disabled;

        private BiConsumer<ButtonInteractionEvent, Invalidatable> action;
        private Map<Permission, Consumer<ButtonInteractionEvent>> permissions;

        protected Builder(ButtonStyle style) {
            this.style = style;
            this.buttonId = SmartComponent.newComponentUID();
        }

        public Builder id(String buttonId) {
            this.buttonId = buttonId;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder emoji(Emoji emoji) {
            this.emoji = emoji;
            return this;
        }

        public Builder disabled() {
            this.disabled = true;
            return this;
        }

        public Builder action(BiConsumer<ButtonInteractionEvent, Invalidatable> action) {
            if(this.action != null)
                this.action = this.action.andThen(action);
            else
                this.action = action;

            return this;
        }

        public Builder requiredPermission(@NotNull Consumer<ButtonInteractionEvent> missingPermissionAction, Permission permission) {
            if(permissions == null)
                permissions = new EnumMap<>(Permission.class);

            permissions.put(permission, missingPermissionAction);

            return this;
        }

        public Builder requiredPermission(@NotNull Consumer<ButtonInteractionEvent> missingPermissionAction, Permission... permission) {
            if(permissions == null)
                permissions = new EnumMap<>(Permission.class);

            for(Permission p : permission)
                permissions.put(p, missingPermissionAction);

            return this;
        }

        public MessageRegistry<Button> build() {
            Checks.notNull(action, "Action");
            return new Registry(makeJDAButton(), action, permissions);
        }

        public Button buildStatic(JDA jda) {
            Checks.notNull(action, "Action");

            final Button button = makeJDAButton();
            SmartComponents.registerComponent(jda, new StaticButton(button.getCustomId(), action, permissions));

            return button;
        }

        private Button makeJDAButton() {
            return new ButtonImpl(buttonId, label, style, null, null, disabled, emoji).checkValid();
        }
    }
}
