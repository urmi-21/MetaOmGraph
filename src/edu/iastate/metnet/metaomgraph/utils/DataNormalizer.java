package edu.iastate.metnet.metaomgraph.utils;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.TableSorter;
import edu.iastate.metnet.metaomgraph.ui.FileSelectionPanel;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;


public class DataNormalizer {
    public DataNormalizer() {
    }

    public static TreeSet<MeanResult> getMeans(File source, char delimiter, int infoCols)
            throws IOException {
        TreeSet<MeanResult> result = new TreeSet();
        RandomAccessFile in = new RandomAccessFile(source, "r");
        Vector<Long> indexList = new Vector();
        String thisLine = in.readLine();
        if (thisLine == null) {
            return null;
        }
        String[] headers = thisLine.split(delimiter + "");
        int cols = headers.length - infoCols;
        ProgressMonitor pm = new ProgressMonitor(null, "Getting mean values",
                null, 0, cols);
        in.seek(0L);
        while (in.nextLine()) {
            for (int i = 0; i < infoCols; i++) {
                in.readString(delimiter, false);
            }
            if (in.peek() > 0) {
                indexList.add(Long.valueOf(in.getFilePointer()));
            }
        }
        Long[] indices = indexList.toArray(new Long[0]);
        ArrayList<Integer> badCols = new ArrayList();
        int rows = indices.length;
        for (int i = 0; (i < cols) && (!pm.isCanceled()); i++) {
            pm.setProgress(i);
            pm.setNote(headers[i]);
            double mean = 0.0D;
            int thisRows = rows;
            int misses = 0;
            int addedVals = 0;
            double[] vals = new double[rows];
            for (int j = 0; j < rows; j++) {
                in.seek(indices[j].longValue());
                double val = Double.NaN;
                try {
                    val = Double.parseDouble(in.readString(delimiter, false));
                    mean += val;
                    vals[j] = val;
                    addedVals++;
                } catch (Exception localException) {
                }


                indices[j] = Long.valueOf(in.getFilePointer());
            }
            mean /= addedVals;
            Arrays.sort(vals);
            double median = Double.NaN;
            if (addedVals % 2 == 1) {
                int index = (addedVals - 1) / 2;
                median = vals[index];
            } else {
                int index = addedVals / 2;
                if (index == 0) {
                    median = 0.0D;
                } else {
                    median = (vals[index] + vals[(index - 1)]) / 2.0D;
                }
            }


            result.add(new MeanResult(Integer.valueOf(i + infoCols), mean, median, headers[
                    (i + infoCols)], addedVals / rows));
        }
        pm.close();
        if (pm.isCanceled()) {
            return null;
        }
        return result;
    }

    public static TreeSet<MeanResult> getWiesiaMeans(File source, char delimiter, int infoCols) throws IOException {
        TreeSet<MeanResult> result = new TreeSet();
        RandomAccessFile in = new RandomAccessFile(source, "r");
        Vector<Long> indexList = new Vector();
        String thisLine = in.readLine();
        if (thisLine == null) {
            return null;
        }
        String[] headers = thisLine.split(delimiter + "");
        int cols = headers.length - infoCols;
        ProgressMonitor pm = new ProgressMonitor(null, "Getting mean values",
                null, 0, cols);
        in.seek(0L);
        while (in.nextLine()) {
            for (int i = 0; i < infoCols; i++) {
                in.readString(delimiter, false);
            }
            if (in.peek() > 0) {
                indexList.add(Long.valueOf(in.getFilePointer()));
            }
        }
        Long[] indices = indexList.toArray(new Long[0]);
        ArrayList<Integer> badCols = new ArrayList();
        int rows = indices.length;
        for (int i = 0; (i < cols) && (!pm.isCanceled()); i++) {
            pm.setProgress(i);
            pm.setNote(headers[i]);
            double mean = 0.0D;
            int thisRows = rows;
            int misses = 0;
            int addedVals = 0;
            double[] vals = new double[rows];
            for (int j = 0; j < rows; j++) {
                in.seek(indices[j].longValue());
                double val = Double.NaN;
                try {
                    val = Double.parseDouble(in.readString(delimiter, false));

                    vals[j] = val;
                    addedVals++;

                } catch (Exception e) {

                    mean += 0.0D;
                }
                indices[j] = Long.valueOf(in.getFilePointer());
            }

            Arrays.sort(vals);
            int twoPercent = (int) (addedVals * 0.02D);

            for (int j = twoPercent; j < addedVals - twoPercent; j++) {
                mean += vals[j];
            }
            mean /= addedVals;
            double median = Double.NaN;
            if (addedVals % 2 == 1) {
                int index = (addedVals - 1) / 2;
                median = vals[index];
            } else {
                int index = addedVals / 2;
                if (index == 0) {
                    median = 0.0D;
                }
            }


            result.add(new MeanResult(Integer.valueOf(i + infoCols), mean, median, headers[
                    (i + infoCols)], addedVals / rows));
        }
        pm.close();
        if (pm.isCanceled()) {
            return null;
        }
        return result;
    }

