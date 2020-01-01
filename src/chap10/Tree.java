package chap10;

import chap2.Function;
import chap5.List;
import chap6.Option;

public abstract class Tree<A extends Comparable<A>> {

    @SuppressWarnings("rawtypes")
    public static Tree EMPTY = new Empty();

    public abstract A value();

    abstract Tree<A> left();

    abstract Tree<A> right();

    abstract Tree<A> insert(A value);

    abstract boolean member(A value);

    abstract int size();

    abstract int height();

    abstract Option<A> max();

    abstract Option<A> min();

    abstract boolean isEmpty();

    abstract Tree<A> removeMerge(Tree<A> ta);

    abstract Tree<A> remove(A ra);

    abstract Tree<A> merge(Tree<A> ta);

    abstract <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g);

    abstract <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f);

    abstract <B> B foldPreOrder(B identity, Function<A, Function<B,Function<B, B>>> f);

    abstract <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f);

    abstract Tree<A> rotateLeft();

    abstract Tree<A> rotateRight();

    abstract List<A> toListInOrderRight();

    private static class Empty<A extends Comparable<A>> extends Tree<A> {
        @Override
        public A value() {
            throw new IllegalStateException("value() called on empty");
        }

        @Override
        Tree<A> left() {
            throw  new IllegalStateException("left() called on empty");
        }

        @Override
        Tree<A> right() {
            throw new IllegalStateException("right() called on empty");
        }

        @Override
        Tree<A> insert(A value) {
            return new T<>(empty(), value, empty());
        }

        @Override
        boolean member(A value) {
            return false;
        }

        @Override
        int size() {
            return 0;
        }

        @Override
        int height() {
            return -1;
        }

        @Override
        Option<A> max() {
            return Option.none();
        }

        @Override
        Option<A> min() {
            return Option.none();
        }

        @Override
        boolean isEmpty() {
            return true;
        }

        @Override
        Tree<A> removeMerge(Tree<A> ta) {
            return ta;
        }

        @Override
        Tree<A> remove(A ra) {
            return this;
        }

        @Override
        Tree<A> merge(Tree<A> ta) {
            return ta;
        }

        @Override
        <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g) {
            return identity;
        }

        @Override
        <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return identity;
        }

        @Override
        <B> B foldPreOrder(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return identity;
        }

        @Override
        <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return identity;
        }

        @Override
        Tree<A> rotateLeft() {
            return this;
        }

        @Override
        Tree<A> rotateRight() {
            return this;
        }

        @Override
        List<A> toListInOrderRight() {
            return List.list();
        }
    }

    private static class T<A extends Comparable<A>> extends Tree<A> {
        private final A value;
        private final Tree<A> left;
        private final Tree<A> right;

        public T(Tree<A> left, A value, Tree<A> right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        @Override
        public A value() {
            return value;
        }

        @Override
        Tree<A> left() {
            return left;
        }

        @Override
        Tree<A> right() {
            return right;
        }

        @Override
        Tree<A> insert(A insertValue) {
            return insertValue.compareTo(value) < 0
                    ? new T<>(left.insert(insertValue), value, right)
                    : insertValue.compareTo(value) > 0
                        ? new T<>(left, value, right.insert(insertValue))
                        : new T<>(left, insertValue, right);
        }

        @Override
        boolean member(A a) {
            return a.compareTo(value) == 0 || (a.compareTo(value) < 0
                    ? left.member(a)
                    : right.member(a));
        }

        @Override
        int size() {
            return 1 + left.size() + right.size();
        }

        @Override
        int height() {
            return 1 + Math.max(left.height(), right.height());
        }

        @Override
        Option<A> max() {
            return right.max().orElse(() -> Option.some(value));
        }

        @Override
        Option<A> min() {
            return left.min().orElse(() -> Option.some(value));
        }

        @Override
        boolean isEmpty() {
            return false;
        }

        @Override
        Tree<A> removeMerge(Tree<A> ta) {
            if (ta.isEmpty()) return this;
            if (ta.value().compareTo(value) < 0)
                return new T<>(left.removeMerge(ta), value, right);
            else if (ta.value().compareTo(value) > 0)
                return new T<>(left, value, right.removeMerge(ta));
            throw new IllegalStateException("error here");
        }

        @Override
        Tree<A> remove(A ra) {
            return ra.compareTo(value) < 0
                    ? new T<>(left.remove(ra), value, right)
                    : ra.compareTo(value) > 0
                        ? new T<>(left, value, right.remove(ra))
                        : left.removeMerge(right);
        }

        @Override
        Tree<A> merge(Tree<A> ta) {
            return ta.isEmpty()
                    ? this
                    : ta.value().compareTo(value) < 0
                        ? new T<>(left.merge(new T<>(ta.left(), ta.value(), empty())), value, right).merge(ta.right())
                        : ta.value().compareTo(value) > 0
                            ? new T<>(right, value, right.merge(new T<>(empty(), ta.value(), ta.right()))).merge(ta.left())
                            : new T<>(left.merge(ta.left()), value, right.merge(ta.right()));
        }

        @Override
        <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g) {
            return g.apply(left.foldLeft(identity, f, g)).apply(f.apply(right.foldLeft(identity, f, g)).apply(value));
        }

        //left -> value -> right
        @Override
        <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return f.apply(left.foldInOrder(identity, f)).apply(value).apply(right.foldInOrder(identity, f));
        }

        //value -> left -> right
        @Override
        <B> B foldPreOrder(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return f.apply(value).apply(left.foldPreOrder(identity, f)).apply(right.foldPreOrder(identity, f));
        }

        //left -> right -> value
        @Override
        <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return f.apply(left.foldPostOrder(identity, f)).apply(right.foldPostOrder(identity, f)).apply(value);
        }

        @Override
        Tree<A> rotateLeft() {
            return right.isEmpty()
                    ? this
                    : new T<>(new T<>(left, value, right.left()), right.value(), right.right());
        }

        @Override
        Tree<A> rotateRight() {
            return left.isEmpty()
                    ? this
                    : new T<>(left.left(), left.value(), new T<>(left.right(), value, right));
        }

        @Override
        List<A> toListInOrderRight() {
            return List.concat(List.concat(left.toListInOrderRight(), List.list(value)), right.toListInOrderRight());
        }
    }

    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Tree<A> empty() {
        return EMPTY;
    }

    public static <A extends Comparable<A>> Tree<A> tree(A... as) {
        return tree(List.list(as));
    }

    public static <A extends Comparable<A>> Tree<A> tree(List<A> la) {
        return la.foldRight(empty(), x -> y -> y.insert(x));
    }
}
