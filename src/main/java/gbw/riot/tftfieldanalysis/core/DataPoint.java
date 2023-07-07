package gbw.riot.tftfieldanalysis.core;

import java.util.*;

public class DataPoint implements Comparable<DataPoint>{
    private final String namespace;
    private HashSet<String> tags;
    private final int id = this.hashCode();

    public DataPoint(String namespaceKey, List<String> tags)
    {
        this.namespace = namespaceKey;
        this.tags = new HashSet<>(tags);
    }

    public boolean has(String key){
        return tags != null && tags.contains(key);
    }

    public boolean put(String value){
        if(tags == null) {
            tags = new HashSet<>();
        }
        return tags.add(value);
    }

    public int getCombinedHash(DataPoint pointB){
        return Objects.hash(this, pointB);
    }

    public String getNamespace(){
        return namespace;
    }

    public HashSet<String> getTags(){
        return tags;
    }

    @Override
    public int compareTo(DataPoint p2) {
        if(!this.namespace.equals(p2.getNamespace())) return -1;
        int hashThis = Objects.hash(this.getTags());
        int hashP2 = Objects.hash(p2.getTags());
        return hashThis - hashP2;
    }
}
