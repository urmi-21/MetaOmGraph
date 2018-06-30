package edu.iastate.metnet.metaomgraph.utils.qdxml;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.jdom.Attribute;
import org.jdom.Element;

public class SimpleXMLElement extends DefaultMutableTreeNode {
    private String name;
    private String text;
    private LinkedHashMap<String, String> attributes;

    public SimpleXMLElement() {
        name = "SimpleXMLElement";
        text = null;
    }

    public SimpleXMLElement(String name) {
        this.name = name;
        text = null;
    }

    public SimpleXMLElement(SimpleXMLElement copyMe) {
        name = copyMe.getName();
        text = copyMe.getText();
        attributes = new LinkedHashMap(copyMe.getAttributes());
    }

    public static SimpleXMLElement fromFile(File source) throws IOException {
        return fromStream(new FileInputStream(source));
    }

    public static SimpleXMLElement fromStream(InputStream source) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(source, "UTF-8"));
        MyDocHandler handler = new MyDocHandler();
        try {
            QDParser.parse(handler, in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler.root;
    }

    private static class MyDocHandler implements DocHandler {
        private ArrayDeque<SimpleXMLElement> nodeStack;
        public SimpleXMLElement root;

        private MyDocHandler() {
        }

        public void startElement(String tag, Hashtable h) throws Exception {
            SimpleXMLElement elem = new SimpleXMLElement(tag);
            Enumeration e = h.keys();
            while (e.hasMoreElements()) {
                Object key = e.nextElement();
                String val = h.get(key) + "";
                elem.setAttribute(key + "", val);
            }
            nodeStack.push(elem);
        }

        public void endElement(String tag) throws Exception {
            SimpleXMLElement elem = nodeStack.pop();
            SimpleXMLElement parent = nodeStack.peek();
            if (parent == null) {
                if (root != null) {
                    System.out.println("WARNING: Root already set");
                }
                root = elem;
            } else {
                parent.add(elem);
            }
        }

        public void startDocument() throws Exception {
            nodeStack = new ArrayDeque();
            root = null;
        }

        public void endDocument() throws Exception {
            if ((root != null) && (nodeStack.isEmpty())) {
                return;
            }
            System.out.println("Looks like something went wrong in XML parsing");
        }

        public void text(String str) throws Exception {
            nodeStack.peek().setText(str);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.trim().equals("")) {
            throw new InvalidParameterException("Invalid name: " + name);
        }
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public SimpleXMLElement setText(String text) {
        if ((text == null) || ("".equals(text.trim()))) {
            this.text = null;
        } else {
            String newText = text.trim();
            newText = newText.replaceAll("<", "&lt;");
            newText = newText.replaceAll(">", "&gt;");
            this.text = newText;
        }
        return this;
    }

    public SimpleXMLElement setAttribute(String name, String value) {
        if (attributes == null) {
            attributes = new LinkedHashMap();
        }
        attributes.put(name, value);
        return this;
    }

    public String getAttributeValue(String name) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(name);
    }

    public Map<String, String> getAttributes() {
        if (attributes == null) {
            return new TreeMap();
        }
        return attributes;
    }

    public SimpleXMLElement removeAttribute(String name) {
        if (attributes != null) {
            attributes.remove(name);
        }
        return this;
    }


    public void setUserObject(Object userObject) {
    }


    public Object getUserObject() {
        return this;
    }

    public String toString() {
        if (getAttributeValue("name") != null) {
            return getAttributeValue("name");
        }
        if (getAttributeValue("field") != null) {
            return getAttributeValue("field");
        }
        return name;
    }

    public void add(MutableTreeNode newChild) {
        throw new InvalidParameterException("Only SimpleXMLElements accepted!");
    }

    public SimpleXMLElement add(SimpleXMLElement newChild) {
        super.add(newChild);
        return this;
    }

    public SimpleXMLElement getChildAt(int index) {
        return (SimpleXMLElement) super.getChildAt(index);
    }

    public SimpleXMLElement getParent() {
        return (SimpleXMLElement) super.getParent();
    }

    public String getFullTag() {
        StringBuilder result = new StringBuilder("<" + getName());
        Map<String, String> myAttributes = getAttributes();
        Set<String> keySet = myAttributes.keySet();
        for (String key : keySet) {
            String value = myAttributes.get(key);
            if (value == null) {
                value = "";
            } else {
                value = value.replace("\"", "&quot;").replaceAll("&quot(?=[^;])", "&quot;");
                value = value.replace("'", "&apos;").replaceAll("&apos(?=[^;])", "&apos;");
                value = value.replace("<", "&lt;").replaceAll("&lt(?=[^;])", "&lt;");
                value = value.replace(">", "&gt;").replaceAll("&gt(?=[^;])", "&gt;");
                value = value.replaceAll("&amp(?=[^;])", "&amp;").replaceAll(
                        "&(?!gt;|lt;|apos;|quot;)", "&amp;");
            }
            result.append(" " + key + "=\"" + value + "\"");
        }
        if ((isLeaf()) && (getText() == null)) {
            result.append(" /");
        }
        result.append(">");
        if (getText() != null) {
            result.append(getText());
            if (isLeaf()) {
                result.append("</" + getName() + ">");
            }
        }
        return result.toString();
    }

    public String toFullString() {
        StringBuilder result = new StringBuilder(getFullTag());
        for (int i = 0; i < getLevel(); i++) {
            result.insert(0, "  ");
        }
        result.append("\n");
        if (isLeaf()) {
            return result.toString();
        }
        for (int i = 0; i < getChildCount(); i++) {
            result.append(getChildAt(i).toFullString());
        }
        for (int i = 0; i < getLevel(); i++) {
            result.append("  ");
        }
        result.append("</" + name + ">\n");
        return result.toString();
    }


    public SimpleXMLElement fullCopy(boolean removeCols) {
        SimpleXMLElement result = new SimpleXMLElement();
        name = name;
        text = text;
        attributes = attributes;
        if (removeCols) {
            attributes.remove("col");
        }
        for (int i = 0; i < getChildCount(); i++) {
            result.add(getChildAt(i).fullCopy(removeCols));
        }
        return result;
    }

    public Element toJDOMElement() {
        Element result = new Element(getName()).setText(getText());
        Set<String> keys = getAttributes().keySet();
        for (String key : keys) {
            result.setAttribute(key, getAttributeValue(key));
        }
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            result.addContent(getChildAt(childIndex).toJDOMElement());
        }
        return result;
    }

    public static SimpleXMLElement fromJDOMElement(Element source) {
        SimpleXMLElement result = new SimpleXMLElement(source.getName()).setText(source.getText());
        List attribs = source.getAttributes();
        Attribute attrib;
        for (Object o : attribs) {
            attrib = (Attribute) o;
            result.setAttribute(attrib.getName(), attrib.getValue());
        }
        List children = source.getChildren();
        for (Object o : children) {
            Element child = (Element) o;
            result.add(fromJDOMElement(child));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        File source = Utils.mandatoryFileOpen();
        SimpleXMLElement root = fromFile(source);
        System.out.println(root.toFullString());
        JFrame f = new JFrame("XML Tree Test");
        JTree tree = new JTree(root);
        f.getContentPane().add(new JScrollPane(tree), "Center");
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
