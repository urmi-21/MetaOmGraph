package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import edu.iastate.metnet.metaomgraph.FilterableTableModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.TableSorter;
import edu.iastate.metnet.metaomgraph.throbber.Throbber;
import edu.iastate.metnet.metaomgraph.utils.Utils;


/**
 * 
 * @author Harsha
 *
 * This is the abstract class that holds the methods required to populate
 * a Statistical Results Panel like DEA and Differential Correlation. This
 * class contains declarations for the table that shows all the statistical
 * results, the list panel that shows all the saved lists (common with 
 * project data lists), toolbar containing the menubar, filter search box,
 * advanced search button and create list from search button. 
 * 
 * The methods formatTable() and projectColumns(List<String> selectedCols); were
	made abstract, so that the subclasses can provide the implementations,
	because the table formatting and column projection depend on the type of
	data being populated, number of columns etc.
	
	The rest of the methods are common for all the Statistical Results.
	If the user requires some additional customization, they can override the
	concrete methods given in this class as well.
 *
 */
public abstract class StatisticalResultsPanel extends JPanel {

	protected StripedTable table;
	protected JList geneLists;
	protected JPanel listPanel;
	protected JToolBar dataToolbar;
	protected JToolBar listToolbar;
	protected JButton listDeleteButton;
	protected JButton listEditButton;
	protected JButton listCreateButton;
	protected JButton listRenameButton;
	protected MetaOmProject myProject;
	protected FilterableTableModel filterModel;
	protected ClearableTextField filterField;
	protected JButton listFromFilterButton;
	protected JButton advFilterButton;
	protected Throbber throbber;
	protected TableSorter sorter;
	protected NoneditableTableModel mainModel;

	protected Object[][] masterTableData;
	protected String[] masterTableColumns;
	protected Object[][] selectedAndProjectedTableData;
	protected String[] selectedAndProjectedTableColumns;
	protected int[] rowIndicesMapping;
	protected String currentSelectedList;
	protected List<String> selectedFeatureColumns;
	
	/**
	 * Default Properties
	 */

	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColor1();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColor2();
	private Color HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
	private Color HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();

	
	
	public JList getGeneLists() {
		return geneLists;
	}
	
	public Object[][] getMasterTableData() {
		return masterTableData;
	}

	public void setMasterTableData(Object[][] masterTableData) {
		this.masterTableData = masterTableData;
	}

	public String[] getMasterTableColumns() {
		return masterTableColumns;
	}

	public void setMasterTableColumns(String[] masterTableColumns) {
		this.masterTableColumns = masterTableColumns;
	}

	public Object[][] getSelectedAndProjectedTableData() {
		return selectedAndProjectedTableData;
	}

	public void setSelectedAndProjectedTableData(Object[][] selectedAndProjectedTableData) {
		this.selectedAndProjectedTableData = selectedAndProjectedTableData;
	}

	public String[] getSelectedAndProjectedTableColumns() {
		return selectedAndProjectedTableColumns;
	}

	public void setSelectedAndProjectedTableColumns(String[] selectedAndProjectedTableColumns) {
		this.selectedAndProjectedTableColumns = selectedAndProjectedTableColumns;
	}

	public int[] getRowIndicesMapping() {
		return rowIndicesMapping;
	}

	public void setRowIndicesMapping(int[] rowIndicesMapping) {
		this.rowIndicesMapping = rowIndicesMapping;
	}

	public String getCurrentSelectedList() {
		return currentSelectedList;
	}

	public void setCurrentSelectedList(String currentSelectedList) {
		this.currentSelectedList = currentSelectedList;
	}

	public List<String> getSelectedFeatureColumns() {
		return selectedFeatureColumns;
	}

	public void setSelectedFeatureColumns(List<String> selectedFeatureColumns) {
		this.selectedFeatureColumns = selectedFeatureColumns;
	}
	
	
	
	/**
	 * 
	 * This method initializes the table with the given background colors.
	 * It also provides a mouse action listener that gives details when
	 * double clicked or when it enters the table row.
	 */
	
