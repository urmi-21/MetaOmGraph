package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.iastate.metnet.metaomgraph.ui.ColorChooseButton;

import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class BoxPlotOpts extends JPanel {

	private final JPanel contentPanel = new JPanel();
	
	JCheckBox chckbxShowMean;
	JCheckBox chckbxNewCheckBox;
	JCheckBox chckbxShowOutliers;
	JCheckBox chckbxShowFarOutliers;
	
	
	private ColorChooseButton medianColor;
	private ColorChooseButton meanColor;
	private ColorChooseButton outColor;
	private ColorChooseButton faroutColor;
	private JSpinner faroutSizespinner;
	private JSpinner outSizespinner;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			//BoxPlotOpts dialog = new BoxPlotOpts();
			//dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			//dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public BoxPlotOpts(boolean showMean,boolean showMedian,boolean showOutliers,boolean showFarOutliers,Color mColor,Color medColor,Color outlierColor,Color faroutlierColor,int outlierSize,int faroutlierSize) {
		setBounds(100, 100, 450, 300);
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			chckbxNewCheckBox = new JCheckBox("Show median");
			if(showMedian) {
				chckbxNewCheckBox.setSelected(true);
			}
			GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
			gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
			gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxNewCheckBox.gridx = 1;
			gbc_chckbxNewCheckBox.gridy = 0;
			contentPanel.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
		}
		{
			medianColor=new ColorChooseButton(medColor, ""); 
			GridBagConstraints gbc_medianColor = new GridBagConstraints();
			gbc_medianColor.anchor = GridBagConstraints.WEST;
			gbc_medianColor.insets = new Insets(0, 0, 5, 0);
			gbc_medianColor.gridx = 3;
			gbc_medianColor.gridy = 0;
			contentPanel.add(medianColor, gbc_medianColor);
		}
		{
			chckbxShowMean = new JCheckBox("Show mean");
			if(showMean) {
				chckbxShowMean.setSelected(true);
			}
			GridBagConstraints gbc_chckbxShowMean = new GridBagConstraints();
			gbc_chckbxShowMean.anchor = GridBagConstraints.WEST;
			gbc_chckbxShowMean.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxShowMean.gridx = 1;
			gbc_chckbxShowMean.gridy = 1;
			contentPanel.add(chckbxShowMean, gbc_chckbxShowMean);
		}
		{
			meanColor=new ColorChooseButton(mColor, ""); 
			GridBagConstraints gbc_meanColor = new GridBagConstraints();
			gbc_meanColor.anchor = GridBagConstraints.WEST;
			gbc_meanColor.insets = new Insets(0, 0, 5, 0);
			gbc_meanColor.gridx = 3;
			gbc_meanColor.gridy = 1;
			contentPanel.add(meanColor, gbc_meanColor);
		}
		{
			chckbxShowOutliers = new JCheckBox("Show outliers");
			if(showOutliers) {
				chckbxShowOutliers.setSelected(true);
			}
			GridBagConstraints gbc_chckbxShowOutliers = new GridBagConstraints();
			gbc_chckbxShowOutliers.anchor = GridBagConstraints.WEST;
			gbc_chckbxShowOutliers.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxShowOutliers.gridx = 1;
			gbc_chckbxShowOutliers.gridy = 2;
			contentPanel.add(chckbxShowOutliers, gbc_chckbxShowOutliers);
		}
		{
			outSizespinner = new JSpinner();
			outSizespinner.setModel(new SpinnerNumberModel(0, 0, 10, 1));
			outSizespinner.setValue(outlierSize);
			GridBagConstraints gbc_outSizespinner = new GridBagConstraints();
			gbc_outSizespinner.insets = new Insets(0, 0, 5, 5);
			gbc_outSizespinner.gridx = 2;
			gbc_outSizespinner.gridy = 2;
			contentPanel.add(outSizespinner, gbc_outSizespinner);
		}
		{
			outColor=new ColorChooseButton(outlierColor, ""); 
			GridBagConstraints gbc_outColor = new GridBagConstraints();
			gbc_outColor.anchor = GridBagConstraints.WEST;
			gbc_outColor.insets = new Insets(0, 0, 5, 0);
			gbc_outColor.gridx = 3;
			gbc_outColor.gridy = 2;
			contentPanel.add(outColor, gbc_outColor);
		}
		{
			chckbxShowFarOutliers = new JCheckBox("Show far outliers");
			if(showFarOutliers) {
				chckbxShowFarOutliers.setSelected(true);;
			}
			
			GridBagConstraints gbc_chckbxShowFarOutliers = new GridBagConstraints();
			gbc_chckbxShowFarOutliers.anchor = GridBagConstraints.WEST;
			gbc_chckbxShowFarOutliers.insets = new Insets(0, 0, 0, 5);
			gbc_chckbxShowFarOutliers.gridx = 1;
			gbc_chckbxShowFarOutliers.gridy = 3;
			contentPanel.add(chckbxShowFarOutliers, gbc_chckbxShowFarOutliers);
		}
		{
			faroutSizespinner = new JSpinner();
			faroutSizespinner.setModel(new SpinnerNumberModel(0, 0, 10, 1));
			faroutSizespinner.setValue(faroutlierSize);
			GridBagConstraints gbc_faroutSizespinner = new GridBagConstraints();
			gbc_faroutSizespinner.insets = new Insets(0, 0, 0, 5);
			gbc_faroutSizespinner.gridx = 2;
			gbc_faroutSizespinner.gridy = 3;
			contentPanel.add(faroutSizespinner, gbc_faroutSizespinner);
		}
		{
			faroutColor=new ColorChooseButton(faroutlierColor, ""); 
			GridBagConstraints gbc_faroutColorColor = new GridBagConstraints();
			gbc_faroutColorColor.anchor = GridBagConstraints.WEST;
			gbc_faroutColorColor.gridx = 3;
			gbc_faroutColorColor.gridy = 3;
			contentPanel.add(faroutColor, gbc_faroutColorColor);
		}
	}
	
	public boolean getShowMean() {
		if(chckbxShowMean.isSelected()) {
			return true;
		}
		return false;
	}
	
	public boolean getShowMedian() {
		if(chckbxNewCheckBox.isSelected()) {
			return true;
		}
		return false;
	}
	
	public boolean getShowOutliers() {
		if(chckbxShowOutliers.isSelected()) {
			return true;
		}
		return false;
	}
	
	public boolean getShowFarOutliers() {
		if(chckbxShowFarOutliers.isSelected()) {
			return true;
		}
		return false;
	}
	
	public Color getMeanColor() {
		return meanColor.getColor();
	}
	
	public Color getMedianColor() {
		return medianColor.getColor();
	}
	
	public Color getOutColor() {
		return outColor.getColor();
	}
	
	public Color getFarOutColor() {
		return faroutColor.getColor();
	}
	
	public int getOutlierSize() {
		return Integer.parseInt(outSizespinner.getValue().toString());
	}
	
	public int getFarOutlierSize() {
		return Integer.parseInt(faroutSizespinner.getValue().toString());
	}

}