    public static TreeSet<MeanResult> getMADMeans(File source, double[] medians, char delimiter, int infoCols) throws IOException {
        TreeSet<MeanResult> result = new TreeSet();
        RandomAccessFile in = new RandomAccessFile(source, "r");
        Vector<Long> indexList = new Vector();
        String thisLine = in.readLine();
        if (thisLine == null) {
            return null;
        }
        String[] headers = thisLine.split(delimiter + "");
        int cols = headers.length - infoCols;
        ProgressMonitor pm = new ProgressMonitor(null, "Getting mean values",
                null, 0, cols);
        in.seek(0L);
        while (in.nextLine()) {
            for (int i = 0; i < infoCols; i++) {
                in.readString(delimiter, false);
            }
            if (in.peek() > 0) {
                indexList.add(Long.valueOf(in.getFilePointer()));
            }
        }
        Long[] indices = indexList.toArray(new Long[0]);
        ArrayList<Integer> badCols = new ArrayList();
        int rows = indices.length;
        for (int i = 0; (i < cols) && (!pm.isCanceled()); i++) {
            pm.setProgress(i);
            pm.setNote(headers[i]);
            double mean = 0.0D;
            int thisRows = rows;
            int misses = 0;
            int addedVals = 0;
            double[] vals = new double[rows];
            for (int j = 0; j < rows; j++) {
                in.seek(indices[j].longValue());
                double val = Double.NaN;
                try {
                    val = Double.parseDouble(in.readString(delimiter, false));
                    mean += val;
                    vals[j] = Math.abs(val - medians[i]);
                    addedVals++;

                } catch (Exception e) {

                    mean += 0.0D;
                }
                indices[j] = Long.valueOf(in.getFilePointer());
            }
            mean /= addedVals;
            Arrays.sort(vals);
            double median = Double.NaN;
            if (addedVals % 2 == 1) {
                int index = (addedVals - 1) / 2;
                median = vals[index];
            } else {
                int index = addedVals / 2;
                if (index != 0) {
                    median = (vals[index] + vals[(index - 1)]) / 2.0D;
                }
            }


            result.add(new MeanResult(Integer.valueOf(i + infoCols), mean, median, headers[
                    (i + infoCols)], addedVals / rows));
        }
        pm.close();
        if (pm.isCanceled()) {
            return null;
        }
        return result;
    }

