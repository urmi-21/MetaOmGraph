/**
 * 
 */
package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * @author sumanth
 * Displays the thridparty libraries
 */
public class ThirdPartyLibs extends JPanel{
	
	private JPanel rangePanel;
	private JPanel rangeViewport;
	private JScrollPane mainScrollPane;
	
	/**
	 * Constructor
	 */
	public ThirdPartyLibs() {
		setLayout(new BorderLayout());
		rangePanel = new JPanel();
		rangePanel.setLayout(new BoxLayout(rangePanel, 1));
		
		rangeViewport = new JPanel(new GridBagLayout());
		
		addHeaderPanel();
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// JFreeChart
		String name = "JFreeChart";
		String websiteUrl = "https://www.jfree.org/jfreechart/";
		String licenseUrl = "https://github.com/jfree/jfreechart/blob/master/licence-LGPL.txt";
		addThirdPartyLibrary(name, websiteUrl, licenseUrl);
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// ApacheCommonsMath
		name = "Apache Commons Math";
		websiteUrl = "https://commons.apache.org/proper/commons-math/";
		licenseUrl = "https://github.com/apache/commons-math/blob/master/LICENSE";
		addThirdPartyLibrary(name, websiteUrl, licenseUrl);
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// Nitrite Database
		name = "Nitrite Database";
		websiteUrl = "https://www.dizitart.org/nitrite-database.html";
		licenseUrl = "https://github.com/nitrite/nitrite-java/blob/develop/LICENSE.md";
		addThirdPartyLibrary(name, websiteUrl, licenseUrl);
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// Colorbrewer
		name = "Colorbrewer";
		websiteUrl = "https://github.com/rcsb/colorbrewer";
		licenseUrl = "https://github.com/rcsb/colorbrewer/blob/master/LICENSE";
		addThirdPartyLibrary(name, websiteUrl, licenseUrl);
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// nd4j
		name = "nd4j";
		websiteUrl = "https://github.com/deeplearning4j/nd4j";
		licenseUrl = "https://github.com/deeplearning4j/nd4j/blob/master/LICENSE";
		addThirdPartyLibrary(name, websiteUrl, licenseUrl);
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// lejon
		name = "Lejon Tsne";
		websiteUrl = "http://lejon.github.io/";
		licenseUrl = "http://lejon.github.io/";
		addThirdPartyLibrary(name, websiteUrl, licenseUrl);
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// R-logo
		websiteUrl = "https://www.r-project.org/logo/";
		licenseUrl = "https://creativecommons.org/licenses/by-sa/4.0/";
		addThirdPartyLogo(MetaOmGraph.getIconTheme().getRIcon(), websiteUrl, licenseUrl);
		rangePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		rangeViewport.add(rangePanel);
		mainScrollPane = new JScrollPane(rangeViewport);
		add(mainScrollPane);
	}
	
	private void addHeaderPanel() {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new GridLayout(1, 5));
		
		JLabel libraryLabel = new JLabel();
		libraryLabel.setText("<html><b>Library</b></html>");
		
		JLabel websiteLabel = new JLabel();
		websiteLabel.setText("<html><b>Website</b></html>");
		
		JLabel licenseLabel = new JLabel();
		licenseLabel.setText("<html><b>License</b></html>");
		
		headerPanel.add(libraryLabel);
		headerPanel.add(new JLabel("         "));
		headerPanel.add(new JSeparator(SwingConstants.VERTICAL));
		headerPanel.add(websiteLabel);
		headerPanel.add(new JSeparator(SwingConstants.VERTICAL));
		headerPanel.add(licenseLabel);
		
		rangePanel.add(headerPanel);
	}

	private void addThirdPartyLibrary(String name, String websiteUrl, String licenseUrl) {
		JPanel rowPanel = new JPanel();
		rowPanel.setLayout(new GridLayout(1, 5));
		
		JLabel libLabel = new JLabel(name);
		
		JLabel siteLabel = new JLabel();
		siteLabel.setText("<html><a href = \"\">website</a></html>");
		siteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		siteLabel.setHorizontalTextPosition(0);
		siteLabel.setForeground(Color.WHITE);
		siteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	try {
					java.awt.Desktop.getDesktop().browse(new URI(websiteUrl));
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
		
		JLabel licenseLabel = new JLabel();
		licenseLabel.setText("<html><a href = \"\">license</a></html>");
		licenseLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		licenseLabel.setHorizontalTextPosition(0);
		licenseLabel.setForeground(Color.WHITE);
		licenseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	try {
					java.awt.Desktop.getDesktop().browse(new URI(licenseUrl));
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
		
		rowPanel.add(libLabel);
		rowPanel.add(new JLabel("         "));
		rowPanel.add(new JSeparator(SwingConstants.VERTICAL));
		rowPanel.add(siteLabel);
		rowPanel.add(new JSeparator(SwingConstants.VERTICAL));
		rowPanel.add(licenseLabel);
		
		rangePanel.add(rowPanel);
	}
	
	private void addThirdPartyLogo(Icon thirdPartyImage, String websiteUrl, String licenseUrl) {
		JPanel rowPanel = new JPanel();
		rowPanel.setLayout(new GridLayout(1, 5));
		
		JLabel libLabel = new JLabel(thirdPartyImage);
		
		JLabel siteLabel = new JLabel();
		siteLabel.setText("<html><a href = \"\">website</a></html>");
		siteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		siteLabel.setHorizontalTextPosition(0);
		siteLabel.setForeground(Color.WHITE);
		siteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	try {
					java.awt.Desktop.getDesktop().browse(new URI(websiteUrl));
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
		
		JLabel licenseLabel = new JLabel();
		licenseLabel.setText("<html><a href = \"\">license</a></html>");
		licenseLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		licenseLabel.setHorizontalTextPosition(0);
		licenseLabel.setForeground(Color.WHITE);
		licenseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	try {
					java.awt.Desktop.getDesktop().browse(new URI(licenseUrl));
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
		
		rowPanel.add(libLabel);
		rowPanel.add(new JLabel("         "));
		rowPanel.add(new JSeparator(SwingConstants.VERTICAL));
		rowPanel.add(siteLabel);
		rowPanel.add(new JSeparator(SwingConstants.VERTICAL));
		rowPanel.add(licenseLabel);
		
		rangePanel.add(rowPanel);
	}
}
