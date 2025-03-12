package net.vadamdev.dbk.framework.config.annotations;

import net.vadamdev.dbk.framework.config.Configuration;

import java.lang.annotation.*;

/**
 * Used in a {@link Configuration} class to specify which fields need to be configurable
 *
 * @author VadamDev
 * @see Configuration
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {
    /**
     * Define the path where the value will be saved in the YAML file
     *
     * @return A string containing the save path
     */
    String path();

    /**
     * Define a comment for the serializable data, multiple lines are supported by using \n or text blocks
     *
     * @return A string representing the comment for the serializable data
     */
    String comment() default "";

    /**
     * Define the path to a {@link net.vadamdev.dbk.framework.config.serializer.ConfigurationSerializer ConfigurationSerializer} class
     * used to have a way to serialize complex objects
     *
     * @return The path to the serializer class
     */
    String serializer() default "";
}