    public static File normalize(File source, char delimiter, int infoCols, Collection<MeanResult> means, double newMean) throws IOException {
        double[] factors = new double[means.size()];
        int index = 0;
        for (MeanResult mean : means) {
            factors[(index++)] = (newMean / mean.getMean());
        }
        File dest = new File(source.getAbsolutePath() + ".normalized");
        BufferedReader in = new BufferedReader(new FileReader(source));
        BufferedWriter out = new BufferedWriter(new FileWriter(dest));
        out.write(in.readLine());
        out.newLine();

        DecimalFormat formatter = new DecimalFormat("#.####");
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            String[] splitLine = thisLine.split(delimiter + "");


            if (infoCols > 0) {
                out.write(splitLine[0]);
            }
            for (int i = 1; i < infoCols; i++) {
                out.write("\t" + splitLine[i]);
            }
            for (int i = 0; i < factors.length; i++) {
                if (i + infoCols >= splitLine.length) {
                    out.write(delimiter);
                } else {
                    String data = splitLine[(i + infoCols)];
                    if ((data != null) && (!data.equals(""))) {
                        try {
                            double val = Double.parseDouble(data);
                            val *= factors[i];
                            out.write(delimiter + formatter.format(val));
                        } catch (NumberFormatException nfe) {
                            System.err.println(nfe);
                            out.write(delimiter);
                        }
                    } else
                        out.write(delimiter);
                }
            }
            out.newLine();
        }
        in.close();
        out.close();
        String originalName = source.getAbsolutePath();
        source.delete();
        if (!dest.renameTo(new File(originalName))) {
            return dest;
        }
        return source;
    }


    public static File medianNormalize(File source, char delimiter, int infoCols, Collection<MeanResult> means, double newMean)
            throws IOException {
        double[] factors = new double[means.size()];
        int index = 0;
        for (MeanResult mean : means) {
            factors[(index++)] = (newMean / mean.getMedian());
        }
        File dest = new File(source.getAbsolutePath() + ".normalized");
        BufferedReader in = new BufferedReader(new FileReader(source));
        BufferedWriter out = new BufferedWriter(new FileWriter(dest));
        out.write(in.readLine());
        out.newLine();

        DecimalFormat formatter = new DecimalFormat("#.####");
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            String[] splitLine = thisLine.split(delimiter + "");


            if (infoCols > 0) {
                out.write(splitLine[0]);
            }
            for (int i = 1; i < infoCols; i++) {
                out.write("\t" + splitLine[i]);
            }
            for (int i = 0; i < factors.length; i++) {
                if (i + infoCols >= splitLine.length) {
                    out.write(delimiter);
                } else {
                    String data = splitLine[(i + infoCols)];
                    if ((data != null) && (!data.equals(""))) {
                        try {
                            double val = Double.parseDouble(data);
                            val *= factors[i];
                            out.write(delimiter + formatter.format(val));
                        } catch (NumberFormatException nfe) {
                            System.err.println(nfe);
                            out.write(delimiter);
                        }
                    } else
                        out.write(delimiter);
                }
            }
            out.newLine();
        }
        in.close();
        out.close();
        String originalName = source.getAbsolutePath();
        source.delete();
        if (!dest.renameTo(new File(originalName))) {
            return dest;
        }
        return source;
    }


    public static File wiesiaNormalize(File source, char delimiter, int infoCols, Collection<MeanResult> means, double newMean)
            throws IOException {
        double[] factors = new double[means.size()];
        double[] newMedians = new double[means.size()];
        int index = 0;
        for (MeanResult mean : means) {
            factors[index] = (newMean / mean.getMean());
            factors[index] *= mean.getMedian();
            index++;
        }

        File dest = new File(source.getAbsolutePath() + ".normalized");
        File tempDest = new File(source.getAbsolutePath() + ".normalized.tmp");
        BufferedReader in = new BufferedReader(new FileReader(source));
        BufferedWriter out = new BufferedWriter(new FileWriter(tempDest));
        out.write(in.readLine());
        out.newLine();

        DecimalFormat formatter = new DecimalFormat("#.####");
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            String[] splitLine = thisLine.split(delimiter + "");


            if (infoCols > 0) {
                out.write(splitLine[0]);
            }
            for (int i = 1; i < infoCols; i++) {
                out.write(delimiter + splitLine[i]);
            }
            for (int i = 0; i < factors.length; i++) {
                if (i + infoCols >= splitLine.length) {
                    out.write(delimiter);
                } else {
                    String data = splitLine[(i + infoCols)];
                    if ((data != null) && (!data.equals(""))) {
                        try {
                            double val = Double.parseDouble(data);
                            val *= factors[i];

                            out.write(delimiter + formatter.format(val));
                        } catch (NumberFormatException nfe) {
                            System.err.println(nfe);
                            out.write(delimiter);
                        }
                    } else
                        out.write(delimiter);
                }
            }
            out.newLine();
        }
        in.close();
        out.close();
        TreeSet<MeanResult> newMeans = getMADMeans(tempDest, newMedians,
                delimiter, infoCols);
        double C = 0.0D;
        for (MeanResult mean : newMeans) {
            C += mean.getMedian();
        }
        C /= newMeans.size();
        factors = new double[newMeans.size()];
        index = 0;
        for (MeanResult mean : newMeans) {
            factors[(index++)] = (C / mean.getMedian());
        }
        in = new BufferedReader(new FileReader(tempDest));
        out = new BufferedWriter(new FileWriter(dest));
        out.write(in.readLine());
        out.newLine();
        while ((thisLine = in.readLine()) != null) {
            String[] splitLine = thisLine.split(delimiter + "");


            if (infoCols > 0) {
                out.write(splitLine[0]);
            }
            for (int i = 1; i < infoCols; i++) {
                out.write("\t" + splitLine[i]);
            }
            for (int i = 0; i < factors.length; i++) {
                if (i + infoCols >= splitLine.length) {
                    out.write(delimiter);
                } else {
                    String data = splitLine[(i + infoCols)];
                    if ((data != null) && (!data.equals(""))) {
                        try {
                            double val = Double.parseDouble(data);
                            val *= factors[i];
                            out.write(delimiter + formatter.format(val));
                        } catch (NumberFormatException nfe) {
                            System.err.println(nfe);
                            out.write(delimiter);
                        }
                    } else
                        out.write(delimiter);
                }
            }
            out.newLine();
        }
        in.close();
        out.close();
        String originalName = source.getAbsolutePath();
        source.delete();

        if (!dest.renameTo(new File(originalName))) {
            return dest;
        }
        return source;
    }


    public static File normalize(File source, File dest, char delimiter, int infoCols, Double newMean)
            throws IOException {
        RandomAccessFile in = new RandomAccessFile(source, "r");
        Vector<Long> indexList = new Vector();
        int cols = in.readLine().split(delimiter + "").length - infoCols;
        in.seek(0L);
        while (in.nextLine()) {
            for (int i = 0; i < infoCols; i++) {
                in.readString(delimiter, false);
            }
            if (in.peek() > 0) {
                indexList.add(Long.valueOf(in.getFilePointer()));
            }
        }
        Long[] indices = indexList.toArray(new Long[0]);
        int rows = indices.length;
        double[] factors = new double[cols];
        for (int i = 0; i < cols; i++) {
            double mean = 0.0D;
            int thisRows = rows;
            for (int j = 0; j < rows; j++) {
                in.seek(indices[j].longValue());
                String val = "no data read yet";
                try {
                    val = in.readString(delimiter, false);
                    mean += Double.parseDouble(val);

                } catch (Exception e) {
                    thisRows--;
                }
                indices[j] = Long.valueOf(in.getFilePointer());
            }
            mean /= thisRows;
            System.out.println("Column " + i + " mean: " + mean);
            factors[i] = (newMean.doubleValue() / mean);
        }


        BufferedWriter out = new BufferedWriter(new FileWriter(dest));
        in.seek(0L);
        out.write(in.readLine());
        out.newLine();
        DecimalFormat formatter = new DecimalFormat("#.####");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < infoCols; col++) {
                out.write(in.readString(delimiter, false) + delimiter);
            }
            for (int col = 0; col < cols; col++) {
                String strVal = "no data read yet";
                try {
                    strVal = in.readString(delimiter, false);
                    double val = Double.parseDouble(strVal);
                    val *= factors[col];
                    out.write(formatter.format(val));
                } catch (Exception e) {
                    System.err.println("Error on row " + row + ", col " + col +
                            ", value=" + strVal);
                    System.err.println("\t" + e);
                }


                if (col + 1 == cols) {
                    out.newLine();
                } else {
                    out.write(delimiter);
                }
            }
        }
        in.close();
        out.close();
        return dest;
    }

    public static class MeanResult implements Comparable<MeanResult> {
        private String name;
        private Integer col;
        private double mean;
        private double median;
        private double percentPresent;

        public MeanResult(Integer col, double mean, double median, String name, double percentPresent) {
            this.col = col;
            this.mean = mean;
            this.name = name;

            this.median = median;
            this.percentPresent = percentPresent;
        }

        public String getName() {
            return name;
        }

        public Integer getCol() {
            return col;
        }

        public double getMean() {
            return mean;
        }

        public double percentPresent() {
            return percentPresent;
        }

        @Override
		public int compareTo(MeanResult o) {
            if (o == null) {
                return 1;
            }
            if (col.intValue() > o.getCol().intValue()) {
                return 1;
            }
            if (col.intValue() < o.getCol().intValue()) {
                return -1;
            }
            return 0;
        }


        public double getMedian() {
            return median;
        }
    }

    public static class SplitResult {
        boolean split;
        File loggedFile;
        File nonloggedFile;

        public SplitResult() {
        }

        public boolean isSplit() {
            return split;
        }

        public File getLoggedFile() {
            return loggedFile;
        }

        public File getNonloggedFile() {
            return nonloggedFile;
        }

        public File getSelectedDataFile() {
            return selectedDataFile;
        }

        public File getSelectedMetadataFile() {
            return selectedMetadataFile;
        }

        public File getNonloggedMetadataFile() {
            return nonloggedMetadataFile;
        }

        public File getLoggedMetadataFile() {
            return loggedMetadataFile;
        }

        File loggedMetadataFile;
        File nonloggedMetadataFile;
        File selectedDataFile;
        File selectedMetadataFile;
    }

    public static void main(String[] args) throws Exception {
        File source = Utils.chooseFileToOpen();
        if (source == null) {
            return;
        }
        TreeSet<MeanResult> means = getMeans(source, '\t', 5);
        int index = 0;
        for (MeanResult mean : means) {
            System.out.println("Column " + index + ": " + mean);
            index++;
        }
    }


    public static void removeBadCols(File source, TreeSet<MeanResult> means, double cutoff)
            throws IOException {
        TreeSet<Integer> badCols = new TreeSet();
        ArrayList<MeanResult> badMeans = new ArrayList();
        for (MeanResult mean : means) {
            if (mean.percentPresent() < cutoff) {
                badCols.add(mean.col);
                badMeans.add(mean);
            }
        }
        means.removeAll(badMeans);
        File dest = new File(source.getParent(), source.getName() +
                ".clean.tmp");
        BufferedReader in = new BufferedReader(new FileReader(source));
        BufferedWriter out = new BufferedWriter(new FileWriter(dest));
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            String[] splitLine = thisLine.split("\t");
            out.write(splitLine[0]);
            for (int i = 1; i < splitLine.length; i++) {
                if (!badCols.contains(Integer.valueOf(i))) {
                    out.write("\t" + splitLine[i]);
                }
            }
            out.newLine();
        }
        in.close();
        out.close();
        source.delete();
        dest.renameTo(source);
    }


    public static SplitResult newshowSplitDialog(final File source, final File metadataFile,
                                                 final TreeSet<MeanResult> means, final boolean force,
                                                 final Double newMean) {
        final SplitResult result = new SplitResult();
        result.split = false;


        ArrayList<MeanResult> nonlogged = new ArrayList();
        ArrayList<MeanResult> logged = new ArrayList();

        boolean missingData = false;
        for (MeanResult thisMean : means) {
            double mean = thisMean.getMean();
            if (mean <= 15.0D) {
                logged.add(thisMean);
            } else {
                nonlogged.add(thisMean);
            }
            if (thisMean.percentPresent() < 1.0D) {
                missingData = true;
            }
        }
        if (((logged.size() == 0) || (nonlogged.size() == 0)) && (!missingData) &&
                (!force)) {
            return result;
        }
        if (!force) {
            StringBuilder message = new StringBuilder(
                    "The imported data has the following potential problems:\n");
            if ((logged.size() != 0) && (nonlogged.size() != 0)) {
                message.append(" - Contains both logged and non-logged data\n");
            }
            if (missingData) {
                message.append(" - Some samples are missing data\n");
            }

            message.append("Would you like to review and correct these problems?");
            int showDialog =
                    JOptionPane.showConfirmDialog(
                            MetaOmGraph.getMainWindow(),
                            message.toString(),
                            "Potential data problem",
                            0,
                            3);
            if (showDialog != 0) {
                return result;
            }
        }
        final String[][] nonloggedData = new String[nonlogged.size()][2];
        final String[][] loggedData = new String[logged.size()][2];
        for (int i = 0; i < nonloggedData.length; i++) {
            MeanResult thisMean = nonlogged.get(i);
            nonloggedData[i][0] = thisMean.getName();
            nonloggedData[i][1] = thisMean.getMean() + "";
        }
        for (int i = 0; i < loggedData.length; i++) {
            MeanResult thisMean = logged.get(i);
            loggedData[i][0] = thisMean.getName();
            loggedData[i][1] = thisMean.getMean() + "";
        }

        NoneditableTableModel loggedModel = new NoneditableTableModel(
                loggedData, new String[]{"Column Name", "Mean"});
        NoneditableTableModel nonloggedModel = new NoneditableTableModel(
                nonloggedData, new String[]{"Column Name", "Mean"});
        StripedTable loggedTable = new StripedTable(loggedModel);
        StripedTable nonloggedTable = new StripedTable(nonloggedModel);
        JScrollPane loggedPane = new JScrollPane(loggedTable);
        JScrollPane nonloggedPane = new JScrollPane(nonloggedTable);
        loggedPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createTitledBorder("Logged")));
        nonloggedPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createTitledBorder("Non-Logged")));
        JPanel splitPanel = new JPanel(new BorderLayout());
        splitPanel.add(nonloggedPane, "West");
        splitPanel.add(loggedPane, "East");
        String[] fileNames = {"Non-Logged Data File", "Logged Data File"};
        String[] extensions = {"txt", "txt"};
        String[] descriptions = {"Tab-Delimited Text Files",
                "Tab-Delimited Text Files"};
        String[] defaultFiles = {
                Utils.removeExtension(source) + " - nonlogged." +
                        Utils.getExtension(source),
                Utils.removeExtension(source) + " - logged." +
                        Utils.getExtension(source)};
        JPanel filePanel = new JPanel(new BorderLayout());
        final FileSelectionPanel fileSelectPanel = new FileSelectionPanel(
                fileNames, defaultFiles, extensions, descriptions);
        final JCheckBox splitBox = new JCheckBox(
                "Split logged and non-logged data");
        splitBox.setSelected(true);
        splitBox.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                fileSelectPanel.setEnabled(splitBox.isSelected());
            }

        });
        filePanel.add(splitBox, "North");
        filePanel.add(fileSelectPanel, "Center");
        splitPanel.add(filePanel, "South");

        JPanel missingPanel = new JPanel(new BorderLayout());
        Object[][] data = new Object[means.size()][2];
        int index = 0;
        for (MeanResult thisMean : means) {
            data[index][0] = thisMean.getName();


            data[index][1] = Double.valueOf(thisMean.percentPresent());
            index++;
        }
        String[] headers = {"Sample Name", "Percent Present"};
        NoneditableTableModel missingModel = new NoneditableTableModel(data,
                headers);
        StripedTable missingTable = new StripedTable(missingModel);
        TableSorter sorter = new TableSorter(missingModel, missingTable.getTableHeader());
        missingTable.setModel(sorter);
        JPanel spinnerPanel = new JPanel();
        final JCheckBox excludeBox = new JCheckBox("Exclude samples with percentage less than:");
        excludeBox.setSelected(false);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.8D, 0.0D, 1.0D, 0.01D);
        final JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setEnabled(false);
        excludeBox.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                spinner.setEnabled(excludeBox.isSelected());
            }

        });
        spinnerPanel.add(excludeBox);
        spinnerPanel.add(spinner);
        spinner.setMinimumSize(new Dimension(75, spinner.getMinimumSize().height));
        spinner.setPreferredSize(new Dimension(75, spinner.getPreferredSize().height));

        missingPanel.add(new JScrollPane(missingTable), "Center");
        missingPanel.add(spinnerPanel, "South");

        final JDialog dialog = new JDialog(MetaOmGraph.getMainWindow(),
                "Column Means", true);


        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Logged/Non-logged", splitPanel);
        tabPane.addTab("Missing Data", missingPanel);
        dialog.add(tabPane, "Center");
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, "South");
        okButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {

                if (!splitBox.isSelected() && !excludeBox.isSelected()) {
                    result.split = false;
                    dialog.dispose();
                }
                if (splitBox.isSelected() && !fileSelectPanel.checkFiles()) {
                    return;
                }


                new AnimatedSwingWorker("Working", true) {

                    @Override
                    public Object construct() {
                        if (excludeBox.isSelected() && !splitBox.isSelected()) {
                            try {
                                removeBadCols(source, means, (Double) spinner.getValue());
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                            result.split = false;
                            return null;
                        }
                        ArrayList<Integer> loggedCols = new ArrayList<Integer>(
                                loggedData.length);
                        ArrayList<Integer> nonloggedCols = new ArrayList<Integer>(
                                nonloggedData.length);
                        File loggedFile = fileSelectPanel.getFile(1);
                        File nonloggedFile = fileSelectPanel.getFile(0);
                        double[] factors;
                        if (newMean != null) {
                            factors = new double[means.size()];
                        } else {
                            factors = null;
                        }
                        int index = 0;
                        for (MeanResult thisMean : means) {
                            if (excludeBox.isSelected() && thisMean.percentPresent() < (Double) spinner.getValue()) {
                                // Do nothing
                            } else if (thisMean.getMean() <= 15) {
                                loggedCols.add(thisMean.getCol());
                            } else {
                                nonloggedCols.add(thisMean.getCol());
                                if (newMean != null) {
                                    factors[index] = newMean
                                            / thisMean.getMean();
                                }
                            }
                            index++;
                        }
                        File[] resultFiles = null;
                        try {
                            if (newMean != null) {
                                resultFiles = FileSplitter.splitAndNormalize(source,
                                        nonloggedFile, loggedFile, metadataFile,
                                        nonloggedCols, loggedCols, "\t",
                                        factors);
                            } else {
                                resultFiles = FileSplitter.splitFile(source, nonloggedFile,
                                        loggedFile, metadataFile, nonloggedCols,
                                        loggedCols, "\t");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(MetaOmGraph
                                            .getMainWindow(),
                                    "There was an error during split:\n"
                                            + e.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        if (resultFiles == null) {
                            result.split = false;
                            return null;
                        }
                        result.split = true;
                        result.nonloggedFile = resultFiles[0];
                        result.loggedFile = resultFiles[1];
                        result.nonloggedMetadataFile = resultFiles[2];
                        result.loggedMetadataFile = resultFiles[3];
                        return null;
                    }

                    @Override
					public void finished() {
                        super.finished();
                        dialog.dispose();
                    }


                }.start();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                result.split = false;
                dialog.dispose();
            }

        });
        dialog.setDefaultCloseOperation(2);
        dialog.pack();
        dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
        dialog.setVisible(true);
        return result;
    }
}
