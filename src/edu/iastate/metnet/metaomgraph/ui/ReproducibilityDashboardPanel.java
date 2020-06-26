package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import java.awt.Insets;
import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JScrollBar;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;

import java.awt.SystemColor;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.google.gson.Gson;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.logging.LoggingTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ScrollPaneConstants;

public class ReproducibilityDashboardPanel extends JPanel {

	/*Harsha- Added logger */

	private static final Logger logger = MetaOmGraph.logger;

	JButton commentButton;
	private JLabel lblNewLabel;
	private final JButton btnSubmit = new JButton("submit");
	private JSeparator separator;
	private JPanel panel;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JSeparator separator_1;
	private JPanel panel_1;
	private JLabel lblNewLabel_1;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;
	private JTabbedPane tabbedPane;
	private JSeparator separator_2;
	private JSplitPane splitPane;
	private JRadioButton rdbtnPermanentlySwitchedOff;
	private JLabel lblNewLabel_2;

	private MetaOmGraph project;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JTable table;
	private JTree playTree;

	private ActionProperties [] APFromGson;
	private HashMap<Integer,DefaultMutableTreeNode> treeStructure;
	
	public ReproducibilityDashboardPanel(MetaOmGraph myself) {

		project = myself;
		
		treeStructure = new HashMap<Integer,DefaultMutableTreeNode>();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{280, 220, 0, 0};
		gridBagLayout.rowHeights = new int[]{47, 20, 0, 40, 33, 13, 0, 90, 13, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.control);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
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
		
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            //MetaOmGraph.getReproducibilityLogMenu().setText("logging on");
	        	MetaOmGraph.getReproducibilityLogMenu().setForeground(Color.BLUE);
	        	LoggerContext context = (LoggerContext) LogManager.getContext(false);
	    		Configuration configuration = context.getConfiguration();
	    		LoggerConfig loggerConfig = configuration.getLoggerConfig("reproducibilityLogger"); 
	    		loggerConfig.setLevel(Level.DEBUG);
	    		context.updateLoggers();

	        }
	    });
	    
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
//	        	 MetaOmGraph.getReproducibilityLogMenu().setText("logging off");
//	        	 MetaOmGraph.getReproducibilityLogMenu().setForeground(Color.BLACK);
	        	MetaOmGraph.getReproducibilityLogMenu().setForeground(Color.BLACK);
	        	LoggerContext context = (LoggerContext) LogManager.getContext(false);
	    		Configuration configuration = context.getConfiguration();
	    		LoggerConfig loggerConfig = configuration.getLoggerConfig("reproducibilityLogger"); 
	    		loggerConfig.setLevel(Level.OFF);
	    		context.updateLoggers();

	        }
	    });
	    
		rdbtnPermanentlySwitchedOff.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
