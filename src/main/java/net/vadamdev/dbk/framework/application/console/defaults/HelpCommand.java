package net.vadamdev.dbk.framework.application.console.defaults;

import net.vadamdev.dbk.framework.application.console.ConsoleCommand;
import net.vadamdev.dbk.framework.application.console.ConsoleCommandManager;
import net.vadamdev.dbk.framework.application.console.sender.Sender;

/**
 * @author VadamDev
 * @since 30/10/2024
 */
public final class HelpCommand extends ConsoleCommand {
    private final ConsoleCommandManager consoleManager;

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
                output.append("- \"" + command.getName() + "\"");
            else
                output.append("- \"" + command.getName() + "\" : " + description);

            output.append("\n");
        }

        sender.reply(output.toString());
    }
}
