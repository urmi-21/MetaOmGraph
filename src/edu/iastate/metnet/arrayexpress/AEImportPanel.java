package edu.iastate.metnet.arrayexpress;

import edu.iastate.metnet.metaomgraph.SwingWorker;
import edu.iastate.metnet.metaomgraph.ui.ClearableTextField;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;
import edu.iastate.metnet.metaomgraph.utils.JDomUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalAddException;
import org.jdom.JDOMException;
import org.jdom.Parent;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class AEImportPanel
        extends JPanel
        implements ActionListener {
    private JTable table;
    private NoneditableTableModel model;
    private UpdatingTree myTree;
    private JComboBox arrayBox;
    private JComboBox perPageBox;
    private JComboBox expTypeBox;
    private DefaultComboBoxModel arrayModel;
    private DefaultComboBoxModel expTypeModel;
    private ClearableTextField searchField;
    private JCheckBox wholeWordsBox;
    private JButton searchButton;
    private JButton firstButton;
    private JButton prevButton;
    private JButton nextButton;
    private JButton lastButton;
    private JButton selectAllButton;
    private JButton selectNoneButton;
    private JPanel infoPanel;
    private JLabel expCountLabel;
    private JLabel sampleCountLabel;
    private JLabel maxPageLabel;
    private JTextField pageField;
    private JCheckBox normalizeBox;
    private JSpinner normalizeSpinner;
    private int pageNum = 1;

    private int pageCount = 1;

    private int expCount = 0;

    private int sampleCount = 0;

    private int ath1Index = 0;

    private JSplitPane splitter;

    private static CheckBoxTree.CheckBoxTreeCellRenderer<AEXMLParser.XMLNodeInfo> checkBoxRenderer2;

    public static final String FIRST_COMMAND = "first page";

    public static final String PREV_COMMAND = "previous page";

    public static final String NEXT_COMMAND = "next page";

    public static final String LAST_COMMAND = "last page";

    public static final String UPDATE_COMMAND = "update information";

    public static final String HGU133A_ARRAY_NAME = "Homo Sapiens (HG-U133A)";

    public static final String MOUSE_4302_ARRAY_NAME = "Mus Musculus (430 2.0)";

    public static final String ATH1_ARRAY_NAME = "Arabidopsis Thaliana (ATH1)";

    public static final String SOYBEAN_ARRAY_NAME = "Soybean Genome Array";

    public static final String RAT_230_ARRAY_NAME = "Rat Genome (230 2.0)";

    public static final String YEAST_ARRAY_NAME = "Yeast Genome (S98)";

    public static final String RICE_ARRAY_NAME = "Rice Genome Array";

    public static final String BARLEY_ARRAY_NAME = "Barley Genome Array";

    public static final String HGU133PLUS2_ARRAY_NAME = "Homo Sapiens (HG-U133 Plus 2.0)";

    public static final String YEAST2_ARRAY_NAME = "Yeast Genome 2.0 Array";

    public static final String ZEBRAFISH_ARRAY_NAME = "Zebrafish Genome Array";

    public static final String SELECT_ALL_COMMAND = "Select all";

    public static final String SELECT_NONE_COMMAND = "Select none";

    private static String selectedSpecies;

    private static TreeMap<String, String> celNames;

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
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                List<String> ids = getSelectedExpIDs();
                for (String id : ids) {
                    System.out.println(id);
                }
            }
        });
    }


    public AEImportPanel() {
        setLayout(new BorderLayout());


        arrayBox = new JComboBox(new String[]{"Loading..."});
        arrayBox.setEnabled(false);
        expTypeBox = new JComboBox(new String[]{"Loading..."});
        expTypeBox.setEnabled(false);
        searchField = new ClearableTextField();
        searchField.setDefaultText("Text search");
        searchField.setDefaultTextColor(Color.LIGHT_GRAY);
        searchField.setEnabled(false);
        try {
            myTree = new UpdatingTree(null);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


        checkBoxRenderer2 = new CheckBoxTree.CheckBoxTreeCellRenderer(myTree);
        myTree.start();
        model = new NoneditableTableModel(null, new String[]{"Attribute",
                "Value"});
        table = new StripedTable(model);
        wholeWordsBox = new JCheckBox("Match whole words");
        searchButton = new JButton("Search");
        searchButton.setEnabled(false);
        perPageBox = new JComboBox(new Integer[]{Integer.valueOf(25), Integer.valueOf(50), Integer.valueOf(100), Integer.valueOf(250), Integer.valueOf(500)});
        perPageBox.setEnabled(false);


        infoPanel = new JPanel(new BorderLayout());
        splitter = new JSplitPane(0, new JScrollPane(
                myTree), new JScrollPane(table));
        splitter.setResizeWeight(0.5D);


        JPanel normalizePanel = new JPanel();


        normalizePanel
                .setLayout(new BoxLayout(normalizePanel, 0));


        normalizeBox = new JCheckBox("Normalize selected experiments to: ");
        normalizeSpinner = new JSpinner(new SpinnerNumberModel(Double.valueOf(100.0D), null,
                null, Double.valueOf(1.0D)));


        normalizeBox.setSelected(true);
        normalizeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                normalizeSpinner.setEnabled(normalizeBox.isSelected());
            }

        });
        normalizePanel.add(normalizeBox);
        normalizePanel.add(normalizeSpinner);


        JPanel newFilterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = .5;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        newFilterPanel.add(searchField, c);
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        newFilterPanel.add(wholeWordsBox, c);
        c.gridy = 0;
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        newFilterPanel.add(arrayBox, c);
        c.gridy = 1;
