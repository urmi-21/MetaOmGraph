/**
 * 
 */
package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.ColorChooseButton;

/**
 * @author sumanth
 * HeatMapChartProperties class to set the properties of heatmap like font color, font style
 */
public class HeatMapChartProperties extends JDialog{
	private ColorChooseButton colorChoserButton;
	private JComboBox<String> fontComboBox;
	private Font font;
	private Color color;
	private boolean valuesChanged = false;
	
	/**
	 * Constructor
	 * @param textFont current text font
	 * @param textColor current text color
	 */
	HeatMapChartProperties(Font textFont, Color textColor){
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle("Properties");
		setLocationRelativeTo(MetaOmGraph.getDesktop());
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
	    c.anchor = GridBagConstraints.WEST;
	    c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		JLabel fontName = new JLabel("Font:");
		add(fontName, c);
		c.gridx = 1;
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fontComboBox = new JComboBox<String>(fontNames);
		fontComboBox.setSelectedItem(textFont.getName());
		add(fontComboBox, c);
		c.gridx = 0;
		c.gridy = 1;
		JLabel colorLabel = new JLabel("Color:");
		add(colorLabel, c);
		c.gridx = 1;
		colorChoserButton = new ColorChooseButton(textColor, "Font color");
		colorChoserButton.setActionCommand("choose color");
		colorChoserButton.setToolTipText("Click to chose the font color");
		add(colorChoserButton, c);
		
		c.gridx = 0;
        c.gridy = 2;
        JButton okButton = new JButton("Ok");
        add(okButton, c);
        
        c.gridx = 1;
        JButton cancelButton = new JButton("Cancel");
        add(cancelButton, c);
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Font selectedFont = Font.decode((String)fontComboBox.getSelectedItem());
				Color selectedColor = colorChoserButton.getColor();
				if(selectedFont != textFont || selectedColor != textColor) {
					font = selectedFont;
					color = selectedColor;
					valuesChanged = true;
				}
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				valuesChanged = false;
				dispose();
			}
		});
		
        setResizable(true);
        pack();
        setVisible(true);
        toFront();
	}
	
	/**
	 * 
	 * @return valuesChanged, If the user didn't change any values, return false, else true
	 */
	public boolean isValuesChanged() {
		return valuesChanged;
	}
	
	/**
	 * 
	 * @return Font, get the user selected font
	 * It is recommended to first call isValuesChanged() and check if the values are changed
	 */
	public Font getSelectedFont() {
		return font;
	}
	
	/**
	 * 
	 * @return Color, get the user selected font color
	 * It is recommended to first call isValuesChanged() and check if the values are changed
	 */
	public Color getSelectedFontColor() {
		return color;
	}
}
