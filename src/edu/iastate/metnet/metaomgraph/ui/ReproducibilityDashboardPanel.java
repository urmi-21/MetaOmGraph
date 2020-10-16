package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.playback.LoggingTreeNode;
import edu.iastate.metnet.metaomgraph.playback.PlaybackAction;
import edu.iastate.metnet.metaomgraph.playback.PlaybackTabData;

/**
 * 
 * @author Harsha
 *
 * <br/>
 * This is the UI class for the Dashboard panel showing the real-time logging actions and allows re-play functionality 
 * for certain actions.
 * <br/>
 * <h2>Description</h2>
 * <p>
 * In this class, we construct the Reproducibility Dashboard Panel, the Frame, which pops up when the "History" button on the
 * top right corner of the MOG is clicked. Below is the overview of the basic parts of this panel and their functionalities:
 * </p>
 * <br/>
 * <p>
 * <b>1. Panel containing radio buttons 'on','off' and 'permanently switched off' :</b> This panel has the three radio buttons, which 
 * allow us to either keep the logging happening (on), or stop it for the current session (off), or else switch it off permanently
 * (permanently switched off) so that no log file is created for the MOG sessions. The 'on' and 'off' buttons work by disabling the
 * appender of Log4J2, so that even if actions occur, the appender will not write anything to the log. However, the 'permanently
 * switched off' operation will store a value in the .prefs file after the current session is closed, and load it back from the file
 * when a new session is opened.
 * <br/>
 * <b>2. Open previous session button :</b> This button opens up the file chooser dialog wherein, users have to choose a previously 
 * written log file. Once the file is chosen, GSON parser is used to parse the JSON formatted file and create a list of 
 * ActionProperties objects. These objects are populated in a separate tab having a play tree and the table.
 * <br/>
 * <b>3. Play button :</b> The play button is used to execute the selected actions in the play tree. The user can select multiple 
 * actions and play them all together. For this release, the play functionality has been extended to charts present in the "plot" 
 * section. The actions that can be played will be displayed in red color in the play tree.
 * <br/>
 * <b>4. Favorite button :</b>  The favorite button, when pressed, will mark the selected actions in the play tree with a golden star. 
 * This would indicate that the particular actions are important. 
 * <br/>
 * <b>5. Log tabs : </b> Below the three buttons, we have a tabbedPane (ClosableTabbedPane tabbedPane) which will initially consist of
 * a non-closable tab for the current session's logging which gets refreshed in real-time while we perform actions in MOG. In case the
 * user opens a previous log session, a new tab would be created and populated with the historically logged actions. Each tab has the
 * same design and format, i.e, a play tree on the left split pane and a JTable which shows the details of the clicked actions on the
 * right pane.
 * <br/>
 * <b>6. Play Tree : </b> This is a tree structure of all the actions that were taken in the particular session. For each session (be
 * it current or historical), there is a play tree on the left split pane of the respective session's tab. Each action element in the
 * log has a property called "parent" in its actionParameters. This will help us create a hierarchical structure of the play tree. The
 * idea is that the action A which are dependent on another action B is added as the child of the parent B. Hence, A's "parent" 
 * property would have B's action number. (For eg: All the actions performed on a project are dependent on "open-project" action) The 
 * task of identifying and writing the parent of an action happens during the action creation. It is the duty of the programmer to 
 * identify the parent action number and add it to the child's "parent" property. In this class however, we are concerned on how to 
 * populate the actions in a hierarchical fashion. This is achieved by reading the "parent" property of each action and adding it as a
 * leaf node of the action number given by "parent". Since the log file is written in a sequential order of actions, a parent action 
 * always preceeds a child action, making things easier.
 * <br/>
 * <b>7. Action Display Table : </b> On the right hand side of each play tree, there is a table having two columns, "Property" and 
 * "Value".This table displays all the key value pairs present in the dataParameters section of the selected action. Since all the log 
 * information is already loaded into a variable having a list of action, on click of any action on the play tree, we just populate the
 * table with the dataParameters of that particular action. We do not read the file for every action selection on the play tree.
 * <br/>
 * <b>8. Samples Table : </b> Below the log tabs section, we have the section where the samples used for the current action are logged.
 * The reason for going with a separate table for the included and excluded samples is that the samples size is usually very large
 * (about 7000+ samples), and logging them to the Action Display table would overwhelm the table which has other important properties.
 * Also, in the future, we can extend the functionalities of this Samples table to have interesting features like selection of samples
 * on the tree by selecting rows etc. 
 * <br/>
 * <b>9. Comments : </b> For each action element, the users can provide their comments in the Action Display Table's "Comment" section.
 * For historical logs, the comments are autosaved to the respective log file at the same time.  But, for the current session, the
 * comments and the favorites are saved once we close the current MOG session ( Quit MOG or Open a new project/Create new project)
 * 
 * 
 * </p>
 * <br/>
 * 
 */
public class ReproducibilityDashboardPanel extends JPanel {



	/* Constants */
	private static final String FOLDER_ICON_PATH = "/resource/loggingicons/tinyfolder.png";
	private static final String PLAY_ICON_PATH = "/resource/loggingicons/tinyplay.png";
	private static final String FAVORITE_ICON_PATH = "/resource/loggingicons/smallorangestar.png";
	private static final String CHART_ICON_PATH = "/resource/loggingicons/chart.png";
	private static final String GENERAL_PROPERTIES_COMMAND = "general-properties";
	private static final String SAMPLE_ACTION_PROPERTY = "Sample Action";
	private static final String DATA_COLUMN_PROPERTY = "Data Column";
	private static final String INCLUDED_SAMPLES_PROPERTY = "Included Samples";
	private static final String EXCLUDED_SAMPLES_PROPERTY = "Excluded Samples";
	private static final String SELECTED_FEATURES_PROPERTY = "Selected Features";
	private static final String FAVORITE_PROPERTY = "favorite";
	private static final String PARENT_PROPERTY = "parent";
	private static final String PLAYABLE_PROPERTY = "Playable";


	/* Session specific variables */
	private static final Logger logger = MetaOmGraph.logger;
	private MetaOmGraph project;
	private HashMap<Integer, DefaultMutableTreeNode> treeStructure;
	private HashMap<Integer, PlaybackTabData> allTabsInfo;
	private PlaybackAction playbackAction;
	private int currentSessionActionNumber;