	public void initTableModel() {
		table = new StripedTable() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (!isRowSelected(row)) {
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);

					if (row % 2 == 0) {
						c.setBackground(BCKGRNDCOLOR1);
					} else {
						c.setBackground(BCKGRNDCOLOR2);
					}

				} else {
					c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}

		};
		
		table.getTableHeader().addMouseListener(new StripedTableHeaderMouseListener(table));


		// end mouse listner

		// disable colum drag
		table.getTableHeader().setReorderingAllowed(false);

		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				default:
					return Double.class;
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		table.setModel(model);
	}
	
	
	
	/**
	 * This method updates the table with the latest selected and projected
	 * data available.
	 * 
	 * Selection is done when the user triggers a p-value filter, or when the
	 * table is getting initialized for the first time.
	 * 
	 * Projection happens when the user selects the Feature Metadata columns
	 * to display by choosing them from "Select Feature Metadata Cols" menu
	 */
	
	public void updateTable() {

		// add data to the model
		mainModel = new NoneditableTableModel(getSelectedAndProjectedTableData(),getSelectedAndProjectedTableColumns());
		filterModel = new FilterableTableModel(mainModel);
		sorter = new TableSorter(filterModel);

		table.setModel(sorter);

		formatTable();
	}
	
	/**
	 * 
	 * This method updates the table model with the given rows and columns
	 */
	public void updateTableRows(Object [][] rows, String[] cols) {

		mainModel = new NoneditableTableModel(rows,cols);
		filterModel = new FilterableTableModel(mainModel);
		sorter = new TableSorter(filterModel);

		table.setModel(sorter);
		

		formatTable();

	}
	
	abstract public void formatTable();
	abstract public void projectColumns(List<String> selectedCols);
	
	
	/**
	 * This method takes the listname as a parameter and calls the
	 * updateTableRows method on the rows present in that list. If Complete list
	 * is selected, then it just calls updateTable(), which displays all the
	 * available rows.
	 */
	public void selectList(String listName) {
		
		if(listName == "Complete List" || listName == null ||  listName.equalsIgnoreCase("")) {
			setCurrentSelectedList("Complete List");
			updateTable();
			listDeleteButton.setEnabled(false);
			listEditButton.setEnabled(false);
			listRenameButton.setEnabled(false);
		}
		else {
			
			listDeleteButton.setEnabled(true);
			listEditButton.setEnabled(true);
			listRenameButton.setEnabled(true);

			setCurrentSelectedList(listName);
			int[] mainTableListIndices = myProject.getGeneListRowNumbers(listName);
			
			List<Object[]> selectedRowsList = new ArrayList<Object[]>();
			for(int i=0; i < mainTableListIndices.length; i++) {
				if(rowIndicesMapping[mainTableListIndices[i]] != -1) {
					selectedRowsList.add(selectedAndProjectedTableData[rowIndicesMapping[mainTableListIndices[i]]]);
				}
			}
			
			Object[][] resultedSelectedRows = new Object[selectedRowsList.size()][selectedAndProjectedTableColumns.length];
			
			int x = 0;
			for(Object row : selectedRowsList) {
				resultedSelectedRows[x] = (Object[]) row;
				x++;
			}
			
			updateTableRows(resultedSelectedRows, selectedAndProjectedTableColumns);

		}
	}
	
	
	/**
	 * This method returns the indices of the rows selected in the table.
	 * It is useful when populating the create list's right hand side table,
	 * that gives the selected rows.
	 */
	public int[] getSelectedRowsIndices() {

		int [] currentTableSelRows = table.getSelectedRows();
		List<String> selectedGeneNames = new ArrayList<String>();
		for(int i=0; i< currentTableSelRows.length; i++) {
			selectedGeneNames.add((String)table.getValueAt(currentTableSelRows[i], 0));
		}

		return myProject.getRowIndexesFromFeatureNames(selectedGeneNames, true);
	}
	
	
	/**
	 * Makes a list from the rows that have been left after a filter has been
	 * applied. The name of the created list will be the filter used.
	 */
	public void makeListFromFilter() {
		String filterText = filterField.getText();
		int filteredTableCount = table.getModel().getRowCount();

		List<String> selectedGeneNames = new ArrayList<String>();
		for(int i=0; i< filteredTableCount; i++) {
			selectedGeneNames.add((String)table.getValueAt(i, 0));
		}

		int[] entries = myProject.getRowIndexesFromFeatureNames(selectedGeneNames, true);

		myProject.addGeneList(filterText, entries, true, false);
	}
	
	
	
	/**
	 * 
	 * @author Harsha
	 *
	 * Class that implements the methods required to have a filter mechanism
	 * on the table.
	 */
	protected class FilterFieldListener implements DocumentListener, ActionListener {
		Timer t;

		public FilterFieldListener() {
			t = new Timer(300, this);
			t.setRepeats(false);
		}

		public void doChange() {
			t.restart();
			if (!Throbber.isAnimating()) {
				throbber.start();
			}
			if (filterField.getText().trim().equals("")) {
				listFromFilterButton.setEnabled(false);
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			doChange();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			doChange();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			filterModel.applyFilter(filterField.getText().trim());
			throbber.stop();
			boolean success = filterModel.getRowCount() != 0;
			listFromFilterButton.setEnabled((success) && (!filterField.getText().trim().equals("")));
			//plotFilterItem.setEnabled((success) && (!filterField.getText().trim().equals("")));
			Utils.setSearchFieldColors(filterField, success);
		}
	}
	
}
