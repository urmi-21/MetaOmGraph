package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.Calendar;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;

/**
 * A JTable in which you can use drag and drop to re-order the rows.
 *
 * @author Nick Ransom
 *
 */
public class OrderableTable extends StripedTable implements DropTargetListener {

    private int dropRow;

    private NoneditableTableModel model;

    private static final int autoScrollMargin = 10;

    private long lastScrollOp;

    private int autoScrolls;

    private static final int scrollRate = 200;

    private static final int scrollAccelRate = 4;

    public OrderableTable(NoneditableTableModel model) {
        super(model);
        this.model = model;
        dropRow = -1;
        this.setDragEnabled(true);
        this.setDropTarget(new DropTarget(this, this));
        this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.setAutoscrolls(true);
        this.setRowSelectionAllowed(true);
        this.setColumnSelectionAllowed(false);
        lastScrollOp = 0;
        autoScrolls = 0;
        // this.setTransferHandler(new OrderTransferHandler());
    }

    public void setModel(TableModel newModel) {
        if (!(newModel instanceof NoneditableTableModel)) {
            System.err.println("Sorry, only NoneditableTableModels are accepted");
            return;
        }
        this.model=(NoneditableTableModel) newModel;
        super.setModel(newModel);
    }

    public void setSelectionMode(int selectionMode) {
        if ((selectionMode == ListSelectionModel.SINGLE_INTERVAL_SELECTION)
                || (selectionMode == ListSelectionModel.SINGLE_SELECTION))
            super.setSelectionMode(selectionMode);
        else {
            System.err
                    .println("OrderableTable only supports single selection and single interval selection.");
            return;
        }
    }

    public static void main(String[] args) {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame f = new JFrame("OrderableTable test");
        String[][] data = new String[100][2];
        for (int x = 0; x < 100; x++) {
            data[x][0] = x + "";
            data[x][1]=((int)(Math.random()*1000))+"";
        }
        String[] headers = { "number", "random" };
        NoneditableTableModel model = new NoneditableTableModel(data, headers);
        OrderableTable table = new OrderableTable(model);
        // table.setModel(model);
        // table.setDragEnabled(true);
        // table.setDropTarget(new DropTarget());
        f.getContentPane().add(new JScrollPane(table));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    public void dragOver(DropTargetDragEvent dtde) {
        dropRow = this.rowAtPoint(dtde.getLocation());
        int firstSelected = this.getSelectedRow();
        int lastSelected = firstSelected + getSelectedRowCount();
        if ((dropRow >= firstSelected) && (dropRow <= lastSelected)) {
            dropRow = -1;
        }
        doMove();
        Point p = dtde.getLocation();
        Rectangle viewable = getVisibleRect();
        if (p.y < autoScrollMargin + viewable.y) {
            int modifier = autoScrolls / scrollAccelRate + 1;
            if (modifier <= 0) {
                modifier = 1;
            }
            long time = Calendar.getInstance().getTimeInMillis();
            if (time - lastScrollOp > scrollRate / modifier) {
                scrollUp();
            }
        } else if (p.y > viewable.y + viewable.height - autoScrollMargin) {
            int modifier = autoScrolls / scrollAccelRate + 1;
            if (modifier <= 0) {
                modifier = 1;
            }
            long time = Calendar.getInstance().getTimeInMillis();
            if (time - lastScrollOp > scrollRate / modifier) {
                scrollDown();
            }
        } else {
            lastScrollOp = 0;
            autoScrolls = 0;
        }
    }

    private void scrollUp() {
        lastScrollOp = Calendar.getInstance().getTimeInMillis();
        autoScrolls++;
        Rectangle visible = this.getVisibleRect();
        Rectangle newVisible = new Rectangle(visible);
        newVisible.y -= getRowHeight();
        this.scrollRectToVisible(newVisible);
    }

    private void scrollDown() {
        lastScrollOp = Calendar.getInstance().getTimeInMillis();
        autoScrolls++;
        Rectangle visible = this.getVisibleRect();
        Rectangle newVisible = new Rectangle(visible);
        newVisible.y += getRowHeight();
        this.scrollRectToVisible(newVisible);
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    public void dragExit(DropTargetEvent dte) {

    }

    public void drop(DropTargetDropEvent dtde) {
        // doMove();
        dropRow = -1;
        lastScrollOp = 0;
        autoScrolls = 0;
    }

    private void doMove() {
        if (this.getSelectedRowCount() <= 0) {
            System.out.println("Nothing selected?!");
            return;
        }
        // int dropRow=this.rowAtPoint(dtde.getLocation());
        if (dropRow < 0) {
            // System.out.println("Didn't drop on a row?!");
            return;
        }
        boolean movingForward = dropRow > getSelectedRow();
        int[] selected = this.getSelectedRows();
        Object[][] data = new Object[selected.length][model.getColumnCount()];
        for (int row = 0; row < selected.length; row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                data[row][col] = model.getValueAt(selected[row], col);
            }
        }
        model.insertRowsBefore(data, dropRow);
        if (!movingForward) {
            this.setRowSelectionInterval(getSelectedRow() - 1, getSelectedRow() + getSelectedRowCount() - 2);
        }
        model.deleteRows(this.getSelectedRows());
        if (movingForward) {
            this.setRowSelectionInterval(dropRow - selected.length, dropRow - 1);
        } else {
            this.setRowSelectionInterval(dropRow, dropRow + selected.length - 1);
        }
    }

}