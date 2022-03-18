package edu.iastate.metnet.metaomgraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ColorUIResource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
//import org.biomage.examples.GetToDataExample;
import org.jdom.JDOMException;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanContrastIJTheme;
import com.l2fprod.common.swing.JTipOfTheDay;


//import com.l2fprod.common.swing.*;

import edu.iastate.metnet.arrayexpress.v2.AEImportDialog;
import edu.iastate.metnet.arrayexpress.v2.AEProjectMaker;
import edu.iastate.metnet.metaomgraph.AnnotationImporter.Annotation;
import edu.iastate.metnet.metaomgraph.MetaOmTips.MetaOmShowTipsChoice;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.ui.AboutFrame;
import edu.iastate.metnet.metaomgraph.ui.AboutFrame4;
import edu.iastate.metnet.metaomgraph.ui.ClickableLabel;
import edu.iastate.metnet.metaomgraph.ui.CustomFileSaveDialog;
import edu.iastate.metnet.metaomgraph.ui.ListMergePanel;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel;
import edu.iastate.metnet.metaomgraph.ui.MetadataFilter;
import edu.iastate.metnet.metaomgraph.ui.MetadataImportWizard;
import edu.iastate.metnet.metaomgraph.ui.Metadataviewer;
import edu.iastate.metnet.metaomgraph.ui.NewProjectDialog;
import edu.iastate.metnet.metaomgraph.ui.ProjectPropertiesPanel;
import edu.iastate.metnet.metaomgraph.ui.ReadMetadata;
import edu.iastate.metnet.metaomgraph.ui.ReproducibilityDashboardPanel;
import edu.iastate.metnet.metaomgraph.ui.SearchByExpressionFrame;
import edu.iastate.metnet.metaomgraph.ui.SetColTypes;
import edu.iastate.metnet.metaomgraph.ui.StatisticalResultsFrame;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;
import edu.iastate.metnet.metaomgraph.ui.TaskbarPanel;
import edu.iastate.metnet.metaomgraph.ui.ThirdPartyLibs;
import edu.iastate.metnet.metaomgraph.ui.WelcomePanel;
import edu.iastate.metnet.metaomgraph.ui.WelcomePanelWin10;
import edu.iastate.metnet.metaomgraph.utils.DataNormalizer;
import edu.iastate.metnet.metaomgraph.utils.DataNormalizer.MeanResult;
import edu.iastate.metnet.metaomgraph.utils.ExceptionHandler;
import edu.iastate.metnet.metaomgraph.utils.MOGColorThemes;
import edu.iastate.metnet.metaomgraph.utils.ProjectMerger;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.VersionCheck;

/**
 * The main class of the MetaOmGraph. Handles all non-project-specific
 * functions.
 *
 *
 */

public class MetaOmGraph implements ActionListener {

	// default colors
	/*
	 * private static Color tableColor1 = null; private static Color tableColor2 =
	 * null; private static Color tableSelectionColor = null; private static Color
	 * tableHighlightColor = null; private static Color tableHyperlinkColor = null;
	 * private static Color chartBackgroundColor = null; private static Color
	 * plotBackgroundColor = null;
	 */

	/*Harsha- Added logger */
	public static Logger logger;
	public static Appender appender;
	private static ReproducibilityDashboardPanel rdp;
	private static ActionProperties openProjectAction;
	private static int currentProjectActionId;
	private static String currentLogFilePath;
	private static boolean loggingRequired;
	private static boolean permanentLogging;
	private static int currentSamplesActionId;
	private static JInternalFrame ReproducibilityDashboardFrame;
	private static TaskbarPanel taskBar;
	private static StatisticalResultsFrame DEAResultsFrame;
	private static StatisticalResultsFrame DCResultsFrame;
	private static JButton plbbutton;
	private static JPanel playbackForMac;


	private enum DownloadSampleProject{
		Metabolomics,
		MicroArray,
		HumanCancerRNASeq
	}

	public static Color getTableColorForeground() {
		return currentTheme.getTableColorForeground();
	}

	public static StatisticalResultsFrame getDEAResultsFrame() {
		return DEAResultsFrame;
	}
	public static void setDEAResultsFrame(StatisticalResultsFrame dEAResultsFrame) {
		DEAResultsFrame = dEAResultsFrame;
	}
	public static StatisticalResultsFrame getDCResultsFrame() {
		return DCResultsFrame;
	}
	public static void setDCResultsFrame(StatisticalResultsFrame dCResultsFrame) {
		DCResultsFrame = dCResultsFrame;
	}
	public static Logger getLogger() {
		return logger;
	}
	public static void setLogger(Logger l) {
		logger = l;
	}
	public static boolean getLoggingRequired() {
		return loggingRequired;
	}
	public static void setLoggingRequired(boolean lr) {
		loggingRequired = lr;
	}
	public static boolean getPermanentLogging() {
		return permanentLogging;
	}
	public static void setPermanentLogging(boolean pl) {
		permanentLogging = pl;
	}
	public static String getCurrentLogFilePath() {
		return currentLogFilePath;
	}
	public static void setCurrentLogFilePath(String log) {
		currentLogFilePath = log;
	}
	public static int getCurrentSamplesActionId() {
		return currentSamplesActionId;
	}
	public static void setCurrentSamplesActionId(int sampId) {
		currentSamplesActionId = sampId;
	}
	public static ReproducibilityDashboardPanel getReproducibilityDashboardPanel() {
		return rdp;
	}

	public static TaskbarPanel getTaskBar() {
		return taskBar;
	}

	public static Color getTableColorEven() {
		return currentTheme.getTableColorEven();
	}

	public static Color getTableColorOdd() {
		return currentTheme.getTableColorOdd();
	}

	public static Color getTableSelectionColor() {
		return currentTheme.getTableSelectionColor();
	}

	public static Color getTableHighlightColor() {
		return currentTheme.getTableHighlightColor();
	}

	public static Color getTableHyperlinkColor() {
		return currentTheme.getTableHyperlinkColor();
	}

	public static Color getChartBackgroundColor() {
		return currentTheme.getChartBackgroundColor();
	}

	public static Color getPlotBackgroundColor() {
		return currentTheme.getPlotBackgroundColor();
	}

	public static void setTableColorEven(Color col) {
		currentTheme.setTableRowEvenColor(col);
	}

	public static void setTableColorOdd(Color col) {
		currentTheme.setTableRowOddColor(col);
	}

	public static void setTableSelectionColor(Color col) {
		currentTheme.setTableSelectionColor(col);
	}

	public static void setTableHighlightColor(Color col) {
		currentTheme.setTableHighlightColor(col);
	}

	public static void setTableHyperlinkColor(Color col) {
		currentTheme.setTableHyperlinkColor(col);
	}

	public static void setChartBackgroundColor(Color col) {
		currentTheme.setChartBackgroundColor(col);
	}

	public static void setPlotBackgroundColor(Color col) {
		currentTheme.setPlotBackgroundColor(col);
	}

	public static JInternalFrame getReproducibilityDashboardFrame() {
		return ReproducibilityDashboardFrame;
	}

	// create themes
	private static MOGColorThemes themeDefault = new MOGColorThemes("default", Color.gray,Color.gray, Color.gray,Color.gray,Color.gray,Color.gray,Color.gray); //gui default theme
	private static MOGColorThemes themeLight = new MOGColorThemes("light", Color.white,
			new ColorUIResource(216, 236, 213), Color.DARK_GRAY, Color.PINK, Color.green, Color.WHITE, Color.WHITE);
	private static MOGColorThemes themeDark = new MOGColorThemes("dark", new ColorUIResource(153, 153, 153),
			new ColorUIResource(204, 204, 204), Color.black, new ColorUIResource(0, 153, 102), Color.RED,
			new ColorUIResource(153, 153, 153), new ColorUIResource(153, 153, 153));
	private static MOGColorThemes themeSky = new MOGColorThemes("sky", new ColorUIResource(241, 250, 238),
			new ColorUIResource(168, 218, 220), new ColorUIResource(69, 123, 157), new ColorUIResource(155, 197, 61),
			Color.green, Color.WHITE, Color.WHITE);

	private static HashMap<String, MOGColorThemes> mogThemes = new HashMap<>();
	private static String currentmogThemeName = "sky";
	private static MOGColorThemes currentTheme;

	public static void initThemes() {
		mogThemes.put(themeLight.getThemeName(), themeLight);
		mogThemes.put(themeDark.getThemeName(), themeDark);
		mogThemes.put(themeSky.getThemeName(), themeSky);
		mogThemes.put(themeDefault.getThemeName(), themeDefault);
	}

	public static void initThemes(HashMap<String, MOGColorThemes> themes) {
		mogThemes = themes;
	}

	public static void addTheme(MOGColorThemes theme) {
		if (mogThemes == null) {
			initThemes();
		}
		mogThemes.put(theme.getThemeName(), theme);
	}

	public static void removeTheme(String name) {
		if (mogThemes.containsKey(name)) {
			mogThemes.remove(name);
		}
	}


	public static HashMap<String, MOGColorThemes> getAllThemes() {
		return mogThemes;
	}

	public static MOGColorThemes getTheme(String name) {
		if (mogThemes.containsKey(name)) {
			return mogThemes.get(name);
		} else {
			return null;
		}
	}

	public static Object[] getAllThemeNames() {
		return mogThemes.keySet().toArray();
	}

	public static void setCurrentThemeName(String name) {
		currentmogThemeName = name;
	}

	public static void setCurrentTheme() {
		setCurrentTheme(currentmogThemeName);
	}

