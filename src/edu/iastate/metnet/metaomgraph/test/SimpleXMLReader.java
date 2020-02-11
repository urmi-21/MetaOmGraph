package edu.iastate.metnet.metaomgraph.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;


public class SimpleXMLReader {
    private DefaultMutableTreeNode root;

    public static void main(String[] args) {
    }

    public SimpleXMLReader(File source)
            throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(source));
        String thisLine = in.readLine();
        while ((thisLine.startsWith("<?")) && (!thisLine.startsWith("<"))) {
            thisLine = in.readLine().trim();
        }
        Element e = new Element(thisLine);
        root = new DefaultMutableTreeNode(e);
        thisLine = "";
        while (thisLine.equals("")) {
            thisLine = in.readLine().trim();
        }
        Element currentElement = null;
        Element currentParent = null;
        while (thisLine != null) {
            e = new Element(thisLine);
        }
    }

    public static class Element {
        private String name;
        private TreeMap<String, String> attributes;
        private boolean closed;

        public Element(String line) {
            attributes = new TreeMap();
            closed = line.endsWith("/>");
            boolean hasAttributes = line.indexOf(' ') > 0;
            if (hasAttributes) {
                int space = line.indexOf(' ');
                name = line.substring(1, space);
                while (space > 0) {
                    int nextSpace = line.indexOf(' ', space + 1);
                    int equals = line.indexOf('=', space);

                    String key = line.substring(space + 1, equals);
                    String value;
                    if (nextSpace > 0) {
                        value = line.substring(equals + 2, nextSpace - 1);
                    } else {
                        if (closed) {
                            value = line.substring(equals + 2,
                                    line.length() - 3);
                        } else {
                            value = line.substring(equals + 2,
                                    line.length() - 2);
                        }
                    }
                    attributes.put(key, value);
                }
            } else {
                name = line.substring(1, line.length() - 2);
            }
        }

        @Override
		public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        public String getAttributeValue(String attributeName) {
            return attributes.get(attributeName);
        }

        public boolean isClosed() {
            return closed;
        }
    }
}
