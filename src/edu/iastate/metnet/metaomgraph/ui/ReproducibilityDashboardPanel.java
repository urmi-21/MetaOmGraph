package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.playback.LoggingTreeNode;
import edu.iastate.metnet.metaomgraph.playback.PlaybackAction;
import edu.iastate.metnet.metaomgraph.playback.PlaybackTabData;

public class ReproducibilityDashboardPanel extends JPanel {


	/* Harsha- Added logger */

	private static final Logger logger = MetaOmGraph.logger;

	private JPanel panel;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JSeparator separator_1;
	private JPanel panel_1;
	private JLabel lblNewLabel_1;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;
	private JRadioButton rdbtnPermanentlySwitchedOff;
	private MetaOmGraph project;
	private HashMap<Integer, DefaultMutableTreeNode> treeStructure;
	private HashMap<Integer, PlaybackTabData> allTabsInfo;
	private PlaybackAction playbackAction;
	private JTree playTree;
	private JTable table;
	private ClosableTabbedPane tabbedPane;
	private ClosableTabbedPane samplesPane;
	private JTable includedSamplesTable;
	private JTable excludedSamplesTable;
	private int currentSessionActionNumber;
	private JButton btnNewButton_3;

	public ReproducibilityDashboardPanel(MetaOmGraph myself) {

		
		project = myself;
		URL starURL = null;
		
		if(getClass().getResource("resource/loggingicons/tinyorangestar.png" ) != null) {
			printDialog("1st is correct");
		}
		else if(getClass().getResource("../resource/loggingicons/tinyorangestar.png" ) != null) {
			printDialog("2nd is correct");
		}
		else if(getClass().getResource("../../resource/loggingicons/tinyorangestar.png" ) != null) {
			printDialog("3rd is correct");
		}
		else if(getClass().getResource("../../../resource/loggingicons/tinyorangestar.png" ) != null) {
			printDialog("4th is correct");
		}
		else if(getClass().getResource("../../../../resource/loggingicons/tinyorangestar.png" ) != null) {
			printDialog("5th is correct");
		}
		else if(getClass().getResource("../../../../../resource/loggingicons/tinyorangestar.png" ) != null) {
			printDialog("6th is correct");
		}
		else if(getClass().getResource("../../../../../../resource/loggingicons/tinyorangestar.png" ) != null) {
			printDialog("7th is correct");
		}
		else if(getClass().getResource("loggingicons/tinyorangestar.png" ) != null) {
			printDialog("8th is correct");
		}
		else if(getClass().getResource("tinyorangestar.png" ) != null) {
			printDialog("9th is correct");
		}
		else if(getClass().getResource("/tinyorangestar.png" ) != null) {
			printDialog("10th is correct");
		}
		else if(getClass().getResource("src/resource/loggingicons/tinyorangestar.png" ) != null) {
			printDialog("11th is correct");
		}
		else if(getClass().getResource("/src/resource/tinyorangestar.png" ) != null) {
			printDialog("12th is correct");
		}
		else if(getClass().getResource("/resource/tinyorangestar.png" ) != null) {
			printDialog("13th is correct");
		}
		else {
			printDialog("none is correct");
		}
		
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

		panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.control);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(panel_1, gbc_panel_1);

		lblNewLabel_1 = new JLabel("logging : ");
		panel_1.add(lblNewLabel_1);

		ButtonGroup G = new ButtonGroup();

		rdbtnNewRadioButton = new JRadioButton("on");
		rdbtnNewRadioButton.setSelected(true);
		panel_1.add(rdbtnNewRadioButton);

		rdbtnNewRadioButton_1 = new JRadioButton("off");
		panel_1.add(rdbtnNewRadioButton_1);

		rdbtnPermanentlySwitchedOff = new JRadioButton("permanently switched off");
		panel_1.add(rdbtnPermanentlySwitchedOff);

		if (MetaOmGraph.getPermanentLogging() == false) {
			rdbtnNewRadioButton.setSelected(false);
			rdbtnNewRadioButton_1.setSelected(false);
			rdbtnPermanentlySwitchedOff.setSelected(true);
		}
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
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

		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
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

		G.add(rdbtnNewRadioButton);
		G.add(rdbtnNewRadioButton_1);
		G.add(rdbtnPermanentlySwitchedOff);

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

