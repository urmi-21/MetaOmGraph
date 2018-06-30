package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import javax.swing.tree.TreePath;
import javax.swing.table.TableCellRenderer;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/*
 * This panel displays the metadata
 * Return an object to MetaOmTablePanel
 * e.g. extInfoPanel = new MetadataPanel(myProject.getMetadata());
 * 
 * MetaOmTablePanel displays this panel
 * 
 */

public class MetadataTreeDisplayPanel extends JPanel {
	private JTable table;
	private JTree tree;
	private JSplitPane splitPane;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JPanel panel;
	private Element XMLroot;
	private TreeMap<Integer, DefaultMutableTreeNode> knownColstoTreeNode;
	// panel menubar
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnEdit;
	private JMenu mnHelp;
	private JMenu mnFilter;
	private JMenuItem mntmByNodeLabels;
	private JMenuItem mntmByMetadata;
	private JMenuItem mntmExportXml;
	private JMenu mnSearch;
	private JMenuItem mntmSimple;
	private JMenuItem mntmClearLastSearch;
	private JMenu mnView;
	private JMenuItem mntmSwitchToTable;
	private List<String> excludedDatacols;

	/**
	 * Create the panel.
	 */
	public MetadataTreeDisplayPanel() {
		this(null);
	}

	public MetadataTreeDisplayPanel(Element XMLroot) {

		// set tree background fill to true otherwise it sets both foregraound and
		// background white
		UIManager.put("Tree.rendererFillBackground", true);
		setLayout(new BorderLayout(0, 0));
		// initialize table
		table = new JTable();
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		Vector<String> aVector = new Vector<String>();
		aVector.add("Attribute");
		aVector.add("Value");
		DefaultTableModel model = new DefaultTableModel(aVector, 0) {

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		table.setModel(model);
		// set alternate colors to table
		table.setDefaultRenderer(Object.class, new TableCellRenderer() {
			private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				if (row % 2 == 0) {

					if (isSelected) {
						c.setBackground(Color.BLACK);
					}
					if (!isSelected) {
						c.setBackground(Color.WHITE);
					}

				} else {

					if (isSelected) {
						c.setBackground(Color.BLACK);
					}
					if (!isSelected) {
						c.setBackground(new ColorUIResource(216, 236, 213));
					}
				}

				return c;
			}

		});
		// table.setForeground(Color.white);

		this.XMLroot = XMLroot;
		// load data into tree and table to display
		initDisplay();
		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		menuBar = new JMenuBar();
		panel.add(menuBar);
		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmExportXml = new JMenuItem("Export XML");
		mntmExportXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// save to file
				// JOptionPane.showMessageDialog(null, "saving file");
				JFileChooser fileChooser = new JFileChooser();
				int rVal = fileChooser.showSaveDialog(MetaOmGraph.getMainWindow());
				if (rVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					// JOptionPane.showMessageDialog(null, "fname:" +
					// fileChooser.getSelectedFile().getName());
					// save xml to file
					XMLOutputter outter = new XMLOutputter();
					outter.setFormat(Format.getPrettyFormat());
					org.jdom.Document res = new org.jdom.Document();

					Element xmlRootclone = (Element) XMLroot.clone();
					res.setRootElement(xmlRootclone);
					String resDoc = outter.outputString(res);

					try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
						out.println(resDoc);
					} catch (FileNotFoundException e) {
					}

				}
			}
		});
		mnFile.add(mntmExportXml);

		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		mnFilter = new JMenu("Filter");
		mnEdit.add(mnFilter);

		mntmByNodeLabels = new JMenuItem("By node labels");
		mnFilter.add(mntmByNodeLabels);

		mntmByMetadata = new JMenuItem("By Metadata");
		mnFilter.add(mntmByMetadata);

		mnView = new JMenu("View");
		mnEdit.add(mnView);

		mntmSwitchToTable = new JMenuItem("Switch To Table");
		mntmSwitchToTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// get current selected node
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				/*
				 * //get the column name using node level and tree structure //int
				 * nodeLevel=node.getLevel(); //JOptionPane.showMessageDialog(null,
				 * "node:"+node.toString()+"lev:"+nodeLevel); //JTree
				 * mstruct=MetaOmGraph.getActiveProject().getMetadataHybrid().getTreeStucture();
				 */

				// search anyfield in the metadata table that matched node name and highlight it
				MetaOmGraph.getActiveTable().selecTabRow(node.toString());

			}
		});
		mnView.add(mntmSwitchToTable);

		mnSearch = new JMenu("Search");
		menuBar.add(mnSearch);

		mntmSimple = new JMenuItem("Search");
		mnSearch.add(mntmSimple);

		mntmClearLastSearch = new JMenuItem("Clear Last Search");
		mnSearch.add(mntmClearLastSearch);

		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		splitPane.setDividerSize(2);
		splitPane.setResizeWeight(.31d);

		scrollPane.setViewportView(table);
		scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		scrollPane_1.setViewportView(tree);

	}

	public TreeMap<Integer, DefaultMutableTreeNode> getColstoTreeMap() {
		return this.knownColstoTreeNode;
	}

	/**
	 * This function builds a TreeMap colindex to JTreenodes equivalent to knowncols
	 */
	public void buildTreemaptoNode() {
		this.knownColstoTreeNode = new TreeMap<Integer, DefaultMutableTreeNode>();
		TreeMap<Integer, Element> knownCols = MetaOmGraph.getActiveProject().getMetadataHybrid().knownCols;
		// for all mappings in knownCols, map same to tree nodes
		List<DefaultMutableTreeNode> treeNodes = new ArrayList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.tree.getModel().getRoot();
		Enumeration<?> e = root.preorderEnumeration();
		while (e.hasMoreElements()) {
			treeNodes.add((DefaultMutableTreeNode) e.nextElement());
		}
		for (Entry<Integer, Element> entry : knownCols.entrySet()) {
			Integer key = entry.getKey();
			Element value = entry.getValue();
			// JOptionPane.showMessageDialog(null, "k:"+key);
			String name = null;
			if (value.getChildren().size() > 0) {
				name = value.getAttributeValue("name").toString();
			} else {
				try {
					name = value.getContent(0).getValue().toString();
				} catch (IndexOutOfBoundsException ie) {
					JOptionPane.showMessageDialog(null, "error key:" + key + " node:" + value.toString());
				}
			}

			if (name == null) {
				JOptionPane.showMessageDialog(null, "Error in tree search");
				return;
			}

			for (int i = 0; i < treeNodes.size(); i++) {
				DefaultMutableTreeNode thisNode = treeNodes.get(i);
				if (thisNode.toString().equals(name)) {
					knownColstoTreeNode.put(key, thisNode);
					break;
				}
			}
		}
	}

	/**
	 * Not used //urmi
	 * 
	 * @param me
	 */
	private void buildTableModel(MouseEvent me) {

		TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
		if (!(tp == null)) {
			// Jtable will have two cols attribute and value
			// reset table and remove existing rows
			Vector<String> aVector = new Vector<String>();
			aVector.add("Attribute");
			aVector.add("Value");
			DefaultTableModel model = new DefaultTableModel(aVector, 0) {

				@Override
				public boolean isCellEditable(int row, int column) {
					// all cells false
					return false;
				}
			};
			// table.setModel(model);
			// JOptionPane.showMessageDialog(null, "node clicked:" + tp.toString());
			// get equivalent node in XML
			List<String> strPath = new ArrayList<>();
			for (int i = 0; i < tp.getPathCount(); i++) {
				strPath.add(tp.getPathComponent(i).toString());
			}
			// strPath contains Root!!!

			buildTableData(getXMLnodeFromPath(strPath, XMLroot), model);
			table.setModel(model);
			table.repaint();
		}

	}

	private void buildTableData(Element node, DefaultTableModel model) {
		Vector<String> aVector = new Vector<String>();
		int childrenSize = node.getChildren().size();
		// add this node data
		if (childrenSize > 0) {
			aVector.addElement(node.getName().toString());
			aVector.addElement(node.getAttributeValue("name"));
			model.addRow(aVector);
			for (int j = 0; j < childrenSize; j++) {
				buildTableData((Element) node.getChildren().get(j), model);
			}

		} else {
			aVector.addElement(node.getName().toString());
			// if content has no value it returns null. Do if to handle cols with missing
			// values
			if(node.getContent(0)==null) {
				JOptionPane.showMessageDialog(null, "Error with content Nodename:"+node.getName().toString());
			}
			if (node.getContent(0).getValue().toString() != null) {
				aVector.addElement(node.getContent(0).getValue().toString());
			} else {
				aVector.addElement("");
			}
			model.addRow(aVector);
		}

		return;

	}

	private Element getXMLnodeFromTreeNode(DefaultMutableTreeNode tn, Element sroot) {
		// System.out.println("Start Search");
		Element result;

		// get to the node iterating over the tree. Assuming no duplicate values at any
		// level in tree
		Element currnode = sroot;
		// search XML tree for the selected node
		for (int j = 0; j < currnode.getChildren().size(); j++) {
			Element thisc = (Element) currnode.getChildren().get(j);
			// System.out.println(thisc.getAttributeValue("name"));
			String attName = thisc.getAttributeValue("name");
			if (!(attName == null)) {
				if (attName.equals(tn.toString())) {
					// System.out.println("Matched at" + thisc.getAttributeValue("name"));
					currnode = thisc;
					break;
				}
			}
			// for leaf node: leaf nodes has no attribute name only has content of size 1
			// with their value
			else {
				// System.out.println("ELSE:" + thisc.getContent(0).getValue().toString());
				if (thisc.getContent(0).getValue().toString().equals(tn.toString())) {
					// System.out.println("Matched at" + thisc.getContent(0).getValue().toString());
					currnode = thisc;
					break;
				}
			}

		}

		result = currnode;
		// System.out.println("final:" + result.getAttributeValue("name"));
		// System.out.println("final:" + result.getContent(0).getValue().toString());
		// JOptionPane.showMessageDialog(null, "final:" +
		// result.getContent(0).getValue().toString());
		// JOptionPane.showMessageDialog(null, "final name:" +
		// result.getAttributeValue("name"));
		return result;
	}

	private Element getXMLnodeFromPath(List<String> strPath, Element sroot) {
		// System.out.println("Start Search");
		Element result;
		// remove the root from the list
		strPath.remove(0);
		// get to the node iterating over the tree. Assuming no duplicate values at any
		// level in tree
		Element currnode = sroot;
		for (int i = 0; i < strPath.size(); i++) {
			// System.out.println(strPath.get(i));
			for (int j = 0; j < currnode.getChildren().size(); j++) {
				Element thisc = (Element) currnode.getChildren().get(j);
				// System.out.println(thisc.getAttributeValue("name"));
				String attName = thisc.getAttributeValue("name");
				if (!(attName == null)) {
					if (attName.equals(strPath.get(i))) {
						// System.out.println("Matched at" + thisc.getAttributeValue("name"));
						currnode = thisc;
						break;
					}
				}
				// for leaf node: leaf nodes has no attribute name only has content of size 1
				// with their value
				else {
					// System.out.println("ELSE:" + thisc.getContent(0).getValue().toString());
					if (thisc.getContent(0).getValue().toString().equals(strPath.get(i))) {
						// System.out.println("Matched at" + thisc.getContent(0).getValue().toString());
						currnode = thisc;
						break;
					}
				}

			}
		}

		result = currnode;
		// System.out.println("final:" + result.getAttributeValue("name"));
		// System.out.println("final:" + result.getContent(0).getValue().toString());
		// JOptionPane.showMessageDialog(null, "final:" +
		// result.getContent(0).getValue().toString());
		// JOptionPane.showMessageDialog(null, "final name:" +
		// result.getAttributeValue("name"));
		return result;
	}

	private void initDisplay() {

		// add XML root data to jtree, tree nodes should display only XMLnode's
		// attribute name or content value for child node
		initExcludedDataCols();
		tree = new JTree();
		tree.setModel(createTreeModel(XMLroot));
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent se) {
				// TODO Auto-generated method stub
				JTree tree = (JTree) se.getSource();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				TreePath tp = se.getPath();
				// get tree path
				List<String> strPath = new ArrayList<>();
				for (int i = 0; i < tp.getPathCount(); i++) {
					strPath.add(tp.getPathComponent(i).toString());
				}
				// create a new empty model
				Vector<String> aVector = new Vector<String>();
				aVector.add("Attribute");
				aVector.add("Value");
				DefaultTableModel model = new DefaultTableModel(aVector, 0) {

					@Override
					public boolean isCellEditable(int row, int column) {
						// all cells false
						return false;
					}
				};
				buildTableData(getXMLnodeFromPath(strPath, XMLroot), model);
				table.setModel(model);
				table.repaint();
			}
		});

		buildTreemaptoNode();
	}

	/*
	 * This function creates a small tree to preview imported tree model
	 */
	public DefaultTreeModel createTreeModel(Element XMLroot) {

		JTree pTree = new JTree();
		pTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Root") {
			{
			}
		}));
		DefaultTreeModel treeModel = (DefaultTreeModel) pTree.getModel();
		Element root = XMLroot;
		List<Element> cList = root.getChildren();
		// get root of Jtree
		DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) treeModel.getRoot();
		ceateDisplayTreefromXML(root, treeRoot);
		return (DefaultTreeModel) pTree.getModel();
	}

	/*
	 * This function reads Root Element and creates a corresponding JTree node and
	 * adds all the child recursively
	 */

	public void ceateDisplayTreefromXML(Element root, DefaultMutableTreeNode node) {
		List<Element> cList = root.getChildren();
		if (cList.size() < 1) {

			return;
		}

		for (Element c : cList) {
			if (this.excludedDatacols != null || this.excludedDatacols.size() > 0) {
				if (!(c.getAttribute("name") == null) && excludedDatacols.contains(c.getAttributeValue("name"))) {
					// JOptionPane.showMessageDialog(null, "1skipping:" +
					// c.getAttributeValue("name"));
					continue;
				}
				if (c.getAttribute("name") == null
						&& excludedDatacols.contains(c.getContent(0).getValue().toString())) {
					// JOptionPane.showMessageDialog(null, "2skipping:" +
					// c.getContent(0).getValue().toString());
					continue;
				}
			}

			DefaultMutableTreeNode newNode;
			if (!(c.getAttribute("name") == null)) {
				String nodeName = "";
				nodeName += c.getAttributeValue("name");
				// JOptionPane.showMessageDialog(null, "nodename:"+nodeName);
				newNode = new DefaultMutableTreeNode(nodeName);
			} else {
				String nodeName = "";
				nodeName += c.getContent(0).getValue().toString();
				newNode = new DefaultMutableTreeNode(nodeName);
			}
			ceateDisplayTreefromXML(c, newNode);
			node.add(newNode);
		}

	}

	public static void main(String[] args) {
		System.out.println("main");
		Element sroot = new Element("root");
		Element p1 = new Element("parent1");
		p1.setAttribute("name", "isparent1");
		Element p2 = new Element("parent2");
		p2.setAttribute("name", "isparent2");
		Element p3 = new Element("parent3");
		p3.setAttribute("name", "isparent3");
		Element l1 = new Element("leaf1");
		l1.addContent("isleaf1");
		p3.addContent(l1);
		p2.addContent(p3);
		p1.addContent(p2);
		sroot.addContent(p1);
		List<String> strPath = new ArrayList<>();
		strPath.add("root");
		strPath.add("isparent1");
		strPath.add("isparent2");
		// strPath.add("isparent3");
		// strPath.add("isleaf1");
		// getXMLnodeFromPath(strPath, sroot);

	}

	public JTree getTree() {
		return this.tree;
	}

	public JSplitPane getSplitPane() {
		return this.splitPane;
	}

	public void initExcludedDataCols() {
		this.excludedDatacols = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataCollection()
				.getExcluded();
		if (this.excludedDatacols == null) {
			this.excludedDatacols = new ArrayList<>();
		}
		// JOptionPane.showMessageDialog(null, "ex:" + excludedDatacols.toString());
	}

	public void updateTree() {
		initDisplay();
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		scrollPane_1.setViewportView(tree);

	}

}
