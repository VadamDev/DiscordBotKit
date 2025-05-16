package net.vadamdev.dbk.framework.menu;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 16/05/2025
 */
public final class InvalidateActions {
    private InvalidateActions() {}

    public static final Consumer<Message> DISABLE_COMPONENTS_ON_INVALIDATE = message -> {
        final List<ActionRow> newRows = new ArrayList<>();
        for(ActionRow actionRow : message.getActionRows()) {
            final List<ItemComponent> newComponents = new ArrayList<>();

            for(ItemComponent component : actionRow.getComponents()) {
                if(!(component instanceof ActionComponent actionComponent) || actionComponent.isDisabled())
                    newComponents.add(component);
                else
                    newComponents.add(actionComponent.asDisabled());
            }

            newRows.add(ActionRow.of(newComponents));
        }

        message.editMessageComponents(newRows).queue();
    };

    public static final Consumer<Message> DELETE_MESSAGE_ON_INVALIDATE = message -> message.delete().queue();
}
