package edu.iastate.metnet.metaomgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.filters.Filters;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.iastate.metnet.metaomgraph.MetaOmProject.RepAveragedData;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

/*
 * This class is the new class for handling metadata and operations on metadata.
 * This will have support for MetadataCollection objects and XML documents objects.
 * All data search, sort, update delete operations will be on MetadataCollection data (tabular)
 * XML object will deal with visualization hierarchy of data.
 * @author: Urmi
 * @date: 3/5/2018
 */
public class MetadataHybrid {
	private MetadataCollection mogCollection;
	private Element XMLroot;
	private JTree treeStructure; // tree structure created by user
	private List<Document> metadata;
	private String dataColumn;
	private String[] metadataHeaders; // only those columns imported in tree structure subset of metadata headers from
										// MOGcollection object
	// objects corresponding to filtered data
	private String[] currentmetadataHeaders;
	// private MetadataCollection currentmogCollection;

	// excluded rows in metadata
	private Set<String> excludedMDRows;
	private Set<String> missingMDRows;
	// removed columns from metadata file, not in tree structure
	private Set<String> removedColsfromMD;

	/**
	 * @author urmi defaultrepsMap maps a "Rep-name" (parent name of data col by
	 *         default) to a list of columns which are in that rep.
	 */
	private TreeMap<String, List<Integer>> defaultrepsMap; // default reps
	private String defaultrepsColumn;

	/*
	 * to make backward compatible with some fumctions knownCols: maps int to
	 * xmlelement, each for a known data point. Each data point is mapped to int
	 * starting from zero e.g. if input data file has
	 * name,target,R1,DRR016111,DRR01,DRR2,DRR3 x1,atttt,3,5,6,2,5 x2,att2,4,1,7,5,5
	 * then 0:R1,1:DRR016111,2:DRR01,3:DRR2,4:DRR3
	 *
	 * fields: maps colnames to int
	 * 
	 */
	public TreeMap<Integer, Element> knownCols;
	// public LinkedHashMap<String, Integer> fields;

	// HashMap<String, Class> mdColType;

	// create blank metadata
	public MetadataHybrid() {

	}

	public MetadataHybrid(MetadataCollection mogCollection, Element XMLroot, TreeMap<Integer, Element> tm,
			String colName, String[] mdheaders, JTree treeStructure, TreeMap<String, List<Integer>> defaultrepsMap,
			String defaultrepsCol, List<String> missingDC, List<String> extraDC, List<String> removedColsfromMD) {
		this.mogCollection = mogCollection;
		this.XMLroot = XMLroot;
		this.metadata = this.mogCollection.returnallData();
		this.knownCols = tm;
		this.dataColumn = colName;
		this.metadataHeaders = mdheaders;
		// treestructure is the tree created by the user that defines the structure
		this.treeStructure = treeStructure;
		this.defaultrepsMap = defaultrepsMap;
		this.defaultrepsColumn = defaultrepsCol;
		// build default reps map
		this.defaultrepsMap = buildRepsMap(defaultrepsCol);

		if (extraDC != null) {
			this.excludedMDRows = new HashSet<>(extraDC);
		} else {
			this.excludedMDRows = new HashSet<>();
		}

		if (missingDC != null) {
			this.missingMDRows = new HashSet<>(missingDC);
		} else {
			this.missingMDRows = new HashSet<>();
		}

		if (removedColsfromMD != null) {
			this.removedColsfromMD = new HashSet<>(removedColsfromMD);
		} else {
			this.removedColsfromMD = new HashSet<>();
		}
		
		
		// add missing DC
		// if (missingDC.size() > 0) { this.mogCollection.addNullData(missingDC); }

	}

	public JTree getTreeStucture() {
		return this.treeStructure;
	}

	public void setTreeStucture(JTree tree) {
		this.treeStructure = tree;
	}

	public String getDefaultRepCol() {
		return this.defaultrepsColumn;
	}

	public void setDefaultRepCol(String newCol) {
		this.defaultrepsColumn = newCol;
		// change default reps map
		this.defaultrepsMap = buildRepsMap(defaultrepsColumn);
	}

