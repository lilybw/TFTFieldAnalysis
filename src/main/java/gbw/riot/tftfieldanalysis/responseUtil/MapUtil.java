package gbw.riot.tftfieldanalysis.responseUtil;

import gbw.riot.tftfieldanalysis.core.DataPoint;

import java.util.*;

public class MapUtil {

    public static class Values {

        public static <T> Map<String, List<T>> setToList(Map<String, Set<T>> input) {
            Map<String, List<T>> outputMap = new HashMap<>();

            for (Map.Entry<String, Set<T>> entry : input.entrySet()) {
                String key = entry.getKey();
                Set<T> values = entry.getValue();
                List<T> valueList = new ArrayList<>(values);
                outputMap.put(key, valueList);
            }

            return outputMap;
        }
    }
}
