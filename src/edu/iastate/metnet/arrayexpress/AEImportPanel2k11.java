package edu.iastate.metnet.arrayexpress;

import edu.iastate.metnet.metaomgraph.FilterableTableModel;
import edu.iastate.metnet.metaomgraph.ui.ClearableTextField;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.UpdatingSortableFilterableTable;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;


public class AEImportPanel2k11
        extends JPanel {
    public static final String BASE_URL = "http://www.ebi.ac.uk/arrayexpress/xml/v2/experiments";
    JTextField searchField;
    JComboBox arrayBox;
    UpdatingSortableFilterableTable expTable;
    JSplitPane splitter;
    JScrollPane expInfoPane;
    JCheckBox normalizeBox;
    JSpinner normalizeSpinner;
    JButton searchButton;
    List<AEXMLParser.XMLNodeInfo> expList;
    UpdateTableThread updateExpThread;

    public AEImportPanel2k11()
            throws IOException {
        expTable = new UpdatingSortableFilterableTable(new DefaultTableModel());
        expTable.setSelectionMode(2);
        expTable.start();

        TreeSet<SpeciesList> arrays = new TreeSet();
        for (SpeciesList s : SpeciesList.values()) {
            if (s.getAcc() != null) {

                arrays.add(s);
            }
        }
        arrayBox = new JComboBox(arrays.toArray());
        arrayBox.setSelectedItem(SpeciesList.ARABIDOPSIS_ATH1);

        updateExpThread = new UpdateTableThread();
        updateExpThread.start();

        searchField = new ClearableTextField("Text Search");
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                expTable.getFilterModel().applyFilter(searchField.getText());
            }


        });
        normalizeBox = new JCheckBox("Normalize selected experiments to:");
        SpinnerNumberModel spinModel = new SpinnerNumberModel(Double.valueOf(100.0D), null, null, Double.valueOf(1.0D));
        normalizeSpinner = new JSpinner(spinModel);

        setLayout(new BorderLayout());
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.weightx = .5;
        c.weighty = .5;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(arrayBox, c);
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = .5;
        searchPanel.add(searchField, c);
        c.gridx = 1;
        c.weightx = .4;
        searchPanel.add(normalizeBox, c);
        c.gridx = 2;
        c.weightx = .1;
        searchPanel.add(normalizeSpinner, c);
        this.add(searchPanel, BorderLayout.NORTH);

        splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitter.setDividerLocation(.5);
        splitter.setLeftComponent(expTable);
        expInfoPane = new JScrollPane();
        splitter.setRightComponent(expInfoPane);
    }

    private class UpdateTableThread extends Thread {
        private UpdateTableThread() {
        }

        public void run() {
            super.run();
            expTable.start();
            SpeciesList selectedSpecies = (SpeciesList) arrayBox.getSelectedItem();
            expList = AEXMLParser.getExperimentList("http://www.ebi.ac.uk/arrayexpress/xml/v2/experiments?array=" + selectedSpecies.getAcc());

            String[] headers = {"ID", "Samples", "Name", "Date"};
            Object[][] data = new Object[expList.size()][];
            for (int i = 0; i < data.length; i++) {
                data[i] = expList.get(i).getListing();
            }
            NoneditableTableModel model = new NoneditableTableModel(data, headers);
            expTable.setModel(model);
            expTable.stop();
        }
    }
}
