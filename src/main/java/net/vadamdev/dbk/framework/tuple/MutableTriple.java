package net.vadamdev.dbk.framework.tuple;

/**
 * @author VadamDev
 * @since 11/10/2024
 */
public final class MutableTriple<L, M, R> implements Triple<L, M, R> {
    public static <L, M, R> MutableTriple<L, M, R> of(L left, M middle, R right) {
        return new MutableTriple<>(left, middle, right);
    }

    public L left;
    public M middle;
    public R right;

    private MutableTriple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    @Override
    public L left() {
        return left;
    }

    @Override
    public M middle() {
        return middle;
    }

    @Override
    public R right() {
        return right;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public void setMiddle(M middle) {
        this.middle = middle;
    }

    public void setRight(R right) {
        this.right = right;
    }
}
