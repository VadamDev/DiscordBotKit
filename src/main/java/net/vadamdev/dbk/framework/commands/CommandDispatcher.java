package net.vadamdev.dbk.framework.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.dbk.framework.commands.api.AutoCompleter;
import net.vadamdev.dbk.framework.commands.api.CommandExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author VadamDev
 * @since 30/10/2024
 */
public class CommandDispatcher extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatcher.class);

    protected final JDA jda;

    private final List<CommandData> commands;
    private boolean closed;

    public CommandDispatcher(JDA jda) {
        this.jda = jda;

        this.commands = new ArrayList<>();
        this.closed = false;

        jda.addEventListener(this);
    }

    public void registerCommand(CommandExecutor<?> executor) {
        if(closed)
            throw new IllegalStateException("Cannot register commands when CommandHandler is closed !");

        commands.add(new CommandData(executor, executor instanceof AutoCompleter autoCompleter ? autoCompleter : null));
    }

    public void registerCommands(CommandExecutor<?>... executors) {
        if(closed)
            throw new IllegalStateException("Cannot register commands when CommandHandler is closed !");

        for(CommandExecutor<?> executor : executors)
            registerCommand(executor);
    }

    public void registerCommandsAndClose() {
        if(closed)
            throw new IllegalStateException("Cannot register commands when CommandHandler is closed !");

        jda.updateCommands().addCommands(
                commands.stream().map(CommandData::createCommandData).toList()
        ).queue();

        closed = true;
    }

    /*
       Event Stuff
     */

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commands.stream()
                .map(CommandData::executor)
                .filter(executor -> executor.match(event.getName()))
                .findFirst().ifPresent(executor -> {
                    try {
                        executor.executeUnsafely(event);
                    }catch (ClassCastException e) {
                        LOGGER.error("An error occurred while executing /" + event.getName() + " :", e);
                    }
                });
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        commands.stream()
                .filter(commandData -> commandData.executor().match(event.getName()))
                .findFirst().flatMap(CommandData::completer).ifPresent(completer -> completer.autoComplete(event));
    }

    /*
       Getters
     */

    public boolean isClosed() {
        return closed;
    }

    /*
       Storage
     */

    private record CommandData(CommandExecutor<?> executor, Optional<AutoCompleter> completer) {
        private CommandData(CommandExecutor<?> executor, @Nullable AutoCompleter completer) {
            this(executor, Optional.ofNullable(completer));
        }

        private net.dv8tion.jda.api.interactions.commands.build.CommandData createCommandData() {
            return executor.createCommandData();
        }
    }
}
