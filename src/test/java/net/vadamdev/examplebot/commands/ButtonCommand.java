package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.vadamdev.dbk.commands.GuildSlashCommand;
import net.vadamdev.dbk.components.entities.SmartComponentDrawer;
import net.vadamdev.dbk.components.entities.button.SmartButton;

/**
 * @author VadamDev
 * @since 03/03/2025
 */
public class ButtonCommand extends GuildSlashCommand {
    public ButtonCommand() {
        super("button");
        setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member sender, SlashCommandInteractionEvent event) {
        final SmartComponentDrawer drawer = new SmartComponentDrawer();

        final Button button = drawer.push(
                SmartButton.builder(ButtonStyle.SECONDARY)
                        .label("Click Me!")
                        .action((e, closeable) -> {
                            e.reply("Hi!").setEphemeral(true).queue();
                        }).build()
        );

        event.reply("Here's a test button:").addComponents(
                ActionRow.of(button)
        ).queue(drawer::registerAllAndClear);
    }
}