//	newFilterPanel.add(expTypeBox,c);
//	c.gridy = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        newFilterPanel.add(normalizePanel, c);
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0;
        c.insets = new Insets(0, 5, 0, 0);
        newFilterPanel.add(new JLabel("Experiments per page:"), c);
        c.gridx = 3;
        newFilterPanel.add(perPageBox, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        newFilterPanel.add(searchButton, c);
        this.add(splitter, BorderLayout.CENTER);
        this.add(newFilterPanel, BorderLayout.NORTH);
        this.add(infoPanel, BorderLayout.SOUTH);
        splitter.setDividerLocation(0.5D);


        arrayBox
                .setSize(new Dimension(450, arrayBox.getPreferredSize().height));
        searchField.setSize(new Dimension(450,
                searchField.getPreferredSize().height));

        final Vector<OptionObject> arrayList = new Vector();
        final Vector<OptionObject> expTypeList = new Vector();
        final AEImportPanel myself = this;
        new SwingWorker() {


            public Object construct() {


                try {


                    arrayList.add(new AEImportPanel.OptionObject("Arabidopsis Thaliana (ATH1)", "13851999"));
                    arrayList.add(new AEImportPanel.OptionObject("Barley Genome Array", "287590865"));
                    arrayList.add(new AEImportPanel.OptionObject("Homo Sapiens (HG-U133A)",
                            "302382080"));
                    arrayList.add(new AEImportPanel.OptionObject("Homo Sapiens (HG-U133 Plus 2.0)", "405156763"));
                    arrayList.add(new AEImportPanel.OptionObject("Mus Musculus (430 2.0)",
                            "417942717&species=mus musculus"));
                    arrayList.add(new AEImportPanel.OptionObject("Rat Genome (230 2.0)", "383811809"));
                    arrayList.add(new AEImportPanel.OptionObject("Rice Genome Array", "1656801082"));
                    arrayList.add(new AEImportPanel.OptionObject("Soybean Genome Array", "1134087197"));
                    arrayList.add(new AEImportPanel.OptionObject("Yeast Genome (S98)", "119528504"));
                    arrayList.add(new AEImportPanel.OptionObject("Yeast Genome 2.0 Array", "486625854"));
                    arrayList.add(new AEImportPanel.OptionObject("Zebrafish Genome Array", "472119251"));
                    AEImportPanel.selectedSpecies = "Arabidopsis Thaliana (ATH1)";

                    expTypeList.add(new AEImportPanel.OptionObject("Any experiment type", ""));
                    expTypeList.add(new AEImportPanel.OptionObject("antigen profiling", "antigen+profiling"));
                    expTypeList.add(new AEImportPanel.OptionObject("ChIP-Chip", "ChIP-Chip"));
                    expTypeList.add(new AEImportPanel.OptionObject("ChIP-seq", "ChIP-seq"));
                    expTypeList.add(new AEImportPanel.OptionObject("comparative genomic hybridization", "comparative+genomic+hybridization"));
                    expTypeList.add(new AEImportPanel.OptionObject("genotyping", "genotyping"));
                    expTypeList.add(new AEImportPanel.OptionObject("metabolimic profiling", "metabolimic+profiling"));
                    expTypeList.add(new AEImportPanel.OptionObject("methylation profiling", "methylation+profiling"));
                    expTypeList.add(new AEImportPanel.OptionObject("microRNA profiling", "microRNA+profiling"));
                    expTypeList.add(new AEImportPanel.OptionObject("other", "other"));
                    expTypeList.add(new AEImportPanel.OptionObject("proteomic profiling", "proteomic+profiling"));
                    expTypeList.add(new AEImportPanel.OptionObject("RNAi", "RNAi"));
                    expTypeList.add(new AEImportPanel.OptionObject("RNA-seq", "RNA-seq"));
                    expTypeList.add(new AEImportPanel.OptionObject("tiling path", "tiling+path"));
                    expTypeList.add(new AEImportPanel.OptionObject("transcription profiling", "transcription+profiling"));
                    expTypeList.add(new AEImportPanel.OptionObject("translation profiling", "translation+profiling"));


                    URL aesource = AEImportPanel.this.buildURL();


                    Document myDoc = new SAXBuilder().build(aesource);


                    Element root = myDoc.getRootElement();
                    AEImportPanel.this.buildInfoPanel(root);
                    DefaultMutableTreeNode treeRoot = AEImportPanel.makeTree(root);
                    myTree.setModel(new DefaultTreeModel(treeRoot));

                    myTree.addTreeSelectionListener(new TableUpdater(table,
                            model));
                } catch (IOException e) {
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(
                            myTree,
                            "Error communicating with ArrayExpress.\nThe site may be down, or you may be having connection problems.\nPlease try again later.",
                            "Error", 0);
                } catch (JDOMException e) {
                    e.printStackTrace();
                }
                return new Boolean(true);
            }

            public void finished() {
                if (get() == null) {
                    return;
                }

                arrayModel = new DefaultComboBoxModel(arrayList);

                arrayBox.setModel(arrayModel);
                arrayBox.setSelectedIndex(ath1Index);


                arrayBox.setActionCommand("update information");

                expTypeModel = new DefaultComboBoxModel(expTypeList);
                expTypeBox.setModel(expTypeModel);
                expTypeBox.setActionCommand("update information");

                searchField.setActionCommand("update information");
                searchField.addActionListener(myself);
                searchButton.setActionCommand("update information");
                searchButton.addActionListener(myself);

                arrayBox.setEnabled(true);
                expTypeBox.setEnabled(true);
                searchField.setEnabled(true);
                searchButton.setEnabled(true);
                perPageBox.setEnabled(true);
                myTree.stop();
            }
        }.start();
    }

    public static DefaultMutableTreeNode makeTree(Element root) {
        boolean addChildren = false;
        DefaultMutableTreeNode result = null;
        String lname = root.getName().toLowerCase();
        if (lname.equals("experiments")) {
            result = new DefaultMutableTreeNode("Experiments");
            addChildren = true;
        } else if (lname.equals("experiment")) {


            if ((root.getAttribute("hybs") == null) ||
                    (root.getAttributeValue("hybs").equals(""))) {
                return null;
            }
            ExperimentData data = ExperimentData.createFromXML(root);
            if (!data.hasProcessedData) {
                return null;
            }
            //DefaultMutableTreeNode result = new DefaultMutableTreeNode(data);
            addChildren = true;
        } else { //DefaultMutableTreeNode result;
            if (lname.equals("sampleattributes")) {
                result = new DefaultMutableTreeNode(
                        SampleAttributes.createFromXML(root));
            } else { //DefaultMutableTreeNode result;
                if (lname.equals("miamescores")) {
                    result = new DefaultMutableTreeNode(MIAMEScore.createFromXML(root));
                } else { //DefaultMutableTreeNode result;
                    if (lname.equals("description")) {
                        result = new DefaultMutableTreeNode(Description.createFromXML(root));
                    } else { //DefaultMutableTreeNode result;
                        if (lname.equals("providers")) {
                            result = new DefaultMutableTreeNode(Providers.createFromXML(root));
                        } else { //DefaultMutableTreeNode result;
                            if (lname.equals("bibliography")) {
                                result = new DefaultMutableTreeNode(
                                        Bibliography.createFromXML(root));
                            } else { //DefaultMutableTreeNode result;
                                if (lname.equals("experimentdesigns")) {
                                    result = new DefaultMutableTreeNode(
                                            ExperimentDesigns.createFromXML(root));
                                } else { //DefaultMutableTreeNode result;
                                    if (lname.equals("factorvalues")) {
                                        result = new DefaultMutableTreeNode(
                                                FactorValues.createFromXML(root));
                                    } else { //DefaultMutableTreeNode result;
                                        if (lname.equals("secondaryaccessions")) {
                                            result = new DefaultMutableTreeNode(
                                                    SecondaryAccessions.createFromXML(root));
                                        } else
                                            result = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (addChildren) {
            List children = root.getChildren();
            for (Object thisChild : children) {
                if ((thisChild instanceof Element)) {
                    DefaultMutableTreeNode addMe = makeTree((Element) thisChild);
                    if (addMe != null) {
                        System.out.println(addMe);
                        result.add(addMe);
                        if ((result.getUserObject() instanceof ExperimentData)) {
                            ((ExperimentData) result.getUserObject()).addMetadata(addMe.getUserObject());
                        }
                    } else {
                        System.out.println("Tried to add " + thisChild +
                                " but was null");
                    }
                }
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

        int written = 0;
        char[] data = new char[100];
        while ((in.read(data)) >= 0) {
            String line = new String(data).trim();
            // System.out.println(thisLine);
            if (line.length() > 0) {
                out.write(data);
                written++;
                if (written % 50 == 0) {
                    // System.out.println("Wrote " + written + " times");
                }
            } else {
                // System.out.println("Blank line after write " + written);
            }
        }


        in.close();
        out.close();
    }

    private URL buildURL() throws MalformedURLException {
        String organism = "";
        String array = "13851999";


        if (arrayBox.getSelectedItem() instanceof OptionObject) {
            array = ((OptionObject) arrayBox.getSelectedItem()).value;
            selectedSpecies = arrayBox.getSelectedItem().toString();
        }


        StringBuilder result = new StringBuilder(
                "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_expts.jsp?sort_by=releasedate&sort_order=descending");
        result.append("&page-size=" + perPageBox.getSelectedItem());
        result.append("&page-number=" + pageNum);
        result.append("&organism=");
        result.append(organism);
        result.append("&array=");
        result.append(array);

        result.append("&keyword=" + searchField.getText());
        if (wholeWordsBox.isSelected()) {
            result.append("&wholewords=checked");
        }
        if ((expTypeBox.getSelectedItem() instanceof OptionObject)) {
            result.append("&exptype=" + ((OptionObject) expTypeBox.getSelectedItem()).value);
        }
        System.out.println(result.toString());
        return new URL(result.toString());
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
            selectAllButton = new JButton("Select All");
            selectAllButton.setActionCommand("Select all");
            selectAllButton.addActionListener(this);
            selectNoneButton = new JButton("Select None");
            selectNoneButton.setActionCommand("Select none");
            selectNoneButton.addActionListener(this);
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
            pageField = new JTextField();
            pageField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        int newPage = Integer.parseInt(pageField.getText());
                        if (newPage > pageCount) {
                            newPage = pageCount;
                        }
                        if (newPage < 1) {
                            newPage = 1;
                        }
                        pageField.setText(newPage + "");
                        pageNum = newPage;
                        updateTree();

                        return;
                    } catch (NumberFormatException nfe) {
                        pageField.setText(pageNum + "");
                    }

                }
            });
            pageField.setPreferredSize(new Dimension(50,
                    pageField.getPreferredSize().height));
            pageField.setMaximumSize(new Dimension(50,
                    pageField.getPreferredSize().height));

            maxPageLabel = new JLabel();
            expCountLabel = new JLabel();
            sampleCountLabel = new JLabel();


            infoPanel.setLayout(new BoxLayout(infoPanel, 0));
            infoPanel.add(expCountLabel);
            infoPanel.add(sampleCountLabel);
            infoPanel.add(Box.createHorizontalGlue());
            infoPanel.add(selectAllButton);
            infoPanel.add(selectNoneButton);
            infoPanel.add(Box.createHorizontalGlue());
            infoPanel.add(firstButton);
            infoPanel.add(prevButton);
            infoPanel.add(new JLabel("Page: "));
            infoPanel.add(pageField);
            infoPanel.add(maxPageLabel);
            infoPanel.add(nextButton);
            infoPanel.add(lastButton);
        }

        expCount = Integer.parseInt(root.getAttributeValue("total"));
        sampleCount = Integer.parseInt(root.getAttributeValue("total-samples"));
        expCountLabel.setText("Experiments: " + expCount + "   ");
        sampleCountLabel.setText("Chips: " + sampleCount);
        pageCount = ((int) Math.ceil(expCount /
                ((Integer) perPageBox.getSelectedItem()).floatValue()));
        prevButton.setEnabled(pageNum != 1);
        firstButton.setEnabled(pageNum != 1);
        nextButton.setEnabled(pageNum != pageCount);
        lastButton.setEnabled(pageNum != pageCount);
        pageField.setText(pageNum + "");
        maxPageLabel.setText("/" + pageCount);
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
        if ("Select all".equals(e.getActionCommand())) {
            checkBoxRenderer2.selectAll();
            return;
        }
        if ("Select none".equals(e.getActionCommand())) {
            checkBoxRenderer2.selectNone();
            return;
        }
    }

    private void updateTree() {
        myTree.start();
        arrayBox.setEnabled(false);
        expTypeBox.setEnabled(false);

        perPageBox.setEnabled(false);
        searchButton.setEnabled(false);
        searchField.setEnabled(false);
        new SwingWorker() {
            public Object construct() {
                try {
                    SAXBuilder builder = new SAXBuilder(false);
                    Document myDoc = builder.build(AEImportPanel.this.buildURL());
                    Element root = myDoc.getRootElement();
                    AEImportPanel.this.buildInfoPanel(root);
                    DefaultMutableTreeNode treeRoot = AEImportPanel.makeTree(root);
                    myTree.setModel(new DefaultTreeModel(treeRoot));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            public void finished() {
                myTree.stop();
                arrayBox.setEnabled(true);
                expTypeBox.setEnabled(true);

                perPageBox.setEnabled(true);
                searchButton.setEnabled(true);
                searchField.setEnabled(true);
            }
        }.start();
    }

    public void setSplitter(double percent) {
        if ((percent >= 0.0D) && (percent <= 1.0D)) {
            splitter.setDividerLocation(percent);
        }
    }

    public static List<String> getSelectedExpIDs() {
        ArrayList<String> result = new ArrayList();


        List<AEXMLParser.XMLNodeInfo> os = checkBoxRenderer2.getSelectedPaths();
        for (AEXMLParser.XMLNodeInfo o : os) {
            result.add(o.accession);
        }


        return result;
    }


    public void doLayout() {
        splitter.setDividerLocation(0.5D);
        super.doLayout();
    }

    public Double getNormalizeValue() {
        if (!normalizeBox.isSelected()) {
            return null;
        }
        return new Double(normalizeSpinner.getValue() + "");
    }

    public Element makeMOGMetadata() {
        Element result = new Element("Experiments");
        List<ExperimentData> selectedData = new ArrayList();

        celNames = new TreeMap();
        for (ExperimentData data : selectedData) {

            Element exp = new Element("Experiment").setAttribute("name", data.name);
            exp.addContent(new Element("ExperimentName").setText(data.name));
            exp.addContent(new Element("ExperimentID").setText(data.id));

            ArrayList<Object> metadata = data.getMetadata();
            for (Object thisMetadata : metadata) {
                if (thisMetadata instanceof Description) {
                    Description descrip = (Description) thisMetadata;
                    Element descripElement;
                    if (descrip.generated) {
                        descripElement = new Element("GeneratedDescription");
                    } else {
                        descripElement = new Element("ExperimentDescription");
                    }
                    descripElement.setText(descrip.descrip);
                    exp.addContent(descripElement);
                } else if (thisMetadata instanceof ExperimentDesigns) {
                    ArrayList<String> designs = ((ExperimentDesigns) thisMetadata)
                            .getDesigns();
                    for (String thisDesign : designs) {
                        exp.addContent(new Element("ExperimentDesign")
                                .setText(thisDesign));
                    }
                } else if (thisMetadata instanceof SampleAttributes) {
                    Object[][] attribs = ((SampleAttributes) thisMetadata)
                            .getTableData();
                    for (Object[] thisAttrib : attribs) {
                        String name = JDomUtils
                                .convertToValidElementName(thisAttrib[0] + "");
                        exp.addContent(new Element(name).setText(thisAttrib[1]
                                + ""));
                    }
                }
            }

            exp.addContent(new Element("Software").setText(data.getSoftware()));
            ArrayList<String> samples = AEDataDownloader.getSampleNames().get(data.id);
            if ((samples != null) && (samples.size() > 0)) {

                for (String sample : samples) {
                    System.out.println("Sample: " + sample);
                }
                Object sdrfData = null;
                boolean isCel = false;
                try {
                    String firstSample = samples.get(0);
                    if ((firstSample.endsWith(".CEL")) && (firstSample.startsWith("GSM"))) {
                        System.out.println("It's a cel");
                        isCel = true;
                    }
                    sdrfData = data.getSDRFData(samples);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                for (String thisSample : samples) {

                    Element sampleElement = new Element("Sample").setAttribute(
                            "name", thisSample);
                    sampleElement.addContent(new Element("SampleName")
                            .setText(thisSample));
                    if (sdrfData != null) {
                        Element[] addUs = (Element[]) ((TreeMap) sdrfData).get(thisSample);
                        if (addUs != null) {
                            for (Element addMe : addUs) {
                                try {
                                    if ((isCel) && (addMe.getName().equals("Comment [Sample_source_name]"))) {
                                        System.out.println("Adding better cel name for " + sampleElement.getAttributeValue("name") + ": " + addMe.getText());
                                        celNames.put(thisSample, addMe.getText());
                                        sampleElement.getChild("SampleName").setText(addMe.getText());
                                        sampleElement.addContent(new Element("ScanName").setText(thisSample));
                                        sampleElement.setAttribute("name", addMe.getText());
                                    } else if ((isCel) && (addMe.getName().equals("HybridizationName"))) {
                                        System.out.println("Adding better cel name for " + sampleElement.getAttributeValue("name") + ": " + addMe.getText());
                                        celNames.put(thisSample, addMe.getText());
                                        sampleElement.getChild("SampleName").setText(addMe.getText());
                                        sampleElement.addContent(new Element("ScanName").setText(thisSample));
                                        sampleElement.setAttribute("name", addMe.getText());

                                    } else {
                                        sampleElement.addContent(addMe);
                                    }
                                } catch (IllegalAddException iae) {
                                    System.err.println("Illegal add of " +
                                            addMe.getName());
                                    System.err.println("Sample: " + thisSample);
                                    System.err.println("Experiment: " + data.name);
                                    System.err.println("Current parent: " +
                                            ((Element) addMe.getParent())
                                                    .getAttributeValue("name"));
                                    System.err.println("Current experiment: " +
                                            ((Element) addMe.getParent()
                                                    .getParent())
                                                    .getAttributeValue("name"));
                                    iae.printStackTrace();
                                }
                            }
                        } else {
                            System.out.println("Couldn't get SDRF data for sample " +
                                    thisSample);
                        }
                    }
                    exp.addContent(sampleElement);
                }
                result.addContent(exp);
            }
        }
        return result;
    }

    public static String getCelName(String celName) {
        if (celNames == null) {
            return null;
        }
        return celNames.get(celName);
    }


    public void outputMOGMetadata(File dest)
            throws IOException {
        Element printMe = makeMOGMetadata();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-16");
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        BufferedWriter out = new BufferedWriter(new FileWriter(dest));
        output.output(printMe, out);
        out.close();
        System.out.println("Output to: " + dest.getAbsolutePath());
    }

    public String getSpecies() {
        return selectedSpecies;
    }
}
