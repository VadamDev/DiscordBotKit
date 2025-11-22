package net.vadamdev.dbk.tuple;

/**
 * @author VadamDev
 * @since 11/10/2024
 */
public interface Triple<L, M, R> extends Pair<L, R> {
    M middle();

    @Override
    default boolean isEmpty() {
        return middle() == null && Pair.super.isEmpty();
    }
}
