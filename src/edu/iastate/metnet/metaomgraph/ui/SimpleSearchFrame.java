package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class SimpleSearchFrame extends JInternalFrame {
	private JTextField txtToSearch;
	private JPanel panel;
	private JLabel lblSimpleSearch;
	private JPanel panel_1;
	private JLabel lblSelectField;
	private JComboBox comboBox;
	private JCheckBox chckbxExactMatch;
	private JPanel panel_2;
	private JButton btnSearch;
	private JButton btnCancel;
	private MetadataTableDisplayPanel parent;
	private MetadataHybrid obj;
	private JCheckBox chckbxMatchCase;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimpleSearchFrame frame = new SimpleSearchFrame(null);
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
	public SimpleSearchFrame(MetadataTableDisplayPanel parent) {
		this.parent = parent;
		//this.obj=parent.getthisCollection();
		this.obj=MetaOmGraph.getActiveProject().getMetadataHybrid();
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panel.setBackground(Color.GRAY);
		getContentPane().add(panel, BorderLayout.NORTH);

		lblSimpleSearch = new JLabel("Simple Search");
		lblSimpleSearch.setFont(new Font("Garamond", Font.PLAIN, 14));
		panel.add(lblSimpleSearch);

		panel_1 = new JPanel();
		panel_1.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		lblSelectField = new JLabel("Select field");
		lblSelectField.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_1.add(lblSelectField);

		comboBox = new JComboBox();
		comboBox.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		// select all the headers in the metadata
		String[] colNames = parent.getHeaders();
		comboBox.addItem("Any Field");
		comboBox.addItem("All Fields");
		for (String c : colNames) {
			comboBox.addItem(c);
		}
		panel_1.add(comboBox);

		txtToSearch = new JTextField();
		txtToSearch.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		txtToSearch.setText("To Search");
		panel_1.add(txtToSearch);
		txtToSearch.setColumns(10);

		chckbxExactMatch = new JCheckBox("Exact match");
		chckbxExactMatch.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_1.add(chckbxExactMatch);
		
		chckbxMatchCase = new JCheckBox("Match case");
		chckbxMatchCase.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_1.add(chckbxMatchCase);

		panel_2 = new JPanel();
		panel_2.setBackground(Color.GRAY);
		getContentPane().add(panel_2, BorderLayout.SOUTH);

		btnCancel = new JButton("Close");
		btnCancel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel_2.add(btnCancel);

		btnSearch = new JButton("Search");
		btnSearch.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// search for query
				String toSearch = txtToSearch.getText();
				String field = comboBox.getSelectedItem().toString();
				boolean exact = chckbxExactMatch.isSelected();
				boolean matchCase=!chckbxMatchCase.isSelected();
				List<String> res;
				if (field.equals("All Fields")) {
					//set field to data column. will use this to highlight results
					field=obj.getDataColName();
					res = obj.searchByValue(toSearch, field, exact, true,matchCase);
				} else if (field.equals("Any Field")) {
					field=obj.getDataColName();
					res = obj.searchByValue(toSearch, field, exact, false,matchCase);
				} else {
					res = obj.searchByValue(field, toSearch, field, exact, true,matchCase);
				}

				JTable table = parent.getTable();
				int fieldIndex = table.getColumn(field).getModelIndex();
				HashMap<Integer, List<String>> th = new HashMap<Integer, List<String>>();
				th.put(fieldIndex, res);
				parent.settoHighlightMap(th);
				table.repaint();
				// JOptionPane.showMessageDialog(parent.returnThisPanel(), "Total rows matched:"
				// + res.size());
				// dispose();
			}
		});
		panel_2.add(btnSearch);
		// pack();
		// setVisible(true);
		setSize(600, 200);
	}
	
	public SimpleSearchFrame() {
		this.parent = parent;
		//this.obj=parent.getthisCollection();
		this.obj=MetaOmGraph.getActiveProject().getMetadataHybrid();
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panel.setBackground(Color.GRAY);
		getContentPane().add(panel, BorderLayout.NORTH);

		lblSimpleSearch = new JLabel("Simple Search");
		lblSimpleSearch.setFont(new Font("Garamond", Font.PLAIN, 14));
		panel.add(lblSimpleSearch);

		panel_1 = new JPanel();
		panel_1.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		lblSelectField = new JLabel("Select field");
		lblSelectField.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_1.add(lblSelectField);

		comboBox = new JComboBox();
		comboBox.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		// select all the headers in the metadata
		String[] colNames = parent.getHeaders();
		comboBox.addItem("Any Field");
		comboBox.addItem("All Fields");
		for (String c : colNames) {
			comboBox.addItem(c);
		}
		panel_1.add(comboBox);

		txtToSearch = new JTextField();
		txtToSearch.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		txtToSearch.setText("To Search");
		panel_1.add(txtToSearch);
		txtToSearch.setColumns(10);

		chckbxExactMatch = new JCheckBox("Exact match");
		chckbxExactMatch.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_1.add(chckbxExactMatch);
		
		chckbxMatchCase = new JCheckBox("Match case");
		chckbxMatchCase.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		panel_1.add(chckbxMatchCase);

		panel_2 = new JPanel();
		panel_2.setBackground(Color.GRAY);
		getContentPane().add(panel_2, BorderLayout.SOUTH);

		btnCancel = new JButton("Close");
		btnCancel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel_2.add(btnCancel);

		btnSearch = new JButton("Search");
		btnSearch.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// search for query
				String toSearch = txtToSearch.getText();
				String field = comboBox.getSelectedItem().toString();
				boolean exact = chckbxExactMatch.isSelected();
				boolean matchCase=chckbxMatchCase.isSelected();
				List<String> res;
				if (field.equals("All Fields")) {
					//set field to data column. will use this to highlight results
					field=obj.getDataColName();
					res = obj.searchByValue(toSearch, field, exact, true,matchCase);
				} else if (field.equals("Any Field")) {
					field=obj.getDataColName();
					res = obj.searchByValue(toSearch, field, exact, false,matchCase);
				} else {
					res = obj.searchByValue(field, toSearch, field, exact, true,matchCase);
				}

				JTable table = parent.getTable();
				int fieldIndex = table.getColumn(field).getModelIndex();
				HashMap<Integer, List<String>> th = new HashMap<Integer, List<String>>();
				th.put(fieldIndex, res);
				parent.settoHighlightMap(th);
				table.repaint();
				// JOptionPane.showMessageDialog(parent.returnThisPanel(), "Total rows matched:"
				// + res.size());
				// dispose();
			}
		});
		panel_2.add(btnSearch);
		// pack();
		// setVisible(true);
		setSize(600, 200);
	}

}
