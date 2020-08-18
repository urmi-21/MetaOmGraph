package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel.ListNameComparator;

public class StatisticalResultsFrame extends TaskbarInternalFrame {

	private ClosableTabbedPane resultTab;
	private List<JList> allTabsInfo;
	
	
	public StatisticalResultsFrame(String taskbarName, String frameName) {
		
		allTabsInfo = new ArrayList<JList>();
		setBounds(100, 100, 800, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout(0, 0));

		resultTab = new ClosableTabbedPane();
		add(resultTab);
		
		FrameModel DEAResultsFrameModel = new FrameModel(taskbarName,frameName,17);
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
	
	public void addTabListToFrame(JList list, String tabName) {
		allTabsInfo.add(list);
	}
	
	public void refreshAllTabsLists() {
		if(allTabsInfo!=null) {
			for (JList temp : allTabsInfo) {
				if(temp != null) {
				String[] listNames2 = MetaOmGraph.getActiveProject().getGeneListNames();
				String [] listNames3 = new String[listNames2.length+1];
				
				Arrays.sort(listNames2, MetaOmGraph.getActiveTablePanel().new ListNameComparator());
				int i=0;
				listNames3[0] = "Current Result";
				for(i=1;i<=listNames2.length;i++) {
					listNames3[i] = listNames2[i-1];
				}
				
				
				temp.setListData(listNames3);
				temp.updateUI();
				}
			}
			
		}
	}
}
