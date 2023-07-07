package gbw.riot.tftfieldanalysis.responseUtil;

import java.util.List;

public record ResponseDetails(String name, String description, List<String> notes){
    public static final ResponseDetails SUCCESS = new ResponseDetails("Success", "", null);
    public static final ResponseDetails ERR_UNKNOWN = new ResponseDetails("Unknown Error", "", null);
}