package gbw.riot.tftfieldanalysis.responseUtil;

import gbw.riot.tftfieldanalysis.responseUtil.dtos.DataPointDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Base generic detailed response.")
public class DetailedResponse<T> {
    @Schema(description = "Response data.")
    public T data;
    @Schema(description = "Response details.")
    public ResponseDetails details;

    public DetailedResponse(){}

    public DetailedResponse(T response, ResponseDetails details){
        this.data = response;
        this.details = details;
    }

    public T response(){ return data; }
    public ResponseDetails details(){ return details; }

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

    public static class PointDTOList extends DetailedResponse<List<DataPointDTO>>{
        public PointDTOList(List<DataPointDTO> response, ResponseDetails details) {
            super(response, details);
        }
    };

}

