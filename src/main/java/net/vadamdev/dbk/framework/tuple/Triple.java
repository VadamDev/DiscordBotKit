package net.vadamdev.dbk.framework.tuple;

/**
 * @author VadamDev
 * @since 11/10/2024
 */
public interface Triple<L, M, R> extends Pair<L, R> {
    M getMiddle();

    @Override
    default boolean isEmpty() {
        return getMiddle() == null && Pair.super.isEmpty();
    }
}
