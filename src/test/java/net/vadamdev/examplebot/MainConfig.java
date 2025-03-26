package net.vadamdev.examplebot;

import net.vadamdev.dbk.framework.config.Configuration;
import net.vadamdev.dbk.framework.config.annotations.ConfigValue;

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
