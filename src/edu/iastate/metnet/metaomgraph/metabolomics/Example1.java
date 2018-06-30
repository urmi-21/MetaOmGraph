package edu.iastate.metnet.metaomgraph.metabolomics;

import ca.ansir.swing.tristate.TriState;
import ca.ansir.swing.tristate.TriStateTreeHandler;
import ca.ansir.swing.tristate.TriStateTreeNode;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


public class Example1 extends JDialog {
    private JTree tree;
    private TriStateTreeHandler handler;

    private Example1() {

        super((Frame) null, "Tri-State Tree - www.ansir.ca", true);
        TriStateTreeNode root = getSimpleTree();
        tree = new JTree(root);
        handler = new TriStateTreeHandler(tree);

        tree.setShowsRootHandles(true);
        JScrollPane sp = new JScrollPane(tree);
        sp.setPreferredSize(new Dimension(150, 150));
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder("Tri-State Tree"));
        p.add(sp);
        getContentPane().add(p, "Center");
        getContentPane().add(getLookAndFeelPanel(), "East");
        setDefaultCloseOperation(2);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        Enumeration treeEnum = root.depthFirstEnumeration();
        while (treeEnum.hasMoreElements()) {
            TriStateTreeNode thisNode = (TriStateTreeNode) treeEnum.nextElement();
            System.out.println(thisNode + " is selected: " + thisNode.isSelected() + ", class=" + thisNode.getUserObject().getClass());
        }
    }


    private TriStateTreeNode getSimpleTree() {
        TriStateTreeNode root = new TriStateTreeNode("Master");
        TriStateTreeNode node = new TriStateTreeNode("Test 1");
        node.setUserObject(new SomeObject("this is a thing", 100));
        root.add(node);
        node = new TriStateTreeNode("Test 2");
        node.setState(TriState.SELECTED);
        root.add(node);
        node = new TriStateTreeNode("Sub Master");
        node.add(new TriStateTreeNode("Test 4"));
        node.add(new TriStateTreeNode("Test 5"));
        root.add(node);
        return root;
    }

    private static class SomeObject {
        String name;
        int data;

        public SomeObject(String name, int data) {
            this.name = name;
            this.data = data;
        }

        public String toString() {
            return name;
        }
    }


    private JComponent getLookAndFeelPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Look And Feel"));
        UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        Arrays.sort(info, new Comparator<LookAndFeelInfo>() {
            public int compare(LookAndFeelInfo o1, LookAndFeelInfo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = 17;
        ButtonGroup bg = new ButtonGroup();
        AbstractButton selectedButton = null;
        for (int i = 0; i < info.length; i++) {
            AbstractButton b = new JRadioButton(info[i].getName());
            p.add(b, c);
            bg.add(b);

            b.addItemListener(new LookAndFeelItemListener(info[i]));
            if (info[i].getClassName().equals(
                    UIManager.getSystemLookAndFeelClassName())) {
                selectedButton = b;
            }
            c.gridy += 1;
        }
        selectedButton.setSelected(true);
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.fill = 1;
        p.add(new JLabel(), c);
        Dimension dim = p.getPreferredSize();
        dim.width *= 1.2;
        dim.height *= 1.2;
        p.setPreferredSize(dim);
        return p;
    }

    private class LookAndFeelItemListener implements ItemListener {
        UIManager.LookAndFeelInfo info;

        private LookAndFeelItemListener(UIManager.LookAndFeelInfo info) {
            this.info = info;
        }

        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == 1) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                    SwingUtilities.updateComponentTreeUI(Example1.this);
                } catch (Exception localException) {
                }
            }
        }
    }


    public static void main(String[] args)
            throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new Example1();
    }
}
