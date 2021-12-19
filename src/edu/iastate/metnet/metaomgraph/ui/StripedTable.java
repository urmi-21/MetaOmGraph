package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.StripedTableMetadata;

public class StripedTable extends JTable {
	// public static final ColorUIResource alternateRowColor = new
	// ColorUIResource(216, 236, 213);
	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
	private Color FOREGROUNDCOLOR = MetaOmGraph.getTableColorForeground();
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColorEven();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColorOdd();
	private Color HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
	private Color HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();
	private boolean USEDEFAULTCOLORS=true;
	private StripedTableMetadata metadata;

	public StripedTable() {

		this.metadata = new StripedTableMetadata();
		List<TableColumn> columnsList = Collections.list(this.getColumnModel().getColumns());

		LinkedHashMap<TableColumn,Boolean> columnVisibilityMap = new LinkedHashMap<TableColumn,Boolean>();
		for(TableColumn c: columnsList) {
			columnVisibilityMap.put(c, true);
		}

		this.metadata.setColumnVisibilityMap(columnVisibilityMap);

	}

	public StripedTable(TableModel model) {
		super(model);
		setDefaultEditor(Color.class, new ColorEditor());
		setDefaultRenderer(Color.class, new ColorRenderer(true));
		//urmi set default colors or not
		if (MetaOmGraph.getCurrentThemeName().equals("default")) {
			USEDEFAULTCOLORS = true;
		}else {
			USEDEFAULTCOLORS = false;
		}

		JTableHeader header = this.getTableHeader();
		header.addMouseListener(new StripedTableHeaderMouseListener(this));

		this.metadata = new StripedTableMetadata();
		List<TableColumn> columnsList = Collections.list(this.getColumnModel().getColumns());

		LinkedHashMap<TableColumn,Boolean> columnVisibilityMap = new LinkedHashMap<TableColumn,Boolean>();
		for(TableColumn c: columnsList) {
			columnVisibilityMap.put(c, true);
		}

		this.metadata.setColumnVisibilityMap(columnVisibilityMap);

	}
	
	public StripedTable(TableModel model, StripedTableMetadata metadata) {
		this(model);
		this.metadata = metadata;
	}



	public StripedTableMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(StripedTableMetadata metadata) {
		this.metadata = metadata;
	}

	/*

	// urmi show p value as tool tip
	@Override
	public String getToolTipText(MouseEvent e) {

		String text = null;
		java.awt.Point p = e.getPoint();
		int rowIndex = rowAtPoint(p);
		int defaultCol = convertColumnIndexToView(MetaOmGraph.getActiveProject().getDefaultColumn());
		String rowName ="";
		//JOptionPane.showMessageDialog(null, "get val at:" + rowIndex + "  col:" + defaultCol);

		// return if column or row doesn't exist
		if (rowIndex < 0 || defaultCol < 0) {
			return null;
		}
		try {
			rowName = getValueAt(rowIndex, defaultCol).toString();
		} catch (ArrayIndexOutOfBoundsException ae) {
			return null;
		}



		rowIndex = MetaOmGraph.getActiveProject().getRowIndexbyName(rowName, true);
		// JOptionPane.showMessageDialog(null, "this rowname:"+rowName+" thisrow
		// ind:"+rowIndex);

		int colIndex = columnAtPoint(p);
		String colName = getColumnName(colIndex);
		// show p value for correlation columns
		// get name for current column
		HashMap<String, CorrelationMetaCollection> corrs = MetaOmGraph.getActiveProject().getMetaCorrRes();

		if (corrs == null) {
			return null;
		}
		if (corrs.containsKey(colName)) {
			try {
				CorrelationMetaCollection thisCorrColl = corrs.get(colName);
				List<CorrelationMeta> corrList = thisCorrColl.getCorrList();
				String thisPval = corrList.get(rowIndex).getpValString();

				text = "<html><table bgcolor=\"#FFFFFF\">" + " <tr>\n" + "            <th>Attribute</th>\n"
						+ "            <th>Value</th>\n" + "        </tr>";
				text += "<tr>";
				text += "<td><font size=-2>" + "P value" + "</font></td>";
				text += "<td><font size=-2>" + thisPval + "</font></td>";

				text += "</tr>";

				// end table
				text += "</table> </div> </body></html>";

				// get p value at pth row

			} catch (RuntimeException e1) {
				// catch null pointer exception if mouse is over an empty line
			}

			return text;
		}

		return null;
	}

	 */
	public Color colorForRow(int row) {
		return row % 2 == 0 ? BCKGRNDCOLOR1 : BCKGRNDCOLOR2;
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		if ((c instanceof ColorRenderer)) {
			return c;
		}
		if (!isCellSelected(row, column)) {
			if(!USEDEFAULTCOLORS) {
				c.setBackground(colorForRow(row));
			}
			c.setForeground(FOREGROUNDCOLOR);
		} else {
			if(!USEDEFAULTCOLORS) {
				c.setBackground(SELECTIONBCKGRND);
			}
		}

		//set font
		//c.setFont(new Font("Serif", Font.LAYOUT_LEFT_TO_RIGHT, 16));

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

		@Override
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

		@Override
		public Object getCellEditorValue() {
			return currentColor;
		}

		@Override
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

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(myColor);
			g.fillRect(getX(), getY(), getWidth(), getHeight());
		}

		@Override
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

		@Override
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

