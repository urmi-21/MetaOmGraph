package edu.iastate.metnet.arrayexpress;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import edu.iastate.metnet.arrayexpress.CheckBoxTree.CheckBoxTreeCellRenderer.BoxPanel;

public class CheckBoxTree extends JTree {
    private CheckBoxTreeCellRenderer renderer;

    public CheckBoxTree(TreeNode root) {
        super(root);
        renderer = new CheckBoxTreeCellRenderer(this);
        setCellRenderer(renderer);
        DefaultTreeSelectionModel selector = new DefaultTreeSelectionModel();
        selector.setSelectionMode(1);
        setSelectionModel(selector);
    }

    public CheckBoxTreeCellRenderer getRenderer() {
        return renderer;
    }


    public static class CheckBoxTreeCellRenderer<E>
            implements TreeCellRenderer, MouseListener {
        TreeMap<E, CheckBoxTreeCellRenderer<E>.BoxPanel> boxMap;

        JTree myTree;

        DefaultTreeCellRenderer defaultRenderer;
        ArrayList<ChangeListener> listeners;

        public CheckBoxTreeCellRenderer(JTree tree) {
            defaultRenderer = new DefaultTreeCellRenderer();
            boxMap = new TreeMap();
            tree.addMouseListener(this);
            myTree = tree;
            myTree.addPropertyChangeListener("model", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    TreeNode root = (TreeNode) myTree.getModel().getRoot();
                    if (root == null) {
                        System.err.println("wtf null root");
                        return;
                    }
                    for (int i = 0; i < root.getChildCount(); i++) {
                        if (!(root.getChildAt(i) instanceof DefaultMutableTreeNode)) {
                            System.out.println("Not defaultmutabletreenode");
                        } else {
                            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);

                            E userObj = (E) child.getUserObject();
                            CheckBoxTree.CheckBoxTreeCellRenderer<E>.BoxPanel result = null;
                            try {
                                if (userObj != null) {
                                    result = boxMap.get(userObj);
                                }
                                if (result == null) {
                                    result = new BoxPanel(child.toString());
                                    if (userObj != null) {
                                        boxMap.put(userObj, result);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            TreeNode root = (TreeNode) myTree.getModel().getRoot();
            if (root == null) {
                return;
            }
            for (int i = 0; i < root.getChildCount(); i++) {
                if (!(root.getChildAt(i) instanceof DefaultMutableTreeNode)) {
                    System.out.println("Not defaultmutabletreenode");
                } else {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                    E userObj = (E) child.getUserObject();
                    CheckBoxTreeCellRenderer<E>.BoxPanel result = null;
                    try {
                        if (userObj != null) {
                            result = boxMap.get(userObj);
                        }
                        if (result == null) {
                            result = new BoxPanel(child.toString());
                            if (userObj != null) {
                                boxMap.put(userObj, result);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        public List<E> getSelectedPaths() {
            ArrayList<E> result = new ArrayList();
            Set<E> keys = boxMap.keySet();
            for (E key : keys) {
                try {
                    CheckBoxTreeCellRenderer<E>.BoxPanel box = boxMap.get(key);
                    if ((box != null) && (box.isSelected())) {
                        result.add(key);
                    }
                } catch (ClassCastException localClassCastException) {
                }
            }


            return result;
        }

        class BoxPanel extends JPanel {
            private JCheckBox box;
            private CheckBoxTree.CheckBoxTreeCellRenderer<E>.BoxPanel.TreeLabel label;

            public BoxPanel(String text) {
                box = new JCheckBox();
                box.setOpaque(false);
                box.setMargin(new Insets(0, 0, 0, 0));
                label = new TreeLabel(text);

                label.setOpaque(true);
                label.setBackground(UIManager.getColor("Tree.background"));

                add(box);
                add(label);
                setOpaque(false);
                setBorder(null);
                box.setOpaque(false);
            }

            public void doLayout() {
                Dimension d_check = box.getPreferredSize();
                Dimension d_label = label.getPreferredSize();
                int y_check = 0;
                int y_label = 0;
                if (d_check.height < d_label.height) {
                    y_check = (d_label.height - d_check.height) / 2;
                } else {
                    y_label = (d_check.height - d_label.height) / 2;
                }
                box.setLocation(0, y_check);
                box.setBounds(0, y_check, d_check.width, d_check.height);
                label.setLocation(d_check.width, y_label);
                label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
            }

            public void setSelected(boolean selected) {
                box.setSelected(selected);
            }

            public boolean isSelected() {
                return box.isSelected();
            }

            public void setBackground(Color bg) {
                if ((bg == null) || (label == null) || (box == null)) {
                    return;
                }
                label.setBackground(bg);
            }

            public void setForeground(Color fg) {
                if ((fg == null) || (label == null) || (box == null)) {
                    return;
                }
                label.setForeground(fg);
            }

            public String getText() {
                return label.getText();
            }

            public CheckBoxTree.CheckBoxTreeCellRenderer<E>.BoxPanel.TreeLabel getLabel() {
                return label;
            }

            public JCheckBox getCheckBox() {
                return box;
            }

            public class TreeLabel extends JLabel {
                boolean isSelected;
                boolean hasFocus;

                public TreeLabel(String text) {
                    super();
                }

                public void paintComponent(Graphics g) {
                    String str;
                    if (((str = getText()) != null) &&
                            (str.length() > 0)) {
                        if (isSelected) {
                            g.setColor(UIManager.getColor("Tree.selectionBackground"));
                        } else {
                            g.setColor(UIManager.getColor("Tree.textBackground"));
                        }
                        Dimension d = getPreferredSize();
                        int imageOffset = 0;
                        Icon currentI = getIcon();
                        if (currentI != null) {
                            imageOffset = currentI.getIconWidth() +
                                    Math.max(0, getIconTextGap() - 1);
                        }
                        g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height);

                        if (hasFocus) {
                            g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
                            g.drawRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 1);
                        }
                    }

                    super.paintComponent(g);
                }

                public void setSelected(boolean selected) {
                    isSelected = selected;
                }
            }
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            TreePath path = tree.getPathForRow(row);
            if ((path == null) || (path.getPathCount() != 2)) {
                return defaultRenderer.getTreeCellRendererComponent(tree, value, selected,
                        expanded, leaf, row, hasFocus);
            }
            CheckBoxTreeCellRenderer<E>.BoxPanel result = null;
            E userObj = null;
            if ((value instanceof DefaultMutableTreeNode)) {
                userObj = (E) ((DefaultMutableTreeNode) value).getUserObject();
            }
            try {
                if (userObj != null) {
                    result = boxMap.get(userObj);
                }
                if (result == null) {
                    result = new BoxPanel(value.toString());


                    if (userObj != null) {
                        boxMap.put(userObj, result);
                    }
                }
                if (selected) {
                    result.setBackground(UIManager.getColor("Tree.selectionBackground"));
                    result.setForeground(UIManager.getColor("Tree.selectionForeground"));
                    result.getLabel().setSelected(true);
                } else {
                    result.setBackground(tree.getBackground());
                    result.setForeground(tree.getForeground());
                    result.getLabel().setSelected(false);
                }
                return result;
            } catch (ClassCastException cce) {
                System.err.println("Error casting " + userObj);
            }
            return defaultRenderer.getTreeCellRendererComponent(tree, value, selected,
                    expanded, leaf, row, hasFocus);
        }

        public void valueChanged(TreeSelectionEvent e) {
            CheckBoxTreeCellRenderer<E>.BoxPanel selMe = boxMap.get(e.getPath());
            if (selMe != null) {
                System.out.println("Changing " + selMe.getText());
                selMe.setSelected(!selMe.isSelected());
            }
        }


        public void treeNodesChanged(TreeModelEvent e) {
        }


        public void treeNodesInserted(TreeModelEvent e) {
        }


        public void treeNodesRemoved(TreeModelEvent e) {
        }


        public void treeStructureChanged(TreeModelEvent e) {
        }


        public void mouseClicked(MouseEvent e) {
        }


        public void mouseEntered(MouseEvent e) {
        }


        public void mouseExited(MouseEvent e) {
        }


        public void mousePressed(MouseEvent e) {
            TreePath path = myTree.getPathForLocation(e.getX(), e.getY());
            if ((path == null) || (path.getPathCount() != 2)) {
                return;
            }
            E value = null;
            if ((path.getLastPathComponent() instanceof DefaultMutableTreeNode)) {
                value = (E) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            }
            CheckBoxTreeCellRenderer<E>.BoxPanel box = boxMap.get(value);
            if (box != null) {
                Rectangle bounds = myTree.getPathBounds(path);


                if (e.getX() > bounds.x && e.getX() <= bounds.x + box.getCheckBox().getWidth()) {

                    box.setSelected(!box.isSelected());


                    boxMap.put(value, box);
                }
                myTree.repaint();
            }
        }


        public void mouseReleased(MouseEvent e) {
        }


        public void addChangeListener(ChangeListener listener) {
            if (listeners == null) {
                listeners = new ArrayList();
            }
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        private void fireStateChanged(ChangeEvent e) {
            if (listeners == null) {
                return;
            }
            for (ChangeListener l : listeners) {
                l.stateChanged(e);
            }
        }

        public void selectAll() {
            for (int i = 0; i < myTree.getRowCount(); i++) {
                TreePath path = myTree.getPathForRow(i);
                if ((path != null) && (path.getPathCount() == 2)) {
                    E value = null;
                    if ((path.getLastPathComponent() instanceof DefaultMutableTreeNode)) {
                        value = (E) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                    }
                    CheckBoxTreeCellRenderer<E>.BoxPanel box = boxMap.get(value);
                    if (box != null) {
                        box.setSelected(true);
                        boxMap.put(value, box);
                    }
                }
            }
            myTree.repaint();
        }

        public void selectNone() {
            for (int i = 0; i < myTree.getRowCount(); i++) {
                TreePath path = myTree.getPathForRow(i);
                if ((path != null) && (path.getPathCount() == 2)) {
                    E value = null;
                    if ((path.getLastPathComponent() instanceof DefaultMutableTreeNode)) {
                        value = (E) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                    }
                    CheckBoxTreeCellRenderer<E>.BoxPanel box = boxMap.get(value);
                    if (box != null) {
                        box.setSelected(false);
                        boxMap.put(value, box);
                    }
                }
            }
            myTree.repaint();
        }
    }


    public static void main(String[] args)
            throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode[] checkNodes = new DefaultMutableTreeNode[3];
        for (int i = 0; i < 3; i++) {
            checkNodes[i] = new DefaultMutableTreeNode("Child " + (i + 1));
            checkNodes[i].add(new DefaultMutableTreeNode("Leaf 1"));
            checkNodes[i].add(new DefaultMutableTreeNode("Leaf 2"));
            checkNodes[i].add(new DefaultMutableTreeNode("Leaf 3"));
            root.add(checkNodes[i]);
        }
        CheckBoxTree tree = new CheckBoxTree(root);
        JFrame f = new JFrame("Checkbox tree test");
        f.getContentPane().add(new javax.swing.JScrollPane(tree));
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
