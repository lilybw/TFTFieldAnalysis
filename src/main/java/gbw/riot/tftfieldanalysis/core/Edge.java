package gbw.riot.tftfieldanalysis.core;

public class Edge {

    Edge(DataPoint pA, DataPoint pB){
        this.pointA = pA;
        this.pointB = pB;
        value++;
    }
    public final int id = this.hashCode();
    public long value;
    public DataPoint pointA;
    public DataPoint pointB;

    public long getValue(){
        return value;
    }

}
