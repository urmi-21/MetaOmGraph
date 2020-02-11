package edu.iastate.metnet.arrayexpress.v2;

import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;
import edu.iastate.metnet.metaomgraph.ui.TextAreaRenderer;
import edu.iastate.metnet.metaomgraph.utils.RepUtils;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.qdxml.DocHandler;
import edu.iastate.metnet.metaomgraph.utils.qdxml.QDParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdom.Element;


public class AEXMLNodeInfo {
    String name;
    String text;
    Hashtable<String, Object> attributes;
    String[] headers;
    ArrayList<Object[]> data;
    String accession;
    boolean hasProcessedData;
    int miameScore;
    Object[] myListing;
    File zippedDataFile;
    File processedDataFile;
    File metadataFile;
    ArrayList<String> samples;
    TreeMap<String, String> sampleNameMap;
    TreeMap<String, Integer> colIndexMap;
    private static TreeMap<String, String> dictionary;
    private Progress p;
    private String status;
    private ArrayList<ChangeListener> listeners;
    private ArrayList<FileInfo> files;

    public AEXMLNodeInfo() {
        name = null;
        text = null;
        attributes = new Hashtable();
        hasProcessedData = false;
        status = "Waiting";
    }

    @Override
	public String toString() {
        if (attributes.get("name") != null) {
            return attributes.get(name) + "";
        }
        return name;
    }

    public JTable getTable() {
        if ((headers != null) && (data != null)) {
            NoneditableTableModel model = new NoneditableTableModel(data.toArray(new Object[0][]),
                    headers);
            return new JTable(model);
        }
        String[] thisHeaders = {"Attribute", "Value"};
        Object[][] thisData;
        if (data != null) {
            thisData = new Object[attributes.size() + data.size() + 2][2];
        } else {
            thisData = new Object[attributes.size() + 2][2];
        }
        thisData[0][0] = "Name";
        thisData[0][1] = name;


        int x = 1;

        Set<String> keys = attributes.keySet();
        for (String key : keys) {
            thisData[x][0] = key;
            thisData[x][1] = attributes.get(key);
            x++;
        }
        if (data != null) {
            for (Object[] addMe : data) {
                thisData[(x++)] = addMe;
            }
        }
        Arrays.sort(thisData, new Comparator<Object[]>() {

            @Override
			public int compare(Object[] o1, Object[] o2) {
                return (o1[0] + "").compareTo(o2[0] + "");
            }

        });
        NoneditableTableModel model = new NoneditableTableModel(thisData, thisHeaders);
        StripedTable result = new StripedTable(model);
        for (int i = 0; i < result.getColumnCount(); i++) {
            result.getColumnModel().getColumn(i).setCellRenderer(new TextAreaRenderer());
        }
        return result;
    }

    public void addData(Object[] values) {
        if (data == null) {
            data = new ArrayList();
        }
        data.add(values);
        if ("accession".equals(values[0])) {
            accession = (String) values[1];
        }
    }

    public Object getData(Object key) {
        if ((data == null) || (key == null)) {
            return null;
        }
        for (Object[] thisData : data) {
            if (key.equals(thisData[0])) {
                return thisData[1];
            }
        }
        return null;
    }

    public Object[] getListing() {
        if (myListing != null) {
            return myListing;
        }
        Object id = getData("accession");
        Object date = getData("lastupdatedate");
        if (date == null) {
            date = getData("releasedate");
        }
        if (date == null) {
            date = getData("loaddate");
        }
        Object samples = getData("samples");
        myListing = new Object[6];
        myListing[0] = new Boolean(false);
        myListing[1] = id;
        myListing[2] = new Integer(samples + "");
        myListing[3] = new Integer(miameScore);
        myListing[4] = getData("name");
        myListing[5] = date;
        return myListing;
    }

    public boolean matches(String filter) {
        String lcaseFilter = filter.toLowerCase();
        if (toString().toLowerCase().contains(lcaseFilter)) {
            return true;
        }
        for (Object[] thisData : data) {
            if (thisData[1].toString().toLowerCase().contains(lcaseFilter)) {
                return true;
            }
        }
        return false;
    }

    public File downloadProcessedData(File destDir) throws Exception {
        System.out.println("Downloading " + accession);
        setStatus("Downloading");
        File dest = new File(destDir, accession);
        if (dest.exists()) {
            p.setValue(p.getMax());
            zippedDataFile = dest;
            fireChangeEvent();
            return dest;
        }

        if (files == null) {
            files = FileListParser.getFileList(accession);
        }
        int fgemcount = 0;
        URL source = null;
        String name = null;
        for (FileInfo thisFile : files)
            if ("fgem".equals(thisFile.kind)) {

                fgemcount++;
                if (source == null) {
                    source = new URL(thisFile.url);
                    name = name;
                    System.out.println(accession + " Found FGEM: " + name);
                }
            }
        if (fgemcount > 1) {
            System.err.println(fgemcount + " fgems detected in " + accession);
        }
        if (source == null) {
            return null;
        }
        System.out.println("Ready to download: " + source);
        URLConnection conn = source.openConnection();
        int attempts = 0;
        boolean success = false;
        while ((!success) && (attempts < 5)) {
            try {
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                conn.connect();
                success = true;
            } catch (Exception e) {
                attempts++;
                System.err.println("Connection attempt " + attempts + " failed.");
                e.printStackTrace();
            }
        }

        if (!success) {
            System.err.println("Unable to download file: " + source);
            return null;
        }
        int size = conn.getContentLength();
        p = new Progress(0, size, 0);
        fireChangeEvent();

        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
        long count = 0L;
        System.out.println("Beginning download");
        int i = 0;
        byte[] bytesIn = new byte[1024];
        int update = 0;
        long startTime = System.currentTimeMillis();
        while ((i = in.read(bytesIn)) >= 0) {
            out.write(bytesIn, 0, i);
            count += i;
            update = (update + 1) % 100;
            if (p != null) {
                p.increaseValue(i);
                fireChangeEvent();
            }
            if (update == 0) {
                double time = System.currentTimeMillis() - startTime;
                time /= 1000.0D;
                i = (int) (count / 1024L / time);
            }
        }


        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        double speed = size / 1024.0D / (timeTaken / 1000.0D);
        System.out.println("Downloaded " + count + " bytes in " + timeTaken + "ms (" + speed +
                " KB/sec)");
        in.close();
        out.close();
        zippedDataFile = dest;
        fireChangeEvent();
        return dest;
    }

