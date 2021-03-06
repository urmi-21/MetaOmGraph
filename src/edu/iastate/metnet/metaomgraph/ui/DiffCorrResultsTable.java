package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import edu.iastate.metnet.metaomgraph.AdjustPval;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.DEAHeaderRenderer;
import edu.iastate.metnet.metaomgraph.DecimalFormatRenderer;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.chart.BoxPlot;
import edu.iastate.metnet.metaomgraph.chart.HistogramChart;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.chart.ScatterPlotChart;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.throbber.MetaOmThrobber;
import edu.iastate.metnet.metaomgraph.throbber.MultiFrameImageThrobber;
import edu.iastate.metnet.metaomgraph.utils.Utils;


/**
 * 
 * @author Harsha
 * 
 * This is the frame that displays the Differential Correlation results.
 * It extends the StatisticalResultsPanel, that contains the initializations for
 * all the display and interaction components for Statistical Results.
 * 
 * The frame is designed similar to the Project Data Frame, with a table displaying
 * the resuts in the middle (table) , a menubar on top with plots and other features, a 
 * search bar to filter the rows of the results table, a listPanel that displays all
 * the saved lists (same list as Project Data), buttons to create, rename, edit or
 * delete lists, Advanced Search option etc.
 * 
 * 
 *
 */
public class DiffCorrResultsTable extends StatisticalResultsPanel {

	private DiffCorrResultsTable currentObj;
	
	private int n1;
	private int n2;
	private double pvThresh = 2;
	String pvAdjMethod;
	DiffCorrResultsTable currentPanel;

	private List<String> featureNames;
	private List<Double> corrVals1;
	private List<Double> corrVals2;
	private List<Double> zVals1;
	private List<Double> zVals2;
	private List<Double> diff;
	private List<Double> zScores;
	private List<Double> pVals;
	private List<Double> adjpVals;


	public List<String> getFeatureNames(){
		return this.featureNames;
	}


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DiffCorrResultsTable frame = new DiffCorrResultsTable();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DiffCorrResultsTable() {
		this(null, 0, 0, null, null, null, null, null, null, null, null);

	}

	
	/**
	 * 
	 * @param featureNames
	 * @param grp1Size
	 * @param grp2Size
	 * @param corrVals1
	 * @param corrVals2
	 * @param zvals1
	 * @param zvals2
	 * @param diffZvals
	 * @param zscores
	 * @param pvals
	 * @param myProject
	 * 
	 * This constructor initializes the table, provides actionListeners to the lists,
	 * menu items, list creation/updation menu items, and other action items from the
	 * menubar
	 */
	public DiffCorrResultsTable(List<String> featureNames, int grp1Size, int grp2Size, List<Double> corrVals1,
			List<Double> corrVals2, List<Double> zvals1, List<Double> zvals2, List<Double> diffZvals,
			List<Double> zscores, List<Double> pvals, MetaOmProject myProject) {
		
		
		try {
		this.myProject = myProject;
		this.featureNames = featureNames;
		this.n1 = grp1Size;
		this.n2 = grp2Size;
		this.corrVals1 = corrVals1;
		this.corrVals2 = corrVals2;
		currentObj = this;

		zVals1 = zvals1;
		zVals2 = zvals2;
		diff = diffZvals;
		zScores = zscores;
		pVals = pvals;

		currentPanel = this;

		if (pVals != null) {
			adjpVals = AdjustPval.computeAdjPV(pVals, pvAdjMethod); // by default use B-H correction
		}

		setBounds(100, 100, 450, 300);
		setLayout(new BorderLayout(0, 0));

		listPanel = new JPanel(new BorderLayout());

		String[] listNames = myProject.getGeneListNames();
		Arrays.sort(listNames, MetaOmGraph.getActiveTablePanel().new ListNameComparator());

		geneLists = new JList(listNames);
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
				CreateListFrameDC clf = new CreateListFrameDC(myProject, (String) geneLists.getSelectedValue(), currentObj);
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
				CreateListFrameDC clf = new CreateListFrameDC(myProject,null,currentObj);

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
		listDeleteButton.setEnabled(false);
		listEditButton.setEnabled(false);
		listRenameButton.setEnabled(false);


		JPanel geneListPanel = new JPanel(new BorderLayout());
		JScrollPane geneListScrollPane = new JScrollPane(geneLists);
		geneListPanel.add(listToolbar, "First");
		geneListPanel.add(geneListScrollPane, "Center");
		Border loweredetched = BorderFactory.createEtchedBorder();
		geneListPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "Lists"));
		initTableModel();


