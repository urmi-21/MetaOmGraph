package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.arrayexpress.AEImportPanel;
import edu.iastate.metnet.arrayexpress.CheckBoxTree;
import edu.iastate.metnet.arrayexpress.CheckBoxTree.CheckBoxTreeCellRenderer;
import edu.iastate.metnet.arrayexpress.UpdatingTree;
import edu.iastate.metnet.metaomgraph.SwingWorker;
import edu.iastate.metnet.metaomgraph.XMLizable;
import edu.iastate.metnet.metaomgraph.ui.ClearableTextField;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.utils.JDomUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class XMLVisualizerBackup
        extends JPanel
        implements ActionListener {
    private JTable table;
    private NoneditableTableModel model;
    private UpdatingTree myTree;
    private JComboBox speciesBox;
    private JComboBox arrayBox;
    private DefaultComboBoxModel speciesModel;
    private DefaultComboBoxModel arrayModel;
    private ClearableTextField searchField;
    private JCheckBox wholeWordsBox;
    private JButton searchButton;
    private JButton firstButton;
    private JButton prevButton;
    private JButton nextButton;
    private JButton lastButton;
    private JPanel infoPanel;
    private JLabel expCountLabel;
    private JLabel sampleCountLabel;
    private JLabel pageLabel;
    private int pageNum = 1;

    private int pageCount = 1;

    private int expCount = 0;

    private int sampleCount = 0;

    private int ath1Index = 0;

    private JSplitPane splitter;

    private CheckBoxTree.CheckBoxTreeCellRenderer checkBoxRenderer;

    public static final String FIRST_COMMAND = "first page";

    public static final String PREV_COMMAND = "previous page";

    public static final String NEXT_COMMAND = "next page";
    public static final String LAST_COMMAND = "last page";
    public static final String UPDATE_COMMAND = "update information";

    public static void main(String[] args)
            throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        AEImportPanel thing = new AEImportPanel();
        JFrame f = new JFrame("ArrayExpress");
        f.getContentPane().add(thing);
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
        thing.setSplitter(0.5D);
    }


    public XMLVisualizerBackup() {
        setLayout(new BorderLayout());
        speciesBox = new JComboBox(new String[]{"Loading..."});
        speciesBox.setEnabled(false);
        arrayBox = new JComboBox(new String[]{"Loading..."});
        arrayBox.setEnabled(false);
        searchField = new ClearableTextField();
        searchField.setDefaultText("Text search");
        searchField.setDefaultTextColor(Color.LIGHT_GRAY);
        searchField.setEnabled(false);
        try {
            myTree = new UpdatingTree(null);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        checkBoxRenderer = new CheckBoxTree.CheckBoxTreeCellRenderer(myTree);
        myTree.setCellRenderer(checkBoxRenderer);
        myTree.start();
        model = new NoneditableTableModel(null, new String[]{"Attribute",
                "Value"});
        table = new JTable(model);
        wholeWordsBox = new JCheckBox("Match whole words");
        searchButton = new JButton("Search");
        infoPanel = new JPanel(new BorderLayout());
        splitter = new JSplitPane(0, new JScrollPane(
                myTree), new JScrollPane(table));
        JPanel inputPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel filterPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, 1));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, 1));
        filterPanel.setLayout(new BorderLayout());

        inputPanel.add(searchField);

        inputPanel.add(speciesBox);

        inputPanel.add(arrayBox);
        buttonPanel.add(searchButton);
        buttonPanel.add(wholeWordsBox);
        filterPanel.add(inputPanel, "Center");
        filterPanel.add(buttonPanel, "East");
        add(splitter, "Center");
        add(filterPanel, "North");
        add(infoPanel, "South");
        splitter.setDividerLocation(0.5D);
        speciesBox.setSize(new Dimension(250,
                speciesBox.getPreferredSize().height));
        arrayBox
                .setSize(new Dimension(450, arrayBox.getPreferredSize().height));
        searchField.setSize(new Dimension(450,
                searchField.getPreferredSize().height));
        final Vector<OptionObject> speciesList = new Vector();
        final Vector<OptionObject> arrayList = new Vector();
        final XMLVisualizerBackup myself = this;
        new SwingWorker() {
            public Object construct() {
                try {
                    URL speciesSource = new URL(
                            "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_species.jsp");
                    URL arraySource = new URL(
                            "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_array.jsp");

                    SAXBuilder builder = new SAXBuilder();
                    speciesList.add(new XMLVisualizerBackup.OptionObject("Any species", ""));
                    List options = builder.build(speciesSource)
                            .getRootElement().getChildren("option");
                    Iterator optionIter = options.iterator();
                    while (optionIter.hasNext()) {
                        Element thisChild = (Element) optionIter.next();
                        String name = thisChild.getValue();
                        String value = thisChild.getAttributeValue("value");
                        if (!value.equals("")) {
                            speciesList.add(new XMLVisualizerBackup.OptionObject(name, value));
                        }
                    }

                    arrayList.add(new XMLVisualizerBackup.OptionObject("Any array", ""));
                    options = builder.build(arraySource).getRootElement()
                            .getChildren("optgroup");
                    optionIter = options.iterator();
                    Iterator i;
                    for (; optionIter.hasNext();


                         i.hasNext()) {
                        Element thisChild = (Element) optionIter.next();
                        String name = "<html><b><i>" +
                                thisChild.getAttributeValue("label") +
                                "</i></b></html>";

                        String value = "";
                        System.out.println("Adding option group: " + name);
                        arrayList.add(new XMLVisualizerBackup.OptionObject(name, value));
                        List children = thisChild.getChildren("option");
                        i = children.iterator();
                        continue;
                        //thisChild = (Element)i.next();
                        //name = "   " + thisChild.getValue();
                        //value = thisChild.getAttributeValue("value");
                        //if (!value.equals("")) {
                        //System.out.println("Adding array: " + name);
                        //if (name.equals("   Affymetrix GeneChip  Arabidopsis Genome ATH1-121501")) {
                        // ath1Index = arrayList.size();
                        // System.out.println("Found it at " + ath1Index);
                        //}
                        //  arrayList.add(new XMLVisualizerBackup.OptionObject(name, value));
                        //}
                    }


                    URL aesource = XMLVisualizerBackup.this.buildURL();


                    System.out.println("Building from URL");
                    long startTime = Calendar.getInstance().getTimeInMillis();
                    Document myDoc = builder.build(aesource);
                    long endTime = Calendar.getInstance().getTimeInMillis();
                    System.out.println("Building complete!  Took: " + (
                            endTime - startTime) + "ms");

                    XMLOutputter output = new XMLOutputter();
                    output.setFormat(Format.getPrettyFormat());
                    output.output(myDoc, new BufferedWriter(new FileWriter(
                            new File("c:\\cleanarrayexpress2.xml"))));
                    System.out.println("Clean text output!");


                    Element root = myDoc.getRootElement();
                    XMLVisualizerBackup.this.buildInfoPanel(root);
                    DefaultMutableTreeNode treeRoot = XMLVisualizerBackup.makeTree(root);
                    myTree.setModel(new DefaultTreeModel(treeRoot));

                    myTree
                            .addTreeSelectionListener(new TreeSelectionListener() {
                                public void valueChanged(TreeSelectionEvent e) {
                                    Object o = e.getPath().getPath()[e
                                            .getPath().getPathCount() - 1];
                                    if (!(o instanceof DefaultMutableTreeNode)) {
                                        return;
                                    }
                                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                                    Object newSel = node.getUserObject();
                                    if (newSel instanceof ExperimentData) {
                                        ExperimentData data = (ExperimentData) newSel;
                                        String[][] tableData = new String[data
                                                .getAttributeCount()][2];
                                        int row = 0;
                                        tableData[row++] = new String[]{
                                                "Name", data.name};
                                        tableData[row++] = new String[]{"ID",
                                                data.id};
                                        tableData[row++] = new String[]{
                                                "Array", data.array};
                                        for (Attribute attrib : data.sampleAttributes) {
                                            tableData[row++] = new String[]{
                                                    attrib.getName(),
                                                    attrib.getValue()};
                                        }
                                        for (Attribute attrib : data.factorValues) {
                                            tableData[row++] = new String[]{
                                                    attrib.getName(),
                                                    attrib.getValue()};
                                        }
                                        model.setData(tableData);
                                    } else {
                                        model.setData(new Object[][]{{"",
                                                newSel}});
                                    }
                                }
                            });
                } catch (Exception e) {
                    System.err.println("Error during construction");
                    e.printStackTrace();
                }
                return null;
            }

            public void finished() {
                speciesModel = new DefaultComboBoxModel(speciesList);
                arrayModel = new DefaultComboBoxModel(arrayList);
                speciesBox.setModel(speciesModel);
                arrayBox.setModel(arrayModel);
                arrayBox.setSelectedIndex(ath1Index);
                speciesBox.setActionCommand("update information");
                speciesBox.addActionListener(myself);
                arrayBox.setActionCommand("update information");
                arrayBox.addActionListener(myself);
                searchField.setActionCommand("update information");
                searchField.addActionListener(myself);
                searchButton.setActionCommand("update information");
                searchButton.addActionListener(myself);
                speciesBox.setEnabled(true);

                searchField.setEnabled(true);
                myTree.stop();
            }
        }.start();
    }

    private static DefaultMutableTreeNode makeTree(Element root) {
        DefaultMutableTreeNode result;

        if (root.getName().toLowerCase().equals("experiment")) {
            result = new DefaultMutableTreeNode(
                    ExperimentData.createFromXML(root));
        } else {
            result = new DefaultMutableTreeNode(root.getName());
        }
        List children = root.getChildren();
        for (Object thisChild : children) {
            if ((thisChild instanceof Element)) {
                result.add(makeTree((Element) thisChild));
            }
        }
        return result;
    }

    private static void writeAEdata() throws IOException {
        URL source = new URL(
                "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_expts.jsp");
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(
                "c:\\arrayexpress.xml")));
        BufferedReader in = new BufferedReader(new InputStreamReader(source
                .openStream()));
        System.out.println("Connection established");
        int written = 0;
        char[] data = new char[100];
        while (in.read(data) >= 0) {
            String line = new String(data).trim();

            if (line.length() > 0) {
                out.write(data);
                written++;
                if (written % 50 == 0) {
                    System.out.println("Wrote " + written + " times");
                }
            } else {
                System.out.println("Blank line after write " + written);
            }
        }
        in.close();
        out.close();
    }

    private URL buildURL() throws MalformedURLException {
        String organism = "";
        String array = "";
        if (speciesBox.isEnabled()) {
            organism = ((OptionObject) speciesBox.getSelectedItem()).value;
        }
        if (arrayBox.isEnabled()) {
            array = ((OptionObject) arrayBox.getSelectedItem()).value;
        }
        String result = "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_expts.jsp?page-size=20&sort_by=releasedate&sort_order=descending";
        result = result + "&page-number=" + pageNum;
        result = result + "&organism=" + organism;

        result = result + "&array=13851999";
        result = result + "&keyword=" + searchField.getText();
        if (wholeWordsBox.isSelected()) {
            result = result + "&wholewords=checked";
        }
        System.out.println(result);
        return new URL(result);
    }

    private static class ExperimentData
            implements XMLizable {
        public String id;
        public String name;
        public String array;
        public ArrayList<Attribute> sampleAttributes;
        public ArrayList<Attribute> factorValues;

        private ExperimentData() {
        }

        public String toString() {
            return id + ": " + name;
        }

        public void fromXML(Element source) {
            id = source.getAttributeValue("accnum");
            name = source.getAttributeValue("name");
            array = source.getAttributeValue("array");
            List children = source.getChild("sampleattributes").getChildren(
                    "sampleattribute");
            sampleAttributes = new ArrayList();
            for (Object thisChild : children) {
                if ((thisChild instanceof Element)) {
                    Element elem = (Element) thisChild;
                    String category = JDomUtils.convertToValidElementName(elem
                            .getAttributeValue("CATEGORY"));
                    String value = elem.getAttributeValue("VALUE");
                    if ((category != null) && (value != null)) {
                        sampleAttributes.add(new Attribute(category, value));
                    }
                }
            }
            if (source.getChild("factorvalues") != null) {
                children = source.getChild("factorvalues").getChildren(
                        "factorvalue");
                factorValues = new ArrayList();
                for (Object thisChild : children) {
                    if ((thisChild instanceof Element)) {
                        Element elem = (Element) thisChild;
                        if (elem.getAttributeValue("FACTORNAME") != null) {
                            String category = JDomUtils.convertToValidElementName(elem.getAttributeValue(
                                    "FACTORNAME"));
                            String value = elem.getAttributeValue("FV_OE");
                            if ((category != null) && (value != null)) {
                                factorValues.add(new Attribute(category, value));
                            }
                        }
                    }
                }
            }
        }

        public Element toXML() {
            return null;
        }

        public static ExperimentData createFromXML(Element source) {
            ExperimentData result = new ExperimentData();
            result.fromXML(source);
            return result;
        }

        public int getAttributeCount() {
            return 3 + sampleAttributes.size() + factorValues.size();
        }
    }

    private static class OptionObject {
        String name;
        String value;

        public OptionObject(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String toString() {
            return name;
        }

        public boolean equals(Object obj) {
            return name.equals(obj);
        }
    }

    private void buildInfoPanel(Element root) {
        if (firstButton == null) {
            firstButton = new JButton("<< First");
            firstButton.setActionCommand("first page");
            firstButton.addActionListener(this);
            prevButton = new JButton("< Prev");
            prevButton.setActionCommand("previous page");
            prevButton.addActionListener(this);
            nextButton = new JButton("Next >");
            nextButton.setActionCommand("next page");
            nextButton.addActionListener(this);
            lastButton = new JButton("Last >>");
            lastButton.setActionCommand("last page");
            lastButton.addActionListener(this);
            pageLabel = new JLabel();
            expCountLabel = new JLabel();
            sampleCountLabel = new JLabel();


            infoPanel.setLayout(new BoxLayout(infoPanel, 0));
            infoPanel.add(expCountLabel);
            infoPanel.add(sampleCountLabel);
            infoPanel.add(Box.createHorizontalGlue());
            infoPanel.add(firstButton);
            infoPanel.add(prevButton);
            infoPanel.add(pageLabel);
            infoPanel.add(nextButton);
            infoPanel.add(lastButton);
        }

        expCount = Integer.parseInt(root.getAttributeValue("total"));
        sampleCount = Integer.parseInt(root.getAttributeValue("total-samples"));
        expCountLabel.setText("Experiments: " + expCount + "   ");
        sampleCountLabel.setText("Samples: " + sampleCount);
        pageCount = ((int) Math.ceil(expCount / 20.0D));
        prevButton.setEnabled(pageNum != 1);
        firstButton.setEnabled(pageNum != 1);
        nextButton.setEnabled(pageNum != pageCount);
        lastButton.setEnabled(pageNum != pageCount);
        pageLabel.setText("Page " + pageNum + " of " + pageCount);
    }

    public void actionPerformed(ActionEvent e) {
        if ("update information".equals(e.getActionCommand())) {
            pageNum = 1;
            updateTree();
            return;
        }
        if ("next page".equals(e.getActionCommand())) {
            if (pageNum == pageCount) {
                return;
            }
            pageNum += 1;
            updateTree();
            return;
        }
        if ("previous page".equals(e.getActionCommand())) {
            if (pageNum == 1) {
                return;
            }
            pageNum -= 1;
            updateTree();
            return;
        }
        if ("first page".equals(e.getActionCommand())) {
            pageNum = 1;
            updateTree();
            return;
        }
        if ("last page".equals(e.getActionCommand())) {
            pageNum = pageCount;
            updateTree();
            return;
        }
    }

    private void updateTree() {
        myTree.start();
        new SwingWorker() {
            public Object construct() {
                try {
                    SAXBuilder builder = new SAXBuilder(false);
                    Document myDoc = builder.build(XMLVisualizerBackup.this.buildURL());
                    Element root = myDoc.getRootElement();
                    XMLVisualizerBackup.this.buildInfoPanel(root);
                    DefaultMutableTreeNode treeRoot = XMLVisualizerBackup.makeTree(root);
                    myTree.setModel(new DefaultTreeModel(treeRoot));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            public void finished() {
                myTree.stop();
            }
        }.start();
    }

    public void setSplitter(double percent) {
        if ((percent >= 0.0D) && (percent <= 1.0D)) {
            splitter.setDividerLocation(percent);
        }
    }

    public List<String> getSelectedExpIDs() {
        List<TreePath> paths = checkBoxRenderer.getSelectedPaths();
        ArrayList<String> result = new ArrayList();
        for (TreePath thisPath : paths) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) thisPath.getLastPathComponent();
            if ((node.getUserObject() instanceof ExperimentData)) {
                ExperimentData data = (ExperimentData) node.getUserObject();
                result.add(data.id);
            }
        }
        return result;
    }
}
