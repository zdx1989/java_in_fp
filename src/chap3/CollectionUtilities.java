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
}
