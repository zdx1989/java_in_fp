package chap2;

/**
 * Created by zhoudunxiong on 2019/11/2.
 */
public interface Function<T, U> {

    U apply(T t);

    default <V> Function<V, U> compose(Function<V, T> f) {
        return v -> apply(f.apply(v));
    }

    default <V> Function<T, V> andThen(Function<U, V> g) {
        return t -> g.apply(apply(t));
    }

    static <T> Function<T, T> identity() {
        return t -> t;
    }

    static <T, U, V> Function<V, U> compose(Function<T, U> f,
                                            Function<V, T> g) {
        return v -> f.apply(g.apply(v));
    }

    static <T, U, V> Function<T, V> andthen(Function<T, U> f,
                                            Function<U, V> g) {
        return t -> g.apply(f.apply(t));
    }

    static <T, U, V> Function<Function<T, U>,
                                Function<Function<U, V>,
                                    Function<T, V>>> compose() {
        return f -> g -> g.compose(f);
    }

    static <T, U , V> Function<Function<T, U>,
                                Function<Function<V, T>,
                                        Function<V, U>>> andThen() {
        return f -> g -> g.andThen(f);
    }

    static <T, U, V> Function<Function<T, U>,
            Function<Function<U, V>,
                    Function<T, V>>> higherCompose() {
        return f -> g -> t -> g.apply(f.apply(t));
    }

    static <T, U , V> Function<Function<T, U>,
            Function<Function<V, T>,
                    Function<V, U>>> highAndThen() {
        return f -> g -> v -> f.apply(g.apply(v));
    }



}
