package net.vadamdev.dbk.interactive.api.registry;

/**
 * Represents an entity that will be registered in a service later in time.
 * Its JDA equivalent can, however, be accessed immediately using the get() method
 *
 * @author VadamDev
 * @since 03/03/2025
 */
public interface DeferredRegister<T, U> {
    void register(U data);
    boolean isRegistered();

    T get();
}
