package edu.iastate.metnet.metaomgraph.ui;
/*
 * This class deals with parsing operations e.g. convert MetadataCollection object to tree and vice-versa.
 * Can include other functions too.
 */

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.dizitart.no2.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;

/**
 * Class handles conversion of tabular to tree data structures
 * 
 * @author urmi
 *
 */
public class ParseTableTree {
	private MetadataCollection obj = null;
	private List<String> allMetadataCols = null;
	// for progress bar
	private JFrame frame;
	private List<String> importedHeaders;
	JProgressBar pBar;
	// set the frame in the middle of screen
	Dimension dim;
	private String dataColName;
	private List<String> colHeaders;
	// maps col index in data file to
	private TreeMap<Integer, Map<String,String>> colIndextoNode;


	private String defaultrepscol;

	public ParseTableTree(MetadataCollection csvObj, List<String> allMetadataCols, String name, String[] colheaders) {
		this.obj = csvObj;
		this.allMetadataCols = allMetadataCols;
		dataColName = name;
		colHeaders = Arrays.asList(colheaders);
	}

	public ParseTableTree(MetadataCollection csvObj, List<String> allMetadataCols, String name) {
		this.obj = csvObj;
		this.allMetadataCols = allMetadataCols;
		dataColName = name;
		if (!(MetaOmGraph.getActiveProject() == null)) {
			colHeaders = Arrays.asList(MetaOmGraph.getActiveProject().getDataColumnHeaders());
		} else {
			JOptionPane.showMessageDialog(null, "Error initializing project");
			return;
		}
		// JOptionPane.showMessageDialog(null, "colheaders in order");
		// JOptionPane.showMessageDialog(null, colHeaders);
	}

	/*
	 * This functions map tabular data to XML format given data and a structure
	 * TODO: add progress bar
	 */
	public void tableToTree() {
		importedHeaders = new ArrayList<>();
		colIndextoNode = new TreeMap<Integer, Map<String,String>>();
		/////////////////
		long startTime = System.currentTimeMillis();
		try {
			buildTree();
		}
		catch(Exception ex) {
			JOptionPane.showMessageDialog(null,"Error building tree from table","Error",JOptionPane.ERROR_MESSAGE);
		}
		
		importedHeaders = Arrays.asList(this.obj.getHeaders());


	}

	public String doctoString(org.jdom.Document res) {
		XMLOutputter outter = new XMLOutputter();
		// outter.setFormat(Format.getPrettyFormat());
		String resDoc = outter.outputString(res);
		// remove all /n MOG doesn't show metadata correctly without this
		resDoc.replaceAll("\n", "");
		return resDoc;
	}



	public String[] getMetadataHeaders() {
		// remove duplicates
		String[] result = new String[importedHeaders.size()];
		result = importedHeaders.toArray(result);
		return result;
	}

	public TreeMap<Integer, Map<String,String>> getMetadataMap() {
		return this.colIndextoNode;
	}

	

	

