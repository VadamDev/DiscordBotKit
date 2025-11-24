package net.vadamdev.dbk.utils;

/**
 * @author VadamDev
 * @since 24/11/2025
 */
@FunctionalInterface
public interface Callable extends Runnable {
    default Callable andThen(Callable next) {
        return () -> { run(); next.run(); };
    }
}
