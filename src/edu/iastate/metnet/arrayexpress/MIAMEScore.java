package edu.iastate.metnet.arrayexpress;

import edu.iastate.metnet.metaomgraph.XMLizable;

import java.util.List;

import org.jdom.Element;


public class MIAMEScore
        implements XMLizable, TableDataNode {
    int array;
    int protocols;
    int factors;
    int raw;
    int processed;
    int score;

    public MIAMEScore() {
    }

    public static MIAMEScore createFromXML(Element source) {
        MIAMEScore result = new MIAMEScore();
        result.fromXML(source);
        return result;
    }

    public void fromXML(Element source) {
        if (!"miamescores".equals(source.getName())) {
            throw new IllegalArgumentException("source must be the root \"miamescores\" element");
        }
        try {
            score = Integer.parseInt(source.getAttributeValue("miamescore"));
        } catch (NumberFormatException nfe) {
            score = 0;
        }
        List children = source.getChildren("miamescore");
        for (Object child : children) {
            String name = ((Element) child).getAttributeValue("name");
            int value = 0;
            try {
                value = Integer.parseInt(((Element) child).getAttributeValue("value"));
            } catch (NumberFormatException localNumberFormatException1) {
            }
            if ("ReporterSequenceScore".equals(name)) {
                array = value;
            } else if ("FactorValueScore".equals(name)) {
                factors = value;
            } else if ("MeasuredBioAssayDataScore".equals(name)) {
                raw = value;
            } else if ("ProtocolScore".equals(name)) {
                protocols = value;
            } else if ("DerivedBioAssayDataScore".equals(name)) {
                processed = value;
            }
        }
    }

    public Element toXML() {
        return null;
    }

    public Object[][] getTableData() {
        Object[][] result = new Object[5][2];
        result[0][0] = "Reporter Sequence";
        result[0][1] = array;
        result[1][0] = "Protocols";
        result[1][1] = protocols;
        result[2][0] = "Factor Value";
        result[2][1] = factors;
        result[3][0] = "Measured BioAssay Data";
        result[3][1] = raw;
        result[4][0] = "Derived BioAssay Data";
        result[4][1] = processed;
        return result;
    }

    public String[] getTableHeaders() {
        return new String[]{"Category", "Score"};
    }

    public String toString() {
        return "MIAME Score: " + score;
    }
}