	/* UI elements */
	private JPanel panel;
	private JButton openPreviousSessionButton;
	private JButton playButton;
	private JSeparator separator_1;
	private JPanel loggingChoicePanel;
	private JLabel loggingLabel;
	private JRadioButton rdbtnOn;
	private JRadioButton rdbtnOff;
	private JRadioButton rdbtnPermanentlySwitchedOff;
	private JTree playTree;
	private JTable table;
	private ClosableTabbedPane tabbedPane;
	private ClosableTabbedPane samplesPane;
	private JTable includedSamplesTable;
	private JTable excludedSamplesTable;
	private JButton addToFavoritesButton;



	/** <h3>Dashboard Constructor</h3>
	 * <p>
	 * The Reproducibility Dashboard Panel constructor takes the current MetaOmGraph instance as a parameter. It initializes all the
	 * required UI components and global variables like:
	 * <br/>
	 * <b>treeStructure</b> - A variable of type HashMap(Integer, DefaultMutableTreeNode) and stores each node of the current session's
	 * play tree. The key attribute is an integer storing the node number of the tree, and the value is a DefaultMutableTreeNode object that
	 * contains the display name, command and actionNumber of the particular action
	 * <br/>
	 * <b>allTabsInfo</b> - A variable of type HashMap(Integer,PlaybackTabData), this variable contains key value pairs of tab number and 
	 * the related PlaybackTabData object for each of the opened tabs (including the current session tab). PlaybackTabData objects contains the
	 * complete set of details required to populate a tab, including the Play tree, Action Data Table, Sample tables objects, tree structure
	 * object, and an arraylist of the action objects. Hence, we can get all the details of all tabs by using this global variable.
	 * <br/>
	 * <b>playbackAction</b> - It is an object of PlaybackAction class. The PlaybackAction class contains all the functionality required to
	 * play an action element. This global variable instance will allow us to call the play functionality anywhere in the program.
	 * <br/>
	 * <b>includedSamplesTable, excludedSamples Table</b> - These are the two JTable objects that represent the Sample Tables. They are
	 * initialized in the constructor ( with alternate colored theme ) and populated only when an action is clicked upon in the play tree.
	 * <br/>
	 * <b>samplesPane</b> - The ClosableTabbedPane for the Samples Table
	 * <br/>
	 * <b>currentSessionActionNumber</b> - When a user opens the Reproducibility Dashboard Panel for the first time, the play tree must be
	 * updated with all the actions that have already happened in the current session. This variable is loaded from the current session's log
	 * file, and used to populate the tree. This is necessary because sometimes, the action number in the log file may not begin with a 0.
	 * <br/>
	 * <b>rdbtnOn, rdbtnOff and rdbtnPermanentlySwitchedOff </b>- This constructor initializes all three radio buttons, and also provides the
	 * ActionPerformed method for the functionality when a radio button selection action is performed.
	 * <br/>
	 * The constructor also provides functionality for the click of playButton, openPreviousSessionButton and addToFavoritesButton.
	 * 
	 * 
	 * */
	public ReproducibilityDashboardPanel(MetaOmGraph myself) {


		project = myself;

		treeStructure = new HashMap<Integer, DefaultMutableTreeNode>();
		allTabsInfo = new HashMap<Integer, PlaybackTabData>();
		playbackAction = new PlaybackAction();
		includedSamplesTable = new JTable(){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component returnComp = super.prepareRenderer(renderer, row, column);
				Color alternateColor = new Color(252,242,206);
				Color whiteColor = Color.WHITE;
				if (!returnComp.getBackground().equals(getSelectionBackground())){
					Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
					returnComp .setBackground(bg);
					bg = null;
				}
				return returnComp;
			}
		};
		excludedSamplesTable = new JTable(){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component returnComp = super.prepareRenderer(renderer, row, column);
				Color alternateColor = new Color(252,242,206);
				Color whiteColor = Color.WHITE;
				if (!returnComp.getBackground().equals(getSelectionBackground())){
					Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
					returnComp .setBackground(bg);
					bg = null;
				}
				return returnComp;
			}
		};
		samplesPane = new ClosableTabbedPane();
		currentSessionActionNumber = 0;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 280, 261, 0 };
		gridBagLayout.rowHeights = new int[] { 47, 20, 0, 40, 3, 3, 250, 150, 2, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		loggingChoicePanel = new JPanel();
		loggingChoicePanel.setBackground(SystemColor.control);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(loggingChoicePanel, gbc_panel_1);

		loggingLabel = new JLabel("logging : ");
		loggingChoicePanel.add(loggingLabel);

		ButtonGroup G = new ButtonGroup();

		rdbtnOn = new JRadioButton("on");
		rdbtnOn.setSelected(true);
		loggingChoicePanel.add(rdbtnOn);

		rdbtnOff = new JRadioButton("pause");
		loggingChoicePanel.add(rdbtnOff);

		rdbtnPermanentlySwitchedOff = new JRadioButton("permanently switched off");
		//		loggingChoicePanel.add(rdbtnPermanentlySwitchedOff);

		rdbtnOn.setSelected(true);
		
		rdbtnOn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				MetaOmGraph.getReproducibilityLogMenu().setForeground(Color.BLUE);
				MetaOmGraph.setLoggingRequired(true);
				MetaOmGraph.setPermanentLogging(true);
				try {

					if (logger != null) {
						LoggerContext context = (LoggerContext) LogManager.getContext(false);
						Configuration configuration = context.getConfiguration();
						LoggerConfig loggerConfig = configuration.getLoggerConfig("reproducibilityLogger");
						loggerConfig.setLevel(Level.DEBUG);
						context.updateLoggers();
					}

				} catch (Exception e3) {

				}

			}
		});

		rdbtnOff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				MetaOmGraph.getReproducibilityLogMenu().setForeground(Color.BLACK);
				MetaOmGraph.setLoggingRequired(false);
				MetaOmGraph.setPermanentLogging(true);
				try {
					LoggerContext context = (LoggerContext) LogManager.getContext(false);
					Configuration configuration = context.getConfiguration();
					LoggerConfig loggerConfig = configuration.getLoggerConfig("reproducibilityLogger");
					loggerConfig.setLevel(Level.OFF);
					context.updateLoggers();
				} catch (Exception e3) {

				}

			}
		});

		rdbtnPermanentlySwitchedOff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MetaOmGraph.getReproducibilityLogMenu().setForeground(Color.BLACK);
				MetaOmGraph.setLoggingRequired(false);
				MetaOmGraph.setPermanentLogging(false);
				try {
					LoggerContext context = (LoggerContext) LogManager.getContext(false);
					Configuration configuration = context.getConfiguration();
					LoggerConfig loggerConfig = configuration.getLoggerConfig("reproducibilityLogger");
					loggerConfig.setLevel(Level.OFF);
					context.updateLoggers();
				} catch (Exception e3) {

				}
			}
		});

		G.add(rdbtnOn);
		G.add(rdbtnOff);
		//G.add(rdbtnPermanentlySwitchedOff);

		separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 2;
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 1;
		add(separator_1, gbc_separator_1);

		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.CENTER);
		panel.setBackground(SystemColor.window);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.anchor = GridBagConstraints.WEST;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		add(panel, gbc_panel);

		openPreviousSessionButton = new JButton("open previous session");
		openPreviousSessionButton.setToolTipText("Open a previous session log file");
		openPreviousSessionButton.setIcon(new ImageIcon(project.getClass().getResource(FOLDER_ICON_PATH)));
		openPreviousSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("open previous session log");

				File projectSource  = MetaOmGraph.getActiveProject().getSourceFile();
				String logPath = FilenameUtils.getFullPathNoEndSeparator(projectSource.getAbsolutePath())+File.separator+"moglog";

				File projectLogsDir = new File(logPath);

				jfc.setCurrentDirectory(projectLogsDir);
				int retValue = jfc.showOpenDialog(MetaOmGraph.getMainWindow());

				/*Check whether the file being opened has valid dimensions (features size and
				Samples size), and warn the user if it doesnt have the same dimensions as the
				currently opened project */
				
				if (retValue == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();

					int[] dimensions = getDimensionsFromFile(file);

					if(dimensions != null) {

						if(dimensions[0] == MetaOmGraph.getActiveProject().getDataColumnCount() && dimensions[1] == MetaOmGraph.getActiveProject().getRowCount()) {

							JTree sessionTree = new JTree();
							JTable sessionTable = new JTable();

							HashMap<Integer, DefaultMutableTreeNode> treeStruct = new HashMap<Integer, DefaultMutableTreeNode>();

							int tabNo = createNewTabAndPopulate(sessionTree, sessionTable, file.getName(), true,
									file.getAbsolutePath());
							readLogAndPopulateTree(file, sessionTree, tabNo, treeStruct);
							tabbedPane.setSelectedIndex(tabNo);

						}
						else {

							int result = JOptionPane.showConfirmDialog((Component) null, "The log file which you are trying to open does not have the same number of features and samples ( Features: "+dimensions[0]+" , Samples: "+dimensions[1]+" ) as the currently opened project ( Features: "+MetaOmGraph.getActiveProject().getDataColumnCount()+" , Samples: "+MetaOmGraph.getActiveProject().getRowCount()+" ). This may cause the play feature to not work properly. Do you still want to proceed?",
									"Warning", JOptionPane.OK_CANCEL_OPTION);

							if(result==0) {

								JTree sessionTree = new JTree();
								JTable sessionTable = new JTable();

								HashMap<Integer, DefaultMutableTreeNode> treeStruct = new HashMap<Integer, DefaultMutableTreeNode>();

								int tabNo = createNewTabAndPopulate(sessionTree, sessionTable, file.getName(), true,
										file.getAbsolutePath());
								readLogAndPopulateTree(file, sessionTree, tabNo, treeStruct);
								tabbedPane.setSelectedIndex(tabNo);
							}
							else {

							}
						}

					}
					else {

						JOptionPane.showMessageDialog((Component) null, "The log file seems to be malformed. Please contact the MOG Support team.");

					}

				}
			}
		});
		panel.add(openPreviousSessionButton);

		playButton = new JButton("play");
		playButton.setToolTipText("Select an action from the tree and play it");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int tabNo = tabbedPane.getSelectedIndex();
				JTree selectedTree = allTabsInfo.get(tabNo).getTabTree();
				TreePath[] allPaths = selectedTree.getSelectionPaths();
				playbackAction.playActions(tabNo, selectedTree, allPaths, allTabsInfo);

			}
		});
		playButton.setIcon(new ImageIcon(project.getClass().getResource(PLAY_ICON_PATH)));
		panel.add(playButton);

		addToFavoritesButton = new JButton();
		addToFavoritesButton
		.setIcon(new ImageIcon(project.getClass().getResource(FAVORITE_ICON_PATH)));
		addToFavoritesButton.setMargin(new Insets(2, 5, 2, 5));
		addToFavoritesButton.setToolTipText("Add to Favorites");

		addToFavoritesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int tabNo = tabbedPane.getSelectedIndex();
				JTree selectedTree = allTabsInfo.get(tabNo).getTabTree();
				DefaultTreeModel model = (DefaultTreeModel) selectedTree.getModel();
				TreePath[] allPaths = selectedTree.getSelectionPaths();


				markActionsAsFavorite(tabNo, selectedTree, model, allPaths);
			}
		});
		panel.add(addToFavoritesButton);

		playTree = new JTree();
		table = new JTable();

		int tabNo = createNewTabAndPopulate(playTree, table, "Current Session", false, getCurrentLoggerFileName());
		File currentLog;
		if (getCurrentLoggerFileName() == "") {
			currentLog = null;
		} else {
			currentLog = new File(getCurrentLoggerFileName());
		}

		readLogAndPopulateTree(currentLog, playTree, tabNo, treeStructure);
		currentSessionActionNumber = allTabsInfo.get(0).getActionObjects().size() - 1;

		GridBagConstraints gbc_samplestabbedPane = new GridBagConstraints();
		gbc_samplestabbedPane.gridwidth = 2;
		gbc_samplestabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_samplestabbedPane.fill = GridBagConstraints.BOTH;
		gbc_samplestabbedPane.gridx = 0;
		gbc_samplestabbedPane.gridy = 7;
		add(samplesPane, gbc_samplestabbedPane);


		JScrollPane samplesPanel = new JScrollPane(includedSamplesTable);
		samplesPane.addNonClosableTab(INCLUDED_SAMPLES_PROPERTY, null, samplesPanel, null);
		JScrollPane samplesPanel2 = new JScrollPane(excludedSamplesTable);
		samplesPane.addNonClosableTab(EXCLUDED_SAMPLES_PROPERTY, null, samplesPanel2, null);


	}


	/**
	 * <p>
	 * This method adds an action object to the play tree (JTree) of a given tab in a hierarchical manner
	 * </p>
	 * <p>
	 * While adding the action object to the play tree, the method checks whether it's Playable property is set to true or not. If yes, then
	 * the color of the node will be written in red color. If the action has Selected Features property, then the first element in the list of
	 * Selected Features is written in square brackets []. And if the action is also a favorite action, a star is appended at the end of the 
	 * node text.
	 * </p>
	 * <p>
	 * Once the node is inserted to the tree, the tree is reloaded and expanded.
	 * </p>
	 */
	public void addActionToLogTree(ActionProperties action, int actionNumber, JTree tree,
			HashMap<Integer, DefaultMutableTreeNode> treeStruct) {

		try {
			DefaultTreeModel dtm = (DefaultTreeModel) tree.getModel();
			String actionCommandString = action.getActionCommand();

			if(action.getOtherParameters().get("Playable") != null) {
				actionCommandString = "<font color=red>"+(String) action.getActionCommand()+"</font>";
			}

			if (!action.getActionCommand().equalsIgnoreCase(GENERAL_PROPERTIES_COMMAND)) {

				DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

				DefaultMutableTreeNode newNode = null;
				try {
					if(action.getOtherParameters() != null ) {
						if (action.getOtherParameters().get(FAVORITE_PROPERTY)!= null && action.getOtherParameters().get(FAVORITE_PROPERTY).equals("true")) {
							if (action.getDataParameters().get(SELECTED_FEATURES_PROPERTY) != null) {
								String[] features = getSelectedFeaturesFromLog(action.getDataParameters().get(SELECTED_FEATURES_PROPERTY));
								newNode = new DefaultMutableTreeNode(new LoggingTreeNode("<html><p>"
										+ actionCommandString + " ["
										+ (String) features[0] + "]"
										+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
										action.getActionCommand(), actionNumber));
							} else {
								newNode = new DefaultMutableTreeNode(new LoggingTreeNode("<html><p>"
										+ actionCommandString
										+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
										action.getActionCommand(), actionNumber));
							}
						} else {
							if (action.getDataParameters().get(SELECTED_FEATURES_PROPERTY) != null) {
								String[] features = getSelectedFeaturesFromLog(action.getDataParameters().get(SELECTED_FEATURES_PROPERTY));
								newNode = new DefaultMutableTreeNode(
										new LoggingTreeNode(
												"<html><p>"+actionCommandString + " ["
														+ features[0] + "]</p></html>",
														action.getActionCommand(), actionNumber));
							} else {
								newNode = new DefaultMutableTreeNode(new LoggingTreeNode("<html><p>"+actionCommandString+"</p></html>",
										action.getActionCommand(), actionNumber));
							}
						}
					}
				} catch (Exception e) {

				}

				Integer parent = (int) Double.parseDouble(action.getActionParameters().get(PARENT_PROPERTY).toString());

				if (parent == -1) {
					dtm.insertNodeInto(newNode, root, root.getChildCount());
				} else {
					DefaultMutableTreeNode parentNode = treeStruct.get(parent);
					dtm.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
				}

				treeStruct.put(action.getActionNumber(), newNode);
			}
			dtm.reload();
			tree.setRootVisible(false);
			expandAllNodes(tree);

		} catch (Exception e) {

		}
	}




	/**
	 * <p>
	 * This method reads the log file for historical and current sessions, converts it to a GSON object and then calls the method
	 * to add each action to the play tree
	 * </p>
	 * <br/>
	 * <p>
	 * The method takes the log file, tree, tab number, and the tree structure as the parameters. It then proceeds to read the log file using
	 * GSON and create the list of ActionProperties variable. Then, it calls addActionToLogTree on each action element to add it to the tree.
	 * </p>
	 * <br/>
	 * <p>
	 * Since this is a generic method, it is used while loading all the tabs while loading the tree.
	 * </p>
	 */
	public void readLogAndPopulateTree(File logFile, JTree tree, int tabNo,
			HashMap<Integer, DefaultMutableTreeNode> treeStruct) {

		try {
			Gson gson = new Gson();
			String jsonLog = "";
			try {
				jsonLog = new String(Files.readAllBytes(logFile.toPath()));
			} catch (Exception e) {
				jsonLog = "";
			}

			tree.removeAll();
			tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("")));

			String listOfActions = "[" + jsonLog + "]";

			ActionProperties[] actionsFromGSON = gson.fromJson(listOfActions, ActionProperties[].class);

			ArrayList<ActionProperties> list1 = new ArrayList<ActionProperties>();
			Collections.addAll(list1, actionsFromGSON);
			allTabsInfo.get(tabNo).setActionObjects(list1);

			allTabsInfo.get(tabNo).setTreeStructure(treeStruct);

			for (int action = 1; action < actionsFromGSON.length; action++) {

				addActionToLogTree(actionsFromGSON[action], action, tree, treeStruct);

			}
		} catch (Exception e) {

		}
	}




	/**
	 * <p>
	 * This method is used to add the current session's actions in real-time to the tree.
	 * </p>
	 * <br/>
	 * <p>
	 * When a user opens a historical log, or opens the Reproducibility Dashboard Panel for the first time, the previous method 
	 * readLogAndPopulateTree is used to populate the tree. But, when an action needs to be populated to the tree in real-time, then, this
	 * method is used.
	 * </p>
	 *
	 */

	public void populateCurrentSessionTree(ActionProperties action) {
		currentSessionActionNumber++;

		allTabsInfo.get(0).getActionObjects().add(action);
		addActionToLogTree(action, currentSessionActionNumber, playTree, treeStructure);

	}



	/**
	 * <p>
	 * This method creates a new tab when a historical/ current log session is opened and calls appropriate methods to populate the
	 * play tree and the table (on mouse click of play tree actions)
	 * </p>
	 * <br/>
	 * <p>
	 * This is the first method called after the play tree of a tab is loaded and the other information of new tab has to be created and loaded
	 * (be it current session tab or historical tab ). Sequentially, this method creates a new tabbedPane if it does not exist, adds the 
	 * splitPane to the new tab, creates and adds a table to the right panel of the split pane, creates and adds the sample tables, adds the
	 * populated playtree to the left panel of the split pane, and then adds a mouseclick listener for each node in the play tree, so that when
	 * any node on the tree is clicked, the dataParameters of the action element clicked will be populated to the table. Since there are many 
	 * different types of variables that could be added to the dataParameters, the method uses formatting techniques to add lists, maps, sets
	 * and other generic objects to the same cell using carriage returns etc. Also, if the action has Samples too, they would be populated in
	 * the corresponding Included Samples and Excluded Samples tables.
	 * </p>
	 * 
	 */

	public int createNewTabAndPopulate(JTree playTree, JTable table, String tabName, boolean isClosable,
			String logFileName) {

		if (tabbedPane == null) {
			tabbedPane = new ClosableTabbedPane();
			//tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
			gbc_tabbedPane.gridwidth = 2;
			gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
			gbc_tabbedPane.fill = GridBagConstraints.BOTH;
			gbc_tabbedPane.gridx = 0;
			gbc_tabbedPane.gridy = 6;
			add(tabbedPane, gbc_tabbedPane);
		}
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.36);

		if (isClosable) {
			tabbedPane.addTab(tabName, null, splitPane, null);
		} else {
			tabbedPane.addNonClosableTab(tabName, null, splitPane, null);
		}

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		scrollPane_1.setViewportView(playTree);

		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_2);


		table.setModel(new DefaultTableModel(new Object[][] { { null, null } }, new String[] { "Property", "Value" }));
		scrollPane_2.setViewportView(table);
		Icon closedIcon = new ImageIcon(project.getClass().getResource(CHART_ICON_PATH));
		Icon openIcon = new ImageIcon(project.getClass().getResource(CHART_ICON_PATH));
		Icon leafIcon = new ImageIcon(project.getClass().getResource(CHART_ICON_PATH));

		int tabNo = tabbedPane.getTabCount() - 1;
		PlaybackTabData newPlaybackTab = new PlaybackTabData();
		newPlaybackTab.setLogFileName(logFileName);
		newPlaybackTab.setTabNumber(tabNo);
		newPlaybackTab.setTabTree(playTree);
		newPlaybackTab.setTabTable(table);
		newPlaybackTab.setIncludedSamplesTable(includedSamplesTable);
		newPlaybackTab.setExcludedSamplesTable(excludedSamplesTable);
		//newPlaybackTab.setFeaturesTable(featuresTable);

		allTabsInfo.put(tabbedPane.getTabCount() - 1, newPlaybackTab);

		playTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {

				try {
					ActionProperties[] actionObj = new ActionProperties[allTabsInfo.get(tabNo).getActionObjects()
					                                                    .size()];
					try {
						actionObj = allTabsInfo.get(tabNo).getActionObjects().toArray(actionObj);
					} catch (Exception e) {

					}

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) playTree.getLastSelectedPathComponent();

					if (node == null)
						return;

					Object nodeInfo = node.getUserObject();

					LoggingTreeNode ltn = (LoggingTreeNode) nodeInfo;

					ArrayList<Object[]> tableList = new ArrayList<Object[]>();

					Object[] commentsObj = new Object[2];
					commentsObj[0] = "<html><b>Comments</b></html>";

					try {
						commentsObj[1] = allTabsInfo.get(tabNo).getActionObjects().get(ltn.getNodeNumber())
								.getOtherParameters().get("comments");
					} catch (Exception e) {
						commentsObj[1] = "";
					}

					tableList.add(commentsObj);



					if (actionObj[ltn.getNodeNumber()].getDataParameters() != null
							&& actionObj[ltn.getNodeNumber()].getDataParameters().size() > 0) {

						for (Map.Entry<String, Object> entry : actionObj[ltn.getNodeNumber()].getDataParameters()
								.entrySet()) {
							Object[] num1Obj = new Object[2];
							num1Obj[0] = entry.getKey();
							num1Obj[1] = "";

							if(num1Obj[0].equals(SELECTED_FEATURES_PROPERTY)) {
								Object featureIndicesList =  entry.getValue();
								String[] selectedFeatures = {};
								if(featureIndicesList != null) {
									selectedFeatures = getSelectedFeaturesFromLog(featureIndicesList);
								}
								for(int sf = 0; sf < selectedFeatures.length; sf++ ) {
									num1Obj[1] += selectedFeatures[sf]+"\n";
								}
							}
							else if (entry.getValue() instanceof List<?>) {
								List<String> actionList = (List<String>) entry.getValue();
								for (int i = 0; i < actionList.size(); i++) {
									num1Obj[1] += String.valueOf(actionList.get(i)) + "\n";

								}
							} else if (entry.getValue() instanceof Map<?, ?>) {
								Map<?, ?> actionMap = (Map<?, ?>) entry.getValue();
								for (Map.Entry<?, ?> entry1 : actionMap.entrySet()) {
									num1Obj[1] += String.valueOf(entry1.getValue()) + "\n";

								}
							} else if (entry.getValue() instanceof String[]) {
								String[] actionArray = (String[]) entry.getValue();
								for (int i = 0; i < actionArray.length; i++) {
									num1Obj[1] += String.valueOf(actionArray[i]) + "\n";

								}
							} else {
								num1Obj[1] = String.valueOf(entry.getValue());

							}

							tableList.add(num1Obj);
						}
					}

					Object[] num1p1 = new Object[2];
					num1p1[0] = "<html><b>Timestamp</b></html>";
					num1p1[1] = String.valueOf(actionObj[ltn.getNodeNumber()].getTimestamp());
					tableList.add(num1p1);

					Object[] tableListObj = (Object[]) tableList.toArray();
					int objLen = tableListObj.length;
					Object[][] exactLengthObj = new Object[objLen][2];

					for (int j = 0; j < objLen; j++) {
						exactLengthObj[j] = (Object[]) tableListObj[j];
					}

					table.setModel(new DefaultTableModel(exactLengthObj, new String[] { "Parameter", "Value" }) {
						@Override
						public void setValueAt(Object aValue, int row, int column) {

							super.setValueAt(aValue, row, column);
							if (row == 0 && column == 1) {
								try {

									setTableRowSize(table, 0, aValue.toString());

									allTabsInfo.get(tabNo).getActionObjects().get(ltn.getNodeNumber())
									.getOtherParameters().put("comments", aValue);

									if (tabNo != 0)
										autoSaveLog(tabNo);
								} catch (Exception e) {

								}

							}

						}
					});


					table.getColumnModel().getColumn(1).setCellRenderer(new MultilineTableRenderer());
					table.getColumnModel().getColumn(1).setCellEditor(new MultilineTableCellEditor());


					for (int i = 0; i < exactLengthObj.length; i++) {

						if (exactLengthObj[i][1] != null) {
							String rowString = exactLengthObj[i][1].toString();

							setTableRowSize(table, i, rowString);

						}
					}

					table.setColumnSelectionAllowed(true);
					table.setRowSelectionAllowed(true);




					if (actionObj[ltn.getNodeNumber()].getOtherParameters() != null) {
						if(actionObj[ltn.getNodeNumber()].getOtherParameters().get(SAMPLE_ACTION_PROPERTY) != null) {

							int sampleActionNumber=0;
							if(actionObj[ltn.getNodeNumber()].getOtherParameters().get(SAMPLE_ACTION_PROPERTY) instanceof Double) {
								double temp = (double) actionObj[ltn.getNodeNumber()].getOtherParameters().get(SAMPLE_ACTION_PROPERTY);
								sampleActionNumber = (int)temp;
							}
							else {
								sampleActionNumber = (int)actionObj[ltn.getNodeNumber()].getOtherParameters().get(SAMPLE_ACTION_PROPERTY);
							}

							try {

								for(int i=0;i<actionObj.length;i++) {

									if(actionObj[i].getActionNumber() == sampleActionNumber) {

										String dataCol = (String)actionObj[i].getDataParameters().get(DATA_COLUMN_PROPERTY);

										List<String> excludedSamples = new ArrayList<String>();
										String [] samples = MetaOmGraph.getActiveProject().getDataColumnHeaders();

										LinkedList<String> allSamplesList = new LinkedList<String>(Arrays.asList(samples));
										LinkedList<String> copyAllSamplesList = new LinkedList<String>(Arrays.asList(samples));

										Object [] exclSamples = null;

										if(actionObj[i].getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY) instanceof List<?> ) {
											List<Double> exclSamplesList2 = (List<Double>)actionObj[i].getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY);
											exclSamples = exclSamplesList2.toArray();
										}
										else if(actionObj[i].getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY) instanceof HashSet<?>) {
											HashSet<Double> exclSamplesList2 = (HashSet<Double>)actionObj[i].getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY);
											exclSamples = exclSamplesList2.toArray();
										}
										else if(actionObj[i].getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY) instanceof Integer[]) {
											exclSamples = (Object[])actionObj[i].getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY);
										}


										for(int eindex = 0; eindex < exclSamples.length; eindex++) {

											int excludedindex = 0;
											if(exclSamples[eindex] instanceof Double) {
												Double a = (Double)exclSamples[eindex];
												excludedindex = a.intValue();
											}
											else if(exclSamples[eindex] instanceof Integer) {
												excludedindex = (int)exclSamples[eindex];
											}


											excludedSamples.add(copyAllSamplesList.get(excludedindex));
											try {
												allSamplesList.remove(copyAllSamplesList.get(excludedindex));
											}
											catch(Exception e) {
												StackTraceElement[] ste = e.getStackTrace();
											}
										}

										Object[] inclSamplesArray = allSamplesList.toArray();
										Object[][] inclSamplesDArray = new Object[inclSamplesArray.length][1];

										for(int x=0;x<inclSamplesArray.length;x++) {
											inclSamplesDArray[x][0] = inclSamplesArray[x];
										}

										includedSamplesTable.setModel(new DefaultTableModel(inclSamplesDArray, new String[] {dataCol}));

										Object[] exclSamplesArray = excludedSamples.toArray();
										Object[][] exclSamplesDArray = new Object[exclSamplesArray.length][1];

										for(int x=0;x<exclSamplesArray.length;x++) {
											exclSamplesDArray[x][0] = exclSamplesArray[x];
										}

										excludedSamplesTable.setModel(new DefaultTableModel(exclSamplesDArray, new String[] {dataCol}));




									}

								}
							}
							catch(Exception e2) {

							}


						}

						else {
							includedSamplesTable.setModel(new DefaultTableModel());
							excludedSamplesTable.setModel(new DefaultTableModel());
						}
					}
				} catch (Exception e) {

				}
			}
		});


		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) playTree.getCellRenderer();

		renderer.setClosedIcon(closedIcon);
		renderer.setOpenIcon(openIcon);
		renderer.setLeafIcon(leafIcon);

		return tabNo;
	}



	/**
	 * This method prints a JDialog box. Useful for debugging
	 */

	public void printDialog(String msg) {
		JDialog jd = new JDialog();
		JTextPane jt = new JTextPane();
		jt.setText(msg);
		jt.setBounds(10, 10, 300, 100);
		jd.getContentPane().add(jt);
		jd.setBounds(100, 100, 500, 200);
		jd.setVisible(true);
	}



	/**
	 * This method returns the current session's log file name
	 */
	public String getCurrentLoggerFileName() {
		if (logger != null) {
			org.apache.logging.log4j.core.Logger loggerImpl = (org.apache.logging.log4j.core.Logger) logger;
			Appender appender = loggerImpl.getAppenders().get("reproducibilityAppender");

			return ((FileAppender) appender).getFileName();
		} else {
			return "";
		}
	}



	/**
	 * Generic method to expand all nodes of any JTree
	 */
	public void expandAllNodes(JTree tree) {
		int j = tree.getRowCount();
		int i = 0;
		while (i < j) {
			tree.expandRow(i);
			i += 1;
			j = tree.getRowCount();
		}
	}


	/**
	 * <p>
	 * This method adds a set of actions as favorites and also makes the play tree display a golden star beside the name of the action
	 * </p>
	 * <br/>
	 * <p>
	 * The method takes the tab number, tree selected, the tree model and the list of action tree paths selected as inputs. Then, based on 
	 * whether the action selected is already a favorite or not, it adds or removes the golden star. Once the star is added/removed, the 
	 * method proceeds to make the "favorite" property as true/false and autosave it to the corresponding log file for future reference. The
	 * tree nodes are expanded once the changes are done to the tree.
	 * </p>
	 * 
	 */
	public void markActionsAsFavorite(int tabNo, JTree selectedTree, DefaultTreeModel model, TreePath[] allPaths) {

		PlaybackTabData currentTabData = allTabsInfo.get(tabNo);
		String logFileName = currentTabData.getLogFileName();

		BufferedWriter out = null;

		try {
			if (tabNo != 0) {
				out = new BufferedWriter(new FileWriter(logFileName, true));
			}

			for (TreePath path : allPaths) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object nodeInfo = node.getUserObject();
				LoggingTreeNode ltn = (LoggingTreeNode) nodeInfo;
				Object nodeObj = node.getUserObject();
				LoggingTreeNode logNode = (LoggingTreeNode) nodeObj;

				ActionProperties likedAction = allTabsInfo.get(tabNo).getActionObjects().get(ltn.getNodeNumber());

				String actionCommandString = likedAction.getActionCommand();

				if(likedAction.getOtherParameters().get("Playable") != null) {
					actionCommandString = "<font color=red>"+(String) likedAction.getActionCommand()+"</font>";
				}

				try {
					if (likedAction.getOtherParameters().get(FAVORITE_PROPERTY).equals("true")) {


						likedAction.getOtherParameters().put(FAVORITE_PROPERTY, "false");

						if(likedAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) != null) {

							String[] features = getSelectedFeaturesFromLog(likedAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY));


							node.setUserObject(new LoggingTreeNode("<html><p>"+actionCommandString+ " ["
									+ (String) features[0] + "]</html></p>"
									,
									logNode.getCommandName(), logNode.getNodeNumber()));

						}
						else {

							node.setUserObject(new LoggingTreeNode(logNode.getCommandName(),
									logNode.getCommandName(), logNode.getNodeNumber()));
						}

						model.reload();
						expandAllNodes(selectedTree);
					} else if (likedAction.getOtherParameters().get(FAVORITE_PROPERTY).equals("false")) {
						likedAction.getOtherParameters().put(FAVORITE_PROPERTY, "true");

						if(likedAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) != null) {

							String[] features = getSelectedFeaturesFromLog(likedAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY));

							node.setUserObject(new LoggingTreeNode("<html><p>" + actionCommandString+ " ["
									+ (String) features[0] + "]"

								+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
								logNode.getCommandName(), logNode.getNodeNumber()));


						}
						else {
							node.setUserObject(new LoggingTreeNode("<html><p>" + actionCommandString
									+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
									logNode.getCommandName(), logNode.getNodeNumber()));
						}


						model.reload();
						expandAllNodes(selectedTree);
					} else {
						likedAction.getOtherParameters().put(FAVORITE_PROPERTY, "true");

						if(likedAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) != null) {

							String[] features = getSelectedFeaturesFromLog(likedAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY));


							node.setUserObject(new LoggingTreeNode("<html><p>" + actionCommandString+ " ["
									+ (String) features[0] + "]"

								+ "   &nbsp; <font color=orange>&#9733;</font></p></html>",
								logNode.getCommandName(), logNode.getNodeNumber()));


						}
						else {
							node.setUserObject(new LoggingTreeNode("<html><p>" + actionCommandString
									+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
									logNode.getCommandName(), logNode.getNodeNumber()));
						}
						model.reload();
						expandAllNodes(selectedTree);

					}
				} catch (Exception e) {

					likedAction.getOtherParameters().put(FAVORITE_PROPERTY, "true");

					if(likedAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) != null) {

						String[] features = getSelectedFeaturesFromLog(likedAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY));

						node.setUserObject(new LoggingTreeNode("<html><p>" + actionCommandString+ " ["
								+ (String) features[0] + "]"

							+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
							logNode.getCommandName(), logNode.getNodeNumber()));


					}
					else {
						node.setUserObject(new LoggingTreeNode("<html><p>" + actionCommandString
								+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
								logNode.getCommandName(), logNode.getNodeNumber()));
					}
					model.reload();
					expandAllNodes(selectedTree);
				}

				if (tabNo != 0)
					autoSaveLog(tabNo);

			}

		} 
		catch (Exception e) {

		}
		finally {
			try {
				if(tabNo!=0 && out!=null) {
					out.close();
				}
			} catch (IOException e) {

			}
		}
	}

	/**
	 * This method is used to auto save the historical/current session log when users type in comments or hit add to favorite
	 */
	public void autoSaveLog(int tabNo) {

		FileWriter fw = null;
		try {
			PlaybackTabData currentTabData = allTabsInfo.get(tabNo);
			String logFileName = currentTabData.getLogFileName();

			Gson gson = new GsonBuilder().create();
			ArrayList<ActionProperties> ap = new ArrayList<ActionProperties>();

			for (ActionProperties act : allTabsInfo.get(tabNo).getActionObjects()) {
				if (act != null) {
					ap.add(act);
				}
			}

			ActionProperties[] apArray = ap.toArray(new ActionProperties[ap.size()]);

			fw = new FileWriter(logFileName, false);
			String outputJson = gson.toJson(apArray);
			String output = outputJson.substring(1, outputJson.length() - 1);
			//output = output.replaceAll("\r", "");
			fw.write(output);

		} catch (Exception e2) {

		} finally {
			try {
				if(fw!=null) {
					fw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block

			}
		}

	}


	/**
	 * This method returns the samples size, and the features size from the given log file in 
	 * an array.  dim[0] - Samples size,  dim[1] - Features size
	 * 
	 * 
	 * @param logfile - The log file from which we need to get the Sample size and feature size
	 * @return
	 */
	int[] getDimensionsFromFile(File logfile) {

		int[] dim = new int[2];

		Gson gson = new Gson();
		String jsonLog = "";
		try {
			
			jsonLog = new String(Files.readAllBytes(logfile.toPath()));
			
			String listOfActions = "[" + jsonLog + "]";

			ActionProperties[] actionsFromGSON = gson.fromJson(listOfActions, ActionProperties[].class);

			ArrayList<ActionProperties> list1 = new ArrayList<ActionProperties>();
			Collections.addAll(list1, actionsFromGSON);

			for (int action = 1; action < 5; action++) {

				if(actionsFromGSON[action].getDataParameters().get("Dimensions")!= null && actionsFromGSON[action].getDataParameters().get("Row Count") != null) {

					dim[0] = Integer.parseInt((String)actionsFromGSON[action].getDataParameters().get("Dimensions"));
					dim[1] = Integer.parseInt((String)actionsFromGSON[action].getDataParameters().get("Row Count"));

					return dim;
				}
			}

		} catch (Exception e) {
			return null;
		}

		return null;
	}



	/**
	 * This method is used to adjust the table row size based on the string being populated to the row
	 */

	public void setTableRowSize(JTable table, int rowNum, String rowString) {

		int numEnters = 0;
		int numChars = 0;
		for (int x = 0; x < rowString.length(); x++) {
			if (rowString.charAt(x) == '\n' || rowString.charAt(x) == '\r') {
				numEnters++;
				numChars = 0;
			}
			else {
				numChars++;
			}

			if(numChars >= 18) {
				numEnters++;
				numChars = 0;
			}
		}
		int rowlen = numEnters * 20;
		rowlen += numEnters*20;
		if (rowlen > 0) {

			table.setRowHeight(rowNum, rowlen);

		} else {

			table.setRowHeight(rowNum, 20);

		}
	}


	
	/**
	 * This is a utility method to get the selected features array from an Object of 
	 * selectedFeatures. It is important because the GSON reads the selectedFeatures
	 * from the log file as an Object, which is sometimes interpreted as a List, or 
	 * a HashMap or an array depending on whether it is the current log or a historical
	 * log opened from the file.This method ensures that SelectedFeatures are converted
	 * to a string array.
	 * 
	 * @param selectedFeatures
	 * @return
	 */
	public String[] getSelectedFeaturesFromLog(Object selectedFeatures) {


		int val2[] = null;

		if(selectedFeatures != null) {
			if(selectedFeatures instanceof List<?> ) {
				List<Double> selFeaturesList = (List<Double>)selectedFeatures;
				Double[] selFeaturesDouble = new Double[selFeaturesList.size()];

				for( int fno = 0 ; fno < selFeaturesList.size(); fno++ ) {
					selFeaturesDouble[fno] = (Double)selFeaturesList.get(fno);
				}

				val2 = new int[selFeaturesDouble.length];

				for( int i =0; i< selFeaturesDouble.length; i++ ) {
					val2[i] = selFeaturesDouble[i].intValue();
				}
			}
			else if(selectedFeatures instanceof HashSet<?>) {
				HashSet<Double> selFeaturesHashSet = (HashSet<Double>)selectedFeatures;
				Double[] selFeaturesDouble = new Double[selFeaturesHashSet.size()];

				selFeaturesDouble = selFeaturesHashSet.toArray(selFeaturesDouble);
				val2 = new int[selFeaturesDouble.length];

				for( int i =0; i< selFeaturesDouble.length; i++ ) {
					val2[i] = selFeaturesDouble[i].intValue();
				}

			}
			else if(selectedFeatures instanceof Integer[]) {
				Integer[] selFeaturesIntArray = (Integer[])selectedFeatures;

				val2 = new int[selFeaturesIntArray.length];

				for( int i =0; i< selFeaturesIntArray.length; i++ ) {
					val2[i] = (int)selFeaturesIntArray[i];
				}
			}
			else if(selectedFeatures instanceof Double[]) {
				Double[] selFeaturesDoubleArray = (Double[])selectedFeatures;

				val2 = new int[selFeaturesDoubleArray.length];

				for( int i =0; i< selFeaturesDoubleArray.length; i++ ) {
					val2[i] = selFeaturesDoubleArray[i].intValue();
				}
			}
			else {
				val2 = (int[])selectedFeatures;
			}

		}
		if(val2 != null && val2.length > 0) {
			String[] selectedGeneNames = MetaOmGraph.getActiveProject().getDefaultRowNames(val2);
			return selectedGeneNames;
		}

		return new String[] {"null"};
	}

}


