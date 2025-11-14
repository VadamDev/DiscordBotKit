package net.vadamdev.dbk.framework.config.loader;

import net.vadamdev.dbk.framework.config.Configuration;
import net.vadamdev.dbk.framework.config.annotations.ConfigRange;
import net.vadamdev.dbk.framework.config.annotations.ConfigValue;
import net.vadamdev.dbk.framework.config.serializer.ILengthHolder;
import net.vadamdev.dbk.framework.config.serializer.SerializerAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlFile;

import java.lang.reflect.Field;

/**
 * @author VadamDev
 * @since 28/10/2025
 */
public record ConfigurableField(Field field, ConfigValue.Value value, @Nullable ConfigRange.Range range) {
    @Nullable
    public Object retrieve(Configuration config) throws IllegalArgumentException, IllegalAccessException {
        final YamlFile yamlFile = config.getYamlFile();
        final SerializerAccessor serializer = value.serializer();

        final String pathToData = value.path();

        Object value;
        if(yamlFile.isSet(pathToData)) {
            //Unserialize, correct range if needed
            value = serializer.unserialize(yamlFile.get(pathToData));
            if(range != null)
                value = range.clampInput(value);

            field.set(config, value);
        }else {
            //Serialize, if result is null, print error
            value = serializer.serialize(field.get(config));
            if(value == null)
                return null; //TODO: log error?

            //Set the default value in the yaml file
            yamlFile.addDefault(pathToData, value);

            //Add Comments
            final String comment = this.value.comment();
            if(comment != null)
                yamlFile.setComment(pathToData, formatComment(comment));
        }

        return value;
    }

    public void save(Configuration config, Object value) {
        final YamlFile yamlFile = config.getYamlFile();
        yamlFile.set(this.value.path(), this.value.serializer().serialize(value));
    }

    public void save(Configuration config) throws IllegalAccessException {
        save(config, field.get(config));
    }

    /*
       Comments
     */

    @Nullable
    private String formatComment(@NotNull String comment) {
        final StringBuilder bakedComment = new StringBuilder(comment);

        if(range != null)
            bakedComment.append("\nRange: " + range.min() + " ~ " + range.max());

        return !bakedComment.isEmpty() ? bakedComment.toString() : null;
    }
}
