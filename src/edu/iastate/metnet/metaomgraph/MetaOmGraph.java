package edu.iastate.metnet.metaomgraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileFilter;

import com.l2fprod.common.swing.JTipOfTheDay;

import edu.iastate.metnet.arrayexpress.v2.AEImportDialog;
import edu.iastate.metnet.arrayexpress.v2.AEProjectMaker;
import edu.iastate.metnet.metaomgraph.AnnotationImporter.Annotation;
import edu.iastate.metnet.metaomgraph.MetaOmTips.MetaOmShowTipsChoice;
import edu.iastate.metnet.metaomgraph.RepInfo.RepAveragedData;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.metabolomics.MetabolomicsProjectMaker;
import edu.iastate.metnet.metaomgraph.ui.AboutFrame;
import edu.iastate.metnet.metaomgraph.ui.AboutFrame4;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.ClickableLabel;
import edu.iastate.metnet.metaomgraph.ui.DisplayMetadataEditor;
import edu.iastate.metnet.metaomgraph.ui.ListMergePanel;
import edu.iastate.metnet.metaomgraph.ui.MetNet3ListExporter;
import edu.iastate.metnet.metaomgraph.ui.MetNet3ListImportPanel;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel;
import edu.iastate.metnet.metaomgraph.ui.MetadataEditor;
import edu.iastate.metnet.metaomgraph.ui.Metadataviewer;
import edu.iastate.metnet.metaomgraph.ui.NewProjectDialog;
import edu.iastate.metnet.metaomgraph.ui.ProjectPropertiesPanel;
import edu.iastate.metnet.metaomgraph.ui.ReadMetadata;
import edu.iastate.metnet.metaomgraph.ui.RepInfoPanel;
import edu.iastate.metnet.metaomgraph.ui.WelcomePanel;
import edu.iastate.metnet.metaomgraph.ui.NewProjectDialog.FileBrowseListener;
import edu.iastate.metnet.metaomgraph.utils.DataNormalizer;
import edu.iastate.metnet.metaomgraph.utils.DataNormalizer.MeanResult;
import edu.iastate.metnet.metaomgraph.utils.ExceptionHandler;
import edu.iastate.metnet.metaomgraph.utils.ProjectMerger;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.my.EntityList;
import edu.iastate.metnet.my.EntityListPart;
import edu.iastate.metnet.soft.SOFTFile;
import edu.iastate.metnet.soft.SOFTParser;
import edu.iastate.metnet.soft.SOFTParserOptionPanel;

/**
 * The main class of the MetaOmGraph. Handles all non-project-specific
 * functions.
 *
 *
 */
public class MetaOmGraph implements ActionListener {

	private static final String VERSION = "Development Version";
	private static final String DATE = "October 8th, 2017";

	public static final String NEW_PROJECT_DELIMITED_COMMAND = "New project from a delimited text file";

	public static final String NEW_PROJECT_SOFT_COMMAND = "New project from a SOFT file";

	public static final String NEW_PROJECT_METABOLOMICS_COMMAND = "new project from metabolomics database";

	public static final String NEW_PROJECT_ARRAYEXPRESS_COMMAND = "New project from ArrayExpress";

	public static final String OPEN_COMMAND = "Open a project";

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

	/** The program's main JFrame */
	private static JFrame mainWindow;

	/** The currently open MetaOmProject */
	public static MetaOmProject activeProject;

	/** The .mog file to save to */
	private static File activeProjectFile;

	/** Internal frame which displays a table of the active project's entries */
	private static JInternalFrame projectTableFrame;

	/** The table panel that appears whenever a project is open */
	private static MetaOmTablePanel activeTablePanel;

	/** mainWindow's content pane. */
	private static JDesktopPane desktop;

	/** The main menu bar */
	private static JMenuBar mainMenuBar;

	/** The "File" menu */
	private static JMenu fileMenu;

	/** Items on the File menu */
	private static JMenuItem newProjectItem, newSOFTItem, newMetabolomicsItem, newArrayExpressItem, openProjectItem,
			saveProjectItem, saveProjectAsItem, quitItem, mergeItem;

	private static JMenu newProjectMenu;

	/** The Recent Projects menu item */
	private static JMenu recentProjectsMenu;

	private static Vector<File> recentProjects;

	private static JMenuItem[] recentProjectsMenuItems;

	/** The Project menu */
	private static JMenu projectMenu;

	/** Items on the Project menu */
	private static JMenuItem closeProjectItem, loadInfoItem, projectPropertiesItem, excludeSamplesItem;
	// urmi
	private static JMenuItem openMetadataStructureItem, metadataViewerItem, loadInfoItem2, loadTree;

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

	private static JMenu exportListsMenu;

	private static JMenuItem exportListsFileItem, exportListsMetNet3Item;

	private static JMenuItem mergeListsItem;

	private static JMenuItem findRepsItem;

	private static JMenu windowMenu;

	private static JMenuItem cascadeItem, closeWindowItem;

	private static JCheckBoxMenuItem[] openWindowItems;

	/** The Help menu */
	private static JMenu helpMenu;

	/** Items on the Help menu */
	private static JMenuItem overviewItem, contextItem, tipsItem, aboutItem, contactItem;

	/** An instance of this class created by the main() method */
	private static MetaOmGraph myself;

	/** The icon used for MetaOmGraph windows */
	private static Image myIcon;

	/** An ActionListener that displays the Help frame when invoked */
	private static MetaOmHelpListener helpListener;

	/** A WindowListener that makes frames/dialogs pseudo-modal */
	private static SimpleModalMaker modalMaker;

	/** A frame that displays the properties for the active project */
	private static JInternalFrame propertiesFrame;

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

