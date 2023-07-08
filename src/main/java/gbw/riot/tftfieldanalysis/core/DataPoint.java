package gbw.riot.tftfieldanalysis.core;

import java.util.*;

public class DataPoint implements Comparable<DataPoint>{
    private final int namespace;
    private Set<Integer> tags;
    private final int id = this.hashCode();

    public DataPoint(int namespaceKey, Set<Integer> tags)
    {
        this.namespace = namespaceKey;
        this.tags = new HashSet<>(tags);
    }

    public int getNamespace(){
        return namespace;
    }

    public Set<Integer> getTags(){
        return tags;
    }

    public int getId(){
        return id;
    }

    @Override
    public int compareTo(DataPoint p2) {
        if(this.namespace != p2.getNamespace()) return -1;
        if(this.tags.size() != p2.getTags().size()) return -1;
        return this.tags.equals(p2.tags) ? 0 : 1;
    }
}
