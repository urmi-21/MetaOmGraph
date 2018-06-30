package edu.iastate.metnet.metaomgraph.utils;

public class CountedString implements Comparable<CountedString> {
    private String text;
    private int count;

    public CountedString(String text, int count) {
        this.text = text;
        this.count = count;
    }

    public String toString() {
        return text + " (" + count + ")";
    }

    public String getText() {
        return text;
    }

    public int getCount() {
        return count;
    }

    public int compareTo(CountedString arg0) {
        if (arg0 == null) {
            return 1;
        }
        return -new Integer(getCount()).compareTo(Integer.valueOf(arg0.getCount()));
    }
}
