package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;


public class TabularDataFile {
    private ArrayList<Long> dataStarts;
    private String[] headers;
    private int infoColumns;
    private ArrayList<String[]> rowNames;
    private int headerCount;
    private int headerRow;
    private RandomAccessFile myFile;
    private File source;
    private char delimiter;
    private boolean ignoreConsecutive;
    private TreeMap<Integer, String[]> buffer;
    private ArrayList<Integer> bufferAge;

    public TabularDataFile(File source, int infoColumns, int headerCount, int headerRow)
            throws IOException {
        this(source, infoColumns, headerCount, headerRow, "csv".equals(Utils.getExtension(source)) ? ',' : '\t', false);
    }

    public TabularDataFile(File source, int infoColumns, int headerCount, int headerRow, char delimiter, boolean ignoreConsecutiveDelimiters)
            throws IOException {
        this.source = source;
        this.infoColumns = infoColumns;
        this.headerCount = headerCount;
        this.headerRow = headerRow;
        myFile = new RandomAccessFile(source, "r");
        dataStarts = new ArrayList();
        int row = 0;
        while (row < headerRow) {
            myFile.nextLine();
            row++;
        }
        headers = myFile.readAndSplitLine('\t', false);
        row++;
        while (row < headerCount) {
            myFile.nextLine();
            row++;
        }
        do {
            if (infoColumns == 0) {
                rowNames.add(new String[]{(row - headerCount) + ""});
            } else {
                String[] names = new String[infoColumns];
                for (int i = 0; i < names.length; i++) {
                    names[i] = myFile.readString(delimiter,
                            ignoreConsecutiveDelimiters);
                }
                rowNames.add(names);
            }
            dataStarts.add(Long.valueOf(myFile.getFilePointer()));
        } while (


                myFile.nextLine());
        buffer = new TreeMap();
        bufferAge = new ArrayList(101);
    }

    public String[] getHeaders() {
        return headers;
    }

    public String getHeader(int row) {
        return headers[row];
    }

    public int getInfoColumns() {
        return infoColumns;
    }

    public ArrayList<String[]> getRowNames() {
        return rowNames;
    }

    public String[] getRowNames(int row) {
        return rowNames.get(row);
    }

    public File getSource() {
        return source;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public String[] getData(int row) throws IOException {
        if (buffer.containsKey(Integer.valueOf(row))) {
            return getRowFromBuffer(row);
        }
        myFile.seek(dataStarts.get(row).longValue());
        return myFile.readAndSplitLine(delimiter, ignoreConsecutive);
    }

    public String[][] getData(int[] rows) throws IOException {
        String[][] result = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            if (buffer.containsKey(Integer.valueOf(rows[i]))) {
                result[i] = getRowFromBuffer(rows[i]);
            } else {
                myFile.seek(dataStarts.get(rows[i]).longValue());
                result[i] = myFile.readAndSplitLine(delimiter,
                        ignoreConsecutive);
            }
        }
        return result;
    }

    public String[] getRowFromBuffer(int row) {
        if (!buffer.containsKey(Integer.valueOf(row))) {
            return null;
        }
        bufferAge.remove(Integer.valueOf(row));
        bufferAge.add(Integer.valueOf(row));
        return buffer.get(Integer.valueOf(row));
    }

    public void addToBuffer(int row, String[] data) {
        if (buffer.containsKey(Integer.valueOf(row))) {
            bufferAge.remove(Integer.valueOf(row));
            bufferAge.add(Integer.valueOf(row));
            return;
        }
        buffer.put(Integer.valueOf(row), data);
        bufferAge.add(Integer.valueOf(row));
        if (bufferAge.size() >= 100) {
            bufferAge.remove(0);
        }
    }
}
