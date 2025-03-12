package net.vadamdev.examplebot;

import net.dv8tion.jda.api.JDABuilder;
import net.vadamdev.dbk.framework.application.JDABot;
import net.vadamdev.examplebot.commands.ButtonCommand;
import net.vadamdev.examplebot.commands.ExampleCommand;
import net.vadamdev.examplebot.commands.ModalCommand;
import net.vadamdev.examplebot.commands.SelectMenuCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VadamDev
 * @since 27/10/2024
 */
public class ExampleBot extends JDABot {
    private final Logger logger;

    public ExampleBot() {
        super(() -> JDABuilder.createDefault(Main.mainConfig.TOKEN));

        this.logger = LoggerFactory.getLogger(ExampleBot.class);
    }

    @Override
    protected void onStart() {
        registerCommands(
                new ExampleCommand(),
                new ButtonCommand(),
                new SelectMenuCommand(),
                new ModalCommand()
        );
    }

    @Override
    protected void onStop() {

    }

    public Logger getLogger() {
        return logger;
    }
}
