package net.vadamdev.dbk.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.vadamdev.dbk.commands.api.CommandExecutor;
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
    protected final String label, description;

    public SlashCommand(String label, String description) {
        super(SlashCommandInteractionEvent.class);

        this.label = label;
        this.description = description;
    }

    public SlashCommand(String label) {
        this(label, "No description where provided");
    }

    public abstract void execute(User sender, SlashCommandInteractionEvent event);

    @Override
    public final void execute(SlashCommandInteractionEvent event) {
        execute(event.getUser(), event);
    }

    @NotNull
    @Override
    public SlashCommandData createCommandData() {
        return Commands.slash(label, description)
                .setContexts(InteractionContextType.BOT_DM);
    }

    @Override
    public boolean match(String label) {
        return this.label.equals(label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlashCommand that)) return false;
        return Objects.equals(label, that.label) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, description);
    }
}
