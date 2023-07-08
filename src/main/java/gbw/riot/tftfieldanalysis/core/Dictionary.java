package gbw.riot.tftfieldanalysis.core;

import java.util.*;

public class Dictionary<T> {

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

    public Set<Integer> insertAll(T[] values){
        Set<Integer> toReturn = new HashSet<>();
        for (T value : values) {
            toReturn.add(insert(value));
        }
        return toReturn;
    }
    public Set<Integer> insertAll(Collection<T> values){
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
    public int[] reverseTranslateAll(Collection<T> values){
        int[] toReturn = new int[values.size()];
        int index = 0;
        for(T value : values){
            toReturn[index] = reverseTranslate(value);
        }
        return toReturn;
    }
    public int[] reverseTranslateAll(T[] values){
        int[] toReturn = new int[values.length];
        for(int i = 0; i < toReturn.length; i++){
            toReturn[i] = reverseTranslate(values[i]);
        }
        return toReturn;
    }

    public T translate(Integer value){
        T toReturn = dictionary.get(value);
        if(toReturn == null){
            return onUntranslatableA;
        }
        return toReturn;
    }
    public List<T> translateAll(Integer[] values){
        List<T> toReturn = new ArrayList<>(values.length);
        for(int i : values){
            toReturn.add(translate(i));
        }
        return toReturn;
    }
    public List<T> translateAll(Collection<Integer> values){
        List<T> toReturn = new ArrayList<>(values.size());
        for(int i : values){
            toReturn.add(translate(i));
        }
        return toReturn;
    }

    public <R> Map<T,R> decompress(Map<Integer,R> map){
        Map<T,R> toReturn = new HashMap<>();
        for(Integer key : map.keySet()){
            toReturn.put(
                    translate(key),
                    map.get(key)
            );
        }
        return toReturn;
    }
    public <M> Map<Integer,M> compress(Map<T,M> map){
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
