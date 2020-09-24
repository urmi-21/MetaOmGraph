package edu.iastate.metnet.metaomgraph.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

public class AboutFrame extends TaskbarInternalFrame {
	JPanel polyPanel;
	JPanel background;

	/**
	 * last digit of version 1 alpha 2 beta 3 release candidate 4 final
	 */
	public static String getLabelText() {
		return "<html><p align=\"center\"><b>MetaOmGraph" + System.getProperty("MOG.version")
		// + "<br>" + "This version was built on Aug 10th 2018"
				+ "<br>" + System.getProperty("MOG.date") + "<br>" + "Wurtele Lab, GDCB, Iowa State University<br>"
				+ "Send questions, comments, bug reports, and feature requests to:<br>" + "usingh@iastate.edu<br><br>"
				+ "For more information on the MetNet project, please visit<br>"
				+ "http://metnetweb.gdcb.iastate.edu<br><br><br></b></p>"
				+ "<p align=\"left\"><b>This program uses code from the following projects:<br>"
				+ "BrowserLauncher2 (http://sourceforge.net/projects/browserlaunch2/)<br>"
				+ "Jakarta POI (http://jakarta.apache.org/poi/)<br>" + "JDOM (http://www.jdom.org)<br>"
				+ "JFreeChart (http://www.jfree.org/jfreechart/)<br>"
				+ "L2FProd Common Components (http://common.l2fprod.com/)<br>"
				+ "Nitrite Database (https://www.dizitart.org/nitrite-database.html)<br><br>"
				+ "Biologists: Eve Syrkin Wurtele, Wieslawa Mentzen, Ling Li, Jianling Peng<br>"
				+ "Bioinformaticists: Jonathan Hurst, Yaping Feng <br>" 
				+ "Developers: Urminder Singh, Nick Ransom, Kumara Sri Harsha Vajjhala, Kaliki Sumanth Kumar, Yusuf Shehata<br><br><br>"
				+ "Published Paper : Urminder Singh, Manhoi Hur, Karin Dorman, Eve Syrkin Wurtele,<br> MetaOmGraph: a workbench for interactive exploratory data analysis of large expression datasets,<br> Nucleic Acids Research, Volume 48, Issue 4, 28 February 2020, Page e23, https://doi.org/10.1093/nar/gkz1209"
				+ "</b></p></html>";
	}

	public AboutFrame() {
		polyPanel = new PolygonPanel(2, 3);
		polyPanel.setLayout(new BoxLayout(polyPanel, 1));

		polyPanel.add(Box.createVerticalGlue());
		JPanel shadowPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setComposite(AlphaComposite.getInstance(10, 0.5F));
				super.paintComponent(g2d);
			}
		};
		shadowPanel.setBackground(Color.BLACK);
		shadowPanel.setFocusable(true);
		shadowPanel.addKeyListener(new KeyAdapter() {
			private StringBuffer buffer;
			private String password = "hesoyam";

			@Override
			public void keyTyped(KeyEvent e) {
				if (buffer == null) {
					buffer = new StringBuffer();
				}
				buffer.append(e.getKeyChar());
				if (buffer.toString().endsWith(password)) {
					dispose();
					MetaOmGraph.showNewAboutFrame();
				}
				if (!password.contains(e.getKeyChar() + "")) {
					buffer = null;
				} else if (buffer.length() > password.length()) {
					buffer = new StringBuffer(buffer.substring(1));
				}

			}
		});
		JLabel myLabel = new JLabel(getLabelText());
		myLabel.setHorizontalTextPosition(0);
		myLabel.setForeground(Color.WHITE);
		shadowPanel.add(myLabel);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, 0));

		centerPanel.add(Box.createHorizontalGlue());
		centerPanel.add(shadowPanel);
		centerPanel.add(Box.createHorizontalGlue());
		centerPanel.setOpaque(false);
		polyPanel.add(centerPanel);

		polyPanel.add(Box.createVerticalGlue());
		try {
			background = new ScrollingTexturePanel(getClass().getResourceAsStream("/resource/misc/metnet.gif"), 300, 5);
			background.setLayout(new BorderLayout());
			background.add(polyPanel, "Center");
			background.setOpaque(true);
			polyPanel.setOpaque(false);
			getContentPane().add(background);
		} catch (IOException e) {
			getContentPane().add(polyPanel);
			e.printStackTrace();
		}

		polyPanel.setPreferredSize(
				new Dimension(shadowPanel.getPreferredSize().width + 100, shadowPanel.getPreferredSize().height + 100));
		// allow closing on click
		MouseAdapter ml = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				// Put JFrame close code here
				if (event.getClickCount() >= 2) {
					dispose();
					if (MetaOmGraph.getActiveProject() == null) {
						try {
							MetaOmGraph.showWelcomeDialog();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};
		polyPanel.addMouseListener(ml);
		setTitle("About MetaOmGraph");
		
		FrameModel aboutFrameModel = new FrameModel("About","About MetaOmGraph",0);
		setModel(aboutFrameModel);
		
		pack();

	}
}
