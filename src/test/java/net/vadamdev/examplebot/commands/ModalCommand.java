package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import net.vadamdev.dbk.commands.GuildSlashCommand;
import net.vadamdev.dbk.components.entities.modal.SmartModal;

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
        final Modal modal = SmartModal.builder("Just for testing!")
                .addComponents(
                        Label.of("Input", TextInput.create("input", TextInputStyle.SHORT).build())
                )
                .longevity(10, TimeUnit.SECONDS)
                .action((e, invalidatable) -> {
                    e.reply("Cool!").setEphemeral(true).queue();
                }).build(event.getJDA());

        event.replyModal(modal).queue();
    }
}
