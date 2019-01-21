package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.chart.HistogramChart;
import edu.iastate.metnet.metaomgraph.ui.MetadataTableDisplayPanel.AlphanumericComparator;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class logFCResultsFrame extends JInternalFrame {
	private JTable table;
	private List<String> featureNames;
	private List<Double> mean1;
	private List<Double> mean2;

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
			public void run() {
				try {
					logFCResultsFrame frame = new logFCResultsFrame(null, null, null);
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
	public logFCResultsFrame(List<String> featureNames, List<Double> mean1, List<Double> mean2) {
		this.featureNames = featureNames;
		this.mean1 = mean1;
		this.mean2 = mean2;
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		initTable();
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmSave = new JMenuItem("Save to file");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTabletofile(table);
			}
		});
		mnFile.add(mntmSave);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmExportSelectedTo = new JMenuItem("Export selected to list");
		mnEdit.add(mntmExportSelectedTo);
		
		JMenu mnPlot = new JMenu("Plot");
		menuBar.add(mnPlot);
		
		JMenu mnSelected = new JMenu("Selected");
		mnPlot.add(mnSelected);
		
		JMenuItem mntmLineChart = new JMenuItem("Liine Chart");
		mnSelected.add(mntmLineChart);
		
		JMenuItem mntmScatterplot = new JMenuItem("Scatter Plot");
		mnSelected.add(mntmScatterplot);
		
		JMenuItem mntmBoxPlot = new JMenuItem("Box Plot");
		mnSelected.add(mntmBoxPlot);
		
		JMenuItem mntmHistogram = new JMenuItem("Histogram");
		mnSelected.add(mntmHistogram);
		
		JMenuItem mntmFcHistogram = new JMenuItem("FC histogram");
		mntmFcHistogram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// plot histogram of current pvalues in table
				double [] fcdata=new double[table.getRowCount()];
				for(int r=0;r<table.getRowCount();r++) {
					
					fcdata[r]=(double) table.getModel().getValueAt(r, table.getColumn("logFC").getModelIndex() );
				}
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {// get data for selected rows
							int nBins = 10;
							HistogramChart f = new HistogramChart(null, nBins, null, 2,fcdata);
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
		mnPlot.add(mntmFcHistogram);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

	}

	private void initTable() {

		table = new JTable() {
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}

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

					// for (int j = 0; j < table.getColumnCount(); j++) {
					/*
					 * for (Integer j : toHighlight.keySet()) {
					 * 
					 * String type = (String) getModel().getValueAt(modelRow, j); if
					 * (highlightThisRow(j, type)) { c.setBackground(HIGHLIGHTCOLOR); if
					 * (!highlightedRows.contains(modelRow)) { highlightedRows.add(modelRow); } }
					 * else { if (row % 2 == 0) { c.setBackground(BCKGRNDCOLOR1); } else {
					 * c.setBackground(BCKGRNDCOLOR2); } } }
					 */

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
		};
		table.setModel(model);
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// add data
		tablemodel.addColumn("Feature");
		tablemodel.addColumn("Mean1");
		tablemodel.addColumn("Mean2");
		tablemodel.addColumn("logFC");
		// for each row add each coloumn
		for (int i = 0; i < featureNames.size(); i++) {
			// create a temp string storing all col values for a row
			Vector temp = new Vector<>();
			temp.add(featureNames.get(i));
			temp.add(mean1.get(i));
			temp.add(mean2.get(i));
			temp.add(mean1.get(i) - mean2.get(i));
			// add ith row in table
			tablemodel.addRow(temp);

		}

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));

	}

}
