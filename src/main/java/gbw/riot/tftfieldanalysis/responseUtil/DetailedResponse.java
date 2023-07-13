package gbw.riot.tftfieldanalysis.responseUtil;

import java.util.List;

public record DetailedResponse<T>(T response, ResponseDetails details) {
    public static <T> DetailedResponse<T> details(ResponseDetails details){
        return new DetailedResponse<>((T) null, details);
    }
    public static <T> DetailedResponse<T> success(T response){
        return new DetailedResponse<>(response, ResponseDetails.SUCCESS);
    }
    public static <T> DetailedResponse<T> unknownError(){
        return new DetailedResponse<>(null, ResponseDetails.ERR_UNKNOWN);
    }
    public static <T> DetailedResponse<T> of(T response, ResponseDetails details){
        return new DetailedResponse<>(response, details);
    }

}

