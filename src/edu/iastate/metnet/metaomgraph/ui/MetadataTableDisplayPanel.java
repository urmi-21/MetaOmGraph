package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.dizitart.no2.Document;

import edu.iastate.metnet.metaomgraph.AlphanumericComparator;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.ComputePCA;
import edu.iastate.metnet.metaomgraph.ComputeRunsSimilarity;
import edu.iastate.metnet.metaomgraph.ComputeTSNE;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.MetadataHeaderRenderer;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.chart.BarChart;
import edu.iastate.metnet.metaomgraph.chart.BoxPlot;
import edu.iastate.metnet.metaomgraph.chart.PlotRunsasSeries;
import edu.iastate.metnet.metaomgraph.chart.ScatterPlotDimReduction;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;

public class MetadataTableDisplayPanel extends JPanel implements ActionListener, ListSelectionListener, ChangeListener {

	private StripedTable table;
	private MetadataCollection obj;
	private List<Document> metadata;
	private String[] headers;
	private HashMap<String, String[]> listHeadersMap;
	private List<String> prevLists = new ArrayList<String>();;
	// private List<String> toHighlight;
	// highlight rows where iths col contains coressponding value. e.g. if
	// tohighlight is 2:{a,b,c,d}, 3:{abc} this will highlight rows
	// where a,b,c or d is present under second column and where abc is present
	// under 3rd column
	private HashMap<Integer, List<String>> toHighlight;
	// highlightedRows contains list of current highlighted rows
	private List<Integer> highlightedRows;
	private List<String> previousSearchedRows = new ArrayList<String>();

	private JScrollPane scrollPane;
	// columns containg SRR and GEO ids to create hyperlinks
	private int srrColumn = -1;
	private int srsColumn = -1;
	private int srxColumn = -1;
	private int srpColumn = -1;
	private int gseColumn = -1;
	private int gsmColumn = -1;
	private boolean autoDetect = false;

	// ListNames panel
	private JList sampleDataList;
	private JScrollPane sampleDataListScrollPane;
	private JButton listCreateButton;
	private JButton listEditButton;
	private JButton listDeleteButton;
	private JButton listRenameButton;
	
	// PCA & TSNE
	private boolean normalizeData;
	private boolean parallel;
	private String selectedGeneListDimRed;
	private double perplexity;
	private int maxIter;
	private double theta;
	private int numDims = 2;
	private boolean usePCA;
	/**
	 * Default Properties
	 */

	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColorEven();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColorOdd();
	private Color HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
	private Color HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();
	private boolean USEDEFAULTCOLORS = true;

	public MetadataTableDisplayPanel() {
		this(null);
	}

	/**
	 * Create the panel.
	 */
	public MetadataTableDisplayPanel(MetadataCollection obj) {

		// urmi set default colors or not
		if (MetaOmGraph.getCurrentThemeName().equals("default")) {
			USEDEFAULTCOLORS = true;
		} else {
			USEDEFAULTCOLORS = false;
		}

		listHeadersMap = new HashMap<String, String[]>();
		this.obj = obj;
		metadata = this.obj.getAllData();
		this.headers = obj.getHeaders();
		setLayout(new BorderLayout(0, 0));

		JPanel mainPanel = new JPanel();
		add(mainPanel, BorderLayout.NORTH);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = initializeMenuBar();

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));

		JPanel listPanel = createListPanel();

		scrollPane = new JScrollPane();
		initTable();
		scrollPane.setViewportView(table);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, listPanel, scrollPane);
		splitPane.setDividerSize(1);
		panel.add(splitPane);

		mainPanel.add(menuBar, "First");
		mainPanel.add(panel, "Center");
		add(mainPanel, "Center");

		MetaOmGraph.getActiveProject().addChangeListener(this);
	}

	// Create sample data list names to add to list panel
	private void createSampleDataListNames() {
		String[] listNames = MetaOmGraph.getActiveProject().getSampleDataListNames();
		Arrays.sort(listNames, new ListNameComparator());
		sampleDataList = new JList(listNames);
		sampleDataList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		sampleDataList.setSelectedIndex(0);
		sampleDataList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				String selectedRowName = (String) sampleDataList.getSelectedValue();
				List<String> values = MetaOmGraph.getActiveProject().getSampleDataListRowNames(selectedRowName);
				// Double click to update include and exclude.
				if(e.getClickCount() > 1) {
					updateTable(values, true);
				}
			}
		});

		sampleDataList.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				JList l = (JList) e.getSource();
				ListModel m = l.getModel();
				int index = l.locationToIndex(e.getPoint());
				if (index > -1) {
					// create tooltip
					String thisListName = m.getElementAt(index).toString();
					int numElements = MetaOmGraph.getActiveProject().getSampleDataListRowNames(thisListName).size();
					l.setToolTipText(thisListName + ":" + numElements + " Elements\n" + "Single click to view the list; Double click to make it active");
				}
			}
			
		});
		
		sampleDataList.addListSelectionListener(this);

		for (String list : listNames) {
			listHeadersMap.put(list, headers);
		}
	}

	// Create the list panel.
	private JPanel createListPanel() {
		JToolBar listToolbar = new JToolBar();
		listToolbar.setFloatable(false);
		IconTheme theme = MetaOmGraph.getIconTheme();
		listDeleteButton = new JButton(theme.getListDelete());
		listDeleteButton.setActionCommand("delete list");
		listDeleteButton.addActionListener(this);
		listDeleteButton.setToolTipText("Delete the selected list");

		listEditButton = new JButton(theme.getListEdit());
		listEditButton.setActionCommand("edit list");
		listEditButton.addActionListener(this);
		listEditButton.setToolTipText("Edit the selected list");

		listRenameButton = new JButton(theme.getListRename());
		listRenameButton.setActionCommand("rename list");
		listRenameButton.addActionListener(this);
		listRenameButton.setToolTipText("Rename the selected list");

		listCreateButton = new JButton(theme.getListAdd());
		listCreateButton.addActionListener(this);
		listCreateButton.setActionCommand("new list");
		listCreateButton.setToolTipText("Create a new list");

		listDeleteButton.setEnabled(false);
		listEditButton.setEnabled(false);
		listRenameButton.setEnabled(false);

		listToolbar.add(listCreateButton);
		listToolbar.add(listEditButton);
		listToolbar.add(listRenameButton);
		listToolbar.add(listDeleteButton);

		JPanel listPanel = new JPanel(new BorderLayout());

		createSampleDataListNames();
		sampleDataListScrollPane = new JScrollPane(sampleDataList);
		listPanel.add(listToolbar, "First");
		listPanel.add(sampleDataListScrollPane, "Center");

		Border loweredetched = BorderFactory.createEtchedBorder();
		listPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "Lists"));
		listPanel.setMinimumSize(listToolbar.getPreferredSize());
		return listPanel;
	}

	// Initialize/Create the menu bar.
	private JMenuBar initializeMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem exportToTextItem = new JMenuItem("Export to text file");
		exportToTextItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTabletofile(table, "Metadata Table Panel");
			}
		});

		JMenuItem exportToExcelItem = new JMenuItem("Export to xlsx");
		exportToExcelItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTableToExcel(table);
			}
		});
		mnFile.add(exportToTextItem);
		mnFile.add(exportToExcelItem);

		JMenuItem mntmNewProjectWith = new JMenuItem("New Project With Selected");

		JMenu mnSearch = new JMenu("Search");
		menuBar.add(mnSearch);
		// remove simple search
		// mnSearch.add(mntmSimple);

		JMenuItem mntmAdvance = new JMenuItem("Search");
		mntmAdvance.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
						MetaOmGraph.getActiveProject(), false);
				final MetadataQuery[] queries;
				queries = tsp.showSearchDialog();
				if (tsp.getQueryCount() <= 0) {
					// System.out.println("Search dialog cancelled");
					// User didn't enter any queries
					return;
				}
				// final int[] result = new
				// int[MetaOmGraph.getActiveProject().getDataColumnCount()];
				final List<String> result = new ArrayList<>();
				new AnimatedSwingWorker("Searching...", true) {
					@Override
					public Object construct() {
						List<String> hits = MetaOmGraph.getActiveProject().getMetadataHybrid().getMatchingRows(queries,
								tsp.matchAll());
						// return if no hits
						if (hits.size() == 0) {
							// JOptionPane.showMessageDialog(null, "hits len:"+hits.length);
							// nohits=true;
							result.add("NULL");
							return null;
						} else {
							for (int i = 0; i < hits.size(); i++) {
								result.add(hits.get(i));
							}
						}

						return null;
					}

				}.start();

				// add highlights to results
				// move rows in result to top then highlight
				int dataColNum = table.getColumn(obj.getDatacol()).getModelIndex();
				String selectedList = (String) sampleDataList.getSelectedValue();
				if (selectedList != "Complete List") {
					List<String> activeListRows = MetaOmGraph.getActiveProject()
							.getSampleDataListRowNames(selectedList);
					List<String> activeListResult = result.stream().filter(activeListRows::contains)
							.collect(Collectors.toList());
					result.clear();
					result.addAll(activeListResult);
				}
				moveRowsToTop(result, dataColNum);
				highlightedRows = null;
				previousSearchedRows = new ArrayList<String>();
				highlightedRows = new ArrayList<>();
				toHighlight = new HashMap<Integer, List<String>>();
				toHighlight.put(dataColNum, result);
				// create a list of highlighted rows and save under highlightedRows
				// this is removed from the rendere function as it didn't add all the rows
				// unless whole table is rendered of scrolled
				for (int modelRow = 0; modelRow < table.getRowCount(); modelRow++) {
					for (Integer j : toHighlight.keySet()) {
						String type = (String) table.getModel().getValueAt(modelRow, j);
						if (highlightThisRow(j, type)) {
							if (!highlightedRows.contains(modelRow)) {
								highlightedRows.add(modelRow);
								previousSearchedRows.add(type);
							}
						}
					}
				}
				JOptionPane.showMessageDialog(null, highlightedRows.size() + " rows matched the query", "Search result",
						JOptionPane.INFORMATION_MESSAGE);
				table.repaint();

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				List<String> mq = new ArrayList();
				for (MetadataQuery q : queries) {
					mq.add(q.toString());
				}
				dataMap.put("Queries", mq);
				dataMap.put("numHits", result.size());

				HashMap<String, Object> resultLog = new HashMap<String, Object>();
				resultLog.put("result", "OK");

				ActionProperties searchMetadataTableAction = new ActionProperties("search-metadata-table", actionMap,
						dataMap, resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				searchMetadataTableAction.logActionProperties();

				// JOptionPane.showMessageDialog(null, "toH:"+toHighlight.toString());

			}
		});
		mnSearch.add(mntmAdvance);

		JMenuItem mntmRemovePrevious = new JMenuItem("Clear Last Search");
		mntmRemovePrevious.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearLastSearchedRows();

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> resultLog = new HashMap<String, Object>();
				resultLog.put("result", "OK");

				ActionProperties searchMetadataTableAction = new ActionProperties("clear-last-search", actionMap, null,
						resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				searchMetadataTableAction.logActionProperties();

			}
		});
		mnSearch.add(mntmRemovePrevious);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenu mnFilter = new JMenu("Filter");
		mnFilter.setToolTipText(
				"Filter data and metadata for the current project. All the plots and analysis will use this reduced dataset.");
		mnEdit.add(mnFilter);

		JMenu mnByRow = new JMenu("By row");
		mnFilter.add(mnByRow);

		JMenuItem mntmFilterSelectedRows = new JMenuItem("Filter selected rows");
		mntmFilterSelectedRows.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object[] options = { "Remove", "Keep", "Cancel" };
				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				JPanel optPanel = new JPanel();
				optPanel.add(new JLabel("Remove or keep selected rows ?"));
				int option = JOptionPane.showOptionDialog(null, optPanel, "Choose an option",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

				if (option == JOptionPane.CANCEL_OPTION) {
					return;
				} else if (option == JOptionPane.YES_OPTION) {
					dataMap.put("option", "remove");
					filterSelectedRows(false);
				} else if (option == JOptionPane.NO_OPTION) {
					dataMap.put("option", "keep");
					filterSelectedRows(true);
				}

				// int[] selected = table.getSelectedRows();
				// List<String> selectedNames = new ArrayList<String>();
				// DefaultTableModel model = (DefaultTableModel) table.getModel();
				// for (int i = 0; i < selected.length; i++) {
				// selectedNames.add(model.getValueAt(table.convertRowIndexToModel(selected[i]),
				// table.getColumn(obj.getDatacol()).getModelIndex()).toString());
				// }
				//
				// // Harsha - reproducibility log
				// HashMap<String, Object> actionMap = new HashMap<String, Object>();
				// actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				// actionMap.put("section", "Sample Metadata Table");
				//
				// dataMap.put("Selected Rows", selectedNames);
				// //dataMap.put("Columns", selectColumn());
				//
				// HashMap<String, Object> resultLog = new HashMap<String, Object>();
				// resultLog.put("result", "OK");
				//
				// ActionProperties filterSelectedRowsAction = new
				// ActionProperties("filter-selected-rows", actionMap,
				// dataMap, resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS
				// zzz").format(new Date()));
				// filterSelectedRowsAction.logActionProperties();

			}
		});
		mnByRow.add(mntmFilterSelectedRows);

		JMenuItem mntmFilterLastSearched = new JMenuItem("Filter last searched");
		mntmFilterLastSearched.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				Object[] options = { "Remove", "Keep", "Cancel" };
				JPanel optPanel = new JPanel();
				optPanel.add(new JLabel("Remove or keep selected rows ?"));
				int option = JOptionPane.showOptionDialog(null, optPanel, "Choose an option",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

				if (option == JOptionPane.CANCEL_OPTION) {
					return;
				} else if (option == JOptionPane.YES_OPTION) {
					// remove highlighted rows
					dataMap.put("option", "remove");
					filterHighlightedRows(false);

				} else if (option == JOptionPane.NO_OPTION) {
					// keep highlighted rows
					dataMap.put("option", "keep");
					filterHighlightedRows(true);

				}

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				dataMap.put("highlightedRows", highlightedRows);

				HashMap<String, Object> resultLog = new HashMap<String, Object>();
				resultLog.put("result", "OK");

				ActionProperties filterLastSearchAction = new ActionProperties("filter-last-search", actionMap, dataMap,
						resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				filterLastSearchAction.logActionProperties();
			}
		});
		mnByRow.add(mntmFilterLastSearched);

		JMenuItem expressionFilter = new JMenuItem("Expression Filter");
		expressionFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							MetadataFilter frame = new MetadataFilter(obj);
							FrameModel fm = new FrameModel("Metadata Filter","Metadata Filter",31);
							frame.setModel(fm);
							MetaOmGraph.getDesktop().add(frame);
							frame.setVisible(true);
							frame.show();
							frame.moveToFront();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnByRow.add(expressionFilter);

		JMenuItem mntmAdvanceFilter = new JMenuItem("Advance filter");
		mntmAdvanceFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							MetadataFilter frame = new MetadataFilter(obj);
							FrameModel fm = new FrameModel("Metadata Filter","Metadata Filter",31);
							frame.setModel(fm);
							MetaOmGraph.getDesktop().add(frame);
							frame.setVisible(true);
							frame.show();
							frame.moveToFront();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnByRow.add(mntmAdvanceFilter);

		JMenuItem mntmReset_1 = new JMenuItem("Reset");
		mntmReset_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				obj.resetRowFilter();
				// clear last searched
				clearLastSearchedRows();
				// update exclude list
				MetaOmAnalyzer.updateExcluded(obj.getExcluded(),true);
				updateTable();

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> resultLog = new HashMap<String, Object>();
				resultLog.put("result", "OK");

				ActionProperties resetRowsAction = new ActionProperties("reset-metadata-table-rows", actionMap, null,
						resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				resetRowsAction.logActionProperties();
			}
		});
		mnByRow.add(mntmReset_1);

		JMenu mnByColumn = new JMenu("By column");
		mnFilter.add(mnByColumn);

		JMenuItem mntmSelectColumn = new JMenuItem("Select columns");
		mntmSelectColumn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MetaOmGraph.getActiveTablePanel().getMetadataTableDisplay().getStripedTable().openColumnSelectorDialog("Sample Metadata");
			}
		});
		mnByColumn.add(mntmSelectColumn);


		JMenu mnRemove = new JMenu("Remove");
		mnRemove.setToolTipText("Permanently delete data or metadata. This can't be undone!");
		mnEdit.add(mnRemove);

		JMenuItem mntmRows = new JMenuItem("Rows");
		mntmRows.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							MetadataFilter frame = new MetadataFilter(obj, true);
							FrameModel fm = new FrameModel("Metadata Filter","Metadata Filter",31);
