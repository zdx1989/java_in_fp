package chap7;

import chap2.Function;
import chap3.Effect;
import chap3.Suppllier;
import chap6.Option;

import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;


public abstract class Result<V> {

    public abstract V getOrElse(final V defaultValue);

    public abstract V getOrElse(final Suppllier<V> defaultValue);

    public abstract <U> Result<U> map(Function<V, U> func);

    public abstract <U> Result<U> flatMap(Function<V, Result<U>> func);

    public abstract Option<V> toOption();

    public abstract void forEachOrThrow(Effect<V> ef);

    public abstract V get();

    public Result<V> filter(Function<V, Boolean> func) {
        return flatMap(x -> func.apply(x) ? success(x) : failure("condition not matched"));
    }

    public boolean exists(Function<V, Boolean> func) {
        return map(func).getOrElse(false);
    }

    public Result<V> orElse(Suppllier<Result<V>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue.get());
    }

    public abstract boolean isSuccess();

    public static class Success<V> extends Result<V> {
        private final V value;

        public Success(V value) {
            super();
            this.value = value;
        }

        @Override
        public V getOrElse(V defaultValue) {
            return this.value;
        }

        @Override
        public V getOrElse(Suppllier<V> defaultValue) {
            return this.value;
        }

        @Override
        public <U> Result<U> map(Function<V, U> func) {
            try {
                return success(func.apply(value));
            } catch (Exception e) {
                return failure(e.getMessage());
            }
        }

        @Override
        public <U> Result<U> flatMap(Function<V, Result<U>> func) {
            try {
                return func.apply(value);
            } catch (Exception e) {
                return failure(e.getMessage());
            }
        }

        @Override
        public Option<V> toOption() {
            return Option.some(value);
        }

        @Override
        public void forEachOrThrow(Effect<V> ef) {
            ef.apply(value);
        }

        @Override
        public String toString() {
            return String.format("Success(%s)", value.toString());
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public V get() {
            return value;
        }
    }

    public static class Failure<V> extends Result<V> {
        private final RuntimeException exception;

        public Failure(RuntimeException exception) {
            super();
            this.exception = exception;
        }

        public Failure(String message) {
            super();
            this.exception = new IllegalStateException(message);
        }

        public Failure(Exception e) {
            super();
            this.exception = new IllegalStateException(e.getMessage(), e);
        }

        @Override
        public V getOrElse(V defaultValue) {
            return defaultValue;
        }

        @Override
        public V getOrElse(Suppllier<V> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public <U> Result<U> map(Function<V, U> func) {
            return failure(exception);
        }

        @Override
        public <U> Result<U> flatMap(Function<V, Result<U>> func) {
            return failure(exception);
        }

        @Override
        public Option<V> toOption() {
            return Option.none();
        }

        @Override
        public void forEachOrThrow(Effect<V> ef) {
            throw exception;
        }

        @Override
        public String toString() {
            return String.format("Failure(%s)", exception.getMessage());
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public V get() {
            throw new IllegalStateException("Failure can not get");
        }
    }

    public static <V> Result<V> success(V value) {
        return new Success<>(value);
    }

    public static <V> Result<V> failure(String message) {
        return new Failure<V>(message);
    }

    public static <V> Result<V> failure(Exception e) {
        return new Failure<V>(e);
    }

    public static <V> Result<V> empty() {
        return new Failure<V>("");
    }

    public static <V> Result<V> failure(RuntimeException e) {
        return new Failure<V>(e);
    }

    public static <V> Result<V> of(V value) {
        return value != null
                ? success(value)
                : failure("Value is null");
    }

    public static <V> Result<V> of (V value, String message) {
        return value != null
                ? success(value)
                : failure(message);
    }

    public static <V> Result<V> of (Predicate<V> func, V value) {
        try {
            return func.test(value)
                    ? success(value)
                    : failure("");
        } catch (Exception e) {
            return failure(e.getMessage());
        }

    }

    public static <V> Result<V> of(Predicate<V> func, V value, String message) {
        try {
            return func.test(value)
                    ? success(value)
                    : failure(String.format(message, value));
        } catch (Exception e) {
            return failure(e.getMessage());
        }
    }

    public static <V, U> Function<Result<V>, Result<U>> lift(Function<V, U> func) {
        return rv -> {
            try {
                return rv.map(func);
            } catch (Exception e) {
                return failure(e.getMessage());
            }
        };
    }

    public static <A, B, C> Function<Result<A>, Function<Result<B>, Result<C>>> lift2(Function<A, Function<B, C>> func) {
        return ra -> rb -> {
            try {
                return ra.flatMap(a ->
                        rb .map(b -> func.apply(a).apply(b)));
            } catch (Exception e){
                return failure(e.getMessage());
            }
        };
    }

    public static <A, B, C> Result<C> map2(Result<A> ra, Result<B> rb, Function<A, Function<B, C>> func) {
        return lift2(func).apply(ra).apply(rb);
    }



}
