package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.my.EntityList;
import edu.iastate.metnet.my.EntityListPart;
import edu.iastate.metnet.my.My;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class MetNet3ListImportPanel extends JPanel {
    private boolean loggedIn;
    private EntityList[] lists;
    private UpdatingTable listTable;
    private UpdatingList entryList;

    public MetNet3ListImportPanel() throws IOException {
        if (!My.IsAuthenticated()) {
            loggedIn = MetNet3LoginDialog.showDialog(null);
            if (!loggedIn) {
                JLabel failLabel = new JLabel("Login failed.");
                add(failLabel);
            }
        } else {
            loggedIn = true;
        }

        listTable = new UpdatingTable();
        listTable.start();
        entryList = new UpdatingList();

        JScrollPane listPane = new JScrollPane(listTable);
        JScrollPane entryPane = new JScrollPane(entryList);
        listPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Lists"));
        entryPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Entries"));
        listTable.setSelectionMode(0);
        listTable.setRowSelectionAllowed(true);
        listTable.setColumnSelectionAllowed(false);
        listTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        int row = listTable.getSelectedRow();
                        if (row < 0) return;
                        if (row >= lists.length) {
                            System.err.println("Selected row: " + row + ", list count: " + lists.length);
                            return;
                        }
                        entryList.start();
                        DefaultListModel model = new DefaultListModel();

                        EntityListPart[] parts = lists[row].getParts();
                        for (EntityListPart part : parts) {
                            model.addElement(part.name);
                        }
                        entryList.setModel(model);
                        entryList.stop();
                    }
                });
        setLayout(new BorderLayout());
        add(listPane, "Center");
        add(entryPane, "East");
        new Thread() {

            public void run() {
                lists = EntityList.list();
                Object[][] data = new Object[lists.length][2];
                for (int i = 0; i < lists.length; i++) {
                    data[i][0] = new Boolean(false);
                    data[i][1] = lists[i].name;
                }

                String[] headers = {"Import", "List Name"};
                NoneditableTableModel model = new NoneditableTableModel(data, headers);
                model.setColumnEditable(0, true);
                listTable.setModel(model);
                listTable.getColumnModel().getColumn(0).setPreferredWidth(50);
                listTable.getColumnModel().getColumn(0).setMaxWidth(50);
                listTable.getColumnModel().getColumn(0).setResizable(false);
                listTable.stop();
            }
        }.start();
    }

    public EntityList[] getLists() {
        if (listTable == null) return null;

        int count = 0;
        for (int i = 0; i < listTable.getRowCount(); i++) {
            if (((Boolean) listTable.getValueAt(i, 0)).booleanValue()) {
                count++;
            }
        }
        EntityList[] result = new EntityList[count];
        int addHere = 0;
        for (int i = 0; i < listTable.getRowCount(); i++) {
            if (((Boolean) listTable.getValueAt(i, 0)).booleanValue()) {
                result[(addHere++)] = lists[i];
            }
        }
        return result;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame("MetNet3 test");
        f.getContentPane().add(new MetNet3ListImportPanel());
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
