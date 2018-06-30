package edu.iastate.metnet.metaomgraph.ui;

import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Hashtable;
import javax.swing.table.AbstractTableModel;


public class NoneditableTableModel extends AbstractTableModel {
    String[] columnNames;
    Object[][] data;
    Hashtable<Integer, Boolean> editableColumns;

    public NoneditableTableModel(Object[][] data, String[] columnNames) {
        this.columnNames = columnNames;
        this.data = data;
        editableColumns = null;
    }

    public int getColumnCount() {
        if (columnNames == null) return 0;
        return columnNames.length;
    }

    public int getRowCount() {
        if (data == null) return 0;
        return data.length;
    }

    public String getColumnName(int col) {
        if (columnNames == null) return null;
        return columnNames[col];
    }

    public void setColumnName(int col, String newName) {
        if (col >= getColumnCount()) return;
        columnNames[col] = newName;
    }

    public Object getValueAt(int row, int col) {
        if ((data == null) || (row >= getRowCount()) || (col >= getColumnCount())) return null;
        if (data[row] == null) return null;
        if (data[row].length <= col) return null;
        return data[row][col];
    }


    public Class<?> getColumnClass(int c) {
        Class result = null;
        int index = 0;
        while ((result == null) && (index < getRowCount())) {
            if ((getValueAt(index, c) != null) && (!"".equals(getValueAt(index, c)))) {
                result = getValueAt(index, c).getClass();
            }
            index++;
        }
        if (result != null) return result;
        return String.class;
    }

    public boolean isCellEditable(int row, int col) {
        if (editableColumns == null) return false;
        if (editableColumns.get(new Integer(col)) == null) return false;
        return editableColumns.get(new Integer(col)).booleanValue();
    }


    public void setColumnEditable(int col, boolean isEditable) {
        if (editableColumns == null) editableColumns = new Hashtable();
        editableColumns.put(new Integer(col), new Boolean(isEditable));
    }

    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

    public void appendColumn(Object[] newData, String newHeader) {
        if (data == null) {
            columnNames = new String[]{newHeader};
            data = new Object[newData.length][1];
            for (int x = 0; x < newData.length; x++)  data[x][0] = newData[x];
            return;
        }

        String[] newColumnNames = new String[columnNames.length + 1];
        Object[][] newValues = new Object[data.length > newData.length ? data.length : newData.length][newColumnNames.length];
        for (int x = 0; x < columnNames.length; x++) newColumnNames[x] = columnNames[x];
        newColumnNames[(newColumnNames.length - 1)] = newHeader;
        for (int x = 0; x < newValues.length; x++) {
            for (int y = 0; y < getColumnCount(); y++)
                if (x < data.length) {
                    newValues[x][y] = data[x][y];
                } else newValues[x][y] = "";
            if (x < newData.length) {
                newValues[x][getColumnCount()] = newData[x];
            } else {
                newValues[x][getColumnCount()] = "";
            }
        }
        columnNames = newColumnNames;
        data = newValues;
        fireTableDataChanged();
    }

    public void appendRow(Object[] newRow) {
        if (newRow.length != getColumnCount()) return;
        Object[][] newData = new Object[getRowCount() + 1][newRow.length];
        for (int x = 0; x < getRowCount(); x++)
            for (int y = 0; y < getColumnCount(); y++) newData[x][y] = data[x][y];
        newData[(newData.length - 1)] = newRow;
        data = newData;

        fireTableRowsInserted(data.length - 1, data.length - 1);
    }

    public boolean hasData() {
        return data != null;
    }

    public void deleteColumn(int c) {
        if ((data == null) || (c >= getColumnCount())) return;
        if (getColumnCount() == 1) {
            data = null;
            columnNames = null;
            return;
        }
        String[] newColumnNames = new String[getColumnCount() - 1];
        Object[][] newData = new Object[data.length][newColumnNames.length];
        int index = 0;
        for (int x = 0; x < getColumnCount(); x++) {
            if (x != c) {
                newColumnNames[index] = columnNames[x];
                index++;
            }
        }
        for (int x = 0; x < getRowCount(); x++) {
            index = 0;
            for (int y = 0; y < getColumnCount(); y++)
                if (y != c) {
                    newData[x][index] = data[x][y];
                    index++;
                }
        }
        data = newData;
        columnNames = newColumnNames;
    }