	/**
	 * convert JTree to XML structure to save to file
	 * 
	 * @param tree
	 * @return
	 */
	public Element jtreetoXML(JTree tree) {

		DefaultTreeModel tmodel = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) tmodel.getRoot();
		Element root = new Element(treeRoot.toString());
		root.setAttribute("name", "Root");
		buildXMLfromJTree(treeRoot, root);
		return root;
	}

	public void buildXMLfromJTree(DefaultMutableTreeNode node, Element element) {
		// element = new Element(node.toString());
		int numchild = node.getChildCount();
		for (int i = 0; i < numchild; i++) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) node.getChildAt(i);
			Element newNode = new Element(thisNode.toString());
			newNode.setAttribute("name", newNode.getName());
			buildXMLfromJTree(thisNode, newNode);
			element.addContent(newNode);
		}

	}

	public void setXMLroot(Element root) {
		this.XMLroot = root;
	}

	public void setmogCollection(MetadataCollection obj) {
		this.mogCollection = obj;
	}

	public TreeMap<String, List<Integer>> getDefaultRepsMap() {
		return this.defaultrepsMap;
	}

	public String[] getMetadataHeaders() {
		return this.metadataHeaders;
	}

	public TreeMap<Integer, Element> createTreeMap() {
		TreeMap<Integer, Element> tm = new TreeMap<Integer, Element>();
		return tm;

	}

	public String getDataColName() {
		return this.dataColumn;
	}

	public MetadataCollection getMetadataCollection() {
		return this.mogCollection;
	}

	public List<Document> getMetadataAsListDoc() {
		return metadata;
	}

	public Element getXMLRoot() {
		return this.XMLroot;
	}

	/**
	 * This function returns metadata assocoated with a column
	 * 
	 * @author urmi
	 * @param int
	 *            columnnum: this is the column index as in the datafile if data
	 *            file has columns R1, R2, R3 then 1 will be passed to get metadata
	 *            for R2 and so on
	 * @return Metadata for a given datacolumn in String[][] attribute,value pairs
	 */
	public String[][] getMetadataForCol(int colIndex) {
		if (colIndex == -1) {
			return new String[0][0];
		}
		String[][] res = null;
		Element thisNode = knownCols.get(colIndex);
		res = getNodeMetadata(thisNode);
		return res;
	}

	/**
	 * @author urmi This function takes a node and returns associated metadata This
	 *         function searches all the parents, their attributes, children and
	 *         their attributes.
	 * @param node
	 * @return
	 */

	public String[][] getNodeMetadata(Element node) {
		if (node == null) {
			return new String[0][0];
		}
		String[][] md = null;
		List<String> attributeList = new ArrayList<>();
		List<String> valueList = new ArrayList<>();
		// Add all parent values and parent attributes i.e. leaf values
		Element thisParent = node.getParentElement();
		while (!thisParent.isRootElement()) {
			attributeList.add(thisParent.getName());
			valueList.add(thisParent.getAttributeValue("name").toString());
			// add leaf attributes
			int numChild = thisParent.getChildren().size();
			for (int i = 0; i < numChild; i++) {
				Element thisC = (Element) thisParent.getChildren().get(i);
				// add if child is attribute
				if (thisC.getChildren().size() == 0) {
					attributeList.add(thisC.getName());
					valueList.add(thisC.getContent(0).getValue().toString());
				}
			}
			// move one step up
			thisParent = thisParent.getParentElement();
		}
		int numChild = node.getChildren().size();
		if (numChild < 1) {
			attributeList.add(node.getName());
			valueList.add(node.getContent(0).getValue().toString());

		} else {
			attributeList.add(node.getName());
			valueList.add(node.getAttributeValue("name").toString());
			for (int i = 0; i < numChild; i++) {
				Element thisC = (Element) node.getChildren().get(i);
				getChildMetadata(attributeList, valueList, thisC);
			}

		}

		md = new String[attributeList.size()][2];
		for (int i = 0; i < attributeList.size(); i++) {
			md[i][0] = attributeList.get(i);
			md[i][1] = valueList.get(i);
		}

		return md;
	}

	/**
	 * @author urmi This function takes a node and metadata of all the children
	 * @param attributeList
	 *            valueList
	 * @return
	 */
	public void getChildMetadata(List<String> attributeList, List<String> valueList, Element node) {
		if (node == null) {
			return;
		}
		int numChild = node.getChildren().size();
		if (numChild < 1) {
			attributeList.add(node.getName());
			valueList.add(node.getContent(0).getValue().toString());
			return;

		} else {
			attributeList.add(node.getName());
			valueList.add(node.getAttributeValue("name").toString());
			for (int i = 0; i < numChild; i++) {
				Element thisC = (Element) node.getChildren().get(i);
				getChildMetadata(attributeList, valueList, thisC);
			}

		}

	}

	public Element getNodeForCol(int col) {
		return knownCols.get(Integer.valueOf(col));
	}

	public Element getParentNodeForCol(int col) {
		return knownCols.get(Integer.valueOf(col)).getParentElement();
	}

	/**
	 * @author urmi From old metadata class Edited by urmi to work with new class
	 * @param field
	 * 
	 * @return
	 */
	public Map<String, Collection<Integer>> cluster(String field) {
		Map<String, Collection<Integer>> result = new TreeMap();
		// knowncols are data columns or Runs
		Set<Integer> cols = knownCols.keySet();

		// if its datacolumn each column is in single bin
		if (field.equals(dataColumn)) {
			for (Iterator localIterator = cols.iterator(); localIterator.hasNext();) {
				int col = ((Integer) localIterator.next()).intValue();
				// JOptionPane.showMessageDialog(null, "curr col:" + col);
				// skip cols not in data file
				if (col < 0) {
					continue;
				}
				Element thisNode = knownCols.get(col);
				String val = null;
				int numChild = thisNode.getChildren().size();
				if (numChild < 1) {
					val = thisNode.getContent(0).getValue().toString();
				} else {
					val = thisNode.getAttributeValue("name").toString();
				}

				if (val == null) {
					val = "";
				}
				Collection<Integer> thisBin = result.get(val);
				if (thisBin == null) {
					thisBin = new ArrayList();
				}
				thisBin.add(Integer.valueOf(col));
				result.put(val, thisBin);
			}
			return result;
		}

		for (Iterator localIterator = cols.iterator(); localIterator.hasNext();) {
			int col = ((Integer) localIterator.next()).intValue();
			// JOptionPane.showMessageDialog(null, "curr col:" + col);
			// skip cols not in data file
			if (col < 0) {
				continue;
			}
			Element thisNode = knownCols.get(col);
			// JOptionPane.showMessageDialog(null, "this node:" +
			// knownCols.get(col).getAttributeValue("name"));
			String[][] thisMetadata = getNodeMetadata(thisNode);

			String val = null;

			for (int i = 0; i < thisMetadata.length; i++) {
				// JOptionPane.showMessageDialog(null, "this md:" + thisMetadata[i][0] + ":" +
				// thisMetadata[i][1]);
				if (thisMetadata[i][0].equals(field)) {
					val = thisMetadata[i][1];
					break;
				}
			}
			if (val == null) {
				val = "";
			}
			Collection<Integer> thisBin = result.get(val);
			if (thisBin == null) {
				thisBin = new ArrayList();
			}
			thisBin.add(Integer.valueOf(col));
			result.put(val, thisBin);
		}
		return result;
	}
	
	
	

	/**
	 * @author urmi This function takes queries and returns array of datacol indexes
	 *         matching the queries From old metadata file changed to work with new
	 *         metadata class
	 * @param queries
	 *            array of queries
	 * @param matchAll
	 * @return
	 */
	public Integer[] search(MetadataQuery[] queries, boolean matchAll) {
		Set<Integer> cols = knownCols.keySet();
		ArrayList<Integer> result = new ArrayList();
		Integer[] toReturn;
		List<String> colVals = new ArrayList<>();
		String[] allfields = new String[queries.length];
		String[] toSearch = new String[queries.length];
		boolean[] isExact = new boolean[queries.length];
		boolean[] matchCase = new boolean[queries.length];
		// search using Metadatacollection object and return indices of matching data
		// columns from knownCols
		// do for all queries

		for (int i = 0; i < queries.length; i++) {
			allfields[i] = queries[i].getField();
			toSearch[i] = queries[i].getTerm();
			isExact[i] = queries[i].isExact();
			matchCase[i] = queries[i].isCaseSensitive();
			// JOptionPane.showMessageDialog(null, "search f:" + fields[i]);

		}
		colVals.addAll(searchByValue(allfields, toSearch, this.dataColumn, isExact, matchAll, matchCase));
		// find all keys with value in colVals
		for (Entry<Integer, Element> entry : knownCols.entrySet()) {
			Integer key = entry.getKey();
			Element value = entry.getValue();
			String thisName = null;
			if (value.getChildren().size() > 0) {
				thisName = value.getAttributeValue("name").toString();

			} else {
				thisName = value.getContent(0).getValue().toString();
			}

			if (colVals.contains(thisName) && key >= 0) {
				if (!result.contains(key)) {
					result.add(key);
				}

			}

		}

		toReturn = result.toArray(new Integer[0]);

		// result.add(0);
		// result.add(1);
		return toReturn;
	}

	/**
	 * @author urmi This function takes queries and returns array of datacol indexes
	 *         matching the queries From old metadata file changed to work with new
	 *         metadata class
	 * @param queries
	 *            array of queries
	 * @param matchAll
	 * @return
	 */
	// public List<String> getMatchingRows(MetadataQuery[] queries, boolean
	// matchAll) {
	// return getMatchingRows(queries, matchAll, false);
	// }

	public List<String> getMatchingRows(MetadataQuery[] queries, boolean matchAll) {
		Set<Integer> cols = knownCols.keySet();
		ArrayList<Integer> result = new ArrayList();
		Integer[] toReturn;
		List<String> colVals = new ArrayList<>();
		String[] allfields = new String[queries.length];
		String[] toSearch = new String[queries.length];
		boolean[] isExact = new boolean[queries.length];
		boolean[] matchCase = new boolean[queries.length];
		// search using Metadatacollection object and return indices of matching data
		// columns from knownCols
		// do for all queries

		for (int i = 0; i < queries.length; i++) {
			allfields[i] = queries[i].getField();
			toSearch[i] = queries[i].getTerm();
			isExact[i] = queries[i].isExact();
			matchCase[i] = queries[i].isCaseSensitive();
			// JOptionPane.showMessageDialog(null, "search f:" + fields[i]);

		}
		colVals.addAll(searchByValue(allfields, toSearch, this.dataColumn, isExact, matchAll, matchCase));

		return colVals;
	}

	/**
	 * @author urmi
	 * @param field
	 *            where to search
	 * @param toSearch
	 *            what to search for
	 * @param toReturn
	 *            what value to return
	 * @param exact
	 *            exact match or near match?
	 * @return values under toReturn column where field column has value toSearch
	 */
	public List<String> searchByValue(String[] field, String[] toSearch, String toReturn, boolean[] exact,
			boolean matchAll, boolean[] matchCase) {
		List<String> res = new ArrayList<>();

		List<List<String>> specialCaseRes = new ArrayList<List<String>>();
		// Filter[] farray = new Filter[toSearch.length];
		Filter comboFilter;
		List<Filter> filterList = new ArrayList<>();

		for (int i = 0; i < toSearch.length; i++) {
			String caseFlag = "";
			if (!matchCase[i]) {
				caseFlag = "(?i)";
			}

			if (field[i] == "All Fields") {

				// List<String> res2 = new ArrayList<>();

				specialCaseRes.add(searchByValue(toSearch[i], toReturn, exact[i], true, matchCase[i]));

			} else if (field[i] == "Any Field") {

				specialCaseRes.add(searchByValue(toSearch[i], toReturn, exact[i], false, matchCase[i]));

			} else {
				if (exact[i]) {

					// farray[i] = Filters.regex(field[i], "^" + toSearch[i] + "$");
					filterList.add(Filters.regex(field[i], caseFlag + "^" + toSearch[i] + "$"));

				} else {

					filterList.add(Filters.regex(field[i], caseFlag + toSearch[i]));

				}
			}
		}
		Filter[] farray = new Filter[filterList.size()];
		for (int i = 0; i < farray.length; i++) {
			farray[i] = filterList.get(i);
		}

		if (matchAll) {
			comboFilter = Filters.and(farray);
		} else {
			comboFilter = Filters.or(farray);
		}

		res = this.mogCollection.getDatabyAttributes(comboFilter, toReturn, true);

		// merge all lists together
		for (int i = 0; i < specialCaseRes.size(); i++) {
			for (String s : specialCaseRes.get(i)) {

				if (!res.contains(s)) {
					res.add(s);
				}
			}
		}

		return res;
	}

	public List<String> searchByValue(String field, String toSearch, String toReturn, boolean exact, boolean matchAll,
			boolean matchCase) {
		return searchByValue(new String[] { field }, new String[] { toSearch }, toReturn, new boolean[] { exact },
				matchAll, new boolean[] { matchCase });

	}

	/**
	 * Search all or any field fields
	 * 
	 * @param toSearch
	 * @param toReturn
	 * @param exact
	 * @param matchAll
	 *            if true search all fields else match anyfield
	 * @param matchCase
	 * @return
	 */
	public List<String> searchByValue(String toSearch, String toReturn, boolean exact, boolean matchAll,
			boolean matchCase) {
		List<String> res = new ArrayList<>();
		JOptionPane.showMessageDialog(null, toSearch+"," +toReturn+","+exact+","+matchAll+","+matchCase);
		res = this.mogCollection.getDatabyAttributes(toSearch, toReturn, exact, true, matchAll, matchCase);
		return res;

	}

	/**
	 * return all columns in a rep
	 * 
	 * @param repName
	 * @return
	 */
	public List<Integer> getColumnsinRep(String repName) {
		List<Integer> res = new ArrayList<>();
		List<Integer> temp = this.defaultrepsMap.get(repName);

		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i) >= 0) {
				res.add(temp.get(i));
			}
		}
		// JOptionPane.showMessageDialog(null, "Fount "+res.size()+" cols in:"+repName);
		return res;
	}

	/**
	 * return all columns in a given rep
	 * 
	 * @param repName
	 * @return
	 */
	public List<Integer> getColumnsinRep(String repName, TreeMap<String, List<Integer>> reps) {
		List<Integer> res = new ArrayList<>();
		List<Integer> temp = reps.get(repName);
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i) >= 0) {
				res.add(temp.get(i));
			}
		}
		// JOptionPane.showMessageDialog(null, "Fount "+res.size()+" cols in:"+repName);
		return res;
	}

	/**
	 * Return xth element in a rep
	 * 
	 * @param repName
	 *            name of rep
	 * @param x
	 *            to return
	 * @return
	 */
	public int getXColumninRep(String repName, int x, TreeMap<String, List<Integer>> reps) {
		// JOptionPane.showMessageDialog(null, "Finding xth col in"+repName);
		List<Integer> res = getColumnsinRep(repName, reps);
		if (res.size() == 0) {
			return -1;
		}
		if (x >= 0 && x < res.size()) {
			return res.get(x);
		} else {
			return -1;
		}
	}

	public String getXColumnNameRep(String repName, int x, TreeMap<String, List<Integer>> reps) {
		// JOptionPane.showMessageDialog(null, "Finding xth col in"+repName);
		List<Integer> res = getColumnsinRep(repName, reps);
		if (res.size() == 0) {
			return null;
		}
		if (x >= 0 && x < res.size()) {
			return getColnamebyIndex(res.get(x));
		} else {
			return null;
		}
	}

	public String[] getAllColumnNameRep(String repName, TreeMap<String, List<Integer>> reps) {
		// JOptionPane.showMessageDialog(null, "Finding xth col in"+repName);
		List<Integer> res = getColumnsinRep(repName, reps);
		if (res.size() == 0) {
			return null;
		}
		List<String> names = new ArrayList<>();
		for (int i = 0; i < res.size(); i++) {
			if (res.get(i) >= 0) {
				names.add(getColnamebyIndex(res.get(i)));
			}
		}
		return names.toArray(new String[0]);
	}

	/**
	 * use default reps
	 * 
	 * @param repName
	 * @param reps
	 * @return
	 */
	public String[] getAllColumnNameRep(String repName) {
		// JOptionPane.showMessageDialog(null, "Finding xth col in"+repName);
		List<Integer> res = getColumnsinRep(repName);
		if (res.size() == 0) {
			return null;
		}
		List<String> names = new ArrayList<>();
		for (int i = 0; i < res.size(); i++) {
			if (res.get(i) >= 0) {
				names.add(getColnamebyIndex(res.get(i)));
			}
		}
		return names.toArray(new String[0]);
	}

	public String[] getIncludedColumnNameRep(String repName) {
		boolean[] excluded = MetaOmAnalyzer.getExclude();
		if (excluded == null) {
			return getAllColumnNameRep(repName);
		}
		List<Integer> res = getColumnsinRep(repName);
		if (res.size() == 0) {
			return null;
		}
		List<String> names = new ArrayList<>();
		for (int i = 0; i < res.size(); i++) {
			// add positive and included cols only
			if (res.get(i) >= 0 && !excluded[res.get(i)]) {
				names.add(getColnamebyIndex(res.get(i)));
			}
		}
		return names.toArray(new String[0]);
	}

	public String[] getIncludedColumnNameRep(String repName, TreeMap<String, List<Integer>> reps) {
		boolean[] excluded = MetaOmAnalyzer.getExclude();
		if (excluded == null) {
			return getAllColumnNameRep(repName, reps);
		}
		List<Integer> res = getColumnsinRep(repName, reps);
		if (res.size() == 0) {
			return null;
		}
		List<String> names = new ArrayList<>();
		for (int i = 0; i < res.size(); i++) {
			// add positive and included cols only
			if (res.get(i) >= 0 && !excluded[res.get(i)]) {
				names.add(getColnamebyIndex(res.get(i)));
			}
		}
		return names.toArray(new String[0]);
	}

	/**
	 * @author urmi Return name of a data column by index
	 * @param index
	 *            index of data column
	 * @return
	 */
	public String getColnamebyIndex(int index) {
		// return knownCols.get(index).getAttributeValue("name").toString();
		return MetaOmGraph.getActiveProject().getDataColumnHeader(index);
	}

	/**
	 * Get index of a column as it appears in datafile. This index is used in
	 * knowncols map
	 * 
	 * @param name
	 * @return
	 */
	public int getColIndexbyName(String name) {
		// order is same as in known cols, searching on String is faster
		String[] allcols = MetaOmGraph.getActiveProject().getDataColumnHeaders();
		for (int i = 0; i < allcols.length; i++) {
			if (allcols[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @author urmi
	 * @param names
	 *            names of columns to get corresponding sortorder
	 * @return in sort order
	 */
	public int[] getSortOrder(String[] names) {
		int[] res = new int[names.length];
		String[] dataCols = MetaOmGraph.getActiveProject().getDataColumnHeaders();
		JOptionPane.showMessageDialog(null, "datacols:" + Arrays.toString(dataCols));
		for (int i = 0; i < names.length; i++) {
			for (int j = 0; j < dataCols.length; j++) {
				if (names[i] == dataCols[j]) {
					res[i] = j;
					break;
				}
			}
		}

		return res;
	}

	/**
	 * @author urmi Remove the fields from the tree which have been removed from
	 *         metadata cols
	 */
	public void prunTreeStructure() {

	}

	public void setCurrentHeaders(String[] h) {
		this.currentmetadataHeaders = h;
	}

	/**
	 * Used for saving current project
	 * 
	 * @return
	 */
	public org.jdom.Document generateFileInfo() {
		org.jdom.Document res = new org.jdom.Document();
		Element root = new Element("MDROOT");
		Element path = new Element("FILEPATH");
		path.setAttribute("name", this.mogCollection.getfilepath());
		Element delim = new Element("DELIMITER");
		delim.setAttribute("name", this.mogCollection.getdelimiter());
		Element datacol = new Element("DATACOL");
		datacol.setAttribute("name", this.mogCollection.getDatacol());
		root.addContent(path);
		root.addContent(delim);
		root.addContent(datacol);
		XMLOutputter outter = new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		res.setRootElement(root);
		return res;
	}

	/**
	 * Function to build custom reps map.
	 * 
	 * @param parentName
	 *            The columnname which will be used to group the datacolumn
	 * @return
	 */
	public TreeMap<String, List<Integer>> buildRepsMapOldnSLOW(String parentName) {
		TreeMap<String, List<Integer>> repsMap = new TreeMap<>();

		for (Map.Entry<Integer, Element> entry : knownCols.entrySet()) {
			Integer key = entry.getKey();
			Element e = entry.getValue();
			int thisIndex = key;
			// get the index of datacolumn from header list
			// if data col has children
			String thisNodeValue = "";
			if (e.getAttributeValue("name") != null) {
				thisNodeValue = e.getAttributeValue("name");
			} else {
				thisNodeValue = e.getContent(0).getValue().toString();
			}
			String thisRepname = mogCollection.getDatabyAttributes(thisNodeValue, parentName, true, true, false, true)
					.get(0);
			Set<String> addedReps = repsMap.keySet();
			if (addedReps.contains(thisRepname)) {
				// append this col
				List<Integer> temp = repsMap.get(thisRepname);
				temp.add(thisIndex);
				repsMap.put(thisRepname, temp);
			} else {
				List<Integer> temp = new ArrayList<>();
				temp.add(thisIndex);
				repsMap.put(thisRepname, temp);
			}
		}

		return repsMap;
	}
	
	
	/**
	 * @author urmi Search rowToMatch under datacolumn in metadata and return value
	 *         under colToreturn column if more than one match is found return only
	 *         first match
	 * @param rowToMatch
	 * @param colToreturn
	 * @return
	 */
	public String getColValueMatchingRow(String rowToMatch, String colToreturn) {

		int thisColindex = MetaOmGraph.getActiveProject().findDataColumnHeader(rowToMatch);
		if (thisColindex < 0) {
			JOptionPane.showMessageDialog(null, "Error in search. " + rowToMatch + " not found in data",
					"Error in search", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		Element thisNode = knownCols.get(thisColindex);
		// if col to return is data column
		if (colToreturn.equals(dataColumn)) {
			int numChild = thisNode.getChildren().size();
			if (numChild < 1) {
				return thisNode.getContent(0).getValue().toString();
			} else {
				return thisNode.getAttributeValue("name").toString();
			}
		}
		String[][] thisMetadata = getNodeMetadata(thisNode);
		String toReturn = "";
		for (int i = 0; i < thisMetadata.length; i++) {
			if (thisMetadata[i][0].equals(colToreturn)) {
				toReturn = thisMetadata[i][1];
				break;
			}
		}

		return toReturn;
	}

	public void setDefaultRepsMap(TreeMap<String, List<Integer>> repsMap) {
		this.defaultrepsMap = repsMap;
	}

	/**
	 * Much faster Build default reps map where each rep is outermost parent in the
	 * tree
	 * 
	 * @param parentName
	 * @return
	 */
	public TreeMap<String, List<Integer>> buildRepsMap(String repColName) {
		MetaOmProject myProj = MetaOmGraph.getActiveProject();
		if (myProj == null) {
			return null;
		}

		TreeMap<String, List<Integer>> repsMap = new TreeMap<>();
		// JOptionPane.showMessageDialog(null, "this kc:" + knownCols.get(1).toString()
		// );
		new AnimatedSwingWorker("Working...", true) {
			@Override
			public Object construct() {
				for (Map.Entry<Integer, Element> entry : knownCols.entrySet()) {
					Integer key = entry.getKey();
					Element e = entry.getValue();
					int thisIndex = key;
					// if datacolumn doesn't exist in data don;t add
					if (key == -1) {
						continue;
					}
					String thisRepname = "";
					String thisDC = myProj.getDataColumnHeader(key);
					//String valExpected = searchByValue(thisDCheader, repColName, true, false, true).get(0);
					//JOptionPane.showMessageDialog(null, "call: "+thisDC+","+repColName);
					String valExpected = mogCollection.getDatabyDataColumn(thisDC, repColName);
					/**
					 * using getNodeMetadata is faster but give ambigous results when multiple
					 * datacolumns have same parent
					 */
					// String[][] thisMetadata = getNodeMetadata(e);
					// JOptionPane.showMessageDialog(null, "expec for col:"+thisDCheader+ "
					// under:"+repColName+" :"+valExpected);
					/*
					 * for (int i = 0; i < thisMetadata.length; i++) {
					 * JOptionPane.showMessageDialog(null, "this md:" + thisMetadata[i][0] + ":" +
					 * thisMetadata[i][1]); if (thisMetadata[i][0].equals(repColName) &&
					 * thisMetadata[i][1].equals(valExpected) ) { thisRepname = thisMetadata[i][1];
					 * break; } }
					 */
					thisRepname = valExpected;
					Set<String> addedReps = repsMap.keySet();
					if (addedReps.contains(thisRepname)) {
						// append this col
						List<Integer> temp = repsMap.get(thisRepname);
						temp.add(thisIndex);
						repsMap.put(thisRepname, temp);
					} else {
						List<Integer> temp = new ArrayList<>();
						temp.add(thisIndex);
						repsMap.put(thisRepname, temp);
					}

				}
				return null;
			}

			@Override
			public void finished() {

			}

		}.start();

		return repsMap;
	}
	
	public TreeMap<String, List<Integer>> buildRepsMapOld(String repColName) {
		MetaOmProject myProj = MetaOmGraph.getActiveProject();
		if (myProj == null) {
			return null;
		}
		TreeMap<String, List<Integer>> repsMap = new TreeMap<>();
		// JOptionPane.showMessageDialog(null, "this kc:" + knownCols.get(1).toString()
		// );
		new AnimatedSwingWorker("Working...", true) {
			@Override
			public Object construct() {
				for (Map.Entry<Integer, Element> entry : knownCols.entrySet()) {
					Integer key = entry.getKey();
					Element e = entry.getValue();
					int thisIndex = key;
					// if datacolumn doesn't exist in data don;t add
					if (key == -1) {
						continue;
					}
					String thisRepname = "";
					//String thisDCheader = myProj.getDataColumnHeader(key);
					//String valExpected = searchByValue(thisDCheader, repColName, true, false, true).get(0);
					/**
					 * using getNodeMetadata is faster but give ambigous results when multiple
					 * datacolumns have same parent
					 */
					 String[][] thisMetadata = getNodeMetadata(e);
										
					for (int i = 0; i < thisMetadata.length; i++) {
						//JOptionPane.showMessageDialog(null, "this md:" + thisMetadata[i][0] + ":" + thisMetadata[i][1]);
						if (thisMetadata[i][0].equals(repColName)) {
							thisRepname = thisMetadata[i][1];
							break;
						}
					}
					
					Set<String> addedReps = repsMap.keySet();
					if (addedReps.contains(thisRepname)) {
						// append this col
						List<Integer> temp = repsMap.get(thisRepname);
						temp.add(thisIndex);
						repsMap.put(thisRepname, temp);
					} else {
						List<Integer> temp = new ArrayList<>();
						temp.add(thisIndex);
						repsMap.put(thisRepname, temp);
					}

				}
				return null;
			}

			@Override
			public void finished() {

			}

		}.start();

		return repsMap;
	}

	/**
	 * After reading metadata file save list of excluded rows from metadata file
	 * 
	 * @param rows
	 */
	public void setExcludedMDRows(List<String> rows) {
		this.excludedMDRows = new HashSet<>(rows);
	}

	/**
	 * Add datacolumns deleted from metadata to Excluded list
	 * 
	 * @param rows
	 */
	public void addExcludedMDRows(Set<String> rows) {

		if (rows == null || rows.size() < 1) {

			return;
		}
		if (this.excludedMDRows == null) {

			this.excludedMDRows = rows;
		} else {

			this.excludedMDRows.addAll(rows);
		}

	}

	/**
	 * Add Missing data columns
	 * 
	 * @param rows
	 */
	public void addMissingMDRows(Set<String> rows) {

		if (rows == null || rows.size() < 1) {

			return;
		}
		if (this.missingMDRows == null) {

			this.missingMDRows = rows;
		} else {

			this.missingMDRows.addAll(rows);
		}

	}

	/**
	 * After reading metadata file save list of missing rows from metadata file
	 * 
	 * @param rows
	 */
	public void setMissingMDRows(List<String> rows) {
		this.missingMDRows = new HashSet<>(rows);
	}

	public Set<String> getMissingMDRows() {
		return missingMDRows;
	}

	public Set<String> getExcludedMDRows() {
		return excludedMDRows;
	}

	public Set<String> getRemovedMDCols() {
		return removedColsfromMD;
	}

	public void setRemovedMDCols(List<String> mdCols) {
		removedColsfromMD = new HashSet<>(mdCols);
	}

	// return columns in metadataheader which are not included in the tree
	// structure.
	public String[] getColumnsNotinTree() {
		List<String> resList = new ArrayList<>();
		List<String> inTree = new ArrayList<>();
		// get headers included in tree
		Enumeration en = ((DefaultMutableTreeNode) treeStructure.getModel().getRoot()).preorderEnumeration();
		String[] allHeaders = mogCollection.getHeaders();
		while (en.hasMoreElements()) {
			inTree.add(en.nextElement().toString());
		}
		for (int i = 0; i < allHeaders.length; i++) {
			if (!inTree.contains(allHeaders[i])) {
				resList.add(allHeaders[i]);
			}
		}
		return resList.toArray(new String[0]);
	}

	public void removeExcludedMDRows() {
		if (excludedMDRows != null)
			mogCollection.removeDataPermanently(excludedMDRows);
	}

	public void initMissingMDRows() {
		// pass as list to access elements by index
		if (missingMDRows != null) {
			List<String> temp = new ArrayList<>(missingMDRows);
			mogCollection.addNullData(temp);
		}
	}

	/**
	 * Convert a list to an XML object
	 * 
	 * @param list
	 * @return
	 */
	public Element listToXML(Set<String> list) {
		Element root = new Element("Root");
		if (list == null) {
			return root;
		}
		for (String s : list) {
			Element newNode = new Element(s);
			newNode.setAttribute("name", newNode.getName());
			root.addContent(newNode);
		}
		return root;
	}

}