	// for splashscreen urmi
	static SplashScreen mySplash;

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
		System.setProperty("MOG.version", VERSION);
		System.setProperty("MOG.date", DATE);
		myself = new MetaOmGraph();
		activeProject = null;
		recentProjects = new Vector<File>();

		File homeDir = new File(System.getProperty("user.home"));
		File prefsFile = new File(homeDir, "metaomgraph.prefs");
		if ((prefsFile.exists()) && (prefsFile.canRead())) {
			try {
				FileInputStream fis = new FileInputStream(prefsFile);
				ObjectInputStream in = new ObjectInputStream(fis);
				Utils.setLastDir((File) in.readObject());
				recentProjects = (Vector<File>) in.readObject();
				showTips = (MetaOmShowTipsChoice) in.readObject();
				currentTip = (Integer) in.readObject();
				in.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				System.err.println("Couldn't read prefs file");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		mainWindow = new JFrame("MetaOmGraph");
		ExceptionHandler.getInstance(mainWindow).setUseBuffer(useBuffer);
		// ExceptionHandler.getInstance(mainWindow).setUseBuffer(false);
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance(mainWindow));
		desktop = new JDesktopPane();
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

		try {
			myIcon = ImageIO.read(myself.getClass().getResourceAsStream("/resource/MetaOmicon.png"));
			mainWindow.setIconImage(myIcon);
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
		newArrayExpressItem = new JMenuItem("From ArrayExpress...");
		newArrayExpressItem.setActionCommand(NEW_PROJECT_ARRAYEXPRESS_COMMAND);
		newArrayExpressItem.addActionListener(myself);
		newArrayExpressItem.setMnemonic(KeyEvent.VK_A);
		newArrayExpressItem.setToolTipText("Create a new MetaOm project from the ArrayExpress database");
		newProjectMenu.add(newProjectItem);
		// urmi remove unused menu
		// newProjectMenu.add(newSOFTItem);
		// newProjectMenu.add(newMetabolomicsItem);
		// newProjectMenu.add(newArrayExpressItem);
		openProjectItem = new JMenuItem("Open Project...");
		openProjectItem.setActionCommand(OPEN_COMMAND);
		openProjectItem.addActionListener(myself);
		openProjectItem.setMnemonic(KeyEvent.VK_O);
		openProjectItem.setToolTipText("Open an existing MetaOm project");
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

		quitItem = new JMenuItem("Quit");
		quitItem.setActionCommand(QUIT_COMMAND);
		quitItem.addActionListener(myself);
		quitItem.setMnemonic(KeyEvent.VK_Q);
		quitItem.setToolTipText("Exit this program");
		quitItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		fileMenu.add(newProjectMenu);
		fileMenu.add(openProjectItem);
		fileMenu.add(recentProjectsMenu);
		fileMenu.add(saveProjectItem);
		fileMenu.add(saveProjectAsItem);
		// fileMenu.add(mergeItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);
		mainMenuBar.add(fileMenu);

		projectMenu = new JMenu("Project");
		projectMenu.setMnemonic(KeyEvent.VK_P);
		projectMenu.setEnabled(false);
		closeProjectItem = new JMenuItem("Close project");
		closeProjectItem.setMnemonic(KeyEvent.VK_C);
		closeProjectItem.setActionCommand(CLOSE_PROJECT_COMMAND);
		closeProjectItem.addActionListener(myself);
		closeProjectItem.setToolTipText("Close all windows related to the active project");
		// closeProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
		// Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		projectMenu.add(closeProjectItem);
		loadInfoItem = new JMenuItem("Load Metadata (xml)");
		loadInfoItem.setActionCommand(LOAD_INFO_COMMAND);
		loadInfoItem.addActionListener(myself);
		loadInfoItem.setMnemonic(KeyEvent.VK_L);
		//projectMenu.add(loadInfoItem);

		// urmi

		loadInfoItem2 = new JMenuItem("Load Metadata (csv)");
		loadInfoItem2.setActionCommand(LOAD_INFO_COMMAND_csv);
		loadInfoItem2.addActionListener(myself);
		// loadInfoItem2.setMnemonic(KeyEvent.VK_L);
		projectMenu.add(loadInfoItem2);

		loadTree = new JMenuItem("Load Metadata structure (tree)");
		loadTree.setActionCommand(LOAD_TREE);
		loadTree.addActionListener(myself);
		// loadInfoItem2.setMnemonic(KeyEvent.VK_L);
		// projectMenu.add(loadTree);
		projectMenu.addSeparator();

		openMetadataStructureItem = new JMenuItem("Edit Metadata structure");
		openMetadataStructureItem.setActionCommand("structure");
		openMetadataStructureItem.addActionListener(myself);
		openMetadataStructureItem.setMnemonic(KeyEvent.VK_L);
		//projectMenu.add(openMetadataStructureItem);
		metadataViewerItem = new JMenuItem("View metadata");
		metadataViewerItem.setActionCommand("viewmetadata");
		metadataViewerItem.addActionListener(myself);
		metadataViewerItem.setMnemonic(KeyEvent.VK_L);
		// projectMenu.add(metadataViewerItem);
		//projectMenu.addSeparator();

		projectPropertiesItem = new JMenuItem("Properties");
		projectPropertiesItem.setActionCommand(PROPERTIES_COMMAND);
		projectPropertiesItem.addActionListener(myself);
		projectPropertiesItem.setMnemonic(KeyEvent.VK_P);
		projectPropertiesItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		projectMenu.add(projectPropertiesItem);
		projectMenu.addSeparator();
		excludeSamplesItem = new JMenuItem("Exclude Samples");
		excludeSamplesItem.setActionCommand(EXCLUDE_SAMPLES_COMMAND);
		excludeSamplesItem.addActionListener(myself);
		excludeSamplesItem.setMnemonic(KeyEvent.VK_S);
		projectMenu.add(excludeSamplesItem);

		logDataItem = new JCheckBoxMenuItem("log2 data");
		logDataItem.setMnemonic(KeyEvent.VK_L);
		projectMenu.add(logDataItem);

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
		projectMenu.add(importAnnotationsMenu);

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
		importListsMenu.add(importListsMetNet3Item);
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
		exportListsMenu.add(exportListsMetNet3Item);
		projectMenu.add(exportListsMenu);

		mergeListsItem = new JMenuItem("Merge Lists...");
		mergeListsItem.setMnemonic(KeyEvent.VK_M);
		mergeListsItem.setActionCommand(MERGE_LISTS_COMMAND);
		mergeListsItem.addActionListener(myself);
		mergeListsItem.setToolTipText("Merge existing lists into a new list");
		projectMenu.add(mergeListsItem);

		mainMenuBar.add(projectMenu);

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

			public void menuCanceled(MenuEvent e) {
				// TODO Auto-generated method stub

			}

			public void menuDeselected(MenuEvent e) {
				// TODO Auto-generated method stub

			}

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
		helpMenu.add(contactItem);
		aboutItem = new JMenuItem("About...");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.setActionCommand(ABOUT_COMMAND);
		aboutItem.addActionListener(myself);
		helpMenu.add(aboutItem);
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
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent arg0) {
				shutdown();
			}

			public void windowClosing(WindowEvent arg0) {
				shutdown();
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
		// Once the tip window is closed
		// Welcome dialog is shown
		showWelcomeDialog();
	}

	/*
	 * Sets system variables and calls <code>init</code> method for creating the
	 * menu bar and dialog box
	 *
	 * @param args[] - a string variable which helps to determine the exception
	 * handling in <code>init</code>method
	 */

	/**
	 * @author urmi
	 * 
	 */
	private static void appInit() {
		// splash screen for 3 secs
		for (int i = 1; i <= 3; i++) {

			try {
				Thread.sleep(1000); // wait a second
			} catch (InterruptedException ex) {
				break;
			}
		}
	}

	/**
	 * @author urmi
	 * 
	 */
	private static void initsplashscreen() {
		// the splash screen object is created by the JVM, if it is displaying a splash
		// image

		mySplash = SplashScreen.getSplashScreen();
		// if there are any problems displaying the splash image
		// the call to getSplashScreen will returned null
		if (mySplash != null) {
			// get the size of the image now being displayed
			Dimension ssDim = mySplash.getSize();
			int height = ssDim.height;
			int width = ssDim.width;

		}
	}

	public static void main(String[] args) {

		// for Splash screen: urmi
		initsplashscreen(); // initialize splash screen drawing parameters
		appInit(); // Show splash image for some time or show a message
		if (mySplash != null) // check if we really had a spash screen
		{
			mySplash.close(); // we're done with it
		}

		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.laf.menu.about.name", "MetaOmGraph");

		// System.setProperty("dock:name", "MetaOmGraph");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MetaOmGraph");
		System.setProperty("swing.aatext", "true");

		System.setProperty("sun.java2d.renderer.doChecks", "true");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			System.setProperty("sun.awt.noerasebackground", "true");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				// if (savedQueries==null)
				// savedQueries=new Hashtable();
				// if (savedSorts==null)
				// savedSorts=new Hashtable();
				// out.writeObject(savedQueries);
				// out.writeObject(savedSorts);
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
			if (activeProjectFile == null)
				saveAs();
			else
				activeProject.saveProject(activeProjectFile);
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
		projectTableFrame = new JInternalFrame("Project Data");
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
				}
				// System.out.print("Setting row names... ");
				if (rowNames != null) {
					activeProject.setRowNames(rowNames);
				}
				// System.out.println("Done!");
				// System.out.print("Setting column headers... ");
				if (colNames != null)
					activeProject.setDataColumnHeaders(colNames);
				// System.out.println("Done!");
				// System.out.print("Loading extended info... ");
				if (!extInfoFile.exists())
					extInfoFile = null;

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
				mainWindow.setTitle(
						"MetaOmGraph - New Project (" + getActiveProject().getDataColumnCount() + " samples)");

				projectOpened();
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
				if (activeProject.saveProject(destination))
					mainWindow.setTitle("MetaOmGraph - " + destination.getName() + " ("
							+ getActiveProject().getDataColumnCount() + " samples)");
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
		public Object construct() {
			activeProject = new MetaOmProject(source);
			return null;
		}

		/**
		 * If the project was opened successfully, this opens a table frame and calls
		 * projectOpened(). Otherwise, it removes all traces of the attempted operation.
		 */
		public void finished() {
			if (activeProject.isInitialized()) {
				mainWindow.setTitle("MetaOmGraph - " + source.getName() + " (" + getActiveProject().getDataColumnCount()
						+ " samples)");
				activeProjectFile = source;
				addRecentProject(activeProjectFile);
				projectOpened();
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
			mainWindow.setTitle(
					"MetaOmGraph - " + source.getName() + " (" + getActiveProject().getDataColumnCount() + " samples)");
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
		MetaOmAnalyzer.reset();
		System.gc();
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
	public void actionPerformed(ActionEvent e) {
		// Close the active project
		if (CLOSE_PROJECT_COMMAND.equals(e.getActionCommand())) {
			closeProject();
			showWelcomeDialog();
		}

		// Open a previously saved project
		if (OPEN_COMMAND.equals(e.getActionCommand())) {
			File source = Utils.chooseFileToOpen(new GraphFileFilter(GraphFileFilter.PROJECT), getMainWindow());
			if (source == null)
				return;
			if (activeProject != null)
				if (!closeProject())
					return;
			Utils.setLastDir(source.getParentFile());
			new OpenProjectWorker(source).start();

			return;
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

		// Save the active project
		/**
		 * urmi
		 */
		if ("save2".equals(e.getActionCommand())) {
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
			//s
			// choose file
			JFileChooser fChooser = new JFileChooser(edu.iastate.metnet.metaomgraph.utils.Utils.getLastDir());
			int rVal = fChooser.showOpenDialog(MetaOmGraph.getMainWindow());
			if (rVal == JFileChooser.APPROVE_OPTION) {
				File source = fChooser.getSelectedFile();
				// choose delimiter
				String[] delims = { "Tab", ",", ";", "Space" };
				String metadataDelim = (String) JOptionPane.showInputDialog(null, "Please choose delimiter for the file...", "Please choose delimiter",
						JOptionPane.QUESTION_MESSAGE, null, delims, delims[0]);
				String delim;
				if(metadataDelim.equals("Tab")) {
					delim="\t";
					
				}else if(metadataDelim.equals("Space")) {
					delim=" ";
				}else {
					delim=metadataDelim;
				}

				new AnimatedSwingWorker("Working...", true) {

					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {

									final ReadMetadata readMetadataframe = new ReadMetadata(source.getAbsolutePath(), delim);
									readMetadataframe.setVisible(true);
									readMetadataframe.toFront();
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
				DisplayMetadataEditor obj = new DisplayMetadataEditor();
				obj.setVisible(true);
			} catch (NullPointerException ne) {
				JOptionPane.showMessageDialog(null, "No .csv metadata read. ERROR");

			}
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
			propertiesFrame = new JInternalFrame("Project Properties", true, true, true, true);
			propertiesFrame.putClientProperty("JInternalFrame.frameType", "normal");
			propertiesFrame.getContentPane().add(ppp, BorderLayout.CENTER);
			propertiesFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
			propertiesFrame.pack();
			MetaOmGraph.getDesktop().add(propertiesFrame);
			propertiesFrame.setVisible(true);
			propertiesFrame.setName("projectproperties.php");
			return;
		}

		if (CONTACT_COMMAND.equals(e.getActionCommand())) {
			ExceptionHandler.getInstance(mainWindow).contact();
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

		if (MERGE_COMMAND.equals(e.getActionCommand())) {
			ProjectMerger.showMergeDialog();
			return;
		}

		// Create a new project from a delimited text file

		if (NEW_PROJECT_DELIMITED_COMMAND.equals(e.getActionCommand())) {
			if (!closeProject())
				return;
			NewProjectDialog npd = new NewProjectDialog(getMainWindow());
			npd.toFront();
			//npd.setAlwaysOnTop(true);
			npd.setVisible(true);
			
			if (!npd.isCancelled()) {

				// urmi call NewProjectWorker with new arguments
				new NewProjectWorker(npd.getSourceFile(), npd.getInfoColumns(), npd.getDelimiter(), npd.getRowArray(),
						npd.getColArray(), npd.getExtendedInfoFile(), npd.getIgnoreConsecutiveDelimiters(),
						npd.getBlankValue(), npd.csvFlag, npd.getMetadataDelimiter()).start();
			}
		}

		// Create a new project from a SOFT file
		if (NEW_PROJECT_SOFT_COMMAND.equals(e.getActionCommand())) {
			if (!closeProject())
				return;
			final SOFTParserOptionPanel spop = new SOFTParserOptionPanel();
			JDialog dialog = spop.makeDialog(getMainWindow());
			dialog.setModal(true);
			dialog.setVisible(true);
			if (!spop.isOK())
				return;
			final SOFTFile[] parseUs = spop.getFilesToParse();
			if ((parseUs == null) || (parseUs.length <= 0))
				return;

			// String[] rowNames=spop.getRowIDs();
			// boolean sortSeries=spop.isSortSeriesSelected();
			// final MetaOmProject project;
			SwingWorker worker = new SwingWorker() {

				@Override
				public Object construct() {
					MetaOmProject project = null;
					try {
						project = new SOFTParser().createProjectFromFiles(parseUs, spop.getDestination(),
								spop.getRowIDs());
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					if (project == null)
						return null;

					openProject(project);
					return null;
				}

				@Override
				public void finished() {
					MetaOmGraph.getMainWindow().setEnabled(true);
				}

			};
			// try {
			// project=new
			// SOFTParser().createProjectFromFile(parseUs[0],spop.getDestination(
			// ),spop.getRowIDs());
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
			// if (project==null)
			// return;
			// openProject(project);
			getMainWindow().setEnabled(false);
			worker.start();
			// try {
			// t.join();
			// } catch (InterruptedException e1) {
			// e1.printStackTrace();
			// }
			return;
		}

		// Create a new project from the metabolomics database
		if (NEW_PROJECT_METABOLOMICS_COMMAND.equals(e.getActionCommand())) {
			if (!closeProject())
				return;

			MetabolomicsProjectMaker maker = new MetabolomicsProjectMaker();
			File source = maker.showProjectMakerPanel(getMainWindow());
			if (source == null)
				return;

			new NewProjectWorker(source, 1, '\t', null, null, maker.getMetadataFile(), false, 0.0).start();
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

			// urmi open tree file
			/*
			 * MetadataTreeStructure obj = null; String
			 * fname=source.getAbsolutePath().replaceFirst("[.][^.]+$", "")+".tree";
			 * //JOptionPane.showMessageDialog(null, "reading f: "+fname); File sourceTree =
			 * new File(fname); //read the tree object file FileInputStream fin=null; try {
			 * fin = new FileInputStream(sourceTree.getAbsolutePath()); ObjectInputStream
			 * objectinputstream = new ObjectInputStream(fin); obj = (MetadataTreeStructure)
			 * objectinputstream.readObject(); } catch (Exception e1) {
			 * e1.printStackTrace(); JOptionPane.showMessageDialog(null, "Tree file "
			 * +fname+" not found.\nPlease choose it manually under Project->Load Metadata structure if available"
			 * ); }
			 * 
			 * //wait so that MetaOmGraph.getActiveProject() is not null //bad way to do try
			 * { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e1) { // TODO
			 * Auto-generated catch block e1.printStackTrace(); } //set tree object
			 * if(MetaOmGraph.getActiveProject()==null) { JOptionPane.showMessageDialog(
			 * null,"Please load the tree file manually under Project->Load Metadata structure."
			 * ); } MetaOmGraph.getActiveProject().setTreeStructure(obj);
			 * JOptionPane.showMessageDialog(null,
			 * "Please load the tab-delimited metadata file(.csv or .txt) if the project was created with tab-delimited metadata file.\nPlease choose it manually under Project->Load Metadata (csv)"
			 * );
			 */

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
					System.gc();
				}
			}.start();
			return;
		}
		if (EXPORT_LISTS_COMMAND.equals(e.getActionCommand())) {
			String[] infoCols = getActiveProject().getInfoColumnNames();
			Object result = JOptionPane.showInputDialog(getMainWindow(), "Which column contains unique IDs?",
					"Export lists", JOptionPane.QUESTION_MESSAGE, null, infoCols,
					infoCols[getActiveProject().getDefaultColumn()]);
			// System.out.println(result);
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
			new AnimatedSwingWorker("Exporting...", true) {
				public Object construct() {
					getActiveProject().exportLists(dest, idCol);
					return null;
				}
			}.start();
			return;
		}
		if (EXPORT_METNET3_LISTS_COMMAND.equals(e.getActionCommand())) {
			MetNet3ListExporter.doExport(getActiveProject());
			return;
		}
		if (IMPORT_LISTS_COMMAND.equals(e.getActionCommand())) {
			FileFilter xmlFilter = Utils.createFileFilter("xml", "XML Files");
			final File source = Utils.chooseFileToOpen(xmlFilter, getMainWindow());
			if (source == null)
				return;

			new AnimatedSwingWorker("Importing...", true) {
				public Object construct() {
					getActiveProject().importLists(source);
					return null;
				}
			}.start();
			return;
		}
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
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		if (MERGE_LISTS_COMMAND.equals(e.getActionCommand())) {
			ListMergePanel.showMergeDialog(getActiveProject());
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
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}
		if (EXCLUDE_SAMPLES_COMMAND.equals(e.getActionCommand())) {
			MetaOmAnalyzer.showExcludeDialog(getActiveProject(), getMainWindow());
			return;
		}
		// if (FIND_REPS_COMMAND.equals(e.getActionCommand())) {
		// RepInfo reps = getActiveProject().getReps();
		// if (reps == null) {
		// reps = new RepInfo(getActiveProject());
		// getActiveProject().setReps(reps);
		// }
		// // JPanel panel = reps.getRepPanel();
		// RepInfoPanel panel = new RepInfoPanel(getActiveProject());
		// // JPanel panel = RepFinder.getRepPanel(getActiveProject());
		// if (panel == null) {
		// return;
		// }
		// final JInternalFrame f = new JInternalFrame("Reps", true, true,
		// true, true);
		// f.getContentPane().setLayout(new BorderLayout());
		// f.getContentPane().add(panel, BorderLayout.CENTER);
		//
		// JButton refreshButton = new JButton(new AbstractAction("Revert") {
		//
		// public void actionPerformed(ActionEvent e) {
		// int result = JOptionPane
		// .showConfirmDialog(
		// getMainWindow(),
		// "WARNING: Any changes you have made to the replicate groups will be undone.
		// Continue?",
		// "Revert Changes",
		// JOptionPane.OK_CANCEL_OPTION,
		// JOptionPane.WARNING_MESSAGE);
		// if (result != JOptionPane.OK_OPTION) {
		// return;
		// }
		// getActiveProject().setReps(null);
		// f.dispose();
		// MetaOmGraph.getInstance().actionPerformed(
		// new ActionEvent(this, 0, FIND_REPS_COMMAND));
		// }
		//
		// });
		//
		// JButton outputButton = new JButton(new AbstractAction(
		// "Create Averaged Rep File") {
		//
		// public void actionPerformed(ActionEvent e) {
		// JPanel panel = new JPanel(new GridBagLayout());
		// GridBagConstraints c = new GridBagConstraints();
		// c.gridx = 0;
		// c.gridy = 0;
		// c.weightx = 1.0;
		// c.weighty = .5;
		// c.anchor = GridBagConstraints.LINE_START;
		// c.fill = GridBagConstraints.BOTH;
		// c.gridwidth = 2;
		// final JRadioButton includeAllButton, removeBadButton;
		// includeAllButton = new JRadioButton(
		// "Include all replicates");
		// removeBadButton = new JRadioButton(
		// "Remove replicates with a correlation less than: ");
		// ButtonGroup group = new ButtonGroup();
		// group.add(includeAllButton);
		// group.add(removeBadButton);
		// includeAllButton.setSelected(true);
		// final SpinnerNumberModel spinModel = new SpinnerNumberModel(
		// 0.9, -1.0, 1.0, .01);
		// final JSpinner cutoffSpinner = new JSpinner(spinModel);
		// // cutoffSpinner.setMinimumSize(new Dimension(150,
		// // cutoffSpinner.getPreferredSize().height));
		// cutoffSpinner.setPreferredSize(new Dimension(75,
		// cutoffSpinner.getPreferredSize().height));
		//
		// cutoffSpinner.setEnabled(false);
		// removeBadButton.addActionListener(new ActionListener() {
		//
		// public void actionPerformed(ActionEvent e) {
		// cutoffSpinner.setEnabled(removeBadButton
		// .isSelected());
		// }
		//
		// });
		// panel.add(includeAllButton, c);
		// c.weightx = .5;
		// c.gridwidth = 1;
		// c.gridy = 1;
		// panel.add(removeBadButton, c);
		// c.gridx = 1;
		// panel.add(cutoffSpinner, c);
		// int result = JOptionPane.showConfirmDialog(getMainWindow(),
		// panel, "Filter out bad replicates?",
		// JOptionPane.OK_CANCEL_OPTION,
		// JOptionPane.QUESTION_MESSAGE);
		// if (result != JOptionPane.OK_OPTION) {
		// return;
		// }
		// final File dest = Utils.chooseFileToSave();
		// if (dest == null) {
		// return;
		// }
		// final BlockingProgressDialog progress = new BlockingProgressDialog(
		// getMainWindow(), "Writing",
		// "Filtering out bad replicates...", 0,
		// getActiveProject().getRowCount(), true);
		// new Thread() {
		// @Override
		// public void run() {
		// try {
		// RepInfo reps = getActiveProject().getReps();
		// Collection<Integer> groupIDs = null;
		// if (removeBadButton.isSelected()) {
		// try {
		// groupIDs = reps.getValidGroups(
		// getActiveProject(),
		// Double.parseDouble(cutoffSpinner
		// .getValue() + ""));
		// } catch (IOException ioe) {
		// ioe.printStackTrace();
		// }
		// } else {
		// groupIDs = reps.getValidGroups(
		// getActiveProject(), -2.0);
		// }
		// if (groupIDs == null) {
		// JOptionPane.showMessageDialog(
		// getMainWindow(),
		// "No valid replicate groups found.",
		// "Error", JOptionPane.ERROR_MESSAGE);
		// return;
		// }
		// BufferedWriter out = new BufferedWriter(
		// new FileWriter(dest));
		// out.write(getActiveProject()
		// .getInfoColumnNames()[0]);
		// for (int i = 1; i < getActiveProject()
		// .getInfoColumnCount(); i++) {
		// String thisName = getActiveProject()
		// .getInfoColumnNames()[i];
		// if (thisName == null) {
		// thisName = "";
		// } else if (thisName.contains("html")) {
		// thisName.replaceAll("<html>", "");
		// thisName.replaceAll("</html>", "");
		// thisName.replaceAll("html>", "");
		// }
		// out.write("\t" + thisName);
		// }
		// for (Integer group : groupIDs) {
		// out.write("\t"
		// + reps.getRepGroupName(group));
		// }
		// out.write("\r\n");
		// for (int i = 0; i < getActiveProject()
		// .getRowCount()
		// && !progress.isCanceled(); i++) {
		// out.write(getActiveProject().getRowName(i)[0]
		// + "");
		// for (int j = 1; j < getActiveProject()
		// .getInfoColumnCount(); j++) {
		// out.write("\t"
		// + getActiveProject()
		// .getRowName(i)[j]);
		// }
		// RepAveragedData data = reps
		// .getRepAveragedData(
		// getActiveProject(), i);
		// for (int j = 0; j < data.values.length; j++) {
		// // if (data.values[j]==null) {
		// // out.write("\t");
		// // } else {
		// out.write("\t");
		// if (!Double.isNaN(data.values[j])) {
		// out.write("" + data.values[j]);
		// }
		// // }
		// }
		// out.write("\r\n");
		// progress.increaseProgress(1);
		// }
		// out.close();
		// if (progress.isCanceled()) {
		// if (!dest.delete()) {
		// dest.deleteOnExit();
		// }
		// return;
		// }
		// } catch (IOException ioe) {
		// ioe.printStackTrace();
		// }
		// progress.dispose();
		// }
		// }.start();
		// progress.setVisible(true);
		// final File metadataDest = new File(dest.getAbsolutePath()
		// + ".metadata.xml");
		// new AnimatedSwingWorker("Writing Metadata...", true) {
		//
		// @Override
		// public Object construct() {
		// try {
		// getActiveProject().getReps()
		// .createMetadataFile(getActiveProject(),
		// metadataDest);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return null;
		// }
		// }.start();
		//
		// }
		//
		// });
		//
		// JButton metadataButton = new JButton(new AbstractAction(
		// "Output Metadata") {
		//
		// public void actionPerformed(ActionEvent e) {
		// final File dest = Utils.chooseFileToSave(
		// Utils.createFileFilter("xml", "XML files"), "xml",
		// getMainWindow(), true);
		// if (dest == null) {
		// return;
		// }
		// new AnimatedSwingWorker("Writing Metadata...", true) {
		//
		// @Override
		// public Object construct() {
		// try {
		// getActiveProject().getReps()
		// .createMetadataFile(getActiveProject(),
		// dest);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return null;
		// }
		// }.start();
		// }
		//
		// });
		//
		// JPanel buttonPanel = new JPanel();
		// buttonPanel.add(refreshButton);
		// buttonPanel.add(outputButton);
		// buttonPanel.add(metadataButton);
		// f.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		// // f.pack();
		// f.setSize(800, 600);
		// f.setLocation(getMainWindow().getSize().width / 2 - f.getWidth()
		// / 2, getMainWindow().getSize().height / 2 - f.getHeight()
		// / 2);
		// getDesktop().add(f);
		// f.setVisible(true);
		// }
		/**
		 * added by Mohammed Alabsi - June 6th, 2006 Used to call HiveMind service used
		 * to send data to BirdsEyeView
		 */
		/*
		 * if (e.getActionCommand().equals("BirdsEyeView")) {
		 *
		 * Registry registry = RegistryBuilder.constructDefaultRegistry();
		 *
		 * ExportToBirdsEyeView toBirdsEyeView = (ExportToBirdsEyeView) registry
		 * .getService("integration.ExportToBirdsEyeView", ExportToBirdsEyeView.class);
		 * toBirdsEyeView.FromMetaOmGraph(activeProject);
		 *
		 * return; }
		 */

		/**
		 * Added by Mohammed Alabsi - June 6th, 2006 Used to call HiveMind service used
		 * to send data to PathBinder
		 */
		/*
		 * if (e.getActionCommand().equalsIgnoreCase("PathBinder")) {
		 *
		 * JTable listDisplay = activeTablePanel.getListDisplay(); if
		 * (listDisplay.getSelectedRowCount() != 2) { String title = "Action
		 * Error"; JOptionPane.showMessageDialog(mainWindow, "Please select two genes to
		 * compare using PathBinder", title, JOptionPane.ERROR_MESSAGE); } else {
		 * ArrayList<String> genes = getSelectedGenes(listDisplay);
		 *
		 * if (genes.size() < 1) { JOptionPane.showMessageDialog(mainWindow,
		 * "Unable to find gene IDs in the selected rows", "Error",
		 * JOptionPane.ERROR_MESSAGE); } else { Registry registry = RegistryBuilder
		 * .constructDefaultRegistry();
		 *
		 * CompareInPathBinder compareInPathBinder = (CompareInPathBinder) registry
		 * .getService("integration.CompareInPathBinder", CompareInPathBinder.class);
		 *
		 * compareInPathBinder.findByGeneId((String) genes.get(0), (String)
		 * genes.get(0)); } } }
		 */

		/**
		 * Added by Mohammed Alabsi - Sept 7th, 2006. Used to call HiveMind Serivce to
		 * send selected genes to BirdsEyeView.
		 */
		/*
		 * if (e.getActionCommand().equalsIgnoreCase("BirdsEyeViewSubset")) { JTable
		 * listDisplay = activeTablePanel.getListDisplay(); if
		 * (listDisplay.getSelectedRowCount() < 1) { String title = "Action
		 * Error"; JOptionPane .showMessageDialog( mainWindow, "Please select at least
		 * one entry to send to BirdsEyeView", title, JOptionPane.ERROR_MESSAGE); } else
		 * {
		 *
		 * ArrayList<String> genes = getSelectedGenes(listDisplay);
		 *
		 * if (genes.size() < 1) { JOptionPane.showMessageDialog(mainWindow,
		 * "Unable to find gene IDs in the selected rows", "Error",
		 * JOptionPane.ERROR_MESSAGE); } else { Registry registry = RegistryBuilder
		 * .constructDefaultRegistry();
		 *
		 * ExportToBirdsEyeView toBirdsEyeView = (ExportToBirdsEyeView) registry
		 * .getService("integration.ExportToBirdsEyeView", ExportToBirdsEyeView.class);
		 * toBirdsEyeView.subsetToBirdsEyeView(activeProject, genes,
		 * listDisplay.getSelectedRows(), listDisplay); } } }
		 */
		/**
		 * added by Mohammed Alabsi - June 6th, 2006 Used to call HiveMind Service to
		 * send Gene to PubMed
		 */
		/*
		 * if (e.getActionCommand().equalsIgnoreCase("PubMed")) {
		 *
		 * JTable listDisplay = activeTablePanel.getListDisplay(); if
		 * (listDisplay.getSelectedRowCount() < 1) { String title = "Error";
		 * JOptionPane.showMessageDialog(mainWindow, "Please select a gene to search for
		 * in PubMet", title, JOptionPane.ERROR_MESSAGE); } else {
		 *
		 * ArrayList<String> genes = getSelectedGenes(listDisplay);
		 *
		 * if (genes.get(0) == null) { JOptionPane .showMessageDialog( mainWindow,
		 * "Unable to find any gene IDs in the selected row(s)", "Error",
		 * JOptionPane.ERROR_MESSAGE); } else { Registry registry = RegistryBuilder
		 * .constructDefaultRegistry();
		 *
		 * SearchByPubMed searchByPubMed = (SearchByPubMed) registry
		 * .getService("integration.SearchByPubMed", SearchByPubMed.class);
		 *
		 * searchByPubMed.searchGeneById((String) genes.get(0)); } } }
		 */

		/**
		 * added by Mohammed Alabsi - Sept 28th, 2006 Used to call HiveMind Service to
		 * lookup gene data
		 */
		/*
		 * if (e.getActionCommand().equalsIgnoreCase("atGeneSearch")) { JTable
		 * listDisplay = activeTablePanel.getListDisplay(); if
		 * (listDisplay.getSelectedRowCount() < 1) { String title = "Error"; JOptionPane
		 * .showMessageDialog( mainWindow, "Please select gene(s) to view their pathwyas
		 * in cytoscape", title, JOptionPane.ERROR_MESSAGE); } else { ArrayList<String>
		 * genes = getSelectedGenes(listDisplay);
		 *
		 * if (genes.size() < 1) { JOptionPane.showMessageDialog(mainWindow,
		 * "Unable to find gene IDs in the selected rows", "Error",
		 * JOptionPane.ERROR_MESSAGE); } else { Iterator iterator = genes.iterator();
		 * String search = ""; while (iterator.hasNext()) { search += iterator.next();
		 * if (iterator.hasNext()) { search += ","; } }
		 *
		 * Registry registry = RegistryBuilder .constructDefaultRegistry(); Browser
		 * browser = (Browser) registry.getService( "integration.Browser",
		 * Browser.class); browser.geneSearch(search); } } }
		 */
		/**
		 * added by Mohammed Alabsi - July 22th, 2006 Used to call HiveMind Service to
		 * view gene pathway in cytoscape
		 */
		/*
		 * if (e.getActionCommand().equalsIgnoreCase("cytoscape")) {
		 *
		 * JTable listDisplay = activeTablePanel.getListDisplay(); if
		 * (listDisplay.getSelectedRowCount() < 1) { String title = "Error"; JOptionPane
		 * .showMessageDialog( mainWindow, "Please select gene(s) to view their pathwyas
		 * in cytoscape", title, JOptionPane.ERROR_MESSAGE); } else {
		 *
		 * ArrayList<String> genes = getSelectedGenes(listDisplay); if (genes.size() <
		 * 1) { JOptionPane.showMessageDialog(mainWindow,
		 * "Unable to find gene IDs in the selected rows", "Error",
		 * JOptionPane.ERROR_MESSAGE); } else { Registry registry = RegistryBuilder
		 * .constructDefaultRegistry(); RequestGeneData geneDataService =
		 * (RequestGeneData) registry .getService("integration.RequestGeneData",
		 * RequestGeneData.class);
		 *
		 * ArrayList<Gene> geneList = new ArrayList<Gene>(); ArrayList<GobiNode>
		 * gobiNodeList = new ArrayList<GobiNode>(); for (int i = 0; i < genes.size();
		 * i++) { geneList.add(geneDataService.getGeneByID( (String)
		 * genes.get(i)).get(0)); gobiNodeList.add(new GobiNodeImpl(geneList.get(i)
		 * .getLocusid(), "gene")); }
		 */
		/*
		 * getting all Genes in List
		 */
		/*
		 * ArrayList<Gene> allGenes = new ArrayList<Gene>(); ArrayList<String>
		 * allGenesLocusID = new ArrayList<String>(); Gene gene = null; int rows =
		 * listDisplay.getRowCount(); for (int x = 0; x < rows; x++) { for (int y = 0; y
		 * < listDisplay.getColumnCount(); y++) { int type = Utils.getIDType((String)
		 * listDisplay .getValueAt(x, y));
		 *
		 * if (type == 1) {
		 *
		 * gene = geneDataService.getGeneByID( (String) listDisplay.getValueAt(x, y))
		 * .get(0); allGenes.add(gene); allGenesLocusID.add(gene.getLocusid()); } else
		 * if (type == 2) {
		 *
		 * gene = geneDataService.getGeneByID( (String) listDisplay.getValueAt(x, y))
		 * .get(0); allGenes.add(gene); allGenesLocusID.add(gene.getLocusid()); } else
		 * if (type == 3) {
		 *
		 * gene = geneDataService.getGeneByID( (String) listDisplay.getValueAt(x, y))
		 * .get(0); allGenes.add(gene); allGenesLocusID.add(gene.getLocusid()); } } } //
		 * /////////////////////////
		 *
		 * GraphExporter graphExporter = GraphExporterImpl .getInstance(); Map
		 * pathwayMap = graphExporter .getPathwayMapByEntityList(gobiNodeList); if
		 * (pathwayMap.size() < 1) { JOptionPane .showMessageDialog( mainWindow, "Genes
		 * Selected do not have pathways. Please refine your selection", "Error",
		 * JOptionPane.ERROR_MESSAGE); } else {
		 * CytoscapeHMService.setSelectedRows(listDisplay .getSelectedRows());
		 * PathwaySelectionFrame frame = PathwaySelectionFrame .getInstance();
		 * frame.init(pathwayMap, gobiNodeList, allGenesLocusID); } } } }
		 */
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setup welcome dialog panel
	 */
	public static void showWelcomeDialog() {
		try {
			welcomeDialog = new JDialog(getMainWindow(), "Welcome to MetaOmGraph", true);
			welcomeDialog.setName("welcome.php");
			welcomeDialog.getContentPane().add(new WelcomePanel());
			welcomeDialog.setResizable(false);
			welcomeDialog.pack();
			welcomeDialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
			AbstractAction action = new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					ActionEvent e2 = new ActionEvent(welcomeDialog, ActionEvent.ACTION_PERFORMED, "welcome.php");
					MetaOmGraph.getHelpListener().actionPerformed(e2);
				}

			};
			welcomeDialog.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");
			welcomeDialog.getRootPane().getActionMap().put("help", action);
			// System.out.println(welcomeDialog.getSize());
			welcomeDialog.setVisible(true);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
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
			aboutFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
			aboutFrame.setResizable(true);
			aboutFrame.setIconifiable(false);
			aboutFrame.setMaximizable(false);
			desktop.add(aboutFrame);
			aboutFrame.setVisible(true);
			// new Thread() {
			// public void run() {
			// // Make the window big, display it, then return to
			// // its original size. This allocates a bit more
			// // memory (~5kb) to the animation, which makes it
			// // run more smoothly.
			// Dimension origSize = aboutFrame.getSize();
			// aboutFrame.setVisible(true);
			// aboutFrame.setSize(MetaOmGraph.getMainWindow().getSize());
			// aboutFrame.setSize(origSize);
			// }
			// }.start();
		}
		return;
	}

	public static void showNewAboutFrame() {
		JDialog dialog = new JDialog(getMainWindow(), "About MetaOmGraph", true);
		dialog.add(new AboutFrame4());
		dialog.pack();
		dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Returns an instance of <code>MetaOmApplicationListener</code>
	 */
	private static void doMacStuff() {
		new MetaOmApplicationListener();
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
			title.append("*");
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
		if (excluded > 0) {
			title.append(", " + excluded + " excluded");
		}
		title.append(")");
		getMainWindow().setTitle(title.toString());

		return title.toString();
	}
}