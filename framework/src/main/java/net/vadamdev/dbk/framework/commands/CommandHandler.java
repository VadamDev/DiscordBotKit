package net.vadamdev.dbk.framework.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.vadamdev.dbk.framework.DBKFramework;
import net.vadamdev.dbk.framework.commands.api.AutoCompleter;
import net.vadamdev.dbk.framework.commands.api.CommandExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author VadamDev
 * @since 30/10/2024
 */
public final class CommandHandler implements EventListener {
    private final JDA jda;

    private final List<CommandData> commands;
    private boolean closed;

    public CommandHandler(JDA jda) {
        this.jda = jda;

        this.commands = new ArrayList<>();
        this.closed = false;

        jda.addEventListener(this);
    }

    public void registerCommand(CommandExecutor<?> executor) {
        if(closed)
            throw new IllegalStateException("Cannot register commands when CommandHandler is closed !");

        AutoCompleter completer = null;
        if(executor instanceof AutoCompleter autoCompleter)
            completer = autoCompleter;

        commands.add(new CommandData(executor, completer));
    }

    public void registerCommandsAndClose() {
        if(closed)
            throw new IllegalStateException("Cannot register commands when CommandHandler is closed !");

        jda.updateCommands().addCommands(
                commands.stream().map(CommandData::toJDACommandData).toList()
        ).queue();

        closed = true;
    }

    /*
       Event Stuff
     */

    @Override
    public void onEvent(GenericEvent event) {
        switch(event) {
            case GenericCommandInteractionEvent commandEvent -> handleSlashInteraction(commandEvent);
            case CommandAutoCompleteInteractionEvent autocompleteEvent -> handleAutoComplete(autocompleteEvent);
            default -> {}
        }
    }

    private void handleSlashInteraction(GenericCommandInteractionEvent event) {
        commands.stream()
                .map(CommandData::executor)
                .filter(commandExecutor -> commandExecutor.isValidFor(event.getName()))
                .findFirst().ifPresent(commandExecutor -> {
                    try {
                        commandExecutor.executeUnsafely(event);
                    }catch (ClassCastException e) {
                        DBKFramework.get().getLogger().error("An error occurred while executing /" + event.getName() + " : " + e.getMessage());
                    }
                });
    }

    private void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
        commands.stream()
                .filter(commandData -> commandData.executor().isValidFor(event.getName()))
                .findFirst().flatMap(CommandData::completer).ifPresent(completer -> completer.autoComplete(event));
    }

    /*
       Storage
     */

    private record CommandData(CommandExecutor<?> executor, Optional<AutoCompleter> completer) {
        private CommandData(CommandExecutor<?> executor, @Nullable AutoCompleter completer) {
            this(executor, Optional.ofNullable(completer));
        }

        private net.dv8tion.jda.api.interactions.commands.build.CommandData toJDACommandData() {
            return executor.createCommandData();
        }
    }
}
