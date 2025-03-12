package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.vadamdev.dbk.framework.commands.GuildSlashCommand;
import net.vadamdev.dbk.framework.interactive.entities.SmartActionRow;
import net.vadamdev.dbk.framework.interactive.entities.buttons.InteractiveButton;

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
        final SmartActionRow group = new SmartActionRow();

        event.reply("Here's a test button:").setActionRow(
                group.offer(InteractiveButton.of(ButtonStyle.SECONDARY)
                        .label("Click Me!")
                        .action((e, closeable) -> {
                            e.reply("Hi!").setEphemeral(true).queue();
                        }).build()
                )
        ).queue(group::registerAllAndClear);
    }
}
