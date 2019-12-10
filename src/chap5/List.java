package chap5;

import chap2.Function;
import chap2.Tuple;
import chap4.TailCall;
import chap7.Result;
import com.sun.org.apache.xpath.internal.operations.Bool;

import static chap4.TailCall.*;

public abstract class List<T> {

    public abstract T head();
    public abstract List<T> tail();
    public abstract boolean isEmpty();
    public abstract List<T> setHead(T t);
    public abstract List<T> dropWhile(Function<T, Boolean> func);
    public abstract List<T> reverse();
    public abstract List<T> init();
    public abstract <U> U foldRight(U identity, Function<T, Function<U, U>> func);
    public abstract int lengthMemoized();
    public int length;

    @SuppressWarnings("rawtypes")
    public static final List NIL = new Nil();

    public List<T> cons(T t) {
        return new Cons<>(t, this);
    }

    public List<T> drop(int n) {
        return n < 0
                ? NIL
                : drop_(this, n).eval();
    }

    private TailCall<List<T>> drop_(List<T> acc, int n) {
        return n < 0 || acc.isEmpty()
                ? ret(acc)
                : sus(() -> drop_(acc.tail(), n - 1));
    }

    public <B> List<B> map(Function<T, B> func) {
        return foldRight(list(), x -> y -> y.cons(func.apply(x)));
    }

    public List<T> filter(Function<T, Boolean> func) {
        return foldRight(list(), x -> y -> func.apply(x) ? y.cons(x) : y);
    }

    public <B> List<B> flatMap(Function<T, List<B>> func) {
        return foldRight(list(), x -> y -> concat1(func.apply(x), y));
    }

    public List<T> filter1(Function<T, Boolean> func) {
        return flatMap(x -> func.apply(x) ? list(x) : list());
    }


    private List() {
    }

    public static class Nil<T> extends List<T> {
        public int length = 0;
        @Override
        public T head() {
           throw new IllegalStateException("head called an empty list");
        }

        @Override
        public List<T> tail() {
            throw new IllegalStateException("tail called an empty list");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public List<T> setHead(T t) {
            throw new IllegalStateException("setHead called an empty list");
        }

        @Override
        public List<T> dropWhile(Function<T, Boolean> func) {
            return this;
        }

        @Override
        public String toString() {
            return "[NIL]";
        }

        @Override
        public List<T> reverse() {
            return this;
        }

        @Override
        public List<T> init() {
            throw new IllegalStateException("init called an empty list");
        }

        @Override
        public <U> U foldRight(U identity, Function<T, Function<U, U>> func) {
            return identity;
        }

        @Override
        public int lengthMemoized() {
            return 0;
        }

        public Result<T> headOption() {
            return Result.empty();
        }

    }

    public static class Cons<T> extends List<T> {
        private T head;
        private List<T> tail;

        public Cons(T head, List<T> tail) {
            this.head = head;
            this.tail = tail;
            length = tail.length + 1;
        }

        @Override
        public T head() {
            return head;
        }

