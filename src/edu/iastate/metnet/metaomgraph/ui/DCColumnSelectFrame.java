package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * 
 * @author Harsha
 * 
 * This is the UI class for the "Select Feature Metadata Cols" feature of 
 * Differential Correlation.
 * 
 * It basically provides the list of Feature Metadata columns, and lets the
 * user choose which columns to display in the Differential Correlation
 * table.
 *
 */
public class DCColumnSelectFrame extends TaskbarInternalFrame{

	private JLabel jLabel;
	private StripedTable jList;
	private JButton jButton;
	private DCColumnSelectFrame currentFrame;

	private static String[] listItems = { "BLUE", "BLACK", "CYAN",
			"GREEN", "GRAY", "RED", "WHITE" };
	private static Color[] colors = { Color.BLUE, Color.BLACK,
			Color.CYAN, Color.GREEN, Color.GRAY, Color.RED, Color.WHITE };

	
	/**
	 * In this constructor, we initialize the Select Feature Metadata frame , add the JPanel
	 * that holds the Label, the list of Feature Metadata columns in a table, and a button that 
	 * will filter out the columns that were not selected in the Feature metadata table, from
	 * the Differential Correlation table.
	 */
	public DCColumnSelectFrame(DiffCorrResultsTable frame) {
		super("Select Feature Metadata columns to be shown");
		setLayout(new FlowLayout());
		setBounds(100, 100, 700, 450);
		setSize(400,300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		currentFrame = this;

		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));


		jLabel = new JLabel("Select the Feature Metadata columns to display in DE Results");
		outerPanel.add(jLabel);
		jLabel.setBorder(new EmptyBorder(10,0,10,0));

		listItems = MetaOmGraph.activeProject.getInfoColumnNames();
		Object[][] fmObj = new Object[listItems.length][1];

		int x=0;
		for(String list_item : listItems) {
			fmObj[x][0] = list_item;
			x++;
		}
		jList = new StripedTable();
		NoneditableTableModel model = new NoneditableTableModel(fmObj,new String[] {"Feature Metadata Cols"});
		jList.setModel(model);
		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


		JScrollPane scrollPane = new JScrollPane(jList);
		scrollPane.setSize(200,500);
		outerPanel.add(scrollPane);

		scrollPane.setBorder(new EmptyBorder(0,0,10,0));

		jButton = new JButton("Submit");
		outerPanel.add(jButton);
		jButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new AnimatedSwingWorker("Working...", true) {
					@Override
					public Object construct() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								try {

									int[] rowIndices = new int[frame.getFeatureNames().size()];
									int i=0;
									for(String row : frame.getFeatureNames()) {
										rowIndices[i] = MetaOmGraph.activeProject.getRowIndexbyName(row,true);
										i++;
									}


									int [] selectedFeatureMetadataCols = jList.getSelectedRows();

									List<String> selectedCols = new ArrayList<String>();

									for(int j=0;j<selectedFeatureMetadataCols.length;j++) {
										selectedCols.add((String) model.getValueAt(selectedFeatureMetadataCols[j], 0));
									}

									Object [][] allRows = MetaOmGraph.activeProject.getRowNames(rowIndices);
									Object [][] totalRows = MetaOmGraph.activeProject.getRowNames();
									String [] allColNames = MetaOmGraph.activeProject.getInfoColumnNames();

									Object [][] newRows = new Object[rowIndices.length][selectedFeatureMetadataCols.length];
									Object [][] newTotalRows = new Object[totalRows.length][selectedFeatureMetadataCols.length];
									String [] newColNames = new String[selectedFeatureMetadataCols.length];

									for(int l=0;l<selectedFeatureMetadataCols.length;l++) {
										newColNames[l] = allColNames[selectedFeatureMetadataCols[l]];

										for(int j=0;j<rowIndices.length;j++) {
											newRows[j][l] = allRows[j][selectedFeatureMetadataCols[l]];
										}
										for(int k=0;k<totalRows.length;k++) {
											newTotalRows[k][l] = totalRows[k][selectedFeatureMetadataCols[l]];
										}
									}

									frame.setFeatureMetadataColumnData(newRows);
									frame.setFeatureMetadataColumnNames(newColNames);
									frame.setFeatureMetadataAllData(newTotalRows);
									frame.updateTable();

									try {
										currentFrame.setClosed(true);
									} catch (PropertyVetoException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						return null;
					}
				}.start();
			}
		});

		FrameModel deaColSelectModel = new FrameModel("Differential Correlation", "DC Select Metadata Columns", 31);
		setModel(deaColSelectModel);

		add(outerPanel);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setVisible(true);

		try {
			setSelected(true);
		} catch (PropertyVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setSize(600, 550);
		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
		toFront();
	}
}
