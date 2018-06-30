package edu.iastate.metnet.arrayexpress;

import java.util.List;
import java.util.TreeSet;

import org.jdom.Element;

public class SampleAttributes implements edu.iastate.metnet.metaomgraph.XMLizable, TableDataNode {
    private TreeSet<SampleAttribute> values;

    public SampleAttributes() {
    }

    private class SampleAttribute implements Comparable {
        public String category;
        public String value;

        public SampleAttribute(String category, String value) {
            this.category = category;
            this.value = value;
        }

        public int compareTo(Object o) {
            return category.compareTo(o + "");
        }

        public String toString() {
            return category;
        }
    }

    public static SampleAttributes createFromXML(Element source) {
        SampleAttributes result = new SampleAttributes();
        result.fromXML(source);
        return result;
    }

    public void fromXML(Element source) {
        if (!"sampleattributes".equals(source.getName().toLowerCase())) {
            throw new IllegalArgumentException("Argument must be \"sampleattributes\" element");
        }
        values = new TreeSet();
        List children = source.getChildren("sampleattribute");
        for (Object child : children) {
            String category = ((Element) child).getAttributeValue("CATEGORY");
            String value = ((Element) child).getAttributeValue("VALUE");
            values.add(new SampleAttribute(category, value));
        }
    }

    public Element toXML() {
        return null;
    }

    public Object[][] getTableData() {
        Object[][] result = new Object[values.size()][2];
        int index = 0;
        for (SampleAttribute addMe : values) {
            result[index][0] = addMe.category;
            result[(index++)][1] = addMe.value;
        }
        return result;
    }

    public String[] getTableHeaders() {
        return new String[]{"Category", "Value"};
    }

    public String toString() {
        return "Sample Attributes";
    }
}
