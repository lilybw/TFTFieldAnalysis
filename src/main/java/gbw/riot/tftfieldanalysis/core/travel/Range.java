package gbw.riot.tftfieldanalysis.core.travel;

import gbw.riot.tftfieldanalysis.core.ValErr;

public class Range {

    public enum Type {
        ABSOLUTE, NORMALIZED, RELATIVE;
    }

    public float min, max;

    public Range(){}
    public Range(float min, float max){
        this.max = max;
        this.min = min;
    }


    public float constrainedLerp(float value){
        if(value >= max) return max;
        if(value <= min) return min;
        return (value - min) / (max - min);
    }

    /**
     * let range = Range(0,2);
     * range.contains(2) ?  FALSE
     * range.contains(0) ?  FALSE
     * range.contains(1) ?  TRUE
     */
    public boolean contains(float value){
        return value < max && value > min;
    }
    /**
     * let range = Range(0,2);
     * range.contains(2) ?  TRUE
     * range.contains(0) ?  TRUE
     * range.contains(1) ?  TRUE
     */
    public boolean includes(float value){
        return value <= max && value >= min;
    }

    public static ValErr<Range,Exception> of(int[] range){
        return ValErr.encapsulate(() -> new Range((float) range[0],(float) range[1]));
    }

    public static ValErr<Range,Exception> of(Object[] range){
        //Dicey, but legal as any ClassCastException, NumberFormatException... etc is caught by encapsulate
        return ValErr.encapsulate(
                () -> new Range(
                        Float.parseFloat((String) range[0]),
                        Float.parseFloat((String) range[1])
                )
        );
    }

    public static ValErr<Range,Exception> of(String range, String separator){
        return Range.of(range.split(separator));
    }



}
