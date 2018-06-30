package edu.iastate.metnet.metaomgraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.dizitart.no2.Document;

import edu.iastate.metnet.metaomgraph.ui.MogTreeTransferHandler;

//class stores the metadata tree structure for data read from .csv or.xls file
public class MetadataTreeStructure {

	private JTree tree = null;
	private DefaultTreeModel treeModel = null;
	private List<String> headers = null;
	private List<org.dizitart.no2.Document> metadata = null;
	private static String exp = null;
	private static String samp = null;
	private static String chip = null;

	public MetadataTreeStructure(JTree jt) {
		// TODO Auto-generated constructor stub
		tree = jt;
		treeModel = getDefaultTreeModel();
		tree = new JTree(treeModel);
	}
	public MetadataTreeStructure() {
		
	}
	public MetadataTreeStructure(List<String> headers, List<Document> metadata, String exp, String samp, String run) {
		// TODO Auto-generated constructor stub
		this.headers = new ArrayList<>();
		this.headers = headers;
		this.metadata = metadata;
		this.exp = exp;
		this.samp=samp;
		this.chip = run;
		// create a Default tree and list
		treeModel = getDefaultTreeModel();
		tree = new JTree(treeModel);
		tree.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setDragEnabled(true);
		tree.setTransferHandler(new MogTreeTransferHandler());
		expandTree(tree);
	}
	
	public String getRuncol() {
		//JOptionPane.showMessageDialog(null, "returning runcol: "+this.tree.getModel().getChild(this.tree.getModel().getRoot(), 1).toString());
		return this.tree.getModel().getChild(this.tree.getModel().getRoot(), 1).toString();
		//return this.chip;
	}
	public String getStudycol() {
		//JOptionPane.showMessageDialog(null, "returning studycol: "+this.tree.getModel().getChild(this.tree.getModel().getRoot(), 0).toString());
		return this.tree.getModel().getChild(this.tree.getModel().getRoot(), 0).toString();
		//return this.exp;
	}

	private void expandTree(JTree tree) {

		// System.out.println("call expand tree");
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Enumeration e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.isLeaf())
				continue;
			int row = tree.getRowForPath(new TreePath(node.getPath()));
			tree.expandRow(row);
		}
	}

	private static DefaultTreeModel getDefaultTreeModel() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("MetaOmGraph");
		DefaultMutableTreeNode parent;
		DefaultMutableTreeNode parentexp;
		DefaultMutableTreeNode parentsamp;
		DefaultMutableTreeNode parentchip;
		parentexp = new DefaultMutableTreeNode(exp);
		root.add(parentexp);
		parentsamp = new DefaultMutableTreeNode(samp);
		root.add(parentsamp);
		//parentexp.add(parentsamp);
		parentchip = new DefaultMutableTreeNode(chip);
		root.add(parentchip);
		//parentsamp.add(parentchip);
		return new DefaultTreeModel(root);
	}

	public JTree getTree() {
		return tree;
	}

	public List<String> getList() {
		return headers;
	}
	
	public void setTree(JTree jt) {
		this.tree=jt;
	}
	
	public void resetTree() {
		treeModel = getDefaultTreeModel();
		tree = new JTree(treeModel);
		tree.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setDragEnabled(true);
		tree.setTransferHandler(new MogTreeTransferHandler());
		expandTree(tree);
	}
}
