package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
//import javax.xml.bind.annotation.XmlRootElement;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultCaret;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

//import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import javax.swing.JLabel;

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
	private TreeMap<Integer, Element> knownCols;
	// panel menubar
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnEdit;
	private JMenu mnHelp;
	private JMenu mnFilter;
	private JMenuItem mntmByMetadata;
	private JMenuItem mntmExportXml;
	// private JMenu mnSearch;
	// private JMenuItem mntmSimple;
	private JMenuItem mntmClearLastSearch;
	private JMenu mnView;
	private JMenuItem mntmSwitchToTable;
	private Set<String> excludedDatacols;
	private Set<String> includedDatacols;
	private String dataColname;
	private JMenu mnSearch_1;
	private JMenuItem mntmSearch;
	private JMenuItem mntmReset;

	private MetadataCollection obj;
	private MetadataHybrid mdhobj;

	private List<DefaultMutableTreeNode> toHighlightNodes;

	private HashMap<DefaultMutableTreeNode, String> jtreeMetadata;

	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColor1();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColor2();
	private Color HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
	private Color HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();

	/**
	 * Create the panel.
	 */
	public MetadataTreeDisplayPanel() {
		this(null);
	}

	public MetadataTreeDisplayPanel(MetadataHybrid mdh) {

		// set tree background fill to true otherwise it sets both foregraound and
		// background white
		UIManager.put("Tree.rendererFillBackground", true);
		setLayout(new BorderLayout(0, 0));
		mdhobj = mdh;
		Element XMLrootOrig = mdhobj.getXMLRoot();
		obj = mdhobj.getMetadataCollection();
		this.dataColname = mdhobj.getDataColName();
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
			//private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
			private WordWrapCellRenderer my_RENDERER= new WordWrapCellRenderer();
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = my_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				if (row % 2 == 0) {

					if (isSelected) {
						c.setBackground(SELECTIONBCKGRND);
						c.setForeground(Color.WHITE);
					}
					if (!isSelected) {
						c.setBackground(BCKGRNDCOLOR1);
						c.setForeground(Color.black);
					}

				} else {

					if (isSelected) {
						c.setBackground(SELECTIONBCKGRND);
						c.setForeground(Color.WHITE);
					}
					if (!isSelected) {
						c.setBackground(BCKGRNDCOLOR2);
						c.setForeground(Color.black);
					}
				}

				return c;
			}

		});
		// table.getColumnModel().getColumn(0).setPreferredWidth(150);
		// table.getColumnModel().getColumn(1).setPreferredWidth(800);

		// table.setForeground(Color.white);

		// this.XMLroot = XMLroot;
		this.XMLroot = (Element) XMLrootOrig.clone();

		// load data into tree and table to display
		initDisplay(XMLroot);
		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		menuBar = new JMenuBar();
		panel.add(menuBar);
		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmExportXml = new JMenuItem("Export XML");
		mntmExportXml.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// save to file
				// JOptionPane.showMessageDialog(null, "saving file");
				File file = Utils.chooseFileToSave(new GraphFileFilter(GraphFileFilter.XML), "xml",
						MetaOmGraph.getMainWindow(), true);
				if (file == null) {
					return;
				}
				// save xml to file
				XMLOutputter outter = new XMLOutputter();
				outter.setFormat(Format.getPrettyFormat());
				org.jdom.Document res = new org.jdom.Document();

				Element xmlRootclone = (Element) XMLroot.clone();
				res.setRootElement(xmlRootclone);
				String resDoc = outter.outputString(res);

				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				HashMap<String,Object> resultLog = new HashMap<String,Object>();

				try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
					out.println(resDoc);

					actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
					dataMap.put("File Path",file.getAbsolutePath());
					dataMap.put("section","Sample Metadata Tree");
					resultLog.put("result", "OK");

					ActionProperties exportXMLAction = new ActionProperties("export-xml",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					exportXMLAction.logActionProperties();
				} catch (FileNotFoundException e) {

					actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
					dataMap.put("section","Sample Metadata Tree");
					resultLog.put("result", "Error");
					resultLog.put("result", "File not found");

					ActionProperties exportXMLAction = new ActionProperties("export-xml",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					exportXMLAction.logActionProperties();
				}
				catch(Exception e) {
					actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
					dataMap.put("section","Sample Metadata Tree");
					resultLog.put("result", "Error");
					resultLog.put("result", "Other Exception");

					ActionProperties exportXMLAction = new ActionProperties("export-xml",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					exportXMLAction.logActionProperties();
				}

			}
		});
		mnFile.add(mntmExportXml);

		mnSearch_1 = new JMenu("Search");
		menuBar.add(mnSearch_1);

		mntmSearch = new JMenuItem("Search");
		mntmSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				QueryResults qr = getQueryResults("Metadata Search");
				if(qr == null)
					return;
				List<String> result = qr.getFresults();
				MetadataQuery[] queries = qr.getFqueries();
				
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Tree");
				
				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				List<String> mq = new ArrayList();
				for(MetadataQuery q: queries) {
					mq.add(q.toString());
				}
				dataMap.put("Queries",mq);
				dataMap.put("numHits", result.size());
				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");
				
				if (result == null || result.size() < 1 || result.get(0).equals("NULL")) {
					JOptionPane.showMessageDialog(null, "No hits found", "No hits", JOptionPane.INFORMATION_MESSAGE);
					resultLog.put("result", "Error");
					resultLog.put("resultComments", "No hits found");
					ActionProperties searchMetadataTreeAction = new ActionProperties("search-metadata-tree",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					searchMetadataTreeAction.logActionProperties();
					return;
				}
				
				ActionProperties searchMetadataTreeAction = new ActionProperties("search-metadata-tree",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				searchMetadataTreeAction.logActionProperties();

				expandNodes(result);

			}
		});
		mnSearch_1.add(mntmSearch);

		mntmClearLastSearch = new JMenuItem("Clear Last Search");
		mntmClearLastSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toHighlightNodes = new ArrayList<>();

				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Tree");

				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");

				ActionProperties clearSearchMetadataTreeAction = new ActionProperties("clear-search-metadata-tree",actionMap,null,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				clearSearchMetadataTreeAction.logActionProperties();
			}
		});
		mnSearch_1.add(mntmClearLastSearch);
		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		mnFilter = new JMenu("Filter");
		mnEdit.add(mnFilter);

		mntmByMetadata = new JMenuItem("By Metadata");
		mntmByMetadata.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				QueryResults qr = getQueryResults("Filter by Metadata"); 
				List<String> result = qr.getFresults();
				MetadataQuery[] queries = qr.getFqueries();
				
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Tree");
				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				List<String> mq = new ArrayList();
				for(MetadataQuery q: queries) {
					mq.add(q.toString());
				}
				dataMap.put("Queries",mq);
				
				dataMap.put("numHits", result.size());
				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");
				
				if (result == null || result.size() < 1 || result.get(0).equals("NULL")) {
					JOptionPane.showMessageDialog(null, "No hits found", "No hits", JOptionPane.INFORMATION_MESSAGE);
					resultLog.put("result", "Error");
					resultLog.put("resultComments", "No hits found");
					ActionProperties filterMetadataTreeAction = new ActionProperties("filter-metadata-tree",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					filterMetadataTreeAction.logActionProperties();
					return;
				}

				boolean keep = false;
				Object[] options = { "Remove", "Keep", "Cancel" };
				JPanel optPanel = new JPanel();
				optPanel.add(new JLabel("Remove or keep searched nodes ?"));
				int option = JOptionPane.showOptionDialog(null, optPanel, "Choose an option",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
				if (option == JOptionPane.YES_OPTION) {
					keep = false;
				} else if (option == JOptionPane.NO_OPTION) {
					keep = true;
				}
				// if invert then invert here
				if (!keep) {
					filterXMLRoot(result);

				} else {
					List<String> result2 = obj.invertSelectedDataCols(result);
					filterXMLRoot(result2);
				}
				
				if (option == JOptionPane.YES_OPTION) {
					dataMap.put("keepOrRemove", "remove");
				}
				else if (option == JOptionPane.NO_OPTION) {
					dataMap.put("keepOrRemove", "keep");
				}
				
				ActionProperties filterMetadataTreeAction = new ActionProperties("filter-metadata-tree",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				filterMetadataTreeAction.logActionProperties();
				
			}
		});
		mnFilter.add(mntmByMetadata);

		mntmReset = new JMenuItem("Reset");
		mntmReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// update excluded included
				obj.resetRowFilter();
				updateTree();
				
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Tree");
				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");
				
				ActionProperties resetTreeAction = new ActionProperties("reset-metadata-tree",actionMap,null,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				resetTreeAction.logActionProperties();
			}
		});
		mnFilter.add(mntmReset);

		mnView = new JMenu("View");
		mnEdit.add(mnView);

		mntmSwitchToTable = new JMenuItem("Switch To Table");
		mntmSwitchToTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get current selected node
				
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Sample Metadata Tree");
				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node == null) {
					JOptionPane.showMessageDialog(null, "No node selected", "Please selcet a node",
							JOptionPane.ERROR_MESSAGE);
					
					resultLog.put("result", "Error");
					resultLog.put("resultComments", "No node selected.Please selcet a node");
					ActionProperties resetTreeAction = new ActionProperties("switch-to-table",actionMap,null,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					resetTreeAction.logActionProperties();
					return;
				}

				// search anyfield in the metadata table that matched node name and highlight it
				MetaOmGraph.getActiveTable().selecTabRow(node.toString());
				
				ActionProperties resetTreeAction = new ActionProperties("switch-to-table",actionMap,null,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				resetTreeAction.logActionProperties();

			}
		});
		mnView.add(mntmSwitchToTable);

		// @TODO Implement me when help manual is added.
		/*mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);*/

		splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		splitPane.setDividerSize(2);
		splitPane.setResizeWeight(.3d);

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
		this.knownCols = mdhobj.knownCols;
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
	 * build table model
	 * 
	 * @param node
	 * @param model
	 */
	/*
	 * private void buildsTableData(Element node, DefaultTableModel model) {
	 * Vector<String> aVector = new Vector<String>(); int childrenSize =
	 * node.getChildren().size(); // add this node data if (childrenSize > 0) {
	 * aVector.addElement(node.getName().toString());
	 * aVector.addElement(node.getAttributeValue("name")); model.addRow(aVector);
	 * for (int j = 0; j < childrenSize; j++) { buildsTableData((Element)
	 * node.getChildren().get(j), model); }
	 * 
	 * } else { JOptionPane.showMessageDialog(null, "thisName:" +
	 * node.getName().toString()); aVector.addElement(node.getName().toString()); //
	 * if content has no value it returns null. Do if to handle cols with missing //
	 * values if (node.getContent(0) == null) { JOptionPane.showMessageDialog(null,
	 * "Error with content Nodename:" + node.getName().toString()); } if
	 * (node.getContent(0).getValue().toString() != null) {
	 * aVector.addElement(node.getContent(0).getValue().toString()); } else {
	 * aVector.addElement(""); } model.addRow(aVector); }
	 * 
	 * return;
	 * 
	 * }
	 */

	private void buildTableData(DefaultMutableTreeNode selectedNode, DefaultTableModel model) {
		Vector<String> aVector = new Vector<String>();
		if (jtreeMetadata == null) {
			// JOptionPane.showMessageDialog(null, "Error. jtreeMetadata is NULL");
			return;
		}
		// iterate over all children
		String thisMD = jtreeMetadata.get(selectedNode);
		if (thisMD == null) {
			// JOptionPane.showMessageDialog(null, "Error. jtreeMetadata is NULL");
			return;
		}
		String[] thisRow = thisMD.split(":::");
		aVector.addElement(thisRow[0]);
		aVector.addElement(thisRow[1]);
		//aVector.addElement("<html><br>"+thisRow[1]+"<br>asasd</html>");
		//JOptionPane.showMessageDialog(null, "<html><br>"+thisRow[1]+"<br>asasd</html>");
		model.addRow(aVector);
		for (int i = 0; i < selectedNode.getChildCount(); i++) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
			buildTableData(thisNode, model);
		}

	}

	// TODO: Duplicate values at same level cause incorrect result.
	/*
	 * private Element getXMLnodeFromPath(List<String> strPath, Element sroot) { //
	 * System.out.println("Start Search"); JOptionPane.showMessageDialog(null,
	 * "path:" + strPath); Element result; // remove the root from the list
	 * strPath.remove(0); // get to the node iterating over the tree. Assuming no
	 * duplicate values at any // level in tree Element currnode = sroot; for (int i
	 * = 0; i < strPath.size(); i++) { // System.out.println(strPath.get(i)); for
	 * (int j = 0; j < currnode.getChildren().size(); j++) { Element thisc =
	 * (Element) currnode.getChildren().get(j); //
	 * System.out.println(thisc.getAttributeValue("name")); String attName =
	 * thisc.getAttributeValue("name"); if (!(attName == null)) { if
	 * (attName.equals(strPath.get(i))) { // System.out.println("Matched at" +
	 * thisc.getAttributeValue("name")); currnode = thisc; break; } } // for leaf
	 * node: leaf nodes has no attribute name only has content of size 1 // with
	 * their value else { JOptionPane.showMessageDialog(null, "ELSE:" +
	 * thisc.getContent(0).getValue().toString()); if
	 * (thisc.getContent(0).getValue().toString().equals(strPath.get(i))) {
	 * System.out.println("Matched at" + thisc.getContent(0).getValue().toString());
	 * currnode = thisc; break; } }
	 * 
	 * } }
	 * 
	 * result = currnode; return result; }
	 */

	private void initDisplay(Element XMLroot) {

		// add XML root data to jtree, tree nodes should display only XMLnode's
		// attribute name or content value for child node
		// new list of nodes which will be highlighted
		toHighlightNodes = new ArrayList<>();
		initExcludedDataCols();
		removeExcludedFromTree(XMLroot);
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
				// buildTableData(getXMLnodeFromPath(strPath, XMLroot), model);

				buildTableData(selectedNode, model);
				table.setModel(model);
				table.getColumnModel().getColumn(0).setPreferredWidth(150);
				table.getColumnModel().getColumn(1).setPreferredWidth(800);
				table.repaint();
			}
		});

		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
						hasFocus);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				if (toHighlightNodes.contains(node)) {
					// TreeNode[] pathNodes = node.getPath();
					// TreePath path = new TreePath(pathNodes);
					// tree.setSelectionPath(path);
					// Rectangle rect = tree.getPathBounds(path);
					label.setForeground(HIGHLIGHTCOLOR);
				}

				return label;
			}
		});

		buildTreemaptoNode();
	}

	/*
	 * This function creates a small tree to preview imported tree model
	 */
	public DefaultTreeModel createTreeModel(Element XMLroot) {
		// to store metadata of each jtree node
		jtreeMetadata = new HashMap<>();
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
			DefaultMutableTreeNode newNode;
			String nodeName = "";
			if (!(c.getAttribute("name") == null)) {

				nodeName += c.getAttributeValue("name");
				// JOptionPane.showMessageDialog(null, "nodename:"+nodeName);
				newNode = new DefaultMutableTreeNode(nodeName);
			} else {

				nodeName += c.getContent(0).getValue().toString();
				newNode = new DefaultMutableTreeNode(nodeName);

			}
			ceateDisplayTreefromXML(c, newNode);

			node.add(newNode);
			// JOptionPane.showMessageDialog(null, "nodeAdded"+ c.getName()+
			// ":::"+nodeName);
			jtreeMetadata.put(newNode, c.getName() + ":::" + nodeName);
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
		this.excludedDatacols = obj.getExcluded();
		if (this.excludedDatacols == null) {
			this.excludedDatacols = new HashSet<>();
		}
		this.includedDatacols = obj.getIncluded();
		if (this.includedDatacols == null) {
			this.includedDatacols = new HashSet<>();
		}

	}

	public void updateTree() {
		new AnimatedSwingWorker("Updating tree...", true) {
			@Override
			public Object construct() {
				// get original xml root
				XMLroot = (Element) mdhobj.getXMLRoot().clone();
				// filter the tree
				initDisplay(XMLroot);
				tree.setShowsRootHandles(true);
				tree.setRootVisible(false);
				scrollPane_1.setViewportView(tree);
				return null;
			}

		}.start();

		// update exclude list
		MetaOmAnalyzer.updateExcluded(obj.getExcluded());
		MetaOmGraph.getActiveTable().updateMetadataTable();

	}

	/**
	 * Update the excluded and included datacolumns
	 * 
	 * @param resColumns
	 */
	public void filterXMLRoot(List<String> resColumns) {

		List<Element> nodesToFilter = new ArrayList<>();
		List<Element> clist = XMLroot.getChildren();
		Set<String> inc = obj.getIncluded();
		Set<String> exc = obj.getExcluded();
		for (int i = 0; i < resColumns.size(); i++) {
			String temp = resColumns.get(i);
			exc.add(temp);
			inc.remove(temp);
		}
		// update included and excluded in mog collection
		obj.setExcluded(exc);
		obj.setIncluded(inc);
		updateTree();
		
		HashMap<String,Object> actionMap = new HashMap<String,Object>();
		HashMap<String,Object> dataMap = new HashMap<String,Object>();
		HashMap<String,Object> result = new HashMap<String,Object>();
		
		try {
			
		actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
		actionMap.put("section", "Feature Metadata");
		
		
		MetadataHybrid mhyb = MetaOmGraph.getActiveProject().getMetadataHybrid();
		if(mhyb !=null) {
			MetadataCollection mcol = mhyb.getMetadataCollection();
			if(mcol!= null) {
				dataMap.put("Data Column", mcol.getDatacol());
				result.put("Included Samples", mcol.getIncluded());
				result.put("Excluded Samples", mcol.getExcluded());
			}
		}
		else {
			result.put("Included Samples", null);
			result.put("Excluded Samples", null);
		}

		result.put("result", "OK");

		ActionProperties sampleFilterAction = new ActionProperties("sample-advance-filter",actionMap,dataMap,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
		sampleFilterAction.logActionProperties();
		
		MetaOmGraph.setCurrentSamplesActionId(sampleFilterAction.getActionNumber());
		}
		catch(Exception e1) {
			
		}

	}

	/**
	 * remove excluded nodes from current tree
	 */
	public void removeExcludedFromTree(Element XMLrootNode) {
		List<Element> nodesToFilter = new ArrayList<>();
		List<Element> clist = XMLrootNode.getChildren();
		for (int i = 0; i < clist.size(); i++) {
			Element thisNode = clist.get(i);
			createfilterXMLNodeList(thisNode, this.excludedDatacols, nodesToFilter);
		}

		// JOptionPane.showMessageDialog(null,"exc len:" + this.excludedDatacols.size()
		// + " " + this.excludedDatacols.toString());
		// JOptionPane.showMessageDialog(null, "ntr:" + nodesToFilter.size());
		// filter nodes
		for (int i = 0; i < nodesToFilter.size(); i++) {
			Element thisNode = nodesToFilter.get(i);
			// remove all children
			clist = thisNode.getChildren();
			for (Iterator it = clist.iterator(); it.hasNext();) {
				Element thisChild = (Element) it.next();
				it.remove();
			}
			// remove parents if they dont have any other children
			Element thisParent = thisNode.getParentElement();
			Element singleParent = thisNode;
			while (thisParent != null) {
				List<Element> thisPclist = thisParent.getChildren();
				int countC = 0;
				for (int k = 0; k < thisPclist.size(); k++) {
					if (thisPclist.get(k).getName().equals(singleParent.getName())) {
						countC++;
					}
					if (countC > 1) {
						break;
					}
				}
				// remove parent if no other children exist
				if (countC == 1) {
					singleParent = thisParent;
					thisParent = thisParent.getParentElement();
				} else {
					// remove single parent from this parent
					// JOptionPane.showMessageDialog(null,
					// "removing:"+singleParent.getAttributeValue("name").toString());
					// thisParent.removeChild(singleParent.getAttributeValue("name").toString());
					singleParent.detach();
					break;

				}
			} // end while

		} // end outer for
	}

	/**
	 * Create a list of Elements to be removed
	 * 
	 * @param thisNode
	 * @param resColumns
	 * @param toRemove
	 */
	public void createfilterXMLNodeList(Element thisNode, Set<String> resColumns, List<Element> toRemove) {
		List<Element> thisclist = thisNode.getChildren();
		if (thisNode.getName().equals(dataColname)) {
			String thisDataCol = "";
			if (thisclist.size() > 0) {
				thisDataCol = thisNode.getAttributeValue("name").toString();
			} else {

				thisDataCol = thisNode.getContent(0).getValue().toString();
			}
			if (resColumns.contains(thisDataCol)) {
				// JOptionPane.showMessageDialog(null,"adding:"+ thisNode.toString()+"
				// "+thisDataCol);
				toRemove.add(thisNode);
			}

			return;
		}

		for (int i = 0; i < thisclist.size(); i++) {
			createfilterXMLNodeList(thisclist.get(i), resColumns, toRemove);
		}

	}

	/**
	 * Display search panel and return search results as a list
	 * 
	 * @return
	 */
	public QueryResults getQueryResults(String title) {
		final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
				MetaOmGraph.getActiveProject(), false);
		final MetadataQuery[] queries;
		queries = tsp.showSearchDialog(title);
		if (tsp.getQueryCount() <= 0) {
			// System.out.println("Search dialog cancelled");
			// User didn't enter any queries
			return null;
		}
		// final int[] result = new
		// int[MetaOmGraph.getActiveProject().getDataColumnCount()];
		final List<String> result = new ArrayList<>();
		new AnimatedSwingWorker("Searching...", true) {
			@Override
			public Object construct() {
				List<String> hits = mdhobj.getMatchingRows(queries, tsp.matchAll());
				// return if no hits
				if (hits.size() == 0) {
					// JOptionPane.showMessageDialog(null, "hits len:"+hits.length);
					// nohits=true;
					result.add("NULL");
					return null;
				} else {
					for (int i = 0; i < hits.size(); i++) {
						result.add(hits.get(i));
					}
				}

				return null;
			}

		}.start();

		QueryResults searchResult = new QueryResults(result,queries);
		return searchResult;

	}

	/**
	 * Expand and highlight all the nodes matching names in a given list
	 * 
	 * @param nodes
	 */
	public void expandNodes(List<String> nodes) {
		// colapse all nodes

		// remove previous searches
		toHighlightNodes = new ArrayList<>();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		TreePath path = new TreePath(root);

		Enumeration en = root.preorderEnumeration();
		while (en.hasMoreElements()) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) en.nextElement();
			String thisName = thisNode.toString();
			if (nodes.contains(thisName)) {
				toHighlightNodes.add(thisNode);
				// expand node
				int thisInd = MetaOmGraph.getActiveProject().getMetadataHybrid().getColIndexbyName(thisName);
				// second argument select parent of the node
				MetaOmGraph.getActiveTable().selectNode(thisInd, false);
			} else {
				// colapse this node
				TreePath p = path.pathByAddingChild(thisNode);
				tree.collapsePath(p);
			}

		}

	}

	public void updateColors() {
		SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
		BCKGRNDCOLOR1 = MetaOmGraph.getTableColor1();
		BCKGRNDCOLOR2 = MetaOmGraph.getTableColor2();
		HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
		HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();
		table.repaint();
	}

	/**
	 * TableCellRenderer to wrap text in jtable rows
	 * @author mrbai
	 *
	 */
	class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		WordWrapCellRenderer() {
			setLineWrap(true);
			setWrapStyleWord(true);
			DefaultCaret caret = (DefaultCaret) getCaret();
			caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			setText(value.toString());
			setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
			setFont(new Font("Tahoma", 0, 11));
			if (table.getRowHeight(row) != getPreferredSize().height) {
				table.setRowHeight(row, getPreferredSize().height);
			}

			return this;
		}
	}

}

class QueryResults{
	private List<String> fresults;
	private MetadataQuery[] fqueries;
	
	
	public QueryResults(List<String> fresults, MetadataQuery[] fqueries) {
		super();
		this.fresults = fresults;
		this.fqueries = fqueries;
	}
	public List<String> getFresults() {
		return fresults;
	}
	public void setFresults(List<String> fresults) {
		this.fresults = fresults;
	}
	public MetadataQuery[] getFqueries() {
		return fqueries;
	}
	public void setFqueries(MetadataQuery[] fqueries) {
		this.fqueries = fqueries;
	}
	
	
}