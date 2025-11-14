package net.vadamdev.dbk.framework.config.serializer;

/**
 * Allow serializing variables with an alternative method than automatic String serialization provided by the Simple-YAML library
 *
 * @author VadamDev
 * @since 15/10/2024
 */
public abstract class FieldSerializer<T> {
    private final Class<T> type;

    public FieldSerializer(Class<T> type) {
        this.type = type;
    }

    protected abstract String serialize(T t);
    protected abstract T unserialize(String data);

    public String serializeObj(Object object) {
        if(!object.getClass().isAssignableFrom(type))
            return null;

        return serialize((T) object);
    }

    public T unserializeObj(Object object) {
        return unserialize(object.toString());
    }
}
