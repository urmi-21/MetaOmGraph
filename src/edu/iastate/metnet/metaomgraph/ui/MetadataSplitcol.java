package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.dizitart.no2.Document;

import edu.iastate.metnet.metaomgraph.MetadataCollection;

import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.JSplitPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MetadataSplitcol extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTable table;
	private JTable table_1;
	private JComboBox comboBox;
	// default preview table size
	private int PSIZE = 1000000;
	// data variables
	private MetadataCollection obj = null;
	private String[] headers = null;
	List<String> colData = null;
	private ReadMetadata parent;

	/**
	 * Default Properties
	 */
	private Color SELECTIONBCKGRND = Color.black;
	private Color BCKGRNDCOLOR1 = Color.white;
	private Color BCKGRNDCOLOR2 = new ColorUIResource(216, 236, 213);
	private Color HIGHLIGHTCOLOR = Color.ORANGE;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MetadataSplitcol frame = new MetadataSplitcol();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MetadataSplitcol() {
		this(null, null);
	}

	/**
	 * Create the frame.
	 */
	public MetadataSplitcol(MetadataCollection obj, ReadMetadata p) {
		parent = p;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				p.enableNext();
			}
		});
		this.obj = obj;
		if (this.obj != null) {
			headers = this.obj.getHeaders();
		}
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setFont(new Font("Garamond", Font.BOLD, 16));
		panel.setForeground(Color.GREEN);
		panel.setBackground(Color.DARK_GRAY);
		contentPane.add(panel, BorderLayout.NORTH);

		JLabel lblSplitAColumn = new JLabel("Split a column");
		lblSplitAColumn.setForeground(Color.GREEN);
		lblSplitAColumn.setFont(new Font("Garamond", Font.BOLD, 16));
		panel.add(lblSplitAColumn);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.DARK_GRAY);
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel_1.add(btnCancel);

		JButton btnDone = new JButton("Done");
		btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				p.enableNext();
				p.toFront();
				dispose();
				addNewColumns();
			}
		});
		btnDone.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnDone.setBackground(Color.GRAY);
		panel_1.add(btnDone);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setResizeWeight(.071d);
		splitPane.setEnabled(false);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel(new BorderLayout());
		panel_2.setBackground(Color.GRAY);
		JPanel panel_2_1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel_2_1.setBackground(Color.GRAY);
		splitPane.setLeftComponent(panel_2);

		JLabel lblChooseColumn = new JLabel("Choose column");
		lblChooseColumn.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_2_1.add(lblChooseColumn);

		// combo box
		comboBox = new JComboBox();
		panel_2_1.add(comboBox);
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// JOptionPane.showMessageDialog(null, "Now sel:" +
					// comboBox.getSelectedItem().toString());
					loadSampleData(comboBox.getSelectedItem().toString());
					// table.repaint();
				}
			}
		});

		JLabel lblNewLabel = new JLabel("Enter header separator");
		lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_2_1.add(lblNewLabel);

		textField = new JTextField();
		textField.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		textField.setToolTipText(
				"Enter the string which separates column name and value.  e.g. if column values are age: 1 || contition: b then : is header separator.");
		panel_2_1.add(textField);
		textField.setColumns(5);

		JLabel lblEnterColumnSplitter = new JLabel("Enter column splitter");
		lblEnterColumnSplitter.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_2_1.add(lblEnterColumnSplitter);

		textField_1 = new JTextField();
		textField_1.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		textField_1.setToolTipText(
				"Enter the string which separates columns. e.g. if column values are age: 1 || contition: b then || is column splitter.");
		panel_2_1.add(textField_1);
		textField_1.setColumns(5);

		JButton btnPreview = new JButton("Split");
		btnPreview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String headerSep = textField.getText();
				String colSep = textField_1.getText();
				if (headerSep.length() < 1 || colSep.length() < 1) {
					return;
				}
				splitData(headerSep, colSep);
			}
		});
		btnPreview.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_2_1.add(btnPreview);
		panel_2.add(panel_2_1, BorderLayout.NORTH);
		JPanel panel_2_south = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel_2_south.setBackground(Color.GRAY);
		JLabel label_table = new JLabel("The column splitted into following columns:");
		label_table.setForeground(Color.GREEN);
		label_table.setFont(new Font("Garamond", Font.PLAIN, 16));
		panel_2_south.add(label_table);
		panel_2.add(panel_2_south, BorderLayout.SOUTH);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(.21d);
		splitPane.setRightComponent(splitPane_1);

		JPanel panel_3 = new JPanel();
		splitPane_1.setLeftComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		JPanel panel_5 = new JPanel();
		panel_3.add(panel_5, BorderLayout.NORTH);
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblOriginal = new JLabel("Original");
		panel_5.add(lblOriginal);

		JScrollPane scrollPane = new JScrollPane();
		panel_3.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		JPanel panel_4 = new JPanel();
		splitPane_1.setRightComponent(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));

		JPanel panel_6 = new JPanel();
		panel_4.add(panel_6, BorderLayout.NORTH);
		panel_6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblNew = new JLabel("New");
		panel_6.add(lblNew);

		JScrollPane scrollPane_1 = new JScrollPane();
		panel_4.add(scrollPane_1, BorderLayout.CENTER);

		table_1 = new JTable();
		initializeDisplay();
		scrollPane_1.setViewportView(table_1);
		scrollPane.setViewportView(table);

		this.setSize(800, 800);
	}

	public void initializeDisplay() {
		comboBox.setModel(new DefaultComboBoxModel(headers));
		initPreviewTable();
		table.repaint();
	}

	private void initPreviewTable() {

		// initialize tables; no data
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

					/*
					 * String type = (String) getModel().getValueAt(modelRow, 0); if
					 * ("Exp1".equals(type)) c.setBackground(Color.GREEN); if ("Sell".equals(type))
					 * c.setBackground(Color.YELLOW);
					 */
				} else {
					c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}
		};

		table_1 = new JTable() {
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

					/*
					 * String type = (String) getModel().getValueAt(modelRow, 0); if
					 * ("Exp1".equals(type)) c.setBackground(Color.GREEN); if ("Sell".equals(type))
					 * c.setBackground(Color.YELLOW);
					 */
				} else {
					c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}
		};

		// load selected row
		loadSampleData(comboBox.getSelectedItem().toString());
	}

	private void loadSampleData(String column) {
		table.setModel(new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// get data to add
		colData = obj.getDatabyAttributes(null, column, false);
		if (colData == null) {
			return;
		}
		// add data
		tablemodel.addColumn(column);
		// for each row add each coloumn
		int minSize = (colData.size() < PSIZE) ? colData.size() : PSIZE;
		for (int i = 0; i < minSize; i++) {
			// create a temp string storing all col values for a row
			String[] temp = new String[headers.length];
			for (int j = 0; j < headers.length; j++) {

				temp[j] = colData.get(i).toString();
			}

			// add ith row in table
			tablemodel.addRow(temp);

		}
	}
	
	/**
	 * Make string regex safe
	 * @param s
	 * @return
	 */
	public String processString(String s) {
		// special chars: [\^$.|?*+(){}
		String[] special = { "\\", "+", "[", "^", "$", ".", "|", "?", "*", "(", ")", "{", "}", "-" };
		String res = s;
		try {

			for (String c : special) {
				res = res.replaceAll("\\" + c, "\\\\" + c);
			}
		} catch (IllegalArgumentException iae) {
			//JOptionPane.showMessageDialog(null, "s:" + s);
		}

		return res;
	}

	private void splitData(String hsep, String colSep) {
		if (colData == null) {
			return;
		}
		hsep=processString(hsep);
		colSep=processString(hsep);

		// example str a:1||b:3 split in to two cols
		int maxCols = 0;
		String[] newCols = null;
		// find max cols
		for (String s : colData) {

			if (s.split(colSep).length > maxCols) {
				maxCols = s.split(colSep).length;
				newCols = s.split(colSep);
			}
		}
		if (maxCols == 1) {
			JOptionPane.showMessageDialog(null, "nothing to split");
			return;
		}

		// create a new table model with maxcols
		DefaultTableModel newtabmodel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		
		
		for (int i = 0; i < maxCols; i++) {
			newtabmodel.addColumn(newCols[i].split(hsep)[0]);
		}

		for (int i = 0; i < colData.size(); i++) {
			String thisRow = colData.get(i);
			// String[] temp = new String[maxCols];
			String[] split1 = thisRow.split(colSep);
			String[] toAdd = new String[maxCols];
			for (int j = 0; j < split1.length; j++) {
				String[] sp2 = split1[j].split(hsep);
				if (sp2.length > 1) {
					toAdd[j] = sp2[1];
				} else {
					toAdd[j] = split1[j];
				}
			}
			newtabmodel.addRow(toAdd);
		}

		table_1.setModel(newtabmodel);

	}

	/**
	 * @author urmi Add new columns to metadata coll obj
	 */
	private void addNewColumns() {
		// get all existing data
		List<Document> allData = obj.getAllData();
		String[] headers = obj.getHeaders();
		List<String> newHeaders = new ArrayList<>();
		for (int i = 0; i < headers.length; i++) {
			newHeaders.add(headers[i]);
		}

		int numCols = table_1.getColumnCount();
		for (int i = 0; i < numCols; i++) {
			newHeaders.add(table_1.getColumnName(i));
		}

		// JOptionPane.showMessageDialog(null, "new cols:"+newHeaders.toString());

		// add data to new cols
		for (int i = 0; i < allData.size(); i++) {
			// JOptionPane.showMessageDialog(null, "tr" + allData.get(i).toString());
			for (int j = 0; j < numCols; j++) {
				Object val = table_1.getModel().getValueAt(i, j);
				if (val == null) {
					allData.get(i).put(table_1.getColumnName(j), "NO_VALUE");
				} else {
					allData.get(i).put(table_1.getColumnName(j), val.toString());
				}

			}

			//JOptionPane.showMessageDialog(null, "tr" + allData.get(i).toString());

		}
		// create new collection with alldata
		obj.updateMetadataColumns(newHeaders, allData);
		parent.updateTable();

	}
}
