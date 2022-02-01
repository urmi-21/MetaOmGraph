package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.HashLoadable;
import edu.iastate.metnet.metaomgraph.HashtableSavePanel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.XMLizable;
import edu.iastate.metnet.metaomgraph.ui.ColorChooseButton;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jdom.Element;


public class CustomSortDialog extends JDialog implements ActionListener, HashLoadable<CustomSortDialog.CustomSortObject> {
    public static final String OK_ACTION_COMMAND = "ok";
    public static final String CANCEL_ACTION_COMMAND = "cancel";
    public static final String RESET_ACTION_COMMAND = "reset";
    public static final String MARK_ACTION_COMMAND = "mark";
    public static final String REMOVE_ACTION_COMMAND = "remove";
    public static final String CUT_COMMAND = "copy";
    public static final String PASTE_COMMAND = "paste";
    private static final int START_COLUMN = 3;
    private static final int END_COLUMN = 4;
    private static final int LABEL_COLUMN = 0;
    private static final int STYLE_COLUMN = 1;
    private static final int COLOR_COLUMN = 2;
    private static final int MARK_COLUMN_COUNT = 5;
    private int[] originalOrder;
    private OrderableTable sortTable;
    private NoneditableTableModel sortTableModel;
    private JScrollPane sortPane;
    private MetaOmChartPanel myChartPanel;
    private boolean cancelled;
    private MetaOmProject myProject;
    private JTabbedPane tabbedPane;
    private NoneditableTableModel markTableModel;
    private StripedTable markTable;
    private JComboBox styleCombo;
    private ColumnValueRenderer renderer;
    private JPanel orderPanel;
    private JPanel markPanel;
    private JScrollPane markScrollPane;
    private JButton okButton;
    private JButton cancelButton;
    private JButton resetButton;
    private JButton markButton;
    private JButton removeButton;
    private JButton cutButton;
    private JButton pasteButton;
    private JButton toTopButton;
    private JButton toBottomButton;
    private int[] cutRows;

