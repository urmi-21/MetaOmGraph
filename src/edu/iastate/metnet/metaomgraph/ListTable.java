package edu.iastate.metnet.metaomgraph;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


public class ListTable
        extends JTable {
    public ListTable() {
    }

    public ListTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public ListTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    public ListTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public ListTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public ListTable(TableModel dm) {
        super(dm);
    }

    public ListTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }
}
