package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.JToolBar.Separator;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.logging.log4j.Logger;

import edu.iastate.metnet.metaomgraph.AdjustPval;
import edu.iastate.metnet.metaomgraph.DEAHeaderRenderer;
import edu.iastate.metnet.metaomgraph.DecimalFormatRenderer;
import edu.iastate.metnet.metaomgraph.DifferentialExpResults;
import edu.iastate.metnet.metaomgraph.FilterableTableModel;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.TableSorter;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.chart.BoxPlot;
import edu.iastate.metnet.metaomgraph.chart.HistogramChart;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.chart.ScatterPlotChart;
import edu.iastate.metnet.metaomgraph.chart.VolcanoPlot;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.throbber.MetaOmThrobber;
import edu.iastate.metnet.metaomgraph.throbber.MultiFrameImageThrobber;
import edu.iastate.metnet.metaomgraph.throbber.Throbber;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel.ListNameComparator;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public class logFCResultsFrame extends JPanel {

	private StripedTable table;
	private JList geneLists;
	private JPanel listPanel;
	private JToolBar dataToolbar;
	private JToolBar listToolbar;
	private JButton listDeleteButton;
	private JButton listEditButton;
	private JButton listCreateButton;
	private JButton listRenameButton;
	private List<String> featureNames;
	private List<Double> mean1;
	private List<Double> mean2;
	private List<Double> testPvals;
	private List<Double> ftestPvals;
	private List<Double> ftestRatiovals;
	private List<Double> testadjutestPvals;
	private List<Double> ftestadjutestPvals;
	private MetaOmProject myProject;
	private FilterableTableModel filterModel;
	private ClearableTextField filterField;
	private JButton listFromFilterButton;
	private JButton advFilterButton;
	private Throbber throbber;
	private TableSorter sorter;
	String name1;
	String name2;
	String methodName;
	String pvAdjMethod;
	logFCResultsFrame currentPanel;
	private NoneditableTableModel mainModel;

	private Object[][] featureMetadataColumnData;
	private String[] featureMetadataColumnNames;
	private Object[][] featureMetadataAllData;
	private Object[][] masterFeatureMetadataAllData;
	private List<String> allColumnNames;

	double pvThresh = 2;

	private Object[][] currentTableData;
	private LinkedHashMap<String,Object[]> currentTableDataMap;

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

	public Object[][] getCurrentTableData() {
		return currentTableData;
	}

	public void setCurrentTableData(Object[][] currentTableData) {
		this.currentTableData = currentTableData;
	}

	public Object[][] getFeatureMetadataColumnData() {
		return featureMetadataColumnData;
	}

	public void setFeatureMetadataColumnData(Object[][] featureMetadataColumnData) {
		this.featureMetadataColumnData = featureMetadataColumnData;
	}

	public String[] getFeatureMetadataColumnNames() {
		return featureMetadataColumnNames;
	}

	public void setFeatureMetadataColumnNames(String[] featureMetadataColumnNames) {
		this.featureMetadataColumnNames = featureMetadataColumnNames;
	}

	public Object[][] getFeatureMetadataAllData() {
		return featureMetadataAllData;
	}
	public void setFeatureMetadataAllData(Object[][] featureMetadataAllData) {
		this.featureMetadataAllData = featureMetadataAllData;
	}
	
	public Object[][] getMasterFeatureMetadataAllData() {
		return masterFeatureMetadataAllData;
	}

	public void setMasterFeatureMetadataAllData(Object[][] masterFeatureMetadataAllData) {
		this.masterFeatureMetadataAllData = masterFeatureMetadataAllData;
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

			}
		});
	}

	/**
	 * Create the frame.
	 */
	public logFCResultsFrame() {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	public logFCResultsFrame(List<String> featureNames, List<Double> mean1, List<Double> mean2,
			MetaOmProject myProject) {
		this(featureNames, mean1, mean2, null, null, null, null, null, null, myProject);
	}

	public logFCResultsFrame(DifferentialExpResults ob, MetaOmProject myProject) {
		this(ob.getRowNames(), ob.getMeanGrp1(), ob.getMeanGrp2(), ob.getGrp1Name(), ob.getGrp2Name(),
				ob.getmethodName(), ob.getPVal(), ob.getfStat(), ob.getFPVal(), myProject);
	}

	public logFCResultsFrame(List<String> featureNames, List<Double> mean1, List<Double> mean2, String name1,
			String name2, String methodName, List<Double> pv, List<Double> ftestratio, List<Double> ftestpv,
			MetaOmProject myProject) {
		this.name1 = name1;
		this.name2 = name2;
		this.methodName = methodName;
		this.featureNames = featureNames;
		this.mean1 = mean1;
		this.mean2 = mean2;
		this.myProject = myProject;
		testPvals = pv;
		ftestRatiovals = ftestratio;
		ftestPvals = ftestpv;

		currentPanel = this;
		// compute adjusted pv
		if (testPvals != null) {
			testadjutestPvals = AdjustPval.computeAdjPV(testPvals, pvAdjMethod);
		}
		if (ftestPvals != null) {

			ftestadjutestPvals = AdjustPval.computeAdjPV(ftestPvals, pvAdjMethod);
		}

		//		setBounds(100, 100, 450, 300);
		//		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout(0, 0));



		listPanel = new JPanel(new BorderLayout());

		String[] listNames = myProject.getGeneListNames();
		String [] listNames2 = new String[listNames.length+1];
		
		Arrays.sort(listNames, MetaOmGraph.getActiveTablePanel().new ListNameComparator());
		
		int i=0;
		listNames2[0] = "Current Result";
		for(i=1;i<=listNames.length;i++) {
			listNames2[i] = listNames[i-1];
		}
		
		
		geneLists = new JList(listNames2);
		geneLists.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		geneLists.setSelectedIndex(0);

		dataToolbar = new JToolBar();
		dataToolbar.setFloatable(false);
		listToolbar = new JToolBar();
		listToolbar.setFloatable(false);
		IconTheme theme = MetaOmGraph.getIconTheme();
		listDeleteButton = new JButton(theme.getListDelete());
		listDeleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
						"Are you sure you want to delete the selected lists '" + geneLists.getSelectedValue().toString()
						+ "'?",
						"Confirm", 0, 3);
				if (result == 0)
					MetaOmGraph.getActiveTablePanel().deleteSelectedList((List<String>)geneLists.getSelectedValuesList());
				return;
			}
		});
		listDeleteButton.setToolTipText("Delete the selected list");

		listEditButton = new JButton(theme.getListEdit());
		listEditButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				CreateListFrameDEA clf = new CreateListFrameDEA(myProject, (String) geneLists.getSelectedValue(), currentPanel);
				clf.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
				clf.setResizable(true);
				clf.setMaximizable(true);
				clf.setIconifiable(true);
				clf.setClosable(true);
				clf.setTitle("Edit List");

				FrameModel editListFrameModel = new FrameModel("List","Edit List",25);
				clf.setModel(editListFrameModel);

				MetaOmGraph.getDesktop().add(clf);
				clf.setVisible(true);
				return;
			}
		});
		listEditButton.setToolTipText("Edit the selected list");
		listRenameButton = new JButton(theme.getListRename());
		listRenameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				myProject.renameGeneList(geneLists.getSelectedValue() + "", null);
				return;
			}
		});
		listRenameButton.setToolTipText("Rename the selected list");

		listCreateButton = new JButton(theme.getListAdd());
		//listCreateButton.addActionListener(this);
		listCreateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				CreateListFrameDEA clf = new CreateListFrameDEA(myProject, null, currentPanel);

				clf.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
				clf.setResizable(true);
				clf.setMaximizable(true);
				clf.setIconifiable(true);
				clf.setClosable(true);
				clf.setTitle("Create New List");

				FrameModel createListFrameModel = new FrameModel("List","Create List",25);
				clf.setModel(createListFrameModel);

				MetaOmGraph.getDesktop().add(clf);
				clf.setVisible(true);
				return;
			}
		});
		listCreateButton.setActionCommand("new list");
		listCreateButton.setToolTipText("Create a new list");
		listToolbar.add(listCreateButton);
		listToolbar.add(listEditButton);
		listToolbar.add(listRenameButton);
		listToolbar.add(listDeleteButton);



		JPanel geneListPanel = new JPanel(new BorderLayout());
		JScrollPane geneListScrollPane = new JScrollPane(geneLists);
		geneListPanel.add(listToolbar, "First");
		geneListPanel.add(geneListScrollPane, "Center");
		Border loweredetched = BorderFactory.createEtchedBorder();
		geneListPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "Lists"));
		initTableModel();
		updateTable();


		geneLists.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {

					if(geneLists.getSelectedValue() == "Current Result") {
						updateTableRows(getCurrentTableData());
						listDeleteButton.setEnabled(false);
						listEditButton.setEnabled(false);
						listRenameButton.setEnabled(false);
					}
					else {
						
						if(geneLists.getSelectedValue() == "Complete List") {
							listDeleteButton.setEnabled(false);
							listEditButton.setEnabled(false);
							listRenameButton.setEnabled(false);
						}
						else {
							listDeleteButton.setEnabled(true);
							listEditButton.setEnabled(true);
							listRenameButton.setEnabled(true);
						}
						int[] entries = myProject.getGeneListRowNumbers((String)geneLists.getSelectedValue());
						String[] defaultRowNames = myProject.getDefaultRowNames(entries);
						if(defaultRowNames != null) {
							//Arrays.sort(defaultRowNames,new NaturalOrderComparator<String>(true));
							Object[][] selectionData = new Object[entries.length][getCurrentTableData().length];

							for(int i=0; i<defaultRowNames.length; i++) {
								selectionData[i] = currentTableDataMap.get((String)defaultRowNames[i]);
								
							}

							if(defaultRowNames.length == 0) {
								
								Map.Entry<String,Object[]> entry = currentTableDataMap.entrySet().iterator().next();
								 String key = entry.getKey();
								 Object[] value = entry.getValue();
								 Object[] dummy = new Object[value.length];
								 for(int j=0;j<value.length;j++) {
									 dummy[j] = 0;
								 }
								 Object[][] dummy2 = new Object[1][value.length];
								 dummy2[0] = dummy;
								 
								 updateTableRows(dummy2);
							}
							else {
								updateTableRows(selectionData);
							}
						}
					}

				}
			}
		});

		geneLists.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JList l = (JList) e.getSource();
				ListModel m = l.getModel();
				int index = l.locationToIndex(e.getPoint());
				if (index > 0) {
					// create tooltip
					String thisListName = m.getElementAt(index).toString();
					int numElements = myProject.getGeneListRowNumbers(thisListName).length;
					l.setToolTipText(thisListName + ":" + numElements + " Elements");
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setViewportView(table);
		geneListPanel.setMinimumSize(listToolbar.getPreferredSize());
		JSplitPane listSplitPane = new JSplitPane(1, true, geneListPanel, scrollPane);
		listSplitPane.setDividerSize(1);
		listPanel.add(dataToolbar, "First");
		listPanel.add(listSplitPane, "Center");

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.NORTH);

		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));


		add(listPanel);


		table.setAutoResizeMode(0);

		JMenuBar menuBar = new JMenuBar();
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel_1.add(menuBar);


		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmSave = new JMenuItem("Save to file");
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTabletofile(table, "LogFCResults Frame");
			}
		});
		mnFile.add(mntmSave);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmExportSelectedTo = new JMenuItem("Export selected to list");
		mntmExportSelectedTo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get selected rowindex
				int[] rowIndices = getSelectedRowIndices();
				if (rowIndices == null || rowIndices.length == 0) {
					JOptionPane.showMessageDialog(null, "No rows selected", "Nothing selected",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				String listName = JOptionPane.showInputDialog(logFCResultsFrame.this, "Enter a name for new list");
				if (listName == null || listName.length() < 1) {
					JOptionPane.showMessageDialog(logFCResultsFrame.this, "Invalid name", "Failed",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (myProject.addGeneList(listName, rowIndices, true, false)) {

					try {
						//Harsha - reproducibility log
						HashMap<String,Object> actionMap = new HashMap<String,Object>();
						actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
						actionMap.put("section", "Feature Metadata");

						HashMap<String,Object> dataMap = new HashMap<String,Object>();
						dataMap.put("Exported List Name", listName);
						dataMap.put("List Elements Count", rowIndices.length);
						Map<Integer,String> selectedItems = new HashMap<Integer,String>();

						for(int rowNum: rowIndices) {
							selectedItems.put(rowNum, myProject.getDefaultRowNames(rowNum));
						}
						dataMap.put("Selected Rows", selectedItems);
						HashMap<String,Object> resultLog = new HashMap<String,Object>();
						resultLog.put("result", "OK");

						ActionProperties mergeListAction = new ActionProperties("export-list",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
						mergeListAction.logActionProperties();
					}
					catch(Exception e1) {

					}
					JOptionPane.showMessageDialog(logFCResultsFrame.this, "List" + listName + " added", "List added",
							JOptionPane.INFORMATION_MESSAGE);
				}
				return;
			}
		});
		mnEdit.add(mntmExportSelectedTo);

		JMenuItem mntmFilter = new JMenuItem("P-value filter");
		mntmFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double pvalThresh = 0;
				try {
					String input = (String) JOptionPane.showInputDialog(null, "Please Enter a value", "Input p-value",
							JOptionPane.QUESTION_MESSAGE, null, null, String.valueOf(pvThresh));
					if (input == null) {
						return;
					}
					pvalThresh = Double.parseDouble(input);

				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Invalid number entered. Please try again.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				pvThresh = pvalThresh;

				updateTable();

				// JOptionPane.showMessageDialog(null, "Done");

			}
		});
		mnEdit.add(mntmFilter);

		JMenuItem mntmPvalueCorrection = new JMenuItem("P-value correction");
		mntmPvalueCorrection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				// choose adjustment method
				JPanel cboxPanel = new JPanel();
				String[] adjMethods = AdjustPval.getMethodNames();
				// get a list of multiple correction methods implemented
				JComboBox pvadjCBox = new JComboBox<>(adjMethods);
				cboxPanel.add(pvadjCBox);
				int opt = JOptionPane.showConfirmDialog(null, cboxPanel, "Select categories",
						JOptionPane.OK_CANCEL_OPTION);
				if (opt == JOptionPane.OK_OPTION) {
					// set selected method to the adjustment method
					pvAdjMethod = pvadjCBox.getSelectedItem().toString();
				} else {
					return;
				}

				// correct p values

				if (testPvals != null) {
					testadjutestPvals = AdjustPval.computeAdjPV(testPvals, pvAdjMethod);
				}
				if (ftestPvals != null) {

					ftestadjutestPvals = AdjustPval.computeAdjPV(ftestPvals, pvAdjMethod);
				}

				// update in table
				updateTable();
			}
		});
		mnEdit.add(mntmPvalueCorrection);

		JMenuItem mntmSelFeatureCols = new JMenuItem("Select Feature Info Cols");
		mntmSelFeatureCols.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int[] rowIndices = new int[featureNames.size()];
				int i=0;
				for(String row : featureNames) {
					rowIndices[i] = MetaOmGraph.activeProject.getRowIndexbyName(row,true);
					i++;
				}

				DEAColumnSelectFrame deaColSelect = new DEAColumnSelectFrame(currentPanel,rowIndices);
				MetaOmGraph.getDesktop().add(deaColSelect);
			}
		});
		mnEdit.add(mntmSelFeatureCols);

		JMenu mnPlot = new JMenu("Plot");
		menuBar.add(mnPlot);

		JMenu mnSelected = new JMenu("Selected");
		mnPlot.add(mnSelected);

		JMenuItem mntmLineChart = new JMenuItem("Line Chart");
		mntmLineChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get selected rowindex
				int[] rowIndices = getSelectedRowIndices();
				if (rowIndices == null || rowIndices.length == 0) {
					JOptionPane.showMessageDialog(null, "No rows selected", "Nothing selected",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				new MetaOmChartPanel(rowIndices, myProject.getDefaultXAxis(), myProject.getDefaultYAxis(),
						myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject)
				.createInternalFrame();
			}
		});
		mnSelected.add(mntmLineChart);

		JMenuItem mntmScatterplot = new JMenuItem("Scatter Plot");
		mntmScatterplot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// get selected rowindex
				int[] rowIndices = getSelectedRowIndices();
				if (rowIndices == null) {
					JOptionPane.showMessageDialog(null, "No rows selected", "Nothing selected",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (rowIndices.length < 1) {
					JOptionPane.showMessageDialog(null,
							"Please select two or more rows and try again to plot a scatterplot.",
							"Invalid number of rows selected", JOptionPane.ERROR_MESSAGE);
					return;
				}

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {// get data for selected rows

							ScatterPlotChart f = new ScatterPlotChart(rowIndices, 0, myProject,false);
							MetaOmGraph.getDesktop().add(f);
							f.setDefaultCloseOperation(2);
							f.setClosable(true);
							f.setResizable(true);
							f.pack();
							f.setSize(1000, 700);
							f.setVisible(true);
							f.toFront();

						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
									JOptionPane.ERROR_MESSAGE);

							e.printStackTrace();
							return;
						}
					}
				});

				return;

			}
		});
		mnSelected.add(mntmScatterplot);

		JMenuItem mntmBoxPlot = new JMenuItem("Box Plot");
		mntmBoxPlot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rowIndices = getSelectedRowIndices();
				if (rowIndices == null || rowIndices.length == 0) {
					JOptionPane.showMessageDialog(null, "No rows selected", "Nothing selected",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// get data for box plot as hasmap
				HashMap<Integer, double[]> plotData = new HashMap<>();
				for (int i = 0; i < rowIndices.length; i++) {
					double[] dataY = null;
					try {
						// dataY = myProject.getIncludedData(selected[i]);
						// send all data; excluded data will be excluded in the boxplot class; this
						// helps in splitting data by categories by reusing cluster function
						dataY = myProject.getAllData(rowIndices[i]);
					} catch (IOException eIO) {
						// TODO Auto-generated catch block
						eIO.printStackTrace();
					}
					plotData.put(rowIndices[i], dataY);
				}

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {// get data for selected rows

							BoxPlot f = new BoxPlot(plotData, 0, myProject, false);
							MetaOmGraph.getDesktop().add(f);
							f.setDefaultCloseOperation(2);
							f.setClosable(true);
							f.setResizable(true);
							f.pack();
							f.setSize(1000, 700);
							f.setVisible(true);
							f.toFront();

						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
									JOptionPane.ERROR_MESSAGE);

							e.printStackTrace();
							return;
						}
					}
				});

			}
		});
		mnSelected.add(mntmBoxPlot);

		JMenuItem mntmHistogram = new JMenuItem("Histogram");
		mntmHistogram.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {// get data for selected rows
							int[] selected = getSelectedRowIndices();
							if (selected == null || selected.length == 0) {
								JOptionPane.showMessageDialog(null, "No rows selected", "Nothing selected",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
							// number of bins
							int nBins = myProject.getIncludedDataColumnCount() / 10;
							HistogramChart f = new HistogramChart(selected, nBins, myProject, 1, null, false);
							MetaOmGraph.getDesktop().add(f);
							f.setDefaultCloseOperation(2);
							f.setClosable(true);
							f.setResizable(true);
							f.pack();
							f.setSize(1000, 700);
							f.setVisible(true);
							f.toFront();

						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
									JOptionPane.ERROR_MESSAGE);

							e.printStackTrace();
							return;
						}
					}
				});
				return;

			}
		});
		mnSelected.add(mntmHistogram);

		JMenuItem mntmVolcanoPlot = new JMenuItem("Volcano plot");
		mntmVolcanoPlot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				makeVolcano();

			}
		});
		mnPlot.add(mntmVolcanoPlot);

		JMenuItem mntmFcHistogram = new JMenuItem("FC histogram");
		mntmFcHistogram.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				plotColumnHistogram("logFC");

				return;

			}
		});
		mnPlot.add(mntmFcHistogram);

		JMenuItem mntmPvalHistogram = new JMenuItem("P-value histogram");
		mntmPvalHistogram.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				plotColumnHistogram(methodName + " pval");

				return;

			}
		});
		mnPlot.add(mntmPvalHistogram);

		JMenuItem mntmHistogramcolumn = new JMenuItem("Histogram (column)");
		mntmHistogramcolumn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				// display option to select a column
				JPanel cboxPanel = new JPanel();
				String[] colNames = new String[table.getColumnCount() - 1];

				// dont display 1st column or other non-numerical columns
				for (int cols = 1; cols < table.getColumnCount(); cols++) {
					colNames[cols-1] = table.getColumnName(cols);
				}
				// get a list of multiple correction methods implemented
				JComboBox options = new JComboBox<>(colNames);
				cboxPanel.add(options);
				int opt = JOptionPane.showConfirmDialog(null, cboxPanel, "Select column", JOptionPane.OK_CANCEL_OPTION);
				if (opt == JOptionPane.OK_OPTION) {
					// draw histogram with the selected column
					plotColumnHistogram(options.getSelectedItem().toString());
				} else {
					return;
				}

			}
		});
		mnPlot.add(mntmHistogramcolumn);


		
		
		
		
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.add(new JLabel("Filter:"), "Before");
		filterField = new ClearableTextField();
		filterField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 27) {
					filterModel.clearFilter();
					filterField.setText("");
				}
			}
		});
		filterField.getDocument().addDocumentListener(new FilterFieldListener());
		filterField.setDefaultText("Use semicolon (;) for multiple filters");
		filterField.setColumns(20);
		searchPanel.add(filterField, "Center");

		try {
			BufferedImage source = ImageIO
					.read(getClass().getResourceAsStream("/resource/tango/22x22/animations/process-working.png"));
			throbber = new MultiFrameImageThrobber(source, 4, 8);
		} catch (IOException e1) {
			throbber = new MetaOmThrobber();
		}
		searchPanel.add(throbber, "After");
		listFromFilterButton = new JButton(theme.getListSave());
		listFromFilterButton.setActionCommand("list from filter");
		
		listFromFilterButton.setEnabled(false);
		listFromFilterButton.setToolTipText("Export the results of the current filter to a new list");
		dataToolbar.add(new Separator());
		dataToolbar.add(searchPanel);
		dataToolbar.add(listFromFilterButton);

		// add advance filter button
		// s
		advFilterButton = new JButton("Advance filter");
		advFilterButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				


				//Harsha - reproducibility log
				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				HashMap<String,Object> result = new HashMap<String,Object>();
				result.put("result", "OK");

				// show advance filter options
				final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
						MetaOmGraph.getActiveProject(), true);
				final MetadataQuery[] queries;
				queries = tsp.showSearchDialog();
				// boolean matchCase=tsp.matchCase();
				boolean matchAll = tsp.matchAll();
				if (tsp.getQueryCount() <= 0) {
					// System.out.println("Search dialog cancelled");
					// User didn't enter any queries
					return;
				}

				String[] headers = myProject.getInfoColumnNames();
				List<String> headersList = Arrays.asList(headers);

				// JOptionPane.showMessageDialog(null, "h:"+headersList);

				// convert queries to filter string
				String allFilter = "";
				for (int i = 0; i < queries.length; i++) {

					String thisFilter = "";
					String thisField = queries[i].getField();
					boolean thismatchCase = queries[i].isCaseSensitive();
					String thisTerm = queries[i].getTerm();
					// JOptionPane.showMessageDialog(null,"F:" + queries[i].getField() + " T:" +
					// queries[i].getTerm() + " isE:" + queries[i].isExact()+ "mC:"+thismatchCase);
					if (thismatchCase) {
						thisTerm += "--C";
					}
					if (thisField.equals("Any Field") || thisField.equals("All Fields")) {
						thisFilter = thisTerm;
					} else {
						int thisCol = headersList.indexOf(thisField);
						thisFilter = thisTerm + ":::" + String.valueOf(thisCol);
					}

					allFilter += thisFilter + ";";
				}

				dataMap.put("allFilters", allFilter);
				filterField.setText(allFilter);

				//			ActionProperties advancedFilterAction = new ActionProperties("advanced-filter",null,dataMap,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				//			advancedFilterAction.logActionProperties();

				return;
			
			}
		});
		
		advFilterButton.setToolTipText("Filter/search the table with multiple queries");
		dataToolbar.add(advFilterButton);
		
		
		panel_1.add(dataToolbar);
		
		

	}

	private void makeVolcano() {
		// create data for volcano plot object
		List<String> featureNames = new ArrayList<>();
		List<Double> fc = new ArrayList<>();
		List<Double> pv = new ArrayList<>();
		for (int i = 0; i < table.getRowCount(); i++) {
			featureNames.add((String) table.getModel().getValueAt(i, table.getColumn("Name").getModelIndex()));
			fc.add((Double) table.getModel().getValueAt(i, table.getColumn("logFC").getModelIndex()));
			pv.add((Double) table.getModel().getValueAt(i, table.getColumn(methodName + " pval").getModelIndex()));
		}

		// make plot
		VolcanoPlot f = new VolcanoPlot(featureNames, fc, pv, name1, name2);
		MetaOmGraph.getDesktop().add(f);
		f.setDefaultCloseOperation(2);
		f.setClosable(true);
		f.setResizable(true);
		f.pack();
		f.setSize(1000, 700);
		f.setVisible(true);
		f.toFront();

	}

	private void initTableModel() {
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

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				int col = table.columnAtPoint(new Point(e.getX(), e.getY()));

			}

			@Override
			public void mouseExited(MouseEvent e) {
				int col = table.columnAtPoint(new Point(e.getX(), e.getY()));

			}
		});
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

	public void updateTable() {

		// add data
		allColumnNames = new ArrayList<String>();
		
		allColumnNames.add("Name");
		allColumnNames.add("Mean(log(" + name1 + "))");
		allColumnNames.add("Mean(log(" + name2 + "))");
		allColumnNames.add("logFC");
		if (testPvals != null) {
			if (ftestRatiovals != null && ftestRatiovals.size() > 0) {
				allColumnNames.add("F statistic");
				allColumnNames.add("F test pval");
				allColumnNames.add("Adj F test pval");
			}
			allColumnNames.add(methodName + " pval");
			allColumnNames.add("Adj pval");
		}

		if(featureMetadataColumnNames!=null) {
			for(String col : featureMetadataColumnNames) {
				allColumnNames.add(col);
			}
		}

		currentTableData = new Object[featureNames.size()][allColumnNames.size()];
		currentTableDataMap = new LinkedHashMap<String,Object[]>();

		// for each row add each coloumn
		for (int i = 0; i < featureNames.size(); i++) {
			// create a temp string storing all col values for a row
			Vector temp = new Vector<>();
			temp.add(featureNames.get(i));
			temp.add(mean1.get(i));
			temp.add(mean2.get(i));
			temp.add(mean1.get(i) - mean2.get(i));
			if (testPvals != null) {
				if (testPvals.get(i) >= pvThresh) {
					continue;
				}
				if (ftestRatiovals != null && ftestRatiovals.size() > 0) {
					temp.add(ftestRatiovals.get(i));
					temp.add(ftestPvals.get(i));
					temp.add(ftestadjutestPvals.get(i));
				}
				temp.add(testPvals.get(i));
				temp.add(testadjutestPvals.get(i));
			}

			if(featureMetadataColumnData!=null) {
				for(int j=0;j<featureMetadataColumnData[i].length;j++) {
					temp.add(featureMetadataColumnData[i][j]);
				}
			}

			currentTableData[i] = temp.toArray();

		}

		
		String[] colNames = new String[allColumnNames.size()];
		int x=0;
		for(String s : allColumnNames) {
			colNames[x] = s;
			x++;
		}
		mainModel = new NoneditableTableModel(currentTableData,colNames);
		filterModel = new FilterableTableModel(mainModel);
		sorter = new TableSorter(filterModel);
		
		table.setModel(sorter);
		

		for (int i = 0; i < featureNames.size(); i++) {
			currentTableDataMap.put((String)currentTableData[i][0], currentTableData[i]);
		}

		
		if(featureMetadataAllData != null && masterFeatureMetadataAllData != null) {
			
			int defaultColCount = 4;
			if (testPvals != null) {
				defaultColCount += 2;
				if (ftestRatiovals != null && ftestRatiovals.size() > 0) {
					defaultColCount += 3;
				}
			}
			
			for(int j = 0; j < featureMetadataAllData.length; j++) {
				 
				if( !currentTableDataMap.containsKey(masterFeatureMetadataAllData[j][MetaOmGraph.activeProject.getDefaultColumn()])) {
					
					Object [] row = new Object[featureMetadataAllData[j].length+defaultColCount];
					
					row[0] = (String)masterFeatureMetadataAllData[j][MetaOmGraph.activeProject.getDefaultColumn()];
					for(int k = 1; k < defaultColCount; k++) {
						row[k] = 0;
					}
					for(int l = defaultColCount; l < featureMetadataAllData[j].length+defaultColCount; l++) {
						row[l] = featureMetadataAllData[j][l-defaultColCount];
					}
					
					currentTableDataMap.put((String)masterFeatureMetadataAllData[j][MetaOmGraph.activeProject.getDefaultColumn()], row);
				}
			}
		}
		
		formatTable();

	}



	public void updateTableRows(Object [][] rows) {

		String[] colNames = new String[allColumnNames.size()];
		int x=0;
		for(String s : allColumnNames) {
			colNames[x] = s;
			x++;
		}
		
		mainModel = new NoneditableTableModel(rows,colNames);
		filterModel = new FilterableTableModel(mainModel);
		sorter = new TableSorter(filterModel);
		
		table.setModel(sorter);
		formatTable();

	}

	
	public void formatTable() {
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		// set decimal formatter to all cols except first
		int colCount = 0;
		if(featureMetadataColumnNames!=null) {
			colCount = table.getColumnCount()-featureMetadataColumnNames.length;
		}
		else {
			colCount = table.getColumnCount();
		}
		
		DecimalFormatRenderer dfr = new DecimalFormatRenderer();
		DEAHeaderRenderer customHeaderCellRenderer = 
				new DEAHeaderRenderer(Color.white,
						Color.red,
						new Font("Consolas",Font.BOLD,14),
						BorderFactory.createEtchedBorder(),
						true);

		for (int i = 1; i < colCount; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(dfr);
			table.getColumnModel().getColumn(i).setHeaderRenderer(customHeaderCellRenderer);
		}

		DEAHeaderRenderer featureMetadataHeaderCellRenderer = 
				new DEAHeaderRenderer(Color.white,
						Color.BLUE,
						new Font("Consolas",Font.BOLD,14),
						BorderFactory.createEtchedBorder(),
						true);

		for(int i=colCount;i<table.getColumnCount();i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer());
			table.getColumnModel().getColumn(i).setHeaderRenderer(featureMetadataHeaderCellRenderer);
		}

	}

	public int[] getSelectedRowsIndices() {
		
		int [] currentTableSelRows = table.getSelectedRows();
		List<String> selectedGeneNames = new ArrayList<String>();
		for(int i=0; i< currentTableSelRows.length; i++) {
			selectedGeneNames.add((String)table.getValueAt(currentTableSelRows[i], 0));
		}
		
		return myProject.getRowIndexbyName(selectedGeneNames, true);
	}

	public void printMessage(String msg) {
		
		JDialog jd = new JDialog();
		JTextPane jt = new JTextPane();
		jt.setText(msg);
		jt.setBounds(10, 10, 300, 100);
		jd.getContentPane().add(jt);
		jd.setBounds(100, 100, 500, 200);
		jd.setVisible(true);
		
	}

	private int[] getSelectedRowIndices() {
		// get correct indices wrt the list
		int[] rowIndices = table.getSelectedRows();
		// JOptionPane.showMessageDialog(null, "sR:" + Arrays.toString(rowIndices));
		List<String> names = new ArrayList<>();
		int j = 0;
		for (int i : rowIndices) {
			names.add(table.getValueAt(i, table.getColumn("Name").getModelIndex()).toString());
		}
		rowIndices = myProject.getRowIndexbyName(names, true);

		return rowIndices;
	}

	
	/**
	 * Function to plot histogram of selected column
	 * 
	 * @param columnName
	 */
	private void plotColumnHistogram(String columnName) {

		// plot histogram of current pvalues in table
		double[] data = new double[table.getRowCount()];
		for (int r = 0; r < table.getRowCount(); r++) {
			// get p values

			data[r] = (double) table.getModel().getValueAt(r, table.getColumn(columnName).getModelIndex());
		}
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {// get data for selected rows
					int nBins = 10;
					HistogramChart f = new HistogramChart(null, nBins, null, 2, data, false);
					f.setTitle(columnName + " histogram");
					MetaOmGraph.getDesktop().add(f);
					f.setDefaultCloseOperation(2);
					f.setClosable(true);
					f.setResizable(true);
					f.pack();
					f.setSize(1000, 700);
					f.setVisible(true);
					f.toFront();

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
							JOptionPane.ERROR_MESSAGE);

					e.printStackTrace();
					return;
				}
			}
		});
		return;

	}
	
	
	private class FilterFieldListener implements DocumentListener, ActionListener {
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
