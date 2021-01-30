
package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.chart.NewCustomSortDialog;
import edu.iastate.metnet.metaomgraph.chart.RangeMarker;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel;
import edu.iastate.metnet.metaomgraph.ui.MetadataEditor;
import edu.iastate.metnet.metaomgraph.ui.ParseTableTree;
import edu.iastate.metnet.metaomgraph.ui.ReadMetadata;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.MetNetUtils;
import edu.iastate.metnet.metaomgraph.utils.MetadataUpdater;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

import java.awt.Color;
import java.awt.EventQueue;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
//import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.apache.commons.io.FilenameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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
	private HashMap<String, Integer> rowMapping;
	private int infoColumns;
	private Long[] fileIndex;
	private File source = null;
	private MetadataCollection metaDataCollection = null;

	private boolean changed;

	private Hashtable<String, int[]> geneLists;
	private HashMap<String, ArrayList<String>> sampleDataLists;

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
	// save differential corr analysis results
	private HashMap<String, DifferentialCorrResults> diffCorrRes;

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
		sampleDataLists = new HashMap<String, ArrayList<String>>();
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
		sampleDataLists = new HashMap<String, ArrayList<String>>();
	}

	public MetaOmProject(File source, int infoColumns, char delimiter, boolean ignoreConsecutiveDelimiters,
			Double blankValue) {
		this(source, infoColumns, delimiter, ignoreConsecutiveDelimiters, blankValue, true);
	}

	public MetaOmProject(File projectFile) {
		streamMode = false;
		allowImport = true;
		initialized = openProject(projectFile);
		// urmi if project has just been opened set change to false
		setChanged(false);

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
			@Override
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

	/**
	 * @author urmi new save project
	 * @param destination
	 * @return
	 */
	public boolean saveProject(File destination) {
		String extension = Utils.getExtension(destination);
		File saveHere;
		String mogPathToSave = "";
		if ((extension != null) && ((extension.equals("mog")) || (extension.equals("mcg")))) {
			saveHere = new File(destination.getAbsolutePath() + ".tmp");
			mogPathToSave = destination.getAbsolutePath();
		} else {
			saveHere = new File(destination.getAbsolutePath() + ".mog.tmp");
			mogPathToSave = destination.getAbsolutePath() + ".mog.tmp";
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

		XMLOutputter output = new XMLOutputter();
		output.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));
//		Document myDoc = new Document(root);

		try {
			ZipOutputStream myZipOut = new ZipOutputStream(new FileOutputStream(saveHere));
			myZipOut.putNextEntry(new ZipEntry("ProjectFile.xml"));
//			output.output(myDoc, myZipOut);
			
			
			////
			////sTax parser
			
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xMLStreamWriter = xmlOutputFactory.createXMLStreamWriter(myZipOut);
			
			xMLStreamWriter.writeStartDocument();
			xMLStreamWriter.writeStartElement("MetaOmProject");
			xMLStreamWriter.writeStartElement("projectInfo");
			
			xMLStreamWriter.writeStartElement("sourcePath");
			xMLStreamWriter.writeCharacters(source.getParent());
	        xMLStreamWriter.writeEndElement();
	         
			xMLStreamWriter.writeStartElement("sourceFile");
			xMLStreamWriter.writeCharacters(source.getName());
	        xMLStreamWriter.writeEndElement();
	         
			xMLStreamWriter.writeStartElement("delimiter");
			if (delimiter == '\t') {
				xMLStreamWriter.writeCharacters("\\t");
			}
			else {
				xMLStreamWriter.writeCharacters(delimiter + "");
			}
	        xMLStreamWriter.writeEndElement();
	        
	        
	        xMLStreamWriter.writeStartElement("ignoreConsecutiveDelimiters");
			xMLStreamWriter.writeCharacters(ignoreConsecutiveDelimiters + "");
	        xMLStreamWriter.writeEndElement();
	        
	        
	        if (getBlankValue() != null) {
	        	
	        	xMLStreamWriter.writeStartElement("blankValue");
				xMLStreamWriter.writeCharacters(getBlankValue() + "");
		        xMLStreamWriter.writeEndElement();
		        
			}
	        
	        
	        xMLStreamWriter.writeStartElement("xLabel");
			xMLStreamWriter.writeCharacters(getDefaultXAxis());
	        xMLStreamWriter.writeEndElement();
	        
	        
	        xMLStreamWriter.writeStartElement("yLabel");
			xMLStreamWriter.writeCharacters(getDefaultYAxis());
	        xMLStreamWriter.writeEndElement();
	        
	        
	        xMLStreamWriter.writeStartElement("title");
			xMLStreamWriter.writeCharacters(getDefaultTitle());
	        xMLStreamWriter.writeEndElement();
	        
	        
	        xMLStreamWriter.writeStartElement("color1");
			xMLStreamWriter.writeCharacters(getColor1().getRGB() + "");
	        xMLStreamWriter.writeEndElement();
	        
	        
	        xMLStreamWriter.writeStartElement("color2");
			xMLStreamWriter.writeCharacters(getColor2().getRGB() + "");
	        xMLStreamWriter.writeEndElement();
	        
	        
	        xMLStreamWriter.writeStartElement("defaultColumn");
			xMLStreamWriter.writeCharacters(defaultColumn + "");
	        xMLStreamWriter.writeEndElement();
				
	        
	        for (int x = 0; x < infoColumns; x++) {
	        	
	        	xMLStreamWriter.writeStartElement("infoColumn");
				xMLStreamWriter.writeCharacters(columnHeaders[x]);
		        xMLStreamWriter.writeEndElement();
		        
			}
	        
			xMLStreamWriter.writeEndElement();
			
			
			//columns
			xMLStreamWriter.writeStartElement("columns");
			
			for (int x = 0; x < getDataColumnCount(); x++) {
				
				xMLStreamWriter.writeStartElement("column");
				xMLStreamWriter.writeCharacters(getDataColumnHeader(x));
		        xMLStreamWriter.writeEndElement();
		        
			}
			
	        xMLStreamWriter.writeEndElement();
			
			
	        
	        //data
	        boolean savedLast = false;
			for (int x = 0; x < rowNames.length; x++) {
				xMLStreamWriter.writeStartElement("data");
				
				for (int y = 0; y < infoColumns; y++) {
					
					if (rowNames[x][y] == null) {
						xMLStreamWriter.writeStartElement("info");
						xMLStreamWriter.writeCharacters("");
						xMLStreamWriter.writeEndElement();

					} else {
						xMLStreamWriter.writeStartElement("info");
						if ((rowNames[x][y] instanceof CorrelationValue)) {
							xMLStreamWriter.writeAttribute("type", "correlation");
							if ((hasLastCorrelation()) && (!savedLast)) {
								xMLStreamWriter.writeAttribute("last", "true");
								savedLast = true;
							}
							if (!((CorrelationValue) rowNames[x][y]).isAsPercent()) {
								xMLStreamWriter.writeAttribute("asPercent", "false");
							}
						}
						xMLStreamWriter.writeCharacters(rowNames[x][y].toString());
						xMLStreamWriter.writeEndElement();
					}
					
				}
				xMLStreamWriter.writeStartElement("location");
				xMLStreamWriter.writeCharacters(fileIndex[x] + "");
				xMLStreamWriter.writeEndElement();
				
				
				xMLStreamWriter.writeEndElement();
			}
	        
			

			
			Enumeration enumer = geneLists.keys();

			while (enumer.hasMoreElements()) {
				String name = enumer.nextElement().toString();
				xMLStreamWriter.writeStartElement("list");
				xMLStreamWriter.writeAttribute("name", name);
				
			
				int[] entries = geneLists.get(name);
				for (int x = 0; x < entries.length; x++) {
					
					xMLStreamWriter.writeStartElement("entry");
					xMLStreamWriter.writeCharacters(entries[x] + "");
					xMLStreamWriter.writeEndElement();
				}
					
				
				xMLStreamWriter.writeEndElement();
				
			}

			for (Map.Entry<String, ArrayList<String>> entry : sampleDataLists.entrySet()) {
				String listName = entry.getKey();
				xMLStreamWriter.writeStartElement("sampleDataList");
				xMLStreamWriter.writeAttribute("name", listName);
				
				ArrayList<String> values = entry.getValue();
				for (int index = 0; index < values.size(); index++) {
					
					xMLStreamWriter.writeStartElement("entry");
					xMLStreamWriter.writeCharacters(values.get(index) + "");
					xMLStreamWriter.writeEndElement();
					
				}
				
				xMLStreamWriter.writeEndElement();
			}

			if (savedSorts != null) {
				enumer = savedSorts.keys();
				while (enumer.hasMoreElements()) {
					String name = enumer.nextElement().toString();
					NewCustomSortDialog.CustomSortObject cso = getSavedSorts().get(name);
					cso.writeToXML(xMLStreamWriter, name);
				}
			}
			
			if (savedQueries != null) {
				enumer = savedQueries.keys();
				while (enumer.hasMoreElements()) {
					String name = enumer.nextElement().toString();
					TreeSearchQueryConstructionPanel.QuerySet thisQuerySet = getSavedQueries().get(name);
					thisQuerySet.writeToXML(xMLStreamWriter, name);
				}
			}
			
			
			if (savedExcludes != null) {
				enumer = savedExcludes.keys();
				while (enumer.hasMoreElements()) {
					String name = enumer.nextElement().toString();
					MetaOmAnalyzer.ExcludeData thisData = savedExcludes.get(name);
					thisData.writeToXML(xMLStreamWriter, name);
				}
			}
	        
			xMLStreamWriter.writeEndElement();
			
			
			//myZipOut.closeEntry();
			if (this.getMetadataHybrid() != null) {
				// write metadata
				myZipOut.putNextEntry(new ZipEntry("metadataFile.xml"));
				// metadata.outputToStream(myZipOut);
				this.getMetadataHybrid().generateFileInfo(xMLStreamWriter);
				//output.output(mdFileinfo, myZipOut);

				// write removed cols from md file
				myZipOut.putNextEntry(new ZipEntry("removedMDCols.xml"));
				//Document removedColsMD = new Document();
				
				this.getMetadataHybrid().writeListToXML(this.getMetadataHybrid().getRemovedMDCols(), xMLStreamWriter);
				//output.output(removedColsMD, myZipOut);

				// write exluded and missing rows from metadata
				myZipOut.putNextEntry(new ZipEntry("excludedMD.xml"));
				//Document excludedMD = new Document();
				this.getMetadataHybrid().writeListToXML(this.getMetadataHybrid().getExcludedMDRows(), xMLStreamWriter);
				//output.output(excludedMD, myZipOut);

				myZipOut.putNextEntry(new ZipEntry("missingMD.xml"));
				//Document missingMD = new Document();
				this.getMetadataHybrid().writeListToXML(this.getMetadataHybrid().getMissingMDRows(), xMLStreamWriter);
				//output.output(missingMD, myZipOut);

				// write tree
				myZipOut.putNextEntry(new ZipEntry("metadataTree.xml"));
				//Document treeStruct = new Document();
				this.getMetadataHybrid().writeJtreetoXML(this.getMetadataHybrid().getTreeStucture(), xMLStreamWriter);
				//output.output(treeStruct, myZipOut);

				// write saved correlations
				myZipOut.putNextEntry(new ZipEntry("correlations.xml"));
				//Document corrs = new Document();
				writeMetaCorrResasXML(xMLStreamWriter);
				//output.output(corrs, myZipOut);

				// write MOG parameters
				myZipOut.putNextEntry(new ZipEntry("params.xml"));
				//Document params = new Document();
				writeParamsasXML(xMLStreamWriter);
				//output.output(params, myZipOut);

				//write diff exp results
				myZipOut.putNextEntry(new ZipEntry("diffexpresults.xml"));
				//Document diffexpXML = new Document();
				writeDEResAsXML(xMLStreamWriter);
				//output.output(diffexpXML, myZipOut);

				// write diff corr results
				myZipOut.putNextEntry(new ZipEntry("diffcorrresults.xml"));
				//Document diffCorrXML = new Document();
				writeDiffCorrResAsXML(xMLStreamWriter);
				//output.output(diffCorrXML, myZipOut);

			}
			myZipOut.closeEntry();
			myZipOut.finish();
			myZipOut.close();
			
			
			//Move the temporary file to the actual mog project
			
			Path from = saveHere.toPath(); 
			Path to = Paths.get(mogPathToSave); 
			Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
			
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
			JOptionPane.showInternalMessageDialog(MetaOmGraph.getDesktop(),
					"Unable to save the project file.  Make sure the destination file is not write-protected.",
					"Error saving project", 0);
			return false;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			JOptionPane.showInternalMessageDialog(MetaOmGraph.getDesktop(),
					sw.toString(),
					"Error saving project", 0);
			return false;
		}
		setChanged(false);

		// Harsha - reproducibility log
		try {
			HashMap<String, Object> saveProjectParameters = new HashMap<String, Object>();
			saveProjectParameters.put("saveFilePath", destination.getAbsolutePath());
			// saveProjectParameters.put("parent",MetaOmGraph.getCurrentProjectActionId());

			HashMap<String, Object> result = new HashMap<String, Object>();
			result.put("result", "OK");
			ActionProperties saveProjectAction = new ActionProperties("save-project-as", saveProjectParameters, null,
					result, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			saveProjectAction.logActionProperties();
		} catch (Exception e) {
		}
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
		boolean diffExpResfound = false;
		boolean diffCorrResfound = false;
		BufferedReader inputReader;
		StringBuilder sb;
		String inline;
		SAXBuilder builder;
		XMLOutputter outter;
		JTree tree = null;
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
						// JOptionPane.showMessageDialog(null, "Error occured");
					}
					projectFileFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("metadataFile.xml")) && (!extendedFound)) {

					String fpath = "";
					String delim = "";
					String datacol = "";

					inputReader = new BufferedReader(new InputStreamReader(instream));

					XMLInputFactory factory = XMLInputFactory.newInstance();
					XMLEventReader eventReader = factory.createXMLEventReader(inputReader);


					while(eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();

						switch(event.getEventType()) {

						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String qName = startElement.getName().getLocalPart();

							if(qName.equals("FILEPATH")) {
								Iterator<Attribute> attributes = startElement.getAttributes();
								fpath = attributes.next().getValue();
							}
							else if(qName.equals("DELIMITER")) {
								Iterator<Attribute> attributes = startElement.getAttributes();
								delim = attributes.next().getValue();

							}
							else if(qName.equals("DATACOL")) {
								Iterator<Attribute> attributes = startElement.getAttributes();
								datacol = attributes.next().getValue();
							}

							break;


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
						metaDataCollection = new MetadataCollection(fpath, delim, datacol);
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
							metaDataCollection = new MetadataCollection(fpath, delim, datacol);
						} else {
							JOptionPane.showMessageDialog(null,
									"Please locate the metadata file. Click OK. " + mdFile.getName());
							JFileChooser fChooser = new JFileChooser(
									edu.iastate.metnet.metaomgraph.utils.Utils.getLastDir());
							int rVal = fChooser.showOpenDialog(MetaOmGraph.getMainWindow());
							if (rVal == JFileChooser.APPROVE_OPTION) {
								File source = fChooser.getSelectedFile();
								metaDataCollection = new MetadataCollection(source.getAbsolutePath(), delim, datacol);

							}

						}
					}


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
					Document mdTreeStruc = builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
					outter = new XMLOutputter();
					outter.setFormat(Format.getPrettyFormat());
					// org.jdom.Document res = new org.jdom.Document();
					// res.setRootElement(XMLroot);
					// String resDoc = outter.outputString(mdTreeStruc);
					// JOptionPane.showMessageDialog(null, resDoc);
					// call parse object
					if (metaDataCollection == null) {
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
					Document corr = builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
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


					XMLInputFactory factory = XMLInputFactory.newInstance();
					XMLEventReader eventReader = factory.createXMLEventReader(inputReader);

					while(eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();

						switch(event.getEventType()) {

						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String qName = startElement.getName().getLocalPart();

							if(qName.equals("permutations")) {
								Iterator<Attribute> attributes = startElement.getAttributes();

								while(attributes.hasNext()) {

									Attribute nextAttr = attributes.next();

									if(nextAttr.getName().getLocalPart().equals("value")) {
										MetaOmGraph.setNumPermutations(Integer.parseInt(nextAttr.getValue()));
									}

								}

							}
							else if(qName.equals("threads")) {
								Iterator<Attribute> attributes = startElement.getAttributes();

								while(attributes.hasNext()) {

									Attribute nextAttr = attributes.next();

									if(nextAttr.getName().getLocalPart().equals("value")) {
										MetaOmGraph.setNumThreads(Integer.parseInt(nextAttr.getValue()));
									}
								}


							}
							else if(qName.equals("hyperlinksCols")) {
								Iterator<Attribute> attributes = startElement.getAttributes();

								while(attributes.hasNext()) {

									Attribute nextAttr = attributes.next();

									if(nextAttr.getName().getLocalPart().equals("srrColumn")) {
										MetaOmGraph._SRR = Integer.parseInt(nextAttr.getValue());
									}
									else if(nextAttr.getName().getLocalPart().equals("srpColumn")) {
										MetaOmGraph._SRP = Integer.parseInt(nextAttr.getValue());
									}
									else if(nextAttr.getName().getLocalPart().equals("srxColumn")) {
										MetaOmGraph._SRX = Integer.parseInt(nextAttr.getValue());
									}
									else if(nextAttr.getName().getLocalPart().equals("srsColumn")) {
										MetaOmGraph._SRS = Integer.parseInt(nextAttr.getValue());
									}
									else if(nextAttr.getName().getLocalPart().equals("gseColumn")) {
										MetaOmGraph._GSE = Integer.parseInt(nextAttr.getValue());
									}
									else if(nextAttr.getName().getLocalPart().equals("gsmColumn")) {
										MetaOmGraph._GSM = Integer.parseInt(nextAttr.getValue());
									}
								}

							}

							break;


						} 
					}

					paramsFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("excludedMD.xml")) && (!excludedFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					
					XMLInputFactory factory = XMLInputFactory.newInstance();
					XMLEventReader eventReader = factory.createXMLEventReader(inputReader);
					excluded = new ArrayList<>();

					while(eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();

						switch(event.getEventType()) {

						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String qName = startElement.getName().getLocalPart();
							
							if(!qName.equals("ROOT")) {
								excluded.add(qName);
							}
							
							break;

						} 
					}

					excludedFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("missingMD.xml")) && (!missingFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					
					XMLInputFactory factory = XMLInputFactory.newInstance();
					XMLEventReader eventReader = factory.createXMLEventReader(inputReader);
					missing = new ArrayList<>();

					while(eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();

						switch(event.getEventType()) {

						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String qName = startElement.getName().getLocalPart();
							
							if(!qName.equals("ROOT")) {
								missing.add(qName);
							}
							
							break;

						} 
					}

					missingFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				} else if ((thisEntry.getName().equals("removedMDCols.xml")) && (!removedMDColsFound)) {
					inputReader = new BufferedReader(new InputStreamReader(instream));
					
					XMLInputFactory factory = XMLInputFactory.newInstance();
					XMLEventReader eventReader = factory.createXMLEventReader(inputReader);
					removedMDCols = new ArrayList<>();

					while(eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();

						switch(event.getEventType()) {

						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String qName = startElement.getName().getLocalPart();
							
							if(!qName.equals("ROOT")) {
								removedMDCols.add(qName);
							}
							
							break;

						} 
					}

					removedMDColsFound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));

				}

				// read diffexpresults
				else if ((thisEntry.getName().equals("diffexpresults.xml")) && (!diffExpResfound)) {
					// JOptionPane.showMessageDialog(null, "reading DE");
					inputReader = new BufferedReader(new InputStreamReader(instream));
					
					XMLInputFactory factory = XMLInputFactory.newInstance();
					XMLEventReader eventReader = factory.createXMLEventReader(inputReader);
					removedMDCols = new ArrayList<>();
					
					String currentDERootName = "";

					int method = 0;
					String g1name = "";
					String g2name = "";
					int g1size = 0;
					int g2size = 0;
					String flistname = "";
					String datatransform = "";
					
					boolean isrownames = false;
					boolean isgrp1 = false;
					boolean isgrp2 = false;
					boolean islogfc = false;
					boolean isfstat = false;
					boolean isfpval = false;
					boolean ispval = false;
					boolean isval = false;
					
					List<String> rowNamesdiff = null;
					List<Double> meangrp1 = null;
					List<Double> meangrp2 = null;
					List<Double> logFC = null;
					List<Double> fStat = null;
					List<Double> fPval = null;
					List<Double> pVal = null;
					
					while(eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();

						switch(event.getEventType()) {

						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String qName = startElement.getName().getLocalPart();
							
							boolean isDERoot = false;
							
							Iterator<Attribute> attributes = startElement.getAttributes();
							
							while(attributes.hasNext()) {
								Attribute attr = attributes.next();
								
								if(attr.getName().toString().equals("method")) {
									isDERoot = true;
									currentDERootName = qName;
									method = Integer.parseInt(attr.getValue());
								}
								else if(attr.getName().toString().equals("Group1")) {
									isDERoot = true;
									currentDERootName = qName;
									g1name = attr.getValue();
								}
								else if(attr.getName().toString().equals("Group2")) {
									isDERoot = true;
									currentDERootName = qName;
									g2name = attr.getValue();
								}
								else if(attr.getName().toString().equals("Group1Size")) {
									isDERoot = true;
									currentDERootName = qName;
									g1size = Integer.parseInt(attr.getValue());
								}
								else if(attr.getName().toString().equals("Group2Size")) {
									isDERoot = true;
									currentDERootName = qName;
									g2size = Integer.parseInt(attr.getValue());
								}
								else if(attr.getName().toString().equals("FeatureList")) {
									isDERoot = true;
									currentDERootName = qName;
									flistname = attr.getValue();
								}
								else if(attr.getName().toString().equals("DataTransform")) {
									isDERoot = true;
									currentDERootName = qName;
									datatransform = attr.getValue();
								}
								
							}
							
							if(isDERoot) {
								rowNamesdiff = new ArrayList<>();
								meangrp1 = new ArrayList<>();
								meangrp2 = new ArrayList<>();
								logFC = new ArrayList<>();
								fStat = new ArrayList<>();
								fPval = new ArrayList<>();
								pVal = new ArrayList<>();
								
							}
							
							
							if(qName.equals("rownames")) {
								isrownames = true;
							}
							else if(qName.equals("grp1")) {
								isgrp1 = true;
							}
							else if(qName.equals("grp2")) {
								isgrp2 = true;
							}
							else if(qName.equals("logfc")) {
								islogfc = true;
							}
							else if(qName.equals("fstat")) {
								isfstat = true;
							}
							else if(qName.equals("fpval")) {
								isfpval = true;
							}
							else if(qName.equals("pval")) {
								ispval = true;
							}
							else if(qName.equals("value")) {
								isval = true;
							}
							
							isDERoot = false;
							
							break;
							
						
						 case XMLStreamConstants.CHARACTERS:
			                  Characters characters = event.asCharacters();
			                  
			                  if(isrownames && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                	  rowNamesdiff.add(characters.getData());
			                	  }
			                  }
			                  else if(isgrp1 && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                	  meangrp1.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(isgrp2 && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                	  meangrp2.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(islogfc && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                	  logFC.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(isfstat && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                	  fStat.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(isfpval && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                	  fPval.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(ispval && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                	  pVal.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			               
			               break;
			               
						case XMLStreamConstants.END_ELEMENT:
			                  EndElement endElement = event.asEndElement();
			                  String endName = endElement.getName().getLocalPart();
			                  
			               if(endName.equalsIgnoreCase(currentDERootName)) {
			                  
			            	   DifferentialExpResults thisOb = new DifferentialExpResults(currentDERootName, method, g1name, g2name, g1size,
										g2size, flistname, datatransform, rowNamesdiff, meangrp1, meangrp2, fStat, fPval, pVal);

								// add this ob to saved DE
								addDiffExpRes(currentDERootName, thisOb);
								
			               }
			               else if(endName.equals("rownames")) {
								isrownames = false;
							}
							else if(endName.equals("grp1")) {
								isgrp1 = false;
							}
							else if(endName.equals("grp2")) {
								isgrp2 = false;
							}
							else if(endName.equals("logfc")) {
								islogfc = false;
							}
							else if(endName.equals("fstat")) {
								isfstat = false;
							}
							else if(endName.equals("fpval")) {
								isfpval = false;
							}
							else if(endName.equals("pval")) {
								ispval = false;
							}
							else if(endName.equals("value")) {
								isval = false;
							}
			               break;

						} 
					}
					
					

					diffExpResfound = true;
					instream = new ZipInputStream(new FileInputStream(projectFile));
				}

				// read diffcorr results diffcorrresults.xml
				else if ((thisEntry.getName().equals("diffcorrresults.xml")) && (!diffCorrResfound)) {
					// JOptionPane.showMessageDialog(null, "reading DE");
					inputReader = new BufferedReader(new InputStreamReader(instream));
					
					
					
					XMLInputFactory factory = XMLInputFactory.newInstance();
					XMLEventReader eventReader = factory.createXMLEventReader(inputReader);
					removedMDCols = new ArrayList<>();
					
					String currentDERootName = "";

					int method = 0;
					String g1name = "";
					String g2name = "";
					String flistname = "";
					String datatransform = "";
					String featureName = "";
					int featureInd = 0;
					
					boolean isrownames = false;
					boolean isgrp1samp = false;
					boolean isgrp2samp = false;
					boolean isgrp1corr = false;
					boolean isgrp2corr = false;
					boolean iszval1 = false;
					boolean iszval2 = false;
					boolean isdiffzvals = false;
					boolean iszscores = false;
					boolean ispvalues = false;
					boolean isval = false;
					
					List<String> rowNamesdiff = null;
					List<String> namesgrp1 = null;
					List<String> namesgrp2 = null;
					List<Double> corrgrp1 = null;
					List<Double> corrgrp2 = null;
					List<Double> zv1List = null;
					List<Double> zv2List = null;
					List<Double> diffList = null;
					List<Double> zsList = null;
					List<Double> pvList = null;
					
					while(eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();

						switch(event.getEventType()) {

						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String qName = startElement.getName().getLocalPart();
							
							boolean isDERoot = false;
							
							Iterator<Attribute> attributes = startElement.getAttributes();
							
							while(attributes.hasNext()) {
								Attribute attr = attributes.next();
								
								if(attr.getName().toString().equals("method")) {
									isDERoot = true;
									currentDERootName = qName;
									method = Integer.parseInt(attr.getValue());
								}
								else if(attr.getName().toString().equals("Group1")) {
									isDERoot = true;
									currentDERootName = qName;
									g1name = attr.getValue();
								}
								else if(attr.getName().toString().equals("Group2")) {
									isDERoot = true;
									currentDERootName = qName;
									g2name = attr.getValue();
								}
								else if(attr.getName().toString().equals("FeatureName")) {
									isDERoot = true;
									currentDERootName = qName;
									featureName = attr.getValue();
								}
								else if(attr.getName().toString().equals("DataTransform")) {
									isDERoot = true;
									currentDERootName = qName;
									datatransform = attr.getValue();
								}
								else if(attr.getName().toString().equals("FeatureList")) {
									isDERoot = true;
									currentDERootName = qName;
									flistname = attr.getValue();
								}
								else if(attr.getName().toString().equals("FeatureIndex")) {
									isDERoot = true;
									currentDERootName = qName;
									featureInd = Integer.parseInt(attr.getValue());
								}
								
							}
							
							if(isDERoot) {
								rowNamesdiff = new ArrayList<>();
								namesgrp1 = new ArrayList<>();
								namesgrp2 = new ArrayList<>();
								corrgrp1 = new ArrayList<>();
								corrgrp2 = new ArrayList<>();
								zv1List = new ArrayList<>();
								zv2List = new ArrayList<>();
								diffList = new ArrayList<>();
								zsList = new ArrayList<>();
								pvList = new ArrayList<>();
								
							}
							
							
							if(qName.equals("rownames")) {
								isrownames = true;
							}
							else if(qName.equals("grp1Samples")) {
								isgrp1samp = true;
							}
							else if(qName.equals("grp2Samples")) {
								isgrp2samp = true;
							}
							else if(qName.equals("grp1Corr")) {
								isgrp1corr = true;
							}
							else if(qName.equals("grp2Corr")) {
								isgrp2corr = true;
							}
							else if(qName.equals("zVals1")) {
								iszval1 = true;
							}
							else if(qName.equals("zVals2")) {
								iszval2 = true;
							}
							else if(qName.equals("diffzVals")) {
								isdiffzvals = true;
							}
							else if(qName.equals("zScores")) {
								iszscores = true;
							}
							else if(qName.equals("pValues")) {
								ispvalues = true;
							}
							else if(qName.equals("value")) {
								isval = true;
							}
							
							isDERoot = false;
							
							break;
							
						
						 case XMLStreamConstants.CHARACTERS:
			                  Characters characters = event.asCharacters();
			                  
			                  if(isrownames && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  rowNamesdiff.add(characters.getData());
			                	  }
			                  }
			                  else if(isgrp1samp && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  namesgrp1.add(characters.getData());
			                	  }
			                  }
			                  else if(isgrp2samp && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  namesgrp2.add(characters.getData());
			                	  }
			                  }
			                  else if(isgrp1corr && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  corrgrp1.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(isgrp2corr && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  corrgrp2.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(iszval1 && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  zv1List.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(iszval2 && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  zv2List.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(isdiffzvals && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  diffList.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(iszscores && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  zsList.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  else if(ispvalues && isval) {
			                	  if(characters.getData() != null && !characters.getData().equalsIgnoreCase("")) {
			                		  pvList.add(Double.parseDouble(characters.getData()));
			                	  }
			                  }
			                  
			               
			               break;
			               
						case XMLStreamConstants.END_ELEMENT:
			                  EndElement endElement = event.asEndElement();
			                  String endName = endElement.getName().getLocalPart();
			                  
			               if(endName.equalsIgnoreCase(currentDERootName)) {
			                  
			            	   DifferentialCorrResults thisOb = new DifferentialCorrResults(flistname, featureName, featureInd,
										namesgrp1, namesgrp2, g1name, g2name, method, rowNamesdiff, corrgrp1, corrgrp2, zv1List,
										zv2List, diffList, zsList, pvList, datatransform, currentDERootName);
								// add this ob to saved DE
								addDiffCorrRes(currentDERootName, thisOb);
								
			               }
			               else if(endName.equals("rownames")) {
								isrownames = false;
							}
							else if(endName.equals("grp1Samples")) {
								isgrp1samp = false;
							}
							else if(endName.equals("grp2Samples")) {
								isgrp2samp = false;
							}
							else if(endName.equals("grp1Corr")) {
								isgrp1corr = false;
							}
							else if(endName.equals("grp2Corr")) {
								isgrp2corr = false;
							}
							else if(endName.equals("zVals1")) {
								iszval1 = false;
							}
							else if(endName.equals("zVals2")) {
								iszval2 = false;
							}
							else if(endName.equals("diffzVals")) {
								isdiffzvals = false;
							}
							else if(endName.equals("zScores")) {
								iszscores = false;
							}
							else if(endName.equals("pValues")) {
								ispvalues = false;
							}
							else if(endName.equals("value")) {
								isval = false;
							}
			               break;

						} 
					}
					
					
					diffCorrResfound = true;
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
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			JOptionPane.showMessageDialog(null, sw.toString());
			
			return false;
		}
		ZipInputStream instream = null;
		setChanged(false);
		if (!extendedFound) {
			try {

				loadMetadata((InputStream) null);
			} catch (IOException e) {
				e.printStackTrace();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				
				JOptionPane.showMessageDialog(null, sw.toString());
			}
		}

		// for older project files
		if (!removedMDColsFound) {
			removedMDCols = new ArrayList<>();
		}

		if (allsWell && projectFileFound && extendedFound) {
			// if sample metadata is present then load these information
			try {

				metaDataCollection.removeUnusedCols(removedMDCols);

				metaDataCollection.removeDataPermanently(new HashSet<>(excluded));

				metaDataCollection.addNullData(missing);

				ParseTableTree ob = new ParseTableTree(metaDataCollection, tree, metaDataCollection.getDatacol(),
						this.getDataColumnHeaders());
				// JOptionPane.showMessageDialog(null, "to table tree");
				org.jdom.Document res = ob.tableToTree();

				// save and read repscolname
				// add
				// JOptionPane.showMessageDialog(null, "Creating MDH");

				loadMetadataHybrid(metaDataCollection, res.getRootElement(), ob.getTreeMap(),
						metaDataCollection.getDatacol(), ob.getMetadataHeaders(), tree, ob.getDefaultRepMap(),
						ob.getDefaultRepCol(), missing, excluded, removedMDCols);

			} catch (NullPointerException | IOException e) {
				// JOptionPane.showMessageDialog(null, "NPE error:");
				e.printStackTrace();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				
				JOptionPane.showMessageDialog(null, sw.toString());
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
						@Override
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
			
			boolean isprojectInfo = false;
			boolean issourcePath = false;
			boolean issourceFile = false;
			boolean isdelimiter = false;
			boolean isignoreConsecutiveDelimiters = false;
			boolean isblankValue = false;
			boolean isxLabel = false;
			boolean isyLabel = false;
			boolean istitle = false;
			boolean iscolor1 = false;
			boolean iscolor2 = false;
			boolean isdefaultColumn = false;
			boolean isinfoColumn = false;
			boolean isColumns = false;
			boolean isColumn = false;
			boolean isData = false;
			boolean isInfo = false;
			boolean isCorrelation = false;
			boolean isAsPercentFalse = false;
			boolean isLastTrue = false;
			boolean isList = false;
			boolean isLocation = false;
			boolean issampleDataList = false;
			boolean isCustomSort = false;
			boolean isOrder = false;
			boolean isMarker = false;
			boolean isStart = false;
			boolean isEnd = false;
			boolean isLabel = false;
			boolean isColor = false;
			boolean isQuerySet = false;
			boolean isQuery = false;
			boolean isField = false;
			boolean isTerm = false;
			boolean isExcludeList = false;
			boolean isEntry = false;
			
			String sourcePath = "";
			String sourceFile = "";
			String delimiterVal = "";
			String ignoreConsecutiveDelimitersVal = "";
			String blankValueVal = "";
			String xLabelVal = "";
			String yLabelVal = "";
			String titleVal = "";
			String color1Val = "";
			String color2Val = "";
			String defaultColumnVal = "";
			List infoColumnVal = new ArrayList();
			List columnsVal = new ArrayList();
			List<List> dataInfoList = new ArrayList<List>();
			List infoList = null;
			List<Long> fileIndexList = new ArrayList<Long>();
			String infoData = "";
			String listName = "";
			String dataListName = "";
			String savedSortName = "";
			String orderVal = "";
			String markerStyle = "";
			int startMarker = 0;
			int endMarker = 0;
			String labelMarker = "";
			Color colorMarker = null;
			String savedQueryName = "";
			boolean querySetMatchAll = false;
			SearchMatchType queryMatchType = SearchMatchType.CONTAINS;
			String queryField = "";
			String queryTerm = "";
			String savedExcludeName = "";
			List<Integer> entryList = null;
			ArrayList<String> dataListValues = null;
			ArrayList<RangeMarker> markerList = null;
			RangeMarker rangeMarker = null;
			ArrayList<MetadataQuery> queryList = null;
			MetadataQuery currentQuery = null;
			
			geneLists = new Hashtable();
			sampleDataLists = new HashMap<String, ArrayList<String>>();
			savedSorts = new Hashtable();
			savedQueries = new Hashtable();
			savedExcludes = new Hashtable();

			
			NewCustomSortDialog.CustomSortObject cso = null;
			TreeSearchQueryConstructionPanel.QuerySet thisQuerySet = null;
			MetaOmAnalyzer.ExcludeData thisExcludeData = null;
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(instream));

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(inputReader);


			while(eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				switch(event.getEventType()) {

				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					String qName = startElement.getName().getLocalPart();

					if(qName.equals("projectInfo")) {
						isprojectInfo = true;
					}
					else if(qName.equals("sourcePath")) {
						issourcePath = true;
					}
					else if(qName.equals("sourceFile")) {
						issourceFile = true;
					}
					else if(qName.equals("delimiter")) {
						isdelimiter = true;
					}
					else if(qName.equals("ignoreConsecutiveDelimiters")) {
						isignoreConsecutiveDelimiters = true;
					}
					else if(qName.equals("blankValue")) {
						isblankValue = true;
					}
					else if(qName.equals("xLabel")) {
						isxLabel = true;
					}
					else if(qName.equals("yLabel")) {
						isyLabel = true;
					}
					else if(qName.equals("title")) {
						istitle = true;
					}
					else if(qName.equals("color1")) {
						iscolor1 = true;
					}
					else if(qName.equals("color2")) {
						iscolor2 = true;
					}
					else if(qName.equals("defaultColumn")) {
						isdefaultColumn = true;
					}
					else if(qName.equals("infoColumn")) {
						isinfoColumn = true;
					}
					else if(qName.equals("columns")) {
						isColumns = true;
					}
					else if(qName.equals("column")) {
						isColumn = true;
					}
					else if(qName.equals("data")) {
						isData = true;
						infoData = null;
						infoList = new ArrayList();
					}
					else if(qName.equals("info")) {
						isInfo = true;
						infoData = null;
						Iterator<Attribute> attributes = startElement.getAttributes();
						
						while(attributes.hasNext()) {

							Attribute nextAttr = attributes.next();

							if(nextAttr.getName().getLocalPart().equals("type")) {
								if(nextAttr.getValue().equals("correlation")){
									isCorrelation = true;
								}
							}
							else if(nextAttr.getName().getLocalPart().equals("asPercent")) {
								if(nextAttr.getValue().equals("false")) {
									isAsPercentFalse = true;
								}
							}
							else if(nextAttr.getName().getLocalPart().equals("last")) {
								if(nextAttr.getValue().equals("true")) {
									isLastTrue = true;
								}
							}
						}
					}
					
					else if(qName.equals("location")) {
						isLocation = true;
					}
					
					else if(qName.equals("list")) {
						isList = true;
						
						Iterator<Attribute> attributes = startElement.getAttributes();
						
						listName = attributes.next().getValue();
						
						entryList = new ArrayList<Integer>();
						
					}
					
					else if( qName.equals("entry") ) {
						
						isEntry = true;
						
						
					}
					else if(qName.equals("sampleDataList")) {
						
						issampleDataList = true;
						
						Iterator<Attribute> attributes = startElement.getAttributes();
						
						dataListName = attributes.next().getValue();
						
						dataListValues = new ArrayList<String>();
						
					}
					else if(qName.equals(NewCustomSortDialog.CustomSortObject.getXMLElementName())) {
						
						isCustomSort = true;
						cso = new NewCustomSortDialog.CustomSortObject();
						markerList = new ArrayList<RangeMarker>();
						
						Iterator<Attribute> attributes = startElement.getAttributes();
						
						while(attributes.hasNext()) {

							Attribute nextAttr = attributes.next();

							if(nextAttr.getName().getLocalPart().equals("name")) {
								savedSortName = nextAttr.getValue();
							}
							
						}
						
					}
					else if(qName.equals("order")) {
						
						orderVal = "";
						isOrder = true;
					}
					else if(qName.equals("marker")) {
						
						Iterator<Attribute> attributes = startElement.getAttributes();
						
						while(attributes.hasNext()) {

							Attribute nextAttr = attributes.next();

							if(nextAttr.getName().getLocalPart().equals("style")) {
								markerStyle = nextAttr.getValue();
							}
							
						}
							
						isMarker = true;
					}
					else if(qName.equals("start")) {
						isStart = true;
					}
					else if(qName.equals("end")) {
						isEnd = true;
					}
					else if(qName.equals("label")) {
						isLabel = true;
					}	
					else if(qName.equals("color")) {
						isColor = true;
					}
					else if(qName.equals(TreeSearchQueryConstructionPanel.QuerySet.getXMLElementName())) {
						
						isQuerySet = true;
						thisQuerySet = new TreeSearchQueryConstructionPanel.QuerySet();
						queryList = new ArrayList<MetadataQuery>();
						
						Iterator<Attribute> attributes = startElement.getAttributes();
						
						while(attributes.hasNext()) {

							Attribute nextAttr = attributes.next();

							if(nextAttr.getName().getLocalPart().equals("name")) {
								savedQueryName = nextAttr.getValue();
							}
							else if(nextAttr.getName().getLocalPart().equals("matchAll")) {
								querySetMatchAll = "true".equals(nextAttr.getValue());
							}
							
						}
						
					}
					else if(qName.equals("query")) {
						isQuery = true;
						
						currentQuery = null;
						queryMatchType = SearchMatchType.CONTAINS;
						
						Iterator<Attribute> attributes = startElement.getAttributes();
						
						while(attributes.hasNext()) {

							Attribute nextAttr = attributes.next();

							if(nextAttr.getName().getLocalPart().equals("exact")) {
								boolean isExact = "true".equals(nextAttr.getValue());
								if(isExact)
									queryMatchType = SearchMatchType.IS;
								
							}
							else if(nextAttr.getName().getLocalPart().equals("matchAll")) {
								queryMatchType = SearchMatchType.valueOf(nextAttr.getValue().toUpperCase());
							}
							
						}
						
					}
					else if(qName.equals("field")) {
						isField = true;
					}
					else if(qName.equals("term")) {
						isTerm = true;
					}
					else if(qName.equals(MetaOmAnalyzer.ExcludeData.getXMLElementName())) {
						
						isExcludeList = true;
						thisExcludeData = new MetaOmAnalyzer.ExcludeData();
						
						Iterator<Attribute> attributes = startElement.getAttributes();
						
						while(attributes.hasNext()) {

							Attribute nextAttr = attributes.next();

							if(nextAttr.getName().getLocalPart().equals("name")) {
								savedExcludeName = nextAttr.getValue();
							}
							
						}
					}

					break;

				case XMLStreamConstants.CHARACTERS:
	                  Characters characters = event.asCharacters();
	                  
	                  if(issourcePath) {
	                	  sourcePath = characters.getData();
	                  }
	                  else if(issourceFile) {
	                	  sourceFile = characters.getData();
	                  }
	                  else if(isdelimiter) {
	                	  delimiterVal = characters.getData();
	                  }
	                  else if(isignoreConsecutiveDelimiters) {
	                	  ignoreConsecutiveDelimitersVal = characters.getData();
	                  }
	                  else if(isblankValue) {
	                	  blankValueVal = characters.getData();
	                  }
	                  else if(isxLabel) {
	                	  xLabelVal = characters.getData();
	                  }
	                  else if(isyLabel) {
	                	  yLabelVal = characters.getData();
	                  }
	                  else if(istitle) {
	                	  titleVal = characters.getData();
	                  }
	                  else if(iscolor1) {
	                	  color1Val = characters.getData();
	                  }
	                  else if(iscolor2) {
	                	  color2Val = characters.getData();
	                  }
	                  else if(isdefaultColumn) {
	                	  defaultColumnVal = characters.getData();
	                  }
	                  else if(isinfoColumn) {
	                	  infoColumnVal.add(characters.getData());
	                  }
	                  else if(isColumns && isColumn) {
	                	  columnsVal.add(characters.getData());
	                  }
	                  else if(isData && isInfo) {
	                	  infoData = characters.getData();
	                  }
	                  else if(isEntry && isList) {
	                	  
	                	  entryList.add(Integer.parseInt(characters.getData()));
	                  }
	                  else if(issampleDataList && isEntry) {
	                	  
	                	  dataListValues.add(characters.getData());
	                	  
	                  }
	                  else if(isLocation) {
	                	  fileIndexList.add(new Long(characters.getData()));
	                  }
	                  else if(isCustomSort && isOrder) {
	                	  
	                	  orderVal = characters.getData();
	                	  
	                  }
	                  else if(isCustomSort && isMarker && isStart) {
	                	  startMarker = Integer.parseInt(characters.getData());
	                  }
	                  else if(isCustomSort && isMarker && isEnd) {
	                	  endMarker = Integer.parseInt(characters.getData());
	                  }
	                  else if(isCustomSort && isMarker && isLabel) {
	                	  labelMarker = characters.getData();
	                  }
	                  else if(isCustomSort && isMarker && isColor) {
	                	  String colorText = characters.getData();
	                	  if (colorText != null) {
	                		  colorMarker = new Color(Integer.parseInt(colorText));
	                      } else {
	                    	  colorMarker = Color.BLACK;
	                      }
	                  }
	                  else if(isQuerySet && isQuery && isField) {
	                	  queryField = characters.getData();
	                  }
	                  else if(isQuerySet && isQuery && isTerm) {
	                	  queryTerm = characters.getData();
	                  }
	              
	                  break;
	             
				case XMLStreamConstants.END_ELEMENT:
					  EndElement endElement = event.asEndElement();
	                  String endName = endElement.getName().getLocalPart();
	                  
	                  if(endName.equals("projectInfo")) {
							isprojectInfo = false;
							
							//Getting source
							
							source = new File(sourcePath + File.separator + sourceFile);
							
							if (!source.exists()) {
								
							source = new File(projectFile.getParentFile().getAbsolutePath() + File.separator
														+ sourceFile);
							if (!source.exists()) {
								int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
										"The file " + source.getName() + " was not found.\nWould you like to locate it yourself?",
										"File not found", 0);
								if (result == 1)
									return false;
								JFileChooser chooser = new JFileChooser(projectFile.getParentFile());
								chooser.setFileFilter(new FileFilter() {
									@Override
									public boolean accept(File f) {
										return (f.isDirectory()) || (f.getName().equals(source.getName()));
									}

									@Override
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
							
							
							//delimiter
							
							if (delimiterVal.equals("")) {
								delimiter = ' ';
							} else if (delimiterVal.equals("\\t")) {
								delimiter = '\t';
							} else
								delimiter = delimiterVal.charAt(0);
							
							
							
							//populate ignoreConsecutiveDelimiters
							
							if (!ignoreConsecutiveDelimitersVal.equals("")) {
								ignoreConsecutiveDelimiters = Boolean.parseBoolean(ignoreConsecutiveDelimitersVal);
							} else {
								ignoreConsecutiveDelimiters = true;
							}
							
							
							
							//populate blankValue
							
							if (!blankValueVal.equals("")) {
								blankValue = Double.valueOf(Double.parseDouble(blankValueVal));
							}
							
							
							//populate other values
							defaultXAxis = xLabelVal;
							defaultYAxis = yLabelVal;
							defaultTitle = titleVal;
							color1 = new Color(Integer.parseInt(color1Val));
							color2 = new Color(Integer.parseInt(color2Val));
							
							
							//populate defaultColumn
							if (defaultColumnVal.equals("")) {
								defaultColumn = 0;
							} else {
								defaultColumn = Integer.parseInt(defaultColumnVal);
							}
							
							
							infoColumns = infoColumnVal.size();
							
							
							
							
						}
	                  else if(endName.equals("sourcePath")) {
							issourcePath = false;
						}
						else if(endName.equals("sourceFile")) {
							issourceFile = false;
						}
						else if(endName.equals("delimiter")) {
							isdelimiter = false;
						}
						else if(endName.equals("ignoreConsecutiveDelimiters")) {
							isignoreConsecutiveDelimiters = false;
						}
						else if(endName.equals("blankValue")) {
							isblankValue = false;
						}
						else if(endName.equals("xLabel")) {
							isxLabel = false;
						}
						else if(endName.equals("yLabel")) {
							isyLabel = false;
						}
						else if(endName.equals("title")) {
							istitle = false;
						}
						else if(endName.equals("color1")) {
							iscolor1 = false;
						}
						else if(endName.equals("color2")) {
							iscolor2 = false;
						}
						else if(endName.equals("defaultColumn")) {
							isdefaultColumn = false;
						}
						else if(endName.equals("infoColumn")) {
							isinfoColumn = false;
						}
						else if(endName.equals("columns")) {
							isColumns = false;
							
							columnHeaders = new String[infoColumns + columnsVal.size()];
							Iterator iter = infoColumnVal.iterator();
							int index = 0;
							while (iter.hasNext()) {
								columnHeaders[index] = (String)iter.next();
								index++;
							}
							System.out.println(columnsVal.size() + " columns");
							iter = columnsVal.iterator();
							while (iter.hasNext()) {
								columnHeaders[index] = (String)iter.next();
								if (columnHeaders[index].length() > maxNameLength)
									maxNameLength = columnHeaders[index].length();
								index++;
							}
							
							
						}
						else if(endName.equals("column")) {
							isColumn = false;
						}
						else if(endName.equals("data")) {
							isData = false;
							
							dataInfoList.add(infoList);
							
						}
						else if(endName.equals("info")) {
							isInfo = false;
							
							if(isCorrelation) {
								
								CorrelationValue thisValue;
								
								if(isAsPercentFalse) {
									if (infoData != null && infoData.length() > 0) {
										thisValue = new CorrelationValue(Double.parseDouble(infoData));
									} else {
										thisValue = null;
									}
									thisValue.setAsPercent(false);
								}
								else {
									
									if (infoData != null && infoData.length() > 0) {
										thisValue = new CorrelationValue(
												Double.parseDouble(infoData.substring(0, infoData.length() - 1)) / 100.0D);
									} else {
										thisValue = null;
									}
								}
									infoList.add(thisValue);
									
									
									if (isLastTrue) {
										hasLastCorrelation = true;
									}
								
							}
							else if (infoData != null && infoData.length() > 0) {
								infoList.add(infoData);
								
							} else {
								infoList.add(null);
								
							}
							
							
							isCorrelation = false;
							isAsPercentFalse = false;
							
						}
	                  
						else if(endName.equals("location")) {
							isLocation = false;
						}
	                  
						else if(endName.equals("list")) {
							isList = false;
							
							int [] entryArr = new int[entryList.size()];
							
							for(int i=0; i<entryList.size(); i++) {
								entryArr[i] = entryList.get(i);
							}
							
							geneLists.put(listName, entryArr);
						}
						else if(endName.equals("sampleDataList")) {
							issampleDataList = false;
							
							sampleDataLists.put(dataListName, dataListValues);
						}
						else if(endName.equals("entry")) {
							isEntry = false;
						}
						else if(endName.equals("order")) {
							isOrder = false;
						}
						else if(endName.equals("marker")) {
							isMarker = false;
							
							int style = "horizontal".equals(markerStyle) ? RangeMarker.HORIZONTAL : RangeMarker.VERTICAL;
							
							rangeMarker = new RangeMarker(startMarker, endMarker, labelMarker, style, colorMarker);
							
							markerList.add(rangeMarker);
							
						}
						else if(endName.equals(NewCustomSortDialog.CustomSortObject.getXMLElementName())) {
							isCustomSort = false;
							
							cso.readFromXML(orderVal, markerList);
							savedSorts.put(savedSortName, cso);
							
						}
						else if(endName.equals("start")) {
							
							isStart = false;
						}
						else if(endName.equals("end")) {
							
							isEnd = false;
						}
						else if(endName.equals("label")) {
							
							isLabel = false;
						}	
						else if(endName.equals("color")) {
							
							isColor = false;
						}
	                  
						else if(endName.equals(TreeSearchQueryConstructionPanel.QuerySet.getXMLElementName())) {
							
							isQuerySet = false;
							
							thisQuerySet.initializeQuerySet(querySetMatchAll, queryList);
							
							savedQueries.put(savedQueryName, thisQuerySet);
							
						}
						else if(endName.equals("query")) {
							isQuery = false;
							
							currentQuery = new MetadataQuery(queryField, queryTerm, queryMatchType, false);
							queryList.add(currentQuery);
							
						}
						else if(endName.equals("field")) {
							isField = false;
						}
						else if(endName.equals("term")) {
							isTerm = false;
						}
						else if(endName.equals("MetaOmProject")) {
							
							rowNames = new Object[dataInfoList.size()][infoColumns];
							fileIndex = new Long[dataInfoList.size()];
							
							
							
							for(int i=0; i<dataInfoList.size(); i++) {
								List currentDataList = dataInfoList.get(i);
								
								for(int j=0; j< currentDataList.size(); j++) {
									
									rowNames[i][j] = currentDataList.get(j);
								}
							}
							
							
							for(int i=0; i<fileIndexList.size(); i++) {
								fileIndex[i] = fileIndexList.get(i);
							}
							
							
						}
	                  
	                  break;

				} 
			}
		}


		catch (NullPointerException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "The file " + projectFile.getName()
			+ " is either not a MetaOmGraph project " + "file, or it is missing required data.");
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			JOptionPane.showMessageDialog(null, sw.toString());
			
			return false;
		} 
		catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			JOptionPane.showMessageDialog(null, sw.toString());
			
			e.printStackTrace();
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
		if (entry < 0) {
			return null;
		}
		if (infoColumns == 0) {
			String[][] result = new String[rowNames.length][1];
			for (int x = 0; x < result.length; x++)
				result[x][0] = (x + 1) + "";
			return result;
		}
		return rowNames[entry];
	}

	/**
	 * returns the row data from the feature metadata table as Object[]
	 * 
	 * @param entry
	 * @return
	 */
	public Object[] getRowName(int entry) {
		if (entry < 0) {
			return null;
		}
		if (infoColumns == 0) {
			String[] result = new String[1];
			result[0] = (entry + 1) + "";
			return result;
		}
		return rowNames[entry];
	}

	/**
	 * Returns the indices of correlation columns
	 * 
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
	 * 
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

	/**
	 * Add sample data list.
	 * 
	 * @param name
	 *            name of the sample data list.
	 * @param entries
	 *            values of the list.
	 * @param notify
	 *            notify the listeners
	 * @param logRequired
	 * @return
	 */
	public boolean addSampleDataList(String name, String[] entries, boolean notify, boolean logRequired) {
		String listName = name;
		if ((listName == null) || (listName.trim().equals(""))) {
			String result = "";
			while ((result != null) && (result.equals(""))) {
				result = JOptionPane.showInputDialog(MetaOmGraph.getMainWindow(),
						"Please enter a name for this sample data list", "Create new sampledata list", 2);
				if (result != null)
					result = result.trim();
				if (sampleDataLists.containsKey(result)) {
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
		ArrayList<String> values = new ArrayList<String>(Arrays.asList(entries));
		sampleDataLists.put(listName, values);
		setChanged(true);
		if (notify) {
			fireStateChanged("create sample data list");
		}
		// TODO add log
		return true;
	}

	/**
	 * Add gene list.
	 * 
	 * @param name
	 *            name of the list.
	 * @param entries
	 *            values of the list.
	 * @param notify
	 *            notify the listeners.
	 * @param logRequired
	 * @return
	 */
	public boolean addGeneList(String name, int[] entries, boolean notify, boolean logRequired) {
		String listName = name;
		if ((listName == null) || (listName.trim().equals(""))) {
			String result = "";
			while ((result != null) && (result.equals(""))) {
				result = JOptionPane.showInputDialog(MetaOmGraph.getMainWindow(),
						"Please enter a name for this gene list", "Create new gene list", 2);
				if (result != null)
					result = result.trim();
				if (getGeneListRowNumbers(result) != null) {
					JOptionPane.showMessageDialog(null,
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

		try {
			// Harsha - reproducibility log
			HashMap<String, Object> actionMap = new HashMap<String, Object>();
			actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());
			actionMap.put("section", "Feature Metadata");

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("Created List Name", listName);
			dataMap.put("List Elements Count", entries.length);
			Map<Integer, String> selectedItems = new HashMap<Integer, String>();

			for (int rowNum : entries) {
				selectedItems.put(rowNum, getDefaultRowNames(rowNum));
			}
			dataMap.put("Selected Rows", selectedItems);
			HashMap<String, Object> resultLog = new HashMap<String, Object>();
			resultLog.put("result", "OK");

			ActionProperties createListAction = new ActionProperties("create-list", actionMap, dataMap, resultLog,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));

			if (logRequired) {
				createListAction.logActionProperties();
			}
		} catch (Exception e) {
		}

		return true;
	}

	// Rename the sample data list name.
	public boolean renameSampleDataList(String oldName, String newName) {
		if ((newName == null) || (newName.trim().equals(""))) {
			String result = "";
			while ((result != null) && (result.equals(""))) {
				result = (String) JOptionPane.showInputDialog(MetaOmGraph.getMainWindow(),
						"Please enter a new name for this sample data list", "Create new sample data list", 3, null,
						null, oldName);
				if (result != null) {
					result = result.trim();
					if (result.equals(oldName))
						return true;
					if (sampleDataLists.containsKey(result)) {
						JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
								"A list with that name already exists.  Please enter a different name.",
								"Duplicate list name", 0);
						result = "";
					}
				}
			}
			if (result == null)
				return false;
			newName = result;
		}
		ArrayList<String> values = sampleDataLists.get(oldName);
		sampleDataLists.remove(oldName);
		sampleDataLists.put(newName, values);
		setChanged(true);
		fireStateChanged("rename sample data list");
		// TODO add log.
		return true;
	}

	// Rename the gene list name.
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

		try {
			// Harsha - reproducibility log
			HashMap<String, Object> actionMap = new HashMap<String, Object>();
			actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("Old List Name", oldName);
			dataMap.put("New List Name", listName);
			dataMap.put("List Elements Count", entries.length);
			Map<Integer, String> selectedItems = new HashMap<Integer, String>();

			for (int rowNum : entries) {
				selectedItems.put(rowNum, getDefaultRowNames(rowNum));
			}
			dataMap.put("Selected Rows", selectedItems);
			HashMap<String, Object> resultLog = new HashMap<String, Object>();
			resultLog.put("result", "OK");

			ActionProperties createListAction = new ActionProperties("rename-list", actionMap, dataMap, resultLog,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			createListAction.logActionProperties();
		} catch (Exception e) {

		}
		return true;
	}

	/**
	 * Gets all the sample data list names.
	 * 
	 * @return all sample data lists.
	 */
	public String[] getSampleDataListNames() {
		String[] result = null;
		//urmi handle new projects
		if (sampleDataLists == null) {
			result = new String[1];
			result[0] = "Complete List";
			return result;
		}

		Set<String> listNames = sampleDataLists.keySet();
		result = new String[listNames.size() + 1];
		result[0] = "Complete List";
		int index = 1;
		for (String listName : listNames) {
			result[index] = listName;
			index++;
		}
		return result;
	}

	/**
	 * Gets all gene list names.
	 * 
	 * @return all gene lists.
	 */
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

	/**
	 * Get the rows/values of the sample data list.
	 * 
	 * @param name
	 *            name of the list.
	 * @return list of row names for a sample data list.
	 */
	public List<String> getSampleDataListRowNames(String name) {
		if (name == null)
			return null;
		if (name.equals("Complete List")) {
			return metaDataCollection.getAllDataCols();
		}
		return sampleDataLists.get(name);
	}

	/**
	 * Get the rows/values of the gene list.
	 * 
	 * @param name
	 *            name of the list.
	 * @return list of row indices for a gene list.
	 */
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
		return addGeneList(null, entries, true, true);
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
		return addGeneList(name, result, true, true);
	}

	/*
	 * delete a list from the sample data list.
	 */
	public void deleteSampleDataList(String name) {
		sampleDataLists.remove(name);
		setChanged(true);
		fireStateChanged("delete sample data list");
	}

	public void deleteGeneList(String name) {
		geneLists.remove(name);
		setChanged(true);
		fireStateChanged("delete list");

		// Harsha - reproducibility log
		try {
			HashMap<String, Object> actionMap = new HashMap<String, Object>();
			actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("deletedListName", name);
			HashMap<String, Object> resultLog = new HashMap<String, Object>();
			resultLog.put("result", "OK");

			ActionProperties deleteListAction = new ActionProperties("delete-list", actionMap, dataMap, resultLog,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			deleteListAction.logActionProperties();
		} catch (Exception e) {

		}
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

		// use list in case of duplicates in names list
		List<Integer> res = new ArrayList<>();

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

		int[] temp = new int[res.size()];
		int k = 0;
		for (int i : res) {
			temp[k++] = i;
		}
		return temp;
	}
	
	
	/**
	 * faster method to return index of rows matching a list of names
	 * 
	 * @param names
	 * @return
	 */
	
	public int[] getRowIndexesFromFeatureNames(List<String> names, boolean matchCase) {
		
		if(rowMapping == null) {
			populateRowMapping();
		}
		
		if (names == null)
			return null;

		// use list in case of duplicates in names list
		List<Integer> res = new ArrayList<>();

		if (matchCase) {
			for (int i = 0; i < names.size(); i++) {
					int val = rowMapping.get(names.get(i));
					res.add(val);
			}
		} else {
			for (String s : names) {
				res.add(getRowIndexbyName(s, true));
			}
		}

		int[] temp = new int[res.size()];
		int k = 0;
		for (int i : res) {
			temp[k++] = i;
		}
		return temp;
		
	}
	
	
	public void populateRowMapping() {
		
		rowMapping = new HashMap<String, Integer>();
		
		if(rowNames != null) {
		for (int i = 0; i < rowNames.length; i++) {
			String thisName = rowNames[i][defaultColumn].toString();
			
			rowMapping.put(thisName, i);
		}
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param matchCase
	 * @return
	 */
	

	public int getRowIndexbyName(String name, boolean matchCase) {
		if (name == null)
			return -1;

		String[] allRownames = getAllDefaultRowNames();

		for (int k = 0; k < allRownames.length; k++) {
			String thisName = allRownames[k];

			if (!matchCase) {
				thisName = thisName.toLowerCase();
				name = name.toLowerCase();
			}
			if (name.equals(thisName)) {
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
					@Override
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
		metaDataCollection = obj;
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
			metaDataCollection = metadataH.getMetadataCollection();
		}
		this.defaultXAxis = dataCol;
		// JOptionPane.showMessageDialog(null, "in loadMetadataHybrid");
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
		// JOptionPane.showMessageDialog(null, "in loadMetadata");
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
			// JOptionPane.showMessageDialog(null, "Reading row " + row + " from memory");
			System.out.println("Reading row " + row + " from memory");
			return data[memoryMap.get(Integer.valueOf(row)).intValue()];
		}
		if (dataIn == null) {
			// JOptionPane.showMessageDialog(null, "datain isNULL");
			dataIn = new RandomAccessFile(getSourceFile().getAbsolutePath(), "r", 20000);
		}
		double[] thisData = new double[getDataColumnCount()];
		dataIn.seek(getFileIndex(row));
		for (int x = 0; x < thisData.length; x++) {
			String tmp = Utils.clean(dataIn.readString(delimiter, ignoreConsecutiveDelimiters));
			try {
				thisData[x] = Double.parseDouble(tmp);
			} catch (NumberFormatException | NullPointerException nfe) {
				// JOptionPane.showMessageDialog(null, "tmp:"+tmp+" delm:"+delimiter+"
				// ig:"+ignoreConsecutiveDelimiters);
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

	/**
	 * @author urmi
	 */
	/*
	 * public void setdataInNull() { this.dataIn=null; }
	 */

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
	 * @author urmi
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
		if (exclude == null || exclude.length<1) {
			return data;
		}
		//find total included columns
		int cnt=0;
		for (int j = 0; j < exclude.length; j++) {
			if (!exclude[j]) {
				cnt++;
			}
		}
		//create result array
		double[] result = new double[cnt];
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
	 * this function will return data of all rows for the given columns e.g. get data
	 * for all genes for a given run
	 * 
	 * @param selectedCols selected sample data column indices
	 * @param selectedList selected gene list name
	 * @return
	 * @throws IOException
	 */
	public HashMap<Integer, double[]> getSelectedListRowData(int[] selectedCols, String selectedList) throws IOException{
		int[]  rowNums = getGeneListRowNumbers(selectedList);
		HashMap<Integer, double[]> res = new HashMap<>();

		for (int i = 0; i < selectedCols.length; i++) {
			double[] temp = new double[rowNums.length];
			res.put(selectedCols[i], temp);
		}

		for(int i = 0; i < rowNums.length; i++) {
			double[] rowData = getAllData(rowNums[i]);
			for(int j = 0; j < selectedCols.length; j++) {
				res.get(selectedCols[j])[i] = rowData[selectedCols[j]];
			}
		}
		return res;
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
				addGeneList(name, rows, false, false);

				try {
					// Harsha - reproducibility log
					HashMap<String, Object> actionMap = new HashMap<String, Object>();
					actionMap.put("parent", MetaOmGraph.getCurrentProjectActionId());

					HashMap<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put("Source File", source.getAbsolutePath());
					dataMap.put("Created List Name", name);
					dataMap.put("List Elements Count", rows.length);

					HashMap<String, Object> resultLog = new HashMap<String, Object>();
					resultLog.put("result", "OK");

					ActionProperties createListAction = new ActionProperties("import-lists", actionMap, dataMap,
							resultLog, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					createListAction.logActionProperties();
				} catch (Exception e) {

				}

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
			// JOptionPane.showMessageDialog(null, "called");
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
		// project changed
		setChanged(true);
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

	public String[] getSavedDiffExpResNames() {
		if (diffExpRes == null) {
			return null;
		}

		Set thisSet = diffExpRes.keySet();
		String[] res = new String[thisSet.size()];
		int k = 0;
		for (Object s : thisSet) {
			res[k++] = (String) s;
		}
		return res;
	}

	public DifferentialExpResults getDiffExpResObj(String key) {
		if (diffExpRes == null) {
			return null;
		}
		return diffExpRes.get(key);
	}

	/**
	 * remove diff exp object
	 * 
	 * @param key
	 */
	public void removeDifferentialExpResults(String key) {

		if (diffExpRes == null) {
			return;
		}
		diffExpRes.remove(key);
	}

	//////////////////////

	/**
	 * @author urmi add diff corr result to list to save with project
	 */
	public void addDiffCorrRes(String id, DifferentialCorrResults val) {
		if (diffCorrRes == null) {
			diffCorrRes = new HashMap<>();
		}
		diffCorrRes.put(id, val);
		// project changed
		setChanged(true);
	}

	/**
	 * @author urmi check if a diff exp result is saved with entered name
	 * @param name
	 * @return
	 */
	public boolean diffCorrNameExists(String name) {
		boolean res = false;
		if (diffCorrRes == null) {
			return res;
		}
		if (diffCorrRes.get(name) != null) {
			res = true;
		}
		return res;
	}

	public String[] getSavedDiffCorrResNames() {
		if (diffCorrRes == null) {
			return null;
		}

		Set thisSet = diffCorrRes.keySet();
		String[] res = new String[thisSet.size()];
		int k = 0;
		for (Object s : thisSet) {
			res[k++] = (String) s;
		}
		return res;
	}

	public DifferentialCorrResults getDiffCorrResObj(String key) {
		if (diffCorrRes == null) {
			return null;
		}
		return diffCorrRes.get(key);
	}

	public void removeDiffCorrResults(String key) {
		if (diffCorrRes == null) {
			return;
		}
		diffCorrRes.remove(key);
	}

	/**
	 * @author urmi add metacorr list to save later
	 */
	public void addMetaCorrRes(String id, CorrelationMetaCollection val) {
		if (metaCorrs == null) {
			metaCorrs = new HashMap<>();
		}
		metaCorrs.put(id, val);
		// project changed
		setChanged(true);
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
	
	/**
	 * write correlation data as XML
	 * 
	 * @return
	 * @throws XMLStreamException 
	 */
	public void writeMetaCorrResasXML(XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
		
		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement("ROOT");
		
		xMLStreamWriter.writeAttribute("name", "Root");
		
		if (metaCorrs != null) {
			
		// add each saved corr to Root
		for (String s : metaCorrs.keySet()) {
			writeCorrXMLNode(s, xMLStreamWriter);
		}
		
		}
		
		xMLStreamWriter.writeEndElement();
		
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
	
	
	
	
	public void writeCorrXMLNode(String s, XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
		
		xMLStreamWriter.writeStartElement("Corr");
		xMLStreamWriter.writeAttribute("name", s);
		
		CorrelationMetaCollection cmcObj = metaCorrs.get(s);

		// check values of table depending on cmcObj and populate the table
		int corrTypeId = cmcObj.getCorrTypeId();
		xMLStreamWriter.writeAttribute("corrtype", String.valueOf(corrTypeId));
		xMLStreamWriter.writeAttribute("corrmodel", cmcObj.getCorrModel());
		xMLStreamWriter.writeAttribute("corrvar", cmcObj.getCorrAgainst());

		// for MI
		if (corrTypeId == 3) {
			xMLStreamWriter.writeAttribute("bins", String.valueOf(cmcObj.getBins()));
			xMLStreamWriter.writeAttribute("order", String.valueOf(cmcObj.getOrder()));
		}

		List<CorrelationMeta> corrList = cmcObj.getCorrList();
		if (metaCorrs != null) {
			if (corrTypeId == 0) {

				for (int i = 0; i < corrList.size(); i++) {
					CorrelationMeta thisObj = corrList.get(i);

					xMLStreamWriter.writeStartElement("row");
					xMLStreamWriter.writeAttribute("name", thisObj.getName());
					xMLStreamWriter.writeAttribute("value", String.valueOf(thisObj.getrVal()));
					xMLStreamWriter.writeAttribute("pvalue", String.valueOf(thisObj.getpVal()));
					xMLStreamWriter.writeAttribute("zval", String.valueOf(thisObj.getzVal()));
					xMLStreamWriter.writeAttribute("qval", String.valueOf(thisObj.getqVal()));
					xMLStreamWriter.writeAttribute("pooledzr", String.valueOf(thisObj.getpooledzr()));
					xMLStreamWriter.writeAttribute("stderr", String.valueOf(thisObj.getstdErr()));
					
					
					xMLStreamWriter.writeEndElement();

				}

			} else {

				for (int i = 0; i < corrList.size(); i++) {
					CorrelationMeta thisObj = corrList.get(i);

					xMLStreamWriter.writeStartElement("row");
					xMLStreamWriter.writeAttribute("name", thisObj.getName());
					xMLStreamWriter.writeAttribute("value", String.valueOf(thisObj.getrVal()));
					xMLStreamWriter.writeAttribute("pvalue", String.valueOf(thisObj.getpVal()));
					
					xMLStreamWriter.writeEndElement();

				}

			}
		}

		xMLStreamWriter.writeEndElement();
	}
	
	

	/**
	 * get saved differential expression results as XML
	 * 
	 * @return
	 * @throws XMLStreamException 
	 */
	
	
public Element getDEResAsXML() {
			
		Element root = new Element("ROOT");
		root.setAttribute("name", "Root");
		if (diffExpRes != null) {
			String[] savedDE = getSavedDiffExpResNames();
			for (String id : savedDE) {
				DifferentialExpResults thisOB = getDiffExpResObj(id);
				root.addContent(thisOB.getAsXMLNode());
			}
		}

		return root;
	}


	public void writeDEResAsXML(XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
		
		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement("ROOT");
		xMLStreamWriter.writeAttribute("name", "Root");
		
		if (diffExpRes != null) {
			String[] savedDE = getSavedDiffExpResNames();
			for (String id : savedDE) {
				DifferentialExpResults thisOB = getDiffExpResObj(id);
				thisOB.writeAsXMLNode(xMLStreamWriter);
			}
		}
		xMLStreamWriter.writeEndElement();

	}

	/**
	 * get saved differential expression results as XML
	 * 
	 * @return
	 */
	public void writeDiffCorrResAsXML(XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
		
		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement("ROOT");
		xMLStreamWriter.writeAttribute("name", "Root");
		
		if (diffCorrRes != null) {
			String[] savedDC = getSavedDiffCorrResNames();
			for (String id : savedDC) {
				DifferentialCorrResults thisOB = getDiffCorrResObj(id);
				thisOB.writeAsXMLNode(xMLStreamWriter);
			}
		}
		xMLStreamWriter.writeEndElement();
		
	}

	/**
	 * return MOG parameters as XML
	 * 
	 * @return
	 */
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

	
	/**
	 * return MOG parameters as XML
	 * 
	 * @return
	 */
	public void writeParamsasXML(XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
		
		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement("ROOT");
		xMLStreamWriter.writeAttribute("name", "Root");
		
		xMLStreamWriter.writeStartElement("permutations");
		xMLStreamWriter.writeAttribute("value", String.valueOf(MetaOmGraph.getNumPermutations()));
		xMLStreamWriter.writeEndElement();
		
		xMLStreamWriter.writeStartElement("threads");
		xMLStreamWriter.writeAttribute("value", String.valueOf(MetaOmGraph.getNumThreads()));
		xMLStreamWriter.writeEndElement();
		
		xMLStreamWriter.writeStartElement("hyperlinksCols");
		xMLStreamWriter.writeAttribute("srrColumn", String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getsrrColumn()));
		xMLStreamWriter.writeAttribute("srpColumn", String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getsrpColumn()));
		xMLStreamWriter.writeAttribute("srxColumn", String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getsrxColumn()));
		xMLStreamWriter.writeAttribute("srsColumn", String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getsrsColumn()));
		xMLStreamWriter.writeAttribute("gseColumn", String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getgseColumn()));
		xMLStreamWriter.writeAttribute("gsmColumn", String.valueOf(MetaOmGraph.getActiveTable().getMetadataTableDisplay().getgsmColumn()));
		xMLStreamWriter.writeEndElement();
		
		xMLStreamWriter.writeEndElement();
		
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
