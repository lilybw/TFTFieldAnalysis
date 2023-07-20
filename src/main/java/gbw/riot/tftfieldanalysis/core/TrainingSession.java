package gbw.riot.tftfieldanalysis.core;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TrainingSession implements Serializable {
    static final long serialVersionUID = 291_839_811L;
    public LocalDateTime localDateTime;
    public long msTaken;
    public TrainingSession(LocalDateTime date, long msTaken){
        this.localDateTime =date;
        this.msTaken = msTaken;
    }

    public LocalDateTime localDateTime() {
        return localDateTime;
    }
    public long msTaken(){
        return msTaken;
    }


}
