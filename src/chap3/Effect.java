package chap3;

/**
 * Created by zhoudunxiong on 2019/11/12.
 */
public interface Effect<T> {

    void apply(T t);
}
