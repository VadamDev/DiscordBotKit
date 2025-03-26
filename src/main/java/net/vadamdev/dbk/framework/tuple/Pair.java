package net.vadamdev.dbk.framework.tuple;

/**
 * @author VadamDev
 * @since 11/10/2024
 */
public interface Pair<L, R> {
    L getLeft();
    R getRight();

    default boolean isEmpty() {
        return getLeft() == null && getRight() == null;
    }
}
