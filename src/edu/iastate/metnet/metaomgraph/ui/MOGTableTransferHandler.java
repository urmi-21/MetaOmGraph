package edu.iastate.metnet.metaomgraph.ui;

import java.awt.datatransfer.*;
import java.io.IOException;

import javax.activation.ActivationDataFlavor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class MOGTableTransferHandler extends TransferHandler {
	public static final DataFlavor FLAVOR = new ActivationDataFlavor(DefaultMutableTreeNode[].class,
			DataFlavor.javaJVMLocalObjectMimeType, "Array of DefaultMutableTreeNode");
	private int[] indices = null;
	private int addIndex = -1; // Location where items were added
	private int addCount = 0; // Number of items added.
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
	
	// change this function work with multiple selection
		protected Transferable createTransferable(JComponent c) {
			JList list = (JList) c;

			indices = list.getSelectedIndices();
			Object[] values = list.getSelectedValues();

			

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

  /*
    public boolean importData(JComponent comp, Transferable t) {
        if(canImport(comp, t.getTransferDataFlavors())) {
            FileStore data = null;
            try {
                data = (FileStore)t.getTransferData(fileStoreFlavor);
            } catch(UnsupportedFlavorException ufe) {
                System.out.println("unsupported flavor for " + fileStoreFlavor +
                                   ": " + ufe.getMessage());
                return false;
            } catch(IOException ioe) {
                System.out.println("io error: " + ioe.getMessage());
                return false;
            }
            JTable table = (JTable)comp;
            int row = table.getSelectedRow();
            if(table.getValueAt(row, 0) == null ) {
                table.setValueAt(data.fileName, row, 0);
                table.setValueAt(data.file, row, 1);
            } else {
                addData(table, data, row);
            }
            return true;
        }
        return false;
    }
 
    private void addData(JTable table, FileStore fileStore, int row) {
        // Find next empty row.
        int emptyRow = getNextEmptyRow(table, row);
        if(emptyRow != -1) {
            shiftValuesDown(table, row, emptyRow);
        } else {
            row = table.getRowCount();
            DefaultTableModel model = (DefaultTableModel)table.getModel();
            model.setRowCount(table.getRowCount()+1);
        }
        table.setValueAt(fileStore.fileName, row, 0);
        table.setValueAt(fileStore.file, row, 1);
    }
  
    private int getNextEmptyRow(JTable table, int start) {
        for(int j = start; j < table.getRowCount(); j++) {
            if(table.getValueAt(j, 0) == null)
                return j;
        }
        return -1;
    }
  
    private void shiftValuesDown(JTable table, int start, int end) {
        for(int j = end; j > start; j--) {
            table.setValueAt(table.getValueAt(j-1, 0), j, 0);
            table.setValueAt(table.getValueAt(j-1, 1), j, 1);
        }
    }
    */
}