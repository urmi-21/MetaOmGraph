package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * This class is frame to display correlation from meta-analysis model
 * display genename, correlation, p value q value calculate conf interval etc.
 * the values are loaded from CorrelationMeta object 
 */

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import edu.iastate.metnet.metaomgraph.AdjustPval;
import edu.iastate.metnet.metaomgraph.CorrelationMeta;
import edu.iastate.metnet.metaomgraph.CorrelationMetaCollection;
import edu.iastate.metnet.metaomgraph.DecimalFormatRenderer;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.chart.HistogramChart;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.chart.ScatterPlotChart;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Point;
import javax.swing.JSeparator;

public class CorrelationMetaTable extends TaskbarInternalFrame {
	private JTable table;
	private HashMap<String, CorrelationMetaCollection> metaCorrRes;
	private JScrollPane scrollPane;
	private JComboBox comboBox;
	private JLabel lblCorrInfo;
	private JPanel plotButtonsPanel;
	private JButton btnPlot;
	private MetaOmProject myProject;
	private static double alpha = 0.05; // default value
	private double minrVal = -99999;
	private double maxrVal = 99999;
	private double minpVal = -99999;
	private double maxpVal = -99999;

	// for multiple correction
	String pvAdjMethod;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					CorrelationMetaTable frame = new CorrelationMetaTable();
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
	public CorrelationMetaTable() {

		this(null);
		this.setSize(800, 500);
		setLocation(new Point(250, 0));
	}

