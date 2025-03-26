package net.vadamdev.dbk.framework.tuple;

import java.util.Map;

/**
 * @author VadamDev
 * @since 11/10/2024
 */
public record ImmutablePair<L, R>(L getLeft, R getRight) implements Pair<L, R> {
    private static final Pair<?, ?> EMPTY = ImmutablePair.of(null, null);
    public static <L, R> ImmutablePair<L, R> empty() {
        return (ImmutablePair<L, R>) EMPTY;
    }

    public static <L, R> ImmutablePair<L, R> of(final L left, final R right) {
        return new ImmutablePair<>(left, right);
    }

    public static <L, R> ImmutablePair<L, R> of(final Map.Entry<L, R> entry) {
        return new ImmutablePair<>(entry.getKey(), entry.getValue());
    }
}
