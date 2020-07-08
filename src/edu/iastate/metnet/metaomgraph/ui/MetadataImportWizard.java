package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Toolkit;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JTable;
import java.awt.Dimension;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class MetadataImportWizard extends JDialog {

	private JPanel contentPane;
	private JTable table;
	private JTree tree;
	private JTree tree_1;
	private int treePreviewSize = 5;
	private String dataColumnName = "";
	private JTextField textField;
	private JList list;
	private JScrollPane scrollPaneTable = new JScrollPane();
	private JScrollPane scrollPaneTree = new JScrollPane();
	private JScrollPane scrollPanePreviewTree = new JScrollPane();
	private boolean removeUnusedCols;
	JCheckBox chckbxRemoveUnusedColumns;
	private List<String> removedCols;
	// private MetadataCollection obj;
	// private String[] headers;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MetadataImportWizard frame = new MetadataImportWizard(null, new String[] { "AA", "bb", "cc" },
							new Dimension(800, 600), new Point(500, 100));
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public String getdataColumnName() {
		return this.dataColumnName;
	}

	public MetadataImportWizard getThisframe() {
		return this;
	}

	/**
	 * Create the frame.
	 * 
	 * @param MetadataCollection
	 *            object with metadata read String[] Headers for metadata Dimension
	 *            of previous frame Location of previous frame
	 */
	public MetadataImportWizard(MetadataCollection obj, String[] headers, Dimension frameDimension,
			Point locationOnScreen) {
		this(obj, headers, frameDimension, locationOnScreen, null, null, null, null, false, null);
	}

	/**
	 * Constructer to edit tree structur
	 * 
	 * @param obj
	 * @param treeStruct
	 * @param removeCols
	 */
	public MetadataImportWizard(MetadataHybrid mdobj, JTree treeStruct, boolean removeCols) {
		this(mdobj.getMetadataCollection(), mdobj.getColumnsNotinTree(), new Dimension(800, 600), new Point(500, 100),
				null, new ArrayList<>(mdobj.getMissingMDRows()), new ArrayList<>(mdobj.getExcludedMDRows()), treeStruct, removeCols,
				new ArrayList<>(mdobj.getRemovedMDCols()) );
	}

	/**
	 * @wbp.parser.constructor
	 */
	public MetadataImportWizard(MetadataCollection obj, String[] headers, Dimension frameDimension,
			Point locationOnScreen, ReadMetadata parent, List<String> missingDC, List<String> extraDC,
			JTree treeStructure, boolean removeCols, List<String> mdrmCols) {

		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(MetadataImportWizard.class.getResource("/resource/MetaOmicon16.png")));
		setTitle("Metadata Table to Tree");
		// setAlwaysOnTop(true);
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 702, 482);
		// remove or keep unused cols
		this.removeUnusedCols = removeCols;
		// already removed cols
		removedCols = mdrmCols;
		chckbxRemoveUnusedColumns = new JCheckBox("Remove unused columns from metadata");
		chckbxRemoveUnusedColumns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxRemoveUnusedColumns.isSelected()) {
					// JOptionPane.showMessageDialog(null, "selc");
					removeUnusedCols = true;
				} else {
					removeUnusedCols = false;
				}
			}
		});
		chckbxRemoveUnusedColumns.setForeground(Color.BLUE);
		chckbxRemoveUnusedColumns.setBackground(Color.GRAY);
		chckbxRemoveUnusedColumns.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		if (removeUnusedCols) {
			chckbxRemoveUnusedColumns.setSelected(true);
		}

		if (obj == null) {
			JOptionPane.showMessageDialog(null, "Error in metadata...", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// set data column
		dataColumnName = obj.getDatacol();
		textField = new JTextField();
		textField.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		textField.setEnabled(false);
		textField.setEditable(false);
		// textField.setColumns(dataColumnName.length()+2);
		textField.setText(dataColumnName);
		// JOptionPane.showMessageDialog(null, Arrays.toString(headers));
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmHelpMe = new JMenuItem("Help Me!");
		mnHelp.add(mntmHelpMe);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		contentPane.add(panel, BorderLayout.SOUTH);

		JButton finish = new JButton("Finish");
		finish.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		finish.setForeground(Color.BLACK);
		finish.setEnabled(false);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				finish.setEnabled(false);
				ReadMetadata ob = new ReadMetadata(obj, obj.getdelimiter());
				ob.setVisible(true);
				dispose();
			}
		});
		panel.add(btnBack);

		JButton btnResetTree = new JButton("Reset Tree");
		btnResetTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// reset the list
				String[] allHeaders= obj.getHeaders();
				//JOptionPane.showMessageDialog(null, "hdrs:"+Arrays.toString(allHeaders));
				DefaultListModel listModel = new DefaultListModel();
				for (int i = 0; i < allHeaders.length; i++) {
					listModel.addElement(allHeaders[i]);
				}
				list = new JList(listModel);
				list.setToolTipText(
						"Drag each column onto the panel on the right and create a hierarchical structure for metadata.");
				list.setForeground(Color.RED);
				list.setFont(new Font("Times New Roman", Font.PLAIN, 20));
				list.setBackground(Color.BLACK);
				list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				list.setDragEnabled(true);
				list.setTransferHandler(new ListTransferHandler());
				list.setDropMode(DropMode.INSERT);
				// align center and other properties
				DefaultListCellRenderer listRenderer = (DefaultListCellRenderer) list.getCellRenderer();
				listRenderer.setHorizontalAlignment(SwingConstants.LEFT);
				scrollPaneTable.setViewportView(list);
				// reset the tree
				// UIManager.put("Tree.rendererFillBackground", false);
				// tree = new JTree();
				tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Root") {
					{
					}
				}));
				tree.setBackground(Color.BLACK);
				tree.setFont(new Font("Times New Roman", Font.PLAIN, 20));
				scrollPaneTree.setViewportView(tree);
				tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
				tree.setDropMode(DropMode.USE_SELECTION);
				tree.setDragEnabled(true);
				tree.setTransferHandler(new MogTreeTransferHandler());

			}
		});
		btnResetTree.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel.add(btnResetTree);

		JButton btnSave = new JButton("Save");
		btnSave.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		// panel.add(btnSave);

		JButton btnImport = new JButton("Next");
		btnImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				finish.setEnabled(false);
				if (obj == null || tree == null) {
					JOptionPane.showMessageDialog(null, "Please create a tree , then click Next.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// return if datacolumn is not in tree
				for (int i = 0; i < list.getModel().getSize(); i++) {
					String item = list.getModel().getElementAt(i).toString();
					if (item.equals(obj.getDatacol())) {
						JOptionPane.showMessageDialog(null, "The data column must be in the tree.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				// which headers were not used in metadata
				List<String> remainingCols = new ArrayList();
				for (int i = 0; i < list.getModel().getSize(); i++) {
					String item = list.getModel().getElementAt(i).toString();
					remainingCols.add(item);
				}

				if (removeUnusedCols) {
					// remove remainingCols from collection
					// JOptionPane.showMessageDialog(null, "remove dc:" + remainingCols.toString());
					obj.removeUnusedCols(remainingCols);
					// clear removed cols from left jlist panel
					DefaultListModel model = (DefaultListModel) list.getModel();
					model.removeAllElements();
					if (removedCols == null) {
						removedCols = new ArrayList<>();
					}
					removedCols.addAll(remainingCols);
				}

				new AnimatedSwingWorker("Working...", true) {

					@Override
					public Object construct() {
						// return if data column has repeated names

						ParseTableTree ob = new ParseTableTree(obj, tree, dataColumnName);
						// org.jdom.Document res = ob.tableToTree(obj, tree);
						org.jdom.Document res = ob.tableToTree();
						if (!(MetaOmGraph.getActiveProject() == null)) {
							try {

								MetaOmGraph.getActiveProject().loadMetadataHybrid(obj, res.getRootElement(),
										ob.getTreeMap(), dataColumnName, ob.getMetadataHeaders(), tree,
										ob.getDefaultRepMap(), ob.getDefaultRepCol(), missingDC, extraDC, removedCols);
								// JOptionPane.showMessageDialog(null, "total child of
								// root:"+res.getRootElement().getChildren().size());
								MetaOmGraph.updateWindow();
								// update datacolumnName for the current project
								// MetaOmGraph.getActiveProject().setDataColumn(dataColumnName);
								// MetaOmGraph.returnprojectTableFrame().setVisible(true);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						return null;
					}

					@Override
					public void finished() {
						JOptionPane.showMessageDialog(null, "Metadata has been loaded into MetaOmGraph", "Done",
								JOptionPane.INFORMATION_MESSAGE);
						finish.setEnabled(true);
						finish.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								dispose();
							}
						});

						// sometimes shows error
						// dispose();
					}

				}.start();

			}
		});
		btnImport.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnImport.setForeground(Color.BLACK);
		// btnImport.setBackground(Color.GRAY);
		panel.add(btnImport);
		panel.add(finish);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		JPanel panel_1_north = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel_1_north.setBackground(Color.GRAY);
		JPanel panel_1_south = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel_1_south.setBackground(Color.GRAY);
		panel_1.add(panel_1_north, BorderLayout.NORTH);

		JLabel lblMapMetadataStructure = new JLabel("Map metadata structure");
		lblMapMetadataStructure.setForeground(Color.GREEN);
		lblMapMetadataStructure.setFont(new Font("Garamond", Font.BOLD, 16));
		panel_1_north.add(lblMapMetadataStructure);
		panel_1.add(panel_1_south, BorderLayout.SOUTH);

		panel_1_south.add(chckbxRemoveUnusedColumns);

		JLabel lblChooseTheData = new JLabel("Data column is:");
		lblChooseTheData.setForeground(Color.GREEN);
		lblChooseTheData.setFont(new Font("Garamond", Font.BOLD, 16));
		panel_1_south.add(lblChooseTheData);

		JButton btnPreviewTree = new JButton("Preview tree");
		btnPreviewTree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (obj == null || headers.length <= 0 || tree == null) {
					JOptionPane.showMessageDialog(null,
							"Please read a metadata file and create a tree , then click Preview.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// return if datacolumn is not in tree
				for (int i = 0; i < list.getModel().getSize(); i++) {
					String item = list.getModel().getElementAt(i).toString();
					if (item.equals(obj.getDatacol())) {
						JOptionPane.showMessageDialog(null, "The data column must be in the tree.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				new AnimatedSwingWorker("Working...", true) {

					@Override
					public Object construct() {
						ParseTableTree ob = new ParseTableTree(obj, tree, dataColumnName);
						org.jdom.Document res = ob.tableToTree();
						org.jdom.Document resCopy = res;
						tree_1.setModel(ob.createPreviewTreeModel(resCopy, treePreviewSize));
						btnImport.setEnabled(true);
						finish.setEnabled(false);
						return null;
					}

				}.start();

			}
		});

		panel_1_south.add(textField);

		btnPreviewTree.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_1_south.add(btnPreviewTree);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(2);
		splitPane.setResizeWeight(.71d);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setDividerSize(2);
		splitPane_1.setResizeWeight(.31d);
		splitPane.setLeftComponent(splitPane_1);

		// scrollpanes for table and trees
		scrollPaneTable = new JScrollPane();
		scrollPaneTable.setBackground(Color.DARK_GRAY);
		scrollPaneTree = new JScrollPane();
		scrollPanePreviewTree = new JScrollPane();

		splitPane_1.setLeftComponent(scrollPaneTable);
		splitPane_1.setRightComponent(scrollPaneTree);
		splitPane.setRightComponent(scrollPanePreviewTree);
		table = new JTable();
		table.setAutoCreateRowSorter(true);
		table.setSelectionForeground(Color.BLACK);
		table.setSelectionBackground(Color.CYAN);
		table.setModel(new DefaultTableModel(new Object[][] { { "sa" }, { "aa" }, { "bb" }, { "cc" }, },
				new String[] { "Column headers" }));
		table.setRowMargin(2);
		table.setRowHeight(20);
		table.setIntercellSpacing(new Dimension(2, 2));
		table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		table.setForeground(Color.RED);
		table.setBackground(Color.BLACK);
		// Do not add table now add later. Make list for now
		// scrollPaneTable.setViewportView(table);

		// define List
		// use this code if list is not displayed
		/*
		 * DefaultListModel listModel = new DefaultListModel(); for (int i = 0; i <
		 * headers.length; i++) { listModel.addElement(headers[i]);
		 * 
		 * } JList list = new JList(listModel);
		 */
		DefaultListModel listModel = new DefaultListModel();
		for (int i = 0; i < headers.length; i++) {
			listModel.addElement(headers[i]);

		}
		list = new JList(listModel);
		list.setToolTipText(
				"Drag each column onto the panel on the right and create a hierarchical structure for metadata.");
		list.setForeground(Color.RED);
		list.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		list.setBackground(Color.BLACK);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setDragEnabled(true);
		list.setTransferHandler(new ListTransferHandler());
		list.setDropMode(DropMode.INSERT);
		// align center and other properties
		DefaultListCellRenderer listRenderer = (DefaultListCellRenderer) list.getCellRenderer();
		listRenderer.setHorizontalAlignment(SwingConstants.LEFT);

		scrollPaneTable.setViewportView(list);

		// define this before tree
		// UIManager.put("Tree.rendererFillBackground", false);
		UIManager.put("Tree.rendererFillBackground", false);

		tree = treeStructure;
		if (tree == null) {
			tree = new JTree();
			tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Root") {
				{
				}
			}));
		}
		tree.setBackground(Color.BLACK);
		tree.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		scrollPaneTree.setViewportView(tree);
		// splitPane_1.setRightComponent(tree);

		tree_1 = new JTree();
		tree_1.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Root") {
			{
			}
		}));
		tree_1.setBackground(Color.BLACK);
		tree_1.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		scrollPanePreviewTree.setViewportView(tree_1);
		// splitPane.setRightComponent(tree_1);
		// tree_1.setEditable(false);

		// change tree icons
		Icon closedIcon = new ImageIcon(getClass().getResource("/resource/customicon/runicon.png"));
		Icon openIcon = new ImageIcon(getClass().getResource("/resource/customicon/runicon.png"));
		Icon leafIcon = new ImageIcon(getClass().getResource("/resource/customicon/runicon.png"));
		// DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)
		// tree.getCellRenderer();
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		DefaultTreeCellRenderer renderer_1 = (DefaultTreeCellRenderer) tree_1.getCellRenderer();
		// renderer.setClosedIcon(closedIcon);
		// renderer.setOpenIcon(openIcon);
		// renderer.setLeafIcon(leafIcon);
		// set colors
		renderer.setTextSelectionColor(new Color(205, 5, 5));
		renderer.setTextNonSelectionColor(new Color(255, 100, 0));
		renderer_1.setTextNonSelectionColor(new Color(182, 255, 115));
		renderer_1.setTextSelectionColor(new Color(0, 254, 0));
		listRenderer.setIcon(leafIcon);

		// settings to allow drag and drop from table to tree
		// for table
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);

		table.setTransferHandler(new TableTransferHandler());

		// for tree
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setDropMode(DropMode.USE_SELECTION);
		tree.setDragEnabled(true);
		tree.setTransferHandler(new MogTreeTransferHandler());

		this.setSize(frameDimension);
		this.setLocation(locationOnScreen);
	}

	/*
	 * private boolean checkRepeatedvalues(MetadataCollection obj, String
	 * dataColumnName) { boolean status = false; // get all data from obj
	 * List<String> l = obj.getSortedUniqueValuesByHeaderName(dataColumnName, true,
	 * false); List<String> l_uniq =
	 * obj.getSortedUniqueValuesByHeaderName(dataColumnName, false, false); if (l ==
	 * null || l_uniq == null) { return status; } // check if sizr of unique and
	 * non-uniqe is same this means no repeats in that // column if (l.size() !=
	 * l_uniq.size()) { // JOptionPane.showMessageDialog(null, "size l:"+l.size()+"
	 * size // l_uniq:"+l_uniq.size()); return status; } status = true; return
	 * status; }
	 */
}
