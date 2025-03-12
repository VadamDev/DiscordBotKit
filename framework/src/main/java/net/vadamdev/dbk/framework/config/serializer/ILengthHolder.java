package net.vadamdev.dbk.framework.config.serializer;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that can have a certain length or size, such as the age of a Cat.
 * <br>
 * It can be paired with {@link net.vadamdev.dbk.framework.config.annotations.ConfigRange ConfigRange} in a {@link net.vadamdev.dbk.framework.config.Configuration Configuration} class
 *
 * @author VadamDev
 * @since 16/10/2024
 */
public interface ILengthHolder {
    /**
     * Return the length of the object, used to be compared with the MIN and MAX value of a {@link net.vadamdev.dbk.framework.config.annotations.ConfigValue ConfigValue}
     *
     * @return A double representing the length of the object
     */
    double length();

    /**
     * Called when the length needs to be clamped
     *
     * @param min The minimum length of the configurable field
     * @param max The maximum length of the configurable field
     *
     * @return A new (or self) clamped object
     */
    @NotNull
    ILengthHolder clampLength(double min, double max);
}
