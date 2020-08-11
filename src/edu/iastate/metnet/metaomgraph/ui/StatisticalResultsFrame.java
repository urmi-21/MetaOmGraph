package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JPanel;

import edu.iastate.metnet.metaomgraph.FrameModel;

public class StatisticalResultsFrame extends TaskbarInternalFrame {

	private ClosableTabbedPane resultTab;
	
	
	public StatisticalResultsFrame() {
		
		setBounds(100, 100, 750, 700);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout(0, 0));

		resultTab = new ClosableTabbedPane();
		add(resultTab);
		
		FrameModel DEAResultsFrameModel = new FrameModel("DEA","DEA Results",17);
		setModel(DEAResultsFrameModel);
		putClientProperty("JInternalFrame.frameType", "normal");
		
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		
	}
	
	public void addTabToFrame(JPanel panel, String tabName) {
		
		if(resultTab != null && resultTab.getTabCount()==0) {
			resultTab.addNonClosableTab(tabName,null, panel, tabName);
		}
		else {
			resultTab.addTab(tabName,null, panel, tabName);
		}
	}
}
