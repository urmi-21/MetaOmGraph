package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.utils.MOGColorThemes;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.event.ActionEvent;

public class ColorProperties extends JInternalFrame {

	private ColorChooseButton tabCol1Button;
	private ColorChooseButton tabCol2Button;
	private ColorChooseButton tabSelButton;
	private ColorChooseButton tabHighlightButton;
	private ColorChooseButton tabHyprlnkButton;
	private ColorChooseButton chartBckButton;
	private ColorChooseButton plotBckButton;
	private boolean themeEdited;
	private JComboBox comboBox;
	
	private String _defaulttheme="light";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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
		// setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		// setSize(600, 600);
		//
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(btnCancel);

		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!themeEdited) {
					String selTheme=comboBox.getSelectedItem().toString();
					applyTheme(MetaOmGraph.getTheme(selTheme));
				} else {
					saveTheme();
				}

			}
		});
		panel.add(btnApply);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!themeEdited) {
					String selTheme=comboBox.getSelectedItem().toString();
					applyTheme(MetaOmGraph.getTheme(selTheme));
				} else {
					saveTheme();
				}

				dispose();
			}
		});
		panel.add(btnOk);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selTheme=comboBox.getSelectedItem().toString();
				if(selTheme.equals("light") || selTheme.equals("dark")) {
					JOptionPane.showMessageDialog(ColorProperties.this, "Can't remove default themes", "Can't remove selected theme", JOptionPane.INFORMATION_MESSAGE);
				}else {
					MetaOmGraph.removeTheme(selTheme);	
					applyTheme(MetaOmGraph.getTheme(_defaulttheme));					
					initcomboBox();
				}
				
			}
		});
		panel.add(btnRemove);

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
		// gbc_lblLoadPreset.gridheight=2;
		panel_1.add(lblLoadPreset, gbc_lblLoadPreset);

		initButtons(MetaOmGraph.getTheme(MetaOmGraph.getCurrentThemeName()));
		comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selectedTheme = comboBox.getSelectedItem().toString();
				// JOptionPane.showMessageDialog(null, "sel theme:"+selectedTheme);
				changeButtons(MetaOmGraph.getTheme(selectedTheme));
			}
		});
		
		initcomboBox();
		
		

		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 4;
		gbc_comboBox.gridy = 1;
		// gbc_comboBox.gridheight=2;
		panel_1.add(comboBox, gbc_comboBox);

		JLabel lblTableBackground = new JLabel("Table background color 1");
		lblTableBackground.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableBackground = new GridBagConstraints();
		gbc_lblTableBackground.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableBackground.gridx = 1;
		gbc_lblTableBackground.gridy = 2;
		// gbc_lblTableBackground.gridheight=2;
		panel_1.add(lblTableBackground, gbc_lblTableBackground);

		GridBagConstraints gbc_tabCol1Button = new GridBagConstraints();
		gbc_tabCol1Button.insets = new Insets(0, 0, 5, 0);
		gbc_tabCol1Button.gridx = 4;
		gbc_tabCol1Button.gridy = 2;
		// gbc_tabCol1Button.gridheight=2;
		gbc_tabCol1Button.fill = GridBagConstraints.BOTH;
		panel_1.add(tabCol1Button, gbc_tabCol1Button);

		JLabel lblTableBackground_1 = new JLabel("Table background color 2");
		lblTableBackground_1.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableBackground_1 = new GridBagConstraints();
		gbc_lblTableBackground_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableBackground_1.gridx = 1;
		gbc_lblTableBackground_1.gridy = 3;
		// gbc_lblTableBackground_1.gridheight = 2;
		panel_1.add(lblTableBackground_1, gbc_lblTableBackground_1);
		GridBagConstraints gbc_tabCol2Button = new GridBagConstraints();
		gbc_tabCol2Button.insets = new Insets(0, 0, 5, 0);
		gbc_tabCol2Button.gridx = 4;
		gbc_tabCol2Button.gridy = 3;
		// gbc_tabCol2Button.gridheight = 2;
		gbc_tabCol2Button.fill = GridBagConstraints.BOTH;
		panel_1.add(tabCol2Button, gbc_tabCol2Button);

		JLabel lblTableSelection = new JLabel("Table selection");
		lblTableSelection.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableSelection = new GridBagConstraints();
		gbc_lblTableSelection.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableSelection.gridx = 1;
		gbc_lblTableSelection.gridy = 4;
		// gbc_lblTableSelection.gridheight = 2;
		gbc_lblTableSelection.fill = GridBagConstraints.BOTH;
		panel_1.add(lblTableSelection, gbc_lblTableSelection);

		GridBagConstraints gbc_tabSelButton = new GridBagConstraints();
		gbc_tabSelButton.insets = new Insets(0, 0, 5, 0);
		gbc_tabSelButton.gridx = 4;
		gbc_tabSelButton.gridy = 4;
		// gbc_tabSelButton.gridheight = 2;
		gbc_tabSelButton.fill = GridBagConstraints.BOTH;
		panel_1.add(tabSelButton, gbc_tabSelButton);

		JLabel lblTableHighlight = new JLabel("Table highlight");
		lblTableHighlight.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableHighlight = new GridBagConstraints();
		gbc_lblTableHighlight.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableHighlight.gridx = 1;
		gbc_lblTableHighlight.gridy = 5;
		// gbc_lblTableHighlight.gridheight = 2;
		gbc_lblTableHighlight.fill = GridBagConstraints.BOTH;
		panel_1.add(lblTableHighlight, gbc_lblTableHighlight);

		GridBagConstraints gbc_tabHighlightButton = new GridBagConstraints();
		gbc_tabHighlightButton.insets = new Insets(0, 0, 5, 0);
		gbc_tabHighlightButton.gridx = 4;
		gbc_tabHighlightButton.gridy = 5;
		// gbc_tabHighlightButton.gridheight = 2;
		gbc_tabHighlightButton.fill = GridBagConstraints.BOTH;
		panel_1.add(tabHighlightButton, gbc_tabHighlightButton);

		JLabel lblTableHyperlink = new JLabel("Table hyperlink");
		lblTableHyperlink.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTableHyperlink = new GridBagConstraints();
		gbc_lblTableHyperlink.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableHyperlink.gridx = 1;
		gbc_lblTableHyperlink.gridy = 6;
		// gbc_lblTableHyperlink.gridheight = 2;
		panel_1.add(lblTableHyperlink, gbc_lblTableHyperlink);

		GridBagConstraints gbc_colorChooseButton = new GridBagConstraints();
		gbc_colorChooseButton.insets = new Insets(0, 0, 5, 0);
		gbc_colorChooseButton.gridx = 4;
		gbc_colorChooseButton.gridy = 6;
		// gbc_colorChooseButton.gridheight = 2;
		gbc_colorChooseButton.fill = GridBagConstraints.BOTH;
		panel_1.add(tabHyprlnkButton, gbc_colorChooseButton);

		JLabel lblNewLabel = new JLabel("Chart background");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 7;
		// gbc_lblNewLabel.gridheight = 2;
		panel_1.add(lblNewLabel, gbc_lblNewLabel);

		GridBagConstraints gbc_chartBckButton = new GridBagConstraints();
		gbc_chartBckButton.insets = new Insets(0, 0, 5, 0);
		gbc_chartBckButton.gridx = 4;
		gbc_chartBckButton.gridy = 7;
		// gbc_chartBckButton.gridheight = 2;
		gbc_chartBckButton.fill = GridBagConstraints.BOTH;
		panel_1.add(chartBckButton, gbc_chartBckButton);

		JLabel lblPlotBackground = new JLabel("Plot background");
		lblPlotBackground.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblPlotBackground = new GridBagConstraints();
		gbc_lblPlotBackground.insets = new Insets(0, 0, 0, 5);
		gbc_lblPlotBackground.gridx = 1;
		gbc_lblPlotBackground.gridy = 8;
		// gbc_lblPlotBackground.gridheight = 2;
		panel_1.add(lblPlotBackground, gbc_lblPlotBackground);

		GridBagConstraints gbc_plotBckButton = new GridBagConstraints();
		gbc_plotBckButton.gridx = 4;
		gbc_plotBckButton.gridy = 8;
		// gbc_plotBckButton.gridheight = 2;
		gbc_plotBckButton.fill = GridBagConstraints.BOTH;
		panel_1.add(plotBckButton, gbc_plotBckButton);

	}

	public void updateTheme(MOGColorThemes theme) {
		theme.setTableColor1(tabCol1Button.getColor());
		theme.setTableColor2(tabCol2Button.getColor());
		theme.setTableSelectionColor(tabSelButton.getColor());
		theme.setTableHighlightColor(tabHighlightButton.getColor());
		theme.setTableHyperlinkColor(tabHyprlnkButton.getColor());
		theme.setChartBackgroundColor(chartBckButton.getColor());
		theme.setPlotBackgroundColor(plotBckButton.getColor());
	}

	private void editTheme() {
		this.themeEdited = true;
	}

	private void initButtons(MOGColorThemes theme) {
		this.themeEdited = false;
		tabCol1Button = new ColorChooseButton(theme.getTableColor1(), "Table background color 1");
		tabCol1Button.setColor(theme.getTableColor1());

		tabCol1Button.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				editTheme();
			}

		});

		tabCol2Button = new ColorChooseButton(theme.getTableColor2(), "Table background color 2");
		tabCol2Button.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				editTheme();
			}

		});

		tabSelButton = new ColorChooseButton(theme.getTableSelectionColor(), "Table selection color");
		tabSelButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				editTheme();
			}

		});

		tabHyprlnkButton = new ColorChooseButton(theme.getTableHyperlinkColor(), "Table hyperlink color");
		tabHyprlnkButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				editTheme();
			}

		});
		tabHighlightButton = new ColorChooseButton(theme.getTableHighlightColor(), "Table highlight color");
		tabHighlightButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				editTheme();
			}

		});

		chartBckButton = new ColorChooseButton(theme.getChartBackgroundColor(), "Chart background color");
		chartBckButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				editTheme();
			}

		});

		plotBckButton = new ColorChooseButton(theme.getPlotBackgroundColor(), "Plot background color");
		plotBckButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				editTheme();
			}

		});

	}

	private void changeButtons(MOGColorThemes theme) {
		this.themeEdited = false;
		tabCol1Button.setColor(theme.getTableColor1());
		tabCol2Button.setColor(theme.getTableColor2());
		tabSelButton.setColor(theme.getTableSelectionColor());
		tabHyprlnkButton.setColor(theme.getTableHyperlinkColor());
		tabHighlightButton.setColor(theme.getTableHighlightColor());
		chartBckButton.setColor(theme.getChartBackgroundColor());
		plotBckButton.setColor(theme.getPlotBackgroundColor());
		revalidate();
		repaint();
	}

	private void saveTheme() {
		// if currently selected themes are light, default then create a new theme
		// if theme is not a default theme overwrite or save as new theme
		String selTheme = comboBox.getSelectedItem().toString();
		
		boolean overWrite = false;
		MOGColorThemes thisTheme = null;
		if (themeEdited) {
			if ( !(selTheme.equals("light") || selTheme.equals("dark")) ) {
				overWrite = true;
			}
			if (overWrite) {
				// display overwrite theme option
				int n = JOptionPane.showConfirmDialog(ColorProperties.this,
						"Would you like to overwrite to the existing theme", "An Inane Question",
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					overWrite = true;
				} else {
					overWrite = false;
				}
			}

			if (overWrite) {
				// overWrite
				thisTheme = MetaOmGraph.getTheme(selTheme);
				

			} else {
				// display save as new theme option
				String newName = JOptionPane.showInputDialog(ColorProperties.this, "Enter a name for new theme",
						"Please enter a name", JOptionPane.OK_OPTION);
				if(newName==null || newName.equals("")) {
					return;
				}
				thisTheme = new MOGColorThemes(newName);
				
				// save theme to MOG
				MetaOmGraph.addTheme(thisTheme);
				// reload combobox
				
			}

		}

		updateTheme(thisTheme);
		applyTheme(thisTheme);
		initcomboBox();
	}

	private void applyTheme(MOGColorThemes newTheme) {
		MetaOmGraph.setCurrentTheme(newTheme.getThemeName());
		MetaOmGraph.getActiveTable().getMetadataTableDisplay().updateColors();
		MetaOmGraph.getActiveTable().getMetadataTreeDisplay().updateColors();
		MetaOmGraph.getActiveTable().getStripedTable().updateColors();
	}

	private void initcomboBox() {
		comboBox.setModel(new DefaultComboBoxModel(MetaOmGraph.getAllThemeNames()));
		comboBox.setSelectedItem(MetaOmGraph.getCurrentThemeName());
		//revalidate();
		//repaint();
	}

}
