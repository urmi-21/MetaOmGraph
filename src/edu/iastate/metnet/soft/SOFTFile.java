package edu.iastate.metnet.soft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

public class SOFTFile extends File {
    private String[] rowIDs;

    public SOFTFile(File source) {
        super(source, "");
        init();
    }

    public SOFTFile(String pathname) {
        super(pathname);
        init();
    }

    private void init() {
        if (!exists()) {
            return;
        }
        String thisLine;
        try {
            BufferedReader in = new BufferedReader(new java.io.FileReader(this));
            do {
                thisLine = in.readLine();
            } while ((thisLine != null) && (!thisLine.toLowerCase().equals("!platform_table_begin")));
            if (thisLine == null) {
                in.close();
                return;
            }
            thisLine = in.readLine();
            thisLine = thisLine.substring(thisLine.indexOf('\t') + 1);
            rowIDs = thisLine.split("\t");
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRowIDs() {
        return rowIDs;
    }


    public Hashtable<String, String[]> getRowIDHash(String[] columns) {
        if (rowIDs == null) {
            return null;
        }
        if (!exists()) {
            return null;
        }
        if ((columns == null) || (columns.length <= 0)) {
            return null;
        }
        int[] colsToGet = new int[columns.length];
        for (int x = 0; x < colsToGet.length; colsToGet[(x++)] = -1) {
        }

        boolean hit = false;
        for (int x = 0; x < columns.length; x++) {
            for (int i = 0; i < rowIDs.length; i++) {
                if (rowIDs[i].toLowerCase().equals(columns[x].toLowerCase())) {
                    colsToGet[x] = i;
                    hit = true;
                }
            }
        }
        if (!hit) {
            return null;
        }
        Hashtable<String, String[]> result = new Hashtable();
        System.out.println("Hashtable created");
        String thisLine;
        try {
            BufferedReader in = new BufferedReader(new java.io.FileReader(this));
            do {
                thisLine = in.readLine();
            } while ((thisLine != null) && (!
                    thisLine.toLowerCase().equals("!platform_table_begin")));
            if (thisLine == null) {
                return null;
            }
            in.readLine();
            thisLine = in.readLine();
            System.out.println("Ready to read IDs");
            while ((thisLine != null) && (!
                    thisLine.toLowerCase().equals("!platform_table_end"))) {
                String[] ids = new String[columns.length];
                String[] splitLine = thisLine.split("\t", rowIDs.length + 1);
                String thisIDRef = splitLine[0];
                for (int x = 0; x < ids.length; x++) {
                    if (colsToGet[x] < 0) {
                        ids[x] = " ";
                    } else
                        ids[x] = splitLine[(colsToGet[x] + 1)];
                }
                result.put(thisIDRef, ids);
                thisLine = in.readLine();
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
