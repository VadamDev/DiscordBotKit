package net.vadamdev.examplebot;

import net.vadamdev.dbk.config.Configuration;
import net.vadamdev.dbk.config.annotations.ConfigValue;

/**
 * @author VadamDev
 * @since 07/10/2024
 */
public final class MainConfig extends Configuration {
    @ConfigValue(path = "client.token")
    String TOKEN = "Token Here";

    MainConfig() {
        super("./config.yml");
    }
}
