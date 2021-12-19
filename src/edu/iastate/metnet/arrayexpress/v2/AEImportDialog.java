package edu.iastate.metnet.arrayexpress.v2;

import edu.iastate.metnet.metaomgraph.ui.ClearableTextField;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.UpdatingSortableFilterableTable;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class AEImportDialog
        extends JDialog {
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
    private UpdatingSortableFilterableTable expTable;
    private List<AEXMLNodeInfo> expList;
    private JScrollPane expPane;
    private JScrollPane infoPane;
    private JSplitPane splitter;
    private JComboBox arrayBox;
    private JButton searchButton;
    private JLabel expLabel;
    private JLabel assayLabel;
    private ClearableTextField searchField;
    private JCheckBox normalizeBox;
    private SpinnerNumberModel normalizeSpinModel;
    private String lastSelectedAcc;
    private boolean canceled;
    private ArrayInfo selectedArray;
    public static final ArrayList<ArrayInfo> arrays = new ArrayList();

    static {
        try {
            ArrayList<String> resources = new ArrayList();
            BufferedReader in = new BufferedReader(new InputStreamReader(AEImportDialog.class.getClassLoader().getResourceAsStream("resource/arrayexpress/arraylist.txt")));
            String thisLine;
            while ((thisLine = in.readLine()) != null) { //String thisLine;
                resources.add(thisLine);
            }
            in.close();
            for (String r : resources) {
                String path = "/resource/arrayexpress/" + r + "/info.txt";
                InputStream instream = AEImportDialog.class.getResourceAsStream(path);
                if (instream == null) {
                    System.out.println("It's null.");
                } else {
                    in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
                    ArrayInfo thisInfo = new ArrayInfo();
                    thisInfo.dir = r;
                    while ((thisLine = in.readLine()) != null) {
                        String[] splitLine = thisLine.split("\t", 2);
                        if (splitLine.length >= 2) {

                            String attrib = splitLine[0];
                            String val = splitLine[1];
                            if ("name".equals(attrib)) {
                                thisInfo.name = val;
                            } else if ("acc".equals(attrib))
                                thisInfo.acc = val;
                        }
                    }
                    arrays.add(thisInfo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AEImportDialog() throws IOException {
        this(null);
    }

    public AEImportDialog(Frame parent) throws IOException {
        super(parent, "ArrayExpress", true);
        canceled = false;
        setLayout(new BorderLayout());

        splitter = new JSplitPane(0);

        NoneditableTableModel model = new NoneditableTableModel(
                new Object[][]{{"Please choose an array and click Search"}},
                new String[]{"Loading..."});
        expTable = new UpdatingSortableFilterableTable(model, "Working...", 0L, 100L) {

            private Color[] myColors = {new Color(215, 25, 28), new Color(253, 174, 97),
                    new Color(255, 255, 191), new Color(166, 217, 106), new Color(26, 150, 65)};

            @Override
			protected Color colorForRow(int row) {
                if ((expList == null) || (row < 0) || (row >= expList.size())) {
                    return super.colorForRow(row);
                }
                AEXMLNodeInfo info = expList.get(getTrueRow(row));
                int score = info.getMIAMEScore() - 1;
                if ((score < 0) || (score >= myColors.length)) {
                    return Color.WHITE;
                }
                return myColors[score];
            }
        };
        expTable.setSelectionMode(2);
        infoPane = new JScrollPane();
        expTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
			public void valueChanged(ListSelectionEvent arg0) {
                if (expTable.getSelectedRowCount() <= 0) {
                    return;
                }
                int selectedRow = expTable.getTrueSelectedRows()[0];
                AEXMLNodeInfo info = expList.get(selectedRow);

                infoPane.setViewportView(info.getTable());
            }
        });
        AEXMLParser.setProgressComponent(expTable);
        expTable.addKeyListener(new KeyAdapter() {
            @Override
			public void keyTyped(KeyEvent e) {
                if ((e.getKeyCode() == 10) || (e.getKeyCode() == 32) || (e.getKeyChar() == ' ')) {
                    int[] rows = expTable.getSelectedRows();
                    int cols = expTable.getColumnCount();
                    for (int row : rows) {
                        for (int col = 0; col < cols; col++) {
                            Object val = expTable.getValueAt(row, col);
                            if ((val instanceof Boolean)) {
                                expTable.setValueAt(Boolean.valueOf(!((Boolean) val).booleanValue()), row, col);
                            }
                        }
                    }
                } else {
                    System.out.println("typed: " + e.getKeyChar());
                }
            }
        });
        expPane = new JScrollPane(expTable);

        splitter.setTopComponent(expPane);

        splitter.setBottomComponent(infoPane);
        add(splitter, "Center");

        JPanel optionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        AbstractAction searchAction = new AbstractAction("Search") {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                AEImportDialog.this.updateExpTable();
            }

        };
        searchField = new ClearableTextField();
        searchField.setDefaultText("Text search");
        searchField.setDefaultTextColor(Color.LIGHT_GRAY);
        searchField.setAction(searchAction);
        arrayBox = new JComboBox(arrays.toArray(new ArrayInfo[0]));
        String[] expTypes = {"Put something here..."};
        JComboBox expTypeBox = new JComboBox(expTypes);
        JCheckBox wholeWordsBox = new JCheckBox("Match Whole Words");
        normalizeBox = new JCheckBox("Normalize selected experiments to:");
        normalizeSpinModel = new SpinnerNumberModel(100.0D, null, null, 1.0D);
        JSpinner spinner = new JSpinner(normalizeSpinModel);
        searchButton = new JButton(searchAction);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        optionPanel.add(searchField, c);
        c.gridx++;
        c.gridwidth = 3;
        optionPanel.add(arrayBox, c);
        c.gridx++;
        c.gridx++;
        c.gridx++;
        c.gridwidth = 1;
        // optionPanel.add(expTypeBox, c);
        c.gridx = 0;
        c.gridy++;
        optionPanel.add(wholeWordsBox, c);
        c.gridx++;
        optionPanel.add(normalizeBox, c);
        c.gridx++;
        optionPanel.add(spinner, c);
        c.gridx++;
        optionPanel.add(searchButton, c);
        add(optionPanel, "North");

        JButton okButton = new JButton(new AbstractAction("OK") {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                canceled = false;
                dispose();
            }
        });
        JButton cancelButton = new JButton(new AbstractAction("Cancel") {
            @Override
			public void actionPerformed(ActionEvent e) {
                canceled = true;
                dispose();
            }

        });
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 4;
        String key;
        if (Utils.isMac()) key = "&#8984";
        else key = "Ctrl-";

        JLabel instructionLabel = new JLabel("<html><font size=\"-2\">Press space to toggle a selected row, " + key + "A to select all visible rows</font></html>");
        bottomPanel.add(instructionLabel, c);
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
        bottomPanel.add(new JPanel(), c);
        c.gridx++;
        c.weightx = 0;
        bottomPanel.add(okButton, c);
        c.gridx++;
        bottomPanel.add(cancelButton, c);
        c.gridx++;
        c.weightx = 1;
        bottomPanel.add(new JPanel(), c);
        add(bottomPanel, "South");

        setSize(800, 600);
        setLocationRelativeTo(null);
        splitter.setDividerLocation(0.5D);
    }


    @Override
	public void setVisible(boolean b) {
        if (b) canceled = true;
        super.setVisible(b);
    }

    private void updateExpTable() {
        expTable.start();
        searchButton.setEnabled(false);
        selectedArray = ((ArrayInfo) arrayBox.getSelectedItem());
        new ExpUpdateThread().start();
    }

    public List<AEXMLNodeInfo> getSelectedExps() {
        ArrayList<AEXMLNodeInfo> result = new ArrayList();
        if (!(expTable.getModel().getValueAt(0, 0) instanceof Boolean)) {
            return result;
        }
        int rows = expTable.getModel().getRowCount();
        for (int i = 0; i < rows; i++) {
            Boolean selected = (Boolean) expTable.getModel().getValueAt(i, 0);
            if (selected.booleanValue()) {
                result.add(expList.get(expTable.getTrueRow(i)));
            }
        }
        return result;
    }

    public List<String> getSelectedExpIDs() {
        List<AEXMLNodeInfo> exps = getSelectedExps();
        ArrayList<String> result = new ArrayList();
        for (AEXMLNodeInfo exp : exps) {
            result.add(exp.accession);
        }
        return result;
    }

    public Double getNormalizeValue() {
        if (!normalizeBox.isSelected()) return null;

        return new Double(normalizeSpinModel.getValue() + "");
    }

    public String getSpecies() {
        return arrayBox.getSelectedItem().toString();
    }


    public boolean isCanceled() {
        return canceled;
    }

    private class ExpUpdateThread extends Thread {
        private ExpUpdateThread() {
        }

        @Override
		public void run() {
            String acc = "A-AFFY-2";
            if (arrayBox != null) {
                AEImportDialog.ArrayInfo info = (AEImportDialog.ArrayInfo) arrayBox.getSelectedItem();
                acc = acc;
            }
            if (!acc.equals(lastSelectedAcc)) {
                updateArray(acc);
                lastSelectedAcc = acc;
            }
            applyFilter(searchField.getText());
            expTable.stop();
            searchButton.setEnabled(true);
        }

        private void updateArray(String acc) {
            String[] headers = {"Download", "ID", "Assays", "MIAME Score", "Name", "Updated"};
            String location = "http://www.ebi.ac.uk/arrayexpress/xml/v2/experiments?array=" + acc;
            System.out.println(location);
            expList = AEXMLParser.getExperimentList(location);
            ArrayList<Object[]> tableRows = new ArrayList();
            int sampleCount = 0;
            for (int i = 0; i < expList.size(); i++) {
                AEXMLNodeInfo info = expList.get(i);
                if (info.hasProcessedData) {
                    tableRows.add(info.getListing());
                    try {
                        sampleCount += Integer.parseInt(info.getData("samples") + "");
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                } else {
                    expList.remove(i);
                    i--;
                }
            }
            System.out.println("Total samples: " + sampleCount);
            Object[][] tableData = new Object[tableRows.size()][headers.length];
            for (int row = 0; row < tableData.length; row++) {
                tableData[row] = tableRows.get(row);
            }
            NoneditableTableModel model = new NoneditableTableModel(tableData, headers);
            model.setColumnEditable(0, true);
            expTable.setModel(model);
        }

        private void applyFilter(String filter) {
            if ((filter == null) || ("".equals(filter.trim()))) {
                expTable.getFilterModel().clearFilter();
                return;
            }
            ArrayList<Integer> goodRows = new ArrayList();
            int index = 0;
            for (AEXMLNodeInfo exp : expList) {
                if (exp.matches(filter)) {
                    goodRows.add(Integer.valueOf(index));
                }
                index++;
            }
            expTable.getFilterModel().filterToRows(goodRows);
        }
    }

    public ArrayInfo getArray() {
        return selectedArray;
    }


    public static void main(String[] args)
            throws Exception {
        AEImportDialog dialog = new AEImportDialog();
        dialog.setDefaultCloseOperation(2);
        dialog.setVisible(true);

        if (dialog.isCanceled()) {
            System.out.println("Canceled");
            System.exit(0);
        } else {
            List<String> ids = dialog.getSelectedExpIDs();
            String loc = null;
            for (String id : ids) {
                System.out.println(id);
                if (loc == null) {
                    loc = "http://www.ebi.ac.uk/arrayexpress/xml/v2/files?accession=" + id;
                } else {
                    loc = loc + "+OR+" + id;
                }
            }
            System.out.println(loc);
            File dest = new File("/Users/mhhur/Desktop/AEDownloads/test.txt");
            File tempDir = new File("/Users/mhhur/Desktop/MOG.temp");

            AEProjectMaker.createProject(dialog.getSelectedExps(), dialog.getArray(), dest, tempDir);
        }
    }

    public static class ArrayInfo {
        String name;
        String acc;
        String dir;

        public ArrayInfo() {
        }

        @Override
		public String toString() {
            return name;
        }
    }


    private static String[] getResourceListing(Class clazz, String path)
            throws URISyntaxException, IOException {
        URL dirURL = clazz.getResource(path);
        if ((dirURL != null) && (dirURL.getProtocol().equals("file"))) {
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }
        if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));

            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries();

            Set<String> result = new HashSet();

            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) {
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {

                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }
        if (dirURL.getProtocol().equals("file")) {
            return new File(dirURL.toURI()).list();
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }
}