//	        	 MetaOmGraph.getReproducibilityLogMenu().setText("logging off");
//	        	 MetaOmGraph.getReproducibilityLogMenu().setForeground(Color.BLACK);

	        }
	    });

		G.add(rdbtnNewRadioButton);
		G.add(rdbtnNewRadioButton_1);
		G.add(rdbtnPermanentlySwitchedOff);
		
		

		separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 2;
		gbc_separator_1.insets = new Insets(0, 0, 5, 5);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 1;
		add(separator_1, gbc_separator_1);

		lblNewLabel = new JLabel("Add a comment to the current session");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		add(lblNewLabel, gbc_lblNewLabel);
		GridBagConstraints gbc_btnSubmit = new GridBagConstraints();
		gbc_btnSubmit.anchor = GridBagConstraints.WEST;
		gbc_btnSubmit.fill = GridBagConstraints.VERTICAL;
		gbc_btnSubmit.insets = new Insets(0, 0, 5, 5);
		gbc_btnSubmit.gridx = 0;
		gbc_btnSubmit.gridy = 4;
		btnSubmit.setForeground(Color.BLACK);
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//Harsha - reproducibility log
				HashMap<String,Object> dataParameters = new HashMap<String,Object>();
				dataParameters.put("comment",textArea.getText());

				HashMap<String,Object> result = new HashMap<String,Object>();
				result.put("result", "OK");
				ActionProperties userCommentAction = new ActionProperties("user-comment",null,dataParameters,result,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
				userCommentAction.logActionProperties(logger);

				lblNewLabel_2.setText("Comment logged Successfully");
				textArea.setText("");
			}
		});

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		add(scrollPane, gbc_scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		btnSubmit.setBackground(Color.BLUE);
		add(btnSubmit, gbc_btnSubmit);

		lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setForeground(Color.BLUE);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 4;
		add(lblNewLabel_2, gbc_lblNewLabel_2);

		separator_2 = new JSeparator();
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.gridwidth = 2;
		gbc_separator_2.insets = new Insets(0, 0, 5, 5);
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 5;
		add(separator_2, gbc_separator_2);

		panel = new JPanel();
		panel.setBackground(SystemColor.inactiveCaptionBorder);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.WEST;
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 6;
		add(panel, gbc_panel);

		btnNewButton = new JButton("open previous session");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("open previous session log");
				jfc.setCurrentDirectory(myself.getActiveProject().getSourceFile());
				int retValue = jfc.showOpenDialog(myself.getMainWindow());

				if(retValue == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					readLogAndPopulateTree(file);
				}
			}
		});
		panel.add(btnNewButton);

		btnNewButton_1 = new JButton("play");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						playTree.getLastSelectedPathComponent();
				
				

				if (node == null)
					//Nothing is selected.     
					return;

				Object nodeInfo = node.getUserObject();

				LoggingTreeNode ltn = (LoggingTreeNode) nodeInfo;
				
				
				if(ltn.getNodeName().equalsIgnoreCase("line-chart")) {
	
					int val2[] = null;
					
					Map<String,String> genes = (Map<String,String>)APFromGson[ltn.getNodeNumber()].getDataParameters().get("selectedGenes");
					
					Set<String> colnum = genes.keySet();
					
					Integer [] val = new Integer[colnum.size()];
					
					
					
					
					try {
					int j = 0;
				      for (String i: colnum) {
				         val[j++] = Integer.parseInt(i);
				      }
				      
					}
					catch(Exception e) {
						JDialog jd = new JDialog();
						JTextPane jt = new JTextPane();
						jt.setText(e.getMessage());
						jt.setBounds(10, 10, 300, 100);
						jd.getContentPane().add(jt);
						jd.setBounds(100, 100, 500, 200);
						jd.setVisible(true);
					}
					
					val2 = new int[val.length];
					
					
					
					for (int i=0;i<val.length;i++) {
						val2[i] = val[i];
					}
					
					
					
//					JDialog jd = new JDialog();
//					JTextPane jt = new JTextPane();
//					jt.setText(val2[0]+" "+val2[1]);
//					jt.setBounds(10, 10, 300, 100);
//					jd.getContentPane().add(jt);
//					jd.setBounds(100, 100, 500, 200);
//					jd.setVisible(true);
					
					MetaOmTablePanel mp = new MetaOmTablePanel(myself.activeProject);
					mp.graphLoggedRows(val2);
					
					
				}
				
			}
		});
		btnNewButton_1.setIcon(new ImageIcon(project.getClass().getResource("/resource/loggingicons/play_alt-512.png")));
		panel.add(btnNewButton_1);

