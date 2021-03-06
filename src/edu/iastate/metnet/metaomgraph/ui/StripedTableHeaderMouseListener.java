package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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

/**
 * A mouse listener class which is used to handle mouse clicking event
 * on column headers of a JTable.
 *
 */
public class StripedTableHeaderMouseListener extends MouseAdapter {

	private StripedTable table;
	private JPopupMenu popup;
	private Map<Integer,TableColumn> hiddenColumns;
	private Map<Integer,TableColumn> allColumns;

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
			

			popup.add(hideCol);
			popup.add(selectColHide);
			popup.add(unHideCol);


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


					
					java.util.List<String> selectedVals = new ArrayList<>();
					LinkedHashMap<TableColumn,Boolean> visibleColumns = (LinkedHashMap<TableColumn,Boolean>)MetaOmGraph.getActiveTablePanel().getStripedTable().getMetadata().getColumnVisibilityMap();
					List<TableColumn> allColumns =  new ArrayList<TableColumn>();
					
					StripedTable table = MetaOmGraph.getActiveTablePanel().getStripedTable();
					
					List<String> metadataHeaders = new ArrayList<String>();
					List<Boolean> metadataSelectedStatus = new ArrayList<Boolean>();
					
					for(Entry<TableColumn, Boolean> col : visibleColumns.entrySet()) {
						TableColumn c = col.getKey();
						allColumns.add(c);
						metadataHeaders.add(c.getHeaderValue().toString());
						metadataSelectedStatus.add(col.getValue());
					}
					
					JPanel outerPanel = new JPanel(new BorderLayout());
					JLabel txt = new JLabel("Select the columns that are to be displayed", JLabel.CENTER);
					
					outerPanel.add(txt,BorderLayout.NORTH);
					
					JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
					JButton selectAllButton = new JButton("Select All");
					JButton deselectAllButton = new JButton("Deselect All");
					
					buttonPanel.add(selectAllButton);
					buttonPanel.add(deselectAllButton);
					
					
					outerPanel.add(buttonPanel,BorderLayout.CENTER);
					// display jpanel with check box
					JCheckBox[] cBoxes = new JCheckBox[metadataHeaders.size() + 1];
					JPanel cbPanel = new JPanel();
					cbPanel.setLayout(new GridLayout(0, 3));
					cbPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
					for (int i = 0; i < metadataHeaders.size(); i++) {
						cBoxes[i] = new JCheckBox(metadataHeaders.get(i));
						
					}
					
					TreeMap<String,Integer> sortedCheckboxesMap = new TreeMap<String,Integer>();
					
					for (int i = 0; i < metadataHeaders.size(); i++) {
						sortedCheckboxesMap.put(metadataHeaders.get(i).toLowerCase(), i);
					}
					
					for(Map.Entry<String, Integer> entry : sortedCheckboxesMap.entrySet()) {
						
						cbPanel.add(cBoxes[entry.getValue()]);
						
						if(metadataSelectedStatus.get(entry.getValue())==true) {
							cBoxes[entry.getValue()].setSelected(true);
						}
						else {
							cBoxes[entry.getValue()].setSelected(false);
						}
						
					}
					
					outerPanel.add(cbPanel,BorderLayout.SOUTH);
					
					
					selectAllButton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							
							for (int i = 0; i < metadataHeaders.size(); i++) {
								cBoxes[i].setSelected(true);
							}
						}
					});

					
					deselectAllButton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							
							for (int i = 0; i < metadataHeaders.size(); i++) {
								cBoxes[i].setSelected(false);
							}
						}
					});

					
					int res = JOptionPane.showConfirmDialog(null, outerPanel, "Hide/Show Feature Metadata Columns",
							JOptionPane.OK_CANCEL_OPTION);
					if (res == JOptionPane.OK_OPTION) {
						
						for (int i = 0; i < metadataHeaders.size(); i++) {
							if (cBoxes[i].isSelected()) {
								metadataSelectedStatus.add(i, true);
								visibleColumns.put(allColumns.get(i), true);
								MetaOmGraph.getActiveTablePanel().getStripedTable().getMetadata().setColumnVisibilityMap(visibleColumns);
							}
							else {
								metadataSelectedStatus.add(i, false);
								visibleColumns.put(allColumns.get(i), false);
								MetaOmGraph.getActiveTablePanel().getStripedTable().getMetadata().setColumnVisibilityMap(visibleColumns);
							}
						}
						
						MetaOmGraph.getActiveTablePanel().getStripedTable().hideColumns();
						
					} else {
						return;
					}
					
					
				
					
					
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
