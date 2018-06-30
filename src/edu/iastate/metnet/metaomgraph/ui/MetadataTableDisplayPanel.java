package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.dizitart.no2.Document;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.chart.RangeMarker;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.awt.event.ActionEvent;

public class MetadataTableDisplayPanel extends JPanel {
	private JTable table;
	private MetadataCollection obj;
	private List<Document> metadata;
	private String[] headers;
	// private List<String> toHighlight;
	// highlight rows where iths col contains coressponding value
	private HashMap<Integer, List<String>> toHighlight;
	private List<Integer> highlightedRows;
	private JScrollPane scrollPane;

	/**
	 * Default Properties
	 */
	private Color SELECTIONBCKGRND = Color.black;
	private Color BCKGRNDCOLOR1 = Color.white;
	private Color BCKGRNDCOLOR2 = new ColorUIResource(216, 236, 213);
	private Color HIGHLIGHTCOLOR = Color.ORANGE;

	public MetadataTableDisplayPanel() {
		this(null);
	}

	/**
	 * Create the panel.
	 */
	public MetadataTableDisplayPanel(MetadataCollection obj) {
		this.obj = obj;
		metadata = this.obj.returnallData();
		this.headers = obj.getHeaders();
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		panel.add(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExport = new JMenuItem("Export");
		mnFile.add(mntmExport);

		JMenuItem mntmNewProjectWith = new JMenuItem("New Project With Selected");
		mnFile.add(mntmNewProjectWith);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenu mnFilter = new JMenu("Filter");
		mnFilter.setToolTipText(
				"Filter data and metadata for the current project. All the plots and analysis will use this reduced dataset.");
		mnEdit.add(mnFilter);

		JMenu mnByRow = new JMenu("By row");
		mnFilter.add(mnByRow);

		JMenuItem mntmFilterSelectedRows = new JMenuItem("Filter selected rows");
		mntmFilterSelectedRows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] options = { "Remove", "Keep", "Cancel" };
				JPanel optPanel = new JPanel();
				optPanel.add(new JLabel("Remove or keep selected rows ?"));
				int option = JOptionPane.showOptionDialog(null, optPanel, "Choose an option",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

				if (option == JOptionPane.YES_OPTION) {
					filterSelectedRows(false);
				} else if (option == JOptionPane.NO_OPTION) {
					filterSelectedRows(true);
				}
				updateTable();
				MetaOmGraph.getActiveTable().updateMetadataTree();
			}
		});
		mnByRow.add(mntmFilterSelectedRows);

		JMenuItem mntmFilterLastSearched = new JMenuItem("Filter last searched");
		mntmFilterLastSearched.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] options = { "Remove", "Keep", "Cancel" };
				JPanel optPanel = new JPanel();
				optPanel.add(new JLabel("Remove or keep selected rows ?"));
				int option = JOptionPane.showOptionDialog(null, optPanel, "Choose an option",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
				if (option == JOptionPane.YES_OPTION) {
					filterHighlightedRows(false);
				} else if (option == JOptionPane.NO_OPTION) {
					filterHighlightedRows(true);
				}
				
				updateTable();
				MetaOmGraph.getActiveTable().updateMetadataTree();
				// clear last search
				toHighlight = new HashMap<Integer, List<String>>();
				// initialize with garbage value for alternate coloring to take effect via
				// prepareRenderer
				toHighlight.put(0, null);
				table.repaint();
				highlightedRows = null;

			}
		});
		mnByRow.add(mntmFilterLastSearched);

		JMenuItem mntmAdvanceFilter = new JMenuItem("Advance filter");
		mntmAdvanceFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MetadataFilter frame = new MetadataFilter(obj);
							frame.setVisible(true);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnByRow.add(mntmAdvanceFilter);

		JMenuItem mntmReset_1 = new JMenuItem("Reset");
		mntmReset_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				obj.resetRowFilter();
				// update exclude list
				MetaOmAnalyzer.updateExcluded(obj.getExcluded());
				updateTable();
				MetaOmGraph.getActiveTable().updateMetadataTree();
			}
		});
		mnByRow.add(mntmReset_1);

		JMenu mnByColumn = new JMenu("By column");
		mnFilter.add(mnByColumn);

		JMenuItem mntmSelectColumn = new JMenuItem("Select column");
		mntmSelectColumn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MetadataRemoveCols frame = new MetadataRemoveCols(headers, obj, getThisPanel(), false);
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnByColumn.add(mntmSelectColumn);

		JMenuItem mntmReset = new JMenuItem("Reset");
		mntmReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetData();
				updateTable();
				updateHeaders();
			}
		});
		mnByColumn.add(mntmReset);

		JMenu mnRemove = new JMenu("Remove");
		mnRemove.setToolTipText("Permanently delete data or metadata. This can't be undone!");
		mnEdit.add(mnRemove);

		JMenuItem mntmRows = new JMenuItem("Rows");
		mntmRows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MetadataFilter frame = new MetadataFilter(obj, true);
							frame.setVisible(true);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnRemove.add(mntmRows);

		JMenuItem mntmColumns = new JMenuItem("Columns");
		mntmColumns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							// remove cols permanently from metadata
							MetadataRemoveCols frame = new MetadataRemoveCols(headers, obj, getThisPanel(), true);
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnRemove.add(mntmColumns);

		JMenu mnView = new JMenu("View");
		mnEdit.add(mnView);

		JMenuItem mntmSwitchToTree = new JMenuItem("Switch To Tree");
		mntmSwitchToTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//highlight selected rows in the tree
				//get selected data cols
				//s;
				int[] selected = table.getSelectedRows();
				String [] selectedNames=new String[selected.length];
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for(int i=0;i<selected.length;i++) {
					selectedNames[i] = model.getValueAt(table.convertRowIndexToModel(selected[i]),
							table.getColumn(obj.getDatacol()).getModelIndex()).toString();
					int thisInd=MetaOmGraph.getActiveProject().getMetadataHybrid().getColIndexbyName(selectedNames[i]);
					MetaOmGraph.getActiveTable().selectNode(thisInd,false);
				}
				MetaOmGraph.tableToFront();
				//int thisInd=MetaOmGraph.getActiveProject().getMetadataHybrid().getColIndexbyName(thisSname);
				//MetaOmGraph.getActiveTable().selectNode(thisInd,true);
			}
		});
		mnView.add(mntmSwitchToTree);

		JMenuItem mntmProperties = new JMenuItem("Properties");
		mnEdit.add(mntmProperties);

		JMenu mnSearch = new JMenu("Search");
		menuBar.add(mnSearch);

		JMenuItem mntmSimple = new JMenuItem("Simple");
		mntmSimple.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * SimpleSearchDialog o= new SimpleSearchDialog();
				 * o.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); o.setVisible(true);
				 * o.setModalityType(Dialog.DEFAULT_MODALITY_TYPE); if(o.clicked) { String
				 * f=o.field; String v=o.toSearch; boolean e=o.exact;
				 * JOptionPane.showMessageDialog(null, "tos:"+v+"f:"+f); o.dispose(); }
				 */
				highlightedRows = null;
				highlightedRows = new ArrayList<>();

				SimpleSearchFrame frame = new SimpleSearchFrame(returnThisPanel());
				frame.setVisible(true);
				MetaOmGraph.getDesktop().add(frame);
				try {
					frame.setSelected(true);
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		// remove simple search
		// mnSearch.add(mntmSimple);

		JMenuItem mntmAdvance = new JMenuItem("Search");
		mntmAdvance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
						MetaOmGraph.getActiveProject(),false);
				final MetadataQuery[] queries;
				queries = tsp.showSearchDialog();
				if (tsp.getQueryCount() <= 0) {
					// System.out.println("Search dialog cancelled");
					// User didn't enter any queries
					return;
				}
				// final int[] result = new
				// int[MetaOmGraph.getActiveProject().getDataColumnCount()];
				final List<String> result = new ArrayList<>();

				new AnimatedSwingWorker("Searching...", true) {

					@Override
					public Object construct() {
						
						List<String> hits = MetaOmGraph.getActiveProject().getMetadataHybrid().getMatchingRows(queries,
								tsp.matchAll(),tsp.matchCase());

						// return if no hits
						if (hits.size() == 0) {
							// JOptionPane.showMessageDialog(null, "hits len:"+hits.length);
							// nohits=true;
							result.add("NULL");
							return null;
						} else {
							for (int i = 0; i < hits.size(); i++) {
								result.add(hits.get(i));
							}
						}

						return null;
					}

				}.start();

				// JOptionPane.showMessageDialog(null, "REs:"+result.toString());
				// JOptionPane.showMessageDialog(null,
				// "fe:"+queries[0].getField()+"index:"+table.getColumn(queries[0].getField()).getModelIndex());
				// add highlights to results
				highlightedRows = null;
				highlightedRows = new ArrayList<>();
				toHighlight = new HashMap<Integer, List<String>>();
				toHighlight.put(table.getColumn(obj.getDatacol()).getModelIndex(), result);
				table.repaint();

			}
		});
		mnSearch.add(mntmAdvance);

		JMenuItem mntmRemovePrevious = new JMenuItem("Clear Last Search");
		mntmRemovePrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toHighlight = new HashMap<Integer, List<String>>();
				// initialize with garbage value for alternate coloring to take effect via
				// prepareRenderer
				toHighlight.put(0, null);
				highlightedRows = null;
				table.repaint();

			}
		});
		mnSearch.add(mntmRemovePrevious);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		initTable(headers);
		scrollPane.setViewportView(table);
		// table.repaint();

	}

	private void initTable(String[] headers) {
		toHighlight = new HashMap<Integer, List<String>>();
		// initialize with garbage value for alternate coloring to take effect via
		// prepareRenderer
		toHighlight.put(0, null);
		// List<String> ls = new ArrayList<String>();
		// ls.add("Exp1");
		// toHighlight.put(0, ls);
		// headers = obj.getHeaders();
		table = new JTable() {
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (!isRowSelected(row)) {
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);

					// for (int j = 0; j < table.getColumnCount(); j++) {
					for (Integer j : toHighlight.keySet()) {

						String type = (String) getModel().getValueAt(modelRow, j);
						if (highlightThisRow(j, type)) {
							c.setBackground(HIGHLIGHTCOLOR);
							if (!highlightedRows.contains(modelRow)) {
								highlightedRows.add(modelRow);
							}
						} else {
							if (row % 2 == 0) {
								c.setBackground(BCKGRNDCOLOR1);
							} else {
								c.setBackground(BCKGRNDCOLOR2);
							}
						}
					}
					/*
					 * String type = (String) getModel().getValueAt(modelRow, 0); if
					 * ("Exp1".equals(type)) c.setBackground(Color.GREEN); if ("Sell".equals(type))
					 * c.setBackground(Color.YELLOW);
					 */
				} else {
					c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}
		};
		table.setModel(new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// add data
		// for each row add each coloumn
		for (int i = 0; i < metadata.size(); i++) {
			// create a temp string storing all col values for a row
			String[] temp = new String[headers.length];
			for (int j = 0; j < headers.length; j++) {

				// add col name
				if (i == 0) {
					tablemodel.addColumn(headers[j]);
				}

				temp[j] = metadata.get(i).get(headers[j]).toString();
			}

			// add ith row in table
			tablemodel.addRow(temp);

		}

		// table.setBackground(Color.GREEN);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));

	}

	public HashMap<Integer, List<String>> gettoHighlightMap() {
		return this.toHighlight;
	}

	public void settoHighlightMap(HashMap<Integer, List<String>> th) {
		this.toHighlight = th;
	}

	private boolean highlightThisRow(int col, String val) {
		// JOptionPane.showMessageDialog(null, "val:"+val+"col#:"+col);
		boolean result = false;
		if (toHighlight == null || toHighlight.size() < 1) {
			return false;
		}
		// search
		List<String> allVals = toHighlight.get(col);
		if (allVals == null) {
			return false;
		}
		if (allVals.contains(val)) {

			return true;
		}
		return result;
	}

	public MetadataTableDisplayPanel returnThisPanel() {
		return this;
	}

	public JTable getTable() {
		return this.table;
	}

	public MetadataTableDisplayPanel getThisPanel() {
		return this;
	}

	/**
	 * @author urmi Update the table model after deleting cols
	 */
	public void updateTable() {
		metadata = this.obj.returnallData();
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// AbstractTableModel tablemodel = (DefaultTableModel) table.getModel();
		// clear table model
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		// tablemodel.getDataVector().removeAllElements();
		// tablemodel.fireTableDataChanged();
		// table.repaint();

		for (int i = 0; i < metadata.size(); i++) {
			// create a temp string storing all col values for a row
			String[] temp = new String[headers.length];
			for (int j = 0; j < headers.length; j++) {

				// add col name
				if (i == 0) {
					tablemodel.addColumn(headers[j]);
				}

				temp[j] = metadata.get(i).get(headers[j]).toString();
			}

			// add ith row in table
			tablemodel.addRow(temp);

		}
	}

	public void updateHeaders() {
		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		tablemodel.setColumnIdentifiers(headers);
		table.repaint();

	}

	public void setHeaders(String[] newHeaders) {
		this.headers = newHeaders;
	}

	public String[] getHeaders() {
		return this.headers;
	}

	public void resetData() {
		this.metadata = this.obj.returnallData();
		this.headers = obj.getHeaders();
	}

	public MetadataCollection getthisCollection() {
		return this.obj;
	}

	/**
	 * @author urmi Exclude data columns in the selected rows
	 */
	public void filterSelectedRows(boolean invert) {
		List<String> inc = obj.getIncluded();
		List<String> exc = obj.getExcluded();
		// get selected rows in table
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int[] selected = table.getSelectedRows();
		if (invert) {
			table.selectAll();
			for (int lastSel : selected) {
				table.removeRowSelectionInterval(lastSel, lastSel);
			}
			selected = table.getSelectedRows();
		}

		/*
		 * String[] temp = new String[1]; for (int c = 0; c < selected.length; c++) {
		 * temp[0] = model.getValueAt(table.convertRowIndexToModel(selected[c]),
		 * 0).toString();
		 * 
		 * 
		 * }
		 */
		// remove rows from excluded
		Arrays.sort(selected);
		for (int i = selected.length - 1; i >= 0; i--) {
			// JOptionPane.showMessageDialog(null, "si:"+selected[i]);
			String temp = model.getValueAt(table.convertRowIndexToModel(selected[i]),
					table.getColumn(obj.getDatacol()).getModelIndex()).toString();
			// JOptionPane.showMessageDialog(null, "rem:"+temp);
			exc.add(temp);
			inc.remove(inc.indexOf(temp));
			model.removeRow(table.convertRowIndexToModel(selected[i]));

		}

		obj.setExcluded(exc);
		obj.setIncluded(inc);
		// update exclude list
		MetaOmAnalyzer.updateExcluded(exc);

	}

	public void filterHighlightedRows(boolean invert) {
		if (highlightedRows == null || highlightedRows.size() < 1) {
			JOptionPane.showMessageDialog(null, "Nothing to remove", "Nothing to remove", JOptionPane.WARNING_MESSAGE);
			return;
		}
		List<String> inc = obj.getIncluded();
		List<String> exc = obj.getExcluded();
		if (invert) {
			List<Integer> temp = new ArrayList<>();
			for (int i = 0; i < table.getRowCount(); i++) {
				if (!highlightedRows.contains(i)) {
					temp.add(i);
				}
			}
			highlightedRows = temp;
		}
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		// JOptionPane.showMessageDialog(null, "torem:" + highlightedRows.toString());
		// remove rows from excluded
		java.util.Collections.sort(highlightedRows, java.util.Collections.reverseOrder());
		for (int i = 0; i < highlightedRows.size(); i++) {
			// JOptionPane.showMessageDialog(null, "si:"+selected[i]);
			String temp = model.getValueAt(highlightedRows.get(i), table.getColumn(obj.getDatacol()).getModelIndex())
					.toString();
			// JOptionPane.showMessageDialog(null, "rem:"+temp);
			exc.add(temp);
			inc.remove(inc.indexOf(temp));
			model.removeRow(highlightedRows.get(i));

		}

		obj.setExcluded(exc);
		obj.setIncluded(inc);
		// update exclude list
		MetaOmAnalyzer.updateExcluded(exc);
	}

	public void filterRows(List<String> s) {
		List<String> inc = obj.getIncluded();
		List<String> exc = obj.getExcluded();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		JOptionPane.showMessageDialog(null, "To rem:" + s.toString());
		for (int i = 0; i < table.getRowCount(); i++) {
			String temp = model.getValueAt(i, table.getColumn(obj.getDatacol()).getModelIndex()).toString();
			if (s.contains(temp)) {
				exc.add(temp);
				inc.remove(inc.indexOf(temp));
				model.removeRow(i);
			}

		}

		obj.setExcluded(exc);
		obj.setIncluded(inc);
		// update exclude list
		MetaOmAnalyzer.updateExcluded(exc);
	}

}
