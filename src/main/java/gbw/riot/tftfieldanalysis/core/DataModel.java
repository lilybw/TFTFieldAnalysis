package gbw.riot.tftfieldanalysis.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;

import java.time.LocalDateTime;
import java.util.*;

public class DataModel {
    public record TrainingSession(LocalDateTime date, long msTaken){ }

    public record ModelMetaData(
            int modelId,
            Set<Integer> matchIdsEvaluated,
            List<TrainingSession> dateSecondsTrainingMap,
            Map<CacheKeys,Long> cachedValues,
            Map<Integer,Long> pointsPerNamespace,
            Map<Integer,Long> pointsWithTagCount,
            Dictionary<String> dictionary
    ) {
        public static ModelMetaData create(int id){
            return new ModelMetaData(
                    id,
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

    public enum CacheKeys {
        MAX_OCCURRENCE_VALUE(-1),
        EDGE_COUNT(0),
        POINT_COUNT(0),
        MIN_OCCURRENCE_VALUE(1);
        public final long defaultValue;
        CacheKeys(int defaultValue){
            this.defaultValue = defaultValue;
        }
    }

    @JsonProperty
    private final Map<Integer, Set<Edge>> pointEdgesMap = new HashMap<>();
    @JsonIgnore
    private final Map<Integer, Edge> edgeTable = new HashMap<>();
    @JsonProperty
    private final Map<Integer,Set<DataPoint>> namespacePointMap = new HashMap<>();
    @JsonIgnore
    private final Set<DataPoint> allPoints = new HashSet<>();
    @JsonIgnore
    private final Map<Integer,DataPoint> pointIdMap = new HashMap<>();
    private final Map<Integer,Set<DataPoint>> pointsWithTagMap = new HashMap<>();
    @JsonIgnore
    private final Set<Edge> allEdges = new HashSet<>();
    private final ModelMetaData metadata = ModelMetaData.create(this.hashCode());

    public Set<DataPoint> getPointsInNamespace(String namespace){
        int translatedNamespace = metadata.dictionary().reverseTranslate(namespace);
        namespacePointMap.computeIfAbsent(translatedNamespace, k -> new HashSet<>());
        return namespacePointMap.get(translatedNamespace);
    }
    public Map<Integer,Set<DataPoint>> getNamespacePointMap(){
        return namespacePointMap;
    }

    public Set<DataPoint> getAllPoints(){
        return allPoints;
    }
    public Map<Integer,DataPoint> getPointMap(){
        return pointIdMap;
    }
    public Set<Edge> getAllEdges(){
        return allEdges;
    }

    public Map<Integer,Set<Edge>> getEdgesForPoints(int[] points){
        Map<Integer,Set<Edge>> toReturn = new HashMap<>();
        for(int i : points){
            toReturn.put(i, pointEdgesMap.get(i));
        }
        return toReturn;
    }
    public Map<Integer,Set<Edge>> getEdgesForPoints(Integer[] points){
        Map<Integer,Set<Edge>> toReturn = new HashMap<>();
        for(Integer i : points){
            toReturn.put(i, pointEdgesMap.get(i));
        }
        return toReturn;
    }

    /**
     * Inserts a point into the model.
     * @param namespace the point should belong to.
     * @param tags tags internally associated with the given point.
     * @return
     */
    public DataPoint insertPoint(String namespace, Set<String> tags){
        return insertPoint(namespace, tags, new String[]{});
    }

    /**
     * Inserts a point into the model.
     * @param namespace the point should belong to.
     * @param tags tags internally associated with the given point.
     * @param metaNoTrack keys (namespace and other) of any a value that should by no means be tracked as part of the metadata unless its already tracked.
     * @return the point inserted.
     */
    public DataPoint insertPoint(String namespace, Set<String> tags, String[] metaNoTrack){
        Set<Integer> translatedTags = metadata.dictionary().insertAll(tags);
        int compressedNamespace = metadata.dictionary().insert(namespace);

        namespacePointMap.computeIfAbsent(compressedNamespace, k -> new HashSet<>());
        for(DataPoint point : namespacePointMap.get(compressedNamespace)){
            if(translatedTags.equals(point.getTags())){
                return point;
            }
        }

        DataPoint newPoint = new DataPoint(compressedNamespace, translatedTags);
        namespacePointMap.get(compressedNamespace).add(newPoint);
        allPoints.add(newPoint);
        pointIdMap.put(newPoint.hashCode(), newPoint);
        translatedTags.forEach(translatedTag ->
        {
            pointsWithTagMap.computeIfAbsent(translatedTag, k -> new HashSet<>());
            pointsWithTagMap.get(translatedTag).add(newPoint);
        });

        metadataUpdateOnPointInsert(compressedNamespace, translatedTags, metaNoTrack);

        return newPoint;
    }

    private void metadataUpdateOnPointInsert(Integer namespace, Set<Integer> tags, String[] metaNoTrack) {
        if(ArrayUtil.contains(
                metadata.dictionary().reverseTranslateAll(metaNoTrack),
                namespace
        )) return;

        metadata.cachedValues.put(CacheKeys.POINT_COUNT,metadata.cachedValues.get(CacheKeys.POINT_COUNT) + 1);
        long previousNamespaceCount = metadata.pointsPerNamespace().computeIfAbsent(namespace, k -> 0L);
        metadata.pointsPerNamespace().put(namespace, previousNamespaceCount + 1);

        for(Integer tag : tags){
            long previousTagCount = metadata.pointsWithTagCount().computeIfAbsent(tag, k -> 0L);
            metadata.pointsWithTagCount().put(tag, previousTagCount + 1);
        }
    }

    public void insertOrIncrementEdge(DataPoint pointA, DataPoint pointB){
        int combinedHash = Objects.hash(pointA, pointB);
        Edge existingEdge = edgeTable.get(combinedHash);
        if(existingEdge != null){
            existingEdge.value++;
            if(existingEdge.value > metadata.cachedValues.get(CacheKeys.MAX_OCCURRENCE_VALUE)){
                metadata.cachedValues.put(CacheKeys.MAX_OCCURRENCE_VALUE, existingEdge.value);
            }
            if(existingEdge.value < metadata.cachedValues.get(CacheKeys.MIN_OCCURRENCE_VALUE)){
                metadata.cachedValues.put(CacheKeys.MIN_OCCURRENCE_VALUE, existingEdge.value);
            }
            return;
        }
        Edge edge = new Edge(pointA, pointB);
        edgeTable.put(combinedHash, edge);
        allEdges.add(edge);
        metadata.cachedValues.put(CacheKeys.EDGE_COUNT, metadata.cachedValues.get(CacheKeys.EDGE_COUNT) + 1);
        pointEdgesMap.computeIfAbsent(pointA.hashCode(), k -> new HashSet<>()).add(edge);
        pointEdgesMap.computeIfAbsent(pointB.hashCode(), k -> new HashSet<>()).add(edge);
    }

    public Set<Integer> getNamespaces(){
        return namespacePointMap.keySet();
    }
    public Set<DataPoint> getPointsWithTags(String[] tags){
        Set<DataPoint> toReturn = new HashSet<>();
        int[] translatedTags = metadata.dictionary().reverseTranslateAll(tags);
        for(int tag : translatedTags){
            toReturn.addAll(pointsWithTagMap.get(tag));
        }
        return toReturn;
    }
    public Set<DataPoint> getPointsWithTag(String tag){
        return pointsWithTagMap.get(
                metadata.dictionary().reverseTranslate(tag)
        );
    }

    public Set<DataPoint> getSpecificDataPoints(int[] ids){
        Set<DataPoint> toReturn = new HashSet<>();
        for(int i : ids){
            toReturn.add(pointIdMap.get(i));
        }
        return toReturn;
    }
    public Set<DataPoint> getPointsWithAnyOf(String[] tags){
        int[] translatedTags = metadata.dictionary().reverseTranslateAll(tags);
        return ArrayUtil.resize(
                allPoints,
                point -> ArrayUtil.containsAnyOf(translatedTags, point.getTags())
        );
    }

    public ModelMetaData getMetaData(){
        return metadata;
    }
    public Map<Integer, Set<Edge>> getPointEdgeMap(){
        return pointEdgesMap;
    }

}
