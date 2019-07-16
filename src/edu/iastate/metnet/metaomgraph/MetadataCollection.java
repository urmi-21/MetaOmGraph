package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import edu.iastate.metnet.metaomgraph.ui.MetadataEditor;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;
//import javassist.expr.NewArray;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.biomage.Array.Array;
import org.biomage.examples.GetToDataExample;
import org.dizitart.no2.*;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.filters.Filters;
import org.dizitart.no2.objects.ObjectRepository;

import com.sun.xml.internal.ws.api.policy.ModelGenerator;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import static org.dizitart.no2.filters.Filters.*;

public class MetadataCollection {
	private Nitrite mogInMemoryDB = null;
	private NitriteCollection mogCollection = null;
	private String[] headers = null;
	private int allowedFails = 20;
	private int numRows = 0;
	// list of datacolumns included or excluded
	private Set<String> included;
	private Set<String> excluded;
	private String dataCol;

	// metadata file and other information
	private String fpath;
	private String delimiter;
	private HashMap<String, String> renamedCols;
	private List<String> removeCols;
	private boolean transpose;
	private HashMap<String, String> replaceVals;

	public MetadataCollection(String fpath, String delim, String datacol) {

		if (delim.equals("TAB")) {
			delim = "\t";
		}
		if (mogInMemoryDB == null)
			mogInMemoryDB = Nitrite.builder().openOrCreate();

		// Create a Default Collection
		if (mogCollection == null)
			mogCollection = mogInMemoryDB.getCollection("MetaOmGraph");
		try {
			this.readMetadataTextFile(fpath, delim, false);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Error reading metadata");
			e.printStackTrace();
		}
		this.setDatacol(datacol);
		this.initializeIncludedList();
	}

	public MetadataCollection() {

		if (mogInMemoryDB == null) {
			// JOptionPane.showMessageDialog(null, "moginmem NULL");
			mogInMemoryDB = Nitrite.builder().openOrCreate();
		}

		// Create a Default Collection
		if (mogCollection == null)
			mogCollection = mogInMemoryDB.getCollection("MetaOmGraph");
	}

	public MetadataCollection(String collName) {
		if (mogInMemoryDB == null)
			mogInMemoryDB = Nitrite.builder().openOrCreate();

		// Create a Nitrite Collection
		if (mogCollection == null)
			mogCollection = mogInMemoryDB.getCollection(collName);
	}

	public String[] getHeaders() {
		return this.headers;
	}

	public Nitrite getMogInMemoryDB() {
		return mogInMemoryDB;
	}

	public NitriteCollection getMogCollection() {
		return mogCollection;
	}

	public void dispose() {
		if (mogInMemoryDB != null)
			mogInMemoryDB.close();
	}

	/*
	 * Function to return number of rows in data
	 */
	public int getNumRows() {
		return this.numRows;
	}

	public void setDatacol(String s) {
		this.dataCol = s;
	}

	public String getDatacol() {
		return this.dataCol;
	}

