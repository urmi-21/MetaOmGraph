
package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.chart.NewCustomSortDialog;
import edu.iastate.metnet.metaomgraph.chart.NewCustomSortDialog.CustomSortObject;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.DisplayMetadataEditor;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel;
import edu.iastate.metnet.metaomgraph.ui.MetadataEditor;
import edu.iastate.metnet.metaomgraph.ui.MetadataTreeDisplayPanel;
import edu.iastate.metnet.metaomgraph.ui.Metadataviewer;
import edu.iastate.metnet.metaomgraph.ui.ParseTableTree;
import edu.iastate.metnet.metaomgraph.ui.ReadMetadata;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
//import edu.iastate.metnet.metaomgraph.ui.CorrelationMetaTable.DecimalFormatRenderer;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel.QuerySet;
import edu.iastate.metnet.metaomgraph.utils.MetNetUtils;
import edu.iastate.metnet.metaomgraph.utils.MetadataUpdater;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
//import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.org.apache.xml.internal.security.Init;

public class MetaOmProject {
	public static final String COMPLETE_LIST = "Complete List";
	public static final String LIST_CREATE_CAUSE = "create list";
	public static final String LIST_DELETE_CAUSE = "delete list";
	public static final String LIST_RENAME_CAUSE = "rename list";
	public static final String NEW_CORRELATION_CAUSE = "new correlation";
	public static final String KEEP_CORRELATION_CAUSE = "keep correlation";
	public static final String ROW_NAME_CHANGE_CAUSE = "row name change";
	public static final String DELETE_INFO_COLUMN_CAUSE = "info column deleted";
	private String[] columnHeaders;
	private Object[][] rowNames;
	private int infoColumns;
	private Long[] fileIndex;
	private File source = null;

	private boolean changed;

	private Hashtable<String, int[]> geneLists;

	private String defaultTitle;

	private String defaultXAxis;

	private String defaultYAxis;

	private Color color1;

	private Color color2;

	private char delimiter;

	// urmi
	private char metadatadelimiter;

	private int defaultColumn;

	private int maxNameLength;

	private Metadata metadata;
	// urmi new metadata class
	private MetadataHybrid metadataH;

	private RandomAccessFile dataIn;

	private boolean initialized;

	private Hashtable<String, TreeSearchQueryConstructionPanel.QuerySet> savedQueries;

	private Hashtable<String, NewCustomSortDialog.CustomSortObject> savedSorts;

	private Hashtable<String, MetaOmAnalyzer.ExcludeData> savedExcludes;

	private boolean streamMode;

	private double[][] data;

	private boolean allowImport;

	private Vector<ChangeListener> changeListeners;

	private boolean ignoreConsecutiveDelimiters;

	private boolean hasLastCorrelation;

	private Double blankValue;

	private HashMap<Integer, Integer> memoryMap;

	private boolean includeMetNet;

	// urmi
	// metadataCollection oject to read csv file
	// private MetadataCollection obj = null;
	private MetadataEditor editor = null;
	private MetadataTreeStructure mTree = null;
	private ReadMetadata readMetadataframe = null;
	private String dataColumnname = null;

	// save meta analysis corr values as mapping of name to correlation
	private HashMap<String, CorrelationMetaCollection> metaCorrs;
	
	// save differential exp analysis results
	private HashMap<String, DifferentialExpResults> diffExpRes;

	// info column type used to sort data
	private HashMap<String, Class> infoColTypes = null;

	// new constructor to add metadata delimiter
	public MetaOmProject(File source, int infoColumns, char delimiter, char mddelimiter,
			boolean ignoreConsecutiveDelimiters, Double blankValue, boolean includeMetNet) {
		this.source = source;
		this.infoColumns = infoColumns;
		this.delimiter = delimiter;
		this.metadatadelimiter = mddelimiter;
		this.ignoreConsecutiveDelimiters = ignoreConsecutiveDelimiters;
		this.blankValue = blankValue;
		this.includeMetNet = includeMetNet;
		allowImport = true;
		streamMode = false;
		initialized = createProjectFromFile();
	}

	public MetaOmProject(File source, int infoColumns, char delimiter, boolean ignoreConsecutiveDelimiters,
			Double blankValue, boolean includeMetNet) {
		this.source = source;
		this.infoColumns = infoColumns;
		this.delimiter = delimiter;
		this.ignoreConsecutiveDelimiters = ignoreConsecutiveDelimiters;
		this.blankValue = blankValue;
		this.includeMetNet = includeMetNet;
		allowImport = true;
		streamMode = false;
		initialized = createProjectFromFile();
	}

	public MetaOmProject(File source, int infoColumns, char delimiter, boolean ignoreConsecutiveDelimiters,
			Double blankValue) {
		this(source, infoColumns, delimiter, ignoreConsecutiveDelimiters, blankValue, true);
	}

	public MetaOmProject(File projectFile) {
		streamMode = false;
		allowImport = true;
		initialized = openProject(projectFile);

	}

	public MetaOmProject(InputStream instream, int infoColumns, char delimiter) {
		this(instream, infoColumns, delimiter, true);
	}

	public MetaOmProject(InputStream instream, int infoColumns, char delimiter, boolean allowImport) {
		if (instream == null) {
			throw new IllegalArgumentException("Input stream cannot be null!");
		}
		this.infoColumns = infoColumns;
		this.delimiter = delimiter;
		this.allowImport = allowImport;
		streamMode = true;
		initialized = createProjectFromStream(instream);
	}

