package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.MetaOmProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTable;


public class RepFinder {
    public static Map<String, ArrayList<String>> repResult;
    public static Map<String, String> expIDMap;

    public RepFinder() {
    }

    public static Map<String, Integer> findReps(MetaOmProject paramMetaOmProject) {
        throw new Error("Unresolved compilation problem: \n");
    }


    private static class Sample
            implements Comparable<Sample> {
        public String name;


        public String expID;


        public Sample(String paramString1, String paramString2) {
        }


        public boolean equals(Object paramObject) {
            throw new Error("Unresolved compilation problem: \n");
        }


        public int compareTo(Sample paramSample) {
            throw new Error("Unresolved compilation problem: \n");
        }
    }


    public static Map<String, Integer> findRepsMetadata(MetaOmProject paramMetaOmProject) {
        throw new Error("Unresolved compilation problems: \n\tExtendedInfoTree cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n");
    }


    public static boolean areReps(String paramString1, String paramString2) {
        throw new Error("Unresolved compilation problem: \n");
    }


    public static String getStringDiff(String paramString1, String paramString2) {
        throw new Error("Unresolved compilation problem: \n");
    }


    public static JPanel getRepPanel(MetaOmProject paramMetaOmProject) {
        throw new Error("Unresolved compilation problems: \n\tThe method getParentData(String) is undefined for the type Metadata\n\tThe method getParentData(String) is undefined for the type Metadata\n\tThe method getData(String) is undefined for the type Metadata\n\tThe method getParentData(String) is undefined for the type Metadata\n");
    }


    public static AveRepResult getRepAveragedData(MetaOmProject paramMetaOmProject, int paramInt)
            throws IOException {
        throw new Error("Unresolved compilation problem: \n");
    }


    public static File aveReps(MetaOmProject paramMetaOmProject)
            throws IOException {
        throw new Error("Unresolved compilation problem: \n");
    }


    private static class RepGroup
            implements Comparable<RepGroup> {
        private String sampleName;

        private ArrayList<String> reps;


        public RepGroup(String paramString) {
        }


        public String getSampleName() {
            throw new Error("Unresolved compilation problem: \n");
        }

        public void addRep(String paramString) {
            throw new Error("Unresolved compilation problem: \n");
        }


        public int compareTo(RepGroup paramRepGroup) {
            throw new Error("Unresolved compilation problem: \n");
        }
    }


    private static class RepAve {
        public double total;

        public int reps;

        public RepAve() {
        }

        public double getAve() {
            throw new Error("Unresolved compilation problem: \n");
        }
    }

    private static void sizeColumnsToFit(JTable paramJTable) {
        throw new Error("Unresolved compilation problem: \n");
    }

    public static class AveRepResult {
        public String[] sampleNames;
        public double[] values;
        public double[] stddevs;
        public int[] repCounts;

        public AveRepResult(String[] paramArrayOfString, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int[] paramArrayOfInt) {
        }
    }
}
