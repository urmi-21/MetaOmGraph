package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.activation.ActivationDataFlavor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

public class TableTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;
	private int[] selectedInd = null;
	// urmi
	public static final DataFlavor FLAVOR = new ActivationDataFlavor(DefaultMutableTreeNode[].class,
			DataFlavor.javaJVMLocalObjectMimeType, "Array of DefaultMutableTreeNode");

	public TableTransferHandler() {
	}




	@Override
	protected Transferable createTransferable(JComponent source) {
		// Create the transferable
		// Because I'm hacking a little, I've included the source table...
		JTable table = (JTable) source;
		selectedInd = table.getSelectedRows();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < selectedInd.length; i++) {
			String val = table.getValueAt(selectedInd[i], 0).toString();
			buff.append(val == null ? "" : val);
			if (i != selectedInd.length - 1) {
				buff.append("\n");
			}
		}
		return new StringSelection(buff.toString());
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
	}

	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
		// return TransferHandler.COPY;
	}
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

	@Override
	public boolean importData(TransferSupport support) {
		// Import failed for some reason...
		boolean imported = false;
		// Only import into JTables...
		Component comp = support.getComponent();
		if (comp instanceof JTable) {
			JTable target = (JTable) comp;
			// Need to know where we are importing to...
			DropLocation dl = support.getDropLocation();
			Point dp = dl.getDropPoint();
			int dropCol = target.columnAtPoint(dp);
			int dropRow = target.rowAtPoint(dp);
			// Get the Transferable at the heart of it all
			Transferable t = support.getTransferable();
			//do transfer

		}
		return imported;
	}
}