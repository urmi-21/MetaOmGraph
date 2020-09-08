package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.Font;
import javax.swing.table.DefaultTableModel;

import edu.iastate.metnet.metaomgraph.MetadataCollection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MetadataHeaderEdit extends JDialog {

	private JPanel contentPane;
	private JTable table;
	private String[] headers;
	private MetadataCollection mobj;
	private ReadMetadata parent=null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		String []h= {"h1","h2"};
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MetadataHeaderEdit frame = new MetadataHeaderEdit(h,null,null)  ;
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MetadataHeaderEdit getThisframe() {
		return this;
	}
	/**
	 * Create the frame.
	 */
	public MetadataHeaderEdit(String[] headervals,MetadataCollection obj,ReadMetadata p) {
		this.parent=p;
		this.mobj=obj;
		this.headers=headervals;
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		JButton btnDone = new JButton("Done");
		btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(mobj==null) {
					JOptionPane.showMessageDialog(panel, "Error!!! Can't update headers. No file read...", "Error", JOptionPane.ERROR_MESSAGE);
				    return;
				}
				String[] newheaders=new String[headers.length];
				boolean []toKeep=new boolean[newheaders.length];
				//get data from table
				DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
				for(int i=0;i<tablemodel.getRowCount();i++) {
					newheaders[i]=tablemodel.getValueAt(i, 1).toString();
					//nothing to remove
					toKeep[i]=true;
					//System.out.println("**"+newheaders[i]);
				}
				
				mobj.setHeaders(newheaders,toKeep);
				parent.updateHeaders();
				getThisframe().dispose();
				parent.setEnabled(true);
				parent.toFront();
			}
		});
		panel.add(btnDone);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.NORTH);
		
		JLabel lblEditMetadataHeaders = new JLabel("Edit metadata headers");
		panel_1.add(lblEditMetadataHeaders);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable(){
		    @Override 
		    public boolean isCellEditable(int row, int column)
		    {
		        if(column==0) {
		        	return false;
		        }
		        return true;
		    }
		};
		table.setRowMargin(5);
		table.setRowHeight(25);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setForeground(Color.RED);
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Column number", "Header"
			}
		));
		
		//add data to table
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		for(int i=0;i<headers.length;i++) {
			tablemodel.addRow(new String[]{String.valueOf(i+1),headers[i]});
		}
		
		table.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		table.setBackground(Color.BLACK);
		scrollPane.setViewportView(table);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

}
