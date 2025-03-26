package net.vadamdev.dbk.framework.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.vadamdev.dbk.framework.commands.api.CommandExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a basic SlashCommand meant to be compatible with direct messages
 *
 * @see GuildSlashCommand
 * @author VadamDev
 * @since 30/10/2024
 */
public abstract class SlashCommand extends CommandExecutor<SlashCommandInteractionEvent> {
    protected final String name, description;

    public SlashCommand(String name, String description) {
        super(SlashCommandInteractionEvent.class);

        this.name = name;
        this.description = description;
    }

    public SlashCommand(String name) {
        this(name, "No description where provided");
    }

    public abstract void execute(User sender, SlashCommandInteractionEvent event);

    @Override
    public final void execute(SlashCommandInteractionEvent event) {
        execute(event.getUser(), event);
    }

    @NotNull
    @Override
    public SlashCommandData createCommandData() {
        return Commands.slash(name, description)
                .setContexts(InteractionContextType.BOT_DM);
    }

    @Override
    public boolean isValidFor(String label) {
        return name.equals(label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlashCommand that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
