package chap7;

import chap2.Function;
import chap5.List;
import static chap7.Either.*;

public class EitherExample {

    static <A extends Comparable<A>> Function<List<A>, Either<String, A>> max() {
        return list -> list.isEmpty()
                        ? left("list is empty")
                        : right(list.foldRight(list.head(), x -> y -> x.compareTo(y) > 0 ? x : y));
    }
}
