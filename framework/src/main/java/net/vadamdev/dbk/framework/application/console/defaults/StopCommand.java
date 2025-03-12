package net.vadamdev.dbk.framework.application.console.defaults;

import net.vadamdev.dbk.framework.DBKConstants;
import net.vadamdev.dbk.framework.DBKFramework;
import net.vadamdev.dbk.framework.application.console.ConsoleCommand;
import net.vadamdev.dbk.framework.application.console.sender.Sender;

/**
 * @author VadamDev
 * @since 30/10/2024
 */
public final class StopCommand extends ConsoleCommand {
    public StopCommand() {
        super(DBKConstants.STOP_COMMAND);
        setDescription("Stop all processes and exit the app");
    }

    @Override
    public void execute(Sender sender, String label, String[] args) {
        sender.reply("Stopping...");
        DBKFramework.stop();
    }
}
