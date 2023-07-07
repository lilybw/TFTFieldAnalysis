package gbw.riot.tftfieldanalysis.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class DataModel {

    @JsonProperty
    private final Map<Integer, Set<Edge>> edgeSetForPoint = new HashMap<>();
    @JsonProperty
    private final Map<Integer, Edge> edgeTable = new HashMap<>();
    @JsonProperty
    private final Map<String,Set<DataPoint>> pointsNamespaceMap = new HashMap<>();

    private final Set<DataPoint> allPoints = new HashSet<>();
    private final Map<Integer,DataPoint> pointIdMap = new HashMap<>();
    private final Set<Edge> allEdges = new HashSet<>();


    public Set<DataPoint> getPointsInNamespace(String namespace){
        pointsNamespaceMap.computeIfAbsent(namespace, k -> new HashSet<>());
        return pointsNamespaceMap.get(namespace);
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
    public boolean insertPoint(String namespace, List<String> tags){
        pointsNamespaceMap.computeIfAbsent(namespace, k -> new HashSet<>());
        for(DataPoint point : pointsNamespaceMap.get(namespace)){
            if(Objects.hash(tags) - Objects.hash(point.getTags()) == 0){
                return false;
            }
        }
        DataPoint newPoint = new DataPoint(namespace, tags);
        pointsNamespaceMap.get(namespace).add(newPoint);
        allPoints.add(newPoint);
        pointIdMap.put(newPoint.hashCode(), newPoint);
        return true;
    }

    public void insertOrIncrementEdge(DataPoint pointA, DataPoint pointB){
        int combinedHash = Objects.hash(pointA, pointB);
        if(edgeTable.get(combinedHash) != null){
            edgeTable.get(combinedHash).value++;
            return;
        }
        Edge edge = new Edge(pointA, pointB);
        edgeTable.put(combinedHash, edge);
        allEdges.add(edge);
        edgeSetForPoint.computeIfAbsent(pointA.hashCode(), k -> new HashSet<>()).add(edge);
        edgeSetForPoint.computeIfAbsent(pointB.hashCode(), k -> new HashSet<>()).add(edge);
    }

    public Set<String> getNamespaces(){
        return pointsNamespaceMap.keySet();
    }

}
