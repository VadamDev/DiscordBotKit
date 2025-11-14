package net.vadamdev.dbk.framework.config.annotations;

import net.vadamdev.dbk.framework.config.Configuration;
import net.vadamdev.dbk.framework.config.serializer.FieldSerializer;
import net.vadamdev.dbk.framework.config.serializer.SerializerAccessor;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Used in a {@link Configuration} class to make a field configurable
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
     * Define the path to a {@link FieldSerializer FieldSerializer} class
     * used to have a way to serialize non-primitive classes
     *
     * @return The path to the serializer class
     */
    String serializer() default "";

    /*
       Record
     */

    record Value(String path, @Nullable String comment, SerializerAccessor serializer) {
        public static Value of(ConfigValue value) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            String comment = value.comment();
            if(comment.isBlank())
                comment = null;

            String serializer = value.serializer();
            if(serializer.isBlank())
                serializer = null;

            return new Value(value.path(), comment, new SerializerAccessor(serializer));
        }
    }
}
