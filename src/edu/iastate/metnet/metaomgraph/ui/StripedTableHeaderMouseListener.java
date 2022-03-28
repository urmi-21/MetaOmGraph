package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataHeaderRenderer;

/**
 * A mouse listener class which is used to handle mouse clicking event
 * on column headers of a StripedTable.
 *
 */
public class StripedTableHeaderMouseListener extends MouseAdapter {

	private StripedTable table;
	private JPopupMenu popup;
	private Map<Integer,TableColumn> hiddenColumns;
	private Map<Integer,TableColumn> allColumns;
	private String tableType;

	public StripedTableHeaderMouseListener(StripedTable table) {
		this.table = table;
		this.hiddenColumns = new HashMap<Integer,TableColumn>();
		this.allColumns = new HashMap<Integer,TableColumn>();

		TableColumnModel originalModel = table.getColumnModel();

		for(int i=0; i<originalModel.getColumnCount(); i++) {
			allColumns.put(i, originalModel.getColumn(i));
		}
	}


	public void mousePressed(MouseEvent e)
	{


		if(allColumns.size() == 0) {
			TableColumnModel originalModel = table.getColumnModel();

			for(int i=0; i<originalModel.getColumnCount(); i++) {
				allColumns.put(i, originalModel.getColumn(i));
			}
		}

		if(e.isPopupTrigger() && e.getSource() instanceof JTableHeader && !(e.getSource() instanceof JTable)) {

			this.popup = new JPopupMenu();
			JMenuItem hideCol = new JMenuItem("Hide column");
			JMenuItem selectColHide = new JMenuItem("Select columns to hide...");
			JMenuItem unHideCol = new JMenuItem("Unhide all columns");
			JMenuItem makeUniqueIDCol = new JMenuItem("Make column Unique ID");
			

			popup.add(hideCol);
			popup.add(selectColHide);
			popup.add(unHideCol);
			popup.add(makeUniqueIDCol);


			table.setComponentPopupMenu(popup);

			JTableHeader source = (JTableHeader)e.getSource();
			TableColumnModel colModel = table.getColumnModel();

			int index = colModel.getColumnIndexAtX(e.getX());
			if (index == -1) {
				return;
			}

			hideCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {

					hideColumn(index);
				}
			});

			unHideCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
			
					Map<TableColumn,Boolean> visibilityMap= table.getMetadata().getColumnVisibilityMap();
					
					
					for (Map.Entry<TableColumn,Boolean> entry : visibilityMap.entrySet()) {

						visibilityMap.put(entry.getKey(), true);

					}
					
					table.hideColumns();


				}
			});
			
			
			selectColHide.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					table.openColumnSelectorDialog("Current Table");
				}
			});
			
			
			makeUniqueIDCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					
					MetaOmGraph.getActiveTable().getListDisplay().getColumnModel().getColumn(MetaOmGraph.getActiveProject().getDefaultColumn()).setHeaderRenderer(null);
					
					MetaOmGraph.getActiveTable().refresh();
					
					MetaOmGraph.getActiveProject().setDefaultColumn(index);
					
					MetadataHeaderRenderer customHeaderCellRenderer = 
							new MetadataHeaderRenderer(Color.white,
									new Color(153,0,0),
									new Font("Consolas",Font.BOLD,12),
									BorderFactory.createEtchedBorder(),
									true);
					
					MetaOmGraph.getActiveTable().getListDisplay().getColumnModel().getColumn(MetaOmGraph.getActiveProject().getDefaultColumn()).setHeaderRenderer(customHeaderCellRenderer);
					
					MetaOmGraph.getActiveProject().setDataColumnName(MetaOmGraph.getActiveTable().getListDisplay().getColumnModel().getColumn(MetaOmGraph.getActiveProject().getDefaultColumn()).getHeaderValue().toString());
					MetaOmGraph.getActiveProject().setChanged(true);
					MetaOmGraph.getActiveTable().getListDisplay().revalidate();
				}
			});

			popup.show(source, e.getX(), e.getY());

		}

	}
	
	public void hideColumn(int index) {
		
		TableColumn column  = table.getColumnModel().getColumn(index);
		Map<TableColumn,Boolean> visibilityMap= table.getMetadata().getColumnVisibilityMap();
		
		visibilityMap.put(column, false);
		
		table.getMetadata().setColumnVisibilityMap(visibilityMap);
		
		table.hideColumns();
	}

}
