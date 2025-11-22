package net.vadamdev.dbk.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

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
        final Member member = event.getMember();
        if(member == null || !member.hasPermission(requiredPermissions))
            return; //Should never happen

        execute(member, event);
    }

    @NotNull
    @Override
    public SlashCommandData createCommandData() {
        return super.createCommandData()
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(computeDefaultPermissions());
    }

    protected DefaultMemberPermissions computeDefaultPermissions() {
        return switch(requiredPermissions.length) {
            case 0 -> DefaultMemberPermissions.ENABLED;
            case 1 -> {
                if(requiredPermissions[0].equals(Permission.ADMINISTRATOR))
                    yield DefaultMemberPermissions.DISABLED;
                else
                    yield DefaultMemberPermissions.enabledFor(requiredPermissions);
            }
            default -> DefaultMemberPermissions.enabledFor(requiredPermissions);
        };
    }

    public void setRequiredPermissions(Permission... requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GuildSlashCommand that = (GuildSlashCommand) o;
        return Objects.deepEquals(requiredPermissions, that.requiredPermissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.hashCode(requiredPermissions));
    }
}
