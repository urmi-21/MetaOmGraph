package edu.iastate.metnet.arrayexpress;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class ExperimentDesigns implements TableDataNode, edu.iastate.metnet.metaomgraph.XMLizable {
    private ArrayList<String> designs;

    public ExperimentDesigns() {
    }

    public Object[][] getTableData() {
        Object[][] result = new Object[designs.size()][1];
        int i = 0;
        for (String thisDesign : designs) {
            result[(i++)][0] = thisDesign;
        }
        return result;
    }

    public String[] getTableHeaders() {
        return new String[]{"Design Type"};
    }

    public void fromXML(Element source) {
        designs = new ArrayList();
        List children = source.getChildren("experimentdesign");
        for (Object thisChild : children) {
            designs.add(((Element) thisChild).getAttributeValue("type"));
        }
    }

    public Element toXML() {
        return null;
    }

    public static ExperimentDesigns createFromXML(Element source) {
        ExperimentDesigns result = new ExperimentDesigns();
        result.fromXML(source);
        return result;
    }

    public String toString() {
        return "Experiment Designs";
    }

    public ArrayList<String> getDesigns() {
        return designs;
    }
}
