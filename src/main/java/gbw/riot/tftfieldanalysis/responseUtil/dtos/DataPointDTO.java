package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.Dictionary;

import java.util.*;

public record DataPointDTO(int id, String namespace, List<String> tags) {
    public static DataPointDTO of(DataPoint point, DataModel model){
        Dictionary<String> dictionary = model.getMetaData().dictionary();
        return new DataPointDTO(
                point.getId(),
                dictionary.translate(point.getNamespace()),
                dictionary.translateAll(point.getTags())
        );
    }
    public static Set<DataPointDTO> of(Set<DataPoint> set, DataModel model){
        Set<DataPointDTO> toReturn = new HashSet<>();
        for(DataPoint p : set){
            toReturn.add(of(p,model));
        }
        return toReturn;
    }
    public static <T> Map<T, Set<DataPointDTO>> of(Map<T, Set<DataPoint>> map, DataModel model){
        Map<T,Set<DataPointDTO>> toReturn = new HashMap<>();
        for(T key : map.keySet()){
            toReturn.put(
                    key,
                    of(map.get(key),model)
                    );
        }
        return toReturn;
    }

}
