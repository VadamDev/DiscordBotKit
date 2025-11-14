package net.vadamdev.dbk.framework.commands.api;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the most abstract command possible.
 * <p>If you don't want to use {@link net.vadamdev.dbk.framework.commands.SlashCommand SlashCommand} or {@link net.vadamdev.dbk.framework.commands.GuildSlashCommand GuildSlashCommand}
 * you can do your own implementation with this abstract class
 *
 * @author VadamDev
 * @since 03/11/2024
 */
public abstract class CommandExecutor<T extends GenericCommandInteractionEvent> {
    private final Class<T> clazz;

    public CommandExecutor(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Called when the event happened
     *
     * @param event The event caught by JDA
     */
    public abstract void execute(T event);

    public final void executeUnsafely(GenericCommandInteractionEvent event) throws ClassCastException {
        if(!clazz.isAssignableFrom(event.getClass()))
            throw new ClassCastException("Invalid event! Provided: " + event.getClass().getSimpleName() + " but " + clazz.getSimpleName() + " is required");

        execute((T) event);
    }

    /**
     * Defines if the provided label matches the label of this command, aliases logic can be done here too
     *
     * @param label The label of a command
     * @return True, if the provided label matches the label of the command represented by this class
     */
    public abstract boolean match(String label);

    @NotNull
    public abstract CommandData createCommandData();
}
