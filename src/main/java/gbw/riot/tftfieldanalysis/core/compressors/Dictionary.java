package gbw.riot.tftfieldanalysis.core.compressors;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.*;

public class Dictionary<T> implements Serializable {
    static final long serialVersionUID = 192_839_801;

    private final Map<Integer,T> dictionary = new HashMap<>();
    private final Map<T,Integer> theOtherWay = new HashMap<>();
    private int nextVal = 0;
    private final T onUntranslatableA;

    public Dictionary(T onUntranslatableA){
        this.onUntranslatableA = onUntranslatableA;
    }

    public int insert(T value){
        Integer existing = theOtherWay.get(value);
        if(existing != null){
            return existing;
        }
        dictionary.put(nextVal, value);
        theOtherWay.put(value, nextVal);

        final int val = nextVal;
        nextVal++;

        return val;
    }

    public Set<Integer> insertAll( @NotNull T[] values){
        Set<Integer> toReturn = new HashSet<>();
        for (T value : values) {
            toReturn.add(insert(value));
        }
        return toReturn;
    }
    public Set<Integer> insertAll( @NotNull Collection<T> values){
        Set<Integer> toReturn = new HashSet<>();
        for (T value : values) {
            toReturn.add(insert(value));
        }
        return toReturn;
    }

    public T remove(int key){
        T existing = dictionary.get(key);
        dictionary.remove(key);
        theOtherWay.remove(existing);
        return existing;
    }

    public Integer reverseTranslate(T value){
        Integer toReturn = theOtherWay.get(value);
        if(toReturn == null){
            return -1;
        }
        return toReturn;
    }
    public int[] reverseTranslateAll( @NotNull Collection<T> values){
        int[] toReturn = new int[values.size()];
        int index = 0;
        for(T value : values){
            toReturn[index] = reverseTranslate(value);
            index++;
        }
        return toReturn;
    }
    public int[] reverseTranslateAll( @NotNull T[] values){
        int[] toReturn = new int[values.length];
        for(int i = 0; i < toReturn.length; i++){
            toReturn[i] = reverseTranslate(values[i]);
        }
        return toReturn;
    }

    public T translate( @NotNull Integer value){
        T toReturn = dictionary.get(value);
        if(toReturn == null){
            return onUntranslatableA;
        }
        return toReturn;
    }
    public List<T> translateAll( @NotNull Integer[] values){
        List<T> toReturn = new ArrayList<>(values.length);
        for(int i : values){
            toReturn.add(translate(i));
        }
        return toReturn;
    }
    public List<T> translateAll( @NotNull Collection<Integer> values){
        List<T> toReturn = new ArrayList<>(values.size());
        for(int i : values){
            toReturn.add(translate(i));
        }
        return toReturn;
    }

    public <R> Map<T,R> decompress( @NotNull Map<Integer,R> map){
        Map<T,R> toReturn = new HashMap<>();
        for(Integer key : map.keySet()){
            toReturn.put(
                    translate(key),
                    map.get(key)
            );
        }
        return toReturn;
    }

    public <M> Map<Integer,M> compress( @NotNull Map<T,M> map){
        Map<Integer,M> toReturn = new HashMap<>();
        for(T key : map.keySet()){
            toReturn.put(
                    insert(key),
                    map.get(key)
            );
        }
        return toReturn;
    }

}
