package net.vadamdev.dbk.config.annotations;

import net.vadamdev.dbk.config.Configuration;
import net.vadamdev.dbk.config.serializer.ILengthHolder;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;

/**
 * Can be used in a {@link Configuration} class to specify the minimum and maximum range for a configurable value
 * <br>Currently compatible with classes extending {@link Number} or {@link ILengthHolder ILengthHolder}, and the {@link String} class
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

    /*
       Record
     */

    record Range(double min, double max) {
        @Nullable
        public static Range of(@Nullable ConfigRange range) {
            return range != null ? new Range(range.min(), range.max()) : null;
        }

        public Object clampInput(@Nullable Object input) throws IllegalArgumentException {
            if(input == null)
                return null;

            final double length = switch(input) {
                case Number number -> number.doubleValue();
                case String str -> str.length();
                case ILengthHolder holder -> holder.length();
                default -> throw new IllegalArgumentException("Failed to retrieve length for class: " + input.getClass().getName());
            };

            if(length >= min && length <= max)
                return input;

            return switch(input) {
                case String str -> str.substring((int) min, (int) max);
                case Number number -> switch(number) {
                    case Integer i -> Math.clamp(i, (int) min, (int) max);
                    case Double d -> Math.clamp(d, min, max);
                    case Float f -> Math.clamp(f, (float) min, (float) max);
                    case Long l -> Math.clamp(l, (long) min, (long) max);
                    case Short s -> (short) Math.clamp(s, (int) min, (int) max);
                    case Byte b -> (byte) Math.clamp(b, (int) min, (int) max);

                    default -> throw new IllegalArgumentException("Unsupported Number type: " + number.getClass().getName());
                };
                case ILengthHolder holder -> holder.clampLength(min, max);
                default -> input;
            };
        }
    }
}
