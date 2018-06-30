package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdom.Element;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.Metadata;
import edu.iastate.metnet.metaomgraph.utils.FilterableTreeModel;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

public class MetadataPanel extends JPanel {

    private JTree myTree;

    private StripedTable dataTable;

    private NoneditableTableModel tableModel;

    private TableModelListener tableEditListener;

    private JSplitPane mySplitPane;

    private JButton filterButton;

    private FilterableTreeModel treeModel;

    private Metadata myMetadata;

    private MenuButton addButton, inspectButton;

    private JButton deleteButton;

    private JPopupMenu addMenu, inspectMenu;

    private JMenuItem addExpItem, addGroupItem, addSampleItem, findRepsItem, associateSamplesItem;

    private JToolBar toolbar;

    /**
     * Creates a MetadataPanel.
     *
     * @param root the Metadata to display
     */
    
    //new metadata to work with XML element urmi
    public MetadataPanel(Element XMLroot) {
    	
    }
    public MetadataPanel(Metadata md) {
    	//set tree background fill to true otherwise it sets both foregraound and background white
    	UIManager.put("Tree.rendererFillBackground", true);
        this.myMetadata = md;
        treeModel = new FilterableTreeModel(md.getXMLRoot());
        myTree = new JTree(treeModel);
        //urmi change selection color
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) myTree.getCellRenderer();
        
