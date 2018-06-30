package edu.iastate.metnet.metaomgraph.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class OriginalOrderMap<K, V> extends AbstractMap<K, V> {

    private int index;

    private TreeMap<Integer, V> myMap;

    private ArrayList<K> keys;

    public OriginalOrderMap() {
        index = 0;
        myMap = new TreeMap<Integer, V>();
        keys = new ArrayList<K>();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        OriginalOrderSet<Entry<K, V>> result = new OriginalOrderSet<Entry<K, V>>();
        for (int i = 0; i < keys.size(); i++) {
            final K key = keys.get(i);
            final V value = myMap.get(i);
            ComparableEntry<K, V> addMe = new ComparableEntry<K, V>() {

                public V setValue(V value) {
                    return null;
                }

                public V getValue() {
                    return value;
                }

                public K getKey() {
                    return key;
                }
            };
            result.add(addMe);
        }
        return result;
    }

    public V put(K key, V value) {
        if (keys.contains(key)) {
            int insertHere = keys.indexOf(key);
            myMap.put(insertHere, value);
            return value;
        }
        myMap.put(keys.size(), value);
        keys.add(key);
        return value;
    }

    @SuppressWarnings("hiding")
    private abstract class ComparableEntry<K, V> implements Entry<K, V>, Comparable<ComparableEntry> {

        public int compareTo(ComparableEntry o) {
            Comparable o1 = (Comparable) getKey();
            Comparable o2 = (Comparable) getKey();
            return o1.compareTo(o2);
        }

        public abstract K getKey();

        public abstract V getValue();

        public abstract V setValue(V value);

    }
}
