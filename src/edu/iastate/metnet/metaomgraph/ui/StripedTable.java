package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import edu.iastate.metnet.metaomgraph.CorrelationMeta;
import edu.iastate.metnet.metaomgraph.CorrelationMetaCollection;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class StripedTable extends JTable {
	public static final ColorUIResource alternateRowColor = new ColorUIResource(216, 236, 213);
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColor1();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColor2();
	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();

	public StripedTable() {
	}

	public StripedTable(TableModel model) {
		super(model);
		setDefaultEditor(Color.class, new ColorEditor());
		setDefaultRenderer(Color.class, new ColorRenderer(true));
	}

	// urmi show p value as tool tip
	@Override
	public String getToolTipText(MouseEvent e) {
		
		String text=null;
		java.awt.Point p = e.getPoint();
		int rowIndex = rowAtPoint(p) ;
		String rowName=getValueAt(rowIndex, MetaOmGraph.getActiveProject().getDefaultColumn()).toString();
		//get correct row index based on rowName if the table has been sorted
		String[] allRownames=MetaOmGraph.getActiveProject().getAllDefaultRowNames();
		for(int k=0;k<allRownames.length;k++) {
			if(rowName.equals(allRownames[k])) {
				rowIndex=k;
				break;
			}
		}
		
		int colIndex = columnAtPoint(p);
		String colName = getColumnName(colIndex);
		// show p value for correlation columns
		// get name for current column
		HashMap<String, CorrelationMetaCollection> corrs=MetaOmGraph.getActiveProject().getMetaCorrRes();
		
		if ( corrs== null) {
			return null;
		}
		if (corrs.containsKey(colName)) {
			try {
				CorrelationMetaCollection thisCorrColl=corrs.get(colName);
				List<CorrelationMeta> corrList=thisCorrColl.getCorrList();
				String thisPval=corrList.get(rowIndex).getpValString();
				
				text = "<html><table bgcolor=\"#FFFFFF\">" + " <tr>\n"
						+ "            <th>Attribute</th>\n" + "            <th>Value</th>\n" + "        </tr>";
				text += "<tr>";
				text += "<td><font size=-2>" +"P value" 
						+ "</font></td>";
				text += "<td><font size=-2>" +  thisPval
						+ "</font></td>";
				
				text += "</tr>";
				
				//end table
				text += "</table> </div> </body></html>";
				
				//get p value at pth row
				
			} catch (RuntimeException e1) {
				// catch null pointer exception if mouse is over an empty line
			}

			return text;
		}

		return null;
	}

	public Color colorForRow(int row) {
		return row % 2 == 0 ? BCKGRNDCOLOR1 : BCKGRNDCOLOR2;
	}

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		if ((c instanceof ColorRenderer)) {
			return c;
		}
		if (!isCellSelected(row, column)) {
			c.setBackground(colorForRow(row));
			c.setForeground(UIManager.getColor("Table.foreground"));
		} else {
			//c.setBackground(UIManager.getColor("Table.selectionBackground"));
			//c.setForeground(UIManager.getColor("Table.selectionForeground"));
			c.setBackground(SELECTIONBCKGRND);
		}
		return c;
	}

	public class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		Color currentColor;
		JButton button;
		JColorChooser colorChooser;
		JDialog dialog;
		protected static final String EDIT = "edit";

		public ColorEditor() {
			button = new JButton();
			button.setActionCommand("edit");
			button.addActionListener(this);
			button.setBorderPainted(false);
		}

		public void actionPerformed(ActionEvent e) {
			if ("edit".equals(e.getActionCommand())) {

				colorChooser = new JColorChooser();
				dialog = JColorChooser.createDialog(button, "Pick a Color", true, colorChooser, this, null);
				button.setBackground(currentColor);
				colorChooser.setColor(currentColor);
				dialog.setVisible(true);

				fireEditingStopped();
			} else {
				currentColor = colorChooser.getColor();
				dialog.dispose();
				colorChooser = null;
				dialog = null;
			}
		}

		public Object getCellEditorValue() {
			return currentColor;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			currentColor = ((Color) value);
			return button;
		}

	}

	public class myColorRenderer extends JComponent implements TableCellRenderer {
		private Color myColor;

		public myColorRenderer() {
			setOpaque(true);
		}

		protected void paintComponent(Graphics g) {
			g.setColor(myColor);
			g.fillRect(getX(), getY(), getWidth(), getHeight());
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			myColor = ((Color) value);
			return new ColorChooseButton(myColor, null);
		}
	}

	public class ColorRenderer extends JLabel implements TableCellRenderer {
		Border unselectedBorder1 = null;

		Border unselectedBorder2 = null;

		Border selectedBorder = null;

		boolean isBordered = true;

		public ColorRenderer(boolean isBordered) {
			this.isBordered = isBordered;
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Color newColor = (Color) color;
			setBackground(newColor);
			if (isBordered) {
				if (isSelected) {
					if (selectedBorder == null) {
						selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
					}
					setBorder(selectedBorder);
				} else {
					if (unselectedBorder1 == null) {
						unselectedBorder1 = BorderFactory.createMatteBorder(2, 5, 2, 5, colorForRow(0));
					}
					if (unselectedBorder2 == null) {
						unselectedBorder2 = BorderFactory.createMatteBorder(2, 5, 2, 5, colorForRow(1));
					}
					setBorder(row % 2 == 0 ? unselectedBorder1 : unselectedBorder2);
				}
			}

			setToolTipText("RGB value: " + newColor.getRed() + ", " + newColor.getGreen() + ", " + newColor.getBlue());
			return this;
		}
	}
}
