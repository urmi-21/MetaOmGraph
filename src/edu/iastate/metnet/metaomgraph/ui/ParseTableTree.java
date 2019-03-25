package edu.iastate.metnet.metaomgraph.ui;
/*
 * This class deals with parsing operations e.g. convert MetadataCollection object to tree and vice-versa.
 * Can include other functions too.
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.print.attribute.standard.JobPriority;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.filters.Filters;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

/**
 * Class handles conversion of tabular to tree data structures
 * 
 * @author urmi
 *
 */
public class ParseTableTree {
	private MetadataCollection obj = null;
	private JTree tree = null;
	// for progress bar
	private JFrame frame;
	private List<String> importedHeaders;
	JProgressBar pBar;
	// set the frame in the middle of screen
	Dimension dim;
	private String dataColName;
	private List<String> colHeaders;
	// maps col index in data file to
	private TreeMap<Integer, Element> colIndextoNode;
	// default reps are under common parent, TreeMap to preserve order
	private TreeMap<String, List<Integer>> defaultrepsMap;

	private String defaultrepscol;

	public ParseTableTree(MetadataCollection csvObj, JTree tree, String name, String[] colheaders) {
		this.obj = csvObj;
		this.tree = tree;
		dataColName = name;
		colHeaders = Arrays.asList(colheaders);
	}

