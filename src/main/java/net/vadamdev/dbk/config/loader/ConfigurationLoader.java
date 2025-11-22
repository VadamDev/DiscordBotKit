package net.vadamdev.dbk.config.loader;

import net.vadamdev.dbk.config.Configuration;
import net.vadamdev.dbk.config.annotations.ConfigRange;
import net.vadamdev.dbk.config.annotations.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Used for loading {@link Configuration} classes
 *
 * @author VadamDev
 * @since 18/10/2022
 */
public class ConfigurationLoader {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoader.class);

    /*
       Helper
     */

    public static void loadConfiguration(Configuration configuration) throws IOException, IllegalAccessException {
        final ConfigurationLoader loader = new ConfigurationLoader(configuration.getClass());
        loader.discover();

        configuration.setLoader(loader);
        configuration.loadFromDisk();
    }

    public static void loadConfigurations(Configuration... configurations) throws IOException, IllegalAccessException {
        for(Configuration configuration : configurations)
            loadConfiguration(configuration);
    }

    /*
       ConfigurationLoader
     */

    private static final int INCOMPATIBILITY_MASK = Modifier.TRANSIENT | Modifier.FINAL | Modifier.STATIC;

    private final Class<? extends Configuration> clazz;

    private final Map<String, ConfigurableField> configurableFields;
    private boolean discovered;

    protected ConfigurationLoader(Class<? extends Configuration> clazz) {
        this.clazz = clazz;

        this.configurableFields = new HashMap<>();
        this.discovered = false;
    }

    protected void discover() {
        if(discovered)
            return;

        for(Field field : clazz.getDeclaredFields()) {
            if((field.getModifiers() & INCOMPATIBILITY_MASK) != 0)
                continue;

            final ConfigValue configValue = field.getAnnotation(ConfigValue.class);
            if(configValue == null)
                continue;

            ConfigValue.Value value;

            try {
                value = ConfigValue.Value.of(configValue);
            }catch (ClassNotFoundException e) {
                LOGGER.error("Failed to find serializer class for field: " + field.getName(), e);
                continue;
            }catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                LOGGER.error("Failed to instantiate serializer for field: " + field.getName(), e);
                continue;
            }

            field.setAccessible(true);

            configurableFields.put(field.getName(), new ConfigurableField(
                    field,
                    value,
                    ConfigRange.Range.of(field.getAnnotation(ConfigRange.class))
            ));
        }

        discovered = true;
    }

    public Collection<ConfigurableField> getFields() {
        return configurableFields.values();
    }

    public Map<String, ConfigurableField> getFieldsMap() {
        return configurableFields;
    }
}
