package chap9;

import chap2.Function;
import chap2.Tuple;
import chap3.Result;
import chap3.Supplier;
import chap4.TailCall;
import chap5.List;
import chap6.Option;


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

    public <B> B foldRight(Supplier<B> identity, Function<A, Function<Supplier<B>, B>> func) {
        return isEmpty()
                ? identity.get()
                : func.apply(head()).apply(() -> tail().foldRight(identity, func));
    }

    public List<A> toList1() {
        return foldRight(List::list, x -> y -> y.get().cons(x));
    }

    public Stream<A> dropWhile(Function<A, Boolean> func) {
        return foldRight(Stream::empty, x -> y -> func.apply(x) ? y.get() : cons(() -> x, y));
    }

    public Stream<A> takeWhile2(Function<A, Boolean> func) {
        return foldRight(Stream::empty, x -> y -> func.apply(x) ? cons(() -> x, y) : y.get());
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

    public boolean exist(Function<A, Boolean> func) {
        return func.apply(head()) || tail().exist(func);
    }

    public boolean exist1(Function<A, Boolean> func) {
        return foldRight(() -> false, x -> y -> func.apply(x) ? true : y.get());
    }

    public  Option<A> headOption1() {
        return foldRight(Option::none, x -> y -> Option.some(x));
    }

    public <B> Stream<B> map(Function<A, B> func) {
        return foldRight(Stream::empty, x -> y -> cons(() -> func.apply(x), y));
    }

    public Stream<A> filter(Function<A, Boolean> func) {
        return foldRight(Stream::empty, x -> y -> func.apply(x) ? cons(() -> x, y) : y.get());
    }

    public Stream<A> append(Supplier<Stream<A>> sa) {
        return foldRight(sa, x -> y -> cons(() -> x, y));
    }

    public <B> Stream<B> flatMap(Function<A, Stream<B>> func) {
        return foldRight(Stream::empty, x -> y -> func.apply(x).append(y));
    }

    public Option<A> find(Function<A, Boolean> func) {
        return filter(func).headOption();
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
        private final Supplier<A> head;
        private A h;
        private final Supplier<Stream<A>> tail;
        private Stream<A> t;

        public Cons(Supplier<A> head, Supplier<Stream<A>> tail) {
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
            return foldRight(Stream::empty, x -> y -> func.apply(x) ? cons(() -> x, y) : y.get());
        }
    }

    public static <A> Stream<A> cons(Supplier<A> head, Supplier<Stream<A>> tail) {
        return new Cons<>(head, tail);
    }

    public static <A> Stream<A> cons(Supplier<A> head, Stream<A> tail) {
        return new Cons<>(head, () -> tail);
    }

    public static Stream<Integer> from(int n) {
        return new Cons<>(() -> n, () -> from(n));
    }

    @SuppressWarnings("unchecked")
    public static <A> Stream<A> empty() {
        return EMPTY;
    }

    public static <A> Stream<A> repeat(A a) {
        return cons(() -> a, repeat(a));
    }

    public static <A> Stream<A> iterate(A seed, Function<A, A> func) {
        return cons(() -> seed, () -> iterate(func.apply(seed), func));
    }

    public static <A> Stream<A> repeat1(A a) {
        return iterate(a, Function.identity());
    }

    public static  Stream<Integer> from(Integer i) {
        return iterate(i, x -> x + 1);
    }

    public static Stream<Integer> fibs() {
        return iterate(new Tuple<>(0, 1), x -> new Tuple<>(x._2, x._1 + x._2)).map(x -> x._1);
    }

    public static <A, S> Stream<A> unfold(S s, Function<S, Option<Tuple<A, S>>> f) {
        return f.apply(s).map(t -> cons(() -> t._1, () -> unfold(t._2, f))).getOrElse(Stream::empty);
    }

}