        @Override
        public List<T> tail() {
            return tail;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public List<T> setHead(T t) {
            return null;
        }

        @Override
        public List<T> dropWhile(Function<T, Boolean> func) {
            return dropWhile_(this, func).eval();
        }

        private TailCall<List<T>> dropWhile_(List<T> list, Function<T, Boolean> func) {
            return !list.isEmpty() && func.apply(head)
                    ? ret(tail)
                    : sus(() -> dropWhile_(tail, func));
        }

        @Override
        public String toString() {
            return String.format("[%sNIL]", toString(new StringBuilder(), this).eval());
        }

        private TailCall<String> toString(StringBuilder acc, List<T> list) {
            return list.isEmpty()
                    ? ret(acc.toString())
                    : sus(() -> toString(acc.append(list.head()).append(", "), list.tail()));
        }

        @Override
        public List<T> reverse() {
            return reverse_(this, List.list()).eval();
        }

        private TailCall<List<T>> reverse_(List<T> list, List<T> res) {
            return list.isEmpty()
                    ? ret(res)
                    : sus(() -> reverse_(list.tail(), res.setHead(list.head())));
        }

        @Override
        public List<T> init() {
            return this.reverse().tail().reverse();
        }

        @Override
        public <U> U foldRight(U identity, Function<T, Function<U, U>> func) {
            return foldRight_(identity, this.reverse(), identity, func).eval();
        }

        private <U> TailCall<U> foldRight_(U acc, List<T> list, U identity, Function<T, Function<U, U>> func) {
            return this.isEmpty()
                    ? ret(acc)
                    : sus(() -> foldRight_(func.apply(list.head()).apply(acc), list.tail(), identity, func));
        }

        @Override
        public int lengthMemoized() {
            return 0;
        }

        public Result<T> headOption() {
            return Result.success(head);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> list() {
        return NIL;
    }

    @SafeVarargs
    public static <T> List<T> list(T... t) {
        List<T> temp = list();
        for (int i = t.length - 1; i >= 0; i--) {
            temp = new Cons<>(t[i], temp);
        }
        return temp;
    }

    public static <T> List<T> setHead(List<T> list, T t) {
        return list.setHead(t);
    }

    public static <T> List<T> concat(List<T> list1, List<T> list2) {
        return list1.isEmpty()
                ? list2
                : new Cons<>(list1.head(), concat(list1.tail(), list2));
    }

    public static <T> List<T> concat1(List<T> list1, List<T> list2) {
        return concat_(list1.reverse(), list2).eval();
    }

    private static <T> TailCall<List<T>> concat_(List<T> list1, List<T> list2) {
        return list1.isEmpty()
                ? ret(list2)
                : sus(() -> concat_(list1.tail(), list2.setHead(list1.head())));
    }

    public static Integer sum(List<Integer> list) {
        return list.isEmpty()
                ? 0
                : list.head() + sum(list.tail());
    }

    public static Integer sum1(List<Integer> list) {
        return sum_(list, 0).eval();
    }

    private static TailCall<Integer> sum_(List<Integer> list, Integer res) {
        return list.isEmpty()
                ? ret(res)
                : sus(() -> sum_(list.tail(), res + list.head()));
    }

    public static Double product(List<Double> list) {
        return list.isEmpty()
                ? 1.0
                : list.head() * product(list.tail());
    }

    public static Double product1(List<Double> list) {
        return list.isEmpty()
                ? 1.0
                : list.head() == 0
                    ? 0
                    : list.head() * product1(list.tail());
    }

    public static <A, B> B foldRight(List<A> list, B identity, Function<A, Function<B, B>> func) {
        return list.isEmpty()
                ? identity
                : func.apply(list.head()).apply(foldRight(list.tail(), identity, func));
    }

    public static Integer sum2(List<Integer> list) {
        return foldRight(list, 0, x -> y -> x + y);
    }

    public static Double product2(List<Integer> list) {
        return foldRight(list, 1.0, x -> y -> x * y);
    }

    public static <A> List<A> concat2(List<A> list1, List<A> list2) {
        return foldRight(list2, list1, x -> y -> y.cons(x));
    }


    public static <A> int length(List<A> list) {
        return foldRight(list, 0, x -> y -> y + 1);
    }

    public static <A, B> B foldLeft(List<A> list, B identity, Function<B, Function<A, B>> func) {
        return foldLeft_(list, identity, func).eval();
    }

    private static <A, B> TailCall<B> foldLeft_(List<A> list, B identity, Function<B, Function<A, B>> func) {
        return list.isEmpty()
                ? ret(identity)
                : sus(() -> foldLeft_(list.tail(), identity, x -> y -> func.apply(x).apply(list.head())));
    }

    public static <A> int length1(List<A> list) {
        return foldLeft(list, 0, x -> y -> x + 1);
    }

    public static <A> List<A> concat3(List<A> list1, List<A> list2) {
        return foldLeft(list1, list2, x -> x::cons);
    }

    public static <A, B> B foldRight1(List<A> list, B identity, Function<A, Function<B, B>> func) {
        return foldLeft(list.reverse(), identity, x -> y -> func.apply(y).apply(x));
    }

    public static <A> List<A> flatten(List<List<A>> list) {
        return foldRight(list, List.list(), x -> y -> concat(x, y));
    }

    public static <A> List<A> flatten1(List<List<A>> list) {
        return list.flatMap(Function.identity());
    }

    public static List<String> doubleString(List<Double> list) {
        return foldRight(list, List.list(), x -> y -> y.cons(x.toString()));
    }

    public static <A> List<A> flattenResult(List<Result<A>> list) {
        return flattenResult_(list, list()).eval();
    }

    public static <A> TailCall<List<A>> flattenResult_(List<Result<A>> lra, List<A> la) {
        return lra.isEmpty()
                ? TailCall.ret(la)
                : lra.head().isSuccess()
                    ? TailCall.sus(() -> flattenResult_(lra.tail(), la.cons(lra.head().get())))
                    : TailCall.sus(() -> flattenResult_(lra.tail(), la));
    }

    public static <A> List<A> flattenResult1(List<Result<A>> list) {
        return list.foldRight(List.list(), ra -> la -> ra.map(la::cons).getOrElse(la));
    }

    public static <A> Result<List<A>> sequence(List<Result<A>> lra) {
        return lra.foldRight(Result.success(list()), ra -> rla -> Result.map2(ra, rla, a -> la -> la.cons(a)));
    }

    public static <A, B> Result<List<B>> traverse(List<Result<A>> lra, Function<A, B> func) {
        return lra.foldRight(Result.success(list()),
                ra -> rlb -> Result.map2(ra, rlb, a -> lb -> lb.cons(func.apply(a))));
    }

    public static <A> Result<List<A>> sequence1(List<Result<A>> lra) {
        return traverse(lra, Function.identity());
    }

    public static <A, B, C> List<C> zipWith(List<A> la, List<B> lb, Function<A, Function<B, C>> func) {
        return zipWith_(list(), la, lb, func).eval().reverse();
    }

    public static <A, B, C> TailCall<List<C>> zipWith_(List<C> acc, List<A> la, List<B> lb, Function<A, Function<B, C>> func) {
        return la.isEmpty() || la.isEmpty()
                ? ret(acc)
                : sus(() -> zipWith_(acc.cons(func.apply(la.head()).apply(lb.head())), la.tail(), lb.tail(), func));
    }

    public static <A, B, C> List<C> product(List<A> la, List<B> lb, Function<A, Function<B, C>> func) {
        return la.flatMap(a ->
                lb.map(b -> func.apply(a).apply(b)));
    }

    public static <A, B> Tuple<List<A>, List<B>> unzip(List<Tuple<A, B>> list) {
        return list.foldRight(new Tuple<>(list(), list()),
                tab -> tList -> new Tuple<>(tList._1.cons(tab._1), tList._2.cons(tab._2)));
    }

    public static <A> boolean hasSubsequence(List<A> list, List<A> subList) {
        return hasSubsequence_(list, subList).eval();
    }

    public static <A> TailCall<Boolean> hasSubsequence_(List<A> list, List<A> subList) {
        return subList.isEmpty()
                ? ret(true)
                : list.isEmpty()
                    ? ret(false)
                    : startWith(list, subList)
                        ? sus(() -> hasSubsequence_(list.tail(), subList))
                        : ret(false);
    }


    public static <A> boolean startWith(List<A> list, List<A> subList) {
        return startWith_(list, subList).eval();
    }

    public static <A> TailCall<Boolean> startWith_(List<A> list, List<A> subList) {
        return subList.isEmpty()
                ? ret(true)
                : list.isEmpty()
                    ? ret(false)
                    : list.head().equals(subList.head())
                        ? ret(false)
                        : sus(() -> startWith_(list.tail(), subList.tail()));
    }

    public static <A> boolean exist(List<A> list, Function<A, Boolean> func) {
        return func.apply(list.head()) || exist(list.tail(), func);
    }

    public static <A> boolean exist1(List<A> list, Function<A, Boolean> func) {
        return foldLeftZero_(list, true, true, x -> y -> x || func.apply(y)).eval();
    }

    public static <A, B> B foldLeftZero(List<A> list, B identity, B zero, Function<B, Function<A, B>> func) {
        return foldLeftZero_(list, identity, zero, func).eval();
    }

    public static <A, B> TailCall<B> foldLeftZero_(List<A> list, B acc, B zero,
                                                   Function<B, Function<A, B>> func) {
       return list.isEmpty() || acc.equals(zero)
                ? ret(acc)
                : sus(() -> foldLeftZero_(list.tail(), func.apply(acc).apply(list.head()), zero, func));
    }
 }
