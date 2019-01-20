package edu.iastate.metnet.metaomgraph.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FilterCorrMetaTablePanel extends JPanel {

	/**
	 * Create the panel.
	 */
	private JTextField maxr;
	private JTextField minr;
	private JTextField minp;
	private JTextField maxp;
	private JCheckBox chckbxCorrelationValue;
	private JCheckBox chckbxPvalue;
	public FilterCorrMetaTablePanel() {
		setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 434, 0 };
		gridBagLayout.rowHeights = new int[] { 10, 227, 33, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gbl_panel_2);

		chckbxCorrelationValue = new JCheckBox("correlation value");
		chckbxCorrelationValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxCorrelationValue.isSelected()) {
					minr.setEditable(true);
					maxr.setEditable(true);
				} else {
					minr.setEditable(false);
					maxr.setEditable(false);
				}
			}
		});
		GridBagConstraints gbc_chckbxCorrelationValue = new GridBagConstraints();
		gbc_chckbxCorrelationValue.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxCorrelationValue.gridx = 1;
		gbc_chckbxCorrelationValue.gridy = 1;
		add(chckbxCorrelationValue, gbc_chckbxCorrelationValue);

		JLabel lblBetween = new JLabel("between");
		GridBagConstraints gbc_lblBetween = new GridBagConstraints();
		gbc_lblBetween.insets = new Insets(0, 0, 5, 5);
		gbc_lblBetween.gridx = 2;
		gbc_lblBetween.gridy = 1;
		add(lblBetween, gbc_lblBetween);

		minr = new JTextField();
		GridBagConstraints gbc_minr = new GridBagConstraints();
		gbc_minr.insets = new Insets(0, 0, 5, 5);
		gbc_minr.fill = GridBagConstraints.HORIZONTAL;
		gbc_minr.gridx = 3;
		gbc_minr.gridy = 1;
		add(minr, gbc_minr);
		minr.setColumns(10);

		JLabel lblAnd = new JLabel("and");
		GridBagConstraints gbc_lblAnd = new GridBagConstraints();
		gbc_lblAnd.insets = new Insets(0, 0, 5, 5);
		gbc_lblAnd.gridx = 4;
		gbc_lblAnd.gridy = 1;
		add(lblAnd, gbc_lblAnd);

		maxr = new JTextField();
		GridBagConstraints gbc_maxr = new GridBagConstraints();
		gbc_maxr.insets = new Insets(0, 0, 5, 5);
		gbc_maxr.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxr.gridx = 5;
		gbc_maxr.gridy = 1;
		add(maxr, gbc_maxr);
		maxr.setColumns(10);

		chckbxPvalue = new JCheckBox("p-value");
		chckbxPvalue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxPvalue.isSelected()) {
					maxp.setEditable(true);
					minp.setEditable(true);
				} else {
					maxp.setEditable(false);
					minp.setEditable(false);
				}
			}
		});
		GridBagConstraints gbc_chckbxPvalue = new GridBagConstraints();
		gbc_chckbxPvalue.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxPvalue.gridx = 1;
		gbc_chckbxPvalue.gridy = 2;
		add(chckbxPvalue, gbc_chckbxPvalue);

		JLabel lblBetween_1 = new JLabel("between");
		GridBagConstraints gbc_lblBetween_1 = new GridBagConstraints();
		gbc_lblBetween_1.anchor = GridBagConstraints.EAST;
		gbc_lblBetween_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblBetween_1.gridx = 2;
		gbc_lblBetween_1.gridy = 2;
		add(lblBetween_1, gbc_lblBetween_1);

		minp = new JTextField();
		GridBagConstraints gbc_minp = new GridBagConstraints();
		gbc_minp.insets = new Insets(0, 0, 0, 5);
		gbc_minp.fill = GridBagConstraints.HORIZONTAL;
		gbc_minp.gridx = 3;
		gbc_minp.gridy = 2;
		add(minp, gbc_minp);
		minp.setColumns(10);

		JLabel lblAnd_1 = new JLabel("and");
		GridBagConstraints gbc_lblAnd_1 = new GridBagConstraints();
		gbc_lblAnd_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblAnd_1.gridx = 4;
		gbc_lblAnd_1.gridy = 2;
		add(lblAnd_1, gbc_lblAnd_1);

		maxp = new JTextField();
		GridBagConstraints gbc_maxp = new GridBagConstraints();
		gbc_maxp.insets = new Insets(0, 0, 0, 5);
		gbc_maxp.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxp.gridx = 5;
		gbc_maxp.gridy = 2;
		add(maxp, gbc_maxp);
		maxp.setColumns(10);
		// initialize
		initFrame();
	}
	
	private void initFrame() {
		minr.setText("0.0");
		maxr.setText("1.0");
		minp.setText("0.0");
		maxp.setText("1.0");
		chckbxCorrelationValue.setSelected(true);
		chckbxPvalue.setSelected(true);

	}
	
	public double getMinp() {
		if(chckbxPvalue.isSelected()) {
			double toReturn = 0;
			// parse values
			try {
				toReturn = Double.parseDouble(minp.getText().trim());
				return toReturn;
			} catch (NumberFormatException nfe) {
				// okflag = false;
				JOptionPane.showMessageDialog(null, "Please enter valid values", "Please check the values",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return -9999;
	}
	
	public double getMaxp() {
		if(chckbxPvalue.isSelected()) {
			double toReturn = 0;
			// parse values
			try {
				toReturn = Double.parseDouble(maxp.getText().trim());
				return toReturn;
			} catch (NumberFormatException nfe) {
				// okflag = false;
				JOptionPane.showMessageDialog(null, "Please enter valid values", "Please check the values",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return 9999;
	}
	
	public double getMinr() {
		if(chckbxCorrelationValue.isSelected()) {
			double toReturn = 0;
			// parse values
			try {
				toReturn = Double.parseDouble(minr.getText().trim());
				return toReturn;
			} catch (NumberFormatException nfe) {
				// okflag = false;
				JOptionPane.showMessageDialog(null, "Please enter valid values", "Please check the values",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return -9999;
	}
	
	public double getMaxr() {
		if(chckbxCorrelationValue.isSelected()) {
			double toReturn = 0;
			// parse values
			try {
				toReturn = Double.parseDouble(maxr.getText().trim());
				return toReturn;
			} catch (NumberFormatException nfe) {
				// okflag = false;
				JOptionPane.showMessageDialog(null, "Please enter valid values", "Please check the values",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return 9999;
	}

}
