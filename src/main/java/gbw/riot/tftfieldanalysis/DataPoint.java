package gbw.riot.tftfieldanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DataPoint {
    private static final List<String> EMPTY = new ArrayList<>();
    private final String namespaceKey;
    private HashMap<String, List<String>> tags;

    public DataPoint(String namespaceKey)
    {
        this.namespaceKey = namespaceKey;
        tags = new HashMap<>();
    }

    public List<String> get(String key){
        return tags == null ? EMPTY : tags.get(key) == null ? EMPTY : tags.get(key);
    }

    public boolean has(String key){
        return tags != null && tags.containsKey(key);
    }

    public boolean put(String key, String value){
        tags.computeIfAbsent(key, k -> new ArrayList<>());
        return tags.get(key).add(value);
    }

    

}