    public File getZippedDataFile() {
        return zippedDataFile;
    }

    public File getProcessedDataFile() {
        return processedDataFile;
    }

    public void unzipAndProcessData() throws IOException {
        setStatus("Processing");
        ZipFile zipIn = null;
        try {
            zipIn = new ZipFile(zippedDataFile);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Done");
            return;
        }
        ZipEntry thisEntry;
        Enumeration<? extends ZipEntry> entryEnum = zipIn.entries();
        File destDir = zippedDataFile.getParentFile();
        File outfile = File.createTempFile("out", null, zippedDataFile.getParentFile());
        ArrayList<ProcessedDataFileInfo> dataFileList = new ArrayList<ProcessedDataFileInfo>();
        while (entryEnum.hasMoreElements()) {
            thisEntry = entryEnum.nextElement();
            if (thisEntry.getName().endsWith(".sdrf.txt")
                    || thisEntry.getName().endsWith(".idf.txt")
                    || thisEntry.getName().endsWith("-info.txt")) {
                continue;
            }
            System.out.println("Unzipping and analyzing: " + accession + " -> "
                    + thisEntry.getName());
            ProcessedDataFileInfo thisFile = new ProcessedDataFileInfo(Utils.unzip(zippedDataFile,
                    thisEntry, accession), accession);
            if (thisFile.dataCols.size() != thisFile.headers.size()) {
                System.err.println(thisFile.source.getName() + " has " + thisFile.dataCols.size()
                        + " data cols and " + thisFile.headers.size() + " headers?!");
            }

            if (thisFile.dataCols.size() <= 0) {
                System.err.println("No data: " + thisEntry.getName());
                continue;
            }
            if (thisFile.dataStarts.isEmpty()) {
                continue;
            }
            dataFileList.add(thisFile);
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
        out.write("Probe ID");
        RandomAccessFile[] in = new RandomAccessFile[dataFileList.size()];
        int index = 0;
        samples = new ArrayList<String>();
        for (ProcessedDataFileInfo thisFile : dataFileList) {
            for (DataCol col : thisFile.newDataCols) {
                out.write("\t" + col.header);
                samples.add(col.header);
            }
            in[index++] = new RandomAccessFile(thisFile.source, "r");
        }
        out.newLine();
        for (String probe : AEProjectMaker.probeList) {
            // for (int i = 0; i < AEProjectMaker.probeList.size(); i++) {
            // String probe = AEProjectMaker.probeList.get(i);
            out.write(probe);
            index = 0;
            for (ProcessedDataFileInfo thisFile : dataFileList) {
                Long start = thisFile.dataStarts.get(probe);
                if (start == null) {
                    for (int col : thisFile.dataCols) {
                        out.write("\t");
                    }
                } else {
                    in[index].seek(thisFile.dataStarts.get(probe));
                    String[] data = in[index].readAndSplitLine('\t', false);
                    for (DataCol col : thisFile.newDataCols) {
                        try {
                            out.write("\t" + data[col.col]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err
                                    .println("Error in column " + col + " on the following line:");
                            for (String thisData : data) {
                                System.err.print(thisData + "\t");
                            }
                            System.err.println();
                            out.write("\t");
                        }
                    }
                }
                index++;
            }
            out.newLine();
        }
        for (RandomAccessFile thisIn : in) {
            thisIn.close();
        }
        out.close();
        this.processedDataFile = outfile;
        // processedFiles.add(outfile.getAbsolutePath());
        for (ProcessedDataFileInfo thisFile : dataFileList) {
            if (!thisFile.source.delete()) {
                thisFile.source.deleteOnExit();
            }
        }
        // zipFiles.remove(source);
        // if (!source.delete()) {
        // System.out.println("Unable to delete "
        // + source.getAbsolutePath());
        // source.deleteOnExit();
        // }
        setStatus("Getting Metadata");
        try {
            // sdrfData=getSDRFData();
            metadataFile = downloadSDRFData(destDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setStatus("Done");
    }

    public String getNewMetadataElement() throws Exception {
        if ((samples == null) || (samples.size() <= 0)) {
            return null;
        }
        if (dictionary == null) {
            loadDictionary();
        }
        HashMap<String, String> attribMap = new HashMap();
        String name = (String) getData("name");
        attribMap.put("name", name);
        StringBuilder result = new StringBuilder(Utils.makeXMLElement("Experiment", null,
                attribMap, 0, false) + "\n");
        attribMap.clear();
        attribMap.put("field", "Experiment Name");
        attribMap.put("value", name);
        result.append(Utils.makeXMLElement("md", null, attribMap, 1, true) + "\n");
        for (Object[] thisMetadata : data) {
            attribMap.clear();
            //String key = thisMetadata[0].trim()+"";
            String key = (thisMetadata[0] + "").trim();
            if (!key.startsWith("sampleattribute")) {

                String value = (thisMetadata[1] + "").trim();
                if (key.startsWith("experimentalfactor")) {
                    String newValue = dictionary.get(key);
                    if (newValue == null) {
                        String[] splitKey = key.split(": ", 2);
                        if (splitKey.length == 2) {
                            newValue = splitKey[1];
                        }
                    }
                    key = "Experimental Factor";
                    value = newValue;
                } else {
                    String betterKey = dictionary.get(key);
                    if (betterKey != null) {
                        key = key.toLowerCase();
                        if (betterKey.equals("Genotype")) {
                            if (key.startsWith("cult")) {
                                value = value + ": Cultivar";
                            } else if (key.startsWith("eco")) {
                                value = value + ": Ecotype";
                            } else if ((key.contains("variation")) || (key.equals("mutant")) ||
                                    (key.equals("geneticmodification"))) {
                                value = value + ": Variant";
                            } else if (key.equals("individual")) {
                                value = value + ": Individual";
                            } else if ((key.startsWith("strain")) || (key.endsWith("background")) ||
                                    (key.startsWith("germplasm")) ||
                                    (key.equals("individualgeneticcharacteristics")) ||
                                    (key.equals("line"))) {
                                value = value + ": Strain";
                            }
                        } else if (betterKey.equals("Developmental Stage")) {
                            if (key.startsWith("flower")) {
                                value = value + ": Flower";
                            } else if (key.contains("stage")) {
                                value = value + ": Stage";
                            } else if (key.contains("age")) {
                                value = value + ": Age";
                            }
                        }
                        key = betterKey;

                    } else if (key.toUpperCase().equals(key)) {
                        key = key.toLowerCase();
                    }

                    if (("null".equals(key)) || ("null".equals(value)) || ("".equals(key)) ||
                            ("".equals(value))) {
                        continue;
                    }
                }
                attribMap.put("field", key);
                attribMap.put("value", value);
                result.append(Utils.makeXMLElement("md", null, attribMap, 1, true) + "\n");
            }
        }
        attribMap.clear();
        attribMap.put("field", "Statistical Analysis");
        attribMap.put("value", getSoftware());
        result.append(Utils.makeXMLElement("md", null, attribMap, 1, true) + "\n");
        for (String sample : samples) {
            System.out.println("Sample: " + sample);
        }
        TreeMap<String, ArrayList<String[]>> sdrfData;
        if ((metadataFile != null) && (metadataFile.exists())) {
            ObjectInputStream metadataIn = new ObjectInputStream(new FileInputStream(metadataFile));

            try {
                sdrfData = (TreeMap) metadataIn.readObject();
            } catch (Exception e) {
                sdrfData = new TreeMap();
            }
            metadataIn.close();
        } else {
            sdrfData = new TreeMap();
        }

        Object unusedSamples = new LinkedHashMap();
        String trueName;
        for (String thisSample : samples) {
            trueName = sampleNameMap.get(thisSample);
            if (trueName == null) {
                trueName = thisSample;
            }
            ((LinkedHashMap) unusedSamples).put(trueName, thisSample);
        }
        ArrayList<ArrayList<String>> sampleGroups = RepUtils.groupSamples(((LinkedHashMap) unusedSamples).keySet());
        for (ArrayList<String> thisSampleGroup : sampleGroups) {
            String groupName = RepUtils.makeGroupName(thisSampleGroup.get(0));
            result.append("  <Group size=\"" + thisSampleGroup.size() + "\" name=\"" + groupName +
                    "\">\n");
            for (String thisSample : thisSampleGroup) {
                String originalName = (String) ((LinkedHashMap) unusedSamples).remove(thisSample);
                if (originalName == null) {
                    System.err.println("Trying to add a sample to a group that is not unused: " +
                            thisSample);
                } else {
                    attribMap.clear();
                    attribMap.put("name", thisSample);
                    Integer col = colIndexMap.get(thisSample);
                    if (col == null) {
                        System.err.println("Unable to find a column for [" + thisSample + "] in [" +
                                accession + "]");
                    } else {
                        attribMap.put("col", col.toString());
                    }
                    result.append(Utils.makeXMLElement("Sample", null, attribMap, 2, false) + "\n");
                    attribMap.clear();
                    attribMap.put("field", "Sample Name");
                    attribMap.put("value", thisSample);
                    result.append(Utils.makeXMLElement("md", null, attribMap, 3, true) + "\n");


                    ArrayList<String[]> sampleMetadata = sdrfData.get(originalName);
                    if (sampleMetadata == null) {
                        System.err.println("No metadata for sample [" + originalName + "] in [" +
                                accession + "]");
                    } else
                        for (String[] thisMetadata : sampleMetadata) {
                            attribMap.clear();
                            String field = thisMetadata[0].trim();
                            String value = thisMetadata[1].trim();
                            String betterField = dictionary.get(field);
                            if (betterField != null) {
                                field = betterField;
                            }
                            if ((!"null".equals(field)) && (!"null".equals(value)) && (!"".equals(field)) &&
                                    (!"".equals(value))) {

                                attribMap.put("field", field);
                                attribMap.put("value", thisMetadata[1]);
                                result.append(Utils.makeXMLElement("md", null, attribMap, 3, true) + "\n");
                            }
                        }
                    result.append("    </Sample>\n");
                }
            }
            result.append("  </Group>\n");
        }
        result.append("</Experiment>");
        if ((metadataFile != null) && (metadataFile.exists()) && (!metadataFile.delete())) {
            metadataFile.deleteOnExit();
        }
        return result.toString();
    }

    public String getMetadataElement() throws Exception {
        HashMap<String, String> attribMap = new HashMap();
        String name = getData("name") + "";
        attribMap.put("name", name);
        StringBuilder result = new StringBuilder(Utils.makeXMLElement("Experiment", null,
                attribMap, 0, false) + "\n");
        result.append(Utils.makeXMLElement("ExperimentName", name, null, 1, true) + "\n");
        result.append(Utils.makeXMLElement("ExperimentID", accession, null, 1, true) + "\n");
        String key;
        for (Object[] thisMetadata : data) {
            key = (thisMetadata[0] + "").trim();
            String value = (thisMetadata[1] + "").trim();
            if ((!"null".equals(key)) && (!"null".equals(value)) && (!"".equals(key)) && (!"".equals(value))) {

                result.append(Utils.makeXMLElement(key.replace(": ", "_").replace(' ', '_'), value,
                        null, 1, true) + "\n");
            }
        }
        result.append(Utils.makeXMLElement("Software", getSoftware(), null, 1, true) + "\n");
        if ((samples == null) || (samples.size() <= 0)) {
            return null;
        }
        for (String sample : samples) {
            System.out.println("Sample: " + sample);
        }
        TreeMap<String, ArrayList<String[]>> sdrfData = getSDRFData();
        for (String thisSample : samples) {
            String originalName = thisSample;

            String trueName = null;
            if (trueName == null) {
                trueName = originalName;
            }
            attribMap = new HashMap();
            attribMap.put("name", trueName);
            result.append(Utils.makeXMLElement("Sample", null, attribMap, 1, false) + "\n");
            result.append(Utils.makeXMLElement("SampleName", trueName, null, 2, true) + "\n");
            if (!trueName.equals(originalName)) {
                result.append(Utils.makeXMLElement("OriginalName", originalName, null, 2, true) +
                        "\n");
            }
            ArrayList<String[]> sampleMetadata = sdrfData.get(originalName);
            for (String[] thisMetadata : sampleMetadata) {
                result.append(Utils.makeXMLElement(thisMetadata[0].replace(": ", "_"),
                        thisMetadata[1], null, 2, true) + "\n");
            }
            result.append("  </Sample>\n");
        }
        result.append("</Experiment>");
        return result.toString();
    }

    public File downloadSDRFData(File destDir) throws Exception {
        File dest = new File(destDir, accession + "metadata");
        if (files == null) {
            files = FileListParser.getFileList(accession);
        }
        TreeMap<String, ArrayList<String[]>> result = new TreeMap();
        sampleNameMap = new TreeMap();
        int sdrfcount = 0;
        URL source = null;
        String filename = null;
        for (FileInfo thisFile : files)
            if (("sdrf".equals(thisFile.kind)) && ("txt".equals(Utils.getExtension(name)))) {

                sdrfcount++;
                if (source == null) {
                    source = new URL(thisFile.url);
                    filename = name;
                    System.out.println(accession + " Found sdrf: " + filename);
                }
            }
        if (sdrfcount > 1) {
            System.err.println(sdrfcount + " sdrfs detected in " + accession);
        }
        if (source == null) {
            return null;
        }
        System.out.println("Ready to download: " + source);
        URLConnection conn = source.openConnection();
        int attempts = 0;
        boolean success = false;
        while ((!success) && (attempts < 5)) {
            try {
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                conn.connect();
                success = true;
            } catch (Exception e) {
                attempts++;
                System.err.println("Connection attempt " + attempts + " failed.");
                e.printStackTrace();
            }
        }
        if (!success) {
            System.err.println("Unable to download file: " + source);
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String[] headers = in.readLine().split("\t");
        String thisLine = in.readLine();
        String[] splitLine = thisLine.split("\t");
        TreeMap<Integer, String> goodColMap = new TreeMap();
        int desiredNameCol = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals("Comment [Sample_source_name]")) {
                desiredNameCol = i;
            } else if (headers[i].equals("Comment [Sample_title]")) {
                desiredNameCol = i;
            } else if ((headers[i].equals("Hybridization Name")) && (desiredNameCol < 0)) {
                desiredNameCol = i;
            } else if ((headers[i].equals("Sample Name")) && (!splitLine[i].matches("GSE\\d+GSM\\d+ sample"))) {
                desiredNameCol = i;
            } else if (headers[i].equals("Scan Name")) {
                desiredNameCol = i;
            } else if (headers[i].equals("Comment [Sample_title]")) {
                desiredNameCol = i;
            } else if (headers[i].equals("Description")) {
                goodColMap.put(Integer.valueOf(i), headers[i]);
            } else if ((headers[i].startsWith("FactorValue")) ||
                    (headers[i].startsWith("Characteristics")) || (headers[i].startsWith("Comment"))) {
                String name = headers[i].substring(headers[i].lastIndexOf('[') + 1,
                        headers[i].lastIndexOf(']'));
                if ((!name.contains("FTP")) && (!name.contains("URI"))) {
                    goodColMap.put(Integer.valueOf(i), name);
                }
            }
        }
        do {
            splitLine = thisLine.split("\t");
            int nameCol = -1;
            for (int i = 0; (i < splitLine.length) && (nameCol < 0); i++) {
                if (samples.contains(splitLine[i])) {
                    nameCol = i;
                }
            }
            if (nameCol >= 0) {

                String name = splitLine[nameCol];
                ArrayList<String[]> thisMetadata = new ArrayList();


                String trueName = splitLine[desiredNameCol].trim();
                if (trueName.equals("")) {
                    trueName = name;
                }
                int rep = 2;
                while (sampleNameMap.containsValue(trueName)) {
                    trueName = splitLine[desiredNameCol] + "-" + rep;
                    rep++;
                }
                sampleNameMap.put(name, trueName);
                Set<Integer> goodCols = goodColMap.keySet();
                for (Integer col : goodCols) {
                    String key = goodColMap.get(col);
                    String value = splitLine[col.intValue()];
                    thisMetadata.add(new String[]{key, value});
                }
                result.put(name, thisMetadata);
            }
        } while ((thisLine = in.readLine()) != null);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dest));
        out.writeObject(result);
        out.close();
        return dest;
    }

    public TreeMap<String, ArrayList<String[]>> getSDRFData() throws Exception {
        if (files == null) {
            files = FileListParser.getFileList(accession);
        }
        TreeMap<String, ArrayList<String[]>> result = new TreeMap();
        TreeMap<String, String> sampleNameMap = new TreeMap();
        int sdrfcount = 0;
        URL source = null;
        String filename = null;
        for (FileInfo thisFile : files)
            if (("sdrf".equals(thisFile.kind)) && ("txt".equals(Utils.getExtension(name)))) {

                sdrfcount++;
                if (source == null) {
                    source = new URL(thisFile.url);
                    filename = name;
                    System.out.println(accession + " Found sdrf: " + filename);
                }
            }
        if (sdrfcount > 1) {
            System.err.println(sdrfcount + " sdrfs detected in " + accession);
        }
        if (source == null) {
            return null;
        }
        System.out.println("Ready to download: " + source);
        URLConnection conn = source.openConnection();
        int attempts = 0;
        boolean success = false;
        while ((!success) && (attempts < 5)) {
            try {
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                conn.connect();
                success = true;
            } catch (Exception e) {
                attempts++;
                System.err.println("Connection attempt " + attempts + " failed.");
                e.printStackTrace();
            }
        }

        if (!success) {
            System.err.println("Unable to download file: " + source);
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String[] headers = in.readLine().split("\t");
        String thisLine = in.readLine();
        String[] splitLine = thisLine.split("\t");
        TreeMap<Integer, String> goodColMap = new TreeMap();
        int desiredNameCol = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals("Comment [Sample_source_name]")) {
                desiredNameCol = i;
            } else if ((headers[i].equals("Hybridization Name")) && (desiredNameCol < 0)) {
                desiredNameCol = i;
            } else if (headers[i].equals("Scan Name")) {
                desiredNameCol = i;
            } else if ((headers[i].equals("Sample Name")) && (!splitLine[i].matches("GSE\\d+GSM\\d+ sample"))) {
                desiredNameCol = i;
            } else if (headers[i].equals("Comment [Sample_title]")) {
                desiredNameCol = i;
            } else if (headers[i].equals("Description")) {
                goodColMap.put(Integer.valueOf(i), headers[i]);
            } else if ((headers[i].startsWith("FactorValue")) ||
                    (headers[i].startsWith("Characteristics")) || (headers[i].startsWith("Comment"))) {
                String name = headers[i].substring(headers[i].lastIndexOf('[') + 1,
                        headers[i].lastIndexOf(']'));
                if ((!name.contains("FTP")) && (!name.contains("URI"))) {
                    goodColMap.put(Integer.valueOf(i), name);
                }
            }
        }
        do {
            splitLine = thisLine.split("\t");
            int nameCol = -1;
            for (int i = 0; (i < splitLine.length) && (nameCol < 0); i++) {
                if (samples.contains(splitLine[i])) {
                    nameCol = i;
                }
            }
            if (nameCol >= 0) {

                String name = splitLine[nameCol];
                ArrayList<String[]> thisMetadata = new ArrayList();


                String trueName = splitLine[desiredNameCol].trim();
                if (trueName.equals("")) {
                    trueName = name;
                }
                int rep = 2;
                while (sampleNameMap.containsValue(trueName)) {
                    trueName = splitLine[desiredNameCol] + "-" + rep;
                    rep++;
                }
                sampleNameMap.put(name, trueName);
                Set<Integer> goodCols = goodColMap.keySet();
                for (Integer col : goodCols) {
                    String key = goodColMap.get(col);
                    String value = splitLine[col.intValue()];
                    thisMetadata.add(new String[]{key, value});
                }
                result.put(name, thisMetadata);
            }
        } while ((thisLine = in.readLine()) != null);
        return result;
    }

