package edu.iastate.metnet.metaomgraph.playback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.iastate.metnet.metaomgraph.logging.ActionProperties;

public class PlaybackTabData {

	private int tabNumber;
	private String logFileName;
	private HashMap<Integer,DefaultMutableTreeNode> treeStructure;
	private ArrayList<ActionProperties> actionObjects;
	private JTree tabTree;
	private JTable tabTable;
	private JTable includedSamplesTable;
	private JTable excludedSamplesTable;
	private JTable featuresTable;
	
	public int getTabNumber() {
		return tabNumber;
	}
	public void setTabNumber(int tabNumber) {
		this.tabNumber = tabNumber;
	}
	public String getLogFileName() {
		return logFileName;
	}
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	public HashMap<Integer, DefaultMutableTreeNode> getTreeStructure() {
		return treeStructure;
	}
	public void setTreeStructure(HashMap<Integer, DefaultMutableTreeNode> treeStructure) {
		this.treeStructure = treeStructure;
	}
	public ArrayList<ActionProperties> getActionObjects() {
		return actionObjects;
	}
	public void setActionObjects(ArrayList<ActionProperties> actionObjects) {
		this.actionObjects = actionObjects;
	}
	public JTree getTabTree() {
		return tabTree;
	}
	public void setTabTree(JTree tabTree) {
		this.tabTree = tabTree;
	}
	public JTable getTabTable() {
		return tabTable;
	}
	public void setTabTable(JTable tabTable) {
		this.tabTable = tabTable;
	}
	public JTable getIncludedSamplesTable() {
		return includedSamplesTable;
	}
	public void setIncludedSamplesTable(JTable includedSamplesTable) {
		this.includedSamplesTable = includedSamplesTable;
	}
	public JTable getExcludedSamplesTable() {
		return excludedSamplesTable;
	}
	public void setExcludedSamplesTable(JTable excludedSamplesTable) {
		this.excludedSamplesTable = excludedSamplesTable;
	}
	public JTable getFeaturesTable() {
		return featuresTable;
	}
	public void setFeaturesTable(JTable featuresTable) {
		this.featuresTable = featuresTable;
	}
	
	
}