//							frame.setModel(fm);
							MetaOmGraph.getDesktop().add(frame);
							frame.setVisible(true);
							frame.show();
//							frame.moveToFront();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnRemove.add(mntmRows);

		JMenuItem mntmColumns = new JMenuItem("Columns");
		mntmColumns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							// remove cols permanently from metadata
							MetadataRemoveCols frame = new MetadataRemoveCols(headers, obj, getThisPanel(), true);
							frame.setVisible(true);
							
							FrameModel metadataColumnModel = new FrameModel("Import Metadata", "Remove Metadata Columns", 42);
							frame.setModel(metadataColumnModel);
							
							frame.setVisible(true);
							frame.setResizable(false);
							MetaOmGraph.getDesktop().add(frame);
							frame.toFront();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnRemove.add(mntmColumns);

		JMenu createSampleListMenu = new JMenu("Create Sample List");
		createSampleListMenu.setToolTipText("Create sample list from the searched samples");
		mnEdit.add(createSampleListMenu);

		JMenuItem selectedRowsItem = new JMenuItem("Selected Rows");
		selectedRowsItem.setActionCommand("Create List with Selected Rows");
		selectedRowsItem.addActionListener(this);

		JMenuItem previousSearchedRowItem = new JMenuItem("Last searched");
		previousSearchedRowItem.setActionCommand("Create List with Last Searched Rows");
		previousSearchedRowItem.addActionListener(this);

		createSampleListMenu.add(selectedRowsItem);
		createSampleListMenu.add(previousSearchedRowItem);

		JMenu mnLinkToSra = new JMenu("Link to SRA");
		mnEdit.add(mnLinkToSra);

		JMenuItem mntmAutomaticallyDetectColumns = new JMenuItem("Automatically detect columns");
		mntmAutomaticallyDetectColumns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				detectSRAColumns();
				autoDetect = true;

			}
		});
		mnLinkToSra.add(mntmAutomaticallyDetectColumns);

		JMenuItem mntmChooseColumns = new JMenuItem("Choose columns");
		mntmChooseColumns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// choose columns with SRRids
				String[] colheaders = new String[table.getColumnCount()];
				for (int i = 0; i < colheaders.length; i++) {
					colheaders[i] = table.getColumnName(i);
				}
				// display form to choose the columns to one of the SRA/GEO ids
				// table to get input
				JDialog optionsDiag = new JDialog(MetaOmGraph.getMainWindow(), "Select Columns", true);
				JScrollPane optionsPane = new JScrollPane();
				JTable optionTable = new JTable() {
					@Override
					public boolean isCellEditable(int row, int column) { // dont let edit first 2 cols
						if (column == 0 || column == 1) {
							return false;
						}
						return true;
					}
				};
				optionTable.setRowMargin(3);
				optionTable.setRowHeight(25);
				optionTable.setIntercellSpacing(new Dimension(2, 2));
				optionTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "Column number", "Header", "Choose ID type" }));
				// set default values
				DefaultTableModel tablemodel = (DefaultTableModel) optionTable.getModel();
				for (int i = 0; i < colheaders.length; i++) {
					tablemodel.addRow(new String[] { String.valueOf(i + 1), colheaders[i], "Please Choose an option" });

				}
				// third column will have options
				TableColumn optionColumn = optionTable.getColumnModel().getColumn(2);
				JComboBox comboBox = new JComboBox();
				comboBox.addItem("SRR ID");
				comboBox.addItem("SRS ID");
				comboBox.addItem("SRX ID");
				comboBox.addItem("SRP ID");
				comboBox.addItem("GSE ID");
				comboBox.addItem("GSM ID");
				comboBox.setSelectedIndex(0);
				optionColumn.setCellEditor(new DefaultCellEditor(comboBox));
				optionTable.setFont(new Font("Times New Roman", Font.PLAIN, 14));
				optionTable.setForeground(Color.RED);
				optionTable.setBackground(Color.BLACK);
				optionsPane.setViewportView(optionTable);

				// OK button
				JButton okbutton = new JButton("OK");
				okbutton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// validate input and set the columns
						for (int i = 0; i < optionTable.getRowCount(); i++) {
							String thisOpt = (String) optionTable.getModel().getValueAt(i, 2);
							if (thisOpt.equals("SRR ID")) {
								srrColumn = i;
							} else if (thisOpt.equals("SRS ID")) {
								srsColumn = i;
							} else if (thisOpt.equals("SRX ID")) {
								srxColumn = i;
							} else if (thisOpt.equals("SRP ID")) {
								srpColumn = i;
							} else if (thisOpt.equals("GSE ID")) {
								gseColumn = i;
							} else if (thisOpt.equals("GSM ID")) {
								gsmColumn = i;
							}
						}
						optionsDiag.dispose();
					}
				});

				// show in jdialog

				optionsPane.setBackground(Color.DARK_GRAY);
				optionsDiag.getContentPane().add(optionsPane, BorderLayout.CENTER);
				optionsDiag.getContentPane().add(okbutton, BorderLayout.SOUTH);
				optionsPane.setViewportView(optionTable);
				optionsDiag.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				optionsDiag.pack();
				optionsDiag.setVisible(true);

				formatHyperlinkColums();

			}
		});
		mnLinkToSra.add(mntmChooseColumns);

		JMenuItem mntmRemoveHyperlinks = new JMenuItem("Remove hyperlinks");
		mntmRemoveHyperlinks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeHyperlinks();
			}
		});
		mnLinkToSra.add(mntmRemoveHyperlinks);

		JMenuItem mntmProperties = new JMenuItem("Properties");
		// mnEdit.add(mntmProperties);

		JMenu mnView_1 = new JMenu("View");
		menuBar.add(mnView_1);

		JMenu mnPlot = new JMenu("Plot");
		mnView_1.add(mnPlot);

		JMenu mnSelectedRows = new JMenu("Selected Rows");
		mnPlot.add(mnSelectedRows);

		JMenuItem mntmAsSeries = new JMenuItem("Line Chart");
		mnSelectedRows.add(mntmAsSeries);

		JMenuItem mntmBoxPlot = new JMenuItem("Box plot");
		mnSelectedRows.add(mntmBoxPlot);

		JMenu mnColumns = new JMenu("Columns");
		mnPlot.add(mnColumns);

		JMenuItem mntmBarChart = new JMenuItem("Bar Chart");
		mntmBarChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selectedCol = selectColumn();
				plotBarChart(selectedCol);

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("Column", selectedCol);

				HashMap<String, Object> resultLog = new HashMap<String, Object>();
				resultLog.put("result", "OK");

				ActionProperties barChartMetadataTableAction = new ActionProperties("barchart-metadata-table",
						actionMap, dataMap, resultLog,
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				barChartMetadataTableAction.logActionProperties();
				return;
			}
		});
		mnColumns.add(mntmBarChart);
		mntmBoxPlot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								// get data for all selceted cols
								try {
									/**
									 * get all the row data for each col map colnum to data array
									 */
									HashMap<Integer, double[]> databyCols = getDataForSelectedDataCols();
									if (databyCols == null) {
										return;
									}
									// create box plot of selected data

									// MetaOmGraph.addInternalFrame(BoxPlotter.getSampleBoxPlot(databyCols),"Box
									// Plot");
									EventQueue.invokeLater(new Runnable() {
										@Override
										public void run() {
											try {// get data for selected rows
												BoxPlot f = new BoxPlot(databyCols, 1, MetaOmGraph.getActiveProject(),
														false);
												MetaOmGraph.getDesktop().add(f);
												f.setDefaultCloseOperation(2);
												f.setClosable(true);
												f.setResizable(true);
												f.pack();
												f.setSize(1000, 700);
												f.setVisible(true);
												f.toFront();

											} catch (Exception e) {
												JOptionPane.showMessageDialog(null,
														"Error occured while reading data!!!", "Error",
														JOptionPane.ERROR_MESSAGE);

												e.printStackTrace();
												return;
											}
										}
									});

								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						});
						return null;
					}
				}.start();

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("Selected Samples", getSelectDataColsName());

				HashMap<String, Object> resultLog = new HashMap<String, Object>();
				resultLog.put("result", "OK");

				ActionProperties boxplotMetadataTableAction = new ActionProperties("boxplot-metadata-table", actionMap,
						dataMap, resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				boxplotMetadataTableAction.logActionProperties();
			}
		});
		mntmAsSeries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								// get data for all selceted cols
								try {
									/**
									 * get all the row data for each col map colnum to data array
									 */
									HashMap<Integer, double[]> databyCols = getDataForSelectedDataCols();
									if (databyCols == null) {
										return;
									}
									PlotRunsasSeries obj = new PlotRunsasSeries("runsPlot", databyCols, 2);
									obj.createPlot();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						});
						return null;
					}
				}.start();

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("Selected Samples", getSelectDataColsName());

				HashMap<String, Object> resultLog = new HashMap<String, Object>();
				resultLog.put("result", "OK");

				ActionProperties linechartMetadataTableAction = new ActionProperties("linechart-metadata-table",
						actionMap, dataMap, resultLog,
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				linechartMetadataTableAction.logActionProperties();

			}
		});

		JMenuItem mntmSwitchToTree_1 = new JMenuItem("Switch to tree");
		mntmSwitchToTree_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// highlight selected rows in the tree
				// get selected data cols
				// s;
				int[] selected = table.getSelectedRows();
				String[] selectedNames = new String[selected.length];
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = 0; i < selected.length; i++) {
					selectedNames[i] = model.getValueAt(table.convertRowIndexToModel(selected[i]),
							table.getColumn(obj.getDatacol()).getModelIndex()).toString();
					int thisInd = MetaOmGraph.getActiveProject().getMetadataHybrid()
							.getColIndexbyName(selectedNames[i]);
					MetaOmGraph.getActiveTable().selectNode(thisInd, false);
				}
				MetaOmGraph.tableToFront();

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("selectedNames", selectedNames);

				HashMap<String, Object> resultLog = new HashMap<String, Object>();
				resultLog.put("result", "OK");

				ActionProperties searchMetadataTableAction = new ActionProperties("switch-to-tree", actionMap, dataMap,
						resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				searchMetadataTableAction.logActionProperties();

				// int
				// thisInd=MetaOmGraph.getActiveProject().getMetadataHybrid().getColIndexbyName(thisSname);
				// MetaOmGraph.getActiveTable().selectNode(thisInd,true);
			}

		});
		mnView_1.add(mntmSwitchToTree_1);
		// mnFile.add(mntmNewProjectWith);

		JMenu mnAnalyze = new JMenu("Analyze");
		menuBar.add(mnAnalyze);

		JMenuItem mntmCosineSililarity = new JMenuItem("Cosine similarity");
		mntmCosineSililarity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/**
				 * select 2 or more runs and display the cosine simmilarity between them
				 */

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> dataMap = new HashMap<String, Object>();

				int[] selectedInd = table.getSelectedRows();
				List<String> selectedNames = new ArrayList<String>();
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = 0; i < selectedInd.length; i++) {
					selectedNames.add(model.getValueAt(table.convertRowIndexToModel(selectedInd[i]),
							table.getColumn(obj.getDatacol()).getModelIndex()).toString());
				}

				dataMap.put("Selected Sample Metadata", selectedNames);

				HashMap<String, Object> resultLog = new HashMap<String, Object>();

				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								// get data for all selceted cols
								try {

									if (table.getSelectedRows().length < 2) {
										JOptionPane.showMessageDialog(null, "Please select at least two rows",
												"Invalid selection", JOptionPane.ERROR_MESSAGE);

										resultLog.put("result", "Error");
										resultLog.put("resultComments", "Please select at least two rows");

										ActionProperties cosineSimilarityAction = new ActionProperties(
												"cosine-similarity-metadata-table", actionMap, null, resultLog,
												new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
										cosineSimilarityAction.logActionProperties();
										return;
									}
									/**
									 * get all the row data for each col map colnum to data array
									 */
									HashMap<Integer, double[]> databyCols = getDataForSelectedDataCols();
									if (databyCols == null) {
										return;
									}

									// compute similarity here
									ComputeRunsSimilarity ob = new ComputeRunsSimilarity(1, databyCols);
									HashMap<String, Double> res = ob.doComputation();
									// display res in JTable
									EventQueue.invokeLater(new Runnable() {
										@Override
										public void run() {
											try {
												SimilarityDisplayFrame frameob = new SimilarityDisplayFrame(res,
														"cosine similarity");

												frameob.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
														MetaOmGraph.getMainWindow().getHeight() / 2);
												frameob.setTitle("Cosine similarity for runs");
												MetaOmGraph.getDesktop().add(frameob);
												frameob.setVisible(true);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
								} catch (IOException | InterruptedException | ExecutionException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						});
						return null;
					}
				}.start();

				resultLog.put("result", "OK");

				ActionProperties cosineSimilarityAction = new ActionProperties("cosine-similarity-metadata-table",
						actionMap, dataMap, resultLog,
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				cosineSimilarityAction.logActionProperties();
			}
		});
		mnAnalyze.add(mntmCosineSililarity);

		JMenuItem mntmPearsonCorrelation = new JMenuItem("Pearson Correlation");
		mntmPearsonCorrelation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				/**
				 * select 2 or more runs and display the pearson correlation between them
				 */

				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				int[] selectedInd = table.getSelectedRows();
				List<String> selectedNames = new ArrayList<String>();
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = 0; i < selectedInd.length; i++) {
					selectedNames.add(model.getValueAt(table.convertRowIndexToModel(selectedInd[i]),
							table.getColumn(obj.getDatacol()).getModelIndex()).toString());
				}

				dataMap.put("Selected Sample Metadata", selectedNames);

				HashMap<String, Object> resultLog = new HashMap<String, Object>();

				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								// get data for all selceted cols
								try {
									/**
									 * get all the row data for each col map colnum to data array
									 */
									if (table.getSelectedRows().length < 2) {
										JOptionPane.showMessageDialog(null, "Please select at least two rows",
												"Invalid selection", JOptionPane.ERROR_MESSAGE);
										resultLog.put("result", "Error");
										resultLog.put("resultComments", "Please select at least two rows");

										return;
									}
									HashMap<Integer, double[]> databyCols = getDataForSelectedDataCols();
									if (databyCols == null) {
										return;
									}

									// compute correlation here 2 for correlation
									ComputeRunsSimilarity ob = new ComputeRunsSimilarity(2, databyCols);
									HashMap<String, Double> res = ob.doComputation();
									// display res in JTable
									// displaySimilarityTable(res, "Pearson's correlation");
									EventQueue.invokeLater(new Runnable() {
										@Override
										public void run() {
											try {
												SimilarityDisplayFrame frameob = new SimilarityDisplayFrame(res,
														"Pearson's correlation");

												frameob.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
														MetaOmGraph.getMainWindow().getHeight() / 2);
												frameob.setTitle("Pearson's correlation for runs");
												MetaOmGraph.getDesktop().add(frameob);
												frameob.setVisible(true);
												resultLog.put("result", "OK");
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
								} catch (IOException | InterruptedException | ExecutionException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
									resultLog.put("result", "Error");
								}
							}
						});
						return null;
					}
				}.start();

				ActionProperties pearsonCorrelationAction = new ActionProperties("pearson-correlation-metadata-table",
						actionMap, dataMap, resultLog,
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				pearsonCorrelationAction.logActionProperties();
			}
		});
		mnAnalyze.add(mntmPearsonCorrelation);

		JMenuItem mntmSpearmanCorrelation = new JMenuItem("Spearman Correlation");
		mntmSpearmanCorrelation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				/**
				 * select 2 or more runs and display the spearman correlation between them
				 */
				// Harsha - reproducibility log
				HashMap<String, Object> actionMap = new HashMap<String, Object>();
				actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Table");

				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				int[] selectedInd = table.getSelectedRows();
				List<String> selectedNames = new ArrayList<String>();
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = 0; i < selectedInd.length; i++) {
					selectedNames.add(model.getValueAt(table.convertRowIndexToModel(selectedInd[i]),
							table.getColumn(obj.getDatacol()).getModelIndex()).toString());
				}

				dataMap.put("Selected Sample Metadata", selectedNames);

				HashMap<String, Object> resultLog = new HashMap<String, Object>();

				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								// get data for all selceted cols
								try {
									/**
									 * get all the row data for each col map colnum to data array
									 */
									if (table.getSelectedRows().length < 2) {
										JOptionPane.showMessageDialog(null, "Please select at least two rows",
												"Invalid selection", JOptionPane.ERROR_MESSAGE);
										resultLog.put("result", "Error");
										resultLog.put("resultComments", "Please select at least two rows");

										return;
									}
									HashMap<Integer, double[]> databyCols = getDataForSelectedDataCols();
									if (databyCols == null) {
										return;
									}

									// compute correlation 3 for spearman correlation
									ComputeRunsSimilarity ob = new ComputeRunsSimilarity(3, databyCols);
									HashMap<String, Double> res = ob.doComputation();
									// display res in JTable
									// displaySimilarityTable(res, "Pearson's correlation");
									EventQueue.invokeLater(new Runnable() {
										@Override
										public void run() {
											try {
												SimilarityDisplayFrame frameob = new SimilarityDisplayFrame(res,
														"Spearman correlation");

												frameob.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
														MetaOmGraph.getMainWindow().getHeight() / 2);
												frameob.setTitle("Spearman correlation for runs");
												MetaOmGraph.getDesktop().add(frameob);
												frameob.setVisible(true);
												resultLog.put("result", "OK");

											} catch (Exception e) {
												e.printStackTrace();
												resultLog.put("result", "Error");
											}
										}
									});
								} catch (IOException | InterruptedException | ExecutionException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
									resultLog.put("result", "Error");
								}
							}
						});
						return null;
					}
				}.start();

				ActionProperties spearmanCorrelationAction = new ActionProperties("spearman-correlation-metadata-table",
						actionMap, dataMap, resultLog,
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				spearmanCorrelationAction.logActionProperties();
			}
		});
		mnAnalyze.add(mntmSpearmanCorrelation);
		
		JMenuItem pcaComputation = new JMenuItem("PCA");
		pcaComputation.setActionCommand("Compute PCA");
		pcaComputation.addActionListener(this);
		mnAnalyze.add(pcaComputation);
		
		JMenuItem tsneComputation = new JMenuItem("t-SNE");
		tsneComputation.setActionCommand("Compute TSNE");
		tsneComputation.addActionListener(this);
		mnAnalyze.add(tsneComputation);
		
		JMenuItem mntmSimple = new JMenuItem("Simple");
		mntmSimple.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * SimpleSearchDialog o= new SimpleSearchDialog();
				 * o.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); o.setVisible(true);
				 * o.setModalityType(Dialog.DEFAULT_MODALITY_TYPE); if(o.clicked) { String
				 * f=o.field; String v=o.toSearch; boolean e=o.exact;
				 * JOptionPane.showMessageDialog(null, "tos:"+v+"f:"+f); o.dispose(); }
				 */
				highlightedRows = null;
				highlightedRows = new ArrayList<>();

				SimpleSearchFrame frame = new SimpleSearchFrame(returnThisPanel());
				frame.setVisible(true);
				MetaOmGraph.getDesktop().add(frame);
				try {
					frame.setSelected(true);
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// @TODO comment temporarily until user manual is fully implemented.
		/*
		 * JMenu mnHelp = new JMenu("Help"); menuBar.add(mnHelp);
		 */

		return menuBar;
	}

	// Create the table to display the sample meta data.
	private void initTable() {
		toHighlight = new HashMap<Integer, List<String>>();
		// initialize with garbage value for alternate coloring to take effect via
		// prepareRenderer
		toHighlight.put(0, null);
		// List<String> ls = new ArrayList<String>();
		// ls.add("Exp1");
		// toHighlight.put(0, ls);
		// headers = obj.getHeaders();
		table = new StripedTable() {

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				// urmi enclose in try catch
				try {
					Component c = super.prepareRenderer(renderer, row, column);
					if (!isRowSelected(row)) {
						c.setBackground(getBackground());
						int modelRow = convertRowIndexToModel(row);

						if (highlightedRows != null && highlightedRows.contains(modelRow)) {
							if (!USEDEFAULTCOLORS)
								c.setBackground(HIGHLIGHTCOLOR);
						} else {
							if (row % 2 == 0) {
								if (!USEDEFAULTCOLORS) {
									c.setBackground(BCKGRNDCOLOR1);
								}
							} else {
								if (!USEDEFAULTCOLORS)
									c.setBackground(BCKGRNDCOLOR2);
							}
						}

					} else {
						if (!USEDEFAULTCOLORS)
							c.setBackground(SELECTIONBCKGRND);
					}

					return c;
				} catch (Exception ex) {

				}

				return null;

			}

		};

		// table mouse listener
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// only do if double click
				if (e.getClickCount() < 2) {
					return;
				}
				int row = table.convertRowIndexToModel(table.rowAtPoint(new Point(e.getX(), e.getY())));
				int col = table.convertColumnIndexToModel(table.columnAtPoint(new Point(e.getX(), e.getY())));

				// only do for columns with links
				if (col == gseColumn || col == gsmColumn || col == srrColumn || col == srpColumn || col == srxColumn
						|| col == srsColumn) {
					System.out.println(row + " " + col);
					String url = (String) table.getModel().getValueAt(row, col);
					System.out.println(url + " was clicked");

					// open url
					URI ns = null;
					try {
						if (col == srrColumn) {
							ns = new URI("https://trace.ncbi.nlm.nih.gov/Traces/sra/?run=" + url);
						}
						if (col == srsColumn) {
							ns = new URI("https://www.ncbi.nlm.nih.gov/sra/" + url);
						}
						if (col == srxColumn) {
							ns = new URI("https://www.ncbi.nlm.nih.gov/sra/" + url);
						}
						if (col == srpColumn) {
							ns = new URI("https://trace.ncbi.nlm.nih.gov/Traces/sra/?study=" + url);
						}
						if (col == gsmColumn) {
							ns = new URI("https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=" + url);
						} else if (col == gseColumn) {
							ns = new URI("https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=" + url);
						}

						java.awt.Desktop.getDesktop().browse(ns);
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				int col = table.columnAtPoint(new Point(e.getX(), e.getY()));

				if (col == srrColumn || col == srpColumn || col == srsColumn || col == srxColumn || col == gseColumn
						|| col == gsmColumn) {
					table.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}

			}

			@Override
			public void mouseExited(MouseEvent e) {
				int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
				if (!(col == srrColumn || col == srpColumn || col == srsColumn || col == srxColumn || col == gseColumn
						|| col == gsmColumn)) {
					table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

			}
		});
		
		// end mouse listner
		
		
		// Start column movement listner
		
		TableColumnModel tableColumnModel = table.getColumnModel();
		
		tableColumnModel.addColumnModelListener(new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
            }

            public void columnRemoved(TableColumnModelEvent e) {
            }

            public void columnMoved(TableColumnModelEvent e) {
            	MetaOmGraph.getActiveProject().setChanged(true);
            }

            public void columnMarginChanged(ChangeEvent e) {
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
            }
        });
		
		
		// End column movement listner

		table.getTableHeader().setReorderingAllowed(true);

		table.setModel(new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int col) {
				Class<?> returnValue;
				returnValue = Object.class;
				return returnValue;

			}

		});
		
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	            
	        	MetaOmGraph.getTaskBar().setNumSamples(getNumberofSamplesSelected());
	        }
	    });

		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// Add column names.
		for (int i = 0; i < headers.length; i++) {
			tablemodel.addColumn(headers[i]);
		}
		// add data
		// for each row add each coloumn
		for (int i = 0; i < metadata.size(); i++) {
			// create a temp string storing all col values for a row
			String[] temp = new String[headers.length];
			for (int j = 0; j < headers.length; j++) {
				temp[j] = metadata.get(i).get(headers[j]).toString();
			}

			// add ith row in table
			tablemodel.addRow(temp);

		}

		// add custom sorter
		TableRowSorter sorter = new TableRowSorter(tablemodel) {

			@Override
			public void toggleSortOrder(int column) {
				List<? extends SortKey> sortKeys = getSortKeys();
				if (sortKeys.size() > 0) {
					if (sortKeys.get(0).getSortOrder() == SortOrder.DESCENDING) {
						setSortKeys(null);
						return;
					}
				}
				super.toggleSortOrder(column);
			}

		};

		for (int i = 0; i < table.getColumnCount(); i++) {
			sorter.setComparator(i, new AlphanumericComparator());
		}
		table.setRowSorter(sorter);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		
		
		MetadataHeaderRenderer customHeaderCellRenderer = 
				new MetadataHeaderRenderer(Color.white,
						Color.red,
						new Font("Consolas",Font.BOLD,12),
						BorderFactory.createEtchedBorder(),
						true);
		
		int colindex = 0;
		
		for(int i=0; i<table.getColumnCount(); i++) {
			if(table.getColumnName(i).equals(obj.getDatacol())) {
				colindex = i;
			}
		}
		table.getColumnModel().getColumn(colindex).setHeaderRenderer(customHeaderCellRenderer);
		
		
		

	}

	public HashMap<Integer, List<String>> gettoHighlightMap() {
		return this.toHighlight;
	}

	public void settoHighlightMap(HashMap<Integer, List<String>> th) {
		this.toHighlight = th;
	}

	private boolean highlightThisRow(int col, String val) {
		// JOptionPane.showMessageDialog(null, "val:"+val+"col#:"+col);
		boolean result = false;
		if (toHighlight == null || toHighlight.size() < 1) {
			return false;
		}
		// search
		List<String> allVals = toHighlight.get(col);
		if (allVals == null) {
			return false;
		}
		if (allVals.contains(val)) {

			return true;
		}
		return result;
	}

	public MetadataTableDisplayPanel returnThisPanel() {
		return this;
	}

	public JTable getTable() {
		return this.table;
	}
	
	public StripedTable getStripedTable() {
		return this.table;
	}

	public MetadataTableDisplayPanel getThisPanel() {
		return this;
	}

	/**
	 * Update sample metadata table with the rows
	 * 
	 * @param rowsInList
	 * @param updateIncludeExclude set true to add the new table values to include and exclude all other.
	 */
	public void updateTable(List<String> rowsInList, boolean updateIncludeExlude) {

		
				DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
				tablemodel.setRowCount(0);
				tablemodel.setColumnCount(0);

				String[] activeListHeaders = getActiveListHeaders();
				// Add column names.
				for (int i = 0; i < activeListHeaders.length; i++) {
					tablemodel.addColumn(activeListHeaders[i]);
				}
			
				// urmi make faster
				// add rows
				List<Document> metadataSelectedRows = obj.getRowsByDatacols(rowsInList);
				for (int i = 0; i < metadataSelectedRows.size(); i++) {
					// create a temp string storing all col values for a row
					String[] temp = new String[headers.length];
					for (int j = 0; j < headers.length; j++) {
						temp[j] = metadataSelectedRows.get(i).get(headers[j]).toString();
					}
					// add ith row in table
					tablemodel.addRow(temp);
				}

				// add sorter
				TableRowSorter sorter = new TableRowSorter(tablemodel) {
					@Override
					public void toggleSortOrder(int column) {
						List<? extends SortKey> sortKeys = getSortKeys();
						if (sortKeys.size() > 0) {
							if (sortKeys.get(0).getSortOrder() == SortOrder.DESCENDING) {
								setSortKeys(null);
								return;
							}
						}
						super.toggleSortOrder(column);
					}
				};

				for (int i = 0; i < table.getColumnCount(); i++) {
					sorter.setComparator(i, new AlphanumericComparator());
				}
				table.setRowSorter(sorter);
				table.repaint();

		// Update excluded and included data according to the list selected.
		if(updateIncludeExlude) {
			Set<String> excluded = new HashSet<String>(getExcludedRowsFromTable(rowsInList));
			obj.setIncluded(rowsInList);
			obj.setExcluded(excluded);
			MetaOmAnalyzer.updateExcluded(excluded, true);
		}
		
		
		table.initializeVisibilityMap();
		table.hideColumns();
	}

	// get rows that are not included (Excluded).
	private ArrayList<String> getExcludedRowsFromTable(List<String> rowsInList) {
		List<String> allRows = obj.getAllDataCols();
		allRows.removeAll(rowsInList);
		ArrayList<String> excludedRowNames = new ArrayList<String>();
		excludedRowNames.addAll(allRows);
		return excludedRowNames;
	}

	/**
	 * @author urmi Update the table model after deleting cols
	 */
	public void updateTable() {
		updateTable(false);
	}

	public void updateTable(boolean colsChanged) {
		metadata = this.obj.getAllData();
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// clear table model
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		String[] activeListHeaders = getActiveListHeaders();
		// add column names.
		List<String> finalHeaders = new ArrayList<String>(Arrays.asList(activeListHeaders));
		if (obj.getRemoveCols() != null && !obj.getRemoveCols().isEmpty())
			finalHeaders.removeAll(obj.getRemoveCols());

		// add columns to table
		for (int i = 0; i < finalHeaders.size(); i++) {
			tablemodel.addColumn(finalHeaders.get(i));
		}

		new AnimatedSwingWorker("Updating table...", true) {
			@Override
			public Object construct() {
				// add rows
				for (int i = 0; i < metadata.size(); i++) {
					// create a temp string storing all col values for a row
					String[] temp = new String[finalHeaders.size()];
					for (int j = 0; j < finalHeaders.size(); j++) {
						temp[j] = metadata.get(i).get(finalHeaders.get(j)).toString();
					}
					// add ith row in table
					tablemodel.addRow(temp);
				}

				// add sorter
				TableRowSorter sorter = new TableRowSorter(tablemodel) {
					@Override
					public void toggleSortOrder(int column) {
						List<? extends SortKey> sortKeys = getSortKeys();
						if (sortKeys.size() > 0) {
							if (sortKeys.get(0).getSortOrder() == SortOrder.DESCENDING) {
								setSortKeys(null);
								return;
							}
						}
						super.toggleSortOrder(column);
					}

				};

				for (int i = 0; i < table.getColumnCount(); i++) {
					sorter.setComparator(i, new AlphanumericComparator());
				}
				table.setRowSorter(sorter);

				// if columns were changed
				if (colsChanged) {
					// format columns showing hyperlinks
					if (autoDetect) {
						detectSRAColumns();
					} else {
						if (srrColumn >= 0 || srpColumn >= 0 || srsColumn >= 0 || srxColumn >= 0 || gseColumn >= 0
								|| gsmColumn >= 0) {
							JOptionPane.showMessageDialog(null, "Hyperlinks to SRA and GEO were reset",
									"Hyperlinks removed", JOptionPane.INFORMATION_MESSAGE);
							removeHyperlinks();
						}
					}
				}

				table.repaint();
				return null;
			}

		}.start();

	}

	public void updateHeaders() {
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		tablemodel.setColumnIdentifiers(getActiveListHeaders());
		table.repaint();

	}

	private String[] getActiveListHeaders() {
		String activeList = getActiveList();
		if(listHeadersMap.containsKey(activeList))
			return listHeadersMap.get(activeList);
		else
			return headers;
	}

	public String getActiveList() {
		return sampleDataList.getSelectedValue().toString();
	}

	public void setHeaders(String[] newHeaders) {
		String activeList = getActiveList();
		listHeadersMap.replace(activeList, newHeaders);
		// this.headers = newHeaders;
	}

	public String[] getAllHeaders() {
		return this.headers;
	}

	public String[] getMetadataTableHeaders() {
		
		if(table != null) {
			int columns = table.getColumnModel().getColumnCount();
			String [] metadataColumns = new String[columns];
			
			for(int i=0; i<columns; i++) {
				metadataColumns[i] = (String)table.getColumnModel().getColumn(i).getHeaderValue();
			}
			
			return metadataColumns;
			
		}
		
		return null;
	}
	public void resetData() {
		this.metadata = this.obj.getAllData();
		this.headers = obj.getHeaders();
		setHeaders(headers);
	}

	public MetadataCollection getthisCollection() {
		return this.obj;
	}

	/**
	 * Return datacolumn of currently selected rows
	 * 
	 * @param invert:
	 *            if true, return rows not selected
	 * @return
	 */
	private List<String> getSelectedRows() {
		return getSelectedRows(false);
	}

	private List<String> getSelectedRows(boolean invert) {

		int[] selectedRows = table.getSelectedRows();

		List<String> selectedRowNames = new ArrayList<String>();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
			String rowDataColName = model.getValueAt(table.convertRowIndexToModel(selectedRows[rowIndex]),
					table.getColumn(obj.getDatacol()).getModelIndex()).toString();
			selectedRowNames.add(rowDataColName);
		}

		if (invert) {
			List<String> allRows = obj.getAllDataCols();
			allRows.removeAll(selectedRowNames);
			ArrayList<String> invertedSelection = new ArrayList<String>();
			invertedSelection.addAll(allRows);
			return invertedSelection;
		}

		return selectedRowNames;
	}

	/**
	 * @author urmi Exclude data columns in the selected rows
	 */
	public void filterSelectedRows(boolean invert) {
		int[] selected = table.getSelectedRows();
		if (selected.length < 1) {
			JOptionPane.showMessageDialog(null, "No rows are selected !!!");
			return;
		}

		Set<String> inc = obj.getIncluded();
		Set<String> exc = obj.getExcluded();
		List<String> removedList = new ArrayList<>();
		// get selected rows in table
		DefaultTableModel model = (DefaultTableModel) table.getModel();

		// JOptionPane.showMessageDialog(null, "selec:" + Arrays.toString(selected));
		if (invert) {
			table.selectAll();
			for (int lastSel : selected) {
				table.removeRowSelectionInterval(lastSel, lastSel);
			}
			selected = table.getSelectedRows();
		}

		// remove rows from included add to excluded
		Arrays.sort(selected);
		for (int i = selected.length - 1; i >= 0; i--) {
			// JOptionPane.showMessageDialog(null, "si:"+selected[i]);
			String temp = model.getValueAt(table.convertRowIndexToModel(selected[i]),
					table.getColumn(obj.getDatacol()).getModelIndex()).toString();
			// JOptionPane.showMessageDialog(null, "rem:"+temp);
			removedList.add(temp);
			exc.add(temp);
			inc.remove(temp);

			model.removeRow(table.convertRowIndexToModel(selected[i]));
		}

		obj.setExcluded(exc);
		obj.setIncluded(inc);
		// update exclude list
		MetaOmAnalyzer.updateExcluded(exc, true);
		// remove selected rows from search result
		removeFromtoHighlight(removedList);
		updateTable();

	}

	public void filterHighlightedRows(boolean invert) {

		if (highlightedRows == null || highlightedRows.size() < 1) {
			JOptionPane.showMessageDialog(null, "Nothing to remove", "Nothing to remove", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Set<String> inc = obj.getIncluded();
		Set<String> exc = obj.getExcluded();
		if (invert) {
			List<Integer> temp = new ArrayList<>();
			for (int i = 0; i < table.getRowCount(); i++) {
				if (!highlightedRows.contains(i)) {
					temp.add(i);
				}
			}
			highlightedRows = temp;
		}
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		// JOptionPane.showMessageDialog(null, "torem:" + highlightedRows.toString());
		// remove rows from excluded
		java.util.Collections.sort(highlightedRows, java.util.Collections.reverseOrder());
		for (int i = 0; i < highlightedRows.size(); i++) {
			// JOptionPane.showMessageDialog(null, "si:"+selected[i]);
			String temp = model.getValueAt(highlightedRows.get(i), table.getColumn(obj.getDatacol()).getModelIndex())
					.toString();
			// JOptionPane.showMessageDialog(null, "rem:"+temp);
			exc.add(temp);
			inc.remove(temp);
			model.removeRow(highlightedRows.get(i));

		}

		obj.setExcluded(exc);
		obj.setIncluded(inc);
		// update exclude list
		MetaOmAnalyzer.updateExcluded(exc, true);
		// after filtering
		updateTable();
		// clear last search
		toHighlight = new HashMap<Integer, List<String>>();
		// initialize with garbage value for alternate coloring to take effect via
		// prepareRenderer
		toHighlight.put(0, null);
		table.repaint();
		highlightedRows = null;

		HashMap<String, Object> actionMap = new HashMap<String, Object>();
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		HashMap<String, Object> result = new HashMap<String, Object>();

	}

	public void filterRows(List<String> s) {
		Set<String> inc = obj.getIncluded();
		Set<String> exc = obj.getExcluded();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		// JOptionPane.showMessageDialog(null, "To rem:" + s.toString());
		for (int i = 0; i < table.getRowCount(); i++) {
			String temp = model.getValueAt(i, table.getColumn(obj.getDatacol()).getModelIndex()).toString();
			if (s.contains(temp)) {
				exc.add(temp);
				inc.remove(temp);
				model.removeRow(i);
			}

		}

		obj.setExcluded(exc);
		obj.setIncluded(inc);
		// update exclude list
		MetaOmAnalyzer.updateExcluded(exc, true);

		HashMap<String, Object> actionMap = new HashMap<String, Object>();
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		HashMap<String, Object> result = new HashMap<String, Object>();

		
	}

	public void displaySimilarityTable(HashMap<String, Double> res) {
		displaySimilarityTable(res, "Value");
	}

	public void displaySimilarityTable(HashMap<String, Double> res, String nameMetric) {

		TaskbarInternalFrame intFrame = new TaskbarInternalFrame("Results");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		// DefaultTableModel model = new DefaultTableModel();

		DefaultTableModel model = new DefaultTableModel() {
			@Override
			// last column, third, is double
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				case 1:
					return String.class;
				case 2:
					return Double.class;
				default:
					return String.class;
				}
			}
		};
		JTable table = new JTable(model);
		// table properties
		table.setAutoCreateRowSorter(true);
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		// create model for table
		// Create a couple of columns
		model.addColumn("Var X");
		model.addColumn("Var Y");
		model.addColumn(nameMetric);
		// add rows to table
		Iterator it = res.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry) it.next();
			String[] vars = pair.getKey().toString().split(":");
			Vector newRow = new Vector<>();
			newRow.add(vars[0]);
			newRow.add(vars[1]);
			newRow.add(Double.valueOf(String.format("%.4g%n", Double.valueOf(pair.getValue().toString()))));
			model.addRow(newRow);
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			it.remove(); // avoids a ConcurrentModificationException
		}
		JScrollPane tableContainer = new JScrollPane(table);
		panel.add(tableContainer, BorderLayout.CENTER);
		intFrame.getContentPane().add(panel);
		intFrame.pack();
		intFrame.putClientProperty("JInternalFrame.frameType", "normal");
		intFrame.setResizable(true);
		intFrame.setMaximizable(true);
		intFrame.setIconifiable(true);
		intFrame.setClosable(true);
		FrameModel similarityResultsFrameModel = new FrameModel("Similarity Results", "Similarity Results", 21);
		intFrame.setModel(similarityResultsFrameModel);

		MetaOmGraph.getDesktop().add(intFrame);
		intFrame.setVisible(true);
		// intFrame.

	}

	public int guessIDtype(List<String> vals, String[] regexArr) {
		int[] scores = new int[regexArr.length];
		// for each regexArr search the values and keep the score
		for (int i = 0; i < regexArr.length; i++) {
			for (int j = 0; j < vals.size(); j++) {
				boolean thisMatch = vals.get(j).matches("(?i)" + regexArr[i]);
				if (thisMatch)
					scores[i]++;
			}
		}

		// return index of max score item
		// min score reqd
		int max = 5;
		int maxInd = -1;
		for (int i = 0; i < scores.length; i++) {
			if (scores[i] > max) {
				max = scores[i];
				maxInd = i;
			}
		}

		return maxInd;
	}

	/**
	 * @author urmi Move the rows to top which match values rowData in colnum colnum
	 *         should have unique row values
	 * @param rowData
	 * @param col
	 */
	public void moveRowsToTop(List<String> rowData, int colnum) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		List<Integer> rowNums = new ArrayList<>();
		// remove any sort order from table
		RowSorter rs = table.getRowSorter();
		rs.setSortKeys(null);
		// bring matched values at top
		String temp;
		for (int c = 0; c < model.getRowCount(); c++) {
			temp = model.getValueAt(table.convertRowIndexToView(c), colnum).toString();
			if (rowData.contains(temp)) {
				rowNums.add(table.convertRowIndexToView(c));
			}
		}
		// JOptionPane.showMessageDialog(null, "thisInd:" + rowNums.toString());
		// move the rows in rowNums to top
		// this changes the row order
		for (int c = 0; c < rowNums.size(); c++) {
			model.moveRow(rowNums.get(c), rowNums.get(c), c);
		}

	}

	public void formatHyperlinkColums() {
		if (srrColumn >= 0) {
			table.getColumn(table.getColumnName(srrColumn)).setCellRenderer(new hyperlinkColRen());
		}

		if (srpColumn >= 0) {
			// JOptionPane.showMessageDialog(null, "SRP");
			table.getColumn(table.getColumnName(srpColumn)).setCellRenderer(new hyperlinkColRen());
		}

		if (srxColumn >= 0) {
			table.getColumn(table.getColumnName(srxColumn)).setCellRenderer(new hyperlinkColRen());
		}

		if (srsColumn >= 0) {
			table.getColumn(table.getColumnName(srsColumn)).setCellRenderer(new hyperlinkColRen());

		}

		if (gseColumn >= 0) {
			table.getColumn(table.getColumnName(gseColumn)).setCellRenderer(new hyperlinkColRen());

		}

		if (gsmColumn >= 0) {
			table.getColumn(table.getColumnName(gsmColumn)).setCellRenderer(new hyperlinkColRen());
		}

		table.repaint();

	}

	public int getsrrColumn() {
		return this.srrColumn;
	}

	public int getsrpColumn() {
		return this.srpColumn;
	}

	public int getsrxColumn() {
		return this.srxColumn;
	}

	public int getsrsColumn() {
		return this.srsColumn;
	}

	public int getgseColumn() {
		return this.gseColumn;
	}

	public int getgsmColumn() {
		return this.gsmColumn;
	}

	public void setsrrColumn(int col) {
		this.srrColumn = col;
	}

	public void setsrpColumn(int col) {
		this.srpColumn = col;
	}

	public void setsrxColumn(int col) {
		this.srxColumn = col;
	}

	public void setsrsColumn(int col) {
		this.srsColumn = col;
	}

	public void setgseColumn(int col) {
		this.gseColumn = col;
	}

	public void setgsmColumn(int col) {
		this.gsmColumn = col;
	}

	/**
	 * remove selected columns from highlight/searched results
	 */
	public void removeFromtoHighlight(List<String> toRemove) {
		if (highlightedRows == null) {
			return;
		}

		// remove data column ids which were marked as highlighted
		int dataColNum = table.getColumn(obj.getDatacol()).getModelIndex();
		List<String> temp = toHighlight.get(dataColNum);
		for (String s : toRemove) {
			temp.remove(s);
		}
		toHighlight.put(dataColNum, temp);
		// reset highlightedRows it will be initialized by renderer
		highlightedRows = null;
		highlightedRows = new ArrayList<>();
		previousSearchedRows.clear();

	}

	// create column renderer class for hyperlinks
	class hyperlinkColRen extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == srrColumn || column == srpColumn || column == srxColumn || column == srsColumn
					|| column == gseColumn || column == gsmColumn) {
				if (isSelected) {
					if (!USEDEFAULTCOLORS)
						c.setForeground(HYPERLINKCOLOR);
				} else {
					c.setForeground(Color.BLUE);
				}

			}
			return c;
		}
	}

	/*
	 * public class AlphanumericComparator implements Comparator { public
	 * AlphanumericComparator() { }
	 * 
	 * public int compare(Object o1, Object o2) { String s1 = o1.toString(); String
	 * s2 = o2.toString(); final Double num1 = getDouble(s1); final Double num2 =
	 * getDouble(s2); if (num1 != null && num2 != null) { return
	 * num1.compareTo(num2); } return s1.compareTo(s2);
	 * 
	 * }
	 * 
	 * private Double getDouble(String number) { try { return
	 * Double.parseDouble(number); } catch (NumberFormatException e) { return null;
	 * } } }
	 */

	public int getNumberofSamplesSelected() {
		
		if(table != null) {
		return table.getSelectedRows().length;
		}
		
		return 0;
	}


	/**
	 * Search a datacol in table and select it. used to switch from tree.
	 * 
	 * @param val
	 */
	public void setSelectedRowWithValue(String val) {

		List<Integer> matchList = new ArrayList<>();
		for (int i = 0; i < table.getRowCount(); i++) {
			for (int j = 0; j < table.getColumnCount(); j++) {
				String thisVal = table.getValueAt(i, j).toString();
				if (thisVal.equals(val)) {
					matchList.add(i);
					break;
				}
			}
		}

		ListSelectionModel model = table.getSelectionModel();
		model.clearSelection();
		for (int i = 0; i < matchList.size(); i++) {
			model.addSelectionInterval(matchList.get(i), matchList.get(i));
			// table.setRowSelectionInterval();
		}

		// scroll table to selected index
		table.scrollRectToVisible(new Rectangle(table.getCellRect(matchList.get(matchList.size() - 1) + 5, 0, true)));
	}

	/**
	 * Function to automatically create hyperlinks to SRA and geo
	 */
	private void detectSRAColumns() {
		// search fisrt 50 rows and choose appropriate ids
		int maxSize = 500;
		if (maxSize > table.getRowCount())
			maxSize = table.getRowCount();
		String[] idRegExList = { "^[SED]RR\\d+", "^[SED]RP\\d+", "^[SED]RS\\d+", "^[SED]RX\\d+", "^[G]SE\\d+",
				"^[G]SM\\d+" };
		for (int j = 0; j < table.getColumnCount(); j++) {
			List<String> thisColvals = new ArrayList<>();
			for (int i = 0; i < maxSize; i++) {
				String thisVal = table.getValueAt(i, j).toString();
				if (thisVal.length() > 0)
					thisColvals.add(thisVal);
			}
			if (thisColvals.size() > 0) {
				// check if thisColvals contains any known Id
				int res = guessIDtype(thisColvals, idRegExList);
				if (res == 0) {
					srrColumn = j;
				} else if (res == 1) {
					srpColumn = j;
				} else if (res == 2) {
					srsColumn = j;
				} else if (res == 3) {
					srxColumn = j;
				} else if (res == 4) {
					gseColumn = j;
				} else if (res == 5) {
					gsmColumn = j;
				}
			}

		}

		// show selected colums
		String message = "The following ids were detected:";
		if (srrColumn >= 0) {
			message += "\n" + "SRR Id:" + table.getColumnName(srrColumn);
		}
		if (srpColumn >= 0) {
			message += "\n" + "SRP Id:" + table.getColumnName(srpColumn);
		}
		if (srxColumn >= 0) {
			message += "\n" + "SRX Id:" + table.getColumnName(srxColumn);
		}
		if (srsColumn >= 0) {
			message += "\n" + "SRS Id:" + table.getColumnName(srsColumn);
		}
		if (gseColumn >= 0) {
			message += "\n" + "GSE Id:" + table.getColumnName(gseColumn);
		}
		if (gsmColumn >= 0) {
			message += "\n" + "GSM Id:" + table.getColumnName(gsmColumn);
		}
		JOptionPane.showMessageDialog(null, message, "Automatic detection", JOptionPane.INFORMATION_MESSAGE);
		formatHyperlinkColums();
	}

	private void removeHyperlinks() {
		srrColumn = -1;
		srxColumn = -1;
		srpColumn = -1;
		srsColumn = -1;
		gseColumn = -1;
		gsmColumn = -1;
		// formatHyperlinkColums();
		updateTable();
	}

	public void updateColors() {
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
		table.repaint();
	}

	private HashMap<Integer, double[]> getDataForSelectedDataCols() throws IOException {
		String[] selectedNames = getSelectDataColsName();
		if (selectedNames == null) {
			return null;
		}
		// for the selected runs find their index in the data file and get data by the
		// index in data file
		int[] selectedIndinData = MetaOmGraph.getActiveProject().getColumnIndexbyHeader(selectedNames);
		final HashMap<Integer, double[]> databyCols;
		databyCols = MetaOmGraph.getActiveProject().getAllRowData(selectedIndinData);
		return databyCols;
	}

	private String[] getSelectDataColsName() {
		int[] selectedInd = table.getSelectedRows();
		if (selectedInd == null || selectedInd.length < 1) {
			JOptionPane.showMessageDialog(null, "Nothing selected", "Invalid selection", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		String[] selectedNames = new String[selectedInd.length];
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = 0; i < selectedInd.length; i++) {
			selectedNames[i] = model.getValueAt(table.convertRowIndexToModel(selectedInd[i]),
					table.getColumn(obj.getDatacol()).getModelIndex()).toString();
		}
		return selectedNames;
	}

	private void clearLastSearchedRows() {
		toHighlight = new HashMap<Integer, List<String>>();
		// initialize with garbage value for alternate coloring to take effect via
		// prepareRenderer
		toHighlight.put(0, null);
		highlightedRows = null;
		previousSearchedRows.clear();
		table.repaint();
	}

	public String selectColumn() {

		String[] items = obj.getHeaders();
		String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
				JOptionPane.PLAIN_MESSAGE, null, items, items[0]);

		return col_val;
	}

	/**
	 * get data under a column in metadata display table
	 * 
	 * @param col
	 * @return
	 */
	List<String> getSampleMetaData(String column) {
		List<String> result = new ArrayList<>();
		// add all values under the colName column
		for (int r = 0; r < table.getRowCount(); r++) {
			String thisVal = (String) table.getModel().getValueAt(r, table.getColumn(column).getModelIndex());
			if (thisVal != null) {
				result.add(thisVal);
			}
		}

		return result;
	}

	/**
	 * Plot frequency barchart with selected columns
	 * 
	 * @param colValue
	 */
	public void plotBarChart(String colValue) {

		// get data for the selected columns
		List<String> chartData = getSampleMetaData(colValue);

		BarChart f2 = new BarChart(MetaOmGraph.getActiveProject(), colValue, chartData, 2);
		MetaOmGraph.getDesktop().add(f2);
		f2.setDefaultCloseOperation(2);
		f2.setClosable(true);
		f2.setResizable(true);
		f2.pack();
		f2.setSize(1000, 700);
		f2.setVisible(true);
		f2.toFront();
	}

	private void createSampleListFrame(String title, boolean edit, List<String> selectedRows,
			List<String> notSelectedRows) {
		SampleMetaDataListFrame sampleListFrame = null;
		String[] activeHeaderList = getActiveListHeaders();
		if (edit) {
			sampleListFrame = new SampleMetaDataListFrame(obj, (String) sampleDataList.getSelectedValue(), selectedRows,
					notSelectedRows, activeHeaderList);
		} else {
			sampleListFrame = new SampleMetaDataListFrame(obj, selectedRows, notSelectedRows, activeHeaderList);

		}
		sampleListFrame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
				MetaOmGraph.getMainWindow().getHeight() / 2);
		sampleListFrame.setResizable(true);
		sampleListFrame.setMaximizable(true);
		sampleListFrame.setIconifiable(true);
		sampleListFrame.setClosable(true);
		sampleListFrame.setTitle(title);
		MetaOmGraph.getDesktop().add(sampleListFrame);
		sampleListFrame.setVisible(true);
		MetaOmGraph.getActiveProject().setChanged(true);
		MetaOmGraph.fixTitle();
	}

	private void deleteSelectedList() {
		List selected = sampleDataList.getSelectedValuesList();

		for (Object s : selected) {
			MetaOmGraph.getActiveProject().deleteSampleDataList(s.toString());
			listHeadersMap.remove(s.toString());
		}
	}

	private void createNewListFromSelectedRows() {
		prevLists.clear();
		prevLists.addAll(Arrays.asList(MetaOmGraph.getActiveProject().getSampleDataListNames()));
		createSampleListFrame("Create new sample list", false, getSelectedRows(), getSelectedRows(true));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("new list".equals(e.getActionCommand())) {
			createNewListFromSelectedRows();
			return;
		}
		if ("edit list".equals(e.getActionCommand())) {
			createSampleListFrame("Edit sample list", true, getSelectedRows(false), getSelectedRows(true));
			return;
		}
		if ("rename list".equals(e.getActionCommand())) {
			MetaOmGraph.getActiveProject().renameSampleDataList((String) sampleDataList.getSelectedValue(), null);
			return;
		}
		if ("delete list".equals(e.getActionCommand())) {
			int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
					"Are you sure you want to delete the selected lists '"
							+ sampleDataList.getSelectedValue().toString() + "'?",
					"Confirm", 0, 3);
			if (result == 0)
				deleteSelectedList();
			return;
		}
		if ("Create List with Last Searched Rows".equals(e.getActionCommand())) {
			if (previousSearchedRows.isEmpty())
				return;
			prevLists.clear();
			prevLists.addAll(Arrays.asList(MetaOmGraph.getActiveProject().getSampleDataListNames()));
			List<String> allRows = obj.getAllDataCols();
			allRows.removeAll(previousSearchedRows);
			ArrayList<String> notIncludedInSearch = new ArrayList<String>();
			notIncludedInSearch.addAll(allRows);
			createSampleListFrame("Create new sample list", false, previousSearchedRows, notIncludedInSearch);
			return;
		}
		if ("Create List with Selected Rows".equals(e.getActionCommand())) {
			createNewListFromSelectedRows();
			return;
		}
		if("Compute PCA".equals(e.getActionCommand())) {
			String[] selectedDataCols = getSelectDataColsName();
			if (selectedDataCols == null) {
				return;
			}
			
			String[] geneList = MetaOmGraph.getActiveProject().getGeneListNames(); 
			createPCAPanel(geneList);
			if(selectedGeneListDimRed == null || selectedGeneListDimRed.isEmpty())
				return;
			String tempGeneList = selectedGeneListDimRed;
			new AnimatedSwingWorker("Computing PCA...", true) {
				@Override
				public Object construct() {
					computePCA(selectedDataCols, selectedGeneListDimRed, normalizeData);
					return null;
				}
			}.start();
			
			// Logging
			HashMap<String, Object> actionMap = new HashMap<String, Object>();
			actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
			actionMap.put("section", "Sample Metadata Table");

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			
			dataMap.put("Selected samples", Arrays.asList(selectedDataCols));
			dataMap.put("Selected feature list", tempGeneList);
			dataMap.put("Normalization", normalizeData);

			HashMap<String, Object> resultLog = new HashMap<String, Object>();
			resultLog.put("result", "OK");

			ActionProperties computePCAAction = new ActionProperties("PCA", actionMap,
					dataMap, resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			computePCAAction.logActionProperties();		
			return;
		}
		
		if("Compute TSNE".equals(e.getActionCommand())) {
			String[] selectedDataCols = getSelectDataColsName();
			if (selectedDataCols == null) {
				return;
			}
			
			String[] geneList = MetaOmGraph.getActiveProject().getGeneListNames(); 
			createTSNEPanel(geneList);
			if(selectedGeneListDimRed == null || selectedGeneListDimRed.isEmpty())
				return;
			
			String tempGeneList = selectedGeneListDimRed;
			new AnimatedSwingWorker("Computing t-SNE...", true) {
				@Override
				public Object construct() {
					computeTSNE(selectedDataCols, selectedGeneListDimRed, perplexity, maxIter, theta, usePCA, parallel);
					return false;
				}
			}.start();
			
			// Logging
			HashMap<String, Object> actionMap = new HashMap<String, Object>();
			actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
			actionMap.put("section", "Sample Metadata Table");

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			
			dataMap.put("Selected samples", Arrays.asList(selectedDataCols));
			dataMap.put("Selected feature list", tempGeneList);
			dataMap.put("Perplexity", perplexity);
			dataMap.put("MaxIter", maxIter);
			dataMap.put("Theta", theta);
			dataMap.put("UsePCA", usePCA);
			dataMap.put("Parallel", parallel);

			HashMap<String, Object> resultLog = new HashMap<String, Object>();
			resultLog.put("result", "OK");

			ActionProperties computePCAAction = new ActionProperties("t-SNE", actionMap,
					dataMap, resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			computePCAAction.logActionProperties();		
			return;
		}

	}
	
	/**
	 * Compute PCA and display the scatter plot
	 * @param selectedDataCols selected samples
	 * @param selectedGeneList selected gene list
	 * @param normalizeData whether to normalize the data when calculating pca
	 */
	public void computePCA(String[] selectedDataCols, String selectedGeneList, boolean normalizeData) {
		// for the selected runs find their index in the data file and get data by the
		// index in data file
		try {
			int[] selectedIndinData = MetaOmGraph.getActiveProject().getColumnIndexbyHeader(selectedDataCols);
			double[][] data = MetaOmGraph.getActiveProject().
					getSelectedListOnlyRowData(selectedIndinData, selectedGeneList);
			if (data == null) {
				return;
			}
			
			
			int rows = data.length;
			int cols = data[0].length;
			try {
				BufferedWriter br = new BufferedWriter(new FileWriter("/Users/harshavk/Documents/Harsha/Fall2021/Thesis/Human-Cancer-Prediction/data-to-pca.csv"));
				StringBuilder sb = new StringBuilder();


				for(int i=0; i<rows; i++) {
					for(int j=0; j<cols; j++) {
//						System.out.print(plotData[i][j]+ " ");
						sb.append(data[i][j]);
						if(j != cols-1)
							sb.append(",");
					}
					sb.append("\n");
//					System.out.println();
				}



				br.write(sb.toString());
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			ComputePCA pca = new ComputePCA(data);
			double[][] pcaData = pca.projectData(numDims, normalizeData);
			double[][] transposedData = Utils.getTransposeMatrix(pcaData);
			String[] rowNames = new String[transposedData.length];
			for(int i = 0; i < rowNames.length; i++) {
				rowNames[i] = "PCA" + Integer.toString(i+1);
			}
			ScatterPlotDimReduction pcaScatterPlotFrame = new 
					ScatterPlotDimReduction(transposedData, rowNames, selectedDataCols, 
							"", "", false);
			MetaOmGraph.getDesktop().add(pcaScatterPlotFrame);
			pcaScatterPlotFrame.setDefaultCloseOperation(2);
			pcaScatterPlotFrame.setClosable(true);
			pcaScatterPlotFrame.setResizable(true);
			pcaScatterPlotFrame.pack();
			pcaScatterPlotFrame.setSize(1000, 700);
			pcaScatterPlotFrame.setVisible(true);
			pcaScatterPlotFrame.toFront();
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Compute TSNE and display the scatter plot
	 * @param selectedDataCols selected samples
	 * @param selectedGeneList selected gene list
	 */
	public void computeTSNE(String[] selectedDataCols, String selectedGeneList, 
			double perplexity, int maxIter, double theta, boolean usePCA, boolean parallel) {
		if(selectedGeneList == null) {
			return;
		}
		// for the selected runs find their index in the data file and get data by the
		// index in data file
		try {
			int[] selectedIndinData = MetaOmGraph.getActiveProject().getColumnIndexbyHeader(selectedDataCols);
			double[][] data = MetaOmGraph.getActiveProject().
					getSelectedListOnlyRowData(selectedIndinData, selectedGeneList);
			if (data == null) {
				return;
			}
			ComputeTSNE tsne = ComputeTSNE.getTsneInstance(data, perplexity, maxIter, theta, numDims, usePCA, parallel);
			double[][] tsneData = tsne.projectData();
			// clear tsne after the projected data is returned
			ComputeTSNE.abortTsne();
			if(tsneData == null) {
				return;
			}
			double[][] transposedData = Utils.getTransposeMatrix(tsneData);
			String[] rowNames = new String[transposedData.length];
			for(int i = 0; i < rowNames.length; i++) {
				rowNames[i] = "t-SNE" + Integer.toString(i+1);
			}
			ScatterPlotDimReduction pcaScatterPlotFrame = new 
					ScatterPlotDimReduction(transposedData, rowNames, selectedDataCols, 
							"", "", false);
			MetaOmGraph.getDesktop().add(pcaScatterPlotFrame);
			pcaScatterPlotFrame.setDefaultCloseOperation(2);
			pcaScatterPlotFrame.setClosable(true);
			pcaScatterPlotFrame.setResizable(true);
			pcaScatterPlotFrame.pack();
			pcaScatterPlotFrame.setSize(1000, 700);
			pcaScatterPlotFrame.setVisible(true);
			pcaScatterPlotFrame.toFront();
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void createTSNEPanel(String[] geneList) {
		JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setTitle("Compute t-SNE");
		dialog.setLocationRelativeTo(MetaOmGraph.getDesktop());
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
	    constraints.anchor = GridBagConstraints.WEST;
	    constraints.insets = new Insets(10, 10, 10, 10);
	        
	    constraints.gridx = 0;
        constraints.gridy = 0;     
        JLabel geneListLabel = new JLabel("Gene List : ");
        dialog.add(geneListLabel, constraints);
        
        constraints.gridx = 1;
        JComboBox<String> geneListComboBox = new JComboBox<String>(geneList);
        geneListComboBox.setMaximumSize(geneListComboBox.getPreferredSize());
        geneListComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.add(geneListComboBox, constraints);
                
        constraints.gridx = 0;
        constraints.gridy = 1;
        JLabel perplexityLabel = new JLabel("Perplexity :");
        dialog.add(perplexityLabel, constraints);
        
        constraints.gridx = 1;
        JTextField perplexityTextField = new JTextField("30");
        dialog.add(perplexityTextField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel maxIterLabel = new JLabel("Maximum Iterations :");
        dialog.add(maxIterLabel, constraints);
        
        constraints.gridx = 1;
        JTextField maxIterTextField = new JTextField("1000");
        dialog.add(maxIterTextField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        JLabel thetaLabel = new JLabel("Theta :");
        dialog.add(thetaLabel, constraints);
        
        constraints.gridx = 1;
        JTextField thetaTextField = new JTextField("0.5");
        dialog.add(thetaTextField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 5;
        JLabel runParallelLabel = new JLabel("Run in parallel :");
        dialog.add(runParallelLabel, constraints);
        
        constraints.gridx = 1;
        JCheckBox runParallelCheckBox = new JCheckBox();
        runParallelCheckBox.setSelected(true);
        dialog.add(runParallelCheckBox, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 4;
        JLabel usePCALabel = new JLabel("Use PCA :");
        dialog.add(usePCALabel, constraints);
        
        constraints.gridx = 1;
        JCheckBox usePCACheckBox = new JCheckBox();
        usePCACheckBox.setSelected(true);
        dialog.add(usePCACheckBox, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 6;
        JButton okButton = new JButton("Ok");
        dialog.add(okButton, constraints);
        
        constraints.gridx = 1;
        JButton cancelButton = new JButton("Cancel");
        dialog.add(cancelButton, constraints);
        
        dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent we) {
				selectedGeneListDimRed = null;
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parallel = runParallelCheckBox.isSelected();
				usePCA = usePCACheckBox.isSelected();
				try {
					perplexity = Double.parseDouble(perplexityTextField.getText());
					maxIter = Integer.parseInt(maxIterTextField.getText());
					theta = Double.parseDouble(thetaTextField.getText());
				}
				catch(Exception e1) {
					JOptionPane.showMessageDialog(dialog, "Please check the values", "Improper values", JOptionPane.ERROR_MESSAGE);
				}
				selectedGeneListDimRed = (String) geneListComboBox.getSelectedItem();
				dialog.dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedGeneListDimRed = null;
				dialog.dispose();
			}
		});
		
        dialog.setResizable(true);
        dialog.pack();
        dialog.setVisible(true);
        dialog.toFront();
	}
	
	private void createPCAPanel(String[] geneList) {
		JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setTitle("Compute PCA");
		dialog.setLocationRelativeTo(MetaOmGraph.getDesktop());
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
	    constraints.anchor = GridBagConstraints.WEST;
	    constraints.insets = new Insets(10, 10, 10, 10);
	        
	    constraints.gridx = 0;
        constraints.gridy = 0;     
        JLabel geneListLabel = new JLabel("Gene List : ");
        dialog.add(geneListLabel, constraints);
        
        constraints.gridx = 1;
        JComboBox<String> geneListComboBox = new JComboBox<String>(geneList);
        geneListComboBox.setMaximumSize(geneListComboBox.getPreferredSize());
        geneListComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.add(geneListComboBox, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        JCheckBox normalizeDataCheckBox = new JCheckBox(" Normalize data (Mean center)");
        dialog.add(normalizeDataCheckBox, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 2;
        JButton okButton = new JButton("Ok");
        dialog.add(okButton, constraints);
        
        constraints.gridx = 1;
        JButton cancelButton = new JButton("Cancel");
        dialog.add(cancelButton, constraints);
        
        dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent we) {
				selectedGeneListDimRed = null;
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				normalizeData = normalizeDataCheckBox.isSelected();
				selectedGeneListDimRed = (String) geneListComboBox.getSelectedItem();
				dialog.dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedGeneListDimRed = null;
				dialog.dispose();
			}
		});
		
        dialog.setResizable(true);
        dialog.pack();
        dialog.setVisible(true);
        dialog.toFront();
	}

	private void updateLists() {
		String[] listNames = MetaOmGraph.getActiveProject().getSampleDataListNames();
		Arrays.sort(listNames, new ListNameComparator());
		sampleDataList = new JList(listNames);
		sampleDataList.setSelectionMode(0);

		sampleDataList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				String selectedRowName = (String) sampleDataList.getSelectedValue();
				List<String> values = MetaOmGraph.getActiveProject().getSampleDataListRowNames(selectedRowName);
				// Double click to update include and exclude.
				if(e.getClickCount() > 1) {
					updateTable(values, true);
				}
			}
			
		});
		
		sampleDataList.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				JList l = (JList) e.getSource();
				ListModel m = l.getModel();
				int index = l.locationToIndex(e.getPoint());
				if (index > -1) {
					// create tooltip
					String thisListName = m.getElementAt(index).toString();
					int numElements = MetaOmGraph.getActiveProject().getSampleDataListRowNames(thisListName).size();
					l.setToolTipText(thisListName + ":" + numElements + " Elements\n" + "Single click to view the list; Double click to make it active");
				}
			}
			
		});
		
		sampleDataList.addListSelectionListener(this);
	}

	private String getNewlyCreatedList() {
		ArrayList<String> newestSampleList = new ArrayList<>(
				Arrays.asList(MetaOmGraph.getActiveProject().getSampleDataListNames()));
		newestSampleList.removeAll(prevLists);
		if (newestSampleList.size() == 0)
			return null;
		else
			return newestSampleList.get(0);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if ("create sample data list".equals(e.getSource()) || "rename sample data list".equals(e.getSource())) {
			int selectedListIndex = sampleDataList.getSelectedIndex();
			String newList = getNewlyCreatedList();
			if (newList != null && !newList.isEmpty()) {
				String[] newListHeaders = listHeadersMap.get(sampleDataList.getSelectedValue().toString());
				listHeadersMap.put(newList, newListHeaders);
			}
			updateLists();
			sampleDataList.setSelectedIndex(selectedListIndex);
		} else if ("delete sample data list".equals(e.getSource())) {
			updateLists();
			sampleDataList.setSelectedIndex(0);
		}
		sampleDataListScrollPane.setViewportView(sampleDataList);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		String selectedRowName = (String) sampleDataList.getSelectedValue();
		List<String> values = MetaOmGraph.getActiveProject().getSampleDataListRowNames(selectedRowName);
		updateTable(values, false);
		if (highlightedRows != null)
			highlightedRows.clear();
		if (sampleDataList.getSelectedIndex() != 0) {
			listDeleteButton.setEnabled(true);
			listEditButton.setEnabled(true);
			listRenameButton.setEnabled(true);
		} else {
			listDeleteButton.setEnabled(false);
			listEditButton.setEnabled(false);
			listRenameButton.setEnabled(false);
		}
	}

	/**
	 * Listname comparator class
	 * 
	 * @author sumanth
	 *
	 */
	public class ListNameComparator implements Comparator<String> {
		public ListNameComparator() {
		}

		@Override
		public int compare(String o1, String o2) {
			if ((!(o1 instanceof String)) || (!(o2 instanceof String)))
				return 0;

			if (o1.equals("Complete List"))
				return -1;
			if (o2.equals("Complete List"))
				return 1;
			return o1.toLowerCase().compareTo(o2.toLowerCase());
		}
	}
}
