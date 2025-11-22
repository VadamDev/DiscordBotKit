package net.vadamdev.dbk.application.console.defaults;

import net.vadamdev.dbk.DBKApplication;
import net.vadamdev.dbk.application.console.ConsoleCommand;
import net.vadamdev.dbk.application.console.sender.Sender;

/**
 * @author VadamDev
 * @since 30/10/2024
 */
public class StopCommand extends ConsoleCommand {
    private final DBKApplication application;

    public StopCommand(DBKApplication application) {
        super("stop");
        setDescription("Stop all process and exit the app");

        this.application = application;
    }

    @Override
    public void execute(Sender sender, String label, String[] args) {
        sender.reply("Stopping...");
        application.stop();
    }
}
