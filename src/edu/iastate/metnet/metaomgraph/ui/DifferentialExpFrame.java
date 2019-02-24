package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.ScrollPaneConstants;

public class DifferentialExpFrame extends JInternalFrame {
	private JTextField txtGroup1;
	private JTextField txtGroup2;

	private JScrollPane jscp1;
	private JTable tableGrp1;

	private JScrollPane jscp2;
	private JTable tableGrp2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DifferentialExpFrame frame = new DifferentialExpFrame();
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
	public DifferentialExpFrame() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setTitle("Differential expression analysis");

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblTop = new JLabel("Select feature list");
		panel.add(lblTop);

		JComboBox comboBox = new JComboBox();
		panel.add(comboBox);

		JLabel label = new JLabel("                             ");
		panel.add(label);

		JLabel lblSelectMethod = new JLabel("Select method");
		panel.add(lblSelectMethod);

		JComboBox comboBox_1 = new JComboBox();
		panel.add(comboBox_1);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		JButton btnOk = new JButton("Ok");
		panel_1.add(btnOk);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(0.75D);
		panel_2.add(splitPane, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();
		splitPane.setRightComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		txtGroup2 = new JTextField();		
		txtGroup2.setText("Group2");
		txtGroup2.setColumns(10);
		JPanel topbtnPnl2 = new JPanel(new FlowLayout());
		JButton sendLeft=new JButton("<<");
		topbtnPnl2.add(sendLeft);
		
		JLabel lblGroupName_1 = new JLabel("Group name:");
		topbtnPnl2.add(lblGroupName_1);
		topbtnPnl2.add(txtGroup2);		
		panel_3.add(topbtnPnl2, BorderLayout.NORTH);
		
		// add table2
		jscp2 = new JScrollPane();
		panel_3.add(jscp2, BorderLayout.CENTER);
		tableGrp2 = new JTable();
		tableGrp2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableGrp2.setModel(new DefaultTableModel(
				new Object[][] { { "dsads", "asd", null, null }, { null, null, null, null },
						{ null, "asd", "asd", null }, { null, null, null, null }, },
				new String[] { "New column", "New column", "New column", "New column" }));

		jscp2.setViewportView(tableGrp2);

		JButton btnAdd2 = new JButton("Add");
		JPanel btnPnl2 = new JPanel(new FlowLayout());
		btnPnl2.add(btnAdd2);
		JButton btnRem2 = new JButton("Remove");
		btnPnl2.add(btnRem2);
		JButton btnSearch2 = new JButton("Search");
		btnPnl2.add(btnSearch2);
		panel_3.add(btnPnl2, BorderLayout.SOUTH);

		JPanel panel_4 = new JPanel();
		splitPane.setLeftComponent(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));

		txtGroup1 = new JTextField();		
		txtGroup1.setText("Group1");
		txtGroup1.setColumns(10);
		JPanel topbtnPnl1 = new JPanel(new FlowLayout());
		JButton sendRight=new JButton(">>");
		
		JLabel lblGroupName = new JLabel("Group name:");
		topbtnPnl1.add(lblGroupName);
		topbtnPnl1.add(txtGroup1);
		topbtnPnl1.add(sendRight);
				
		panel_4.add(topbtnPnl1, BorderLayout.NORTH);

		// add table1
		jscp1 = new JScrollPane();
		panel_4.add(jscp1, BorderLayout.CENTER);
		tableGrp1 = new JTable();
		tableGrp1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableGrp1.setModel(new DefaultTableModel(
				new Object[][] { { "dsads", "asd", null, null }, { null, null, null, null },
						{ null, "asd", "asd", null }, { null, null, null, null }, },
				new String[] { "New column", "New column", "New column", "New column" }));

		jscp1.setViewportView(tableGrp1);

		JButton btnAdd1 = new JButton("Add");
		JPanel btnPnl1 = new JPanel(new FlowLayout());
		btnPnl1.add(btnAdd1);
		JButton btnRem1 = new JButton("Remove");
		btnPnl1.add(btnRem1);
		JButton btnSearch1 = new JButton("Search");
		btnPnl1.add(btnSearch1);

		panel_4.add(btnPnl1, BorderLayout.SOUTH);

	}

}