	public static void setCurrentTheme(String theme) {
		if (mogThemes.containsKey(theme)) {
			currentTheme = mogThemes.get(theme);
			setCurrentThemeName(theme);
		} else {
			JOptionPane.showMessageDialog(null, "Changing theme failed", "Failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static String getCurrentThemeName() {
		return currentmogThemeName;
	}

	public static int getCurrentProjectActionId() {
		return currentProjectActionId;
	}
	public static MOGColorThemes getCurrentTheme() {
		return mogThemes.get(getCurrentThemeName());
	}

	// save hyperlink columns while loading project
	// initialize to -1 and only be updated if read in project file
	public static int _SRR = -1;
	public static int _SRP = -1;
	public static int _SRX = -1;
	public static int _SRS = -1;
	public static int _GSE = -1;
	public static int _GSM = -1;

	// urmi functions and vars to manage system RPath
	public static String getOsName() {
		String OS = null;
		if (OS == null) {
			OS = System.getProperty("os.name");
		}
		return OS;
	}

	/**
	 * return default R path
	 * 
	 * @return
	 */
	public static String getdefaulrRPath() {
		String res = "NA";
		String OS = getOsName();
		if (OS.indexOf("win") >= 0 || OS.indexOf("Win") >= 0) {
			res = "C:\\Program Files\\R\\R-3.4.3\\bin\\Rscript.exe";
			File[] dirList = new File("C:\\Program Files\\R\\").listFiles(File::isDirectory);
			List<String> dirNames = new ArrayList<>();
			for (File f : dirList) {
				dirNames.add(f.getName());
			}
			// reverse sort list of names to select most recent R
			dirNames.sort(null);
			Collections.reverse(dirNames);
			for (String s : dirNames) {
				if (s.startsWith("R")) {
					res = "C:\\Program Files\\R\\" + s + "\\bin\\Rscript.exe";
					break;
				}
			}

		} else if (OS.indexOf("mac") >= 0 || OS.indexOf("Mac") >= 0) {
			res = "/usr/local/bin/Rscript";
		} else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
			res = "/usr/local/bin/Rscript";
		}
		return res;
	}

	public static String getRPath() {
		if (defaultRpath) {
			return getdefaulrRPath();
		} else {
			return userRPath;
		}
	}

	public static void setUserRPath(String path) {
		if (path == null || path.equals("")) {
			defaultRpath = true;
			return;
		}
		if (path.equals(getdefaulrRPath())) {
			defaultRpath = true;
			return;
		}
		userRPath = path;
		defaultRpath = false;
	}

	public static void useDefaultRPath() {
		userRPath = "";
		defaultRpath = true;
	}

	public static boolean defaultRpath = true;
	private static String userRPath = "";
	private static String pathtoRscrips = "";

	public static String getpathtoRscrips() {
		return pathtoRscrips;
	}

	public static void setpathtoRscrips(String s) {
		pathtoRscrips = s;
	}

	// some default parameters
	private static int _N = 100; // num permutations
	private static int _T = 6; // num of threads
	private static int _M = 10; // num bins in MI
	private static int _k = 3; // order in MI

	public static int getNumBins() {
		return _M;
	}

	public static void setNumBins(int X) {
		_M = X;
	}

	public static int getOrder() {
		return _k;
	}

	public static void setOrder(int X) {
		_k = X;
	}

	public static int getNumPermutations() {
		return _N;
	}

	public static void setNumPermutations(int X) {
		_N = X;

	}

	public static int getNumThreads() {
		return _T;
	}

	public static void setNumThreads(int X) {
		_T = X;
	}

	// for line chart
	private static int lineWidth = 1;

	public static int getDefaultLineWidth() {
		return lineWidth;
	}

	/////////////////////
	private static final String VERSION = "1.8.2beta4";
	private static final String DATE = "Feb 2, 2021";

	public static String getVersion() {
		return VERSION;
	}

	public static final String NEW_PROJECT_DELIMITED_COMMAND = "New project from a delimited text file";

	public static final String NEW_PROJECT_SOFT_COMMAND = "New project from a SOFT file";

	public static final String NEW_PROJECT_METABOLOMICS_COMMAND = "new project from metabolomics database";

	public static final String NEW_PROJECT_ARRAYEXPRESS_COMMAND = "New project from ArrayExpress";

	public static final String OPEN_COMMAND = "Open a project";

	public static final String DOWNLOAD_METABOLOMICS_PROJ_COMMAND = "Sample MOG project- A. thaliana Metabolomics (0.8 MB) Try me";

	public static final String DOWNLOAD_MICROARRAY_PROJ_COMMAND = "Sample MOG project- A. thaliana Microarray (29.5 MB) Try me";

	public static final String DOWNLOAD_CANCER_RNASEQ_PROJ_COMMAND = "Sample MOG project- Human Cancer RNA-Seq (247 MB) Try me";

	public static final String SAVE_COMMAND = "Save the current project";

	public static final String SAVE_AS_COMMAND = "Save as";

	public static final String MERGE_COMMAND = "Merge projects";

	public static final String QUIT_COMMAND = "quit";

	public static final String CLOSE_PROJECT_COMMAND = "close the current project";

	public static final String LOAD_INFO_COMMAND = "load extended info";
	public static final String LOAD_INFO_COMMAND_csv = "load extended info_csv";
	public static final String LOAD_TREE = "load_tree";

	public static final String PROPERTIES_COMMAND = "project properties";

	public static final String ABOUT_COMMAND = "show about window";
	public static final String PLAYBACK_COMMAND = "playback command";
	private static final String THIRD_PARTY_LIBS_COMMAND = "show third party libs window";

	public static final String RECENT_PROJECT_COMMAND = "open a recent project";

	public static final String SHOW_TIPS_COMMAND = "show tip of the day";

	public static final String EXCLUDE_SAMPLES_COMMAND = "exclude samples";

	public static final String IMPORT_ANNOTATION_COMMAND_PREFIX = "import annotation ";

	public static final String IMPORT_HGU133A_COMMAND_SUFFIX = "hgu133a";
	public static final String IMPORT_HGU133PLUS2_COMMAND_SUFFIX = "hgu133plus2";

	public static final String IMPORT_ATH1_COMMAND_SUFFIX = "Arabidopsis ATH1";
	public static final String IMPORT_MOUSE430_2_COMMAND_SUFFIX = " MOUSE430_2";
	public static final String IMPORT_RAE230A_COMMAND_SUFFIX = " RAE230A";
	public static final String IMPORT_RAEX1_COMMAND_SUFFIX = " RAEX1";
	public static final String IMPORT_RAGENE1_COMMAND_SUFFIX = " RAGENE1";
	public static final String IMPORT_RAT230_2_COMMAND_SUFFIX = " RAT230_2";
	public static final String IMPORT_SOYBEAN_COMMAND_SUFFIX = " SOYBEAN";
	public static final String IMPORT_U34A_COMMAND_SUFFIX = " U34A";
	public static final String IMPORT_S98_COMMAND_SUFFIX = "S98";
	public static final String IMPORT_YEAST2_COMMAND_SUFFIX = "YEAST2";
	public static final String IMPORT_BARLEY_SUFFIX = " BARLEY";
	public static final String IMPORT_RICE_SUFFIX = " RICE";
	public static final String IMPORT_ZEBRAFISH_COMMAND_SUFFIX = " ZEBRAFISH";
	public static final String IMPORT_CUSTOM_COMMAND_SUFFIX = " CUSTOM";

	public static final String IMPORT_LISTS_COMMAND = "import lists";

	public static final String IMPORT_METNET3_LISTS_COMMAND = "import metnet3";

	public static final String EXPORT_LISTS_COMMAND = "export lists";

	public static final String EXPORT_METNET3_LISTS_COMMAND = "export metnet3";

	public static final String MERGE_LISTS_COMMAND = "merge lists";

	public static final String CASCADE_WINDOWS_COMMAND = "cascade windows";

	public static final String SWITCH_WINDOW_COMMAND = "switch window";

	public static final String CLOSE_WINDOW_COMMAND = "close window";

	public static final String FIND_REPS_COMMAND = "find reps";

	public static final String CONTACT_COMMAND = "send an email";

	public static final String HIDE_SHOW_FEATURE_METADATA_COLUMNS = "hide/show feature metadata cols";

	public static final String HIDE_SHOW_SAMPLE_METADATA_COLUMNS = "hide/show sample metadata cols";


	/** The program's main JFrame */
	private static JFrame mainWindow;

	/** The currently open MetaOmProject */
	public static MetaOmProject activeProject;

	/** The .mog file to save to */
	private static File activeProjectFile;

	/** Internal frame which displays a table of the active project's entries */
	private static TaskbarInternalFrame projectTableFrame;

	/** The table panel that appears whenever a project is open */
	private static MetaOmTablePanel activeTablePanel;

	/** mainWindow's content pane. */
	private static JDesktopPane desktop;

	/** The main menu bar */
	private static JMenuBar mainMenuBar;

	/** The "File" menu */
	private static JMenu fileMenu;

	/** Items on the File menu */
	private static JMenuItem newProjectItem, newSOFTItem, newMetabolomicsItem, openProjectItem,
	saveProjectItem, saveProjectAsItem, quitItem, mergeItem;

	private static JMenu newProjectMenu;

	/** The Recent Projects menu item */
	private static JMenu recentProjectsMenu;

	private static Vector<File> recentProjects;

	private static JMenuItem[] recentProjectsMenuItems;

	/** The Project menu */
	private static JMenu projectMenu;

	// tools menu urmi
	private static JMenu toolsMenu;
	private static JMenu infoButtonMenu;
	private static JMenuItem geneCardsItem;
	private static JMenuItem ensemblItem;
	private static JMenuItem ensemblPItem;
	private static JMenuItem refseqItem;
	private static JMenuItem atgsItem;
	private static JMenuItem tairItem;
	private static JMenuItem thaleMineItem;
	private static JMenuItem jBrowseItem;
	public static final String ATGENESEARCH_COMMAND = "atgenesearch";
	public static final String GENECARDS_COMMAND = "genecardssearch";
	public static final String ENSEMBL_COMMAND = "ensemblsearch";
	public static final String ENSEMBL_PLANTSCOMMAND = "ensemblplantsearch";
	public static final String REFSEQ_COMMAND = "refseqsearch";
	public static final String TAIR_COMMAND = "tair";
	public static final String ARAPORT_THALEMINE_COMMAND = "ThaleMine";
	public static final String ARAPORT_JBROWSE_COMMAND = "JBrowse";
	private static JMenuItem findSamples;

	private static JMenuItem tTest;

	public static final String REPORT_COMMAND = "report";

	/** Items on the Project menu */
	private static JMenuItem closeProjectItem, loadInfoItem, projectPropertiesItem, excludeSamplesItem;
	// urmi
	private static JMenuItem openMDColTypes, openInfoColTypes, openMetadataStructureItem, metadataViewerItem,
	loadInfoItem2, loadTree, hideShowFeatureMetadataColumns, hideShowSampleMetadataColumns;

	private static JCheckBoxMenuItem logDataItem;

	/** The Import Annotations menu */
	private static JMenu importAnnotationsMenu;

	/** Items on the Import Annotations menu */
	private static JMenuItem importHumanItem, importMouseItem, importSoybeanItem, importRatItem, importYeastItem,
	importBarleyItem, importRiceItem, importCustomItem, import133plus2Item, importArabidopsisItem,
	importYeast2Item, importZebrafishItem;

	private static JMenu importRatMenu;

	private static JMenuItem importRatItem1, importRatItem2, importRatItem3, importRatItem4, importRatItem5;

	private static JMenu importListsMenu;
	private static JMenuItem importListsFileItem, importListsMetNet3Item;

	// urmi
	private static JMenu dataTransformMenu;
	private static JMenuItem noneItem, log2Item, log10Item, logeItem, sqrtItem;

	private static JMenu exportListsMenu;

	private static JMenuItem exportListsFileItem, exportListsMetNet3Item;

	private static JMenuItem mergeListsItem;

	private static JMenuItem findRepsItem;

	private static JMenu windowMenu;

	private static JMenuItem cascadeItem, closeWindowItem;

	private static JCheckBoxMenuItem[] openWindowItems;

	/** The Help menu */
	private static JMenu helpMenu;

	/** The History menu */
	private static JMenu historyMenu;

	/** Items on the Help menu */
	private static JMenuItem checkUpdateitem, overviewItem, contextItem, tipsItem, aboutItem, contactItem;
	private static JMenuItem thirdPartyLibs;

	/** An instance of this class created by the main() method */
	private static MetaOmGraph myself;

	/** The icon used for MetaOmGraph windows */
	private static Image myIcon;

	/** An ActionListener that displays the Help frame when invoked */
	private static MetaOmHelpListener helpListener;

	/** A WindowListener that makes frames/dialogs pseudo-modal */
	private static SimpleModalMaker modalMaker;

	/** A frame that displays the properties for the active project */
	private static TaskbarInternalFrame propertiesFrame;

	/** A frame that displays About information */
	private static AboutFrame aboutFrame;

	private static JTipOfTheDay tipper;

	private static MetaOmTips.MetaOmShowTipsChoice showTips;

	private static Integer currentTip;

	private static IconTheme iconTheme;

	/**
	 * The Actions Menu Added by Mohammed Alabsi - June 6th, 2006
	 */
	private static JMenu actionsMenu;

	private static JMenuItem birdsEyeViewItem;

	private static JMenuItem pubMedItem;

	private static JMenuItem pathBinderItem;

	private static JMenuItem cytoscapeItem;

	private static JMenuItem birdsEyeViewSubsetItem;

	private static JMenuItem atGeneSearchItem;

	private static JDialog welcomeDialog;


	private static Themes activeTheme;

	public enum Themes{
		Light,
		Dark,
		System,
		IntelliJ,
		DeepOcean,
		Cobalt,
		Carbon
	}

	public static boolean setTheme(Themes theme) {
		try {
			switch(theme) {
			case Light:
				UIManager.setLookAndFeel(new FlatLightLaf());
				break;
			case Dark:
				UIManager.setLookAndFeel(new FlatDarkLaf());
				break;

			case IntelliJ :
				UIManager.setLookAndFeel(new FlatArcDarkOrangeIJTheme());
				break;
			case DeepOcean :
				UIManager.setLookAndFeel(new FlatMaterialDeepOceanContrastIJTheme());
				break;
			case Cobalt :
				UIManager.setLookAndFeel(new FlatCobalt2IJTheme());
				break;
			case Carbon :
				UIManager.setLookAndFeel(new FlatCarbonIJTheme());
				break;

			case System:
				//UIManager.setLookAndFeel(UIManager.get getSystemLookAndFeelClassName());
				//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				//UIManager.setLookAndFeel(new FlatLightLaf());
				break;
			}


			activeTheme = theme;
			if (mainWindow!=null) {

				SwingUtilities.updateComponentTreeUI(mainWindow);

			}
		}
		catch (Exception e) {
			//TODO: handle exception when theme change fails
			//JOptionPane.showMessageDialog(null, "THEMEERRORRRR"+e);
			//activeTheme = Themes.Light;
			return false;
		}
		return true;
	}

	public static Themes getActiveTheme() {
		if (activeTheme==null) {
			return Themes.Light;
		}
		return activeTheme;
	}

	/**
	 * Fetches the active MetaOmProject.
	 *
	 * @return The open MetaOmProject, or null if there is no open project.
	 */
	public static MetaOmProject getActiveProject() {
		return activeProject;
	}

	/*
	 * Initialises the variables before start up
	 */
	public static void init() {
		init(false);
	}


	public static MetaOmTablePanel getActiveTablePanel() {
		return activeTablePanel;
	}
	/**
	 * Creates a set of variables for the start up process. Sets up and configures
	 * the main window and desktop Creates the menu bar with corresponding elements.
	 * Each element is derived from the root element 'JMenuBar'. Each menu bar
	 * elements is associated with Action command which will be called when the
	 * particular element is pressed.
	 * 
	 * @param useBuffer
	 *            - a boolean variable which handles exception
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void init(boolean useBuffer) {
		System.setProperty("MOG.version", " v. " + VERSION);
		System.setProperty("MOG.date", DATE);

		myself = new MetaOmGraph();
		activeProject = null;
		recentProjects = new Vector<File>();

		//setting loggingRequired parameter for reproducibility logging
		MetaOmGraph.setLoggingRequired(true);

		//setting loggingRequired parameter for reproducibility logging
		MetaOmGraph.setLoggingRequired(true);

		File homeDir = new File(System.getProperty("user.home"));
		File prefsFile = new File(homeDir, "metaomgraph.prefs");
		if ((prefsFile.exists()) && (prefsFile.canRead())) {
			try {
				FileInputStream fis = new FileInputStream(prefsFile);
				ObjectInputStream in = new ObjectInputStream(fis);

				File lastF = (File) in.readObject();
				Utils.setLastDir(lastF);
				// JOptionPane.showMessageDialog(null, "Lastpath:"+lastF.getAbsolutePath());
				recentProjects = (Vector<File>) in.readObject();
				// JOptionPane.showMessageDialog(null, recentProjects.toString());
				if(recentProjects.size() > 0)
					Utils.setLastDir(recentProjects.get(0));
				showTips = (MetaOmShowTipsChoice) in.readObject();
				currentTip = (Integer) in.readObject();
				try {
					List<String> rObs = (List<String>) in.readObject();
					setUserRPath(rObs.get(0));
					setpathtoRscrips(rObs.get(1));
				} catch (Exception ex) {
					setUserRPath("");
				}


				try {

					String prevSessionLafTheme = (String) in.readObject();
					setTheme(Themes.valueOf(prevSessionLafTheme));
				}
				catch (Exception e) {

					setTheme(Themes.Light);
				}

				// read mog themes
				try {
					String lastThemeName = (String) in.readObject();
					HashMap<String, MOGColorThemes> themes = (HashMap<String, MOGColorThemes>) in.readObject();
					if (lastThemeName != null && themes != null && themes.size() > 0) {
						initThemes(themes);
						setCurrentTheme(lastThemeName);
					} else {
						initThemes();
						setCurrentTheme("sky");
					}

					//UserPreferences up = new UserPreferences(recentProjects,showTips,lastThemeName);
				} catch (Exception ex) {
					initThemes();
					setCurrentTheme("sky");
				}


				in.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				System.err.println("Couldn't read prefs file");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			//urmi IF .prefs file is absent 
			//init default themes when prefs file is absent
			setTheme(Themes.Light);
			initThemes();
			setCurrentTheme("sky");

		}

		mainWindow = new JFrame("MetaOmGraph");


		//urmi
		///////////////enable debug mode//////////////////
		ExceptionHandler.getInstance(mainWindow).setUseBuffer(useBuffer);
		//urmi set setUseBuffer(false) to print to console debug mode
		//ExceptionHandler.getInstance(mainWindow).setUseBuffer(false);
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance(mainWindow));
		desktop = new JDesktopPane();
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

		try {
			myIcon = ImageIO.read(myself.getClass().getResourceAsStream("/resource/MetaOmicon.png"));
			if (getOsName().indexOf("Mac") >= 0) {
				//				Application application = Application.getApplication();
				//				application.setDockIconImage(myIcon);
			} else {
				mainWindow.setIconImage(myIcon);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		mainWindow.getContentPane().add(desktop, BorderLayout.CENTER);

		mainMenuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		newProjectMenu = new JMenu("New Project");
		newProjectMenu.setMnemonic(KeyEvent.VK_N);
		newProjectItem = new JMenuItem("From Delimited Text File...");
		newProjectItem.setActionCommand(NEW_PROJECT_DELIMITED_COMMAND);
		newProjectItem.addActionListener(myself);
		newProjectItem.setMnemonic(KeyEvent.VK_D);
		newProjectItem.setToolTipText("Create a new MetaOm project " + "from a delimited text file");
		newSOFTItem = new JMenuItem("From SOFT File...");
		newSOFTItem.setActionCommand(NEW_PROJECT_SOFT_COMMAND);
		newSOFTItem.addActionListener(myself);
		newSOFTItem.setMnemonic(KeyEvent.VK_S);
		newSOFTItem.setToolTipText("Create a new MetaOm project from a SOFT file");
		newMetabolomicsItem = new JMenuItem("From Metabolomics Database...");
		newMetabolomicsItem.setActionCommand(NEW_PROJECT_METABOLOMICS_COMMAND);
		newMetabolomicsItem.addActionListener(myself);
		newMetabolomicsItem.setMnemonic(KeyEvent.VK_M);
		newMetabolomicsItem.setToolTipText(
				"Create a new MetaOm project from the metabolomics database at http://plantmetabolomics.org");

		/*
		 * Removed this newArrayExpressItem = new JMenuItem("From ArrayExpress...");
		newArrayExpressItem.setActionCommand(NEW_PROJECT_ARRAYEXPRESS_COMMAND);
		newArrayExpressItem.addActionListener(myself);
		newArrayExpressItem.setMnemonic(KeyEvent.VK_A);
		newArrayExpressItem.setToolTipText("Create a new MetaOm project from the ArrayExpress database");
		// urmi remove unused menu
		// newProjectMenu.add(newSOFTItem);
		// newProjectMenu.add(newMetabolomicsItem);
		newProjectMenu.add(newArrayExpressItem);
		 */
		newProjectMenu.add(newProjectItem);

		openProjectItem = new JMenuItem("Open Project...");
		openProjectItem.setActionCommand(OPEN_COMMAND);
		openProjectItem.addActionListener(myself);
		openProjectItem.setMnemonic(KeyEvent.VK_O);
		openProjectItem.setToolTipText("Open an existing MetaOmGraph project");
		openProjectItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		recentProjectsMenu = new JMenu("Recent Projects");
		recentProjectsMenu.setMnemonic(KeyEvent.VK_R);
		initRecentProjectsMenu();

		saveProjectItem = new JMenuItem("Save Project");
		saveProjectItem.setActionCommand(SAVE_COMMAND);
		saveProjectItem.addActionListener(myself);
		saveProjectItem.setMnemonic(KeyEvent.VK_S);
		saveProjectItem.setEnabled(false);
		saveProjectItem.setToolTipText("Save the active project");
		saveProjectItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		saveProjectAsItem = new JMenuItem("Save Project As...");
		saveProjectAsItem.setActionCommand(SAVE_AS_COMMAND);
		saveProjectAsItem.addActionListener(myself);
		saveProjectAsItem.setMnemonic(KeyEvent.VK_A);
		saveProjectAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
		saveProjectAsItem.setEnabled(false);
		saveProjectAsItem.setToolTipText("Save the active project to a new file");
		mergeItem = new JMenuItem("Merge projects...");
		mergeItem.setActionCommand(MERGE_COMMAND);
		mergeItem.addActionListener(myself);
		mergeItem.setMnemonic(KeyEvent.VK_M);

		quitItem = new JMenuItem("Quit MOG");
		quitItem.setActionCommand(QUIT_COMMAND);
		quitItem.addActionListener(myself);
		quitItem.setMnemonic(KeyEvent.VK_Q);
		quitItem.setToolTipText("Exit this program");
		quitItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		closeProjectItem = new JMenuItem("Close project");
		closeProjectItem.setMnemonic(KeyEvent.VK_C);
		closeProjectItem.setActionCommand(CLOSE_PROJECT_COMMAND);
		closeProjectItem.addActionListener(myself);
		closeProjectItem.setToolTipText("Close all windows related to the active project");

		fileMenu.add(newProjectMenu);
		fileMenu.add(openProjectItem);
		fileMenu.add(closeProjectItem);
		fileMenu.add(recentProjectsMenu);
		fileMenu.add(saveProjectItem);
		fileMenu.add(saveProjectAsItem);
		// fileMenu.add(mergeItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);
		mainMenuBar.add(fileMenu);

		projectMenu = new JMenu("Edit");
		projectMenu.setMnemonic(KeyEvent.VK_P);
		projectMenu.setEnabled(false);

		// closeProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
		// Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		//projectMenu.add(closeProjectItem); //urmi moved close project to file menu
		loadInfoItem = new JMenuItem("Load Metadata (xml)");
		loadInfoItem.setActionCommand(LOAD_INFO_COMMAND);
		loadInfoItem.addActionListener(myself);
		loadInfoItem.setMnemonic(KeyEvent.VK_L);
		// projectMenu.add(loadInfoItem);

		// urmi

		loadInfoItem2 = new JMenuItem("Import Metadata");
		loadInfoItem2.setActionCommand(LOAD_INFO_COMMAND_csv);
		loadInfoItem2.addActionListener(myself);
		// loadInfoItem2.setMnemonic(KeyEvent.VK_L);
		projectMenu.add(loadInfoItem2);

		loadTree = new JMenuItem("Load Metadata structure (tree)");
		loadTree.setActionCommand(LOAD_TREE);
		loadTree.addActionListener(myself);
		// loadInfoItem2.setMnemonic(KeyEvent.VK_L);
		// projectMenu.add(loadTree);
		// projectMenu.addSeparator();

		openMetadataStructureItem = new JMenuItem("Edit Metadata structure");
		openMetadataStructureItem.setActionCommand("structure");
		openMetadataStructureItem.addActionListener(myself);
		// openMetadataStructureItem.setMnemonic(KeyEvent.VK_L);

		openInfoColTypes = new JMenuItem("Edit Info Columns type");
		openInfoColTypes.setActionCommand("coltypes");
		openInfoColTypes.addActionListener(myself);

		openMDColTypes = new JMenuItem("Edit Info Columns type");
		openMDColTypes.setActionCommand("coltypesMD");
		openMDColTypes.addActionListener(myself);

		projectMenu.add(openMetadataStructureItem);
		// removed temporarily: urmi
		// projectMenu.add(openInfoColTypes);

		projectMenu.addSeparator();

		// metadataViewerItem = new JMenuItem("View metadata");
		// metadataViewerItem.setActionCommand("viewmetadata");
		// metadataViewerItem.addActionListener(myself);
		// metadataViewerItem.setMnemonic(KeyEvent.VK_L);
		// projectMenu.add(metadataViewerItem);
		// projectMenu.addSeparator();

		projectPropertiesItem = new JMenuItem("Properties");
		projectPropertiesItem.setActionCommand(PROPERTIES_COMMAND);
		projectPropertiesItem.addActionListener(myself);
		projectPropertiesItem.setMnemonic(KeyEvent.VK_P);
		projectPropertiesItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		projectMenu.add(projectPropertiesItem);
		projectMenu.addSeparator();
		excludeSamplesItem = new JMenuItem("Manage Samples");
		excludeSamplesItem.setActionCommand(EXCLUDE_SAMPLES_COMMAND);
		excludeSamplesItem.addActionListener(myself);
		excludeSamplesItem.setMnemonic(KeyEvent.VK_S);
		projectMenu.add(excludeSamplesItem);

		// logDataItem = new JCheckBoxMenuItem("log2 data");
		// logDataItem.setMnemonic(KeyEvent.VK_L);
		// projectMenu.add(logDataItem);

		// urmi add data transformation
		dataTransformMenu = new JMenu("Transform data");

		noneItem = new JCheckBoxMenuItem("None");
		noneItem.setActionCommand("noTransform");
		noneItem.addActionListener(myself);
		noneItem.setSelected(true);

		log2Item = new JCheckBoxMenuItem("<html>log<sub>2</sub></html>");
		log2Item.setActionCommand("log2");
		log2Item.addActionListener(myself);

		log10Item = new JCheckBoxMenuItem("<html>log<sub>10</sub></html>");
		log10Item.setActionCommand("log10");
		log10Item.addActionListener(myself);

		logeItem = new JCheckBoxMenuItem("<html>log<sub>e</sub></html>");
		logeItem.setActionCommand("loge");
		logeItem.addActionListener(myself);

		sqrtItem = new JCheckBoxMenuItem("sqrt data");
		sqrtItem.setActionCommand("sqrt");
		sqrtItem.addActionListener(myself);

		dataTransformMenu.add(noneItem);
		dataTransformMenu.add(log2Item);
		dataTransformMenu.add(log10Item);
		dataTransformMenu.add(logeItem);
		dataTransformMenu.add(sqrtItem);

		projectMenu.add(dataTransformMenu);

		findRepsItem = new JMenuItem("Find Replicates");
		findRepsItem.setActionCommand(FIND_REPS_COMMAND);
		findRepsItem.addActionListener(myself);
		// findRepsItem.setMnemonic(KeyEvent.VK_F);
		// projectMenu.add(findRepsItem);
		projectMenu.addSeparator();

		importAnnotationsMenu = new JMenu("Import Array Information");
		importAnnotationsMenu.setMnemonic(KeyEvent.VK_N);
		importAnnotationsMenu.setToolTipText("Import array annotation information");

		importArabidopsisItem = new JMenuItem("Arabidopsis (ATH1)");
		importArabidopsisItem.setMnemonic(KeyEvent.VK_A);
		importArabidopsisItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_ATH1_COMMAND_SUFFIX);
		importArabidopsisItem.addActionListener(myself);
		importAnnotationsMenu.add(importArabidopsisItem);

