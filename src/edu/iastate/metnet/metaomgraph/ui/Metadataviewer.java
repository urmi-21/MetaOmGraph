package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.dizitart.no2.Document;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

//this class is used to display the tabular metadata
public class Metadataviewer extends JDialog implements ActionListener {

	private List<Document> metadata = null;
	private String[] headers = null;
	private int[] displayCol = null; // array contains info about which cols are displayed 1 means display 0 means
										// hide
	private JTable table = null;
	private TableColumnModel tColmodel = null;
	private static MetadataCollection metadataColl = null;

	public Metadataviewer(List<Document> csv_metadata, String[] headers, String title) {
		// TODO Auto-generated constructor stub
		// metadataColl = MetaOmGraph.getActiveProject().returnCollection();
		this.setLayout(new BorderLayout());
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("Metadata Viewer Main");
		this.setJMenuBar(createmenubar());
		metadata = csv_metadata;
		this.headers = headers;
		displayCol = new int[headers.length];
		Arrays.fill(displayCol, 1); // all 1 means all cols are displayed
		this.add(createTable(), BorderLayout.CENTER);
		this.setSize(900, 900);
		tColmodel = table.getColumnModel();
		this.pack();
		this.setTitle(title);

	}

	//this is called inside MOG
	public Metadataviewer(String title) {
		// TODO Auto-generated constructor stub
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("Metadata Viewer");
		this.setJMenuBar(createmenubar());
		metadataColl = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataCollection();
		metadata = metadataColl.getAllData();
		this.headers = metadataColl.getHeaders();
		displayCol = new int[headers.length];
		Arrays.fill(displayCol, 1); // all 1 means all cols are displayed
		this.add(createTable());
		this.setSize(800, 800);
		this.setTitle(title);
	}

	private JMenuBar createmenubar() {
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem eMenuItem = new JMenuItem("Exit");
		eMenuItem.setToolTipText("Exit application");
		eMenuItem.addActionListener((ActionEvent event) -> {
			this.dispose();
		});
		file.add(eMenuItem);
		menubar.add(file);

		JMenu edit = new JMenu("Edit");
		JMenuItem properties = new JMenuItem("Properties");
		JMenuItem search = new JMenuItem("Search");
		JMenuItem filter = new JMenuItem("Filter");
		JMenuItem hide = new JMenuItem("Show/Hide columns");
		properties.setToolTipText("Set table properties");
		search.setToolTipText("Search the table");
		filter.setToolTipText("Filter data");
		hide.setToolTipText("Show/Hide columns");
		hide.setActionCommand("hide");
		search.setActionCommand("search");
		properties.setActionCommand("properties");
		edit.add(hide);
		edit.add(search);
		edit.add(filter);
		edit.add(properties);
		menubar.add(edit);

		hide.addActionListener(this);
		search.addActionListener(this);
		properties.addActionListener(this);

		return menubar;
	}

	private JToolBar createToolbar() {
		JToolBar tb = null;

		return tb;
	}

