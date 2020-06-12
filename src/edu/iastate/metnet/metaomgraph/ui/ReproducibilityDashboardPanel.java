package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.Insets;
import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JScrollBar;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JEditorPane;
import java.awt.SystemColor;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReproducibilityDashboardPanel extends JPanel {
	JButton commentButton;
	private JLabel lblNewLabel;
	private final JButton btnSubmit = new JButton("submit");
	private JSeparator separator;
	private JTextArea textArea;
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
	private JTree tree;
	private JTable table;

	public ReproducibilityDashboardPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{187, 220, 0};
		gridBagLayout.rowHeights = new int[]{47, 20, 0, 55, 33, 13, 0, 90, 13, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
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
		
		rdbtnNewRadioButton = new JRadioButton("on");
		panel_1.add(rdbtnNewRadioButton);
		
		rdbtnNewRadioButton_1 = new JRadioButton("off");
		panel_1.add(rdbtnNewRadioButton_1);
		
		rdbtnPermanentlySwitchedOff = new JRadioButton("permanently switched off");
		panel_1.add(rdbtnPermanentlySwitchedOff);
		
		separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 2;
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
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
		
		textArea = new JTextArea();
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 2;
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 3;
		add(textArea, gbc_textArea);
		GridBagConstraints gbc_btnSubmit = new GridBagConstraints();
		gbc_btnSubmit.fill = GridBagConstraints.BOTH;
		gbc_btnSubmit.insets = new Insets(0, 0, 5, 5);
		gbc_btnSubmit.gridx = 0;
		gbc_btnSubmit.gridy = 4;
		btnSubmit.setBackground(SystemColor.activeCaption);
		add(btnSubmit, gbc_btnSubmit);
		
		separator_2 = new JSeparator();
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.gridwidth = 2;
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 5;
		add(separator_2, gbc_separator_2);
		
		panel = new JPanel();
		panel.setBackground(SystemColor.inactiveCaptionBorder);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 6;
		add(panel, gbc_panel);
		
		btnNewButton = new JButton("play");
		panel.add(btnNewButton);
		
		btnNewButton_1 = new JButton("play entire session");
		panel.add(btnNewButton_1);
		
		btnNewButton_2 = new JButton("open previous session");
		panel.add(btnNewButton_2);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.gridwidth = 2;
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 7;
		add(tabbedPane, gbc_tabbedPane);
		
		splitPane = new JSplitPane();
		tabbedPane.addTab("New tab", null, splitPane, null);
		
		tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("06122020") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("line-chart");
						node_1.add(new DefaultMutableTreeNode("save-to-image"));
						node_1.add(new DefaultMutableTreeNode("violet"));
						node_1.add(new DefaultMutableTreeNode("red"));
						node_1.add(new DefaultMutableTreeNode("yellow"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("box-plot");
						node_1.add(new DefaultMutableTreeNode("basketball"));
						node_1.add(new DefaultMutableTreeNode("soccer"));
						node_1.add(new DefaultMutableTreeNode("football"));
						node_1.add(new DefaultMutableTreeNode("hockey"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("filter");
						node_1.add(new DefaultMutableTreeNode("hot dogs"));
						node_1.add(new DefaultMutableTreeNode("pizza"));
						node_1.add(new DefaultMutableTreeNode("ravioli"));
						node_1.add(new DefaultMutableTreeNode("bananas"));
					add(node_1);
				}
			}
		));
		splitPane.setLeftComponent(tree);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
			},
			new String[] {
				"New column", "New column"
			}
		));
		splitPane.setRightComponent(table);
		
		separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridwidth = 2;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 8;
		add(separator, gbc_separator);
		
		commentButton = new JButton();  
		commentButton.setText("Submit"); 
		commentButton.setVisible(true);

	}

	
}
