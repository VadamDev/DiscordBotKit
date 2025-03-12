package net.vadamdev.dbk.framework.config.serializer;

import net.vadamdev.dbk.framework.config.ConfigurationLoader;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Allow serializing variables with an alternative method than automatic String serialization provided by the Simple-YAML library
 * <br>A new instance of this class is created each time serialization is required
 *
 * @author VadamDev
 * @since 15/10/2024
 */
public abstract class ConfigurationSerializer<T> {
    private final Class<T> type;

    public ConfigurationSerializer(Class<T> type) {
        this.type = type;
    }

    public abstract String serialize(T t);
    public abstract T unserialize(String data);

    private String serializeObject(Object object) {
        if(!object.getClass().isAssignableFrom(type))
            return null;

        return serialize((T) object);
    }

    /*
       Utility Methods
     */

    @Nullable
    public static Object serializeField(Object input, @Nullable String serializerClass) {
        if(input == null)
            return null;

        if(serializerClass == null || serializerClass.isBlank())
            return input; //If no serializer where provided, we return the input

        return generateSerializerSafely(serializerClass)
                .map(serializer -> serializer.serializeObject(input))
                .orElse(null);
    }

    @Nullable
    public static Object unserializeField(Object input, @Nullable String serializerClass) {
        if(input == null)
            return null;

        if(serializerClass == null || serializerClass.isBlank())
            return input; //If no serializer where provided, we return the input

        return generateSerializerSafely(serializerClass)
                .map(serializer -> serializer.unserialize(input.toString()))
                .orElse(null);
    }

    private static Optional<ConfigurationSerializer<?>> generateSerializerSafely(String serializerClass) {
        try {
            final Class<?> clazz = Class.forName(serializerClass);
            if(!ConfigurationSerializer.class.isAssignableFrom(clazz))
                throw new ClassNotFoundException();

            return Optional.of(((Constructor<? extends ConfigurationSerializer<?>>) clazz.getConstructor()).newInstance());
        }catch (ClassNotFoundException e) {
            ConfigurationLoader.LOGGER.error("Failed to find serializer class at given path: " + serializerClass);
        }catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            ConfigurationLoader.LOGGER.error("Failed to find a valid constructor for: " + serializerClass);
        }

        return Optional.empty();
    }
}
