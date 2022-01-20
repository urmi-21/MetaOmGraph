package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.iastate.metnet.metaomgraph.logging.ActionProperties;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;


/**
 * 
 * @author Harsha
 * 
 * This is the prototype class of Statistical results. It can hold
 * multiple tabs. Each tab will contain a separate statistical result
 * instance. All the tabs except the first one are closable.
 * 
 * By default, when a new tab is added to the frame, it de-iconifies,
 * and selects the tab that was newly added.
 * 
 * The class also provides methods to add a list to the tab, and
 * propagate changes in list contents to all the other tabs that
 * are open.
 *
 */
public class StatisticalResultsFrame extends TaskbarInternalFrame {

	private ClosableTabbedPane resultTab;
	private List<JList> allTabsInfo;

	
	/**
	 * 
	 * @param taskbarName
	 * @param frameName
	 * 
	 * Creates a new Statistical Results frame and adds it to the taskbar.
	 * The input parameters taskbar - name given to the menu item in the taskbar
	 * frameName - name of the frame.
	 */
	public StatisticalResultsFrame(String taskbarName, String frameName) {

		allTabsInfo = new ArrayList<JList>();
		setBounds(100, 100, 800, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout(0, 0));

		UIManager.put("TabbedPane.selected", Color.red);
		UIManager.put("TabbedPane.unselectedForeground", Color.gray);
		UIManager.put("TabbedPane.selectedBackground", Color.white);
		
		resultTab = new ClosableTabbedPane();

		add(resultTab);
		
		resultTab.updateUI();
		FrameModel DEAResultsFrameModel = new FrameModel(taskbarName,frameName,17);
		setModel(DEAResultsFrameModel);
		putClientProperty("JInternalFrame.frameType", "normal");
		
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
	}
	
	
	/**
	 * Method that adds a tab (closable if not the first tab) to the frame,
	 * and selects the newly added tab.
	 */
	public void addTabToFrame(JPanel panel, String tabName, int parentActionNumber) {


		if(resultTab != null && resultTab.getTabCount()==0) {
			resultTab.addNonClosableTab(tabName,null, panel, tabName, parentActionNumber);
			resultTab.setSelectedIndex(resultTab.getTabCount()-1);

		}
		else {
			resultTab.addTab(tabName,null, panel, tabName, parentActionNumber);
			resultTab.setSelectedIndex(resultTab.getTabCount()-1);

		}
	}
	
	
	/**
	 * Adds a given JList to the allTabsInfo master data
	 */
	public void addTabListToFrame(JList list, String tabName) {
		allTabsInfo.add(list);
	}
	
	
	/**
	 * Propagates the changes in the list contents to all the tabs
	 * that are open in the StatisticalResultsFrame
	 */
	public void refreshAllTabsLists() {
		if(allTabsInfo!=null) {
			for (JList temp : allTabsInfo) {
				if(temp != null) {
				String[] listNames2 = MetaOmGraph.getActiveProject().getGeneListNames();
				String [] listNames3 = new String[listNames2.length+1];
				
				Arrays.sort(listNames2, MetaOmGraph.getActiveTablePanel().new ListNameComparator());
				
				temp.setListData(listNames2);
				temp.updateUI();
				}
			}
			
		}
	}

	public int getSelectedTabActionNumber() {
		return resultTab.getSelectedTabActionNumber();
	}
}
