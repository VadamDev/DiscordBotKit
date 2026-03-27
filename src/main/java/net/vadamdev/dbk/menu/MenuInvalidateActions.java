package net.vadamdev.dbk.menu;

import net.dv8tion.jda.api.entities.Message;

import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 27/03/2026
 */
public final class MenuInvalidateActions {
    private MenuInvalidateActions() {}

    public static final Consumer<Message> DISABLE_COMPONENTS = message -> message.editMessageComponents(message.getComponentTree().asDisabled()).queue();
    public static final Consumer<Message> DELETE_MESSAGE = message -> message.delete().queue();
}
