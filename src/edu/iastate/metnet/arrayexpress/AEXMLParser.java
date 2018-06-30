package edu.iastate.metnet.arrayexpress;

import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;
import edu.iastate.metnet.metaomgraph.ui.TextAreaRenderer;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.qdxml.DocHandler;
import edu.iastate.metnet.metaomgraph.utils.qdxml.QDParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class AEXMLParser implements DocHandler {
    private static final String[] IGNORE_NODES = {"files",
            "bioassaydatagroup", "rawdatafiles", "arraydesign", "id"};

    private static DefaultMutableTreeNode root;

    private ArrayDeque<DefaultMutableTreeNode> nodes;

    private static TreeMap<String, Integer> nodeCounts;

    private static int openTags;

    private int experiments;

    private ExpName thisExpName;

    private String lastStartedTag;

    private String thisTagText;
    private int toIgnore;
    private TreeSet<String> myIgnoreSet;

    private AEXMLParser() {
        nodes = new ArrayDeque();
        nodeCounts = new TreeMap();
        openTags = 0;
        toIgnore = 0;
        myIgnoreSet = new TreeSet();
        for (String s : IGNORE_NODES) {
            myIgnoreSet.add(s);
        }
    }

    public static DefaultMutableTreeNode getAETree(String location) {
        Utils.startWatch();
        AEXMLParser parser = new AEXMLParser();
        try {
            URL url = new URL(location);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream(), "UTF-8"));
            System.out.println("Starting parsing...");
            parser.experiments = 0;
            QDParser.parse(parser, in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Parsing took " + Utils.stopWatch());
        Set<String> tags = nodeCounts.keySet();
        for (String tag : tags) {
            Integer count = nodeCounts.get(tag);
            System.out.println(tag + ": " + count);
        }
        return root;
    }

    public static List<XMLNodeInfo> getExperimentList(String location) {
        DefaultMutableTreeNode root = getAETree(location);
        ArrayList<XMLNodeInfo> result = new ArrayList();
        for (int i = 0; i < root.getChildCount(); i++) {
            result.add((XMLNodeInfo) ((DefaultMutableTreeNode) root
                    .getChildAt(i)).getUserObject());
        }
        return result;
    }

    public void startElement(String tag, Hashtable h) throws Exception {
        if (toIgnore > 0) {
            toIgnore += 1;
            return;
        }
        if (myIgnoreSet.contains(tag)) {
            toIgnore = 1;
            return;
        }
        if ("a".equals(tag)) {

            return;
        }
        lastStartedTag = tag;
        if (nodes.size() <= 0) {
            XMLNodeInfo info = new XMLNodeInfo();
            info.name = "Experiments";
            info.attributes = h;

            root = new DefaultMutableTreeNode(info);
            nodes.push(root);
            return;
        }

        if (nodes.size() == 1) {

            experiments += 1;
            thisExpName = new ExpName();
            XMLNodeInfo info = new XMLNodeInfo();
            info.name = "Experiment";
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(info);
            nodes.push(node);
            if (experiments % 20 == 0) {
                System.out.println("Starting experiment " + experiments);
            }
            return;
        }

        XMLNodeInfo info = new XMLNodeInfo();
        info.name = tag;
        info.attributes = h;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(info);
        nodes.push(node);
    }

    public void endElement(String tag) throws Exception {
        if (toIgnore > 0) {
            toIgnore -= 1;
            return;
        }
        if ("a".equals(tag)) {
            return;
        }
        openTags -= 1;
        DefaultMutableTreeNode node = nodes.pop();
        if (!nodes.isEmpty()) {
            XMLNodeInfo thisInfo = (XMLNodeInfo) node.getUserObject();
            Object o = nodes.peek().getUserObject();
            XMLNodeInfo parentInfo = (XMLNodeInfo) o;

            if (nodes.size() == 1) {
                thisInfo.name = thisExpName.toString();
            }
            if (thisInfo == null) {
                System.out.println("null node info? " + tag);
            }
            if ((node.isLeaf())
                    && ((thisInfo.data == null) || ((thisInfo.data.size() == 1) && ("text"
                    .equals(thisInfo.data.get(0)[0]))))
                    && (thisInfo.attributes.size() <= 0)) {

                if ((thisInfo.text != null)
                        && (!"".equals(thisInfo.text.trim()))) {
                    String[] data = {thisInfo.name, thisInfo.text};
                    parentInfo.addData(data);
                } else if ((thisInfo.data != null)
                        && (thisInfo.data.size() == 1)) {
                    String[] data = {thisInfo.name,
                            thisInfo.data.get(0)[1] + ""};
                    parentInfo.addData(data);
                }
            } else {
                StringBuilder provider;
                if ("provider".equals(thisInfo.name)) {
                    String name = null, email = null, role = null;
                    for (Object[] thisData : thisInfo.data) {
                        String key = thisData[0] + "";
                        String val = thisData[1] + "";
                        if ("contact".equals(key)) {
                            name = val;
                        } else if ("email".equals(key)) {
                            email = val;
                        } else if ("role".equals(key)) {
                            role = val;
                        }
                    }
                    provider = new StringBuilder(name);
                    if (email != null) {
                        provider.append(" (" + email + ")");
                    }
                    if (role != null) {
                        provider.append(", " + role);
                    }
                    parentInfo.addData(new Object[]{"provider",
                            provider.toString()});
                } else if ("fgemdatafiles".equals(thisInfo.name)) {
                    // Checking whether or not processed data exists
                    parentInfo.hasProcessedData = "true"
                            .equals(thisInfo.attributes.get("available"));
                } else {

                    if (("experimentalfactor".equals(thisInfo.name))
                            || ("sampleattribute".equals(thisInfo.name))) {
                        String name = null;
                        StringBuilder values = null;
                        for (Object[] thisData : thisInfo.data) {
                            String key = thisData[0] + "";
                            String val = thisData[1] + "";
                            if ("category".equals(key) || "name".equals(key)) {
                                name = thisInfo.name + ": " + val;
                            } else if ("value".equals(key)) {
                                if (values == null) {
                                    values = new StringBuilder(val);
                                } else {
                                    values.append(", " + val);
                                }
                            }
                        }
                        if (values == null) {
                            values = new StringBuilder("");
                        }
                        if (name != null) {
                            parentInfo.addData(new Object[]{name,
                                    values.toString()});
                        } else {
                            System.out.println(name + " error: " + name + ", "
                                    + values);
                        }
                    } else if ("miamescores".equals(thisInfo.name)) {
                        StringBuilder scores = null;
                        for (Object[] thisData : thisInfo.data) {
                            String key = thisData[0] + "";
                            String val = thisData[1] + "";
                            if (scores == null) {
                                scores = new StringBuilder(key + ": " + val);
                            } else {
                                scores.append("\n" + key + ": " + val);
                            }
                        }
                        parentInfo.addData(new Object[]{"miamescores",
                                scores.toString()});
                    } else if ("bibliography".equals(thisInfo.name)) {
                        StringBuilder info = new StringBuilder();
                        for (Object[] thisData : thisInfo.data) {
                            String key = thisData[0] + "";
                            String val = thisData[1] + "";
                            if ("authors".equals(key)) {
                                info.insert(0, val + "\n\n");
                            } else if ("title".equals(key)) {
                                info.append(val);
                            }
                        }
                        parentInfo.addData(new Object[]{"bibliography",
                                info.toString()});
                    } else {
                        nodes.peek().add(node);
                    }
                }
            }
        } else {
            root = node;
        }
    }

    public void startDocument() throws Exception {
    }

    public void endDocument() throws Exception {
    }

    public void text(String str) throws Exception {
        if (nodes.size() <= 0) {
            System.out.println("Text for an empty stack?");
            System.out.println(str);
        }
        if (toIgnore > 0) {
            return;
        }
        thisTagText = str;
        if (nodes.size() == 3) {
            if ("accession".equals(lastStartedTag)) {
                thisExpName.id = str;
            } else if ("name".equals(lastStartedTag)) {
                thisExpName.name = str;
            } else if ("lastupdatedate".equals(lastStartedTag)) {
                thisExpName.date = str;
            } else if (("releasedate".equals(lastStartedTag))
                    && (thisExpName.date == null)) {
                thisExpName.date = str;
            } else if (("loaddate".equals(lastStartedTag))
                    && (thisExpName.date == null)) {
                thisExpName.date = str;
            } else if ("samples".equals(lastStartedTag)) {

                thisExpName.size = str;
            }
        }
        Object o = nodes.peek().getUserObject();
        if ((o instanceof XMLNodeInfo)) {
            XMLNodeInfo info = (XMLNodeInfo) o;
            if (info.text != null) {
                info.text += str;
            } else {
                info.text = str;
            }
        }
    }

    public class XMLNodeInfo {
        String name;
        String text;
        Hashtable<String, Object> attributes;
        String[] headers;
        ArrayList<Object[]> data;
        String accession;
        boolean hasProcessedData;
        Object[] myListing;

        public XMLNodeInfo() {
            name = null;
            text = null;
            attributes = new Hashtable();
            hasProcessedData = false;
        }

        public String toString() {
            if (attributes.get("name") != null) {
                return attributes.get(name) + "";
            }
            return name;
        }

        public JTable getTable() {
            if ((headers != null) && (data != null)) {
                NoneditableTableModel model = new NoneditableTableModel(
                        data.toArray(new Object[0][]), headers);
                return new JTable(model);
            }
            String[] thisHeaders = {"Attribute", "Value"};
            Object[][] thisData;

            if (data != null) {
                thisData = new Object[attributes.size() + data.size() + 2][2];
            } else {
                thisData = new Object[attributes.size() + 2][2];
            }
            thisData[0][0] = "Name";
            thisData[0][1] = name;
            thisData[1][0] = "Text";
            thisData[1][1] = text;
            int x = 2;

            Set<String> keys = attributes.keySet();
            for (String key : keys) {
                thisData[x][0] = key;
                thisData[x][1] = attributes.get(key);
                x++;
            }
            if (data != null) {
                for (Object[] addMe : data) {
                    thisData[(x++)] = addMe;
                }
            }
            Arrays.sort(thisData, new Comparator<Object[]>() {

                public int compare(Object[] o1, Object[] o2) {
                    return (o1[0] + "").compareTo(o2[0] + "");
                }

            });
            NoneditableTableModel model = new NoneditableTableModel(thisData,
                    thisHeaders);
            StripedTable result = new StripedTable(model);
            for (int i = 0; i < result.getColumnCount(); i++) {
                result.getColumnModel().getColumn(i)
                        .setCellRenderer(new TextAreaRenderer());
            }
            return result;
        }

        public void addData(Object[] values) {
            if (data == null) {
                data = new ArrayList();
            }
            data.add(values);
            if ("accession".equals(values[0])) {
                accession = values[1] + "";
            }
        }

        public Object getData(Object key) {
            if ((data == null) || (key == null)) {
                return null;
            }
            for (Object[] thisData : data) {
                if (key.equals(thisData[0])) {
                    return thisData[1];
                }
            }
            return null;
        }

        public Object[] getListing() {
            if (myListing != null) {
                return myListing;
            }
            Object id = getData("accession");
            Object date = getData("lastupdatedate");
            if (date == null) {
                date = getData("releasedate");
            }
            if (date == null) {
                date = getData("loaddate");
            }
            Object samples = getData("samples");
            myListing = new String[4];
            myListing[0] = id;
            myListing[1] = samples;
            myListing[2] = name;
            myListing[3] = date;
            return myListing;
        }
    }

    public class ExpName {
        String id;
        String name;
        String date;
        String size;

        public ExpName() {
        }

        public String toString() {
            return id + " (" + size + "): " + name + " (" + date + ")";
        }
    }

    public static void main(String[] args) {
        DefaultMutableTreeNode root = getAETree("http://www.ebi.ac.uk/arrayexpress/xml/v2/experiments?array=A-AFFY-33&pagesize=25");
        JTree tree = new JTree(root);
        final JSplitPane splitter = new JSplitPane(0);
        splitter.setTopComponent(new JScrollPane(tree));
        JScrollPane tablePane = new JScrollPane();
        splitter.setBottomComponent(tablePane);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            private int switches = 0;

            public void valueChanged(TreeSelectionEvent e) {
                AEXMLParser.XMLNodeInfo info = (AEXMLParser.XMLNodeInfo) ((DefaultMutableTreeNode) e
                        .getPath().getLastPathComponent()).getUserObject();
                splitter.setBottomComponent(new JScrollPane(info.getTable()));

            }

        });
        JFrame f = new JFrame("New AE XML test");
        f.add(splitter, "Center");
        f.setSize(1000, 1000);
        splitter.setDividerLocation(0.5D);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