    public Object[] deleteRow(int r) {
        if ((data == null) || (r >= getRowCount())) return null;

        Object[] deleted = data[r];
        Object[][] newData = new Object[getRowCount() - 1][getColumnCount()];
        int index = 0;
        for (int x = 0; x < getRowCount(); x++)
            if (x != r) {
                newData[index] = data[x];
                index++;
            }
        data = newData;
        return deleted;
    }

    public Object[][] deleteRows(int[] rows) {
        if ((rows == null) || (rows.length <= 0)) {
            return null;
        }
        Object[][] deleted = new Object[rows.length][getColumnCount()];
        for (int x = 0; x < rows.length; x++) {
            if (rows[x] > getRowCount()) return null;

            deleted[x] = data[rows[x]];
        }
        if (rows.length == getRowCount()) {
            data = null;
        } else {
            int[] sortedRows = new int[rows.length];
            for (int x = 0; x < rows.length; x++) sortedRows[x] = rows[x];
            Arrays.sort(sortedRows);
            int deleteMe = 0;
            Object[][] newData = new Object[getRowCount() - rows.length][getColumnCount()];
            int addHere = 0;
            for (int x = 0; x < getRowCount(); x++) {
                if ((deleteMe < sortedRows.length) && (x == sortedRows[deleteMe])) {
                    deleteMe++;
                } else {
                    newData[addHere] = data[x];
                    addHere++;
                }
            }
            data = newData;
        }
        fireTableDataChanged();
        return deleted;
    }

    public void appendRows(Object[][] newRows) {
        if ((newRows == null) || (newRows.length <= 0)) {
            if (newRows != null) {
                System.err.println("newRows.length=" + newRows.length);
            } else {
                System.err.println("newRows is null");
            }
            throw new InvalidParameterException("newRows must not be null, and must have length>0");
        }
        Object[][] newData = new Object[getRowCount() + newRows.length][getColumnCount()];
        for (int x = 0; x < getRowCount(); x++) {
            for (int y = 0; y < getColumnCount(); y++) {
                newData[x][y] = data[x][y];
            }
        }
        for (int x = getRowCount(); x < newData.length; x++) {
            for (int y = 0; y < getColumnCount(); y++) {
                if (y >= newRows[(x - getRowCount())].length) {
                    newData[x][y] = "";
                } else {
                    newData[x][y] = newRows[(x - getRowCount())][y];
                }
            }
        }
        data = newData;
        fireTableDataChanged();
    }

    public Object[][] getData() {
        return data;
    }

    public String[] getHeaders() {
        return columnNames;
    }

    public void insertRowsBefore(Object[][] newData, int destRow) {
        if ((data == null) || (data.length <= 0)) {
            appendRows(newData);
            return;
        }
        if ((newData == null) || (data.length < 0)) return;
        if (newData[0].length != getColumnCount()) return;
        System.out.println("destRow: " + destRow);
        System.out.println("Column count: " + getColumnCount());
        Object[][] preData = new Object[destRow][getColumnCount()];
        Object[][] postData = new Object[getRowCount() - destRow][getColumnCount()];
        for (int x = 0; x < destRow; x++) {
            preData[x] = data[x];
        }
        for (int x = destRow; x < getRowCount(); x++) {
            postData[(x - destRow)] = data[x];
        }
        Object[][] assembledData = new Object[getRowCount() + newData.length][getColumnCount()];
        for (int x = 0; x < preData.length; x++) {
            assembledData[x] = preData[x];
        }
        for (int x = 0; x < newData.length; x++) {
            assembledData[(x + preData.length)] = newData[x];
        }
        for (int x = 0; x < postData.length; x++) {
            assembledData[(x + preData.length + newData.length)] = postData[x];
        }
        data = assembledData;
        fireTableRowsInserted(destRow, destRow + newData.length);
    }

    public void insertRowAt(Object[] newData, int destRow) {
        Object[][] myData = new Object[1][newData.length];
        myData[0] = newData;
        insertRowsBefore(myData, destRow);
    }

    public void setData(Object[][] newData) {
        if ((newData == null) || (newData.length <= 0)) {
            data = null;
            fireTableDataChanged();
            return;
        }
        if (newData[0].length != getColumnCount()) {
            throw new InvalidParameterException("New data must have the same number of columns!");
        }
        data = newData;
        fireTableDataChanged();
    }
}
