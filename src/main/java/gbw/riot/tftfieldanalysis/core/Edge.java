package gbw.riot.tftfieldanalysis.core;

import java.io.Serializable;

public class Edge implements Serializable {
    static final long serialVersionUID = 192_839_826;

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
