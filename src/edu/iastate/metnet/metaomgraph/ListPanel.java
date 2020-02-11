package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;


public class ListPanel
        extends JPanel
        implements ActionListener {
    private JToolBar listToolbar;
    private JButton listDeleteButton;
    private JButton listEditButton;
    private JButton listCreateButton;
    private ListTree tree;
    private JScrollPane scrollPane;

    public ListPanel() {
        listToolbar = new JToolBar();
        listToolbar.setFloatable(false);
        listDeleteButton = new JButton(new ImageIcon(getClass().getResource(
                "/resource/javaicon/Delete16.gif")));
        listDeleteButton.setActionCommand("delete list");
        listDeleteButton.addActionListener(this);
        listDeleteButton.setToolTipText("Delete the selected list");
        listEditButton = new JButton(new ImageIcon(getClass().getResource(
                "/resource/javaicon/Edit16.gif")));
        listEditButton.setActionCommand("edit list");
        listEditButton.addActionListener(this);
        listEditButton.setToolTipText("Edit the selected list");
        listCreateButton = new JButton(new ImageIcon(getClass().getResource(
                "/resource/javaicon/Add16.gif")));
        listCreateButton.setActionCommand("create list");
        listCreateButton.addActionListener(this);
        listCreateButton.setToolTipText("Create a new list");
        listToolbar.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(listCreateButton);
        buttonPanel.add(listEditButton);
        buttonPanel.add(listDeleteButton);
        listToolbar.add(buttonPanel, "North");
        final JCheckBox sortBox = new JCheckBox("Autosort");
        sortBox.addItemListener(new ItemListener() {
            @Override
			public void itemStateChanged(ItemEvent e) {
                tree.setAutosort(sortBox.isSelected());
            }

        });
        listToolbar.add(sortBox, "South");
        tree = new ListTree();
        scrollPane = new JScrollPane(tree);
        setLayout(new BorderLayout());
        add(listToolbar, "First");
        add(scrollPane, "Center");
    }

    public ListTransferHandler getListTransferHandler() {
        return tree.getListTransferHandler();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("create list")) {
            int[] rows = {(int) (Math.random() * 100.0D),
                    (int) (Math.random() * 100.0D), (int) (Math.random() * 100.0D)};
            tree.addListAtSelection(new GeneList("List " +
                    (int) (Math.random() * 100.0D), rows));
        }
        if (e.getActionCommand().equals("edit list"))
            tree.addFolder("Folder " + (int) (Math.random() * 100.0D));
        if (e.getActionCommand().equals("delete list"))
            tree.deleteSelected();
        scrollPane.setViewportView(tree);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        JFrame f = new JFrame("List Test");
        ListPanel panel = new ListPanel();
        final JTable table = new JTable(
                new String[][]{{"no rows"}},
                new String[]{"row"});
        table.setTransferHandler(panel.getListTransferHandler());
        System.out.println("transfer handler" + panel.getListTransferHandler());
        table.setDragEnabled(true);
        final JScrollPane scrolly = new JScrollPane(table);
        panel.tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
			public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                        .getPath().getLastPathComponent();
                if (!node.getAllowsChildren()) {
                    GeneList list = (GeneList) node.getUserObject();
                    int[] rows = list.getRows();
                    Object[][] tableRows = new Object[rows.length][1];
                    for (int x = 0; x < rows.length; x++) {
                        tableRows[x][0] = new Integer(rows[x]);
                    }
                    NoneditableTableModel model = new NoneditableTableModel(
                            tableRows, new String[]{"rows"});
                    table.setModel(model);
                    table.setDragEnabled(true);

                    scrolly.setViewportView(table);
                }

            }
        });
        JSplitPane splitty = new JSplitPane(1, panel, scrolly);
        table.setDragEnabled(true);
        f.getContentPane().add(splitty);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(3);
    }
}
