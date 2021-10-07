package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;

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
import javax.swing.BorderFactory;
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
import java.util.Map;
import java.util.TreeMap;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class MetadataImportWizard extends TaskbarInternalFrame {

	private JPanel contentPane;
	private JTable table;
	private JTree tree;
	private JTree tree_1;
	private JCheckBox[] cBoxes;
	private int treePreviewSize = 5;
	private String dataColumnName = "";
	private JTextField textField;
	private JList list;
	private JScrollPane scrollPaneTable = new JScrollPane();
	private JScrollPane scrollPaneTree = new JScrollPane();
	private JScrollPane scrollPanePreviewTree = new JScrollPane();
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
		
		//urmi dispose the parent (frame which called this frame; used in  order to go back to previous step) internal frame
		if(parent != null)
			parent.dispose();

		
		setTitle("Metadata Column Selection");
		// setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 702, 182);
		// already removed cols
		removedCols = mdrmCols;

		if (obj == null) {
			JOptionPane.showMessageDialog(null, "Error in metadata...", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// set data column
		dataColumnName = obj.getDatacol();

		// JOptionPane.showMessageDialog(null, Arrays.toString(headers));
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		contentPane = new JPanel();
//		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
//		panel.setBackground(Color.DARK_GRAY);
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
				dispose();
				ReadMetadata ob = new ReadMetadata(obj, obj.getdelimiter());
				ob.setVisible(true);
				
			}
		});
		panel.add(btnBack);


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
		

		JButton btnImport = new JButton("Next");
		btnImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				finish.setEnabled(false);

				// which headers were not used in metadata
				List<String> remainingCols = new ArrayList();
				List<String> selectedCols = new ArrayList();
				
				for (int i = 0; i < cBoxes.length; i++) {
					if(!(cBoxes[i] == null)){
						if(!cBoxes[i].isSelected()){
							String item = cBoxes[i].getName();
							remainingCols.add(item);
						}
						else {
							String item = cBoxes[i].getName();
							selectedCols.add(item);
						}
					}
				}

				
				obj.setRemoveCols(remainingCols);
				

				new AnimatedSwingWorker("Working...", true) {

					@Override
					public Object construct() {
						// return if data column has repeated names

						ParseTableTree ob = new ParseTableTree(obj, tree, dataColumnName);
						// org.jdom.Document res = ob.tableToTree(obj, tree);
						org.jdom.Document res = ob.tableToTree();
						
						if (!(MetaOmGraph.getActiveProject() == null)) {
							try {

								MetaOmGraph.getActiveProject().loadMetadataHybrid(obj, null,
										null, dataColumnName, selectedCols.toArray(new String[0]), null,
										null, null, missingDC, extraDC, removedCols);
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
//		panel_1_north.setBackground(Color.GRAY);
		JPanel panel_1_south = new JPanel(new FlowLayout(FlowLayout.CENTER));
//		panel_1_south.setBackground(Color.GRAY);
		panel_1.add(panel_1_north, BorderLayout.NORTH);


		panel_1.add(panel_1_south, BorderLayout.SOUTH);
		
		
		JPanel outerPanel = new JPanel(new BorderLayout());
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton selectAllButton = new JButton("Select All");
		JButton deselectAllButton = new JButton("Deselect All");
		
		buttonPanel.add(selectAllButton);
		buttonPanel.add(deselectAllButton);
		
		
		outerPanel.add(buttonPanel,BorderLayout.NORTH);
		// display jpanel with check box
		cBoxes = new JCheckBox[headers.length + 1];
		JPanel cbPanel = new JPanel();
		GridLayout gLayout = new GridLayout(0, 3);
		gLayout.setHgap(25);
		gLayout.setVgap(23);
		
		cbPanel.setLayout(gLayout);
		
		cbPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		for (int i = 0; i < headers.length; i++) {
			cBoxes[i] = new JCheckBox(headers[i]);
			
		}
		
		for (int i = 0; i < headers.length; i++) {
			cBoxes[i].setSelected(true);
		}
		
		TreeMap<String,Integer> sortedCheckboxesMap = new TreeMap<String,Integer>();
		
		for (int i = 0; i < headers.length; i++) {
			sortedCheckboxesMap.put(headers[i].toLowerCase(), i);
		}
		
		for(Map.Entry<String, Integer> entry : sortedCheckboxesMap.entrySet()) {
			
			cbPanel.add(cBoxes[entry.getValue()]);
			
			
		}
		
		outerPanel.add(cbPanel,BorderLayout.SOUTH);
		
		
		selectAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (int i = 0; i < headers.length; i++) {
					cBoxes[i].setSelected(true);
				}
			}
		});

		
		deselectAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (int i = 0; i < headers.length; i++) {
					cBoxes[i].setSelected(false);
				}
			}
		});

		
		contentPane.add(outerPanel, BorderLayout.CENTER);

		this.setSize(frameDimension.width, frameDimension.height - frameDimension.height/3);
//		this.pack();
		this.setLocation(locationOnScreen);
	}

}
