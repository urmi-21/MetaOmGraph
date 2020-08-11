package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

public class DEAColumnSelectFrame extends TaskbarInternalFrame{

	private JLabel jLabel;
	private JList jList;
	private JButton jButton;
	private DEAColumnSelectFrame currentFrame;
	
	private static String[] listItems = { "BLUE", "BLACK", "CYAN",
			"GREEN", "GRAY", "RED", "WHITE" };
	private static Color[] colors = { Color.BLUE, Color.BLACK,
			Color.CYAN, Color.GREEN, Color.GRAY, Color.RED, Color.WHITE };

	public DEAColumnSelectFrame(logFCResultsFrame frame, int[] rowIndices) {
		super("Select Feature Metadata columns to be shown");
		setLayout(new FlowLayout());
		setBounds(100, 100, 400, 250);
		setSize(400,300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		currentFrame = this;
		
		jLabel = new JLabel("Select the Feature Metadata columns to display in DE Results");
		add(jLabel);
		
		listItems = MetaOmGraph.activeProject.getInfoColumnNames();
		jList = new JList(listItems);
		jList.setFixedCellHeight(15);
		jList.setFixedCellWidth(100);
		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jList.setVisibleRowCount(15);
		add(new JScrollPane(jList));
		

		jButton = new JButton("Submit");
		add(jButton);
		jButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> selectedCols = jList.getSelectedValuesList();
				int[] selectedFeatureMetadataCols = jList.getSelectedIndices();
				
				Object [][] allRows = MetaOmGraph.activeProject.getRowNames(rowIndices);
				String [] allColNames = MetaOmGraph.activeProject.getInfoColumnNames();
				
				Object [][] newRows = new Object[rowIndices.length][selectedFeatureMetadataCols.length];
				String [] newColNames = new String[selectedFeatureMetadataCols.length];
				
				for(int i=0;i<selectedFeatureMetadataCols.length;i++) {
					newColNames[i] = allColNames[selectedFeatureMetadataCols[i]];
					
					for(int j=0;j<rowIndices.length;j++) {
						newRows[j][i] = allRows[j][selectedFeatureMetadataCols[i]];
					}
				}
				
				frame.setFeatureMetadataColumnData(newRows);
				frame.setFeatureMetadataColumnNames(newColNames);
				frame.updateTable();
				
				try {
					currentFrame.setClosed(true);
				} catch (PropertyVetoException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		FrameModel deaColSelectModel = new FrameModel("DEA", "DEA Select Metadata Columns", 30);
		setModel(deaColSelectModel);

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
		setSize(400, 400);
		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
		toFront();
	}
}