/**
 * 
 * @author Harsha
 *
 * This is a custom Table Cell Renderer class used for wrapping text in a JTable
 */
class MultilineTableRenderer extends JTextArea implements TableCellRenderer {
	public MultilineTableRenderer() {
		setOpaque(true);
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (isSelected) {
			setForeground(Color.WHITE);
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(Color.BLACK);
			setBackground(table.getBackground());
		}

		setText((value == null) ? "" : value.toString());
		setSize(table.getColumnModel().getColumn(column).getWidth(),
				Short.MAX_VALUE);


		return this;
	}

}



/**
 * 
 * @author Harsha
 *
 * This is a custom Table Cell Editor class used for wrapping text during editing of a cell in a JTable
 */
class MultilineTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	JComponent component = new JTextArea();

	public MultilineTableCellEditor() {

	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex,
			int vColIndex) {

		if(rowIndex == 0) {

			if(component == null) {
				component = new JTextArea();
			}
			((JTextArea) component).setText((String) value);

			((JTextArea) component).setLineWrap(true);
			((JTextArea) component).setWrapStyleWord(true);
			((JTextArea) component).addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					super.componentResized(e);
					table.setRowHeight(rowIndex, (int) (((JTextArea) component).getPreferredSize().getHeight()));
				}
			});
			((JTextArea) component).addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					super.keyTyped(e);
					table.setRowHeight(rowIndex, (int) (((JTextArea) component).getPreferredSize().getHeight()));
				}
			});
		}
		else {
			component = (JComponent) table.getCellEditor();
		}

		return component;
	}

	public Object getCellEditorValue() {
		return ((JTextArea) component).getText();
	}
}
