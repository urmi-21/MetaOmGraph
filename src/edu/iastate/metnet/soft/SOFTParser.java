package edu.iastate.metnet.soft;

import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalNameException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class SOFTParser {
    private ArrayList<Sample> sampleList;
    private ArrayList<Series> seriesList;
    private File lastDir;
    private RandomAccessFile dataIn;
    private Element metadataRoot;
    private ArrayList<SampleInfo> automaticSamplesList;
    private ProgressMonitor pm;

    public SOFTParser() {
    }

    public static void main(String[] args)
            throws IOException {
        System.setProperty("swing.aatext", "true");
        Utils.setLastDir(new File("z:\\soft stuff\\"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }

        SOFTFile source = new SOFTFile(Utils.chooseFileToOpen());
        int extIndex = source.getName().lastIndexOf(".");
        String newName = source.getName().substring(0, extIndex) + ".data.txt";
        newName = source.getParent() + File.separator + newName;
        try {
            new SOFTParser().createProjectFromFile(source, new File(newName),
                    null);
        } catch (OutOfMemoryError oops) {
            System.err.println("Well hey I caught that memory error!");
        }
    }


    public MetaOmProject createProjectFromFile(SOFTFile paramSOFTFile, File paramFile, String[] paramArrayOfString)
            throws IOException {
        throw new Error("Unresolved compilation problem: \n\tThe method loadMetadata(File) in the type MetaOmProject is not applicable for the arguments (Element)\n");
    }


    public MetaOmProject createProjectFromFiles(SOFTFile[] paramArrayOfSOFTFile, File paramFile, String[] paramArrayOfString)
            throws IOException {
        throw new Error("Unresolved compilation problem: \n\tThe method loadMetadata(File) in the type MetaOmProject is not applicable for the arguments (Element)\n");
    }

    public void setLastDir(File newLastDir) {
        lastDir = newLastDir;
    }

    public File getLastDir() {
        return lastDir;
    }

    public JFrame getResultFrame() {
        final JFrame resultFrame = new JFrame("Parsing results");
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, 0));
        String[] entityIDs = new String[seriesList.size()];
        for (int x = 0; x < entityIDs.length; x++) {
            entityIDs[x] = seriesList.get(x).getIdentifier();
        }
        final JList entityList = new JList(entityIDs);
        entityList.setSelectionMode(0);
        JTextArea entityInfo = new JTextArea("Nothing selected");
        JTable entityTable = new JTable();
        final JScrollPane entityInfoPane = new JScrollPane(entityInfo);
        final JScrollPane entityTablePane = new JScrollPane(entityTable);
        entityList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int index = entityList.getSelectedIndex();
                if (index < 0)
                    return;
                Entity thisEntity = seriesList.get(index);
                if (thisEntity == null)
                    return;
                entityInfoPane.setViewportView(new JTextArea(thisEntity
                        .toString()));
                if (thisEntity.hasTableData()) {
                    JTable newTable = new JTable(thisEntity.getTableData(),
                            thisEntity.getTableHeaders());
                    newTable.setAutoResizeMode(0);
                    entityTablePane.setViewportView(newTable);
                } else {
                    entityTablePane.setViewportView(new JTable());
                }

            }
        });
        resultPanel.add(new JScrollPane(entityList));
        JSplitPane splitty = new JSplitPane(1, true,
                entityInfoPane, entityTablePane);
        resultPanel.add(splitty);
        resultFrame.getContentPane().setLayout(
                new BoxLayout(resultFrame.getContentPane(), 1));
        resultFrame.getContentPane().add(resultPanel);
        JPanel buttonPanel = new JPanel();
        JButton metadataButton = new JButton("Output Metadata");
        JButton dataFileButton = new JButton("Output Data File");
        JButton finishedButton = new JButton("Finished");
        finishedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resultFrame.dispose();
            }

        });
        metadataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File destination = Utils.chooseFileToSave();
                if (destination == null)
                    return;
                try {
                    outputMetadata(destination);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

            }
        });
        dataFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File destination = Utils.chooseFileToSave();
                if (destination == null)
                    return;
                try {
                    outputData(destination);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

            }
        });
        buttonPanel.add(metadataButton);
        buttonPanel.add(dataFileButton);
        buttonPanel.add(finishedButton);
        resultFrame.add(buttonPanel);
        resultFrame.pack();
        resultFrame.setDefaultCloseOperation(3);
        return resultFrame;
    }

    public void outputMetadata(File outFile) throws FileNotFoundException {
        Element root = new Element("Metadata");
        for (int x = 0; x < sampleList.size(); x++) {
            Sample thisSample = sampleList.get(x);
            Element sampleEntity = new Element("Sample").setAttribute("name",
                    thisSample.getIdentifier());
            sampleEntity.addContent(new Element("Sample_name")
                    .setText(thisSample.getIdentifier()));
            Vector<Attribute> attributes = thisSample.getAttributes();
            for (int i = 0; i < attributes.size(); i++) {
                Attribute thisAttrib = attributes.get(i);
                String name = convertToValidXMLName(thisAttrib.getKey());
                try {
                    sampleEntity.addContent(new Element(name)
                            .setText(thisAttrib.getValue()));
                } catch (IllegalNameException ine) {
                    System.err.println("Key: " + thisAttrib.getKey() +
                            ", Value: " + thisAttrib.getValue() + ", Name:" +
                            name);
                    ine.printStackTrace();
                }
            }
            root.addContent(sampleEntity);
        }
        FileOutputStream out = new FileOutputStream(outFile);
        XMLOutputter output = new XMLOutputter();
        Document myDoc = new Document(root);
        output.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));
        try {
            output.output(myDoc, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done with metadata!");
    }

    public void outputData(File outfile) throws FileNotFoundException {
        char delim = '\t';
        try {
            FileWriter out = new FileWriter(outfile);
            out.write("ID_REF" + delim);
            int maxRowCount = -1;

            Sample minRowSample = sampleList.get(0);
            int minRowCount = minRowSample.getTableData().length;
            for (int x = 0; x < sampleList.size(); x++) {
                Sample thisSample = sampleList.get(x);
                out.write(thisSample.getIdentifier() + delim);
                if (thisSample.getTableData().length > maxRowCount) {
                    maxRowCount = thisSample.getTableData().length;
                }

                if (thisSample.getTableData().length < minRowCount) {
                    minRowCount = thisSample.getTableData().length;
                    minRowSample = thisSample;
                }
            }
            out.write(System.getProperty("line.separator"));
            for (int row = 0; row < minRowCount; row++) {
                out.write(minRowSample.getRowID(row) + delim);
                for (int col = 0; col < sampleList.size(); col++) {
                    Sample thisSample = sampleList.get(col);
                    if (row < thisSample.getTableData().length) {
                        out.write(thisSample.getRowValue(row) + delim);
                    }
                }
                out.write(System.getProperty("line.separator"));
            }
            out.close();
            System.out.println("All done with data file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String convertToValidXMLName(String name) {
        String result = name;


        result = result.replace('/', '-');
        result = result.replace('\\', '-');


        return result;
    }


    private class SampleInfo {
        private String sampleID;

        private long tableStart;

        private int idCol;

        private int valueCol;
        private int rows;
        private long nextRow;
        private RandomAccessFile dataIn;

        public SampleInfo(String ID) {
            sampleID = ID;
            rows = -1;
        }

        public void resetRowAccess() {
            nextRow = tableStart;
        }

        public String getNextValue() throws IOException {
            dataIn.seek(nextRow);
            for (int x = 0; x < valueCol; x++) {
                dataIn.readString();
            }
            String result = dataIn.readString();
            dataIn.nextLine();
            nextRow = dataIn.getFilePointer();
            return result;
        }

        public String getNextValue(RandomAccessFile dataIn) throws IOException {
            dataIn.seek(nextRow);
            for (int x = 0; x < valueCol; x++) {
                dataIn.readString();
            }
            String result = dataIn.readString();
            dataIn.nextLine();
            nextRow = dataIn.getFilePointer();
            return result;
        }


        public String getValue(RandomAccessFile dataIn, int row)
                throws IOException {
            dataIn.seek(tableStart);
            for (int x = 0; x < row; x++) {
                dataIn.nextLine();
            }
            for (int x = 0; x < valueCol; x++) {
                dataIn.readString();
            }
            return dataIn.readString();
        }


        public String getID_REF(RandomAccessFile dataIn, int row)
                throws IOException {
            long startTime = Calendar.getInstance().getTimeInMillis();
            dataIn.seek(tableStart);
            for (int x = 0; x < row; x++) {
                dataIn.nextLine();
            }
            for (int x = 0; x < idCol; x++) {
                dataIn.readString();
            }
            long endTime = Calendar.getInstance().getTimeInMillis();
            System.out.println("ID_REF took " + (endTime - startTime) + "ms!");
            return dataIn.readString();
        }

        public String[] getAllIDRefs() throws IOException {
            return getAllIDRefs(dataIn);
        }

        public String[] getAllIDRefs(RandomAccessFile dataIn) throws IOException {
            if (rows < 0) {
                throw new IOException(
                        "Must call scanTable() before getAllIDRefs()!");
            }
            String[] result = new String[rows];
            dataIn.seek(tableStart);
            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < idCol; y++) {
                    dataIn.readString();
                }
                result[x] = dataIn.readString();
                dataIn.nextLine();
            }
            return result;
        }

        public String[] getAllValues(RandomAccessFile dataIn) throws IOException {
            if (rows < 0) {
                throw new IOException(
                        "Must call scanTable() before getAllValues()!");
            }
            String[] result = new String[rows];
            dataIn.seek(tableStart);
            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < valueCol; y++) {
                    dataIn.readString();
                }
                result[x] = dataIn.readString();
                dataIn.nextLine();
            }
            return result;
        }


        public int scanTable(RandomAccessFile dataIn)
                throws IOException {
            this.dataIn = dataIn;
            String[] headers = dataIn.readLine().split("\t");
            tableStart = dataIn.getFilePointer();
            nextRow = dataIn.getFilePointer();
            for (int x = 0; x < headers.length; x++) {
                if (headers[x].equals("ID_REF")) {
                    idCol = x;
                } else if (headers[x].equals("VALUE")) {
                    valueCol = x;
                }
            }
            rows = 0;
            while ((dataIn.peek() != '!') && (!pm.isCanceled()) &&
                    (dataIn.getFilePointer() < dataIn.length())) {
                rows += 1;
                dataIn.nextLine();
            }
            return rows;
        }

        public String getSampleID() {
            return sampleID;
        }
    }
}
