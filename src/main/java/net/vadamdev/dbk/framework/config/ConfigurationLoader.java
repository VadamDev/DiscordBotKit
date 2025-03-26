package net.vadamdev.dbk.framework.config;

import net.vadamdev.dbk.framework.config.annotations.ConfigRange;
import net.vadamdev.dbk.framework.config.annotations.ConfigValue;
import net.vadamdev.dbk.framework.config.serializer.ConfigurationSerializer;
import net.vadamdev.dbk.framework.config.serializer.ILengthHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Used for loading {@link Configuration} classes
 *
 * @author VadamDev
 * @since 18/10/2022
 */
public final class ConfigurationLoader {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoader.class);

    private ConfigurationLoader() {}

    /**
     * Load a configuration
     * <p>
     * Currently, it supports:
     * <br> 1. Limit inputs with {@link ConfigRange @ConfigRange}
     * <br> 2. Ignores transient fields
     * <br> 3. Ignore final fields for deserialization
     * <br> 4. Comments
     *
     * @param configuration The configuration that needs to be loaded
     *
     * @throws IOException If an exception happened with the YAML file
     * @throws IllegalAccessException If a configurable field cannot be edited
     */
    public static void loadConfiguration(@NotNull Configuration configuration) throws IOException, IllegalAccessException {
        //Load the YAML file
        final YamlFile yamlFile = configuration.getYamlFile();
        yamlFile.createOrLoad();

        for(Field field : configuration.getClass().getDeclaredFields()) {
            final ConfigValue configValue = field.getAnnotation(ConfigValue.class);
            if(configValue == null)
                continue;

            final int modifiers = field.getModifiers();

            //Ignore transient fields
            if(Modifier.isTransient(modifiers))
                continue;

            //If the field is protected or private, we make it accessible
            if(!field.canAccess(configuration))
                field.setAccessible(true);

            final String pathToData = configValue.path();
            if(yamlFile.isSet(pathToData)) {
                //Skip deserialization for final fields
                if(Modifier.isFinal(modifiers))
                    continue;

                final Object correctedValue = checkAndCorrectInput(
                        ConfigurationSerializer.unserializeField(yamlFile.get(pathToData), configValue.serializer()),
                        field.getAnnotation(ConfigRange.class),
                        pathToData
                );

                field.set(configuration, correctedValue);
            }else {
                final Object value = ConfigurationSerializer.serializeField(field.get(configuration), configValue.serializer());
                if(value == null) {
                    LOGGER.error("Failed to serialize field: " + field.getName());
                    continue;
                }

                yamlFile.addDefault(pathToData, value);

                final String comment = configValue.comment();
                if(comment != null && !comment.isBlank())
                    yamlFile.setComment(pathToData, bakeComment(field, comment));
            }

            if(field.isAccessible())
                field.setAccessible(false);
        }

        configuration.save();
    }

    /**
     * Load multiple configurations
     *
     * @param configurations Configurations that need to be loaded
     *
     * @throws IllegalArgumentException if no configurations where provided
     */
    public static void loadConfigurations(@NotNull Configuration... configurations) throws IOException, IllegalAccessException {
        if(configurations.length == 0)
            throw new IllegalArgumentException("No configuration(s) where provided");

        for(Configuration configuration : configurations)
            loadConfiguration(configuration);
    }

    /*
       Input Correction
     */

    @Nullable
    private static Object checkAndCorrectInput(@Nullable Object object, @Nullable ConfigRange configRange, String pathToData) {
        if(object == null || configRange == null)
            return object;

        final double length;
        switch (object) {
            case Number number -> length = number.doubleValue();
            case String str -> length = str.length();
            case ILengthHolder holder -> length = holder.length();
            default -> {
                return object;
            }
        }

        final double min = configRange.min();
        final double max = configRange.max();

        if(length > min && length < max)
            return object;

        if(!configRange.setToNearestLimit()) {
            LOGGER.error("Provided data in " + pathToData + " is not in the required range ! (min: " + min + " | max: " + max + ", but provided is: " + length + ")");
            return object instanceof Number ? 0 : null;
        }

        return correctValue(object, min, max);
    }

    private static Object correctValue(Object object, double min, double max) {
        //TODO: log that we corrected the value

        if(object instanceof String str)
            return (max < str.length() && max > 0) ? str.substring(0, (int) max) : null;
        else if(object instanceof Number number)
            return switch(number) {
                case Integer i -> Math.clamp(i, (int) min, (int) max);
                case Double d -> Math.clamp(d, min, max);
                case Float f -> Math.clamp(f, (float) min, (float) max);
                case Long l -> Math.clamp(l, (long) min, (long) max);
                case Short s -> (short) Math.clamp(s, (int) min, (int) max);
                case Byte b -> (byte) Math.clamp(b, (int) min, (int) max);

                default -> throw new IllegalArgumentException("Unsupported Number type: " + number.getClass().getName());
            };
        else if(object instanceof ILengthHolder holder)
            return holder.clampLength(min, max);

        return object;
    }

    /*
       Comments
     */

    private static String bakeComment(Field field, String comment) {
        final StringBuilder bakedComment = new StringBuilder(comment);

        final ConfigRange configRange = field.getAnnotation(ConfigRange.class);
        if(configRange != null)
            bakedComment.append("\nRange: " + configRange.min() + " ~ " + configRange.max());

        return !bakedComment.isEmpty() ? bakedComment.toString() : null;
    }
}
