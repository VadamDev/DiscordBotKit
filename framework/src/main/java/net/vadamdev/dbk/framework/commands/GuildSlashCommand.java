package net.vadamdev.dbk.framework.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a SlashCommand meant to be used only in Guilds
 *
 * @author VadamDev
 * @since 01/11/2024
 */
public abstract class GuildSlashCommand extends SlashCommand {
    protected Permission[] requiredPermissions;

    public GuildSlashCommand(String name, String description) {
        super(name, description);

        requiredPermissions = new Permission[0];
    }

    public GuildSlashCommand(String name) {
        this(name, "No description where provided");
    }

    public abstract void execute(Member sender, SlashCommandInteractionEvent event);

    @Override
    public void execute(User sender, SlashCommandInteractionEvent event) {
        execute(event.getMember(), event);
    }

    @NotNull
    @Override
    public SlashCommandData createCommandData() {
        return super.createCommandData()
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(processPermissions());
    }

    private DefaultMemberPermissions processPermissions() {
        if(requiredPermissions.length == 0)
            return DefaultMemberPermissions.ENABLED;

        if(requiredPermissions.length == 1 && requiredPermissions[0].equals(Permission.ADMINISTRATOR))
            return DefaultMemberPermissions.DISABLED;

        return DefaultMemberPermissions.enabledFor(requiredPermissions);
    }

    public void setRequiredPermissions(Permission... requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
    }
}
