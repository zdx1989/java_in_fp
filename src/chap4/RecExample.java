package chap4;

import chap2.Function;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static chap3.CollectionUtilities.*;
import static chap4.TailCall.*;

public class RecExample {

    public static void main(String[] args) {
        List<Integer> test = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        TailCall<Integer> res = sumTail(test, 0);
        System.out.println(res.eval());

        System.out.println(fib1(10000));

        System.out.println(foldLeft1(test, 0, x -> y -> x + y));

        System.out.println(range1(1, 9));

    }

    static Integer sum(List<Integer> list) {
        if (list.isEmpty()) return 0;
        else return sum(tail(list)) + head(list);
    }

    static Integer sum1(List<Integer> list) {
        return sum_(list, 0);
    }

    static Integer sum_(List<Integer> list, Integer acc) {
        return list.isEmpty()
                ? acc
                : sum_(tail(list), acc + head(list));
    }

    static TailCall<Integer> sumTail(List<Integer> list, Integer acc) {
        return list.isEmpty()
                ? new TailCall.Return<>(acc)
                : new TailCall.Suspend<>(() -> sumTail(tail(list), acc + head(list)));
    }

    static BigInteger fib_(BigInteger acc1, BigInteger acc2, BigInteger i) {
        if (i.equals(BigInteger.ZERO)) return BigInteger.ZERO;
        else if (i.equals(BigInteger.ONE)) return acc1.add(acc2);
        else return fib_(acc2, acc1.add(acc2), i.subtract(BigInteger.ONE));
    }

    static BigInteger fib(int i) {
        return fib_(BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(i));
    }

    static TailCall<BigInteger> fibTail(BigInteger acc1, BigInteger acc2, BigInteger x) {
        if (x.equals(BigInteger.ZERO)) return ret(BigInteger.ZERO);
        else if (x.equals(BigInteger.ONE)) return ret(acc1.add(acc2));
        else return sus(() -> fibTail(acc2, acc1.add(acc2), x.subtract(BigInteger.ONE)));
    }

    static BigInteger fib1(int i) {
        return fibTail(BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(i)).eval();
    }

    static <T, U> U foldLeft(List<T> list, U u, Function<U, Function<T, U>> func) {
        return list.isEmpty()
                ? u
                : foldLeft(tail(list), func.apply(u).apply(head(tail(list))), func);
    }

    static <T, U> TailCall<U> foldLeftTail(List<T> list, U u, Function<U, Function<T, U>> func) {
        return list.isEmpty()
                ? ret(u)
                : sus(() -> foldLeftTail(tail(list), func.apply(u).apply(head(list)), func));
    }

    static <T, U> U foldLeft1(List<T> list, U u, Function<U, Function<T, U>> func) {
        return foldLeftTail(list, u, func).eval();
    }

    static List<Integer> range0(int start, int end) {
        return end <= start
                ? list()
                : prepend(range0(start + 1, end), start);
    }

    static List<Integer> range(List<Integer> acc, int start, int end) {
        return end <= start
                ? acc
                : range(append(acc, start), start + 1, end);
    }

    static TailCall<List<Integer>> range_(List<Integer> acc, int start, int end) {
        return end <= start
                ? ret(acc)
                : sus(() -> range_(append(acc, start), start + 1, end));
    }

    static List<Integer> range1(int start, int end) {
        return range_(list(), start, end).eval();
    }
}
