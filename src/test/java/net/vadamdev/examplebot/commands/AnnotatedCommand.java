package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.vadamdev.dbk.commands.GuildSlashCommand;
import net.vadamdev.dbk.commands.annotations.AnnotatedCommandDispatcher;
import net.vadamdev.dbk.commands.annotations.SubCommand;
import org.jetbrains.annotations.NotNull;

/**
 * @author VadamDev
 * @since 20/03/2025
 */
public class AnnotatedCommand extends GuildSlashCommand {
    private final AnnotatedCommandDispatcher dispatcher;

    public AnnotatedCommand() {
        super("annotated");
        setRequiredPermissions(Permission.MESSAGE_MANAGE);

        this.dispatcher = new AnnotatedCommandDispatcher(this);
    }

    @SubCommand(name = "one")
    @SubCommand.Permission(requiredPermissions = { Permission.ADMINISTRATOR })
    public void one(SlashCommandInteractionEvent event) {
        event.reply("Hello from another command processor !").queue();
    }

    @SubCommand(group = "group", name = "one")
    public void oneButInAGroup(SlashCommandInteractionEvent event) {
        event.reply("Hello from a command processor !").queue();
    }

    @NotNull
    @Override
    public SlashCommandData createCommandData() {
        return super.createCommandData()
                .addSubcommandGroups(
                        new SubcommandGroupData("group", "Nop").addSubcommands(
                                new SubcommandData("one", "Nop"),
                                new SubcommandData("two", "Nop")
                        )
                )
                .addSubcommands(
                        new SubcommandData("one", "Nop")
                );
    }

    @Override
    public void execute(Member sender, SlashCommandInteractionEvent event) {
        dispatcher.onCommand(event);
    }
}
