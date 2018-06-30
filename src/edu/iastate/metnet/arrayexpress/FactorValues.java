package edu.iastate.metnet.arrayexpress;

import java.util.List;
import java.util.TreeSet;

import org.jdom.Element;

public class FactorValues implements edu.iastate.metnet.metaomgraph.XMLizable, TableDataNode {
    private TreeSet<FactorValue> values;

    public FactorValues() {
    }

    private class FactorValue implements Comparable {
        public String category;
        public String value;

        public FactorValue(String category, String value) {
            this.category = category;
            this.value = value;
        }

        public int compareTo(Object o) {
            return category.compareTo((String) o);
        }

        public String toString() {
            return category;
        }
    }

    public static FactorValues createFromXML(Element source) {
        FactorValues result = new FactorValues();
        result.fromXML(source);
        return result;
    }

    public void fromXML(Element source) {
        values = new TreeSet();
        List children = source.getChildren("sampleattribute");
        for (Object child : children) {
            Element e = (Element) child;
            String category = e.getAttributeValue("FACTORNAME");
            String value = e.getAttributeValue("FV_OE");
            if (value == null) {
                value = e.getAttributeValue("FV_MEASUREMENT");
            }
            values.add(new FactorValue(category, value));
        }
    }

    public Element toXML() {
        return null;
    }

    public Object[][] getTableData() {
        Object[][] result = new Object[values.size()][2];
        int index = 0;
        for (FactorValue addMe : values) {
            result[index][0] = addMe.category;
            result[(index++)][1] = addMe.value;
        }
        return result;
    }

    public String[] getTableHeaders() {
        return new String[]{"Name", "Value"};
    }

    public String toString() {
        return "Factor Values";
    }
}
