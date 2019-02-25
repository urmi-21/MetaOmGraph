package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.collections.CollectionUtils;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;
import edu.iastate.metnet.metaomgraph.calculateLogFC;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;

import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DifferentialExpFrame extends JInternalFrame {

	private JComboBox comboBox;
	private JComboBox comboBox_1;

	private JTextField txtGroup1;
	private JTextField txtGroup2;

	private JScrollPane jscp1;
	private JTable tableGrp1;

	private JScrollPane jscp2;
	private JTable tableGrp2;

	private MetadataHybrid mdob;
	private MetaOmProject myProject;

	private boolean[] excludedCopy;

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

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
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
			public void actionPerformed(ActionEvent e) {
				//check if two lists (sets) are disjoint
				List<String> grp1=getAllRows(tableGrp1);
				if(grp1==null||grp1.size()<1) {
					JOptionPane.showMessageDialog(null, "Please check the lists. First list is empty", "Empty list", JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<String> grp2=getAllRows(tableGrp2);
				if(grp2==null||grp2.size()<1) {
					JOptionPane.showMessageDialog(null, "Please check the lists. Second list is empty", "Empty list", JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<String> intrsection=(List<String>) CollectionUtils.intersection(grp1, grp2);
				if(intrsection.size()>0) {
					JOptionPane.showMessageDialog(null, "The two groups must be disjoint. Please check the lists", "Please check the lists", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String selectedFeatureList=comboBox.getSelectedItem().toString();
				String selectedMethod=comboBox_1.getSelectedItem().toString();
				//if paired test is selected lists must be equal size
				if(selectedMethod.equals("Paired t Test")) {
					if(grp1.size()!=grp2.size()) {
						JOptionPane.showMessageDialog(null, "The two groups must be equal to perform paired test. Please check the lists.", "Unequal lists", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				//warning message is size is less than 30
				if(grp1.size()<30 || grp2.size()<30) {
					JOptionPane.showMessageDialog(null, "Smaller group size will have lower statistical power. Group size > 30 is recommended", "Unequal lists", JOptionPane.WARNING_MESSAGE);
				}
				
				//all checks completed, compute logFC				
				calculateLogFC ob = new calculateLogFC(selectedFeatureList, "", myProject, false);
				
				
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
		JPanel topbtnPnl2 = new JPanel(new FlowLayout());
		JButton sendLeft = new JButton("<<");
		sendLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveSelectedtoLeft();
			}
		});
		topbtnPnl2.add(sendLeft);

		JLabel lblGroupName_1 = new JLabel("Group name:");
		topbtnPnl2.add(lblGroupName_1);
		topbtnPnl2.add(txtGroup2);
		panel_3.add(topbtnPnl2, BorderLayout.NORTH);

		// add table2
		jscp2 = new JScrollPane();
		tableGrp2 = initTableModel();
		//updateTableData(tableGrp2, mdob.getMetadataCollection().getAllDataCols());
		updateTableData(tableGrp2,null);
		jscp2.setViewportView(tableGrp2);
		panel_3.add(jscp2, BorderLayout.CENTER);

		JButton btnAdd2 = new JButton("Add");
		btnAdd2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> queryRes = showSearchMetadataPanel();
				if(queryRes==null || queryRes.size()<1) {
					return;
				}
				//JOptionPane.showConfirmDialog(null, "match:" + queryRes.toString());
				addRows(tableGrp2, queryRes);
			}
		});
		JPanel btnPnl2 = new JPanel(new FlowLayout());
		btnPnl2.add(btnAdd2);
		JButton btnRem2 = new JButton("Remove");
		btnRem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedRows(tableGrp2);
			}
		});
		btnPnl2.add(btnRem2);
		JButton btnSearch2 = new JButton("Search");
		btnSearch2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// search in list by metadatata
				List<String> queryRes = showSearchMetadataPanel();
				if(queryRes==null || queryRes.size()<1) {
					return;
				}
				// get intersection
				List<String> allRows=getAllRows(tableGrp2);
				List<String> res= (List<String>) CollectionUtils.intersection(queryRes, allRows);
				//set selected and bring to top
				setSelectedRows(res, tableGrp2);
			}
		});
		btnPnl2.add(btnSearch2);
		panel_3.add(btnPnl2, BorderLayout.SOUTH);

		JPanel panel_4 = new JPanel();
		splitPane.setLeftComponent(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));

		txtGroup1 = new JTextField();
		txtGroup1.setText("Group1");
		txtGroup1.setColumns(10);
		JPanel topbtnPnl1 = new JPanel(new FlowLayout());
		JButton sendRight = new JButton(">>");
		sendRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				moveSelectedtoRight();
			}
		});

		JLabel lblGroupName = new JLabel("Group name:");
		topbtnPnl1.add(lblGroupName);
		topbtnPnl1.add(txtGroup1);
		topbtnPnl1.add(sendRight);

		panel_4.add(topbtnPnl1, BorderLayout.NORTH);

		// add table1
		jscp1 = new JScrollPane();
		tableGrp1 = initTableModel();
		updateTableData(tableGrp1, null);
		jscp1.setViewportView(tableGrp1);
		panel_4.add(jscp1, BorderLayout.CENTER);

		JButton btnAdd1 = new JButton("Add");
		btnAdd1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> queryRes = showSearchMetadataPanel();
				if(queryRes==null || queryRes.size()<1) {
					return;
				}
				//JOptionPane.showConfirmDialog(null, "match:" + queryRes.toString());
				addRows(tableGrp1, queryRes);
			}
		});
		JPanel btnPnl1 = new JPanel(new FlowLayout());
		btnPnl1.add(btnAdd1);
		JButton btnRem1 = new JButton("Remove");
		btnRem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeSelectedRows(tableGrp1);
				
			}
		});
		btnPnl1.add(btnRem1);
		JButton btnSearch1 = new JButton("Search");
		btnSearch1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// search in list by metadatata
				List<String> queryRes = showSearchMetadataPanel();
				if(queryRes==null || queryRes.size()<1) {
					return;
				}
				// get intersection
				List<String> allRows=getAllRows(tableGrp1);
				List<String> res= (List<String>) CollectionUtils.intersection(queryRes, allRows);
				//set selected and bring to top
				setSelectedRows(res, tableGrp1);
				
			}
		});
		btnPnl1.add(btnSearch1);
		panel_4.add(btnPnl1, BorderLayout.SOUTH);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

	}

	private JTable initTableModel() {
		JTable table = new JTable() {
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
		String[] methods = new String[] { "M-W U Test", "t Test", "Welch Test", "Paired t Test" };
		comboBox_1 = new JComboBox(methods);
	}

	/**
	 * move selected rows from table 1 to table 2
	 */
	private void moveSelectedtoRight() {
		List<String> selected1 = getSelectedRows(tableGrp1);
		addRows(tableGrp2, selected1);
		removeSelectedRows(tableGrp1);
	}

	private void moveSelectedtoLeft() {
		List<String> selected2 = getSelectedRows(tableGrp2);
		addRows(tableGrp1, selected2);
		removeSelectedRows(tableGrp2);

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
		//JOptionPane.showMessageDialog(null, "sel:" + res);

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
		//JOptionPane.showMessageDialog(null, "tokeep:" + toKeep.toString());
		updateTableData(table, toKeep);

	}

	private void addRows(JTable table, List<String> toAdd) {
		toAdd.addAll(getAllRows(table));
		//JOptionPane.showMessageDialog(null, "toAdd:" + toAdd.toString());
		updateTableData(table, toAdd);
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
	
	/**
	 * @author urmi
	 * bring the matched items to top and set them as selected
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
		final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(myProject, false);
		final MetadataQuery[] queries;
		queries = tsp.showSearchDialog();
		if (tsp.getQueryCount() <= 0) {
			System.out.println("Search dialog cancelled");
			// User didn't enter any queries
			return null;
		}
		// final int[] result = new int[myProject.getDataColumnCount()];
		Collection<Integer> result = new ArrayList<>();
		new AnimatedSwingWorker("Searching...", true) {
			@Override
			public Object construct() {
				ArrayList<Integer> toAdd = new ArrayList<Integer>(result.size());
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
		}.start();

		// create a split index with "hits" as one category and all others as second
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

}
