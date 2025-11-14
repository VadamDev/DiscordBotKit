package net.vadamdev.dbk.framework.application.console;

import net.vadamdev.dbk.framework.DBKApplication;
import net.vadamdev.dbk.framework.application.console.defaults.HelpCommand;
import net.vadamdev.dbk.framework.application.console.defaults.StopCommand;
import net.vadamdev.dbk.framework.application.console.sender.ConsoleSender;
import net.vadamdev.dbk.framework.application.console.sender.Sender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.InputStream;
import java.util.*;

/**
 * A class that manages console commands
 *
 * @author VadamDev
 * @since 27/10/2024
 */
public class ConsoleCommandManager implements Runnable {
    protected final Sender sender;
    protected final Scanner scanner;

    protected final Thread thread;
    protected volatile boolean running;

    protected final List<ConsoleCommand> commands;
    private boolean addedDefaultCommands;

    public ConsoleCommandManager(Sender sender, InputStream in) {
        this.sender = sender;
        this.scanner = new Scanner(in);

        this.thread = new Thread(this, "DBK-ConsoleThread");
        this.running = false;

        this.commands = new ArrayList<>();
        this.addedDefaultCommands = false;
    }

    public ConsoleCommandManager(InputStream in) {
        this(new ConsoleSender(), in);
    }

    /*
       Start / Stop
     */

    public void start() {
        if(running)
            throw new IllegalStateException("Command manager is already running!");

        running = true;
        thread.start();
    }

    public void stop() {
        if(!running)
            throw new IllegalStateException("Command manager is not running!");

        running = false;
    }

    /*
       Loop
     */

    @Override
    public void run() {
        while(running && scanner.hasNext()) {
            final String[] split = scanner.nextLine().split(" ");

            final String[] args;
            if(split.length == 1)
                args = new String[0];
            else
                args = Arrays.copyOfRange(split, 1, split.length - 1);

            final String label = split[0];

            try {
                final boolean found = dispatchCommand(label, args);

                if(!found) {
                    String reply;
                    if(addedDefaultCommands || commands.stream().anyMatch(HelpCommand.class::isInstance))
                        reply = "Command \"" + label + "\" doesn't exist. Type \"help\" to find a list of available commands!";
                    else
                        reply = "Command \"" + label + "\" doesn't exist.";

                    sender.reply(reply);
                }
            }catch (Exception e) {
                sender.reply("An error occurred while executing the command \"" + label + "\"");
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    private boolean dispatchCommand(String name, String[] args) {
        for(ConsoleCommand command : commands) {
            if(!command.match(name))
                continue;

            command.execute(sender, name, args);
            return true;
        }

        return false;
    }

    /*
        Registry
     */

    public void registerCommand(@NotNull ConsoleCommand command) {
        commands.add(command);
    }

    public void registerCommands(@NotNull ConsoleCommand... commands) {
        for(ConsoleCommand command : commands)
            registerCommand(command);
    }

    public final void addDefaultCommands(DBKApplication application) {
        if(addedDefaultCommands)
            return;

        registerCommands(
                new HelpCommand(this),
                new StopCommand(application)
        );

        addedDefaultCommands = true;
    }

    /*
       Getters
     */

    public Sender getSender() {
        return sender;
    }

    public boolean isRunning() {
        return running;
    }

    @Unmodifiable
    public List<ConsoleCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public boolean hasAddedDefaultCommands() {
        return addedDefaultCommands;
    }
}
