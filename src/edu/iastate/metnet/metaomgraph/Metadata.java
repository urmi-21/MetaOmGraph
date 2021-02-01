package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.utils.MetadataUpdater;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLizable;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Metadata {
	private SimpleXMLElement metadataRoot;
	public TreeMap<Integer, SimpleXMLElement> knownCols;
	public LinkedHashMap<String, Integer> fields;
	static BufferedImage image;

	public Metadata(File source, MetaOmProject myProject) throws IOException {
		this(new FileInputStream(source), myProject);
	}

	public Metadata(InputStream source, MetaOmProject myProject) throws IOException {
		// JOptionPane.showMessageDialog(null, "loadfromstreamstart");
		loadFromStream(source, myProject);
		JOptionPane.showMessageDialog(null, "Metadata is now loaded into project.");
		
	}

	public Metadata(MetaOmProject myProject) {
		metadataRoot = new SimpleXMLElement("MOGMetadata");
		knownCols = new TreeMap();
		fields = new LinkedHashMap();
		createBlankMetadata(myProject);
	}

	private void loadFromStream(InputStream instream, MetaOmProject myProject) throws IOException {
		System.out.println("Loading metadata from " + instream);
		BufferedInputStream source2 = new BufferedInputStream(instream);
		source2.mark(52);
		byte[] buffer = new byte[52];
		source2.read(buffer, 0, 52);
		source2.reset();
		System.out.println("First " + buffer.length + " chars: " + new String(buffer));
		if (new String(buffer).contains("<Experiments>")) {
			JOptionPane.showMessageDialog(MetaOmGraph.getDesktop(),
					"MetaOmGraph needs to update the selected metadata. Please be patient, this may take a while.",
					"Metadata Update", 1);
			System.out.println("Updating metadata");
			final PipedOutputStream newMetadataOut = new PipedOutputStream();
			final BufferedInputStream oldMetadataIn = new BufferedInputStream(source2);
			PipedInputStream newMetadataIn = new PipedInputStream(newMetadataOut);

			// JOptionPane.showMessageDialog(null, "run start");
			new Thread() {
				@Override
				public void run() {
					try {
						MetadataUpdater.update(oldMetadataIn, newMetadataOut);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}.start();
			// JOptionPane.showMessageDialog(null, "run end");
			loadFromStream(newMetadataIn, myProject);
			return;
		}
		// JOptionPane.showMessageDialog(null, "final strt");
		metadataRoot = SimpleXMLElement.fromStream(source2);
		knownCols = new TreeMap();
		fields = new LinkedHashMap();
		analyzeMetadata(metadataRoot, myProject);
		createBlankMetadata(myProject);
		// JOptionPane.showMessageDialog(null, "final end");
	}

	private void analyzeMetadata(SimpleXMLElement root, MetaOmProject myProject) {
		// urmi: doesnt work
		// if (root==null) {
		// return;
		// }
		String col = "";
		// JOptionPane.showMessageDialog(null, root.toFullString().substring(0, 10));
		// urmi catch null exception
		try {
			col = root.getAttributeValue("col");
		} catch (java.lang.NullPointerException npe) {
			JOptionPane.showMessageDialog(null,
					"Error...There seems to be something wrong with the metadata. Please make sure that metadata structure is correct and there is no empty information in parent nodes.");
			// JOptionPane.showMessageDialog(null, root.toFullString());
			// col="xx";
		}

		if ((col != null) && (!"".equals(col))) {
			try {
				int colIndex = Integer.parseInt(col);
				knownCols.put(Integer.valueOf(colIndex), root);
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse column value: " + col);
			}
		} else if ("Sample".equals(root.getName())) {
			String name = root.getAttributeValue("name");
			if (name != null) {
				String fixedName = Utils.condenseString(name);
				boolean found = false;
				for (int i = 0; i < myProject.getDataColumnCount(); i++)
					if (!knownCols.containsKey(Integer.valueOf(i))) {

						String colName = Utils.condenseString(myProject.getDataColumnHeader(i));
						if (colName.equals(fixedName)) {
							knownCols.put(Integer.valueOf(i), root);
							root.setAttribute("col", col);
							found = true;
							System.out.println("Forcibly matched [" + fixedName + "] to " + i);
						}
					}
			}
		}
		String field = root.getAttributeValue("field");
		if (field != null) {
			Integer count = fields.get(field);
			if (count == null) {
				count = Integer.valueOf(0);
			}
			count = Integer.valueOf(count.intValue() + 1);
			fields.put(field, count);
		}
		if (!root.isLeaf()) {
			for (int i = 0; i < root.getChildCount(); i++) {
				analyzeMetadata(root.getChildAt(i), myProject);
			}
		}
	}

	public void scanForCols(SimpleXMLElement root) {
		String col = root.getAttributeValue("col");
		if ((col != null) && (!"".equals(col))) {
			try {
				knownCols.put(new Integer(col), root);
			} catch (NumberFormatException localNumberFormatException) {
			}
		}
		for (int i = 0; i < root.getChildCount(); i++) {
			scanForCols(root.getChildAt(i));
		}
	}

	private void createBlankMetadata(MetaOmProject myProject) {
		for (int i = 0; i < myProject.getDataColumnCount(); i++) {
			if (!knownCols.containsKey(Integer.valueOf(i))) {
				SimpleXMLElement node = new SimpleXMLElement("Sample");
				node.setAttribute("name", myProject.getDataColumnHeader(i));
				node.setAttribute("col", i + "");
				metadataRoot.add(node);
				knownCols.put(Integer.valueOf(i), node);
			}
		}
	}

	public int getColForNode(SimpleXMLElement node) {
		String col = node.getAttributeValue("col");
		if ((col != null) && (!"".equals(col))) {
			try {
				return Integer.parseInt(col);
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse column value: " + col);
			}
		}
		if (!node.isRoot()) {
			return getColForNode(node.getParent());
		}
		return -1;
	}

	public SimpleXMLElement getNodeForCol(int col) {
		return knownCols.get(Integer.valueOf(col));
	}

	public String[][] getMetadataForCol(int col) {
		SimpleXMLElement node = getNodeForCol(col);
		return getMetadataForNode(node);
	}

	public String[][] getMetadataForCol(int col, boolean includeParents) {
		SimpleXMLElement node = getNodeForCol(col);
		return getMetadataForNode(node, includeParents);
	}

	private String[][] getMetadataForNode(SimpleXMLElement node) {
		return getMetadataForNode(node, true);
	}

	private String[][] getMetadataForNode(SimpleXMLElement node, boolean includeParents) {
		if (node == null) {
			return null;
		}

		ArrayList<String> fields = new ArrayList();
		ArrayList<String> values = new ArrayList();
		String thisField = node.getAttributeValue("field");
		String thisValue = node.getAttributeValue("value");
		if (thisField != null) {
			fields.add(thisField);
			values.add(thisValue);
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			SimpleXMLElement childNode = node.getChildAt(i);
			if (childNode.isLeaf()) {
				thisField = childNode.getAttributeValue("field");
				thisValue = childNode.getAttributeValue("value");
				if (thisField != null) {
					fields.add(thisField);
					values.add(thisValue);
				}
			}
		}
		if (includeParents) {
			SimpleXMLElement parent = node.getParent();
			while (parent != null) {
				for (int i = 0; i < parent.getChildCount(); i++) {
					SimpleXMLElement childNode = parent.getChildAt(i);
					if (childNode.isLeaf()) {
						thisField = childNode.getAttributeValue("field");
						thisValue = childNode.getAttributeValue("value");
						if (thisField != null) {
							fields.add(thisField);
							values.add(thisValue);
						}
					}
				}
				parent = parent.getParent();
			}
		}
		String[][] result = new String[fields.size()][2];
		for (int i = 0; i < fields.size(); i++) {
			result[i][0] = fields.get(i);
			result[i][1] = values.get(i);
		}
		return result;
	}

	public String[] getFields() {
		if (fields == null) {
			return null;
		}
		Set<String> keys = fields.keySet();
		String[] result = new String[fields.size()];
		int index = 0;
		for (String key : keys) {
			result[(index++)] = key;
		}
		return result;
	}

	public void outputToFile(File dest) throws IOException {
		outputToStream(new FileOutputStream(dest));
	}

	public void outputToStream(OutputStream outstream) throws IOException {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outstream, "UTF-8"));
			out.write(metadataRoot.toFullString());
			out.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public Integer[] search(String term) {
		return search(null, term, SearchMatchType.CONTAINS);
	}

	public Integer[] search(String field, String term, SearchMatchType matchType) {
		return search(new String[] { field }, new String[] { term }, new SearchMatchType[] { matchType }, true);
	}

	public Integer[] search(String[] fields, String[] terms, SearchMatchType[] matchTypes, boolean matchAll) {
		MetadataQuery[] queries = new MetadataQuery[terms.length];
		for (int i = 0; i < queries.length; i++) {
			String term = terms[i];
			String field = null;
			SearchMatchType matchType = SearchMatchType.CONTAINS;
			if (i < fields.length) {
				field = fields[i];
			}
			if (i < matchTypes.length) {
				matchType = matchTypes[i];
			}
			queries[i] = new MetadataQuery(field, term, matchType,false);
		}
		return search(queries, matchAll);
	}

	public Integer[] search(MetadataQuery query) {
		return search(new MetadataQuery[] { query }, true);
	}

	public Integer[] search(MetadataQuery[] queries, boolean matchAll) {
		Set<Integer> cols = knownCols.keySet();
		ArrayList<Integer> result = new ArrayList();
		for (Integer col : cols) {
			SimpleXMLElement node = knownCols.get(col);
			int[] hits = matchChildren(node, queries);
			for (int thisHits : hits) {
				if (thisHits > 0) {
					System.out.println("Found some hits!");
				}
			}
			SimpleXMLElement parent = node.getParent();
			while (parent != null) {
				for (int childIndex = 0; childIndex < parent.getChildCount(); childIndex++) {
					SimpleXMLElement child = parent.getChildAt(childIndex);
					String field = child.getAttributeValue("field");
					String value = child.getAttributeValue("value");
					if (value != null) {
						for (int queryIndex = 0; queryIndex < queries.length; queryIndex++) {
							if (queries[queryIndex].matches(field, value)) {
								hits[queryIndex] += 1;
								System.out.println("Hit for column " + col);
							}
						}
					}
				}
				parent = parent.getParent();
			}
			boolean failed = false;
			if (matchAll) {
				int i = 0;
				do {
					if (hits[i] <= 0) {
						failed = true;
					}
					i++;
					if (i >= hits.length)
						break;
				} while (!failed);

			} else {

				failed = true;
				for (int i = 0; (i < hits.length) && (failed); i++) {
					if (hits[i] > 0) {
						failed = false;
					}
				}
			}
			if (!failed) {
				result.add(col);
			}
		}
		return result.toArray(new Integer[0]);
	}

	private int[] matchChildren(SimpleXMLElement root, MetadataQuery[] queries) {
		int[] hits = new int[queries.length];
		String field = root.getAttributeValue("field");
		String value = root.getAttributeValue("value");
		if (value != null) {
			for (int i = 0; i < queries.length; i++) {
				if (queries[i].matches(field, value)) {
					hits[i] += 1;
				}
			}
		}

		for (int i = 0; i < root.getChildCount(); i++) {
			int[] childHits = matchChildren(root.getChildAt(i), queries);
			for (int hitIndex = 0; hitIndex < hits.length; hitIndex++) {
				hits[hitIndex] += childHits[hitIndex];
			}
		}
		return hits;
	}

	public SimpleXMLElement getXMLRoot() {
		return metadataRoot;
	}

	public SimpleXMLElement splitMetadata(Map<Integer, Integer> colMap) {
		SimpleXMLElement newMetadataRoot = new SimpleXMLElement("MOGMetadata");
		Set<Integer> keys = colMap.keySet();
		TreeSet<Integer> colsToKeep = new TreeSet();
		for (Integer thisCol : keys) {
			colsToKeep.add(thisCol);
		}

		while (!colsToKeep.isEmpty()) {
			Integer thisCol = colsToKeep.first();
			colsToKeep.remove(thisCol);
			SimpleXMLElement node = getNodeForCol(thisCol.intValue());
			SimpleXMLElement parent = node.getParent();
			SimpleXMLElement addMe = node.fullCopy(true);
			addMe.setAttribute("col", colMap.get(thisCol) + "");

			while (parent != metadataRoot) {
				SimpleXMLElement newParent = new SimpleXMLElement(parent);
				newParent.add(addMe);
				for (int i = 0; i < parent.getChildCount(); i++) {
					SimpleXMLElement child = parent.getChildAt(i);

					if (child.getName().equals("md")) {
						newParent.add(child.fullCopy(true));
					} else if (child.getAttributeValue("col") != null) {
						try {
							Integer colAttribute = new Integer(child.getAttributeValue("col"));
							if (colsToKeep.contains(colAttribute)) {
								System.out.println(
										"Bringing over " + colAttribute + " - " + child.getAttributeValue("name"));
								SimpleXMLElement newChild = child.fullCopy(true);
								newChild.setAttribute("col", colMap.get(colAttribute) + "");
								newParent.add(newChild);
								colsToKeep.remove(colAttribute);
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Couldn't parse " + child.getAttributeValue("col"));
						}
					}
					addMe = newParent;
				}
				parent = parent.getParent();
			}
			newMetadataRoot.add(addMe);
		}
		return newMetadataRoot;
	}

	public String getFieldValue(String field, int col) {
		SimpleXMLElement node = getNodeForCol(col);
		for (int i = 0; i < node.getChildCount(); i++) {
			SimpleXMLElement child = node.getChildAt(i);
			if (field.equals(child.getAttributeValue("field"))) {
				return child.getAttributeValue("value");
			}
		}
		SimpleXMLElement parent = node.getParent();
		while ((!"Experiment".equals(parent.getName())) && (!parent.isRoot())) {
			parent = parent.getParent();
		}
		for (int i = 0; i < parent.getChildCount(); i++) {
			SimpleXMLElement sibling = parent.getChildAt(i);
			if (sibling != node) {

				if (field.equals(sibling.getAttributeValue("field")))
					return sibling.getAttributeValue("value");
			}
		}
		return null;
	}

	public String[] getFieldValues(String field, int col) {
		SimpleXMLElement node = getNodeForCol(col);
		ArrayList<String> result = new ArrayList();
		for (int i = 0; i < node.getChildCount(); i++) {
			SimpleXMLElement child = node.getChildAt(i);
			if (field.equals(child.getAttributeValue("field"))) {
				result.add(child.getAttributeValue("value"));
			}
		}
		SimpleXMLElement parent = node.getParent();
		for (int i = 0; i < parent.getChildCount(); i++) {
			SimpleXMLElement sibling = parent.getChildAt(i);
			if (sibling != node) {

				if (field.equals(sibling.getAttributeValue("field")))
					result.add(sibling.getAttributeValue("value"));
			}
		}
		return result.toArray(new String[0]);
	}

	public Map<String, Collection<Integer>> cluster(String field, int minSize) {
		Map<String, Collection<Integer>> result = new TreeMap();
		Set<Integer> cols = knownCols.keySet();
		for (Iterator localIterator = cols.iterator(); localIterator.hasNext();) {
			int col = ((Integer) localIterator.next()).intValue();
			String val = getFieldValue(field, col);
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

	public static class MetadataQuery implements Serializable, SimpleXMLizable<MetadataQuery> {
		private String field;

		private String term;

		private SearchMatchType matchType;
		
		private boolean matchCase;

		public MetadataQuery() {
		}

		public MetadataQuery(String field, String term, SearchMatchType matchType,boolean matchCase) {
			this.field = field;
			this.term = term.trim();
			this.matchType = matchType;
			this.matchCase=matchCase;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getTerm() {
			return term;
		}

		public void setTerm(String term) {
			this.term = term;
		}

		public SearchMatchType getMatchType() {
			return matchType;
		}
		
		public void setMatchType(SearchMatchType matchType) {
			this.matchType = matchType;
		}
		
		//urmi
		public boolean isCaseSensitive() {
			return matchCase;
		}
		
		public void setCaseSensitive(boolean flag) {
			matchCase=flag;
		}

		@Override
		public SimpleXMLElement toXML() {
			SimpleXMLElement result = new SimpleXMLElement(getXMLElementName()).setAttribute("matchType",
					matchType.toString());
			if (field != null) {
				result.add(new SimpleXMLElement("field").setText(field));
			}
			result.add(new SimpleXMLElement("term").setText(term));
			return result;
		}
		
		
		/**
		 * 
		 * @param xMLStreamWriter
		 * @throws XMLStreamException
		 * 
		 * Method to write metadata to the .mog file using StAX parser
		 * 
		 */
		public void writeToXML(XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
			
			xMLStreamWriter.writeStartElement(getXMLElementName());
			xMLStreamWriter.writeAttribute("matchAll", matchType.toString());
			
			if (field != null) {
				xMLStreamWriter.writeStartElement("field");
				xMLStreamWriter.writeCharacters(field);
				xMLStreamWriter.writeEndElement();
			}
			
			xMLStreamWriter.writeStartElement("term");
			xMLStreamWriter.writeCharacters(term);
			xMLStreamWriter.writeEndElement();
			
			xMLStreamWriter.writeEndElement();
		}

		@Override
		public MetadataQuery fromXML(SimpleXMLElement source) {
			matchType = SearchMatchType.CONTAINS;
			try {
				matchType = SearchMatchType.valueOf(source.getAttributeValue("matchType").toUpperCase());
			}
			// to make compatible with older versions of mog.
			catch(NullPointerException e) {
				boolean isExact = "true".equals(source.getAttributeValue("exact"));
				if(isExact)
					matchType = SearchMatchType.IS;
			}
			for (int i = 0; i < source.getChildCount(); i++) {
				SimpleXMLElement child = source.getChildAt(i);
				if ("field".equals(child.getName())) {
					field = child.getText();
				} else if ("term".equals(child.getName())) {
					term = child.getText();
				}
			}
			return this;
		}

		public boolean matches(SimpleXMLElement node) {
			String field = node.getAttributeValue("field");
			String value = node.getAttributeValue("value");
			return matches(field, value);
		}

		public boolean matches(String field, String value) {
			String thisField = getField();
			if ((thisField == null) || ("".equals(thisField)) || (thisField.equalsIgnoreCase(field))) {
				String term = getTerm();
				if (getMatchType() == SearchMatchType.IS) {
					if (value.equalsIgnoreCase(term)) {
						return true;
					}
				} else if (getMatchType() == SearchMatchType.CONTAINS) {
					if (value.toLowerCase().contains(term.toLowerCase()))
						return true;
				}
				else {
					if (!value.equalsIgnoreCase(term)) {
						return true;
					}
				}
			}

			return false;
		}

		public static String getXMLElementName() {
			return "query";
		}

		@Override
		public String toString() {
			return "MetadataQuery [field=" + field + ", term=" + term + ", matchType=" + matchType + ", matchCase="
					+ matchCase + "]";
		}
		
		
	}

	public ArrayList<ArrayList<SimpleXMLElement>> getSampleGroups(SimpleXMLElement root) {
		ArrayList<ArrayList<SimpleXMLElement>> result = new ArrayList();
		ArrayList<SimpleXMLElement> myList = new ArrayList();
		for (int i = 0; i < root.getChildCount(); i++) {
			SimpleXMLElement node = root.getChildAt(i);
			if ("Sample".equals(node.getName())) {
				myList.add(node);
			} else {
				result.addAll(getSampleGroups(node));
			}
		}
		result.add(myList);
		return result;
	}

	public ArrayList<ArrayList<SimpleXMLElement>> getSampleGroups() {
		return getSampleGroups(metadataRoot);
	}

	public ArrayList<SimpleXMLElement> getSampleNodes(SimpleXMLElement root) {
		ArrayList<SimpleXMLElement> result = new ArrayList();
		if ("Sample".equals(root.getName())) {
			result.add(root);
		} else {
			for (int i = 0; i < root.getChildCount(); i++) {
				result.addAll(getSampleNodes(root.getChildAt(i)));
			}
		}
		return result;
	}

	public ArrayList<SimpleXMLElement> getSampleNodes() {
		return getSampleNodes(metadataRoot);
	}

	public void findReps_OLD() {
		System.out.println("Finding replicates");

		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Finding Replicates", "Searching Metadata for Replicates", 0L, metadataRoot.getChildCount(), true);
		new Thread() {
			@Override
			public void run() {
				int groupIndex = 1;
				ArrayList<ArrayList<SimpleXMLElement>> sampleGroups = getSampleGroups();
				for (ArrayList<SimpleXMLElement> thisGroup : sampleGroups) {
					while (thisGroup.size() > 0) {
						// ArrayList<Integer> thisRepGroup = new ArrayList<Integer>();
						SimpleXMLElement node = thisGroup.remove(0);
						String name = node.getAttributeValue("name");
						SimpleXMLElement groupNode = createGroupNode(makeGroupName(name), node.getParent());
						node.removeFromParent();
						groupNode.add(node);
						for (int i = 0; i < thisGroup.size(); i++) {
							SimpleXMLElement sibling = thisGroup.get(i);
							String siblingName = sibling.getAttributeValue("name");
							if (areReps(name, siblingName)) {
								sibling.removeFromParent();
								groupNode.add(sibling);
								thisGroup.remove(i);
								i--;
							}
						}
					}

					progress.increaseProgress(1L);
				}

				progress.dispose();
			}
		}.start();
		progress.setVisible(true);
		progress.isCanceled();
	}
	
	
	//changed by urmi
	//define reps to be all runs in a sample
	public void findReps() {
		System.out.println("Finding replicates");

		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Finding Replicates", "Searching Metadata for Replicates", 0L, metadataRoot.getChildCount(), true);
		new Thread() {
			@Override
			public void run() {
				int groupIndex = 1;
				//get list of groups
				ArrayList<ArrayList<SimpleXMLElement>> sampleGroups = getSampleGroups();
				for (ArrayList<SimpleXMLElement> thisGroup : sampleGroups) {
					while (thisGroup.size() > 0) {
						// ArrayList<Integer> thisRepGroup = new ArrayList<Integer>();
						SimpleXMLElement node = thisGroup.remove(0);
						//name of the rep group
						String name = node.getAttributeValue("name");
						SimpleXMLElement groupNode = createGroupNode(makeGroupName(""), node.getParent());
						node.removeFromParent();
						groupNode.add(node);
						for (int i = 0; i < thisGroup.size(); i++) {
							SimpleXMLElement sibling = thisGroup.get(i);
							String siblingName = sibling.getAttributeValue("name");
							//areReps(name, siblingName) finds reps using name
							/*if (areReps(name, siblingName)) {
								sibling.removeFromParent();
								groupNode.add(sibling);
								thisGroup.remove(i);
								i--;
							}*/
							//add all siblings to group
							if (true) {
								sibling.removeFromParent();
								groupNode.add(sibling);
								thisGroup.remove(i);
								i--;
							}
							
						}
					}

					progress.increaseProgress(1L);
				}

				progress.dispose();
			}
		}.start();
		progress.setVisible(true);
		progress.isCanceled();
	}
	

	public static Integer[] getDiffLocations(String s1, String s2) {
		ArrayList<Integer> diffs = new ArrayList();

		for (int i = 0; (i < s1.length()) && (i < s2.length()); i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				diffs.add(Integer.valueOf(i));
			}
		}

		return diffs.toArray(new Integer[0]);
	}

	public static String getStringDiff(String s1, String s2) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; (i < s1.length()) && (i < s2.length()); i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				result.append(s2.charAt(i));
			} else if (result.length() != 0) {
				result.append(" ");
			}
		}
		if (s1.length() > s2.length()) {
			result.append(s1.substring(s2.length()));
		} else if (s2.length() > s1.length()) {
			result.append(s2.substring(s1.length()));
		}
		return result.toString().trim();
	}

	/**
	 * Checks whether the chip names are repetitive
	 *
	 * @param s1
	 *            <code>String</code> element denoting first chip name
	 * @param s2
	 *            <code>String</code> element denoting second chip name
	 * @return true if it is, false otherwise
	 */
	public static boolean areReps(String s1, String s2) {
		// TODO: If difference is ", biological rep", match
		String n1 = s1.replaceAll("[\\s\\-\\=_:]|����|퀌�|�\\?�\\?|�\\?�\\?", "").toLowerCase();
		n1 = Utils.removeExtendedChars(n1);
		String n2 = s2.replaceAll("[\\s\\-\\=_:]|����|퀌�|�\\?�\\?|�\\?�\\?", "").toLowerCase();
		n2 = Utils.removeExtendedChars(n2);
		n1 = n1.replaceAll("ath1", "").replaceAll("^atgen\\d+", "");
		n2 = n2.replaceAll("ath1", "").replaceAll("^atgen\\d+", "");
		String diff = getStringDiff(n1, n2);
		Matcher m1 = Pattern.compile("rep(?:licate)?\\d+?").matcher(n1);
		Matcher m2 = Pattern.compile("rep(?:licate)?\\d+?").matcher(n2);
		boolean match = false;
		if (m1.find() && m2.find()) {
			int end1 = n1.lastIndexOf("rep");
			int end2 = n2.lastIndexOf("rep");
			String name1 = n1.substring(0, end1);
			String name2 = n2.substring(0, end2);
			String num1 = n1.substring(end1);
			String num2 = n2.substring(end2);
			String numDiff = getStringDiff(num1, num2);
			String nameDiff = getStringDiff(name1, name2);
			if (numDiff.length() > 0) {
				// TODO Problem: if chip names differ after the rep number, they
				// still count as reps. Rare, but possible.
				// Other problems: (ATGE_100_A, ATGE_100_B, ATGE_100_C) and
				// (AtGen_6-9621_Heatstress(3h)+21hrecovery-Roots-24.0h_Rep1,
				// AtGen_6-9622_Heatstress(3h)+21hrecovery-Roots-24.0h_Rep2)
				if (name1.equals(name2)) {
					match = true;
				} else {
					try {
						// System.out.println(name1+" - "+name2+" = "+nameDiff);
						Integer.parseInt(nameDiff);
						// Problem: "something 0h rep 1" matches
						// "something 4h rep 1"
						// But: "Yang_1-1_WT(COL)-1_Rep1_ATH1" should match
						// "Yang_1-2_WT(COL)-2_Rep2_ATH1"
						match = true;
						Integer[] diffs = getDiffLocations(n1, n2);
						for (int d : diffs) {
							if (d + 1 >= name1.length() && d + 1 >= name2.length()) {
								continue;
							}
							if (n1.length() >= n2.length()) {
								if (n1.charAt(d + 1) == 'h' || n1.charAt(d + 1) == 'd') {
									match = false;
								}
							} else {
								if (n2.charAt(d + 1) == 'h' || n2.charAt(d + 1) == 'd') {
									match = false;
								}
							}
							if (d - 1 < 0)
								continue;

							if (!(Character.isDigit(n1.charAt(d - 1)) && Character.isDigit(n2.charAt(d - 1)))) {
								match = false;
							}
						}
					} catch (NumberFormatException nfe) {
					}
				}
			}
		} else if (n1.substring(0, n1.length() - 1).equals(n2.substring(0, n2.length() - 1))) {
			match = true;
			Integer[] diffs = getDiffLocations(n1, n2);
			for (int d : diffs) {
				if (d + 1 >= n1.length() && d + 1 >= n2.length())
					continue;

				if (n1.length() >= n2.length()) {
					if (n1.charAt(d + 1) == 'h' || n1.charAt(d + 1) == 'd') {
						match = false;
					}
				} else {
					if (n2.charAt(d + 1) == 'h' || n2.charAt(d + 1) == 'd') {
						match = false;
					}
				}
			}
			// } else if (diff.length() == 1) {
			// match = true;
		} else if (diff.length() == 1) {
			try {
				Integer.parseInt(diff);
				match = true;
				Integer[] diffs = getDiffLocations(n1, n2);
				for (int d : diffs) {
					if (d + 1 >= n1.length() && d + 1 >= n2.length())
						continue;

					if (n1.length() >= n2.length()) {
						if (n1.charAt(d + 1) == 'h' || n1.charAt(d + 1) == 'd') {
							match = false;
						}
					} else {
						if (n2.charAt(d + 1) == 'h' || n2.charAt(d + 1) == 'd') {
							match = false;
						}
					}
				}
			} catch (NumberFormatException nfe) {
			}
		}
		return match;
	}

	public static String makeGroupName(String sampleName) {
		String result = sampleName.replaceAll("(?i)[_.]?replicate\\W*\\d+\\z|[_.]?rep\\W*\\d+\\z", "");
		if (!result.equals(sampleName)) {
			return Utils.superClean(result);
		}
		Pattern regex = Pattern.compile(".*(?=\\W?\\d$)", 194);

		Matcher regexMatcher = regex.matcher(sampleName);
		regexMatcher.find();
		try {
			result = regexMatcher.group();
		} catch (Exception localException) {
		}

		if (result != null) {
			return Utils.superClean(result);
		}
		return Utils.superClean(sampleName);
	}

	public SimpleXMLElement createExperimentNode(String name) {
		SimpleXMLElement newNode = new SimpleXMLElement("Experiment");
		newNode.setAttribute("name", name);
		metadataRoot.add(newNode);
		return newNode;
	}

	public SimpleXMLElement createSampleNode(String name, SimpleXMLElement parentNode) {
		if ((!"Experiment".equals(parentNode.getName())) && (!"Group".equals(parentNode.getName()))
				&& (!parentNode.isRoot())) {
			throw new IllegalArgumentException("Parent node must be an Experiment or Group node");
		}
		SimpleXMLElement newNode = new SimpleXMLElement("Sample");
		newNode.setAttribute("name", name);
		parentNode.add(newNode);
		return newNode;
	}

	// parent node should now be OuterSamp UPDATED 2/10/2018 by urmi
	// see MetadaUpdater.java
	// for older project parent node is experiment
	public SimpleXMLElement createGroupNode(String name, SimpleXMLElement parentExp) {
		if ((!("OuterSamp".equals(parentExp.getName()) || "Experiment".equals(parentExp.getName())))
				&& (!parentExp.isRoot())) {
			throw new IllegalArgumentException("Parent node must be an OuterSamp or Experiment node");
		}

		SimpleXMLElement newNode = new SimpleXMLElement("Group");
		newNode.setAttribute("name", name);
		parentExp.add(newNode);
		return newNode;
	}

	public void clearRepGroups() {
		clearRepGroups(metadataRoot);
	}

	public void clearRepGroups(SimpleXMLElement root) {
		if ("Group".equals(root.getName())) {
			SimpleXMLElement parent = root.getParent();
			for (int i = 0; i < root.getChildCount(); i++) {
				SimpleXMLElement child = root.getChildAt(i);
				if ("Sample".equals(child.getName())) {
					child.removeFromParent();
					parent.add(child);
				}
			}
			root.removeFromParent();
		} else {
			for (int i = 0; i < root.getChildCount(); i++) {
				if (root.getChildAt(i).getName().equals("Group")) {
					clearRepGroups(root.getChildAt(i));
					i--;
				} else {
					clearRepGroups(root.getChildAt(i));
				}
			}
		}
	}

	public void addMetadata(String field, String value, SimpleXMLElement parentNode) {
		if ("md".equals(parentNode.getName())) {
			throw new IllegalArgumentException("Parent node must be one of Experiment, Sample, Group, or the root");
		}
		SimpleXMLElement newNode = new SimpleXMLElement("md");
		newNode.setAttribute("field", field);
		newNode.setAttribute("value", value);
		parentNode.add(newNode);
	}

	public void setColumnForSample(SimpleXMLElement sampleNode, int col) {
		if (!"Sample".equals(sampleNode.getName())) {
			throw new IllegalArgumentException("Node must be a sample node");
		}
		sampleNode.setAttribute("col", col + "");
		knownCols.put(Integer.valueOf(col), sampleNode);
	}

	public boolean hasRepGroups() {
		return hasRepGroups(metadataRoot);
	}

	public boolean hasRepGroups(SimpleXMLElement root) {
		if ("Group".equals(root.getName())) {
			return true;
		}
		for (int i = 0; i < root.getChildCount(); i++) {
			SimpleXMLElement child = root.getChildAt(i);
			if (hasRepGroups(child)) {
				return true;
			}
		}
		return false;
	}

	public List<RepGroup> getRepGroups() {
		return getRepGroups(metadataRoot);
	}

	public List<RepGroup> getRepGroups(SimpleXMLElement root) {
		ArrayList<RepGroup> result = new ArrayList();
		if ("Group".equals(root.getName())) {
			// JOptionPane.showMessageDialog(null,"this rootgetname:"+root.getName());
			RepGroup thisGroup = new RepGroup();
			thisGroup.name = root.getAttributeValue("name");
			thisGroup.cols = new ArrayList();
			// JOptionPane.showMessageDialog(null,"c count:"+root.getChildCount());
			scanForCols(this.getXMLRoot());
			for (int i = 0; i < root.getChildCount(); i++) {
				SimpleXMLElement child = root.getChildAt(i);

				String thisCol = child.getAttributeValue("col"); // gives null
				// get col name other method
				// String []tempnames=myChartPanel.getSampleNames();
				// MetaOmGraph.getActiveProject().
				// for(int t=0;t<tempnames.length;tempnames++) {

				// }
				// get the col number from header
				int temp = MetaOmGraph.getActiveProject().findDataColumnHeader(child.getAttributeValue("name"));
				// When run is not found as it is extra in metadata or in unknown col then
				// ignore it
				if (temp == -1) {
					thisCol = "";
				} else {
					thisCol = Integer.toString(temp);
				}

				// JOptionPane.showMessageDialog(null,"thisCol:"+child.getAttributeValue("col"));
				// JOptionPane.showMessageDialog(null,"thisCol
				// name:"+child.getAttributeValue("name"));
				// JOptionPane.showMessageDialog(null,"thisCol name:"+child.toFullString());
				if ((thisCol != null) && (!"".equals(thisCol))) {
					thisGroup.cols.add(new Integer(thisCol));
				}
			}
			if (thisGroup.cols.size() > 0) {
				result.add(thisGroup);
			}
		} else {
			for (int i = 0; i < root.getChildCount(); i++) {
				// JOptionPane.showMessageDialog(null,"this child
				// rootgetname:"+root.getChildAt(i));
				result.addAll(getRepGroups(root.getChildAt(i)));
			}
		}
		return result;
	}

	public void associate(SimpleXMLElement node, Integer col) {
		if ((col == null) && (node.getAttributeValue("col") != null)) {
			Integer oldCol = new Integer(node.getAttributeValue("col"));
			knownCols.remove(oldCol);
			node.setAttribute("col", null);
			return;
		}
		if (knownCols.containsKey(col)) {
			SimpleXMLElement oldNode = knownCols.get(col);
			knownCols.remove(col);
			oldNode.setAttribute("col", null);
		}
		knownCols.put(col, node);
		node.setAttribute("col", col + "");
	}

	public static class RepGroup {
		String name;
		List<Integer> cols;

		public RepGroup() {
		}
	}
}
