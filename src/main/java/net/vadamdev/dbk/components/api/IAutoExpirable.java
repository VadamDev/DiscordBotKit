package net.vadamdev.dbk.components.api;

import java.util.concurrent.TimeUnit;

/**
 * A {@link SmartComponent} implementing this interface will be able to be automatically unregistered once his lifetime is superior to his longevity
 *
 * @author VadamDev
 * @since 20/03/2026
 */
public interface IAutoExpirable extends Invalidatable {
    long DEFAULT_LONGEVITY = TimeUnit.MINUTES.toMillis(60); //1-hour lifetime by default, it can be modified by overriding getLongevity()

    /**
     * Determines the duration since the component has been created and registered.
     *
     * @return The duration since the component has been created and registered in milliseconds
     */
    long getLifetimeMs();

    /**
     * Determines the longevity (= max lifetime) of the component
     *
     * @return A duration in milliseconds
     */
    default long getLongevity() {
        return DEFAULT_LONGEVITY;
    }

    /**
     * Determines if the component can be expired, returning false here will prevent the component from being unregistered if its lifetime is superior to his longevity
     *
     * @return False, if the component cannot be expired
     */
    default boolean canExpire() {
        return getLongevity() > 0;
    }
}
