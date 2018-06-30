package edu.iastate.metnet.arrayexpress;

import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.TextAreaRenderer;

import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class TableUpdater implements TreeSelectionListener {
    private TableModel model;
    private JTable table;

    public TableUpdater(JTable table, TableModel model) {
        this.table = table;
        this.model = model;
    }

    public void valueChanged(TreeSelectionEvent e) {
        Object o = e.getPath().getPath()[(e.getPath().getPathCount() - 1)];
        if (!(o instanceof DefaultMutableTreeNode)) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
        Object newSel = node.getUserObject();
        if ((newSel instanceof TableDataNode)) {
            TableDataNode selData = (TableDataNode) newSel;
            model = new NoneditableTableModel(selData.getTableData(), selData.getTableHeaders());
            table.setModel(model);
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(new TextAreaRenderer());
            }


        } else {

            model = new NoneditableTableModel(new Object[][]{{"", newSel}}, new String[]{
                    "Attribute", "Value"});
            table.setModel(model);
        }
    }
}
