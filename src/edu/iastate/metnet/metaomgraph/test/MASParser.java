package edu.iastate.metnet.metaomgraph.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;


public class MASParser {
    public MASParser() {
    }

    public static void main(String[] args)
            throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(0);
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showOpenDialog(null) != 0) {
            return;
        }
        File[] infiles = chooser.getSelectedFiles();
        MASFile[] mases = new MASFile[infiles.length];
        int headers = Integer.parseInt(JOptionPane.showInputDialog(
                "How many header rows?", Integer.valueOf(1)));
        for (int i = 0; i < mases.length; i++) {
            mases[i] = new MASFile(infiles[i].getAbsolutePath(), headers);
            System.out.println("Added " + mases[i].getName());
        }
        int rows = mases[0].getRowCount();
        System.out.println("Row count: " + rows);
        boolean error = false;
        for (MASFile thisFile : mases) {
            if (thisFile.getRowCount() != rows) {
                System.err.println(thisFile.getName() + ": " +
                        thisFile.getRowCount() + "!=" + rows);
                error = true;
            }
        }
        if (error)
            return;
        parseThem(mases);
    }

    private static File parseThem(MASFile[] infiles) throws IOException {
        File outfile = new File(infiles[0].getParent(), "MASparsed.csv");
        BufferedReader[] ins = new BufferedReader[infiles.length];
        for (int i = 0; i < ins.length; i++) {
            ins[i] = new BufferedReader(new FileReader(infiles[i]));
            for (int j = 1; j++ < infiles[i].getHeaderRowCount(); ins[i].readLine()) {
            }
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(outfile));

        int j;

        String[] splitLine;

        int k;

        for (int i = 0; i < infiles[0].getRowCount(); i++) {
            out.write(infiles[0].getRowID(i));
            for (j = 0; j < ins.length; j++) {
                splitLine = ins[j].readLine().split("\t");
                for (k = 0; k < infiles[j].getSignalColCount(); k++) {
                    try {
                        out.write(',' + splitLine[infiles[j].getSignalCol(k)]);
                    } catch (Exception e) {
                        System.err.println("Error: trying to print for file " +
                                infiles[j].getName() + " column " +
                                infiles[j].getSignalCol(k) + " row " + i);
                        e.printStackTrace();
                    }
                }
            }
            out.newLine();
        }
        out.close();
        for (BufferedReader thisIn : ins) {
            thisIn.close();
        }
        return outfile;
    }

    private static File parseIt(File infile) throws IOException {
        if (infile == null)
            throw new InvalidParameterException("infile can't be null");
        if (!infile.canRead()) {
            throw new InvalidParameterException(infile.getAbsolutePath() +
                    " can't be read!");
        }
        File outfile = new File(infile.getAbsolutePath() + ".mogfriendly.csv");
        BufferedReader in = new BufferedReader(new FileReader(infile));
        String thisLine = in.readLine();
        String nextLine = in.readLine();
        while (nextLine.startsWith(" ")) {
            thisLine = nextLine;
            nextLine = in.readLine();
        }
        ArrayList<Integer> signalCols = getSignalCols(thisLine);
        String[] splitLine = thisLine.split("\t");
        BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
        out.write(splitLine[0]);
        for (int i = 0; i < signalCols.size(); i++) {
            out.write(',' + splitLine[signalCols.get(i).intValue()]);
        }
        out.newLine();
        while ((thisLine = in.readLine()) != null) {
            splitLine = thisLine.split("\t");
            out.write(splitLine[0]);
            for (int i = 0; i < signalCols.size(); i++) {
                out.write(',' + splitLine[signalCols.get(i).intValue()]);
            }
            out.newLine();
        }
        in.close();
        out.close();
        return outfile;
    }

    private static ArrayList<Integer> getSignalCols(String headers) {
        ArrayList<Integer> result = new ArrayList();
        String[] splitLine = headers.split("\t");
        for (int i = 1; i < splitLine.length; i++) {
            if (splitLine[i].endsWith("_Signal")) {
                result.add(Integer.valueOf(i));
            }
        }
        return result;
    }

    private static class MASFile
            extends File {
        private String[] headers;
        private ArrayList<Integer> signalCols;
        private ArrayList<String> rowIDs;
        private int headerRowCount;

        public MASFile(String pathname, int headerRows)
                throws IOException {
            super(pathname);
            rowIDs = new ArrayList();
            BufferedReader in = new BufferedReader(new FileReader(this));

            for (int j = 1; j++ < headerRows; in.readLine()) {
            }

            String header = null;
            String thisLine;
            while ((thisLine = in.readLine()) != null) {
                String[] splitLine = thisLine.split("\t", 2);
                if (splitLine.length > 1) {
                    rowIDs.add(splitLine[0]);
                    if (header == null) {
                        header = thisLine;
                    }
                }
            }
            signalCols = MASParser.getSignalCols(header);
            headers = header.split("\t");
            headerRowCount = headerRows;
            in.close();
        }

        public int getRowCount() {
            return rowIDs.size();
        }

        public String getRowID(int row) {
            return rowIDs.get(row);
        }

        public String getColumnHeader(int col) {
            return headers[col];
        }

        public int getSignalCol(int col) {
            return signalCols.get(col).intValue();
        }

        public int getSignalColCount() {
            return signalCols.size();
        }

        public int getHeaderRowCount() {
            return headerRowCount;
        }
    }
}
