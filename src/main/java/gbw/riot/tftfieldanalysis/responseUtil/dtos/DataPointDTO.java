package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.Dictionary;

import java.util.List;

public record DataPointDTO(int id, String namespace, List<String> tags) {
    public static DataPointDTO of(DataPoint point, DataModel model){
        Dictionary<String> dictionary = model.getMetaData().dictionary();
        return new DataPointDTO(
                point.getId(),
                dictionary.translate(point.getNamespace()),
                dictionary.translateAll(point.getTags())
        );
    }
}
