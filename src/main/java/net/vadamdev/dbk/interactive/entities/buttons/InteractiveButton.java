package net.vadamdev.dbk.interactive.entities.buttons;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.interactive.InteractiveComponents;
import net.vadamdev.dbk.interactive.api.Invalidatable;
import net.vadamdev.dbk.interactive.api.components.InteractiveComponent;
import net.vadamdev.dbk.interactive.api.registry.MessageRegistry;
import net.vadamdev.dbk.interactive.entities.PermissiblePersistentComponent;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 03/03/2025
 */
public class InteractiveButton extends PermissiblePersistentComponent<ButtonInteractionEvent> {
    public static MessageRegistry<Button> of(Button button, BiConsumer<ButtonInteractionEvent, Invalidatable> action, @Nullable Map<Permission, Consumer<ButtonInteractionEvent>> requiredPermissions) {
        checkButtonStyle(button.getStyle());
        return new ButtonRegistry(button, action, requiredPermissions);
    }

    public static MessageRegistry<Button> of(Button button, BiConsumer<ButtonInteractionEvent, Invalidatable> action) {
        return of(button, action, null);
    }

    @CheckReturnValue
    public static Builder of(ButtonStyle style) {
        checkButtonStyle(style);
        return new Builder(style);
    }

    private final String buttonId;
    private final BiConsumer<ButtonInteractionEvent, Invalidatable> action;

    private final long messageId, channelId, guildId;

    protected InteractiveButton(String buttonId, BiConsumer<ButtonInteractionEvent, Invalidatable> action, @Nullable Map<Permission,
                                        Consumer<ButtonInteractionEvent>> permissions, long messageId, long channelId, long guildId) {
        super(permissions);

        this.buttonId = buttonId;
        this.action = action;

        this.messageId = messageId;
        this.channelId = channelId;
        this.guildId = guildId;
    }

    @Override
    public void executeWithRequiredPermissions(ButtonInteractionEvent event) {
        action.accept(event, this);
    }

    @Override
    public boolean isValidFor(ButtonInteractionEvent event) {
        return buttonId.equals(event.getComponentId());
    }

    @Override
    public Class<ButtonInteractionEvent> getClassType() {
        return ButtonInteractionEvent.class;
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

    private static void checkButtonStyle(ButtonStyle style) {
        final boolean validStyle = switch(style) {
            case PRIMARY, SECONDARY, SUCCESS, DANGER -> true;
            default -> false;
        };

        if(!validStyle)
            throw new IllegalArgumentException("Style " + style + " cannot be used in a Interactive Button");
    }

    /*
       Builder
     */

    public static final class Builder {
        private final ButtonStyle style;

        private String buttonId, label;
        private Emoji emoji;
        private boolean disabled;

        private BiConsumer<ButtonInteractionEvent, Invalidatable> action;

        private Map<Permission, Consumer<ButtonInteractionEvent>> requiredPermissions;

        private Builder(ButtonStyle style) {
            this.style = style;

            this.buttonId = InteractiveComponent.generateComponentUID();
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

        public Builder addRequiredPermission(@NotNull Consumer<ButtonInteractionEvent> missingPermissionAction, Permission permission) {
            if(requiredPermissions == null)
                requiredPermissions = new EnumMap<>(Permission.class);

            requiredPermissions.put(permission, missingPermissionAction);

            return this;
        }

        public Builder addRequiredPermission(@NotNull Consumer<ButtonInteractionEvent> missingPermissionAction, Permission... permission) {
            if(requiredPermissions == null)
                requiredPermissions = new EnumMap<>(Permission.class);

            for(Permission p : permission)
                requiredPermissions.put(p, missingPermissionAction);

            return this;
        }

        public MessageRegistry<Button> build() {
            Checks.check(action != null, "Cannot make an InteractiveButton without an action!");
            return of(makeJDAButton(), action, requiredPermissions);
        }

        public LightweightButton lightweight(JDA jda) {
            Checks.check(action != null, "Cannot make an InteractiveButton without an action!");
            final LightweightButton button = new LightweightButton(makeJDAButton().toData(), action, requiredPermissions);

            InteractiveComponents.registerComponent(jda, button);

            return button;
        }

        private Button makeJDAButton() {
            return new ButtonImpl(buttonId, label, style, null, null, disabled, emoji).checkValid();
        }
    }
}
