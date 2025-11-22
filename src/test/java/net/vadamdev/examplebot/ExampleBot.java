package net.vadamdev.examplebot;

import net.dv8tion.jda.api.JDABuilder;
import net.vadamdev.dbk.DBKApplication;
import net.vadamdev.dbk.application.JDABot;
import net.vadamdev.dbk.application.annotations.AppConfig;
import net.vadamdev.dbk.application.annotations.Bot;
import net.vadamdev.examplebot.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VadamDev
 * @since 27/10/2024
 */
public class ExampleBot extends JDABot {
    private final Logger logger;

    public ExampleBot() {
        super(() -> JDABuilder.createLight(MAIN_CONFIG.TOKEN));

        this.logger = LoggerFactory.getLogger(ExampleBot.class);
    }

    @Override
    protected void onStart() {
        registerCommands(
                new ExampleCommand(),
                new MenuCommand(),
                new ButtonCommand(),
                new SelectMenuCommand(),
                new AnnotatedCommand(),
                new ModalCommand()
        );
    }

    @Override
    protected void onStop() {

    }

    public Logger getLogger() {
        return logger;
    }

    /*
       Main
     */

    @AppConfig
    public static final MainConfig MAIN_CONFIG = new MainConfig();

    @Bot
    private static final ExampleBot INSTANCE = new ExampleBot();

    public static void main(String[] args) {
        DBKApplication.run(ExampleBot.class, INSTANCE.getLogger());
    }

    public ExampleBot get() {
        return INSTANCE;
    }
}
