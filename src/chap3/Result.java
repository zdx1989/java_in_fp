package chap3;

/**
 * Created by zhoudunxiong on 2019/11/7.
 */
public interface Result {

    public class Success implements Result {}

    public class Failure implements Result {

        private final String message;

        public Failure(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}
