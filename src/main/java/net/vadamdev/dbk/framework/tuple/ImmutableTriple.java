package net.vadamdev.dbk.framework.tuple;

/**
 * @author VadamDev
 * @since 11/10/2024
 */
public record ImmutableTriple<L, M, R>(L left, M middle, R right) implements Triple<L, M, R> {
    private static final Triple<?, ?, ?> EMPTY = ImmutableTriple.of(null, null, null);
    public static <L, M, R> ImmutableTriple<L, M, R> empty() {
        return (ImmutableTriple<L, M, R>) EMPTY;
    }

    public static <L, M, R> ImmutableTriple<L, M, R> of(final L left, final M middle, final R right) {
        return new ImmutableTriple<>(left, middle, right);
    }
}
