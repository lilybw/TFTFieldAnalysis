package gbw.riot.tftfieldanalysis.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Edge {

    public static final HashMap<DataPoint, HashSet<Edge>> EDGE_MAP_FOR_POINT = new HashMap<>();
    public static final HashMap<Integer, Edge> EDGE_TABLE = new HashMap<>();

    public static void createOrIncrement(DataPoint pointA, DataPoint pointB){
        int combinedHash = Objects.hash(pointA, pointB);
        if(EDGE_TABLE.get(combinedHash) != null){
            EDGE_TABLE.get(combinedHash).value++;
            return;
        }
        Edge edge = new Edge(pointA, pointB);
        EDGE_TABLE.put(combinedHash, edge);
        EDGE_MAP_FOR_POINT.computeIfAbsent(pointA, k -> new HashSet<>()).add(edge);
        EDGE_MAP_FOR_POINT.computeIfAbsent(pointB, k -> new HashSet<>()).add(edge);
    }

    Edge(DataPoint pA, DataPoint pB){
        this.pointA = pA;
        this.pointB = pB;
        value++;
    }
    private final int id = this.hashCode();
    public long value;
    public DataPoint pointA;
    public DataPoint pointB;

}
