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
		// JOptionPane.showMessageDialog(null, "Time taken(s):" + elapsedTime/1000);
		/////////////////////////
		/*
		 * JOptionPane.showMessageDialog(null, "adding nodes..."); long startTime1 =
		 * System.currentTimeMillis(); addNodes(root, XMLroot); long stopTime1 =
		 * System.currentTimeMillis(); long elapsedTime1 = stopTime1 - startTime1;
		 * XMLOutputter outter = new XMLOutputter();
		 * outter.setFormat(Format.getPrettyFormat()); org.jdom.Document res = new
		 * org.jdom.Document(); // res.setRootElement(XMLroot); String resDoc = null; //
		 * System.out.println(resDoc); System.out.
		 * println("**********************After validataion..**************************"
		 * ); // validate tree with respect to the data and build a treemap to map col
		 * index // to XML nodes colIndextoNode = new TreeMap<Integer, Element>();
		 * res.setRootElement(XMLroot); resDoc = outter.outputString(res);
		 */
		/*
		 * try (PrintWriter out = new PrintWriter(
		 * "D:\\MOGdata\\mog_testdata\\jing\\JL_yeast_MOG\\shortfilename_before.txt")) {
		 * out.println(resDoc); } catch (FileNotFoundException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		/*
		 * JOptionPane.showMessageDialog(null, "validating nodes..."); long startTime2 =
		 * System.currentTimeMillis(); validateXML(XMLroot); long stopTime2 =
		 * System.currentTimeMillis(); long elapsedTime2 = stopTime2 - startTime2; long
		 * totalel = elapsedTime2 + elapsedTime1; JOptionPane.showMessageDialog(null,
		 * "Time elapsed old method:" + totalel + " addNode:" + elapsedTime1 +
		 * " validate:" + elapsedTime2 + " New m" + elapsedTime);
		 * JOptionPane.showMessageDialog(null, "Finished validating nodes...");
		 */

		XMLOutputter outter = new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		org.jdom.Document res = new org.jdom.Document();
		res.setRootElement(XMLroot);
		String resDoc = outter.outputString(res);
		// System.out.println(resDoc);
		System.out.println("**********************After names..**************************");
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

	/*
	 * This function recursively builds a tree
	 *
	 */

	/**
	 * @author urmi
	 * @param root
	 *            JTree root to display tree
	 * @param XMLroot
	 *            XML root to store data and structure
	 */
	private void addNodes(TreeNode root, Element XMLroot) {
		for (int i = 0; i < root.getChildCount(); i++) {
			TreeNode thisChild = root.getChildAt(i);
			String thisColumn = thisChild.toString();
			List<String> uniqVals;
			if (root.toString().equals("Root")) {
				// get all unique values from Collection object for thisColoumn
				uniqVals = obj.getSortedUniqueValuesByHeaderName(thisColumn, false, false);
				if (!importedHeaders.contains(thisColumn)) {
					importedHeaders.add(thisColumn);
				}
			} else {
				if (!importedHeaders.contains(thisColumn)) {
					importedHeaders.add(thisColumn);
				}
				// System.out.println("*****************************asdddddsl:");
				// get all unique values for thisColumn such that rootcolumn has value equal to
				// XMLroot name
				// remove special charecters before regex
				String reg = processString(XMLroot.getAttributeValue("name"));
				Filter f = Filters.regex(root.toString(), "^" + reg + "$");
				uniqVals = obj.getDatabyAttributes(f, thisColumn, true);
				// uniqVals.size() should be >0
				if (uniqVals.size() <= 0) {
					// System.out.println("NOmatches found for:" + reg);
					JOptionPane.showMessageDialog(null, "Metadata search failed while building tree", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			// System.out.println("unq vals for col:"+thisColumn);
			for (String s : uniqVals) {
				/*
				 * Do not replace empty values, it changes validation if(s.equals("") ||
				 * s.length()==0) { s="ABSENT"; }
				 */

				Element newNode = new Element(thisColumn);
				// Element newNode = new Element("Experiment");
				if (thisChild.getChildCount() > 0) {
					newNode.setAttribute("name", s);
					addNodes(thisChild, newNode);
				} else {
					// if (s.equals("")) {
					// newNode.addContent("NA");
					// } else {
					// newNode.addContent(s);
					// }
					// Do not replace empty values, it changes validation
					newNode.addContent(s);
				}
				XMLroot.addContent(newNode);
			}
		}

	}

	/**
	 * @author urmi
	 * @param Root
	 *            Element of an XML tree This function checks for the nodes falsely
	 *            added to tree and removes them. Correction is only possible when
	 *            we have a fully connected tree. During tree building its not
	 *            possible to check as the edges are added later after nodes are
	 *            added
	 * 
	 *            Check possible values children of a node that can have erroneous
	 *            values and delete them
	 * 
	 *
	 */

	private void validateXML(Element XMLroot) {
		// skip root
		if (XMLroot.isRootElement()) {
			for (int i = 0; i < XMLroot.getChildren().size(); i++) {
				// System.out.println(XMLroot.getChildren().get(i).toString());
				validateXML((Element) XMLroot.getChildren().get(i));
			}
		} else {
			// get data from table
			List<Filter> flist = new ArrayList<>();
			buildPathtoRootFilterlist(XMLroot, flist);
			// finally filter value of current node
			String regStr;
			if (!(XMLroot.getAttributeValue("name") == null)) {
				regStr = processString(XMLroot.getAttributeValue("name"));
			} else {
				regStr = processString(XMLroot.getContent().get(0).toString());
			}
			flist.add(Filters.regex(XMLroot.getName(), "^" + regStr + "$"));
			Filter[] farray = new Filter[flist.size()];
			for (int k = 0; k < flist.size(); k++) {
				farray[k] = flist.get(k);
			}
			Filter finalFilter = Filters.and(farray);
			List<Document> uniqVals = obj.getDatabyAttributes(finalFilter, true);
			// get all children of this node
			List<Element> cList = XMLroot.getChildren();
			// System.out.println("Children of " + XMLroot.getName() + ":" +
			// XMLroot.getAttributeValue("name"));
			for (Element c : cList) {
				boolean noMatch = true;
				// if children is internal node it will have attribute name
				if (c.getChildren().size() > 0) {
					// System.out.println(c.getName() + ":" + c.getAttributeValue("name"));
					// search if children should exist
					for (int i = 0; i < uniqVals.size(); i++) {
						// if present in data
						if (c.getAttributeValue("name").equals(uniqVals.get(i).get(c.getName()))) {
							noMatch = false;
							break;
						}

					}
					// if doesn't exist in data delete that node
					if (noMatch) {
						// delete
						// System.out.println("Children not found in data " + c.getName() + ":" +
						// c.getAttributeValue("name"));
						c.setName("ACCvxtas092S");

						// XMLroot.removeChild("ACCvxtas092S");
						// XMLroot.removeChild(c.getName());
					}

				}
				// if children is leaf node it will have content name
				else {
					// System.out.println(c.getName() + ":");
					// System.out.println(c.getName() + "**:" + c.getContent(0).getValue());
					for (int i = 0; i < uniqVals.size(); i++) {
						// if present in data
						if (c.getContent(0).getValue().equals(uniqVals.get(i).get(c.getName()))) {
							noMatch = false;
							break;
						}

					}
					if (noMatch) {
						// delete
						c.setName("ACCvxtas092S");

					}
				}

			}
			XMLroot.removeChildren("ACCvxtas092S");

			// update after deletions,if any
			cList = XMLroot.getChildren();
			for (Element c : cList) {
				// check if Element c is datacolumn element
				if (c.getName().equals(dataColName)) {
					String cValue;
					// leaf nodes have no attribute name, only content
					/*
					 * How to handle repeating names are not allowed in data column ???
					 */
					if (c.getChildren().size() > 0) {
						cValue = c.getAttributeValue("name").toString();
					} else {
						cValue = c.getContent(0).getValue().toString();
					}
					// add data column to knownCols
					int thisIndex = colHeaders.indexOf(cValue);
					colIndextoNode.put(Integer.valueOf(thisIndex), c);
					// JOptionPane.showMessageDialog(null, cValue+":Mapto:"+thisIndex);

					// add to defaultrepsMap
					String thisRepname = c.getParentElement().getAttributeValue("name");
					Set<String> addedReps = defaultrepsMap.keySet();
					if (addedReps.contains(thisRepname)) {
						// append this col
						List<Integer> temp = defaultrepsMap.get(thisRepname);
						temp.add(thisIndex);
						defaultrepsMap.put(thisRepname, temp);
					} else {
						List<Integer> temp = new ArrayList<>();
						temp.add(thisIndex);
						defaultrepsMap.put(thisRepname, temp);
					}

				}

				validateXML(c);
			}
		}
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
	 * This function finds values of all the parents for a given node and creates a
	 * filter. This allows exact matching of values in different columns.
	 */
	private void buildPathtoRootFilterlist(Element node, List<Filter> flist) {
		if (!(node.getParentElement() == null)) {
			Element parent = node.getParentElement();
			if (!(parent.isRootElement())) {
				Filter f = Filters.regex(parent.getName(), "^" + processString(parent.getAttributeValue("name")) + "$");
				// add filter to list
				flist.add(f);
				buildPathtoRootFilterlist(parent, flist);
			}
		}

		return;
	}

	/**
	 * @author urmi This function take a string and converts into regex compatible
	 *         format by escaping special charecters
	 * 
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
			JOptionPane.showMessageDialog(null, "s:" + s);
		}

		return res;
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
		//JOptionPane.showMessageDialog(null, "Start..");
		// get the data
		List<Document> tabData = obj.getAllData();
		if(tabData.size()==0) {
			return null;
		}
		HashMap<Integer, List<String>> nodeLevels = getNodesbyLevel(root);
		HashMap<String, String> getnodeParent = getTreeNodeParents(root);
		// store paths to nodes no need to compute inside loop
		HashMap<String, List<String>> pathsToNode = buildPaths(root, getnodeParent);
		// store children of all nodes
		HashMap<String, List<String>> nodeChildren = getNodeChildren(root);

		//List<Element> dataColNodes = new ArrayList<>();

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
		//to build default reps col
		defaultrepscol = nodeLevels.get(1).get(0);
		//JOptionPane.showMessageDialog(null, "P1..Tabsize:" +  tabData.size());
		
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
			}
			
			//JOptionPane.showMessageDialog(null, "P2..");
			
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
							//dataColNodes.add(newChild);
							//add to colIndextoNode
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
						//dataColNodes.add(newChild);
						//add to colIndextoNode
						int thisIndex = colHeaders.indexOf(child);
						colIndextoNode.put(Integer.valueOf(thisIndex), newChild);
					
						
					}
					List<String> plist = new ArrayList<>();
					plist.add(expColname + ":::" + expName);
					childParentXML.put(s + ":::" + child, plist);
				}

				
			}
			
			//JOptionPane.showMessageDialog(null, "P3..");
			
			// add nodes in other levels
			// for levels in the tree structure
			for (int j = 3; j <= maxLevel; j++) {
				nodesCurrlevel = nodeLevels.get(j);
				// build thisElemet into tree
				// for all Jtree nodes in current level
				for (String s : nodesCurrlevel) {
					JOptionPane.showMessageDialog(null, "Curr level:"+s);
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
							Element newChild = new Element(s);
							if (nodeChildren.get(s).size() == 0) {
								newChild.addContent(child);
							} else {
								newChild.setAttribute("name", child);
							}
							toAdd.addContent(newChild);
							if (s.equals(dataColName)) {
								//dataColNodes.add(newChild);
								//add to colIndextoNode
								int thisIndex = colHeaders.indexOf(child);
								colIndextoNode.put(Integer.valueOf(thisIndex), newChild);
							
							}
							plist.add(toAdd.getName() + ":::" + toAdd.getAttributeValue("name"));
							childParentXML.put(s + ":::" + child, plist);
						} else {
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
								//JOptionPane.showMessageDialog(null, "UP" + uniqPaths.toString());
								//JOptionPane.showMessageDialog(null, "Found here");
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
									//dataColNodes.add(newChild);
									int thisIndex = colHeaders.indexOf(child);
									colIndextoNode.put(Integer.valueOf(thisIndex), newChild);
								
								}
								plist.add(toAdd.getName() + ":::" + toAdd.getAttributeValue("name"));
								childParentXML.put(s + ":::" + child, plist);
							}
						}

					} else {
						Element newChild = new Element(s);
						//String thisName="";
						if (nodeChildren.get(s).size() == 0) {
							newChild.addContent(child);
						} else {
							newChild.setAttribute("name", child);
						}
						toAdd.addContent(newChild);
						
						if (s.equals(dataColName)) {
							//dataColNodes.add(newChild);
							int thisIndex = colHeaders.indexOf(child);
							colIndextoNode.put(Integer.valueOf(thisIndex), newChild);
												
						}
						
						List<String> plist = new ArrayList<>();
						plist.add(toAdd.getName() + ":::" + toAdd.getAttributeValue("name"));
						childParentXML.put(s + ":::" + child, plist);
					}

				}
			}
			
			//JOptionPane.showMessageDialog(null, "P4..");
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
