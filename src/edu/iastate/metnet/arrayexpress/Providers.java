package edu.iastate.metnet.arrayexpress;

import java.util.List;
import java.util.TreeSet;

import org.jdom.Element;

public class Providers implements TableDataNode, edu.iastate.metnet.metaomgraph.XMLizable {
    private TreeSet<Provider> names;

    public Providers() {
    }

    public Object[][] getTableData() {
        Object[][] result = new Object[names.size()][3];
        int index = 0;
        for (Provider addMe : names) {
            result[index][0] = addMe.role;
            result[index][1] = addMe.name;
            result[(index++)][2] = addMe.email;
        }
        return result;
    }

    public String[] getTableHeaders() {
        return new String[]{"Role", "Name", "Email"};
    }


    public void fromXML(Element source) {
        List children = source.getChildren("provider");
        names = new TreeSet();
        for (Object child : children) {
            String name = ((Element) child).getAttributeValue("contact");
            String role = ((Element) child).getAttributeValue("role");
            String email = ((Element) child).getAttributeValue("email");
            names.add(new Provider(name, email, role));
        }
    }

    public Element toXML() {
        return null;
    }

    public String toString() {
        return "Providers";
    }

    public static Providers createFromXML(Element source) {
        Providers result = new Providers();
        result.fromXML(source);
        return result;
    }

    private class Provider implements Comparable<Provider> {
        String name;
        String email;
        String role;

        public Provider(String name, String email, String role) {
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public int compareTo(Provider o) {
            return role.compareTo(role);
        }
    }
}
