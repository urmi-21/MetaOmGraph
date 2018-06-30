package edu.iastate.metnet.arrayexpress;

import edu.iastate.metnet.metaomgraph.XMLizable;
import edu.iastate.metnet.metaomgraph.utils.JDomUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Element;


public class ExperimentData
        implements XMLizable, TableDataNode, Comparable<Object> {
    public String id;
    public String name;
    public String array;
    public String sdrf;
    public String idf;
    public String date;
    public int hybs;
    public ArrayList<Attribute> sampleAttributes;
    public ArrayList<Attribute> factorValues;
    public ArrayList<Object> metadata;
    public boolean hasProcessedData;

    public ExperimentData() {
    }

    public String toString() {
        return
                id + " (" + hybs + "): " + name + " (" + date + ")                    ";
    }

    public void newFromXML(Element source) {
        id = source.getChildText("accession");
        name = source.getChildText("name");
    }

    public void fromXML(Element source) {
        id = source.getAttributeValue("accnum");
        name = source.getAttributeValue("name");
        array = source.getAttributeValue("array");

        sdrf = ("/files/" + id + "/" + id + ".sdrf.txt");
        idf = ("/files/" + id + "/" + id + ".idf.txt");
        date = source.getAttributeValue("releasedate");
        hybs = Integer.parseInt(source.getAttributeValue("hybs"));
        List children = source.getChild("sampleattributes").getChildren(
                "sampleattribute");
        sampleAttributes = new ArrayList();
        for (Object thisChild : children) {
            if ((thisChild instanceof Element)) {
                Element elem = (Element) thisChild;
                String category = JDomUtils.convertToValidElementName(elem
                        .getAttributeValue("CATEGORY"));
                String value = elem.getAttributeValue("VALUE");
                if ((category != null) && (value != null)) {
                    sampleAttributes.add(new Attribute(category, value));
                }
            }
        }
        if (source.getChild("factorvalues") != null) {
            children = source.getChild("factorvalues").getChildren(
                    "factorvalue");
            factorValues = new ArrayList();
            for (Object thisChild : children) {
                if ((thisChild instanceof Element)) {
                    Element elem = (Element) thisChild;
                    if (elem.getAttributeValue("FACTORNAME") != null) {
                        String category =
                                JDomUtils.convertToValidElementName(elem
                                        .getAttributeValue("FACTORNAME"));
                        String value = elem.getAttributeValue("FV_OE");
                        if ((category != null) && (value != null)) {
                            factorValues.add(new Attribute(category, value));
                        }
                    }
                }
            }
        }
        hasProcessedData = false;
        children = source.getChild("bioassaydatagroups").getChildren();
        for (Object thisChild : children) {
            if ((thisChild instanceof Element)) {
                if (((Element) thisChild).getAttributeValue("is_derived").equals("1")) {
                    hasProcessedData = true;
                }
            }
        }
    }

    public Element toXML() {
        return null;
    }

    public static ExperimentData createFromXML(Element source) {
        ExperimentData result = new ExperimentData();
        result.fromXML(source);
        return result;
    }

    public int getAttributeCount() {
        return 3 + sampleAttributes.size() + factorValues.size();
    }

    public Object[][] getTableData() {
        String[][] tableData = new String[getAttributeCount()][2];
        int row = 0;

        tableData[row++] = new String[]{"Name", name};
        tableData[row++] = new String[]{"ID", id};
        tableData[row++] = new String[]{"Array", array};

        for (Attribute attrib : sampleAttributes) {
            tableData[row++] = new String[]{attrib.getName(),
                    attrib.getValue()};
        }
        for (Attribute attrib : factorValues) {
            tableData[row++] = new String[]{attrib.getName(),
                    attrib.getValue()};
        }
        return tableData;

    }

    public String[] getTableHeaders() {
        return new String[]{"Attribute", "Value"};
    }

    public String getSoftware() {
        String result = "Unknown";
        try {
            URLConnection conn = new URL("http://www.ebi.ac.uk/microarray-as/ae/" + idf)
                    .openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String thisLine;
            do {
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
            } while ((thisLine != null) && (!
                    thisLine.startsWith("Protocol Software")));
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
        URLConnection conn;
        return result;
    }

    public TreeMap<String, Element[]> getSDRFData(Collection<String> samples) throws IOException {
        if ((sdrf == null) || (sdrf.equals(""))) {
            return null;
        }
        boolean isCel = false;
        String firstSample = samples.iterator().next();
        if ((firstSample.endsWith(".CEL")) && (firstSample.startsWith("GSM"))) {
            isCel = true;
        }
        try {
            URLConnection conn;
            conn = new URL("http://www.ebi.ac.uk/microarray-as/ae/" + sdrf).openConnection();
        } catch (MalformedURLException e) {
            URLConnection conn;
            e.printStackTrace();
            return null;
        }
        URLConnection conn = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(conn
                .getInputStream()));
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
                    (headers[i].startsWith("Characteristics")) ||
                    (headers[i].startsWith("Comment"))) {
                String name = headers[i].substring(headers[i].lastIndexOf('['),
                        headers[i].lastIndexOf(']'));
                if ((!name.contains("FTP")) && (!name.contains("URI"))) {
                    name = JDomUtils.convertToValidElementName(name);
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
        while ((thisLine = in.readLine()) != null) { //String thisLine;
            String[] splitLine = thisLine.split("\t");
            if ((splitLine.length > nameCol) && (!splitLine[nameCol].equals(""))) {

                if (!samples.contains(splitLine[nameCol])) {
                    if ((hybCol >= 0) && (samples.contains(splitLine[hybCol]))) {
                        nameCol = hybCol;
                    } else if ((scanCol >= 0) && (samples.contains(splitLine[scanCol]))) {
                        nameCol = scanCol;
                    }
                }


                Element[] metadata;
                int index = 0;
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
                    if ((splitLine.length <= colIndex) ||
                            (splitLine[colIndex].equals(""))) {
                        metadata[(index++)] = new Element(name);
                    } else {
                        metadata[(index++)] = new Element(name)
                                .setText(splitLine[colIndex]);
                    }
                }

                if (splitLine[nameCol].equals("null")) {
                    String key = splitLine[0];
                    result.put(splitLine[0], metadata);
                } else {
                    String key = splitLine[nameCol];
                    Pattern regex = Pattern.compile(
                            "(?<=GSE[\\d]{1,10})GSM\\d{1,10}(?= sample)",
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

    public void addMetadata(Object metadata) {
        if (this.metadata == null) {
            this.metadata = new ArrayList();
        }
        this.metadata.add(metadata);
    }

    public ArrayList<Object> getMetadata() {
        if (metadata == null) {
            return new ArrayList();
        }
        return metadata;
    }

    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        if ((o instanceof ExperimentData)) {
            return id.compareTo(id);
        }
        return toString().compareTo(o.toString());
    }
}
