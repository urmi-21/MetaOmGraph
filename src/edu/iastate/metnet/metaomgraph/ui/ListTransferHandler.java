package edu.iastate.metnet.metaomgraph.ui;

import javax.activation.ActivationDataFlavor;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ListTransferHandler extends TransferHandler {
	private int[] indices = null;
	private int addIndex = -1; // Location where items were added
	private int addCount = 0; // Number of items added.
	// urmi
	public static final DataFlavor FLAVOR = new ActivationDataFlavor(DefaultMutableTreeNode[].class,
			DataFlavor.javaJVMLocalObjectMimeType, "Array of DefaultMutableTreeNode");

	/**
	 * only support importing strings.
	 */
	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		// Check for String flavor
		// if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
		// return false;
		// }
		if (!info.isDataFlavorSupported(FLAVOR)) {
			return false;
		}
		return true;
	}

	/**
	 * Bundle up the selected items in a single list for export. Each line is
	 * separated by a newline.
	 */
	// change this function work with multiple selection
	@Override
	protected Transferable createTransferable(JComponent c) {
		JList list = (JList) c;
		indices = list.getSelectedIndices();
		Object[] values = list.getSelectedValuesList().toArray();
		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < values.length; i++) {
			Object val = values[i];
			buff.append(val == null ? "" : val.toString());
			if (i != values.length - 1) {
				buff.append("\n");
			}
		}

		return new StringSelection(buff.toString());
	}

	/**
	 * We support both copy and move actions.
	 */
	// set to move to remove items from list after drop
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
		// return TransferHandler.COPY;
	}

	/**
	 * Perform the actual import. This demo only supports drag and drop.
	 */
	@Override
	public boolean importData(TransferHandler.TransferSupport info) {

		// System.out.println("in import");
		if (!info.isDrop()) {
			// System.out.println("ret1");
			return false;
		}

		// urmi
		if (!canImport(info)) {
			// System.out.println("ret2");
			return false;
		}

		JList list = (JList) info.getComponent();
		DefaultListModel listModel = (DefaultListModel) list.getModel();
		JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
		int index = dl.getIndex();
		boolean insert = dl.isInsert();

		// Get the string that is being dropped.
		Transferable t = info.getTransferable();
		String data = "";
		DefaultMutableTreeNode[] nodes = null;

		try {
			// System.out.println("importing");
			nodes = (DefaultMutableTreeNode[]) t.getTransferData(FLAVOR);
			// System.out.println(nodes[0].toString());
		} catch (UnsupportedFlavorException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			//System.out.println("Data size:"+nodes.length+":end");
			for (int i = 0; i < nodes.length; i++) {
				
				data += nodes[i].toString() + "\n";
			}
			//System.out.println("Data insret:"+data+":end");
			// data = nodes[0].toString();
		} catch (Exception e) {
			return false;
		}

		// Extract transfer data.
		

		// Wherever there is a newline in the incoming data,
		// break it into a separate item in the list.
		
		String[] values = data.split("\n");

		addIndex = index;
		addCount = values.length;

		// Perform the actual import.

		for (int i = 0; i < values.length; i++) {
			//System.out.println(i);
			boolean exists = false;
			// print existing list
			// if item exists do not import
			for (int y = 0; y < listModel.size(); y++) {
				//System.out.println(listModel.get(y).toString() + "\t" + values[i]);
				// System.out.println("exists error");
				if (values[i].equals(listModel.get(y).toString())) {
					// System.out.println(listModel.get(y).toString()+"\t"+values[i]);
					//System.out.println("exists error");
					exists = true;
					break;
				}
			}

			if (exists == false) {

				if (insert) {
					listModel.add(index++, values[i]);
				} else {
					// If the items go beyond the end of the current
					// list, add them in.

					if (index < listModel.getSize()) {
						listModel.set(index++, values[i]);
					} else {
						listModel.add(index++, values[i]);
					}

				}
			}
		}
		return true;
	}

	/**
	 * Remove the items moved from the list.
	 */
	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		JList source = (JList) c;
		DefaultListModel listModel = (DefaultListModel) source.getModel();

		if (action == TransferHandler.MOVE) {
			for (int i = indices.length - 1; i >= 0; i--) {
				listModel.remove(indices[i]);
			}
		}

		indices = null;
		addCount = 0;
		addIndex = -1;

	}
}