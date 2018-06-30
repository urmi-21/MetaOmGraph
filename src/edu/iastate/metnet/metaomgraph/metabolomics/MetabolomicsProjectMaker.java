package edu.iastate.metnet.metaomgraph.metabolomics;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ca.ansir.swing.tristate.TriState;
import ca.ansir.swing.tristate.TriStateTreeHandler;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class MetabolomicsProjectMaker {

    private File dataFile;

    private File metadataFile;

    private CustomTriStateTreeNode root;

    private static String[] samples = {"NW1", "NW2", "NW3", "DLW1", "DLW2",
            "DLW3", "ILW1", "ILW2", "ILW3", "PTW1", "PTW2", "PTW3", "NTW1",
            "NTW2", "NTW3", "Nplus1W1", "Nplus1W2", "Nplus1W3", "Nplus3W1",
            "Nplus3W2", "Nplus3W3", "NM1", "NM2", "NM3"};

    private static String[] metadataSamples = {"NW-1", "NW-2", "NW-3",
            "DLW-1", "DLW-2", "DLW-3", "ILW-1", "ILW-2", "ILW-3", "PTW-1",
            "PTW-2", "PTW-3", "NTW-1", "NTW-2", "NTW-3", "N(+1)W-1",
            "N(+1)W-2", "N(+1)W-3", "N(+3)W-1", "N(+3)W-2", "N(+3)W-3", "NM-1",
            "NM-2", "NM-3"};

    private static String[] metadataFields = {"TreatmentDesc", "Genotype",
            "TreatmentLightIntensity", "TreatmentTemp", "HarvestDelay"};

    /**
     * @param args
     */
    public static void main(String[] args) {
        new MetabolomicsProjectMaker().showProjectMakerPanel(null);
    }

    public File getMetadataFile() {
        return metadataFile;
    }

    public File getDataFile() {
        return dataFile;
    }

    public File showProjectMakerPanel(Frame parent) {
        dataFile = null;
        final JDialog dialog = new JDialog(parent, "Experiment Tree Test", true);
        JPanel makerPanel = new JPanel(new BorderLayout());
        root = getExperimentTree();
        JTree experimentTree = new JTree(root);
        experimentTree.setEditable(true);
        TriStateTreeHandler handler = new TriStateTreeHandler(experimentTree);
        // handler.unconfigureTree();
        // experimentTree.setShowsRootHandles(true);
        makerPanel.add(new JScrollPane(experimentTree), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }

        });
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                FileFilter filter = Utils.createFileFilter("txt",
                        "Tab-delimited text files");
                dataFile = Utils.chooseFileToSave(filter, "txt", null, true);
                if (dataFile == null) {
                    return;
                }
                String connString = "jdbc:mysql://localhost/sandbox_pbais05";
                Connection connection = null;
                Statement statement = null;
                try {
                    connection = DriverManager.getConnection(connString,
                            "guest", "");
                    statement = connection.createStatement();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    return;
                }
                Vector<ExperimentInfo> targets = getTargetExpIDs(root);
                // System.out.println("Doing these:");
                ResultSet rs;
                FileWriter out = null;
                String sep = System.getProperty("line.separator");
                try {
                    out = new FileWriter(dataFile);
                    out.write("Metabolite Name");
                    for (String thisSample : metadataSamples) {
                        out.write('\t' + thisSample);
                    }
                    out.write(sep);
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return;
                }
                String selectString = null;
                for (int i = 0; i < samples.length; i++) {
                    if (selectString == null) {
                        selectString = "select MetaboliteName, " + samples[i]
                                + " as '" + metadataSamples[i] + "'";
                    } else {
                        selectString += ", " + samples[i] + " as '"
                                + metadataSamples[i] + "'";
                    }
                }
                selectString += " from metabolitedata where targetExpID=";
                try {
                    for (ExperimentInfo thisInfo : targets) {
                        System.out.println(thisInfo);
                        String sql = selectString + thisInfo.targetExpID
                                + " and experimentID=" + thisInfo.expID;
                        System.out.println(sql);
                        rs = statement.executeQuery(sql);
                        rs.first();
                        while (!rs.isAfterLast()) {
                            out.write(rs.getString("MetaboliteName"));
                            for (String thisSample : metadataSamples) {
                                out.write('\t' + rs.getString(thisSample));
                            }
                            out.write(sep);
                            rs.next();
                        }
                    }
                    out.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                metadataFile = new File(dataFile.getParentFile(),
                        "metadata.xml");
                Element metadataRoot = new Element("Experiments");
                String sql = "select * from biologymetageneral";
                try {
                    Statement experimentStatement = connection
                            .createStatement();
                    ResultSet experimentResults = experimentStatement
                            .executeQuery(sql);
                    if (experimentResults.first()) {
                        while (!experimentResults.isAfterLast()) {
                            Element thisExperiment = new Element("Experiment")
                                    .setAttribute("name", experimentResults
                                            .getString("ExperimentName"));
                            thisExperiment.addContent(new Element(
                                    "GrowthConditions")
                                    .setText(experimentResults
                                            .getString("GrowthConditions")));
                            thisExperiment.addContent(new Element(
                                    "ExperimentGoal").setText(experimentResults
                                    .getString("ExperimentGoal")));
                            sql = "select * from biologytimeline where experimentid="
                                    + experimentResults
                                    .getString("ExperimentID");
                            rs = statement.executeQuery(sql);
                            String timeline = null;
                            if (rs.first()) {
                                while (!rs.isAfterLast()) {
                                    if (timeline == null) {
                                        timeline = rs.getString("EventDate")
                                                + ": "
                                                + rs.getString("EventDesc");
                                    } else {
                                        timeline += "\n"
                                                + rs.getString("EventDate")
                                                + ": "
                                                + rs.getString("EventDesc");
                                    }
                                    rs.next();
                                }
                                thisExperiment.addContent(new Element(
                                        "Timeline").setText(timeline));
                            }
                            sql = "select * from biologymetadata";
                            rs = statement.executeQuery(sql);
                            rs.first();
                            while (!rs.isAfterLast()) {
                                Element thisSampleElement = new Element(
                                        "sample").setAttribute("name", rs
                                        .getString("SampleName"));
                                for (String thisField : metadataFields) {
                                    thisSampleElement.addContent(new Element(
                                            thisField).setText(rs
                                            .getString(thisField)));
                                }
                                thisExperiment.addContent(thisSampleElement);
                                rs.next();
                            }
                            metadataRoot.addContent(thisExperiment);
                            experimentResults.next();
                        }
                        XMLOutputter xmlOut = new XMLOutputter();
                        xmlOut.setFormat(Format.getPrettyFormat().setEncoding(
                                "ISO-8859-1"));
                        Document myDoc = new Document(metadataRoot);
                        // myDoc.addContent(0, new Comment("Produced "
                        // + Calendar.getInstance().getTime()));
                        out = new FileWriter(metadataFile);
                        xmlOut.output(myDoc, out);
                        out.close();
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                System.out.println("All done!");
            }

        });
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.getContentPane().add(makerPanel, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(400, 400);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dataFile;
    }

    private static String convertToSQLName(String name) {
        String result = "";
        for (int i = 0; i < name.length(); i++) {
            char thisChar = name.charAt(i);
            switch (thisChar) {
                case '+':
                    result += "plus";
                    break;
                case '(':
                case ')':
                case '-':
                    break;
                default:
                    result += thisChar;
                    break;
            }
        }
        return result;
    }

    private static CustomTriStateTreeNode getExperimentTree() {
        CustomTriStateTreeNode root = new CustomTriStateTreeNode(
                new ExperimentInfo("Experiments", 0, 0));
        String connString = "jdbc:mysql://localhost/sandbox_pbais05";
        Connection connection = null;
        try {
            org.gjt.mm.mysql.Driver.class.newInstance();
            connection = DriverManager.getConnection(connString, "guest", "");
            Statement statement = connection.createStatement();
            String sql = "SELECT ExperimentID, ExperimentName, count(*) as metabolites FROM metabolitedata m group by ExperimentID";
            ResultSet rs = statement.executeQuery(sql);
            if (!rs.last()) {
                System.err.println("No experiments!");
                return root;
            }
            CustomTriStateTreeNode[] experiments = new CustomTriStateTreeNode[rs
                    .getRow()];
            rs.first();
            for (int i = 0; i < experiments.length; i++) {
                experiments[i] = new CustomTriStateTreeNode(new ExperimentInfo(
                        rs.getString("ExperimentName") + " ["
                                + rs.getString("metabolites") + "]", 0, 0));
                experiments[i].setState(TriState.SELECTED);
                root.add(experiments[i]);
                rs.next();
            }
            sql = "SELECT ExperimentID, targetExpID, targetExpName, LabPI, count(*) as metabolites FROM metabolitedata m group by experimentID, targetExpID";
            rs = statement.executeQuery(sql);
            rs.first();
            while (!rs.isAfterLast()) {
                int expID = rs.getInt("ExperimentID");
                String nodeName = rs.getString("targetExpName");
                if (nodeName == null) {
                    nodeName = "<unnamed>";
                }
                nodeName += " [" + rs.getString("LabPI") + "; "
                        + rs.getString("metabolites") + "]";
                ExperimentInfo info = new ExperimentInfo(nodeName, expID, rs
                        .getInt("targetExpID"));
                CustomTriStateTreeNode thisNode = new CustomTriStateTreeNode(
                        info);
                // thisNode.setUserObject(info);
                thisNode.setState(TriState.SELECTED);
                experiments[expID - 1].add(thisNode);
                rs.next();
            }
        } catch (SQLException sqle) {
            System.err.println("SQLException!");
            System.err.println("Error code: " + sqle.getErrorCode());
            System.err.println("Reason: " + sqle.getMessage());
            System.err.println("State: " + sqle.getSQLState());
            System.err.println("Next: " + sqle.getNextException());
            sqle.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return root;
    }

    private static Vector<ExperimentInfo> getTargetExpIDs(
            CustomTriStateTreeNode root) {
        Enumeration treeEnum = root.depthFirstEnumeration();
        Vector<ExperimentInfo> result = new Vector<ExperimentInfo>();
        while (treeEnum.hasMoreElements()) {
            CustomTriStateTreeNode thisNode = (CustomTriStateTreeNode) treeEnum
                    .nextElement();
            if ((thisNode.isLeaf()) && (thisNode.isSelected())) {
                if (!(thisNode.getUserObject() instanceof ExperimentInfo)) {
                    System.err.println(thisNode.getUserObject() + " is a "
                            + thisNode.getUserObject().getClass());
                } else {
                    result.add((ExperimentInfo) thisNode.getUserObject());
                    System.out.println("Added " + thisNode.getUserObject());
                }
            } else {
                // System.out.println("Skipped " + thisNode.getUserObject());
                // System.out.println("isLeaf(): " + thisNode.isLeaf());
                // System.out.println("getState(): " + thisNode.getState());
            }
        }
        return result;
    }

    private static class ExperimentInfo {
        String name;

        int expID;

        int targetExpID;

        ExperimentInfo(String name, int expID, int targetExpID) {
            this.name = name;
            this.expID = expID;
            this.targetExpID = targetExpID;
        }

        public String toString() {
            return name;
        }
    }
}
