package chap6;

import chap2.Function;
import chap3.Supplier;
import chap5.List;
import static chap5.List.*;

public abstract class Option<A> {

    public abstract A getOrThrow();
    public abstract A getOrElse(Supplier<A> defaultValue);
    public abstract <B> Option<B> map(Function<A, B> func);
    public abstract boolean isSome();

    public <B> Option<B> flatMap(Function<A, Option<B>> func) {
        return map(func).getOrElse(Option::none);
    }

    public Option<A> orElse(Supplier<Option<A>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }

    public Option<A> filter(Function<A, Boolean> f) {
        return flatMap(x -> f.apply(x) ? some(x) : none());
    }

    private static Option none = new None();

    public static class None<A> extends Option<A> {

        private None() {
        }

        @Override
        public A getOrThrow() {
            throw new IllegalStateException("get called on none");
        }

        @Override
        public A getOrElse(Supplier<A> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public <B> Option<B> map(Function<A, B> func) {
            return none();
        }

        @Override
        public <B> Option<B> flatMap(Function<A, Option<B>> func) {
            return none();
        }

        @Override
        public String toString() {
            return "none";
        }

        @Override
        public boolean isSome() {
            return false;
        }
    }

    public static class Some<A> extends Option<A> {

        private A value;

        public Some(A value) {
            this.value = value;
        }

        @Override
        public A getOrThrow() {
            return value;
        }

        @Override
        public A getOrElse(Supplier<A> defaultValue) {
            return value;
        }

        @Override
        public <B> Option<B> map(Function<A, B> func) {
            return new Some<>(func.apply(value));
        }

        @Override
        public <B> Option<B> flatMap(Function<A, Option<B>> func) {
            return func.apply(value);
        }

        @Override
        public String toString() {
            return String.format("Some(%s)", value);
        }

        @Override
        public boolean isSome() {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public static <A> Option<A> none() {
        return none;
    }

    public static <A> Option<A> some(A value) {
        return new Some<>(value);
    }

    public static <A extends Comparable<A>> Function<List<A>, Option<A>> max() {
        return list -> list.isEmpty()
                ? none()
                : some(list.foldRight(list.head(), x -> y -> x.compareTo(y) > 0 ? x : y));
    }

    public static <A, B> Function<Option<A>, Option<B>> lift(Function<A, B> func) {
        return oa -> {
            try {
                return oa.map(func);
            } catch (Exception e) {
                return none();
            }
        };
    }

    public static <A, B, C> Option<C> map2(Option<A> oa, Option<B> ob, Function<A, Function<B, C>> func) {
        return oa.flatMap(a ->
                    ob.map(b ->
                        func.apply(a).apply(b)));
    }


    public static <A> Option<List<A>> sequence(List<Option<A>> list) {
        return list.isEmpty()
                ? some(List.list())
                : list.head().flatMap(oa -> sequence(list.tail()).map(la -> la.cons(oa)));
    }

    public static <A> Option<List<A>> sequence1(List<Option<A>> list) {
        return list.foldRight(some(List.list()), x -> y -> map2(x, y, a -> b -> b.cons(a)));
    }


    public static <A, B> Option<List<B>> traverse(List<A> list, Function<A, Option<B>> func) {
        return list.foldRight(some(list()), x -> y -> func.apply(x).flatMap(m -> y.map(n -> n.cons(m))));
    }

    public static <A, B> Option<List<A>> sequence2(List<Option<A>> list) {
        return traverse(list, Function.identity());
    }


}
