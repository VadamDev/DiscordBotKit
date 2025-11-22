package net.vadamdev.dbk.commands.api;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.vadamdev.dbk.commands.SlashCommand;

/**
 * A {@link SlashCommand} implementing this interface will be able to listen for {@link CommandAutoCompleteInteractionEvent} automatically
 *
 * @author VadamDev
 * @since 01/11/2024
 */
public interface AutoCompleter {
    void autoComplete(CommandAutoCompleteInteractionEvent event);
}