		btnNewButton = new JButton("open previous session");
		btnNewButton.setToolTipText("Open a previous session log file");
		btnNewButton.setIcon(new ImageIcon(project.getClass().getResource("/resource/loggingicons/tinyfolder.png")));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("open previous session log");
				jfc.setCurrentDirectory(MetaOmGraph.getActiveProject().getSourceFile());
				int retValue = jfc.showOpenDialog(MetaOmGraph.getMainWindow());

				if (retValue == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();

					JTree sessionTree = new JTree();
					JTable sessionTable = new JTable();

					HashMap<Integer, DefaultMutableTreeNode> treeStruct = new HashMap<Integer, DefaultMutableTreeNode>();

					int tabNo = createNewTabAndPopulate(sessionTree, sessionTable, file.getName(), true,
							file.getAbsolutePath());
					readLogAndPopulateTree(file, sessionTree, tabNo, treeStruct);
					tabbedPane.setSelectedIndex(tabNo);

				}
			}
		});
		panel.add(btnNewButton);

		btnNewButton_1 = new JButton("play");
		btnNewButton_1.setToolTipText("Select and play an action item");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int tabNo = tabbedPane.getSelectedIndex();

				// DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				// allTabsInfo.get(tabNo).getTabTree().getLastSelectedPathComponent();

				JTree selectedTree = allTabsInfo.get(tabNo).getTabTree();

				TreePath[] allPaths = selectedTree.getSelectionPaths();

				for (TreePath path : allPaths) {
					DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) path.getLastPathComponent();
					Object nodeObj = node2.getUserObject();
					LoggingTreeNode ltn = (LoggingTreeNode) nodeObj;

					ActionProperties playedAction = allTabsInfo.get(tabNo).getActionObjects().get(ltn.getNodeNumber());
					
					int samplesActionId = 1;
					if(playedAction.getOtherParameters().get("Sample Action") instanceof Double) {
						double temp = (double) playedAction.getOtherParameters().get("Sample Action");
						samplesActionId = (int)temp;
					}
					else {
						samplesActionId = (int)playedAction.getOtherParameters().get("Sample Action");
					}
					
					HashSet<String> includedSamples = new HashSet<String>();
					HashSet<String> excludedSamples = new HashSet<String>();
					
					for(int i=0;i<allTabsInfo.get(tabNo).getActionObjects().size();i++) {
						
						if(allTabsInfo.get(tabNo).getActionObjects().get(i).getActionNumber() == samplesActionId) {
							
							ActionProperties sampleAction = allTabsInfo.get(tabNo).getActionObjects().get(i);
							
							if(sampleAction.getOtherParameters().get("Included Samples") instanceof List<?>) {
								includedSamples = new HashSet<String>((List<String>)sampleAction.getOtherParameters().get("Included Samples"));
							}
							else if(sampleAction.getOtherParameters().get("Included Samples") instanceof HashSet<?>) {
								includedSamples = (HashSet<String>)sampleAction.getOtherParameters().get("Included Samples");
							}
							
							if(sampleAction.getOtherParameters().get("Excluded Samples") instanceof List<?>) {
								excludedSamples = new HashSet<String>((List<String>)sampleAction.getOtherParameters().get("Excluded Samples"));
							}
							else if(sampleAction.getOtherParameters().get("Excluded Samples") instanceof HashSet<?>) {
								excludedSamples = (HashSet<String>)sampleAction.getOtherParameters().get("Excluded Samples");
							}
							
						}
					}
					
					
					if (ltn.getCommandName().equalsIgnoreCase("line-chart")) {
						playbackAction.playChart(playedAction, "line-chart", includedSamples, excludedSamples);

					} else if (ltn.getCommandName().equalsIgnoreCase("scatter-plot")) {
						playbackAction.playChart(playedAction, "scatter-plot", includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase("box-plot")) {
						playbackAction.playChart(playedAction, "box-plot", includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase("histogram")) {
						playbackAction.playChart(playedAction, "histogram", includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase("line-chart-default-grouping")) {
						playbackAction.playChart(playedAction, "line-chart-default-grouping", includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase("line-chart-choose-grouping")) {
						playbackAction.playChart(playedAction, "line-chart-choose-grouping", includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase("bar-chart")) {
						playbackAction.playChart(playedAction, "bar-chart", includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase("correlation-histogram")) {
						playbackAction.playChart(playedAction, "correlation-histogram", includedSamples, excludedSamples);
					}

				}
				

			}
		});
		btnNewButton_1.setIcon(new ImageIcon(project.getClass().getResource("/resource/loggingicons/tinyplay.png")));
		panel.add(btnNewButton_1);

		btnNewButton_3 = new JButton();
		btnNewButton_3
				.setIcon(new ImageIcon(project.getClass().getResource("/resource/loggingicons/smallorangestar.png")));
		btnNewButton_3.setMargin(new Insets(2, 5, 2, 5));
		btnNewButton_3.setToolTipText("Add to Favorites");

		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int tabNo = tabbedPane.getSelectedIndex();
				JTree selectedTree = allTabsInfo.get(tabNo).getTabTree();
				DefaultTreeModel model = (DefaultTreeModel) selectedTree.getModel();
				TreePath[] allPaths = selectedTree.getSelectionPaths();

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
						try {
							if (likedAction.getOtherParameters().get("favorite").equals("true")) {
								
								
								likedAction.getOtherParameters().put("favorite", "false");
								
								if(likedAction.getDataParameters().get("Selected Features") != null) {
									if (likedAction.getDataParameters().get("Selected Features") instanceof LinkedTreeMap<?, ?>) {
										
										LinkedTreeMap<String, Object> features = (LinkedTreeMap<String, Object>) likedAction
												.getDataParameters().get("Selected Features");
										
										node.setUserObject(new LoggingTreeNode(logNode.getCommandName()+ " ["
												+ (String) features.entrySet().iterator().next().getValue() + "]"
												,
												logNode.getCommandName(), logNode.getNodeNumber()));
									}
									else {
										
										HashMap<String, Object> features = (HashMap<String, Object>) likedAction
												.getDataParameters().get("Selected Features");
										
										node.setUserObject(new LoggingTreeNode(logNode.getCommandName()+ " ["
												+ (String) features.entrySet().iterator().next().getValue() + "]"
												,
												logNode.getCommandName(), logNode.getNodeNumber()));
										
									}
									
								}
								else {
									
									node.setUserObject(new LoggingTreeNode(logNode.getCommandName(),
											logNode.getCommandName(), logNode.getNodeNumber()));
								}
								
								model.reload();
								expandAllNodes(selectedTree);
							} else if (likedAction.getOtherParameters().get("favorite").equals("false")) {
								likedAction.getOtherParameters().put("favorite", "true");
								
								if(likedAction.getDataParameters().get("Selected Features") != null) {
									
									if (likedAction.getDataParameters().get("Selected Features") instanceof LinkedTreeMap<?, ?>) {
										LinkedTreeMap<String, Object> features = (LinkedTreeMap<String, Object>) likedAction
												.getDataParameters().get("Selected Features");
										
										node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()+ " ["
												+ (String) features.entrySet().iterator().next().getValue() + "]"
												
										+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
										logNode.getCommandName(), logNode.getNodeNumber()));
									}
									else {
										
										HashMap<String, Object> features = (HashMap<String, Object>) likedAction
												.getDataParameters().get("Selected Features");
										
										node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()+ " ["
												+ (String) features.entrySet().iterator().next().getValue() + "]"
												
										+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
										logNode.getCommandName(), logNode.getNodeNumber()));
										
									}
									
								}
								else {
									node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()
									+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
									logNode.getCommandName(), logNode.getNodeNumber()));
								}
								
								
								model.reload();
								expandAllNodes(selectedTree);
							} else {
								likedAction.getOtherParameters().put("favorite", "true");
								
								if(likedAction.getDataParameters().get("Selected Features") != null) {
									
									if (likedAction.getDataParameters().get("Selected Features") instanceof LinkedTreeMap<?, ?>) {
										
										LinkedTreeMap<String, Object> features = (LinkedTreeMap<String, Object>) likedAction
												.getDataParameters().get("Selected Features");
										
										node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()+ " ["
												+ (String) features.entrySet().iterator().next().getValue() + "]"
												
										+ "   &nbsp; <font color=orange>&#9733;</font></p></html>",
										logNode.getCommandName(), logNode.getNodeNumber()));
									}
									else {
										
										HashMap<String, Object> features = (HashMap<String, Object>) likedAction
												.getDataParameters().get("Selected Features");
										
										node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()+ " ["
												+ (String) features.entrySet().iterator().next().getValue() + "]"
												
										+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
										logNode.getCommandName(), logNode.getNodeNumber()));
									}
									
								}
								else {
									node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()
									+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
									logNode.getCommandName(), logNode.getNodeNumber()));
								}
								model.reload();
								expandAllNodes(selectedTree);

							}
						} catch (Exception e) {
							
							likedAction.getOtherParameters().put("favorite", "true");
							
							if(likedAction.getDataParameters().get("Selected Features") != null) {
								
								if (likedAction.getDataParameters().get("Selected Features") instanceof LinkedTreeMap<?, ?>) {
									
									LinkedTreeMap<String, Object> features = (LinkedTreeMap<String, Object>) likedAction
											.getDataParameters().get("Selected Features");
									
									node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()+ " ["
											+ (String) features.entrySet().iterator().next().getValue() + "]"
											
									+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
									logNode.getCommandName(), logNode.getNodeNumber()));
								}
								else {
									
									HashMap<String, Object> features = (HashMap<String, Object>) likedAction
											.getDataParameters().get("Selected Features");
									
									node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()+ " ["
											+ (String) features.entrySet().iterator().next().getValue() + "]"
											
									+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
									logNode.getCommandName(), logNode.getNodeNumber()));
								}
								
							}
							else {
								node.setUserObject(new LoggingTreeNode("<html><p>" + logNode.getCommandName()
								+ "   &nbsp;<font color=orange>&#9733;</font></p></html>",
								logNode.getCommandName(), logNode.getNodeNumber()));
							}
							model.reload();
							expandAllNodes(selectedTree);
						}

						if (tabNo != 0)
							autoSaveLog(tabNo);

					}

				} catch (IOException e) {
					System.out.println("exception occoured" + e);
				} finally {
					try {
						if(tabNo!=0 && out!=null) {
						out.close();
						}
					} catch (IOException e) {
						
					}
				}
			}
		});
		panel.add(btnNewButton_3);

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
		samplesPane.addNonClosableTab("Included Samples", null, samplesPanel, null);
		JScrollPane samplesPanel2 = new JScrollPane(excludedSamplesTable);
		samplesPane.addNonClosableTab("Excluded Samples", null, samplesPanel2, null);
		

	}

	public void addActionToLogTree(ActionProperties action, int actionNumber, JTree tree,
			HashMap<Integer, DefaultMutableTreeNode> treeStruct) {

		try {
			DefaultTreeModel dtm = (DefaultTreeModel) tree.getModel();

			if (!action.getActionCommand().equalsIgnoreCase("general-properties")) {

				DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

				DefaultMutableTreeNode newNode = null;
				try {
					if (action.getOtherParameters().get("favorite").equals("true")) {
						if (action.getDataParameters().get("Selected Features") != null) {
							LinkedTreeMap<String, Object> features = (LinkedTreeMap<String, Object>) action
									.getDataParameters().get("Selected Features");
							newNode = new DefaultMutableTreeNode(new LoggingTreeNode("<html><p>"
									+ action.getActionCommand() + " ["
									+ (String) features.entrySet().iterator().next().getValue() + "]"
									+ "   &nbsp;<img src=\"file:src/resource/loggingicons/tinyorangestar.png\" style=\"display:none\" ></p></html>",
									action.getActionCommand(), actionNumber));
						} else {
							newNode = new DefaultMutableTreeNode(new LoggingTreeNode("<html><p>"
									+ action.getActionCommand()
									+ "   &nbsp;<img src=\"file:src/resource/loggingicons/tinyorangestar.png\" style=\"display:none\" ></p></html>",
									action.getActionCommand(), actionNumber));
						}
					} else {
						if (action.getDataParameters().get("Selected Features") != null) {
							LinkedTreeMap<String, Object> features = (LinkedTreeMap<String, Object>) action
									.getDataParameters().get("Selected Features");
							newNode = new DefaultMutableTreeNode(
									new LoggingTreeNode(
											action.getActionCommand() + " ["
													+ features.entrySet().iterator().next().getValue() + "]",
											action.getActionCommand(), actionNumber));
						} else {
							newNode = new DefaultMutableTreeNode(new LoggingTreeNode(action.getActionCommand(),
									action.getActionCommand(), actionNumber));
						}
					}
				} catch (Exception e) {

					if (action.getDataParameters().get("Selected Features") != null) {
						if (action.getDataParameters().get("Selected Features") instanceof LinkedTreeMap<?, ?>) {
							LinkedTreeMap<String, Object> features = (LinkedTreeMap<String, Object>) action
									.getDataParameters().get("Selected Features");
							newNode = new DefaultMutableTreeNode(
									new LoggingTreeNode(
											action.getActionCommand() + " ["
													+ features.entrySet().iterator().next().getValue() + "]",
											action.getActionCommand(), actionNumber));
						} else if(action.getDataParameters().get("Selected Features") instanceof HashMap<?, ?>){
							HashMap<String, Object> features = (HashMap<String, Object>) action.getDataParameters()
									.get("Selected Features");
							newNode = new DefaultMutableTreeNode(
									new LoggingTreeNode(
											action.getActionCommand() + " ["
													+ features.entrySet().iterator().next().getValue() + "]",
											action.getActionCommand(), actionNumber));
						}
						else {
							newNode = new DefaultMutableTreeNode(
									new LoggingTreeNode(
											action.getActionCommand() ,
											action.getActionCommand(), actionNumber));
						}

					} else {
						newNode = new DefaultMutableTreeNode(new LoggingTreeNode(action.getActionCommand(),
								action.getActionCommand(), actionNumber));
					}
				}

				Integer parent = (int) Double.parseDouble(action.getActionParameters().get("parent").toString());

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
			
			StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            printDialog(exceptionAsString);
		}
	}

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
			StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            printDialog(exceptionAsString);
		}
	}

	public void populateCurrentSessionTree(ActionProperties action) {
		currentSessionActionNumber++;

		allTabsInfo.get(0).getActionObjects().add(action);
		addActionToLogTree(action, currentSessionActionNumber, playTree, treeStructure);

	}

	public int createNewTabAndPopulate(JTree playTree, JTable table, String tabName, boolean isClosable,
			String logFileName) {

		if (tabbedPane == null) {
			tabbedPane = new ClosableTabbedPane();
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
		
		
		//JTable featuresTable = new JTable();
		//JScrollPane samplesPanel3 = new JScrollPane(featuresTable);
		//samplesPane.addTab("Features", null, samplesPanel3, null);
		
		// table = new JTable();
		table.setModel(new DefaultTableModel(new Object[][] { { null, null } }, new String[] { "Property", "Value" }));
		scrollPane_2.setViewportView(table);
		Icon closedIcon = new ImageIcon(project.getClass().getResource("/resource/loggingicons/chart.png"));
		Icon openIcon = new ImageIcon(project.getClass().getResource("/resource/loggingicons/chart.png"));
		Icon leafIcon = new ImageIcon(project.getClass().getResource("/resource/loggingicons/chart.png"));

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
						// Nothing is selected.
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
							if (entry.getValue() instanceof List<?>) {
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
							// Here update DB with a SwingWorker and the new provided value
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

					// table.setShowHorizontalLines(false);
					table.setColumnSelectionAllowed(true);
					table.setRowSelectionAllowed(true);
					
					
					
					
					if (actionObj[ltn.getNodeNumber()].getOtherParameters() != null) {
						if(actionObj[ltn.getNodeNumber()].getOtherParameters().get("Sample Action") != null) {
						
							int sampleActionNumber=0;
							if(actionObj[ltn.getNodeNumber()].getOtherParameters().get("Sample Action") instanceof Double) {
								double temp = (double) actionObj[ltn.getNodeNumber()].getOtherParameters().get("Sample Action");
								sampleActionNumber = (int)temp;
							}
							else {
								sampleActionNumber = (int)actionObj[ltn.getNodeNumber()].getOtherParameters().get("Sample Action");
							}

						try {
							
						for(int i=0;i<actionObj.length;i++) {
							
							if(actionObj[i].getActionNumber() == sampleActionNumber) {

								String dataCol = (String)actionObj[i].getDataParameters().get("Data Column");
								
								if(actionObj[i].getOtherParameters().get("Included Samples") instanceof List<?>) {
									
									
									List<String> inclSamplesList = (List<String>)actionObj[i].getOtherParameters().get("Included Samples");
									Object[] inclSamplesArray = inclSamplesList.toArray();
									Object[][] inclSamplesDArray = new Object[inclSamplesArray.length][1];
									
									for(int x=0;x<inclSamplesArray.length;x++) {
										inclSamplesDArray[x][0] = inclSamplesArray[x];
									}
									
									includedSamplesTable.setModel(new DefaultTableModel(inclSamplesDArray, new String[] {dataCol}));
								}
								else if(actionObj[i].getOtherParameters().get("Included Samples") instanceof HashSet<?>) {
									
									
									HashSet<String> inclSamplesSet = (HashSet<String>)actionObj[i].getOtherParameters().get("Included Samples");
									Object[] inclSamplesArray = inclSamplesSet.toArray();
									Object[][] inclSamplesDArray = new Object[inclSamplesArray.length][1];
									
									for(int x=0;x<inclSamplesArray.length;x++) {
										inclSamplesDArray[x][0] = inclSamplesArray[x];
									}
									includedSamplesTable.setModel(new DefaultTableModel(inclSamplesDArray, new String[] {dataCol}));
									
								}
									
									
								
								if(actionObj[i].getOtherParameters().get("Excluded Samples") instanceof List<?> ) {
									List<String> exclSamplesList = (List<String>)actionObj[i].getOtherParameters().get("Excluded Samples");
									Object[] exclSamplesArray = exclSamplesList.toArray();
									Object[][] exclSamplesDArray = new Object[exclSamplesArray.length][1];
									
									for(int x=0;x<exclSamplesArray.length;x++) {
										exclSamplesDArray[x][0] = exclSamplesArray[x];
									}
									excludedSamplesTable.setModel(new DefaultTableModel(exclSamplesDArray, new String[] {dataCol}));
									
								}
								else if(actionObj[i].getOtherParameters().get("Excluded Samples") instanceof HashSet<?>) {
									HashSet<String> exclSamplesSet = (HashSet<String>)actionObj[i].getOtherParameters().get("Excluded Samples");
									Object[] exclSamplesArray = exclSamplesSet.toArray();
									Object[][] exclSamplesDArray = new Object[exclSamplesArray.length][1];
									
									for(int x=0;x<exclSamplesArray.length;x++) {
										exclSamplesDArray[x][0] = exclSamplesArray[x];
									}
									excludedSamplesTable.setModel(new DefaultTableModel(exclSamplesDArray, new String[] {dataCol}));
									
								}


							}

						}
						}
						catch(Exception e2) {
							StringWriter sw = new StringWriter();
							e2.printStackTrace(new PrintWriter(sw));
							String exceptionAsString = sw.toString();
							printDialog(exceptionAsString);
						}
					

					}
					}
				} catch (Exception e) {
				
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionAsString = sw.toString();
					printDialog(exceptionAsString);
				}
			}
		});

		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) playTree.getCellRenderer();

		renderer.setClosedIcon(closedIcon);
		renderer.setOpenIcon(openIcon);
		renderer.setLeafIcon(leafIcon);

		return tabNo;
	}

	public void printDialog(String msg) {
		JDialog jd = new JDialog();
		JTextPane jt = new JTextPane();
		jt.setText(msg);
		jt.setBounds(10, 10, 300, 100);
		jd.getContentPane().add(jt);
		jd.setBounds(100, 100, 500, 200);
		jd.setVisible(true);
	}

	public String getCurrentLoggerFileName() {
		if (logger != null) {
			org.apache.logging.log4j.core.Logger loggerImpl = (org.apache.logging.log4j.core.Logger) logger;
			Appender appender = loggerImpl.getAppenders().get("reproducibilityAppender");

			return ((FileAppender) appender).getFileName();
		} else {
			return "";
		}
	}

	public void expandAllNodes(JTree tree) {
		int j = tree.getRowCount();
		int i = 0;
		while (i < j) {
			tree.expandRow(i);
			i += 1;
			j = tree.getRowCount();
		}
	}

	public void autoSaveLog(int tabNo) {

		FileWriter fw = null;
		try {
			PlaybackTabData currentTabData = allTabsInfo.get(tabNo);
			String logFileName = currentTabData.getLogFileName();

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			ArrayList<ActionProperties> ap = new ArrayList<ActionProperties>();

			for (ActionProperties act : allTabsInfo.get(tabNo).getActionObjects()) {
				if (act != null) {
					ap.add(act);
				}
			}

			ActionProperties[] apArray = ap.toArray(new ActionProperties[ap.size()]);

			fw = new FileWriter(logFileName, false);
			String outputJson = gson.toJson(apArray);
			String output = outputJson.substring(1, outputJson.length() - 2);
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
	
	
	
	public void setTableRowSize(JTable table, int rowNum, String rowString) {
		
		int numEnters = 0;
		int numChars = 0;
		for (int x = 0; x < rowString.length(); x++) {
			if (rowString.charAt(x) == '\n' || rowString.charAt(x) == '\r') {
				numEnters++;
				numChars = 0;
			}
			
			numChars++;
			
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
	
	
}

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

class MultilineTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	JComponent component = new JTextArea();

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex,
			int vColIndex) {

		((JTextArea) component).setText((String) value);
		((JTextArea) component).setSize(table.getColumnModel().getColumn(vColIndex).getWidth(),
	            Short.MAX_VALUE);

		return component;
	}

	public Object getCellEditorValue() {
		return ((JTextArea) component).getText();
	}
}