package net.vadamdev.dbk.config.loader;

import net.vadamdev.dbk.config.Configuration;
import net.vadamdev.dbk.config.annotations.ConfigRange;
import net.vadamdev.dbk.config.annotations.ConfigValue;
import net.vadamdev.dbk.config.serializer.SerializerAccessor;
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

        Object obj;
        if(yamlFile.isSet(pathToData)) {
            //Exists in config, unserialize and set the field
            obj = serializer.unserialize(yamlFile.get(pathToData));
            if(range != null)
                obj = range.clampInput(obj);

            field.set(config, obj);
        }else {
            //Doesn't exist yet, serialize the default value and set it in the yaml file
            obj = field.get(config);
            if(obj == null)
                return null; //TODO: log error?

            yamlFile.addDefault(pathToData, serializer.serialize(obj));

            final String comment = formatComment(value().comment());
            if(comment != null)
                yamlFile.setComment(pathToData, comment);
        }

        return obj;
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
    private String formatComment(@Nullable String comment) {
        if(comment == null && range == null)
            return null;

        final StringBuilder bakedComment = new StringBuilder();

        if(comment != null)
            bakedComment.append(comment);

        if(range != null)
            bakedComment.append("\nRange: " + range.min() + " ~ " + range.max());

        return !bakedComment.isEmpty() ? bakedComment.toString() : null;
    }
}
