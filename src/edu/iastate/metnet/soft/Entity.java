package edu.iastate.metnet.soft;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


public abstract class Entity {
    private Vector<Attribute> attributes;
    private String identifier;
    private Hashtable<String, String> headerDescriptors;
    private String[] headers;
    private Object[][] tableData;
    private int tableValueColumn;
    private int tableIDColumn;
    private long[] rowPointers;

    public abstract boolean isValidAttribute(String paramString);

    public abstract String[] getRequiredAttributes();

    public Entity(String identifier) {
        this.identifier = identifier;
        tableValueColumn = -1;
        tableIDColumn = -1;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Vector getMissingAttributes() {
        String[] reqAttribs = getRequiredAttributes();
        Vector<String> result = new Vector();
        for (String attrib : reqAttribs) {
            if (!attributes.contains(attrib))
                result.add(attrib);
        }
        return result;
    }

    public boolean hasRequiredAttributes() {
        String[] reqAttribs = getRequiredAttributes();
        if (reqAttribs == null)
            return true;
        Arrays.sort(reqAttribs);
        int reqnum = reqAttribs.length;
        for (int x = 0; x < attributes.size(); x++) {
            if (Arrays.binarySearch(reqAttribs, attributes.get(x)) >= 0) {
                reqnum--;
            }
        }
        return reqnum <= 0;
    }

    public boolean addAttribute(String attribute) throws SOFTException {
        int split = attribute.indexOf('=');
        if (split < 0)
            return false;
        return addAttribute(attribute.substring(0, split), attribute.substring(split + 1));
    }


    public boolean addAttribute(String key, String value)
            throws SOFTException {
        Attribute attrib = new Attribute(key, value);
        if (attributes == null)
            attributes = new Vector();
        attributes.add(attrib);
        return true;
    }

    public void addHeaderDescriptor(String header, String descriptor) {
        if (headerDescriptors == null)
            headerDescriptors = new Hashtable();
        headerDescriptors.put(header, descriptor);
    }

    public String getHeaderDescriptor(String header) {
        if (headerDescriptors == null)
            return null;
        return headerDescriptors.get(header);
    }

    public void setTableHeaders(String[] headers) {
        this.headers = headers;
        for (int x = 0; x < headers.length; x++) {
            if (headers[x].equals("ID_REF")) {
                tableIDColumn = x;
            } else if (headers[x].equals("VALUE")) {
                tableValueColumn = x;
            }
        }
    }

    public String[] getTableHeaders() {
        return headers;
    }

    public void setTableData(Object[][] tableData) {
        this.tableData = tableData;
    }

    public void setRowPointers(List<Long> pointers) {
        rowPointers = new long[pointers.size()];
        for (int x = 0; x < rowPointers.length; x++) {
            rowPointers[x] = pointers.get(x).longValue();
        }
        Arrays.sort(rowPointers);
    }

    public Object[][] getTableData() {
        return tableData;
    }

    public long[] getRowPointers() {
        return rowPointers;
    }

    public String toString() {
        String result = getClass() + " [identifier=" + identifier + " attributes=";
        for (int x = 0; x < attributes.size(); x++) {
            Attribute thisAttrib = attributes.get(x);
            result = result + thisAttrib.getKey() + ":" + thisAttrib.getValue() + "\n";
        }
        result = result + "]";
        return result;
    }

    public boolean hasTableData() {
        return tableData != null;
    }

    public Vector<Attribute> getAttributes() {
        return attributes;
    }

    public String getRowID(int row) {
        if ((tableData == null) || (row > tableData.length)) {
            return null;
        }
        return tableData[row][tableIDColumn] + "";
    }

    public String getRowValue(int row) {
        if ((tableData == null) || (row > tableData.length)) {
            return "0";
        }
        if (tableValueColumn >= tableData[row].length) {
            return "0";
        }
        return tableData[row][tableValueColumn] + "";
    }
}