	private JPanel createTable() {
		JPanel panel = new JPanel();
		String[] colNames = headers;
		panel.setLayout(new BorderLayout());
		table = new JTable() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}
		};
		table.setModel(new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// add data
		// for each row add each coloumn
		for (int i = 0; i < metadata.size(); i++) {
			// create a temp string storing all col values for a row
			String[] temp = new String[colNames.length];
			for (int j = 0; j < colNames.length; j++) {

				// add col name
				if (i == 0) {
					tablemodel.addColumn(colNames[j]);
				}

				temp[j] = metadata.get(i).get(colNames[j]).toString();
			}

			// add ith row in table
			tablemodel.addRow(temp);

		}

		// table.setBackground(Color.GREEN);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);

		// JScrollPane tableContainer = new JScrollPane(table);
		// panel.add(tableContainer, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		// scrollPane.setSize(900, 900);
		panel.add(scrollPane, BorderLayout.CENTER);

		// add filter button
		JButton jButton1 = new JButton("Filter");
		jButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("button pressed");
				// filter metadata
				metadata = metadataColl.fullTextSearch("study_accession", "SRP00", "", false);

				// remove data from table
				for (int i = tablemodel.getRowCount() - 1; i >= 0; i--) {
					tablemodel.removeRow(i);
				}
				// add data
				// for each row add each coloumn
				for (int i = 0; i < metadata.size(); i++) {
					// create a temp string storing all col values for a row
					String[] temp = new String[colNames.length];
					for (int j = 0; j < colNames.length; j++) {

						temp[j] = metadata.get(i).get(colNames[j]).toString();
					}

					// add ith row in table
					tablemodel.addRow(temp);

				}

			}
		});
		// panel.add(jButton1, BorderLayout.SOUTH);
		// tableContainer.setBackground(Color.CYAN);
		panel.setBackground(Color.BLUE);
		panel.setSize(900, 900);
		return panel;

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getActionCommand() == "hide") {
			// JOptionPane.showMessageDialog(null, "hiding...");
			showHideframe();
		}
		if (arg0.getActionCommand() == "search") {
			// JOptionPane.showMessageDialog(null, "hiding...");
			showSearchframe();
		}
		if (arg0.getActionCommand() == "properties") {
			// JOptionPane.showMessageDialog(null, "edit look...");
			 editProperties();
		}
		

	}

	public void showHideframe() {
		JFrame frame = new JFrame("Select Columns");

		JPanel panel = new JPanel();
		JPanel buttonpanel = new JPanel();
		JButton okbutton = new JButton("OK");
		JButton rbutton = new JButton("RESET");
		panel.setLayout(new GridLayout(6, 6));

		JCheckBox[] cboxes = new JCheckBox[headers.length];
		for (int i = 0; i < headers.length; i++) {
			cboxes[i] = new JCheckBox(headers[i]);
			// System.out.println(displayCol[i]);
			if (displayCol[i] == 1) {
				cboxes[i].setSelected(true);
			}
			panel.add(cboxes[i]);
		}
		buttonpanel.add(okbutton, BorderLayout.CENTER);
		buttonpanel.add(rbutton, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.NORTH);
		frame.add(buttonpanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setSize(500, 400);
		frame.pack();
		frame.setVisible(true);

		okbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// when button is clicked hide all cols which are not selected
				// get list of columns to hide
				// remove col at ind 0
				// add all cols and remove those needed
				resetTable();
				// int i=0;
				int len = cboxes.length;

				/*
				 * while(i<len) { if(cboxes[i].isSelected()==false) { //TableColumn column =
				 * tColmodel.getColumn(i); //table.removeColumn(column);
				 * System.out.println(cboxes[i].getText()); System.out.println(i);
				 * toremove.add(i); toremove_name.add(cboxes[i].getText());
				 * 
				 * } i++; }
				 * 
				 * for(int j=0;j<toremove.size();j++) { System.out.println(toremove.get(j)); int
				 * thiscol= tColmodel.getColumnIndex(toremove_name.get(j)); //TableColumn column
				 * = tColmodel.getColumn(table.getValueAt( 0,
				 * table.getColumn(toremove_name.get(j)).getModelIndex()); TableColumn column =
				 * tColmodel.getColumn(thiscol);
				 * System.out.println(column.getHeaderValue().toString());
				 * table.removeColumn(column); }
				 */

				for (int i = 0; i < cboxes.length; i++) {
					// remove unchecked cols
					if (cboxes[i].isSelected() == false) {
						System.out.println("false:" + cboxes[i].getText());
						int thiscol = tColmodel.getColumnIndex(cboxes[i].getText());
						if (thiscol >= 0) {
							TableColumn column = tColmodel.getColumn(thiscol);
							System.out.println("hv" + column.getHeaderValue().toString());
							table.removeColumn(column);
						}
					}
				}

				// resetTable();
			}
		});

		rbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetTable();
				Arrays.fill(displayCol, 1); // all 1 means all cols are displayed
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();
				frame.dispose();
				showHideframe();

			}
		});

	}

	public void resetTable() {
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		// for each row add each coloumn
		String[] colNames = headers;
		for (int i = 0; i < metadata.size(); i++) {
			// create a temp string storing all col values for a row
			String[] temp = new String[colNames.length];
			for (int j = 0; j < colNames.length; j++) {

				// add col name
				if (i == 0) {
					tablemodel.addColumn(colNames[j]);
				}

				temp[j] = metadata.get(i).get(colNames[j]).toString();
			}

			// add ith row in table
			tablemodel.addRow(temp);
			table.repaint();

		}
	}

	public void showSearchframe() {
		JFrame frame = new JFrame("Search keywords: ");
		JPanel panel = new JPanel();
		JPanel buttonpanel = new JPanel();
		JPanel searchpanel = new JPanel();
		JButton okbutton = new JButton("OK");
		JButton okbutton2 = new JButton("OK2");
		JButton okbutton3 = new JButton("OK3");
		JButton abutton = new JButton("Select All");
		panel.setLayout(new GridLayout(6, 6));
		JComboBox jcb1=new JComboBox();
		JLabel jlb2 = new JLabel("Select column to search in");
		// add all column names to panel
		JCheckBox[] cboxes = new JCheckBox[headers.length];
		for (int i = 0; i < headers.length; i++) {
			cboxes[i] = new JCheckBox(headers[i]);
			//add to combobox
			jcb1.addItem(headers[i]);
			System.out.println(displayCol[i]);
			if (displayCol[i] == 1) {
				cboxes[i].setSelected(true);
			}
			//panel.add(cboxes[i]);
		}
		panel.add(jlb2);
		panel.add(jcb1);

		JTextField jtf1 = new JTextField(50);
		JLabel jlb1 = new JLabel("Enter keyword to search(Regex allowed)");
		searchpanel.add(jlb1);
		searchpanel.add(jtf1);
		buttonpanel.add(abutton);
		//buttonpanel.add(okbutton);
		//buttonpanel.add(okbutton2);
		buttonpanel.add(okbutton3);
		frame.add(panel, BorderLayout.NORTH);
		frame.add(searchpanel, BorderLayout.CENTER);
		frame.add(buttonpanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setSize(500, 400);
		frame.pack();
		frame.setVisible(true);

		abutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < cboxes.length; i++) {
					cboxes[i] = new JCheckBox(headers[i]);
					cboxes[i].setSelected(true);
				}
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();

			}
		});

		okbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tosearch = jtf1.getText();
				if (tosearch.equals("")) {
					System.out.println("Noting to search");
					return;
				}

				System.out.println("Searching:" + tosearch);
				ArrayList<String> cols = new ArrayList<String>();
				// add all col names where search will be performed
				for (int i = 0; i < cboxes.length; i++) {
					if (cboxes[i].isSelected()) {
						cols.add(cboxes[i].getText());
						// System.out.println(cboxes[i].getText());
					}
				}

				// start search
				// DefaultTableModel currtableModel = (DefaultTableModel) table.getModel();
				// To empty the table before search
				// currtableModel.setRowCount(0);
				// DefaultTableModel newtableModel = (DefaultTableModel) table.getModel();
				JTable newtable = new JTable(table.getModel());
				System.out.println("total cols in new:" + newtable.getColumnCount());
				System.out.println("total cols in org:" + table.getColumnCount());
				TableColumnModel newtColmodel = newtable.getColumnModel();
				// remove unchecked columns
				for (int i = 0; i < cboxes.length; i++) {
					// remove unchecked cols
					if (cboxes[i].isSelected() == false) {
						int thiscol = -1;
						try {
							// System.out.println("false:" + cboxes[i].getText());
							thiscol = newtColmodel.getColumnIndex(cboxes[i].getText());
						} catch (IllegalArgumentException il) {
							// System.out.println("errr");

						}
						if (thiscol >= 0) {
							TableColumn column = newtColmodel.getColumn(thiscol);
							// System.out.println("hv" + column.getHeaderValue().toString());
							newtable.removeColumn(column);
						}
					}
				}
				System.out.println("cols left:");
				for (int i = 0; i < newtable.getColumnCount(); i++) {
					System.out.println(newtable.getColumnName(i));
				}

				System.out.println("total rows now:" + newtable.getRowCount());
				// perform search in newtable
				DefaultTableModel newtableModel = (DefaultTableModel) newtable.getModel();
				final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(newtableModel);
				String text = tosearch;
				if (text.length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter(text));
				}
				System.out.println("total rows after:" + newtable.getRowCount());
				displayTable(newtable);

			}
		});

		okbutton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTable newtable = new JTable(table.getModel());
				newtable.setAutoCreateRowSorter(true);
				newtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				newtable.setPreferredScrollableViewportSize(table.getPreferredSize());
				newtable.setFillsViewportHeight(true);
				displayTable(newtable);
				final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(newtable.getModel());
				newtable.setRowSorter(sorter);
				String text = jtf1.getText();
				System.out.println("searc:" + text);
				if (text.length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter(text));
				}

			}
		});

		okbutton3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get filtered document from MOGCollection class and display results

				List<Document> res = null;
				String strUniqueID = jcb1.getSelectedItem().toString();
				String key =  jcb1.getSelectedItem().toString();
				String value = jtf1.getText();
				//metadataColl.fullTextSearch("study_accession", "SRP00", "", false);
				res = metadataColl.fullTextSearch(key, value, strUniqueID, true);
				if (res.size() <= 0) {
					System.out.println("search failed");
					JOptionPane.showMessageDialog(null, "Nothing Found..");
				} else {
					String title = "Search res for " + value;
					
					Metadataviewer o = new Metadataviewer(res, metadataColl.getHeaders(), title);
					o.setVisible(true);
				}

			}
		});

	}
	
	public void editProperties() {
		JFrame frame = new JFrame("Edit Properties");
		JPanel jp = new JPanel();
		JLabel jlb1=new JLabel("select background color");
		JButton selectbckcol=new JButton("select");
		
		jp.add(jlb1);
		jp.add(selectbckcol);
		frame.add(jp);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setSize(500, 400);
		frame.pack();
		frame.setVisible(true);

		
		
		selectbckcol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Color initialcolor=Color.WHITE;    
				Color color=JColorChooser.showDialog(null,"Select a color",initialcolor);    
				table.setBackground(color);    
			}
		});
		
	}

	public void displayTable(JTable t) {
		JFrame frame = new JFrame("Search Results");
		JPanel jp = new JPanel();
		JScrollPane scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		// scrollPane.setSize(900, 900);
		jp.add(scrollPane, BorderLayout.CENTER);

		jp.setBackground(Color.BLUE);
		jp.setSize(900, 900);
		frame.add(jp);
		frame.setSize(500, 400);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		System.out.println("SSMAIN");
		List<Document> data;
		MetadataCollection obj = new MetadataCollection();
		// read the data from file
		try {
			obj.readMetadataTextFile("D:\\MOGdata\\mog_testdata\\ATdata\\eswdata\\sd.txt", "\\t", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		metadataColl = obj;
		Metadataviewer o = new Metadataviewer(obj.getAllData(), obj.getHeaders(),"Metadata Viewer");
		o.setVisible(true);

	}
}