	public ParseTableTree(MetadataCollection csvObj, JTree tree, String name) {
		this.obj = csvObj;
		this.tree = tree;
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
	public org.jdom.Document tableToTree() {
		defaultrepsMap = new TreeMap<>();
		importedHeaders = new ArrayList<>();
		// Get root node The tree root is named "Root"
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		Element XMLroot = new Element("Experiments");
		colIndextoNode = new TreeMap<Integer, Element>();

		/////////////////
		long startTime = System.currentTimeMillis();
		buildTree(root, XMLroot);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;

		XMLOutputter outter = new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		org.jdom.Document res = new org.jdom.Document();
		res.setRootElement(XMLroot);
		// String resDoc = outter.outputString(res);
		// System.out.println(resDoc);
		// System.out.println("**********************After
		// names..**************************");
		// changeElementNames(XMLroot);
		// res.setRootElement(XMLroot);
		// resDoc = outter.outputString(res);
		// System.out.println(resDoc);
		/*
		 * try (PrintWriter out = new
		 * PrintWriter("D:\\MOGdata\\mog_testdata\\jing\\JL_yeast_MOG\\newfilename.txt")
		 * ) { out.println(resDoc); } catch (FileNotFoundException e) {
		 * e.printStackTrace(); }
		 */
		return res;

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

	public TreeMap<Integer, Element> getTreeMap() {
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

	public TreeMap<String, List<Integer>> getDefaultRepMap() {
		return this.defaultrepsMap;
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
	private Element buildTree(TreeNode root, Element XMLroot) {
		// JOptionPane.showMessageDialog(null, "Start..");
		// get the data
		List<Document> tabData = obj.getAllData();
		if (tabData.size() == 0) {
			return null;
		}
		HashMap<Integer, List<String>> nodeLevels = getNodesbyLevel(root);
		HashMap<String, String> getnodeParent = getTreeNodeParents(root);
		// store paths to nodes no need to compute inside loop
		HashMap<String, List<String>> pathsToNode = buildPaths(root, getnodeParent);
		// store children of all nodes
		HashMap<String, List<String>> nodeChildren = getNodeChildren(root);

		// List<Element> dataColNodes = new ArrayList<>();

		// a list to quickly search if anew node be added or not
		List<String[]> uniqPaths = new ArrayList<>();

		// JOptionPane.showMessageDialog(null, "parents:" + getnodeParent.toString());
		// JOptionPane.showMessageDialog(null, "paths:" + pathsToNode.toString());
		// JOptionPane.showMessageDialog(null, "children:" + nodeChildren.toString());
		HashMap<String, Element> addedXML = new HashMap<>();
		HashMap<String, List<String>> childParentXML = new HashMap<>();

		List<Integer> levels = new ArrayList<>();
		levels.addAll(nodeLevels.keySet());
		Collections.sort(levels);
		int maxLevel = levels.get(levels.size() - 1);
		// expName is the outermost attribute
		// there is only one outer most node
		String expColname = nodeLevels.get(1).get(0);
		// to build default reps col
		defaultrepscol = nodeLevels.get(1).get(0);
		// JOptionPane.showMessageDialog(null, "P1..Tabsize:" + tabData.size());

		// for all rows in data
		for (int i = 0; i < tabData.size(); i++) {
			// add outermost node i.e. level 1
			String expName = tabData.get(i).get(expColname).toString();
			Element thisElement = null;

			if (addedXML.containsKey(expName)) {
				thisElement = addedXML.get(expName);
			} else {
				thisElement = new Element(expColname);
				thisElement.setAttribute("name", expName);
				addedXML.put(expName, thisElement);
				thisElement = addedXML.get(expName);
				// if this element is data column add to colIndextoNode
				if (expColname.equals(dataColName)) {
					// dataColNodes.add(newChild);
					// add to colIndextoNode
					int thisIndex = colHeaders.indexOf(expName);
					colIndextoNode.put(Integer.valueOf(thisIndex), thisElement);
				}
			}

			// JOptionPane.showMessageDialog(null, "P2..");

			// add level 2 elements directly to expName
			List<String> nodesCurrlevel = nodeLevels.get(2);
			for (String s : nodesCurrlevel) {
				// JOptionPane.showMessageDialog(null, "Adding:" + s);
				String child = tabData.get(i).get(s).toString();

				if (childParentXML.containsKey(s + ":::" + child)) {
					List<String> plist = childParentXML.get(s + ":::" + child);
					// if node is already present donot add
					if (!plist.contains(expColname + ":::" + expName)) {
						Element newChild = new Element(s);
						if (nodeChildren.get(s).size() == 0) {
							newChild.addContent(child);
						} else {
							newChild.setAttribute("name", child);
						}
						thisElement.addContent(newChild);
						if (s.equals(dataColName)) {
							// dataColNodes.add(newChild);
							// add to colIndextoNode
							int thisIndex = colHeaders.indexOf(child);
							colIndextoNode.put(Integer.valueOf(thisIndex), newChild);

						}
						plist.add(expColname + ":::" + expName);
						childParentXML.put(s + ":::" + child, plist);
					}

				} else {
					Element newChild = new Element(s);
					if (nodeChildren.get(s).size() == 0) {
						newChild.addContent(child);
					} else {
						newChild.setAttribute("name", child);
					}
					thisElement.addContent(newChild);
					if (s.equals(dataColName)) {
						// dataColNodes.add(newChild);
						// add to colIndextoNode
						int thisIndex = colHeaders.indexOf(child);
						colIndextoNode.put(Integer.valueOf(thisIndex), newChild);

					}
					List<String> plist = new ArrayList<>();
					plist.add(expColname + ":::" + expName);
					childParentXML.put(s + ":::" + child, plist);
				}

			}

			// JOptionPane.showMessageDialog(null, "P3..");

			// add nodes in other levels
			// for levels in the tree structure
			for (int j = 3; j <= maxLevel; j++) {
				nodesCurrlevel = nodeLevels.get(j);
				// build thisElemet into tree
				// for all Jtree nodes in current level
				for (String s : nodesCurrlevel) {
					// JOptionPane.showMessageDialog(null, "Curr level:"+s);
					// value to add
					String child = tabData.get(i).get(s).toString();

					/*
					 * if(child.length()==0) { child="NO_VALUE"; }
					 */
					// Search the XML tree and ADD new node
					// find path to add
					List<String> path = pathsToNode.get(s);
					Element toAdd = null; // toAdd is the node to which we add the new node i.e. parent of the new node
					Element temp = thisElement;
					// JOptionPane.showMessageDialog(null, "This element name:"+temp.getName());
					for (String p : path) {
						// get the child matching colname and value
						List<Element> clist = temp.getChildren(p);
						// JOptionPane.showMessageDialog(null, "curr exp:" + expName);

						String attVal = tabData.get(i).get(p).toString();
						// JOptionPane.showMessageDialog(null, "searching for:" + attVal + "in col:" +
						// p);
						for (Element c : clist) {
							// JOptionPane.showMessageDialog(null, "aval:" + c.getAttributeValue("name"));
							if (attVal.equals(c.getAttributeValue("name"))) {
								// JOptionPane.showMessageDialog(null, "Found add:" +
								// c.getAttributeValue("name"));
								temp = c;
								break;
							}
						}

					}
					// toAdd is the node to which we add the new node i.e. parent of the new node
					toAdd = temp;

					// add to toAdd node

					// TODO: check all the way to root of tree if item is added or not
					if (childParentXML.containsKey(s + ":::" + child)) {
						List<String> plist = childParentXML.get(s + ":::" + child);
						// if node is already present donot add
						if (!plist.contains(toAdd.getName() + ":::" + toAdd.getAttributeValue("name"))) {
							// JOptionPane.showMessageDialog(null, "adding:"+s + ":::" + child);
							Element newChild = new Element(s);
							if (nodeChildren.get(s).size() == 0) {
								newChild.addContent(child);
							} else {
								newChild.setAttribute("name", child);
							}
							toAdd.addContent(newChild);
							if (s.equals(dataColName)) {
								// dataColNodes.add(newChild);
								// add to colIndextoNode
								int thisIndex = colHeaders.indexOf(child);
								colIndextoNode.put(Integer.valueOf(thisIndex), newChild);

							}
							plist.add(toAdd.getName() + ":::" + toAdd.getAttributeValue("name"));
							childParentXML.put(s + ":::" + child, plist);
						} else {
							// JOptionPane.showMessageDialog(null, "ELSEadding:"+s + ":::" + child);
							// check if parent also match if not then add the node
							List<String> fullPath = new ArrayList<>();
							fullPath.add(0, expColname);
							fullPath.addAll(pathsToNode.get(s));
							// JOptionPane.showMessageDialog(null, "paths:" + pathsToNode.toString());
							String[] pathVals = new String[fullPath.size()];
							for (int m = 0; m < fullPath.size(); m++) {
								pathVals[m] = tabData.get(i).get(fullPath.get(m)).toString();
							}
							// JOptionPane.showMessageDialog(null, "Currpath:" + fullPath.toString());
							// JOptionPane.showMessageDialog(null, "Currpath vals:" +
							// Arrays.toString(pathVals));
							// check if the pathVals are unique if not ts been added to tree before else add
							// to tree

							boolean uniquePath = true; // is current path unique path ?

							if (uniqPaths.contains(pathVals)) {
								uniquePath = false;
								// JOptionPane.showMessageDialog(null, "UP" + uniqPaths.toString());
								// JOptionPane.showMessageDialog(null, "Found here");
							} else {
								String[] tempVals = new String[fullPath.size()];
								for (int m = 0; m < i; m++) {

									if (tabData.get(m).get(s).toString().equals(child)) {

										for (int n = 0; n < fullPath.size(); n++) {
											tempVals[n] = tabData.get(m).get(fullPath.get(n)).toString();
										}
										// JOptionPane.showMessageDialog(null, "tempval: "+Arrays.toString(tempVals));
										if (Arrays.equals(pathVals, tempVals)) {
											// JOptionPane.showMessageDialog(null, "uniq path:
											// "+Arrays.toString(tempVals));
											uniquePath = false;
											break;
										}
									}

								}
							}

							if (uniquePath == true) {
								// reset uniqpaths
								if (uniqPaths.size() > 500) {
									uniqPaths = new ArrayList<>();
								}
								uniqPaths.add(pathVals);
								// JOptionPane.showMessageDialog(null, "Add: "+s + ":::" + child);
								Element newChild = new Element(s);
								if (nodeChildren.get(s).size() == 0) {
									newChild.addContent(child);
								} else {
									newChild.setAttribute("name", child);
								}
								toAdd.addContent(newChild);
								if (s.equals(dataColName)) {
									// dataColNodes.add(newChild);
									int thisIndex = colHeaders.indexOf(child);
									colIndextoNode.put(Integer.valueOf(thisIndex), newChild);

								}
								plist.add(toAdd.getName() + ":::" + toAdd.getAttributeValue("name"));
								childParentXML.put(s + ":::" + child, plist);
							}
						}

					} else {
						// JOptionPane.showMessageDialog(null, "ELSEadding:"+s + ":::" + child);
						Element newChild = null;

						newChild = new Element(s);

						// String thisName="";
						if (nodeChildren.get(s).size() == 0) {
							newChild.addContent(child);
						} else {
							newChild.setAttribute("name", child);
						}
						toAdd.addContent(newChild);

						if (s.equals(dataColName)) {
							// dataColNodes.add(newChild);
							int thisIndex = colHeaders.indexOf(child);
							colIndextoNode.put(Integer.valueOf(thisIndex), newChild);

						}

						List<String> plist = new ArrayList<>();
						plist.add(toAdd.getName() + ":::" + toAdd.getAttributeValue("name"));
						childParentXML.put(s + ":::" + child, plist);
					}

				}
			}

			// JOptionPane.showMessageDialog(null, "P4..");
		}

		// add all nodes in Jtree to imported headers
		importedHeaders.addAll(getnodeParent.keySet());

		// JOptionPane.showMessageDialog(null, "data cols to ind" +
		// colIndextoNode.toString());

		for (String key : addedXML.keySet()) {
			XMLroot.addContent(addedXML.get(key));
		}

		return null;
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
