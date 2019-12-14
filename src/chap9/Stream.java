package chap9;

import chap2.Function;
import chap3.Suppllier;
import chap4.TailCall;
import chap5.List;
import chap6.Option;
import com.sun.org.apache.xpath.internal.operations.Bool;

import static chap4.TailCall.ret;
import static chap4.TailCall.sus;

public abstract class Stream<A> {

    private static final Stream EMPTY = new Empty();
    public abstract A head();
    public abstract Stream<A> tail();
    public abstract boolean isEmpty();
    public abstract Option<A> headOption();
    public abstract Stream<A> drop(int n);
    public abstract Stream<A> take(int n);
    public abstract Stream<A> takeWhile(Function<A, Boolean> func);

    private Stream() {};

    public List<A> toList() {
        return toList(this, List.list()).eval().reverse();
    }

    public TailCall<List<A>> toList(Stream<A> stream, List<A> acc) {
        return stream.isEmpty()
                ? ret(acc)
                : sus(() -> toList(stream.tail(), acc.cons(stream.head())));
    }

    public <B> B foldRight(Suppllier<B> identity, Function<A, Function<Suppllier<B>, Suppllier<B>>> func) {
        return isEmpty()
                ? identity.get()
                : func.apply(head()).apply(() -> tail().foldRight(identity, func)).get();
    }

    public List<A> toList1() {
        return foldRight(List::list, x -> y -> () -> y.get().cons(x));
    }

    public Stream<A> dropWhile(Function<A, Boolean> func) {
        return foldRight(Stream::empty, x -> y -> func.apply(x) ? y : () -> cons(() -> x, y));
    }

    public TailCall<Stream<A>> dropWhile(Function<A, Boolean> func, Stream<A> acc) {
        return isEmpty()
                ? ret(acc)
                : func.apply(acc.head())
                    ? sus(() -> dropWhile(func, acc.tail()))
                    : ret(acc);
    }

    public Stream<A> dropWhile1(Function<A, Boolean> func) {
        return dropWhile(func, this).eval();
    }

    public static class Empty<A> extends Stream<A> {
        @Override
        public A head() {
            throw new IllegalStateException("head called on Empty");
        }

        @Override
        public Stream<A> tail() {
            throw new IllegalStateException("tail called on Empty");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Option<A> headOption() {
            return Option.none();
        }

        @Override
        public Stream<A> drop(int n) {
            return this;
        }

        @Override
        public Stream<A> take(int n) {
            return this;
        }

        @Override
        public Stream<A> takeWhile(Function<A, Boolean> func) {
            return this;
        }
    }

    public static class Cons<A> extends Stream<A> {
        private final Suppllier<A> head;
        private A h;
        private final Suppllier<Stream<A>> tail;
        private Stream<A> t;

        public Cons(Suppllier<A> head, Suppllier<Stream<A>> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public A head() {
            if (h == null) {
                h = head.get();
            }
            return h;
        }

        @Override
        public Stream<A> tail() {
            if (t == null) {
                t = tail.get();
            }
            return t;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Option<A> headOption() {
            return Option.some(head());
        }

        @Override
        public Stream<A> drop(int n) {
            return n <= 0
                    ? tail()
                    : drop(n - 1);
        }

        @Override
        public Stream<A> take(int n) {
            return n <= 0
                    ? empty()
                    : cons(head, () -> tail().take(n - 1));
        }

        @Override
        public Stream<A> takeWhile(Function<A, Boolean> func) {
            return func.apply(head())
                    ? cons(head, () -> tail().takeWhile(func))
                    : empty();
        }

        public Stream<A> takeWhile1(Function<A, Boolean> func) {
            return foldRight(Stream::empty, x -> y -> func.apply(x) ? () -> cons(() -> x, y) : y);
        }
    }

    public static <A> Stream<A> cons(Suppllier<A> head, Suppllier<Stream<A>> tail) {
        return new Cons<>(head, tail);
    }

    public static <A> Stream<A> cons(Suppllier<A> head, Stream<A> tail) {
        return new Cons<>(head, () -> tail);
    }

    public static Stream<Integer> from(int n) {
        return new Cons<>(() -> n, () -> from(n));
    }

    @SuppressWarnings("unchecked")
    public static <A> Stream<A> empty() {
        return EMPTY;
    }
}
