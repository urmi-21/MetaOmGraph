package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class UniversalProjectCreator
        extends JPanel
        implements ActionListener {
    private JList fileList;
    private DefaultListModel fileListModel;
    private JButton addButton;
    private JButton removeButton;
    public static final String ADD_COMMAND = "add file";
    public static final String REMOVE_COMMAND = "remove file";
    public static final String OK_COMMAND = "ok";
    public static final String CANCEL_COMMAND = "cancel";

    public UniversalProjectCreator() {
        GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout());
        fileListModel = new DefaultListModel();
        fileList = new JList(fileListModel);
        IconTheme theme = MetaOmGraph.getIconTheme();
        addButton = new JButton(theme.getListAdd());
        addButton.addActionListener(this);
        addButton.setActionCommand("add file");
        removeButton = new JButton(theme.getListDelete());
        removeButton.addActionListener(this);
        removeButton.setActionCommand("remove file");
        JToolBar fileToolBar = new JToolBar();
        fileToolBar.add(addButton);
        fileToolBar.add(removeButton);
        JPanel fileListPanel = new JPanel(new BorderLayout());
        fileListPanel.add(fileToolBar, "North");
        fileListPanel.add(fileList, "Center");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 0;
        c.gridheight = 0;
        add(fileListPanel, c);
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame f = new JFrame("testin out");
        f.add(new UniversalProjectCreator(), "Center");
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if ("add file".equals(e.getActionCommand())) {
            try {
                MetaOmFile source = new MetaOmFile(Utils.chooseFileToOpen());
                fileListModel.addElement(source);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


    private class MetaOmFile
            extends File {
        private int rowCount;
        private double mean;
        private int headerRowCount;
        private int infoColumnCount;
        private char delimiter;

        public MetaOmFile(File source)
                throws IOException {
            this(source.getAbsolutePath());
        }

        public MetaOmFile(String pathname) throws IOException {
            super(pathname);
            analyzeFile(this);
        }

        private void analyzeFile(File source) throws IOException {
            RandomAccessFile dataIn = new RandomAccessFile(source, "r");
            String firstLine = dataIn.readLine().trim();
            String secondLine = dataIn.readLine().trim();
            System.out.println("First line:");
            System.out.println(firstLine);
            System.out.println("Second line:");
            System.out.println(secondLine);
            if ((firstLine == null) || (secondLine == null)) {
                if (firstLine == null) {
                    System.err.println("firstline==null");
                } else
                    System.err.println("secondLine==null");
                return;
            }
            if ((firstLine.equals("")) ||
                    (secondLine.equals(""))) {
                if (firstLine.equals("")) {
                    System.err.println("firstLine.equals(\"\")");
                } else
                    System.err.println("secondLine.equals(\"\"))");
                return;
            }


            int firstTabs = 0;
            int secondTabs = 0;
            int firstSpaces = 0;
            int secondSpaces = 0;
            int firstCommas = 0;
            int secondCommas = 0;
            int firstSemis = 0;
            int secondSemis = 0;
            for (int i = 0; i < firstLine.length(); i++) {
                switch (firstLine.charAt(i)) {
                    case '\t':
                        firstTabs++;
                        break;
                    case ' ':
                        firstSpaces++;
                        break;
                    case ',':
                        firstCommas++;
                        break;
                    case ';':
                        firstSemis++;
                }

            }
            for (int i = 0; i < secondLine.length(); i++) {
                switch (secondLine.charAt(i)) {
                    case '\t':
                        secondTabs++;
                        break;
                    case ' ':
                        secondSpaces++;
                        break;
                    case ',':
                        secondCommas++;
                        break;
                    case ';':
                        secondSemis++;
                }

            }
            if ((firstTabs == secondTabs) && (firstTabs != 0)) {
                delimiter = '\t';
            } else if ((firstSemis == secondSemis) && (firstSemis != 0)) {
                delimiter = ';';
            } else if ((firstCommas == secondCommas) &&
                    (firstCommas != 0)) {
                delimiter = ',';
            } else if ((firstSpaces == secondSpaces) &&
                    (firstSpaces != 0)) {
                delimiter = ' ';
            }
            if (delimiter == 0) {
                if (Utils.getExtension(this).equals("csv")) {
                    delimiter = ',';
                } else {
                    System.err.println("Couldn't figure out delimiter");
                    System.err.println("Tabs: " + firstTabs + ", " + secondTabs);
                    System.err.println("Spaces: " + firstSpaces + ", " + secondSpaces);
                    System.err.println("Commas: " + firstCommas + ", " + secondCommas);
                    System.err.println("Semis: " + firstSemis + ", " + secondSemis);
                    return;
                }
            }
            dataIn.seek(0L);
            boolean headersDone = false;
            headerRowCount = 0;
            long dataStart = -1L;
            long thisRow = dataIn.getFilePointer();
            String thisLine = dataIn.readLine();
            while ((!headersDone) && (thisLine != null)) {
                String[] splitLine = thisLine.split(delimiter + "");
                int index = splitLine.length - 1;
                while ((index >= 0) && (!headersDone)) {
                    try {
                        Double.parseDouble(splitLine[index]);
                        headersDone = true;
                        dataStart = thisRow;
                    } catch (NumberFormatException nfe) {
                        index--;
                    }
                }
                if (!headersDone) {
                    headerRowCount += 1;
                }
                thisRow = dataIn.getFilePointer();
                thisLine = dataIn.readLine();
            }
            System.out.println("Tabs: " + firstTabs + ", " + secondTabs);
            System.out.println("Spaces: " + firstSpaces + ", " + secondSpaces);
            System.out.println("Commas: " + firstCommas + ", " + secondCommas);
            System.out.println("Semis: " + firstSemis + ", " + secondSemis);
            System.out.println("Delimiter: [" + delimiter + "]");
            System.out.println("Header rows: " + headerRowCount);
            System.out.println("Data starts at: " + dataStart);
            dataIn.seek(dataStart);
            String[] splitLine = dataIn.readLine().split(delimiter + "");
            infoColumnCount = -1;
            int totalColumnCount = splitLine.length;
            int index = splitLine.length - 1;
            while ((infoColumnCount < 0) && (index >= 0)) {
                try {
                    System.out.print("Parsing: " + splitLine[index] + "... ");
                    Double.parseDouble(splitLine[index]);
                    System.out.println("Success!");
                    index--;
                } catch (NumberFormatException nfe) {
                    System.out.println("Failure!");
                    infoColumnCount = (index + 1);
                }
            }
            if (infoColumnCount < 0) {
                infoColumnCount = 0;
            }
            System.out.println("Info column count: " + infoColumnCount);
            int dataColumnCount = totalColumnCount - infoColumnCount;
            double[] means = new double[dataColumnCount];
            dataIn.seek(dataStart);
            int rowCount = 0;
            while ((thisLine = dataIn.readLine()) != null) {
                rowCount++;
                splitLine = thisLine.split(delimiter + "");
                for (int i = 0; i < means.length; i++) {
                    double thisVal = Double.parseDouble(splitLine[infoColumnCount + i]);
                    means[i] += thisVal;
                }
            }


            System.out.println("Row count: " + rowCount);
            for (int i = 0; i < means.length; i++) {
                means[i] /= rowCount;
                System.out.println("Mean " + i + ": " + means[i]);
            }
        }

        @Override
		public String toString() {
            return getName();
        }
    }
}
