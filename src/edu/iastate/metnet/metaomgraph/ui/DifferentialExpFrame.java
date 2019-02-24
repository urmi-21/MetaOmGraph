package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;

import javax.swing.ScrollPaneConstants;

public class DifferentialExpFrame extends JInternalFrame {
	
	private JComboBox comboBox;
	private JComboBox comboBox_1;
	
	private JTextField txtGroup1;
	private JTextField txtGroup2;

	private JScrollPane jscp1;
	private JTable tableGrp1;

	private JScrollPane jscp2;
	private JTable tableGrp2;
	
	private MetadataHybrid mdob;

	/**
	 * Default Properties
	 */

	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColor1();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColor2();
	private Color HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
	private Color HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();

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

		//init objects
		mdob=MetaOmGraph.getActiveProject().getMetadataHybrid();
		if(mdob==null) {
			JOptionPane.showMessageDialog(null, "Error. No metadata found", "Error", JOptionPane.ERROR_MESSAGE);
			dispose();
		}
		
		initComboBoxes();
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		JLabel lblTop = new JLabel("Select feature list");
		panel.add(lblTop);
		panel.add(comboBox);
		JLabel label = new JLabel("                             ");
		panel.add(label);
		JLabel lblSelectMethod = new JLabel("Select method");
		panel.add(lblSelectMethod);
		panel.add(comboBox_1);
		

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		JButton btnOk = new JButton("Ok");
		panel_1.add(btnOk);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		panel_2.add(splitPane, BorderLayout.CENTER);
		splitPane.setResizeWeight(0.5);

		JPanel panel_3 = new JPanel();
		splitPane.setRightComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		txtGroup2 = new JTextField();
		txtGroup2.setText("Group2");
		txtGroup2.setColumns(10);
		JPanel topbtnPnl2 = new JPanel(new FlowLayout());
		JButton sendLeft = new JButton("<<");
		topbtnPnl2.add(sendLeft);

		JLabel lblGroupName_1 = new JLabel("Group name:");
		topbtnPnl2.add(lblGroupName_1);
		topbtnPnl2.add(txtGroup2);
		panel_3.add(topbtnPnl2, BorderLayout.NORTH);
 
		// add table2
		jscp2 = new JScrollPane();
		tableGrp2 = initTableModel();
		updateTableData(tableGrp2,mdob.getMetadataCollection().getAllDataCols());
		jscp2.setViewportView(tableGrp2);
		panel_3.add(jscp2, BorderLayout.CENTER);

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
		JButton sendRight = new JButton(">>");

		JLabel lblGroupName = new JLabel("Group name:");
		topbtnPnl1.add(lblGroupName);
		topbtnPnl1.add(txtGroup1);
		topbtnPnl1.add(sendRight);

		panel_4.add(topbtnPnl1, BorderLayout.NORTH);

		// add table1
		jscp1 = new JScrollPane();
		tableGrp1 = initTableModel();
		updateTableData(tableGrp1,null);
		jscp1.setViewportView(tableGrp1);
		panel_4.add(jscp1, BorderLayout.CENTER);

		JButton btnAdd1 = new JButton("Add");
		JPanel btnPnl1 = new JPanel(new FlowLayout());
		btnPnl1.add(btnAdd1);
		JButton btnRem1 = new JButton("Remove");
		btnPnl1.add(btnRem1);
		JButton btnSearch1 = new JButton("Search");
		btnPnl1.add(btnSearch1);
		panel_4.add(btnPnl1, BorderLayout.SOUTH);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

	}

	private JTable initTableModel() {
		JTable table = new JTable() {
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (!isRowSelected(row)) {
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);

					if (row % 2 == 0) {
						c.setBackground(BCKGRNDCOLOR1);
					} else {
						c.setBackground(BCKGRNDCOLOR2);
					}

				} else {
					c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}

		};
		// disable colum drag
		table.getTableHeader().setReorderingAllowed(false);
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				default:
					return String.class;
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "New column" }));
		// set properties
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
		return table;
	}

	private void updateTableData(JTable table,List<String> rows) {
		if(rows==null || rows.size() <1 ) {
			return;
		}
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		// add data
		String dcName=mdob.getDataColName();
		tablemodel.addColumn(dcName);
		Vector temp=null;
		for(String s:rows) {
			temp = new Vector<>();
			temp.add(s);
			tablemodel.addRow(temp);
		}
		
	}
	
	private void initComboBoxes() {
		 comboBox=new JComboBox(MetaOmGraph.getActiveProject().getGeneListNames());
		 String []methods=new String[]{"M-W U Test","t Test","Welch Test","Paired t Test"};
		 comboBox_1=new JComboBox(methods);
	}

}
