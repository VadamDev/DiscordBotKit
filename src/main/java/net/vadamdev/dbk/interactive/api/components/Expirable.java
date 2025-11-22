package net.vadamdev.dbk.interactive.api.components;

import java.util.concurrent.TimeUnit;

/**
 * A {@link InteractiveComponent} implementing this interface will be able to be automatically unregistered once his lifetime is superior to his longevity
 *
 * @author VadamDev
 * @since 12/12/2024
 */
public interface Expirable {
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
