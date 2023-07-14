package gbw.riot.tftfieldanalysis.core.compressors;

import java.util.*;

public class BiDirectionalMap<T,R> implements Map<T,R> {

    private final Map<T,R> front = new HashMap<>();
    private final Map<R,T> back = new HashMap<>();

    @Override
    public int size() {
        return front.size();
    }

    @Override
    public boolean isEmpty() {
        return front.isEmpty() && back.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return front.containsKey(key) || back.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return front.containsValue(value) || back.containsValue(value);
    }

    @Override
    public R get(Object key) {
        return front.get(key);
    }


    @Override
    public R put(Object key, Object value) {
        back.put((R) value,(T) key);
        return front.put((T) key,(R) value);
    }

    @Override
    public R remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map m) {

    }

    @Override
    public void clear() {
        front.clear();
        back.clear();
    }

    @Override
    public Set<T> keySet() {
        return front.keySet();
    }

    @Override
    public Collection<R> values() {
        return front.values();
    }

    @Override
    public Set<Entry<T,R>> entrySet() {
        return front.entrySet();
    }
}
