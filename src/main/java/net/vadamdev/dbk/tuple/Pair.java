package net.vadamdev.dbk.tuple;

/**
 * @author VadamDev
 * @since 11/10/2024
 */
public interface Pair<L, R> {
    L left();
    R right();

    default boolean isEmpty() {
        return left() == null && right() == null;
    }
}
