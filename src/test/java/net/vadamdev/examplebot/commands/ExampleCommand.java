package net.vadamdev.examplebot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.vadamdev.dbk.commands.GuildSlashCommand;
import net.vadamdev.dbk.commands.api.AutoCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author VadamDev
 * @since 30/10/2024
 */
public class ExampleCommand extends GuildSlashCommand implements AutoCompleter {
    public ExampleCommand() {
        super("example", "Say that the provided input is cool!");
    }

    @Override
    public void execute(Member sender, SlashCommandInteractionEvent event) {
        event.reply(event.getOption("input", OptionMapping::getAsString) + " are cool!").queue();
    }

    @Override
    public @NotNull SlashCommandData createCommandData() {
        return super.createCommandData().addOptions(
                new OptionData(OptionType.STRING, "input", "An input", true, true)
        );
    }

    private final String[] words = new String[] {"apple", "apricot", "banana", "cherry", "coconut", "cranberry"};

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        if(!event.getFocusedOption().getName().equals("input"))
            return;

        List<Command.Choice> options = Stream.of(words)
                .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                .map(word -> new Command.Choice(word, word)) // map the words to choices
                .toList();

        event.replyChoices(options).queue();
    }
}
