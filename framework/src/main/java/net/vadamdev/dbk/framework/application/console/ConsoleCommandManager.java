package net.vadamdev.dbk.framework.application.console;

import net.vadamdev.dbk.framework.application.console.defaults.HelpCommand;
import net.vadamdev.dbk.framework.application.console.defaults.StopCommand;
import net.vadamdev.dbk.framework.application.console.sender.ConsoleSender;
import net.vadamdev.dbk.framework.application.console.sender.Sender;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.*;

/**
 * A class that manages console commands
 *
 * @author VadamDev
 * @since 27/10/2024
 */
public final class ConsoleCommandManager implements Runnable {
    private final Sender sender;
    private final Scanner scanner;

    private final Thread thread;
    private boolean running;

    private final List<ConsoleCommand> commands;

    public ConsoleCommandManager(Sender sender, InputStream in) {
        this.sender = sender;
        this.scanner = new Scanner(in);

        this.thread = new Thread(this, "DBK-ConsoleThread");
        this.running = false;

        this.commands = new ArrayList<>();
    }

    public ConsoleCommandManager(InputStream in) {
        this(new ConsoleSender(), in);
    }

    public void registerCommand(@NotNull ConsoleCommand command) {
        commands.add(command);
    }

    public void registerCommands(@NotNull ConsoleCommand... commands) {
        for(ConsoleCommand command : commands)
            registerCommand(command);
    }

    public List<ConsoleCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public void start() {
        thread.start();
        running = true;
    }

    public void stop() {
        thread.interrupt();
        scanner.close();

        running = false;
    }

    @Override
    public void run() {
        do {
            final String[] split = scanner.nextLine().split(" ");

            final String[] args;
            if(split.length == 1)
                args = new String[0];
            else
                args = Arrays.copyOfRange(split, 1, split.length - 1);

            final boolean found = dispatchCommand(split[0], args);
            if(!found)
                sender.reply("Command \"" + split[0] + "\" doesn't exist. Type \"help\" to find a list of available commands!");
        }while(running && scanner.hasNext());
    }

    private boolean dispatchCommand(String name, String[] args) {
        for(ConsoleCommand command : commands) {
            if(!command.test(name))
                continue;

            command.execute(sender, name, args);
            return true;
        }

        return false;
    }

    public void addDefaultCommands() {
        if(commands.stream().anyMatch(cmd -> cmd instanceof HelpCommand || cmd instanceof StopCommand))
            return;

        registerCommands(
                new HelpCommand(this),
                new StopCommand()
        );
    }
}
