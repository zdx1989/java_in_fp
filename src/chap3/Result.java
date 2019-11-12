package chap3;


/**
 * Created by zhoudunxiong on 2019/11/7.
 */
public interface Result<T> {

    void bind(Effect<T> success, Effect<String> failure);

    static <T> Result<T> success(T t) {
        return new Success<>(t);
    }

    static <T> Result<T> failure(String message) {
        return new Failure<>(message);
    }

    public class Success<T> implements Result<T> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            success.apply(value);
        }
    }

    public class Failure<T> implements Result<T> {

        private final String message;

        public Failure(String message) {
            this.message = message;
        }

        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            failure.apply(message);
        }
    }

}
