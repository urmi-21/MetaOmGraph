package edu.iastate.metnet.metaomgraph.utils;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.ui.FileSelectionPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class ProjectMerger {
    public ProjectMerger() {
    }

    public static File mergeProjects(File project1, File project2, File dest)
            throws JDOMException, IOException {
        File dataDest = new File(dest.getAbsolutePath() + ".txt");
        ZipInputStream zip1 = new ZipInputStream(new FileInputStream(project1));
        ZipInputStream zip2 = new ZipInputStream(new FileInputStream(project2));
        ZipEntry file1 = null;
        ZipEntry file2 = null;
        ZipEntry meta1 = null;
        ZipEntry meta2 = null;

        ZipEntry thisEntry = zip1.getNextEntry();
        if (thisEntry == null) {
            throw new NullPointerException("Project 1 is empty");
        }
        if (thisEntry.getName().equals("ProjectFile.xml")) {
            file1 = thisEntry;
        } else if (thisEntry.getName().equals("extended.xml")) {
            meta1 = thisEntry;
        }
        thisEntry = zip1.getNextEntry();
        if (thisEntry != null) {
            if (thisEntry.getName().equals("ProjectFile.xml")) {
                file1 = thisEntry;
            } else if (thisEntry.getName().equals("extended.xml")) {
                meta1 = thisEntry;
            }
        }
        if (file1 == null) {
            throw new IOException("Project 1 is not a MOG project");
        }
        thisEntry = zip2.getNextEntry();
        if (thisEntry == null) {
            throw new NullPointerException("Project 2 is empty");
        }
        if (thisEntry.getName().equals("ProjectFile.xml")) {
            file2 = thisEntry;
        } else if (thisEntry.getName().equals("extended.xml")) {
            meta2 = thisEntry;
        }
        thisEntry = zip2.getNextEntry();
        if (thisEntry != null) {
            if (thisEntry.getName().equals("ProjectFile.xml")) {
                file2 = thisEntry;
            } else if (thisEntry.getName().equals("extended.xml")) {
                meta2 = thisEntry;
            }
        }
        if (file2 == null) {
            throw new IOException("Project 2 is not a MOG project");
        }
        ZipFile zipSource1 = new ZipFile(project1);
        ZipFile zipSource2 = new ZipFile(project2);
        System.out.println("Building projects");
        BufferedInputStream projectFile1 = new BufferedInputStream(zipSource1
                .getInputStream(file1));
        BufferedInputStream projectFile2 = new BufferedInputStream(zipSource2
                .getInputStream(file2));
        Document doc1 = new SAXBuilder().build(projectFile1);
        Document doc2 = new SAXBuilder().build(projectFile2);
        Element newRoot = new Element("MetaOmProject");
        Element newProjectInfo = new Element("projectInfo");
        newProjectInfo.addContent(new Element("sourcePath").setText(dataDest
                .getAbsolutePath()));
        newProjectInfo.addContent(new Element("sourceFile").setText(dataDest
                .getName()));
        List infoChildren = doc1.getRootElement().getChild("projectInfo")
                .getChildren();
        for (Object o : infoChildren) {
            Element e = (Element) o;
            if (!e.getName().startsWith("source")) {
                newProjectInfo.addContent((Element) e.clone());
            }
        }
        newRoot.addContent(newProjectInfo);
        Element info1 = doc1.getRootElement().getChild("projectInfo");
        File source1 = new File(project1.getParentFile(), info1
                .getChildText("sourceFile"));
        if (!source1.exists()) {
            source1 = new File(info1.getChildText("sourcePath"), info1
                    .getChildText("sourceFile"));
        }
        if (!source1.exists()) {
            System.err.println("Unable to locate source file 1: " +
                    source1.getAbsolutePath());
            return null;
        }
        System.out.println("Getting info columns");
        List children = info1.getChildren("infoColumn");
        String[] myCols1 = new String[children.size()];
        int index = 0;
        for (Object o : children) {
            myCols1[(index++)] = ((Element) o).getText();
        }

        Element info2 = doc2.getRootElement().getChild("projectInfo");
        File source2 = new File(project2.getParentFile(), info2
                .getChildText("sourceFile"));
        if (!source2.exists()) {
            source2 = new File(info2.getChildText("sourcePath"), info2
                    .getChildText("sourceFile"));
        }
        if (!source2.exists()) {
            System.err.println("Unable to locate source file 2: " +
                    source2.getAbsolutePath());
            return null;
        }

        children = info2.getChildren("infoColumn");
        String[] myCols2 = new String[children.size()];
        index = 0;
        for (Object o : children) {
            myCols2[(index++)] = ((Element) o).getText();
        }

        int[] idCols = getIDCols(project1.getName(), myCols1, project2
                .getName(), myCols2);
        if ((idCols[0] < 0) || (idCols[1] < 0)) {
            return null;
        }


        Element delimiterElement = info1.getChild("delimiter");
        char delimiter1;
        if (delimiterElement.getText().equals("")) {
            delimiter1 = ' ';
        } else {
            if (delimiterElement.getText().equals("\\t")) {
                delimiter1 = '\t';
            } else
                delimiter1 = delimiterElement.getText().charAt(0);
        }
        delimiterElement = info2.getChild("delimiter");
        char delimiter2;
        if (delimiterElement.getText().equals("")) {
            delimiter2 = ' ';
        } else {
            if (delimiterElement.getText().equals("\\t")) {
                delimiter2 = '\t';
            } else {
                delimiter2 = delimiterElement.getText().charAt(0);
            }
        }
        Element newColumnsElement = new Element("columns");
        int infoCols1 = info1.getChildren("infoColumn").size();
        int infoCols2 = info2.getChildren("infoColumn").size();
        boolean ignoreConsecutive1 = "true".equals(info1
                .getChildText("ignoreConsecutiveDelimiters"));
        boolean ignoreConsecutive2 = "true".equals(info2
                .getChildText("ignoreConsecutiveDelimiters"));

        RandomAccessFile in1 = new RandomAccessFile(source1, "r");
        RandomAccessFile in2 = new RandomAccessFile(source2, "r");

        RandomAccessFile out = new RandomAccessFile(dataDest, "rw");
        String line1 = in1.readLine();
        String[] split1 = split(line1, delimiter1, ignoreConsecutive1);
        int cols1 = split1.length;
        for (int i = 0; i < split1.length; i++) {
            if (i != 0) {
                out.writeByte(delimiter1);
            }
            out.writeBytes(split1[i]);
            if (i >= infoCols1) {
                newColumnsElement.addContent(new Element("column")
                        .setText(split1[i]));
            }
        }


        String line2 = in2.readLine();
        String[] split2 = split(line2, delimiter2, ignoreConsecutive2);
        int cols2 = split2.length;
        for (int i = infoCols2; i < split2.length; i++) {
            out.writeBytes(delimiter1 + split2[i]);
            newColumnsElement.addContent(new Element("column")
                    .setText(split2[i]));
        }
        out.writeBytes("\r\n");
        newRoot.addContent(newColumnsElement);


        System.out.println("Getting file 2 IDs");
        TreeMap<String, Long> idMap = new TreeMap();

        children = doc2.getRootElement().getChildren("data");

        for (Object o : children) {
            Element e = (Element) o;
            String id = ((Element) e.getChildren("info").get(idCols[1]))
                    .getText();
            long loc = Long.parseLong(e.getChildText("location"));
            idMap.put(id, Long.valueOf(loc));
            System.out.println("Added id: " + id);
        }
        System.out.println("Merging data files");
        TreeMap<String, Long> newLocMap = new TreeMap();
        children = doc1.getRootElement().getChildren("data");
        int i;
        while ((line1 = in1.readLine()) != null) {
            split1 = Arrays.copyOf(line1.split(delimiter1 + ""), cols1);
            String id = split1[idCols[0]];
            out.writeBytes(split1[0] + "\t");
            newLocMap.put(id, Long.valueOf(out.getFilePointer()));


            for (i = 1; i < split1.length; i++) {
                if (i != 1) {
                    out.writeByte(delimiter1);
                }
                Element infoElement = new Element("info");
                if (split1[i] != null) {
                    out.writeBytes(split1[i]);
                    infoElement.setText(split1[i]);
                }
            }


            Long loc = idMap.get(id);
            if (loc == null) {
                for (i = 0; i < cols2 - infoCols2; i++) {
                    out.writeByte(delimiter1);
                }
            } else {
                in2.seek(loc.longValue());
                split2 = Arrays.copyOf(split(in2.readLine(), delimiter2,
                        ignoreConsecutive2), cols2);

                for (i = 1; i < split2.length; i++) {
                    out.writeByte(delimiter1);
                    if (split2[i] != null) {
                        out.writeBytes(split2[i]);
                    }
                }
            }

            out.writeBytes("\r\n");
        }
        int missing = 0;
        for (Object o : children) {
            Element e = (Element) o;
            int childIndex = 0;
            List children2 = e.getChildren();
            Long loc = null;
            Element newDataElement = new Element("data");
            String id = null;
            for (Object o2 : children2) {
                Element e2 = (Element) o2;
                if (childIndex == idCols[0]) {
                    id = e2.getText();
                    loc = newLocMap.get(id);
                }
                if (!e2.getName().equals("location")) {
                    newDataElement.addContent((Element) e2.clone());
                }
                childIndex++;
            }
            if (loc != null) {
                newDataElement.addContent(new Element("location").setText(loc
                        .toString()));
                newRoot.addContent(newDataElement);
            } else {
                System.err.println("No location for " + id);
                missing++;
            }
        }

        System.out.println("Missed: " + missing);
        out.close();
        in1.close();
        in2.close();
        System.out.println("Outputting new project");


        ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(
                dest));
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        outStream.putNextEntry(new ZipEntry("ProjectFile.xml"));
        output.output(newRoot, outStream);
        outStream.closeEntry();


        System.out.println("Combining metadata");
        Element metaRoot = new Element("Experiments");
        if (meta1 != null) {
            BufferedInputStream metadata1 = new BufferedInputStream(zipSource1
                    .getInputStream(meta1));
            Element root1 = new SAXBuilder().build(metadata1).getRootElement();
            metaRoot.addContent(root1.cloneContent());
        }
        if (meta2 != null) {
            BufferedInputStream metadata2 = new BufferedInputStream(zipSource2
                    .getInputStream(meta2));
            Element root2 = new SAXBuilder().build(metadata2).getRootElement();
            metaRoot.addContent(root2.cloneContent());
        } else {
            System.out.println("No metadata to combine");
        }
        if (metaRoot.getContentSize() > 0) {
            outStream.putNextEntry(new ZipEntry("extended.xml"));
            output.output(metaRoot, outStream);
            outStream.closeEntry();
        }
        outStream.close();
        return dest;
    }

    private static String[] split(String line, char delimiter, boolean ignoreConsecutive) {

        String[] splitLine;
        if (ignoreConsecutive) {
            splitLine = line.split(delimiter + "+");
        } else {
            splitLine = line.split(delimiter + "");
        }
        return splitLine;
    }

    private static int[] getIDCols(String filename1, String[] cols1, String filename2, String[] cols2) {
        final int[] result = new int[2];
        Arrays.fill(result, -1);
        final JDialog dialog = new JDialog((Frame) null, "ID Columns", true);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));

        final JList list1 = new JList(cols1);
        list1.setSelectionMode(0);
        final JList list2 = new JList(cols2);
        list2.setSelectionMode(0);

        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(new JScrollPane(list1), "Center");
        panel1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), filename1 + ":"));
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(new JScrollPane(list2), "Center");
        panel2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), filename2 + ":"));
        mainPanel.add(panel1, "West");
        mainPanel.add(panel2, "East");
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(new AbstractAction("OK") {
            public void actionPerformed(ActionEvent e) {
                result[0] = list1.getSelectedIndex();
                result[1] = list2.getSelectedIndex();
                if ((result[0] < 0) || (result[1] < 0)) {
                    JOptionPane.showMessageDialog(dialog,
                            "You must select one column from each list",
                            "Error", 0);
                    Arrays.fill(result, -1);
                } else {
                    dialog.dispose();
                }

            }
        });
        JButton cancelButton = new JButton(new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }

        });
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        JLabel message = new JLabel("Please select which columns to match",
                0);
        message.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        dialog.getContentPane().add(mainPanel, "Center");
        dialog.getContentPane().add(message, "North");
        dialog.getContentPane().add(buttonPanel, "South");

        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(2);
        dialog.setVisible(true);
        return result;
    }


    public static void main(String[] args) {
        String testMe = "abc\tdef\tghi\tjkl";
        String[] split = split(testMe, '\t', true);
        for (String s : split) {
            System.out.println(s);
        }
        System.exit(0);
    }


    public static void showMergeDialog() {
        final JDialog dialog = new JDialog(MetaOmGraph.getMainWindow(),
                "Project Merger", true);
        String[] names = {"Project 1", "Project 2"};
        String[] defaultFiles = new String[2];
        String[] extensions = {"mog", "mog"};
        String[] descriptions = {"MetaOmGraph projects",
                "MetaOmGraph projects"};
        JPanel mainPanel = new JPanel(new BorderLayout());
        final FileSelectionPanel openPanel = new FileSelectionPanel(names,
                defaultFiles, extensions, descriptions);
        openPanel.setOpenMode(true);
        final FileSelectionPanel savePanel = new FileSelectionPanel("Save as",
                null, "mog", "MetaOmGraph Project");
        mainPanel.add(openPanel, "Center");
        mainPanel.add(savePanel, "South");
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(new AbstractAction("OK") {
            public void actionPerformed(ActionEvent e) {
                if (!openPanel.checkFiles()) {
                    return;
                }
                dialog.dispose();
                final File[] files = openPanel.getFiles();


                final File dest = savePanel.getFile(0);
                new AnimatedSwingWorker("Merging... ", true) {
                    public Object construct() {
                        try {
                            return ProjectMerger.mergeProjects(files[0], files[1], dest);
                        } catch (JDOMException e1) {
                            e1.printStackTrace();
                            return e1;
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            return e1;
                        }
                    }

                    public void finished() {
                        super.finished();
                        if ((get() != null) && ((get() instanceof File))) {
                            JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Projects merged successfully!", "Success", 1);
                        } else if (get() != null) {
                            JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Unable to merge projects.", "Error", 0);
                        }
                    }
                }.start();
                System.out.println("Projects merged successfully");
            }
        });
        JButton cancelButton = new JButton(new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }

        });
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(500, dialog.getSize().height);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(2);
        dialog.setVisible(true);
    }
}
