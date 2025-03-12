package net.vadamdev.dbk.framework.tuple;

import java.util.Map;

/**
 * @author VadamDev
 * @since 11/10/2024
 */
public final class MutablePair<L, R> implements Pair<L, R> {
    public static <L, R> MutablePair<L, R> of(L left, R right) {
        return new MutablePair<>(left, right);
    }

    public static <L, R> MutablePair<L, R> of(Map.Entry<L, R> entry) {
        return new MutablePair<>(entry.getKey(), entry.getValue());
    }

    private L left;
    private R right;

    private MutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public R getRight() {
        return right;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public void setRight(R right) {
        this.right = right;
    }
}