	/*
	 * This function creates a small tree to preview imported tree model
	 */
	public DefaultTreeModel createPreviewTreeModel(org.jdom.Document res, int n) {

		JTree pTree = new JTree();
		pTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Root") {
			{
			}
		}));
		DefaultTreeModel treeModel = (DefaultTreeModel) pTree.getModel();
		Element root = res.getRootElement();
		List<Element> cList = root.getChildren();
		// add only first n child of the root, if less then add all
		// remove all other children
		for (int i = 0; i < cList.size(); i++) {
			if (i >= n) {
				cList.get(i).setName("sjkvSDE");
			}
		}
		root.removeChildren("sjkvSDE");
		DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) treeModel.getRoot();

		ceateTreefromXML(root, treeRoot);
		return (DefaultTreeModel) pTree.getModel();
	}

	/*
	 * This function reads Root Element and creates a corresponding JTree node and
	 * adds all the child recursively
	 */

	public void ceateTreefromXML(Element root, DefaultMutableTreeNode node) {
		List<Element> cList = root.getChildren();
		if (cList.size() < 1) {

			return;
		}

		for (Element c : cList) {
			DefaultMutableTreeNode newNode;
			if (!(c.getAttribute("name") == null)) {
				String nodeName = "";
				nodeName += c.getName() + ":";
				nodeName += c.getAttributeValue("name");
				newNode = new DefaultMutableTreeNode(nodeName);
			} else {
				String nodeName = "";
				nodeName += c.getName() + ":";
				nodeName += c.getContent(0).getValue().toString();
				newNode = new DefaultMutableTreeNode(nodeName);
			}
			ceateTreefromXML(c, newNode);
			node.add(newNode);
		}

	}


	public String getDefaultRepCol() {
		return this.defaultrepscol;
	}

	/**
	 * Non recursive function to convert table to XML
	 * 
	 * @param root
	 * @return
	 */
	private void buildTree() {
		// JOptionPane.showMessageDialog(null, "Start..");
		// get the data
		List<Document> tabData = obj.getAllData();
		if (tabData.size() == 0) {
			return;
		}
		
		defaultrepscol = this.dataColName;
		
		// for all rows in data
		for (int i = 0; i < tabData.size(); i++) {
			// add outermost node i.e. level 1
			String expName = tabData.get(i).get(dataColName).toString();
			
			int thisIndex = colHeaders.indexOf(expName);
			
			String [] mHeaders = obj.getHeaders();
			
			HashMap<String, String> metadataMap = new HashMap<String, String>();
					
			for(String col: mHeaders) {
				String val = (String)tabData.get(i).get(col);
				
				metadataMap.put(col, val);
			}
			colIndextoNode.put(Integer.valueOf(thisIndex), metadataMap);
		}
				
	}

	private HashMap<String, List<String>> getNodeChildren(TreeNode root) {
		HashMap<String, List<String>> res = new HashMap<>();
		Enumeration e = ((DefaultMutableTreeNode) root).preorderEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) e.nextElement();
			Enumeration clist = thisNode.children();
			List<String> childrenName = new ArrayList<>();
			while (clist.hasMoreElements()) {
				childrenName.add(clist.nextElement().toString());
			}
			res.put(thisNode.toString(), childrenName);
		}
		return res;
	}

	private HashMap<String, List<String>> buildPaths(TreeNode root, HashMap<String, String> getnodeParent) {
		HashMap<String, List<String>> res = new HashMap<>();

		// for each node do
		Enumeration e = ((DefaultMutableTreeNode) root).preorderEnumeration();
		e.nextElement(); // skiproot
		e.nextElement(); // skip expName
		while (e.hasMoreElements()) {
			String s = e.nextElement().toString();
			List<String> path = new ArrayList<>();
			String currNode = s;
			while (true) {
				String thisParent = getnodeParent.get(currNode);
				if (thisParent.equals("Root")) {
					break;
				}
				path.add(thisParent);
				currNode = thisParent;

			}
			Collections.reverse(path);
			// remove first entry as outermost attribute is unique
			path.remove(0);
			res.put(s, path);
		}
		return res;
	}

	/**
	 * this function maps each node to its unique parent
	 * 
	 * @param root
	 *            root of java tree
	 * @return
	 */
	private HashMap<String, String> getTreeNodeParents(TreeNode root) {
		HashMap<String, String> res = new HashMap<>();
		// build a hash map with child-->parent entries

		Enumeration e = ((DefaultMutableTreeNode) root).preorderEnumeration();
		e.nextElement(); // skiproot
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) e.nextElement();
			res.put(thisNode.toString(), thisNode.getParent().toString());
		}
		return res;
	}

	private HashMap<Integer, List<String>> getNodesbyLevel(TreeNode root) {
		HashMap<Integer, List<String>> res = new HashMap<>();
		Enumeration e = ((DefaultMutableTreeNode) root).preorderEnumeration();

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) e.nextElement();
			int level = thisNode.getLevel();
			String name = thisNode.toString();
			// JOptionPane.showMessageDialog(null, "tn" + name + " l:" + level);
			if (res.containsKey(level)) {
				List<String> temp = res.get(level);
				temp.add(name);
			} else {
				List<String> temp = new ArrayList<>();
				temp.add(name);
				res.put(level, temp);
			}
		}
		return res;
	}
}
