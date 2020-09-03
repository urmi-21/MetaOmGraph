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
import javax.swing.Box;
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
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
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

/**
 * 
 * @author Harsha
 * 
 * This is the frame that displays the Differential Expression Analysis results.
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
public class logFCResultsFrame extends StatisticalResultsPanel {


	private List<String> featureNames;
	private List<Double> mean1;
	private List<Double> mean2;
	private List<Double> testPvals;
	private List<Double> ftestPvals;
	private List<Double> ftestRatiovals;
	private List<Double> testadjutestPvals;
	private List<Double> ftestadjutestPvals;
	String name1;
	String name2;
	String methodName;
	String pvAdjMethod;
	logFCResultsFrame currentPanel;
	

	double pvThresh = 2;


	public List<String> getFeatureNames(){
		return this.featureNames;
	}
	public String getSelectedList() {
		return geneLists.getSelectedValue().toString();
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

	
	/**
	 * 
	 * @param featureNames
	 * @param mean1
	 * @param mean2
	 * @param name1
	 * @param name2
	 * @param methodName
	 * @param pv
	 * @param ftestratio
	 * @param ftestpv
	 * @param myProject
	 * 
	 * This constructor initializes the table, provides actionListeners to the lists,
	 * menu items, list creation/updation menu items, and other action items from the
	 * menubar
	 */
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
										int[] rowIndices = MetaOmGraph.activeProject.getRowIndexbyName(rowNames, true);
										
										for(int j = 0; j < rowIndices.length; j++) {
											rowIndicesMapping[rowIndices[j]] = j;
										}

										Object[][] featureInfoRows = MetaOmGraph.activeProject.getRowNames(rowIndices);	
										String [] featureInfoColNames = MetaOmGraph.activeProject.getInfoColumnNames();
										
										int nonFeatureColCount = 0;
										
										List<String> allColumnNames = new ArrayList<String>();
										allColumnNames.add("Name");
										allColumnNames.add("Mean(log(" + name1 + "))");
										allColumnNames.add("Mean(log(" + name2 + "))");
										allColumnNames.add("logFC");
										nonFeatureColCount += 4;
										if (testPvals != null) {
											if (ftestRatiovals != null && ftestRatiovals.size() > 0) {
												allColumnNames.add("F statistic");
												allColumnNames.add("F test pval");
												allColumnNames.add("Adj F test pval");
												nonFeatureColCount += 3;
											}
											
											allColumnNames.add(methodName + " pval");
											allColumnNames.add("Adj pval");
											nonFeatureColCount += 2;
										}

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

											if(featureInfoRows!=null) {
												for(int k=0;k<featureInfoRows[i].length;k++) {
													temp.add(featureInfoRows[i][k]);
												}
											}

											pValRows.add(temp);

										}

										Object [][] pValLimitedData = new Object[pValRows.size()][featureInfoColNames.length+nonFeatureColCount];
										
										
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

									if (testPvals != null) {
										testadjutestPvals = AdjustPval.computeAdjPV(testPvals, pvAdjMethod);
									}
									if (ftestPvals != null) {

										ftestadjutestPvals = AdjustPval.computeAdjPV(ftestPvals, pvAdjMethod);
									}

									// update in table
									
									if(featureNames != null) {

										//Get Feature metadata rows
										List<String> rowNames = featureNames;
										int[] rowIndices = MetaOmGraph.activeProject.getRowIndexbyName(rowNames, true);
										
										for(int j = 0; j < rowIndices.length; j++) {
											rowIndicesMapping[rowIndices[j]] = j;
										}

										Object[][] featureInfoRows = MetaOmGraph.activeProject.getRowNames(rowIndices);	
										String [] featureInfoColNames = MetaOmGraph.activeProject.getInfoColumnNames();
										
										int nonFeatureColCount = 0;
										
										List<String> allColumnNames = new ArrayList<String>();
										allColumnNames.add("Name");
										allColumnNames.add("Mean(log(" + name1 + "))");
										allColumnNames.add("Mean(log(" + name2 + "))");
										allColumnNames.add("logFC");
										nonFeatureColCount += 4;
										if (testPvals != null) {
											if (ftestRatiovals != null && ftestRatiovals.size() > 0) {
												allColumnNames.add("F statistic");
												allColumnNames.add("F test pval");
												allColumnNames.add("Adj F test pval");
												nonFeatureColCount += 3;
											}
											allColumnNames.add(methodName + " pval");
											allColumnNames.add("Adj pval");
											nonFeatureColCount += 2;
										}

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

											if(featureInfoRows!=null) {
												for(int k=0;k<featureInfoRows[i].length;k++) {
													temp.add(featureInfoRows[i][k]);
												}
											}

											pValRows.add(temp);

										}

										Object [][] pValLimitedData = new Object[pValRows.size()][featureInfoColNames.length+nonFeatureColCount];
										
										
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

		JMenuItem mntmSelFeatureCols = new JMenuItem("Select Feature Metadata Cols");
		mntmSelFeatureCols.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				DEAColumnSelectFrame deaColSelect = new DEAColumnSelectFrame(currentPanel);
				MetaOmGraph.getDesktop().add(deaColSelect);
				deaColSelect.moveToFront();
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
				final TreeSearchQueryConstructionPanelDEA tsp = new TreeSearchQueryConstructionPanelDEA(
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
		dataToolbar.add(Box.createHorizontalGlue());
		dataToolbar.add(advFilterButton);


		panel_1.add(dataToolbar);

		
		
		//populating table
		
		rowIndicesMapping = new int[MetaOmGraph.activeProject.getRowCount()];
		for(int i=0; i < rowIndicesMapping.length; i++) {
			rowIndicesMapping[i] = -1;
		}
		//Combining the Diff corr columns and feature info columns into masterData before updating the table
		if(featureNames != null) {

			//Get Feature metadata rows
			List<String> rowNames = featureNames;
			int[] rowIndices = MetaOmGraph.activeProject.getRowIndexbyName(rowNames, true);
			
			for(int j = 0; j < rowIndices.length; j++) {
				rowIndicesMapping[rowIndices[j]] = j;
			}

			Object[][] featureInfoRows = MetaOmGraph.activeProject.getRowNames(rowIndices);	
			String [] featureInfoColNames = MetaOmGraph.activeProject.getInfoColumnNames();
			
			
			List<String> allColumnNames = new ArrayList<String>();
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

			if(featureInfoColNames!=null) {
				for(String col : featureInfoColNames) {
					allColumnNames.add(col);
				}
			}

			Object [][] masterData = new Object[featureNames.size()][allColumnNames.size()];
			String [] masterColumns = new String[allColumnNames.size()];
			
			for(int i=0; i< allColumnNames.size(); i++) {
				masterColumns[i] = (String) allColumnNames.get(i);
			}

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

	
	/**
	 * Method that creates a volcano chart from the selected rows
	 */
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
		// set decimal formatter to all cols except first
		int colCount = 6;
		if (testPvals != null) {
			if (ftestRatiovals != null && ftestRatiovals.size() > 0) {
				colCount = 9;
			}
		}
		else {
			colCount = 6;
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
		
		int nonFeatureColCount = 4;
		if (testPvals != null) {
			if (ftestRatiovals != null && ftestRatiovals.size() > 0) {
				nonFeatureColCount += 3;
			}
			nonFeatureColCount += 2;
		}
		
		
		String[] originalMasterColumns = getMasterTableColumns();
		String[] projectedMasterColumns = new String[selectedCols.size()+nonFeatureColCount];
		
		
		for(int k = 0; k < nonFeatureColCount ; k++) {
			projectedMasterColumns[k] = originalMasterColumns[k];
		}
		
		for(int l = 0; l < selectedCols.size(); l++) {
			projectedMasterColumns[l+nonFeatureColCount] = selectedCols.get(l);
		}
		
		setSelectedAndProjectedTableColumns(projectedMasterColumns);
		
		
		
		Object[][] originalMasterData = getMasterTableData();
		Object[][] projectedMasterData = new Object[originalMasterData.length][projectedMasterColumns.length];
		
		for(int a = 0; a < originalMasterData.length; a++) {
			for(int b = 0; b < nonFeatureColCount; b++) {
				projectedMasterData[a][b] = originalMasterData[a][b];
			}
			for(int c = 0; c < selectedFeatureMetadataCols.length; c++) {
				projectedMasterData[a][c+nonFeatureColCount] = originalMasterData[a][selectedFeatureMetadataCols[c]+nonFeatureColCount];
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




}
