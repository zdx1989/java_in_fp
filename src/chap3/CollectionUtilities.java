package chap3;

import chap2.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhoudunxiong on 2019/11/3.
 */
public class CollectionUtilities {


    public static <T> List<T> list() {
        return Collections.emptyList();
    }

    public static <T> List<T> list(T t) {
        return Collections.singletonList(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> list(List<T> s) {
        return Collections.unmodifiableList(new ArrayList(s));
    }


    @SafeVarargs
    public static <T> List<T> list(T... t) {
        return Collections.unmodifiableList(Arrays.asList(t));
    }

    private static <T> List<T> copy(List<T> lt) {
        return new ArrayList<T>(lt);
    }

    public static <T> T head(List<T> lt) {
        if (lt.isEmpty()) throw new IllegalArgumentException("head of empty list");
        else return lt.get(0);
    }

    public static <T> List<T> tail(List<T> lt) {
        if (lt.isEmpty())
            throw new IllegalArgumentException("tail of empty list");
        List<T> workList = copy(lt);
        workList.remove(0);
        return Collections.unmodifiableList(workList);
    }

    public static <T> List<T> append(List<T> lt, T t) {
        List<T> workList = copy(lt);
        workList.add(t);
        return Collections.unmodifiableList(workList);
    }

    public static Integer fold(List<Integer> is, Integer identity,
                               Function<Integer, Function<Integer, Integer>> f) {
        int res = identity;
        for (Integer i: is) {
            res = f.apply(res).apply(i);
        }
        return res;
    }

    public static <T, U> U foldLeft(List<T> lt, U identity, Function<U, Function<T, U>> f) {
        U res = identity;
        for (T t: lt) {
            res = f.apply(res).apply(t);
        }
        return res;
    }

    public static <T, U> U foldRight(List<T> lt, U identity, Function<T, Function<U, U>> f) {
        U res = identity;
        for (int i = lt.size() - 1; i >= 0; i--) {
            res = f.apply(lt.get(i)).apply(res);
        }
        return res;
    }

    public static <T, U> U foldRightRecur(List<T> lt, U identity, Function<T, Function<U, U>> f) {
        return list().isEmpty()?
                identity:
                f.apply(head(lt)).apply(foldRightRecur(tail(lt), identity, f));
    }


    public static <T> List<T> prepend(List<T> list, T t) {
        return foldLeft(list, list(t), a -> b -> append(a, b));
    }


    public static <T> List<T> reverse(List<T> list) {
        return foldLeft(list, list(), a -> b -> prepend(a, b));
    }

    public static <T, U> List<U> mapLeft(List<T> list, Function<T, U> f) {
        return foldRight(list, list(), b -> a -> prepend(a, f.apply(b)));
    }

    public static <T, U> List<U> mapRight(List<T> list, Function<T, U> f) {
        return foldLeft(list, list(), a -> b -> append(a, f.apply(b)));
    }

    public List<Integer> range0(int start, int end) {
        List<Integer> result = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            result.add(i);
        }
        return result;
    }

    public List<Integer> range1(int start, int end) {
        List<Integer> result = new ArrayList<>();
        int i = start;
        while (i <= end) {
            result.add(i);
            i++;
        }
        return result;
    }

    public static <T> List<T> unfold(T seed, Function<T, T> f, Function<T, Boolean> p) {
        List<T> result = new ArrayList<>();
        T temp = seed;
        while (p.apply(temp)) {
            append(result, temp);
            temp = f.apply(temp);
        }
        return result;
    }

    public static List<Integer> range(int start, int end) {
        return unfold(start, i -> i + 1, i -> i < end);
    }

    public static List<Integer> range2(int start, int end) {
        return end <= start
                ? CollectionUtilities.list()
                : CollectionUtilities.prepend(range(start + 1, end), start);
    }



}
