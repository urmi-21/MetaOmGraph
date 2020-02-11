package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.AtGeneSearch;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.Metadata;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

// new testing by mhhur
import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;

public class ChartDataExporter extends JInternalFrame implements ActionListener {
    private static final double SCALE = 36.57142857142857D;
    private static final int[] ATGS_COL_WIDTHS = {300, 70, 70, 80, 95, 100, 90, 100, 80, 100, 70, 70, 70, 70};
    private static final short[] HSSF_COLORS = {46, 44, 44, 31, 31, 31, 42, 52, 46, 43, 47, 47, 47, 47};
    private MetaOmChartPanel myChartPanel;
    private JTable seriesTable;
    private ButtonGroup includeGroup;
    private JRadioButton allVisibleButton;
    private JRadioButton selectedButton;
    private JCheckBox atgsBox;
    private JCheckBox colorCodeBox;
    private JTextField rangeField;
    private JButton rangePickButton;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel mainPanel;
    private JLabel selectLabel;
    private int rangeStart;
    private int rangeEnd;

    public ChartDataExporter(MetaOmChartPanel mcp) {
        myChartPanel = mcp;
        setTitle("Export Data");
        setClosable(true);
        setIconifiable(false);
        setMaximizable(false);
        setResizable(false);
        String[] headers = {"Series", "Include"};
        XYDataset dataset = mcp.getChart().getXYPlot().getDataset();
        Object[][] series = new Object[dataset.getSeriesCount()][2];
        for (int i = 0; i < series.length; i++) {
            series[i][0] = dataset.getSeriesKey(i);
            if (myChartPanel.getSelectedSeries() < 0) series[i][1] = new Boolean(true);
            else series[i][1] = new Boolean(i == myChartPanel.getSelectedSeries());
        }

        NoneditableTableModel model = new NoneditableTableModel(series, headers);
        model.setColumnEditable(1, true);
        seriesTable = new JTable(model);
        allVisibleButton = new JRadioButton("Include all visible columns");
        selectedButton = new JRadioButton("Select columns to include");
        includeGroup = new ButtonGroup();
        includeGroup.add(allVisibleButton);
        includeGroup.add(selectedButton);
        allVisibleButton.setSelected(true);
        rangeField = new JTextField();
        rangeField.setEditable(false);
        rangePickButton = new JButton("Select...");
        rangePickButton.setActionCommand("pick");
        rangePickButton.addActionListener(this);
        okButton = new JButton("OK");
        okButton.setActionCommand("ok");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        atgsBox = new JCheckBox("Download information from AtGeneSearch.  WARNING: May take a long time");
        colorCodeBox = new JCheckBox("Use AtGeneSearch colors");
        int[] plottedRows = myChartPanel.getSelectedRows();
        boolean hasGeneIDs = false;
        for (int i = 0; i < plottedRows.length; i++) {
            Object[] names = myChartPanel.getProject().getRowName(plottedRows[i]);
            for (int j = 0; (j < names.length) && (!hasGeneIDs); j++) {
                if (Utils.isGeneID(names[j] + "")) hasGeneIDs = true;
            }
        }
        atgsBox.setEnabled(hasGeneIDs);
        atgsBox.setSelected(hasGeneIDs);
        colorCodeBox.setSelected(hasGeneIDs);
        colorCodeBox.setEnabled(hasGeneIDs);
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 3;
        c.fill = 1;
        c.weightx = 1.0D;
        c.weighty = 0.0D;
        mainPanel.add(allVisibleButton, c);
        c.gridy = 1;
        mainPanel.add(selectedButton, c);
        c.gridy = 2;
        c.gridwidth = 2;
        c.insets = new Insets(0, 20, 5, 0);
        mainPanel.add(rangeField, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 0.0D;
        c.insets = new Insets(0, 5, 5, 5);
        mainPanel.add(rangePickButton, c);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0D;
        mainPanel.add(atgsBox, c);
        c.gridy = 4;
        c.insets = new Insets(0, 20, 5, 0);
        mainPanel.add(colorCodeBox, c);
        c.insets = new Insets(5, 5, 5, 5);
        c.gridy = 5;
        c.gridwidth = 3;
        c.weighty = 1.0D;
        mainPanel.add(new JScrollPane(seriesTable), c);
        c.gridy = 6;
        c.weighty = 0.0D;
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, c);
        getContentPane().add(mainPanel);
        pack();
        selectLabel = new JLabel("Click and drag on the chart to select a range...");
        selectedButton.addItemListener(new ItemListener() {
            @Override
			public void itemStateChanged(ItemEvent e) {
                rangePickButton.setEnabled(selectedButton.isSelected());
                rangeField.setEnabled(selectedButton.isSelected());
            }

        });
        atgsBox.addItemListener(new ItemListener() {
            @Override
			public void itemStateChanged(ItemEvent e) {
                colorCodeBox.setEnabled(atgsBox.isSelected());
            }

        });
        rangePickButton.setEnabled(false);
        rangeField.setEnabled(false);
    }

