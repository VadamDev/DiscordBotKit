package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.vadamdev.dbk.framework.commands.GuildSlashCommand;
import net.vadamdev.dbk.framework.commands.annotations.AnnotationProcessor;
import net.vadamdev.dbk.framework.commands.annotations.CommandProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * @author VadamDev
 * @since 20/03/2025
 */
public class AnnotatedCommand extends GuildSlashCommand {
    public AnnotatedCommand() {
        super("annotated");
        setRequiredPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(Member sender, SlashCommandInteractionEvent event) {
        AnnotationProcessor.processAnnotations(event, this);
    }

    @CommandProcessor(subCommand = "one")
    @CommandProcessor.Permissions(requiredPermissions = { Permission.ADMINISTRATOR })
    public void one(SlashCommandInteractionEvent event) {
        event.reply("Hello from another command processor !").queue();
    }

    @CommandProcessor(subCommand = "one", group = "group")
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
}
