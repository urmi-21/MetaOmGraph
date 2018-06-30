package edu.iastate.metnet.arrayexpress;

import edu.iastate.metnet.metaomgraph.XMLizable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.jdom.Element;


public class FullExperimentData
        implements XMLizable, TableDataNode, Comparable<Object> {
    public String id;
    public String name;
    public String array;
    public String sdrf;
    public String fgem;
    public String date;
    public ArrayList<String> descrips;
    public ArrayList<String> genDescrips;
    public ArrayList<String> expDesigns;
    public ArrayList<String> expTypes;
    public String species;
    public int hybs;
    public boolean hasProcessedData;
    public TreeMap<String, ArrayList<String>> attributes;

    public FullExperimentData() {
    }

    public void fromXML(Element source) {
        id = source.getChildText("accession");
        name = source.getChildText("name");
        date = source.getChildText("releasedate");
        species = source.getChildText("species");
        hybs = Integer.parseInt(source.getChildText("assays"));

        descrips = new ArrayList();
        genDescrips = new ArrayList();
        List childList = source.getChildren("description");
        for (Object o : childList) {
            Element e = (Element) o;
            String text = e.getChildText("text");
            if ((text != null) && (!"".equals(text))) {

                if (text.startsWith("(Generated description)")) {
                    genDescrips.add(text.substring("(Generated description) ".length()));
                } else {
                    descrips.add(text);
                }
            }
        }
        expDesigns = new ArrayList();
        childList = source.getChildren("experimentdesign");
        for (Object o : childList) {
            String text = ((Element) o).getText();
            if (!"".equals(text)) {
                expDesigns.add(text);
            }
        }
        expTypes = new ArrayList();
        childList = source.getChildren("experimenttype");
        for (Object o : childList) {
            String text = ((Element) o).getText();
            if (!"".equals(text)) {
                expTypes.add(text);
            }
        }
        attributes = new TreeMap();
        childList = source.getChildren("sampleattribute");
        for (Object o : childList) {
            Element e = (Element) o;
            String key = e.getChildText("category");
            ArrayList<String> value = new ArrayList();
            List vals = e.getChildren("value");
            for (Object o2 : vals) {
                String text = ((Element) o2).getText();
                if (!"".equals(text)) {
                    value.add(text);
                }
            }
            if (!attributes.containsKey(key)) {
                attributes.put(key, value);
            } else {
                System.err.println(id + ": Duplicate attribute: " + key);
            }
        }
        childList = source.getChildren("experimentalfactor");
        for (Object o : childList) {
            Element e = (Element) o;
            String key = e.getChildText("name");
            ArrayList<String> value = new ArrayList();
            List vals = e.getChildren("value");
            for (Object o2 : vals) {
                String text = ((Element) o2).getText();
                if (!"".equals(text)) {
                    value.add(text);
                }
            }
            if (!attributes.containsKey(key)) {
                attributes.put(key, value);
            } else {
                System.err.println(id + ": Duplicate attribute: " + key);
            }
        }
    }

    public Element toXML() {
        return null;
    }

    public Object[][] getTableData() {
        return null;
    }

    public String[] getTableHeaders() {
        return null;
    }


    public int compareTo(Object o) {
        return 0;
    }

    private static class Provider {
        public String role;
        public String name;
        public String email;

        public Provider(String role, String name, String email) {
            this.role = role;
            this.name = name;
            this.email = email;
        }
    }
}
