package edu.iastate.metnet.metaomgraph.utils;

import edu.iastate.metnet.metaomgraph.MetaOmProject;

import java.util.TreeMap;
import java.util.TreeSet;

public class DuplicateFinder {
    public DuplicateFinder() {
    }

    public static void findDuplicates(MetaOmProject project) {
        System.out.println("Finding duplicate values: ");
        String[] headers = project.getDataColumnHeaders();
        TreeMap<String, Integer> counts = new TreeMap();
        for (String s : headers) {
            Integer c = counts.get(s);
            if (c == null) {
                c = Integer.valueOf(0);
            }
            c = Integer.valueOf(c.intValue() + 1);
            counts.put(s, c);
        }

        java.util.Set<String> keys = counts.keySet();
        for (String key : keys) {
            Integer c = counts.get(key);
            if (c.intValue() > 1) {
                System.out.println(c + "\t" + key);
            }
        }
    }

    public static void deepFindDuplicates(MetaOmProject project) throws java.io.IOException {
        System.out.println("Doing a deep duplicate search");
        TreeMap<Integer, TreeSet<Integer>> columnHashes = new TreeMap();
        for (int i = 0; i < project.getDataColumnCount(); i++) {
            System.out.println("Column " + i);
            double[] data = project.getDataForColumn(i);
            int hash = data.hashCode();
            TreeSet<Integer> cols = columnHashes.get(Integer.valueOf(hash));
            if (cols == null) {
                cols = new TreeSet();
            }
            cols.add(Integer.valueOf(i));
            columnHashes.put(Integer.valueOf(hash), cols);
        }
        java.util.Set<Integer> keys = columnHashes.keySet();
        for (Integer hash : keys) {
            TreeSet<Integer> cols = columnHashes.get(hash);
            if (cols.size() > 1) {
                System.out.println("Hash: " + hash);
                for (Integer col : cols) {
                    System.out.println("  (" + col + ") " + project.getDataColumnHeader(col.intValue()));
                }
            }
        }
        System.out.println("That is all duplicates");
    }
}
