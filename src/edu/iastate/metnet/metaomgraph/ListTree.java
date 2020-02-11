package edu.iastate.metnet.metaomgraph;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ListTree extends JTree implements DropTargetListener, java.awt.dnd.DragSourceListener, java.awt.dnd.DragGestureListener, Autoscroll {
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;
    private DragSource dragSource;
    private DropTarget dropTarget;
    private Rectangle lastDrawnRect;
    private BufferedImage _imgGhost;
    private Rectangle _raGhost;
    private Point _ptOffset;
    private boolean autoSort;
    private ListTransferHandler transferHandler;
    private static final int autoScrollMargin = 5;
    private static final int INSERT_ABOVE = 0;
    private static final int INSERT_BELOW = 1;
    private static final int MERGE = 2;
    private static final int NOTHING = -1;

    public static class DraggableNode extends DefaultMutableTreeNode implements Transferable, Serializable {
        private static DataFlavor nodeFlavor;

        public static DataFlavor getNodeFlavor() {
            if (nodeFlavor == null) nodeFlavor = new DataFlavor(DraggableNode.class, "Draggable Node");
            return nodeFlavor;
        }

        @Override
		public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{getNodeFlavor()};
        }

        @Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(getNodeFlavor());
        }

        @Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return this;
        }

        public DraggableNode() {
        }

        public DraggableNode(Object userObject, boolean allowsChildren) {
            super(allowsChildren);
        }

        public DraggableNode(Object userObject) {
            super();
        }
    }


    public boolean isAutosort() {
        return autoSort;
    }

    public void setAutosort(boolean autosort) {
        this.autoSort = autosort;
        if (autosort)  resort();
    }

    public ListTree() {
        super(new DefaultMutableTreeNode("Lists"));
        treeModel = ((DefaultTreeModel) getModel());
        treeModel.setAsksAllowsChildren(true);
        root = ((DefaultMutableTreeNode) treeModel.getRoot());
        root.setAllowsChildren(true);
        root.add(new DefaultMutableTreeNode(GeneList.getCompleteList(), false));
        root.add(new DraggableNode("words"));
        dropTarget = new DropTarget(this, this);

        dropTarget.isActive();
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, 3, this);
        transferHandler = ListTransferHandler.getInstance();
        setTransferHandler(transferHandler);
        autoSort = true;
    }

    public void addFolder(String folderName) {
        TreePath path = getSelectionPath();

        DefaultMutableTreeNode parent;
        if (path == null) {
            parent = root;
        } else { //DefaultMutableTreeNode parent;
            if (((TreeNode) path.getLastPathComponent()).getAllowsChildren()) {
                parent = (DefaultMutableTreeNode) path.getLastPathComponent();
            } else
                parent = (DefaultMutableTreeNode) path.getParentPath().getLastPathComponent();
        }
        addObject(parent, folderName, true);
    }

    public void addListAtSelection(GeneList list) {
        TreePath path = getSelectionPath();
        DefaultMutableTreeNode parent;
        if (path == null) {
            parent = root;
        } else { //DefaultMutableTreeNode parent;
            if (((TreeNode) path.getLastPathComponent()).getAllowsChildren()) {
                parent = (DefaultMutableTreeNode) path.getLastPathComponent();
            } else
                parent = (DefaultMutableTreeNode) path.getParentPath().getLastPathComponent();
        }
        addObject(parent, list, false);
    }

    public void addFolderToRoot(String folderName) {
        DraggableNode folder = new DraggableNode(folderName);
        root.add(folder);
    }

    public void deleteSelected() {
        TreePath currentSelection = getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentSelection
                    .getLastPathComponent();
            MutableTreeNode parent = (MutableTreeNode) currentNode.getParent();
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }
    }

    private int findInsertionTarget(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        if (parent.getChildCount() == 0)
            return 0;
        int x = 0;
        if (parent.isRoot()) x++;
        String inserting = child.getUserObject().toString().toLowerCase();

        String search = ((DefaultMutableTreeNode) parent.getChildAt(x)).getUserObject().toString().toLowerCase();
        while ((x < parent.getChildCount()) && (search.compareTo(inserting) < 0)) {
            x++;
            if (x < parent.getChildCount()) search = ((DefaultMutableTreeNode) parent.getChildAt(x)).getUserObject().toString().toLowerCase();
        }
        if ((parent.isRoot()) && (x == 0)) {
            System.out.println("Tried to insert " + inserting + " above the Complete List");
            x = 1;
        } else {
            System.out.println("Inserting " + inserting + " at " + x + ", right before " + search);
        }
        return x;
    }

    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean allowsChildren) {
        return addObject(parent, child, parent.getChildCount(), allowsChildren);
    }

    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, int location, boolean allowsChildren) {
        DraggableNode childNode;
        //DraggableNode childNode;
        if ((child instanceof DraggableNode)) {
            childNode = (DraggableNode) child;
        } else
            childNode = new DraggableNode(child, allowsChildren);
        if (parent == null) parent = root;
        if (autoSort) location = findInsertionTarget(parent, childNode);
        if ((parent.isRoot()) && (location == 0)) location = 1;
        treeModel.insertNodeInto(childNode, parent, location);
        scrollPathToVisible(new TreePath(childNode.getPath()));

        return childNode;
    }

    @Override
	public void dragEnter(DropTargetDragEvent droptargetdragevent) {
    }

    private void nodeDragOver(DropTargetDragEvent dtde) {
        Point pt = dtde.getLocation();
        Graphics2D g2 = (Graphics2D) getGraphics();
        if (!DragSource.isDragImageSupported()) {
            if (_raGhost != null) paintImmediately(_raGhost.getBounds());

            _raGhost = new Rectangle(pt.x - _ptOffset.x, pt.y - _ptOffset.y, _imgGhost.getWidth(), _imgGhost.getHeight());
            g2.drawImage(_imgGhost, java.awt.geom.AffineTransform.getTranslateInstance(_raGhost.getX(), _raGhost.getY()), null);
        }
        g2.dispose();
    }

    private void rowDragOver(DropTargetDragEvent dtde) {
        dtde.equals(null);
    }

    @Override
	public void dragOver(DropTargetDragEvent dtde) {
        if ((transferHandler.getLastMadeTransferable() instanceof DraggableNode)) {
            nodeDragOver(dtde);
        } else if ((transferHandler.getLastMadeTransferable() instanceof ListTransferHandler.DraggableRows))
            rowDragOver(dtde);
        Point pt = dtde.getLocation();
        TreePath path = getClosestPathForLocation(pt.x, pt.y);
        Rectangle bounds = getPathBounds(path);
        bounds.x -= 1;
        bounds.width += 1;
        int dropDest = getDropDestination(path, pt);
        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.setColor(getBackground());
        if (lastDrawnRect != null) g2d.draw(lastDrawnRect);
        if (dropDest != -1) {
            if (dtde.getDropAction() == 1) {
                setCursor(DragSource.DefaultCopyDrop);
            } else
                setCursor(DragSource.DefaultMoveDrop);
            if (dropDest == 0) {
                bounds = new Rectangle(0, bounds.y, getWidth(), 1);
            } else if (dropDest == 1)
                bounds = new Rectangle(0, bounds.y + bounds.height, getWidth(), 1);
            g2d.setColor(Color.BLACK);
            g2d.draw(bounds);
            lastDrawnRect = bounds;
        }
        g2d.dispose();
    }

    @Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
        System.out.println("Changed to: " + dtde.getDropAction());
        if (dtde.getDropAction() == 1) {
            setCursor(DragSource.DefaultCopyDrop);
        } else
            setCursor(DragSource.DefaultMoveDrop);
    }

    @Override
	public void drop(DropTargetDropEvent dtde) {
        setCursor(Cursor.getDefaultCursor());
        if (lastDrawnRect != null) {
            Graphics2D g2d = (Graphics2D) getGraphics();
            g2d.setXORMode(Color.GRAY);
            g2d.draw(lastDrawnRect);
            lastDrawnRect = null;
            g2d.dispose();
        }
        Point dropPoint = dtde.getLocation();
        TreePath dropPath = getClosestPathForLocation(dropPoint.x, dropPoint.y);
        DefaultMutableTreeNode destNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
        int dropDest = getDropDestination(dropPath, dropPoint);
        if (dropDest == -1) {
            dtde.dropComplete(false);
            repaint();
            return;
        }
        DraggableNode droppedNode = null;
        System.out.println("Flavors:");
        DataFlavor[] flavors = dtde.getTransferable().getTransferDataFlavors();
        for (int x = 0; x < flavors.length; x++) {
            System.out.println(flavors[x]);
        }
        try {
            System.out.println(dtde.getTransferable().getTransferData(DraggableNode.getNodeFlavor()));
        } catch (UnsupportedFlavorException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.flush();
        try {
            droppedNode = (DraggableNode) dtde.getTransferable().getTransferData(DraggableNode.getNodeFlavor());
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (droppedNode == null) {
            dtde.dropComplete(false);
            return;
        }
        if (dropDest == 2) {
            if (destNode.getAllowsChildren()) {
                int location = destNode.getChildCount();
                if (dtde.getDropAction() == 2) { moveNode(droppedNode, destNode, location); }
                else copyNode(droppedNode, destNode, location);
            }
            else {
                GeneList draggedList = (GeneList) droppedNode.getUserObject();
                GeneList dropList = (GeneList) destNode.getUserObject();
                JPanel messagePanel = new JPanel(new BorderLayout());
                messagePanel.add(new JLabel("Are you sure you want to merge " + draggedList + " into " + dropList + "?"), "First");
                JCheckBox delBox = new JCheckBox("Delete " + draggedList + " after merging");
                delBox.setSelected(dtde.getDropAction() == 2);
                messagePanel.add(delBox, "Last");
                int result = JOptionPane.showConfirmDialog(null, messagePanel, "Merge lists", 2, 3);
                if (result == 0) {
                    dropList.merge(draggedList);
                    if (delBox.isSelected()) treeModel.removeNodeFromParent(droppedNode);
                    setSelectionPath(new TreePath(destNode.getPath()));
                }
            }
        } else {
            DefaultMutableTreeNode newParent = (DefaultMutableTreeNode) destNode.getParent();
            int location = newParent.getIndex(destNode);
            if (dropDest == 1) location++;
            if (dtde.getDropAction() == 2) { moveNode(droppedNode, newParent, location);
            } else copyNode(droppedNode, newParent, location);
        }
        dtde.dropComplete(true);
    }


    @Override
	public void dragExit(DropTargetEvent droptargetevent) {
    }


    @Override
	public void dragEnter(DragSourceDragEvent dragsourcedragevent) {
    }


    @Override
	public void dragOver(DragSourceDragEvent dragsourcedragevent) {
    }


    @Override
	public void dropActionChanged(DragSourceDragEvent dragsourcedragevent) {
    }

    @Override
	public void dragExit(DragSourceEvent dragsourceevent) {
    }

    @Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
        System.out.println("done");

        repaint();
    }

    @Override
	public void dragGestureRecognized(DragGestureEvent dge) {
        Point ptDragOrigin = dge.getDragOrigin();
        System.out.println("reckognized");
        if (getSelectionPath() == null) {
            System.out.println("nothing selected");
            return;
        }
        if (!(getSelectionPath().getLastPathComponent() instanceof DraggableNode)) {
            System.out.println("Not a draggable node!");
            return;
        }
        DraggableNode draggingNode = (DraggableNode) getSelectionPath().getLastPathComponent();
        JLabel lbl = (JLabel) getCellRenderer().getTreeCellRendererComponent(this, draggingNode, false, isExpanded(new TreePath(draggingNode.getPath())), draggingNode.isLeaf(), 0, false);
        Rectangle raPath = getPathBounds(new TreePath(draggingNode.getPath()));
        _ptOffset = new Point(ptDragOrigin.x - raPath.x, ptDragOrigin.y - raPath.y);

        lbl.setSize((int) raPath.getWidth(), (int) raPath.getHeight());
        _imgGhost = new BufferedImage((int) raPath.getWidth(), (int) raPath.getHeight(), 3);
        Graphics2D g2 = _imgGhost.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(2, 0.5F));
        lbl.paint(g2);
        Icon icon = lbl.getIcon();
        int nStartOfText = icon != null ? icon.getIconWidth() + lbl.getIconTextGap() : 0;
        g2.setComposite(AlphaComposite.getInstance(10, 0.5F));
        g2.setPaint(new java.awt.GradientPaint(nStartOfText, 0.0F, java.awt.SystemColor.controlShadow, lbl.getWidth(), 0.0F, new Color(255, 255, 255, 0)));
        g2.fillRect(nStartOfText, 0, getWidth(), _imgGhost.getHeight());
        g2.dispose();

        transferHandler.exportAsDrag(this, dge.getTriggerEvent(),2);
    }

    @Override
	public Insets getAutoscrollInsets() {
        Rectangle outer = getBounds();
        Rectangle inner = getParent().getBounds();

        return new Insets((inner.y - outer.y) + autoScrollMargin,
                (inner.x - outer.x) + autoScrollMargin, (outer.height
                - inner.height - inner.y)
                + outer.y + autoScrollMargin, (outer.width
                - inner.width - inner.x)
                + outer.x + autoScrollMargin);
    }

    @Override
	public void autoscroll(Point p) {
        int realrow = getRowForLocation(p.x, p.y);
        Rectangle outer = getBounds();
        realrow = p.y + outer.y > 5 ? realrow >= getRowCount() - 1 ? realrow : realrow + 1 : realrow >= 1 ? realrow - 1 : 0;
        scrollRowToVisible(realrow);
    }

    public void resort() {
        resortThisNode(root, true);
    }

    public void resortThisNode(DefaultMutableTreeNode node, boolean sortSubDirs) {
        if (!node.getAllowsChildren())
            return;
        DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[node.getChildCount()];
        for (int x = children.length - 1; x >= 0; x--) {
            children[x] = ((DefaultMutableTreeNode) node.getChildAt(x));
            node.remove(x);
        }

        for (int x = 0; x < children.length; x++) {
            addObject(node, children[x], children[x].getAllowsChildren());
            if ((children[x].getAllowsChildren()) && (sortSubDirs)) {
                resortThisNode(children[x], true);
            }
        }
    }

    private boolean copyNode(DraggableNode copyMe, DefaultMutableTreeNode newParent, int location) {
        if (newParent.isNodeAncestor(copyMe)) return false;
        DraggableNode newNode = cloneNode(copyMe);
        if (autoSort) location = findInsertionTarget(newParent, newNode);
        treeModel.insertNodeInto(newNode, newParent, location);
        scrollPathToVisible(new TreePath(newNode.getPath()));
        return true;
    }

    private boolean moveNode(DraggableNode moveMe, DefaultMutableTreeNode newParent, int location) {
        System.out.println("Moving! " + moveMe.getClass());
        System.out.println("Old parent: " + moveMe.getParent());
        if (copyNode(moveMe, newParent, location)) {
            System.out.println("Root? " + moveMe.isRoot());
            moveMe.setUserObject("I should be deleted!");
            ((DefaultMutableTreeNode) moveMe.getParent()).remove(moveMe);
            return true;
        }
        System.out.println("move failed");
        return false;
    }

    private DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode cloneMe) {
        DefaultMutableTreeNode cloned = new DefaultMutableTreeNode(cloneMe.getUserObject(), cloneMe.getAllowsChildren());
        for (int x = 0; x < cloneMe.getChildCount(); x++) {
            if ((cloneMe.getChildAt(x) instanceof DraggableNode)) {
                cloned.add(cloneNode((DraggableNode) cloneMe.getChildAt(x)));
            } else {
                cloned.add(cloneNode((DefaultMutableTreeNode) cloneMe.getChildAt(x)));
            }
        }
        return cloned;
    }

    private DraggableNode cloneNode(DraggableNode cloneMe) {
        DraggableNode cloned = new DraggableNode(cloneMe.getUserObject(), cloneMe.getAllowsChildren());
        for (int x = 0; x < cloneMe.getChildCount(); x++) {
            if ((cloneMe.getChildAt(x) instanceof DraggableNode)) {
                cloned.add(cloneNode((DraggableNode) cloneMe.getChildAt(x)));
            } else {
                cloned.add(cloneNode((DefaultMutableTreeNode) cloneMe.getChildAt(x)));
            }
        }
        return cloned;
    }

    private int getNodeDropDest(TreePath destPath, Point pt,
                                DraggableNode draggingNode) {
        if (draggingNode == null) {
            System.out.println("bad source");
            return NOTHING;
        }
        if (destPath.getLastPathComponent().equals(draggingNode)) return NOTHING;
        if (autoSort) return MERGE;
        if (((DefaultMutableTreeNode) destPath.getLastPathComponent()).getUserObject().equals(GeneList.getCompleteList()))
            return INSERT_ABOVE;
        if (((DefaultMutableTreeNode) destPath.getLastPathComponent()).isRoot())
            return MERGE;
        Rectangle bounds;
        if (draggingNode.getAllowsChildren() && !((DefaultMutableTreeNode) destPath.getLastPathComponent()).getAllowsChildren()) {
            bounds = getPathBounds(destPath);
            return pt.y >= bounds.getHeight() / 2D + bounds.y ? INSERT_BELOW : INSERT_ABOVE;
        }
        bounds = getPathBounds(destPath);
        double thirdHeight = bounds.getHeight() / 3D;
        if (pt.y < thirdHeight + bounds.y) return INSERT_ABOVE;
        return pt.y <= 2D * thirdHeight + bounds.y ? MERGE : INSERT_BELOW;
    }

    private int getRowDropDest(TreePath destPath) {
        DefaultMutableTreeNode destNode = (DefaultMutableTreeNode) destPath.getLastPathComponent();
        if (destNode.getAllowsChildren()) return -1;
        if (!(destNode.getUserObject() instanceof GeneList)) return -1;
        return 2;
    }

    private int getDropDestination(TreePath destPath, Point pt) {
        Object transferable = transferHandler.getLastMadeTransferable();
        if ((transferable instanceof DraggableNode)) return getNodeDropDest(destPath, pt, (DraggableNode) transferable);
        if ((transferable instanceof ListTransferHandler.DraggableRows)) { return getRowDropDest(destPath); }
        return -1;
    }

    public DefaultTreeModel getDefaultModel() {
        return treeModel;
    }

    public ListTransferHandler getListTransferHandler() {
        return transferHandler;
    }
}
