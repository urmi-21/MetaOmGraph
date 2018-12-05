package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.ComponentOrientation;
import java.awt.Component;
import javax.swing.border.LineBorder;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import javax.swing.border.EtchedBorder;
import java.awt.Rectangle;
import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;

import com.sun.javafx.embed.swing.Disposer;

public class WelcomePanelWin10 extends JPanel {

	private JPanel panel_2;
	private List<JButton> btnList;

	/**
	 * Create the panel.
	 */

	public WelcomePanelWin10() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		add(panel, BorderLayout.NORTH);

		JLabel lblWelcomeToMetaomgraph = new JLabel("Welcome to MetaOmGraph");
		lblWelcomeToMetaomgraph.setForeground(Color.WHITE);
		lblWelcomeToMetaomgraph.setFont(new Font("Segoe UI", Font.BOLD, 20));
		panel.add(lblWelcomeToMetaomgraph);

		JPanel panel_1 = new JPanel();
		panel_1.setForeground(Color.WHITE);
		panel_1.setBackground(Color.BLACK);
		add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JButton btnQuickStartGuide = new JButton("Quick start guide");
		btnQuickStartGuide.setContentAreaFilled(false);
		btnQuickStartGuide.setBorder(null);
		btnQuickStartGuide.setBorderPainted(false);
		btnQuickStartGuide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				URI thisURI = null;
				try {
					thisURI = new URI("http://metnetweb.gdcb.iastate.edu/MetaOmGraph/help/newhelp/quickstart.php");
					java.awt.Desktop.getDesktop().browse(thisURI);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		JButton btnNewButton = new JButton("From delimited file");
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setAlignmentY(Component.TOP_ALIGNMENT);
		btnNewButton.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		btnNewButton.setMargin(new Insets(0, 0, 0, 0));
		btnNewButton.setInheritsPopupMenu(true);
		btnNewButton.setIgnoreRepaint(true);
		btnNewButton.setIconTextGap(0);
		btnNewButton.setBorder(null);
		btnNewButton.setBackground(new Color(0, 102, 235));
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnNewButton.setPreferredSize(new Dimension(220, 80));
		btnNewButton.setMaximumSize(new Dimension(89, 89));
		btnNewButton.setOpaque(true);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// open new project dialog
				MetaOmGraph.startNewFromDelimited();
				

			}
		});

		JLabel lblCreateANew = new JLabel("New project");
		lblCreateANew.setForeground(Color.WHITE);
		lblCreateANew.setFont(new Font("Segoe UI", Font.BOLD, 18));
		GridBagConstraints gbc_lblCreateANew = new GridBagConstraints();
		gbc_lblCreateANew.insets = new Insets(0, 0, 5, 5);
		gbc_lblCreateANew.gridx = 1;
		gbc_lblCreateANew.gridy = 1;
		panel_1.add(lblCreateANew, gbc_lblCreateANew);

		JLabel lblRecentProjects = new JLabel("Recent projects");
		lblRecentProjects.setForeground(Color.WHITE);
		lblRecentProjects.setFont(new Font("Segoe UI", Font.BOLD, 18));
		GridBagConstraints gbc_lblRecentProjects = new GridBagConstraints();
		gbc_lblRecentProjects.gridwidth = 3;
		gbc_lblRecentProjects.insets = new Insets(0, 0, 5, 5);
		gbc_lblRecentProjects.gridx = 6;
		gbc_lblRecentProjects.gridy = 1;
		panel_1.add(lblRecentProjects, gbc_lblRecentProjects);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 2;
		panel_1.add(btnNewButton, gbc_btnNewButton);

		// panel to show recent projects
		panel_2 = new JPanel();
		panel_2.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		panel_2.setBackground(Color.BLACK);
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridheight = 6;
		gbc_panel_2.gridwidth = 3;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 6;
		gbc_panel_2.gridy = 2;
		panel_1.add(panel_2, gbc_panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		initRecentProjects();
		addRecentBtns();

		JLabel lblGetHelp = new JLabel("Get help");
		lblGetHelp.setForeground(Color.WHITE);
		lblGetHelp.setFont(new Font("Segoe UI", Font.BOLD, 18));
		GridBagConstraints gbc_lblGetHelp = new GridBagConstraints();
		gbc_lblGetHelp.gridwidth = 2;
		gbc_lblGetHelp.insets = new Insets(0, 0, 5, 5);
		gbc_lblGetHelp.gridx = 1;
		gbc_lblGetHelp.gridy = 3;
		panel_1.add(lblGetHelp, gbc_lblGetHelp);
		btnQuickStartGuide.setPreferredSize(new Dimension(110, 80));

		btnQuickStartGuide.setMaximumSize(new Dimension(89, 89));
		btnQuickStartGuide.setMargin(new Insets(0, 0, 0, 0));
		btnQuickStartGuide.setInheritsPopupMenu(true);
		btnQuickStartGuide.setIgnoreRepaint(true);
		btnQuickStartGuide.setIconTextGap(0);
		btnQuickStartGuide.setForeground(Color.WHITE);
		btnQuickStartGuide.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnQuickStartGuide.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		btnQuickStartGuide.setBackground(new Color(0, 102, 235));
		btnQuickStartGuide.setOpaque(true);
		btnQuickStartGuide.setAlignmentY(0.0f);
		GridBagConstraints gbc_btnQuickStartGuide = new GridBagConstraints();
		gbc_btnQuickStartGuide.insets = new Insets(0, 0, 5, 5);
		gbc_btnQuickStartGuide.gridx = 1;
		gbc_btnQuickStartGuide.gridy = 4;
		panel_1.add(btnQuickStartGuide, gbc_btnQuickStartGuide);

		JButton btnHelp_1 = new JButton("Overview");
		btnHelp_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				URI thisURI = null;
				try {
					thisURI = new URI("http://metnetweb.gdcb.iastate.edu/MetaOmGraph/help/newhelp/quickstart.php");
					java.awt.Desktop.getDesktop().browse(thisURI);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnHelp_1.setPreferredSize(new Dimension(110, 80));

		btnHelp_1.setMaximumSize(new Dimension(89, 89));
		btnHelp_1.setMargin(new Insets(0, 0, 0, 0));
		btnHelp_1.setInheritsPopupMenu(true);
		btnHelp_1.setIgnoreRepaint(true);
		btnHelp_1.setIconTextGap(0);
		btnHelp_1.setForeground(Color.WHITE);
		btnHelp_1.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnHelp_1.setContentAreaFilled(false);
		btnHelp_1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		btnHelp_1.setBorder(null);
		btnHelp_1.setBackground(new Color(0, 102, 235));
		btnHelp_1.setOpaque(true);
		btnHelp_1.setAlignmentY(0.0f);
		GridBagConstraints gbc_btnHelp_1 = new GridBagConstraints();
		gbc_btnHelp_1.anchor = GridBagConstraints.WEST;
		gbc_btnHelp_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnHelp_1.gridx = 2;
		gbc_btnHelp_1.gridy = 4;
		panel_1.add(btnHelp_1, gbc_btnHelp_1);

		JButton btnHelp = new JButton("Downloads");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				URI thisURI = null;
				try {
					thisURI = new URI("http://metnetweb.gdcb.iastate.edu/MetNet_MetaOmGraph.htm");
					java.awt.Desktop.getDesktop().browse(thisURI);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnHelp.setPreferredSize(new Dimension(110, 80));

		btnHelp.setMaximumSize(new Dimension(89, 89));
		btnHelp.setMargin(new Insets(0, 0, 0, 0));
		btnHelp.setInheritsPopupMenu(true);
		btnHelp.setIgnoreRepaint(true);
		btnHelp.setIconTextGap(0);
		btnHelp.setForeground(Color.WHITE);
		btnHelp.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnHelp.setContentAreaFilled(false);
		btnHelp.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		btnHelp.setBorder(null);
		btnHelp.setBackground(new Color(0, 102, 235));
		btnHelp.setOpaque(true);
		btnHelp.setAlignmentY(0.0f);
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnHelp.gridx = 1;
		gbc_btnHelp.gridy = 5;
		panel_1.add(btnHelp, gbc_btnHelp);

		JButton btnHelp_2 = new JButton("GitHub");
		btnHelp_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				URI thisURI = null;
				try {
					thisURI = new URI("https://github.com/urmi-21/MetaOmGraph");
					java.awt.Desktop.getDesktop().browse(thisURI);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnHelp_2.setPreferredSize(new Dimension(110, 80));
		btnHelp_2.setMaximumSize(new Dimension(89, 89));
		btnHelp_2.setMargin(new Insets(0, 0, 0, 0));
		btnHelp_2.setInheritsPopupMenu(true);
		btnHelp_2.setIgnoreRepaint(true);
		btnHelp_2.setIconTextGap(0);
		btnHelp_2.setForeground(Color.WHITE);
		btnHelp_2.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnHelp_2.setContentAreaFilled(false);
		btnHelp_2.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		btnHelp_2.setBorder(null);
		btnHelp_2.setBackground(new Color(0, 102, 235));
		btnHelp_2.setOpaque(true);
		btnHelp_2.setAlignmentY(0.0f);
		GridBagConstraints gbc_btnHelp_2 = new GridBagConstraints();
		gbc_btnHelp_2.insets = new Insets(0, 0, 5, 5);
		gbc_btnHelp_2.gridx = 2;
		gbc_btnHelp_2.gridy = 5;
		panel_1.add(btnHelp_2, gbc_btnHelp_2);

		JButton btnNewButton_1 = new JButton("About");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MetaOmGraph.showAboutFrame();
				MetaOmGraph.closeWelcomeDialog();
			}
		});
		btnNewButton_1.setContentAreaFilled(false);
		btnNewButton_1.setBorderPainted(false);
		btnNewButton_1.setPreferredSize(new Dimension(110, 40));
		btnNewButton_1.setForeground(Color.WHITE);
		btnNewButton_1.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnNewButton_1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		btnNewButton_1.setBorder(null);
		btnNewButton_1.setBackground(new Color(255, 99, 71));
		btnNewButton_1.setOpaque(true);

		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 8;
		panel_1.add(btnNewButton_1, gbc_btnNewButton_1);

		JButton btnBut = new JButton("Cite");
		btnBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Please cite as...");
			}
		});
		btnBut.setPreferredSize(new Dimension(110, 40));
		btnBut.setForeground(Color.WHITE);
		btnBut.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnBut.setContentAreaFilled(false);
		btnBut.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		btnBut.setBorderPainted(false);
		btnBut.setBorder(null);
		btnBut.setBackground(new Color(255, 99, 71));
		btnBut.setOpaque(true);
		GridBagConstraints gbc_btnBut = new GridBagConstraints();
		gbc_btnBut.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnBut.insets = new Insets(0, 0, 0, 5);
		gbc_btnBut.gridx = 2;
		gbc_btnBut.gridy = 8;
		panel_1.add(btnBut, gbc_btnBut);

		JButton btnBut_1 = new JButton("Exit MetaOmGraph");
		btnBut_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MetaOmGraph.shutdown();
				return;
			}
		});
		btnBut_1.setPreferredSize(new Dimension(220, 40));

		btnBut_1.setForeground(Color.WHITE);
		btnBut_1.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnBut_1.setContentAreaFilled(false);
		btnBut_1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		btnBut_1.setBorderPainted(false);
		btnBut_1.setBorder(null);
		btnBut_1.setBackground(new Color(255, 99, 71));
		btnBut_1.setOpaque(true);

		GridBagConstraints gbc_btnBut_1 = new GridBagConstraints();
		gbc_btnBut_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnBut_1.gridwidth = 3;
		gbc_btnBut_1.gridx = 6;
		gbc_btnBut_1.gridy = 8;
		panel_1.add(btnBut_1, gbc_btnBut_1);

		/*
		 * pos to add clickable labels GridBagConstraints gbc_dtrpnFdssfdsfdFdsfsdfsd =
		 * new GridBagConstraints(); gbc_dtrpnFdssfdsfdFdsfsdfsd.gridheight = 7;
		 * gbc_dtrpnFdssfdsfdFdsfsdfsd.gridwidth = 4; gbc_dtrpnFdssfdsfdFdsfsdfsd.insets
		 * = new Insets(0, 0, 5, 5); gbc_dtrpnFdssfdsfdFdsfsdfsd.fill =
		 * GridBagConstraints.BOTH; gbc_dtrpnFdssfdsfdFdsfsdfsd.gridx = 5;
		 * gbc_dtrpnFdssfdsfdFdsfsdfsd.gridy = 2;
		 */

		setSize(600, 500);

	}

	private void initRecentProjects() {
		// get recent projects
		Collection<File> recentProjects = MetaOmGraph.getRecentProjects();
		int maxn = 6;// show last n projects
		int n = recentProjects.size();
		if (n > maxn) {
			n = maxn;
		}
		btnList = new ArrayList<>();
		int i = 0;
		for (File thisProject : recentProjects) {
			if(i>=n) break;
			JButton thisBtn = null;
			String thisFilePath = thisProject.getAbsolutePath();

			thisBtn = new JButton(thisFilePath);
			thisBtn.setToolTipText(thisFilePath);
			// add action listener
			thisBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					// add action
					MetaOmGraph.openRecentProject(thisFilePath);
				}
			});

			// set button properties
			thisBtn.setMinimumSize(new Dimension(280, 50));
			thisBtn.setMaximumSize(new Dimension(280, 50));
			thisBtn.setPreferredSize(new Dimension(280, 50));
			thisBtn.setForeground(Color.WHITE);
			thisBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
			thisBtn.setContentAreaFilled(false);
			thisBtn.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			thisBtn.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			thisBtn.setBorderPainted(true);
			thisBtn.setBackground(new Color(255, 99, 71));
			thisBtn.setBackground(new Color(0, 142, 10));
			thisBtn.setOpaque(true);
			thisBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

			btnList.add(thisBtn);
			i++;
		}

		// at the end add open another project button
		JButton thisBtn = new JButton("Open another project...");
		thisBtn.setToolTipText("Open other MOG projects");
		// add action listener
		thisBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// add action
				MetaOmGraph.openAnotherProject();
			}
		});
		// set button properties
		thisBtn.setMinimumSize(new Dimension(280, 50));
		thisBtn.setMaximumSize(new Dimension(280, 50));
		thisBtn.setPreferredSize(new Dimension(280, 50));
		thisBtn.setForeground(Color.WHITE);
		thisBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
		thisBtn.setContentAreaFilled(false);
		thisBtn.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		thisBtn.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		thisBtn.setBorderPainted(true);
		thisBtn.setBackground(new Color(255, 99, 71));
		thisBtn.setBackground(new Color(0, 142, 10));
		thisBtn.setOpaque(true);
		thisBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnList.add(thisBtn);
	}

	private void addRecentBtns() {
		for (int i = 0; i < btnList.size(); i++) {
			panel_2.add(Box.createRigidArea(new Dimension(280, 5)));
			panel_2.add(btnList.get(i));
		}
	}

}
