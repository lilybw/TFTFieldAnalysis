package gbw.riot.tftfieldanalysis.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class DataModel {

    public enum CacheKeys {
        MAX_OCCURRENCE_VALUE(-1),
        EDGE_COUNT(0),
        POINT_COUNT(0),
        MIN_OCCURRENCE_VALUE(-1);
        public final long defaultValue;
        CacheKeys(int defaultValue){
            this.defaultValue = defaultValue;
        }
    }

    @JsonProperty
    private final Map<Integer, Set<Edge>> edgeSetForPoint = new HashMap<>();
    @JsonIgnore
    private final Map<Integer, Edge> edgeTable = new HashMap<>();
    @JsonProperty
    private final Map<String,Set<DataPoint>> namespacePointMap = new HashMap<>();
    @JsonProperty
    private final Map<CacheKeys,Long> cachedValues = getCacheMap();
    private static Map<CacheKeys, Long> getCacheMap() {
        Map<CacheKeys, Long> toReturn = new HashMap<>();
        for(CacheKeys key : CacheKeys.values()){
            toReturn.put(key, key.defaultValue);
        }
        return toReturn;
    }
    @JsonIgnore
    private final Set<DataPoint> allPoints = new HashSet<>();
    @JsonIgnore
    private final Map<Integer,DataPoint> pointIdMap = new HashMap<>();
    @JsonIgnore
    private final Set<Edge> allEdges = new HashSet<>();
    @JsonProperty
    private final Set<String> matchIdsEvaluated = new HashSet<>();

    public Set<DataPoint> getPointsInNamespace(String namespace){
        namespacePointMap.computeIfAbsent(namespace, k -> new HashSet<>());
        return namespacePointMap.get(namespace);
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

    public Map<Integer,Set<Edge>> getEdgesForPoints(Integer[] points){
        Map<Integer,Set<Edge>> toReturn = new HashMap<>();
        for(Integer i : points){
            toReturn.put(i, edgeSetForPoint.get(i));
        }
        return toReturn;
    }

    /**
     * Returns false if that point already existed
     * @param namespace
     * @return
     */
    public DataPoint insertPoint(String namespace, List<String> tags){
        namespacePointMap.computeIfAbsent(namespace, k -> new HashSet<>());
        for(DataPoint point : namespacePointMap.get(namespace)){
            if(Objects.hash(tags) - Objects.hash(point.getTags()) == 0){
                return point;
            }
        }
        DataPoint newPoint = new DataPoint(namespace, tags);
        namespacePointMap.get(namespace).add(newPoint);
        allPoints.add(newPoint);
        pointIdMap.put(newPoint.hashCode(), newPoint);
        cachedValues.put(CacheKeys.POINT_COUNT,cachedValues.get(CacheKeys.POINT_COUNT) + 1);
        return newPoint;
    }

    public void insertOrIncrementEdge(DataPoint pointA, DataPoint pointB){
        int combinedHash = Objects.hash(pointA, pointB);
        Edge existingEdge = edgeTable.get(combinedHash);
        if(existingEdge != null){
            existingEdge.value++;
            if(existingEdge.value > cachedValues.get(CacheKeys.MAX_OCCURRENCE_VALUE)){
                cachedValues.put(CacheKeys.MAX_OCCURRENCE_VALUE, existingEdge.value);
            }
            if(existingEdge.value < cachedValues.get(CacheKeys.MIN_OCCURRENCE_VALUE)){
                cachedValues.put(CacheKeys.MIN_OCCURRENCE_VALUE, existingEdge.value);
            }
            return;
        }
        Edge edge = new Edge(pointA, pointB);
        edgeTable.put(combinedHash, edge);
        allEdges.add(edge);
        cachedValues.put(CacheKeys.EDGE_COUNT, cachedValues.get(CacheKeys.EDGE_COUNT) + 1);
        edgeSetForPoint.computeIfAbsent(pointA.hashCode(), k -> new HashSet<>()).add(edge);
        edgeSetForPoint.computeIfAbsent(pointB.hashCode(), k -> new HashSet<>()).add(edge);
    }

    public Set<String> getNamespaces(){
        return namespacePointMap.keySet();
    }

    public int getId(){
        return this.hashCode();
    }

    public boolean addMatchId(String matchId){
        return matchIdsEvaluated.add(matchId);
    }
    public Map<Integer, Set<Edge>> getPointEdgeMap(){
        return edgeSetForPoint;
    }
    public Map<CacheKeys, Long> getCachedValues(){
        return cachedValues;
    }
    public Map<String,Set<DataPoint>> getNamespacePointMap(){
        return namespacePointMap;
    }
    public Set<String> getEvaluatedMatches() {
        return matchIdsEvaluated;
    }

}
