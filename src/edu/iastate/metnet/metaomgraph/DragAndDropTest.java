package edu.iastate.metnet.metaomgraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class DragAndDropTest {
    public JComponent makeUI() {
        DefaultListModel<Thumbnail> m = new DefaultListModel<>();
        for(String s: Arrays.asList("error","information","question","warning")) {
            m.addElement(new Thumbnail(s));
        }

        JList<Thumbnail> list = new JList<>(m);
        list.getSelectionModel().setSelectionMode(
        ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setTransferHandler(new ListItemTransferHandler());
        list.setDropMode(DropMode.INSERT);
        list.setDragEnabled(true);
        //http://java-swing-tips.blogspot.jp/2008/10/rubber-band-selection-drag-and-drop.html
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(0);
        list.setFixedCellWidth(80);
        list.setFixedCellHeight(80);
        list.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        list.setCellRenderer(new ListCellRenderer<Thumbnail>() {
            private final JPanel p = new JPanel(new BorderLayout());
            private final JLabel icon = new JLabel((Icon)null, JLabel.CENTER);
            private final JLabel label = new JLabel("", JLabel.CENTER);

            @Override
            public Component getListCellRendererComponent(
                    JList<? extends Thumbnail> list, Thumbnail value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                icon.setIcon(value.icon);
                label.setText(value.name);
                label.setForeground(isSelected ? list.getSelectionForeground()
                        : list.getForeground());
                p.add(icon);
                p.add(label, BorderLayout.SOUTH);
                p.setBackground(isSelected ? list.getSelectionBackground()
                        : list.getBackground());
                return p;
            }
        });
        return new JScrollPane(list);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> createAndShowGUI());
    }

    public static void createAndShowGUI() {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.getContentPane().add(new DragAndDropTest().makeUI());
        f.setSize(320, 240);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

class Thumbnail {
    public final String name;
    public final Icon icon;
    public Thumbnail(String name) {
        this.name = name;
        this.icon = UIManager.getIcon("OptionPane."+name+"Icon");
    }
}

//@camickr already suggested above.
//http://docs.oracle.com/javase/tutorial/uiswing/dnd/dropmodedemo.html
@SuppressWarnings("serial")
class ListItemTransferHandler extends TransferHandler {
    private final DataFlavor localObjectFlavor;
      private Object[] transferedObjects = null;
    public ListItemTransferHandler() {
        localObjectFlavor = new ActivationDataFlavor(
        Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Transferable createTransferable(JComponent c) {
        @SuppressWarnings("unchecked")
        JList<Thumbnail> list = (JList<Thumbnail>) c;
        indices = list.getSelectedIndices();
        transferedObjects = list.getSelectedValues();
        return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
    }

    @Override
    public boolean canImport(TransferSupport info) {
        if(!info.isDrop() || !info.isDataFlavorSupported(localObjectFlavor))
            return false;
        return true;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE; //TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferSupport info) {
        if(!canImport(info))
            return false;

        @SuppressWarnings("unchecked")
        JList<Object> target = (JList<Object>) info.getComponent();
        JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
        DefaultListModel<Object> listModel = (DefaultListModel<Object>) target.getModel();
        int index = dl.getIndex();
        int max = listModel.getSize();

        if(index<0 || index>max)
            index = max;

        addIndex = index;

        try {
            Object[] values = (Object[]) info.getTransferable().getTransferData(
            localObjectFlavor);
            addCount = values.length;
            for(int i = 0; i < values.length; i++) {
                int idx = index++;
                listModel.add(idx, values[i]);
                target.addSelectionInterval(idx, idx);
            }
            return true;
        } catch(UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        cleanup(c, action == MOVE);
    }

    private void cleanup(JComponent c, boolean remove) {
        if(remove && indices != null) {
            @SuppressWarnings("unchecked")
            JList<Thumbnail> source = (JList<Thumbnail>) c;
            DefaultListModel<Thumbnail> model = (DefaultListModel<Thumbnail>) source.getModel();
            if(addCount > 0) {
                //http://java-swing-tips.googlecode.com/svn/trunk/DnDReorderList/src/java/example/MainPanel.java
                for(int i=0; i<indices.length; i++) {
                    if(indices[i]>=addIndex)
                        indices[i] += addCount;
                }
            }

            for(int i = indices.length - 1; i >= 0; i--) {
                model.remove(indices[i]);
            }
        }

        indices  = null;
        addCount = 0;
        addIndex = -1;
    }

    private int[] indices = null;
    private int addIndex  = -1; //Location where items were added
    private int addCount  = 0;  //Number of items added.

}
