package gbw.riot.tftfieldanalysis.core;

import java.io.Serializable;

public enum CacheKeys implements Serializable {

    MAX_OCCURRENCE_VALUE(-1),
    EDGE_COUNT(0),
    POINT_COUNT(0),
    MIN_OCCURRENCE_VALUE(1);
    public final long defaultValue;
    static final long serialVersionUID = 192_839_819;
    CacheKeys(int defaultValue){
        this.defaultValue = defaultValue;
    }
}
