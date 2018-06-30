package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.TableSorter;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class DualTablePanel extends JPanel implements ActionListener {
	public static final int MAKE_ALL_INACTIVE = 0;
	public static final int REVERT_TO_ORIGINAL = 1;
	public static final String ADD_COMMAND = "add";
	public static final String REMOVE_COMMAND = "remove";
	public static final String RESET_COMMAND = "reset";
	public static final String ADD_ALL_COMMAND = "add all";
	public static final String ADD_EVENT = "add event";
	public static final String REMOVE_EVENT = "remove event";
	public static final String RESET_EVENT = "reset event";
	private Object[][] originalActiveData;
	private Object[][] originalInactiveData;
	private SortableFilterableTable inactiveTable;
	private SortableFilterableTable activeTable;
	
	
	private JButton addAllButton;
	private JButton addButton;
	private JButton removeButton;
	private JButton resetButton;
	private NoneditableTableModel activeModel;
	private NoneditableTableModel inactiveModel;
	private String[] headers;
	private Vector<ChangeListener> changeListeners;
	private JScrollPane activePane;
	private JScrollPane inactivePane;
	private JPanel inactivePanel;
	private JPanel activePanel;
	private JPanel buttonPanel;
	private JComponent inactiveDecoration;
	private JComponent activeDecoration;
	private int resetBehavior;
	private Hashtable<Integer, Integer> colWidths;
	private int defaultSortColumn;

	// urmi
	private JButton searchActive;
	private JButton searchInactive;

	public DualTablePanel(Object[][] inactiveData, String[] headers) {
		this(inactiveData, null, headers);
	}

	public DualTablePanel(Object[][] inactiveData, Object[][] activeData, String[] headers) {
		originalInactiveData = inactiveData;
		originalActiveData = activeData;
		this.headers = headers;
		resetBehavior = 1;
		defaultSortColumn = -1;
		inactiveModel = new NoneditableTableModel(originalInactiveData, headers);
		inactiveTable = new SortableFilterableTable(inactiveModel);
		searchActive = new JButton("Search");
		searchActive.setBorder(new LineBorder(Color.BLACK));
		searchInactive = new JButton("Search");
		searchInactive.setBorder(new LineBorder(Color.BLACK));
		activeModel = new NoneditableTableModel(originalActiveData, headers);
		activeTable = new SortableFilterableTable(activeModel);
		//disable sort by click
		//activeTable.getTableHeader().setEnabled(false);
		//inactiveTable.getTableHeader().setEnabled(false);
		
		
		
		inactivePanel = new JPanel(new BorderLayout());
		activePanel = new JPanel(new BorderLayout());

		activePane = new JScrollPane(activeTable);
		inactivePane = new JScrollPane(inactiveTable);

		searchInactive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//JOptionPane.showMessageDialog(null, "Searching");
				new AnimatedSwingWorker("Searching...", true) {

					@Override
					public Object construct() {
						searchInactiveTab();
						return null;
					}

				}.start();
				//searchInactiveTab();
				return;
				
			}
		});
		inactivePanel.add(searchInactive, "North");

		inactivePanel.add(inactivePane, "Center");
		Border etchedBorder = BorderFactory.createEtchedBorder();
		inactivePanel.setBorder(
				BorderFactory.createCompoundBorder(etchedBorder, BorderFactory.createTitledBorder("Inactive")));

		searchActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//JOptionPane.showMessageDialog(null, "Searching");
				new AnimatedSwingWorker("Searching...", true) {

					@Override
					public Object construct() {
						searchActiveTab();
						return null;
					}

				}.start();
				//searchActiveTab();
				return;
			}
		});
		activePanel.add(searchActive, "North");
		activePanel.add(activePane, "Center");
		activePanel.setBorder(
				BorderFactory.createCompoundBorder(etchedBorder, BorderFactory.createTitledBorder("Active")));
		addAllButton = new JButton("Add all >>>");
		addAllButton.setActionCommand("add all");
		addAllButton.addActionListener(this);
		addButton = new JButton("Add >>");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		removeButton = new JButton("<< Remove");
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(this);
		resetButton = new JButton("Reset");
		resetButton.setActionCommand("reset");
		resetButton.addActionListener(this);
		
		Dimension maxSize = addButton.getMaximumSize();
		maxSize.width = 10000;
		addButton.setMaximumSize(maxSize);
		removeButton.setMaximumSize(maxSize);
		resetButton.setMaximumSize(maxSize);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, 1));
		//buttonPanel.add(addAllButton);
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		//buttonPanel.add(resetButton);
		setLayout(new BoxLayout(this, 0));
		add(inactivePanel);
		add(buttonPanel);
		add(activePanel);

		inactiveTable.setAutoResizeMode(0);
		activeTable.setAutoResizeMode(0);

	}

	private void searchInactiveTab() {
		// TODO Auto-generated method stub
		final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
				MetaOmGraph.getActiveProject(), true);
		
		final MetadataQuery[] queries;
		queries = tsp.showSearchDialog();
		if (tsp.getQueryCount() <= 0) {
			// System.out.println("Search dialog cancelled");
			// User didn't enter any queries
			return;
		}
		java.util.List<Integer> matchingRows = new ArrayList<>();
		
		for (int i = 0; i < queries.length; i++) {
			//JOptionPane.showMessageDialog(null, "F:" + queries[i].getField() + " T:" + queries[i].getTerm());
			matchingRows.addAll(
					getMatchingRow(queries[i].getField(), queries[i].getTerm(), inactiveTable, queries[i].isExact()));
		}

		// remove duplicates
		Set<Integer> hs = new HashSet<>();
		hs.addAll(matchingRows);
		matchingRows.clear();
		matchingRows.addAll(hs);

		//JOptionPane.showMessageDialog(null, "matc:" + matchingRows.toString());
		setSelectedRows(inactiveTable, matchingRows);

	}

	private void setSelectedRows(JTable tab, java.util.List<Integer> rows) {
		ListSelectionModel model = tab.getSelectionModel();
		model.clearSelection();
		for (int i = 0; i < tab.getRowCount(); i++) {
			if (rows.contains(i)) {
				model.addSelectionInterval(i, i);
			}
		}
		// DefaultTableModel model = (DefaultTableModel)tab.getModel();

		/*
		 * for (int i = 0; i < tab.getRowCount(); i++) { if(rows.contains(i)) { String
		 * currVal=tab.getModel().getValueAt(i, 0).toString();
		 * tab.getModel().setValueAt(-999,i, 0); } }
		 */

		// sort the table by 0th col
		/*
		 * TableRowSorter<TableModel> sorter = new
		 * TableRowSorter<TableModel>(tab.getModel()); tab.setRowSorter(sorter);
		 * java.util.List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		 * sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		 * sorter.setSortKeys(sortKeys); tab.setRowSelectionInterval(0, rows.size()-1);
		 */
	}

	private java.util.List<Integer> getMatchingRow(String colName, String val, JTable tab, boolean exact) {
		java.util.List<Integer> res = new ArrayList<>();

		// special case
		if (colName.equals("All Fields")) {
			//JOptionPane.showMessageDialog(null, "case all");
			boolean breakFlag = false;
			for (int i = 0; i < tab.getRowCount(); i++) {
				breakFlag = false;
				for (int j = 1; j < tab.getColumnCount(); j++) {
					if (exact) {
						if (!tab.getModel().getValueAt(i, j).equals(val)) {
							breakFlag = true;
							break;
						}
					} else {
						if (!tab.getModel().getValueAt(i, j).toString().contains(val)) {
							breakFlag = true;
							break;
						}
					}
				}
				if (!breakFlag) {
					res.add(i);
				}
			}
		} else if (colName.equals("Any Field")) {
			//JOptionPane.showMessageDialog(null, "case any");
			boolean foundFlag = false;
			for (int i = 0; i < tab.getRowCount(); i++) {
				foundFlag = false;
				for (int j = 1; j < tab.getColumnCount(); j++) {// For each column in that row
					if (exact) {
						if (tab.getModel().getValueAt(i, j).equals(val)) {
							foundFlag = true;
							break;
						}
					} else {
						if (tab.getModel().getValueAt(i, j).toString().contains(val)) {
							//JOptionPane.showMessageDialog(null, "col" + j + ":" + tab.getColumnName(j) + " row:" + i);
							//JOptionPane.showMessageDialog(null, "mval:" + tab.getModel().getValueAt(i, j).toString());
							foundFlag = true;
							break;
						}
					}
				}

				if (foundFlag) {
					res.add(i);
				}
			}
		} else {
			int colIndex = tab.getColumn(colName).getModelIndex();
			for (int i = 0; i < tab.getRowCount(); i++) {
				//JOptionPane.showMessageDialog(null, "valn:" + tab.getModel().getValueAt(i, colIndex));
				if (exact) {
					if (tab.getModel().getValueAt(i, colIndex).equals(val)) {
						res.add(i);
					}
				} else {
					if (tab.getModel().getValueAt(i, colIndex).toString().contains(val)) {
						res.add(i);
					}
				}
			}

		}
		return res;
	}

	private void searchActiveTab() {
		final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
				MetaOmGraph.getActiveProject(), true);
		final MetadataQuery[] queries;
		queries = tsp.showSearchDialog();
		if (tsp.getQueryCount() <= 0) {
			// System.out.println("Search dialog cancelled");
			// User didn't enter any queries
			return;
		}
		
		
		java.util.List<Integer> matchingRows = new ArrayList<>();
		for (int i = 0; i < queries.length; i++) {
			//JOptionPane.showMessageDialog(null, "F:" + queries[i].getField() + " T:" + queries[i].getTerm());
			matchingRows.addAll(
					getMatchingRow(queries[i].getField(), queries[i].getTerm(), activeTable, queries[i].isExact()));
		}

		// remove duplicates
		Set<Integer> hs = new HashSet<>();
		hs.addAll(matchingRows);
		matchingRows.clear();
		matchingRows.addAll(hs);

		//JOptionPane.showMessageDialog(null, "matc:" + matchingRows.toString());
		setSelectedRows(activeTable, matchingRows);

	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	public Object[][] getActiveValues() {
		Object[][] result = new Object[activeModel.getRowCount()][activeModel.getColumnCount()];

		for (int x = 0; x < result.length; x++)
			result[x] = activeModel.getData()[x];
		return result;
	}

	public Object[][] getInactiveValues() {
		Object[][] result = new Object[inactiveModel.getRowCount()][inactiveModel.getColumnCount()];

		for (int x = 0; x < result.length; x++)
			result[x] = inactiveModel.getData()[x];
		return result;
	}

	public void hideColumn(int col) {
		if (col > inactiveModel.getColumnCount())
			throw new InvalidParameterException("There is no column " + col);
		TableColumnModel colModel = inactiveTable.getColumnModel();
		colModel.removeColumn(colModel.getColumn(col));
		colModel = activeTable.getColumnModel();
		colModel.removeColumn(colModel.getColumn(col));
	}

	public boolean hasActiveValues() {
		return activeModel.getRowCount() > 0;
	}

	public void setActiveLabel(String label) {
		activePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createTitledBorder(label)));
	}

	public void setInactiveLabel(String label) {
		inactivePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createTitledBorder(label)));
	}

	public void addChangeListener(ChangeListener addMe) {
		if (changeListeners == null)
			changeListeners = new Vector();
		changeListeners.add(addMe);
	}

	private void fireChange(ChangeEvent event) {
		if (changeListeners == null)
			return;
		Iterator iter = changeListeners.iterator();
		while (iter.hasNext()) {
			((ChangeListener) iter.next()).stateChanged(event);
		}
	}

	public void makeActive(int[] moveUs) {
		if ((moveUs == null) || (moveUs.length <= 0))
			return;
		activeModel.appendRows(inactiveModel.deleteRows(moveUs));
		activePane.setViewportView(activeTable);
		inactivePane.setViewportView(inactiveTable);
		inactiveTable.clearSelection();

		fireChange(new ChangeEvent("add event"));
	}

	public void makeInactive(int[] moveUs) {
		if ((moveUs == null) || (moveUs.length <= 0))
			return;
		inactiveModel.appendRows(activeModel.deleteRows(moveUs));
		activePane.setViewportView(activeTable);
		inactivePane.setViewportView(inactiveTable);
		fireChange(new ChangeEvent("remove event"));
	}

	public void setValues(Object[][] inactiveData, Object[][] activeData, String[] headers) {
		originalInactiveData = inactiveData;
		originalActiveData = activeData;
		this.headers = headers;
		inactiveModel.setData(originalInactiveData);
		activeModel.setData(originalActiveData);

		if (defaultSortColumn >= 0) {
			activeTable.getSorter().setSortingStatus(defaultSortColumn, 1);
			inactiveTable.getSorter().setSortingStatus(defaultSortColumn, 1);
		}
		if (colWidths != null) {
			Set<Integer> colSet = colWidths.keySet();
			for (Integer thisCol : colSet) {
				getActiveTable().getColumnModel().getColumn(thisCol.intValue())
						.setMaxWidth(colWidths.get(thisCol).intValue());
				getInactiveTable().getColumnModel().getColumn(thisCol.intValue())
						.setMaxWidth(colWidths.get(thisCol).intValue());
			}
		}
	}

	private void doAdd() {
		if (inactiveTable.getSelectedRowCount() <= 0)
			return;
		makeActive(inactiveTable.getTrueSelectedRows());
	}

	private void doRemove() {
		if (activeTable.getSelectedRowCount() <= 0)
			return;
		makeInactive(activeTable.getTrueSelectedRows());
	}

	private void doReset() {
		if (resetBehavior == 1) {
			activeModel.setData(originalActiveData);
			inactiveModel.setData(originalInactiveData);
			activeModel = new NoneditableTableModel(originalActiveData, headers);
			inactiveModel = new NoneditableTableModel(originalInactiveData, headers);
			activeTable.setModel(activeModel);
			inactiveTable.setModel(inactiveModel);
			if (defaultSortColumn >= 0) {
				activeTable.getSorter().setSortingStatus(defaultSortColumn, 1);
				inactiveTable.getSorter().setSortingStatus(defaultSortColumn, 1);
			}
			if (colWidths != null) {
				Set<Integer> colSet = colWidths.keySet();
				for (Integer thisCol : colSet) {
					getActiveTable().getColumnModel().getColumn(thisCol.intValue())
							.setMaxWidth(colWidths.get(thisCol).intValue());
					getInactiveTable().getColumnModel().getColumn(thisCol.intValue())
							.setMaxWidth(colWidths.get(thisCol).intValue());
				}
			}
			fireChange(new ChangeEvent("reset event"));
		} else {
			int[] removeUs = new int[activeTable.getRowCount()];
			for (int i = 0; i < removeUs.length; i++) {
				removeUs[i] = i;
			}
			makeInactive(removeUs);
			fireChange(new ChangeEvent("reset event"));
		}
	}

	private void doAddAll() {
		int[] addUs = new int[inactiveTable.getRowCount()];
		for (int i = 0; i < addUs.length; addUs[i] = (i++)) {
		}
		makeActive(addUs);
		fireChange(new ChangeEvent("add event"));
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals("add")) {
			doAdd();
			return;
		}
		if (arg0.getActionCommand().equals("remove")) {
			doRemove();
			return;
		}
		if (arg0.getActionCommand().equals("reset")) {
			doReset();
			return;
		}
		if (arg0.getActionCommand().equals("add all")) {
			doAddAll();
			return;
		}
	}

	public void setInactiveDecoration(JComponent decoration) {
		if (inactiveDecoration != null) {
			inactivePanel.remove(inactiveDecoration);
		}
		inactiveDecoration = decoration;
		inactivePanel.add(inactiveDecoration, "North");
	}

	public void setActiveDecoration(JComponent decoration) {
		if (activeDecoration != null) {
			activePanel.remove(activeDecoration);
		}
		activeDecoration = decoration;
		activePanel.add(activeDecoration, "North");
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			String antialising = "swing.aatext";
			System.setProperty(antialising, "true");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JFrame f = new JFrame("Dual Table Panel test");
		String[][] data = new String[10][3];
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				if (y == 0) {
					data[x][y] = x + "";
				} else
					data[x][y] = ((int) (Math.random() * 1000.0D)) + "";
			}
		}
		String[] headers = { "original row", "header 1", "header 2" };
		final DualTablePanel myPanel = new DualTablePanel(data, headers);
		myPanel.hideColumn(0);
		f.getContentPane().add(myPanel, "Center");
		f.setDefaultCloseOperation(3);
		JButton button = new JButton("Report values");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				Object[][] activeValues = myPanel.getActiveValues();
				Object[][] inactiveValues = myPanel.getInactiveValues();
				System.out.println("Active values:");
				for (int x = 0; x < activeValues.length; x++) {
					for (int y = 0; y < activeValues[x].length; y++) {
						System.out.print(activeValues[x][y] + " ");
					}
					System.out.println();
				}
				System.out.println("Inactive values:");
				for (int x = 0; x < inactiveValues.length; x++) {
					for (int y = 0; y < inactiveValues[x].length; y++) {
						System.out.print(inactiveValues[x][y] + " ");
					}
					System.out.println();
				}
			}

		});
		f.getContentPane().add(button, "Last");
		f.pack();
		f.setVisible(true);
	}

	public NoneditableTableModel getActiveModel() {
		return activeModel;
	}

	public SortableFilterableTable getActiveTable() {
		return activeTable;
	}

	public NoneditableTableModel getInactiveModel() {
		return inactiveModel;
	}

	public SortableFilterableTable getInactiveTable() {
		return inactiveTable;
	}

	public void setAddAllButtonText(String newText) {
		addAllButton.setText(newText + " >>>");
	}

	public void setAddButtonText(String newText) {
		addButton.setText(newText + " >>");
	}

	public void setRemoveButtonText(String newText) {
		removeButton.setText("<< " + newText);
	}

	public void setResetButtonText(String newText) {
		resetButton.setText(newText);
	}

	public void setResetButtonBehavior(int behavior) {
		if ((behavior != 0) && (behavior != 1)) {
			throw new IllegalArgumentException(
					"behavior must be either DualTablePanel.MAKE_ALL_INACTIVE or DualTablePanel.REVERT_TO_ORIGINAL");
		}
		resetBehavior = behavior;
	}

	public void setDefaultSortColumn(int column) {
		defaultSortColumn = column;
		if (defaultSortColumn >= 0) {
			inactiveTable.getSorter().setSortingStatus(defaultSortColumn, 1);
			activeTable.getSorter().setSortingStatus(defaultSortColumn, 1);
		}
	}

	public void setMaxColumnWidth(int column, int width) {
		if (colWidths == null) {
			colWidths = new Hashtable();
		}
		colWidths.put(Integer.valueOf(column), Integer.valueOf(width));
		getActiveTable().getColumnModel().getColumn(column).setMaxWidth(width);
		getInactiveTable().getColumnModel().getColumn(column).setMaxWidth(width);
	}
}
