package edu.iastate.metnet.metaomgraph.utils;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;

public class OriginalOrderSet<E> extends AbstractSet<E> {

    private ArrayList<E> list;

    public OriginalOrderSet() {
        list = new ArrayList<E>();
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
	public boolean add(E o) {
        if (list.contains(o)) {
            return false;
        }
        list.add(o);
        return true;
    }

}
