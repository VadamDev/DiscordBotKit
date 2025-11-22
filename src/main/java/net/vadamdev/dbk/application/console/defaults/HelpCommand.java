package net.vadamdev.dbk.application.console.defaults;

import net.vadamdev.dbk.application.console.ConsoleCommand;
import net.vadamdev.dbk.application.console.ConsoleCommandManager;
import net.vadamdev.dbk.application.console.sender.Sender;

/**
 * @author VadamDev
 * @since 30/10/2024
 */
public class HelpCommand extends ConsoleCommand {
    protected final ConsoleCommandManager consoleManager;

    public HelpCommand(ConsoleCommandManager consoleManager) {
        super("help");
        setDescription("Display a list of available commands with their description if provided");

        this.consoleManager = consoleManager;
    }

    @Override
    public void execute(Sender sender, String label, String[] args) {
        final StringBuilder output = new StringBuilder("\nList of currently available commands:\n");

        for(ConsoleCommand command : consoleManager.getCommands()) {
            final String description = command.getDescription();
            if(description == null)
                output.append("- \"" + command.getLabel() + "\"");
            else
                output.append("- \"" + command.getLabel() + "\" : " + description);

            output.append("\n");
        }

        sender.reply(output.toString());
    }
}