    public CustomSortDialog(int[] currentOrder, MetaOmChartPanel mcp) {
        super(MetaOmGraph.getMainWindow(), true);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        setTitle("Custom sort");
        originalOrder = currentOrder;
        myChartPanel = mcp;
        myProject = myChartPanel.getProject();
        cancelled = true;
        tabbedPane = new JTabbedPane();
        orderPanel = new JPanel(new BorderLayout());
        markPanel = new JPanel(new BorderLayout());
        initSortModel(originalOrder);
        sortTable = new OrderableTable(sortTableModel) {
            @Override
			protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                for (int x = 0; x < markTableModel.getRowCount(); x++) {
                    int start = ((Integer) markTableModel.getValueAt(x, 3)).intValue();
                    int end = ((Integer) markTableModel.getValueAt(x,4)).intValue() + 1;
                    g2d.setColor((Color) markTableModel.getValueAt(x, 2));
                    g2d.setStroke(new BasicStroke(3.0F, 0, 2, 1.0F, new float[]{4.0F, 8.0F}, 0.0F));
                    g2d.drawLine(0, start * getRowHeight() - 1, getWidth(), start * getRowHeight() - 1);
                    g2d.setStroke(new BasicStroke(3.0F, 0, 2, 1.0F, new float[]{4.0F, 8.0F}, 4.0F));
                    g2d.drawLine(0, end * getRowHeight() - 1, getWidth(), end * getRowHeight() - 1);
                }
                g2d.dispose();
            }
        };
        TableColumnModel colModel = sortTable.getColumnModel();
        colModel.removeColumn(colModel.getColumn(0));
        JPanel confirmPanel = new JPanel();
        JPanel sortButtonPanel = new JPanel();
        JPanel markButtonPanel = new JPanel();
        okButton = new JButton("OK");
        okButton.setActionCommand("ok");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        resetButton = new JButton("Reset");
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);
        markButton = new JButton("Mark");
        markButton.setActionCommand("mark");
        markButton.addActionListener(this);
        removeButton = new JButton("Remove");
        removeButton.setActionCommand("remove");
        removeButton.addActionListener(this);
        cutButton = new JButton("Cut");
        cutButton.setActionCommand("copy");
        cutButton.addActionListener(this);
        cutButton.setMnemonic(88);
        pasteButton = new JButton("Paste");
        pasteButton.setActionCommand("paste");
        pasteButton.addActionListener(this);
        pasteButton.setMnemonic(86);
        pasteButton.setEnabled(false);

        confirmPanel.add(okButton);
        confirmPanel.add(cancelButton);
        sortButtonPanel.add(cutButton);
        sortButtonPanel.add(pasteButton);
        sortButtonPanel.add(markButton);
        sortButtonPanel.add(resetButton);
        markButtonPanel.add(removeButton);
        getContentPane().setLayout(new BorderLayout());
        sortPane = new JScrollPane(sortTable);
        orderPanel.add(sortPane, "Center");
        orderPanel.add(sortButtonPanel, "Last");
        getContentPane().add(confirmPanel, "Last");

        HashtableSavePanel savePanel = new HashtableSavePanel(myProject.getSavedSorts(), this);
        savePanel.setNoun("sort");
        orderPanel.add(savePanel, "Before");

        initMarkModel(myChartPanel.getDataSorter().getRangeMarkers());
        markTable = new StripedTable(markTableModel);
        styleCombo = new JComboBox(new String[]{"Horizontal", "Vertical"});
        styleCombo.setEditable(false);
        colModel = markTable.getColumnModel();
        colModel.getColumn(1).setCellEditor(new DefaultCellEditor(styleCombo));
        colModel.getColumn(1).setMinWidth(styleCombo.getPreferredSize().width);
        colModel.getColumn(1).setMaxWidth(styleCombo.getPreferredSize().width);
        renderer = new ColumnValueRenderer();
        colModel.getColumn(3).setCellRenderer(renderer);
        colModel.getColumn(4).setCellRenderer(renderer);
        markScrollPane = new JScrollPane(markTable);

        markPanel.add(markScrollPane, "Center");
        markPanel.add(markButtonPanel, "Last");

        tabbedPane.addTab("Sort", orderPanel);
        tabbedPane.addTab("Markers", markPanel);
        getContentPane().add(tabbedPane, "Center");
        pack();
        setLocationRelativeTo(null);
        AbstractAction helpAction = new AbstractAction() {
            @Override
			public void actionPerformed(ActionEvent e) { MetaOmGraph.getHelpListener().actionPerformed(new ActionEvent(this, 0, "customsort.php"));
            }
        };
        getRootPane().getActionMap().put("help", helpAction);
        InputMap im = getRootPane().getInputMap(1);
        im.put(KeyStroke.getKeyStroke(112, 0), "help");
    }

    public void initSortModel(int[] order) {
        Object[][] data = new Object[order.length][2];
        String[] headers = {"Entry", myChartPanel.getChart().getXYPlot().getDomainAxis().getLabel()};
        String[] sampleNames = myChartPanel.getSampleNames();
        for (int x = 0; x < data.length; x++) {
            data[x][0] = order[x];
            data[x][1] = sampleNames[order[x]];
        }
        sortTableModel = new NoneditableTableModel(data, headers);
    }

    public void initMarkModel(Vector<RangeMarker> rangeMarkers) {
        Object[][] data = null;
        if ((rangeMarkers != null) && (rangeMarkers.size() > 0)) {
            data = new Object[rangeMarkers.size()][5];
            for (int x = 0; x < rangeMarkers.size(); x++) {
                RangeMarker thisMarker = rangeMarkers.get(x);
                data[x][0] = thisMarker.getLabel();
                data[x][1] = (thisMarker.getStyle() == RangeMarker.HORIZONTAL ? "Horizontal" :
                        "Vertical");
                data[x][3] = thisMarker.getStart();
                data[x][4] = thisMarker.getEnd();
                data[x][2] = thisMarker.getColor();
            }
        }
        String[] headers = new String[5];
        headers[3] = "Start";
        headers[4] = "End";
        headers[0] = "Label";
        headers[1] = "Style";
        headers[2] = "Color";
        markTableModel = new NoneditableTableModel(data, headers);
        markTableModel.setColumnEditable(0, true);
        markTableModel.setColumnEditable(1, true);
        markTableModel.setColumnEditable(2, true);
        removeButton.setEnabled(markTableModel.getRowCount() > 0);
    }

    public CustomSortObject showSortDialog() {
        cancelled = true;
        setVisible(true);
        if (cancelled) return null;
        return new CustomSortObject(getSortOrder(), getRangeMarkers());
    }

    public int[] getSortOrder() {
        int[] result = new int[originalOrder.length];
        for (int x = 0; x < result.length; x++) {
            result[x] = ((Integer) sortTableModel.getValueAt(x, 0)).intValue();
        }
        return result;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if ("ok".equals(e.getActionCommand())) {
            cancelled = false;
            dispose();
            return;
        }
        if ("cancel".equals(e.getActionCommand())) {
            cancelled = true;
            dispose();
            return;
        }
        if ("reset".equals(e.getActionCommand())) {
            initSortModel(originalOrder);
            sortTable.setModel(sortTableModel);
            TableColumnModel colModel = sortTable.getColumnModel();
            colModel.removeColumn(colModel.getColumn(0));
            sortPane.setViewportView(sortTable);
            return;
        }
        if ("mark".equals(e.getActionCommand())) {
            markSelectedRows();
            return;
        }
        if ("remove".equals(e.getActionCommand())) {
            markTableModel.deleteRows(markTable.getSelectedRows());
            removeButton.setEnabled(markTableModel.getRowCount() > 0);
            return;
        }
        if ("copy".equals(e.getActionCommand())) {
            doCut();
            return;
        }
        if ("paste".equals(e.getActionCommand())) {
            doPaste();
            return;
        }
    }

    private void markSelectedRows() {
        if (sortTable.getSelectedRowCount() <= 0) {
            JOptionPane.showMessageDialog(null,
                    "You must select the rows to mark!", "No rows selected",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        int start = sortTable.getSelectedRow();
        int end = start + sortTable.getSelectedRowCount() - 1;
        JPanel optionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        ButtonGroup group = new ButtonGroup();
        JRadioButton verticalButton = new JRadioButton("Vertical");
        ImageIcon verticalIcon = new ImageIcon(getClass().getResource("/resource/misc/tinyChartVertical.png"));
        JRadioButton horizontalButton = new JRadioButton("Horizontal");
        ImageIcon horizontalIcon = new ImageIcon(getClass().getResource("/resource/misc/tinyChartHorizontal.png"));
        group.add(verticalButton);
        group.add(horizontalButton);
        verticalButton.setSelected(true);
        final JTextField labelField = new JTextField();
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(new JLabel("Label: "), "Before");
        labelPanel.add(labelField, "Center");
        JPanel colorPanel = new JPanel();
        ColorChooseButton cButton = new ColorChooseButton(Color.BLACK, "Pick a Color");
        colorPanel.add(new JLabel("Color:"));
        colorPanel.add(cButton);
        labelPanel.add(colorPanel, "After");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.fill = 2;
        c.insets = new Insets(5, 5, 5, 5);
        optionPanel.add(labelPanel, c);
        c.gridy = 1;
        c.gridwidth = 1;
        optionPanel.add(verticalButton, c);
        c.gridy = 2;
        optionPanel.add(new JLabel(verticalIcon), c);
        c.gridx = 1;
        c.gridy = 1;
        optionPanel.add(horizontalButton, c);
        c.gridy = 2;
        optionPanel.add(new JLabel(horizontalIcon), c);


        JOptionPane pane = new JOptionPane(optionPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog(null, "Add marker");
        dialog.addWindowListener(new WindowAdapter() {

            @Override
			public void windowActivated(WindowEvent e) {
                labelField.requestFocusInWindow();
            }

        });
        dialog.setVisible(true);
        Integer result = (Integer) pane.getValue();
        if ((result == null) || (result.intValue() != JOptionPane.OK_OPTION))
            return;
        String label = labelField.getText();
        String orientation = verticalButton.isSelected() ? "Vertical" : "Horizontal";
        Object[] data = new Object[5];
        data[3] = start;
        data[4] = end;
        data[0] = label;
        data[1] = orientation;
        data[2] = cButton.getColor();
        int dest = 0;
        System.out.println(markTableModel.getValueAt(dest, 3) + ">" + start);
        while ((dest < markTableModel.getRowCount()) && (((Integer) markTableModel.getValueAt(dest, START_COLUMN)).intValue() <= start)) {
            dest++;
        }

        markTableModel.insertRowAt(data, dest);
        removeButton.setEnabled(true);
        sortTable.repaint();
    }

    public Vector<RangeMarker> getRangeMarkers() {
        if (markTableModel.getRowCount() <= 0) return null;

        Vector<RangeMarker> result = new Vector();
        for (int x = 0; x < markTableModel.getRowCount(); x++) {
            int start = ((Integer) markTableModel.getValueAt(x, 3)).intValue();
            int end = ((Integer) markTableModel.getValueAt(x, 4)).intValue();
            String label = markTableModel.getValueAt(x, 0) + "";
            int orientation = markTableModel.getValueAt(x, 1).equals("Horizontal") ? RangeMarker.HORIZONTAL : RangeMarker.VERTICAL;
            Color myColor = (Color) markTableModel.getValueAt(x, 2);
            result.add(new RangeMarker(start, end, label, orientation, myColor));
        }
        return result;
    }

    @Override
	public CustomSortObject getSaveData() {
        return new CustomSortObject(getSortOrder(), getRangeMarkers());
    }

    @Override
	public void loadData(CustomSortObject data) {
        initSortModel(data.getSortOrder());
        sortTable.setModel(sortTableModel);
        TableColumnModel colModel = sortTable.getColumnModel();
        colModel.removeColumn(colModel.getColumn(0));
        sortPane.setViewportView(sortTable);
        initMarkModel(data.getRangeMarkers());
        markTable.setModel(markTableModel);
        colModel = markTable.getColumnModel();
        colModel.getColumn(1).setCellEditor(new DefaultCellEditor(styleCombo));
        colModel.getColumn(1).setMinWidth(styleCombo.getPreferredSize().width);
        colModel.getColumn(1).setMaxWidth(styleCombo.getPreferredSize().width);
        colModel.getColumn(3).setCellRenderer(renderer);
        colModel.getColumn(4).setCellRenderer(renderer);
        markScrollPane.setViewportView(markTable);
    }

    @Override
	public String getNoun() {
        return "custom sort";
    }

    public void doCut() {
        int[] rows = sortTable.getSelectedRows();
        if ((rows == null) || (rows.length <= 0)) {
            pasteButton.setEnabled(false);
            System.out.println("No rows to cut!");
        } else {
            cutRows = rows;
            pasteButton.setEnabled(true);
            System.out.println("Cutting rows:");
            for (int i : cutRows) {
                System.out.println(i);
            }
        }
    }

    public void doPaste() {
        Object[][] data = sortTableModel.deleteRows(cutRows);
        int row = sortTable.getSelectedRow();
        if (row < 0) {
            System.out.println("Pasting with no row selected.");
            row = 0;
        }
        sortTableModel.insertRowsBefore(data, row);
        pasteButton.setEnabled(false);
    }

    public static class CustomSortObject implements Serializable, XMLizable {
        private int[] sortOrder;
        private Vector<RangeMarker> rangeMarkers;

        public CustomSortObject() {
        }

        public CustomSortObject(int[] sortOrder, Vector<RangeMarker> rangeMarkers) {
            this.sortOrder = sortOrder;
            this.rangeMarkers = rangeMarkers;
        }

        public static String getXMLElementName() {
            return "sort";
        }

        public Vector<RangeMarker> getRangeMarkers() {
            return rangeMarkers;
        }

        public void setRangeMarkers(Vector<RangeMarker> rangeMarkers) {
            this.rangeMarkers = rangeMarkers;
        }

        public int[] getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(int[] sortOrder) {
            this.sortOrder = sortOrder;
        }

        @Override
		public Element toXML() {
            Element sort = new Element(getXMLElementName());
            String orderString = sortOrder[0] + "";
            for (int x = 1; x < sortOrder.length; x++) {
                orderString = orderString + "," + sortOrder[x];
            }
            sort.addContent(new Element("order").setText(orderString));
            if (rangeMarkers != null) {
                for (int x = 0; x < rangeMarkers.size(); x++) {
                    RangeMarker thisMarker = rangeMarkers.get(x);
                    Element markerElement = new Element("marker").setAttribute("style", thisMarker.getStyle() == RangeMarker.HORIZONTAL ? "horizontal" : "vertical");
                    markerElement.addContent(new Element("start").setText(thisMarker.getStart() + ""));
                    markerElement.addContent(new Element("end").setText(thisMarker.getEnd() + ""));
                    markerElement.addContent(new Element("label").setText(thisMarker.getLabel()));
                    markerElement.addContent(new Element("color").setText(thisMarker.getColor().getRGB() + ""));
                    sort.addContent(markerElement);
                }
            }
            return sort;
        }

        @Override
		public void fromXML(Element source) {
            String order = source.getChildText("order");
            String[] splitOrder = order.split(",");
            sortOrder = new int[splitOrder.length];
            for (int x = 0; x < sortOrder.length; x++) {
                sortOrder[x] = Integer.parseInt(splitOrder[x]);
            }
            List markerList = source.getChildren("marker");
            rangeMarkers = null;
            if (!markerList.isEmpty()) {
                rangeMarkers = new Vector();
                Iterator markerIter = markerList.iterator();
                while (markerIter.hasNext()) {
                    Element thisMarkerElement = (Element) markerIter.next();
                    int style = "horizontal".equals(thisMarkerElement.getAttributeValue("style")) ? RangeMarker.HORIZONTAL : RangeMarker.VERTICAL;
                    int start = Integer.parseInt(thisMarkerElement.getChildText("start"));
                    int end = Integer.parseInt(thisMarkerElement.getChildText("end"));
                    String label = thisMarkerElement.getChildText("label");
                    String colorText = thisMarkerElement.getChildText("color");
                    Color myColor;
                    if (colorText != null) {
                        myColor = new Color(Integer.parseInt(thisMarkerElement.getChildText("color")));
                    } else {
                        myColor = Color.BLACK;
                    }
                    rangeMarkers.add(new RangeMarker(start, end, label, style, myColor));
                }
            }
        }
    }

    public class ColumnValueRenderer
            extends DefaultTableCellRenderer {
        private JFormattedTextField.AbstractFormatter formatter;


        public ColumnValueRenderer() {
        }

        @Override
		public void setValue(Object value) {
            if (formatter == null) {
                formatter = new JFormattedTextField.AbstractFormatter() {
                    @Override
					public Object stringToValue(String text) throws ParseException {
                        System.out.println("stringToValue called for some reason");
                        return null;
                    }

                    @Override
					public String valueToString(Object value) throws ParseException {
                        if (!(value instanceof Integer)) {
                            throw new ParseException(
                                    "ColumnValueRenderer can only render Integers",
                                    0);
                        }
                        return "(" + value + ") " + myProject.getDataColumnHeader(((Integer) sortTableModel.getValueAt(((Integer) value).intValue(), 0)).intValue());
                    }
                };
            }
            try {
                setText(value == null ? "" : formatter.valueToString(value));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
