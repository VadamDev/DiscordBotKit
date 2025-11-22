package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.vadamdev.dbk.commands.GuildSlashCommand;
import net.vadamdev.dbk.interactive.entities.modal.InteractiveModal;

import java.util.concurrent.TimeUnit;

/**
 * @author VadamDev
 * @since 08/11/2024
 */
public class ModalCommand extends GuildSlashCommand {
    public ModalCommand() {
        super("modal");
        setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member sender, SlashCommandInteractionEvent event) {
        final var modal = InteractiveModal.dynamic(event.getJDA(), "Just for testing !")
                .addActionRow(TextInput.create("input", "Input", TextInputStyle.SHORT).build())
                .longevity(10, TimeUnit.SECONDS)
                .action((e, closeable) -> {
                    e.reply("Cool!").setEphemeral(true).queue();
                }).build();

        event.replyModal(modal).queue();
    }
}
