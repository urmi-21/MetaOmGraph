package edu.iastate.metnet.arrayexpress.v2;

import edu.iastate.metnet.metaomgraph.ui.UpdatingSortableFilterableTable;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.qdxml.DocHandler;
import edu.iastate.metnet.metaomgraph.utils.qdxml.QDParser;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;


public class AEXMLParser
        implements DocHandler {
    private static final String[] IGNORE_NODES = {"files",
            "bioassaydatagroup", "rawdatafiles", "arraydesign", "id"};

    private static DefaultMutableTreeNode root;

    private ArrayDeque<DefaultMutableTreeNode> nodes;

    private static TreeMap<String, Integer> nodeCounts;

    private static int openTags;

    private static int experiments;

    private ExpName thisExpName;

    private String lastStartedTag;

    private String thisTagText;

    private int toIgnore;
    private TreeSet<String> myIgnoreSet;
    private static UpdatingSortableFilterableTable progressMe;

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
            experiments = 0;
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

    public static List<AEXMLNodeInfo> getExperimentList(String location) {
        DefaultMutableTreeNode root = getAETree(location);
        ArrayList<AEXMLNodeInfo> result = new ArrayList();
        for (int i = 0; i < root.getChildCount(); i++) {
            result.add((AEXMLNodeInfo) ((DefaultMutableTreeNode) root.getChildAt(i)).getUserObject());
        }
        return result;
    }

    @Override
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
            AEXMLNodeInfo info = new AEXMLNodeInfo();
            info.name = "Experiments";
            info.attributes = h;
            System.out.println(h.get("total") + " exps found");
            if (progressMe != null) {
                progressMe.setMin(Long.valueOf(0L));
                progressMe.setMax(new Long(h.get("total") + ""));
                progressMe.setProgress(Long.valueOf(0L));
                progressMe.setText("Reading " + progressMe.getMax() + " experiments");
            }
            root = new DefaultMutableTreeNode(info);
            nodes.push(root);
            return;
        }

        if (nodes.size() == 1) {

            experiments += 1;
            thisExpName = new ExpName();
            AEXMLNodeInfo info = new AEXMLNodeInfo();
            info.name = "Experiment";
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(info);
            nodes.push(node);
            if (progressMe != null) {
                progressMe.increaseProgress(Long.valueOf(1L));
            } else if (experiments % 20 == 0) {
                System.out.println("Starting experiment " + experiments);
            }
            return;
        }


        AEXMLNodeInfo info = new AEXMLNodeInfo();
        info.name = tag;
        info.attributes = h;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(info);
        nodes.push(node);
    }


    @Override
	public void endElement(String tag)
            throws Exception {
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
            AEXMLNodeInfo thisInfo = (AEXMLNodeInfo) node.getUserObject();
            Object o = nodes.peek().getUserObject();
            AEXMLNodeInfo parentInfo = (AEXMLNodeInfo) o;
            if (nodes.size() == 1) {
                thisInfo.name = thisExpName.toString();
            }
            if (thisInfo == null) {
                System.out.println("null node info? " + tag);
            }
            if ((node.isLeaf()) &&
                    ((thisInfo.data == null) || ((thisInfo.data.size() == 1) &&
                            ("text".equals(thisInfo.data.get(0)[0])))) &&
                    (thisInfo.attributes.size() <= 0)) {

                if ((thisInfo.text != null) && (!"".equals(thisInfo.text.trim()))) {
                    String[] data = {thisInfo.name, thisInfo.text};
                    parentInfo.addData(data);
                } else if ((thisInfo.data != null) && (thisInfo.data.size() == 1)) {
                    String[] data = {thisInfo.name,
                            thisInfo.data.get(0)[1] + ""};
                    parentInfo.addData(data);
                }
            } else {
                StringBuilder provider;
                if ("provider".equals(thisInfo.name)) {
                    String name = null;
                    String email = null;
                    String role = null;
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
                } else if ("processeddatafiles".equals(thisInfo.name)) {
                    parentInfo.hasProcessedData = "true".equals(thisInfo.attributes.get("available"));
                } else if (("experimentalfactor".equals(thisInfo.name)) || ("sampleattribute".equals(thisInfo.name))) {
                    String name = null;
                    StringBuilder values = null;
                    for (Object[] thisData : thisInfo.data) {
                        String key = thisData[0] + "";
                        String val = thisData[1] + "";
                        if (("category".equals(key)) || ("name".equals(key))) {
                            name = name + ": " + val;
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
                        parentInfo.addData(new Object[]{name, values.toString()});
                    } else
                        System.out.println(name + " error: " + name + ", " + values);
                } else {
                    if ("miamescores".equals(thisInfo.name)) {
                        StringBuilder scores = null;
                        int thisOverallScore = -1;
                        for (Object[] thisData : thisInfo.data) {
                            String key = thisData[0] + "";
                            String val = thisData[1] + "";
                            if (scores == null) {
                                scores = new StringBuilder(key + ": " + val);
                            } else {
                                scores.append("\n" + key + ": " + val);
                            }
                            if ("overallscore".equals(key)) {
                                try {
                                    thisOverallScore = Integer.parseInt(val);
                                }
                                catch (NumberFormatException localNumberFormatException) {
                                }
                            }
                        }
                        parentInfo.addData(new Object[]{"miamescores", scores.toString()});
                        if (thisOverallScore >= 0) {
                            parentInfo.miameScore = thisOverallScore;
                        }
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
                        parentInfo.addData(new Object[]{"bibliography", info.toString()});
                    } else {
                        nodes.peek().add(node);
                    }
                }
            }
        } else {
            root = node;
        }
    }

    @Override
	public void startDocument()
            throws Exception {
    }

    @Override
	public void endDocument()
            throws Exception {
    }

    @Override
	public void text(String str)
            throws Exception {
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
            } else if (("releasedate".equals(lastStartedTag)) &&
                    (thisExpName.date == null)) {
                thisExpName.date = str;
            } else if (("loaddate".equals(lastStartedTag)) &&
                    (thisExpName.date == null)) {
                thisExpName.date = str;
            } else if ("samples".equals(lastStartedTag)) {

                thisExpName.size = str;
            }
        }
        Object o = nodes.peek().getUserObject();
        if ((o instanceof AEXMLNodeInfo)) {
            AEXMLNodeInfo info = (AEXMLNodeInfo) o;
            if (info.text != null) {
                info.text += str;
            } else
                info.text = str;
        }
    }

    public class ExpName {
        String id;
        String name;
        String date;
        String size;

        public ExpName() {
        }

        @Override
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

            @Override
			public void valueChanged(TreeSelectionEvent e) {
                AEXMLNodeInfo info = (AEXMLNodeInfo) ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();

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


    public static String[] showImportDialog(Window parent) {
        JSplitPane splitter = new JSplitPane(0);

        return null;
    }

    public static void setProgressComponent(UpdatingSortableFilterableTable comp) {
        progressMe = comp;
    }
}
