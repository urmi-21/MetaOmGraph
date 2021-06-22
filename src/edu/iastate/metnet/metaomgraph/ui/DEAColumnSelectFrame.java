package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * 
 * @author Harsha
 * 
 * This is the UI class for the "Select Feature Metadata Cols" feature of 
 * Differential Exxpression Analysis.
 * 
 * It basically provides the list of Feature Metadata columns, and lets the
 * user choose which columns to display in the Differential Expression Analysis
 * table.
 *
 */
public class DEAColumnSelectFrame extends TaskbarInternalFrame{

	private JLabel jLabel;
	private StripedTable jList;
	private JButton jButton;
	private DEAColumnSelectFrame currentFrame;
	private Map<Integer,JCheckBox> checkboxMap;

	private static String[] listItems = { "BLUE", "BLACK", "CYAN",
			"GREEN", "GRAY", "RED", "WHITE" };
	private static Color[] colors = { Color.BLUE, Color.BLACK,
			Color.CYAN, Color.GREEN, Color.GRAY, Color.RED, Color.WHITE };

	
	/**
	 * In this constructor, we initialize the Select Feature Metadata frame , add the JPanel
	 * that holds the Label, the list of Feature Metadata columns in a table, and a button that 
	 * will filter out the columns that were not selected in the Feature metadata table, from
	 * the Differential Expression Analysis table.
	 */
	public DEAColumnSelectFrame(logFCResultsFrame frame) {
		super("Show/Hide Feature metadata columns");
		
		setLayout(new FlowLayout());
		setBounds(100, 100, 300, 450);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		currentFrame = this;

		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));


		jLabel = new JLabel("Check the columns that should be displayed in the DE results");
		outerPanel.add(jLabel);

		jLabel.setBorder(new EmptyBorder(10,0,10,0));

		listItems = MetaOmGraph.activeProject.getInfoColumnNames();
		Object[][] fmObj = new Object[listItems.length][1];

		int x=0;
		for(String list_item : listItems) {
			fmObj[x][0] = list_item;
			x++;
		}

		
		checkboxMap = new HashMap<Integer,JCheckBox>();
		
		JPanel checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
		
		int itemno = 0;
		
		 for (String s:listItems){
			    
			    JCheckBox chk=new JCheckBox(s);
			    //add the checkbox to the panel
			    chk.setSelected(true);
			    checkboxPanel.add(chk);
			    
			    
			    checkboxMap.put(itemno, chk);
			    itemno++;

			  }

		JScrollPane scrollPane = new JScrollPane(checkboxPanel);
		scrollPane.setPreferredSize(new Dimension(100,400));
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

									ArrayList<Integer> columnsToHide = new ArrayList<Integer>();
									
									for(Map.Entry<Integer, JCheckBox> checkboxEntry : checkboxMap.entrySet()) {
										
										JCheckBox ck = (JCheckBox)checkboxEntry.getValue();
										
										if(!ck.isSelected()) {
											columnsToHide.add(checkboxEntry.getKey());
										}
									}
									
									frame.hideColumns(columnsToHide, checkboxMap.size());
									

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

		FrameModel deaColSelectModel = new FrameModel("DEA", "DEA Select Metadata Columns", 30);
		setModel(deaColSelectModel);

		add(outerPanel);
		setResizable(false);
		setVisible(true);

		try {
			setSelected(true);
		} catch (PropertyVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setSize(900, 550);
		setClosable(true);
		setMaximizable(false);
		setIconifiable(true);
		toFront();
	}
}
