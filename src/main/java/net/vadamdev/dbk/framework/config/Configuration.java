package net.vadamdev.dbk.framework.config;

import net.vadamdev.dbk.framework.config.annotations.ConfigValue;
import net.vadamdev.dbk.framework.config.serializer.ConfigurationSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Represents a configuration file.
 * <br>Every field annotated with {@link ConfigValue} will be configurable in the associated yml file.
 *
 * @author VadamDev
 * @since 18/10/2022
 */
public class Configuration {
    protected final YamlFile yamlFile;

    public Configuration(YamlFile yamlFile) {
        this.yamlFile = yamlFile;
    }

    public Configuration(File file) {
        this(new YamlFile(file));
    }

    public Configuration(String path) {
        this(new YamlFile(path));
    }

    /**
     * Change the provided value in the yml object and field.
     * <br>Use the save() function to save the changes in the yml file.
     *
     * @param name Field name
     * @param value New value
     */
    public void setValue(String name, @Nullable Object value) {
        try {
            final Field field = getClass().getField(name);

            final ConfigValue configValue = field.getAnnotation(ConfigValue.class);
            if(configValue == null)
                return;

            field.set(this, value);
            yamlFile.set(configValue.path(), ConfigurationSerializer.serializeField(value, configValue.serializer()));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the provided fieldName exists.
     *
     * @param fieldName Field name to check
     * @return True if the provided field name is a field
     */
    public boolean hasField(@NotNull String fieldName) {
        try {
            return getClass().getField(fieldName).isAnnotationPresent(ConfigValue.class);
        }catch (NoSuchFieldException ignored) {}

        return false;
    }

    /**
     * Saves changes in the YAML file.
     */
    public void save() throws IOException {
        yamlFile.save();
    }

    public YamlFile getYamlFile() {
        return yamlFile;
    }
}
