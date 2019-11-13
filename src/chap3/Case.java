package chap3;

import chap2.Tuple;

import java.util.function.Supplier;

/**
 * Created by zhoudunxiong on 2019/11/13.
 */
public class Case<T>  extends Tuple<Supplier<Boolean>, Supplier<Result<T>>> {

    private Case(Supplier<Boolean> condition, Supplier<Result<T>> result) {
        super(condition, result);
    }

    public static <T> Case<T> mcase(Supplier<Boolean> condition, Supplier<Result<T>> result) {
        return new Case<>(condition, result);
    }

    public static <T> DefaultCase<T> mcase(Supplier<Result<T>> result) {
        return new DefaultCase<>(() -> true, result);
    }

    private static class DefaultCase<T> extends Case<T> {
        public DefaultCase(Supplier<Boolean> condition, Supplier<Result<T>> result) {
            super(condition, result);
        }
    }

    @SafeVarargs
    public static <T> Result<T> match(DefaultCase<T> defaultCase, Case<T>... matchers) {
        for (Case<T> matcher : matchers) {
            if (matcher._1.get()) return matcher._2.get();
        }
        return defaultCase._2.get();
    }
}
