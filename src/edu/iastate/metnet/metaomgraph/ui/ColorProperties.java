package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import javax.swing.JButton;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.SwingConstants;

public class ColorProperties extends JInternalFrame {

	private ColorChooseButton tabCol1Button;
	private ColorChooseButton tabCol2Button;
	private ColorChooseButton tabSelButton;
	private ColorChooseButton tabHighlightButton;
	private ColorChooseButton tabHyprlnkButton;
	private ColorChooseButton chartBckButton;
	private ColorChooseButton plotBckButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ColorProperties frame = new ColorProperties();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ColorProperties() {

		// frame properties
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		//setSize(300, 200);
		//
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnCancel = new JButton("Cancel");
		panel.add(btnCancel);

		JButton btnApply = new JButton("Apply");
		panel.add(btnApply);

		JButton btnOk = new JButton("OK");
		panel.add(btnOk);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JLabel lblLoadPreset = new JLabel("Load Preset");
		lblLoadPreset.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblLoadPreset = new GridBagConstraints();
		gbc_lblLoadPreset.insets = new Insets(0, 0, 5, 5);
		gbc_lblLoadPreset.gridx = 1;
		gbc_lblLoadPreset.gridy = 1;
		gbc_lblLoadPreset.gridheight=2;
		panel_1.add(lblLoadPreset, gbc_lblLoadPreset);

		JComboBox comboBox = new JComboBox();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 4;
		gbc_comboBox.gridy = 1;
		gbc_comboBox.gridheight=2;
		panel_1.add(comboBox, gbc_comboBox);

		JLabel lblTableBackground = new JLabel("Table background1");
		lblTableBackground.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableBackground = new GridBagConstraints();
		gbc_lblTableBackground.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableBackground.gridx = 1;
		gbc_lblTableBackground.gridy = 2;
		gbc_lblTableBackground.gridheight=2;
		panel_1.add(lblTableBackground, gbc_lblTableBackground);

		tabCol1Button = new ColorChooseButton(MetaOmGraph.getTableColor1(), "Table background color 1");
		tabCol1Button.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

			}

		});
		GridBagConstraints gbc_tabCol1Button = new GridBagConstraints();
		gbc_tabCol1Button.insets = new Insets(0, 0, 5, 0);
		gbc_tabCol1Button.gridx = 4;
		gbc_tabCol1Button.gridy = 2;
		gbc_tabCol1Button.gridheight=2;
		gbc_tabCol1Button.fill = GridBagConstraints.BOTH;
		panel_1.add(tabCol1Button, gbc_tabCol1Button);

		tabCol2Button = new ColorChooseButton(MetaOmGraph.getTableColor2(), "Table background color 2");
		tabCol2Button.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

			}

		});

		JLabel lblTableBackground_1 = new JLabel("Table background color 2");
		lblTableBackground_1.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableBackground_1 = new GridBagConstraints();
		gbc_lblTableBackground_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableBackground_1.gridx = 1;
		gbc_lblTableBackground_1.gridy = 3;
		gbc_lblTableBackground_1.gridheight = 2;
		panel_1.add(lblTableBackground_1, gbc_lblTableBackground_1);
		GridBagConstraints gbc_tabCol2Button = new GridBagConstraints();
		gbc_tabCol2Button.insets = new Insets(0, 0, 5, 0);
		gbc_tabCol2Button.gridx = 4;
		gbc_tabCol2Button.gridy = 3;
		gbc_tabCol2Button.gridheight = 2;
		gbc_tabCol2Button.fill = GridBagConstraints.BOTH;
		panel_1.add(tabCol2Button, gbc_tabCol2Button);

		JLabel lblTableSelection = new JLabel("Table selection");
		lblTableSelection.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableSelection = new GridBagConstraints();
		gbc_lblTableSelection.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableSelection.gridx = 1;
		gbc_lblTableSelection.gridy = 4;
		gbc_lblTableSelection.gridheight = 2;
		gbc_lblTableSelection.fill = GridBagConstraints.BOTH;
		panel_1.add(lblTableSelection, gbc_lblTableSelection);

		tabSelButton = new ColorChooseButton(MetaOmGraph.getTableSelectionColor(), "Table selection color");
		tabSelButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

			}

		});
		GridBagConstraints gbc_tabSelButton = new GridBagConstraints();
		gbc_tabSelButton.insets = new Insets(0, 0, 5, 0);
		gbc_tabSelButton.gridx = 4;
		gbc_tabSelButton.gridy = 4;
		gbc_tabSelButton.gridheight = 2;
		gbc_tabSelButton.fill = GridBagConstraints.BOTH;
		panel_1.add(tabSelButton, gbc_tabSelButton);

		JLabel lblTableHighlight = new JLabel("Table highlight");
		lblTableHighlight.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableHighlight = new GridBagConstraints();
		gbc_lblTableHighlight.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableHighlight.gridx = 1;
		gbc_lblTableHighlight.gridy = 5;
		gbc_lblTableHighlight.gridheight = 2;
		gbc_lblTableHighlight.fill = GridBagConstraints.BOTH;
		panel_1.add(lblTableHighlight, gbc_lblTableHighlight);

		tabHighlightButton = new ColorChooseButton(MetaOmGraph.getTableHighlightColor(), "Table highlight color");
		tabHighlightButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

			}

		});
		GridBagConstraints gbc_tabHighlightButton = new GridBagConstraints();
		gbc_tabHighlightButton.insets = new Insets(0, 0, 5, 0);
		gbc_tabHighlightButton.gridx = 4;
		gbc_tabHighlightButton.gridy = 5;
		gbc_tabHighlightButton.gridheight = 2;
		gbc_tabHighlightButton.fill = GridBagConstraints.BOTH;
		panel_1.add(tabHighlightButton, gbc_tabHighlightButton);

		JLabel lblTableHyperlink = new JLabel("Table hyperlink");
		lblTableHyperlink.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableHyperlink = new GridBagConstraints();
		gbc_lblTableHyperlink.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableHyperlink.gridx = 1;
		gbc_lblTableHyperlink.gridy = 6;
		gbc_lblTableHyperlink.gridheight = 2;
		panel_1.add(lblTableHyperlink, gbc_lblTableHyperlink);

		tabHyprlnkButton = new ColorChooseButton(MetaOmGraph.getTableHyperlinkColor(), "Table hyperlink color");
		GridBagConstraints gbc_colorChooseButton = new GridBagConstraints();
		gbc_colorChooseButton.insets = new Insets(0, 0, 5, 0);
		gbc_colorChooseButton.gridx = 4;
		gbc_colorChooseButton.gridy = 6;
		gbc_colorChooseButton.gridheight = 2;
		gbc_colorChooseButton.fill = GridBagConstraints.BOTH;
		panel_1.add(tabHyprlnkButton, gbc_colorChooseButton);

		JLabel lblNewLabel = new JLabel("Chart background");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 7;
		gbc_lblNewLabel.gridheight = 2;
		panel_1.add(lblNewLabel, gbc_lblNewLabel);

		chartBckButton = new ColorChooseButton(MetaOmGraph.getChartBackgroundColor(), "Chart background color");
		chartBckButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

			}

		});
		GridBagConstraints gbc_chartBckButton = new GridBagConstraints();
		gbc_chartBckButton.insets = new Insets(0, 0, 5, 0);
		gbc_chartBckButton.gridx = 4;
		gbc_chartBckButton.gridy = 7;
		gbc_chartBckButton.gridheight = 2;
		gbc_chartBckButton.fill = GridBagConstraints.BOTH;
		panel_1.add(chartBckButton, gbc_chartBckButton);

		JLabel lblPlotBackground = new JLabel("Plot background");
		lblPlotBackground.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblPlotBackground = new GridBagConstraints();
		gbc_lblPlotBackground.insets = new Insets(0, 0, 0, 5);
		gbc_lblPlotBackground.gridx = 1;
		gbc_lblPlotBackground.gridy = 8;
		gbc_lblPlotBackground.gridheight = 2;
		panel_1.add(lblPlotBackground, gbc_lblPlotBackground);

		plotBckButton = new ColorChooseButton(MetaOmGraph.getPlotBackgroundColor(), "Plot background color");
		plotBckButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

			}

		});
		GridBagConstraints gbc_plotBckButton = new GridBagConstraints();
		gbc_plotBckButton.gridx = 4;
		gbc_plotBckButton.gridy = 8;
		gbc_plotBckButton.gridheight = 2;
		gbc_plotBckButton.fill = GridBagConstraints.BOTH;
		panel_1.add(plotBckButton, gbc_plotBckButton);

	}

}
