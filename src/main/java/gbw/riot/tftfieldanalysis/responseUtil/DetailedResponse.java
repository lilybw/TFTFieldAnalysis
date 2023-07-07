package gbw.riot.tftfieldanalysis.responseUtil;

import java.util.List;

public record DetailedResponse<T>(T data, ResponseDetails details) {

}

