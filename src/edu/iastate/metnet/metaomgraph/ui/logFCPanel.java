package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JPanel;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Insets;
import java.util.Arrays;

import javax.swing.JButton;

public class logFCPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	JComboBox geneList;
	JComboBox groupID;
	JComboBox estimateFn;
	
	public logFCPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		initPanel();
		
		JLabel lblSelectValues = new JLabel("Select values");
		GridBagConstraints gbc_lblSelectValues = new GridBagConstraints();
		gbc_lblSelectValues.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectValues.gridx = 2;
		gbc_lblSelectValues.gridy = 0;
		add(lblSelectValues, gbc_lblSelectValues);
		
		JLabel lblSelectFeatureList = new JLabel("Select feature list");
		GridBagConstraints gbc_lblSelectFeatureList = new GridBagConstraints();
		gbc_lblSelectFeatureList.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectFeatureList.gridx = 1;
		gbc_lblSelectFeatureList.gridy = 1;
		add(lblSelectFeatureList, gbc_lblSelectFeatureList);
		
		JLabel lblSelectGrp = new JLabel("Select grouping");
		GridBagConstraints lblSelectGrp_GBC = new GridBagConstraints();
		lblSelectGrp_GBC.insets = new Insets(0, 0, 5, 0);
		lblSelectGrp_GBC.gridx = 4;
		lblSelectGrp_GBC.gridy = 1;
		add(lblSelectGrp, lblSelectGrp_GBC);
		
		//geneList = new JComboBox();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		add(geneList, gbc_comboBox);
		
		//groupID = new JComboBox();
		GridBagConstraints gbc_geneList = new GridBagConstraints();
		gbc_geneList.insets = new Insets(0, 0, 5, 0);
		gbc_geneList.fill = GridBagConstraints.HORIZONTAL;
		gbc_geneList.gridx = 4;
		gbc_geneList.gridy = 2;
		add(groupID, gbc_geneList);
		
		

	}
	
	private void initPanel() {
		geneList=new JComboBox(MetaOmGraph.getActiveProject().getGeneListNames());		
		String[] fields = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataHeaders();
		String[] fields2 = new String[fields.length + 1];
		for (int i = 0; i < fields.length; i++) {
			fields2[i] = fields[i];
		}
		fields2[fields2.length - 1] = "By Query";
		//fields2[fields2.length - 1] = "More...";
		groupID=new JComboBox(fields2);
		
	}
	
	public String getselectedGeneList() {
		return geneList.getSelectedItem().toString();
	}

	public String getselectedGrpID() {
		return groupID.getSelectedItem().toString();
	}
}