	public void readMetadataTextFile(String metadataFile, String regex, boolean bVerbose) throws IOException {
		List<Document> docList = new ArrayList<>();
		try {
			// included=new ArrayList<>();
			// excluded=new ArrayList<>();
			String thisLine;
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(MetadataCollection.class.getResourceAsStream(metadataFile)));
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(metadataFile)));
			int nCount = 0;
			int totalFails = 0;
			while ((thisLine = in.readLine()) != null) {
				// System.out.println(nCount);
				// if too many fails exit
				if (totalFails > allowedFails) {
					JOptionPane.showMessageDialog(null,
							"Too many errors found please check your file and delimiter. Abort.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// remove all non ascii chars urmi
				// important, otherwise xml parsing will crash
				// removes special chars in xml
				thisLine = thisLine.replaceAll("[^\\x00-\\x7F]", "");
				// remove backslash etc
				thisLine = thisLine.replaceAll("<", "lessthan");
				thisLine = thisLine.replaceAll(">", "greaterthan");
				thisLine = thisLine.replaceAll("\\\\", "bckslsh");
				thisLine = thisLine.replaceAll("&", "*and*");
				// don't remove quotes. This works if data has delimiter inside quotes like
				// col1, "col2 adsad,dsadd", col3. This is common situation in csv data from
				// excel
				// thisLine = thisLine.replaceAll("\"",""); // remove quotes
				// thisLine = thisLine.replaceAll("'", "");
				// thisLine = thisLine.replaceAll("\\*", "(star)");

				if (nCount == 0) {
					// remove quotes from headers
					thisLine = thisLine.replaceAll("\"", "");
					thisLine = thisLine.replaceAll("'", "");
					String[] temp = thisLine.split(regex);
					// remove all blank spaces in headers otherwise MOG hangs
					for (int l = 0; l < temp.length; l++) {
						temp[l] = temp[l].replaceAll(" ", "");
						temp[l] = temp[l].replaceAll("/", "or");
						temp[l] = temp[l].replaceAll("\\(", "_");
						temp[l] = temp[l].replaceAll("\\)", "_");
						// rmove all dots IMPORTANT other wise NO2 wont index
						temp[l] = temp[l].replaceAll("\\.", "_");
						// remove * from headers
						temp[l] = temp[l].replaceAll("\\*", "");

					}
					headers = temp;
					
					//check for duplicate headers
					List<String> headerList=Arrays.asList(headers);
					Set<String> headerSet = new HashSet<String>(headerList);
					// if duplicates found return
					if(headerSet.size()<headers.length) {
						//find duplicate headers
						List<String> duplicates=new ArrayList<>();
						for(String s:headerSet) {
							if(Collections.frequency(headerList, s)>1) {
								duplicates.add(s);
							}
						}
						JOptionPane.showMessageDialog(null,"Metadata validation failed. Please remove following duplicate headers from the metadata file:\n"+String.join(",", duplicates),"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					

				} else {

					// split to avoid spliting in text in ""
					// source
					// https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
					// String[] row = thisLine.split("\t(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
					// changed to
					String[] row = thisLine.split(regex + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
					// String[] row = thisLine.split(regex);

					// System.out.println(row.length+"::"+row[0]);
					if (row.length != headers.length) {
						// JOptionPane.showMessageDialog(null, "rowlen:"+row.length+"
						// hlen:"+headers.length);
						// JOptionPane.showMessageDialog(null, "rows:"+Arrays.toString(row)+"**
						// heads:"+Arrays.toString(headers));
						// System.out.println("headers:"+Arrays.toString(headers));
						// System.out.println("rows:"+Arrays.toString(row));
						JOptionPane.showMessageDialog(null,
								"Metadata validation failed at line:" + (nCount + 1)
										+ ". MOG will skip this. Please check file delimiters at this line.",
								"Error", JOptionPane.ERROR_MESSAGE);
						//JOptionPane.showMessageDialog(null, thisLine);
						totalFails++;
						nCount++;
						continue;
						// return;

					}

					final Map<String, String> metadataMap = new HashMap<>();

					for (int i = 0; i < headers.length; i++) {
						if (row[i].equals("")) {
							row[i] = "NO_VALUE";
						}
						metadataMap.put(headers[i], row[i]);
					}
					Document doc = new Document();
					doc.putAll(metadataMap);
					docList.add(doc);
					// mogCollection.insert(doc);
				}
				nCount++;
			}

			// insert in mogcollection
			int g = 0;
			Document[] docArr = new Document[docList.size()];
			docArr = docList.toArray(docArr);
			mogCollection.insert(docArr);
			/*
			 * for(Document d: docList) { System.out.println(g++); mogCollection.insert }
			 */

			numRows = nCount + 1;

			if (in != null)
				in.close();

			// indexing for finding text
			for (int i = 0; i < headers.length; i++) {
				// System.out.println(headers[i] + "#");
				mogCollection.createIndex(headers[i], IndexOptions.indexOptions(IndexType.Fulltext, false));
			}

			if (bVerbose) {
				System.out.println("////////// ALL Headers /////////////////");
				for (int i = 0; i < getHeaders().length; i++)
					System.out.println(getHeaders()[i]);
				System.out.println("///////////////////////////");
			}

			this.fpath = metadataFile;

			if (regex.equals("\t")) {
				this.delimiter = "TAB";
				// JOptionPane.showMessageDialog(null, "tab");
			} else {
				this.delimiter = regex;
			}

		} catch (IOException e) {
			System.err.println("Error reading metadata file");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Function to update new metadata. Called after split column to add info about
	 * the new columns
	 * 
	 * @param newHeaders
	 * @param data
	 */
	public void updateMetadataColumns(List<String> newHeaders, List<Document> data) {
		String[] neaheaderarr = newHeaders.toArray(new String[newHeaders.size()]);
		updateMetadataColumns(neaheaderarr, data);
	}

	public void updateMetadataColumns(String[] newHeaders, List<Document> data) {
		// remove current collection
		mogCollection.dropAllIndices();
		for (Document d : data) {
			mogCollection.remove(d);
		}
		// Create a Default Collection
		if (mogInMemoryDB == null) {
			mogInMemoryDB = Nitrite.builder().openOrCreate();
		}
		if (mogCollection == null) {
			mogCollection = mogInMemoryDB.getCollection("MetaOmGraph");
		}

		String[] row = new String[newHeaders.length];
		for (int i = 0; i < data.size(); i++) {
			// get each row in data
			int ctr = 0;

			for (int j = 0; j < newHeaders.length; j++) {
				// JOptionPane.showMessageDialog(null, "hdr"+j+newHeaders[j]+"
				// val:"+data.get(i).get(newHeaders[j]).toString());
				row[ctr++] = data.get(i).get(newHeaders[j]).toString();

			}
			final Map<String, String> metadataMap = new HashMap<>();
			ctr = 0;
			for (int k = 0; k < newHeaders.length; k++) {
				metadataMap.put(newHeaders[k], row[ctr++]);
			}
			Document doc = new Document();
			doc.putAll(metadataMap);
			mogCollection.insert(doc);
		}
		// set headers as new_headers

		this.headers = newHeaders;
		// indexing for finding text
		for (int i = 0; i < headers.length; i++) {
			// System.out.println(":new2:" + headers[i]);
			mogCollection.createIndex(headers[i], IndexOptions.indexOptions(IndexType.Fulltext, false));
			mogCollection.rebuildIndex(headers[i], false);
		}

	}

	/**
	 * @author urmi // urmi change headers of existing metadata object // first
	 *         string[] is the list of headers, toKeep is flag to keep each of these
	 *         // headers // if toKeep[i] is false remove that coloumn
	 * @param newHeaders
	 * @param toKeep
	 */

	public void setHeaders(String[] newHeaders, boolean[] toKeep) {
		if (newHeaders.length != headers.length) {
			JOptionPane.showMessageDialog(null, "Error!!! Unequal header sizes...", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// add new headers
		// get all data
		List<Document> data = mogCollection.find().toList();
		// remove current collection
		mogCollection.dropAllIndices();
		for (Document d : data) {
			mogCollection.remove(d);
		}
		// Create a Default Collection
		if (mogInMemoryDB == null) {
			mogInMemoryDB = Nitrite.builder().openOrCreate();
		}
		if (mogCollection == null) {
			mogCollection = mogInMemoryDB.getCollection("MetaOmGraph");
		}

		// add data and header again
		int newS = 0;
		for (int i = 0; i < toKeep.length; i++) {
			if (toKeep[i])
				newS++;
		}
		String[] row = new String[newS];
		for (int i = 0; i < data.size(); i++) {
			// get each row in data
			int ctr = 0;
			for (int j = 0; j < newHeaders.length; j++) {
				// System.out.println("::" + headers[j]);
				if (toKeep[j]) {
					row[ctr++] = data.get(i).get(headers[j]).toString();
					// mogCollection.dropIndex(headers[j]);
				}
			}
			final Map<String, String> metadataMap = new HashMap<>();
			ctr = 0;
			for (int k = 0; k < newHeaders.length; k++) {
				if (toKeep[k]) {
					metadataMap.put(newHeaders[k], row[ctr++]);
				}
			}
			Document doc = new Document();
			doc.putAll(metadataMap);
			mogCollection.insert(doc);
		}
		// set headers as new_headers
		// this.headers = newHeaders;
		// create new headers. if cols are removed headers are removed
		String temp[] = new String[newS];
		int ctr = 0;
		for (int i = 0; i < newHeaders.length; i++) {
			if (toKeep[i]) {
				temp[ctr++] = newHeaders[i];
			}
		}
		this.headers = temp;
		// indexing for finding text
		for (int i = 0; i < headers.length; i++) {
			// System.out.println(":new2:" + headers[i]);
			mogCollection.createIndex(headers[i], IndexOptions.indexOptions(IndexType.Fulltext, false));
			mogCollection.rebuildIndex(headers[i], false);
		}

	}

	/**
	 * @author urmi
	 * @param dataColsToremove
	 *            List of datacolumns to remove, if none given remove excluded list
	 * 
	 */
	public void removeDataPermanently() {
		removeDataPermanently(this.excluded);

	}

	/**
	 * remove rows from metadata containg list of data columns
	 * 
	 * @param dataColsToremove
	 */

	public void removeDataPermanently(List<String> dataColsToremove) {
		removeDataPermanently(new HashSet(dataColsToremove));
	}

	public void removeDataPermanently(Set<String> dataColsToremove) {
		if (dataColsToremove == null) {
			return;
		}
		// get all data
		List<Document> data = mogCollection.find().toList();
		String[] headers = this.getHeaders();
		// remove current collection
		mogCollection.dropAllIndices();
		for (Document d : data) {
			mogCollection.remove(d);
		}
		// Create a Default Collection
		if (mogInMemoryDB == null) {
			mogInMemoryDB = Nitrite.builder().openOrCreate();
		}
		if (mogCollection == null) {
			mogCollection = mogInMemoryDB.getCollection("MetaOmGraph");
		}

		// add data and header
		String[] row = new String[headers.length];
		for (int i = 0; i < data.size(); i++) {
			// get each row in data
			String thisdataCol = data.get(i).get(this.dataCol).toString();
			if (dataColsToremove.contains(thisdataCol)) {
				continue;
			}
			int ctr = 0;
			for (int j = 0; j < headers.length; j++) {
				row[ctr++] = data.get(i).get(headers[j]).toString();

			}

			final Map<String, String> metadataMap = new HashMap<>();
			ctr = 0;
			for (int k = 0; k < headers.length; k++) {
				metadataMap.put(headers[k], row[ctr++]);
			}
			Document doc = new Document();
			doc.putAll(metadataMap);
			mogCollection.insert(doc);
		}
		// indexing for finding text
		for (int i = 0; i < headers.length; i++) {
			// System.out.println(":new2:" + headers[i]);
			mogCollection.createIndex(headers[i], IndexOptions.indexOptions(IndexType.Fulltext, false));
			mogCollection.rebuildIndex(headers[i], false);
		}

		// init included cols
		initializeIncludedList();

	}

	/**
	 * remove columns from a collection
	 * 
	 * @param colsToRemove
	 */
	public void removeUnusedCols(List<String> colsToRemove) {
		if (colsToRemove == null || colsToRemove.size() < 1) {
			return;
		}
		String[] allHeaders = getHeaders();
		boolean[] toKeep = new boolean[allHeaders.length];
		for (int i = 0; i < allHeaders.length; i++) {
			if (colsToRemove.contains(allHeaders[i])) {
				toKeep[i] = false;
			} else {
				toKeep[i] = true;
			}
		}
		setHeaders(allHeaders, toKeep);
	}

	/**
	 * add null data for missing columns
	 * 
	 * @param dataColsToremove
	 */
	public void addNullData(List<String> dataColsToAdd) {

		if (dataColsToAdd == null) {
			return;
		}

		// get current collection

		for (int j = 0; j < dataColsToAdd.size(); j++) {
			Map<String, String> metadataMap = new HashMap<>();

			for (int i = 0; i < headers.length; i++) {
				if (headers[i].equals(dataCol)) {
					metadataMap.put(headers[i], dataColsToAdd.get(j));
				} else
					metadataMap.put(headers[i], "NO_VALUE");
			}
			Document doc = new Document();
			doc.putAll(metadataMap);
			mogCollection.insert(doc);

		}
		// init included cols
		initializeIncludedList();

	}

	public void readMetadataTextFile2(String metadataFile, String regex, boolean bVerbose) throws IOException {
		try {
			String thisLine;
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(MetadataCollection.class.getResourceAsStream(metadataFile)));
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(metadataFile)));
			int nCount = 0;

			while ((thisLine = in.readLine()) != null) {

				if (nCount == 0) {

					String[] temp = thisLine.split(regex);
					headers = temp;

				} else {
					String[] row = thisLine.split(regex);
					// split to avoid spliting in text in ""
					// source
					// https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes

					final Map<String, String> metadataMap = new HashMap<>();

					for (int i = 0; i < headers.length; i++)
						metadataMap.put(headers[i], row[i]);

					Document doc = new Document();
					doc.putAll(metadataMap);
					mogCollection.insert(doc);
				}
				nCount++;
			}

			if (in != null)
				in.close();

			// indexing for finding text
			for (int i = 0; i < headers.length; i++)
				mogCollection.createIndex(headers[i], IndexOptions.indexOptions(IndexType.Fulltext, false));
			;

			if (bVerbose) {
				System.out.println("////////// ALL Headers /////////////////");
				for (int i = 0; i < getHeaders().length; i++)
					System.out.println(getHeaders()[i]);
				System.out.println("///////////////////////////");
			}

		} catch (IOException e) {
			System.err.println("Error reading metadata file");
			e.printStackTrace();
			return;
		}
	}

	public List<String> getSortedUniqueValuesByHeaderName(String headerName, boolean bAllowDuplicates,
			boolean bVerbose) {
		Filter filter = Filters.ALL;
		List<Document> list = mogCollection.find(filter).toList();
		List<String> output = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Document doc = list.get(i);
			output.add(doc.get(headerName).toString());
		}

		output.sort(String::compareToIgnoreCase);

		if (!bAllowDuplicates) {
			Object[] st = output.toArray();
			for (Object s : st) {
				if (output.indexOf(s) != output.lastIndexOf(s)) {
					output.remove(output.lastIndexOf(s));
				}
			}
		}

		if (bVerbose) {
			System.out.println("////////// List of " + headerName + " /////////////////");
			for (int i = 0; i < output.size(); i++)
				System.out.println(output.get(i));
			System.out.println("///////////////////////////");
		}

		return output;
	}

	public List<Document> fullTextSearch(String key, String value, String uniqueID, boolean bVerbose) {

		List<Document> output;

		Filter filter = Filters.regex(key, value);
		output = mogCollection.find(filter, FindOptions.sort(getHeaders()[0], SortOrder.Ascending)).toList();

		if (bVerbose) {
			for (int i = 0; i < output.size(); i++) {
				System.out.println("[" + output.get(i).get(uniqueID) + "] " + output.get(i).get(key));
			}
		}

		return output;
	}

	/**
	 * @author urmi function to search values in particular column and return a set
	 *         of columns corresponding to that Filter example
	 *         Filters.regex("study_accession", "^Exp3$") finds all rows with //
	 *         study accession equal to Exp3 // matches all documents where 'name'
	 *         value starts with 'jim' or 'joe'. // collection.find(regex("name",
	 *         "^(jim|joe).*"));
	 * @param filter
	 * @param uniqueFlag
	 * @return
	 */

	public List<Document> getDatabyAttributes(Filter filter, boolean uniqueFlag) {
		List<Document> output = null;
		output = mogCollection.find(filter).toList();
		if (uniqueFlag) {
			// remove duplicate matches using set
			Set<Document> tempset = new HashSet<Document>();
			tempset.addAll(output);
			output.clear();
			output.addAll(tempset);
		}
		return output;
	}

	// returns String<list> as only one column is returned
	public List<String> getDatabyAttributes(Filter filter, String targetCol, boolean uniqueFlag) {
		if (Arrays.asList(headers).contains(targetCol)) {
			List<Document> output = null;
			List<String> result = new ArrayList<>();
			output = mogCollection.find(filter).toList();
			for (int i = 0; i < output.size(); i++) {
				result.add(output.get(i).get(targetCol).toString());
			}
			if (uniqueFlag) {
				// remove duplicate matches using set
				Set<String> tempset = new HashSet<String>();
				tempset.addAll(result);
				result.clear();
				result.addAll(tempset);
			}
			return result;
		} else {
			JOptionPane.showMessageDialog(null,
					"Error. Target column " + targetCol + " not found while returning search...", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public List<String> getDatabyAttributes(String toSearch, String searchCol, String targetCol, boolean exact,
			boolean uniqueFlag, boolean caseFlag) {
		// create filter
		Filter fa = null;
		if (exact) {
			fa = Filters.regex(searchCol, caseFlag + "^" + toSearch + "$");
		} else {
			fa = Filters.regex(searchCol, caseFlag + toSearch);
		}

		return getDatabyAttributes(fa, targetCol, uniqueFlag);
	}

	/**
	 * This function searches for a value in all the headers and returns
	 * corresponding values under targetCol
	 * 
	 * @param toSearch
	 *            String to search
	 * @param targetCol
	 *            column name whose values to return
	 * @param exact
	 *            match exact values
	 * @param uniqueFlag
	 *            return unique values remove duplicates
	 * @param AND
	 *            match all fields i.e AND operation
	 * @return
	 */
	public List<String> getDatabyAttributes(String toSearch, String targetCol, boolean exact, boolean uniqueFlag,
			boolean AND, boolean matchCase) {
		if (Arrays.asList(headers).contains(targetCol)) {
			List<Document> output = null;
			List<String> result = new ArrayList<>();
			Filter filter;
			String caseFlag = "";
			if (!matchCase) {
				caseFlag = "(?i)";
			}
			// create a filter over all cols
			Filter[] fa = new Filter[this.getHeaders().length];
			for (int i = 0; i < fa.length; i++) {

				if (exact) {
					fa[i] = Filters.regex(this.getHeaders()[i], caseFlag + "^" + toSearch + "$");
				} else {
					fa[i] = Filters.regex(this.getHeaders()[i], caseFlag + toSearch);
				}
			}
			if (AND) {
				filter = Filters.and(fa);
			} else {
				filter = Filters.or(fa);
			}
			output = mogCollection.find(filter).toList();
			for (int i = 0; i < output.size(); i++) {
				result.add(output.get(i).get(targetCol).toString());
			}
			if (uniqueFlag) {
				// remove duplicate matches using set
				Set<String> tempset = new HashSet<String>();
				tempset.addAll(result);
				result.clear();
				result.addAll(tempset);
			}
			return result;
		} else {
			JOptionPane.showMessageDialog(null,
					"Error. Target column " + targetCol + " not found while returning search...", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/**
	 * This function searches for a value in all the headers and returns all the
	 * columns
	 * 
	 * 
	 * @param toSearch
	 *            String to search
	 * @param exact
	 *            match exact values
	 * @param uniqueFlag
	 *            return unique values remove duplicates
	 * @param AND
	 *            match all fields i.e AND operation
	 * @return
	 */
	public List<Document> getDatabyAttributes(String toSearch, boolean exact, boolean uniqueFlag, boolean AND,
			boolean matchCase) {
		List<Document> output = null;
		Filter filter;
		String caseFlag = "";
		if (!matchCase) {
			caseFlag = "(?i)";
		}
		// create a filter over all cols
		Filter[] fa = new Filter[this.getHeaders().length];
		for (int i = 0; i < fa.length; i++) {
			// System.out.println("regex:"+caseFlag+toSearch);
			if (exact) {
				fa[i] = Filters.regex(this.getHeaders()[i], caseFlag + "^" + toSearch + "$");
			} else {
				fa[i] = Filters.regex(this.getHeaders()[i], caseFlag + toSearch);
			}
		}
		if (AND) {
			filter = Filters.and(fa);
		} else {
			filter = Filters.or(fa);
		}
		output = mogCollection.find(filter).toList();
		if (uniqueFlag) {
			// remove duplicate matches using set
			Set<Document> tempset = new HashSet<Document>();
			tempset.addAll(output);
			output.clear();
			output.addAll(tempset);
		}
		return output;
	}

	// add function to search like sql
	/*
	 * public List<Document> returnallData2() { List<Document> output = null; output
	 * = mogCollection.find().toList(); return output; }
	 */
	/**
	 * return all data filtered by included rows only
	 * 
	 * @return
	 */
	// by default filter results
	public List<Document> getAllData() {
		return getAllData(true);
	}

	public List<Document> getAllData(boolean exclude) {
		List<Document> output = null;
		output = mogCollection.find().toList();
		if (excluded == null || excluded.size() == 0) {
			return output;
		}

		if (exclude) {
			return filterResults(output);
		}
		return output;
	}

	/**
	 * filter results to keep only included rows
	 * 
	 * @param s
	 * @return
	 */
	public List<Document> filterResults(List<Document> s) {
		List<Document> res = new ArrayList<>();
		// filter s by the included datacolumns
		for (int i = 0; i < s.size(); i++) {
			String thisDatacolVal = s.get(i).get(this.dataCol).toString();
			if (included.contains(thisDatacolVal)) {
				res.add(s.get(i));
			}
		}
		return res;
	}

	/**
	 * 
	 * @param colVals
	 *            List of keywords to search for
	 * @param field
	 *            field in which search is done
	 * @param keep
	 *            return rows matched data(true) or which doesnet match(false)
	 * @return
	 */
	public List<Document> getAllData(List<String> colVals, String field, boolean keep) {
		List<Document> output = null;
		Filter[] fa = new Filter[colVals.size()];
		for (int i = 0; i < fa.length; i++) {
			Filter f = Filters.regex(field, "^" + colVals.get(i) + "$");
			if (!keep) {
				fa[i] = Filters.not(f);
			} else {
				fa[i] = f;
			}
		}
		Filter filter;
		if (!keep) {
			filter = Filters.and(fa);
		} else {
			filter = Filters.or(fa);
		}
		output = mogCollection.find(filter).toList();
		return output;
	}

	/**
	 * @author urmi get value under a column corresponding to a data column. This
	 *         value will be unique as DC is unique
	 * @param dataColValue
	 *            value of data column to search
	 * @param targetCol
	 *            name of column to return the calue from matched row
	 * @return
	 */
	public String getDatabyDataColumn(String dataColValue, String targetCol) {
		String res = "";
		dataColValue = "^" + dataColValue + "$";
		Filter filter = Filters.regex(getDatacol(), dataColValue);
		List<Document> output = mogCollection.find(filter).toList();
		res = (String) output.get(0).get(targetCol);
		return res;
	}

	public Document getDataColumnRow(String dataColValue) {
		dataColValue = Utils.processStringforRegex(dataColValue);
		dataColValue = "^" + dataColValue + "$";
		Filter filter = Filters.regex(getDatacol(), dataColValue);
		List<Document> output = mogCollection.find(filter).toList();
		if (output.size() > 0) {
			return output.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @param toSearch
	 * @param toSearchColName
	 * @param targetCol
	 * @return
	 */
	public String getDatabyColumn(String toSearch, String toSearchColName, String targetCol) {
		String res = "";
		toSearch = "^" + toSearch + "$";
		Filter filter = Filters.regex(toSearchColName, toSearch);
		List<Document> output = mogCollection.find(filter).toList();
		res = (String) output.get(0).get(targetCol);
		return res;
	}

	public Set<String> getIncluded() {
		return this.included;
	}

	public void setIncluded(List<String> s) {
		setIncluded(new HashSet(s));
	}

	public void setIncluded(Set<String> s) {
		this.included = s;
	}

	public Set<String> getExcluded() {
		return this.excluded;
	}

	public void resetRowFilter() {
		included.addAll(excluded);
		excluded = new HashSet<>();
	}

	public void setExcluded(List<String> s) {
		setExcluded(new HashSet(s));
	}

	public void setExcluded(Set<String> s) {
		this.excluded = s;
	}

	public void initializeIncludedList() {
		included = new HashSet<>();
		excluded = new HashSet<>();
		// get all the data columns in metadata
		List<String> temp = getDatabyAttributes(null, dataCol, true);
		included = new HashSet(temp);

	}

	public String getfilepath() {
		// TODO Auto-generated method stub
		return this.fpath;
	}

	public void setfilepath(String path) {
		// TODO Auto-generated method stub
		this.fpath = path;
	}

	public String getdelimiter() {
		// TODO Auto-generated method stub
		return this.delimiter;
	}

	/**
	 * invert a list of data columns
	 * 
	 * @return
	 */
	public List<String> invertSelectedDataCols(List<String> toInvert) {
		List<String> res = new ArrayList<>();
		List<String> allDC = getAllDataCols();
		for (int i = 0; i < allDC.size(); i++) {
			if (!toInvert.contains(allDC.get(i))) {
				res.add(allDC.get(i));
			}
		}
		return res;
	}

	public List<String> getAllDataCols() {
		return getDatabyAttributes(null, dataCol, true);
	}

}

class MetadataCollectionTest {
	public static void main(String[] args) {

		try {

			System.out.println("In Main..");
			// read metadata for rna-seq
			MetadataCollection mogColl = new MetadataCollection();
			// mogColl.dispose();
			// reading metadata file you want
			// String metadataFile =
			// "D:\\MOGdata\\mog_testdata\\human\\US_human_metadata_6-13-18.txt";
			// String metadataFile =
			// "D:\\MOGdata\\mog_testdata\\human\\US_human_polyA_4-3-18_removedtabs.txt";
			String metadataFile = "D:\\MOGdata\\mog_testdata\\cancer\\raw\\combined\\tcgaMetadataALL\\gtex\\TCGA_GTEX_merged_Metadata_inData.tsv";
			// String metadataFile ="D:\\MOGdata\\mog_testdata\\xml\\sample_small_data.txt";
			// String metadataFile = "C:\\Users\\mrbai\\Downloads\\US_AT_removedtabs.tsv";
			mogColl.readMetadataTextFile(metadataFile, "\\t", false);
			System.out.println("Read Done...");

			String[] hdr = mogColl.getHeaders();
			for (String s : hdr) {
				System.out.println(s);
			}

			mogColl.setDatacol("portions_analytes_aliquots_submitter_id");

			List<String> dc = mogColl.getAllDataCols();
			for (int i = 0; i < 10; i++) {
				System.out.println(dc.get(i));
			}
			int N = 50;
			// search test
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < N; i++) {
				String valExpected = mogColl
						.getDatabyAttributes(dc.get(i), mogColl.getDatacol(), true, true, false, true).get(0);
				if (!dc.get(i).equals(valExpected)) {
					System.out.println("1Failed..." + valExpected);
				}

			}
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("test1 time:" + elapsedTime);

			startTime = System.currentTimeMillis();

			for (int i = 0; i < N; i++) {
				String valExpected = mogColl.getDatabyDataColumn(dc.get(i), mogColl.getDatacol());
				if (!dc.get(i).equals(valExpected)) {
					System.out.println("2Failed..." + valExpected);
				}
			}
			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			System.out.println("test2 time:" + elapsedTime);
			// Filter filter = Filters.regex(mogColl.getDatacol(), dc.get(0));
			// output = mogCollection.find(filter, FindOptions.sort(getHeaders()[0],
			// SortOrder.Ascending)).toList();

			// mogColl.fullTextSearch(key, value, uniqueID, bVerbose)

		} catch (IOException e) {
			System.err.println("Error reading metadata file");
			e.printStackTrace();
			return;
		}
	}
}
