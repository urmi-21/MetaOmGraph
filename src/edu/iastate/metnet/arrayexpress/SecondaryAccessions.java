package edu.iastate.metnet.arrayexpress;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class SecondaryAccessions implements TableDataNode, edu.iastate.metnet.metaomgraph.XMLizable {
    private ArrayList<String> accessions;

    public SecondaryAccessions() {
    }

    public Object[][] getTableData() {
        Object[][] result = new Object[accessions.size()][1];
        int i = 0;
        for (String thisDesign : accessions) {
            result[(i++)][0] = thisDesign;
        }
        return result;
    }

    public String[] getTableHeaders() {
        return new String[]{"Secondary Accession"};
    }

    public void fromXML(Element source) {
        accessions = new ArrayList();
        List children = source.getChildren("secondaryaccession");
        for (Object thisChild : children) {
            String text = ((Element) thisChild).getText();
            if ((text != null) && (!text.equals(""))) {
                accessions.add(((Element) thisChild).getText());
            }
        }
    }

    public Element toXML() {
        return null;
    }

    public static SecondaryAccessions createFromXML(Element source) {
        SecondaryAccessions result = new SecondaryAccessions();
        result.fromXML(source);
        return result;
    }

    public String toString() {
        return "Secondary Accessions";
    }
}
