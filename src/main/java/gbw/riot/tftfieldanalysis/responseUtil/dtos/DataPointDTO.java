package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.compressors.Dictionary;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;

@Schema(name = "DataPointDTO")
public record DataPointDTO(int id, String namespace, List<String> tags) {
    public static DataPointDTO of(DataPoint point, DataModel model){
        Dictionary<String> dictionary = model.getMetaData().dictionary();
        return new DataPointDTO(
                point.getId(),
                dictionary.translate(point.getNamespace()),
                dictionary.translateAll(point.getTags())
        );
    }
    public static List<DataPointDTO> of(Collection<DataPoint> collection, DataModel model){
        List<DataPointDTO> toReturn = new ArrayList<>();
        for(DataPoint p : collection){
            toReturn.add(of(p,model));
        }
        return toReturn;
    }

    public static <T> Map<T, List<DataPointDTO>> of(Map<T, List<DataPoint>> map, DataModel model){
        Map<T,List<DataPointDTO>> toReturn = new HashMap<>();
        for(T key : map.keySet()){
            toReturn.put(
                    key,
                    of(map.get(key),model)
            );
        }
        return toReturn;
    }

}