	public void updateColors() {
		//urmi		
		if (MetaOmGraph.getCurrentThemeName().equals("default")) {
			USEDEFAULTCOLORS = true;
		} else {
			USEDEFAULTCOLORS = false;
			SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
			BCKGRNDCOLOR1 = MetaOmGraph.getTableColorEven();
			BCKGRNDCOLOR2 = MetaOmGraph.getTableColorOdd();
			HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
			HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();
		}

		repaint();

	}


	public void initializeVisibilityMap() {

		if(this.metadata.getColumnVisibilityMap() == null || this.metadata.getColumnVisibilityMap().size() == 0) {
			List<TableColumn> columnsList = Collections.list(this.getColumnModel().getColumns());

			LinkedHashMap<TableColumn,Boolean> columnVisibilityMap = new LinkedHashMap<TableColumn,Boolean>();
			for(TableColumn c: columnsList) {
				columnVisibilityMap.put(c, true);
			}

			this.metadata.setColumnVisibilityMap(columnVisibilityMap);

		}
	}


	public void hideColumns() {

		TableColumnModel cm = this.getColumnModel();
		while (cm.getColumnCount()!=0) {                
			TableColumn column = cm.getColumn(0);
			cm.removeColumn(column);
		}

		List<TableColumn> allColumns =  new ArrayList<TableColumn>();

		for(Entry<TableColumn, Boolean> col : metadata.getColumnVisibilityMap().entrySet()) {
			TableColumn c = col.getKey();
			allColumns.add(c);
		}

		for (int i = 0; i < allColumns.size(); i++) {
			if (metadata.getColumnVisibilityMap().get(allColumns.get(i)) == true) {
				cm.addColumn(allColumns.get(i));
			}
		}

	}
	
	
	
	public void openColumnSelectorDialog(String tableType) {
		
		
		java.util.List<String> selectedVals = new ArrayList<>();
		
		this.initializeVisibilityMap();
		
		LinkedHashMap<TableColumn,Boolean> visibleColumns = (LinkedHashMap<TableColumn,Boolean>)this.getMetadata().getColumnVisibilityMap();
		List<TableColumn> allColumns =  new ArrayList<TableColumn>();
		
		StripedTable table = this;
		
		List<String> metadataHeaders = new ArrayList<String>();
		List<Boolean> metadataSelectedStatus = new ArrayList<Boolean>();
		
		for(Entry<TableColumn, Boolean> col : visibleColumns.entrySet()) {
			TableColumn c = col.getKey();
			allColumns.add(c);
			metadataHeaders.add(c.getHeaderValue().toString());
			metadataSelectedStatus.add(col.getValue());
		}
		
		JPanel outerPanel = new JPanel(new BorderLayout());
		JLabel txt = new JLabel("Select the columns that are to be displayed", JLabel.CENTER);
		
		outerPanel.add(txt,BorderLayout.NORTH);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton selectAllButton = new JButton("Select All");
		JButton deselectAllButton = new JButton("Deselect All");
		
		buttonPanel.add(selectAllButton);
		buttonPanel.add(deselectAllButton);
		
		
		outerPanel.add(buttonPanel,BorderLayout.CENTER);
		// display jpanel with check box
		JCheckBox[] cBoxes = new JCheckBox[metadataHeaders.size() + 1];
		JPanel cbPanel = new JPanel();
		cbPanel.setLayout(new GridLayout(0, 3));
		cbPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		for (int i = 0; i < metadataHeaders.size(); i++) {
			cBoxes[i] = new JCheckBox(metadataHeaders.get(i));
			
		}
		
		TreeMap<String,Integer> sortedCheckboxesMap = new TreeMap<String,Integer>();
		
		for (int i = 0; i < metadataHeaders.size(); i++) {
			sortedCheckboxesMap.put(metadataHeaders.get(i).toLowerCase(), i);
		}
		
		for(Map.Entry<String, Integer> entry : sortedCheckboxesMap.entrySet()) {
			
			cbPanel.add(cBoxes[entry.getValue()]);
			
			if(metadataSelectedStatus.get(entry.getValue())==true) {
				cBoxes[entry.getValue()].setSelected(true);
			}
			else {
				cBoxes[entry.getValue()].setSelected(false);
			}
			
		}
		
		outerPanel.add(cbPanel,BorderLayout.SOUTH);
		
		
		selectAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (int i = 0; i < metadataHeaders.size(); i++) {
					cBoxes[i].setSelected(true);
				}
			}
		});

		
		deselectAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (int i = 0; i < metadataHeaders.size(); i++) {
					cBoxes[i].setSelected(false);
				}
			}
		});

		
		int res = JOptionPane.showConfirmDialog(null, outerPanel, "Hide/Show "+tableType+" Columns",
				JOptionPane.OK_CANCEL_OPTION);
		if (res == JOptionPane.OK_OPTION) {
			
			for (int i = 0; i < metadataHeaders.size(); i++) {
				if (cBoxes[i].isSelected()) {
					metadataSelectedStatus.add(i, true);
					visibleColumns.put(allColumns.get(i), true);
					this.getMetadata().setColumnVisibilityMap(visibleColumns);
				}
				else {
					metadataSelectedStatus.add(i, false);
					visibleColumns.put(allColumns.get(i), false);
					this.getMetadata().setColumnVisibilityMap(visibleColumns);
				}
			}
			
			this.hideColumns();
			
		} else {
			return;
		}
		
		
	
	}

}
