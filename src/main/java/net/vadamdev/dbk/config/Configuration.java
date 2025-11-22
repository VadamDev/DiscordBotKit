package net.vadamdev.dbk.config;

import net.vadamdev.dbk.config.annotations.ConfigValue;
import net.vadamdev.dbk.config.loader.ConfigurableField;
import net.vadamdev.dbk.config.loader.ConfigurationLoader;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;

/**
 * Represents a configuration file.
 * <br>Every field annotated with {@link ConfigValue} will be configurable in the associated yml file.
 *
 * @author VadamDev
 * @since 18/10/2022
 */
public class Configuration {
    protected final YamlFile yamlFile;
    protected ConfigurationLoader loader;

    public Configuration(YamlFile yamlFile) {
        this.yamlFile = yamlFile;
    }

    public Configuration(File file) {
        this(new YamlFile(file));
    }

    public Configuration(String path) {
        this(new YamlFile(path));
    }

    public void setLoader(ConfigurationLoader loader) {
        if(isLoaderSet())
            throw new IllegalStateException("Loader is already set!");

        this.loader = loader;
    }

    public boolean isLoaderSet() {
        return loader != null;
    }

    /*
       Utility Methods
     */

    public void save(String fieldName, Object value) throws IOException, IllegalAccessException {
        final ConfigurableField field = loader.getFieldsMap().get(fieldName);
        if(field == null)
            throw new IllegalArgumentException("No configurable field with name " + fieldName + " found!");

        field.field().set(this, value);
        field.save(this, value);

        yamlFile.save();
    }

    public void save(String fieldName) throws IOException, IllegalAccessException {
        final ConfigurableField field = loader.getFieldsMap().get(fieldName);
        if(field == null)
            throw new IllegalArgumentException("No configurable field with name " + fieldName + " found!");

        field.save(this);
        yamlFile.save();
    }

    public void saveAll() throws IOException, IllegalAccessException {
        for(ConfigurableField field : loader.getFields())
            field.save(this);

        yamlFile.save();
    }

    public void loadFromDisk() throws IOException, IllegalAccessException {
        yamlFile.createOrLoadWithComments();

        for(ConfigurableField field : loader.getFields())
            field.retrieve(this);

        yamlFile.save();
    }

    /*
        Getters
     */

    public YamlFile getYamlFile() {
        return yamlFile;
    }
}
