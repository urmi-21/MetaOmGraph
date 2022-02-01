package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.filters.Filters;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.model.MetadataModel;
import edu.iastate.metnet.metaomgraph.model.MetadataTreeModel;
import edu.iastate.metnet.metaomgraph.utils.Utils;

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
	public TreeMap<Integer, Map<String,String>> knownCols;
	// public LinkedHashMap<String, Integer> fields;

	// HashMap<String, Class> mdColType;

	// create blank metadata
	public MetadataHybrid() {

	}

	public MetadataHybrid(MetadataCollection mogCollection, TreeMap<Integer, Map<String,String>> tm,
			String colName, String[] mdheaders,
			String defaultrepsCol, List<String> missingDC, List<String> extraDC, List<String> removedColsfromMD) {
		this.mogCollection = mogCollection;
		// this.metadata = this.mogCollection.getAllData();
		this.knownCols = tm;
		this.dataColumn = colName;
		this.metadataHeaders = mdheaders;

		this.defaultrepsColumn = defaultrepsCol;
		// build default reps map
		
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
	
	
	
	/**
	 * write JTree to XML structure to save to file
	 * 
	 * @param tree
	 * @return
	 * @throws XMLStreamException 
	 */
	public void writeJtreetoXML(JTree tree, XMLStreamWriter xMLStreamWriter) throws XMLStreamException {

		DefaultTreeModel tmodel = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) tmodel.getRoot();
		
		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement(treeRoot.toString());
		xMLStreamWriter.writeAttribute("name", "Root");
		
		buildAndWriteXMLfromJTree(treeRoot, xMLStreamWriter);
		xMLStreamWriter.writeEndElement();
		
	}

	public void buildAndWriteXMLfromJTree(DefaultMutableTreeNode node, XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
		// element = new Element(node.toString());
		int numchild = node.getChildCount();
		for (int i = 0; i < numchild; i++) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) node.getChildAt(i);
			xMLStreamWriter.writeStartElement(thisNode.toString());
			xMLStreamWriter.writeAttribute("name", thisNode.toString());
			
			buildAndWriteXMLfromJTree(thisNode, xMLStreamWriter);
			xMLStreamWriter.writeEndElement();
		}

	}
	
	/**
	 * write JTree to XML structure to save to file
	 * 
	 * @param tree
	 * @return
	 * @throws XMLStreamException 
	 */
	public void writeJtreetoJSON(JTree tree, JsonWriter writer) {

		DefaultTreeModel tmodel = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) tmodel.getRoot();
		List<String> treeChildren = new ArrayList<String>();

		int numchild = treeRoot.getChildCount();
		for (int i = 0; i < numchild; i++) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) treeRoot.getChildAt(i);
		
			if(thisNode != null) {
			treeChildren.add(thisNode.toString());
			
			if(thisNode.getChildCount() > 0){
				addSampleColumnToList(thisNode, treeChildren);
            } 
			
			}
		}
		
		try {
			
			MetadataTreeModel mtm = new MetadataTreeModel(treeRoot.toString(), treeChildren);
			
			Gson gson = new Gson();
			
			gson.toJson(mtm, MetadataTreeModel.class, writer);
			

		}
		catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showInternalMessageDialog(MetaOmGraph.getDesktop(),
					"Unable to save the project file.  Make sure the destination file is not write-protected.",
					"Error saving project", 0);
		}


		
	}
	
	
	public void addSampleColumnToList(DefaultMutableTreeNode node, List<String> treeChildren) {
		
		int numchild = node.getChildCount();
		
		for (int i = 0; i < numchild; i++) {
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) node.getChildAt(i);
		
			if(thisNode != null) {
			treeChildren.add(thisNode.toString());
			
			if(thisNode.getChildCount() > 0){
				addSampleColumnToList(thisNode, treeChildren);
            } 
			
			}
		}
		
	}
	


	public void setmogCollection(MetadataCollection obj) {
		this.mogCollection = obj;
	}

	public TreeMap<String, List<Integer>> getDefaultRepsMap() {
		// build defaultrepsMap in case samples were changed
		setDefaultRepsMap(buildRepsMap(getDefaultRepCol()));
		return this.defaultrepsMap;
	}

	/**
	 * return headers. sorted by default
	 * 
	 * @return
	 */
	public String[] getMetadataHeaders() {
		return getMetadataHeaders(true);
	}

	/**
	 * 
	 * @param sorted
	 *            if true return sorted by name
	 * @return
	 */
	public String[] getMetadataHeaders(boolean sorted) {
		if (sorted) {
			String[] temp = new String[metadataHeaders.length];
			System.arraycopy(metadataHeaders, 0, temp, 0, metadataHeaders.length);
			Arrays.sort(temp);
			return temp;
		}
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
		return mogCollection.getAllData();
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
		String[][] res = getNodeMetadata(colIndex);
		return res;
	}

	/**
	 * @author urmi This function takes a node and returns associated metadata This
	 *         function searches all the parents, their attributes, children and
	 *         their attributes.
	 * @param dataColName
	 *            data column
	 * @return
	 */

	public String[][] getNodeMetadata(String dataColName) {

		String[][] md = null;
		String thisDC = dataColName;
		Document thisRow = mogCollection.getDataColumnRow(thisDC);
		if (thisRow == null) {
			JOptionPane.showMessageDialog(null, "found null");
			return null;
		}
		// JOptionPane.showMessageDialog(null, thisRow.toString());
		// convert to 2d array
		Set<String> entries = thisRow.keySet();
		// minus 3 for Document class keys i.e. id, revision, modified
		md = new String[entries.size() - 3][2];
		int rowNum = 0;
		for (String s : entries) {
			String thisAtt = s;
			if (thisAtt.equals("_id") || thisAtt.equals("_revision") || thisAtt.equals("_modified")) {
				continue;
			}
			md[rowNum][0] = thisAtt;
			md[rowNum][1] = thisRow.get(s).toString();
			rowNum++;
		}
		// JOptionPane.showMessageDialog(null, Arrays.deepToString(md));
		return md;

	}

	public String[][] getNodeMetadata(int nodeNum) {
		if (nodeNum < 0) {
			return new String[0][0];
		}
		MetaOmProject myProj = MetaOmGraph.getActiveProject();
		String thisDC = myProj.getDataColumnHeader(nodeNum);
		// JOptionPane.showMessageDialog(null, "finding md for "+thisDC);
		return getNodeMetadata(thisDC);
	}


	/**
	 * @author urmi This function takes a node and returns metadata of all the
	 *         children
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

	public Map<String,String> getNodeForCol(int col) {
		return knownCols.get(Integer.valueOf(col));
	}


	/**
	 * cluster the indices by values together
	 * 
	 * @author urmi
	 * @param field
	 * 
	 * @return
	 */

	public Map<String, Collection<Integer>> cluster(String field) {
		List<String> fieldList = new ArrayList<>();
		fieldList.add(field);
		return cluster(fieldList);
	}

	/**
	 * Cluster by multiple fields
	 * 
	 * @param field
	 * @return
	 */
	public Map<String, Collection<Integer>> cluster(List<String> field) {
		Map<String, Collection<Integer>> result = new TreeMap();
		// knowncols are data columns or Runs
		// get all data
		List<Document> allData = mogCollection.getAllData();
		for (int i = 0; i < allData.size(); i++) {
			Document thisRow = allData.get(i);
			String thisVal = "";
			for (String f : field) {
				thisVal += thisRow.get(f).toString() + ";";
			}
			if(thisVal.length() > 0) {
			thisVal = thisVal.substring(0, thisVal.length() - 1);
			}
			String thisDc = thisRow.get(dataColumn).toString();
			int thisInd = MetaOmGraph.getActiveProject().findDataColumnHeader(thisDc);
			Collection<Integer> thisBin = result.get(thisVal);
			if (thisBin == null) {
				thisBin = new ArrayList();
			}
			thisBin.add(Integer.valueOf(thisInd));
			result.put(thisVal, thisBin);
		}
		return result;
	}

	/**
	 * Cluster by multiple fields
	 * 
	 * @param field sample data column fields
	 * @param selectedDataCols sample data columns
	 * @return
	 */
	public Map<String, Collection<Integer>> cluster(List<String> field, List<String> selectedDataCols){
		Map<String, Collection<Integer>> result = new TreeMap();

		List<Document> allData = mogCollection.getRowsByDatacols(selectedDataCols);
		for (int i = 0; i < allData.size(); i++) {
			Document thisRow = allData.get(i);
			String thisVal = "";
			for (String f : field) {
				thisVal += thisRow.get(f).toString() + ";";
			}
			
			if(thisVal.length() > 0) {
			thisVal = thisVal.substring(0, thisVal.length() - 1);
			}
			String thisDc = thisRow.get(dataColumn).toString();
			int thisInd = selectedDataCols.indexOf(thisDc);
			Collection<Integer> thisBin = result.get(thisVal);
			if (thisBin == null) {
				thisBin = new ArrayList();
			}
			thisBin.add(Integer.valueOf(thisInd));
			result.put(thisVal, thisBin);
		}
		return result;
	}

	/**
	 * create and return a map mapping datacolumn --> givenCol
	 * 
	 * @param field
	 * @param exclude
	 *            true --> don't get data for excluded cols
	 * @return
	 */
	public HashMap<String, String> getDataColMap(String field, boolean exclude) {
		HashMap<String, String> res = new HashMap<>();
		List<Document> allData = mogCollection.getAllData(exclude);
		for (int i = 0; i < allData.size(); i++) {
			Document thisRow = allData.get(i);
			String thisVal = thisRow.get(field).toString();
			String thisDc = thisRow.get(dataColumn).toString();
			res.put(thisDc, thisVal);
		}
		return res;
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

		ArrayList<Integer> result = new ArrayList();
		Integer[] toReturn;
		List<String> colVals = new ArrayList<>();
		String[] allfields = new String[queries.length];
		String[] toSearch = new String[queries.length];
		SearchMatchType[] matchTypes = new SearchMatchType[queries.length];
		boolean[] matchCase = new boolean[queries.length];
		// search using Metadatacollection object and return indices of matching data
		// columns from knownCols
		// do for all queries

		for (int i = 0; i < queries.length; i++) {
			allfields[i] = queries[i].getField();
			toSearch[i] = queries[i].getTerm();
			matchTypes[i] = queries[i].getMatchType();
			matchCase[i] = queries[i].isCaseSensitive();
			// JOptionPane.showMessageDialog(null, "search f:" + fields[i]);
		}

		colVals.addAll(searchByValue(allfields, toSearch, this.dataColumn, matchTypes, matchAll, matchCase));

		// JOptionPane.showMessageDialog(null, "colvals:" + colVals.toString());
		// JOptionPane.showMessageDialog(null, "knownCols:" + knownCols.toString());

		// find all keys with value in colVals
		for (Entry<Integer, Map<String,String>> entry : knownCols.entrySet()) {
			Integer key = entry.getKey();
			Map<String,String> value = entry.getValue();
			String thisName = null;
			
			thisName = value.get(this.dataColumn);

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
		SearchMatchType[] matchTypes = new SearchMatchType[queries.length];
		boolean[] matchCase = new boolean[queries.length];
		// search using Metadatacollection object and return indices of matching data
		// columns from knownCols
		// do for all queries
		for (int i = 0; i < queries.length; i++) {
			allfields[i] = queries[i].getField();
			toSearch[i] = queries[i].getTerm();
			matchTypes[i] = queries[i].getMatchType();
			matchCase[i] = queries[i].isCaseSensitive();
		}

		colVals.addAll(searchByValue(allfields, toSearch, this.dataColumn, matchTypes, matchAll, matchCase));

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
	public List<String> searchByValue(String[] field, String[] toSearch, String toReturn, SearchMatchType[] matchType,
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
				specialCaseRes.add(searchByValue(toSearch[i], toReturn, matchType[i], true, matchCase[i]));

			} else if (field[i] == "Any Field") {
				specialCaseRes.add(searchByValue(toSearch[i], toReturn, matchType[i], false, matchCase[i]));

			} else {
				if (matchType[i] == SearchMatchType.IS) {
					// farray[i] = Filters.regex(field[i], "^" + toSearch[i] + "$");
					filterList.add(Filters.regex(field[i], caseFlag + "^" + toSearch[i] + "$"));

				} else if(matchType[i] == SearchMatchType.CONTAINS) {
					filterList.add(Filters.regex(field[i], caseFlag + toSearch[i]));
				} else if(matchType[i] == SearchMatchType.NOT){
					// exactly not
					filterList.add(Filters.regex(field[i], caseFlag + "^(?!" + toSearch[i] + "$).*$"));
				} else {
					filterList.add(Filters.regex(field[i], caseFlag + "^(?!" + toSearch[i] + ").*$"));
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

		// merge results with special case results
		if (matchAll) {
			// do intersection of all results
			// Utils.getListIntersection(specialCaseRes);
			// JOptionPane.showMessageDialog(null, "spRES:" + specialCaseRes.toString() +
			// "res:" + res.toString()+ " intr:" +
			// Utils.getListIntersection(specialCaseRes));

			// if there were other queries get the intersection
			if (farray.length > 0) {
				specialCaseRes.add(res);
			}
			res = Utils.getListIntersection(specialCaseRes);

			// JOptionPane.showMessageDialog(null, "res:" + res);

		} else {

			specialCaseRes.add(res);
			res = Utils.getListUnion(specialCaseRes);
			/*
			 * for (int i = 0; i < specialCaseRes.size(); i++) { for (String s :
			 * specialCaseRes.get(i)) {
			 * 
			 * if (!res.contains(s)) { res.add(s); } } }
			 */
		}

		return res;
	}

	public List<String> searchByValue(String field, String toSearch, String toReturn, SearchMatchType matchType, boolean matchAll,
			boolean matchCase) {
		return searchByValue(new String[] { field }, new String[] { toSearch }, toReturn, 
				new SearchMatchType[] { matchType }, matchAll, new boolean[] { matchCase });

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
	public List<String> searchByValue(String toSearch, String toReturn, SearchMatchType matchType, boolean matchAll,
			boolean matchCase) {
		List<String> res = new ArrayList<>();
		res = this.mogCollection.getDatabyAttributes(toSearch, toReturn, matchType, true, matchAll, matchCase);
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
	public void generateFileInfo(XMLStreamWriter xMLStreamWriter) {
		//org.jdom.Document res = new org.jdom.Document();

		try {
			xMLStreamWriter.writeStartDocument();
			xMLStreamWriter.writeStartElement("MDROOT");

			xMLStreamWriter.writeStartElement("FILEPATH");
			xMLStreamWriter.writeAttribute("name", this.mogCollection.getfilepath());
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement("DELIMITER");
			xMLStreamWriter.writeAttribute("name", this.mogCollection.getdelimiter());
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement("DATACOL");
			xMLStreamWriter.writeAttribute("name", this.mogCollection.getDatacol());
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeEndElement();

		}
		catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showInternalMessageDialog(MetaOmGraph.getDesktop(),
					"Unable to save the project file.  Make sure the destination file is not write-protected.",
					"Error saving project", 0);
		}

	}
	
	
	/**
	 * Used for saving current project as a JSON
	 * 
	 * @return
	 */
	public void generateFileInfoJSON(JsonWriter writer) {
		
		try {
			
			MetadataModel mm = new MetadataModel(this.mogCollection.getfilepath(), this.mogCollection.getdelimiter(), this.mogCollection.getDatacol(), MetaOmGraph.getActiveTable().getMetadataTableDisplay().getMetadataTableHeaders());
			
			Gson gson = new Gson();
			
			gson.toJson(mm, MetadataModel.class, writer);
			

		}
		catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showInternalMessageDialog(MetaOmGraph.getDesktop(),
					"Unable to save the project file.  Make sure the destination file is not write-protected.",
					"Error saving project", 0);
		}

	}
	
	public void setDefaultRepsMap(TreeMap<String, List<Integer>> repsMap) {
		this.defaultrepsMap = repsMap;
	}

	/**
	 * Get the metadata file path in the project
	 * @return
	 */
	public String getMetadataFilePath() {
		return this.mogCollection.getfilepath();
	}


	/**
	 * Function to build custom reps map.
	 * 
	 * @param parentName
	 *            The columnname which will be used to group the datacolumn
	 * @return
	 */

	public TreeMap<String, List<Integer>> buildRepsMap(String repColName) {
		MetaOmProject myProj = MetaOmGraph.getActiveProject();
		if (myProj == null) {
			return null;
		}

		TreeMap<String, List<Integer>> repsMap = new TreeMap<>();
		// Map<String, Collection<Integer>> clusterResult = new TreeMap();
		new AnimatedSwingWorker("Working...", true) {
			@Override
			public Object construct() {
				Map<String, Collection<Integer>> clusterResult = cluster(repColName);
				for (String s : clusterResult.keySet()) {
					repsMap.put(s, new ArrayList(clusterResult.get(s)));
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


	/**
	 * Method that writes a list to the .mog file using StAX parser
	 * 
	 * @param list
	 * @return
	 * @throws XMLStreamException 
	 */
	public void writeListToXML(Set<String> list, XMLStreamWriter xMLStreamWriter) throws XMLStreamException {

		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement("Root");

		if(list != null) {
			for (String s : list) {

				xMLStreamWriter.writeStartElement(s);
				xMLStreamWriter.writeAttribute("name", s);
				xMLStreamWriter.writeEndElement();

			}
		}
		xMLStreamWriter.writeEndElement();

	}
	
	
	/**
	 * Method that writes a list to the .mog file using GSON
	 * 
	 * @param list
	 * @return
	 */
	public void writeListToJSON(Set<String> list, JsonWriter writer) {
		
		
		try {

			Gson gson = new Gson();

			gson.toJson(list, Set.class, writer);

		}
		catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showInternalMessageDialog(MetaOmGraph.getDesktop(),
					"Unable to save the project file.  Make sure the destination file is not write-protected.",
					"Error saving project", 0);
		}


	}

}
