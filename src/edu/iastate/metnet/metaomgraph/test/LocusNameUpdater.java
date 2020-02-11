package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;


public class LocusNameUpdater {
    public LocusNameUpdater() {
    }

    public static void main(String[] args)
            throws IOException {
        File infile = Utils.chooseFileToOpen();
        BufferedReader in = new BufferedReader(new FileReader(infile));

        TreeMap<String, String> mapper = new TreeMap();
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            String[] splitLine = thisLine.split(",");
            if (splitLine.length != 2) {
                System.out.println("[" + thisLine + "] splits into " + splitLine.length);
            } else {
                Utils.appendMapEntry(mapper, splitLine[0], splitLine[1], "; ");
            }
        }
        in.close();
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(infile.getParent(), "locusentityconvert.txt")));
        Set<String> keys = mapper.keySet();
        for (String thisKey : keys) {
            out.write(thisKey + " " + mapper.get(thisKey));
            out.newLine();
        }
        out.close();
    }
}
