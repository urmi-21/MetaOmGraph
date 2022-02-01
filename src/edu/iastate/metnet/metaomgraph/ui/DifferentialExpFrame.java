package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.collections4.CollectionUtils;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;
import edu.iastate.metnet.metaomgraph.CalculateLogFC;
import edu.iastate.metnet.metaomgraph.DifferentialExpResults;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;


/**
 * 
 * UI class to choose which features to use in the Differential Expression Analysis
 *
 */

public class DifferentialExpFrame extends TaskbarInternalFrame {

	private JComboBox comboBox;
	private JComboBox comboBox_1;

	JLabel lblN2;
	JLabel lblN1;

	private JTextField txtGroup1;
	private JTextField txtGroup2;

	private JScrollPane jscp1;
	private JTable tableGrp1;

	private JScrollPane jscp2;
	private JTable tableGrp2;

	private MetadataHybrid mdob;
	private MetaOmProject myProject;

	private boolean[] excludedCopy;

	private JCheckBox chckbxSaveResultsWith;
	
	private DifferentialExpFrame thisInternalFrame;
	/**
	 * Default Properties
	 */

	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColorEven();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColorOdd();
	private Color HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
	private Color HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DifferentialExpFrame frame = new DifferentialExpFrame();
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
	public DifferentialExpFrame() {

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setTitle("Differential expression analysis");

		// init objects
		myProject = MetaOmGraph.getActiveProject();
		mdob = myProject.getMetadataHybrid();
		if (mdob == null) {
			JOptionPane.showMessageDialog(null, "Error. No metadata found", "Error", JOptionPane.ERROR_MESSAGE);
			dispose();
		}

		// get excluded
		boolean[] excluded = MetaOmAnalyzer.getExclude();
		if (excluded != null) {
			excludedCopy = new boolean[excluded.length];
			System.arraycopy(excluded, 0, excludedCopy, 0, excluded.length);
		}

		initComboBoxes();
		
		thisInternalFrame = getCurrentFrame();

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		chckbxSaveResultsWith = new JCheckBox("Save results with MOG");
		chckbxSaveResultsWith.setSelected(true);

		panel.add(chckbxSaveResultsWith);
		JLabel lblTop = new JLabel("Select feature list");
		panel.add(lblTop);
		panel.add(comboBox);
		JLabel label = new JLabel("                             ");
		panel.add(label);
		JLabel lblSelectMethod = new JLabel("Select method");
		panel.add(lblSelectMethod);
		panel.add(comboBox_1);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// check if two lists (sets) are disjoint
				List<String> grp1 = getAllRows(tableGrp1);
				if (grp1 == null || grp1.size() < 1) {
					JOptionPane.showMessageDialog(null, "Please check the lists. First list is empty", "Empty list",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<String> grp2 = getAllRows(tableGrp2);
				if (grp2 == null || grp2.size() < 1) {
					JOptionPane.showMessageDialog(null, "Please check the lists. Second list is empty", "Empty list",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<String> intrsection = (List<String>) CollectionUtils.intersection(grp1, grp2);
				if (intrsection.size() > 0) {
					JOptionPane.showMessageDialog(null, "The two groups must be disjoint. Please check the lists",
							"Please check the lists", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String selectedFeatureList = comboBox.getSelectedItem().toString();
				String selectedMethod = comboBox_1.getSelectedItem().toString();
				// if paired test is selected lists must be equal size
				if (selectedMethod.equals("Paired t-test") || selectedMethod.equals("Wilcoxon Signed Rank Test")
						|| selectedMethod.equals("Permutation test (paired samples)")) {
					if (grp1.size() != grp2.size()) {
						JOptionPane.showMessageDialog(null,
								"The two groups must be equal to perform paired test. Please check the input lists.",
								"Unequal lists", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				// warning message is size is less than 30
				if (grp1.size() < 30 || grp2.size() < 30) {
					JOptionPane.showMessageDialog(null,
							"Smaller group size will have lower statistical power. Group size > 30 is recommended",
							"Small groups", JOptionPane.WARNING_MESSAGE);
				}

				// all checks completed, compute logFC

				// measure time
				// long startTime = System.nanoTime();

				CalculateLogFC ob = new CalculateLogFC(selectedFeatureList, grp1, grp2, txtGroup1.getText(),
						txtGroup2.getText(), myProject, comboBox_1.getSelectedIndex());

				// start calculation
				ob.doCalc();

				// check if calculation was cancelled
				// compare if pvalues math size of gene list. hacky way to do
				if (ob.testPV()==null || ob.testPV().size() < myProject.getGeneListRowNumbers(selectedFeatureList).length) {
					// JOptionPane.showMessageDialog(null, "cancelled");
					return;
				}

				// save object
				String id = "";
				if (chckbxSaveResultsWith.isSelected()) {
					id = JOptionPane.showInputDialog(MetaOmGraph.getMainWindow(),
							"Please enter a name for this analysis:", "Save differential expression results", 2);
					if (id == null) {
						// cancelled
						return;
					}
					id = id.trim();
					if (myProject.diffExpNameExists(id)) {
						while (myProject.diffExpNameExists(id)) {
							id = JOptionPane.showInputDialog(MetaOmGraph.getDesktop(),
									"A previous analysis exists with the same name. Please enter a different name for this analysis",
									"Save differential expression results", 2);
							if (id == null) {
								// cancelled
								return;
							}
							id = id.trim();
						}
					}

					//if name starts with number its illegal
					if (Character.isDigit(id.charAt(0))) {
						while (Character.isDigit(id.charAt(0))) {
							id = JOptionPane.showInputDialog(MetaOmGraph.getDesktop(),
									"Name can't start with a number. Please enter a different name for this analysis",
									"Save differential expression results", 2);
							if (id == null) {
								// cancelled
								return;
							}
							id = id.trim();
						}
					}
				}

				final String id_f = id;
				//cant'start with string

				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				HashMap<String,Object> result = new HashMap<String,Object>();
				ActionProperties deaAction = new ActionProperties("differential-expression-analysis",actionMap,dataMap,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));

				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								try {
									// create DifferentialExpResults object to store results in MOG
									DifferentialExpResults diffExpObj = new DifferentialExpResults(id_f, comboBox_1.getSelectedIndex(),
											txtGroup1.getText(), txtGroup2.getText(), getAllRows(tableGrp1).size(),
											getAllRows(tableGrp2).size(), selectedFeatureList, MetaOmGraph.getInstance().getTransform(),
											ob.getFeatureNames(), ob.getMean1(), ob.getMean2(), ob.ftestRatios(), ob.ftestPV(),
											ob.testPV());

									if (chckbxSaveResultsWith.isSelected()) {
										myProject.addDiffExpRes(diffExpObj.getID(), diffExpObj);
									}

									// display result using diffExpObj
									logFCResultsFrame frame = null;
									frame = new logFCResultsFrame(diffExpObj, myProject);
									frame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
									//frame.setTitle("DE results");


									if(MetaOmGraph.getDEAResultsFrame()!=null && !MetaOmGraph.getDEAResultsFrame().isClosed()) {
										MetaOmGraph.getDEAResultsFrame().addTabToFrame(frame, diffExpObj.getID(), deaAction.getActionNumber());
										MetaOmGraph.getDEAResultsFrame().addTabListToFrame(frame.getGeneLists(), diffExpObj.getID());
										MetaOmGraph.getDEAResultsFrame().setTitle("DE results");
										MetaOmGraph.getDEAResultsFrame().getDesktopPane().getDesktopManager().maximizeFrame(MetaOmGraph.getDEAResultsFrame());
										MetaOmGraph.getDEAResultsFrame().getDesktopPane().getDesktopManager().minimizeFrame(MetaOmGraph.getDEAResultsFrame());
										MetaOmGraph.getDEAResultsFrame().moveToFront();
										frame.setEnabled(true);
									}
									else {
										MetaOmGraph.setDEAResultsFrame(new StatisticalResultsFrame("DEA","DEA Results"));
										MetaOmGraph.getDEAResultsFrame().addTabToFrame(frame, diffExpObj.getID(), deaAction.getActionNumber());
										MetaOmGraph.getDEAResultsFrame().addTabListToFrame(frame.getGeneLists(), diffExpObj.getID());
										MetaOmGraph.getDesktop().add(MetaOmGraph.getDEAResultsFrame());
										MetaOmGraph.getDEAResultsFrame().setTitle("DE results");
										MetaOmGraph.getDEAResultsFrame().setVisible(true);
										MetaOmGraph.getDEAResultsFrame().getDesktopPane().getDesktopManager().maximizeFrame(MetaOmGraph.getDEAResultsFrame());
										MetaOmGraph.getDEAResultsFrame().getDesktopPane().getDesktopManager().minimizeFrame(MetaOmGraph.getDEAResultsFrame());
										MetaOmGraph.getDEAResultsFrame().moveToFront();
										frame.setEnabled(true);
									}


								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									PrintWriter pw = new PrintWriter(sw);
									e.printStackTrace(pw);
									String sStackTrace = sw.toString();

									JDialog jd = new JDialog();
									JTextPane jt = new JTextPane();
									jt.setText(sStackTrace);
									jt.setBounds(10, 10, 300, 100);
									jd.getContentPane().add(jt);
									jd.setBounds(100, 100, 500, 200);
									jd.setVisible(true);
								}
							}
						});
						return null;
					}
				}.start();

				try {

					actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
					actionMap.put("section", "All");

					dataMap.put("Selected Feature List", selectedFeatureList);
					dataMap.put("Selected Method", selectedMethod);
					dataMap.put("Group 1 Name", txtGroup1.getText());
					dataMap.put("Group 2 Name", txtGroup2.getText());
					dataMap.put("Group 1 List", grp1);
					dataMap.put("Group 2 List", grp2);
					dataMap.put("Save Results", chckbxSaveResultsWith.isSelected());
					dataMap.put("Analysis Name", id);

					result.put("result", "OK");

					deaAction.logActionProperties();

				}
				catch(Exception e1) {

				}

			}
		});
		panel_1.add(btnOk);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		panel_2.add(splitPane, BorderLayout.CENTER);
		splitPane.setResizeWeight(0.5);

		JPanel panel_3 = new JPanel();
		splitPane.setRightComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		txtGroup2 = new JTextField();
		txtGroup2.setText("Group2");
		txtGroup2.setColumns(10);
		JPanel topbtnPnl2 = new JPanel();
		JButton sendLeft = new JButton("<<");
		sendLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveSelectedtoLeft();
			}
		});
		topbtnPnl2.add(sendLeft);

		JLabel lblGroupName_1 = new JLabel("Group name:");
		topbtnPnl2.add(lblGroupName_1);
		topbtnPnl2.add(txtGroup2);
		panel_3.add(topbtnPnl2, BorderLayout.NORTH);
		//topbtnPnl2.setLayout(new BoxLayout(topbtnPnl2, BoxLayout.LINE_AXIS));
		topbtnPnl2.setLayout(new FlowLayout());
		lblN2 = new JLabel("n=0");
		lblN2.setFont(new Font("Tahoma", Font.BOLD, 13));
		topbtnPnl2.add(lblN2);

		// add table2
		jscp2 = new JScrollPane();
		tableGrp2 = initTableModel();
		// updateTableData(tableGrp2, mdob.getMetadataCollection().getAllDataCols());
		updateTableData(tableGrp2, null);
		jscp2.setViewportView(tableGrp2);
		panel_3.add(jscp2, BorderLayout.CENTER);

		JButton btnAddImport2 = new JButton("Import");
		btnAddImport2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ImportSamplesFrame frame = new ImportSamplesFrame(thisInternalFrame, tableGrp2);
				FrameModel metadataColumnModel = new FrameModel("DEA", "Import samples by name", 45);
				frame.setModel(metadataColumnModel);
				
				frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				frame.setResizable(false);
				MetaOmGraph.getDesktop().add(frame);
				frame.setVisible(true);
				frame.toFront();
			}
		});
		
		JButton btnAdd2 = new JButton("Add");
		btnAdd2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> queryRes = showSearchMetadataPanel();
				if (queryRes == null || queryRes.size() < 1) {
					return;
				}
				// JOptionPane.showConfirmDialog(null, "match:" + queryRes.toString());
				addRows(tableGrp2, queryRes);
			}
		});
		JPanel btnPnl2 = new JPanel(new FlowLayout());
		btnPnl2.add(btnAddImport2);
		btnPnl2.add(btnAdd2);
		JButton btnRem2 = new JButton("Remove");
		btnRem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeSelectedRows(tableGrp2);
			}
		});
		btnPnl2.add(btnRem2);
		JButton btnSearch2 = new JButton("Search");
		btnSearch2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// search in list by metadatata
				List<String> queryRes = showSearchMetadataPanel();
				if (queryRes == null || queryRes.size() < 1) {
					return;
				}
				// get intersection
				List<String> allRows = getAllRows(tableGrp2);
				List<String> res = (List<String>) CollectionUtils.intersection(queryRes, allRows);
				// set selected and bring to top
				setSelectedRows(res, tableGrp2);
			}
		});
		btnPnl2.add(btnSearch2);
		panel_3.add(btnPnl2, BorderLayout.SOUTH);

		//btnPnl2.setLayout(new BoxLayout(btnPnl2, BoxLayout.LINE_AXIS));
		btnPnl2.setLayout(new FlowLayout());
		JPanel panel_4 = new JPanel();
		splitPane.setLeftComponent(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));

		txtGroup1 = new JTextField();
		txtGroup1.setText("Group1");
		txtGroup1.setColumns(10);
		JPanel topbtnPnl1 = new JPanel();
		JButton sendRight = new JButton(">>");
		sendRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				moveSelectedtoRight();
			}
		});

		lblN1 = new JLabel("n=0");
		lblN1.setFont(new Font("Tahoma", Font.BOLD, 13));
		topbtnPnl1.add(lblN1);

		JLabel lblGroupName = new JLabel("Group name:");
		topbtnPnl1.add(lblGroupName);
		topbtnPnl1.add(txtGroup1);
		topbtnPnl1.add(sendRight);

		panel_4.add(topbtnPnl1, BorderLayout.NORTH);

		//topbtnPnl1.setLayout(new BoxLayout(topbtnPnl1, BoxLayout.LINE_AXIS));
		topbtnPnl1.setLayout(new FlowLayout());

		// add table1
		jscp1 = new JScrollPane();
		tableGrp1 = initTableModel();
		updateTableData(tableGrp1, null);
		jscp1.setViewportView(tableGrp1);
		panel_4.add(jscp1, BorderLayout.CENTER);

		JButton btnAddImport = new JButton("Import");
		btnAddImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ImportSamplesFrame frame = new ImportSamplesFrame(thisInternalFrame, tableGrp1);
				FrameModel metadataColumnModel = new FrameModel("DEA", "Import samples by name", 45);
				frame.setModel(metadataColumnModel);
				
				frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				frame.setResizable(false);
				MetaOmGraph.getDesktop().add(frame);
				frame.setVisible(true);
				frame.toFront();
			}
		});
		
		JButton btnAdd1 = new JButton("Add");
		btnAdd1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> queryRes = showSearchMetadataPanel();
				if (queryRes == null || queryRes.size() < 1) {
					return;
				}
				// JOptionPane.showConfirmDialog(null, "match:" + queryRes.toString());
				addRows(tableGrp1, queryRes);

			}
		});
		JPanel btnPnl1 = new JPanel();
		btnPnl1.add(btnAddImport);
		btnPnl1.add(btnAdd1);
		JButton btnRem1 = new JButton("Remove");
		btnRem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeSelectedRows(tableGrp1);

			}
		});
		btnPnl1.add(btnRem1);
		JButton btnSearch1 = new JButton("Search");
		btnSearch1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// search in list by metadatata
				List<String> queryRes = showSearchMetadataPanel();
				if (queryRes == null || queryRes.size() < 1) {
					return;
				}
				// get intersection
				List<String> allRows = getAllRows(tableGrp1);
				List<String> res = (List<String>) CollectionUtils.intersection(queryRes, allRows);
				// set selected and bring to top
				setSelectedRows(res, tableGrp1);

			}
		});
		btnPnl1.add(btnSearch1);
		panel_4.add(btnPnl1, BorderLayout.SOUTH);

		//btnPnl1.setLayout(new BoxLayout(btnPnl1, BoxLayout.LINE_AXIS));
		btnPnl1.setLayout(new FlowLayout());

		// frame properties
		this.setClosable(true);
		pack();

		int defaultWidth = this.getWidth();
		int defaultHeight = MetaOmGraph.getMainWindow().getHeight() / 2;
		DifferentialExpFrame thisFrame = this;

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub

				if(thisFrame.getWidth() < defaultWidth || thisFrame.getHeight() < defaultHeight) {
					thisFrame.setSize(defaultWidth, defaultHeight);
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}
		});

		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

		FrameModel diffExpFrameModel = new FrameModel("DEA","Differential Expression Analysis",12);
		setModel(diffExpFrameModel);
	}

	
	public DifferentialExpFrame getCurrentFrame() {
		return this;
	}
	
	
	private JTable initTableModel() {
		JTable table = new JTable() {
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
					return String.class;
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(model);
		// set properties
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		return table;
	}

	private void updateTableData(JTable table, List<String> rows) {
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		// add data
		String dcName = mdob.getDataColName();
		tablemodel.addColumn(dcName);

		if (rows == null) {
			return;
		}

		// convert list to set remove duplicates
		Set<String> tempSet = new TreeSet<String>(rows);
		rows = new ArrayList<>();
		rows.addAll(tempSet);
		Vector temp = null;
		for (String s : rows) {
			temp = new Vector<>();
			temp.add(s);
			tablemodel.addRow(temp);
		}
	}

	private void initComboBoxes() {
		comboBox = new JComboBox(MetaOmGraph.getActiveProject().getGeneListNames());
		String[] methods = new String[] { "M-W U test", "Student's t-test", "Welch's t-test", "Permutation test",
				"Paired t-test", "Wilcoxon Signed Rank Test", "Permutation test (paired samples)" };
		comboBox_1 = new JComboBox(methods);
	}

	/**
	 * move selected rows from table 1 to table 2
	 */
	private void moveSelectedtoRight() {
		List<String> selected1 = getSelectedRows(tableGrp1);
		addRows(tableGrp2, selected1);
		removeSelectedRows(tableGrp1);
		updateLabelN();
	}

	private void moveSelectedtoLeft() {
		List<String> selected2 = getSelectedRows(tableGrp2);
		addRows(tableGrp1, selected2);
		removeSelectedRows(tableGrp2);
		updateLabelN();

	}

	/**
	 * get selected rows from a table
	 * 
	 * @param table
	 * @return
	 */
	private List<String> getSelectedRows(JTable table) {
		return getSelectedRows(table, false);
	}

	private List<String> getSelectedRows(JTable table, boolean invert) {
		int selected[] = table.getSelectedRows();
		List<String> res = new ArrayList<>();
		for (int i = 0; i < selected.length; i++) {
			String thisRow = "";
			thisRow = (String) table.getValueAt(selected[i], 0);
			res.add(thisRow);
		}
		// JOptionPane.showMessageDialog(null, "sel:" + res);

		if (invert) {
			List<String> temp = new ArrayList<>();
			for (int i = 0; i < table.getRowCount(); i++) {
				String thisRow = "";
				thisRow = (String) table.getValueAt(i, 0);
				if (!res.contains(thisRow)) {
					temp.add(thisRow);
				}
			}
			res = temp;
		}

		return res;
	}

	private void removeSelectedRows(JTable table) {
		List<String> toKeep = getSelectedRows(table, true);
		// JOptionPane.showMessageDialog(null, "tokeep:" + toKeep.toString());
		updateTableData(table, toKeep);
		updateLabelN();

	}

	public void addRows(JTable table, List<String> toAdd) {
		toAdd.addAll(getAllRows(table));
		// JOptionPane.showMessageDialog(null, "toAdd:" + toAdd.toString());
		updateTableData(table, toAdd);
		updateLabelN();
	}

	private List<String> getAllRows(JTable table) {
		// get existing rows
		List<String> temp = new ArrayList<>();
		for (int i = 0; i < table.getRowCount(); i++) {
			String thisRow = "";
			thisRow = (String) table.getValueAt(i, 0);
			temp.add(thisRow);
		}
		return temp;
	}

	private void updateLabelN() {

		int n1 = getAllRows(tableGrp1).size();
		lblN1.setText("n=" + String.valueOf(n1));
		int n2 = getAllRows(tableGrp2).size();
		lblN2.setText("n=" + String.valueOf(n2));

	}

	/**
	 * @author urmi bring the matched items to top and set them as selected
	 * @param res
	 * @param tab
	 */
	private void setSelectedRows(List<String> res, JTable tab) {
		DefaultTableModel model = (DefaultTableModel) tab.getModel();
		List<String> newVals = new ArrayList<>();
		// bring matched values at top
		String temp;
		int total_matches = 0;
		for (int c = 0; c < model.getRowCount(); c++) {
			temp = model.getValueAt(c, 0).toString();
			if (res.contains(temp)) {
				newVals.add("\t:::" + temp);
				total_matches++;
			} else {
				newVals.add("~:::" + temp);
			}
		}
		java.util.Collections.sort(newVals);
		// JOptionPane.showMessageDialog(null, newVals.toString());
		model.setRowCount(0);
		for (int i = 0; i < newVals.size(); i++) {
			Vector<String> v = new Vector<>();
			v.add(newVals.get(i).split(":::")[1]);
			model.addRow(v);

		}
		if (total_matches > 0) {
			tab.setRowSelectionInterval(0, total_matches - 1);
		}
	}

	private List<String> showSearchMetadataPanel() {
		// search datacolumns by metadata and add results
		// display query panel
		try {
			final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(myProject, false);
			final MetadataQuery[] queries;
			queries = tsp.showSearchDialog();
			if (tsp.getQueryCount() <= 0) {
				// System.out.println("Search dialog cancelled");
				// User didn't enter any queries
				return null;
			}
			// final int[] result = new int[myProject.getDataColumnCount()];
			Collection<Integer> result = new ArrayList<>();
			new AnimatedSwingWorker("Searching...", true) {
				@Override
				public Object construct() {
					try {
						ArrayList<Integer> toAdd = new ArrayList<Integer>();
						for (int i = 0; i < myProject.getDataColumnCount(); i++) {
							toAdd.add(i);
						}
						Integer[] hits = myProject.getMetadataHybrid().search(queries, tsp.matchAll());

						// remove excluded cols from list
						// urmi
						boolean[] excluded = excludedCopy;
						if (excluded != null) {
							List<Integer> temp = new ArrayList<>();
							for (Integer i : hits) {
								if (!excluded[i]) {
									temp.add(i);
								}
							}
							hits = new Integer[temp.size()];
							hits = temp.toArray(hits);
						}
						int index;
						for (index = 0; index < hits.length; index++) {
							result.add(hits[index]);
							toAdd.remove(hits[index]);
						}

						return null;
					}
					catch(Exception e) {
						return null;
					}
				}
			}.start();

			// category
			if (result.size() < 1) {
				JOptionPane.showMessageDialog(null, "No hits found", "No hits", JOptionPane.INFORMATION_MESSAGE);
				return null;
			} else {
				List<String> hitsColumns = new ArrayList<>();
				// get datacolumn names
				for (int i : result) {
					hitsColumns.add(myProject.getDataColumnHeader(i));
				}
				return hitsColumns;
			}

		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null, "No hits found", "No hits", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
	}

}