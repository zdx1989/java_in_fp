package chap6;

import chap2.Function;
import chap5.List;

import java.util.Map;

import static chap5.List.*;
import static chap6.Option.*;


public class OptionExample {

    static Function<List<Double>, Double> sum =
            list -> list.foldRight(0.0, x -> y -> x + y);

    static Function<List<Double>, Option<Double>> mean =
            list -> list.isEmpty()
                    ? none()
                    : some(sum.apply(list) / length(list));

    static Function<List<Double>, Option<Double>> variance =
            list -> mean.apply(list)
                    .flatMap(x -> mean.apply(list.map(y -> Math.pow(y, 2))));


}
