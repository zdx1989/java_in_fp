package chap4;


import java.util.function.Supplier;

public abstract class TailCall<T> {

    public abstract TailCall<T> resume();
    public abstract T eval();
    public abstract boolean isSuspend();

    private TailCall() {
    }

    public static <T> Return<T> ret(T t) {
        return new Return<>(t);
    }

    public static <T> Suspend<T> sus(Supplier<TailCall<T>> resume) {
        return new Suspend<>(resume);
    }

    public static class Return<T> extends TailCall<T> {
        private final T value;

        public Return(T value) {
            this.value = value;
        }

        @Override
        public TailCall<T> resume() {
            throw new IllegalStateException("Return has no resume");
        }

        @Override
        public T eval() {
            return value;
        }

        @Override
        public boolean isSuspend() {
            return false;
        }
    }

    public static class Suspend<T> extends TailCall<T> {
        private Supplier<TailCall<T>> resume;

        public Suspend(Supplier<TailCall<T>> resume) {
            this.resume = resume;
        }

        @Override
        public TailCall<T> resume() {
            return resume.get();
        }

        @Override
        public T eval() {
            TailCall<T> tailRec = this;
            while (tailRec.isSuspend()) {
                tailRec = tailRec.resume();
            }
            return tailRec.eval();
        }

        @Override
        public boolean isSuspend() {
            return true;
        }
    }
}
