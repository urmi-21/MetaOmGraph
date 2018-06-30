package edu.iastate.metnet.arrayexpress;

//import edu.iastate.metnet.arrayexpress.AEDataDownloader.FilePlusRow;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.Utils.ChangeableInt;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


public class AEDataDownloader {
    static String[] suffix = {"Bytes", "KB", "MB", "GB", "TB"};

    private static TreeMap<String, ArrayList<String>> sampleNames;

    private static boolean canceled;

    public AEDataDownloader() {
    }

    public static void main(String[] args)
            throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        getItAll();
    }


    public static boolean compileProcessedData(final Collection<String> expIDs, final File dest, final File metadataFile, final AEImportPanel ae, String species) throws IOException {
        final ArrayList<String> headerList = new ArrayList();
        final ArrayList<String> probeList = new ArrayList();
        System.out.println("Reading header list");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(AEDataDownloader.class.getResourceAsStream("/resource/misc/headerlist.txt")));

            String thisLine;
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
            String probeFile = null;
            if ("Arabidopsis Thaliana (ATH1)".equals(species)) {
                probeFile = "/resource/arrayexpress/ath1/probelist.txt";
            } else if ("Homo Sapiens (HG-U133A)".equals(species)) {
                probeFile = "/resource/arrayexpress/hgu133a/probelist.txt";
            } else if ("Mus Musculus (430 2.0)".equals(species)) {
                probeFile = "/resource/arrayexpress/mouse4302/probelist.txt";
            } else if ("Soybean Genome Array".equals(species)) {
                probeFile = "/resource/arrayexpress/soybean/probelist.txt";
            } else if ("Rat Genome (230 2.0)".equals(species)) {
                probeFile = "/resource/arrayexpress/rat230/probelist.txt";
            } else if ("Yeast Genome (S98)".equals(species)) {
                probeFile = "/resource/arrayexpress/yeasts98/probelist.txt";
            } else if ("Barley Genome Array".equals(species)) {
                probeFile = "/resource/arrayexpress/barley/probelist.txt";
            } else if ("Rice Genome Array".equals(species)) {
                probeFile = "/resource/arrayexpress/rice/probelist.txt";
            } else if ("Homo Sapiens (HG-U133 Plus 2.0)".equals(species)) {
                probeFile = "/resource/arrayexpress/hgu133plus2/probelist.txt";
            } else if ("Yeast Genome 2.0 Array".equals(species)) {
                probeFile = "/resource/arrayexpress/yeast2/probelist.txt";
            } else if ("Zebrafish Genome Array".equals(species)) {
                probeFile = "/resource/arrayexpress/zebrafish/probelist.txt";
            }
            if (probeFile == null) {
                throw new IllegalArgumentException("Unknown species: " + species);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(AEDataDownloader.class.getResourceAsStream(probeFile)));
            String thisLine;
            while ((thisLine = in.readLine()) != null) {
                probeList.add(thisLine);
            }

            in.close();
        } catch (IOException e) {
            System.err.println("Error reading probe list");
            e.printStackTrace();
            return false;
        }
        sampleNames = new TreeMap();
        AnimatedSwingWorker worker = new AnimatedSwingWorker("Connecting...", true) {

            File tempDir = new File(dest.getParentFile().getAbsolutePath()
                    + System.getProperty("file.separator") + "MOG.temp"
                    + System.getProperty("file.separator"));

            @Override
            public Object construct() {
                PrintStream originalErr = System.err;
                // try {
                // System.setErr(new PrintStream(new
                // File("/Users/Rannic/Desktop/AE.err")));
                // } catch (FileNotFoundException e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                try {
                    ArrayList<File> zipFiles = new ArrayList<File>();
                    ArrayList<String> processedFiles = new ArrayList<String>();
                    TreeMap<String, String> expFileMap = new TreeMap<String, String>();
                    // Download all the experiments' zip files
                    // File sourceDir=new
                    // File("/Users/mhhur/Desktop/allarab/");
                    // File[] files=sourceDir.listFiles();
                    // for (File f : files) {
                    // if (Utils.getExtension(f).equals("zip")) {
                    // zipFiles.add(f);
                    // }
                    // }

                    int index = 1;
                    if (!tempDir.exists()) tempDir.mkdir();

                    for (String expID : expIDs) {
                        File outfile = File.createTempFile("mog", ".zip", tempDir);
                        System.out.println("Retrieving experiment " + index + "/" + expIDs.size());
                        if (getProcessedData(expID, outfile) != null) {
                            zipFiles.add(outfile);
                            expFileMap.put(outfile.getName(), expID);
                        } else if (canceled) return false;

                        index++;
                    }
                    // File tempDir = new File("/tmp/");
                    // File[] downloadedFiles = tempDir.listFiles();
                    // for (File f : downloadedFiles) {
                    // if (f.getName().endsWith(".zip")) {
                    // zipFiles.add(f);
                    // }
                    // }
                    // } else if (f.getName().endsWith(".tmp")) {
                    // processedFiles.add(f.getAbsolutePath());
                    // }
                    // }
                    class DataCol implements Comparable<DataCol> {

                        String header;
                        int col;

                        public DataCol(int col, String header) {
                            this.col = col;
                            if (header.endsWith(".CEL") && header.startsWith("GSM")) {
                                this.header = AEImportPanel.getCelName(header);
                            }
                            if (this.header == null) this.header = header;
                        }

                        public int compareTo(DataCol o) {
                            return header.compareTo(o.header);
                        }

                    }

                    class FileWithInfo {
                        File source;
                        TreeMap<String, Long> dataStarts;
                        TreeSet<Integer> dataCols;
                        ArrayList<String> headers;
                        int headerRow;
                        TreeSet<DataCol> newDataCols;

                        public FileWithInfo(File source, String expID) throws IOException {
                            // String[] splitName = source.getName().split("-");
                            // String expID = null;
                            // if (splitName.length == 3) {
                            // expID = splitName[0] + "-" + splitName[1] + "-"
                            // + splitName[2];
                            // } else {
                            // System.out.println("No experiment ID: "+name);
                            // expID=Utils.removeExtension(name);
                            // }
                            // System.out.println("EXP ID: " + expID);
                            this.source = source;
                            dataStarts = new TreeMap<String, Long>();
                            dataCols = new TreeSet<Integer>();
                            newDataCols = new TreeSet<DataCol>();
                            RandomAccessFile in = new RandomAccessFile(source,"r");
                            int headersToUse = -1;
                            int dataRow = 0;
                            // ArrayList<String> headerRows = new
                            // ArrayList<String>();
                            String thisLine;
                            long headerRowPointer = 0;
                            long thisRowPointer = 0;
                            while ((thisLine = in.readString('\t', false)) != null && !thisLine.startsWith("Affymetrix:CompositeSequence")) {
                                if (thisLine.startsWith("Hybridization REF")) {
                                    headersToUse = dataRow;
                                    headerRowPointer = thisRowPointer;
                                }
                                dataRow++;
                                // headerRows.add(thisLine);
                                in.nextLine();
                                thisRowPointer = in.getFilePointer();
                            }
                            if (headersToUse >= 0) headerRow = headersToUse;
                            else headerRow = 0;

                            System.out.println("Using header row " + headerRow);
                            in.seek(0);
                            headers = new ArrayList<String>();
                            for (int i = 0; i < dataRow; i++) {
                                String[] splitLine = in.readAndSplitLine('\t', false);
                                for (int j = 0; j < splitLine.length; j++) {
                                    for (String ending : headerList) {
                                        if (splitLine[j].endsWith(ending)) {
                                            if (dataCols.contains(j)) {
                                                System.err.println("Column " + j + " is already in there!");
                                                continue;
                                            }
                                            dataCols.add(j);
                                            // if (headersToUse >= 0) {
                                            // headers.add(headerRows.get(
                                            // headersToUse).split(
                                            // "\t")[j]);
                                            // } else {
                                            // headers.add(splitLine[j]);
                                            // }
                                            long currentPointer = in.getFilePointer();
                                            in.seek(headerRowPointer);
                                            String[] splitHeaders = in.readAndSplitLine('\t', false);
                                            if (j >= splitHeaders.length) {
                                                headers.add(expID + " - " + j);
                                                newDataCols.add(new DataCol(j, expID + " - " + j));
                                            } else {
                                                headers.add(splitHeaders[j]);
                                                newDataCols.add(new DataCol(j, splitHeaders[j]));
                                            }
                                            in.seek(currentPointer);
                                            System.out.println("Found data column: " + j);
                                        }
                                    }
                                }
                            }
                            if (sampleNames.get(expID) != null) {
                                ArrayList<String> allHeaders = sampleNames.get(expID);
                                allHeaders.addAll(headers);
                                sampleNames.put(expID, allHeaders);
                            } else {
                                sampleNames.put(expID, headers);
                            }
                            in.seek(0);
                            long pointer = 0;
                            while ((thisLine = in.readString('\t', false)) != null) {
                                String probe = thisLine.substring(thisLine.lastIndexOf(":") + 1);
                                // probe=Utils.clean(probe);
                                if (probeList.contains(probe)) {
                                    // dataStarts.put(probe,
                                    // in.getFilePointer());
                                    dataStarts.put(probe, pointer);
                                    // System.out.println("Found probe: "+probe);
                                }
                                in.nextLine();
                                pointer = in.getFilePointer();
                            }
                            in.close();
                        }
                    }
                    // Unzip downloaded files and write a MOG-friendly version
                    // for each
                    this.setMessage("Processing...");
                    for (File source : zipFiles) {
                        ZipFile zipIn = null;
                        try {
                            zipIn = new ZipFile(source);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                        ZipEntry thisEntry;
                        Enumeration<? extends ZipEntry> entryEnum = zipIn.entries();
                        while (entryEnum.hasMoreElements()) {
                            thisEntry = entryEnum.nextElement();
                            if (thisEntry.getName().endsWith(".sdrf.txt") || thisEntry.getName().endsWith(".idf.txt") || thisEntry.getName().endsWith("-info.txt")) {
                                continue;
                            }
                            String expID = expFileMap.get(source.getName());
                            System.out.println("Unzipping and analyzing: " + expID + " -> " + thisEntry.getName());
                            this.setMessage("<html><p align=\"center\">Analyzing:<br>" + thisEntry.getName() + "</p></html>");
                            FileWithInfo thisFile = new FileWithInfo(Utils.unzip(source, thisEntry), expID);
                            if (thisFile.dataCols.size() != thisFile.headers.size()) {
                                System.err.println(thisFile.source.getName()
                                        + " has " + thisFile.dataCols.size()
                                        + " data cols and "
                                        + thisFile.headers.size()
                                        + " headers?!");
                            }
                            if (thisFile.dataCols.size() <= 0) {
                                System.err.println("No data: " + thisEntry.getName());
                                continue;
                            }
                            if (thisFile.dataStarts.isEmpty()) {
                                continue;
                            }
                            File outfile = File.createTempFile("out", null, tempDir);
                            // out.seek(lastWritten.get(0));
                            boolean errored = false;
                            RandomAccessFile in = new RandomAccessFile(thisFile.source, "r");
                            BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
                            out.write("Probe ID");
                            // for (String header : thisFile.headers) {
                            // out.write("\t" + header);
                            // }
                            for (DataCol col : thisFile.newDataCols) {
                                out.write("\t" + col.header);
                            }
                            out.newLine();
                            for (int i = 0; i < probeList.size(); i++) {
                                String probe = probeList.get(i);
                                out.write(probe);
                                Long start = thisFile.dataStarts.get(probe);
                                if (start == null) {
                                    for (int col : thisFile.dataCols) {
                                        out.write("\t");
                                    }
                                } else {
                                    in.seek(thisFile.dataStarts.get(probe));
                                    String[] data = in.readAndSplitLine('\t',false);
                                    // for (int col : thisFile.dataCols) {
                                    for (DataCol col : thisFile.newDataCols) {
                                        try {
                                            out.write("\t" + data[col.col]);
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            System.err.println("Error in column " + col + " on the following line:");
                                            for (String thisData : data) {
                                                System.err.print(thisData + "\t");
                                            }
                                            System.err.println();
                                            out.write("\t");
                                        }
                                    }
                                }
                                out.newLine();
                            }
                            in.close();
                            out.close();
                            processedFiles.add(outfile.getAbsolutePath());
                            if (!thisFile.source.delete()) {
                                System.out.println("Unable to delete " + thisFile.source.getAbsolutePath());
                                thisFile.source.deleteOnExit();
                            }
                        }
                        // zipFiles.remove(source);
                        // if (!source.delete()) {
                        // System.out.println("Unable to delete "
                        // + source.getAbsolutePath());
                        // source.deleteOnExit();
                        // }
                    }
                    this.setMessage("Generating Metadata...");
                    ae.outputMOGMetadata(metadataFile);
                    this.setMessage("Writing...");
                    BufferedWriter out = new BufferedWriter(new FileWriter(dest));
                    TreeMap<String, Long> lastRead = new TreeMap<String, Long>();
                    for (String thisFile : processedFiles) {
                        lastRead.put(thisFile, (long) 0);
                    }
                    long startTime = System.currentTimeMillis();
                    RandomAccessFile in;
                    for (int line = 0; line <= probeList.size(); line++) {
                        for (int i = 0; i < processedFiles.size(); i++) {
                            try {
                                in = new RandomAccessFile(processedFiles.get(i), "r");
                                in.seek(lastRead.get(processedFiles.get(i)));
                                if (line == 0) {
                                    String[] headers = in.readLine().split("\t");
                                    for (int j = 0; j < headers.length; j++) {
                                        if (headers[j].endsWith(".CEL") && headers[j].startsWith("GSM")) {
                                            String newName = AEImportPanel.getCelName(headers[j]);
                                            if (newName != null) {
                                                System.out.println("Replacing " + headers[j] + " with " + newName);
                                                headers[j] = newName;
                                            } else {
                                                System.out.println("No better name for " + headers[j]);
                                            }
                                        }
                                    }
                                    if (i == 0) {
                                        out.write(headers[0]);
                                    }
                                    for (int j = 1; j < headers.length; j++) {
                                        out.write("\t" + headers[j]);
                                    }
                                } else if (i == 0) {
                                    out.write(in.readLine());
                                } else {
                                    String thisLine = in.readLine();
                                    if (thisLine == null) {
                                        System.err.println("Error in "
                                                + processedFiles.get(i)
                                                + " line " + line
                                                + ": Line is completely empty");
                                    }
                                    String[] splitLine = thisLine.split("\t", 2);
                                    if (splitLine.length < 2) {
                                        System.err.println("Error in "
                                                + processedFiles.get(i)
                                                + " line " + line
                                                + ": Line has no data");
                                    } else {
                                        out.write("\t" + splitLine[1]);
                                    }
                                }
                                lastRead.put(processedFiles.get(i), in.getFilePointer());
                                in.close();
                                // if (line!=0 && line%5000==0) {
                                // System.gc();
                                // }
                                // System.gc();
                            } catch (NullPointerException e) {
                                System.err.println("Combining error");
                                e.printStackTrace();
                            }
                        }
                        out.newLine();
                        // out.flush();
                        long timePerLine = ((System.currentTimeMillis() - startTime) / (line + 1));
                        // System.out.println("Wrote line " + line + " ("
                        // + timePerLine + "ms per line)");
                    }
                    out.close();
                    for (String thisFile : processedFiles) {
                        File delMe = new File(thisFile);
                        try {
                            if (!delMe.delete()) {
                                System.out.println("Unable to delete " + thisFile);
                                delMe.deleteOnExit();
                            }
                        } catch (Exception e) {
                            System.err.println("Delete error");
                            e.printStackTrace();
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    System.setErr(originalErr);
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
        return ((Boolean) worker.get()).booleanValue();
    }
  /*
  public static void oldcompileProcessedData(Collection<String> expIDs, final File dest, String species)
    throws IOException
  {
    final ArrayList<String> headerList = new ArrayList();
    final TreeSet<String> probeList = new TreeSet();
    System.out.println("Reading header list");
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(
        AEDataDownloader.class
        .getResourceAsStream("/resource/misc/headerlist.txt")));
      String thisLine; while ((thisLine = in.readLine()) != null) { 
        headerList.add(thisLine);
      }
      
      in.close();
    } catch (IOException e) {
      System.err.println("Error reading header list");
      e.printStackTrace();
      return;
    }
    System.out.println("Reading probelist");
    try
    {
      String probeFile = null;
      if ("Arabidopsis Thaliana (ATH1)".equals(species)) {
        probeFile = "/resource/arrayexpress/ath1/probelist.txt";
      } else if ("Homo Sapiens (HG-U133A)".equals(species)) {
        probeFile = "/resource/arrayexpress/hgu133a/probelist.txt";
      } else if ("Mus Musculus (430 2.0)".equals(species)) {
        probeFile = "/resource/arrayexpress/mouse4302/probelist.txt";
      }
      if (probeFile == null) {
        throw new IllegalArgumentException("Unknown species: " + 
          species);
      }
      BufferedReader in = new BufferedReader(new InputStreamReader(
        AEDataDownloader.class.getResourceAsStream(probeFile)));
      String thisLine; while ((thisLine = in.readLine()) != null) { 
        probeList.add(thisLine);
      }
      
      in.close();
    } catch (IOException e) {
      System.err.println("Error reading probe list");
      e.printStackTrace();
      return;
    }
    
    sampleNames = new TreeMap();
    new AnimatedSwingWorker("Connecting...", true)
    {
      public Object construct() {
        try {
          ArrayList<File> zipFiles = new ArrayList();
          File sourceDir = new File("/Users/mhhur/Desktop/allarab/");
          File[] files = sourceDir.listFiles();
          for (File f : files) {
            zipFiles.add(f);
          }
         
          ArrayList<1FileWithInfo> unzippedFiles = new ArrayList();
          for (File source : zipFiles) {
            zipIn = null;
            try {
              zipIn = new ZipFile(source);
            } catch (Exception e) {
              e.printStackTrace();
              continue;
            }
            
            Enumeration<? extends ZipEntry> entryEnum = ((ZipFile)zipIn)
              .entries();
            while (entryEnum.hasMoreElements()) {
              ZipEntry thisEntry = (ZipEntry)entryEnum.nextElement();
              if ((!thisEntry.getName().endsWith(".sdrf.txt")) && 
                (!thisEntry.getName().endsWith(".idf.txt")))
              {
                if (!thisEntry.getName().endsWith("-info.txt"))
                {

                  System.out.println("Unzipping and analyzing: " + 
                    source.getName() + " -> " + 
                    thisEntry.getName());
                  
                  setMessage("<html><p align=\"center\">Analyzing:<br>" + 
                    thisEntry.getName() + 
                    "</p></html>");
                  try
                  {
                    unzippedFiles.add(new Object() {
                      File source;
                      TreeMap<String, Long> dataStarts;
                      TreeSet<Integer> dataCols;
                      int headerRow;
                    });
                  } catch (ZipException ze) {
                    ze.printStackTrace();
                  }
                } } }
            zipFiles.remove(source);
            source.delete();
          }
          setMessage("Writing...");
          BufferedWriter out = new BufferedWriter(
            new FileWriter(dest));
          out.write("Probe ID");
          int col; for (Object zipIn = unzippedFiles.iterator(); ((Iterator)zipIn).hasNext();) { Object thisFile = (1FileWithInfo)((Iterator)zipIn).next();
            System.out.println("Writing headers for " + 
              source.getName());
            RandomAccessFile in = new RandomAccessFile(
              source, "r");
            col = 0;
            for (Iterator localIterator2 = dataCols.iterator(); localIterator2.hasNext();) { int dataCol = ((Integer)localIterator2.next()).intValue();
              while (col < dataCol) {
                in.readString('\t', false);
                col++;
              }
              out.write("\t" + in.readString('\t', false));
              col++;
            }
            in.close();
          }
          out.newLine();
          System.out.println("Writing data");
          Object probe; for (zipIn = probeList.iterator(); ((Iterator)zipIn).hasNext();) { probe = (String)((Iterator)zipIn).next();
            out.write((String)probe);
            for (Object thisFile : unzippedFiles)
              if (dataStarts.get(probe) == null) {
                for (int i = 0; i < dataCols.size(); i++) {
                  out.write("\t");
                }
              }
              else {
                RandomAccessFile in = new RandomAccessFile(
                  source, "r");
                in.seek(((Long)dataStarts.get(probe)).longValue());
                int col = 1;
                for (Iterator localIterator3 = dataCols.iterator(); localIterator3.hasNext();) { int dataCol = ((Integer)localIterator3.next()).intValue();
                  while (col < dataCol) {
                    in.readString('\t', false);
                    col++;
                  }
                  out.write("\t" + in.readString('\t', false));
                  col++;
                }
                in.close();
              }
            out.newLine();
          }
          out.close();
          for (zipIn = unzippedFiles.iterator(); ((Iterator)zipIn).hasNext(); probe = (1FileWithInfo)((Iterator)zipIn).next()) {}
        }
        catch (IOException ioe)
        {
          ioe.printStackTrace();
        }
        return null;
      }
      
      public void finished()
      {
        super.finished();
      }
    }.start();
  }
*/

    public static File getProcessedData(String expID, File dest) throws IOException {
        System.out.println("getProcessedData called");
        canceled = false;
        int firstDash = expID.indexOf('-');
        int lastDash = expID.lastIndexOf('-');
        String expType;
        if (firstDash != lastDash) {
            expType = expID.substring(expID.indexOf('-') + 1, expID.lastIndexOf('-'));
        } else {
            expType = "";
        }
        System.out.println("expType determined");

        if (expType.length() != 4) {
            throw new IllegalArgumentException(expID + " is not a valid ArrayExpress experiment ID (" + expType + ")");
        }

        try {
            System.out.println("Creating URL");
            URL source = new URL("ftp://ftp.ebi.ac.uk/pub/databases/microarray/data/experiment/" + expType + "/" + expID + "/" + expID + ".processed.zip");
            System.out.println("Opening connection");
            URLConnection conn = source.openConnection();
            int attempts = 0;
            boolean success = false;
            while ((!success) && (attempts < 5)) {
                try {
                    conn.setConnectTimeout(60000);
                    conn.setReadTimeout(60000);
                    conn.connect();
                    success = true;
                } catch (Exception e) {
                    attempts++;
                    System.err.println("Connection attempt " + attempts + " failed.");
                    e.printStackTrace();
                }
            }

            System.out.println("Connection opened");

            Utils.ChangeableInt size = new Utils.ChangeableInt(conn.getContentLength());
            if (size.getValue() <= 0) {

                source = new URL("ftp://ftp.ebi.ac.uk/pub/databases/microarray/data/experiment/" + expType + "/" + expID + "/" + expID + ".processed.1.zip");
                System.out.println("Opening connection 2");
                conn = source.openConnection();
                System.out.println("Connection 2 opened");
                attempts = 0;
                success = false;
                while ((!success) && (attempts < 5)) {
                    try {
                        conn.setConnectTimeout(60000);
                        conn.setReadTimeout(60000);
                        conn.connect();
                        success = true;
                    } catch (Exception e) {
                        attempts++;
                        System.err.println("Connection attempt " + attempts + " failed.");
                        e.printStackTrace();
                    }
                    size.setValue(conn.getContentLength());
                }
                System.out.println("Content length begotten");
                if (size.getValue() <= 0) {
                    System.out.println(expID + ": 0-length content for " + source.toExternalForm());
                    return null;
                }
            }
            System.out.println(expID + ": " + size);
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            FileOutputStream out = new FileOutputStream(dest);

            long count = 0L;
            System.out.println("Beginning download");
            int i = 0;
            byte[] bytesIn = new byte[1024];
            final BlockingProgressDialog pm = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Downloading", "<html>"
                    + expID + "<br>0 KB of " + size.getValue() / 1024
                    + " KB complete<br>" + "0 KB/sec</html>", 0L, size.getValue() / bytesIn.length, true);
            pm.setSize(300, pm.getPreferredSize().height);
            pm.setLocationRelativeTo(MetaOmGraph.getMainWindow());

            int update = 0;
            long startTime = getTime();
            new Thread() {
                public void run() {
                    super.run();
                    pm.setVisible(true);
                }
            }.start();

            while (((i = in.read(bytesIn)) >= 0) && (!pm.isCanceled())) {
                out.write(bytesIn, 0, i);
                count += i;
                update = (update + 1) % 100;
                if (update == 0) {
                    double time = getTime() - startTime;
                    time /= 1000.0D;
                    int speed = (int) (count / 1024L / time);
                    pm.setProgress(count / bytesIn.length);
                    pm.setMessage("<html>" + expID + "<br>" + count / 1024L +
                            "KB of " + size.getValue() / 1024 +
                            "KB complete<br>" + speed + " KB/sec</html>");
                }
            }
            pm.dispose();
            canceled = pm.isCanceled();
            long endTime = Calendar.getInstance().getTimeInMillis();
            long timeTaken = endTime - startTime;
            double speed = size.getValue() / 1024.0D / (timeTaken / 1000.0D);
            System.out.println("Downloaded " + count + " bytes in " + timeTaken + "ms (" + speed + " KB/sec)");
            in.close();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return canceled ? null : dest;
    }

    public static void downloadTest() throws IOException {
        URL source = new URL("http://metnetdb.org/Nick/downtest2.txt");
        URLConnection conn = source.openConnection();
        int size = conn.getContentLength();
        System.out.println("Size: " + size);
        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
        byte[] bytesIn = new byte[1024];
        int i = 0;
        System.out.println("Buffer size: 1024");
        long startTime = getTime();
        while ((i = in.read(bytesIn)) >= 0) {
        }

        long endTime = getTime();
        in.close();
        System.out.println("Took " + (endTime - startTime) + "ms");

        conn = source.openConnection();
        in = new BufferedInputStream(conn.getInputStream());
        bytesIn = new byte[size / 1024];
        i = 0;
        System.out.println("Buffer size: size/1024");
        startTime = getTime();
        while ((i = in.read(bytesIn)) >= 0) {
        }

        endTime = getTime();
        in.close();
        System.out.println("Took " + (endTime - startTime) + "ms");

        conn = source.openConnection();
        in = new BufferedInputStream(conn.getInputStream());
        bytesIn = new byte[1];
        i = 0;
        System.out.println("Buffer size: 1");
        startTime = getTime();
        while ((i = in.read(bytesIn)) >= 0) {
        }

        endTime = getTime();
        in.close();
        System.out.println("Took " + (endTime - startTime) + "ms");

        conn = source.openConnection();
        in = new BufferedInputStream(conn.getInputStream());
        bytesIn = new byte[size];
        i = 0;
        System.out.println("Buffer size: size");
        startTime = getTime();
        while ((i = in.read(bytesIn)) >= 0) {
        }

        endTime = getTime();
        in.close();
        System.out.println("Took " + (endTime - startTime) + "ms");
    }

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static void getItAll() throws Exception {
        String url = "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_expts.jsp?page-size=5000&sort_by=releasedate&sort_order=descending&page-number=1&organism=&array=13851999&keyword=";
        String destDir = "/Users/mhhur/Desktop/allarab";
        URL site = new URL(url);
        SAXBuilder builder = new SAXBuilder();
        Document myDoc = builder.build(site);
        System.out.println("Document built");
        List exps = myDoc.getRootElement().getChildren("experiment");
        int size = exps.size();
        for (int x = 0; x < size; x++) {
            Element thisChild = (Element) exps.get(x);
            String expID = thisChild.getAttributeValue("accnum");
            File dest = new File(destDir, expID + ".zip");
            if ((!dest.exists()) && (!"0".equals(thisChild.getAttributeValue("fgem-count")))) {
                getProcessedData(expID, dest);
            }
            System.out.println("Finished " + x + " of " + size);
        }
    }

    public static void getItAll(int id) throws Exception {
        String url = "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_expts.jsp?page-size=500&sort_by=releasedate&sort_order=descending&page-number=1&organism=&array=" +
                id + "&keyword=";
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(1);
        int result = chooser.showSaveDialog(null);
        String destDir = "c:\\documents and settings\\Nick\\desktop\\aedata\\Arabidopsis AG1 metadata";
        URL site = new URL(url);
        SAXBuilder builder = new SAXBuilder();
        Document myDoc = builder.build(site);
        System.out.println("Document built");
        List exps = myDoc.getRootElement().getChildren("experiment");
        int size = exps.size();
        for (int x = 0; x < size; x++) {
            Element thisChild = (Element) exps.get(x);
            String expID = thisChild.getAttributeValue("accnum");
            File dest = new File(destDir, expID + ".zip");
            getProcessedData(expID, dest);
            System.out.println("Finished " + x + " of " + size);
        }
    }

    public static void getItAll(int id, File destDir) throws Exception {
        String url = "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_expts.jsp?page-size=500&sort_by=releasedate&sort_order=descending&page-number=1&organism=&array=" +
                id + "&keyword=";
        URL site = new URL(url);
        SAXBuilder builder = new SAXBuilder();
        Document myDoc = builder.build(site);
        System.out.println("Document built");
        List exps = myDoc.getRootElement().getChildren("experiment");
        int size = exps.size();
        for (int x = 0; x < size; x++) {
            Element thisChild = (Element) exps.get(x);
            String expID = thisChild.getAttributeValue("accnum");
            File dest = new File(destDir, expID + ".zip");
            File thisZip = getProcessedData(expID, dest);
            Utils.unzip(thisZip, destDir);
            thisZip.delete();
            System.out.println("Finished " + x + " of " + size);
        }
    }

    public static void analyzeHeader() throws Exception {
        File sourceDir = new File("c:\\documents and settings\\Nick\\desktop\\aedata\\Human HG-U133A\\extracted");
        analyzeHeaders(sourceDir);
    }

    public static void analyzeHeaders(File sourceDir) throws Exception {
        PrintStream newerr = new PrintStream(new File(sourceDir, "analyze.err"));
        System.setErr(newerr);
        File[] files = sourceDir.listFiles();
        TreeMap<String, TreeSet<String>> endings = new TreeMap();
        int hitCount = 0;
        TreeSet<String> errors = new TreeSet();
        TreeSet<FilePlusRow> misses = new TreeSet();
        TreeMap<String, TreeSet<String>> headerStarts = new TreeMap();
        for (File thisFile : files)
            if ((!thisFile.getName().startsWith("header")) && (thisFile.getName().endsWith(".txt"))) {

                BufferedReader in = new BufferedReader(new FileReader(thisFile));
                try {
                    String[][] lines = new String[10][];
                    for (int i = 0; i < lines.length; i++) {
                        lines[i] = in.readLine().split("\t");
                        if (lines[i].length <= 0) {
                            lines[i] = new String[]{"nothing here"};
                        }
                    }
                    int dataRow = 0;
                    while ((lines[dataRow].length > 0) && (!lines[dataRow][0].startsWith("Affymetrix:CompositeSequence")) && (dataRow < lines.length)) {
                        TreeSet<String> starts = headerStarts.get(lines[dataRow][0]);
                        if (starts == null) {
                            starts = new TreeSet();
                        }
                        starts.add(thisFile.getName());
                        headerStarts.put(lines[dataRow][0], starts);
                        dataRow++;
                    }
                    boolean hit = false;
                    int col = -1;
                    int row = -1;
                    for (col = 0; col < lines[dataRow].length; col++) {
                        try {
                            Integer.parseInt(lines[dataRow][col]);
                        } catch (NumberFormatException nfe) {
                            try {
                                double val = Double.parseDouble(lines[dataRow][col]);
                                if (val >= 1.0D) {
                                    row = 0;
                                    do {
                                        if ((col < lines[row].length) && (lines[row][col].contains("/"))) {

                                            String thisEnd = lines[row][col].substring(lines[row][col].indexOf("/"));
                                            TreeSet<String> thisSet = endings.get(thisEnd);
                                            if (thisSet == null) {
                                                thisSet = new TreeSet();
                                            }
                                            thisSet.add(thisFile.getName());
                                            endings.put(thisEnd, thisSet);
                                            if (!hit) {
                                                hitCount++;
                                                hit = true;
                                            }
                                        }
                                        row++;
                                        if (row >= dataRow) break;
                                    } while (!hit);
                                }
                            }
                            catch (NumberFormatException localNumberFormatException1) {
                            }
                        }
                    }

                    System.out.println("Analyzed " + thisFile.getName());
                    if (!hit) {
                        FilePlusRow addMe = new FilePlusRow();
                        addMe.file = thisFile.getName();
                        addMe.row = row;
                        addMe.col = col;
                        misses.add(addMe);
                    }
                } catch (Exception e) {
                    errors.add(thisFile.getName());
                    System.err.println("Error in " + thisFile.getName());
                    e.printStackTrace();
                }
                in.close();
            }
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(
                sourceDir, "headerstarts.txt")));
        Set<String> keys = headerStarts.keySet();
        for (String thisKey : keys) {
            // out.write(thisKey);
            // out.newLine();
            out.write(thisKey + " - " + headerStarts.get(thisKey).size()
                    + "files");
            out.newLine();
            for (String f : headerStarts.get(thisKey)) {
                out.write(f + ", ");
            }
            out.newLine();
            out.newLine();
        }

        out.close();
        out = new BufferedWriter(new FileWriter(new File(sourceDir,"headeranalysis no bs.txt")));
        keys = endings.keySet();
        for (String thisKey : keys) {
            out.write(thisKey);
            out.newLine();
            // out.write(thisKey + " - " + endings.get(thisKey).size() + "
            // files");
            // out.newLine();
            // for (String f : endings.get(thisKey)) {
            // out.write(f + ", ");
            // }
            // out.newLine();
            // out.newLine();
        }
        out.write("Found headers in " + hitCount + " files");
        out.newLine();
        out.newLine();
        out.write("No headers found in the following " + misses.size() + " files: ");
        out.newLine();
        for (FilePlusRow thisMiss : misses) {
            out.write(thisMiss.toString());
            out.newLine();
        }
        out.newLine();
        out.write("Errors in the following " + errors.size() + " files:");
        out.newLine();
        for (String thisMiss : errors) {
            out.write(thisMiss);
            out.newLine();
        }
        out.close();
    }

    private static class FilePlusRow implements Comparable {
        int row, col;

        String file;

        @Override
        public String toString() {
            return file + ": row " + row + " col " + col;
        }

        public int compareTo(Object o) {
            return file.compareTo(o + "");
        }
    }

    public static Map<String, ArrayList<String>> getSampleNames() {
        return sampleNames;
    }

    public static void analyzeMetadata() throws Exception {
        File sourceDir = new File("c:\\documents and settings\\Nick\\desktop\\aedata\\Arabidopsis ATH1 processed metadata");
        File[] texts = sourceDir.listFiles();
        ArrayList<String> hybNames = new ArrayList();
        ArrayList<String> nameless = new ArrayList();
        TreeMap<String, ArrayList<String>> headers = new TreeMap();
        BufferedReader in;
        String thisLine;
        for (File thisFile : texts) {
            in = new BufferedReader(new FileReader(thisFile));
            String[] splitLine = in.readLine().split("\t");
            int hit = -1;
            for (int i = 0; i < splitLine.length; i++) {
                if (splitLine[i].equals("Hybridization Name")) {
                    hit = i;
                } else if (headers.get(splitLine[i]) == null) {
                    ArrayList<String> list = new ArrayList();

                    list.add(thisFile.getName());

                    headers.put(splitLine[i], list);
                } else {
                    headers.get(splitLine[i]).add(thisFile.getName());
                }
            }

            if (hit < 0) {
                nameless.add(thisFile.getName());
            } else {
                while ((thisLine = in.readLine()) != null) {
                    splitLine = thisLine.split("\t");
                    if ((splitLine.length > hit) && (!splitLine[hit].equals(""))) {
                        hybNames.add(thisLine.split("\t")[hit] + " (" + thisFile.getName() + ")");
                    }
                }
            }

            in.close();
            System.out.println(thisFile.getName() + " analyzed");
        }
        File outfile = new File(sourceDir, "report.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
        out.write("The following hybridization names were found:\n");
        for (String thisName : hybNames) {
            out.write(thisName + "\n");
        }

        out.write("\nThe following files had no \"Hybridization Name\" header:\n");
        for (String thisName : nameless) {
            out.write(thisName + "\n");
        }
        out.write("\nThe following headers were found:\n");
        Set<String> keys = headers.keySet();
        for (String thisHeader : keys) {
            out.write(headers.get(thisHeader).size() + " files: " + thisHeader
                    + "(");
            ArrayList<String> list = headers.get(thisHeader);
            for (String thisName : list) {
                out.write(thisName + " ");
            }
            out.write(")\n");
        }
        out.close();
    }
}