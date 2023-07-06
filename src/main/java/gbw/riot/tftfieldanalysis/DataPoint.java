package gbw.riot.tftfieldanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DataPoint {

    private static final List<String> EMPTY = new ArrayList<>();

    private HashMap<String, List<String>> tags;


    public List<String> get(String key){
        return tags == null ? EMPTY : tags.get(key) == null ? EMPTY : tags.get(key);
    }

    public boolean has(String key){
        return tags != null && tags.containsKey(key);
    }

    public boolean put(String key, String value){

    }

}
