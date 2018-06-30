package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.my.My;
import edu.iastate.metnet.my.edit.EntityList_E;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class MetNet3ListExporter {
    private static StripedTable table;
    private static JComboBox columnBox;
    private static JList list;

    public MetNet3ListExporter() {
    }

    public static void doExport(final MetaOmProject myProject) {
        String[] lists = myProject.getGeneListNames();
        if (lists.length <= 1) {
            JOptionPane.showMessageDialog(null,
                    "This project contains no lists.", "Error",
                    0);
            return;
        }


        if (!My.IsAuthenticated()) {
            boolean loggedIn = MetNet3LoginDialog.showDialog(null);
            if (!loggedIn) return;
        }

        final Object[][] data = new Object[lists.length - 1][2];
        for (int i = 1; i < lists.length; i++) {
            data[(i - 1)][0] = new Boolean(false);
            data[(i - 1)][1] = lists[i];
        }
        String[] headers = {"Export", "List Name"};
        NoneditableTableModel model = new NoneditableTableModel(data, headers);
        model.setColumnEditable(0, true);
        table = new StripedTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setResizable(false);
        table.setSelectionMode(0);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);

        final JDialog dialog = new JDialog(edu.iastate.metnet.metaomgraph.MetaOmGraph.getMainWindow(),"Export Lists to MetNet3", true);
        dialog.add(new JScrollPane(table), "Center");

        JPanel columnSelectionPanel = new JPanel();
        JLabel columnLabel = new JLabel("Export IDs from the following column: ");
        columnBox = new JComboBox(myProject.getInfoColumnNames());
        columnBox.setEditable(false);
        columnBox.setSelectedIndex(myProject.getDefaultColumn());
        columnSelectionPanel.add(columnLabel);
        columnSelectionPanel.add(columnBox);
        dialog.add(columnSelectionPanel, "North");
        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final ArrayList<String> exportUs = new ArrayList();

                for (int i = 0; i < data.length; i++) {
                    if ((Boolean) data[i][0]) {
                        exportUs.add(data[i][1] + "");
                    }
                }
                if (exportUs.isEmpty()) {
                    dialog.dispose();
                    return;
                }
                int size = 0;
                for (String exportMe : exportUs) {
                    size += myProject.getGeneListRowNumbers(exportMe).length;
                }
                final BlockingProgressDialog progress = new BlockingProgressDialog(
                        dialog, "Exporting", "Exporting " + exportUs.get(0), 0L,
                        size, true);
                progress.setCancelable(false);
                new Thread() {
                    public void run() {
                        // Create and export lists
                        for (String exportMe : exportUs) {
                            progress.setMessage("Exporting " + exportMe);
                            EntityList_E list = EntityList_E.Create(exportMe,
                                    "Exported from MetaOmGraph");
                            int[] rows = myProject.getGeneListRowNumbers(exportMe);
                            int col = columnBox.getSelectedIndex();
                            for (int row : rows) {
                                Object addMe = myProject.getRowName(row)[col];
                                if (addMe == null) {
                                    continue;
                                }
                                if ("".equals(addMe.toString().trim())) {
                                    continue;
                                }
                                list.AttachPart(addMe.toString());
                                progress.increaseProgress(1);
                            }
                        }
                        progress.dispose();
                    }
                }.start();
                progress.setVisible(true);
                dialog.dispose();
            }

        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }

        });
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, "South");

        list = new JList();
        dialog.add(new JScrollPane(list), "East");

        ListDisplayer displayer = new ListDisplayer(myProject);
        table.getSelectionModel().addListSelectionListener(displayer);
        columnBox.addActionListener(displayer);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static class ListDisplayer implements ListSelectionListener, ActionListener {
        private MetaOmProject myProject;

        public ListDisplayer(MetaOmProject myProject) {
            this.myProject = myProject;
        }

        public void doIt() {
            int row = MetNet3ListExporter.table.getSelectedRow();
            if (row < 0) {
                return;
            }
            String listName = myProject.getGeneListNames()[(row + 1)];
            int col = MetNet3ListExporter.columnBox.getSelectedIndex();
            DefaultListModel listModel = new DefaultListModel();
            Object[][] rowNames = myProject.getGeneListRowNames(listName);
            for (int i = 0; i < rowNames.length; i++) {
                Object val = rowNames[i][col];
                if (val != null) {

                    if (!"".equals(val.toString().trim())) {

                        listModel.addElement(val);
                    }
                }
            }
            MetNet3ListExporter.list.setModel(listModel);
        }

        public void actionPerformed(ActionEvent e) {
            doIt();
        }

        public void valueChanged(ListSelectionEvent e) {
            doIt();
        }
    }
}
