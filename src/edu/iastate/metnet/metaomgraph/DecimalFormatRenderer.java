package edu.iastate.metnet.metaomgraph;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

//class to format decimal in jTable
	public  class DecimalFormatRenderer extends DefaultTableCellRenderer {
		private static final DecimalFormat formatter = new DecimalFormat("#.0000");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			// First format the cell value as required

			value = formatter.format((Number) value);

			// And pass it on to parent class

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}