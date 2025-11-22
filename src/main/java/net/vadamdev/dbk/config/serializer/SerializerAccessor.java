package net.vadamdev.dbk.config.serializer;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * @author VadamDev
 * @since 29/10/2025
 */
public class SerializerAccessor {
    @Nullable
    private final FieldSerializer<?> serializer;

    public SerializerAccessor(@Nullable String serializerClassPath) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(serializerClassPath == null)
            serializer = null;
        else {
            final Class<FieldSerializer<?>> serializerClass = (Class<FieldSerializer<?>>) Class.forName(serializerClassPath);
            serializer = serializerClass.getConstructor().newInstance();
        }
    }

    @Nullable
    public Object serialize(@Nullable Object input) {
        if(input == null)
            return null;

        if(serializer == null)
            return input.toString();

        return serializer.serializeObj(input);
    }

    @Nullable
    public Object unserialize(@Nullable Object input) {
        if(input == null)
            return null;

        if(serializer == null)
            return input;

        return serializer.unserializeObj(input.toString());
    }
}
