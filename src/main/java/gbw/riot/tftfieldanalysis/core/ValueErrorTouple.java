package gbw.riot.tftfieldanalysis.core;

//Because GoLang rules and Java kinda sucks...
public record ValueErrorTouple<T,R>(T value, R error) {
    public static <T,R> ValueErrorTouple<T,R> value(T value){
        return new ValueErrorTouple<>(value, null);
    }
    public static <T,R> ValueErrorTouple<T,R> error(R error){
        return new ValueErrorTouple<>(null, error);
    }
    public static <T,R> ValueErrorTouple<T,R> of(T value, R error){
        return new ValueErrorTouple<>(value, error);
    }
}