	public CorrelationMetaTable(HashMap<String, CorrelationMetaCollection> metaCorrRes) {
		setLocation(new Point(250, 0));
		myProject = MetaOmGraph.activeProject;
		IconTheme theme = MetaOmGraph.getIconTheme();
		int width = MetaOmGraph.getMainWindow().getWidth();
		int height = MetaOmGraph.getMainWindow().getHeight();
		this.setSize(width - 200, height - 200);
		this.setLocation((width - this.getWidth()) / 2, (height - this.getHeight()) / 2);
		putClientProperty("JInternalFrame.frameType", "normal");
		// this.setSize(800,500);
		setTitle("Statistical infrence");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		this.metaCorrRes = metaCorrRes;
		setBounds(100, 100, 450, 300);

		FrameModel CorrelationMetaFrameModel = new FrameModel("Correlation","Metadata Correlation",9);
		setModel(CorrelationMetaFrameModel);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblChooseCorrelationData = new JLabel("Choose correlation data");
		panel.add(lblChooseCorrelationData, BorderLayout.WEST);

		comboBox = new JComboBox();
		panel.add(comboBox, BorderLayout.CENTER);
		// change data in table with combobox selection
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// Do Something
				loadDatainTable(comboBox.getSelectedItem().toString());
			}
		});

		JButton btnNewButton = new JButton("New button");
		// panel.add(btnNewButton, BorderLayout.EAST);
		// add info label
		lblCorrInfo = new JLabel("dasasas");
		lblCorrInfo.setToolTipText("Correlation information");
		lblCorrInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblCorrInfo.setBackground(Color.WHITE);
		lblCorrInfo.setFont(new Font("Garamond", Font.PLAIN, 18));
		panel.add(lblCorrInfo, BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		JButton btnNewButton_1 = new JButton("New buttonBottom");
		// panel_1.add(btnNewButton_1);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);

		DefaultTableModel model = new DefaultTableModel();
		table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		model.addColumn("Name");
		model.addColumn("r");
		model.addColumn("pval");
		model.addColumn("CI");
		model.addColumn("z");
		model.addColumn("Q");
		scrollPane.setViewportView(table);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmSaveTable = new JMenuItem("Save table");
		mntmSaveTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTabletofile(table, "Correlation Metadata Table");
			}
		});
		mnFile.add(mntmSaveTable);

		JMenuItem mntmLoadTable = new JMenuItem("Load table");
		// mnFile.add(mntmLoadTable);

		JMenu mnPlot = new JMenu("Plot");
		menuBar.add(mnPlot);

		loadJCombobox();
		loadDatainTable(comboBox.getSelectedItem().toString());

		JMenuItem mntmPlotLineChart = new JMenuItem("Line Chart");
		mntmPlotLineChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
		mnPlot.add(mntmPlotLineChart);

		JMenuItem mntmPlotScatterPlot = new JMenuItem("Scatter Plot");
		mntmPlotScatterPlot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
		mnPlot.add(mntmPlotScatterPlot);

		JSeparator separator = new JSeparator();
		mnPlot.add(separator);

		JMenuItem mntmPvalueHistogram = new JMenuItem("Histogram p-value");
		mntmPvalueHistogram.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// plot histogram of current pvalues in table
				double[] pdata = new double[table.getRowCount()];
				for (int r = 0; r < table.getRowCount(); r++) {

					pdata[r] = (double) table.getModel().getValueAt(r, table.getColumn("pval").getModelIndex());
				}
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {// get data for selected rows
							int nBins = 10;
							HistogramChart f = new HistogramChart(null, nBins, null, 2, pdata, false);
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
		mnPlot.add(mntmPvalueHistogram);

		JMenuItem mntmHistogramRValues = new JMenuItem("Histogram r values");
		mntmHistogramRValues.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// plot histogram of current pvalues in table
				double[] pdata = new double[table.getRowCount()];
				for (int r = 0; r < table.getRowCount(); r++) {

					pdata[r] = (double) table.getModel().getValueAt(r, table.getColumn("r").getModelIndex());
				}
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {// get data for selected rows
							int nBins = 10;
							HistogramChart f = new HistogramChart(null, nBins, null, 2, pdata, false);
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
		mnPlot.add(mntmHistogramRValues);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmAlphaForCi = new JMenuItem("alpha for CI");
		mntmAlphaForCi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// choose pval dialog
				boolean flag = false;
				while (flag == false) {
					double newalpha = 0;
					try {
						newalpha = Double.parseDouble(JOptionPane.showInputDialog("Please enter alpha"));
					} catch (java.lang.NullPointerException e) {

					}
					if (newalpha < 1 && newalpha > 0) {
						alpha = newalpha;
						flag = true;
						// update table
						loadDatainTable(comboBox.getSelectedItem().toString());
					} else {
						JOptionPane.showMessageDialog(null, "Please enter a valid alpha between 0 and 1");
						flag = false;
					}
				}
			}
		});

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
				loadDatainTable(comboBox.getSelectedItem().toString());

			}
		});
		mnEdit.add(mntmPvalueCorrection);
		mnEdit.add(mntmAlphaForCi);

		JMenuItem mntmRemoveCorrelation = new JMenuItem("Remove correlation");
		mntmRemoveCorrelation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(null, "Remove correlation");

				JDialog dialog = new JDialog();
				dialog.setLocationRelativeTo(null);
				dialog.setTitle("Please choose...");

				// display list of correlations and let user choose which one to remove
				DefaultTableModel model = new DefaultTableModel() {
				};
				model.addColumn("Name");
				// add correlation name to the displayed table
				for (Object s : metaCorrRes.keySet().toArray()) {
					Vector row = new Vector();
					row.add(s);
					model.addRow(row);
				}
				JTable tabNames = new JTable(model);
				tabNames.setAutoCreateRowSorter(true);
				tabNames.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				tabNames.setFont(new Font("Times New Roman", Font.PLAIN, 13));
				tabNames.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

				JScrollPane spTable = new JScrollPane(tabNames);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(spTable, BorderLayout.CENTER);
				JLabel lab1 = new JLabel("Please select the rows to remove and click remove");
				lab1.setFont(new Font("Times New Roman", Font.PLAIN, 13));
				panel.add(lab1, BorderLayout.NORTH);
				JButton removeButton = new JButton("Remove");
				// action performed
				removeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int[] selectedInd = tabNames.getSelectedRows();
						// JOptionPane.showMessageDialog(null, "remove:" +
						// Arrays.toString(selectedInd));
						for (int i = 0; i < selectedInd.length; i++) {
							String keyToRem = (String) tabNames
									.getValueAt(tabNames.convertRowIndexToModel(selectedInd[i]), 0);
							// JOptionPane.showMessageDialog(null, "removing:" + keyToRem);
							metaCorrRes.remove(keyToRem);
							/**
							 * TODO Remove correlation columns from main table
							 */
							String[] infocolnames = myProject.getInfoColumnNames();
							int colNum = 0;
							for (int j = 0; j < infocolnames.length; j++) {
								if (keyToRem.equals(infocolnames[j])) {
									myProject.deleteInfoColumn(j);
								}
							}

						}

						// if all values are removed
						if (metaCorrRes.isEmpty()) {
							JOptionPane.showMessageDialog(null, "All data deleted");
							dispose();
							dialog.dispose();
						} else {
							// reload checkbox and internal frame and finally dispose this dialog
							loadJCombobox();
							loadDatainTable(comboBox.getSelectedItem().toString());
							dialog.dispose();
						}
					}
				});
				panel.add(removeButton, BorderLayout.SOUTH);

				panel.setVisible(true);
				dialog.getContentPane().add(panel);

				dialog.pack();
				dialog.setVisible(true); // show the dialog on the screen
				// Do something here

			}
		});
		mnEdit.add(mntmRemoveCorrelation);

		JMenuItem mntmFilter = new JMenuItem("Filter");
		mntmFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FilterCorrMetaTablePanel optPanel = new FilterCorrMetaTablePanel();

				int res = JOptionPane.showConfirmDialog(null, optPanel, "Enter values", JOptionPane.OK_CANCEL_OPTION);
				if (res == JOptionPane.OK_OPTION) {
					loadDatainTable(comboBox.getSelectedItem().toString(), optPanel.getMinr(), optPanel.getMaxr(),
							optPanel.getMinp(), optPanel.getMaxp());
				} else {
					return;
				}
			}
		});
		mnEdit.add(mntmFilter);

	}

	/**
	 * load combobox
	 */
	private void loadJCombobox() {
		if (metaCorrRes != null) {
			// populate jcombobox
			comboBox.setModel(new DefaultComboBoxModel(metaCorrRes.keySet().toArray()));

		} else {
			JOptionPane.showMessageDialog(null, "Error!!! Metacorrelation List can't be null");
		}
	}

	/**
	 * load data for correlation with the key s
	 * 
	 * @param s
	 */
	private void loadDatainTable(String s) {
		// this is call called by default when no rows are filtered
		loadDatainTable(s, -9999, 9999, -9999, 9999);
	}

	/**
	 * Load data in table
	 * 
	 * @param s
	 *            name of correlation
	 * @param minr
	 *            minimum r val
	 * @param maxr
	 *            maximum r val
	 * @param minp
	 *            minimum p val
	 * @param maxp
	 *            maximum p val
	 */
	private void loadDatainTable(String s, double minr, double maxr, double minp, double maxp) {
		CorrelationMetaCollection cmcObj = metaCorrRes.get(s);
		// check values of table depending on cmcObj and populate the table
		int corrTypeId = cmcObj.getCorrTypeId();
		// list of all correlation i.e. for each row
		List<CorrelationMeta> corrList = cmcObj.getCorrList();

		if (corrList != null) {

			List<Double> pvalueList = new ArrayList<>(); // store all p values in this list use later for multiple
															// correction

			// if object is type of metacorrelation
			if (corrTypeId == 0) {
				DefaultTableModel model = new DefaultTableModel() {
					@Override
					public boolean isCellEditable(int row, int column) {
						// all cells false
						return false;
					}

					@Override
					public Class getColumnClass(int column) {
						switch (column) {
						case 0:
							return String.class;
						case 1:
							return Double.class;
						case 5:
							return Double.class;
						case 6:
							return Double.class;
						default:
							return Object.class;
						}
					}
				};
				table = new JTable(model);
				model.addColumn("Name");
				model.addColumn("r");
				double cilevel = (1 - alpha) * 100;
				model.addColumn(cilevel + "% CI for r");
				model.addColumn("z");
				model.addColumn("Q");
				model.addColumn("pval");
				table.setAutoCreateRowSorter(true);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
				table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				table.getColumnModel().getColumn(1).setCellRenderer(new DecimalFormatRenderer());
				table.getColumnModel().getColumn(2).setCellRenderer(new DecimalFormatRenderer());
				table.getColumnModel().getColumn(4).setCellRenderer(new DecimalFormatRenderer());
				table.getColumnModel().getColumn(5).setCellRenderer(new DecimalFormatRenderer());

				for (int i = 0; i < corrList.size(); i++) {
					CorrelationMeta thisObj = corrList.get(i);
					Vector row = new Vector();
					double thisr = thisObj.getrVal();
					double thisp = thisObj.getpVal();
					if (thisr >= minr && thisr <= maxr && thisp >= minp && thisp <= maxp) {
						row.add(thisObj.getName());
						row.add(thisObj.getrVal());
						row.add(thisObj.getrCI(alpha));
						row.add(thisObj.getzVal());
						row.add(thisObj.getqVal());
						row.add(thisObj.getpVal());
						pvalueList.add(thisObj.getpVal()); // add p values in the list
						model.addRow(row);
					}

				}

			} else {
				DefaultTableModel model = new DefaultTableModel() {
					@Override
					public boolean isCellEditable(int row, int column) {
						// all cells false
						return false;
					}

					@Override
					public Class getColumnClass(int column) {
						switch (column) {
						case 0:
							return String.class;
						case 1:
							return Double.class;
						case 2:
							return Double.class;
						case 3:
							return Double.class;
						default:
							return Object.class;
						}
					}
				};
				table = new JTable(model);
				model.addColumn("Name");
				model.addColumn("r");
				model.addColumn("pval");
				table.setAutoCreateRowSorter(true);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
				table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				table.getColumnModel().getColumn(1).setCellRenderer(new DecimalFormatRenderer());
				table.getColumnModel().getColumn(2).setCellRenderer(new DecimalFormatRenderer());

				for (int i = 0; i < corrList.size(); i++) {
					CorrelationMeta thisObj = corrList.get(i);
					Vector row = new Vector();

					double thisr = thisObj.getrVal();
					double thisp = thisObj.getpVal();

					if (thisr >= minr && thisr <= maxr && thisp >= minp && thisp <= maxp) {
						row.add(thisObj.getName());
						row.add(thisObj.getrVal());
						row.add(thisObj.getpVal());
						pvalueList.add(thisObj.getpVal());
						model.addRow(row);
					}
				}
			}

			// add adjusted p value
			AdjustPval.computeAdjPV(pvalueList, pvAdjMethod);
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.addColumn("Adj pval", AdjustPval.computeAdjPV(pvalueList, pvAdjMethod).toArray());

			scrollPane.setViewportView(table);
		}
		// update corr info label
		// String corrInfo=getcorrInfo(cmcObj);
		String infoText = cmcObj.getcorrInfo();

		lblCorrInfo.setText(infoText);
	}

	/**
	 * return alpha value
	 * 
	 * @return
	 */
	public static double getAlpha() {
		return alpha;
	}

	/**
	 * return indices of selected rows in table
	 * 
	 * @return
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

}
