package edu.iastate.metnet.metaomgraph.test;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;


class CheckNode
        extends DefaultMutableTreeNode {
    public static final int SINGLE_SELECTION = 0;
    public static final int DIG_IN_SELECTION = 4;
    protected int selectionMode;
    protected boolean isSelected;

    public CheckNode() {
        this(null);
    }

    public CheckNode(Object userObject) {
        this(userObject, true, false);
    }

    public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected) {
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        setSelectionMode(4);
    }

    public void setSelectionMode(int mode) {
        selectionMode = mode;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;

        if ((selectionMode == 4) && (children != null)) {
            Enumeration e = children.elements();
            while (e.hasMoreElements()) {
                CheckNode node = (CheckNode) e.nextElement();
                node.setSelected(isSelected);
            }
        }
    }

    public boolean isSelected() {
        return isSelected;
    }
}
