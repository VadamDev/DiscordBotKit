package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.vadamdev.dbk.framework.commands.GuildSlashCommand;
import net.vadamdev.dbk.framework.interactive.api.components.InteractiveComponent;
import net.vadamdev.dbk.framework.interactive.entities.SmartActionRow;
import net.vadamdev.dbk.framework.interactive.entities.dropdowns.InteractiveEntitySelectMenu;
import net.vadamdev.dbk.framework.interactive.entities.dropdowns.InteractiveStringSelectMenu;

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
        final var firstMenu = InteractiveStringSelectMenu.of(StringSelectMenu.create(InteractiveComponent.generateComponentUID())
                        .addOption("First Option", "first")
                        .addOption("Second Option", "second")
                        .build())
                .action((e, invalidatable) -> {
                    e.reply("Select Option " + e.getSelectedOptions().get(0).getValue()).setEphemeral(true).queue();
                }).build();

        final var secondMenu = InteractiveEntitySelectMenu.of(EntitySelectMenu.create(InteractiveComponent.generateComponentUID(), EntitySelectMenu.SelectTarget.USER)
                        .build())
                .action((e, invalidatable) -> {
                    e.reply("Select Option " + e.getMentions().getMembers().get(0).getAsMention()).setEphemeral(true).queue();
                }).build();

        final SmartActionRow group = new SmartActionRow().offer(firstMenu, secondMenu);

        event.reply("Here's some select menus:").setComponents(
                group.getAsActionRow(0),
                group.getAsActionRow(1)
        ).queue(group::registerAllAndClear);
    }
}
