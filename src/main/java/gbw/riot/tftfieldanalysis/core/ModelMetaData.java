package gbw.riot.tftfieldanalysis.core;

import gbw.riot.tftfieldanalysis.core.compressors.Dictionary;

import java.io.Serializable;
import java.util.*;

public class ModelMetaData implements Serializable {

    static final long serialVersionUID = 292_839_811L;

    public Map<Integer, Long> pointsWithTagCount() {
        return pointsWithTagCount;
    }

    public Map<Integer, Long> pointsPerNamespace() {
        return pointsPerNamespace;
    }

    public Dictionary<String> dictionary() {
        return dictionary;
    }

    public Map<CacheKeys, Long> cachedValues() {
        return cachedValues;
    }

    public List<TrainingSession> dateSecondsTrainingMap() {
        return dateSecondsTrainingMap;
    }

    public int modelId() {
        return modelId;
    }

    public Set<Integer> matchIdsEvaluated() {
        return matchIdsEvaluated;
    }

    public Map<Integer,Long> pointsWithTagCount, pointsPerNamespace;
    public Dictionary<String> dictionary;
    public Map<CacheKeys,Long> cachedValues;
    public List<TrainingSession> dateSecondsTrainingMap;
    public int modelId;
    public Set<Integer> matchIdsEvaluated;

    private ModelMetaData(
            int modelId,
            Set<Integer> matchIdsEvaluated,
            List<TrainingSession> dateSecondsTrainingMap,
            Map<CacheKeys,Long> cachedValues,
            Map<Integer,Long> pointsPerNamespace,
            Map<Integer,Long> pointsWithTagCount,
            Dictionary<String> dictionary
    ){
        this.matchIdsEvaluated = matchIdsEvaluated;
        this.modelId = modelId;
        this.dateSecondsTrainingMap = dateSecondsTrainingMap;
        this.cachedValues = cachedValues;
        this.pointsPerNamespace = pointsPerNamespace;
        this.pointsWithTagCount = pointsWithTagCount;
        this.dictionary = dictionary;
    }

    private static int idCounter = -1;
    public static ModelMetaData create(){
        idCounter++;
        return new ModelMetaData(
                idCounter,
                new HashSet<>(),
                new ArrayList<>(),
                getCacheMap(),
                new HashMap<>(),
                new HashMap<>(),
                new Dictionary<>("")
        );
    }
    private static Map<CacheKeys, Long> getCacheMap() {
        Map<CacheKeys, Long> toReturn = new HashMap<>();
        for(CacheKeys key : CacheKeys.values()){
            toReturn.put(key, key.defaultValue);
        }
        return toReturn;
    }

}
