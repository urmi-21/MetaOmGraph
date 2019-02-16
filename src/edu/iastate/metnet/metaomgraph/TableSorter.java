//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.iastate.metnet.metaomgraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableSorter extends AbstractTableModel {
	protected TableModel tableModel;
	public static final int DESCENDING = -1;
	public static final int NOT_SORTED = 0;
	public static final int ASCENDING = 1;
	private static TableSorter.Directive EMPTY_DIRECTIVE = new TableSorter.Directive(-1, 0);
	public static final Comparator COMPARABLE_COMAPRATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			return ((Comparable) o1).compareTo(o2);
		}
	};
	public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			//JOptionPane.showMessageDialog(null, "here");
			return o1.toString().compareTo(o2.toString());
		}
	};
	
	public static final Comparator DOUBLE_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			double diff=(double)o1-(double)o2;
			return (int) diff;
		}
	};
	private TableSorter.Row[] viewToModel;
	private int[] modelToView;
	private JTableHeader tableHeader;
	private MouseListener mouseListener;
	private TableModelListener tableModelListener;
	private Map columnComparators;
	private List sortingColumns;
	private boolean rebuilding;

	public TableSorter() {
		this.columnComparators = new HashMap();
		this.sortingColumns = new ArrayList();
		this.rebuilding = false;
		this.mouseListener = new TableSorter.MouseHandler();
		this.tableModelListener = new TableSorter.TableModelHandler();
	}

	public TableSorter(TableModel tableModel) {
		this();
		this.setTableModel(tableModel);
	}

	public TableSorter(TableModel tableModel, JTableHeader tableHeader) {
		this();
		this.setTableHeader(tableHeader);
		this.setTableModel(tableModel);
	}

	private void clearSortingState() {
		this.viewToModel = null;
		this.modelToView = null;
	}

	public TableModel getTableModel() {
		return this.tableModel;
	}

	public void setTableModel(TableModel tableModel) {
		if (this.tableModel != null) {
			this.tableModel.removeTableModelListener(this.tableModelListener);
		}

		this.tableModel = tableModel;
		if (this.tableModel != null) {
			this.tableModel.addTableModelListener(this.tableModelListener);
		}

		this.clearSortingState();
		this.fireTableStructureChanged();
	}

	public JTableHeader getTableHeader() {
		return this.tableHeader;
	}

	public void setTableHeader(JTableHeader tableHeader) {
		if (this.tableHeader != null) {
			this.tableHeader.removeMouseListener(this.mouseListener);
			TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
			if (defaultRenderer instanceof TableSorter.SortableHeaderRenderer) {
				this.tableHeader
						.setDefaultRenderer(((TableSorter.SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
			}
		}

		this.tableHeader = tableHeader;
		if (this.tableHeader != null) {
			this.tableHeader.addMouseListener(this.mouseListener);
			this.tableHeader
					.setDefaultRenderer(new TableSorter.SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
		}

	}

	public boolean isSorting() {
		return this.sortingColumns.size() != 0;
	}

	private TableSorter.Directive getDirective(int column) {
		for (int i = 0; i < this.sortingColumns.size(); ++i) {
			TableSorter.Directive directive = (TableSorter.Directive) this.sortingColumns.get(i);
			if (directive.column == column) {
				return directive;
			}
		}

		return EMPTY_DIRECTIVE;
	}

	public int getSortingStatus(int column) {
		return this.getDirective(column).direction;
	}

	private void sortingStatusChanged() {
		this.clearSortingState();
		this.fireTableDataChanged();
		if (this.tableHeader != null) {
			this.tableHeader.repaint();
		}
	}

	public void setSortingStatus(int column, int status) {
		TableSorter.Directive directive = this.getDirective(column);
		if (directive != EMPTY_DIRECTIVE) {
			this.sortingColumns.remove(directive);
		}

		if (status != 0) {
			this.sortingColumns.add(new TableSorter.Directive(column, status));
		}

		this.sortingStatusChanged();
	}

	protected Icon getHeaderRendererIcon(int column, int size) {
		TableSorter.Directive directive = this.getDirective(column);
		return directive == EMPTY_DIRECTIVE ? null
				: new TableSorter.Arrow(directive.direction == -1, size, this.sortingColumns.indexOf(directive));
	}

	private void cancelSorting() {
		this.sortingColumns.clear();
		this.sortingStatusChanged();
	}

	public void setColumnComparator(Class type, Comparator comparator) {
		if (comparator == null) {
			this.columnComparators.remove(type);
		} else {
			this.columnComparators.put(type, comparator);
		}

	}

	protected Comparator getComparator(int column) {
		Class columnType = this.tableModel.getColumnClass(column);
		Comparator comparator = (Comparator) this.columnComparators.get(columnType);
		
		if (comparator != null) {
			
			return comparator;
		}
		else {
			
			return Comparable.class.isAssignableFrom(columnType) ? COMPARABLE_COMAPRATOR : LEXICAL_COMPARATOR;
		}
	}

	private TableSorter.Row[] getViewToModel() {
		if (this.viewToModel == null) {
			this.rebuildViewToModel();
		}

		return this.viewToModel;
	}

	public synchronized void rebuildViewToModel() {
		while (this.rebuilding) {
			try {
				this.wait();
			} catch (InterruptedException ie) {
			}
		}

		this.rebuilding = true;
		int tableModelRowCount = this.tableModel.getRowCount();
		this.viewToModel = new TableSorter.Row[tableModelRowCount];

		for (int row = 0; row < tableModelRowCount; ++row) {
			this.viewToModel[row] = new TableSorter.Row(row);
		}

		if (this.viewToModel == null) {
			System.out.println("wtf1");
		}

		if (this.isSorting()) {
			Arrays.sort(this.viewToModel);
		}

		if (this.viewToModel == null) {
			System.out.println("wtf2");
		}

		this.rebuilding = false;
		this.notifyAll();
	}

	public synchronized int modelIndex(int viewIndex) {
		while (this.rebuilding) {
			try {
				this.wait();
			} catch (InterruptedException ie) {
			}
		}

		//urmi add viewIndex<0
		if (this.getViewToModel() == null ) {
			//System.out.println("viewtomodel is null");
		} else {
			if (viewIndex >= this.getViewToModel().length) {
				return viewIndex;
			}

			if (this.getViewToModel()[viewIndex] == null) {
				System.out.println("viewToModel[viewIndex] is null");
			}
		}
		
		//urmi avoid array out of bounds exception
		if(viewIndex < 0) {
			viewIndex =0;
		}

		return this.getViewToModel()[viewIndex].modelIndex;
	}

	public synchronized int[] modelIndex(int[] viewIndex) {
		while (this.rebuilding) {
			try {
				this.wait();
			} catch (InterruptedException ie) {
			}
		}

		int[] result = new int[viewIndex.length];

		for (int x = 0; x < viewIndex.length; ++x) {
			result[x] = this.modelIndex(viewIndex[x]);
		}

		return result;
	}

	private int[] getModelToView() {
		if (this.modelToView == null) {
			int n = this.getViewToModel().length;
			this.modelToView = new int[n];

			for (int i = 0; i < n; i++)
				this.modelToView[modelIndex(i)] = i;
		}

		return this.modelToView;
	}

	public int getRowCount() {
		return this.tableModel != null ? this.tableModel.getRowCount() : 0;
	}

	public int getColumnCount() {
		return this.tableModel != null ? this.tableModel.getColumnCount() : 0;
	}

	public String getColumnName(int column) {
		return this.tableModel.getColumnName(column);
	}

	public Class getColumnClass(int column) {
		Class result = null;
		// check if all values are number
		String thisHeader=getColumnName(column);
		result=MetaOmGraph.getActiveProject().getInfoColType(thisHeader);
		if(result!=null) {
			//JOptionPane.showMessageDialog(null, " sortr hdr:"+thisHeader+"v:"+result.toString());
			return result;
			
		}
		//JOptionPane.showMessageDialog(null, "return null sorter");
		return this.tableModel.getColumnClass(column);
	}

	public boolean isCellEditable(int row, int column) {
		return this.tableModel.isCellEditable(this.modelIndex(row), column);
	}

	public synchronized Object getValueAt(int row, int column) {
		return row <= this.tableModel.getRowCount() && column <= this.tableModel.getColumnCount()
				? this.tableModel.getValueAt(this.modelIndex(row), column)
				: null;
	}

	public void setValueAt(Object aValue, int row, int column) {
		this.tableModel.setValueAt(aValue, this.modelIndex(row), column);
	}

	private static class Arrow implements Icon {
		private boolean descending;
		private int size;
		private int priority;

		public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
			java.awt.Color color = c != null ? c.getBackground() : java.awt.Color.GRAY;
			int dx = (int) ((double) (size / 2) * Math.pow(0.80000000000000004D, priority));
			int dy = descending ? dx : -dx;
			y = y + (5 * size) / 6 + (descending ? -dy : 0);
			int shift = descending ? 1 : -1;
			g.translate(x, y);
			g.setColor(color.darker());
			g.drawLine(dx / 2, dy, 0, 0);
			g.drawLine(dx / 2, dy + shift, 0, shift);
			g.setColor(color.brighter());
			g.drawLine(dx / 2, dy, dx, 0);
			g.drawLine(dx / 2, dy + shift, dx, shift);
			if (descending)
				g.setColor(color.darker().darker());
			else
				g.setColor(color.brighter().brighter());
			g.drawLine(dx, 0, 0, 0);
			g.setColor(color);
			g.translate(-x, -y);
		}

		public int getIconWidth() {
			return this.size;
		}

		public int getIconHeight() {
			return this.size;
		}

		public Arrow(boolean descending, int size, int priority) {
			this.descending = descending;
			this.size = size;
			this.priority = priority;
		}
	}

	private static class Directive {
		private int column;
		private int direction;

		public Directive(int column, int direction) {
			this.column = column;
			this.direction = direction;
		}
	}

	private class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			try {
				JTableHeader h = (JTableHeader) e.getSource();
				TableColumnModel columnModel = h.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				int column = columnModel.getColumn(viewColumn).getModelIndex();
				if (column != -1) {
					int status = TableSorter.this.getSortingStatus(column);
					if (!e.isControlDown()) {
						TableSorter.this.cancelSorting();
					}

					int oldstatus = status;
					status += e.isShiftDown() ? -1 : 1;
					status = (status + 4) % 3 - 1;
					TableSorter.this.setSortingStatus(column, status);
					PropertyChangeListener[] listeners = columnModel.getColumn(viewColumn).getPropertyChangeListeners();
					PropertyChangeEvent evt = new PropertyChangeEvent(columnModel.getColumn(viewColumn), "sort",
							new Integer(oldstatus), new Integer(status));

					for (int x = 0; x < listeners.length;)
						listeners[x++].propertyChange(evt);
				}

			} catch (ArrayIndexOutOfBoundsException ae) {

			}
		}

		MouseHandler() {
		}
	}

	private class Row implements Comparable {
		private int modelIndex;

		public int compareTo(Object o) {
			int row1 = this.modelIndex;
			int row2 = ((TableSorter.Row) o).modelIndex;
			for (Iterator it = sortingColumns.iterator(); it.hasNext();) {
				Directive directive = (Directive) it.next();
				int column = directive.column;
				Object o1 = tableModel.getValueAt(row1, column);
				Object o2 = tableModel.getValueAt(row2, column);

				int comparison = 0;
				// System.out.println("Comparing "+o1+" to "+o2);
				if (o1 == null && o2 == null)
					comparison = 0;
				else if (o1 == null)
					comparison = 1;
				else if (o2 == null)
					comparison = -1;
				else
					comparison = getComparator(column).compare(o1, o2);
				
				if (comparison != 0)
					return directive.direction != -1 ? comparison : -comparison;
			}
			return 0;
		}

		public Row(int index) {
			this.modelIndex = index;
		}
	}

	private class SortableHeaderRenderer implements TableCellRenderer {
		private TableCellRenderer tableCellRenderer;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component c = this.tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
			if (c instanceof JLabel) {
				JLabel l = (JLabel) c;
				l.setHorizontalTextPosition(2);
				int modelColumn = table.convertColumnIndexToModel(column);
				l.setIcon(TableSorter.this.getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
			}

			return c;
		}

		public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
			this.tableCellRenderer = tableCellRenderer;
		}
	}

	private class TableModelHandler implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			TableSorter.this.rebuildViewToModel();
			if (!TableSorter.this.isSorting()) {
				TableSorter.this.clearSortingState();
				TableSorter.this.fireTableChanged(e);
			} else if (e.getFirstRow() == -1) {
				TableSorter.this.cancelSorting();
				TableSorter.this.fireTableChanged(e);
			} else {
				int column = e.getColumn();
				if (e.getFirstRow() == e.getLastRow() && column != -1 && TableSorter.this.getSortingStatus(column) == 0
						&& TableSorter.this.modelToView != null) {
					int viewIndex = TableSorter.this.getModelToView()[e.getFirstRow()];
					TableSorter.this.fireTableChanged(
							new TableModelEvent(TableSorter.this, viewIndex, viewIndex, column, e.getType()));
				} else {
					TableSorter.this.clearSortingState();
					TableSorter.this.fireTableDataChanged();
				}
			}
		}

		TableModelHandler() {
		}
	}
}
