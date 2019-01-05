package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

//import com.itextpdf.xmp.impl.Utils;


import apple.awt.CButton;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.ui.MetadataTableDisplayPanel.AlphanumericComparator;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class SearchByExpressionFrame extends JInternalFrame {
	private JTextField textField;
	private JTextField textField_1;
	private JComboBox comboBox;
	private JPanel panel;
	private JPanel panel_1;
	private JSplitPane splitPane;
	private MetaOmProject myProject;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JTable table;
	private JTable table_1;
	private boolean searchDC = true;

	/**
	 * Default Properties
	 */
	private Color SELECTIONBCKGRND = Color.black;
	private Color BCKGRNDCOLOR1 = Color.white;
	private Color BCKGRNDCOLOR2 = new ColorUIResource(216, 236, 213);
	private JButton btnGo;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmExportResults;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SearchByExpressionFrame frame = new SearchByExpressionFrame(null);
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
	public SearchByExpressionFrame(MetaOmProject project) {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		myProject = project;

		getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblSearchFor = new JLabel("Search for");

		JLabel lblWithExpressionLevel = new JLabel("with expression level between");

		textField = new JTextField();
		textField.setColumns(7);

		JLabel lblAnd = new JLabel("and");

		textField_1 = new JTextField();
		textField_1.setColumns(7);

		panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		splitPane = new JSplitPane();
		splitPane.setDividerSize(2);
		splitPane.setResizeWeight(.71d);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		scrollPane = new JScrollPane();
		scrollPane.setToolTipText("Select rows and click Go. Results will be displayed in the table to the right.");
		splitPane.setLeftComponent(scrollPane);
		table = new JTable();
		scrollPane.setViewportView(table);

		scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);

		table_1 = new JTable();
		scrollPane_1.setViewportView(table_1);

		comboBox = new JComboBox();
		/*
		 * String[] cboxOpts = new String[2]; cboxOpts[0] =
		 * myProject.getInfoColumnNames()[myProject.getDefaultColumn()]; cboxOpts[1] =
		 * myProject.getMetadataHybrid().getDataColName(); comboBox = new JComboBox();
		 */
		initFrame();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selected = comboBox.getSelectedIndex();
				if (selected == 0) {
					searchDC = true;
				} else {
					searchDC = false;
				}

				initLeftTable();

			}
		});

		// add to panel
		panel.add(lblSearchFor);
		panel.add(comboBox);
		panel.add(lblWithExpressionLevel);
		panel.add(textField);
		panel.add(lblAnd);
		panel.add(textField_1);

		btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double minVal = 0;
				double maxVal = 0;
				List<String> colNames = new ArrayList<>();
				try {
					minVal = Double.parseDouble(textField.getText());
					maxVal = Double.parseDouble(textField_1.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Please check min and max values", "Values error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// get results for selected rows
				int[] selected = table.getSelectedRows();
				if(selected ==null || selected.length<1) {
					return;
				}
				List<List<String>> res = new ArrayList<>();
				if (searchDC) {
					try {
						for (int s = 0; s < selected.length; s++) {
							int thisIndex=table.convertRowIndexToModel( selected[s]);
							List<String> temp = new ArrayList<>();
							colNames.add(myProject.getDefaultRowNames(thisIndex));
							double[] values = myProject.getAllData(thisIndex);
							for (int i = 0; i < values.length; i++) {
								try {
									if (values[i] >= minVal && values[i] <= maxVal)
										temp.add(myProject.getDataColumnHeader(i));
								} catch (NumberFormatException nfe) {
									System.err.println("value at " + i + " is not a number");
								}
							}
							res.add(temp);
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					// JOptionPane.showMessageDialog(null, "cn:" + colNames.toString());
					// JOptionPane.showMessageDialog(null, "res:" + res.toString());
				} else {
					try {
						for (int s = 0; s < selected.length; s++) {
							int thisIndex=table.convertRowIndexToModel(selected[s]);
							List<String> temp = new ArrayList<>();
							colNames.add(myProject.getDataColumnHeader(thisIndex));
							double[] values = myProject.getDataForColumn(thisIndex);
							for (int i = 0; i < values.length; i++) {
								try {
									if (values[i] >= minVal && values[i] <= maxVal)
										temp.add(myProject.getDefaultRowNames(i));
								} catch (NumberFormatException nfe) {
									System.err.println("value at " + i + " is not a number");
								}
							}
							res.add(temp);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				initRightTable(colNames, res);
			}
		});
		panel.add(btnGo);

		// frame properties
		this.setClosable(true);
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmExportResults = new JMenuItem("Export results");
		mntmExportResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				edu.iastate.metnet.metaomgraph.utils.Utils.saveJTabletofile(table_1);
			}
		});
		mnFile.add(mntmExportResults);
	}

	public void initFrame() {
		// init conbo box
		String[] cboxOpts = null;
		if (myProject.getMetadataHybrid() != null) {
			cboxOpts = new String[2];
			cboxOpts[0] = myProject.getInfoColumnNames()[myProject.getDefaultColumn()];
			cboxOpts[1] = myProject.getMetadataHybrid().getDataColName();
		} else {
			cboxOpts = new String[1];
			cboxOpts[0] = myProject.getInfoColumnNames()[myProject.getDefaultColumn()];
		}

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(cboxOpts));

		textField = new JTextField("0");
		textField.setColumns(7);
		textField_1 = new JTextField("100");
		textField_1.setColumns(7);
		initLeftTable();
	}

	public void initLeftTable() {

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

				} else {
					c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}

		};

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

		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// add data
		if (searchDC) {
			// get all rows
			String[] rowNames = myProject.getAllDefaultRowNames();
			tablemodel.addColumn(comboBox.getItemAt(0).toString());
			for (int i = 0; i < rowNames.length; i++) {
				String[] temp = new String[1];
				temp[0] = rowNames[i];
				tablemodel.addRow(temp);
			}

		} else {
			// get all rows
			String[] rowNames = myProject.getDataColumnHeaders();
			tablemodel.addColumn(comboBox.getItemAt(1).toString());
			for (int i = 0; i < rowNames.length; i++) {
				String[] temp = new String[1];
				temp[0] = rowNames[i];
				tablemodel.addRow(temp);
			}

		}

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

		// sorter.setComparator(0, new
		// MetadataTableDisplayPanel.AlphanumericComparator());

		table.setRowSorter(sorter);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		scrollPane.setViewportView(table);

	}

	public void initRightTable(List<String> colNames, List<List<String>> data) {

		table_1 = new JTable() {
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

				} else {
					c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}

		};

		table_1.setModel(new DefaultTableModel() {

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

		DefaultTableModel tablemodel = (DefaultTableModel) table_1.getModel();
		// add data
		int maxRows = -1;
		int maxInd = -1;
		int minRows = 999999999;
		int minInd = 999999999;
		List<String> intersection = new ArrayList<>();
		List<String> union = new ArrayList<>();
		// add intersection and union
		colNames.add("Intersection");
		colNames.add("Union");
		for (int j = 0; j < colNames.size(); j++) {
			tablemodel.addColumn(colNames.get(j));

			// last two colnames are intersect and union. there is no list in data foor
			// those right now
			if (j < colNames.size() - 2) {
				if (data.get(j).size() > maxRows) {
					maxRows = data.get(j).size();
					maxInd = j;
				}

				if (data.get(j).size() < minRows) {
					minRows = data.get(j).size();
					minInd = j;
				}
			}

		}
		List<String> maxList = data.get(maxInd);
		List<String> minList = data.get(minInd);
		// JOptionPane.showMessageDialog(null, "mI" + minInd + "ml:" +
		// minList.toString());
		// get intersection
		for (int j = 0; j < minList.size(); j++) {

			String thisItem = minList.get(j);
			boolean breakflag = false;
			for (int i = 0; i < data.size(); i++) {
				if (!data.get(i).contains(thisItem)) {
					breakflag = true;
					break;
				}
			}
			if (!breakflag) {
				intersection.add(thisItem);
			}
		}

		// get union
		for (int i = 0; i < data.size(); i++) {
			union.addAll(data.get(i));
		}

		Set<String> set = new HashSet<String>(union);
		union = new ArrayList<>(set);
		// JOptionPane.showMessageDialog(null, "alldataU" +union.toString());

		// add row data
		data.add(intersection);
		data.add(union);
		if (union.size() > maxRows) {
			maxRows = union.size();
		}

		for (int i = 0; i < maxRows; i++) {
			Vector temp = new Vector<>();
			for (int j = 0; j < colNames.size(); j++) {
				if (i < data.get(j).size()) {
					temp.add(data.get(j).get(i));
				} else {
					temp.add("");
				}

			}

			tablemodel.addRow(temp);
		}

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

		// sorter.setComparator(0, new
		// MetadataTableDisplayPanel.AlphanumericComparator());

		table_1.setRowSorter(sorter);

		table_1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table_1.setPreferredScrollableViewportSize(table.getPreferredSize());
		table_1.setFillsViewportHeight(true);
		table_1.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		scrollPane_1.setViewportView(table_1);

	}

}
