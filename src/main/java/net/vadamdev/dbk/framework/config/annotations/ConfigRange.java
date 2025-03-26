package net.vadamdev.dbk.framework.config.annotations;

import net.vadamdev.dbk.framework.config.Configuration;

import java.lang.annotation.*;

/**
 * Used in a {@link Configuration} class to specify the minimum and maximum range for an input
 * <br>Currently compatible with classes extending {@link Number} or {@link net.vadamdev.dbk.framework.config.serializer.ILengthHolder ILengthHolder}, and the {@link String} class
 *
 * @author VadamDev
 * @see Configuration
 * @since 30/06/2024
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigRange {
    /**
     * Define the minimum value
     *
     * @return A double representing the minimum length of the configurable value
     */
    double min() default Integer.MIN_VALUE;

    /**
     * Define the maximum value
     *
     * @return A double representing the maximum length of the configurable value
     */
    double max() default Integer.MAX_VALUE;

    /**
     * Defines if the read value should be clamped to the nearest limit instead of throwing an error
     *
     * @return True, if the value should be clamped
     */
    boolean setToNearestLimit() default true;
}
