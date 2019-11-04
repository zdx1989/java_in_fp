package chap2;

/**
 * Created by zhoudunxiong on 2019/11/2.
 */
public class FunctionExample {

    public static void main(String[] args) {
        Function<Integer, Integer> tripe = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i * 3;
            }
        };

        Function<Integer, Integer> square = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i * i;
            }
        };

        System.out.println(compose(tripe, square).apply(3));

        Function<Integer, Integer> tripe1 = i -> i * 3;
        Function<Integer, Integer> square1 = i -> i * i;

        System.out.println(compose1(tripe1, square1).apply(3));

        Function<Integer, Function<Integer, Integer>> add = a -> b -> a + b;
        System.out.println(add.apply(2).apply(3));

        BinaryOperator multi = a -> b -> a * b;
        System.out.println(multi.apply(2).apply(3));

        Function<Function<Integer, Integer>,
                Function<Function<Integer, Integer>, Function<Integer, Integer>>> compose =
                a -> b -> c -> a.apply(b.apply(c));
        System.out.println(compose.apply(tripe1).apply(square1).apply(3));

        Function<Integer, Integer> f =
                FunctionExample.<Integer, Integer, Integer>higherCompose().apply(tripe1).apply(square1);
        System.out.println(f.apply(3));

        Function<Integer, Integer> f1 =
                FunctionExample.<Integer, Integer, Integer>higherAndThen().apply(square1).apply(tripe1);
        System.out.println(f1.apply(3));
    }


    static Function<Integer, Integer> compose(Function<Integer, Integer> f1, Function<Integer, Integer> f2) {
        return new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return f1.apply(f2.apply(i));
            }
        };
    }

    static Function<Integer, Integer> compose1(Function<Integer, Integer> f1, Function<Integer, Integer> f2) {
        return i -> f1.apply(f2.apply(i));
    }

     static <T, U, V> Function<Function<U, V>,
             Function<Function<T, U>, Function<T, V>>> higherCompose() {
         return f -> g -> c -> f.apply(g.apply(c));
     }

     static <T, U, V> Function<Function<T, U>,
            Function<Function<U, V>, Function<T, V>>> higherAndThen() {
         return f -> g -> c -> g.apply(f.apply(c));
     }

     static <A, B, C> Function<B, C> partialA(A a, Function<A, Function<B, C>> f) {
         return f.apply(a);
     }

     static <A, B, C> Function<A, C> partialB(B b, Function<A, Function<B, C>> f) {
         return a -> f.apply(a).apply(b);
     }

     <A, B, C, D> String func(A a, B b, C c, D d) {
         return String.format("%s, %s, %s, %s", a, b, c, d);
     }

     <A, B, C, D> Function<A,
                            Function<B,
                                        Function<C,
                                                Function<D, String>>>> func() {
         return a -> b -> c -> d -> String.format("%s, %s, %s, %s", a, b, c, d);
     }

     <A, B, C> Function<A, Function<B, C>> curry(Function<Tuple<A, B>, C> f) {
         return a -> b -> f.apply(new Tuple<>(a, b));
     }

     <A, B, C> Function<B, Function<A, C>> reverseArgs(Function<A, Function<B, C>> f) {
         return b -> a -> f.apply(a).apply(b);
     }

     Function<Integer, Integer> factorial() {
         return n -> n == 0 ? 1 : n * factorial().apply(n - 1);
     }

}