		importBarleyItem = new JMenuItem("Barley");
		importBarleyItem.setMnemonic(KeyEvent.VK_I);
		importBarleyItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_BARLEY_SUFFIX);
		importBarleyItem.addActionListener(myself);
		importBarleyItem.setToolTipText("Import annotations for the Affymetrix Barley Genome array");
		importAnnotationsMenu.add(importBarleyItem);

		importHumanItem = new JMenuItem("Human (HG-U133A)");
		importHumanItem.setMnemonic(KeyEvent.VK_H);
		importHumanItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_HGU133A_COMMAND_SUFFIX);
		importHumanItem.addActionListener(myself);
		importHumanItem.setToolTipText("Import annotations for the Affymetrix HG-U133A array");
		importAnnotationsMenu.add(importHumanItem);
		import133plus2Item = new JMenuItem("Human (HG-U133 Plus 2.0)");
		import133plus2Item.setMnemonic(KeyEvent.VK_P);
		import133plus2Item.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_HGU133PLUS2_COMMAND_SUFFIX);
		import133plus2Item.addActionListener(myself);
		import133plus2Item.setToolTipText("Import annotations for the Affymetrix HG-U133 Plus 2.0 array");
		importAnnotationsMenu.add(import133plus2Item);

		importMouseItem = new JMenuItem("Mouse (430 2.0)");
		importMouseItem.setMnemonic(KeyEvent.VK_M);
		importMouseItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_MOUSE430_2_COMMAND_SUFFIX);
		importMouseItem.addActionListener(myself);
		importMouseItem.setToolTipText("Import annotations for the Affymetrix Mouse 430 2.0 array");
		importAnnotationsMenu.add(importMouseItem);
		importRatMenu = new JMenu("Rat");
		importRatMenu.setMnemonic(KeyEvent.VK_R);

		importRatItem1 = new JMenuItem("RAE 230A");
		importRatItem1.setMnemonic(KeyEvent.VK_A);
		importRatItem1.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_RAE230A_COMMAND_SUFFIX);
		importRatItem1.addActionListener(myself);
		importRatItem1.setToolTipText("Import annotations for the Affymetrix Rat RAE230A array");
		importRatMenu.add(importRatItem1);

		importRatItem2 = new JMenuItem("Rat Exon 1.0 ST");
		importRatItem2.setMnemonic(KeyEvent.VK_E);
		importRatItem2.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_RAEX1_COMMAND_SUFFIX);
		importRatItem2.addActionListener(myself);
		importRatItem2.setToolTipText("Import annotations for the Affymetrix Rat Exon array");
		importRatMenu.add(importRatItem2);

		importRatItem3 = new JMenuItem("Rat Gene 1.0 ST");
		importRatItem3.setMnemonic(KeyEvent.VK_G);
		importRatItem3.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_RAGENE1_COMMAND_SUFFIX);
		importRatItem3.addActionListener(myself);
		importRatItem3.setToolTipText("Import annotations for the Affymetrix Rat Gene array");
		importRatMenu.add(importRatItem3);

		importRatItem4 = new JMenuItem("230 2.0");
		importRatItem4.setMnemonic(KeyEvent.VK_2);
		importRatItem4.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_RAT230_2_COMMAND_SUFFIX);
		importRatItem4.addActionListener(myself);
		importRatItem4.setToolTipText("Import annotations for the Affymetrix Rat 230 2.0 array");
		importRatMenu.add(importRatItem4);

		importRatItem5 = new JMenuItem("U34A");
		importRatItem5.setMnemonic(KeyEvent.VK_U);
		importRatItem5.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_U34A_COMMAND_SUFFIX);
		importRatItem5.addActionListener(myself);
		importRatItem5.setToolTipText("Import annotations for the Affymetrix Rat U34A array");
		importRatMenu.add(importRatItem5);
		// importAnnotationsMenu.add(importRatMenu);

		importRatItem = new JMenuItem("Rat (230 2.0)");
		importRatItem.setMnemonic(KeyEvent.VK_R);
		importRatItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_RAT230_2_COMMAND_SUFFIX);
		importRatItem.addActionListener(myself);
		importRatItem.setToolTipText("Import annotations for the Affymetrix Rat 230.2 array");
		importAnnotationsMenu.add(importRatItem);

		importRiceItem = new JMenuItem("Rice");
		importRiceItem.setMnemonic(KeyEvent.VK_I);
		importRiceItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_RICE_SUFFIX);
		importRiceItem.addActionListener(myself);
		importRiceItem.setToolTipText("Import annotations for the Affymetrix Rice Genome array");
		importAnnotationsMenu.add(importRiceItem);

		importSoybeanItem = new JMenuItem("Soybean");
		importSoybeanItem.setMnemonic(KeyEvent.VK_S);
		importSoybeanItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_SOYBEAN_COMMAND_SUFFIX);
		importSoybeanItem.addActionListener(myself);
		importSoybeanItem.setToolTipText("Import annotations for the Affymetrix Soybean Array");
		importAnnotationsMenu.add(importSoybeanItem);

		importYeast2Item = new JMenuItem("Yeast 2.0");
		importYeast2Item.setMnemonic(KeyEvent.VK_2);
		importYeast2Item.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_YEAST2_COMMAND_SUFFIX);
		importYeast2Item.addActionListener(myself);
		importYeast2Item.setToolTipText("Import annotations for the Affymetrix Yeast 2.0 array");
		importAnnotationsMenu.add(importYeast2Item);

		importYeastItem = new JMenuItem("Yeast (S98)");
		importYeastItem.setMnemonic(KeyEvent.VK_Y);
		importYeastItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_S98_COMMAND_SUFFIX);
		importYeastItem.addActionListener(myself);
		importYeastItem.setToolTipText("Import annotations for the Affymetrix Yeast S98 array");
		importAnnotationsMenu.add(importYeastItem);

		importZebrafishItem = new JMenuItem("Zebrafish");
		importZebrafishItem.setMnemonic(KeyEvent.VK_Y);
		importZebrafishItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_ZEBRAFISH_COMMAND_SUFFIX);
		importZebrafishItem.addActionListener(myself);
		importZebrafishItem.setToolTipText("Import annotations for the Affymetrix Zebrafish array");
		importAnnotationsMenu.add(importZebrafishItem);

		importCustomItem = new JMenuItem("Custom...");
		importCustomItem.setMnemonic(KeyEvent.VK_C);
		importCustomItem.setActionCommand(IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_CUSTOM_COMMAND_SUFFIX);
		importCustomItem.addActionListener(myself);
		importCustomItem.setToolTipText("Import annotations from a custom .csv or tab-delimited text file");
		importAnnotationsMenu.addSeparator();
		importAnnotationsMenu.add(importCustomItem);
		// urmi remove annotation menu
		// projectMenu.add(importAnnotationsMenu);

		importListsMenu = new JMenu("Import Lists");
		importListsFileItem = new JMenuItem("From file...");
		importListsFileItem.setMnemonic(KeyEvent.VK_L);
		importListsFileItem.setActionCommand(IMPORT_LISTS_COMMAND);
		importListsFileItem.addActionListener(myself);
		importListsFileItem.setToolTipText("Import all lists from an XML file");
		importListsMenu.add(importListsFileItem);
		importListsMetNet3Item = new JMenuItem("From MetNet Online...");
		importListsMetNet3Item.setActionCommand(IMPORT_METNET3_LISTS_COMMAND);
		importListsMetNet3Item.addActionListener(myself);
		importListsMetNet3Item.setToolTipText("Import lists from a MetNet Online account");
		// urmi remove this item
		// importListsMenu.add(importListsMetNet3Item);
		projectMenu.add(importListsMenu);

		exportListsMenu = new JMenu("Export Lists");
		exportListsFileItem = new JMenuItem("To file...");
		exportListsFileItem.setMnemonic(KeyEvent.VK_X);
		exportListsFileItem.setActionCommand(EXPORT_LISTS_COMMAND);
		exportListsFileItem.addActionListener(myself);
		exportListsFileItem.setToolTipText("Export all lists to an XML file");
		exportListsMenu.add(exportListsFileItem);
		exportListsMetNet3Item = new JMenuItem("To MetNet Online...");
		exportListsMetNet3Item.setActionCommand(EXPORT_METNET3_LISTS_COMMAND);
		exportListsMetNet3Item.addActionListener(myself);
		exportListsMetNet3Item.setToolTipText("Export lists to a MetNet Online account");
		// urmi remove this item
		// exportListsMenu.add(exportListsMetNet3Item);
		projectMenu.add(exportListsMenu);

		mergeListsItem = new JMenuItem("Merge Lists...");
		mergeListsItem.setMnemonic(KeyEvent.VK_M);
		mergeListsItem.setActionCommand(MERGE_LISTS_COMMAND);
		mergeListsItem.addActionListener(myself);
		mergeListsItem.setToolTipText("Merge existing lists into a new list");
		projectMenu.add(mergeListsItem);


		projectMenu.addSeparator();

		hideShowFeatureMetadataColumns = new JMenuItem("Hide/Show Feature Metadata columns");
		hideShowFeatureMetadataColumns.setActionCommand(HIDE_SHOW_FEATURE_METADATA_COLUMNS);
		hideShowFeatureMetadataColumns.addActionListener(myself);

		hideShowSampleMetadataColumns = new JMenuItem("Hide/Show Sample Metadata columns");
		hideShowSampleMetadataColumns.setActionCommand(HIDE_SHOW_SAMPLE_METADATA_COLUMNS);
		hideShowSampleMetadataColumns.addActionListener(myself);

		projectMenu.add(hideShowFeatureMetadataColumns);
		projectMenu.add(hideShowSampleMetadataColumns);


		projectMenu.addSeparator();

		hideShowFeatureMetadataColumns = new JMenuItem("Hide/Show Feature Metadata columns");
		hideShowFeatureMetadataColumns.setActionCommand(HIDE_SHOW_FEATURE_METADATA_COLUMNS);
		hideShowFeatureMetadataColumns.addActionListener(myself);

		hideShowSampleMetadataColumns = new JMenuItem("Hide/Show Sample Metadata columns");
		hideShowSampleMetadataColumns.setActionCommand(HIDE_SHOW_SAMPLE_METADATA_COLUMNS);
		hideShowSampleMetadataColumns.addActionListener(myself);

		projectMenu.add(hideShowFeatureMetadataColumns);
		projectMenu.add(hideShowSampleMetadataColumns);

		mainMenuBar.add(projectMenu);

		// urmi add tools in menu
		toolsMenu = new JMenu("Tools");
		// adddd to tools menu
		geneCardsItem = new JMenuItem("GeneCards");
		geneCardsItem.setActionCommand(GENECARDS_COMMAND);
		geneCardsItem.addActionListener(myself);
		geneCardsItem.setToolTipText("Visit GeneCards for information on selected genes");

		ensemblItem = new JMenuItem("Ensembl");
		ensemblItem.setActionCommand(ENSEMBL_COMMAND);
		ensemblItem.addActionListener(myself);
		ensemblItem.setToolTipText("Visit Ensembl for information on selected genes");

		ensemblPItem = new JMenuItem("EnsemblPlants");
		ensemblPItem.setActionCommand(ENSEMBL_PLANTSCOMMAND);
		ensemblPItem.addActionListener(myself);
		ensemblPItem.setToolTipText("Visit EnsemblPlants for information on selected genes");

		refseqItem = new JMenuItem("RefSeq");
		refseqItem.setActionCommand(REFSEQ_COMMAND);
		refseqItem.addActionListener(myself);
		refseqItem.setToolTipText("Visit RefSeq for information on selected genes");

		atgsItem = new JMenuItem("AtGeneSearch");
		atgsItem.setActionCommand(ATGENESEARCH_COMMAND);
		atgsItem.addActionListener(myself);
		atgsItem.setToolTipText("Visit AtGeneSearch for information on selected genes");
		tairItem = new JMenuItem("TAIR");
		tairItem.setActionCommand(TAIR_COMMAND);
		tairItem.addActionListener(myself);
		tairItem.setToolTipText("Visit for information on the first selected gene");

		thaleMineItem = new JMenuItem("Araport-ThaleMine");
		thaleMineItem.setActionCommand(ARAPORT_THALEMINE_COMMAND);
		thaleMineItem.addActionListener(myself);
		thaleMineItem.setToolTipText("Connect to Araport-ThaleMine for information on the first selected gene");

		jBrowseItem = new JMenuItem("Araport-JBrowse");
		jBrowseItem.setActionCommand(ARAPORT_JBROWSE_COMMAND);
		jBrowseItem.addActionListener(myself);
		jBrowseItem.setToolTipText("Visit Araport-JBrowse for information on the first selected gene");

		infoButtonMenu = new JMenu("External web applications");
		infoButtonMenu.setToolTipText("Visit an external website for more info on the selected genes");
		infoButtonMenu.add(geneCardsItem);
		infoButtonMenu.add(ensemblItem);
		infoButtonMenu.add(refseqItem);
		infoButtonMenu.add(ensemblPItem);
		infoButtonMenu.add(atgsItem);
		infoButtonMenu.add(tairItem);
		infoButtonMenu.add(thaleMineItem);
		infoButtonMenu.add(jBrowseItem);
		toolsMenu.add(infoButtonMenu);

		findSamples = new JMenuItem("Search by expression level");
		findSamples.setActionCommand(REPORT_COMMAND);
		findSamples.addActionListener(myself);
		toolsMenu.add(findSamples);
		mainMenuBar.add(toolsMenu);

		///////////// end tool menu//////////////////

		cascadeItem = new JMenuItem("Arrange Windows");
		cascadeItem.setActionCommand(CASCADE_WINDOWS_COMMAND);
		cascadeItem.addActionListener(myself);
		closeWindowItem = new JMenuItem("Close");
		closeWindowItem.setActionCommand(CLOSE_WINDOW_COMMAND);
		closeWindowItem.addActionListener(myself);
		closeWindowItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		windowMenu = new JMenu("Window");
		windowMenu.add(closeWindowItem);
		windowMenu.add(cascadeItem);
		windowMenu.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void menuDeselected(MenuEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void menuSelected(MenuEvent e) {
				JInternalFrame[] frames = getDesktop().getAllFrames();
				if (openWindowItems != null) {
					for (JMenuItem item : openWindowItems) {
						windowMenu.remove(item);
					}
				} else if (frames != null && frames.length > 0) {
					windowMenu.addSeparator();
				}
				if (frames == null || frames.length <= 0) {
					openWindowItems = null;
					return;
				}
				openWindowItems = new JCheckBoxMenuItem[frames.length];
				for (int i = 0; i < frames.length; i++) {
					openWindowItems[i] = new JCheckBoxMenuItem(frames[i].getTitle());
					openWindowItems[i].setActionCommand(SWITCH_WINDOW_COMMAND);
					openWindowItems[i].setName(i + "");
					openWindowItems[i].addActionListener(myself);
					if (frames[i].isSelected()) {
						openWindowItems[i].setSelected(true);
						closeWindowItem.setEnabled(frames[i].isClosable());
					} else {
						openWindowItems[i].setSelected(false);
					}
					windowMenu.add(openWindowItems[i]);
				}
			}

		});
		mainMenuBar.add(windowMenu);

		/*
		 * Added by Mohammed Alabsi - to add actionsMenu Date: June 16th, 2006
		 */

		actionsMenu = new JMenu("Actions");
		actionsMenu.setMnemonic(KeyEvent.VK_H);

		/*
		 * sending all data to BEV
		 */
		birdsEyeViewItem = new JMenuItem("Export all data to BirdsEyeView");
		birdsEyeViewItem.setMnemonic(KeyEvent.VK_C);
		birdsEyeViewItem.setActionCommand("BirdsEyeView");
		birdsEyeViewItem.addActionListener(myself);
		birdsEyeViewItem.setToolTipText("Export project to BirdsEyeView");
		actionsMenu.add(birdsEyeViewItem);

		/*
		 * sending selected rows to BEV
		 */
		birdsEyeViewSubsetItem = new JMenuItem("Export selected entities to BirdsEyeView");
		birdsEyeViewSubsetItem.setMnemonic(KeyEvent.VK_C);
		birdsEyeViewSubsetItem.setActionCommand("BirdsEyeViewSubset");
		birdsEyeViewSubsetItem.addActionListener(myself);
		birdsEyeViewSubsetItem.setToolTipText("Export selected entities to BirdsEyeView");
		actionsMenu.add(birdsEyeViewSubsetItem);

		/*
		 * Searching a phrase in PubMed
		 */
		pubMedItem = new JMenuItem("Search using PubMed");
		pubMedItem.setMnemonic(KeyEvent.VK_C);
		pubMedItem.setActionCommand("PubMed");
		pubMedItem.addActionListener(myself);
		pubMedItem.setToolTipText("Search for articles using PubMed");
		actionsMenu.add(pubMedItem);

		/*
		 * Search for sentences in common using PathBinder
		 */
		pathBinderItem = new JMenuItem("Search using PathBinder");
		pathBinderItem.setMnemonic(KeyEvent.VK_C);
		pathBinderItem.setActionCommand("PathBinder");
		pathBinderItem.addActionListener(myself);
		pathBinderItem.setToolTipText("Search two genes using PathBinder");
		actionsMenu.add(pathBinderItem);
		actionsMenu.setEnabled(false);

		/*
		 * View Pathways in Cytoscape
		 */
		cytoscapeItem = new JMenuItem("Visualize pathways in Cytoscape");
		cytoscapeItem.setMnemonic(KeyEvent.VK_C);
		cytoscapeItem.setActionCommand("cytoscape");
		cytoscapeItem.addActionListener(myself);
		cytoscapeItem.setToolTipText("Visualize pathways in Cytoscape");
		actionsMenu.add(cytoscapeItem);
		/*
		 * Lookup Gene Data
		 */
		atGeneSearchItem = new JMenuItem("Lookup Gene Data");
		atGeneSearchItem.setMnemonic(KeyEvent.VK_C);
		atGeneSearchItem.setActionCommand("atGeneSearch");
		atGeneSearchItem.addActionListener(myself);
		atGeneSearchItem.setToolTipText("Lookup Gene Data");
		actionsMenu.add(atGeneSearchItem);

		actionsMenu.setEnabled(false);
		// mainMenuBar.add(actionsMenu);
		// //////////////////////////////

		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		checkUpdateitem = new JMenuItem("Check for updates");

		checkUpdateitem.setName("update");
		checkUpdateitem.setActionCommand("update");
		checkUpdateitem.addActionListener(myself);
		helpMenu.add(checkUpdateitem);

		overviewItem = new JMenuItem("Overview");
		overviewItem.setMnemonic(KeyEvent.VK_O);
		helpListener = new MetaOmHelpListener();
		overviewItem.addActionListener(new MetaOmHelpListener());
		overviewItem.setName("overview");
		helpMenu.add(overviewItem);
		contextItem = new JMenuItem("Context Help");
		contextItem.setMnemonic(KeyEvent.VK_H);
		contextItem.addActionListener(helpListener);
		contextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		contextItem.setName("context");
		helpMenu.add(contextItem);
		tipsItem = new JMenuItem("Tip of the Day");
		tipsItem.setMnemonic(KeyEvent.VK_T);
		tipsItem.setActionCommand(SHOW_TIPS_COMMAND);
		tipsItem.addActionListener(myself);
		tipsItem.setName("tips");
		helpMenu.add(tipsItem);
		helpMenu.addSeparator();
		contactItem = new JMenuItem("Contact the Developer");
		contactItem.setMnemonic(KeyEvent.VK_C);
		contactItem.setActionCommand(CONTACT_COMMAND);
		contactItem.addActionListener(myself);
		//helpMenu.add(contactItem);
		aboutItem = new JMenuItem("About...");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.setActionCommand(ABOUT_COMMAND);
		aboutItem.addActionListener(myself);
		helpMenu.add(aboutItem);
		thirdPartyLibs = new JMenuItem("Third party libraries");
		thirdPartyLibs.setActionCommand(THIRD_PARTY_LIBS_COMMAND);
		thirdPartyLibs.addActionListener(myself);
		helpMenu.add(thirdPartyLibs);

		//historyMenu = new JMenu("Playback");
		JMenuItem playbackMenu = new JMenuItem("Playback Dashboard");
		playbackMenu.setMnemonic(KeyEvent.VK_A);
		playbackMenu.setActionCommand(PLAYBACK_COMMAND);
		playbackMenu.addActionListener(myself);

		mainMenuBar.add(helpMenu);


		// Menu bar created

		setMenuIcons();

		modalMaker = new SimpleModalMaker();

		if (Utils.isMac())
			doMacStuff();

		// Setting up the main window

		mainWindow.setJMenuBar(mainMenuBar);

		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		// int width=1124;
		// int height=868;
		// Configure and display the main frame
		mainWindow.setSize(width - 100, height - 100);
		// mainWindow.setLocation((width - mainWindow.getWidth()) / 2,
		// (height - mainWindow.getHeight()) / 2);
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
		mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				shutdown();
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				shutdown();
			}

			@Override
			public void windowStateChanged(WindowEvent e) {
				// TODO Auto-generated method stub
				//refreshPlaybackButton();
			}
		});
		if (activeProjectFile != null)
			new OpenProjectWorker(activeProjectFile).start();

		// Show tips during start up
		if (showTips == null)
			showTips = new MetaOmTips.MetaOmShowTipsChoice();

		if (showTips.isShowingOnStartup()) {
			tipper = new JTipOfTheDay(new MetaOmTips());
			if (currentTip != null) {
				tipper.setCurrentTip(currentTip.intValue());
				tipper.nextTip();
			}
			tipper.showDialog(mainWindow, showTips);
		}

		// urmi: check for updates and notify if updates are available
		checkUpdates(false);
		// Once the tip window is closed
		// Welcome dialog is shown
		try {
			showWelcomeDialog();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		taskBar = new TaskbarPanel();
		mainWindow.getContentPane().add(taskBar, BorderLayout.SOUTH);


	}

	/*
	 * Sets system variables and calls <code>init</code> method for creating the
	 * menu bar and dialog box
	 *
	 * @param args[] - a string variable which helps to determine the exception
	 * handling in <code>init</code>method
	 */

	public static void main(String[] args) {


		// begin with the interactive portion of the program
		/////////////////////////////////////////////////////////////////////////////

		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.laf.menu.about.name", "MetaOmGraph");

		// System.setProperty("dock:name", "MetaOmGraph");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MetaOmGraph");
		System.setProperty("swing.aatext", "true");

		System.setProperty("sun.java2d.renderer.doChecks", "true");

		if (args.length > 0) {
			if ("nobuffer".equals(args[0])) {
				init(false);
			} else {
				init(true);
			}
		} else {
			init(true);
		}
	}

	/**
	 * Prompts the user to save unsaved changes (if necessary), then exits the
	 * program. Doesn't exit if the user cancels the save prompt, though.
	 *
	 */
	public static void shutdown() {
		if (activeProject != null) {
			if (!closeProject())
				return;
		}
		File homeDir = new File(System.getProperty("user.home"));
		File prefsFile = new File(homeDir, "metaomgraph.prefs");
		if (!prefsFile.exists()) {
			String message = "Would you like to save your preferences?\nPreferences will be saved to "
					+ prefsFile.getAbsolutePath();
			int result = JOptionPane.showConfirmDialog(getMainWindow(), message, "Create preferences file",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				try {
					prefsFile.createNewFile();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(getMainWindow(), "Unable to create preferences file.", "Error",
							JOptionPane.ERROR_MESSAGE);
					System.err.println("Unable to create preferences file");
					e.printStackTrace();
				}
			}
		}
		if (prefsFile.exists()) {
			try {
				FileOutputStream fos = new FileOutputStream(prefsFile);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(Utils.getLastDir());

				if (recentProjects != null)
					out.writeObject(recentProjects);

				out.writeObject(showTips);
				if (tipper != null) {
					out.writeObject(new Integer(tipper.getCurrentTip()));
				} else if (currentTip != null) {
					out.writeObject(currentTip);
				}

				// save R path and R scripts path
				List<String> rObs = new ArrayList<>();
				if (userRPath == null) {
					rObs.add("");
				} else {
					rObs.add(userRPath);
				}
				if (getpathtoRscrips() == null) {
					rObs.add("");
				} else {
					rObs.add(getpathtoRscrips());
				}

				out.writeObject(rObs);
				out.writeObject(activeTheme.toString());
				out.writeObject(currentmogThemeName);
				out.writeObject(mogThemes);

				//writing reproducibility logging required parameter
				out.writeObject(permanentLogging);

				out.close();
				fos.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(getMainWindow(), "Unable to write to preferences file.", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.err.println("Unable to write to prefs file");
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	/**
	 * Fetches the main JFrame used by this program
	 *
	 * @return the program's main JFrame
	 */
	public static JFrame getMainWindow() {
		return mainWindow;
	}

	/**
	 * Fetches the desktop used in the main JFrame.
	 *
	 * @return The main desktop
	 */
	public static JDesktopPane getDesktop() {
		return desktop;
	}

	/**
	 * Prompts the user to save the active project. Doesn't check to see if there IS
	 * an active project or not, though. If the user hasn't yet saved the project
	 * and elects to do so here, a Save As dialog will appear.
	 *
	 * @return false if the user closes or cancels the dialog, true otherwise.
	 */
	public static boolean promptToSave() {
		int result = JOptionPane.showConfirmDialog(mainWindow, "Save changes to the active project?", "Unsaved changes",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if ((result == JOptionPane.CANCEL_OPTION) || (result == JOptionPane.CLOSED_OPTION))
			return false;
		else if (result == JOptionPane.YES_OPTION) {

			new AnimatedSwingWorker("Saving...", true) {

				@Override
				public Object construct() {
					if (activeProjectFile == null)
						saveAs();
					else
						activeProject.saveProject(activeProjectFile);
					return null;
				}

			}.start();
		}
		return true;
	}

	/**
	 * Runs when a project is opened, or a new project is created.
	 */
	private static void projectOpened() {
		if (welcomeDialog != null && welcomeDialog.isVisible()) {
			welcomeDialog.dispose();
			welcomeDialog = null;
		}

		// getActiveProject().showMeansDialog();
		projectTableFrame = new TaskbarInternalFrame("Project Data");
		FrameModel fm = new FrameModel("Project Data","Project Data",1);
		projectTableFrame.setModel(fm);

		projectTableFrame.putClientProperty("JInternalFrame.frameType", "normal");
		activeTablePanel = new MetaOmTablePanel(getActiveProject());
		activeProject.addChangeListener(activeTablePanel);
		projectTableFrame.getContentPane().add(activeTablePanel);
		projectTableFrame.setClosable(false);
		projectTableFrame.setIconifiable(true);
		projectTableFrame.setMaximizable(true);
		projectTableFrame.setResizable(true);
		desktop.add(projectTableFrame);
		// getMainWindow().getContentPane().add(activeTablePanel);
		projectTableFrame.setSize(800, 600);
		projectTableFrame.show();
		projectTableFrame.setName("projectdata.php");
		activeTablePanel.setExtInfoDividerPos(.5);
		saveProjectAsItem.setEnabled(true);
		saveProjectItem.setEnabled(true);
		projectMenu.setEnabled(true);
		activeTablePanel.getTabbedPane().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (activeTablePanel.getTabbedPane().getSelectedIndex() == 0) {
					projectTableFrame.setName("projectdata.php");
				} else if (activeTablePanel.getTabbedPane().getSelectedIndex() == 1) {
					projectTableFrame.setName("metadata.php");
				}
			}
		});
	}

	/*
	 * Adds an active project in to recent project vector
	 *
	 * @param projectFile - an instance of active project file
	 */
	public static void addRecentProject(File projectFile) {
		if (recentProjects == null)
			recentProjects = new Vector<File>();

		recentProjects.remove(projectFile);
		recentProjects.add(0, activeProjectFile);
		if (recentProjects.size() > 5)
			recentProjects.remove(5);

		initRecentProjectsMenu();
	}

	/*
	 * Adds the recently operated projects in recent project menu
	 */
	private static void initRecentProjectsMenu() {
		// Empty the Recent Projects menu if necessary
		if (recentProjectsMenuItems != null) {
			for (int x = 0; x < recentProjectsMenuItems.length; x++) {
				recentProjectsMenu.remove(recentProjectsMenuItems[x]);
			}
		}
		if ((recentProjects == null) || (recentProjects.size() <= 0)) {
			recentProjectsMenuItems = new JMenuItem[1];
			recentProjectsMenuItems[0] = new JMenuItem("No recent projects");
			recentProjectsMenuItems[0].setEnabled(false);
		} else {
			recentProjectsMenuItems = new JMenuItem[recentProjects.size()];
			for (int x = 0; x < recentProjectsMenuItems.length; x++) {
				recentProjectsMenuItems[x] = new JMenuItem(recentProjects.get(x).getAbsolutePath());
				recentProjectsMenuItems[x].setName(recentProjects.get(x).getAbsolutePath());
				recentProjectsMenuItems[x].setActionCommand(RECENT_PROJECT_COMMAND);
				recentProjectsMenuItems[x].addActionListener(myself);
			}
		}
		for (int x = 0; x < recentProjectsMenuItems.length; x++) {
			recentProjectsMenu.add(recentProjectsMenuItems[x]);
		}
	}

	/**
	 * Worker that constructs a new project. This is implemented as a separate
	 * thread so that the progress monitor will work.
	 */
	public static class NewProjectWorker extends SwingWorker {

		/** The number of info columns */
		private int infoColumns;

		/**
		 * An array of row names to use in place of the row names in the source file (if
		 * any exist)
		 */
		private Object[][] rowNames;

		/**
		 * An array of column names to use in place of the column names in the source
		 * file (if any exist)
		 */
		private Object[] colNames;

		/** An XML file containing extended info for the new project */
		private File extInfoFile;

		/** The delimited text file that contains the data for this project */
		private File source;

		/** The character used as a delimiter in the source file */
		private char delimiter;

		// urmi
		/** The character used as a delimiter in the metadata file */
		private char metadataDelimiter;

		/**
		 * The boolean variable which determines whether to ignore the consecutive
		 * delimiters or not
		 */
		private boolean ignoreConsecutiveDelimiters;

		/** A double variable which determines the blank space value */
		private Double blankValue;

		private boolean includeMetNet;

		// urmi

		private int readcsv = -1;

		/**
		 * Constructor.
		 *
		 * @param source
		 *            the delimited text file that contains the data for this project
		 * @param infoColumns
		 *            the number of info columns in the source file
		 * @param delimiter
		 *            the character used as a delimiter in the source file
		 * @param rowNames
		 *            the names of the rows (can be null). Replaces any row names in the
		 *            source file if non-null.
		 * @param colNames
		 *            the names of the columns (can be null). Replaces any column names
		 *            in the source file if non-null.
		 * @param extInfoFile
		 *            an XML file that contains extended info for the new project (can
		 *            be null)
		 */
		public NewProjectWorker(File source, int infoColumns, char delimiter, Object[][] rowNames, Object[] colNames,
				File extInfoFile, boolean ignoreConsecutiveDelimiters, Double blankValue, boolean includeMetNet) {
			this.delimiter = delimiter;
			this.rowNames = rowNames;
			this.colNames = colNames;
			this.extInfoFile = extInfoFile;
			this.infoColumns = infoColumns;
			this.source = source;
			this.ignoreConsecutiveDelimiters = ignoreConsecutiveDelimiters;
			this.blankValue = blankValue;
			this.includeMetNet = includeMetNet;
		}

		/**
		 * Constructor.
		 *
		 * @param source
		 *            the delimited text file that contains the data for this project
		 * @param infoColumns
		 *            the number of info columns in the source file
		 * @param delimiter
		 *            the character used as a delimiter in the source file
		 * @param rowNames
		 *            the names of the rows (can be null). Replaces any row names in the
		 *            source file if non-null.
		 * @param colNames
		 *            the names of the columns (can be null). Replaces any column names
		 *            in the source file if non-null.
		 * @param extInfoFile
		 *            an XML file that contains extended info for the new project (can
		 *            be null)
		 * @param ignoreConsecutiveDelimiters
		 *            The boolean variable which determines whether to \ ignore the
		 *            consecutive delimiters or not
		 * @param blankValue
		 *            A double variable which determines the blank space value.
		 */

		public NewProjectWorker(File source, int infoColumns, char delimiter, Object[][] rowNames, Object[] colNames,
				File extInfoFile, boolean ignoreConsecutiveDelimiters, Double blankValue) {

			this(source, infoColumns, delimiter, rowNames, colNames, extInfoFile, ignoreConsecutiveDelimiters,
					blankValue, true);
		}

		// urmi new constructor used with added csv_flag option
		public NewProjectWorker(File source, int infoColumns, char delimiter, Object[][] rowNames, Object[] colNames,
				File extInfoFile, boolean ignoreConsecutiveDelimiters, Double blankValue, int csv_flag) {

			this(source, infoColumns, delimiter, rowNames, colNames, extInfoFile, ignoreConsecutiveDelimiters,
					blankValue, true);
			this.readcsv = csv_flag;
		}

		// urmi new constructor used with added csv_flag and metadata delimiter added as
		// arguments
		public NewProjectWorker(File source, int infoColumns, char delimiter, Object[][] rowNames, Object[] colNames,
				File extInfoFile, boolean ignoreConsecutiveDelimiters, Double blankValue, int csv_flag,
				char metadatadelim) {

			this(source, infoColumns, delimiter, rowNames, colNames, extInfoFile, ignoreConsecutiveDelimiters,
					blankValue, true);
			this.readcsv = csv_flag;
			this.metadataDelimiter = metadatadelim;
		}

		/**
		 * Creates the project.
		 */
		@Override
		public Object construct() {
			// try {
			// activeProject=new MetaOmProject(new
			// FileInputStream(source),infoColumns,delimiter);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// activeProject = new MetaOmProject(source, infoColumns, delimiter,
			// ignoreConsecutiveDelimiters, blankValue,includeMetNet);
			// urmi using new constructor allows to initialize metadata delimiter value
			activeProject = new MetaOmProject(source, infoColumns, delimiter, metadataDelimiter,
					ignoreConsecutiveDelimiters, blankValue, includeMetNet);
			return null;
		}

		/**
		 * If the project was created successfully, this sets up the extra information
		 * (row names, column names, extended info) and displays the project table.
		 * Otherwise, it removes all traces of the attempted operation.
		 */
		@Override
		public void finished() {
			// System.out.println("Finished!");
			if (activeProject.isInitialized()) {
				if (activeProject.getDataColumnCount() <= 0) {
					JOptionPane.showMessageDialog(getMainWindow(), source.getName() + " does not contain any data.",
							"Error", JOptionPane.ERROR_MESSAGE);
					activeProject = null;
					closeProject();
					desktop.validate();
					mainWindow.validate();
					return;
				} else if (!activeProject.isUniqueDataCols()) {
					JOptionPane.showMessageDialog(getMainWindow(),
							source.getName()
							+ " contains duplicate column names for data columns.\nPlease check your file.",
							"Error", JOptionPane.ERROR_MESSAGE);
					activeProject = null;
					closeProject();
					desktop.validate();
					mainWindow.validate();
					return;
				}
				// System.out.print("Setting row names... ");
				if (rowNames != null) {
					activeProject.setRowNames(rowNames);
				}
				// System.out.println("Done!");
				// System.out.print("Setting column headers... ");
				if (colNames != null) {
					activeProject.setDataColumnHeaders(colNames);
				}

				// System.out.println("Done!");
				// System.out.print("Loading extended info... ");
				if (!extInfoFile.exists())
					extInfoFile = null;

				projectOpened();
				try {
					// JOptionPane.showMessageDialog(null, "LOADING MD");
					if (this.readcsv == -1) {
						// JOptionPane.showMessageDialog(null, "Loading Xml");
						activeProject.loadMetadata(extInfoFile);
					}
					if (this.readcsv == 1) {
						// JOptionPane.showMessageDialog(null, "Loading csv");

						activeProject.loadMetadata_csv(extInfoFile);

					}

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				// System.out.println("Done!");

				// mainWindow.setTitle("MetaOmGraph - New Project (" +
				// getActiveProject().getDataColumnCount() + " samples)");
				// urmi
				fixTitle();
				//projectOpened();


				try {
					String projFileName = source.getAbsolutePath();

					String projectName = projFileName.substring(projFileName.lastIndexOf(File.separator)+1,projFileName.lastIndexOf('.'));
					String currDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
					String logFilePath = projFileName.substring(0,projFileName.lastIndexOf(File.separator))+File.separator+"moglog"+File.separator+projectName+"_"+currDate+".log";


					logger = updateLogger(logFilePath);
					setCurrentLogFilePath(logFilePath);

					setLoggingRequired(true);
					logGeneralProperties();
					int newProjectId = logNewProject(source.getAbsolutePath(),extInfoFile.getAbsolutePath());

					currentProjectActionId = newProjectId;

				}
				catch(Exception e) {

				}

				// if metadata is being read wait for the metadata read window to show up other
				// wise just proceed with the data file to the main table
				if (this.readcsv == 1) {
					while (activeProject.getActiveReadmetadataForm() == null) {

					}
					activeProject.getActiveReadmetadataForm().toFront();
				}

				// init col type
				if (MetaOmGraph.getActiveTable() != null) {
					activeProject.initColTypes();

				}

				if(activeProject.getColumnHeaders() != null) {
					activeProject.setDataColumnName(activeProject.getColumnHeaders()[0]);
				}

			} else {
				// No need to report errors since MetaOmProject does it for
				// us.
				activeProject = null;
				closeProject();
				desktop.validate();
				mainWindow.validate();
			}
		}

	}

	/**
	 * Prompts the user to select a destination file, then saves the active project
	 * to that file.
	 *
	 */
	private static void saveAs() {
		final File destination = Utils.chooseFileToSave(new GraphFileFilter(GraphFileFilter.PROJECT), "mog",
				getMainWindow(), true);
		if (destination == null)
			return;
		activeProjectFile = destination;
		new AnimatedSwingWorker("Saving...", true) {

			@Override
			public Object construct() {
				if (activeProject.saveProject(destination)) {
					/*
					 * mainWindow.setTitle("MetaOmGraph - " + destination.getName() + " (" +
					 * getActiveProject().getDataColumnCount() + " samples)");
					 */
					fixTitle();

					//Harsha - reproducibility log
					HashMap<String,Object> saveProjectParameters = new HashMap<String,Object>();
					saveProjectParameters.put("mogFilePath",destination.getAbsolutePath());

					HashMap<String,Object> result = new HashMap<String,Object>();
					result.put("result", "OK");
					ActionProperties saveProjectAction = new ActionProperties("save-as-project",saveProjectParameters,null,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					//saveProjectAction.logActionProperties();

				}
				addRecentProject(activeProjectFile);
				return null;
			}
		}.start();
	}

	/**
	 * Creates a MetaOmProject from a previously saved .mcg or .mog project file.
	 *
	 * @author Nick Ransom
	 *
	 */
	public static class OpenProjectWorker extends AnimatedSwingWorker {

		/**
		 * The MetaOmGraph project file to open
		 */
		File source;

		/**
		 * Constructor
		 *
		 * @param source
		 *            the MetaOmGraph project file to open
		 */
		public OpenProjectWorker(File source) {
			super("Opening project...");
			this.source = source;
		}

		/**
		 * Creates the project.
		 */
		@Override
		public Object construct() {
			// hide welcome dialog
			if (welcomeDialog != null) {
				welcomeDialog.setVisible(false);
			}
			// invokeAndWait to fix problems of swing in mac (doesn't fix the issue)
			try {
				EventQueue.invokeAndWait(new Runnable() {
					@Override
					public void run() {

						try{
							activeProject = new MetaOmProject(source);
							String projFileName = source.getAbsolutePath();
							String projectName = projFileName.substring(projFileName.lastIndexOf(File.separator)+1,projFileName.lastIndexOf('.'));
							String currDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
							String logFilePath = projFileName.substring(0,projFileName.lastIndexOf(File.separator))+File.separator+"moglog"+File.separator+projectName+"_"+currDate+".log";

							if(getLoggingRequired()) {
								logger = updateLogger(logFilePath);

							}

							setCurrentLogFilePath(logFilePath);

							logGeneralProperties();
							logOpenProject(projectName,source);


							currentProjectActionId = openProjectAction.getActionNumber();

						}
						catch(Exception e){
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							e.printStackTrace(pw);
							JOptionPane.showMessageDialog(null, sw.toString());
						}

					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				// TODO Auto-generated catch block

				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);

				JOptionPane.showMessageDialog(null, sw.toString());

			}
			// activeProject = new MetaOmProject(source);
			return null;
		}

		/**
		 * If the project was opened successfully, this opens a table frame and calls
		 * projectOpened(). Otherwise, it removes all traces of the attempted operation.
		 */
		@Override
		public void finished() {
			if (activeProject == null) {
				JOptionPane.showMessageDialog(null, "Failed to open the project.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (activeProject.isInitialized()) {
				// mainWindow.setTitle("MetaOmGraph - " + source.getName() + " (" +
				// getActiveProject().getDataColumnCount()+ " samples)");
				// urmi
				activeProjectFile = source;
				addRecentProject(activeProjectFile);
				projectOpened();
				// init col types when opening a new project
				activeProject.initColTypes();
				// init default reps
				MetadataHybrid ob = activeProject.getMetadataHybrid();
				if (ob != null) {
					ob.setDefaultRepsMap(ob.buildRepsMap(ob.getDefaultRepCol()));
				}
				fixTitle();

			} else {
				JOptionPane
				.showMessageDialog(MetaOmGraph.getMainWindow(),
						"There was a problem reading " + source.getName()
						+ ".  Make sure it's a valid MetaOmGraph file.",
						"Error", JOptionPane.ERROR_MESSAGE);
				activeProject = null;
				closeProject();
				desktop.validate();
				mainWindow.validate();
			}
		}

	}
	/*
	 * Opens a new or existing project
	 *
	 * @param myProject - instance of the MetaOmProject which is to be opened
	 *
	 * @return false if the currently opened project cannot be closed, true
	 * otherwise
	 */

	public static boolean openProject(MetaOmProject myProject) {
		if (activeProject != null)
			if (!closeProject())
				return false;
		activeProject = myProject;
		File source = activeProject.getSourceFile();
		if (source != null) {
			// mainWindow.setTitle("MetaOmGraph - " + source.getName() + " (" +
			// getActiveProject().getDataColumnCount() + " samples)");
			// urmi
			fixTitle();
		} else {
			mainWindow.setTitle("MetaOmGraph - New Project (" + getActiveProject().getDataColumnCount() + " samples)");
		}
		activeProjectFile = source;
		projectOpened();
		return true;
	}

	/**
	 * Closes the active project. If there are unsaved changes to the active
	 * project, the user is prompted to save them. If the user cancels the save
	 * prompt, this returns false and does nothing. Otherwise, it closes the
	 * project, removes any open JInternalFrames, and disables the appropriate menu
	 * items.
	 *
	 * @return false if the user cancelled the save prompt, true otherwise
	 */
	public static boolean closeProject() {
		if ((activeProject != null) && (activeProject.isChanged()))
			if (!promptToSave())
				return false;

		//Harsha - reproducibility log
		HashMap<String,Object> closeProjectParameters = new HashMap<String,Object>();
		try {
			closeProjectParameters.put("dataFilePath",activeProject.getSourceFile().getAbsolutePath());
			closeProjectParameters.put("mogFilePath",activeProjectFile.getAbsolutePath());
			closeProjectParameters.put("dimensions",String.valueOf(activeProject.getDataColumnCount()));
			closeProjectParameters.put("parent", -1);
		}
		catch(Exception e) {

		}
		activeProject = null;
		JInternalFrame[] closeUs = desktop.getAllFrames();
		for (int x = 0; x < closeUs.length; x++)
			closeUs[x].dispose();
		saveProjectAsItem.setEnabled(false);
		saveProjectItem.setEnabled(false);
		projectMenu.setEnabled(false);
		mainWindow.setTitle("MetaOmGraph");
		activeProjectFile = null;
		Component[] comps = desktop.getComponents();
		// Remove all open frames.
		for (int x = 0; x < comps.length; x++)
			if (comps[x] instanceof JInternalFrame)
				((JInternalFrame) (comps[x])).dispose();
		taskBar.removeAllTabsFromTaskbar();
		MetaOmAnalyzer.reset();
		// System.gc();

		//Harsha - reproducibility log

		try {
			HashMap<String,Object> dataMap = new HashMap<String,Object>();
			HashMap<String,Object> result = new HashMap<String,Object>();
			dataMap.put("Command", "Close current project");
			result.put("result", "OK");
			ActionProperties closeProjectAction = new ActionProperties("close-project",closeProjectParameters,dataMap,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			closeProjectAction.logActionProperties();

			currentProjectActionId = closeProjectAction.getActionNumber();
			stopLogger();
			getReproducibilityDashboardPanel().autoSaveLog(0);

		}
		catch(Exception e) {

		}

		return true;
	}

	/**
	 * Fetches the MetaOmGraph icon as an Image.
	 *
	 * @return an Image of the MetaOmGraph icon
	 */
	public static Image getIconImage() {
		return myIcon;
	}

	/**
	 * Fetches the ActionListener used to display a Help frame.
	 *
	 * @return the ActionListener used to dispaly a Help frame
	 */
	public static MetaOmHelpListener getHelpListener() {
		return helpListener;
	}

	/**
	 * Fetches the WindowListener used to make a frame/dialog modal, but still be
	 * able to access the Help frame. Normally, when a dialog is modal, it
	 * completely blocks the current process, thus disabling access to all
	 * frames/dialogs besides the modal dialog and its children. By applying this
	 * listener to a frame or dialog, when that frame/dialog is visible, it will
	 * disable only the main MetaOmGraph window, thus allowing the user to still
	 * access other frames.
	 *
	 * @return a WindowListener that, when applied to a frame or dialog, will
	 *         disable the main MetaOmGraph window when the frame/dialog is visible
	 */
	public static SimpleModalMaker getModalMaker() {
		return modalMaker;
	}

	/**
	 * Fetches the MetaOmTablePanel for the active project.
	 *
	 * @return the MetaOmTablePanel for the active project
	 */
	public static MetaOmTablePanel getActiveTable() {
		return activeTablePanel;
	}

	/**
	 * Selects a row inside the internal frame
	 *
	 * @throws PropertyVetoException
	 *             if the proposed change to a property represents an unacceptable
	 *             value
	 */
	public static void tableToFront() {
		try {
			projectTableFrame.setSelected(true);
			projectTableFrame.toFront();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// urmi return project table
	public static JInternalFrame returnprojectTableFrame() {
		return projectTableFrame;
	}

	// urmi update the metadata after updating metadata model
	public static void updateWindow() {
		// JOptionPane.showMessageDialog(null, "updating");
		// Display the extended info tab in the table panel
		activeTablePanel.addExtInfoTab();
		activeTablePanel.setExtInfoDividerPos(.6);

	}

	/**
	 * ActionListener for the MetaOmGraph window's menu items.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Close the active project
		if (CLOSE_PROJECT_COMMAND.equals(e.getActionCommand())) {
			if (closeProject()) {
				try {
					showWelcomeDialog();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			return;
		}

		// Open a previously saved project
		if (OPEN_COMMAND.equals(e.getActionCommand())) {
			openAnotherProject();

		}

		if(DOWNLOAD_METABOLOMICS_PROJ_COMMAND.equals(e.getActionCommand())) {
			downloadAndOpenProject(DownloadSampleProject.Metabolomics);
		}

		if(DOWNLOAD_MICROARRAY_PROJ_COMMAND.equals(e.getActionCommand())) {
			downloadAndOpenProject(DownloadSampleProject.MicroArray);
		}

		if(DOWNLOAD_CANCER_RNASEQ_PROJ_COMMAND.equals(e.getActionCommand())) {
			downloadAndOpenProject(DownloadSampleProject.HumanCancerRNASeq);
		}

		if(DOWNLOAD_METABOLOMICS_PROJ_COMMAND.equals(e.getActionCommand())) {
			downloadAndOpenProject(DownloadSampleProject.Metabolomics);
		}

		if(DOWNLOAD_MICROARRAY_PROJ_COMMAND.equals(e.getActionCommand())) {
			downloadAndOpenProject(DownloadSampleProject.MicroArray);
		}

		if(DOWNLOAD_CANCER_RNASEQ_PROJ_COMMAND.equals(e.getActionCommand())) {
			downloadAndOpenProject(DownloadSampleProject.HumanCancerRNASeq);
		}

		// Save the active project to a new file
		if (SAVE_AS_COMMAND.equals(e.getActionCommand())) {
			saveAs();
			return;
		}

		// Save the active project
		if (SAVE_COMMAND.equals(e.getActionCommand())) {
			// If the user hasn't saved this project before, this operation
			// should be treated as Save As...
			new AnimatedSwingWorker("Saving...", true) {

				@Override
				public Object construct() {
					if (activeProjectFile == null)
						saveAs();
					else
						activeProject.saveProject(activeProjectFile);
					return null;
				}

			}.start();

			return;
		}

		// Exit the program
		if (QUIT_COMMAND.equals(e.getActionCommand())) {
			shutdown();
			return;
		}

		// Load extended info
		if (LOAD_INFO_COMMAND.equals(e.getActionCommand())) {
			new AnimatedSwingWorker("Loading Metadata", true) {

				@Override
				public Object construct() {
					try {
						getActiveProject().loadMetadata();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					if (getActiveProject().getMetadata() != null) {
						// Notify any open chart panels that extended info is
						// loaded
						for (int x = 0; x < getDesktop().getComponentCount(); x++)
							if (getDesktop().getComponent(x) instanceof MetaOmChartPanel.MetaOmFrame) {
								((MetaOmChartPanel.MetaOmFrame) getDesktop().getComponent(x)).enableExtendedInfo();
							}
						// Display the extended info tab in the table panel
						activeTablePanel.addExtInfoTab();
						activeTablePanel.setExtInfoDividerPos(.5);
					}
					return null;
				}
			}.start();
			return;
		}

		// urmi load csv metadata for stored project
		if (LOAD_INFO_COMMAND_csv.equals(e.getActionCommand())) {

			// read csv file and set MOGcollection objects
			// s
			// choose file
			JFileChooser fChooser = new JFileChooser(edu.iastate.metnet.metaomgraph.utils.Utils.getLastDir());
			int rVal = fChooser.showOpenDialog(MetaOmGraph.getMainWindow());
			if (rVal == JFileChooser.APPROVE_OPTION) {
				File source = fChooser.getSelectedFile();
				// choose delimiter
				String[] delims = { "Tab", ",", ";", "Space" };
				String metadataDelim = (String) JOptionPane.showInputDialog(null,
						"Please choose delimiter for the file...", "Please choose delimiter",
						JOptionPane.QUESTION_MESSAGE, null, delims, delims[0]);
				String delim;
				if (metadataDelim.equals("Tab")) {
					delim = "\t";

				} else if (metadataDelim.equals("Space")) {
					delim = " ";
				} else {
					delim = metadataDelim;
				}

				new AnimatedSwingWorker("Working...", true) {

					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								try {

									final ReadMetadata readMetadataframe = new ReadMetadata(source.getAbsolutePath(),
											delim);


									MetaOmGraph.getDesktop().add(readMetadataframe);

									FrameModel importMetadataModel = new FrameModel("Import Sample Metadata", "Read Metadata File", 40);

									readMetadataframe.setModel(importMetadataModel);

									readMetadataframe.setVisible(true);

									readMetadataframe.setResizable(false);

									readMetadataframe.toFront();

									//Harsha - reproducibility log
									HashMap<String,Object> actionMap = new HashMap<String,Object>();
									actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
									actionMap.put("section", "Sample Metadata Table");

									HashMap<String,Object> dataMap = new HashMap<String,Object>();
									dataMap.put("Metadata File",source.getAbsolutePath());

									HashMap<String,Object> resultLog = new HashMap<String,Object>();
									resultLog.put("result", "OK");

									ActionProperties importMetadataAction = new ActionProperties("import-metadata",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
									importMetadataAction.logActionProperties();

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						return null;
					}

				}.start();
			}
			return;
		}

		if (LOAD_TREE.equals(e.getActionCommand())) {

			// read tree file and set MOGcollection objects
			// metadataCollection oject to read csv file
			MetadataTreeStructure obj = null;
			File source = Utils.chooseFileToOpen(new GraphFileFilter(GraphFileFilter.TREE),
					MetaOmGraph.getMainWindow());
			// read the tree object file
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(source.getAbsolutePath());
				ObjectInputStream objectinputstream = new ObjectInputStream(fin);
				obj = (MetadataTreeStructure) objectinputstream.readObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// set tree object
			if (obj == null) {
				JOptionPane.showMessageDialog(null, "MOG tree file import failed!");
				return;
			} else {
				MetaOmGraph.getActiveProject().setTreeStructure(obj);
				JOptionPane.showMessageDialog(null, "MOG tree file imported successfully!");
			}

			return;
		}

		if ("structure".equals(e.getActionCommand())) {
			// check if csv is loaded
			try {
				// DisplayMetadataEditor obj = new DisplayMetadataEditor();
				// obj.setVisible(true);

				MetadataImportWizard frame = new MetadataImportWizard(getActiveProject().getMetadataHybrid(), false);
				frame.setVisible(true);
			} catch (NullPointerException ne) {
				JOptionPane.showMessageDialog(null, "No metadata found. ERROR");

			}
		}

		if ("coltypes".equals(e.getActionCommand())) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {

						SetColTypes f = new SetColTypes(activeProject.getInfoColumnNames(), false);
						f.setDefaultCloseOperation(2);
						f.setClosable(true);
						f.setResizable(true);
						f.pack();
						// JOptionPane.showMessageDialog(null, "addd f");
						f.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
								MetaOmGraph.getMainWindow().getHeight() / 2);
						MetaOmGraph.getDesktop().add(f);
						f.setVisible(true);
						f.toFront();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error");
						e.printStackTrace();
					}
				}
			});

		}

		if ("coltypesMD".equals(e.getActionCommand())) {
			if (getActiveProject().getMetadataHybrid() == null) {
				JOptionPane.showMessageDialog(null, "No metadata found", "No Metadata", JOptionPane.ERROR_MESSAGE);
			}
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {

						SetColTypes f = new SetColTypes(activeProject.getInfoColumnNames(), true);
						f.setDefaultCloseOperation(2);
						f.setClosable(true);
						f.setResizable(true);
						f.pack();
						// JOptionPane.showMessageDialog(null, "addd f");
						f.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
								MetaOmGraph.getMainWindow().getHeight() / 2);
						MetaOmGraph.getDesktop().add(f);
						f.setVisible(true);
						f.toFront();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error");
						e.printStackTrace();
					}
				}
			});

		}

		if ("viewmetadata".equals(e.getActionCommand())) {
			// check if csv is loaded
			try {

				Metadataviewer obj = new Metadataviewer("MetadataViewer");
				obj.setVisible(true);
			} catch (NullPointerException ne) {
				JOptionPane.showMessageDialog(null, "No .csv metadata read. ERROR");

			}

		}

		// Display the Project Properties frame
		if (PROPERTIES_COMMAND.equals(e.getActionCommand())) {
			if ((propertiesFrame != null) && (propertiesFrame.isVisible())) {
				propertiesFrame.toFront();
				return;
			}

			ProjectPropertiesPanel ppp = new ProjectPropertiesPanel(getActiveProject());
			propertiesFrame = new TaskbarInternalFrame("Properties");
			FrameModel propertiesFrameModel = new FrameModel("Properties", "Properties", 33);
			propertiesFrame.setModel(propertiesFrameModel);
			propertiesFrame.putClientProperty("JInternalFrame.frameType", "normal");
			propertiesFrame.getContentPane().add(ppp, BorderLayout.CENTER);
			propertiesFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			propertiesFrame.pack();
			propertiesFrame.setSize(400, 400);
			propertiesFrame.setClosable(true);
			propertiesFrame.setMaximizable(true);
			propertiesFrame.setIconifiable(true);
			MetaOmGraph.getDesktop().add(propertiesFrame);
			propertiesFrame.setVisible(true);
			propertiesFrame.setName("projectproperties.php");
			return;
		}

		if (CONTACT_COMMAND.equals(e.getActionCommand())) {
			ExceptionHandler.getInstance(mainWindow).contact();
			return;
		}

		if ("update".equals(e.getActionCommand())) {
			checkUpdates(true);
			return;
		}

		// Display the About frame
		if (ABOUT_COMMAND.equals(e.getActionCommand())) {
			// if (e.getModifiers() == (ActionEvent.CTRL_MASK
			// | ActionEvent.SHIFT_MASK)) {
			if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
				showAboutFrame();
			} else {
				showNewAboutFrame();

			}
			return;
		}

		if(THIRD_PARTY_LIBS_COMMAND.equals(e.getActionCommand())) {
			JDialog dialog = new JDialog(getMainWindow(), "Third party libraries used", true);
			dialog.add(new ThirdPartyLibs());
			dialog.pack();
			dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}

		if (PLAYBACK_COMMAND.equals(e.getActionCommand())) {
			if(ReproducibilityDashboardFrame == null) {
				createReproducibilityLoggingFrame();
				if(ReproducibilityDashboardFrame!= null) {
					ReproducibilityDashboardFrame.setClosable(true);
				}
			}
		}

		if(THIRD_PARTY_LIBS_COMMAND.equals(e.getActionCommand())) {
			JDialog dialog = new JDialog(getMainWindow(), "Third party libraries used", true);
			dialog.add(new ThirdPartyLibs());
			dialog.pack();
			dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}

		if (PLAYBACK_COMMAND.equals(e.getActionCommand())) {
			if(ReproducibilityDashboardFrame == null) {
				createReproducibilityLoggingFrame();
				if(ReproducibilityDashboardFrame!= null) {
					ReproducibilityDashboardFrame.setClosable(true);
				}
			}
		}

		if (MERGE_COMMAND.equals(e.getActionCommand())) {
			ProjectMerger.showMergeDialog();
			return;
		}

		// Create a new project from a delimited text file

		if (NEW_PROJECT_DELIMITED_COMMAND.equals(e.getActionCommand())) {
			if (welcomeDialog != null) {
				welcomeDialog.setVisible(false);
			}
			startNewFromDelimited();
		}

		// Create a new project from a SOFT file
		if (NEW_PROJECT_SOFT_COMMAND.equals(e.getActionCommand())) {
			//nothing to do
			return;
		}

		// Create a new project from the metabolomics database
		if (NEW_PROJECT_METABOLOMICS_COMMAND.equals(e.getActionCommand())) {
			if (!closeProject())
				return;
			//nothing to do
			return;
		}

		if (NEW_PROJECT_ARRAYEXPRESS_COMMAND.equals(e.getActionCommand())) {
			if (!closeProject())
				return;

			final AEImportDialog dialog;
			try {
				dialog = new AEImportDialog(getMainWindow());
				dialog.getRootPane().getActionMap().put("help",
						MetaOmGraph.getHelpListener().createHelpAction("arrayexpress.php"));
				dialog.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");
				dialog.setVisible(true);
				final List<String> ids = dialog.getSelectedExpIDs();
				final Double normalizeValue = dialog.getNormalizeValue();

				if (ids.size() == 0)
					return;

				final File dest = Utils.chooseFileToSave(Utils.createFileFilter("txt", "Text files"), "txt",
						getMainWindow(), true);
				if (dest == null)
					return;

				File tempDir = new File(dest.getParent() + "/MOG.temp/");
				// AEDataDownloader.compileProcessedData(ids, tempDest, ae
				// .getSpecies());
				boolean success = AEProjectMaker.createProject(dialog.getSelectedExps(), dialog.getArray(), dest,
						tempDir);

				if (!success)
					return;

				if (dest.exists()) {
					new AnimatedSwingWorker("Analyzing") {

						File dataFile, metadataFile;

						@Override
						public Object construct() {
							metadataFile = AEProjectMaker.getLastMetadataFile();
							try {
								// final TreeSet<MeanResult> means =
								// DataNormalizer
								// .getWiesiaMeans(dest, '\t', 1);
								final TreeSet<MeanResult> means = DataNormalizer.getMeans(dest, '\t', 1);
								// DataNormalizer.removeBadCols(dest, means);
								DataNormalizer.SplitResult splitResult = DataNormalizer.newshowSplitDialog(dest,
										AEProjectMaker.getLastMetadataFile(), means, false, normalizeValue);

								if (splitResult.isSplit()) {
									int result = JOptionPane.showOptionDialog(getMainWindow(),
											"Split successful.  Which file would you like to open?", "Split",
											JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
											new String[] { "Non-logged", "Logged" }, "Non-logged");
									if (result == JOptionPane.YES_OPTION) {
										// System.out
										// .println("Opening non-logged file");
										dataFile = splitResult.getNonloggedFile();
										metadataFile = splitResult.getNonloggedMetadataFile();
										// if (normalizeValue != null) {
										// this.setMessage("Normalizing");
										// dataFile = DataNormalizer.normalize(
										// dataFile, '\t', 1, means,
										// normalizeValue);
										// }
									} else {
										// System.out.println("Opening logged file");
										dataFile = splitResult.getLoggedFile();
										metadataFile = splitResult.getLoggedMetadataFile();
									}
								} else if (normalizeValue != null) {
									this.setMessage("Normalizing");
									dataFile = DataNormalizer.normalize(dest, '\t', 1, means, normalizeValue);
								} else {
									dataFile = dest;
								}
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
							return null;
						}

						@Override
						public void finished() {
							super.finished();
							boolean includeMetNet = false;

							if (AEImportDialog.ATH1_ARRAY_NAME.equals(dialog.getSpecies()))
								includeMetNet = true;

							new NewProjectWorker(dataFile, 1, '\t', null, null, metadataFile, false, null,
									includeMetNet) {
								@Override
								public void finished() {
									super.finished();
									if (getActiveProject() == null)
										return;

									if (AEImportDialog.HGU133A_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_HGU133A_COMMAND_SUFFIX));
									} else if (AEImportDialog.MOUSE_4302_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_MOUSE430_2_COMMAND_SUFFIX));
									} else if (AEImportDialog.SOYBEAN_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_SOYBEAN_COMMAND_SUFFIX));
									} else if (AEImportDialog.RAT_230_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_RAT230_2_COMMAND_SUFFIX));
									} else if (AEImportDialog.YEAST_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_S98_COMMAND_SUFFIX));
									} else if (AEImportDialog.BARLEY_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_BARLEY_SUFFIX));
									} else if (AEImportDialog.RICE_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_RICE_SUFFIX));
									} else if (AEImportDialog.HGU133PLUS2_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_HGU133PLUS2_COMMAND_SUFFIX));
									} else if (AEImportDialog.YEAST2_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_YEAST2_COMMAND_SUFFIX));
									} else if (AEImportDialog.ZEBRAFISH_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_ZEBRAFISH_COMMAND_SUFFIX));
									} else if (AEImportDialog.ATH1_ARRAY_NAME.equals(dialog.getSpecies())) {
										myself.actionPerformed(new ActionEvent(this, 0,
												IMPORT_ANNOTATION_COMMAND_PREFIX + IMPORT_ATH1_COMMAND_SUFFIX));
									}
									long endTime = System.currentTimeMillis();
									// System.out.println("Took "
									// + (endTime - startTime) + "ms");
								}
							}.start();
						}

					}.start();
				} else {
					JOptionPane.showMessageDialog(getMainWindow(), "Error opening file:\n" + dest.getAbsolutePath(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
				return;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
						"Error during construction:\n" + ioe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}

		// Open a recent project
		if (RECENT_PROJECT_COMMAND.equals(e.getActionCommand())) {
			if (activeProject != null)
				if (!closeProject())
					return;

			File source;
			if (e.getSource() instanceof Component) {
				source = new File(((Component) e.getSource()).getName());
			} else if (e.getSource() instanceof JComponent) {
				source = new File(((JComponent) e.getSource()).getName());
			} else if (e.getSource() instanceof ClickableLabel) {
				source = new File(((ClickableLabel) e.getSource()).getName());
			} else {
				System.err.println("Recent project command called by " + e.getSource().getClass());
				return;
			}
			if (!source.exists()) {
				JOptionPane.showMessageDialog(getMainWindow(), source.getAbsolutePath() + "\nwas not found.",
						"File not found", JOptionPane.ERROR_MESSAGE);
				return;
			}
			new OpenProjectWorker(source).start();

			return;
		}

		// Show Tip of the Day
		if (SHOW_TIPS_COMMAND.equals(e.getActionCommand())) {
			if (tipper == null) {
				tipper = new JTipOfTheDay(new MetaOmTips());
				if (currentTip != null) {
					tipper.setCurrentTip(currentTip.intValue());
				}
			}
			tipper.nextTip();
			tipper.showDialog(getMainWindow(), showTips, true);
			return;
		}

		// Import human annotation information
		if (e.getActionCommand() != null && e.getActionCommand().startsWith(IMPORT_ANNOTATION_COMMAND_PREFIX)) {
			final Annotation annot;
			if (e.getActionCommand().endsWith(IMPORT_HGU133A_COMMAND_SUFFIX)) {
				annot = Annotation.HUMAN_HGU133A;
			} else if (e.getActionCommand().endsWith(IMPORT_MOUSE430_2_COMMAND_SUFFIX)) {
				annot = Annotation.MOUSE_430_2;
			} else if (e.getActionCommand().endsWith(IMPORT_RAE230A_COMMAND_SUFFIX)) {
				annot = Annotation.RAT_RAE230A;
			} else if (e.getActionCommand().endsWith(IMPORT_RAEX1_COMMAND_SUFFIX)) {
				annot = Annotation.RAT_RAEX1;
			} else if (e.getActionCommand().endsWith(IMPORT_RAGENE1_COMMAND_SUFFIX)) {
				annot = Annotation.RAT_RAGENE1;
			} else if (e.getActionCommand().endsWith(IMPORT_RAT230_2_COMMAND_SUFFIX)) {
				annot = Annotation.RAT_230_2;
			} else if (e.getActionCommand().endsWith(IMPORT_SOYBEAN_COMMAND_SUFFIX)) {
				annot = Annotation.SOYBEAN;
			} else if (e.getActionCommand().endsWith(IMPORT_U34A_COMMAND_SUFFIX)) {
				annot = Annotation.RAT_U34A;
			} else if (e.getActionCommand().endsWith(IMPORT_S98_COMMAND_SUFFIX)) {
				annot = Annotation.YEAST_S98;
			} else if (e.getActionCommand().endsWith(IMPORT_BARLEY_SUFFIX)) {
				annot = Annotation.BARLEY;
			} else if (e.getActionCommand().endsWith(IMPORT_RICE_SUFFIX)) {
				annot = Annotation.RICE;
			} else if (e.getActionCommand().endsWith(IMPORT_HGU133PLUS2_COMMAND_SUFFIX)) {
				annot = Annotation.HUMAN_HGU133PLUS2;
			} else if (e.getActionCommand().endsWith(IMPORT_YEAST2_COMMAND_SUFFIX)) {
				annot = Annotation.YEAST2;
			} else if (e.getActionCommand().endsWith(IMPORT_ATH1_COMMAND_SUFFIX)) {
				annot = Annotation.ARABIDOPSIS_ATH1;
			} else if (e.getActionCommand().endsWith(IMPORT_ZEBRAFISH_COMMAND_SUFFIX)) {
				annot = Annotation.ZEBRAFISH;
			} else if (e.getActionCommand().endsWith(IMPORT_CUSTOM_COMMAND_SUFFIX)) {
				annot = Annotation.CUSTOM;
				File source = Utils.chooseFileToOpen();
				if (source == null)
					return;

				annot.setSource(source);
			} else {
				return;
			}
			new AnimatedSwingWorker("Importing...") {

				@Override
				public Object construct() {
					try {
						AnnotationImporter.importAnnotation(getActiveProject(), annot);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					return null;
				}

				@Override
				public void finished() {
					super.finished();
					getActiveTable().sizeColumnsToFit();
					getActiveTable().getTable().paintImmediately(0, 0, getActiveTable().getTable().getWidth(),
							getActiveTable().getTable().getHeight());
					getActiveTable().refresh();
					// System.gc();
				}
			}.start();
			return;
		}
		if (EXPORT_LISTS_COMMAND.equals(e.getActionCommand())) {
			String[] infoCols = getActiveProject().getInfoColumnNames();
			Object result = JOptionPane.showInputDialog(getMainWindow(), "Which column contains unique IDs?",
					"Export lists", JOptionPane.QUESTION_MESSAGE, null, infoCols,
					infoCols[getActiveProject().getDefaultColumn()]);
			if (result == null)
				return;

			int col = 0;
			while (col < infoCols.length && !result.equals(infoCols[col])) {
				col++;
			}
			final int idCol = col;
			if (idCol >= infoCols.length) {
				System.err.println("Selected value didn't match any info cols");
				return;
			}
			FileFilter xmlFilter = Utils.createFileFilter("xml", "XML Files");
			final File dest = Utils.chooseFileToSave(xmlFilter, "xml", MetaOmGraph.getMainWindow(), true);
			if(dest == null)
				return;
			new AnimatedSwingWorker("Exporting...", true) {

				@Override
				public Object construct() {
					getActiveProject().exportLists(dest, idCol);
					return null;
				}
			}.start();

			try {
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "Feature Metadata");

				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("UniqueID Column", (String)result);
				dataMap.put("Export File Name", dest.getAbsolutePath());

				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");

				ActionProperties createListAction = new ActionProperties("export-lists",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				createListAction.logActionProperties();
			}
			catch(Exception e1) {

			}
			return;
		}
		if (EXPORT_METNET3_LISTS_COMMAND.equals(e.getActionCommand())) {
			//MetNet3ListExporter.doExport(getActiveProject());
			return;
		}
		if (IMPORT_LISTS_COMMAND.equals(e.getActionCommand())) {
			FileFilter xmlFilter = Utils.createFileFilter("xml", "XML Files");
			final File source = Utils.chooseFileToOpen(xmlFilter, getMainWindow());
			if (source == null)
				return;

			new AnimatedSwingWorker("Importing...", true) {
				@Override
				public Object construct() {
					getActiveProject().importLists(source);
					return null;
				}
			}.start();

			//			try {
			//				//Harsha - reproducibility log
			//				HashMap<String,Object> actionMap = new HashMap<String,Object>();
			//				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
			//
			//				HashMap<String,Object> dataMap = new HashMap<String,Object>();
			//				dataMap.put("Import File Name", source.getAbsolutePath());
			//				
			//				HashMap<String,Object> resultLog = new HashMap<String,Object>();
			//				resultLog.put("result", "OK");
			//
			//				ActionProperties createListAction = new ActionProperties("import-list",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			//				createListAction.logActionProperties(logger);
			//			}
			//			catch(Exception e1) {
			//
			//			}
			return;
		}


		/* urmi: this function is not used
		if (IMPORT_METNET3_LISTS_COMMAND.equals(e.getActionCommand())) {
			final MetNet3ListImportPanel importer;
			try {
				importer = new MetNet3ListImportPanel();
				if (!importer.isLoggedIn())
					return;

				final JDialog dialog = new JDialog(getMainWindow(), "Import from MetNet3", true);
				dialog.getContentPane().add(importer, BorderLayout.CENTER);
				JPanel buttonPanel = new JPanel();
				JButton okButton, cancelButton;
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						final EntityList[] lists = importer.getLists();
						if (lists.length <= 0) {
							dialog.dispose();
							return;
						}
						int count = 0;
						for (EntityList list : lists) {
							count += list.getParts().length;
						}
						final BlockingProgressDialog progress = new BlockingProgressDialog((Frame) null, "Importing",
								"Importing " + lists[0].name, 0, count, true);
						final Object[][] rowNames = getActiveProject().getRowNames();
						new Thread() {
							public void run() {
								for (EntityList list : lists) {
									progress.setMessage("Importing " + list.name);
									EntityListPart[] parts = list.getParts();
									TreeSet<Integer> addUs = new TreeSet<Integer>();
									for (EntityListPart part : parts) {
										boolean found = false;
										for (int row = 0; row < rowNames.length && !found; row++) {
											for (int col = 0; col < rowNames[row].length; col++) {
												String[] splitVals = (rowNames[row][col] + "").split(";");
												for (String val : splitVals) {
													if (val.trim().equalsIgnoreCase(part.name)) {
														addUs.add(row);
														found = true;
													}
												}
											}
										}
										progress.increaseProgress(1);
									}
									int count = 1;
									String newName = list.name;
									while (Utils.isIn(newName, getActiveProject().getGeneListNames())) {
										newName = list.name + "(" + count + ")";
										count++;
									}
									getActiveProject().addGeneList(newName, addUs);
								}
								progress.dispose();
								dialog.dispose();
							}
						}.start();
						progress.setVisible(true);
					}
				});
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}

				});
				buttonPanel.add(okButton);
				buttonPanel.add(cancelButton);
				dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
				dialog.pack();
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			} catch (

			IOException e1) {
				e1.printStackTrace();
			}
			return;
		}*/
		if (MERGE_LISTS_COMMAND.equals(e.getActionCommand())) {
			ListMergePanel.showMergeDialog(getActiveProject());
			return;
		}

		if(HIDE_SHOW_FEATURE_METADATA_COLUMNS.equals(e.getActionCommand())) {
			MetaOmGraph.getActiveTablePanel().getStripedTable().openColumnSelectorDialog("Feature Metadata");
		}


		if(HIDE_SHOW_SAMPLE_METADATA_COLUMNS.equals(e.getActionCommand())) {
			MetaOmGraph.getActiveTablePanel().getMetadataTableDisplay().getStripedTable().openColumnSelectorDialog("Sample Metadata");
		}


		if(HIDE_SHOW_FEATURE_METADATA_COLUMNS.equals(e.getActionCommand())) {
			MetaOmGraph.getActiveTablePanel().getStripedTable().openColumnSelectorDialog("Feature Metadata");
		}


		if(HIDE_SHOW_SAMPLE_METADATA_COLUMNS.equals(e.getActionCommand())) {
			MetaOmGraph.getActiveTablePanel().getMetadataTableDisplay().getStripedTable().openColumnSelectorDialog("Sample Metadata");
		}


		if (GENECARDS_COMMAND.equals(e.getActionCommand())) {
			// open genecards
			try {
				getActiveTable().launchGeneCards();
			} catch (URISyntaxException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (ENSEMBL_COMMAND.equals(e.getActionCommand()) || ENSEMBL_PLANTSCOMMAND.equals(e.getActionCommand())) {
			try {
				if (ENSEMBL_PLANTSCOMMAND.equals(e.getActionCommand())) {
					getActiveTable().launchEnsembl("plants");
				} else {
					getActiveTable().launchEnsembl("all");
				}
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}

		if (REFSEQ_COMMAND.equals(e.getActionCommand())) {
			try {

				getActiveTable().launchRefSeq();

			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}

		if (ATGENESEARCH_COMMAND.equals(e.getActionCommand())) {
			getActiveTable().launchAtGeneSearch();
			return;
		}
		if (TAIR_COMMAND.equals(e.getActionCommand())) {
			getActiveTable().launchTAIR();
			return;
		}
		if (ARAPORT_JBROWSE_COMMAND.equals(e.getActionCommand())) {
			getActiveTable().launchAraportJbrowse();

			return;
		}
		if (ARAPORT_THALEMINE_COMMAND.equals(e.getActionCommand())) {
			getActiveTable().launchAraportThaleMine();
			return;
		}
		if (REPORT_COMMAND.equals(e.getActionCommand())) {
			// getActiveTable().makeReport();
			SearchByExpressionFrame frame = new SearchByExpressionFrame(getActiveProject());
			frame.setSize(900, 700);
			frame.setTitle("Search by expression level");
			MetaOmGraph.getDesktop().add(frame);
			frame.setVisible(true);

			return;
		}


		if (CASCADE_WINDOWS_COMMAND.equals(e.getActionCommand())) {
			JInternalFrame[] frames = desktop.getAllFrames();
			SortableData[] sortOrder = new SortableData[frames.length];
			for (int i = 0; i < sortOrder.length; i++) {
				double size = frames[i].getSize().width * frames[i].getSize().height;
				sortOrder[i] = new SortableData(size, i);
			}
			Arrays.sort(sortOrder);
			for (int i = 0; i < sortOrder.length; i++) {
				frames[sortOrder[i].getIndex()].setLocation(i * 32, i * 32);
			}
			return;
		}
		if (CLOSE_WINDOW_COMMAND.equals(e.getActionCommand())) {
			JInternalFrame frame = desktop.getSelectedFrame();
			frame.putClientProperty("JInternalFrame.frameType", "normal");
			if (frame.isClosable())
				frame.dispose();

			return;
		}
		if (SWITCH_WINDOW_COMMAND.equals(e.getActionCommand())) {
			try {
				int index = Integer.parseInt(((JMenuItem) e.getSource()).getName());

				getDesktop().getAllFrames()[index].toFront();
				getDesktop().setSelectedFrame(getDesktop().getAllFrames()[index]);
				getDesktop().getAllFrames()[index].setSelected(true);
			} catch (

					Exception ex) {
				ex.printStackTrace();
				return;
			}
		}
		if (EXCLUDE_SAMPLES_COMMAND.equals(e.getActionCommand())) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						MetadataFilter frame = new MetadataFilter(
								getActiveProject().getMetadataHybrid().getMetadataCollection());
						FrameModel fm = new FrameModel("Metadata Filter","Metadata Filter",31);
						frame.setModel(fm);
						frame.setVisible(true);
						desktop.add(frame);
						frame.show();
						frame.moveToFront();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			// MetaOmAnalyzer.showExcludeDialog(getActiveProject(), getMainWindow());
			return;
		}

		if ("noTransform".equals(e.getActionCommand())) {
			noneItem.setSelected(true);
			log2Item.setSelected(false);
			logeItem.setSelected(false);
			log10Item.setSelected(false);
			sqrtItem.setSelected(false);
			fixTitle();

			try {
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "All");

				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("Transformation Name", "None");

				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");

				ActionProperties changeTransformationAction = new ActionProperties("change-data-transformation",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				changeTransformationAction.logActionProperties();
			}
			catch(Exception e1) {

			}
			return;
		}

		if ("log2".equals(e.getActionCommand())) {
			noneItem.setSelected(false);
			log2Item.setSelected(true);
			logeItem.setSelected(false);
			log10Item.setSelected(false);
			sqrtItem.setSelected(false);
			fixTitle();

			try {
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "All");

				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("Transformation Name", "log2");

				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");

				ActionProperties changeTransformationAction = new ActionProperties("change-data-transformation",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				changeTransformationAction.logActionProperties();
			}
			catch(Exception e1) {

			}
			return;
		}

		if ("log10".equals(e.getActionCommand())) {
			noneItem.setSelected(false);
			log2Item.setSelected(false);
			logeItem.setSelected(false);
			log10Item.setSelected(true);
			sqrtItem.setSelected(false);
			fixTitle();

			try {
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "All");

				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("Transformation Name", "log10");

				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");

				ActionProperties changeTransformationAction = new ActionProperties("change-data-transformation",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				changeTransformationAction.logActionProperties();
			}
			catch(Exception e1) {

			}
			return;
		}

		if ("loge".equals(e.getActionCommand())) {
			noneItem.setSelected(false);
			log2Item.setSelected(false);
			logeItem.setSelected(true);
			log10Item.setSelected(false);
			sqrtItem.setSelected(false);
			fixTitle();

			try {
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "All");

				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("Transformation Name", "loge");

				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");

				ActionProperties changeTransformationAction = new ActionProperties("change-data-transformation",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				changeTransformationAction.logActionProperties();
			}
			catch(Exception e1) {

			}
			return;
		}

		if ("sqrt".equals(e.getActionCommand())) {
			noneItem.setSelected(false);
			log2Item.setSelected(false);
			logeItem.setSelected(false);
			log10Item.setSelected(false);
			sqrtItem.setSelected(true);
			fixTitle();

			try {
				//Harsha - reproducibility log
				HashMap<String,Object> actionMap = new HashMap<String,Object>();
				actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
				actionMap.put("section", "All");

				HashMap<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("Transformation Name", "sqrt");

				HashMap<String,Object> resultLog = new HashMap<String,Object>();
				resultLog.put("result", "OK");

				ActionProperties changeTransformationAction = new ActionProperties("change-data-transformation",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				changeTransformationAction.logActionProperties();
			}
			catch(Exception e1) {

			}
			return;
		}

	}

	/**
	 * Return an instance of <code>IconTheme</code> class
	 *
	 * @return instance of <code>IconTheme</code> class
	 */
	public static IconTheme getIconTheme() {
		if (iconTheme == null) {
			iconTheme = new TangoIconTheme();
		}
		return iconTheme;
	}

	/**
	 * Sets instance of <code>IconTheme</code> class
	 *
	 * @param newTheme
	 *            instance of <code>IconTheme</code> class
	 *
	 */
	public static void setIconTheme(IconTheme newTheme) {
		iconTheme = newTheme;
	}

	/**
	 * Refreshes the chart
	 */
	public static void refreshCharts() {
		for (int x = 0; x < MetaOmGraph.getDesktop().getComponentCount(); x++)
			if (MetaOmGraph.getDesktop().getComponent(x) instanceof MetaOmChartPanel.MetaOmFrame) {
				((MetaOmChartPanel.MetaOmFrame) MetaOmGraph.getDesktop().getComponent(x)).refreshSorts();
			}

	}

	/*
	 * Returns an instance of MetaOmGraph
	 */
	public static MetaOmGraph getInstance() {
		return myself;
	}

	/**
	 * Returns an instance of Recent project
	 *
	 * @return recent project items as a vector
	 */

	public static Collection<File> getRecentProjects() {
		return recentProjects;
	}

	/**
	 * Added by Mohammed Alabsi
	 *
	 * @param listDisplay
	 * @return
	 */
	private ArrayList<String> getSelectedGenes(JTable listDisplay) {
		int[] selected = listDisplay.getSelectedRows();
		ArrayList<String> genes = new ArrayList<String>();
		for (int x = 0; x < selected.length; x++) {
			boolean found = false;
			for (int y = 0; ((y < listDisplay.getColumnCount()) && (!found)); y++) {
				int type = Utils.getIDType((String) listDisplay.getValueAt(selected[x], y));

				if (type == 1 || type == 2 || type == 3) {
					found = true;
					genes.add((String) listDisplay.getValueAt(selected[x], y));
				}
			}
		}
		return genes;
	}

	/*
	 * Creates icons for menu bar elements
	 *
	 * @see /resource/tango/16x16/actions
	 *
	 */
	private static void setMenuIcons() {
		try {
			openProjectItem.setIcon(
					new ImageIcon(myself.getClass().getResource("/resource/tango/16x16/actions/document-open.png")));
			saveProjectItem.setIcon(
					new ImageIcon(myself.getClass().getResource("/resource/tango/16x16/actions/document-save.png")));
			saveProjectAsItem.setIcon(
					new ImageIcon(myself.getClass().getResource("/resource/tango/16x16/actions/document-save-as.png")));
			quitItem.setIcon(
					new ImageIcon(myself.getClass().getResource("/resource/tango/16x16/actions/process-stop.png")));
			closeProjectItem.setIcon(
					new ImageIcon(myself.getClass().getResource("/resource/tango/16x16/actions/edit-clear.png")));
			loadInfoItem.setIcon(
					new ImageIcon(myself.getClass().getResource("/resource/tango/16x16/status/mail-attachment.png")));
			projectPropertiesItem.setIcon(new ImageIcon(
					myself.getClass().getResource("/resource/tango/16x16/actions/document-properties.png")));
			overviewItem.setIcon(
					new ImageIcon(myself.getClass().getResource("/resource/tango/16x16/apps/help-browser.png")));
			tipsItem.setIcon(new ImageIcon(
					myself.getClass().getResource("/resource/tango/16x16/status/dialog-information.png")));
			plbbutton.setIcon(new ImageIcon(((new ImageIcon(myself.getClass().getResource("/resource/loggingicons/loggingicon2.png")).getImage()).getScaledInstance(14, 14, java.awt.Image.SCALE_SMOOTH))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setup welcome dialog panel
	 */
	public static void showWelcomeDialog() throws InterruptedException {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {
					welcomeDialog = new JDialog(getMainWindow(), "Welcome to MetaOmGraph "+getVersion(), true);
					String OS = getOsName();
					if (OS.indexOf("swin") >= 0 || OS.indexOf("sWin") >= 0) {
						// show new welcome dialog
						WelcomePanelWin10 d1 = new WelcomePanelWin10();
						// JDialog welcomeDialog = new JDialog(getMainWindow(), "Welcome to
						// MetaOmGraph", true);
						welcomeDialog.setSize(600, 500);
						welcomeDialog.setUndecorated(true);
						welcomeDialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
						welcomeDialog.setResizable(false);
						welcomeDialog.setContentPane(d1);
						d1.setVisible(true);
						welcomeDialog.setVisible(true);

					} else {

						// welcomeDialog = new JDialog(getMainWindow(), "Welcome to MetaOmGraph", true);
						welcomeDialog.setName("welcome.php");
						welcomeDialog.getContentPane().add(new WelcomePanel());
						welcomeDialog.setResizable(false);
						welcomeDialog.pack();
						welcomeDialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
						AbstractAction action = new AbstractAction() {

							@Override
							public void actionPerformed(ActionEvent e) {
								ActionEvent e2 = new ActionEvent(welcomeDialog, ActionEvent.ACTION_PERFORMED,
										"welcome.php");
								MetaOmGraph.getHelpListener().actionPerformed(e2);
							}

						};
						welcomeDialog.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
								"help");
						welcomeDialog.getRootPane().getActionMap().put("help", action);
						// System.out.println(welcomeDialog.getSize());
						welcomeDialog.setVisible(true);
					}

				} catch (

						IOException ioe) {
					ioe.printStackTrace();
				}

			}
		});
	}

	/**
	 * Closes the welcome dialog box
	 */
	public static void closeWelcomeDialog() {
		if (welcomeDialog != null) {
			welcomeDialog.dispose();
		}
	}

	/**
	 * Shows About menu item in welcome dialog box
	 */
	public static void showAboutFrame() {
		if ((aboutFrame != null) && (aboutFrame.isVisible()))
			aboutFrame.toFront();
		else {
			aboutFrame = new AboutFrame();
			aboutFrame.setClosable(true);
			aboutFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			aboutFrame.setResizable(true);
			aboutFrame.setIconifiable(false);
			aboutFrame.setMaximizable(false);
			desktop.add(aboutFrame);
			// remove titlebas
			((javax.swing.plaf.basic.BasicInternalFrameUI) aboutFrame.getUI()).setNorthPane(null);
			aboutFrame.setSize(800, 800);
			// position in middle
			Dimension desktopSize = desktop.getSize();
			Dimension aboutFrameSize = aboutFrame.getSize();
			aboutFrame.setLocation((desktopSize.width - aboutFrameSize.width) / 2,
					(desktopSize.height - aboutFrameSize.height) / 2);
			aboutFrame.setVisible(true);

		}
		return;
	}

	public static void showNewAboutFrame() {
		JDialog dialog = new JDialog(getMainWindow(), "About MetaOmGraph", true);
		dialog.add(new AboutFrame4());
		dialog.pack();
		dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Returns an instance of <code>MetaOmApplicationListener</code>
	 */
	private static void doMacStuff() {
		//		new MetaOmApplicationListener();
	}

	/**
	 * Adds internal frame to the main window
	 *
	 * @param panel
	 *            instance of JPanel
	 * @param title
	 *            title of the Internal Frame
	 */
	public static void addInternalFrame(JPanel panel, String title) {
		JInternalFrame f = new JInternalFrame(title, true, true, true, true);
		f.putClientProperty("JInternalFrame.frameType", "normal");
		f.add(panel);
		f.pack();
		getDesktop().add(f);
		f.setVisible(true);
	}

	/**
	 * Returns the state of the button.
	 * 
	 * @return True if the toggle button is selected, false if it's not.
	 */
	public boolean isLogging() {
		return logDataItem.isSelected();
	}

	public boolean isLog2() {
		return log2Item.isSelected();
	}

	public boolean isLog10() {
		return log10Item.isSelected();
	}

	public boolean isSqrt() {
		return sqrtItem.isSelected();
	}

	/**
	 * retun a type of data transform
	 * 
	 * @return
	 */
	public static String getTransform() {
		if (log2Item.isSelected()) {
			return "log2";
		} else if (log10Item.isSelected()) {
			return "log10";
		} else if (logeItem.isSelected()) {
			return "loge";
		} else if (sqrtItem.isSelected()) {
			return "sqrt";
		} else {
			return "NONE";
		}
	}

	public static void setTransform(String val) {
		log2Item.setSelected(false);
		log10Item.setSelected(false);
		logeItem.setSelected(false);
		sqrtItem.setSelected(false);
		noneItem.setSelected(false);

		if (val.equals("log2")) {
			log2Item.setSelected(true);
		} else if (val.equals("log10")) {
			log10Item.setSelected(true);
		} else if (val.equals("loge")) {
			logeItem.setSelected(true);
		} else if (val.equals("sqrt")) {
			sqrtItem.setSelected(true);
		} else {
			noneItem.setSelected(true);
		}

	}

	/**
	 * Sets title to the active project window
	 * 
	 * @return title of the active project
	 */
	public static String fixTitle() {
		if (activeProject == null || !activeProject.isInitialized()) {
			return "MetaOmGraph";
		}
		StringBuilder title = new StringBuilder();
		if (activeProject.isChanged()) {
			title.append("* ");
		}
		title.append("MetaOmGraph - ");
		// String fname=activeProjectFile.getName();
		if (activeProjectFile != null) {
			title.append(activeProjectFile.getName());
		} else {
			title.append("New Project");
		}
		title.append(" (" + activeProject.getDataColumnCount() + " samples");
		int excluded = MetaOmAnalyzer.getExcludeCount();
		int included = activeProject.getDataColumnCount() - excluded;
		if (excluded > 0) {
			title.append(", " + included + " included, " + excluded + " excluded");
		}
		title.append(")");
		// add info about rows
		int totalRows = activeProject.getRowCount();
		title.append("; " + totalRows + " features");

		if (!getInstance().getTransform().equals("NONE")) {
			title.append("; transform data:" + getInstance().getTransform());
		}

		getMainWindow().setTitle(title.toString());

		return title.toString();
	}

	// rewriting wlcome menu commands as functions can be invoked from outside
	public static void startNewFromDelimited() {
		if (!closeProject()) {
			return;
		}

		// invokeLater to fix swing issue in mac
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				NewProjectDialog npd = new NewProjectDialog(getMainWindow());
				npd.toFront();
				// npd.setAlwaysOnTop(true);
				npd.setVisible(true);
				if (!npd.isCancelled()) {
					// urmi call NewProjectWorker with new arguments
					new NewProjectWorker(npd.getSourceFile(), npd.getInfoColumns(), npd.getDelimiter(),
							npd.getRowArray(), npd.getColArray(), npd.getExtendedInfoFile(),
							npd.getIgnoreConsecutiveDelimiters(), npd.getBlankValue(), npd.csvFlag,
							npd.getMetadataDelimiter()).start();
				} else {
					if (welcomeDialog != null)
						welcomeDialog.setVisible(true);
				}
			}
		});

	}

	public static void openAnotherProject() {
		File source = Utils.chooseFileToOpen(new GraphFileFilter(GraphFileFilter.PROJECT), getMainWindow());
		if (source == null)
			return;
		if (activeProject != null)
			if (!closeProject())
				return;
		Utils.setLastDir(source.getParentFile());
		// JOptionPane.showMessageDialog(null, "setlf:"+Utils.getLastDir());
		new OpenProjectWorker(source).start();

		return;
	}


	private static String downloadProjSelectionPanel(File projDirectory, DownloadSampleProject project) {
		String destPath = "";
		URL projURL = null;
		if(project == DownloadSampleProject.Metabolomics) {
			try {
				projURL = new URL("https://metnetweb.gdcb.iastate.edu/MetaOmGraph/RNASeq/MOG_Athaliana_metabolomics.zip");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			destPath = projDirectory.getAbsolutePath();
			destPath += File.separator + "MOG_Athaliana_Metabolomics";
			if(Utils.downloadFile(projURL, destPath + ".zip")) {
				Utils.unZipFile(destPath + ".zip", destPath);
			} else {
				destPath = "";
			}
		} else if (project == DownloadSampleProject.MicroArray) {
			try {
				projURL = new URL("https://metnetweb.gdcb.iastate.edu/MetaOmGraph/RNASeq/MOG_AthalianaMAProj.zip");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			destPath = projDirectory.getAbsolutePath();
			destPath += File.separator + "MOG_Athaliana_MicroArray";
			if(Utils.downloadFile(projURL, destPath + ".zip")) {
				Utils.unZipFile(destPath + ".zip", destPath);
			} else {
				destPath = "";
			}
		} else {
			try {
				projURL = new URL("https://metnetweb.gdcb.iastate.edu/MetaOmGraph/RNASeq/MOG_HumanCancerRNASeqProject.zip");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			destPath = projDirectory.getAbsolutePath();
			destPath += File.separator + "MOG_HumanCancerRNASeq";
			if(Utils.downloadFile(projURL, destPath + ".zip")) {
				Utils.unZipFile(destPath + ".zip", destPath);
			} else {
				destPath = "";
			}
		}
		return destPath;
	}

	private static void downloadAndOpenProject(DownloadSampleProject project) {
		File currDir = Utils.getLastDir();
		File projSelDir = CustomFileSaveDialog.showDirectoryDialog(currDir, "Save sample project to");
		if(projSelDir == null) {
			return;
		}
		String projDir = downloadProjSelectionPanel(projSelDir, project);
		if(projDir.isEmpty()) {
			return;
		}
		File projDirFile = new File(projDir);
		File[] mogFiles = projDirFile.listFiles(new FilenameFilter() { 
			public boolean accept(File dir, String filename)
			{ return filename.endsWith(".mog"); }
		} );
		if(mogFiles.length == 0)
			return;
		Utils.setLastDir(projDirFile);
		new OpenProjectWorker(mogFiles[0]).start();
	}

	public static void openRecentProject(String name) {
		if (activeProject != null)
			if (!closeProject())
				return;

		File source = new File(name);
		if (!source.exists()) {
			JOptionPane.showMessageDialog(getMainWindow(), source.getAbsolutePath() + "\nwas not found.",
					"File not found", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Utils.setLastDir(source.getParentFile());
		new OpenProjectWorker(source).start();

		return;
	}

	/**
	 * @author urmi Function to check MOG updates from metnet
	 * 
	 */
	public static void checkUpdates(boolean showCurrentMessage) {
		//skip check if using beta
		if(getVersion().contains("beta")) {
			return;
		}
		VersionCheck ob = new VersionCheck(getVersion());
		if (!ob.isLatestMOG()) {

			Object[] options = { "Yes, take me to the download.", "Nope." };
			int response = -1;
			try {
				response = JOptionPane.showOptionDialog(null,
						"A newer version of MOG is available for download. We highly recommend using the latest version.\nYour version: "
								+ getVersion() + "\nLatest version: " + ob.getLatestVersionOnline(),

								"New version available!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
								options, options[0]);
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == 0) {
				// open metnet download
				try {
					java.awt.Desktop.getDesktop()
					.browse(new URI("http://metnetweb.gdcb.iastate.edu/MetNet_MetaOmGraph.htm"));
					shutdown();
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			if (showCurrentMessage) {
				JOptionPane.showMessageDialog(null, "You already have the latest version of MOG (" + getVersion() + ")",
						"No updates available", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}


	/**
	 * This method initializes the Playback Dashboard Internal frame
	 */

	public static void createReproducibilityLoggingFrame() {

		ReproducibilityDashboardFrame =  new JInternalFrame("Playback");

		ColorUIResource oldActiveTitleBackground = (ColorUIResource) UIManager.get("InternalFrame.activeTitleBackground");
		ColorUIResource oldInactiveTitleBackground = (ColorUIResource) UIManager.get("InternalFrame.inactiveTitleBackground");
		Font oldFont = UIManager.getFont("InternalFrame.titleFont");

		UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(new Color(240,128,128)));
		UIManager.put("InternalFrame.inactiveTitleBackground", new ColorUIResource(new Color(240,128,128)));
		UIManager.put("InternalFrame.titleFont", new Font("SansSerif", Font.BOLD,12));

		javax.swing.plaf.basic.BasicInternalFrameUI ui = new javax.swing.plaf.basic.BasicInternalFrameUI(ReproducibilityDashboardFrame);

		ReproducibilityDashboardFrame.setUI(ui);


		rdp = new ReproducibilityDashboardPanel(myself);
		ReproducibilityDashboardFrame.add(rdp);
		desktop.add(ReproducibilityDashboardFrame);


		ReproducibilityDashboardFrame.setClosable(false);
		ReproducibilityDashboardFrame.setIconifiable(false);
		ReproducibilityDashboardFrame.setMaximizable(false);
		ReproducibilityDashboardFrame.setResizable(true);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();

		ReproducibilityDashboardFrame.setSize(550, (int)rect.getMaxY()-200);

		if(Utils.isMac()) {
			ReproducibilityDashboardFrame.setLocation(MetaOmGraph.getDesktop().getWidth()-550, MetaOmGraph.getDesktop().getHeight() - 700);
		}
		else {
			ReproducibilityDashboardFrame.setLocation(MetaOmGraph.getDesktop().getWidth()-550, MetaOmGraph.getDesktop().getHeight() - 700);
		}
		ReproducibilityDashboardFrame.show();    


		UIManager.put("InternalFrame.activeTitleBackground", oldActiveTitleBackground);
		UIManager.put("InternalFrame.inactiveTitleBackground", oldInactiveTitleBackground);
		UIManager.put("InternalFrame.titleFont", oldFont);


	}


	/**
	 * This method is used to update the logging file name for each open-project action
	 * It will ensure that each project's action will be written to its own project directory's log folder
	 */
	public static Logger updateLogger(String file_name){
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration configuration = context.getConfiguration();
		//  Layout<? extends Serializable> old_layout = configuration.getAppender(appender_name).getLayout();

		//delete old appender/logger
		if(appender != null && appender.isStarted()) {
			appender.stop();
			configuration.removeLogger("reproducibilityLogger");

		}
		//create new appender/logger
		LoggerConfig loggerConfig = new LoggerConfig("reproducibilityLogger", Level.DEBUG, false);
		appender = FileAppender.createAppender(file_name, "false", "false", "reproducibilityAppender", "true","false", "false", "4000", null, null, "false", null, configuration);
		appender.start();
		configuration.addAppender(appender);
		loggerConfig.addAppender(appender, Level.DEBUG, null);
		configuration.addLogger("reproducibilityLogger", loggerConfig);

		context.updateLoggers();

		Logger l = context.getLogger("reproducibilityLogger");

		return l;
	}


	/**
	 * This method is used to stop logging to the log file
	 */
	public static void stopLogger() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration configuration = context.getConfiguration();

		if(appender != null && appender.isStarted()) {
			appender.stop();
			configuration.removeLogger("reproducibilityLogger");

		}
	}


	/**
	 * This method is used to log the General system properties, project properties and samples
	 */
	public static void logGeneralProperties() {
		try {

			//Harsha - reproducibility log
			HashMap<String,Object> actionMap = new HashMap<String,Object>();
			HashMap<String,Object> dataMap = new HashMap<String,Object>();
			HashMap<String,Object> result = new HashMap<String,Object>();

			actionMap.put("parent",-1);
			actionMap.put("section", "All");

			dataMap.put("Mog Version",VERSION);
			dataMap.put("Java Version",System.getProperty("java.version"));
			dataMap.put("OS Name", System.getProperty("os.name"));
			dataMap.put("CPU",(System.getenv("PROCESSOR_IDENTIFIER")+", architecture: "+System.getenv("PROCESSOR_ARCHITECTURE")+", numProcessors: "+System.getenv("NUMBER_OF_PROCESSORS")));
			dataMap.put("Memory", String.valueOf(Runtime.getRuntime().totalMemory()));
			dataMap.put("Session ID", String.valueOf(Instant.now().toEpochMilli()));

			MetadataHybrid mdhObj = MetaOmGraph.getActiveProject().getMetadataHybrid();
			MetadataCollection mcol = null;
			if(mdhObj != null) {
				mcol = mdhObj.getMetadataCollection();
			}
			if(MetaOmAnalyzer.getExclude() != null) {
				boolean [] excludedSamplesBoolean = MetaOmAnalyzer.getExclude();
				List<Integer> excludedSamplesInteger = new ArrayList<Integer>();

				for(int index=0; index < excludedSamplesBoolean.length; index++) {
					if(excludedSamplesBoolean[index]) {
						excludedSamplesInteger.add(index);
					}
				}
				result.put("Excluded Samples", excludedSamplesInteger.toArray(new Integer[excludedSamplesInteger.size()]));
				dataMap.put("Data Column", mcol.getDatacol());
			}
			else {
				result.put("Excluded Samples", new Integer[0]);
				dataMap.put("Data Column", mcol.getDatacol());
			}
			dataMap.put("Start Timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));

			result.put("result", "OK");

			ActionProperties generalPropertiesAction = new ActionProperties("general-properties",actionMap,dataMap,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			generalPropertiesAction.logActionProperties();

			setCurrentSamplesActionId(generalPropertiesAction.getActionNumber());
		}
		catch(Exception e) {
		}
	}


	/**
	 * This method is used to log the open-project action
	 */
	public static void logOpenProject(String projectName, File source) {

		try {
			HashMap<String,Object> openProjectParameters = new HashMap<String,Object>();
			openProjectParameters.put("parent",-1);

			HashMap<String,Object> dataMap = new HashMap<String,Object>();
			dataMap.put("Project Name", projectName);
			dataMap.put("Mog FilePath",source.getAbsolutePath());
			dataMap.put("Dimensions",String.valueOf(activeProject.getDataColumnCount()));
			dataMap.put("Row Count", String.valueOf(activeProject.getRowCount()));
			dataMap.put("Excluded count",String.valueOf(MetaOmAnalyzer.getExcludeCount()));
			dataMap.put("Logfile Name", getCurrentLogFilePath());

			HashMap<String,Object> result = new HashMap<String,Object>();
			result.put("result", "OK");
			openProjectAction = new ActionProperties("open-project [ "+projectName+" ]",openProjectParameters,dataMap,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			openProjectAction.logActionProperties();
		}
		catch(Exception e) {

		}

	}


	/**
	 * This method is used to log when a new project is opened
	 */
	public static int logNewProject(String dataFileName, String metadataFileName) {

		try {
			HashMap<String,Object> newProjectParameters = new HashMap<String,Object>();
			newProjectParameters.put("parent",-1);

			HashMap<String,Object> dataMap = new HashMap<String,Object>();
			dataMap.put("Data File", dataFileName);
			dataMap.put("Metadata File",metadataFileName);
			dataMap.put("Number of Samples",String.valueOf(activeProject.getDataColumnCount()));
			dataMap.put("Row Count", String.valueOf(activeProject.getRowCount()));
			dataMap.put("Excluded count",String.valueOf(MetaOmAnalyzer.getExcludeCount()));

			HashMap<String,Object> result = new HashMap<String,Object>();
			result.put("result", "OK");
			ActionProperties newProjectAction = new ActionProperties("new-project [ "+dataFileName+" ]",newProjectParameters,dataMap,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			newProjectAction.logActionProperties();

			return newProjectAction.getActionNumber();
		}
		catch(Exception e) {
			return -1;
		}

	}


}
