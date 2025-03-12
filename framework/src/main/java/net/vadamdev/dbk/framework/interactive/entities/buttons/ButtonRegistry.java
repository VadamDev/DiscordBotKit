package net.vadamdev.dbk.framework.interactive.entities.buttons;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vadamdev.dbk.framework.interactive.InteractiveComponents;
import net.vadamdev.dbk.framework.interactive.api.Invalidatable;
import net.vadamdev.dbk.framework.interactive.api.registry.MessageRegistry;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 03/03/2025
 */
public final class ButtonRegistry implements MessageRegistry<Button> {
    private final Button button;
    private final BiConsumer<ButtonInteractionEvent, Invalidatable> action;
    private final Map<Permission, Consumer<ButtonInteractionEvent>> permissions;

    private boolean registered;

    ButtonRegistry(Button button, BiConsumer<ButtonInteractionEvent, Invalidatable> action, Map<Permission, Consumer<ButtonInteractionEvent>> permissions) {
        this.button = button;
        this.action = action;
        this.permissions = permissions;

        this.registered = false;
    }

    @Override
    public void register(Message message) {
        if(registered)
            return;

        final InteractiveButton result = new InteractiveButton(
                button.getId(), action, permissions,

                message.getIdLong(),
                message.getChannelIdLong(),
                message.getChannelType() != ChannelType.PRIVATE ? message.getGuildIdLong() : 0
        );

        InteractiveComponents.registerComponent(message.getJDA(), result);

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
