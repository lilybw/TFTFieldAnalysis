package gbw.riot.tftfieldanalysis.responseUtil;

import gbw.riot.tftfieldanalysis.responseUtil.dtos.DataPointDTO;

import java.util.List;

public class DetailedResponse<T> {

    public T response;
    public ResponseDetails details;

    public DetailedResponse(T response, ResponseDetails details){
        this.response = response;
        this.details = details;
    }

    public T response(){ return response; }
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

