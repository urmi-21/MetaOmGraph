package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FilterCorrelationMetaTable extends JInternalFrame {
	private JTextField maxr;
	private JTextField minr;
	private JTextField minp;
	private JTextField maxp;
	private JCheckBox chckbxCorrelationValue;
	private JCheckBox chckbxPvalue;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FilterCorrelationMetaTable frame = new FilterCorrelationMetaTable();
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
	public FilterCorrelationMetaTable() {
		setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 434, 0 };
		gridBagLayout.rowHeights = new int[] { 10, 227, 33, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.NORTH;
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		getContentPane().add(panel_1, gbc_panel_1);

		JLabel lblEnterValuesBelow = new JLabel("Enter values below");
		panel_1.add(lblEnterValuesBelow);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		getContentPane().add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

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
		panel_2.add(chckbxCorrelationValue, gbc_chckbxCorrelationValue);

		JLabel lblBetween = new JLabel("between");
		GridBagConstraints gbc_lblBetween = new GridBagConstraints();
		gbc_lblBetween.insets = new Insets(0, 0, 5, 5);
		gbc_lblBetween.gridx = 2;
		gbc_lblBetween.gridy = 1;
		panel_2.add(lblBetween, gbc_lblBetween);

		minr = new JTextField();
		GridBagConstraints gbc_minr = new GridBagConstraints();
		gbc_minr.insets = new Insets(0, 0, 5, 5);
		gbc_minr.fill = GridBagConstraints.HORIZONTAL;
		gbc_minr.gridx = 3;
		gbc_minr.gridy = 1;
		panel_2.add(minr, gbc_minr);
		minr.setColumns(10);

		JLabel lblAnd = new JLabel("and");
		GridBagConstraints gbc_lblAnd = new GridBagConstraints();
		gbc_lblAnd.insets = new Insets(0, 0, 5, 5);
		gbc_lblAnd.gridx = 4;
		gbc_lblAnd.gridy = 1;
		panel_2.add(lblAnd, gbc_lblAnd);

		maxr = new JTextField();
		GridBagConstraints gbc_maxr = new GridBagConstraints();
		gbc_maxr.insets = new Insets(0, 0, 5, 5);
		gbc_maxr.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxr.gridx = 5;
		gbc_maxr.gridy = 1;
		panel_2.add(maxr, gbc_maxr);
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
		panel_2.add(chckbxPvalue, gbc_chckbxPvalue);

		JLabel lblBetween_1 = new JLabel("between");
		GridBagConstraints gbc_lblBetween_1 = new GridBagConstraints();
		gbc_lblBetween_1.anchor = GridBagConstraints.EAST;
		gbc_lblBetween_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblBetween_1.gridx = 2;
		gbc_lblBetween_1.gridy = 2;
		panel_2.add(lblBetween_1, gbc_lblBetween_1);

		minp = new JTextField();
		GridBagConstraints gbc_minp = new GridBagConstraints();
		gbc_minp.insets = new Insets(0, 0, 0, 5);
		gbc_minp.fill = GridBagConstraints.HORIZONTAL;
		gbc_minp.gridx = 3;
		gbc_minp.gridy = 2;
		panel_2.add(minp, gbc_minp);
		minp.setColumns(10);

		JLabel lblAnd_1 = new JLabel("and");
		GridBagConstraints gbc_lblAnd_1 = new GridBagConstraints();
		gbc_lblAnd_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblAnd_1.gridx = 4;
		gbc_lblAnd_1.gridy = 2;
		panel_2.add(lblAnd_1, gbc_lblAnd_1);

		maxp = new JTextField();
		GridBagConstraints gbc_maxp = new GridBagConstraints();
		gbc_maxp.insets = new Insets(0, 0, 0, 5);
		gbc_maxp.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxp.gridx = 5;
		gbc_maxp.gridy = 2;
		panel_2.add(maxp, gbc_maxp);
		maxp.setColumns(10);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		getContentPane().add(panel, gbc_panel);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(btnCancel);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double minrVal=0;
				double maxrVal=0;
				double minpVal=0;
				double maxpVal=0;
				
				//parse values
				boolean okflag = false;
				while (!okflag) {
					try {
						minrVal = Double.parseDouble(minr.getText().trim());
						maxrVal = Double.parseDouble(maxr.getText().trim());
						minpVal = Double.parseDouble(minp.getText().trim());
						maxpVal = Double.parseDouble(maxp.getText().trim());
						
						okflag = true;
					} catch (NumberFormatException nfe) {
						okflag = false;
						JOptionPane.showMessageDialog(null,
								"Please enter a valid integer in the number of permutations field", "Error",
								JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		});
		panel.add(btnOk);

		// initialize
		initFrame();

		// frame properties
		this.setClosable(true);
		pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

	}

	private void initFrame() {
		minr.setText("0.0");
		maxr.setText("1.0");
		minp.setText("0.0");
		maxp.setText("1.0");
		chckbxCorrelationValue.setSelected(false);
		chckbxPvalue.setSelected(false);

	}

}