//		btnNewButton_2 = new JButton("play entire session");
//		btnNewButton_2.setIcon(new ImageIcon(project.getClass().getResource("/resource/loggingicons/playall.png")));
//		btnNewButton_2.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//			}
//		});
//		//btnNewButton_2.setIcon(new ImageIcon(project.getClass().getResource("/resource/loggingicons/openlogbutton.png")));
//		panel.add(btnNewButton_2);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.gridwidth = 3;
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 5);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 7;
		add(tabbedPane, gbc_tabbedPane);

		splitPane = new JSplitPane();
		tabbedPane.addTab("New tab", null, splitPane, null);
		Icon closedIcon = new ImageIcon(project.getClass().getResource("/resource/loggingicons/chart.png"));
		Icon openIcon = new ImageIcon(project.getClass().getResource("/resource/loggingicons/chart.png"));
		Icon leafIcon = new ImageIcon(project.getClass().getResource("/resource/loggingicons/chart.png"));
		splitPane.setDividerLocation(0.5);
		playTree = new JTree();
		playTree.setModel(new DefaultTreeModel( new DefaultMutableTreeNode("CurrentSession")
				));

		playTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {

				
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						playTree.getLastSelectedPathComponent();

				if (node == null)
					//Nothing is selected.     
					return;

				Object nodeInfo = node.getUserObject();

				LoggingTreeNode ltn = (LoggingTreeNode) nodeInfo;
				
				

				Object [][] tableObj = new Object[100][2];
				tableObj[0][0] = "<html><b>Action Command</b></html>";
				tableObj[0][1] = APFromGson[ltn.getNodeNumber()].getActionCommand();
				
				int num = 2;
				int num1 = 2;
				int num2 = 2;
				if(APFromGson[ltn.getNodeNumber()].getActionParameters() != null && APFromGson[ltn.getNodeNumber()].getActionParameters().size() > 0) {
				tableObj[2][0] = "<html><b>Action Parameters</b></html>";
				num = 3;
				for (Map.Entry<String,Object> entry : APFromGson[ltn.getNodeNumber()].getActionParameters().entrySet()) {
					tableObj[num][0]=entry.getKey();
					
					if (entry.getValue() instanceof List<?>){
						List<String> actionList = (List<String>) entry.getValue();
						for(int i=0;i<actionList.size();i++) {
							tableObj[num][1]=actionList.get(i);
							num++;
						}
					}
					else if (entry.getValue() instanceof Map<?,?>){
						Map<?,?> actionMap = (Map<?,?>) entry.getValue();
						for(Map.Entry<?, ?> entry1 : actionMap.entrySet()) {
							tableObj[num][1] = entry1.getKey() + " : " + entry1.getValue();
							num++;
						}
					}
					else if(entry.getValue() instanceof String[]) {
						String [] actionArray = (String[]) entry.getValue();
						for(int i=0;i<actionArray.length;i++) {
							tableObj[num][1]=actionArray[i];
							num++;
						}
					}
					else {
					tableObj[num][1]=entry.getValue();
					num++;
					}
					
				}
				}
				
				num1 = num+2;
				if(APFromGson[ltn.getNodeNumber()].getDataParameters()!= null && APFromGson[ltn.getNodeNumber()].getDataParameters().size() > 0) {
				tableObj[num+1][0] = "<html><b>Data Parameters</b></html>";
				
				for (Map.Entry<String,Object> entry : APFromGson[ltn.getNodeNumber()].getDataParameters().entrySet()) {
					tableObj[num1][0]=entry.getKey();
					
					if (entry.getValue() instanceof List<?>){
						List<String> actionList = (List<String>) entry.getValue();
						for(int i=0;i<actionList.size();i++) {
							tableObj[num1][1]=actionList.get(i);
							num1++;
						}
					}
					else if (entry.getValue() instanceof Map<?,?>){
						Map<?,?> actionMap = (Map<?,?>) entry.getValue();
						for(Map.Entry<?, ?> entry1 : actionMap.entrySet()) {
							tableObj[num1][1] = entry1.getValue();
							num1++;
						}
					}
					else if(entry.getValue() instanceof String[]) {
						String [] actionArray = (String[]) entry.getValue();
						for(int i=0;i<actionArray.length;i++) {
							tableObj[num1][1]=actionArray[i];
							num1++;
						}
					}
					else {
					tableObj[num1][1]=entry.getValue();
					num1++;
					}
				}
				}
				

//				JDialog jd = new JDialog();
//				JTextPane jt = new JTextPane();
//				jt.setText("CLICKED");
//				jt.setBounds(10, 10, 300, 100);
//				jd.getContentPane().add(jt);
//				jd.setBounds(100, 100, 500, 200);
//				jd.setVisible(true);
				
				if(APFromGson[ltn.getNodeNumber()].getOtherParameters()!= null && APFromGson[ltn.getNodeNumber()].getOtherParameters().size() > 0) {
				tableObj[num1+2][0] = "<html><b>Other Parameters</b></html>";
				num2 = num1+2;
				for (Map.Entry<String,Object> entry : APFromGson[ltn.getNodeNumber()].getOtherParameters().entrySet()) {
					tableObj[num2][0]=entry.getKey();
					
					if (entry.getValue() instanceof List<?>){
						List<String> actionList = (List<String>) entry.getValue();
						for(int i=0;i<actionList.size();i++) {
							tableObj[num2][1]=actionList.get(i);
							num2++;
						}
					}
					else if (entry.getValue() instanceof Map<?,?>){
						Map<?,?> actionMap = (Map<?,?>) entry.getValue();
						for(Map.Entry<?, ?> entry1 : actionMap.entrySet()) {
							tableObj[num2][1] = entry1.getKey() + " : " + entry1.getValue();
							num2++;
						}
					}
					else if(entry.getValue() instanceof String[]) {
						String [] actionArray = (String[]) entry.getValue();
						for(int i=0;i<actionArray.length;i++) {
							tableObj[num2][1]=actionArray[i];
							num2++;
						}
					}
					else {
					tableObj[num2][1]=entry.getValue();
					num2++;
					}
					num2++;
				}
				}
				
				tableObj[num2+1][0] = "<html><b>Timestamp</b></html>";
				tableObj[num2+1][1] = APFromGson[ltn.getNodeNumber()].getTimestamp();
				
				
				

				table.setModel(new DefaultTableModel(
						tableObj,
						new String[] {
								"Parameter", "Value"
						}
						));
				
				
				



			}
		});

		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) playTree.getCellRenderer();

		renderer.setClosedIcon(closedIcon);
		renderer.setOpenIcon(openIcon);
		renderer.setLeafIcon(leafIcon);

		scrollPane_1 = new JScrollPane(playTree);
		splitPane.setLeftComponent(scrollPane_1);

		

		table = new JTable();
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setFillsViewportHeight(true);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{null, ""},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
			},
			new String[] {
				"Parameter", "Value"
			}
		));
		table.getColumnModel().getColumn(0).setMaxWidth(200);
		table.getColumnModel().getColumn(1).setMaxWidth(200);
		
		scrollPane_2 = new JScrollPane(table);
		splitPane.setRightComponent(scrollPane_2);

		splitPane.setDividerLocation(0.5);

		separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 0, 5);
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridwidth = 2;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 8;
		add(separator, gbc_separator);

		commentButton = new JButton();  
		commentButton.setText("Submit"); 
		commentButton.setVisible(true);

	}



	public void addActionToLogTree(ActionProperties action, Integer parent, int actionNumber) {


		DefaultTreeModel dtm = (DefaultTreeModel) playTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new LoggingTreeNode(action.getActionCommand(),actionNumber));
		
		if(parent == -1) {
			dtm.insertNodeInto(newNode, root, root.getChildCount());	
		}
		else {
			DefaultMutableTreeNode parentNode = treeStructure.get(parent);
			dtm.insertNodeInto(newNode, parentNode, parentNode.getChildCount());

		}
		
		treeStructure.put(action.getActionNumber(),newNode);
		dtm.reload();
	}

	
	
	public void readLogAndPopulateTree(File logFile) {

		Gson gson = new Gson();
		String jsonLog = "";
		try {
			jsonLog = new String( Files.readAllBytes(logFile.toPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		playTree.removeAll();
		playTree.setModel(new DefaultTreeModel( new DefaultMutableTreeNode(logFile.getName())));

		
		String actions = jsonLog.substring(jsonLog.indexOf('}') + 1);
		String listOfActions = "["+actions.substring(actions.indexOf(',') + 1)+"]";

		APFromGson = gson.fromJson(listOfActions, ActionProperties[].class);



		for(int action=0; action<APFromGson.length; action++) {

			double par = Double.parseDouble(APFromGson[action].getActionParameters().get("parent").toString());
			
			Integer parent = (int) par;
			addActionToLogTree(APFromGson[action],parent,action);
			tabbedPane.setTitleAt(0, logFile.getName());


		}


	}



}
