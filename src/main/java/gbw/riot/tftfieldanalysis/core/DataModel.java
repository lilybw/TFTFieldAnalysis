package gbw.riot.tftfieldanalysis.core;

import java.util.*;

public class DataModel {

    private final HashMap<DataPoint, HashSet<Edge>> edgeSetForPoint = new HashMap<>();
    private final HashMap<Integer, Edge> edgeTable = new HashMap<>();
    private final HashMap<String,HashSet<DataPoint>> pointsNamespaceMap = new HashMap<>();


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
        edgeSetForPoint.computeIfAbsent(pointA, k -> new HashSet<>()).add(edge);
        edgeSetForPoint.computeIfAbsent(pointB, k -> new HashSet<>()).add(edge);
    }

    public Set<String> getNamespaces(){
        return pointsNamespaceMap.keySet();
    }

}
