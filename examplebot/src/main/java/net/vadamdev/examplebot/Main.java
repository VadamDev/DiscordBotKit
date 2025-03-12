package net.vadamdev.examplebot;

import net.vadamdev.dbk.framework.DBKApp;
import net.vadamdev.dbk.framework.DBKFramework;
import net.vadamdev.dbk.framework.application.annotations.AppConfig;
import net.vadamdev.dbk.framework.application.annotations.Bot;

/**
 * @author VadamDev
 * @since 04/10/2024
 */
@DBKApp(appName = "ExampleBot", appVersion = "1.0.0")
public class Main {
    @AppConfig
    public static final MainConfig mainConfig = new MainConfig();

    @Bot
    public static final ExampleBot exampleBot = new ExampleBot();

    public static void main(String[] args) {
        DBKFramework.launch(Main.class, exampleBot.getLogger());
    }
}