    private void doExport(int[] columns, int[] series, File destination) {
        Object[][] result = new Object[series.length + 1][columns.length + 1];
        XYDataset dataset = myChartPanel.getChart().getXYPlot().getDataset();
        result[0][0] = myChartPanel.getChart().getXYPlot().getDomainAxis().getLabel();
        for (int i = 1; i < result[0].length; i++) {
            result[0][i] = myChartPanel.getFormatter().format(columns[(i - 1)]);
        }
        for (int x = 1; x < result.length; x++) {
            result[x][0] = dataset.getSeriesKey(series[(x - 1)]);
            for (int y = 1; y < result[x].length; y++) {
                result[x][y] = dataset.getY(series[(x - 1)], columns[(y - 1)]);
            }
        }

        String[][] atgsData = null;
        if (atgsBox.isSelected()) {
            atgsData = new String[series.length + 1][14];
            atgsData[0] = AtGeneSearch.getHeaders();

            for (int i = 1; i < atgsData.length; i++) {
                try {
                    atgsData[i] = AtGeneSearch.doQuery((String) dataset.getSeriesKey(series[(i - 1)]));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        Metadata eit = myChartPanel.getProject().getMetadata();
        Hashtable[] metadata = null;
        Vector<String> metadataKeys = null;
        if (eit != null) {
            metadata = new Hashtable[result[0].length];
            metadata[0] = new Hashtable();
            metadataKeys = new Vector();
            for (int x = 1; x < metadata.length; x++) {
                metadata[x] = new Hashtable();
                String[][] thisMetadata = eit.getMetadataForCol(columns[(x - 1)], false);
                for (int metadataIndex = 0; metadataIndex < thisMetadata.length; metadataIndex++) {
                    metadata[x].put(thisMetadata[metadataIndex][0], thisMetadata[metadataIndex][1]);
                    if (!metadataKeys.contains(thisMetadata[metadataIndex][0])) {
                        metadataKeys.add(thisMetadata[metadataIndex][0]);
                    }
                }
            }
        }


        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("MetaOmGraph data");

        if (colorCodeBox.isSelected()) {
            HSSFPalette pal = wb.getCustomPalette();
            pal.setColorAtIndex((short) 46, (byte) -23, (byte) -45, (byte) -15);
            pal.setColorAtIndex((short) 44, (byte) -122, (byte) -92, (byte) -23);
            pal.setColorAtIndex((short) 31, (byte) -65, (byte) -33, (byte) -1);
            pal.setColorAtIndex((short) 42, (byte) -50, (byte) -22, (byte) -40);
            pal.setColorAtIndex((short) 52, (byte) -9, (byte) -60, (byte) 77);
            pal.setColorAtIndex((short) 43, (byte) -1, (byte) -1, (byte) -100);
            pal.setColorAtIndex((short) 47, (byte) -10, (byte) -52, (byte) -99);
        }

        HSSFCellStyle cs = wb.createCellStyle();
        for (int rowIndex = 0; rowIndex < result.length; rowIndex++) {
            HSSFRow row = sheet.createRow(rowIndex);
            row.createCell((short) 0).setCellValue((String) result[rowIndex][0]);

            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            row.getCell((short) 0).setCellStyle(cs);
            int offset = 0;
            if ((atgsData != null) && (atgsData[rowIndex] != null)) {
                cs = wb.createCellStyle();
                for (int i = 0; i < atgsData[rowIndex].length; i++) {
                    HSSFCell atgsCell = row.createCell((short) (i + 1));

                    cs.setWrapText(true);
                    cs.setVerticalAlignment(VerticalAlignment.CENTER);
                    if (colorCodeBox.isSelected()) {
                        cs.setFillForegroundColor(HSSF_COLORS[i]);
                        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND); //setFillPattern((short) 1);
                        cs.setBorderBottom(BorderStyle.THIN);//cs.setBorderBottom((short) 1);
                        cs.setBorderTop(BorderStyle.THIN);//cs.setBorderTop((short) 1);
                        cs.setBorderLeft(BorderStyle.THIN);//cs.setBorderLeft((short) 1);
                        cs.setBorderRight(BorderStyle.THIN);//cs.setBorderRight((short) 1);
                        cs.setTopBorderColor((short) 8);
                        cs.setBottomBorderColor((short) 8);
                        cs.setLeftBorderColor((short) 8);
                        cs.setRightBorderColor((short) 8);
                    }
                    atgsCell.setCellStyle(cs);
                    atgsCell.setCellValue(atgsData[rowIndex][i]);
                }

                offset += atgsData[rowIndex].length;
            }
            for (int colIndex = 1; colIndex < result[rowIndex].length; colIndex++) {
                row.createCell((short) (colIndex + offset)).setCellValue(result[rowIndex][colIndex] + "");
                cs.setVerticalAlignment(VerticalAlignment.CENTER);//cs.setVerticalAlignment((short) 1);
                row.getCell((short) (colIndex + offset)).setCellStyle(cs);
            }
        }

        if (metadata != null) {
            int keyCount = metadataKeys.size();
            int offset = 0;
            if (atgsData != null)  offset += atgsData[0].length;
            for (int x = 0; x < keyCount; x++) sheet.createRow(result.length + x);

            cs = wb.createCellStyle();
            for (int y = 0; y < keyCount; y++) {
                HSSFRow row = sheet.getRow(result.length + y);
                String thisKey = metadataKeys.get(y);
                for (int x = 0; x < metadata.length; x++) {
                    Object thisValue = metadata[x].get(thisKey);
                    String stringValue;
                    if (thisValue == null)  stringValue = "";
                    else stringValue = thisKey + ": " + thisValue;
                    HSSFCell cell = row.createCell((short) (x + offset));

                    cs.setWrapText(true);
                    cs.setVerticalAlignment(VerticalAlignment.CENTER);//cs.setVerticalAlignment((short) 1);
                    cell.setCellStyle(cs);
                    cell.setCellValue(stringValue);
                }
            }
        }

        sheet.setColumnWidth((short) 0, (short) 2925);
        if (atgsData != null) {
            for (short x = 0; x < atgsData[0].length; x = (short) (x + 1)) {
                sheet.setColumnWidth((short) (x + 1), (short) (int) (ATGS_COL_WIDTHS[x] * 36.57142857142857D));
            }
        }
        boolean finished = false;
        while (!finished) {
            try {
                FileOutputStream fileOut = new FileOutputStream(destination);
                wb.write(fileOut);
                fileOut.close();
                finished = true;
            } catch (IOException e) {
                e.printStackTrace();
                Object[] options = {"Try the same file", "Try a different file", "Cancel"};
                int retry = JOptionPane.showOptionDialog(MetaOmGraph.getMainWindow(),"There was a problem writing to " + destination.getName() + ".\nMake sure the file isn't open.", "Write error", 1, 0, null, options, options[0]);
                if (retry == 0) finished = false;
                else if (retry == 1) {
                    FileFilter filter = Utils.createFileFilter("xls", "Excel spreadsheet");
                    destination = Utils.chooseFileToSave(filter, "xls", MetaOmGraph.getMainWindow(), true);
                    finished = destination == null;
                } else {
                    finished = true;
                }
            }
        }
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("ok")) {
            Vector<Integer> selectedSeries = new Vector();
            for (int x = 0; x < seriesTable.getRowCount(); x++) {
                Boolean show = (Boolean) seriesTable.getValueAt(x, 1);
                if (show.booleanValue()) {
                    selectedSeries.add(new Integer(x));
                }
            }
            final int[] series = new int[selectedSeries.size()];
            for (int x = 0; x < series.length; x++) {
                series[x] = selectedSeries.get(x).intValue();
            }
            final int[] columns;
            if (allVisibleButton.isSelected()) {
                ValueAxis domain = myChartPanel.getChart().getXYPlot().getDomainAxis();
                int lowerBound = (int) domain.getLowerBound();
                if (lowerBound < domain.getLowerBound()) lowerBound++;
                if (lowerBound < 0) lowerBound = 0;
                int upperBound = (int) domain.getUpperBound() + 1;
                if (upperBound > myChartPanel.getProject().getDataColumnCount()) upperBound = myChartPanel.getProject().getDataColumnCount() - 1;
                columns = new int[upperBound - lowerBound];
                for (int x = 0; x < columns.length; x++) {
                    columns[x] = (lowerBound + x);
                }
            } else {
                if (rangeField.getText().equals("")) {
                    JOptionPane.showMessageDialog(getParent(), "Please select a range first.","No range selected", 0);
                    return;
                }
                columns = new int[rangeEnd - rangeStart + 1];
                for (int x = 0; x < columns.length; x++) {
                    columns[x] = (rangeStart + x);
                }
            }
            int finalColumns = columns.length;
            if (atgsBox.isSelected()) finalColumns += 14;
            if (finalColumns > 255) {
                JOptionPane.showMessageDialog(getParent(), "An Excel file cannot contain more than 255 columns.\nPlease select a smaller range.", "Range too large", 0);
                return;
            }
            FileFilter filter = Utils.createFileFilter("xls", "Excel spreadsheet");
            final File destination = Utils.chooseFileToSave(filter, "xls", MetaOmGraph.getMainWindow(), true);
            if (destination == null) return;

            new AnimatedSwingWorker("Exporting...") {
                @Override
				public Object construct() {
                    ChartDataExporter.this.doExport(columns, series, destination);
                    return null;
                }

            }.start();
            dispose();
            return;
        }

        if (e.getActionCommand().equals("cancel")) {
            dispose();
            return;
        }
        if (e.getActionCommand().equals("pick")) {
            myChartPanel.getRangeSelector().addActionListener(this);
            myChartPanel.enableRangeSelection();
            getContentPane().remove(mainPanel);
            getContentPane().add(selectLabel);
            pack();
            SwingUtilities.getWindowAncestor(myChartPanel).toFront();
            return;
        }
        if (e.getActionCommand().equals("A range has been selected")) {
            RangeSelector rs = myChartPanel.getRangeSelector();
            rs.removeActionListener(this);
            rangeStart = rs.getRangeStart();
            rangeEnd = rs.getRangeEnd();
            String start = myChartPanel.getFormatter().format(rangeStart);
            String end = myChartPanel.getFormatter().format(rangeEnd);
            rangeField.setText(start + " - " + end);
            getContentPane().remove(selectLabel);
            getContentPane().add(mainPanel);
            pack();
        }
    }
}
