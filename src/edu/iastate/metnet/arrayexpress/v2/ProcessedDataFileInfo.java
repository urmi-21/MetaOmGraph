package edu.iastate.metnet.arrayexpress.v2;

import edu.iastate.metnet.metaomgraph.RandomAccessFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class ProcessedDataFileInfo {
    File source;
    TreeMap<String, Long> dataStarts;
    TreeSet<Integer> dataCols;
    ArrayList<String> headers;
    int headerRow;
    TreeSet<DataCol> newDataCols;
    private ArrayList<String> allHeaders;

    public ProcessedDataFileInfo(File source, String expID) throws IOException {
        this.source = source;
        dataStarts = new TreeMap();
        dataCols = new TreeSet();
        newDataCols = new TreeSet();
        allHeaders = new ArrayList();
        RandomAccessFile in = new RandomAccessFile(source, "r");
        int headersToUse = -1;
        int dataRow = 0;


        long headerRowPointer = 0L;
        long thisRowPointer = 0L;
        String thisLine;
        while (((thisLine = in.readString('\t', false)) != null) && (!
                thisLine.startsWith("Affymetrix:CompositeSequence"))) { //String thisLine;
            if (thisLine.startsWith("Hybridization REF")) {
                headersToUse = dataRow;
                headerRowPointer = thisRowPointer;
            }
            dataRow++;

            in.nextLine();
            thisRowPointer = in.getFilePointer();
        }
        if (headersToUse >= 0) {
            headerRow = headersToUse;
        } else {
            headerRow = 0;
        }
        System.out.println("Using header row " + headerRow);
        in.seek(0L);
        headers = new ArrayList();
        for (int i = 0; i < dataRow; i++) {
            String[] splitLine = in.readAndSplitLine('\t', false);
            if (splitLine != null) {

                for (int j = 0; j < splitLine.length; j++) {
                    for (String ending : AEProjectMaker.headerList)
                        if (((splitLine[j].endsWith(ending)) || (
                                (source.getName().endsWith("sample_table.txt")) &&
                                        ("VALUE".equals(splitLine[j])))) &&
                                (!dataCols.contains(Integer.valueOf(j)))) {


                            dataCols.add(Integer.valueOf(j));
                            long currentPointer = in.getFilePointer();
                            in.seek(headerRowPointer);
                            String[] splitHeaders = in.readAndSplitLine('\t', false);

                            String fileName = source.getName();
                            String thisHeader;
                            if (((fileName.endsWith("sample_table.txt")) ||
                                    (fileName.endsWith("externaldata.txt")) ||
                                    (fileName.endsWith("_norm.txt"))) && (
                                    ("VALUE".equals(splitLine[j])) ||
                                            ("GC-RMA_Signal".equals(splitLine[j])) ||
                                            ("AFFYMETRIX_VALUE".equals(splitLine[j])) ||
                                            ("Signal".equals(splitLine[j])))) {


                                thisHeader = fileName.substring(expID.length());
                            } else { //String thisHeader;
                                if (j >= splitHeaders.length) {
                                    thisHeader = expID + " - " + j;
                                } else
                                    thisHeader = splitHeaders[j];
                            }
                            headers.add(thisHeader);
                            newDataCols.add(new DataCol(j, thisHeader));
                            in.seek(currentPointer);
                            System.out.println("Found data column: " + j);
                        }
                }
            }
        }
        allHeaders.addAll(headers);
        in.seek(0L);
        long pointer = 0L;
        while ((thisLine = in.readString('\t', false)) != null) {
            String probe = thisLine.substring(thisLine.lastIndexOf(":") + 1);

            if (AEProjectMaker.probeList.contains(probe)) {

                dataStarts.put(probe, Long.valueOf(pointer));
            }

            in.nextLine();
            pointer = in.getFilePointer();
        }
        in.close();
    }
}
