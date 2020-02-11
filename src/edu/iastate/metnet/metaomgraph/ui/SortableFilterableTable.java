package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.FilterableTableModel;
import edu.iastate.metnet.metaomgraph.TableSorter;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


public class SortableFilterableTable
        extends JTable {
    public static final ColorUIResource alternateRowColor = new ColorUIResource(
            216, 236, 213);

    private FilterableTableModel filterModel;

    private TableSorter sorter;

    private TableModel mainModel;

    private boolean striped;

    public SortableFilterableTable(TableModel model) {
        mainModel = model;
        filterModel = new FilterableTableModel(mainModel);
        sorter = new TableSorter(filterModel);
        super.setModel(sorter);
        sorter.setTableHeader(getTableHeader());
        //urmi changed
        //sorter.setColumnComparator(String.class, new NumberStringComparator());
        striped = true;
    }

    public int getTrueRow(int row) {
        return filterModel.getUnfilteredRow(sorter.modelIndex(row));
    }


    public int[] getAllTrueRows() {
        int[] allRows = new int[filterModel.getRowCount()];
        for (int x = 0; x < allRows.length; allRows[x] = (x++)) {
        }

        return filterModel.getUnfilteredRows(allRows);
    }

    public int getTrueSelectedRow() {
        return filterModel.getUnfilteredRow(sorter.modelIndex(getSelectedRow()));
    }


    public int[] getTrueSelectedRows() {
        int[] result = new int[getSelectedRowCount()];
        int[] falseRows = getSelectedRows();
        for (int i = 0; i < result.length; i++) {
            result[i] = filterModel.getUnfilteredRow(sorter
                    .modelIndex(falseRows[i]));
        }

        return result;
    }

    public TableSorter getSorter() {
        return sorter;
    }

    public FilterableTableModel getFilterModel() {
        return filterModel;
    }


    @Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if ((c instanceof StripedTable.ColorRenderer)) {
            return c;
        }
        if (!isCellSelected(row, column)) {
            c.setBackground(colorForRow(row));
            c.setForeground(UIManager.getColor("Table.foreground"));
        } else {
            c.setBackground(UIManager.getColor("Table.selectionBackground"));
            c.setForeground(UIManager.getColor("Table.selectionForeground"));
        }
        return c;
    }

    protected Color colorForRow(int row) {
        if (striped) {
            return row % 2 == 0 ? alternateRowColor : getBackground();
        }
        return getBackground();
    }

    public void setStriped(boolean striped) {
        this.striped = striped;
    }

    @Override
	public void setModel(TableModel dataModel) {
        mainModel = dataModel;
        filterModel = new FilterableTableModel(mainModel);
        sorter = new TableSorter(filterModel);
        super.setModel(sorter);
        sorter.setTableHeader(getTableHeader());
    }

    public static void main(String[] args) {
    }
}
