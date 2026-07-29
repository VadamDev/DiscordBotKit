package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.vadamdev.dbk.commands.GuildSlashCommand;
import net.vadamdev.dbk.components.entities.SmartComponentDrawer;
import net.vadamdev.dbk.components.entities.dropdowns.SmartEntitySelectMenu;
import net.vadamdev.dbk.components.entities.dropdowns.SmartStringSelectMenu;

/**
 * @author VadamDev
 * @since 11/03/2025
 */
public class SelectMenuCommand extends GuildSlashCommand {
    public SelectMenuCommand() {
        super("selectmenu");
        setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member sender, SlashCommandInteractionEvent event) {
        final var firstMenu = SmartStringSelectMenu.builder()
                .addOption("First Option", "first")
                .addOption("Second Option", "second")
                .action((e, invalidatable) -> {
                    e.reply("Select Option " + e.getSelectedOptions().get(0).getValue()).setEphemeral(true).queue();
                }).build();

        final var secondMenu = SmartEntitySelectMenu.builder(EntitySelectMenu.SelectTarget.USER)
                .action((e, invalidatable) -> {
                    e.reply("Select Option " + e.getMentions().getMembers().get(0).getAsMention()).setEphemeral(true).queue();
                }).build();

        final SmartComponentDrawer drawer = new SmartComponentDrawer();

        event.reply("Here's some select menus:").setComponents(
                ActionRow.of(drawer.push(firstMenu)),
                ActionRow.of(drawer.push(secondMenu))
        ).queue(drawer::registerAllAndClear);
    }
}
