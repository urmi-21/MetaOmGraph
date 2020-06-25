package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.border.LineBorder;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.dizitart.no2.Document;

//import com.sun.deploy.uitoolkit.impl.fx.Utils;
//import com.sun.glass.events.WindowEvent;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;

import java.awt.ComponentOrientation;
import javax.swing.DefaultComboBoxModel;
import java.awt.Dimension;

public class ReadMetadata extends JDialog {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private File metadataFile;
	private String[] metadatadelims = { "\t", ",", ";", " " };
	private int previewSize = 50;
	private MetadataCollection obj = null;
	private String metadataDelim;
	private List<Document> metadata = null;
	private String[] headers = null;
	JButton btnBut;
	JButton btnBut_1;
	JButton btnBut_2;
	JButton btnFindAndReplace;
	JButton btnTranspose;
	JButton btnPriview;
	JButton btnBrowse;
	JButton btnNext;
	JComboBox comboBox_1;
	private String dataColumnName;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private int missinginMD;
	private int missinginD;
	private JScrollPane scrollPane;

	private List<String> missingDC;
	private List<String> extraDC;
	private List<String> removedCols;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// ReadMetadata frame = new ReadMetadata();
					ReadMetadata frame = new ReadMetadata("D:\\MOGdata\\mog_testdata\\xml\\sample_metadata2.csv", "\t");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * start from loaded data
	 */
	public ReadMetadata(MetadataCollection dataObj, String delim) {
		this();
		
		if(!this.loadMetadata(dataObj, delim)) {
			return;
		}
		this.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(MetadataImportWizard.class.getResource("/resource/MetaOmicon16.png")));
		this.setTitle("Import Metadata");
		this.toFront();
	}

	/*
	 * start from known file path
	 */
	public ReadMetadata(String path, String delim) {
		this();
		this.loadMetadata(path, delim);
		this.textField.setText(path);
		this.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(MetadataImportWizard.class.getResource("/resource/MetaOmicon16.png")));
		this.setTitle("Read metadata file");
		this.toFront();
		this.pack();
		
	}

	/**
	 * Create the frame.
	 */
	public ReadMetadata() {
		this.toFront();
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		comboBox_1 = new JComboBox();

		comboBox_1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				// check whether there is any selection
				if (comboBox_1.getSelectedIndex() != -1) {
					// JOptionPane.showMessageDialog(null, "Now
					// selected:"+comboBox_1.getSelectedItem().toString());
					int[] missingextra = getMissingDC();
					missinginD = missingextra[1];
					missinginMD = missingextra[0];
					textField_3.setText(String.valueOf(missingextra[1]));
					textField_4.setText(String.valueOf(missingextra[0]));

				}
			}
		});

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenu mnAdvanceOptions = new JMenu("Advance Options");
		menuBar.add(mnAdvanceOptions);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		contentPane.add(panel, BorderLayout.NORTH);

		JLabel lblReadMetadataFile = new JLabel("Read metadata file");
		lblReadMetadataFile.setForeground(Color.GREEN);
		lblReadMetadataFile.setFont(new Font("Garamond", Font.BOLD, 18));
		lblReadMetadataFile.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblReadMetadataFile);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.GRAY);
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.GRAY);
		panel_1.add(panel_2);

		btnNext = new JButton("Next");
		btnNext.setEnabled(false);
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				// call MetadataImportWizard
				// get thisform dimention
				if (obj == null || headers.length <= 0) {
					JOptionPane.showMessageDialog(null, "Please read a metadata file, then click Next.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				dataColumnName = comboBox_1.getSelectedItem().toString();
				if (!checkRepeatedvalues(obj, dataColumnName)) {
					JOptionPane.showMessageDialog(panel,
							"The data column has ambiguous (repeated) values. Entries in data column should be unique in order to identify each data source unambiguously.");

					return;
				}
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {

							obj.setDatacol(dataColumnName);
							// add all data_col values as included
							obj.initializeIncludedList();
							// JOptionPane.showMessageDialog(null, "init:"+obj.getIncluded().toString());
							// JOptionPane.showMessageDialog(null, "init ex:"+obj.getExcluded().toString());

							if (missinginD > 0) {
								// JOptionPane.showMessageDialog(null, "removing");
								removeExtraRowsfromMD();
							}
							if (missinginMD > 0) {
								// removeMissingfromD();
								// add null metadata for missing cols
								// add missing Data after making the tree
								addMissingMD();
							}

							JOptionPane.showMessageDialog(getThisFrame(),
									"Sample Id column selected is: " + obj.getDatacol(), "Sample Id column",
									JOptionPane.INFORMATION_MESSAGE);
							MetadataImportWizard frame = new MetadataImportWizard(obj, headers, getThisFrame().getSize(), getThisFrame().getLocationOnScreen(), getThisFrame(),
									missingDC, extraDC, null, true, removedCols);
							frame.setVisible(true);
							getThisFrame().dispose();
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, "Error....");
						}
					}
				});
			}
		});
		btnNext.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_2.add(btnNext);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		splitPane.setDividerSize(1);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();
		JPanel topButtonPanel = new JPanel();
		panel_3.setBackground(Color.DARK_GRAY);
		splitPane.setLeftComponent(panel_3);
		panel_3.setLayout(new BorderLayout(1, 1));

		// create top button panel
		topButtonPanel.setBackground(Color.DARK_GRAY);
		topButtonPanel.setLayout(new FlowLayout());

		JLabel lblMetadataFile = new JLabel("Metadata file");
		lblMetadataFile.setForeground(Color.ORANGE);
		lblMetadataFile.setFont(new Font("Garamond", Font.PLAIN, 15));
		topButtonPanel.add(lblMetadataFile);

		textField = new JTextField();
		topButtonPanel.add(textField);
		textField.setColumns(10);
		// textField.setText("D:\\MOGdata\\mog_testdata\\xml\\sample_metadata2.csv");

		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(edu.iastate.metnet.metaomgraph.utils.Utils.getLastDir());
				// MetaOmGraph.
				int rVal = fileChooser.showOpenDialog(null);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					metadataFile = fileChooser.getSelectedFile();
					textField.setText(metadataFile.getAbsolutePath());
					// System.out.println(selectedFile.getName());
				}
			}
		});
		btnBrowse.setFont(new Font("Times New Roman", Font.PLAIN, 11));
		topButtonPanel.add(btnBrowse);

		JLabel lblSelectDelimiter = new JLabel("Select Delimiter");
		lblSelectDelimiter.setForeground(Color.ORANGE);
		lblSelectDelimiter.setFont(new Font("Garamond", Font.PLAIN, 15));
		topButtonPanel.add(lblSelectDelimiter);

		JComboBox comboBox = new JComboBox();
		comboBox.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		comboBox.setModel(
				new DefaultComboBoxModel(new String[] { "Tab (\\t)", "Comma (,)", "Semicolon (;)", "Space" }));
		comboBox.setForeground(Color.BLACK);
		comboBox.setBackground(Color.GRAY);
		topButtonPanel.add(comboBox);

		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.DARK_GRAY);
		topButtonPanel.add(panel_4);

		btnPriview = new JButton("Preview");
		btnPriview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// read metadata file and display in table first 20 lines
				obj = new MetadataCollection();
				// read file into obj

				metadataDelim = metadatadelims[comboBox.getSelectedIndex()];
				loadMetadata(textField.getText(), metadataDelim);
				/*
				 * try { obj.readMetadataTextFile(metadataFile.getAbsolutePath(), metadataDelim,
				 * true); } catch (IOException e1) { // TODO Auto-generated catch block
				 * e1.printStackTrace(); } // comboBox_1.setModel(new
				 * DefaultComboBoxModel(obj.getHeaders())); updateTable();
				 * comboBox_1.setSelectedIndex(guessDatacolumnIndex()); // set buttons enabled
				 * btnNext.setEnabled(true); btnBut.setEnabled(true); btnBut_1.setEnabled(true);
				 * btnBut_2.setEnabled(true); btnFindAndReplace.setEnabled(true);
				 * btnTranspose.setEnabled(true);
				 */

			}
		});
		btnPriview.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_4.add(btnPriview);

		JLabel lblChooseDataColumn = new JLabel("Choose Sample Id Column");
		lblChooseDataColumn.setFont(new Font("Garamond", Font.PLAIN, 15));
		lblChooseDataColumn.setForeground(Color.ORANGE);
		topButtonPanel.add(lblChooseDataColumn);

		topButtonPanel.add(comboBox_1);
		panel_3.add(topButtonPanel, BorderLayout.NORTH);

		JPanel panel_6 = new JPanel();
		panel_6.setBackground(Color.DARK_GRAY);
		panel_3.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new BorderLayout(0, 0));

		JLabel lblMetadataFileStats = new JLabel("Metadata file stats");
		lblMetadataFileStats.setHorizontalAlignment(SwingConstants.CENTER);
		lblMetadataFileStats.setForeground(Color.GREEN);
		lblMetadataFileStats.setFont(new Font("Garamond", Font.BOLD, 18));
		panel_6.add(lblMetadataFileStats, BorderLayout.NORTH);

		JPanel panel_7 = new JPanel();
		panel_7.setBackground(Color.GRAY);
		panel_6.add(panel_7, BorderLayout.CENTER);
		panel_7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblTotalRows = new JLabel("Total Rows");
		lblTotalRows.setForeground(Color.BLUE);
		lblTotalRows.setFont(new Font("Garamond", Font.PLAIN, 17));
		panel_7.add(lblTotalRows);

		textField_1 = new JTextField();
		textField_1.setForeground(Color.RED);
		textField_1.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		textField_1.setEditable(false);
		panel_7.add(textField_1);
		textField_1.setColumns(5);

		JLabel lblTotalColumns = new JLabel("Total Columns");
		lblTotalColumns.setForeground(Color.BLUE);
		lblTotalColumns.setFont(new Font("Garamond", Font.PLAIN, 17));
		panel_7.add(lblTotalColumns);

		textField_2 = new JTextField();
		textField_2.setForeground(Color.RED);
		textField_2.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		textField_2.setEditable(false);
		panel_7.add(textField_2);
		textField_2.setColumns(5);

		JLabel lblMissingValues = new JLabel("Extra Samples");
		lblMissingValues.setForeground(Color.BLUE);
		lblMissingValues.setFont(new Font("Garamond", Font.PLAIN, 17));
		panel_7.add(lblMissingValues);

		textField_3 = new JTextField();
		textField_3.setForeground(Color.RED);
		textField_3.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		textField_3.setEditable(false);
		panel_7.add(textField_3);
		textField_3.setColumns(5);

		JLabel lblMissingDataColumns = new JLabel("Missing Samples");
		lblMissingDataColumns.setForeground(Color.BLUE);
		lblMissingDataColumns.setFont(new Font("Garamond", Font.PLAIN, 17));
		panel_7.add(lblMissingDataColumns);

		textField_4 = new JTextField();
		textField_4.setForeground(Color.RED);
		textField_4.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		textField_4.setEditable(false);
		panel_7.add(textField_4);
		textField_4.setColumns(5);
		splitPane.setResizeWeight(.2d);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setEnabled(false);
		splitPane_1.setDividerSize(1);
		splitPane_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		splitPane_1.setBackground(Color.DARK_GRAY);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setResizeWeight(.051d);
		splitPane.setRightComponent(splitPane_1);

		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.DARK_GRAY);
		splitPane_1.setLeftComponent(panel_5);

		btnBut = new JButton("Rename headers");
		btnBut.setEnabled(false);
		btnBut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// if no metadata read
				if (metadata == null) {
					JOptionPane.showMessageDialog(panel, "Error!!! No file read...", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// pop up and let user edit header_names
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							MetadataHearderEdit frame = new MetadataHearderEdit(headers, obj, getThisFrame());
							setEnabled(false);
							frame.addWindowListener(new java.awt.event.WindowAdapter() {
								@Override
								public void windowClosing(java.awt.event.WindowEvent windowEvent) {
									setEnabled(true);
								}
							});
							frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnBut.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_5.add(btnBut);

		btnBut_1 = new JButton("Remove columns");
		btnBut_1.setEnabled(false);
		btnBut_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							MetadataRemoveCols frame = new MetadataRemoveCols(headers, obj, getThisFrame());
							setEnabled(false);
							frame.addWindowListener(new java.awt.event.WindowAdapter() {
								@Override
								public void windowClosing(java.awt.event.WindowEvent windowEvent) {
									setEnabled(true);
								}
							});
							frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnBut_1.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_5.add(btnBut_1);

		btnBut_2 = new JButton("Split columns");
		// btnBut_2.setEnabled(false);
		btnBut_2.setEnabled(true);
		btnBut_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// let user select a column and 2 other parameter
				// colheader sep, col separator
				// e.g. A column sample_attribute has values like
				// age: 50_days || dev_stage: senescence || cultivar: not applicable || ecotype:
				// Columbia || isolate: not applicable
				// now it should split this column into columns age, cultivar, ecotype and
				// isolate and original column is removed
				// Handle where values are absent or missing

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							MetadataSplitcol frame = new MetadataSplitcol(obj, getThisFrame());
							// setEnabled(false);
							frame.addWindowListener(new java.awt.event.WindowAdapter() {
								@Override
								public void windowClosing(java.awt.event.WindowEvent windowEvent) {
									setEnabled(true);
									enableNext();
								}
							});
							frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							frame.setVisible(true);
							disableNext();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});

			}
		});
		btnBut_2.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_5.add(btnBut_2);

		btnTranspose = new JButton("Transpose");
		btnTranspose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		btnTranspose.setEnabled(false);
		btnTranspose.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		// panel_5.add(btnTranspose);

		btnFindAndReplace = new JButton("Find and replace");
		btnFindAndReplace.setEnabled(false);
		btnFindAndReplace.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		// panel_5.add(btnFindAndReplace);

		scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		scrollPane.setBackground(Color.BLACK);
		scrollPane.setBorder(null);
		scrollPane.setForeground(Color.GREEN);
		splitPane_1.setRightComponent(scrollPane);

		table = new JTable();
		table.setAutoCreateRowSorter(true);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setIntercellSpacing(new Dimension(2, 2));
		table.setToolTipText("Preview of first 50 lines in metadata file");
		table.setRowMargin(2);
		table.setRowHeight(25);
		table.setGridColor(Color.PINK);
		table.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		table.setForeground(Color.GREEN);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		table.setBackground(Color.BLACK);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{},
				{},
				{},
				{},
				{},
				{},
				{},
				{},
				{},
				{},
			},
			new String[] {
			}
		));
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);

		this.setSize(900, 600);
	}

	public void updateHeaders() {
		headers = obj.getHeaders();
		comboBox_1.setModel(new DefaultComboBoxModel(headers));
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		tablemodel.setColumnIdentifiers(headers);
		table.repaint();
		// for (int i = 0; i < tablemodel.getColumnCount(); i++) {
		// System.out.println("Now hs:"+headers[i]);
		// }
	}

	public void updateRemovedCols(List<String> removed) {
		if (this.removedCols == null) {
			removedCols = new ArrayList<>();
		}
		this.removedCols.addAll(removed);
		// JOptionPane.showMessageDialog(null, "now removed:"+removedCols.toString());
	}

	public void updateTable() {
		// Update preview in table
		metadata = new ArrayList<>();
		metadata = obj.getAllData();
		headers = obj.getHeaders();
		comboBox_1.setModel(new DefaultComboBoxModel(headers));
		String[] colNames = obj.getHeaders();
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// AbstractTableModel tablemodel = (DefaultTableModel) table.getModel();
		// clear table model
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		// tablemodel.getDataVector().removeAllElements();
		// tablemodel.fireTableDataChanged();
		// table.repaint();

		// for each row add each coloumn
		int n = previewSize;
		// if metadata size is smaller than default choose smaller value
		if (metadata.size() < previewSize) {
			n = metadata.size();
		}
		for (int i = 0; i < n; i++) {
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
		comboBox_1.setSelectedIndex(guessDatacolumnIndex());
	}

	public ReadMetadata getThisFrame() {
		return this;
	}

	public boolean loadMetadata(MetadataCollection ob, String delim) {
		if (ob == null) {
			JOptionPane.showMessageDialog(null, "Metadata file not loaded.", "Error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		this.obj = ob;
		this.metadataDelim = delim;
		// comboBox_1.setModel(new DefaultComboBoxModel(obj.getHeaders()));
		updateTable();
		// comboBox_1.setSelectedIndex(guessDatacolumnIndex());
		btnNext.setEnabled(true);
		btnBut.setEnabled(true);
		btnBut_1.setEnabled(true);
		btnBut_2.setEnabled(true);
		btnFindAndReplace.setEnabled(true);
		btnTranspose.setEnabled(true);
		textField_1.setText(String.valueOf(ob.getNumRows()));
		textField_2.setText(String.valueOf(this.headers.length));

		// find missing and extra datacolumns

		int[] missingextra = getMissingDC();
		missinginD = missingextra[1];
		missinginMD = missingextra[0];
		textField_3.setText(String.valueOf(missingextra[1]));
		textField_4.setText(String.valueOf(missingextra[0]));
		
		return true;

	}

	public int[] getMissingDC() {
		int[] res = new int[2];
		List<String> datacolheaders = Arrays.asList(MetaOmGraph.getActiveProject().getDataColumnHeaders());
		List<String> mdcolheaders = obj.getDatabyAttributes(null, comboBox_1.getSelectedItem().toString(), true);
		List<String> missingDCnames = new ArrayList<>();
		int common = 0;
		for (int i = 0; i < datacolheaders.size(); i++) {
			if (mdcolheaders.contains(datacolheaders.get(i))) {
				common++;
			} else {
				// get names of columns which have missing metadata
				missingDCnames.add(datacolheaders.get(i));
			}
		}

		int missing = datacolheaders.size() - common;
		int extra = mdcolheaders.size() - common;

		res[0] = missing;
		res[1] = extra;
		// store missing DC names to class
		this.missingDC = missingDCnames;
		return res;
	}

	public boolean loadMetadata(String path, String delim) {
		obj = new MetadataCollection();
		try {
			obj.readMetadataTextFile(path, delim, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//if failed to read the metadata
		if (obj.getNumRows() < 1) {
			JOptionPane.showMessageDialog(null, "Metadata file not loaded or empty.", "Error",
					JOptionPane.INFORMATION_MESSAGE);
			
			return false;
		}
		// JOptionPane.showMessageDialog(null, "Reading metadata..DONE");
		this.metadataDelim = delim;
		// comboBox_1.setModel(new DefaultComboBoxModel(obj.getHeaders()));
		updateTable();
		comboBox_1.setSelectedIndex(guessDatacolumnIndex());
		btnNext.setEnabled(true);
		btnBut.setEnabled(true);
		btnBut_1.setEnabled(true);
		// btnBut_2.setEnabled(true);
		// btnFindAndReplace.setEnabled(true);
		// btnTranspose.setEnabled(true);
		// show info
		textField_1.setText(String.valueOf(obj.getNumRows()));
		textField_2.setText(String.valueOf(this.headers.length));
		// find missing and extra datacolumns

		int[] missingextra = getMissingDC();
		missinginD = missingextra[1];
		missinginMD = missingextra[0];
		textField_3.setText(String.valueOf(missinginD));
		textField_4.setText(String.valueOf(missinginMD));
		
		return true;

	}

	public MetadataCollection getCollectionobj() {
		return this.obj;
	}

	/**
	 * Remove rows from metadata which don't match any column in data file
	 */
	public void removeExtraRowsfromMD() {
		// delete rows in MD file
		JOptionPane.showMessageDialog(null,
				"Rows from metadata file which don't match data columns/samples in datafile will be removed.");
		String[] datacolheaders = MetaOmGraph.getActiveProject().getDataColumnHeaders();
		List<String> datacolheadersList = Arrays.asList(datacolheaders);
		List<String> mdcolheaders = obj.getDatabyAttributes(null, comboBox_1.getSelectedItem().toString(), true);
		List<String> dataToremove = new ArrayList<>();
		for (int i = 0; i < mdcolheaders.size(); i++) {
			if (!datacolheadersList.contains(mdcolheaders.get(i))) {
				dataToremove.add(mdcolheaders.get(i));
			}
		}
		this.extraDC = dataToremove;
		obj.removeDataPermanently(extraDC);

	}

	private void removeMissingfromD() {
		// delete cols in data file
		// JOptionPane.showMessageDialog(null, "Removing from d");

	}

	/**
	 * Add null data for data column not present in metadata file
	 */
	private void addMissingMD() {
		if (this.missingDC == null || this.missingDC.size() < 1) {
			return;
		}
		// JOptionPane.showMessageDialog(null, "adding null" + missingDC.toString());
		obj.addNullData(missingDC);

	}

	// get the index of data column by matchin data column from data file
	private int guessDatacolumnIndex() {
		// String res=null;
		List<String> dataCols = Arrays.asList(MetaOmGraph.getActiveProject().getDataColumnHeaders());
		TableModel dtm = table.getModel();
		int r = dtm.getRowCount();
		int c = dtm.getColumnCount();
		int maxMatches = 0;
		int bestIndex = 0;

		for (int i = 0; i < c; i++) {
			int thisMatch = 0;
			for (int j = 1; j < r; j++) {
				if (dataCols.contains(dtm.getValueAt(j, i).toString())) {
					thisMatch++;
				}
			}
			if (thisMatch > maxMatches) {
				maxMatches = thisMatch;
				bestIndex = i;
			}
		}

		return bestIndex;
	}

	private boolean checkRepeatedvalues(MetadataCollection obj, String dataColumnName) {
		boolean status = false;
		// get all data from obj
		List<String> l = obj.getSortedUniqueValuesByHeaderName(dataColumnName, true, false);
		List<String> l_uniq = obj.getSortedUniqueValuesByHeaderName(dataColumnName, false, false);
		if (l == null || l_uniq == null) {
			return status;
		}
		// check if sizr of unique and non-uniqe is same this means no repeats in that
		// column
		if (l.size() != l_uniq.size()) {
			// JOptionPane.showMessageDialog(null, "size l:"+l.size()+" size
			// l_uniq:"+l_uniq.size());
			return status;
		}
		status = true;
		return status;
	}

	public void disableNext() {
		// TODO Auto-generated method stub
		btnNext.setEnabled(false);

	}

	public void enableNext() {
		// TODO Auto-generated method stub
		btnNext.setEnabled(true);

	}

}