    public TreeMap<String, Element[]> getSDRFData(Collection<String> samples) throws Exception {
        if (files == null) {
            files = FileListParser.getFileList(accession);
        }
        int sdrfcount = 0;
        URL source = null;
        String filename = null;
        for (FileInfo thisFile : files)
            if (("sdrf".equals(thisFile.kind)) && ("txt".equals(Utils.getExtension(name)))) {

                sdrfcount++;
                if (source == null) {
                    source = new URL(thisFile.url);
                    filename = name;
                    System.out.println(accession + " Found sdrf: " + filename);
                }
            }
        if (sdrfcount > 1) {
            System.err.println(sdrfcount + " sdrfs detected in " + accession);
        }
        if (source == null) {
            return null;
        }
        System.out.println("Ready to download: " + source);
        URLConnection conn = source.openConnection();
        int attempts = 0;
        boolean success = false;
        while ((!success) && (attempts < 5)) {
            try {
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                conn.connect();
                success = true;
            } catch (Exception e) {
                attempts++;
                System.err.println("Connection attempt " + attempts + " failed.");
                e.printStackTrace();
            }
        }

        if (!success) {
            System.err.println("Unable to download file: " + source);
            return null;
        }

        boolean isCel = false;
        String firstSample = samples.iterator().next();
        if ((firstSample.endsWith(".CEL")) && (firstSample.startsWith("GSM"))) {
            isCel = true;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String[] headers = in.readLine().split("\t");
        int nameCol = -1;
        int hybCol = -1;
        int scanCol = -1;
        ArrayList<Integer> goodCols = new ArrayList();
        ArrayList<String> colNames = new ArrayList();
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals("Comment [Sample_source_name]")) {
                System.out.println("Found sample source name");
                hybCol = i;
            } else if ((headers[i].equals("Hybridization Name")) && (hybCol < 0)) {
                hybCol = i;
            } else if (headers[i].equals("Sample Name")) {
                nameCol = i;
            } else if (headers[i].equals("Scan Name")) {
                scanCol = i;
            } else if (headers[i].equals("Description")) {
                goodCols.add(Integer.valueOf(i));
                colNames.add(headers[i]);
            } else if ((headers[i].startsWith("FactorValue")) ||
                    (headers[i].startsWith("Characteristics")) || (headers[i].startsWith("Comment"))) {
                String name = headers[i].substring(headers[i].lastIndexOf('['),
                        headers[i].lastIndexOf(']'));
                if ((!name.contains("FTP")) && (!name.contains("URI"))) {
                    colNames.add(name);
                    goodCols.add(Integer.valueOf(i));
                }
            }
        }
        if (nameCol < 0) {
            if (hybCol >= 0) {
                nameCol = hybCol;
            } else {
                return null;
            }
        }
        TreeMap<String, Element[]> result = new TreeMap();

