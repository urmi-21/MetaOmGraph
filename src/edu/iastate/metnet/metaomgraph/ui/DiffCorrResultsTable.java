package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.analysis.function.Atanh;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.logging.log4j.Logger;

import edu.iastate.metnet.metaomgraph.AdjustPval;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.chart.BoxPlot;
import edu.iastate.metnet.metaomgraph.chart.HistogramChart;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.chart.ScatterPlotChart;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.DecimalFormatRenderer;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;

public class DiffCorrResultsTable extends JInternalFrame {
	
	/*Harsha- Added logger */

	private static final Logger logger = MetaOmGraph.logger;
	private JTable table;
	private List<String> featureNames;
	private List<Double> corrVals1;
	private List<Double> corrVals2;
	private List<Double> zVals1;
	private List<Double> zVals2;
	private List<Double> diff;
	private List<Double> zScores;
	private List<Double> pVals;
	private List<Double> adjpVals;
	private MetaOmProject myProject;

	private int n1;
	private int n2;
	private double pvThresh = 2;
	String pvAdjMethod;

	/**
	 * Default Properties
	 */

	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColor1();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColor2();
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

	public DiffCorrResultsTable(List<String> featureNames, int grp1Size, int grp2Size, List<Double> corrVals1,
			List<Double> corrVals2, List<Double> zvals1, List<Double> zvals2, List<Double> diffZvals,
			List<Double> zscores, List<Double> pvals, MetaOmProject myProject) {
		this.myProject = myProject;
		this.featureNames = featureNames;
		this.n1 = grp1Size;
		this.n2 = grp2Size;
		this.corrVals1 = corrVals1;
		this.corrVals2 = corrVals2;

		zVals1 = zvals1;
		zVals2 = zvals2;
		diff = diffZvals;
		zScores = zscores;
		pVals = pvals;

		if (pVals != null) {
			adjpVals = AdjustPval.computeAdjPV(pVals, pvAdjMethod); // by default use B-H correction
		}

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.NORTH);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);

		initTableModel();
		updateTable();
		scrollPane.setViewportView(table);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

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
						mergeListAction.logActionProperties(logger);
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
				updateTable();
			}
		});
		mnEdit.add(mntmPvalueCorrection);

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

							ScatterPlotChart f = new ScatterPlotChart(rowIndices, 0, myProject);
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

							BoxPlot f = new BoxPlot(plotData, 0, myProject);
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
							HistogramChart f = new HistogramChart(selected, nBins, myProject, 1, null);
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

		// frame properties
		this.setClosable(true);
		setTitle("Differential Correlation Results");
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

	}

	private void initTableModel() {
		table = new JTable() {
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

	private void updateTable() {

		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		// add data
		tablemodel.addColumn("Name");
		tablemodel.addColumn("r1");
		tablemodel.addColumn("r2");
		tablemodel.addColumn("z1");
		tablemodel.addColumn("z2");
		tablemodel.addColumn("z1-z2");
		tablemodel.addColumn("zScore");
		tablemodel.addColumn("p-value");
		tablemodel.addColumn("Adj p-value");

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

			// add ith row in table
			tablemodel.addRow(temp);

		}

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));

		// set decimal formatter to all cols except first
		for (int i = 1; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i)
					.setCellRenderer(new edu.iastate.metnet.metaomgraph.DecimalFormatRenderer());
		}

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
		rowIndices = myProject.getRowIndexbyName(names, true);

		return rowIndices;
	}

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
					HistogramChart f = new HistogramChart(null, nBins, null, 2, data);
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
