package chap2;

/**
 * Created by zhoudunxiong on 2019/11/2.
 */
public class Function0Example {

    public static void main(String[] args) {
        Function0 tripe = new Function0() {
            @Override
            public int apply(int a) {
                return a * 3;
            }
        };

        Function0 square = new Function0() {
            @Override
            public int apply(int a) {
                return a * a;
            }
        };

        System.out.println(tripe.apply(2));
        System.out.println(square.apply(tripe.apply(2)));

        System.out.println(compose(tripe, square).apply(3));
    }

    static Function0 compose(final Function0 f1, final Function0 f2) {
        return new Function0() {
            @Override
            public int apply(int a) {
                return f1.apply(f2.apply(a));
            }
        };
    }


}