        System.out.println("scanCol=" + scanCol + ", hybCol=" + hybCol);
        String thisLine;
        while ((thisLine = in.readLine()) != null) {// String thisLine;
            String[] splitLine = thisLine.split("\t");
            if ((splitLine.length > nameCol) && (!splitLine[nameCol].equals(""))) {

                if (!samples.contains(splitLine[nameCol])) {
                    if ((hybCol >= 0) && (samples.contains(splitLine[hybCol]))) {
                        nameCol = hybCol;
                    } else if ((scanCol >= 0) && (samples.contains(splitLine[scanCol]))) {
                        nameCol = scanCol;
                    } else if (samples.contains(splitLine[0])) {
                        nameCol = 0;
                    }
                }


                int index = 0;
                Element[] metadata;
                if ((isCel) && (hybCol >= 0)) {
                    metadata = new Element[goodCols.size() + 1];
                    String name = "HybridizationName";
                    if ((splitLine.length <= hybCol) || (splitLine[hybCol].equals(""))) {
                        metadata[0] = new Element(name);
                    } else {
                        metadata[0] = new Element(name).setText(splitLine[hybCol]);
                    }
                    index = 1;
                } else {
                    metadata = new Element[goodCols.size()];
                }

                for (int i = 0; i < goodCols.size(); i++) {
                    int colIndex = goodCols.get(i).intValue();
                    String name = colNames.get(i);
                    if ((splitLine.length <= colIndex) || (splitLine[colIndex].equals(""))) {
                        metadata[(index++)] = new Element(name);
                    } else {
                        metadata[(index++)] = new Element(name).setText(splitLine[colIndex]);
                    }
                }

                if (splitLine[nameCol].equals("null")) {
                    String key = splitLine[0];
                    result.put(splitLine[0], metadata);
                } else {
                    String key = splitLine[nameCol];
                    Pattern regex = Pattern.compile("(?<=GSE[\\d]{1,10})GSM\\d{1,10}(?= sample)",
                            128);
                    Matcher regexMatcher = regex.matcher(key);
                    if (regexMatcher.find()) {
                        key = regexMatcher.group() + ".CEL";
                    }
                    result.put(key, metadata);
                }
            }
        }
        return result;
    }

    public String getSoftware() throws Exception {
        if (files == null) {
            files = FileListParser.getFileList(accession);
        }
        int idfcount = 0;
        URL source = null;
        String filename = null;
        for (FileInfo thisFile : files)
            if (("idf".equals(thisFile.kind)) && ("txt".equals(Utils.getExtension(name)))) {

                idfcount++;
                if (source == null) {
                    source = new URL(thisFile.url);
                    filename = name;
                    System.out.println(accession + " Found idf: " + filename);
                }
            }
        if (idfcount > 1) {
            System.err.println(idfcount + " idfs detected in " + accession);
        }
        if (source == null) {
            return null;
        }
        System.out.println("Ready to download: " + source);
        URLConnection conn = source.openConnection();
        int attempts = 0;
        boolean success = false;
        while ((!success) && (attempts < 5)) {
            try {
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                conn.connect();
                success = true;
            } catch (Exception e) {
                attempts++;
                System.err.println("Connection attempt " + attempts + " failed.");
                e.printStackTrace();
            }
        }

        if (!success) {
            System.err.println("Unable to download file: " + source);
            return null;
        }

        String result = "Unknown";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String thisLine = in.readLine();

            while ((thisLine != null) && (!thisLine.startsWith("Protocol Software"))) {
                thisLine = in.readLine();
                try {
                    if (thisLine.contains("MAS 5.0")) {
                        result = "MicroArraySuite 5.0";
                    } else if (thisLine.contains(" RMA ")) {
                        result = "RMA";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (thisLine != null) {
                String[] splitLine = thisLine.split("\t+");
                if (splitLine.length > 1) {
                    result = splitLine[1];
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return result;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return result;
        }
        return result;
    }

    public Progress getDownloadProgress() {
        if (p == null) {
            p = new Progress(0, 100, 0);
        }
        return p;
    }

    public String getStatus() {
        return status;
    }

    public void abortDownload() {
        if ((getProcessedDataFile() != null) &&
                (!getProcessedDataFile().delete())) {
            getProcessedDataFile().deleteOnExit();
        }

        if ((getZippedDataFile() != null) &&
                (!getZippedDataFile().delete()))
            getZippedDataFile().deleteOnExit();
    }

    private static class FileInfo {
        String kind;
        String size;
        String url;
        String name;

        private FileInfo() {
        }
    }

    private static class FileListParser implements DocHandler {
        AEXMLNodeInfo.FileInfo thisFile;
        String thisText;
        ArrayList<AEXMLNodeInfo.FileInfo> files;

        public FileListParser() {
            thisFile = null;
            files = new ArrayList();
            thisText = null;
        }

        public static ArrayList<AEXMLNodeInfo.FileInfo> getFileList(String acc) throws Exception {
            FileListParser parser = new FileListParser();
            URL url = new URL("http://www.ebi.ac.uk/arrayexpress/xml/v2/files/" + acc);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            System.out.println("Getting file list...");
            QDParser.parse(parser, in);
            System.out.println("File list gotten!");
            return parser.getFileList();
        }

        public ArrayList<AEXMLNodeInfo.FileInfo> getFileList() {
            return files;
        }

        @Override
		public void startElement(String tag, Hashtable h) throws Exception {
            if ("file".equals(tag)) {
                thisFile = new AEXMLNodeInfo.FileInfo();
                return;
            }
        }

        @Override
		public void endElement(String tag) throws Exception {
            if ("kind".equals(tag)) {
                thisFile.kind = thisText;
                return;
            }

            if ("name".equals(tag)) {
                thisFile.name = thisText;
                return;
            }

            if ("size".equals(tag)) {
                thisFile.size = thisText;
                return;
            }

            if ("url".equals(tag)) {
                thisFile.url = thisText;
                return;
            }

            if ("file".equals(tag)) {
                files.add(thisFile);
            }
        }

        @Override
		public void startDocument()
                throws Exception {
        }

        @Override
		public void endDocument() throws Exception {
        }

        @Override
		public void text(String str) throws Exception {
            thisText = str;
        }
    }

    public void addChangeListener(ChangeListener l) {
        if (listeners == null) {
            listeners = new ArrayList();
        }
        listeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    protected void setStatus(String status) {
        this.status = status;
        fireChangeEvent();
    }

    private void fireChangeEvent() {
        if ((listeners == null) || (listeners.size() == 0)) {
            return;
        }
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(e);
        }
    }

    public void setColumnIndex(String sampleName, int colIndex) {
        if (colIndexMap == null) {
            colIndexMap = new TreeMap();
        }
        colIndexMap.put(sampleName, Integer.valueOf(colIndex));
    }

    public int getMIAMEScore() {
        return miameScore;
    }

    public static void loadDictionary() {
        dictionary = new TreeMap();
        dictionary.put("AGE", "Age");
        dictionary.put("AGENT", "Agent");
        dictionary.put("Age", "Age");
        dictionary.put("BioSourceProvider", "BioSource Provider");
        dictionary.put("BioSourceType", "BioSource Type");
        dictionary.put("COMPOUND", "Compound");
        dictionary.put("CellLine", "Cell Line");
        dictionary.put("CellType", "Cell Type");
        dictionary.put("Cultivar", "Cultivar");
        dictionary.put("DEVELOPMENTAL STAGE", "Developmental Stage");
        dictionary.put("Description", "Description");
        dictionary.put("DevelopmentalStage", "Developmental Stage");
        dictionary.put("ECOTYPE", "Ecotype");
        dictionary.put("EXP", "");
        dictionary.put("Ecotype", "Ecotype");
        dictionary.put("Experiment Name", "Experiment Name");
        dictionary.put("Experiment Start Date", "Experiment Start Date");
        dictionary.put("FLOWER STAGE", "Flower Stage");
        dictionary.put("GENOTYPE", "Genotype");
        dictionary.put("GENOTYPE/VARIATION", "Genotype");
        dictionary.put("GROWTH CONDITION", "Growth Condition");
        dictionary.put("GROWTH PROTOCOL (BEFORE START OF EXPT)", "Growth Condition");
        dictionary.put("GeneticModification", "Genetic Modification");
        dictionary.put("Genotype", "Genotype");
        dictionary.put("GrowthCondition", "Growth Condition");
        dictionary.put("INDIVIDUAL", "Individual");
        dictionary.put("INFECTION", "Infection");
        dictionary.put("Individual", "Individual");
        dictionary.put("IndividualGeneticCharacteristics", "Individual Genetic Characteristics");
        dictionary.put("InitialTimePoint", "Initial Time Point");
        dictionary.put("MaterialSample", "Material Sample");
        dictionary.put("ORGANISM", "Organism");
        dictionary.put("ORGANISM PART", "Organism Part");
        dictionary.put("Organism", "Organism");
        dictionary.put("OrganismPa", "Organism Part");
        dictionary.put("OrganismPart", "Organism Part");
        dictionary.put("Original Name", "Original Name");
        dictionary.put("PROTOCOL", "Protocol");
        dictionary.put("REPLICATE", "Replicate");
        dictionary.put("SAMPLING TIME POINT", "Sampling Time Point");
        dictionary.put("STAGE", "Stage");
        dictionary.put("Sample Name", "Sample Name");
        dictionary.put("Sample_characteristics", "Sample Characteristics");
        dictionary.put("Sample_description", "Sample Description");
        dictionary.put("Stage", "Stage");
        dictionary.put("Statistical Analysis", "Statistical Analysis");
        dictionary.put("StrainOrLine", "Strain or Line");
        dictionary.put("TEMPERATURE", "Temperature");
        dictionary.put("TIME", "Time");
        dictionary.put("TREATMENT", "Treatment");
        dictionary.put("TREATMENT DURATION", "Treatment Duration");
        dictionary.put("TREATMENT TIME", "Treatment Time");
        dictionary.put("TimeUnit", "Time Unit");
        dictionary.put("VARIATION", "Variation");
        dictionary.put("accession", "Accession");
        dictionary.put("age", "Age");
        dictionary.put("assays", "Assays");
        dictionary.put("background", "Background");
        dictionary.put("bibliography", "Bibliography");
        dictionary.put("cultivar", "Cultivar");
        dictionary.put("description", "Description");
        dictionary.put("development stage", "Developmental Stage");
        dictionary.put("developmental stage", "Developmental Stage");
        dictionary.put("ecotype", "Ecotype");
        dictionary.put("ecotype background", "Ecotype Background");
        dictionary.put("experimentalfactor: 1 LEU2", "");
        dictionary.put("experimentalfactor: ABIOTIC STRESS", "Environmental Stress");
        dictionary.put("experimentalfactor: AFFYMETRIX RNA LABELLING PROTOCOL",
                "Affymetrix RNA Labelling Protocol");
        dictionary.put("experimentalfactor: AGE", "Development");
        dictionary.put("experimentalfactor: AGENT", "Chemical");
        dictionary.put("experimentalfactor: BIOSOURCEPROVIDER", "BioSource Provider");
        dictionary.put("experimentalfactor: CARBON SOURCE", "Chemical");
        dictionary.put("experimentalfactor: CELL TYPE", "Organism Part");
        dictionary.put("experimentalfactor: COLLECTION TIME AFTER 3AT TREATMENT", "Time");
        dictionary.put("experimentalfactor: COMPOUND", "Chemical");
        dictionary.put("experimentalfactor: CULTURE AGE (DAYS)", "Time");
        dictionary.put(
                "experimentalfactor: Chemostat culture dilution rate (culture volume per hour)",
                "Growth Condition");
        dictionary.put(
                "experimentalfactor: Chemostat culture dilution rate culture volume per hour",
                "Growth Condition");
        dictionary.put("experimentalfactor: Compound", "Chemical");
        dictionary.put("experimentalfactor: Compound based treatment", "Chemical");
        dictionary.put("experimentalfactor: DEVELOPMENTAL STAGE", "Development");
        dictionary.put("experimentalfactor: DEVELOPMENTAL_STAGE", "Development");
        dictionary.put("experimentalfactor: DevelopmentalStage", "Development");
        dictionary.put("experimentalfactor: ECOTYPE", "Genotype");
        dictionary.put("experimentalfactor: EnvironmentalStress", "Environmental Stress");
        dictionary.put("experimentalfactor: FLOWER STAGE", "Development");
        dictionary.put("experimentalfactor: GENOTYPE", "Genotype");
        dictionary.put("experimentalfactor: GENOTYPE/VARIATION", "Genotype");
        dictionary.put("experimentalfactor: GROWTH CONDITION", "Growth Condition");
        dictionary.put("experimentalfactor: GROWTH PROTOCOL (BEFORE START OF EXPT)",
                "Growth Condition");
        dictionary.put("experimentalfactor: GROWTHCONDITION", "Growth Condition");
        dictionary.put("experimentalfactor: GSH LOCALIZATION", "GSH Localization");
        dictionary.put("experimentalfactor: Genotype", "Genotype");
        dictionary.put("experimentalfactor: GrowthCondition", "Growth Condition");
        dictionary.put("experimentalfactor: INDIVIDUAL", "Individual");
        dictionary.put("experimentalfactor: INFECTION", "Environmental Stress");
        dictionary.put("experimentalfactor: INJURY", "Environmental Stress");
        dictionary.put("experimentalfactor: KARYOTYPE", "Genotype");
        dictionary.put("experimentalfactor: Limiting nutrient", "Growth Condition");
        dictionary.put("experimentalfactor: MEDIUM", "Growth Condition");
        dictionary.put("experimentalfactor: MUTANT", "Genotype");
        dictionary.put("experimentalfactor: Material type", "Organism Part");
        dictionary.put("experimentalfactor: Media", "Chemical");
        dictionary.put("experimentalfactor: Nutrients", "Chemical");
        dictionary.put("experimentalfactor: ORGANISM", "Organism");
        dictionary.put("experimentalfactor: ORGANISM PART", "Organism Part");
        dictionary.put("experimentalfactor: ORGANISMPART", "Organism Part");
        dictionary.put("experimentalfactor: PROTOCOL", "Growth Condition");
        dictionary.put("experimentalfactor: REPLICATE", "Replicate");
        dictionary.put("experimentalfactor: Rate limiting nutrient", "Environmental Stress");
        dictionary.put("experimentalfactor: SAMPLING TIME POINT", "Time");
        dictionary.put("experimentalfactor: SPECIFIC GROWTH RATE (H-1)", "Time");
        dictionary.put("experimentalfactor: STAGE", "Development");
        dictionary.put("experimentalfactor: STARVATION DURATION (MINUTES)", "Environmental Stress");
        dictionary.put("experimentalfactor: STRAIN", "Genotype");
        dictionary.put("experimentalfactor: STRESS", "Environmental Stress");
        dictionary.put("experimentalfactor: StrainOrLine", "Genotype");
        dictionary.put("experimentalfactor: TEMPERATURE", "Environmental Stress");
        dictionary.put("experimentalfactor: TIME", "Time");
        dictionary.put("experimentalfactor: TIME POINT", "Time");
        dictionary.put("experimentalfactor: TIME POST-INOCULATION", "Time");
        dictionary.put("experimentalfactor: TREATMENT", "Treatment");
        dictionary.put("experimentalfactor: TREATMENT DURATION", "Time");
        dictionary.put("experimentalfactor: TREATMENT PROTOCOL", "Treatment");
        dictionary.put("experimentalfactor: TREATMENT TIME", "Time");
        dictionary.put("experimentalfactor: Temperature", "Environmental Stress");
        dictionary.put("experimentalfactor: Time", "Time");
        dictionary.put("experimentalfactor: VARIATION", "Genotype");
        dictionary.put("experimentalfactor: clinical information", "Clinical Information");
        dictionary.put("experimentalfactor: compound", "Chemical");
        dictionary.put("experimentalfactor: dose", "Chemical");
        dictionary.put("experimentalfactor: genotype", "Genotype");
        dictionary.put("experimentalfactor: growth condition", "Growth Condition");
        dictionary.put("experimentalfactor: material type", "Organism Part");
        dictionary.put("experimentalfactor: media", "Growth Condition");
        dictionary.put("experimentalfactor: strain", "Genotype");
        dictionary.put("experimentalfactor: strain or line", "Genotype");
        dictionary.put("experimentalfactor: time", "Time");
        dictionary.put("experimentdesign", "Experiment Design");
        dictionary.put("experimenttype", "Experiment Type");
        dictionary.put("flower stage", "Flower Stage");
        dictionary.put("genetic background", "Genetic Background");
        dictionary.put("genetic variation", "Genetic Variation");
        dictionary.put("genome/variation", "Genome");
        dictionary.put("genotype", "Genotype");
        dictionary.put("genotype/variation", "Genotype");
        dictionary.put("germplasm name", "Germplasm");
        dictionary.put("growth media", "Growth Condition");
        dictionary.put("growth protocol (before start of expt)", "Growth Condition");
        dictionary.put("gsh localization", "gsh localization");
        dictionary.put("infection", "Environmental Stress");
        dictionary.put("lastupdatedate", "Last Updated");
        dictionary.put("miamescores", "MIAME Scores");
        dictionary.put("mutant", "Genotype");
        dictionary.put("name", "Name");
        dictionary.put("organ", "Organism Part");
        dictionary.put("organism part", "Organism Part");
        dictionary.put("provider", "Provider");
        dictionary.put("releasedate", "Release Date");
        dictionary.put("sample source", "Sample Source");
        dictionary.put("samples", "Samples");
        dictionary.put("secondaryaccession", "Secondary Accession");
        dictionary.put("species", "Species");
        dictionary.put("stock code", "Stock Code");
        dictionary.put("strain", "Strain");
        dictionary.put("strain/ecotype", "Ecotype");
        dictionary.put("submissiondate", "Submission Date");
        dictionary.put("system used", "System Used");
        dictionary.put("time", "Time");
        dictionary.put("time (of harvest)", "Time of Harvest");
        dictionary.put("tissue", "Organism Part");
        dictionary.put("treatment", "Treatment");
        dictionary.put("variation", "Variation");

        dictionary.put("growth conditions", "Growth Condition");
        dictionary.put("GROWTH CONDITIONS", "Growth Conditions");
    }

    public static void destroyDictionary() {
        dictionary = null;
    }
}
