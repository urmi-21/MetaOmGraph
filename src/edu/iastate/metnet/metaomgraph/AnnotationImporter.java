package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.utils.FlatFileConverter;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

public class AnnotationImporter {
    public AnnotationImporter() {
    }

    public enum Annotation {
        HUMAN_HGU133A("hgu133a"), MOUSE_430_2("mouse430_2"), ARABIDOPSIS_ATH1(
                "ATH1"), SOYBEAN("soybean"), RAT_RAE230A("rae230a"), RAT_RAEX1(
                "raex1"), RAT_RAGENE1("ragene1"), RAT_230_2("rat230_2"), RAT_U34A(
                "u34a"), YEAST_S98("yeasts98"), RICE("rice"), BARLEY("barley"), HUMAN_HGU133PLUS2(
                "hgu133plus2"), YEAST2("yeast2"), ZEBRAFISH("zebrafish"), CUSTOM("custom");

        private final String arrayID;
        private String delimiter;
        private File source;

        Annotation(String arrayID) {
            this.arrayID = arrayID;
            delimiter = "\t";
            source = null;
        }

        public void setSource(File source) {
            this.source = source;
        }

        public String getID() {
            return arrayID;
        }

        public InputStream getAnnotationStream() {
            if (source != null) {
                if (source.getName().endsWith(".csv")) delimiter = ",";

                try {
                    return new java.io.FileInputStream(source);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return getClass().getResourceAsStream(
                    "/resource/annotations/" + getID() + "/annotation.txt");
        }

        public String getDelimiter() {
            return delimiter;
        }
    }

    public static void importAnnotation(MetaOmProject myProject, Annotation myArray) throws IOException {
        if (myArray.getID().equals("ATH1")) {
            doArabidopsis(myProject);
            return;
        }
        ParseResults result = parseMultiColumnStream(myArray.getAnnotationStream(), myArray.getDelimiter());
        Object[][] newNames = new Object[myProject.getRowCount()][myProject.getInfoColumnCount() + result.nameCount];
        for (int i = 0; i < newNames.length; i++) {
            Object[] oldNames = myProject.getRowName(i);
            String[] addUs = null;
            int j;
            for (j = 0; j < myProject.getInfoColumnCount(); j++) {
                newNames[i][j] = oldNames[j];
                if (oldNames[j] != null && result.newNames.get(oldNames[j]) != null && addUs == null) {
                    addUs = result.newNames.get(oldNames[j]);
                }
            }
            for (; j < newNames[i].length; j++) {
                newNames[i][j] = addUs[j - myProject.getInfoColumnCount()];
            }
        }
        String[] newHeaders = new String[myProject.getInfoColumnCount() + result.nameCount];
        String[] oldHeaders = myProject.getInfoColumnNames();
        int i;
        for (i = 0; i < oldHeaders.length; i++) {
            newHeaders[i] = oldHeaders[i];
        }
        for (; i < newHeaders.length; i++) {
            newHeaders[i] = result.newHeaders[i - oldHeaders.length];
        }
        myProject.setRowNames(newNames, newHeaders);
    }

    public static void oldimportAnnotation(MetaOmProject myProject, Annotation myArray) {
        if (myArray == Annotation.ARABIDOPSIS_ATH1) {
            doArabidopsis(myProject);
            return;
        }
        Object[][] newNames = new Object[myProject.getRowCount()][myProject.getInfoColumnCount() + 5];

        for (int i = 0; i < newNames.length; i++) {
            Object[] names = myProject.getRowName(i);
            for (int j = 0; j < names.length; j++) {
                newNames[i][j] = names[j];
            }
        }
        int offset = 0;
        String[][] addUs = new String[5][newNames.length];
        InputStream source = MetaOmGraph.class.getResourceAsStream("/resource/annotations/" + myArray.getID() + "/genenames.txt");
        addUs[(offset++)] = add(myProject, source);
        source = MetaOmGraph.class.getResourceAsStream("/resource/annotations/" + myArray.getID() + "/genesymbols.txt");
        addUs[(offset++)] = add(myProject, source);
        source = MetaOmGraph.class.getResourceAsStream("/resource/annotations/" + myArray.getID() + "/gocomp.txt");
        addUs[(offset++)] = add(myProject, source);
        source = MetaOmGraph.class.getResourceAsStream("/resource/annotations/" + myArray.getID() + "/gofunc.txt");
        addUs[(offset++)] = add(myProject, source);
        source = MetaOmGraph.class.getResourceAsStream("/resource/annotations/" + myArray.getID() + "/goproc.txt");
        addUs[(offset++)] = add(myProject, source);
        for (int i = 0; i < newNames.length; i++) {
            for (int j = 0; j < addUs.length; j++) {
                newNames[i][(myProject.getInfoColumnCount() + j)] = addUs[j][i];
            }
        }
        String[] newHeaders = new String[myProject.getInfoColumnCount() + 5];
        String[] oldHeaders = myProject.getInfoColumnNames();
        for (int i = 0; i < oldHeaders.length; i++) {
            newHeaders[i] = oldHeaders[i];
        }
        newHeaders[oldHeaders.length] = "Gene Name";
        newHeaders[(oldHeaders.length + 1)] = "Gene Symbol";
        newHeaders[(oldHeaders.length + 2)] = "GO Cellular Component";
        newHeaders[(oldHeaders.length + 3)] = "GO Molecular Function";
        newHeaders[(oldHeaders.length + 4)] = "GO Biological Process";
        myProject.setRowNames(newNames, newHeaders);
    }

    private static String[] add(MetaOmProject myProject, InputStream source) {
        FlatFileConverter converter = new FlatFileConverter(source, "\t");
        String[] result = new String[myProject.getRowCount()];
        for (int i = 0; i < result.length; i++) {
            Object[] names = myProject.getRowName(i);
            boolean match = false;
            for (int j = 0; (j < names.length) && (!match); j++) {
                String newName = converter.convert(names[j] + "");
                if (newName != null) {
                    result[i] = newName;
                    match = true;
                }
            }
        }
        return result;
    }

    private static void doArabidopsis(MetaOmProject myProject) {
        int idcol = -1;
        int row = 0;
        while (idcol < 0 && row < myProject.getRowCount()) {
            Object[] names = myProject.getRowName(row++);
            for (int j = 0; j < names.length; j++) {
                if (Utils.getIDType(names[j] + "") == Utils.AFFY25K) {
                    idcol = j;
                }
            }
        }
        myProject.addMetNetRowData(idcol);
    }

    private static ParseResults parseMultiColumnStream(InputStream source, String delimiter) throws IOException {
        BufferedReader in = new BufferedReader(new java.io.InputStreamReader(source));
        String thisLine = in.readLine();
        if (thisLine == null) throw new IOException("Empty file");

        String[] splitLine = thisLine.split(delimiter);
        if (splitLine.length <= 1) {
            throw new IOException("Unable to split " + thisLine + " using delimiter " + delimiter);
        }
        int colCount = splitLine.length - 1;
        ParseResults result = new ParseResults();
        result.nameCount = colCount;
        result.newHeaders = new String[colCount];
        for (int i = 1; i < splitLine.length; i++) {
            result.newHeaders[(i - 1)] = splitLine[i];
        }
        while ((thisLine = in.readLine()) != null) {
            splitLine = thisLine.split(delimiter);
            if (splitLine.length != colCount + 1) {
                System.out.println("Warning: line beginning with " + splitLine[0] + " has a different number of columns");
            }
            String[] newNames = new String[colCount];
            for (int i = 1; i < colCount; i++) {
                if (i >= splitLine.length) newNames[(i - 1)] = "";
                else newNames[(i - 1)] = splitLine[i];
            }
            result.newNames.put(splitLine[0], newNames);
        }
        in.close();
        return result;
    }

    private static class ParseResults {
        public String[] newHeaders;
        public TreeMap<String, String[]> newNames;
        public int nameCount;

        public ParseResults() {
            newNames = new TreeMap();
        }
    }
}
