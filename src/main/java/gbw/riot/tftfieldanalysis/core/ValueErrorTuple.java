package gbw.riot.tftfieldanalysis.core;

//Because GoLang rules and Java kinda sucks...
public record ValueErrorTuple<T, R>(T value, R error) {

    @FunctionalInterface
    public interface ZeroParameterTupleRetriever<K, M extends Throwable> {
        K run() throws M;
    }
    @FunctionalInterface
    public interface OneParameterTupleRetriever<K, N, M extends Throwable> {
        K run(N value) throws M;
    }
    @FunctionalInterface
    public interface TwoParameterTupleRetriever<K, N, L, M extends Throwable> {
        K run(N var1, L var2) throws M;
    }


    public static <T, R> ValueErrorTuple<T, R> value(T value) {
        return new ValueErrorTuple<>(value, null);
    }

    public static <T, R> ValueErrorTuple<T, R> error(R error) {
        return new ValueErrorTuple<>(null, error);
    }

    public static <T, R> ValueErrorTuple<T, R> of(T value, R error) {
        return new ValueErrorTuple<>(value, error);
    }

    public static <T, R extends Throwable> ValueErrorTuple<T, R> encapsulate(ZeroParameterTupleRetriever<T, R> retriever) {
        T value = null;
        try {
            value = retriever.run();
        } catch (Throwable e) {
            return of(value,(R) e);
        }
        return of(value, null);
    }
    public static <T, N, R extends Throwable> ValueErrorTuple<T, R> encapsulate(OneParameterTupleRetriever<T, N, R> retriever, N value) {
        T result = null;
        try {
            result = retriever.run(value);
        } catch (Throwable e) {
            return of(result,(R) e);
        }
        return of(result, null);
    }
    //Yeah I'm not going beyond this, one person can only keep track so much
    public static <T,N,L,R extends Throwable> ValueErrorTuple<T,R> encapsulate(TwoParameterTupleRetriever<T,N,L,R> retriever, N var1, L var2){
        T result = null;
        try {
            result = retriever.run(var1,var2);
        } catch (Throwable e) {
            return of(result,(R) e);
        }
        return of(result, null);
    }

    public boolean hasError(){
        return this.error() != null;
    }
}
