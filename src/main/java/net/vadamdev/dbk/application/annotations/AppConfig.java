package net.vadamdev.dbk.application.annotations;

import net.vadamdev.dbk.config.Configuration;

import java.lang.annotation.*;

/**
 * Notify DBK that this field is a {@link Configuration Configuration} file for the application. It will be loaded before discord bots.
 * <br> The field needs to be STATIC and INSTANCED before starting DBK
 *
 * @author VadamDev
 * @since 07/10/2024
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AppConfig {
    /**
     * Defines if the app should exit on first launch.
     * Can help if you need to provide a Token in a config file, for example
     *
     * @return True, if the app should exit of first launch
     */
    boolean shouldExitOnFirstLaunch() default true;

    /**
     * Defines the exit message to display when the app exits on first launch
     *
     * @return A String representing the message to display
     */
    String exitMessage() default "Generated default app configuration, please put the bot token in the config file";
}
