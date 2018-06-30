package edu.iastate.metnet.metaomgraph;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;
import javax.activation.*;
import javax.swing.*;
import javax.swing.tree.*;

public class Test2 {
  public JComponent makeUI() {
    DefaultMutableTreeNode root    = new DefaultMutableTreeNode("root");
    DefaultMutableTreeNode string1 = new DefaultMutableTreeNode("String 1");
    DefaultMutableTreeNode order1  = new DefaultMutableTreeNode(new ParentObject());
    order1.add(new DefaultMutableTreeNode(new ChildObject()));
    order1.add(new DefaultMutableTreeNode(new ChildObject()));
    string1.add(order1);
    root.add(string1);

    DefaultTreeModel model = new DefaultTreeModel(root);
    JTree tree = new JTree(model);
    tree.setShowsRootHandles(true);
    tree.setRootVisible(false);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setTransferHandler(new TreeTransferHandler());
    tree.setDragEnabled(true);

    DefaultListModel<ChildObject> listModel = new DefaultListModel<ChildObject>();
    JList<ChildObject> list = new JList<ChildObject>();
    list.setModel(listModel);
    list.setDropTarget(new DropTarget(list, TransferHandler.COPY, new DropTargetAdapter() {
      private void print(Transferable tr) {
        try {
          Object node = tr.getTransferData(TreeTransferHandler.FLAVOR);
          System.out.println(node); // I want the actual object
        } catch (UnsupportedFlavorException | IOException ex) {
          ex.printStackTrace();
        }
      }
      @Override
      public void drop(DropTargetDropEvent dtde) {
        if (dtde.isDataFlavorSupported(TreeTransferHandler.FLAVOR)) {
          print(dtde.getTransferable());
        }
      }
      @Override public void dragExit(DropTargetEvent dte) {}
      @Override public void dragEnter(DropTargetDragEvent dtde) {}
      @Override public void dragOver(DropTargetDragEvent dtde) {}
    }, true, null));

    JPanel contentPane = new JPanel(new GridLayout(1, 2));
    contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    contentPane.add(new JScrollPane(tree), BorderLayout.WEST);
    contentPane.add(new JScrollPane(list), BorderLayout.CENTER);

    return contentPane;
  }
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override public void run() {
        try {
          JFrame f = new JFrame();
          f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          f.getContentPane().add(new Test2().makeUI());
          f.setBounds(100, 100, 450, 300);
          f.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}

class ParentObject implements Serializable {
  public ArrayList<ChildObject> getChildren() {
    return new ArrayList<ChildObject>();
  }
  @Override
  public String toString() {
    return "ParentObject";
  }
}

class ChildObject implements Serializable {
  @Override
  public String toString() {
    return "ChildObject";
  }
}

class TreeTransferHandler extends TransferHandler {
  public static final DataFlavor FLAVOR = new ActivationDataFlavor(
    DefaultMutableTreeNode[].class,
    DataFlavor.javaJVMLocalObjectMimeType,
    "Array of DefaultMutableTreeNode");
  @Override protected Transferable createTransferable(JComponent c) {
    JTree source = (JTree) c;
    TreePath[] paths = source.getSelectionPaths();
    DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[paths.length];
    for (int i = 0; i < paths.length; i++) {
      nodes[i] = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
    }
    return new DataHandler(nodes, FLAVOR.getMimeType());
  }
  @Override public int getSourceActions(JComponent c) {
    return TransferHandler.COPY;
  }
}