package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SetProgramParameters extends JInternalFrame {
	private JTextField textField;
	private JComboBox<Integer> comboBox;
	private JComboBox comboBox_1;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SetProgramParameters frame = new SetProgramParameters();
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
	public SetProgramParameters() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int thisThreads = Integer.parseInt(comboBox.getSelectedItem().toString());
				int thisPerms = 0;
				boolean flag = false;
				while (!flag) {
					try {
						thisPerms = Integer.parseInt(textField.getText().trim());
						flag = true;
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null,
								"Please enter a valid integer in the number of permutations field", "Error",
								JOptionPane.ERROR_MESSAGE);
					}

				}

				MetaOmGraph.setNumPermutations(thisPerms);
				MetaOmGraph.setNumThreads(thisThreads);
				MetaOmGraph.setpathtoRscrips(textField_2.getText().trim());
				MetaOmGraph.setUserRPath(textField_1.getText().trim());
				MetaOmGraph.getActiveProject().getMetadataHybrid().setDefaultRepCol(comboBox_1.getSelectedItem().toString());
				dispose();

			}
		});
		JPanel buttonPanel=new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnDefault = new JButton("Default");
		btnDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textField.setText("100");
				MetaOmGraph.useDefaultRPath();
				textField_1.setText(MetaOmGraph.getRPath());
				textField_2.setText("");
			}
		});
		buttonPanel.add(btnDefault);
		buttonPanel.add(btnOk);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblSetParameters = new JLabel("Set Parameters");
		panel.add(lblSetParameters);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[][][][grow]", "[][][][][][][]"));

		JLabel lblNewLabel = new JLabel("Number of permutations");
		panel_1.add(lblNewLabel, "cell 1 1");

		textField = new JTextField();
		textField.setText(String.valueOf(MetaOmGraph.getNumPermutations()));
		panel_1.add(textField, "cell 3 1,growx");
		textField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Number of threads");
		panel_1.add(lblNewLabel_1, "cell 1 3");

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
				"11", "12", "13", "14", "15", "16" }));
		comboBox.setSelectedIndex(MetaOmGraph.getNumThreads()-1);
		panel_1.add(comboBox, "cell 3 3,growx");
		
		JLabel lblPathToRscript = new JLabel("Path to \"Rscript\"");
		panel_1.add(lblPathToRscript, "cell 1 4");
		
		textField_1 = new JTextField();
		panel_1.add(textField_1, "cell 3 4,growx");
		textField_1.setColumns(20);
		textField_1.setText(MetaOmGraph.getRPath());
		JLabel lblPathToFolder = new JLabel("Path to folder containing R scripts to make plots");
		panel_1.add(lblPathToFolder, "cell 1 5");
		
		textField_2 = new JTextField();
		panel_1.add(textField_2, "cell 3 5,growx");
		textField_2.setColumns(20);
		textField_2.setText(MetaOmGraph.getpathtoRscrips());
		
		JLabel lblDefaultReplicateGroup = new JLabel("Default replicate group column");
		panel_1.add(lblDefaultReplicateGroup, "cell 1 6");
		
		comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel<>(MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataHeaders(true)));
		panel_1.add(comboBox_1, "cell 3 6,growx");

		// frame properties
		this.setClosable(true);
		//pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

	}

}