        tableModel = new NoneditableTableModel(null, new String[]{"Attribute", "Value"});
        dataTable = new StripedTable(tableModel);
        dataTable.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        dataTable.setRowSelectionAllowed(true);
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setGridColor(Color.GRAY);
        tableModel.setColumnEditable(0, true);
        tableModel.setColumnEditable(1, true);
        tableEditListener = new TableModelListener() {

            public void tableChanged(TableModelEvent arg0) {
                if (dataTable.getSelectedRow() >= 0) {
                    dataTable.scrollRectToVisible(dataTable.getCellRect(dataTable.getSelectedRow(),
                            dataTable.getSelectedColumn(), true));
                }
                if (arg0.getType() != TableModelEvent.UPDATE) {
                    return;
                }
                int changedRow = arg0.getFirstRow();
                int changedCol = arg0.getColumn();
                if (changedRow < 0 || changedCol < 0) {
                    return;
                }
                String newVal = tableModel.getValueAt(changedRow, changedCol) + "";
                SimpleXMLElement targetNode = (SimpleXMLElement) myTree.getSelectionPath()
                        .getLastPathComponent();
                boolean newNode = false;
                if (!"md".equals(targetNode.getName())) {
                    if (changedRow < targetNode.getChildCount()) {
                        int childIndex = changedRow;
                        SimpleXMLElement newTarget;
                        do {
                            newTarget = targetNode.getChildAt(childIndex);
                            childIndex++;
                        } while (!"md".equals(newTarget.getName())
                                && childIndex < targetNode.getChildCount());

                        if (!"md".equals(newTarget.getName())) {
                            newTarget = new SimpleXMLElement("md");
                            newNode = true;
                            targetNode.add(newTarget);
                            targetNode = newTarget;
                        } else {
                            targetNode = newTarget;
                        }
                    } else {
                        SimpleXMLElement newTarget = new SimpleXMLElement("md");
                        newNode = true;
                        targetNode.add(newTarget);
                        targetNode = newTarget;
                    }
                }
                if (changedCol == 0) {
                    targetNode.setAttribute("field", newVal);
                    MetaOmGraph.getActiveProject().setChanged(true);
                } else if (changedCol == 1) {
                    targetNode.setAttribute("value", newVal);
                    MetaOmGraph.getActiveProject().setChanged(true);
                }
                if (!newNode) {
                    treeModel.reload(targetNode);
                } else {
                    treeModel.reload(targetNode.getParent());
                    tableModel.appendRow(new Object[]{"", ""});
                }
            }

        };
        final JScrollPane treePane = new JScrollPane(myTree);
        final JScrollPane dataPane = new JScrollPane(dataTable);
        myTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent arg0) {
                if (myTree.getLastSelectedPathComponent() == null) {
                    deleteButton.setEnabled(false);
                }
                if (!(myTree.getLastSelectedPathComponent() instanceof SimpleXMLElement))
                    return;
                deleteButton.setEnabled(true);
                SimpleXMLElement node = (SimpleXMLElement) myTree.getLastSelectedPathComponent();
                tableModel.removeTableModelListener(tableEditListener);
                if (node.isLeaf()) {
                    String field = node.getAttributeValue("field");
                    String value = node.getAttributeValue("value");
                    tableModel.setData(new Object[][]{{field, value}});
                } else {
                    ArrayList<Object[]> newData = new ArrayList<Object[]>();
                    for (int x = 0; x < node.getChildCount(); x++) {
                        SimpleXMLElement thisChild = node.getChildAt(x);
                        if (!thisChild.isLeaf()) {
                            continue;
                        }
                        String field = thisChild.getAttributeValue("field");
                        String value = thisChild.getAttributeValue("value");
                        newData.add(new Object[]{field, value});
                    }
                    newData.add(new Object[]{"", ""});
                    if (newData.size() > 0) {
                        tableModel.setData(newData.toArray(new Object[1][1]));
                    } else {
                        tableModel.setData(null);
                    }
                }
                tableModel.addTableModelListener(tableEditListener);
                // dataPane.getViewport().scrollRectToVisible(new
                // Rectangle(1,1,2,2));
            }

        });
        myTree.setRootVisible(false);
        myTree.setShowsRootHandles(true);
        myTree.setDragEnabled(true);
        myTree.setDropMode(DropMode.ON);
        myTree.setTransferHandler(new MetadataTransferHandler());
        MetadataTreeCellRenderer treeRenderer = new MetadataTreeCellRenderer();
        myTree.setCellRenderer(treeRenderer);
        myTree.setEditable(true);
        myTree.setCellEditor(new MetadataTreeCellEditor(myTree, treeRenderer));
        myTree.getCellEditor().addCellEditorListener(new CellEditorListener() {

            public void editingCanceled(ChangeEvent e) {

            }

            public void editingStopped(ChangeEvent e) {
                String newVal = myTree.getCellEditor().getCellEditorValue() + "";
                SimpleXMLElement node = (SimpleXMLElement) myTree.getEditingPath()
                        .getLastPathComponent();
                if ("md".equals(node.getName())) {
                    node.setAttribute("field", newVal);
                } else {
                    node.setAttribute("name", newVal);
                }
                MetaOmGraph.getActiveProject().setChanged(true);
            }

        });
        myTree.setInvokesStopCellEditing(true);
        myTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (myTree.getSelectionPath() == null) {
                    return;
                }
                SimpleXMLElement clickedNode = (SimpleXMLElement) myTree.getSelectionPath()
                        .getLastPathComponent();
                if (e.getClickCount() == 2 && "Sample".equals(clickedNode.getName())) {
                    Vector<String> unknownCols = new Vector<String>();
                    unknownCols.add("-- Remove this sample association");
                    Integer oldCol;
                    try {
                        oldCol = new Integer(clickedNode.getAttributeValue("col"));
                    } catch (NumberFormatException nfe) {
                        oldCol = null;
                    }
                    if (oldCol != null) {
                        unknownCols.add(oldCol + ": "
                                + MetaOmGraph.getActiveProject().getDataColumnHeader(oldCol));
                    }
                    for (int i = 0; i < MetaOmGraph.getActiveProject().getDataColumnCount(); i++) {
                        if (myMetadata.getNodeForCol(i) == null) {
                            unknownCols.add(i + ": "
                                    + MetaOmGraph.getActiveProject().getDataColumnHeader(i));
                        }
                    }
                    if (unknownCols.size() <= 1) {
                        JOptionPane.showMessageDialog(
                                MetaOmGraph.getDesktop(),
                                "All columns in the current project already have metadata associated with them.  Please un-associate existing metadata to proceed.",
                                "All Data Accounted For", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    JComboBox comboBox = new JComboBox(unknownCols);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.add(new JLabel("Which column does the selected metadata represent?"));
                    panel.add(comboBox);
                    int result = JOptionPane.showConfirmDialog(MetaOmGraph.getDesktop(), panel,
                            "Associate Sample Metadata", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result != JOptionPane.OK_OPTION) {
                        return;
                    }
                    if (comboBox.getSelectedIndex() == 0) {
                        myMetadata.associate(clickedNode, null);
                        return;
                    }
                    String selected = comboBox.getSelectedItem().toString();
                    Integer newCol = new Integer(selected.substring(0, selected.indexOf(":") - 1));
                    myMetadata.associate(clickedNode, newCol);

                }
            }
        });
        ToolTipManager.sharedInstance().registerComponent(myTree);
        mySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, treePane, dataPane);
        mySplitPane.setDividerLocation(.5);
        // final JPanel searchPanel = new JPanel(new BorderLayout());
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        final ClearableTextField filterField = new ClearableTextField();
        filterField.setDefaultText("Filter metadata");
        filterButton = new JButton("Filter");
        ActionListener filterListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                treeModel.setFilter(filterField.getText());
            }

        };
        filterButton.addActionListener(filterListener);
        //filterButton.setEnabled(false);
        filterField.addActionListener(filterListener);
        // searchField.getDocument().addDocumentListener(searchListener);

        addMenu = new JPopupMenu();
        AbstractAction addExpAction = new AbstractAction("Add Experiment") {

            public void actionPerformed(ActionEvent arg0) {
                String name = JOptionPane.showInputDialog("Name?", "");
                SimpleXMLElement newNode = myMetadata.createExperimentNode(name);
                treeModel.reload(newNode.getParent());
                TreePath newPath = new TreePath(newNode.getPath());
                myTree.scrollPathToVisible(newPath);
                myTree.setSelectionPath(newPath);
                MetaOmGraph.getActiveProject().setChanged(true);
            }

        };
        AbstractAction addGroupAction = new AbstractAction("Add Replicate Group") {

            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Name?", "");
                SimpleXMLElement parent = (SimpleXMLElement) myTree.getLastSelectedPathComponent();
                if (parent == null) {
                    parent = (SimpleXMLElement) myTree.getModel().getRoot();
                }
                while (!"Experiment".equals(parent.getName()) && !parent.isRoot()) {
                    parent = parent.getParent();
                }
                SimpleXMLElement newNode = myMetadata.createGroupNode(name, parent);
                treeModel.reload(newNode.getParent());
                myTree.scrollPathToVisible(new TreePath(newNode.getPath()));
                MetaOmGraph.getActiveProject().setChanged(true);
            }

        };
        AbstractAction addSampleAction = new AbstractAction("Add Sample") {

            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Name?", "");
                SimpleXMLElement parent = (SimpleXMLElement) myTree.getLastSelectedPathComponent();
                if (parent == null) {
                    parent = (SimpleXMLElement) myTree.getModel().getRoot();
                }
                while (!"Experiment".equals(parent.getName()) && !"Group".equals(parent.getName())
                        && !parent.isRoot()) {
                    parent = parent.getParent();
                }
                SimpleXMLElement newNode = myMetadata.createSampleNode(name, parent);
                treeModel.reload(newNode.getParent());
                myTree.scrollPathToVisible(new TreePath(newNode.getPath()));
                MetaOmGraph.getActiveProject().setChanged(true);
            }

        };

        addExpItem = new JMenuItem(addExpAction);
        addGroupItem = new JMenuItem(addGroupAction);
        addSampleItem = new JMenuItem(addSampleAction);
        addMenu.add(addExpItem);
        addMenu.add(addGroupItem);
        addMenu.add(addSampleItem);
        addButton = new MenuButton("Add", new ImageIcon(getClass().getResource(
                "/resource/tango/16x16/actions/list-add.png")), addMenu);
        addButton.setToolTipText("Add metadata");

        inspectMenu = new JPopupMenu();
        AbstractAction findRepsAction = new AbstractAction("Generate replicate groups") {

            public void actionPerformed(ActionEvent e) {
                if (myMetadata.hasRepGroups()) {
                    int result = JOptionPane
                            .showConfirmDialog(
                                    MetaOmGraph.getDesktop(),
                                    "All existing replicate groups will be removed and replaced with automatically-generated groups.  Continue?",
                                    "Overwrite Existing Groups", JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE);
                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                    myMetadata.clearRepGroups();
                }
                myMetadata.findReps();
                treeModel.reload(myMetadata.getXMLRoot());
                // myTree.scrollPathToVisible(new TreePath(newNode.getPath()));
                MetaOmGraph.getActiveProject().setChanged(true);
            }

        };
        findRepsItem = new JMenuItem(findRepsAction);
        AbstractAction associateSamplesAction = new AbstractAction(
                "Connect sample metadata to data") {

            JInternalFrame f;

            int selectedRow;

            public void actionPerformed(ActionEvent e) {
                String[] headers = {"Sample Node", "Associated Column"};
                ArrayList<SimpleXMLElement> sampleNodes = myMetadata.getSampleNodes();
                Object[][] data = new Object[sampleNodes.size()][2];
                for (int i = 0; i < sampleNodes.size(); i++) {
                    SimpleXMLElement thisNode = sampleNodes.get(i);
                    data[i][0] = thisNode;
                    if (thisNode.getAttributeValue("col") != null) {
                        int col = Integer.parseInt(thisNode.getAttributeValue("col"));
                        data[i][1] = col + ": "
                                + MetaOmGraph.getActiveProject().getDataColumnHeader(col);
                    }
                }
                NoneditableTableModel model = new NoneditableTableModel(data, headers);
                final StripedTable myTable = new StripedTable(model);
                myTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent arg0) {
                        selectedRow = myTable.getSelectedRow();
                    }

                });

                TableColumnModel colModel = myTable.getColumnModel();
                Vector<String> unknownCols = new Vector<String>();
                unknownCols.add("");
                for (int i = 0; i < MetaOmGraph.getActiveProject().getDataColumnCount(); i++) {
                    if (myMetadata.getNodeForCol(i) == null) {
                        unknownCols.add(i + ": "
                                + MetaOmGraph.getActiveProject().getDataColumnHeader(i));
                    }
                }
                final ComboBoxTableCellRenderer comboEditor = new ComboBoxTableCellRenderer(
                        unknownCols);
                comboEditor.setEditable(false);
                comboEditor.addPopupMenuListener(new PopupMenuListener() {

                    public void popupMenuCanceled(PopupMenuEvent arg0) {
                        // TODO Auto-generated method stub

                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
                        // TODO Auto-generated method stub

                    }

                    public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
                        Object oldVal = myTable.getValueAt(myTable.getSelectedRow(), 1);
                        comboEditor.removePopupMenuListener(this);
                        comboEditor.insertItemAt(oldVal, 0);
                        comboEditor.addPopupMenuListener(this);
                    }

                });
                comboEditor.addItemListener(new ItemListener() {

                    public void itemStateChanged(ItemEvent arg0) {
                        Object item = arg0.getItem();
                        int state = arg0.getStateChange();
                        if (state == ItemEvent.DESELECTED) {
                            System.out.println(item + " deselected");
                        } else if (state == ItemEvent.SELECTED) {
                            System.out.println(item + " selected");
                        } else {
                            System.out.println("Not sure what happened to " + item);
                        }
                    }

                });
                colModel.getColumn(1).setCellEditor(
                        new DefaultCellEditor(new JComboBox(unknownCols)));
                colModel.getColumn(1).setMinWidth(comboEditor.getPreferredSize().width);
                colModel.getColumn(1).setCellRenderer(comboEditor);
                // colModel.getColumn(1).setMaxWidth(
                // colCombo.getPreferredSize().width);
                model.setColumnEditable(1, true);
                if (f != null && f.isVisible()) {
                    f.toFront();
                } else {
                    f = new JInternalFrame("Associate Metadata", true, true, true, true);
                    f.putClientProperty("JInternalFrame.frameType", "normal");
                    f.getContentPane().add(new JScrollPane(myTable), BorderLayout.CENTER);
                    f.setSize(500, 500);
                    MetaOmGraph.getDesktop().add(f);
                    f.setVisible(true);
                }

            }

        };
        associateSamplesItem = new JMenuItem(associateSamplesAction);
        inspectMenu.add(findRepsItem);
        //inspectMenu.add(associateSamplesItem);
        inspectButton = new MenuButton("Inspect", new ImageIcon(getClass().getResource(
                "/resource/tango/16x16/actions/edit-find.png")), inspectMenu);
        inspectButton.setToolTipText("Metadata analysis");

        AbstractAction deleteAction = new AbstractAction("Delete", new ImageIcon(getClass()
                .getResource("/resource/tango/16x16/places/user-trash.png"))) {

            public void actionPerformed(ActionEvent e) {
                int ok = JOptionPane
                        .showConfirmDialog(
                                MetaOmGraph.getDesktop(),
                                "Are you sure you want to delete the selected metadata? This cannot be undone.",
                                "Confirm Delete", JOptionPane.WARNING_MESSAGE);
                if (ok != JOptionPane.YES_OPTION) {
                    return;
                }
                TreePath[] paths = myTree.getSelectionPaths();
                for (TreePath path : paths) {
                    SimpleXMLElement removeMe = (SimpleXMLElement) path.getLastPathComponent();
                    SimpleXMLElement parent = removeMe.getParent();
                    removeMe.removeFromParent();
                    treeModel.reload(parent);
                }
            }

        };
        deleteButton = new JButton(deleteAction);
        deleteButton.setToolTipText("Delete selected metadata");
        deleteButton.setEnabled(false);

        // searchPanel.add(new JLabel("Filter:"), BorderLayout.LINE_START);
        toolbar.add(addButton);
        toolbar.add(inspectButton);
        toolbar.add(deleteButton);
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(filterField, BorderLayout.CENTER);
        toolbar.add(searchPanel);
        toolbar.add(filterButton);
        // searchPanel.add(addButton, BorderLayout.LINE_START);
        // searchPanel.add(filterField, BorderLayout.CENTER);
        // searchPanel.add(filterButton, BorderLayout.LINE_END);
        this.setLayout(new BorderLayout());
        this.add(toolbar, BorderLayout.NORTH);
        this.add(mySplitPane, BorderLayout.CENTER);
    }

    public JTree getTree() {
        return myTree;
    }

    public JSplitPane getSplitPane() {
        return mySplitPane;
    }

    private class MetadataTransferHandler extends TransferHandler {

        private DataFlavor metadataFlavor;

        private class MetadataPackage {
            ArrayList<SimpleXMLElement> nodes;
            String elementName;

            public MetadataPackage(int size) {
                nodes = new ArrayList<SimpleXMLElement>(size);
            }

            public void add(SimpleXMLElement addMe) {
                nodes.add(addMe);
            }
        }

        public MetadataTransferHandler() {
            try {
                metadataFlavor = new DataFlavor(
                        DataFlavor.javaJVMLocalObjectMimeType
                                + ";class=edu.iastate.metnet.metaomgraph.ui.MetadataPanel$MetadataTransferHandler$MetadataPackage");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            final JTree sourceTree = (JTree) c;
            final int[] rows = sourceTree.getSelectionRows();
            final MetadataPackage result = new MetadataPackage(rows.length);
            SimpleXMLElement firstNode = (SimpleXMLElement) sourceTree.getPathForRow(rows[0])
                    .getLastPathComponent();
            String targetName = firstNode.getName();
            result.add(firstNode);
            result.elementName = targetName;
            for (int i = 1; i < rows.length; i++) {
                // Check the remaining rows to make sure they're the
                // same type as the first, so we don't end up trying to
                // move both a Sample and an Experiment for example
                SimpleXMLElement thisNode = (SimpleXMLElement) sourceTree.getPathForRow(rows[i])
                        .getLastPathComponent();
                String thisName = thisNode.getName();
                if (!thisName.equals(targetName)) {
                    return null;
                }
                result.add(thisNode);
            }
            Transferable transferMe = new Transferable() {

                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException,
                        IOException {
                    if (!flavor.equals(metadataFlavor)) {
                        throw new UnsupportedFlavorException(flavor);
                    }
                    if (rows == null || rows.length <= 0) {
                        throw new IOException("No nodes selected!");
                    }

                    return result;
                }

                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{metadataFlavor};
                }

                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return metadataFlavor.equals(flavor);
                }

            };
            return transferMe;
        }

        @Override
        protected void exportDone(JComponent c, Transferable t, int action) {
            if (action != MOVE) {
                return;
            }
            JTree tree = (JTree) c;
            TreeModel tm = tree.getModel();
            // Delete the original nodes
            MetadataPackage dropped;
            try {
                dropped = (MetadataPackage) t.getTransferData(metadataFlavor);
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            for (SimpleXMLElement node : dropped.nodes) {
                TreeNode parent = node.getParent();
                node.removeFromParent();
                treeModel.reload(parent);
            }
            MetaOmGraph.getActiveProject().setChanged(true);
        }

        @Override
        public boolean canImport(TransferSupport support) {
            if (!support.isDataFlavorSupported(metadataFlavor)) {
                return false;
            }

            JTree.DropLocation loc = (javax.swing.JTree.DropLocation) support.getDropLocation();
            String dropName = ((SimpleXMLElement) loc.getPath().getLastPathComponent()).getName();
            if ("md".equals(dropName)) {
                // Can't drop on a plain metadata node
                return false;
            }

            String sourceName;
            try {
                if (support.getTransferable() == null
                        || support.getTransferable().getTransferData(metadataFlavor) == null) {
                    return false;
                }
                sourceName = ((MetadataPackage) support.getTransferable().getTransferData(
                        metadataFlavor)).elementName;
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
            if ("Experiment".equals(dropName)) {
                // Only allow md, Sample, Group
                if (!sourceName.equals("md") && !sourceName.equals("Sample")
                        && !sourceName.equals("Group")) {
                    return false;
                }
            } else if ("Group".equals(dropName) || "MOGMetadata".equals(dropName)) {
                // Only allow md, Sample
                if (!sourceName.equals("md") && !sourceName.equals("Sample")) {
                    return false;
                }
            } else if ("Sample".equals(dropName)) {
                // Only allow md
                if (!sourceName.equals("md")) {
                    return false;
                }
            } else {
                // The drop location is a node that shouldn't exit
                System.err.println("Unknown metadata node: " + dropName);
                return false;
            }

            return true;
        }

        @Override
        public boolean importData(TransferSupport support) {
            System.out.println("Importing!");
            MetadataPackage dropMe;
            try {
                dropMe = (MetadataPackage) support.getTransferable()
                        .getTransferData(metadataFlavor);
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            JTree.DropLocation loc = (javax.swing.JTree.DropLocation) support.getDropLocation();
            SimpleXMLElement dest = (SimpleXMLElement) loc.getPath().getLastPathComponent();

            boolean isCopy = (support.getDropAction() == COPY);

            for (SimpleXMLElement node : dropMe.nodes) {
                SimpleXMLElement newNode = node.fullCopy(isCopy);
                dest.add(newNode);
                if (!isCopy) {
                    // Check the moved nodes for col values
                    myMetadata.scanForCols(newNode);
                }
                treeModel.reload(dest);

            }
            return true;
        }

    }

    private static class MetadataTreeCellRenderer extends DefaultTreeCellRenderer {

        Icon expIcon, repsIcon, knownSampleIcon, unknownSampleIcon,outerSampIcon;

        public MetadataTreeCellRenderer() {
            expIcon = new ImageIcon(getClass().getResource("/resource/customicon/exp icon2.png"));
            repsIcon = new ImageIcon(getClass().getResource("/resource/customicon/reps icon.png"));
            //knownSampleIcon = new ImageIcon(getClass().getResource(  "/resource/customicon/known sample icon.png"));
            //unknownSampleIcon = new ImageIcon(getClass().getResource("/resource/customicon/unknown sample icon.png"));
            //change icon urmi        
            knownSampleIcon = new ImageIcon(getClass().getResource(  "/resource/customicon/runicon.png"));
            unknownSampleIcon = new ImageIcon(getClass().getResource("/resource/customicon/runicon.png"));
            outerSampIcon = new ImageIcon(getClass().getResource("/resource/customicon/outer sample icon.png"));
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if (!(value instanceof SimpleXMLElement)) {
                return this;
            }
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            String name = ((SimpleXMLElement) value).getName();
            if ("Experiment".equals(name)) {
                setIcon(expIcon);
            } else if ("Group".equals(name)) {
                setIcon(repsIcon);
            }else if ("OuterSamp".equals(name)) {
                setIcon(outerSampIcon);
            } else if ("Sample".equals(name)) {
                Integer col = null;
                try {
                    col = new Integer(((SimpleXMLElement) value).getAttributeValue("col"));
                } catch (NumberFormatException nfe) {

                }
                JCheckBox newResult = new JCheckBox(value.toString());
                newResult.setForeground(getForeground());
                newResult.setBackground(getBackground());
                newResult.setBorder(getBorder());
                newResult.setFont(getFont());
                newResult.setText(getText());
                newResult.setOpaque(false);
                if (col != null) {
                    setIcon(knownSampleIcon);
                    newResult.setToolTipText("This metadata is associated with column " + col
                            + ": " + MetaOmGraph.getActiveProject().getDataColumnHeader(col));
                } else {
                    setIcon(unknownSampleIcon);
                    newResult
                            .setToolTipText("MetaOmGraph does not know what data this sample represents.  Double-click to associate it with a column.");
                }
                // return newResult;
            }
            return this;
        }
    }

    private static class MetadataTreeCellEditor extends DefaultTreeCellEditor {

        public MetadataTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
        }

        @Override
        public boolean stopCellEditing() {
            String newVal = getCellEditorValue() + "";
            return !newVal.equals("");
        }

    }

    public class ComboBoxTableCellRenderer extends JComboBox implements TableCellRenderer {
        public ComboBoxTableCellRenderer(Object[] items) {
            super(items);
        }

        public ComboBoxTableCellRenderer(Vector<? extends Object> items) {
            super(items);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            // Select the current value
            setSelectedItem(value);
            return this;
        }
    }
}
