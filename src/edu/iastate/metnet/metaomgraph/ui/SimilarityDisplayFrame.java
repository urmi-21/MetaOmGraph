package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.h2.command.dml.Set;

import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.chart.HistogramChart;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class SimilarityDisplayFrame extends JInternalFrame {

	private HashMap<String, Double> data;
	private String metricName;
	private JTable table;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimilarityDisplayFrame frame = new SimilarityDisplayFrame();
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
	public SimilarityDisplayFrame() {
		this(null, "");
		setBounds(100, 100, 450, 300);
	}

	public SimilarityDisplayFrame(HashMap<String, Double> res, String mName) {
		this.data = res;
		this.metricName = mName;

		setBounds(100, 100, 450, 300);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExport = new JMenuItem("Export");
		mntmExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// save to file
				Utils.saveJTabletofile(table);
			}
		});
		mnFile.add(mntmExport);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		JMenuItem mntmAsMatrix = new JMenuItem("As matrix");
		mntmAsMatrix.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				initTableMatrix();
				table.repaint();
				// important set viewport after updating model
				scrollPane.setViewportView(table);
			}
		});
		mnView.add(mntmAsMatrix);

		JMenuItem mntmAsLiist = new JMenuItem("As list");
		mntmAsLiist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initTableList();
				table.repaint();
				scrollPane.setViewportView(table);
			}
		});
		mnView.add(mntmAsLiist);
		
		JMenu mnPlot = new JMenu("Plot");
		menuBar.add(mnPlot);
		
		JMenuItem mntmHistogram = new JMenuItem("Histogram");
		mntmHistogram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {


				// plot histogram of current pvalues in table
				double[] data = new double[table.getRowCount()];
				for (int r = 0; r < table.getRowCount(); r++) {

					data[r] = (double) table.getModel().getValueAt(r, table.getColumn(metricName).getModelIndex());
				}
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {// get data for selected rows
							int nBins = data.length/100;
							if(nBins<10) {
								nBins=10;
							}
							HistogramChart f = new HistogramChart(null, nBins, null, 2, data);
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
		mnPlot.add(mntmHistogram);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		initTableList();
		// initTableMatrix();
		scrollPane.setViewportView(table);

		// frame properties
		this.setClosable(true);
		pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
	}

	private void initTableMatrix() {
		java.util.Set<String> vars = new TreeSet<String>();
		HashMap<String, Double> dataCopy = (HashMap<String, Double>) data.clone();
		Iterator it = dataCopy.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry) it.next();
			String[] temp = pair.getKey().toString().split(":");
			vars.add(temp[0]);
			vars.add(temp[1]);
			it.remove(); // avoids a ConcurrentModificationException
		}

		// JOptionPane.showMessageDialog(null, "set:" + vars.toString());
		List varList = new ArrayList(vars);

		DefaultTableModel model = new DefaultTableModel();
		table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		//set font of first column
		// a custom renderer which uses a special font
		DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
		    Font font = new Font("Garamond", Font.BOLD, 14);
		    @Override
		    public Component getTableCellRendererComponent(JTable table,
		            Object value, boolean isSelected, boolean hasFocus,
		            int row, int column) {
		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
		                row, column);
		        setFont(font);
		        return this;
		    }
		};
		
		
		// add columns
		model.addColumn(" ");
		for (int i = 0; i < varList.size(); i++) {
			model.addColumn(varList.get(i));
		}
		// add values to table
		for (int i = 0; i < varList.size(); i++) {
			Vector thisRow = new Vector<>();
			// add var name to row
			thisRow.add(varList.get(i));
			// add n more values
			for (int j = 0; j < varList.size(); j++) {
				if (i == j) {
					thisRow.add(1);
				} else {
					// key is varX:varY or the other way so one should match
					String thisKey_1 = varList.get(i) + ":" + varList.get(j);
					String thisKey_2 = varList.get(j) + ":" + varList.get(i);

					if (data.get(thisKey_1) != null) {
						thisRow.add(Double
								.valueOf(String.format("%.4g%n", Double.valueOf(data.get(thisKey_1).toString()))));
					} else {
						thisRow.add(Double
								.valueOf(String.format("%.4g%n", Double.valueOf(data.get(thisKey_2).toString()))));
					}
				}
			}
			model.addRow(thisRow);
		}
		
		//change font of first column to bold
		table.getColumnModel().getColumn(0).setCellRenderer(r);
	}

	private void initTableList() {
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
		table = new JTable(model);
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
		model.addColumn(metricName);
		// add rows to table
		// copy data before modifying it
		HashMap<String, Double> dataCopy = (HashMap<String, Double>) data.clone();
		Iterator it = dataCopy.entrySet().iterator();
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
	}

}
