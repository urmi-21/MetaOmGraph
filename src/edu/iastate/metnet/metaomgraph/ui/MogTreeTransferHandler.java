package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MogTreeTransferHandler extends TransferHandler {
	DefaultMutableTreeNode[] nodesToRemove;
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];

	public MogTreeTransferHandler() {
		try {
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
					+ javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound: " + e.getMessage());
		}
	}

	public String toString() {
		return getClass().getName();
	}

	public int getSourceActions(JComponent c) {

		return TransferHandler.MOVE;

	}

	public Transferable createTransferable(JComponent c) {
		JTree tree = (JTree) c;
		TreePath[] paths = tree.getSelectionPaths();

		if (paths != null) {
			// Make up a node array of copies for transfer and
			// another for/of the nodes that will be removed in
			// exportDone after a successful drop.
			List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
			List<DefaultMutableTreeNode> toRemove = new ArrayList<DefaultMutableTreeNode>();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
			DefaultMutableTreeNode copy = copy(node);
			copies.add(copy);
			toRemove.add(node);
			// urmi

			// don't move nodes that has child
			if (node.getChildCount() > 0) {
				JOptionPane.showMessageDialog(null, "Can't move node with children. Move all children first", "Error",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}

			// disable drop for root
			if (!node.isRoot()) {
				// if (! (node == tree.getModel().getChild(tree.getModel().getRoot(), 0) ) ) {
				for (int i = 1; i < paths.length; i++) {
					DefaultMutableTreeNode next = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
					// Do not allow higher level nodes to be added to list.
					if (next.getLevel() < node.getLevel()) {
						break;
					} else if (next.getLevel() > node.getLevel()) { // child node
						copy.add(copy(next));
						// node already contains child
					} else { // sibling
						copies.add(copy(next));
						toRemove.add(next);
					}
				}
				DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
				nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
				// System.out.println("torem size:"+toRemove.size());
				return new MogTreeTransferHandler.NodesTransferable(nodes);
			}
			// version 2 urmi
			// remove node and all its children
			/*
			 * System.out.println("tree path:"); for(int i=0;i<paths.length;i++) {
			 * System.out.println(paths[i]);
			 * System.out.println(paths[i].getLastPathComponent()); node =
			 * (DefaultMutableTreeNode) paths[i].getLastPathComponent(); toRemove.add(node);
			 * 
			 * }
			 * 
			 * if (!node.isRoot()) { System.out.println("torem size:"+toRemove.size());
			 * nodesToRemove = toRemove.toArray(new
			 * DefaultMutableTreeNode[toRemove.size()]);
			 * 
			 * }
			 */
			else {
				JOptionPane.showMessageDialog(null, "Can't move Root node.", "Error", JOptionPane.ERROR_MESSAGE);

			}
		}
		return null;
	}

	/** Defensive copy used in createTransferable. */
	private DefaultMutableTreeNode copy(TreeNode node) {
		return new DefaultMutableTreeNode(node);
	}

	public boolean canImport(TransferHandler.TransferSupport info) {
		
		if (!info.isDrop()) {
			return false;
		}
		info.setShowDropLocation(true);
		// urmi changed to move tree nodes
		// we only import Strings and tree nodes to move nodes in tree
		// if (!info.isDataFlavorSupported(DataFlavor.stringFlavor) &&
		// !info.isDataFlavorSupported(nodesFlavor)) {
		if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		// fetch the drop location
		JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
		TreePath path = dl.getPath();
		
		//System.out.println("p:"+path+"::wep");
		if (path == null)
			return false;
		// we don't support invalid paths or descendants of the names folder
		// if (path == null || namesPath.isDescendant(path)) {
		// return false;
		// }
		//do not drop to Root if it already has a child
		//(DefaultMutableTreeNode) dl.getChildCount();
		DefaultMutableTreeNode thisNode=(DefaultMutableTreeNode) path.getLastPathComponent();
		if(thisNode.toString().equals("Root") && thisNode.getChildCount()>0) {
			JOptionPane.showMessageDialog(null, "Root can have only one child...","Please check",JOptionPane.ERROR_MESSAGE);
			//JOptionPane.showMessageDialog(null, "path:"+path.toString());
		}
		
		return true;
	}

	private boolean haveCompleteNode(JTree tree) {
		int[] selRows = tree.getSelectionRows();
		TreePath path = tree.getPathForRow(selRows[0]);
		DefaultMutableTreeNode first = (DefaultMutableTreeNode) path.getLastPathComponent();
		int childCount = first.getChildCount();
		// first has children and no children are selected.
		if (childCount > 0 && selRows.length == 1)
			return false;
		// first may have children.
		for (int i = 1; i < selRows.length; i++) {
			path = tree.getPathForRow(selRows[i]);
			DefaultMutableTreeNode next = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (first.isNodeChild(next)) {
				// Found a child of first.
				if (childCount > selRows.length - 1) {
					// Not all children of first are selected.
					return false;
				}
			}
		}
		return true;
	}

	public boolean importData2(TransferHandler.TransferSupport info) {
		// if we can't handle the import, say so
		if (!canImport(info)) {
			return false;
		}

		// fetch the drop location
		JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();

		// fetch the path and child index from the drop location
		TreePath path = dl.getPath();
		int childIndex = dl.getChildIndex();

		// fetch the data and bail if this fails
		String data;
		try {
			data = (String) info.getTransferable().getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		String temparr[] = data.split("/n");
		System.out.println("tarr");
		for (int i = 0; i < temparr.length; i++) {
			System.out.println(temparr[i]);
		}

		for (int u = 0; u < temparr.length; u++) {
			data = temparr[u];
			System.out.println("now data:" + data);
			JTree tree = (JTree) info.getComponent();
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

			// if child index is -1, the drop was on top of the path, so we'll
			// treat it as inserting at the end of that path's list of children
			if (childIndex == -1) {
				childIndex = tree.getModel().getChildCount(path.getLastPathComponent());
			}

			int action = info.getDropAction();
			if (action == COPY_OR_MOVE) {
				return haveCompleteNode(tree);
			}

			// create a new node to represent the data and insert it into the model
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("sdasssdwwd");
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			// doesnt allows to add to root
			if (parentNode.toString() == "MetaOmGraph")
				return false;

			String childNode = "";
			boolean foundNode = false;
			for (int i = 0; i < parentNode.getChildCount(); i++) {
				childNode = parentNode.getChildAt(i).toString();
				if (childNode.equalsIgnoreCase(data)) {
					foundNode = true;
					break;
				}
			}
			if (!foundNode) {
				model.insertNodeInto(newNode, parentNode, childIndex);

				// make the new node visible and scroll so that it's visible
				tree.makeVisible(path.pathByAddingChild(newNode));
				tree.scrollRectToVisible(tree.getPathBounds(path.pathByAddingChild(newNode)));
				// prevents dropping on child nodes
				newNode.setAllowsChildren(false);
			} else
				return false;

			// model.removeAllElements();
			// model.insertElementAt("String " + (++count), 0);
			// end demo stuff
		}
		return true;
	}

	// urmi; handles multiple selection
	public boolean importData(TransferHandler.TransferSupport info) {
		// if we can't handle the import, say so
		if (!canImport(info)) {
			return false;
		}
		// fetch the drop location
		JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
		// fetch the path and child index from the drop location
		TreePath path = dl.getPath();
		int childIndex = dl.getChildIndex();
		// fetch the data and bail if this fails
		String data;
		try {
			data = (String) info.getTransferable().getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		// get all the strings in teparr
		String temparr[] = data.split("\n");
		// all noes to tree
		JTree tree = (JTree) info.getComponent();
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		// if child index is -1, the drop was on top of the path, so we'll
		// treat it as inserting at the end of that path's list of children
		if (childIndex == -1) {
			childIndex = tree.getModel().getChildCount(path.getLastPathComponent());
		}
		int action = info.getDropAction();
		if (action == COPY_OR_MOVE) {
			System.out.println("IN funccc)(&&");
			return haveCompleteNode(tree);
		}
		// create nodes
		DefaultMutableTreeNode[] newNodes = new DefaultMutableTreeNode[temparr.length];
		for (int i = 0; i < temparr.length; i++) {
			// System.out.println(temparr[i]);
			newNodes[i] = new DefaultMutableTreeNode(temparr[i]);
			// System.out.println(newNodes[i].toString()+")(&&");
		}
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		// doesnt allows to add to root
		/*
		 * if (parentNode.toString() == "MetaOmGraph") return false;
		 */

		for (int y = 0; y < newNodes.length; y++) {
			// check for existing node
			String childNode = "";
			boolean foundNode = false;
			for (int i = 0; i < parentNode.getChildCount(); i++) {
				childNode = parentNode.getChildAt(i).toString();
				if (childNode.equalsIgnoreCase(data)) {
					foundNode = true;
					break;
				}
			}
			if (!foundNode) {
				model.insertNodeInto(newNodes[y], parentNode, childIndex);

				// make the new node visible and scroll so that it's visible
				tree.makeVisible(path.pathByAddingChild(newNodes[y]));
				tree.scrollRectToVisible(tree.getPathBounds(path.pathByAddingChild(newNodes[y])));
				// prevents dropping on child nodes
				// newNodes[y].setAllowsChildren(false);
			}
		}
		return true;
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
		if ((action & MOVE) == MOVE) {
			JTree tree = (JTree) source;
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			// Remove nodes saved in nodesToRemove in createTransferable.
			for (int i = 0; i < nodesToRemove.length; i++) {
				model.removeNodeFromParent(nodesToRemove[i]);
			}
		}
	}

	public class NodesTransferable implements Transferable {
		DefaultMutableTreeNode[] nodes;

		public NodesTransferable(DefaultMutableTreeNode[] nodes) {
			this.nodes = nodes;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return nodes;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return nodesFlavor.equals(flavor);
		}
	}
}
