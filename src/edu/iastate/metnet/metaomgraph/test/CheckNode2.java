package edu.iastate.metnet.metaomgraph.test;

import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;


public class CheckNode2
        extends DefaultMutableTreeNode {
    public static final int SINGLE_SELECTION = 0;
    public static final int DIG_IN_SELECTION = 4;
    protected int selectionMode;
    protected boolean isSelected;

    public CheckNode2() {
        this(null);
    }

    public CheckNode2(Object userObject) {
        this(userObject, true, false);
    }

    public CheckNode2(Object userObject, boolean allowsChildren, boolean isSelected) {
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
            Enumeration en = children.elements();
            while (en.hasMoreElements()) {
                CheckNode2 node = (CheckNode2) en.nextElement();
                node.setSelected(isSelected);
            }
        }
    }

    public boolean isSelected() {
        return isSelected;
    }


    public static void main(String[] args) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        CheckNode2[] checkNodes = new CheckNode2[3];
        for (int i = 0; i < 3; i++) {
            checkNodes[i] = new CheckNode2("Child " + (i + 1));
            checkNodes[i].add(new DefaultMutableTreeNode("Leaf 1"));
            checkNodes[i].add(new DefaultMutableTreeNode("Leaf 2"));
            checkNodes[i].add(new DefaultMutableTreeNode("Leaf 3"));
            root.add(checkNodes[i]);
        }
        JTree tree = new JTree(root);
        JFrame f = new JFrame("Checkbox header test");
        f.getContentPane().add(new JScrollPane(tree));
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
