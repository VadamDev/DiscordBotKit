package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.vadamdev.dbk.commands.GuildSlashCommand;
import net.vadamdev.dbk.interactive.entities.buttons.InteractiveButton;
import net.vadamdev.dbk.menu.AbstractMenu;
import net.vadamdev.dbk.menu.InteractiveComponentMenu;

/**
 * @author VadamDev
 * @since 20/03/2025
 */
@SuppressWarnings("unchecked")
public class MenuCommand extends GuildSlashCommand {
    public MenuCommand() {
        super("menu");
        setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    AbstractMenu menu;

    @Override
    public void execute(Member sender, SlashCommandInteractionEvent event) {
        menu = InteractiveComponentMenu.builder()
                .addEmbed(new EmbedBuilder().setDescription("Funny Menu").build())
                .addActionRow(
                        InteractiveButton.of(ButtonStyle.SECONDARY)
                                .label("Button 1")
                                .action((e, invalidatable) -> {
                                    e.reply("Hi!").setEphemeral(true).queue();
                                })
                                .build(),
                        InteractiveButton.of(ButtonStyle.SECONDARY)
                                .label("Button 2")
                                .action((e, invalidatable) -> {
                                    e.reply("Hello!").setEphemeral(true).queue();
                                })
                                .build()
                )
                .addActionRow(
                        InteractiveButton.of(ButtonStyle.DANGER)
                                .label("Close")
                                .action((e, invalidatable) -> {
                                    menu.invalidate(e.getJDA());
                                    e.reply("Done!").setEphemeral(true).queue();
                                })
                                .build()
                )
                .build();

        menu.display(event, false);
    }
}