	private boolean createProjectFromStream(InputStream instream) {
		maxNameLength = 0;
		defaultColumn = 0;
		geneLists = new Hashtable();
		if (getMetadataHybrid() != null) {
			defaultXAxis = getMetadataHybrid().getDataColName();
		} else {
			defaultXAxis = "Data Column";
		}
		defaultYAxis = "Expression level";
		defaultTitle = "";
		color1 = new Color(80, 194, 80);
		color2 = Color.WHITE;
		BufferedReader in = new BufferedReader(new InputStreamReader(instream));
		try {
			String line = in.readLine();
			String[] splitLine = line.split(delimiter + "");
			columnHeaders = splitLine;
			for (int x = 0; x < splitLine.length; x++) {
				if (splitLine[x].length() > maxNameLength) {
					maxNameLength = splitLine[x].length();
				}
			}
			Vector<Object[]> rowNameVector = new Vector();
			Vector<double[]> dataVector = new Vector();
			line = in.readLine();
			boolean hasGeneIDs = false;
			int affyColumn = -1;
			while (line != null) {
				splitLine = line.split(delimiter + "");
				Object[] thisRowNames = new Object[infoColumns];
				double[] thisRowData = new double[splitLine.length - infoColumns];
				for (int x = 0; x < infoColumns; x++) {
					thisRowNames[x] = splitLine[x];
					if ((Utils.isGeneID(thisRowNames[x] + "")) && (!hasGeneIDs)) {
						hasGeneIDs = true;
						affyColumn = x;
					}
				}
				for (int x = 0; x < thisRowData.length; x++) {
					try {
						thisRowData[x] = Double.parseDouble(splitLine[(x + infoColumns)]);
					} catch (NumberFormatException nfe) {
						thisRowData[x] = 0.0D;
					} catch (NullPointerException npe) {
						thisRowData[x] = 0.0D;
					}
				}
				rowNameVector.add(thisRowNames);
				dataVector.add(thisRowData);
				line = in.readLine();
			}
			if (infoColumns <= 0) {
				rowNames = new Object[rowNameVector.size()][1];
				for (int x = 0; x < rowNames.length; x++) {
					rowNames[x][0] = new Integer(x);
				}
			} else {
				rowNames = new Object[rowNameVector.size()][infoColumns];
				for (int x = 0; x < rowNames.length; x++) {
					rowNames[x] = rowNameVector.get(x);
				}
			}
			data = new double[dataVector.size()][columnHeaders.length - infoColumns];
			for (int x = 0; x < data.length; x++) {
				data[x] = dataVector.get(x);
			}
			if ((hasGeneIDs) && (allowImport)) {

				int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
						"It looks like you've imported some gene IDs.\nWould you like to automatically add additional gene information as well?",

						"Gene IDs detected", 0, 3);
				if (result == 0) {
					addMetNetRowData(affyColumn);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		setChanged(true);
		return true;
	}

	/**
	 * This function reads the data file part of MetaOmProject constructor Annotated
	 * by urmi
	 * 
	 * @return
	 */
	private boolean createProjectFromFile() {
		MetaOmGraph.closeWelcomeDialog();

		maxNameLength = 0;

		Vector<Object[]> resultNames = new Vector();
		defaultColumn = 0;
		geneLists = new Hashtable();
		if (getMetadataHybrid() != null) {
			defaultXAxis = getMetadataHybrid().getDataColName();
		} else {
			defaultXAxis = "Data Column";
		}
		defaultYAxis = "Expression level";

		defaultTitle = "";
		color1 = new Color(80, 194, 80);
		color2 = Color.WHITE;

		long fileSize = source.length();

		final BlockingProgressDialog progressWindow = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Parsing",
				"Parsing " + source.getName(), 0L, fileSize / 100, true);
		new Thread() {
			public void run() {
				progressWindow.setVisible(true);

			}

		}.start();
		int geneIDCol = -1;
		boolean hasGeneIDs = false;
		try {
			dataIn = new RandomAccessFile(source.getAbsolutePath(), "r", 20000);

			String tmp = dataIn.readLine();
			StringTokenizer st = new StringTokenizer(tmp, delimiter + "");
			Vector<String> columnList = new Vector();
			int j = 0;
			while (st.hasMoreTokens()) {
				String temp = Utils.clean(st.nextToken());
				if ((temp.length() > maxNameLength) && (j >= infoColumns))
					maxNameLength = temp.length();
				j++;
				columnList.add(temp);
			}
			Object[] tempArray = columnList.toArray();
			System.out.println("columns: " + tempArray.length);

			do {
				long thisLinePointer = dataIn.getFilePointer();

				if (dataIn.peek() != '\n') {
					Object[] thisData = new Object[infoColumns + 1];
					boolean okToAdd = false;

					if (infoColumns == 0) {
						thisLinePointer = dataIn.getFilePointer();
						if ((Utils.clean(dataIn.readString(delimiter, ignoreConsecutiveDelimiters)) != "")
								&& (Utils.clean(dataIn.readString(delimiter, ignoreConsecutiveDelimiters)) != null)) {
							okToAdd = true;
						}
					} else {
						for (int x = 1; x <= infoColumns; x++) {
							thisData[x] = Utils.clean(dataIn.readString(delimiter, ignoreConsecutiveDelimiters));

							if ((thisData[x] != null) && (!thisData[x].equals(""))) {
								okToAdd = true;

								if ((Utils.isGeneID(thisData[x].toString(), true)) && (!hasGeneIDs)) {
									// System.out.println("Found gene id: " + thisData[x]);
									hasGeneIDs = true;
									geneIDCol = x - 1;
								}
							}
						}
					}
					if (okToAdd) {
						if (infoColumns > 0)
							thisLinePointer = dataIn.getFilePointer();
						thisData[0] = new Long(thisLinePointer);
						resultNames.add(thisData);
					}
				}

				progressWindow.setProgress(thisLinePointer / 100);
				if (progressWindow.isCanceled()) {

					dataIn.close();
					try {
						MetaOmGraph.showWelcomeDialog();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
			} while (dataIn.nextLine());
			progressWindow.dispose();

			fileIndex = new Long[resultNames.size()];

			rowNames = new Object[resultNames.size()][infoColumns];
			columnHeaders = new String[tempArray.length];
			for (int x = 0; x < columnHeaders.length; x++) {
				columnHeaders[x] = tempArray[x].toString();
			}
			for (int x = 0; x < resultNames.size(); x++) {
				Object[] thisData = resultNames.get(x);
				fileIndex[x] = ((Long) thisData[0]);
				for (int z = 0; z < rowNames[x].length; z++) {
					rowNames[x][z] = thisData[(z + 1)];
				}
			}
			if (hasGeneIDs) {
			}

			// System.gc();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		} catch (NoSuchElementException nsee) {
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
					source.getName() + " does not appear to be a valid delimited text file!", "Error", 0);
			return false;
		}
		String tmp;
		setChanged(true);
		return true;
	}

	protected void addMetNetRowData(int geneIDCol) {
		System.out.println("Adding metnet stuff at " + geneIDCol);
		String[] idCol = new String[rowNames.length];
		for (int i = 0; i < idCol.length; i++) {
			idCol[i] = rowNames[i][geneIDCol] + "";
		}

		int rowIndex = 0;
		while (Utils.getIDType(rowNames[rowIndex][geneIDCol] + "") == -1)
			rowIndex++;
		int addHere;
		if (Utils.getIDType(rowNames[rowIndex][geneIDCol] + "") == 1) {
			addHere = geneIDCol + 1;
		} else {
			addHere = geneIDCol;
		}
		String[][] metnetInfo = MetNetUtils.getMetNetInfo(idCol);
		for (int i = 0; i < metnetInfo.length; i++) {
			Object[] thisRow = rowNames[i];
			String[] thisInfo = metnetInfo[i];
			Object[] newRow = new Object[thisRow.length + thisInfo.length];
			for (int x = 0; x < addHere; x++)
				newRow[x] = thisRow[x];
			for (int y = 0; y < thisInfo.length; y++)
				newRow[(y + addHere)] = thisInfo[y];
			for (int z = addHere; z < thisRow.length; z++) {
				newRow[(z + thisInfo.length)] = thisRow[z];
			}
			rowNames[i] = newRow;
		}
		int newColCount = metnetInfo[0].length;
		String[] newColNames = new String[columnHeaders.length + newColCount];
		int x;
		for (x = 0; x < addHere; x++) {
			newColNames[x] = columnHeaders[x];
		}
		if (newColCount == 4) {
			newColNames[(x++)] = "Locus ID";
		}
		newColNames[(x++)] = "Gene Name";
		newColNames[(x++)] = "Regulon";
		newColNames[(x++)] = "Pathways";
		for (; x - newColCount < columnHeaders.length; x++) {
			newColNames[x] = columnHeaders[(x - newColCount)];
		}

		columnHeaders = newColNames;
		infoColumns += newColCount;

		String[] newInfoCols = new String[infoColumns];
		for (int i = 0; i < newInfoCols.length; i++) {
			newInfoCols[i] = columnHeaders[i];
		}
		setRowNames(rowNames, newInfoCols);
	}

	public boolean saveProject2(File destination) {
		String extension = Utils.getExtension(destination);
		File saveHere;
		if ((extension != null) && ((extension.equals("mog")) || (extension.equals("mcg")))) {
			saveHere = destination;
		} else {
			saveHere = new File(destination.getAbsolutePath() + ".mog");
		}

		if (isCreatedFromStream()) {
			source = new File(saveHere + ".data.txt");
			fileIndex = new Long[this.data.length];
			try {
				RandomAccessFile dataOut = new RandomAccessFile(source, "rw");
				for (int x = 0; x < this.data.length; x++) {
					fileIndex[x] = new Long(dataOut.getFilePointer());
					for (int y = 0; y < this.data[x].length; y++) {
						if (y != 0) {
							dataOut.write(delimiter);
						}
						dataOut.writeBytes(this.data[x][y] + "");
					}
					dataOut.writeBytes(System.getProperty("line.separator"));
				}
				dataOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Element root = new Element("MetaOmProject");
		Element projectInfo = new Element("projectInfo");
		Element sourcePathElement = new Element("sourcePath").setText(source.getParent());
		Element sourceFileElement = new Element("sourceFile").setText(source.getName());
		Element delimiterElement = new Element("delimiter").setText(delimiter + "");
		Element consecutiveElement = new Element("ignoreConsecutiveDelimiters")
				.setText(ignoreConsecutiveDelimiters + "");
		if (delimiter == '\t') {
			delimiterElement.setText("\\t");
		}
		projectInfo.addContent(sourcePathElement);
		projectInfo.addContent(sourceFileElement);
		projectInfo.addContent(delimiterElement);
		projectInfo.addContent(consecutiveElement);
		if (getBlankValue() != null) {
			projectInfo.addContent(new Element("blankValue").setText(getBlankValue() + ""));
		}
		projectInfo.addContent(new Element("xLabel").setText(getDefaultXAxis()));
		projectInfo.addContent(new Element("yLabel").setText(getDefaultYAxis()));
		projectInfo.addContent(new Element("title").setText(getDefaultTitle()));
		projectInfo.addContent(new Element("color1").setText(getColor1().getRGB() + ""));
		projectInfo.addContent(new Element("color2").setText(getColor2().getRGB() + ""));
		projectInfo.addContent(new Element("defaultColumn").setText(defaultColumn + ""));

		for (int x = 0; x < infoColumns; x++) {
			Element infoColumnElement = new Element("infoColumn").setText(columnHeaders[x]);
			projectInfo.addContent(infoColumnElement);
		}
		root.addContent(projectInfo);
		Element columnsElement = new Element("columns");

		for (int x = 0; x < getDataColumnCount(); x++) {
			Element column = new Element("column").setText(getDataColumnHeader(x));
			columnsElement.addContent(column);
		}
		root.addContent(columnsElement);

		boolean savedLast = false;
		for (int x = 0; x < rowNames.length; x++) {
			Element data = new Element("data");
			for (int y = 0; y < infoColumns; y++) {
				Element info;
				if (rowNames[x][y] == null) {
					info = new Element("info").setText("");

				} else {
					info = new Element("info").setText(rowNames[x][y].toString());
					if ((rowNames[x][y] instanceof CorrelationValue)) {
						info.setAttribute("type", "correlation");
						if ((hasLastCorrelation()) && (!savedLast)) {
							info.setAttribute("last", "true");
							savedLast = true;
						}
						if (!((CorrelationValue) rowNames[x][y]).isAsPercent()) {
							info.setAttribute("asPercent", "false");
						}
					}
				}
				data.addContent(info);
			}
			Element location = new Element("location").setText(fileIndex[x] + "");
			data.addContent(location);
			root.addContent(data);
		}

		Enumeration enumer = geneLists.keys();

		while (enumer.hasMoreElements()) {
			String name = enumer.nextElement().toString();
			Element list = new Element("list").setAttribute("name", name);
			int[] entries = geneLists.get(name);
			for (int x = 0; x < entries.length; x++)
				list.addContent(new Element("entry").setText(entries[x] + ""));
			root.addContent(list);
		}
		if (savedSorts != null) {
			enumer = savedSorts.keys();
			while (enumer.hasMoreElements()) {
				String name = enumer.nextElement().toString();
				NewCustomSortDialog.CustomSortObject cso = getSavedSorts().get(name);
				Element sort = cso.toXML().setAttribute("name", name);
				root.addContent(sort);
			}
		}
		if (savedQueries != null) {
			enumer = savedQueries.keys();
			while (enumer.hasMoreElements()) {
				String name = enumer.nextElement().toString();
				TreeSearchQueryConstructionPanel.QuerySet thisQuerySet = getSavedQueries().get(name);
				Element query = thisQuerySet.toXML().setAttribute("name", name).toJDOMElement();
				root.addContent(query);
			}
		}
		if (savedExcludes != null) {
			enumer = savedExcludes.keys();
			while (enumer.hasMoreElements()) {
				String name = enumer.nextElement().toString();
				MetaOmAnalyzer.ExcludeData thisData = savedExcludes.get(name);
				Element exclude = thisData.toXML().setAttribute("name", name);
				root.addContent(exclude);
			}
		}

		// write metadata
		XMLOutputter output = new XMLOutputter();
		output.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));
		Document myDoc = new Document(root);

		try {
			ZipOutputStream myZipOut = new ZipOutputStream(new FileOutputStream(saveHere));
			myZipOut.putNextEntry(new ZipEntry("ProjectFile.xml"));
			output.output(myDoc, myZipOut);
			myZipOut.closeEntry();
			if (metadata != null) {
				myZipOut.putNextEntry(new ZipEntry("metadata.xml"));
				metadata.outputToStream(myZipOut);
			}
			myZipOut.closeEntry();
			myZipOut.finish();
			myZipOut.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			JOptionPane.showInternalMessageDialog(MetaOmGraph.getDesktop(),
					"Unable to save the project file.  Make sure the destination file is not write-protected.",
					"Error saving project", 0);
			return false;
		}
		setChanged(false);
		return true;
	}

	/**
	 * @author urmi new save project
	 * @param destination
	 * @return
	 */
	public boolean saveProject(File destination) {
		String extension = Utils.getExtension(destination);
		File saveHere;
		if ((extension != null) && ((extension.equals("mog")) || (extension.equals("mcg")))) {
			saveHere = destination;
		} else {
			saveHere = new File(destination.getAbsolutePath() + ".mog");
		}

		if (isCreatedFromStream()) {
			source = new File(saveHere + ".data.txt");
			fileIndex = new Long[this.data.length];
			try {
				RandomAccessFile dataOut = new RandomAccessFile(source, "rw");
				for (int x = 0; x < this.data.length; x++) {
					fileIndex[x] = new Long(dataOut.getFilePointer());
					for (int y = 0; y < this.data[x].length; y++) {
						if (y != 0) {
							dataOut.write(delimiter);
						}
						dataOut.writeBytes(this.data[x][y] + "");
					}
					dataOut.writeBytes(System.getProperty("line.separator"));
				}
				dataOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Element root = new Element("MetaOmProject");
		Element projectInfo = new Element("projectInfo");
		Element sourcePathElement = new Element("sourcePath").setText(source.getParent());
		Element sourceFileElement = new Element("sourceFile").setText(source.getName());
		Element delimiterElement = new Element("delimiter").setText(delimiter + "");
		Element consecutiveElement = new Element("ignoreConsecutiveDelimiters")
				.setText(ignoreConsecutiveDelimiters + "");
		if (delimiter == '\t') {
			delimiterElement.setText("\\t");
		}
		projectInfo.addContent(sourcePathElement);
		projectInfo.addContent(sourceFileElement);
		projectInfo.addContent(delimiterElement);
		projectInfo.addContent(consecutiveElement);
		if (getBlankValue() != null) {
			projectInfo.addContent(new Element("blankValue").setText(getBlankValue() + ""));
		}
		projectInfo.addContent(new Element("xLabel").setText(getDefaultXAxis()));
		projectInfo.addContent(new Element("yLabel").setText(getDefaultYAxis()));
		projectInfo.addContent(new Element("title").setText(getDefaultTitle()));
		projectInfo.addContent(new Element("color1").setText(getColor1().getRGB() + ""));
		projectInfo.addContent(new Element("color2").setText(getColor2().getRGB() + ""));
		projectInfo.addContent(new Element("defaultColumn").setText(defaultColumn + ""));

		for (int x = 0; x < infoColumns; x++) {
			Element infoColumnElement = new Element("infoColumn").setText(columnHeaders[x]);
			projectInfo.addContent(infoColumnElement);
		}
		root.addContent(projectInfo);
		Element columnsElement = new Element("columns");

		for (int x = 0; x < getDataColumnCount(); x++) {
			Element column = new Element("column").setText(getDataColumnHeader(x));
			columnsElement.addContent(column);
		}
		root.addContent(columnsElement);

		boolean savedLast = false;
		for (int x = 0; x < rowNames.length; x++) {
			Element data = new Element("data");
			for (int y = 0; y < infoColumns; y++) {
				Element info;
				if (rowNames[x][y] == null) {
					info = new Element("info").setText("");

				} else {
					info = new Element("info").setText(rowNames[x][y].toString());
					if ((rowNames[x][y] instanceof CorrelationValue)) {
						info.setAttribute("type", "correlation");
						if ((hasLastCorrelation()) && (!savedLast)) {
							info.setAttribute("last", "true");
							savedLast = true;
						}
						if (!((CorrelationValue) rowNames[x][y]).isAsPercent()) {
							info.setAttribute("asPercent", "false");
						}
					}
				}
				data.addContent(info);
			}
			Element location = new Element("location").setText(fileIndex[x] + "");
			data.addContent(location);
			root.addContent(data);
		}

		Enumeration enumer = geneLists.keys();

		while (enumer.hasMoreElements()) {
			String name = enumer.nextElement().toString();
			Element list = new Element("list").setAttribute("name", name);
			int[] entries = geneLists.get(name);
			for (int x = 0; x < entries.length; x++)
				list.addContent(new Element("entry").setText(entries[x] + ""));
			root.addContent(list);
		}
		if (savedSorts != null) {
			enumer = savedSorts.keys();
			while (enumer.hasMoreElements()) {
				String name = enumer.nextElement().toString();
				NewCustomSortDialog.CustomSortObject cso = getSavedSorts().get(name);
				Element sort = cso.toXML().setAttribute("name", name);
				root.addContent(sort);
			}
		}
		if (savedQueries != null) {
			enumer = savedQueries.keys();
			while (enumer.hasMoreElements()) {
				String name = enumer.nextElement().toString();
				TreeSearchQueryConstructionPanel.QuerySet thisQuerySet = getSavedQueries().get(name);
				Element query = thisQuerySet.toXML().setAttribute("name", name).toJDOMElement();
				root.addContent(query);
			}
		}
		if (savedExcludes != null) {
			enumer = savedExcludes.keys();
			while (enumer.hasMoreElements()) {
				String name = enumer.nextElement().toString();
				MetaOmAnalyzer.ExcludeData thisData = savedExcludes.get(name);
				Element exclude = thisData.toXML().setAttribute("name", name);
				root.addContent(exclude);
			}
		}

		XMLOutputter output = new XMLOutputter();
		output.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));
		Document myDoc = new Document(root);

		try {
			ZipOutputStream myZipOut = new ZipOutputStream(new FileOutputStream(saveHere));
			myZipOut.putNextEntry(new ZipEntry("ProjectFile.xml"));
			output.output(myDoc, myZipOut);
			myZipOut.closeEntry();
			if (this.getMetadataHybrid() != null) {
				// write metadata
				myZipOut.putNextEntry(new ZipEntry("metadataFile.xml"));
				// metadata.outputToStream(myZipOut);
				Document mdFileinfo = this.getMetadataHybrid().generateFileInfo();
				output.output(mdFileinfo, myZipOut);

				// write removed cols from md file
				myZipOut.putNextEntry(new ZipEntry("removedMDCols.xml"));
				Document removedColsMD = new Document();
				removedColsMD.setRootElement(
						this.getMetadataHybrid().listToXML(this.getMetadataHybrid().getRemovedMDCols()));
				output.output(removedColsMD, myZipOut);

				// write exluded and missing rows from metadata
				myZipOut.putNextEntry(new ZipEntry("excludedMD.xml"));
				Document excludedMD = new Document();
				excludedMD.setRootElement(
						this.getMetadataHybrid().listToXML(this.getMetadataHybrid().getExcludedMDRows()));
				output.output(excludedMD, myZipOut);

				myZipOut.putNextEntry(new ZipEntry("missingMD.xml"));
				Document missingMD = new Document();
				missingMD.setRootElement(
						this.getMetadataHybrid().listToXML(this.getMetadataHybrid().getMissingMDRows()));
				output.output(missingMD, myZipOut);

				// write tree
				myZipOut.putNextEntry(new ZipEntry("metadataTree.xml"));
				Document treeStruct = new Document();
				treeStruct.setRootElement(
						this.getMetadataHybrid().jtreetoXML(this.getMetadataHybrid().getTreeStucture()));
				output.output(treeStruct, myZipOut);

				// write saved correlations
				myZipOut.putNextEntry(new ZipEntry("correlations.xml"));
				Document corrs = new Document();
				corrs.setRootElement(getMetaCorrResasXML());
				output.output(corrs, myZipOut);

				// write MOG parameters
				myZipOut.putNextEntry(new ZipEntry("params.xml"));
				Document params = new Document();
				params.setRootElement(getParamsasXML());
				output.output(params, myZipOut);

			}
			myZipOut.closeEntry();
			myZipOut.finish();
			myZipOut.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			JOptionPane.showInternalMessageDialog(MetaOmGraph.getDesktop(),
					"Unable to save the project file.  Make sure the destination file is not write-protected.",
					"Error saving project", 0);
			return false;
		}
		setChanged(false);

		return true;
	}

	/**
	 * @author urmi Initialize column type to sort numbers correctly.
	 */
	public void initColTypes() {

		String[] infoColNames = getInfoColumnNames();
		MetaOmTablePanel tab = MetaOmGraph.getActiveTable();
		if (tab == null) {
			JOptionPane.showMessageDialog(null, "Can't set column type. Table is NULL!!");
			return;
		}
		// set default coltypes
		boolean[] isNumber = new boolean[infoColNames.length];
		for (int i = 0; i < infoColNames.length; i++) {
			// check if current infoCol could be a number
			isNumber[i] = true;
			int min = getRowCount();
			for (int j = 0; j < min; j++) {
				try {
					Double.parseDouble(tab.getMainTableItemat(j, i));

				} catch (NumberFormatException | NullPointerException e) {
					isNumber[i] = false;
					break;
				}
			}
		}
		HashMap<String, Class> map = new HashMap<>();
		for (int i = 0; i < infoColNames.length; i++) {
			if (!isNumber[i]) {
				map.put(infoColNames[i], String.class);
			} else {
				// JOptionPane.showMessageDialog(null, "dbl:"+infoColNames[i]);
				map.put(infoColNames[i], double.class);
			}
		}
		setInfoColTypes(map);

	}

	/**
	 * open a new project edited: urmi
	 * 
	 * @author
	 * 
	 * @param projectFile
	 * @return
	 */
	private boolean openProject(File projectFile) {
		if (!projectFile.exists()) {
			return false;
		}
		boolean allsWell = true;
		boolean projectFileFound = false;
		boolean extendedFound = false;
		boolean treeFound = false;
		boolean corrFound = false;
		boolean paramsFound = false;
		boolean excludedFound = false;
		boolean missingFound = false;
		boolean removedMDColsFound = false;
		BufferedReader inputReader;
		StringBuilder sb;
		String inline;
		SAXBuilder builder;
		XMLOutputter outter;
		JTree tree = null;
		MetadataCollection newcollection = null;
		List<String> excluded = null;
		List<String> missing = null;
		List<String> removedMDCols = null;
		try {

			ZipInputStream instream = new ZipInputStream(new FileInputStream(projectFile));
			ZipEntry thisEntry = instream.getNextEntry();
			while ((allsWell) && (thisEntry != null)) {
				if ((thisEntry.getName().equals("ProjectFile.xml")) && (!projectFileFound)) {

					allsWell = loadProjectFile(instream, projectFile);
					if (!allsWell) {
						System.out.println("Failure at project file load");
					}
					projectFileFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("metadataFile.xml")) && (!extendedFound)) {
					// JOptionPane.showMessageDialog(MetaOmGraph.getDesktop(),"MetaOmGraph needs to
					// update this project's metadata. Please be patient, this may take a while.",
					// "Metadata Update", 1);
					// read xml file
					inputReader = new BufferedReader(new InputStreamReader(instream));
					sb = new StringBuilder();
					inline = "";
					while ((inline = inputReader.readLine()) != null) {
						sb.append(inline);
					}
					builder = new SAXBuilder();
					Document mdFileInfo = (Document) builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
					outter = new XMLOutputter();
					outter.setFormat(Format.getPrettyFormat());
					// org.jdom.Document res = new org.jdom.Document();
					// res.setRootElement(XMLroot);
					// String resDoc = outter.outputString(mdFileInfo);
					// JOptionPane.showMessageDialog(null, resDoc);
					// get data from file
					String fpath = "";
					String delim = "";
					String datacol = "";
					Element root = mdFileInfo.getRootElement();
					int nc = root.getChildren().size();
					for (int i = 0; i < nc; i++) {
						Element thisC = (Element) root.getChildren().get(i);
						if (thisC.getName().equals("FILEPATH")) {
							fpath = thisC.getAttributeValue("name").toString();
							// JOptionPane.showMessageDialog(null, "fp:"+fpath);
						} else if (thisC.getName().equals("DELIMITER")) {
							delim = thisC.getAttributeValue("name").toString();
							// JOptionPane.showMessageDialog(null, "del:"+delim);
						}
						if (thisC.getName().equals("DATACOL")) {
							datacol = thisC.getAttributeValue("name").toString();
							// JOptionPane.showMessageDialog(null, "dc:"+datacol);
						}
					}

					// read mogcollection obj
					// change path according to OS
					if (MetaOmGraph.getOsName().indexOf("win") >= 0 || MetaOmGraph.getOsName().indexOf("Win") >= 0) {
						fpath = FilenameUtils.separatorsToWindows(fpath);
					} else {
						fpath = FilenameUtils.separatorsToUnix(fpath);
					}

					File mdFile = new File(fpath);
					if (mdFile.exists()) {
						newcollection = new MetadataCollection(fpath, delim, datacol);
					} else {

						// try only file name in current directory
						String thisName = mdFile.getName();
						String projFilePath = projectFile.getAbsolutePath().substring(0,
								projectFile.getAbsolutePath().lastIndexOf(File.separator));
						fpath = projFilePath + File.separator + thisName;
						mdFile = new File(fpath);
						// JOptionPane.showMessageDialog(null, "New file path:" +
						// mdFile.getAbsolutePath());
						if (mdFile.exists()) {
							newcollection = new MetadataCollection(fpath, delim, datacol);
						} else {
							JOptionPane.showMessageDialog(null, "Please locate the metadata file. Click OK. "+mdFile.getName());
							JFileChooser fChooser = new JFileChooser(
									edu.iastate.metnet.metaomgraph.utils.Utils.getLastDir());
							int rVal = fChooser.showOpenDialog(MetaOmGraph.getMainWindow());
							if (rVal == JFileChooser.APPROVE_OPTION) {
								File source = fChooser.getSelectedFile();
								newcollection = new MetadataCollection(source.getAbsolutePath(), delim, datacol);

							}

						}
					}

					// JOptionPane.showMessageDialog(null, "datacol"+newcollection.getDatacol());
					// this.setMogcollection(newcollection);

					extendedFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("metadataTree.xml")) && (!treeFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					sb = new StringBuilder();
					inline = "";
					while ((inline = inputReader.readLine()) != null) {
						sb.append(inline);
					}
					builder = new SAXBuilder();
					Document mdTreeStruc = (Document) builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
					outter = new XMLOutputter();
					outter.setFormat(Format.getPrettyFormat());
					// org.jdom.Document res = new org.jdom.Document();
					// res.setRootElement(XMLroot);
					// String resDoc = outter.outputString(mdTreeStruc);
					// JOptionPane.showMessageDialog(null, resDoc);
					// call parse object
					if (newcollection == null) {
						// JOptionPane.showMessageDialog(null, "returning");
						return false;
					}
					tree = new JTree();
					// get Jtree structure
					Element xmlRoot = mdTreeStruc.getRootElement();
					tree.setModel(xmltoJtree(xmlRoot));
					// JOptionPane.showMessageDialog(null, "model created:"+xmlRoot.toString());
					// JOptionPane.showMessageDialog(null,
					// "colhed"+Arrays.toString(this.getDataColumnHeaders()));
					treeFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("correlations.xml")) && (!corrFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					sb = new StringBuilder();
					inline = "";
					while ((inline = inputReader.readLine()) != null) {
						sb.append(inline);
					}
					builder = new SAXBuilder();
					Document corr = (Document) builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
					Element xmlRoot = corr.getRootElement();
					// load data into corrmeta objects
					// each element under root is a coormetacollection and each "corr" element is a
					// List of corrmeta objects
					List corrList = xmlRoot.getChildren();
					for (int i = 0; i < corrList.size(); i++) {
						Element thisCorrElement = (Element) corrList.get(i);
						String thisCorrName = thisCorrElement.getAttributeValue("name");
						int thisCorrtype = Integer.parseInt(thisCorrElement.getAttributeValue("corrtype"));
						String thisCorrModel = thisCorrElement.getAttributeValue("corrmodel");
						String thisCorrVar = thisCorrElement.getAttributeValue("corrvar");
						// for MI
						int thisCorrBins = -1;
						int thisCorrOrder = -1;
						if (thisCorrtype == 3) {
							thisCorrBins = Integer.parseInt(thisCorrElement.getAttributeValue("bins"));
							thisCorrOrder = Integer.parseInt(thisCorrElement.getAttributeValue("order"));
						}

						// create CorrelationMeta objects for each entry
						List rowList = thisCorrElement.getChildren();
						List<CorrelationMeta> correlationMetaList = new ArrayList<>();
						for (int j = 0; j < rowList.size(); j++) {
							Element thisRowElement = (Element) rowList.get(j);
							CorrelationMeta temp = null;
							String thisRowName = thisRowElement.getAttributeValue("name");
							double thisRowVal = Double.parseDouble(thisRowElement.getAttributeValue("value"));
							double thisRowPval = Double.parseDouble(thisRowElement.getAttributeValue("pvalue"));
							if (thisCorrtype == 0) {

								double thisRowZval = Double.parseDouble(thisRowElement.getAttributeValue("zval"));
								double thisRowQval = Double.parseDouble(thisRowElement.getAttributeValue("qval"));
								double thisRowPooledzr = Double
										.parseDouble(thisRowElement.getAttributeValue("pooledzr"));
								double thisRowstdErr = Double.parseDouble(thisRowElement.getAttributeValue("stderr"));
								temp = new CorrelationMeta(thisRowVal, thisRowPval, thisRowZval, thisRowQval,
										thisRowPooledzr, thisRowstdErr);

							} else {
								temp = new CorrelationMeta(thisRowVal, thisRowPval);
							}
							// add to list
							temp.settargetName(thisRowName);
							correlationMetaList.add(temp);
						}

						// create CorrelationMetaCollection obj and add to myProject
						CorrelationMetaCollection cmcObj = null;
						if (thisCorrtype == 3) {
							cmcObj = new CorrelationMetaCollection(thisCorrName, thisCorrtype, thisCorrModel,
									thisCorrVar, correlationMetaList, thisCorrBins, thisCorrOrder);
						} else {
							cmcObj = new CorrelationMetaCollection(thisCorrName, thisCorrtype, thisCorrModel,
									thisCorrVar, correlationMetaList);
						}
						addMetaCorrRes(thisCorrName, cmcObj);

					}
					corrFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("params.xml")) && (!paramsFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					sb = new StringBuilder();
					inline = "";
					while ((inline = inputReader.readLine()) != null) {
						sb.append(inline);
					}
					builder = new SAXBuilder();
					Document params = (Document) builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
					Element xmlRoot = params.getRootElement();
					// read program parameters
					List<Element> clist = xmlRoot.getChildren();
					for (int i = 0; i < clist.size(); i++) {

						Element thisElement = clist.get(i);
						String thisElementName = thisElement.getName();
						// JOptionPane.showMessageDialog(null, "n:"+thisElementName);
						if (thisElementName.equals("permutations")) {
							MetaOmGraph.setNumPermutations(Integer.parseInt(thisElement.getAttributeValue("value")));
						} else if (thisElementName.equals("threads")) {
							MetaOmGraph.setNumThreads(Integer.parseInt(thisElement.getAttributeValue("value")));
						} /*
							 * else if (thisElementName.equals("rpath")) { if
							 * (thisElement.getAttributeValue("default").equals("false")) {
							 * MetaOmGraph.defaultRpath = false;
							 * MetaOmGraph.setUserRPath(thisElement.getAttributeValue("value")); } else {
							 * MetaOmGraph.defaultRpath = true; } } else if
							 * (thisElementName.equals("pathtorfiles")) {
							 * MetaOmGraph.setpathtoRscrips(thisElement.getAttributeValue("value")); }
							 */ else if (thisElementName.equals("hyperlinksCols")) {
							// these values will be passed to MetadataTableDisplayPanel once the object is
							// created
							MetaOmGraph._SRR = Integer.parseInt(thisElement.getAttributeValue("srrColumn"));
							MetaOmGraph._SRP = Integer.parseInt(thisElement.getAttributeValue("srpColumn"));
							MetaOmGraph._SRX = Integer.parseInt(thisElement.getAttributeValue("srxColumn"));
							MetaOmGraph._SRS = Integer.parseInt(thisElement.getAttributeValue("srsColumn"));
							MetaOmGraph._GSE = Integer.parseInt(thisElement.getAttributeValue("gseColumn"));
							MetaOmGraph._GSM = Integer.parseInt(thisElement.getAttributeValue("gsmColumn"));
						}
					}

					paramsFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("excludedMD.xml")) && (!excludedFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					sb = new StringBuilder();
					inline = "";
					while ((inline = inputReader.readLine()) != null) {
						sb.append(inline);
					}
					builder = new SAXBuilder();
					Document doc = (Document) builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
					Element xmlRoot = doc.getRootElement();
					// read program parameters
					List<Element> clist = xmlRoot.getChildren();
					excluded = new ArrayList<>();
					for (int i = 0; i < clist.size(); i++) {
						Element thisElement = clist.get(i);
						String thisElementName = thisElement.getName();
						excluded.add(thisElementName);
					}

					excludedFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("missingMD.xml")) && (!missingFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					sb = new StringBuilder();
					inline = "";
					while ((inline = inputReader.readLine()) != null) {
						sb.append(inline);
					}
					builder = new SAXBuilder();
					Document doc = (Document) builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
					Element xmlRoot = doc.getRootElement();
					// read program parameters
					List<Element> clist = xmlRoot.getChildren();
					missing = new ArrayList<>();
					for (int i = 0; i < clist.size(); i++) {
						Element thisElement = clist.get(i);
						String thisElementName = thisElement.getName();
						missing.add(thisElementName);
					}

					missingFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("removedMDCols.xml")) && (!removedMDColsFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					sb = new StringBuilder();
					inline = "";
					while ((inline = inputReader.readLine()) != null) {
						sb.append(inline);
					}
					builder = new SAXBuilder();
					Document doc = (Document) builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
					Element xmlRoot = doc.getRootElement();
					// read program parameters
					List<Element> clist = xmlRoot.getChildren();
					removedMDCols = new ArrayList<>();
					for (int i = 0; i < clist.size(); i++) {
						Element thisElement = clist.get(i);
						String thisElementName = thisElement.getName();
						removedMDCols.add(thisElementName);

					}

					removedMDColsFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				}

				try {
					thisEntry = instream.getNextEntry();
				} catch (IOException e) {
					System.out.println("stream probably closed");
				}
			}
			instream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		ZipInputStream instream = null;
		setChanged(false);
		if (!extendedFound) {
			try {

				loadMetadata((InputStream) null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// for older project files
		if (!removedMDColsFound) {
			removedMDCols = new ArrayList<>();
		}

		if (allsWell && projectFileFound) {
			try {
				//JOptionPane.showMessageDialog(null, "removedcols:"+removedMDCols.toString());
				newcollection.removeUnusedCols(removedMDCols);
				newcollection.removeDataPermanently(new HashSet<>(excluded));
				newcollection.addNullData(missing);
				//JOptionPane.showMessageDialog(null, "parse tree");
				ParseTableTree ob = new ParseTableTree(newcollection, tree, newcollection.getDatacol(),
						this.getDataColumnHeaders());
				//JOptionPane.showMessageDialog(null, "to table tree");
				org.jdom.Document res = ob.tableToTree();
				// save and read repscolname
				// add
				//JOptionPane.showMessageDialog(null, "Creating MDH");
				loadMetadataHybrid(newcollection, res.getRootElement(), ob.getTreeMap(), newcollection.getDatacol(),
						ob.getMetadataHeaders(), tree, ob.getDefaultRepMap(), ob.getDefaultRepCol(), missing, excluded,
						removedMDCols);

			} catch (NullPointerException | IOException e) {
				//JOptionPane.showMessageDialog(null, "error:");
				e.printStackTrace();
				return false;
			}
		}
		return (allsWell) && (projectFileFound);
	}

	/**
	 * convert xml to jtree
	 */
	private DefaultTreeModel xmltoJtree(Element XMLroot) {
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
		addNodesToJtree(root, treeRoot);
		return (DefaultTreeModel) pTree.getModel();
	}

	public void addNodesToJtree(Element root, DefaultMutableTreeNode node) {
		List<Element> cList = root.getChildren();
		if (cList.size() < 1) {

			return;
		}

		for (Element c : cList) {
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
			addNodesToJtree(c, newNode);
			node.add(newNode);
		}

	}

	private boolean openProjectOLD(File projectFile) {
		if (!projectFile.exists()) {
			return false;
		}
		boolean allsWell = true;
		boolean projectFileFound = false;
		boolean extendedFound = false;
		try {
			ZipInputStream instream = new ZipInputStream(new FileInputStream(projectFile));
			ZipEntry thisEntry = instream.getNextEntry();
			while ((allsWell) && (thisEntry != null)) {
				if ((thisEntry.getName().equals("ProjectFile.xml")) && (!projectFileFound)) {
					allsWell = loadProjectFile(instream, projectFile);
					if (!allsWell) {
						System.out.println("Failure at project file load");
					}
					projectFileFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("extended.xml")) && (!extendedFound)) {
					JOptionPane.showMessageDialog(MetaOmGraph.getDesktop(),
							"MetaOmGraph needs to update this project's metadata. Please be patient, this may take a while.",
							"Metadata Update", 1);
					System.out.println("Updating metadata");
					final PipedOutputStream newMetadataOut = new PipedOutputStream();
					final BufferedInputStream oldMetadataIn = new BufferedInputStream(instream);
					PipedInputStream newMetadataIn = new PipedInputStream(newMetadataOut);
					new Thread() {
						public void run() {
							try {
								MetadataUpdater.update(oldMetadataIn, newMetadataOut);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();
					loadMetadata(newMetadataIn);
					newMetadataIn.close();
					newMetadataOut.close();
					extendedFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("metadata.xml")) && (!extendedFound)) {
					loadMetadata(instream);
					extendedFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				}
				try {
					thisEntry = instream.getNextEntry();
				} catch (IOException e) {
					System.out.println("stream probably closed");
				}
			}
			instream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		ZipInputStream instream = null;
		setChanged(false);
		if (!extendedFound) {
			try {
				loadMetadata((InputStream) null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return (allsWell) && (projectFileFound);
	}

	private boolean loadProjectFile(InputStream instream, File projectFile) {
		try {
			maxNameLength = 0;
			SAXBuilder builder = new SAXBuilder();
			Document myDoc = builder.build(instream);

			Element root = myDoc.getRootElement();
			Element projectInfo = root.getChild("projectInfo");

			source = new File(projectInfo.getChild("sourcePath").getText() + File.separator
					+ projectInfo.getChild("sourceFile").getText());
			if (!source.exists()) {

				source = new File(projectFile.getParentFile().getAbsolutePath() + File.separator
						+ projectInfo.getChild("sourceFile").getText());
				if (!source.exists()) {
					int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
							"The file " + source.getName() + " was not found.\nWould you like to locate it yourself?",
							"File not found", 0);
					if (result == 1)
						return false;
					JFileChooser chooser = new JFileChooser(projectFile.getParentFile());
					chooser.setFileFilter(new FileFilter() {
						public boolean accept(File f) {
							return (f.isDirectory()) || (f.getName().equals(source.getName()));
						}

						public String getDescription() {
							return source.getName();
						}

					});
					result = 0;
					while ((!source.exists()) && (result == 0)) {
						result = chooser.showOpenDialog(MetaOmGraph.getMainWindow());
						source = chooser.getSelectedFile();
					}
					if (result != 0)
						return false;
					Utils.setLastDir(source.getParentFile());
				}
			}

			Element delimiterElement = projectInfo.getChild("delimiter");

			if (delimiterElement.getText().equals("")) {
				delimiter = ' ';
			} else if (delimiterElement.getText().equals("\\t")) {
				delimiter = '\t';
			} else
				delimiter = delimiterElement.getText().charAt(0);
			Element ignoreConsecutiveElement = projectInfo.getChild("ignoreConsecutiveDelimiters");
			if (ignoreConsecutiveElement != null) {
				ignoreConsecutiveDelimiters = Boolean.parseBoolean(ignoreConsecutiveElement.getText());
			} else {
				ignoreConsecutiveDelimiters = true;
			}
			Element blankValueElement = projectInfo.getChild("blankValue");
			if (blankValueElement != null) {
				blankValue = Double.valueOf(Double.parseDouble(blankValueElement.getText()));
			}
			defaultXAxis = projectInfo.getChild("xLabel").getText();
			defaultYAxis = projectInfo.getChild("yLabel").getText();
			defaultTitle = projectInfo.getChild("title").getText();
			color1 = new Color(Integer.parseInt(projectInfo.getChild("color1").getText()));
			color2 = new Color(Integer.parseInt(projectInfo.getChild("color2").getText()));
			if (projectInfo.getChild("defaultColumn") == null) {
				defaultColumn = 0;
			} else {
				defaultColumn = Integer.parseInt(projectInfo.getChild("defaultColumn").getText());
			}
			List infoColumnList = projectInfo.getChildren("infoColumn");
			infoColumns = infoColumnList.size();
			List dataColumnList = root.getChild("columns").getChildren("column");
			columnHeaders = new String[infoColumns + dataColumnList.size()];
			Iterator iter = infoColumnList.iterator();
			int index = 0;
			while (iter.hasNext()) {
				columnHeaders[index] = ((Element) iter.next()).getText();
				index++;
			}
			System.out.println(dataColumnList.size() + " columns");
			iter = dataColumnList.iterator();
			while (iter.hasNext()) {
				columnHeaders[index] = ((Element) iter.next()).getText();
				if (columnHeaders[index].length() > maxNameLength)
					maxNameLength = columnHeaders[index].length();
				index++;
			}
			List dataList = root.getChildren("data");
			iter = dataList.iterator();

			index = 0;

			rowNames = new Object[dataList.size()][infoColumns];
			fileIndex = new Long[dataList.size()];
			while (iter.hasNext()) {
				Element thisDataItem = (Element) iter.next();
				infoColumnList = thisDataItem.getChildren("info");
				Iterator iter2 = infoColumnList.iterator();
				int index2 = 0;
				while (iter2.hasNext()) {
					Element thisElement = (Element) iter2.next();
					String text = thisElement.getText();
					if ("correlation".equals(thisElement.getAttributeValue("type"))) {
						CorrelationValue thisValue;
						if ("false".equals(thisElement.getAttributeValue("asPercent"))) {
							if (text.length() > 0) {
								thisValue = new CorrelationValue(Double.parseDouble(text));
							} else {
								thisValue = null;
							}
							thisValue.setAsPercent(false);
						} else {
							if (text.length() > 0) {
								thisValue = new CorrelationValue(
										Double.parseDouble(text.substring(0, text.length() - 1)) / 100.0D);
							} else {
								thisValue = null;
							}
						}
						rowNames[index][index2] = thisValue;
						if ("true".equals(thisElement.getAttributeValue("last"))) {
							hasLastCorrelation = true;
						}
					} else if (text.length() > 0) {
						rowNames[index][index2] = thisElement.getText();
					} else {
						rowNames[index][index2] = null;
					}

					index2++;
				}
				fileIndex[index] = new Long(thisDataItem.getChild("location").getText());
				index++;
			}

			List geneListList = root.getChildren("list");
			iter = geneListList.iterator();

			geneLists = new Hashtable();
			while (iter.hasNext()) {
				Element thisGeneList = (Element) iter.next();
				List entryList = thisGeneList.getChildren("entry");
				int[] entries = new int[entryList.size()];
				Iterator iter2 = entryList.iterator();
				index = 0;
				while (iter2.hasNext()) {
					entries[index] = Integer.parseInt(((Element) iter2.next()).getText());
					index++;
				}
				geneLists.put(thisGeneList.getAttributeValue("name"), entries);
			}

			List sortList = root.getChildren(NewCustomSortDialog.CustomSortObject.getXMLElementName());
			iter = sortList.iterator();

			savedSorts = new Hashtable();
			while (iter.hasNext()) {
				Element thisSortElement = (Element) iter.next();
				String name = thisSortElement.getAttributeValue("name");
				NewCustomSortDialog.CustomSortObject cso = new NewCustomSortDialog.CustomSortObject();
				cso.fromXML(thisSortElement);
				savedSorts.put(name, cso);
			}

			List querySetList = root.getChildren(TreeSearchQueryConstructionPanel.QuerySet.getXMLElementName());
			iter = querySetList.iterator();

			savedQueries = new Hashtable();
			while (iter.hasNext()) {
				Element thisQuerySetElement = (Element) iter.next();
				String name = thisQuerySetElement.getAttributeValue("name");
				TreeSearchQueryConstructionPanel.QuerySet thisQuerySet = new TreeSearchQueryConstructionPanel.QuerySet();
				thisQuerySet.fromXML(SimpleXMLElement.fromJDOMElement(thisQuerySetElement));
				savedQueries.put(name, thisQuerySet);
			}

			List excludeList = root.getChildren(MetaOmAnalyzer.ExcludeData.getXMLElementName());
			iter = excludeList.iterator();

			savedExcludes = new Hashtable();
			while (iter.hasNext()) {
				Element thisExcludeElement = (Element) iter.next();
				String name = thisExcludeElement.getAttributeValue("name");
				MetaOmAnalyzer.ExcludeData thisExcludeData = new MetaOmAnalyzer.ExcludeData();
				thisExcludeData.fromXML(thisExcludeElement);
				savedExcludes.put(name, thisExcludeData);
			}
			Element repElement = root.getChild("reps");
		} catch (JDOMException e) {
			Element repElement;

			e.printStackTrace();
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "The file " + projectFile.getName()
					+ " does not appear to be a valid " + "MetaOmGraph project file!");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
					"Error reading the file " + projectFile.getName());
			return false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "The file " + projectFile.getName()
					+ " is either not a MetaOmGraph project " + "file, or it is missing required data.");
			return false;
		}
		return true;
	}

	public boolean isChanged() {
		return changed;
	}

	public Object[][] getRowNames() {
		if (infoColumns == 0) {
			String[][] result = new String[rowNames.length][1];
			for (int x = 0; x < result.length; x++)
				result[x][0] = (x + 1) + "";
			return result;
		}
		return rowNames;
	}

	/**
	 * @author urmi get gene/tx name only
	 * @param entry
	 * @return
	 */
	public Object[] getGeneName(int entry) {
		if (infoColumns == 0) {
			String[][] result = new String[rowNames.length][1];
			for (int x = 0; x < result.length; x++)
				result[x][0] = (x + 1) + "";
			return result;
		}
		return rowNames[entry];
	}

	
	public Object[] getRowName(int entry) {
		if (infoColumns == 0) {
			String[] result = new String[1];
			result[0] = (entry + 1) + "";
			return result;
		}
		return rowNames[entry];
	}

	/**
	 * Returns the indices of correlation columns
	 * @return
	 */
	public ArrayList<Integer> getCorrelationColumns() {
		ArrayList<Integer> result = new ArrayList();
		for (int col = 0; col < rowNames[0].length; col++) {
			boolean found = false;
			int row = 0;
			while ((!found) && (row < rowNames.length)) {
				if ((rowNames[row][col] != null) && (!"".equals(rowNames[row][col]))) {
					found = true;
					if ((rowNames[row][col] instanceof CorrelationValue)) {
						result.add(Integer.valueOf(col));
					}
				}
				row++;
			}
		}
		return result;
	}

	/**
	 * get source file
	 * @return
	 */
	public File getSourceFile() {
		return source;
	}

	public long getFileIndex(int index) {
		if (fileIndex == null)
			return -1L;
		return fileIndex[index].longValue();
	}

	public String[] getInfoColumnNames() {
		if (infoColumns == 0)
			return new String[] { "Entry" };
		if (columnHeaders.length == 0) {
			return new String[] { "Entry" };
		}
		String[] result = new String[infoColumns];
		for (int i = 0; i < infoColumns; i++)
			result[i] = Utils.clean(columnHeaders[i]);
		return result;
	}

	public String[] getColumnHeaders() {
		return columnHeaders;
	}

	public boolean addGeneList(String name, int[] entries, boolean notify) {
		String listName = name;
		if ((listName == null) || (listName.trim().equals(""))) {
			String result = "";
			while ((result != null) && (result.equals(""))) {
				result = JOptionPane.showInputDialog(MetaOmGraph.getMainWindow(),
						"Please enter a name for this gene list", "Create new gene list", 2);
				if (result != null)
					result = result.trim();
				if (getGeneListRowNumbers(result) != null) {
					JOptionPane.showInternalMessageDialog(null,
							"A list with that name already exists.  Please enter a different name.",
							"Duplicate list name", 0);
					result = "";
				}
			}
			if (result == null)
				return false;
			listName = result;
		}
		geneLists.put(listName, entries);
		setChanged(true);
		if (notify) {
			fireStateChanged("create list");
		}
		return true;
	}

	public boolean renameGeneList(String oldName, String newName) {
		String listName = newName;
		if ((listName == null) || (listName.trim().equals(""))) {
			String result = "";
			while ((result != null) && (result.equals(""))) {
				result = (String) JOptionPane.showInputDialog(MetaOmGraph.getMainWindow(),
						"Please enter a new name for this gene list", "Create new gene list", 3, null, null, oldName);
				if (result != null) {
					result = result.trim();
					if (result.equals(oldName))
						return true;
					if (getGeneListRowNumbers(result) != null) {
						JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
								"A list with that name already exists.  Please enter a different name.",
								"Duplicate list name", 0);
						result = "";
					}
				}
			}
			if (result == null)
				return false;
			listName = result;
		}
		int[] entries = getGeneListRowNumbers(oldName);
		geneLists.remove(oldName);
		geneLists.put(listName, entries);
		setChanged(true);
		fireStateChanged("rename list");
		return true;
	}

	public String[] getGeneListNames() {
		Enumeration enumer = geneLists.keys();

		String[] result = new String[geneLists.keySet().size() + 1];
		result[0] = "Complete List";
		int x = 1;
		while (enumer.hasMoreElements()) {
			result[x] = ((String) enumer.nextElement());
			x++;
		}
		return result;
	}

	public int[] getGeneListRowNumbers(String name) {
		if (name == null)
			return null;
		if (name.equals("Complete List")) {
			int[] result = new int[rowNames.length];
			for (int i = 0; i < rowNames.length; i++)
				result[i] = i;
			return result;
		}
		return geneLists.get(name);
	}

	public Object[][] getGeneListRowNames(String name) {
		if (name.equals("Complete List"))
			return getRowNames();
		int[] entries = geneLists.get(name);
		Object[][] result;
		if (getInfoColumnCount() <= 0) {
			result = new Object[entries.length][1];
		} else {
			result = new Object[entries.length][getInfoColumnCount()];
		}
		Object[][] myRowNames = getRowNames(entries);
		for (int x = 0; x < entries.length; x++)
			result[x] = myRowNames[x];
		return result;
	}

	public boolean addGeneList(int[] entries) {
		return addGeneList(null, entries, true);
	}

	public boolean addGeneList(Collection<Integer> entries) {
		int[] result = new int[entries.size()];
		int addHere = 0;
		for (Iterator localIterator = entries.iterator(); localIterator.hasNext();) {
			int addMe = ((Integer) localIterator.next()).intValue();
			result[(addHere++)] = addMe;
		}
		return addGeneList(result);
	}

	public boolean addGeneList(String name, Collection<Integer> entries) {
		int[] result = new int[entries.size()];
		int addHere = 0;
		for (Iterator localIterator = entries.iterator(); localIterator.hasNext();) {
			int addMe = ((Integer) localIterator.next()).intValue();
			result[(addHere++)] = addMe;
		}
		return addGeneList(name, result, true);
	}

	public void deleteGeneList(String name) {
		geneLists.remove(name);
		setChanged(true);
		fireStateChanged("delete list");
	}

	public int getInfoColumnCount() {
		return infoColumns;
	}

	public int getDataColumnCount() {
		return columnHeaders.length - infoColumns;
	}

	/**
	 * Return a list of Datacol index not in the given list
	 * 
	 * @param inputList
	 * @return
	 */
	public Collection<Integer> getComplentDataColumns(Collection<Integer> inputList, boolean removeExcluded) {
		List<Integer> res = new ArrayList<>();
		for (int i = 0; i < getDataColumnCount(); i++) {
			if (!inputList.contains(i)) {
				res.add(i);
			}
		}

		if (removeExcluded) {
			boolean[] excluded = MetaOmAnalyzer.getExclude();
			if (excluded != null) {
				List<Integer> toRem = new ArrayList<>();
				for (int k = 0; k < excluded.length; k++) {
					if (excluded[k]) {
						toRem.add(k);
					}
				}
				res.removeAll(toRem);
			}
		}

		return res;
	}

	/**
	 * Return true if all datacolumns in data file are unique
	 */
	public boolean isUniqueDataCols() {
		List<String> colList = Arrays.asList(getDataColumnHeaders());
		Set<String> colSet = new HashSet<String>(colList);
		if (colSet.size() == colList.size()) {
			return true;
		}
		return false;
	}

	public Object[][] getRowNames(int[] rows) {
		if (rows == null)
			return null;
		Object[][] result;
		if (infoColumns == 0) {
			result = new Object[rows.length][1];
			for (int i = 0; i < result.length; i++)
				result[i][0] = rows[i];
		} else {
			result = new Object[rows.length][infoColumns];
			for (int i = 0; i < result.length; i++)
				result[i] = getRowName(rows[i]);
		}
		return result;
	}

	/**
	 * @author urmi
	 * @param rows
	 * @return
	 */
	public String[] getDefaultRowNames(int[] rows) {
		if (rows == null)
			return null;
		Object[][] result;
		String[] res = new String[rows.length];
		if (infoColumns == 0) {
			result = new Object[rows.length][1];
			for (int i = 0; i < result.length; i++) {
				result[i][0] = rows[i];
				res[i] = result[i][defaultColumn].toString();
			}
		} else {
			result = new Object[rows.length][infoColumns];
			for (int i = 0; i < result.length; i++) {
				result[i] = getRowName(rows[i]);
				res[i] = result[i][defaultColumn].toString();
			}
		}

		return res;
	}

	/**
	 * return index of rows matching a list of names
	 * 
	 * @param names
	 * @return
	 */
	public int[] getRowIndexbyName(List<String> names, boolean matchCase) {
		if (names == null)
			return null;
		
		//use list in case of duplicates in names list
		List<Integer> res=new ArrayList<>();
		
		if (matchCase) {
			for (int i = 0; i < rowNames.length; i++) {
				String thisName = rowNames[i][defaultColumn].toString();
				if (names.contains(thisName)) {
					res.add(i);
					
				}
			}
		} else {
			for (String s : names) {
				res.add(getRowIndexbyName(s, true));
			}
		}
		
		int [] temp=new int[res.size()];
		int k=0;
		for(int i:res) {
			temp[k++]=i;
		}
		return temp ;
	}

	public int getRowIndexbyName(String name, boolean matchCase) {
		if (name == null)
			return -1;
		
		String [] allRownames=getAllDefaultRowNames();
		
		for(int k=0;k<allRownames.length;k++) {
			String thisName = allRownames[k];
			
			if (!matchCase) {
				thisName = thisName.toLowerCase();
				name = name.toLowerCase();
			}
			if(name.equals(thisName)) {
				return k;
			}
		}
		
		return -1;

	}

	/**
	 * Get rowname under defaultColumn
	 * 
	 * @author urmi
	 * @param rows
	 * @return
	 */
	public String getDefaultRowNames(int row) {
		if (row < 0)
			return null;
		Object[] result = new Object[1];
		;
		String res;
		if (infoColumns == 0) {
			result[0] = row;
			res = result[defaultColumn].toString();

		} else {
			result = getRowName(row);
			res = result[defaultColumn].toString();

		}

		return res;
	}

	/**
	 * Get all the row names in project
	 * 
	 * @return
	 */
	public String[] getAllDefaultRowNames() {
		Object[][] result;
		String[] res = new String[getRowCount()];
		if (infoColumns == 0) {
			result = new Object[getRowCount()][1];
			for (int i = 0; i < result.length; i++) {
				result[i][0] = i;
				res[i] = result[i][defaultColumn].toString();
			}
		} else {
			result = new Object[getRowCount()][infoColumns];
			for (int i = 0; i < result.length; i++) {
				result[i] = getRowName(i);
				res[i] = result[i][defaultColumn].toString();
			}
		}
		return res;
	}

	/**
	 * get name of data column by its index
	 * 
	 * @param index
	 * @return
	 */
	public String getDataColumnHeader(int index) {
		if (index + infoColumns >= columnHeaders.length) {
			return "";
		}
		return columnHeaders[(index + infoColumns)];
	}

	/*
	 * Sort data in rowindex and return the datacolumn at index after sorting
	 * increasing order
	 * 
	 * NOT USED
	 */
	public String getDatainSortedOrderNoExclude(int rowIndex, int index) throws IOException {
		double[] thisData = getAllData(rowIndex);
		int[] thisDatacolIndex = new int[getDataColumnCount()];
		for (int i = 0; i < thisDatacolIndex.length; i++) {
			thisDatacolIndex[i] = i;
		}

		// sort thisData and thisDatacolIndex together
		/* Bubble Sort */
		for (int p = 0; p < thisData.length; p++) {
			for (int q = 0; q < thisData.length - 1 - p; q++) {
				if (thisData[q] > thisData[q + 1]) {
					double swapString = thisData[q];
					thisData[q] = thisData[q + 1];
					thisData[q + 1] = swapString;
					int swapInt = thisDatacolIndex[q];
					thisDatacolIndex[q] = thisDatacolIndex[q + 1];
					thisDatacolIndex[q + 1] = swapInt;
				}
			}
		}
		return getDataColumnHeader(thisDatacolIndex[index]);
	}

	/**
	 * return datacolumn headers according to sorted data. Used in finding metadata
	 * of a point in scatter plot NOT USED
	 * 
	 * @param rowIndex
	 * @param index
	 * @param excludedCopy
	 * @return
	 * @throws IOException
	 */
	public String getDatainSortedOrder(int rowIndex, int index, boolean[] excludedCopy) throws IOException {

		if (excludedCopy == null) {
			return getDatainSortedOrderNoExclude(rowIndex, index);
		}

		boolean[] excludedLocalCopy = new boolean[excludedCopy.length];
		System.arraycopy(excludedCopy, 0, excludedLocalCopy, 0, excludedCopy.length);

		double[] thisData = getAllData(rowIndex);
		int[] thisDatacolIndex = new int[getDataColumnCount()];
		for (int i = 0; i < thisDatacolIndex.length; i++) {
			thisDatacolIndex[i] = i;
		}

		// sort thisData and thisDatacolIndex together
		/* Bubble Sort */
		for (int p = 0; p < thisData.length; p++) {
			for (int q = 0; q < thisData.length - 1 - p; q++) {
				if (thisData[q] > thisData[q + 1]) {
					double swapString = thisData[q];
					thisData[q] = thisData[q + 1];
					thisData[q + 1] = swapString;
					int swapInt = thisDatacolIndex[q];
					thisDatacolIndex[q] = thisDatacolIndex[q + 1];
					thisDatacolIndex[q + 1] = swapInt;
					boolean swapBool = excludedLocalCopy[q];
					excludedLocalCopy[q] = excludedLocalCopy[q + 1];
					excludedLocalCopy[q + 1] = swapBool;
				}
			}
		}

		// find item at index after ignoring excluded columns
		int res = -1;
		int count = -1;
		for (int i = 0; i < excludedLocalCopy.length; i++) {
			if (!excludedLocalCopy[i]) {
				count++;
			}
			if (count == index) {
				res = i;
				break;
			}
		}

		return getDataColumnHeader(thisDatacolIndex[res]);
	}

	public int getCorrectDataColumnForScatterPlot(int rowIndex, int index, Collection<Integer> dataColInd,
			boolean[] excludedCopy) throws IOException {

		List<Integer> includedDataCol = new ArrayList<>();
		double[] thisData = getAllData(rowIndex);
		List<Double> dataInSeries = new ArrayList<>();
		if (excludedCopy != null) {
			for (int i : dataColInd) {
				if (!excludedCopy[i]) {
					includedDataCol.add(i);
				}
			}
		} else {
			for (int i : dataColInd) {
				includedDataCol.add(i);
			}
		}

		for (int i : includedDataCol) {
			dataInSeries.add(thisData[i]);
		}
		// create a copy
		List<Double> dataInSeriesOrig = new ArrayList<>(dataInSeries);
		Collections.sort(dataInSeries);
		// get index in the orignal data array
		// this approach may fail for repeating data values
		int thisInd = dataInSeriesOrig.indexOf(dataInSeries.get(index));

		return includedDataCol.get(thisInd);
	}

	public String getDataColumnHeader(int index, boolean shorten) {
		if (!shorten)
			return getDataColumnHeader(index);
		String result = getDataColumnHeader(index);
		if (result.length() > 15)
			result = result.substring(0, 14) + "...";
		return result;
	}

	public String[] getDataColumnHeaders() {
		String[] result = new String[columnHeaders.length - infoColumns];
		for (int x = infoColumns; x < columnHeaders.length; x++)
			result[(x - infoColumns)] = columnHeaders[x];
		return result;
	}

	/**
	 * return dataColumn names for selected indices
	 * 
	 * @param selected
	 * @return
	 */
	public String[] getDataColumnHeaders(int selected[]) {
		String[] result = new String[selected.length];
		String[] allresult = getDataColumnHeaders();
		for (int i = 0; i < selected.length; i++) {
			result[i] = allresult[selected[i]];
		}
		return result;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setRowNames(Object[][] rowNames, String[] infoColumnNames) {
		if ((rowNames == null) || (infoColumnNames == null))
			throw new IllegalArgumentException("Null parameters not allowed!");
		if (rowNames.length <= 0)
			throw new IllegalArgumentException("rowNames must be non-null, and have length>0!");
		if (infoColumnNames.length != rowNames[0].length)
			throw new IllegalArgumentException("infoColumnNames.length (" + infoColumnNames.length
					+ ") must equal rowNames[x].length (" + rowNames[0].length + ")!");
		String[] newColumnHeaders = new String[infoColumnNames.length + getDataColumnCount()];
		for (int x = 0; x < infoColumnNames.length; x++)
			newColumnHeaders[x] = infoColumnNames[x];
		for (int x = 0; x < getDataColumnCount(); x++)
			newColumnHeaders[(x + infoColumnNames.length)] = getDataColumnHeader(x);
		System.out.print("headers assembled... ");

		if (fileIndex != null) {
			this.rowNames = new Object[fileIndex.length][rowNames[0].length];
		} else {
			this.rowNames = new Object[data.length][rowNames[0].length];
		}
		for (int x = 0; x < this.rowNames.length; x++) {
			if (x < rowNames.length) {
				this.rowNames[x] = rowNames[x];
			} else {
				this.rowNames[x][0] = x;
				for (int y = 1; y < this.rowNames[x].length; y++)
					this.rowNames[x][y] = "";
			}
		}
		System.out.print("row names assembled... ");
		columnHeaders = newColumnHeaders;
		infoColumns = infoColumnNames.length;
		System.out.print("new names assigned... ");
		setChanged(true);
		fireStateChanged("row name change");
	}

	public void setRowNames(Object[][] rowNames) {
		if ((rowNames == null) || (rowNames.length <= 0) || (rowNames[0].length <= 0))
			throw new IllegalArgumentException("Bad row names");
		Object[][] newRowNames = new Object[rowNames.length - 1][rowNames[0].length];
		String[] headers = new String[rowNames[0].length];
		for (int x = 0; x < rowNames[0].length; x++)
			headers[x] = rowNames[0][x].toString();
		for (int x = 1; x < rowNames.length; x++) {
			for (int y = 0; y < rowNames[x].length; y++) {
				newRowNames[(x - 1)][y] = rowNames[x][y];
			}
		}
		System.out.print("new names assembled (" + newRowNames.length + "x" + newRowNames[0].length + ")... ");
		setRowNames(newRowNames, headers);
	}

	public void setDataColumnHeaders(Object[][] headers) {
		String[] newHeaders = new String[columnHeaders.length - infoColumns];
		for (int x = 0; x < newHeaders.length; x++) {
			if (x >= headers.length) {
				columnHeaders[(x + infoColumns)] = x + "";
			} else
				columnHeaders[(x + infoColumns)] = headers[x][0].toString();
		}
		setChanged(true);
	}

	public void setDataColumnHeaders(Object[] headers) {
		Object[][] newArray = new Object[headers.length][1];
		for (int i = 0; i < headers.length; i++)
			newArray[i][0] = headers[i];
		setDataColumnHeaders(newArray);
	}

	public Color getColor1() {
		return color1;
	}

	public void setColor1(Color color1) {
		if (!color1.equals(this.color1)) {
			setChanged(true);
		}
		this.color1 = color1;
	}

	public Color getColor2() {
		return color2;
	}

	public void setColor2(Color color2) {
		if (!color2.equals(this.color2))
			setChanged(true);
		this.color2 = color2;
	}

	public int getDefaultColumn() {
		return defaultColumn;
	}

	public void setDefaultColumn(int defaultColumn) {
		this.defaultColumn = defaultColumn;
	}

	public String getDefaultTitle() {
		return defaultTitle;
	}

	public void setDefaultTitle(String defaultTitle) {
		if (!defaultTitle.equals(this.defaultTitle))
			setChanged(true);
		this.defaultTitle = defaultTitle;
	}

	public String getDefaultXAxis() {
		return defaultXAxis;
	}

	public void setDefaultXAxis(String defaultXAxis) {
		if (!defaultXAxis.equals(defaultTitle))
			setChanged(true);
		this.defaultXAxis = defaultXAxis;
	}

	public String getDefaultYAxis() {
		return defaultYAxis;
	}

	public void setDefaultYAxis(String defaultYAxis) {
		if (!defaultYAxis.equals(this.defaultYAxis))
			setChanged(true);
		this.defaultYAxis = defaultYAxis;
	}

	public int getMaxNameLength() {
		int result = 0;
		for (int x = 0; x < getDataColumnCount(); x++)
			if (getDataColumnHeader(x).length() > result)
				result = getDataColumnHeader(x).length();
		return result;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public boolean loadMetadata() throws IOException {
		File source = Utils.chooseFileToOpen(new GraphFileFilter(0), null);
		if (source == null)
			return false;
		return loadMetadata(source);
	}

	public boolean loadMetadata(File source) throws IOException {
		if (source == null) {
			return loadMetadata((InputStream) null);
		}

		// if file is xml
		return loadMetadata(new FileInputStream(source));

	}

	// urmi loadMetadata when csv is read
	public boolean loadMetadata_csv(File source) throws IOException {
		if (source == null) {
			return loadMetadata((InputStream) null);
		}
		// Metadatacollection object is made inside ReadMetadata class
		String metadataDelim = String.valueOf(this.metadatadelimiter);

		// JOptionPane.showMessageDialog(null, "Deli:"+metadataDelim);

		new AnimatedSwingWorker("Working...", true) {

			@Override
			public Object construct() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {

							readMetadataframe = new ReadMetadata(source.getAbsolutePath(), metadataDelim);
							// JOptionPane.showMessageDialog(null, "delimP:"+metadataDelim);
							readMetadataframe.toFront();
							readMetadataframe.setVisible(true);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				return null;
			}

		}.start();

		/*
		 * EventQueue.invokeLater(new Runnable() { public void run() { try {
		 * 
		 * readMetadataframe = new ReadMetadata(source.getAbsolutePath(),
		 * metadataDelim); readMetadataframe.setVisible(true); } catch (Exception e) {
		 * e.printStackTrace(); } } });
		 */

		// return true;
		return loadMetadata((InputStream) null);

	} // end loadmetadata_csv

	// public MetadataCollection returnCollection() {
	// return readMetadataframe.getCollectionobj();
	// }

	public ReadMetadata getActiveReadmetadataForm() {
		return this.readMetadataframe;
	}

	// function to return MetadataCollection object. This allows access to Metadata
	// from other classes

	public MetadataEditor returneditor() {
		return editor;
	}

	public void setMogcollection(MetadataCollection obj) {
		readMetadataframe = new ReadMetadata(obj, "");
	}

	public MetadataTreeStructure returntree() {
		return mTree;
	}

	public void setTreeStructure() {
		this.mTree = null;
	}

	public void setTreeStructure(MetadataTreeStructure obj) {
		this.mTree = obj;
	}

	/**
	 * @author urmi
	 * @param MetadataCollection
	 *            object
	 * @param XMLroot
	 *            Root Element
	 * @param tm
	 *            TreeMap to colIndex and Nodes for data columns
	 * @return status changed
	 * @throws IOException
	 */
	public boolean loadMetadataHybrid(MetadataCollection ob, Element XMLroot, TreeMap<Integer, Element> tm,
			String dataCol, String[] mdheaders, JTree treeStructure, TreeMap<String, List<Integer>> defaultrepsMap,
			String defaultrepscol, List<String> missingDC, List<String> extraDC, List<String> removedCols)
			throws IOException {
		if (source == null) {
			// metadataH = new MetadataHybrid();
			// if null there is no metadata
			metadataH = null;
			// JOptionPane.showMessageDialog(null, "loading null stream");
		} else {
			// JOptionPane.showMessageDialog(null, "loading stream");
			metadataH = new MetadataHybrid(ob, XMLroot, tm, dataCol, mdheaders, treeStructure, defaultrepsMap,
					defaultrepscol, missingDC, extraDC, removedCols);
		}
		this.defaultXAxis = dataCol;
		setChanged(true);
		return true;
	}

	public MetadataHybrid getMetadataHybrid() {

		return this.metadataH;
	}

	// urmi make new metadata object
	public boolean loadMetadata(InputStream source) throws IOException {
		if (source == null) {
			metadata = new Metadata(this);
			// JOptionPane.showMessageDialog(null, "loading null stream");
		} else {
			// JOptionPane.showMessageDialog(null, "loading stream");
			metadata = new Metadata(source, this);
		}
		setChanged(true);
		return true;
	}

	/**
	 * Find data column by name and return its index
	 * 
	 * @param header
	 * @return
	 */
	public int findDataColumnHeader(String header) {
		for (int i = infoColumns; i < columnHeaders.length; i++) {
			if (columnHeaders[i].equals(header))
				return i - infoColumns;
		}
		return -1;
	}

	protected void closeDataFile() throws IOException {
		if (dataIn != null) {
			dataIn.close();
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Get data for a given row
	 * 
	 * @param row
	 * @return
	 * @throws IOException
	 */

	public double[] getAllData(int row) throws IOException {
		return getAllData(row, false); // by default apply transformation
	}

	public double[] getAllData(int row, boolean noTransform) throws IOException {
		double[] result;

		if (!streamMode) {
			result = getDataFromFile(row);
			// JOptionPane.showMessageDialog(null, "From file...");
		} else {
			// JOptionPane.showMessageDialog(null, "From mem...");
			result = getDataFromMemory(row);
		}

		// urmi
		String transform = MetaOmGraph.getInstance().getTransform();
		if (transform.equals("NONE")) {
			return result;
		}

		// return raw data
		if (noTransform) {
			return result;
		}

		if (MetaOmGraph.getInstance() != null) {

			for (int i = 0; i < result.length; i++) {
				// add +1 to before applying log
				if (transform.equals("log2")) {
					double log2b10 = Math.log(2.0D);
					result[i] = (Math.log(result[i] + 1) / log2b10);
				} else if (transform.equals("log10")) {
					result[i] = Math.log10(result[i] + 1);
				} else if (transform.equals("loge")) {
					result[i] = Math.log(result[i] + 1);
				} else if (transform.equals("sqrt")) {
					if (result[i] <= 0) {
						result[i] = 0.00;
					} else {
						result[i] = Math.sqrt(result[i]);
					}
				}

			}
		}

		return result;
	}

	/**
	 * @author urmi function to return all the data as list of double[]
	 * @return
	 * @throws IOException
	 */
	public List<double[]> getAllData() throws IOException {
		List<double[]> resultList = new ArrayList<>();
		// urmi
		String transform = MetaOmGraph.getInstance().getTransform();
		if (!streamMode) {
			dataIn = new RandomAccessFile(getSourceFile().getAbsolutePath(), "r", 20000);
			double[] result = new double[getDataColumnCount()];

			for (int i = 0; i < getRowCount(); i++) {
				dataIn.seek(getFileIndex(i));
				for (int x = 0; x < result.length; x++) {
					String tmp = Utils.clean(dataIn.readString(delimiter, ignoreConsecutiveDelimiters));
					try {
						result[x] = Double.parseDouble(tmp);
					} catch (NumberFormatException nfe) {
						result[x] = Double.NaN;
					} catch (NullPointerException npe) {
						result[x] = Double.NaN;
					}
				}
				double[] resCopy = result.clone();
				transformData(resCopy, transform);
				resultList.add(resCopy);
			}
			return resultList;

		}
		for (int i = 0; i < getRowCount(); i++) {
			double[] result = getDataFromMemory(i);
			transformData(result, transform);
			resultList.add(result);
		}

		return resultList;
	}

	/**
	 * Apply transformation on data and return transformed data
	 * 
	 * @param dataIn
	 * @param transform
	 * @return
	 */
	private double[] transformData(double[] dataIn, String transform) {

		if (MetaOmGraph.getInstance() != null) {
			if (transform.equals("NONE")) {
				return dataIn;
			}
			for (int i = 0; i < dataIn.length; i++) {
				// add +1 to before applying log
				if (transform.equals("log2")) {
					double log2b10 = Math.log(2.0D);
					dataIn[i] = (Math.log(dataIn[i] + 1) / log2b10);
				} else if (transform.equals("log10")) {
					dataIn[i] = Math.log10(dataIn[i] + 1);
				} else if (transform.equals("loge")) {
					dataIn[i] = Math.log(dataIn[i] + 1);
				} else if (transform.equals("sqrt")) {
					if (dataIn[i] <= 0) {
						dataIn[i] = 0.00;
					} else {
						dataIn[i] = Math.sqrt(dataIn[i]);
					}
				}

			}
		}

		return dataIn;
	}

	private double[] getDataFromFile(int row) throws IOException {
		boolean showWarning = false;
		if (row > getRowCount())
			throw new IllegalArgumentException("Row " + row + " does not exist!");
		if ((memoryMap != null) && (memoryMap.containsKey(Integer.valueOf(row)))) {
			System.out.println("Reading row " + row + " from memory");
			return data[memoryMap.get(Integer.valueOf(row)).intValue()];
		}
		if (dataIn == null)
			dataIn = new RandomAccessFile(getSourceFile().getAbsolutePath(), "r", 20000);
		double[] thisData = new double[getDataColumnCount()];
		dataIn.seek(getFileIndex(row));
		for (int x = 0; x < thisData.length; x++) {
			String tmp = Utils.clean(dataIn.readString(delimiter, ignoreConsecutiveDelimiters));
			try {
				thisData[x] = Double.parseDouble(tmp);
			} catch (NumberFormatException | NullPointerException nfe) {
				// replace NAN value by blank value provided by user
				if (getBlankValue() == null) {
					thisData[x] = Double.NaN;
				} else {
					thisData[x] = getBlankValue();
				}
				showWarning = true;

			}
		}
		if (showWarning) {
			String message = "Found missing/non-number values in data file. This may affect the analysis. Please check the data file. \n\n\t\t Acessing Row name: "
					+ getGeneName(row)[defaultColumn];
			if (getBlankValue() != null) {
				message += "\n\n Treating missing value as " + getBlankValue();
			}
			JOptionPane.showMessageDialog(null, message, "Found missing/non-number values",
					JOptionPane.WARNING_MESSAGE);
		}
		return thisData;
	}

	public double[] getUnloggedData(int row) throws IOException {
		if (!streamMode) {
			return getDataFromFile(row);
		}

		return getDataFromMemory(row);
	}

	/**
	 * get data for selected row and only included columns
	 * 
	 * @param row
	 * @return
	 * @throws IOException
	 */
	public double[] getIncludedData(int row) throws IOException {
		double[] data = getAllData(row);
		boolean[] exclude = MetaOmAnalyzer.getExclude();
		if (exclude == null) {
			return data;
		}
		double[] result = new double[data.length - MetaOmAnalyzer.getExcludeCount()];
		int addHere = 0;
		for (int i = 0; i < data.length; i++) {
			if (exclude[i] == false) {
				result[addHere] = data[i];
				addHere++;
			}
		}
		return result;
	}

	/**
	 * return data after removing excluded columns given as a parameter. This
	 * excluded parameter may be different than MetaOmAnalyzer.getexclude()
	 * 
	 * @param row
	 * @return
	 * @throws IOException
	 */
	public double[] getIncludedData(int row, boolean[] localExclude) throws IOException {
		double[] data = getAllData(row);
		boolean[] exclude = localExclude;
		if (exclude == null) {
			return data;
		}
		double[] result = new double[data.length - MetaOmAnalyzer.getExcludeCount()];
		int addHere = 0;
		for (int i = 0; i < data.length; i++) {
			if (exclude[i] == false) {
				result[addHere] = data[i];
				addHere++;
			}
		}
		return result;
	}

	public double[] getIncludedData(int row, boolean replaceMissing) throws IOException {
		double[] data = getAllData(row);
		boolean[] exclude = MetaOmAnalyzer.getExclude();
		if (exclude == null) {
			return data;
		}
		double[] result = new double[data.length - MetaOmAnalyzer.getExcludeCount()];
		int addHere = 0;
		for (int i = 0; i < data.length; i++) {
			if (exclude[i] == false) {
				result[addHere] = data[i];
				addHere++;
			}
		}
		return result;
	}

	/**
	 * replace NA values by blankvalue if missing data is found
	 * 
	 * @param data
	 * @return
	 */
	public double[] replaceMissingVals(double[] data) {
		for (int i = 0; i < data.length; i++) {
			if ((Double.isNaN(data[i]))) {
				data[i] = blankValue.doubleValue();
			}
		}

		return data;
	}

	/**
	 * this function will return data of all rows for a given column e.g. get data
	 * for all genes for a given run
	 * 
	 * @param col
	 * @return
	 * @throws IOException
	 */
	public HashMap<Integer, double[]> getAllRowData(int[] selectedCols) throws IOException {
		HashMap<Integer, double[]> res = new HashMap<>();
		int row = 0;
		// get all data
		List<double[]> allData = getAllData();

		// keep only selected columns from data
		for (int i = 0; i < selectedCols.length; i++) {
			double[] temp = new double[getRowCount()];
			res.put(selectedCols[i], temp);
		}

		for (int j = 0; j < allData.size(); j++) {
			for (int i = 0; i < selectedCols.length; i++) {
				res.get(selectedCols[i])[j] = allData.get(j)[selectedCols[i]];
			}

		}
		return res;
	}

	/**
	 * @author urmi return the index of datacolumns in data file for a given array
	 *         with datacolumn headers
	 * @param headers
	 * @return
	 */
	public int[] getColumnIndexbyHeader(String[] headers) {
		int[] res = new int[headers.length];
		List allHeaders = Arrays.asList(getDataColumnHeaders());
		for (int i = 0; i < headers.length; i++) {
			// find index if headers[i] in allHeaders
			res[i] = allHeaders.indexOf(headers[i]);
		}
		return res;
	}

	/**
	 * Corrected urmi
	 * 
	 * @return
	 */
	public String[] getIncludedDataColumnHeaders() {
		String[] headers = getDataColumnHeaders();
		boolean[] exclude = MetaOmAnalyzer.getExclude();
		if (exclude == null) {
			return headers;
		}
		String[] result = new String[headers.length - MetaOmAnalyzer.getExcludeCount()];
		int addHere = 0;
		for (int i = 0; i < headers.length; i++) {
			if (exclude[i] == false) {
				result[addHere++] = headers[i];
			}
			// addHere++;
		}
		return result;
	}

	/**
	 * return the number of included data columns
	 * 
	 * @return
	 */
	public int getIncludedDataColumnCount() {
		return getIncludedDataColumnHeaders().length;
	}

	public synchronized double[] getDataForColumn(int col) throws IOException {
		if (dataIn == null)
			dataIn = new RandomAccessFile(getSourceFile().getAbsolutePath(), "r", 20000);
		double[] thisData = new double[getRowCount()];
		for (int row = 0; row < thisData.length; row++) {
			dataIn.seek(getFileIndex(row));
			for (int x = 0; x < col; x++) {
				dataIn.readString(delimiter, ignoreConsecutiveDelimiters);
			}
			String tmp = Utils.clean(dataIn.readString(delimiter, ignoreConsecutiveDelimiters));
			try {
				thisData[row] = Double.parseDouble(tmp);
			} catch (NumberFormatException nfe) {
				thisData[row] = Double.NaN;
			} catch (NullPointerException npe) {
				thisData[row] = Double.NaN;
			}
		}
		return thisData;
	}

	private double[] getDataFromMemory(int row) {
		return data[row];
	}

	public int getRowCount() {
		return rowNames.length;
	}

	public void saveQueries(TreeSearchQueryConstructionPanel.QuerySet queries, String name) {
		if (savedQueries == null)
			savedQueries = new Hashtable();
		savedQueries.put(name, queries);
		setChanged(true);
	}

	public Hashtable<String, TreeSearchQueryConstructionPanel.QuerySet> getSavedQueries() {
		if (savedQueries == null) {
			savedQueries = new Hashtable();
		}
		return savedQueries;
	}

	public void saveSort(NewCustomSortDialog.CustomSortObject sort, String name) {
		if (savedSorts == null) {
			savedSorts = new Hashtable();
		}
		savedSorts.put(name, sort);
		setChanged(true);
		MetaOmGraph.refreshCharts();
	}

	public Hashtable<String, NewCustomSortDialog.CustomSortObject> getSavedSorts() {
		if (savedSorts == null) {
			savedSorts = new Hashtable();
		}
		return savedSorts;
	}

	public Hashtable<String, MetaOmAnalyzer.ExcludeData> getSavedExcludes() {
		if (savedExcludes == null) {
			savedExcludes = new Hashtable();
		}
		return savedExcludes;
	}

	public boolean isCreatedFromStream() {
		return streamMode;
	}

	public void addChangeListener(ChangeListener listener) {
		if (listener == null)
			return;
		if (changeListeners == null) {
			changeListeners = new Vector();
		}
		changeListeners.add(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		if (listener == null)
			return;
		if (changeListeners == null)
			return;
		changeListeners.remove(listener);
	}

	protected void fireStateChanged(String cause) {
		if (changeListeners == null)
			return;
		ChangeEvent event = new ChangeEvent(cause);
		for (ChangeListener cl : changeListeners) {
			cl.stateChanged(event);
		}
	}

	public boolean ignoreConsecutiveDelimiters() {
		return ignoreConsecutiveDelimiters;
	}

	public boolean mayContainBlankValues() {
		return !ignoreConsecutiveDelimiters;
	}

	public void setLastCorrelation(Number[] lastCorrelation, String name) {
		if ((lastCorrelation == null) || (lastCorrelation.length != getRowCount())) {
			throw new IllegalArgumentException("lastCorrelation must be non-null and must have length==getRowCount()");
		}
		if (!hasLastCorrelation) {
			String[] newColumnHeaders = new String[getDataColumnCount() + getInfoColumnNames().length + 1];
			newColumnHeaders[0] = name;
			int x;
			for (x = 0; x < getInfoColumnNames().length; x++) {
				newColumnHeaders[(x + 1)] = getInfoColumnNames()[x];
			}
			for (int y = 0; y < getColumnHeaders().length - x; y++) {
				newColumnHeaders[(x + y + 1)] = getColumnHeaders()[(x + y)];
			}
			Object[][] newRowNames = new Object[getRowNames().length][getInfoColumnNames().length + 1];
			for (x = 0; x < newRowNames.length; x++) {
				newRowNames[x][0] = lastCorrelation[x];
				for (int y = 0; y < getInfoColumnNames().length; y++) {
					newRowNames[x][(y + 1)] = getRowNames()[x][y];
				}
			}
			columnHeaders = newColumnHeaders;
			rowNames = newRowNames;
			if (infoColumns == 0) {
				infoColumns += 2;
			} else {
				infoColumns += 1;
			}
			defaultColumn += 1;
			hasLastCorrelation = true;
		} else {
			columnHeaders[0] = name;
			for (int x = 0; x < rowNames.length; x++) {
				rowNames[x][0] = lastCorrelation[x];
			}
		}
		setChanged(true);
		fireStateChanged("new correlation");
	}

	/**
	 * Add p-vals column
	 * 
	 * @author urmi
	 * @param pvals
	 * @param name
	 */
	public void setLastPval(Number[] pvals, String name) {
		if ((pvals == null) || (pvals.length != getRowCount())) {
			throw new IllegalArgumentException("lastCorrelation must be non-null and must have length==getRowCount()");
		}
		if (!hasLastCorrelation) {
			String[] newColumnHeaders = new String[getDataColumnCount() + getInfoColumnNames().length + 1];
			newColumnHeaders[0] = name;
			int x;
			for (x = 0; x < getInfoColumnNames().length; x++) {
				newColumnHeaders[(x + 1)] = getInfoColumnNames()[x];
			}
			for (int y = 0; y < getColumnHeaders().length - x; y++) {
				newColumnHeaders[(x + y + 1)] = getColumnHeaders()[(x + y)];
			}
			Object[][] newRowNames = new Object[getRowNames().length][getInfoColumnNames().length + 1];
			for (x = 0; x < newRowNames.length; x++) {
				newRowNames[x][0] = pvals[x];
				for (int y = 0; y < getInfoColumnNames().length; y++) {
					newRowNames[x][(y + 1)] = getRowNames()[x][y];
				}
			}
			columnHeaders = newColumnHeaders;
			rowNames = newRowNames;
			if (infoColumns == 0) {
				infoColumns += 2;
			} else {
				infoColumns += 1;
			}
			defaultColumn += 1;
			hasLastCorrelation = true;
		} else {
			columnHeaders[0] = name;
			for (int x = 0; x < rowNames.length; x++) {
				rowNames[x][0] = pvals[x];
			}
		}
		setChanged(true);
		fireStateChanged("new correlation");
	}

	public void renameColumnHeader(int col, String name) {
		if ((col < 0) || (col > columnHeaders.length)) {
			throw new IllegalArgumentException("col must be between 0 and columnHeaders.length");
		}
		if (name == null) {
			throw new IllegalArgumentException("name must be non-null");
		}
		columnHeaders[col] = name;
	}

	public boolean hasLastCorrelation() {
		return hasLastCorrelation;
	}

	public void keepLastCorrelation() {
		if (!hasLastCorrelation()) {
			throw new NullPointerException("No correlation to save");
		}
		hasLastCorrelation = false;
		setChanged(true);
		fireStateChanged("keep correlation");
	}

	public void removeLastCorrelation() {
		hasLastCorrelation = false;
	}

	public void deleteInfoColumn(int col) {
		if ((col < 0) || (col > getInfoColumnCount())) {
			throw new IllegalArgumentException("col must be between 0 and getInfoColumnCount() (col=" + col
					+ " getInfoColumnCount()=" + getInfoColumnCount() + ")");
		}
		String[] newColumnHeaders = new String[columnHeaders.length - 1];
		Object[][] newRowNames = new Object[rowNames.length][getInfoColumnCount() - 1];
		// urmi
		String thisColName = "";
		int index = 0;
		for (int i = 0; i < columnHeaders.length; i++) {
			if (i != col) {
				newColumnHeaders[(index++)] = columnHeaders[i];
			} else {
				thisColName = columnHeaders[i];
			}
		}
		for (int row = 0; row < rowNames.length; row++) {
			index = 0;
			for (int column = 0; column < rowNames[row].length; column++) {
				if (column != col) {
					newRowNames[row][(index++)] = rowNames[row][column];
				}
			}
		}
		rowNames = newRowNames;
		columnHeaders = newColumnHeaders;
		infoColumns -= 1;
		if ((col == 0) && (hasLastCorrelation)) {
			removeLastCorrelation();
		}
		if (col < getDefaultColumn()) {
			defaultColumn -= 1;
		}

		// also remove from MetaCorrRes
		if (metaCorrs != null) {
			metaCorrs.remove(thisColName);
		}

		setChanged(true);
		fireStateChanged("info column deleted");
	}

	/**
	 * Delete column by name
	 * 
	 * @param col
	 */
	public void deleteInfoColumn(String colName) {

		// get col number
		int col = 0;
		deleteInfoColumn(col);

	}

	public Double getBlankValue() {
		return blankValue;
	}

	public void loadRowsIntoMemory(int[] rows) {
		if (streamMode) {
			System.err.println("Project created from stream; rows already in memory");
			return;
		}
		if (data == null) {
			data = new double[rows.length][getDataColumnCount()];
			memoryMap = new HashMap();
			try {
				for (int i = 0; i < rows.length; i++) {
					data[i] = getAllData(rows[i]);
					memoryMap.put(Integer.valueOf(rows[i]), Integer.valueOf(i));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				data = null;
				memoryMap = null;
				return;
			}
		} else {
			double[][] newData = new double[data.length + rows.length][getDataColumnCount()];
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			try {
				for (int i = data.length; i < newData.length; i++) {
					newData[i] = getAllData(rows[(i - data.length)]);
					memoryMap.put(Integer.valueOf(rows[(i - data.length)]), Integer.valueOf(i));
				}
				data = newData;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return;
			}
		}
	}

	public void clearMemory() {
		data = null;
		memoryMap = null;
	}

	public Collection<Integer> getRowsInMemory() {
		return memoryMap.keySet();
	}

	public void exportLists(File dest, int idCol) {
		Element root = new Element("Lists");
		String[] names = getGeneListNames();
		for (int i = 1; i < names.length; i++) {
			Element addMe = new Element("List").setAttribute("name", names[i]);
			Object[][] rows = getGeneListRowNames(names[i]);
			for (Object[] thisRow : rows) {
				addMe.addContent(new Element("id").setText(thisRow[idCol] + ""));
			}
			root.addContent(addMe);
		}
		XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(dest));
			output.output(root, out);
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error during list export:\n" + ioe.getMessage(),
					"Error", 0);
		}
	}

	public void importLists(File source) {
		try {
			Element root = new SAXBuilder().build(source).getRootElement();
			List addUs = root.getChildren("List");
			for (Object addMe : addUs) {
				Element list = (Element) addMe;
				String name = list.getAttributeValue("name");
				if ((name.equals("")) || (name == null)) {
					throw new JDOMException("Unnamed list");
				}
				if (getGeneListRowNumbers(name) != null) {
					int number = 1;
					String newName = name + "(" + number + ")";
					while (getGeneListRowNumbers(newName) != null) {
						number++;
						newName = name + "(" + number + ")";
					}
					name = newName;
				}
				TreeSet<Integer> rowsToAdd = new TreeSet();
				List ids = list.getChildren("id");
				for (Object thisID : ids) {
					String id = ((Element) thisID).getText();
					boolean found = false;
					int row = 0;
					while (!found && row < getRowCount()) {
						Object[] names = getRowName(row);
						for (Object thisName : names) {
							if (id.equals(thisName + "") && !rowsToAdd.contains(row)) {
								rowsToAdd.add(row);
								found = true;
							}
						}
						row++;
					}
				}

				int[] rows = new int[rowsToAdd.size()];
				int index = 0;
				for (Integer thisRow : rowsToAdd) {
					rows[(index++)] = thisRow.intValue();
				}
				addGeneList(name, rows, false);
			}
			fireStateChanged("create list");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "List import failed:\n" + e.getMessage(),
					"Error", 0);
		}
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
		if (MetaOmGraph.getMainWindow() == null) {
			return;
		}
		String title = MetaOmGraph.getMainWindow().getTitle();
		boolean titleChanged = false;
		if (changed) {
			if (!title.startsWith("* ")) {
				title = "* " + title;
				titleChanged = true;
			}
		} else if (title.startsWith("* ")) {
			title = title.substring(2);
			titleChanged = true;
		}

		if (titleChanged) {
			MetaOmGraph.getMainWindow().setTitle(title);
		}
	}

	public class RepAveragedData {
		public String[] repGroupNames;
		public double[] values;
		public double[] stdDevs;
		public int[] repCounts;

		public RepAveragedData(MetaOmProject myProject, int row) throws IOException {
			List<Metadata.RepGroup> goodGroups = getMetadata().getRepGroups();
			repGroupNames = new String[goodGroups.size()];
			values = new double[goodGroups.size()];
			stdDevs = new double[goodGroups.size()];
			repCounts = new int[goodGroups.size()];

			double[] data = myProject.getAllData(row);
			// JOptionPane.showMessageDialog(null,"data size:"+data.length);
			// JOptionPane.showMessageDialog(null,"goodGroups size:"+goodGroups.size());
			int index = 0;
			for (Metadata.RepGroup thisGroup : goodGroups) {
				// JOptionPane.showMessageDialog(null,"tgn:"+thisGroup.name);
				repGroupNames[index] = thisGroup.name;
				double ave = 0.0D;
				// JOptionPane.showMessageDialog(null,"col size:"+thisGroup.cols.size());
				for (Integer col : thisGroup.cols) {
					// JOptionPane.showMessageDialog(null,"data col:"+col);
					// JOptionPane.showMessageDialog(null,"data col
					// name:"+MetaOmGraph.getActiveProject().getMetadata().getNodeForCol(col).getAttributeValue("name"));
					// JOptionPane.showMessageDialog(null,"data col int val:"+col.intValue());
					try {
						ave += data[col.intValue()];
					} catch (ArrayIndexOutOfBoundsException exception) {
						JOptionPane.showMessageDialog(null, "Error...:" + col);
						JOptionPane.showMessageDialog(null, "data col:" + col);
						JOptionPane.showMessageDialog(null, "data col name:" + MetaOmGraph.getActiveProject()
								.getMetadata().getNodeForCol(col).getAttributeValue("name"));
						JOptionPane.showMessageDialog(null, "data col int val:" + col.intValue());
					}

				}
				repCounts[index] = thisGroup.cols.size();
				// JOptionPane.showMessageDialog(null,"this avg bef:"+ave);
				// JOptionPane.showMessageDialog(null,"repind:"+repCounts[index]);
				ave /= repCounts[index];
				// JOptionPane.showMessageDialog(null,"this avg:"+ave);
				values[index] = ave;

				double diffSum = 0.0D;
				for (Integer col : thisGroup.cols) {
					diffSum += (data[col.intValue()] - ave) * (data[col.intValue()] - ave);
				}
				diffSum /= repCounts[index];
				stdDevs[index] = Math.sqrt(diffSum);
				index++;
			}
		}
	}

	public RepAveragedData getRepAveragedData(MetaOmProject myProject, int row) throws IOException {
		return new RepAveragedData(myProject, row);
	}

	public RepAveragedData getRepAveragedData(int row) throws IOException {
		return new RepAveragedData(this, row);
	}
	
	
	/**
	 * @author urmi add diff exp result to list to save with project
	 */
	public void addDiffExpRes(String id, DifferentialExpResults val) {
		if (diffExpRes == null) {
			diffExpRes = new HashMap<>();
		}
		diffExpRes.put(id, val);
	}
	
	/**
	 * @author urmi check if a diff exp result is saved with entered name
	 * @param name
	 * @return
	 */
	public boolean diffExpNameExists(String name) {
		boolean res = false;
		if (diffExpRes == null) {
			return res;
		}
		if (diffExpRes.get(name) != null) {
			res = true;
		}
		return res;
	}
	

	/**
	 * @author urmi add metacorr list to save later
	 */
	public void addMetaCorrRes(String id, CorrelationMetaCollection val) {
		if (metaCorrs == null) {
			metaCorrs = new HashMap<>();
		}
		metaCorrs.put(id, val);

	}

	/**
	 * @author urmi check if a correlation is saved with entered name
	 * @param name
	 * @return
	 */
	public boolean correlatioNameExists(String name) {
		boolean res = false;
		if (metaCorrs == null) {
			return res;
		}
		if (metaCorrs.get(name) != null) {
			res = true;
		}
		return res;
	}

	/**
	 * Return the map of name to correlation details
	 * 
	 * @return
	 */
	public HashMap<String, CorrelationMetaCollection> getMetaCorrRes() {
		return this.metaCorrs;
	}

	/**
	 * Return save correlation data as XML to save
	 * 
	 * @return
	 */
	public Element getMetaCorrResasXML() {
		Element root = new Element("ROOT");
		root.setAttribute("name", "Root");
		if (metaCorrs == null) {
			return root;
		}
		// add each saved corr to Root
		for (String s : metaCorrs.keySet()) {
			root.addContent(createCorrXMLNode(s));
		}
		// s
		return root;
	}

	public Element createCorrXMLNode(String s) {
		Element thisNode = new Element("Corr");
		CorrelationMetaCollection cmcObj = metaCorrs.get(s);

		// check values of table depending on cmcObj and populate the table
		thisNode.setAttribute("name", s);
		int corrTypeId = cmcObj.getCorrTypeId();
		thisNode.setAttribute("corrtype", String.valueOf(corrTypeId));
		thisNode.setAttribute("corrmodel", cmcObj.getCorrModel());
		thisNode.setAttribute("corrvar", cmcObj.getCorrAgainst());

		// for MI
		if (corrTypeId == 3) {
			thisNode.setAttribute("bins", String.valueOf(cmcObj.getBins()));
			thisNode.setAttribute("order", String.valueOf(cmcObj.getOrder()));
		}

		List<CorrelationMeta> corrList = cmcObj.getCorrList();
		if (metaCorrs != null) {
			if (corrTypeId == 0) {

				for (int i = 0; i < corrList.size(); i++) {
					CorrelationMeta thisObj = corrList.get(i);

					Element row = new Element("row");
					row.setAttribute("name", thisObj.getName());
					row.setAttribute("value", String.valueOf(thisObj.getrVal()));
					row.setAttribute("pvalue", String.valueOf(thisObj.getpVal()));
					row.setAttribute("zval", String.valueOf(thisObj.getzVal()));
					row.setAttribute("qval", String.valueOf(thisObj.getqVal()));
					row.setAttribute("pooledzr", String.valueOf(thisObj.getpooledzr()));
					row.setAttribute("stderr", String.valueOf(thisObj.getstdErr()));

					/*
					 * Element val = new Element("value");
					 * val.addContent(String.valueOf(thisObj.getrVal())); tName.addContent(val);
					 * Element pval = new Element("pvalue");
					 * pval.addContent(String.valueOf(thisObj.getpVal())); tName.addContent(pval);
					 * Element ci = new Element("ci");
					 * ci.addContent(String.valueOf(thisObj.getrCI(CorrelationMetaTable.getAlpha()))
					 * ); tName.addContent(ci); Element zval = new Element("zval");
					 * zval.addContent(String.valueOf(thisObj.getzVal())); tName.addContent(zval);
					 * Element qval = new Element("qval");
					 * qval.addContent(String.valueOf(thisObj.getqVal())); tName.addContent(qval);
					 */
					thisNode.addContent(row);

				}

			} else {

				for (int i = 0; i < corrList.size(); i++) {
					CorrelationMeta thisObj = corrList.get(i);

					Element row = new Element("row");
					row.setAttribute("name", thisObj.getName());
					row.setAttribute("value", String.valueOf(thisObj.getrVal()));
					row.setAttribute("pvalue", String.valueOf(thisObj.getpVal()));

					thisNode.addContent(row);

				}

			}
		}

		return thisNode;
	}

	public Element getParamsasXML() {
		Element root = new Element("ROOT");
		root.setAttribute("name", "Root");
		// add parameters to root
		Element perms = new Element("permutations");
		perms.setAttribute("value", String.valueOf(MetaOmGraph.getNumPermutations()));
		root.addContent(perms);

		Element threads = new Element("threads");
		threads.setAttribute("value", String.valueOf(MetaOmGraph.getNumThreads()));
		root.addContent(threads);

		// rpath is save as global param for MOG
		/*
		 * Element rpath = new Element("rpath"); rpath.setAttribute("value",
		 * String.valueOf(MetaOmGraph.getRPath())); rpath.setAttribute("default",
		 * String.valueOf(MetaOmGraph.defaultRpath)); root.addContent(rpath);
		 * 
		 * Element pathtorfiles = new Element("pathtorfiles");
		 * pathtorfiles.setAttribute("value",
		 * String.valueOf(MetaOmGraph.getpathtoRscrips()));
		 * root.addContent(pathtorfiles);
		 */

		// info about hyperlinked columns
		Element hyperlinks = new Element("hyperlinksCols");

		hyperlinks.setAttribute("srrColumn",
				String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getsrrColumn()));
		hyperlinks.setAttribute("srpColumn",
				String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getsrpColumn()));
		hyperlinks.setAttribute("srxColumn",
				String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getsrxColumn()));
		hyperlinks.setAttribute("srsColumn",
				String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getsrsColumn()));
		hyperlinks.setAttribute("gseColumn",
				String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getgseColumn()));
		hyperlinks.setAttribute("gsmColumn",
				String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getgsmColumn()));

		root.addContent(hyperlinks);

		return root;
	}

	public boolean setInfoColTypes(HashMap<String, Class> map) {
		this.infoColTypes = map;
		return true;
	}

	public HashMap<String, Class> getAllInfoColTypes() {
		return this.infoColTypes;
	}

	public Class getInfoColType(String colName) {
		if (infoColTypes == null) {
			// JOptionPane.showMessageDialog(null, "all null for:"+colName);
			return null;
		}
		/*
		 * for (String name : infoColTypes.keySet()) { String key = name.toString();
		 * String value = infoColTypes.get(name).toString();
		 * JOptionPane.showMessageDialog(null, "" + key + ": " + value);
		 * 
		 * }
		 */
		// JOptionPane.showMessageDialog(null, "type for:"+infoColTypes.get("PS_ID"));

		if (infoColTypes.containsKey(colName)) {
			// JOptionPane.showMessageDialog(null,
			// "cn:"+colName+"v:"+infoColTypes.get(colName));
			return infoColTypes.get(colName);
		}

		// JOptionPane.showMessageDialog(null, "null for:"+colName);
		return null;
	}

}
