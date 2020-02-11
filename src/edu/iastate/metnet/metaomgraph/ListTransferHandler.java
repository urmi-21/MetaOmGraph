package edu.iastate.metnet.metaomgraph;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

public class ListTransferHandler extends TransferHandler {
    private Transferable transferable;
    private static ListTransferHandler _instance;

    public static class DraggableRows implements Transferable {
        private int[] rows;
        private static DataFlavor rowFlavor;

        public DraggableRows(int[] rows) {
            this.rows = rows;
        }


        public static DataFlavor getRowFlavor() {
            if (rowFlavor == null)

                rowFlavor = new DataFlavor(int[].class,
                        "Draggable Rows");
            return rowFlavor;
        }

        @Override
		public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{getRowFlavor()};
        }

        @Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(getRowFlavor());
        }

        @Override
		public Object getTransferData(DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
            return null;
        }

        public int[] getTransferData() {
            return rows;
        }
    }


    @Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        boolean result = super.canImport(comp, transferFlavors);
        System.out.println("canImport? " + result);
        return result;
    }

    @Override
	protected Transferable createTransferable(JComponent c) {
        if ((c instanceof ListTree)) {
            System.out.println("Creating a ListTree transferable!");
            ListTree tree = (ListTree) c;
            Object selected = tree.getSelectionPath().getLastPathComponent();
            if ((selected instanceof ListTree.DraggableNode)) {
                transferable = ((ListTree.DraggableNode) selected);
            } else {
                System.out.println("Dragging a non-draggable node!");
                transferable = null;
            }
        } else if ((c instanceof JTable)) {
            System.out.println("Creating a transferable rows!");
            int[] rows = ((JTable) c).getSelectedRows();
            transferable = new DraggableRows(rows);
        } else {
            transferable = super.createTransferable(c);
        }
        System.out.println("Transferable: " + transferable.getClass());
        return transferable;
    }


    private ListTransferHandler() {
    }

    public Transferable getLastMadeTransferable() {
        return transferable;
    }

    @Override
	protected void exportDone(JComponent source, Transferable data, int action) {
        System.out.println("Export done!  Transferable: " + data);
        if (((data instanceof ListTree.DraggableNode)) &&
                (action == 2)) {
            ListTree tree = (ListTree) source;
            ListTree.DraggableNode node = (ListTree.DraggableNode) data;
            System.out.println("Removing!");

            tree.getDefaultModel().removeNodeFromParent(node);
        }

        super.exportDone(source, data, action);
    }

    @Override
	public int getSourceActions(JComponent c) {
        if ((c instanceof ListTree)) {
            return 3;
        }
        return super.getSourceActions(c);
    }

    @Override
	public boolean importData(JComponent comp, Transferable t) {
        return super.importData(comp, t);
    }

    public static ListTransferHandler getInstance() {
        if (_instance == null)
            _instance = new ListTransferHandler();
        return _instance;
    }
}
