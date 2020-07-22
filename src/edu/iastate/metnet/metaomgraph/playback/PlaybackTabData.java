package edu.iastate.metnet.metaomgraph.playback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.iastate.metnet.metaomgraph.logging.ActionProperties;

/**
 * 
 * @author Harsha
 * <br/>
 *<p>
 * This is a bean class to store all the related information of a tab of the Reproducibility Dashboard Panel.
 * </p>
 * <h3>Variables :</h3>
 * <p>
 * <b>1. tabNumber -</b>  number of the tab
 * <b>2. logFileName -</b> log file name that will be used to populate the tab's play tree and Display table
 * <b>3. treeStructure -</b> the structure of the play tree of this tab
 * <b>4. actionObjects -</b> the list of action objects that will be populated in the play tree
 * <b>5. tabTree -</b> the JTree instance of the play tree for this tab
 * <b>6. tabTable -</b> the Action Display Table JTable instance for this tab
 * <b>7. includedSamplesTable -</b> the included samples table instance
 * <b>8. excludedSamplesTable -</b> the excluded samples table instance
 * </p>
 */
public class PlaybackTabData {

	private int tabNumber;
	private String logFileName;
	private HashMap<Integer,DefaultMutableTreeNode> treeStructure;
	private ArrayList<ActionProperties> actionObjects;
	private  JTree tabTree;
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
