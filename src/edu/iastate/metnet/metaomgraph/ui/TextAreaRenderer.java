package edu.iastate.metnet.metaomgraph.ui;

import java.util.Iterator;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

public class TextAreaRenderer extends javax.swing.JTextArea implements javax.swing.table.TableCellRenderer {
    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

    protected final Map cellSizes = new java.util.HashMap();

    public TextAreaRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }


    public java.awt.Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        adaptee.getTableCellRendererComponent(table, obj,
                isSelected, hasFocus, row, column);
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());


        javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
        setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) getPreferredSize().getHeight();
        addSize(table, row, column, height_wanted);
        height_wanted = findTotalMaximumRowSize(table, row);
        if (height_wanted < 1) {
            height_wanted = 16;
        }
        if (height_wanted > table.getRowHeight(row)) {
            table.setRowHeight(row, height_wanted);
        }
        return this;
    }

    protected void addSize(JTable table, int row, int column, int height) {
        Map rows = (Map) cellSizes.get(table);
        if (rows == null) {
            cellSizes.put(table, rows = new java.util.HashMap());
        }
        Map rowheights = (Map) rows.get(new Integer(row));
        if (rowheights == null) {
            rows.put(new Integer(row), rowheights = new java.util.HashMap());
        }
        rowheights.put(new Integer(column), new Integer(height));
    }


    private int findTotalMaximumRowSize(JTable table, int row) {
        int maximum_height = 0;
        java.util.Enumeration columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = (TableColumn) columns.nextElement();
            javax.swing.table.TableCellRenderer cellRenderer = tc.getCellRenderer();
            if ((cellRenderer instanceof TextAreaRenderer)) {
                TextAreaRenderer tar = (TextAreaRenderer) cellRenderer;
                maximum_height = Math.max(maximum_height,
                        tar.findMaximumRowSize(table, row));
            }
        }
        return maximum_height;
    }

    private int findMaximumRowSize(JTable table, int row) {
        Map rows = (Map) cellSizes.get(table);
        if (rows == null) return 0;
        Map rowheights = (Map) rows.get(new Integer(row));
        if (rowheights == null) return 0;
        int maximum_height = 0;
        for (Iterator it = rowheights.entrySet().iterator();
             it.hasNext(); ) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            int cellHeight = ((Integer) entry.getValue()).intValue();
            maximum_height = Math.max(maximum_height, cellHeight);
        }
        return maximum_height;
    }
}
