package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SetColTypes extends JInternalFrame {
	private JTable table;
	private JButton btnOk;
	private JButton btnBack;
	private String[] infoColNames=null;
	private boolean mdCols; //if true then chage types of metadata columns in the metadatahybrid class

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SetColTypes frame = new SetColTypes(null,false);
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
	
	public SetColTypes(String[] infoCols, boolean metadataCols) {
		setBounds(100, 100, 450, 300);
		mdCols=metadataCols;
		infoColNames=infoCols;
		getContentPane().setLayout(new BorderLayout(0, 0));
		setTitle("Please indicate column types");
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		btnBack = new JButton("Back");
		//panel.add(btnBack);
		
		btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HashMap<String, Class> map=new HashMap<>();
				for(int i=0;i<table.getRowCount();i++) {
					String thisCol=table.getModel().getValueAt(i, 1).toString();
					String thisType=table.getModel().getValueAt(i, 2).toString();
					if(thisType.equals("String")) {
						map.put(thisCol, String.class);
					}else {
						map.put(thisCol, double.class);
					}
				}
				if(MetaOmGraph.getActiveProject().setInfoColTypes(map)) {
					dispose();
				}
				
				
				/*for (String name : map.keySet()) {
					String key = name.toString();
					String value = map.get(name).toString();
					JOptionPane.showMessageDialog(null, "" + key + ": " + value);

				}*/
				
				
			}
		});
		panel.add(btnOk);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null},
				{null, null},
			},
			new String[] {
				"New column", "New column"
			}
		));
		initTable();		
		scrollPane.setViewportView(table);
	}
	
	
	public void initTable() {
		table = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column) { // dont let edit firstcol
				if (column == 0 ) {
					return false;
				}
				return true;
			}
		};
		table.setRowMargin(3);
		table.setRowHeight(25);
		table.setIntercellSpacing(new Dimension(2, 2));
		table.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setModel(
				new DefaultTableModel(new Object[][] {}, new String[] { "Column number", "Column number", "Type" }));
		// set default values
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		
		MetaOmProject proj=MetaOmGraph.getActiveProject();
		MetaOmTablePanel tab=MetaOmGraph.getActiveTable();
		boolean[] isNumber = new boolean[infoColNames.length];
		for (int i = 0; i < infoColNames.length; i++) {
			//check if current infoCol could be a number
			isNumber[i]=true;
			int min=proj.getRowCount();
			if(100<min) {
				min=100;
			}
			for(int j=0;j<min;j++) {
				try {
					Double.parseDouble(tab.getMainTableItemat(j, i));
					
				} catch (NumberFormatException | NullPointerException e) {
					isNumber[i] = false;
					break;
				}
			}
			
			
			
		}

		for (int i = 0; i < infoColNames.length; i++) {
						
			if (!isNumber[i]) {
				tablemodel.addRow(new String[] { String.valueOf(i + 1), infoColNames[i], "String" });
			} else {
				tablemodel.addRow(new String[] { String.valueOf(i + 1), infoColNames[i], "Alphanumeric" });
			}
		}
		
		TableColumn optionColumn = table.getColumnModel().getColumn(2);
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("String");
		comboBox.addItem("Alphanumeric");
		comboBox.setSelectedIndex(0);
		optionColumn.setCellEditor(new DefaultCellEditor(comboBox));

		
		
		
	}

}
