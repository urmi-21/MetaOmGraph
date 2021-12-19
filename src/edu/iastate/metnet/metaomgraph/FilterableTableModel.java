package edu.iastate.metnet.metaomgraph;

import com.sun.source.tree.Tree;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class FilterableTableModel extends AbstractTableModel implements DocumentListener, TableModelListener {

	private TableModel model;
	private Object[][] filteredData;
	private TreeMap<Integer, Integer> rowMap;
	private boolean available;
	private String filterText;
	private boolean manualFilter;
	private boolean caseSensitive;

	public FilterableTableModel(TableModel model) {
		this.model = model;
		filteredData = new Object[model.getRowCount()][model.getColumnCount()];
		for (int x = 0; x < filteredData.length; x++)
			for (int y = 0; y < filteredData[x].length; y++)
				filteredData[x][y] = model.getValueAt(x, y);
		available = true;
		this.model.addTableModelListener(this);
		caseSensitive = false;
	}

	public void applyFilter(String newFilter) {
		if ((newFilter == null) || (newFilter.equals(""))) {
			filterText = "";
			clearFilter();
		} else {
			boolean complex = false;
			filterText = newFilter;
			String[] filters = null;
			filters = newFilter.split(";|\\^");
			// JOptionPane.showMessageDialog(null, Arrays.toString(filters));
			for (int i = 0; i < filters.length; i++) {
				filters[i] = filters[i].trim();
				if (filters[i].contains("AND") || filters[i].contains("OR")) {
					complex = true;
				}
			}
			if (complex) {
				applyComplexFilter(filters);
			} else {
				applyFilter(filters);
			}
		}
	}

	// look at this for adding buttons
	public void applyFilterold(String[] values) {
		TreeSet<Integer> hits = new TreeSet();

		for (int row = 0; row < model.getRowCount(); row++) {
			for (int col = 0; col < model.getColumnCount(); col++) {
				String thisValue = model.getValueAt(row, col) + "";
				if (!caseSensitive) {
					thisValue = thisValue.toLowerCase();
				}

				for (String findMe : values) {
					if (!caseSensitive) {
						findMe = findMe.toLowerCase();
					}
					if (thisValue.indexOf(findMe) >= 0) {
						hits.add(Integer.valueOf(row));
					}
				}
			}
		}
		filterToRows(hits);
		manualFilter = false;
		fireTableChanged(new TableModelEvent(this));
	}
	
	private TreeSet<Integer> applyAnyColFilter(String value, 
			boolean isNotFlag, boolean doesNotFlag, boolean isFlag){
		
		TreeSet<Integer> hits = new TreeSet();
		for (int row = 0; row < model.getRowCount(); row++) {
			for (int col = 0; col < model.getColumnCount(); col++) {
				String thisValue = model.getValueAt(row, col) + "";
				if (!caseSensitive) {
					thisValue = thisValue.toLowerCase();
					value = value.toLowerCase();
				}
				if(isNotFlag) {
					if(!thisValue.contentEquals(value)) {
						hits.add(Integer.valueOf(row));
						break;
					}
				} else if(doesNotFlag) {
					if(!thisValue.contains(value)) {
						hits.add(Integer.valueOf(row));
						break;
					}
				}
				else if(isFlag) {
					if (thisValue.contentEquals(value)) {
						hits.add(Integer.valueOf(row));
						break;
					}
				}
				else if(thisValue.contains(value)) {
					hits.add(Integer.valueOf(row));
					break;
				}
			}
		}
		return hits;
	}
	
	private TreeSet<Integer> applyAllColsFilter(String value, 
			boolean isNotFlag, boolean doesNotFlag, boolean isFlag){
		
		TreeSet<Integer> hits = new TreeSet();
		for (int row = 0; row < model.getRowCount(); row++) {
			boolean allFields = true;
			for (int col = 0; col < model.getColumnCount(); col++) {
				String thisValue = model.getValueAt(row, col) + "";
				if (!caseSensitive) {
					thisValue = thisValue.toLowerCase();
					value = value.toLowerCase();
				}
				if(isNotFlag) {
					if(thisValue.contentEquals(value)) {
						allFields = false;
						break;
					}
				} else if(doesNotFlag) {
					if(thisValue.contains(value)) {
						allFields = false;
						break;
					}
				} else if(isFlag) {
					if (!thisValue.contentEquals(value)) {
							allFields = false;
							break;
					}
				} else if(!thisValue.contains(value)) {
					allFields = false;
					break;
				}
			}
			if(allFields) {
				hits.add(Integer.valueOf(row));
			}
		}
		return hits;
	}

	public TreeSet<Integer> applyExprFilter(Double min, Double n, Double minTotal) {
		TreeSet<Integer> hits = new TreeSet();
		MetaOmProject myProject = MetaOmGraph.getActiveProject();
		Boolean minTotalExist = (minTotal != null);
		int nCount;
		double geneSum;

		int[] rowList = myProject.getGeneListRowNumbers(MetaOmGraph.getActiveTable().getSelectedListName());
		for(int i = 0; i < rowList.length; i++) {
			nCount = 0;
			geneSum = 0;

			try {
				double[] rowData = myProject.getIncludedData(rowList[i]);
				for (double curr : rowData) {
					if (curr > min) {
						nCount++;
						geneSum += curr;
					}
				}
				if (nCount >= n) {
					if (minTotalExist) {
						if (geneSum > minTotal) {
							hits.add(Integer.valueOf(i));
						}
					} else {
						hits.add(Integer.valueOf(i));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hits;
	}

	public TreeSet<Integer> applyMeanFilter(Double min, Double max) {
		TreeSet<Integer> hits = new TreeSet();
		MetaOmProject myProject = MetaOmGraph.getActiveProject();
		double mean;

		int[] rowList = myProject.getGeneListRowNumbers(MetaOmGraph.getActiveTable().getSelectedListName());
		for(int i = 0; i < rowList.length; i++) {
			try {
				mean = 0;
				double[] rowData = myProject.getIncludedData(rowList[i]);
				if (rowData.length == 0) { // prevent divide by zero
					continue;
				}
				for (double curr : rowData) {
					mean += curr;
				}
				mean = mean / rowData.length;
				if (mean >= min && mean <= max) {
					hits.add(Integer.valueOf(i));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hits;
	}

	public void applyComplexFilter(String[] values) {
		// query contating column info is like "searchterm":::"colnumber" where
		// colnumber starts from zero
		String delim = ":::";

		List<ComplexQuery> complexQueries = new ArrayList<>();

		// if query ends with --C: means case sensitive
		String caseFlag = "--C";
		caseSensitive = false;
		TreeSet<Integer> hits = new TreeSet();
		try {
			// Parse the values into ComplexQueries
			for (String findMe : values) {
				boolean isNotFlag = false;
				boolean doesNotFlag = false;
				boolean isFlag = false;
				if (findMe.length() > 1) {
					if (findMe.substring(0, 2).contentEquals("!=")) {
						isNotFlag = true;
						findMe = findMe.substring(2);
					} else if (findMe.charAt(0) == '!') {
						doesNotFlag = true;
						findMe = findMe.substring(1);
					} else if (findMe.charAt(0) == '=') {
						isFlag = true;
						findMe = findMe.substring(1);
					}
				}
				String andOr = null;
				if (findMe.split(delim).length > 2) {
					andOr = findMe.split(delim)[2];
				}
				ComplexQuery complexQuery = new ComplexQuery(findMe.split(delim)[0], findMe.split(delim)[1], andOr);
				if (findMe.contains("--C")) {
					complexQuery.markCaseFlagTrue();
				}
				if (isFlag) {
					complexQuery.markIsFlagTrue();
				} else if (isNotFlag) {
					complexQuery.markIsNotFlagTrue();
				} else if (doesNotFlag) {
					complexQuery.markDoesNotFlagTrue();
				} else {
					complexQuery.markAsContains();
				}
				complexQueries.add(complexQuery);
			}

			int numAnds = 0;
			int numOrs = 0;
			for (ComplexQuery query : complexQueries) {
				if (query.isOr()) {
					numOrs++;
				} else if (query.isAnd()) {
					numAnds++;
				}
			}

			// Get all rows to begin trimming down
			TreeSet<Integer> currentRows = new TreeSet<>();
			hits = currentRows;
			for (int i = 0; i < model.getRowCount(); i++) {
				currentRows.add(Integer.valueOf(i));
			}
			// If queries are all OR statements use existing logic for any col
			if (numAnds == 0) {
				hits.retainAll(checkOrStatements(complexQueries, currentRows));
			// If queries are all AND statements use existing logic for all col and only include if all are true
			} else if (numOrs == 0) {
				for (ComplexQuery query : complexQueries) {
					hits.retainAll(findRelevantRows(query.generateFilter()));
					if (hits.isEmpty()) {
						break;
					}
				}
			} else {
				// If neither of the above is true we have a mixed group of ANDs and ORs
				List<ComplexQuery> orSetToCheck;
				ComplexQuery query;
				for (int i = 0; i < complexQueries.size(); i++) {
					query = complexQueries.get(i);
					if (query.isOr()) {
						orSetToCheck = new ArrayList<>();
						while (query.isOr()) {
							orSetToCheck.add(query);
							query = complexQueries.get(++i);
						}
						orSetToCheck.add(query);
						hits.retainAll(checkOrStatements(orSetToCheck, currentRows));
					} else {
						hits.retainAll(findRelevantRows(query.generateFilter()));
					}
					if (hits.isEmpty()) { // hits is empty so no rows match our entire query
						break;
					} else {
						currentRows = hits;
					}
				}
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ae) {

		}

		filterToRows(hits);
		manualFilter = false;
		fireTableChanged(new TableModelEvent(this));

		//Harsha - Reproducibility log
		if (MetaOmGraph.getLoggingRequired()) {
			try {
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());

				HashMap<String, Object> dataMap = new HashMap<String, Object>();

				dataMap.put("Filter Strings", values);
				dataMap.put("Num Hits", hits.size());

				HashMap<String, Object> result = new HashMap<String, Object>();
				result.put("Color 1", MetaOmGraph.getActiveProject().getColor1());
				result.put("Color 2", MetaOmGraph.getActiveProject().getColor2());
				result.put("Sample Action", MetaOmGraph.getCurrentSamplesActionId());
				result.put("Playable", "true");
				result.put("result", "OK");

				ActionProperties filterAction = new ActionProperties("filter", actionMap, dataMap, result, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				filterAction.logActionProperties();
			} catch (Exception e) {

			}
		}
	}

	private TreeSet<Integer> checkOrStatements(List<ComplexQuery> queries, TreeSet<Integer> rows) {
		TreeSet<Integer> hits = new TreeSet<>();
		for (ComplexQuery query : queries) {
			hits.addAll(findRelevantRows(query.generateFilter()));
		}
		hits.retainAll(rows);
		return hits;
	}

	public TreeSet<Integer> findRelevantRows(String filter) {
		String delim = ":::";

		// if query ends with --C: means case sensitive
		String caseFlag = "--C";
		caseSensitive = false;
		TreeSet<Integer> hits = new TreeSet();
		try {
			boolean isNotFlag = false;
			boolean doesNotFlag = false;
			boolean isFlag = false;
			if (filter.length() > 1) {
				if (filter.substring(0, 2).contentEquals("!=")) {
					isNotFlag = true;
					filter = filter.substring(2);
				} else if (filter.charAt(0) == '!') {
					doesNotFlag = true;
					filter = filter.substring(1);
				} else if (filter.charAt(0) == '=') {
					isFlag = true;
					filter = filter.substring(1);
				}
			}
			boolean colFlag = false;
			int colInt = -1;
			boolean allCols = false;
			boolean anyCol = true;
			// delim should be present and should have values on both sides
			if (filter.indexOf(delim) > -1 && filter.indexOf(delim) < filter.length() - delim.length()) {
				String col = filter.split(delim)[1];
				if (col.contentEquals("ALL")) {
					allCols = true;
					anyCol = false;
					filter = filter.split(delim)[0];
				} else if (col.contentEquals("ANY")) {
					anyCol = true;
					allCols = false;
					filter = filter.split(delim)[0];
				} else {
					// check if col is a valid integer
					try {
						colInt = Integer.parseInt(col);
						colFlag = true;

					} catch (NumberFormatException e) {
						// not an integer!
						colFlag = false;
					}
					if (colFlag) {
						// check if col is in range of tables
						if (colInt < 0 || colInt >= model.getColumnCount()) {
							colFlag = false;
						}
					}
				}

			}

			if (colFlag) {
				// search only specified column
				filter = filter.split(delim)[0];

				// JOptionPane.showMessageDialog(null, "searching:" + filter + " col:" +
				// colInt);

				if (filter.endsWith(caseFlag)) {
					caseSensitive = true;
					filter = filter.split(caseFlag)[0];
				}

				for (int row = 0; row < model.getRowCount(); row++) {
					// for (int col = 0; col <= colInt; col++) {
					String thisValue = model.getValueAt(row, colInt) + "";
					if (!caseSensitive) {
						thisValue = thisValue.toLowerCase();
						filter = filter.toLowerCase();
					}
					if (isNotFlag) {
						if (!thisValue.contentEquals(filter)) {
							hits.add(Integer.valueOf(row));
						}
					} else if (doesNotFlag) {
						if (!thisValue.contains(filter)) {
							hits.add(Integer.valueOf(row));
						}
					} else if (isFlag) {
						if (thisValue.contentEquals(filter)) {
							hits.add(Integer.valueOf(row));
						}
					} else if (thisValue.contains(filter)) {
						hits.add(Integer.valueOf(row));
					}
				}
			} else {
				if (filter.endsWith(caseFlag)) {
					caseSensitive = true;
					filter = filter.split(caseFlag)[0];
				}
				if (anyCol) {
					hits.addAll(applyAnyColFilter(filter, isNotFlag, doesNotFlag, isFlag));
				} else if (allCols) {
					hits.addAll(applyAllColsFilter(filter, isNotFlag, doesNotFlag, isFlag));
				}
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ae) {

		}
		return hits;
	}

	/**
	 * @author urmi modified urmi add columns to query for better searching
	 * @param values
	 * 
	 */
	public void applyFilter(String[] values) {
		// query contating column info is like "searchterm":::"colnumber" where
		// colnumber starts from zero
		String delim = ":::";

		// if query ends with --C: means case sensitive
		String caseFlag = "--C";
		caseSensitive = false;
		TreeSet<Integer> hits = new TreeSet();
		try {
			for (String findMe : values) {
				boolean isNotFlag = false;
				boolean doesNotFlag = false;
				boolean isFlag = false;
				boolean exprFlag = false;
				boolean meanFlag = false;
				if (findMe.length() >= 4) {
					if (findMe.contains("EXPR")) {
						exprFlag = true;

						findMe = findMe.replace("EXPR", "");
						findMe = findMe.replace("{", "");
						findMe = findMe.replace("}", "");
					} else if (findMe.contains("MEAN")) {
						meanFlag = true;

						findMe = findMe.replace("MEAN", "");
						findMe = findMe.replace("{", "");
						findMe = findMe.replace("}", "");
					}
				}
				if(findMe.length() > 1) {
					if(findMe.substring(0, 2).contentEquals("!=")) {
						isNotFlag = true;
						findMe = findMe.substring(2);
					} else if(findMe.charAt(0) == '!') {
						doesNotFlag = true;
						findMe = findMe.substring(1);
					} else if(findMe.charAt(0) == '=') {
						isFlag = true;
						findMe = findMe.substring(1);
					}
				}
				boolean colFlag = false;
				int colInt = -1;
				boolean allCols = false;
				boolean anyCol = true;
				// delim should be present and should have values on both sides
				if (findMe.indexOf(delim) > -1 && findMe.indexOf(delim) < findMe.length() - delim.length()) {
					String col = "";
					if (exprFlag || meanFlag) {
						anyCol = false;
						allCols = false;
					} else {
						col = findMe.split(delim)[1];
					}
					if(col.contentEquals("ALL")) {
						allCols = true;
						anyCol = false;
						findMe = findMe.split(delim)[0];
					} else if(col.contentEquals("ANY")) {
						anyCol = true;
						allCols = false;
						findMe = findMe.split(delim)[0];
					} else {
						// check if col is a valid integer
						try {
							colInt = Integer.parseInt(col);
							colFlag = true;

						} catch (NumberFormatException e) {
							// not an integer!
							colFlag = false;
						}
						if (colFlag) {
							// check if col is in range of tables
							if (colInt < 0 || colInt >= model.getColumnCount()) {
								colFlag = false;
							}
						}
					}

				}

				if (colFlag) {
					// search only specified column
					findMe = findMe.split(delim)[0];

					// JOptionPane.showMessageDialog(null, "searching:" + findMe + " col:" +
					// colInt);

					if (findMe.endsWith(caseFlag)) {
						caseSensitive = true;
						findMe = findMe.split(caseFlag)[0];
					}

					for (int row = 0; row < model.getRowCount(); row++) {
						// for (int col = 0; col <= colInt; col++) {
						String thisValue = model.getValueAt(row, colInt) + "";
						if (!caseSensitive) {
							thisValue = thisValue.toLowerCase();
							findMe = findMe.toLowerCase();
						}
						if(isNotFlag) {
							if(!thisValue.contentEquals(findMe)) {
								hits.add(Integer.valueOf(row));
							}
						} else if(doesNotFlag) {
							if(!thisValue.contains(findMe)) {
								hits.add(Integer.valueOf(row));
							}
						} else if(isFlag) {
							if (thisValue.contentEquals(findMe)) {
								hits.add(Integer.valueOf(row));
							}
						} else if(thisValue.contains(findMe)) {
							hits.add(Integer.valueOf(row));
						}
					}
				} else {
					if (findMe.endsWith(caseFlag)) {
						caseSensitive = true;
						findMe = findMe.split(caseFlag)[0];
					}
					if(anyCol) {
						hits.addAll(applyAnyColFilter(findMe, isNotFlag, doesNotFlag, isFlag));
					} else if(allCols) {
						hits.addAll(applyAllColsFilter(findMe, isNotFlag, doesNotFlag, isFlag));
					} else if (exprFlag) {
						try {
							Double min = Double.parseDouble(findMe.split(delim)[0]);
							Double max = Double.parseDouble(findMe.split(delim)[1]);
							Double minTotal = null;
							if (findMe.split(delim).length == 3) {
								minTotal = Double.parseDouble(findMe.split(delim)[2]);
							}
							hits.addAll(applyExprFilter(min, max, minTotal));
						} catch (NumberFormatException e) {
							// if one of the search terms is invalid handle silently
						}
					} else if (meanFlag) {
						try {
							Double min = Double.parseDouble(findMe.split(delim)[0]);
							Double max = Double.parseDouble(findMe.split(delim)[1]);
							hits.addAll(applyMeanFilter(min, max));
						} catch (NumberFormatException e) {
							// if one of the search terms is invalid handle silently
						}
					}
				}
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ae) {

		}

		filterToRows(hits);
		manualFilter = false;
		fireTableChanged(new TableModelEvent(this));

		//Harsha - Reproducibility log
		if (MetaOmGraph.getLoggingRequired()) {
			try {
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());

				HashMap<String, Object> dataMap = new HashMap<String, Object>();

				dataMap.put("Filter Strings", values);
				dataMap.put("Num Hits", hits.size());

				HashMap<String, Object> result = new HashMap<String, Object>();
				result.put("Color 1", MetaOmGraph.getActiveProject().getColor1());
				result.put("Color 2", MetaOmGraph.getActiveProject().getColor2());
				result.put("Sample Action", MetaOmGraph.getCurrentSamplesActionId());
				result.put("Playable", "true");
				result.put("result", "OK");

				ActionProperties filterAction = new ActionProperties("filter", actionMap, dataMap, result, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				filterAction.logActionProperties();
			} catch (Exception e) {

			}
		}
	}

	public synchronized void filterToRows(int[] rows) {
		if ((rows == null) || (rows.length <= 0)) {
			throw new InvalidParameterException("rows must be non-null and have length>0");
		}
		while (!available) {
			try {
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		available = false;
		filteredData = new Object[rows.length][model.getColumnCount()];
		rowMap = new TreeMap();
		for (int row = 0; row < rows.length; row++) {
			rowMap.put(Integer.valueOf(row), Integer.valueOf(rows[row]));
			for (int col = 0; col < filteredData[row].length; col++) {
				filteredData[row][col] = model.getValueAt(rows[row], col);
			}
		}
		available = true;
		notifyAll();
		fireTableChanged(new TableModelEvent(this));
		manualFilter = true;
	}

	public synchronized void filterToRows(Collection<Integer> rows) {
		while (!available) {
			try {
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		available = false;
		filteredData = new Object[rows.size()][model.getColumnCount()];
		rowMap = new TreeMap();
		int row = 0;
		for (Iterator localIterator = rows.iterator(); localIterator.hasNext();) {
			int thisRow = ((Integer) localIterator.next()).intValue();
			rowMap.put(Integer.valueOf(row), Integer.valueOf(thisRow));
			for (int col = 0; col < filteredData[row].length; col++) {
				filteredData[row][col] = model.getValueAt(thisRow, col);
			}
			row++;
		}
		available = true;
		notifyAll();
		fireTableChanged(new TableModelEvent(this));
		manualFilter = true;
	}

	public synchronized void clearFilter() {
		while (!available) {
			try {
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		available = false;
		rowMap = null;
		filteredData = new Object[model.getRowCount()][model.getColumnCount()];
		for (int x = 0; x < filteredData.length; x++)
			for (int y = 0; y < filteredData[x].length; y++)
				filteredData[x][y] = model.getValueAt(x, y);
		available = true;
		notifyAll();
		fireTableChanged(new TableModelEvent(this));
	}

	@Override
	public void fireTableChanged(TableModelEvent e) {
		TableModelListener[] listeners = getTableModelListeners();
		for (int x = 0; x < listeners.length; x++) {
			listeners[x].tableChanged(e);
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		while (!available) {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		Object result;
		synchronized (filteredData) { // Object result;
			if ((filteredData == null) || (row >= getRowCount()) || (col >= getColumnCount())) {
				result = null;
			} else { // Object result;
				if ((row < 0) || (col < 0)) {
					result = null;
				} else {
					result = filteredData[row][col];
				}
			}
		}
		// Object result;
		return result;
	}

	@Override
	public int getRowCount() {
		if (filteredData == null)
			return 0;
		return filteredData.length;
	}

	public int getUnfilteredRowCount() {
		return model.getRowCount();
	}

	public Object getUnfilteredValueAt(int row, int col) {
		return model.getValueAt(row, col);
	}

	public int[] getUnfilteredRows(int[] selectedRows) {
		if (rowMap == null)
			return selectedRows;
		int[] result = new int[selectedRows.length];
		for (int x = 0; x < result.length; x++)
			result[x] = rowMap.get(Integer.valueOf(x));
		return result;
	}

	public int getUnfilteredRow(int selectedRow) {
		if (rowMap == null)
			return selectedRow;
		return rowMap.get(Integer.valueOf(selectedRow));
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Filterable Table Test");
		final JTextField filterField = new JTextField();
		String[][] data = new String[100][3];
		try {
			RandomAccessFile dataIn = new RandomAccessFile("z:\\supercluster75line.txt", "r");
			for (int x = 0; x < data.length; x++) {
				for (int y = 0; y < data[x].length; y++) {
					data[x][y] = dataIn.readString(' ', true);
				}
			}

			dataIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] headers = { "col1", "col2", "col3" };
		final FilterableTableModel model = new FilterableTableModel(new NoneditableTableModel(data, headers));
		TableSorter sorter = new TableSorter(model);
		final JTable table = new JTable(sorter);
		sorter.setTableHeader(table.getTableHeader());
		f.getContentPane().add(filterField, "First");
		final JScrollPane scrolly = new JScrollPane(table);
		f.getContentPane().add(scrolly, "Center");
		f.setSize(800, 600);
		f.setDefaultCloseOperation(3);
		filterField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.applyFilter(filterField.getText());
				scrolly.setViewportView(table);
			}

		});
		f.setVisible(true);
	}

	@Override
	public int getColumnCount() {
		return model.getColumnCount();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return model.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Class result = null;
		// check if all values are number
		String thisHeader = getColumnName(columnIndex);
		result = MetaOmGraph.getActiveProject().getInfoColType(thisHeader);
		if (result != null) {
			// JOptionPane.showMessageDialog(null," filterabletb hdr:"+thisHeader+"v:"+result.toString());
			return result;

		}
		//JOptionPane.showMessageDialog(null, "return null filterablemodel");
		return model.getColumnClass(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		while (!available) {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		if (rowMap != null) {
			int trueRow = rowMap.get(Integer.valueOf(rowIndex)).intValue();
			filteredData[rowIndex][columnIndex] = aValue;
			model.setValueAt(aValue, trueRow, columnIndex);
		} else {
			filteredData[rowIndex][columnIndex] = aValue;
			model.setValueAt(aValue, rowIndex, columnIndex);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return model.getColumnName(columnIndex);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		documentChange(e.getDocument());
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		documentChange(e.getDocument());
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		documentChange(e.getDocument());
	}

	private void documentChange(Document doc) {
		try {
			String text = doc.getText(0, doc.getLength());
			applyFilter(text);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public String getFilterText() {
		return filterText;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (!manualFilter) {
			applyFilter(filterText);
		}
		fireTableDataChanged();
	}
}
