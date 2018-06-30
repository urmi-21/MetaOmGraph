package edu.iastate.metnet.arrayexpress.v2;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.iastate.metnet.arrayexpress.v2.AEImportDialog.ArrayInfo;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class AEProjectMaker {

    private static int threadIndex, currentDownloads, maxDownloads=5;

    private static boolean starting=false;

    private static DownloadThread[] downloadThreads;

    private static StripedTable downloadTable;

    private static TreeMap<String, Integer> rowMap;

    private static boolean canceled;

    public static TreeSet<String> headerList, probeList;

//	protected static TreeMap<String, String> celNames;

    private static int expsRemaining;

    private static JDialog downloadDialog;

    private static File dest, tempDir, metadataFile;

    private static ArrayInfo array;

    private static Collection<AEXMLNodeInfo> exps;

    private AEProjectMaker() {
    }

    private static class DownloadListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            AEXMLNodeInfo exp = (AEXMLNodeInfo) e.getSource();
            int row = rowMap.get(exp.accession);
            long size = exp.getDownloadProgress().getMax();
            if ("Done".equals(exp.getStatus())) {
                expsRemaining--;
            }
            if (expsRemaining == 0) {
                downloadDialog.dispose();
            }
            if (downloadTable==null || downloadTable.getModel()==null) {
                return;
            }
            downloadTable.getModel().setValueAt(Utils.getSizeString(size), row, 1);
            downloadTable.getModel().setValueAt(exp.getDownloadProgress(), row, 2);
            downloadTable.getModel().setValueAt(exp.getStatus(), row, 3);
        }

    }

    public static boolean createProject(Collection<AEXMLNodeInfo> exps,
                                        ArrayInfo array, File dest, File tempDir) {
        return createProject(exps,array,dest,tempDir,null);
    }

    public static boolean createProject(Collection<AEXMLNodeInfo> exps,
                                        ArrayInfo array, File dest, File tempDir, Frame parent) {
        expsRemaining = exps.size();
        AEProjectMaker.exps = exps;
        AEProjectMaker.array = array;
        AEProjectMaker.dest = dest;
        AEProjectMaker.tempDir = tempDir;
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        headerList = new TreeSet<String>();
        probeList = new TreeSet<String>();
//		celNames = new TreeMap<String, String>();
        System.out.println("Reading header list");
        try {
            String thisLine;
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    AEProjectMaker.class.getResourceAsStream("/resource/headerlist.txt")));
            while ((thisLine = in.readLine()) != null) {
                headerList.add(thisLine);
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Error reading header list");
            e.printStackTrace();
            return false;
        }
        System.out.println("Reading probelist");
        try {
            String thisLine;
            String probeFile = array.dir + "/probelist.txt";
            System.out.println(probeFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(AEProjectMaker.class.getResourceAsStream("/resource/arrayexpress/"+probeFile)));
            while ((thisLine = in.readLine()) != null) {
                probeList.add(thisLine);
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Error reading probe list");
            e.printStackTrace();
            return false;
        }
        String[] headers = { "ID", "Size", "Progress", "Status" };
        Object[][] tableData = new Object[exps.size()][headers.length];
        int index = 0;
        rowMap = new TreeMap<String, Integer>();
        DownloadListener listener = new DownloadListener();
        downloadThreads = new DownloadThread[exps.size()];
        for (AEXMLNodeInfo exp : exps) {
            tableData[index][0] = exp.accession;
            tableData[index][1] = "unknown";
            tableData[index][2] = new Progress(0, 100, 0);
            tableData[index][3] = exp.getStatus();
            downloadThreads[index] = new DownloadThread(exp);
            rowMap.put(exp.accession, index);
            exp.addChangeListener(listener);
            index++;
        }
//		for (threadIndex=0;threadIndex<)
        System.out.println(tableData.length);
        NoneditableTableModel model = new NoneditableTableModel(tableData,
                headers);
        downloadTable = new StripedTable(model);
        downloadTable.getColumnModel().getColumn(2)
                .setCellRenderer(new ProgressTableCellRenderer());
        downloadDialog = new JDialog(parent,"ArrayExpress",true);
        downloadDialog.add(new JScrollPane(downloadTable));
        downloadDialog.setSize(800, 600);
        downloadDialog.pack();
        downloadDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        startDownloading();
        downloadDialog.setVisible(true);
        if (canceled) {
            return false;
        }
        try {
            return compileProcessedData();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class DownloadThread extends Thread {

        AEXMLNodeInfo exp;

        private boolean done;

        public DownloadThread(AEXMLNodeInfo exp) {
            this.exp = exp;
            this.done = false;
        }

        public Progress getProgress() {
            return exp.getDownloadProgress();
        }

        @Override
        public void run() {
            System.out.println("Starting download: "+exp.accession);
            System.out.flush();
            try {
                exp.downloadProcessedData(tempDir);
                exp.unzipAndProcessData();
            } catch (Exception e) {
                System.err.println("Error in "+exp.accession);
                e.printStackTrace();
                exp.setStatus("Error");
            } finally {
                System.out.println("Done downloading: "+exp.accession);
                this.done = true;
                startNextThread();
            }
        }

        public boolean isFinished() {
            return done;
        }

    }

    public static boolean compileProcessedData() throws IOException {
        canceled = false;
        AnimatedSwingWorker worker = new AnimatedSwingWorker("Finishing...",
                true) {

            // File tempDir = new File(destDir
            // + System.getProperty("file.separator") + "MOG.temp"
            // + System.getProperty("file.separator"));

            @Override
            public Object construct() {
                try {

					/*
					 * DataCol used to be here
					 */

					/*
					 * FileWithInfo used to be here
					 */

                    // Unzip downloaded files and write a MOG-friendly version
                    // for each
//					for (AEXMLNodeInfo exp : exps) {
                    // exp.unzipAndProcessData();
//					}
                    this.setMessage("Writing...");
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(dest), "UTF-8"));
                    TreeMap<String, Long> lastRead = new TreeMap<String, Long>();
                    for (AEXMLNodeInfo exp : exps) {
                        File thisFile = exp.getProcessedDataFile();
                        if (thisFile!=null) {
                            lastRead.put(thisFile.getAbsolutePath(), (long) 0);
                        } else {
                            System.err.println("No processed data found for: "+exp.accession);
                        }
                    }
                    long startTime = System.currentTimeMillis();
                    RandomAccessFile in;
                    for (int line = 0; line <= probeList.size(); line++) {
                        int i = 0;
                        int colIndex=0;
                        for (AEXMLNodeInfo exp : exps) {
                            try {
                                in = new RandomAccessFile(
                                        exp.getProcessedDataFile(), "r");
                                in.seek(lastRead.get(exp.getProcessedDataFile()
                                        .getAbsolutePath()));
                                if (line == 0) {
                                    String[] headers = in.readLine()
                                            .split("\t");
                                    for (int j = 0; j < headers.length; j++) {
                                        if (exp.sampleNameMap!=null && exp.sampleNameMap.get(headers[j])!=null) {
                                            headers[j]=exp.sampleNameMap.get(headers[j]);
                                        }
                                    }
                                    if (i == 0) {
                                        out.write(headers[0]);
                                    }
                                    for (int j = 1; j < headers.length; j++) {
                                        out.write("\t" + headers[j]);
                                        System.out.println(exp.accession+": Setting column "+colIndex+" for "+headers[j]);
                                        exp.setColumnIndex(headers[j],colIndex);
                                        colIndex++;
                                    }
                                } else if (i == 0) {
                                    out.write(in.readLine());
                                } else {
                                    String thisLine = in.readLine();
                                    if (thisLine == null) {
                                        System.err.println("Error in "
                                                + exp.getProcessedDataFile()
                                                .getName() + " line "
                                                + line
                                                + ": Line is completely empty");
                                    }
                                    String[] splitLine = thisLine
                                            .split("\t", 2);
                                    if (splitLine.length < 2) {
//										System.err.println("Error in "
//												+ exp.getProcessedDataFile()
//														.getName() + " line "
//												+ line + ": Line has no data");
                                    } else {
                                        out.write("\t" + splitLine[1]);
                                    }
                                }
                                lastRead.put(exp.getProcessedDataFile()
                                        .getAbsolutePath(), in.getFilePointer());
                                in.close();
                                // if (line!=0 && line%5000==0) {
                                // System.gc();
                                // }
                                // System.gc();
                            } catch (NullPointerException e) {
                                System.err.println("Combining error");
                                e.printStackTrace();
                            }
                            i++;
                        }
                        out.newLine();
                        // out.flush();
                        long timePerLine = ((System.currentTimeMillis() - startTime) / (line + 1));
                        // System.out.println("Wrote line " + line + " ("
                        // + timePerLine + "ms per line)");
                    }
                    out.close();
                    this.setMessage("Generating Metadata...");
                    metadataFile = new File(dest.getParentFile(),
                            dest.getName() + " - metadata.xml");
                    out = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(
                                    metadataFile), "UTF-8"));
                    out.write("<MOGMetadata>\n");
                    for (AEXMLNodeInfo exp : exps) {
                        try {
                            String element=exp.getNewMetadataElement();
                            if (element!=null) {
                                out.write(element+"\n");
                            }
                        } catch (Exception e) {
                            System.err.println("Metadata error in "+exp.accession);
                            e.printStackTrace();
                        }
                    }
                    out.write("</MOGMetadata>");
                    out.flush();
                    out.close();

                    for (AEXMLNodeInfo exp : exps) {
                        File delMe = exp.getProcessedDataFile();
                        try {
                            if (!delMe.delete()) {
                                System.out.println("Unable to delete "
                                        + exp.getProcessedDataFile());
                                delMe.deleteOnExit();
                            }
                        } catch (Exception e) {
                            System.err.println("Delete error");
                            e.printStackTrace();
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                return true;
            }

            @Override
            public void finished() {
                super.finished();

                File[] files = tempDir.listFiles();
                for (File f : files) {
                    if (!f.delete()) {
                        f.deleteOnExit();
                    }
                }
                if (!tempDir.delete()) {
                    tempDir.deleteOnExit();
                }
            }
        };
        worker.start();
        return (Boolean) worker.get();

    }

    public static synchronized void startDownloading() {
        starting=true;
        System.out.println(downloadThreads.length+" exps to download");
        for (threadIndex=0;threadIndex<downloadThreads.length && threadIndex<maxDownloads;threadIndex++) {
            downloadThreads[threadIndex].start();
        }
        starting=false;
    }

    public static synchronized void startNextThread() {
        while (starting) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        if (threadIndex<downloadThreads.length) {
            downloadThreads[threadIndex++].start();
        }
    }

    public static File getLastMetadataFile() {
        return metadataFile;
    }

}