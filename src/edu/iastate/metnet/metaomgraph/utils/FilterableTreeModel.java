package edu.iastate.metnet.metaomgraph.utils;

import edu.iastate.metnet.metaomgraph.ui.ClearableTextField;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


public class FilterableTreeModel
        extends DefaultTreeModel {
    Filter currentFilter;
    DefaultTreeModel filteredModel;
    TreeNode filteredRoot;
    TreeNode originalRoot;

    public FilterableTreeModel(TreeNode root) {
        super(root);
        currentFilter = null;
    }

    public void setFilter(final String filter) {
        if ((filter == null) || ("".equals(filter))) {
            setFilter((Filter) null);
        }
        setFilter(new Filter() {
            @Override
			public boolean include(Object testMe) {
                if (!(testMe instanceof DefaultMutableTreeNode)) {
                    return false;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) testMe;
                boolean result = node.toString().toLowerCase().contains(filter.toLowerCase());
                for (int childIndex = 0; (childIndex < node.getChildCount()) && (!result); childIndex++) {
                    result = include(node.getChildAt(childIndex));
                }
                return result;
            }
        });
    }

    public void setFilter(Filter myFilter) {
        currentFilter = myFilter;
        reload();
    }

    @Override
	public Object getChild(Object parent, int index) {
        if ((!(parent instanceof DefaultMutableTreeNode)) || (currentFilter == null)) {
            return super.getChild(parent, index);
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
        int hitIndex = -1;
        int childIndex;

        for (childIndex = 0; (childIndex < node.getChildCount()) && (hitIndex < index); childIndex++) {
            if (currentFilter.include(node.getChildAt(childIndex))) {
                hitIndex++;
            }
        }
        if (hitIndex == index) {
            return node.getChildAt(childIndex - 1);
        }
        System.err.println("Asked for bad index: " + index);
        return null;
    }

    @Override
	public int getChildCount(Object parent) {
        if ((!(parent instanceof DefaultMutableTreeNode)) || (currentFilter == null)) {
            return super.getChildCount(parent);
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
        int hitCount = 0;
        for (int childIndex = 0; childIndex < node.getChildCount(); childIndex++) {
            if (currentFilter.include(node.getChildAt(childIndex))) {
                hitCount++;
            }
        }
        return hitCount;
    }


    public static void main(String[] args)
            throws Exception {
        File source = Utils.mandatoryFileOpen("xml");
        SimpleXMLElement root = SimpleXMLElement.fromFile(source);
        final FilterableTreeModel model = new FilterableTreeModel(root);
        JTree tree = new JTree(model);
        JFrame f = new JFrame();
        f.add(tree, "Center");
        final ClearableTextField field = new ClearableTextField("Tree filter");
        field.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                model.setFilter(field.getText());
            }
        });
        f.add(field, "North");
        f.setSize(500, 500);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }

    public interface Filter {
        boolean include(Object paramObject);
    }
}