		geneLists.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting() && geneLists.getSelectedValue()!=null && !geneLists.getSelectedValue().toString().equalsIgnoreCase("")) {

					selectList((String)geneLists.getSelectedValue());

				}
			}
		});

		geneLists.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JList l = (JList) e.getSource();
				ListModel m = l.getModel();
				int index = l.locationToIndex(e.getPoint());
				if (index >= 0) {
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

		JMenuItem mntmExportToFile = new JMenuItem("Export to file");
		mntmExportToFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTabletofile(table, "Diff Correlation Table");
			}
		});
		mnFile.add(mntmExportToFile);

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
				String listName = JOptionPane.showInputDialog(DiffCorrResultsTable.this, "Enter a name for new list");
				if (listName == null || listName.length() < 1) {
					JOptionPane.showMessageDialog(DiffCorrResultsTable.this, "Invalid name", "Failed",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (myProject.addGeneList(listName, rowIndices, true, false)) {

					try {
						//Harsha - reproducibility log
						HashMap<String,Object> actionMap = new HashMap<String,Object>();
						actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());

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

						ActionProperties mergeListAction = new ActionProperties("export-to-list",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
						mergeListAction.logActionProperties();
					}
					catch(Exception e1) {

					}

					JOptionPane.showMessageDialog(DiffCorrResultsTable.this, "List" + listName + " added", "List added",
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
				
				
				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								try {

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

									
									
									if(featureNames != null) {

										//Get Feature metadata rows
										List<String> rowNames = featureNames;
										int[] rowIndices = MetaOmGraph.activeProject.getRowIndexesFromFeatureNames(rowNames, true);
										
										for(int j = 0; j < rowIndices.length; j++) {
											rowIndicesMapping[rowIndices[j]] = j;
										}

										Object[][] featureInfoRows = MetaOmGraph.activeProject.getRowNames(rowIndices);	
										String [] featureInfoColNames = MetaOmGraph.activeProject.getInfoColumnNames();
										
										List allColumnNames = new ArrayList<String>();

										allColumnNames.add("Name");
										allColumnNames.add("r1");
										allColumnNames.add("r2");
										allColumnNames.add("z1");
										allColumnNames.add("z2");
										allColumnNames.add("z1-z2");
										allColumnNames.add("zScore");
										allColumnNames.add("p-value");
										allColumnNames.add("Adj p-value");

										if(featureInfoColNames!=null) {
											for(String col : featureInfoColNames) {
												allColumnNames.add(col);
											}
										}
										
										String [] masterColumns = new String[allColumnNames.size()];
										
										for(int i=0; i< allColumnNames.size(); i++) {
											masterColumns[i] = (String) allColumnNames.get(i);
										}

										ArrayList<ArrayList> pValRows = new ArrayList<ArrayList>();
										// for each row add each coloumn
										for (int i = 0; i < featureNames.size(); i++) {
											// create a temp string storing all col values for a row
											ArrayList temp = new ArrayList();
											temp.add(featureNames.get(i));
											temp.add(corrVals1.get(i));
											temp.add(corrVals2.get(i));
											temp.add(zVals1.get(i));
											temp.add(zVals2.get(i));
											temp.add(diff.get(i));
											temp.add(zScores.get(i));

											// skip if p value is high
											if (pVals.get(i) >= pvThresh) {
												continue;
											}
											temp.add(pVals.get(i));

											temp.add(adjpVals.get(i));

											if(featureInfoRows!=null) {
												for(int k=0;k<featureInfoRows[i].length;k++) {
													temp.add(featureInfoRows[i][k]);
												}
											}

											pValRows.add(temp);

										}
										
										Object [][] pValLimitedData = new Object[pValRows.size()][featureInfoColNames.length+9];
										
									
										int[] rowIndices2 = new int[pValRows.size()];
										
										if(pValRows.size() > 0) {
											
											for(int i = 0; i < rowIndicesMapping.length; i++) {
												rowIndicesMapping[i] = -1;
											}
										}
										for(int i = 0 ; i < pValRows.size(); i++ ) {
											ArrayList temp = pValRows.get(i);
											pValLimitedData[i] = temp.toArray();
											rowIndices2[i] = MetaOmGraph.activeProject.getRowIndexbyName((String)pValLimitedData[i][0],true);
											rowIndicesMapping[rowIndices2[i]] = i;
										}
										
										setMasterTableData(pValLimitedData);
										setMasterTableColumns(masterColumns);
										setSelectedAndProjectedTableData(pValLimitedData);
										setSelectedAndProjectedTableColumns(masterColumns);
										
										projectColumns(getSelectedFeatureColumns());

									}

									// JOptionPane.showMessageDialog(null, "Done");


								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						return null;
					}
				}.start();
				
				
				
			}
		});
		mnEdit.add(mntmFilter);

		JMenuItem mntmPvalueCorrection = new JMenuItem("P-value correction");
		mntmPvalueCorrection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				
				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								try {

									// choose adjustment method
									JPanel cboxPanel = new JPanel();
									String[] adjMethods = AdjustPval.getMethodNames();
									JComboBox pvadjCBox = new JComboBox<>(adjMethods);
									cboxPanel.add(pvadjCBox);
									int opt = JOptionPane.showConfirmDialog(null, cboxPanel, "Select categories",
											JOptionPane.OK_CANCEL_OPTION);
									if (opt == JOptionPane.OK_OPTION) {
										pvAdjMethod = pvadjCBox.getSelectedItem().toString();
									} else {
										return;
									}

									// correct p values
									if (pVals != null) {
										adjpVals = AdjustPval.computeAdjPV(pVals, pvAdjMethod);
									}

									// update in table
									
									if(featureNames != null) {

										//Get Feature metadata rows
										List<String> rowNames = featureNames;
										int[] rowIndices = MetaOmGraph.activeProject.getRowIndexesFromFeatureNames(rowNames, true);
										
										for(int j = 0; j < rowIndices.length; j++) {
											rowIndicesMapping[rowIndices[j]] = j;
										}

										Object[][] featureInfoRows = MetaOmGraph.activeProject.getRowNames(rowIndices);	
										String [] featureInfoColNames = MetaOmGraph.activeProject.getInfoColumnNames();
										
										List allColumnNames = new ArrayList<String>();

										allColumnNames.add("Name");
										allColumnNames.add("r1");
										allColumnNames.add("r2");
										allColumnNames.add("z1");
										allColumnNames.add("z2");
										allColumnNames.add("z1-z2");
										allColumnNames.add("zScore");
										allColumnNames.add("p-value");
										allColumnNames.add("Adj p-value");

										if(featureInfoColNames!=null) {
											for(String col : featureInfoColNames) {
												allColumnNames.add(col);
											}
										}
										
										String [] masterColumns = new String[allColumnNames.size()];
										
										for(int i=0; i< allColumnNames.size(); i++) {
											masterColumns[i] = (String) allColumnNames.get(i);
										}

										ArrayList<ArrayList> pValRows = new ArrayList<ArrayList>();
										// for each row add each coloumn
										for (int i = 0; i < featureNames.size(); i++) {
											// create a temp string storing all col values for a row
											ArrayList temp = new ArrayList();
											temp.add(featureNames.get(i));
											temp.add(corrVals1.get(i));
											temp.add(corrVals2.get(i));
											temp.add(zVals1.get(i));
											temp.add(zVals2.get(i));
											temp.add(diff.get(i));
											temp.add(zScores.get(i));

											// skip if p value is high
											if (pVals.get(i) >= pvThresh) {
												continue;
											}
											temp.add(pVals.get(i));

											temp.add(adjpVals.get(i));

											if(featureInfoRows!=null) {
												for(int k=0;k<featureInfoRows[i].length;k++) {
													temp.add(featureInfoRows[i][k]);
												}
											}

											pValRows.add(temp);

										}
										
										Object [][] pValLimitedData = new Object[pValRows.size()][featureInfoColNames.length+9];
										
									
										int[] rowIndices2 = new int[pValRows.size()];
										
										if(pValRows.size() > 0) {
											for(int i = 0; i < rowIndicesMapping.length; i++) {
												rowIndicesMapping[i] = -1;
											}
										}
										for(int i = 0 ; i < pValRows.size(); i++ ) {
											ArrayList temp = pValRows.get(i);
											pValLimitedData[i] = temp.toArray();
											rowIndices2[i] = MetaOmGraph.activeProject.getRowIndexbyName((String)pValLimitedData[i][0],true);
											rowIndicesMapping[rowIndices2[i]] = i;
										}
										
										setMasterTableData(pValLimitedData);
										setMasterTableColumns(masterColumns);
										setSelectedAndProjectedTableData(pValLimitedData);
										setSelectedAndProjectedTableColumns(masterColumns);
										
										projectColumns(getSelectedFeatureColumns());

									}

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						return null;
					}
				}.start();
				
				
				
			}
		});
		mnEdit.add(mntmPvalueCorrection);


		JMenuItem mntmSelFeatureCols = new JMenuItem("Hide/Show DC Result columns");
		mntmSelFeatureCols.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				

				table.initializeVisibilityMap();
				java.util.List<String> selectedVals = new ArrayList<>();
				LinkedHashMap<TableColumn,Boolean> visibleColumns = (LinkedHashMap<TableColumn,Boolean>)table.getMetadata().getColumnVisibilityMap();
				List<TableColumn> allColumns =  new ArrayList<TableColumn>();
				
				
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

				
				int res = JOptionPane.showConfirmDialog(null, outerPanel, "Hide/Show DC Result Columns",
						JOptionPane.OK_CANCEL_OPTION);
				if (res == JOptionPane.OK_OPTION) {
					
					for (int i = 0; i < metadataHeaders.size(); i++) {
						if (cBoxes[i].isSelected()) {
							metadataSelectedStatus.add(i, true);
							visibleColumns.put(allColumns.get(i), true);
							table.getMetadata().setColumnVisibilityMap(visibleColumns);
						}
						else {
							metadataSelectedStatus.add(i, false);
							visibleColumns.put(allColumns.get(i), false);
							table.getMetadata().setColumnVisibilityMap(visibleColumns);
						}
					}
					
					table.hideColumns();
					
				} else {
					return;
				}
				
				
			
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

							BoxPlot f = new BoxPlot(plotData, 0, myProject,false);
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

		JMenuItem mntmPvalueHistogram = new JMenuItem("P-value histogram");
		mntmPvalueHistogram.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//plot histogram of column p-value
				plotColumnHistogram("p-value");
				return;
			}
		});
		mnPlot.add(mntmPvalueHistogram);

		JMenuItem mntmHistogramcolumn = new JMenuItem("Histogram (column)");
		mntmHistogramcolumn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int nonFeatureColCount = 9;
				
				// display option to select a column
				JPanel cboxPanel = new JPanel();
				String[] colNames = new String[nonFeatureColCount - 1];

				// dont display 1st column or other non-numerical columns
				for (int cols = 1; cols < nonFeatureColCount; cols++) {
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
		filterField.setColumns(30);
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
		listFromFilterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				makeListFromFilter();
			}
		});
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
				final TreeSearchQueryConstructionPanelDC tsp = new TreeSearchQueryConstructionPanelDC(
						MetaOmGraph.getActiveProject(), true, getSelectedAndProjectedTableColumns());
				final MetadataQuery[] queries;
				queries = tsp.showSearchDialog();
				// boolean matchCase=tsp.matchCase();
				boolean matchAll = tsp.matchAll();
				if (tsp.getQueryCount() <= 0) {
					// System.out.println("Search dialog cancelled");
					// User didn't enter any queries
					return;
				}

				List<String> headersList = Arrays.asList(getSelectedAndProjectedTableColumns());

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

		
		rowIndicesMapping = new int[MetaOmGraph.activeProject.getRowCount()];
		for(int i=0; i < rowIndicesMapping.length; i++) {
			rowIndicesMapping[i] = -1;
		}
		//Combining the Diff corr columns and feature info columns into masterData before updating the table
		if(featureNames != null) {

			//Get Feature metadata rows
			List<String> rowNames = featureNames;
			int[] rowIndices = MetaOmGraph.activeProject.getRowIndexesFromFeatureNames(rowNames, true);
			
			for(int j = 0; j < rowIndices.length; j++) {
				rowIndicesMapping[rowIndices[j]] = j;
			}

			Object[][] featureInfoRows = MetaOmGraph.activeProject.getRowNames(rowIndices);	
			String [] featureInfoColNames = MetaOmGraph.activeProject.getInfoColumnNames();
			
			Object [][] masterData = new Object[featureNames.size()][featureInfoColNames.length+9];
			
			List allColumnNames = new ArrayList<String>();

			allColumnNames.add("Name");
			allColumnNames.add("r1");
			allColumnNames.add("r2");
			allColumnNames.add("z1");
			allColumnNames.add("z2");
			allColumnNames.add("z1-z2");
			allColumnNames.add("zScore");
			allColumnNames.add("p-value");
			allColumnNames.add("Adj p-value");

			if(featureInfoColNames!=null) {
				for(String col : featureInfoColNames) {
					allColumnNames.add(col);
				}
			}
			
			String [] masterColumns = new String[allColumnNames.size()];
			
			for(int i=0; i< allColumnNames.size(); i++) {
				masterColumns[i] = (String) allColumnNames.get(i);
			}

			// for each row add each coloumn
			for (int i = 0; i < featureNames.size(); i++) {
				// create a temp string storing all col values for a row
				Vector temp = new Vector<>();
				temp.add(featureNames.get(i));
				temp.add(corrVals1.get(i));
				temp.add(corrVals2.get(i));
				temp.add(zVals1.get(i));
				temp.add(zVals2.get(i));
				temp.add(diff.get(i));
				temp.add(zScores.get(i));

				// skip if p value is high
				if (pVals.get(i) >= pvThresh) {
					continue;
				}
				temp.add(pVals.get(i));

				temp.add(adjpVals.get(i));

				if(featureInfoRows!=null) {
					for(int k=0;k<featureInfoRows[i].length;k++) {
						temp.add(featureInfoRows[i][k]);
					}
				}

				masterData[i] = temp.toArray();


			}

			setMasterTableData(masterData);
			setMasterTableColumns(masterColumns);
			setSelectedAndProjectedTableData(masterData);
			setSelectedAndProjectedTableColumns(masterColumns);
			setSelectedFeatureColumns(Arrays.asList(myProject.getInfoColumnNames()));
			
			updateTable();

		}


		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * Overriden method that formats the Statistical Result Panel table to show the
	 * DEA columns in Red color and Feature metadata columns in Blue.
	 */
	@Override
	public void formatTable() {

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 12));

		int colCount = 9;

		DecimalFormatRenderer dfr = new DecimalFormatRenderer();
		DEAHeaderRenderer customHeaderCellRenderer = 
				new DEAHeaderRenderer(Color.white,
						Color.red,
						new Font("Consolas",Font.BOLD,14),
						BorderFactory.createEtchedBorder(),
						true);

		// set decimal formatter to all cols except first
		for (int i = 1; i < colCount; i++) {
			table.getColumnModel().getColumn(i)
			.setCellRenderer(new edu.iastate.metnet.metaomgraph.DecimalFormatRenderer());
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
		
		
		table.initializeVisibilityMap();
		table.hideColumns();

	}
	
	
	/**
	 * Overriden method that projects the feature metadata columns after a user
	 * chooses the required columns.
	 */
	@Override
	public void projectColumns(List<String> selectedCols) {
		
		String [] featureInfoColNames = MetaOmGraph.activeProject.getInfoColumnNames();
		int [] selectedFeatureMetadataCols = new int[selectedCols.size()];
		
		for(int i = 0; i < selectedCols.size(); i++) {
			for(int j = 0; j < featureInfoColNames.length ; j++) {
				if(selectedCols.get(i).equals(featureInfoColNames[j])) {
					selectedFeatureMetadataCols[i] = j;
				}
			}
		}
		
		String[] originalMasterColumns = getMasterTableColumns();
		String[] projectedMasterColumns = new String[selectedCols.size()+9];
		
		
		for(int k = 0; k < 9 ; k++) {
			projectedMasterColumns[k] = originalMasterColumns[k];
		}
		
		for(int l = 0; l < selectedCols.size(); l++) {
			projectedMasterColumns[l+9] = selectedCols.get(l);
		}
		
		setSelectedAndProjectedTableColumns(projectedMasterColumns);
		
		
		
		Object[][] originalMasterData = getMasterTableData();
		Object[][] projectedMasterData = new Object[originalMasterData.length][projectedMasterColumns.length];
		
		for(int a = 0; a < originalMasterData.length; a++) {
			for(int b = 0; b < 9; b++) {
				projectedMasterData[a][b] = originalMasterData[a][b];
			}
			for(int c = 0; c < selectedFeatureMetadataCols.length; c++) {
				projectedMasterData[a][c+9] = originalMasterData[a][selectedFeatureMetadataCols[c]+9];
			}
		}
		
		setSelectedAndProjectedTableData(projectedMasterData);
		
		updateTable();
		selectList(getCurrentSelectedList());
		
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

	/**
	 * convert r values to z applying Fisher's transform
	 * 
	 * @param rVals
	 * @return
	 */
	/*
	 * private List<Double> converttoZ(List<Double> rVals) { List<Double> res = new
	 * ArrayList<>(); Atanh atan = new Atanh(); for (double d : rVals) {
	 * res.add(atan.value(d)); // JOptionPane.showMessageDialog(null,
	 * "val:"+d+" atan:"+atan.value(d)); } return res; }
	 * 
	 * private List<Double> getDiff(List<Double> rVals1, List<Double> rVals2) {
	 * List<Double> res = new ArrayList<>(); for (int i = 0; i < rVals1.size(); i++)
	 * { res.add(rVals1.get(i) - rVals2.get(i)); } return res; }
	 * 
	 * private List<Double> getZscores(List<Double> diff) { List<Double> res = new
	 * ArrayList<>(); for (int i = 0; i < diff.size(); i++) { double thisZ =
	 * diff.get(i); double denom = Math.sqrt((1 / ((double) n1 - 3)) + (1 /
	 * ((double) n2 - 3))); // JOptionPane.showMessageDialog(null, "denom:" +
	 * denom); thisZ = thisZ / denom; res.add(thisZ); } return res; }
	 * 
	 * private List<Double> getPVals(List<Double> zScores) { List<Double> res = new
	 * ArrayList<>(); NormalDistribution nob = new NormalDistribution();
	 * 
	 * for (int i = 0; i < zScores.size(); i++) { double thisZ = zScores.get(i); if
	 * (thisZ > 0) { thisZ = thisZ * -1; } res.add(nob.cumulativeProbability(thisZ)
	 * * 2); } return res; }
	 */

	private int[] getSelectedRowIndices() {
		// get correct indices wrt the list
		int[] rowIndices = table.getSelectedRows();
		// JOptionPane.showMessageDialog(null, "sR:" + Arrays.toString(rowIndices));
		List<String> names = new ArrayList<>();
		int j = 0;
		for (int i : rowIndices) {
			names.add(table.getValueAt(i, table.getColumn("Name").getModelIndex()).toString());
		}
		rowIndices = myProject.getRowIndexesFromFeatureNames(names, true);

		return rowIndices;
	}

	
	/**
	 * Method to plot the column Histogram of the given data
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



}


