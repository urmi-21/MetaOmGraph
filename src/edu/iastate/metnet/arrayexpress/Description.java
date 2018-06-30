package edu.iastate.metnet.arrayexpress;

import org.jdom.Element;

public class Description implements TableDataNode, edu.iastate.metnet.metaomgraph.XMLizable {
    public String descrip;
    public boolean generated;

    public Description() {
    }

    public static Description createFromXML(Element source) {
        Description result = new Description();
        result.fromXML(source);
        return result;
    }

    public Object[][] getTableData() {
        Object[][] result = new Object[1][1];
        result[0][0] = descrip;
        return result;
    }

    public String[] getTableHeaders() {
        return new String[]{"Description"};
    }

    public void fromXML(Element source) {
        descrip = source.getValue();
        if (descrip.startsWith("(Generated description) ")) {
            generated = true;
            descrip = descrip.substring("(Generated description) ".length());
        } else {
            generated = false;
        }
    }

    public Element toXML() {
        return null;
    }

    public String toString() {
        if (generated) {
            return "Generated Description";
        }
        return "Description";
    }
}
