package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class ImportListDialog
        extends JDialog
        implements ActionListener {
    private JTextArea textArea;
    private MetaOmProject myProject;
    private TreeSet<Integer> result;

    private ImportListDialog(MetaOmProject project) {
        super(MetaOmGraph.getMainWindow(), "Import List Data", true);
        myProject = project;

        textArea = new JTextArea();
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("ok");
        okButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        JLabel instructions = new JLabel(
                "Type/paste the entries you want to add, one per line.  Entered values must match a row name value exactly.");
        getContentPane().add(instructions, "First");
        getContentPane().add(textArea, "Center");
        getContentPane().add(buttonPanel, "Last");
        setDefaultCloseOperation(2);
    }


    private int[] getResultArray() {
        if (result != null) {
            int[] ints = new int[result.size()];
            int index = 0;
            for (Integer i : result) {
                ints[(index++)] = i.intValue();
            }
            return ints;
        }
        return null;
    }


    public static int[] doImport(MetaOmProject project) {
        ImportListDialog ild = new ImportListDialog(project);
        ild.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
                MetaOmGraph.getMainWindow().getHeight() / 2);
        ild.setVisible(true);
        return ild.getResultArray();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
            result = null;
            dispose();
            return;
        }
        if (e.getActionCommand().equals("ok")) {
            textArea.setText(textArea.getText().trim());
            if (textArea.getText().length() <= 0) {
                JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
                        "You must enter some value!", "Error",
                        0);
                return;
            }
            result = new TreeSet<Integer>();
            StringTokenizer st = new StringTokenizer(textArea.getText(), "\n");
            int totalMatches=0;
            while (st.hasMoreTokens()) {
                String thisToken = st.nextToken().trim().toLowerCase();
                boolean done = false;
                /*for (int x = 0; x < myProject.getRowNames().length && !done; x++) {
                    for (int y = 0; y < myProject.getRowName(x).length && !done; y++) {
                        String[] splitName = (myProject.getRowName(x)[y] + "").split(";");
                        for (String thisName : splitName) {
                            if (thisName.equalsIgnoreCase(thisToken)) {
                                result.add(new Integer(x));
                                done = true;
                            }
                        }
                    }
                }*/
               int thisInd= myProject.getRowIndexbyName(thisToken,false);
               if(thisInd>=0) {
            	   result.add(new Integer(thisInd));
            	   totalMatches++;
               }
               
             /* String[] allRownames=myProject.getAllDefaultRowNames();
                for (int x = 0; x < allRownames.length && !done; x++) {
                	String thisName=allRownames[x];
                	if (thisName.equalsIgnoreCase(thisToken)) {
                        result.add(new Integer(x));
                        done = true;
                        totalMatches++;
                    }
                }*/
            }
            

            if (result.size() <= 0) {
                JOptionPane.showMessageDialog(
                        MetaOmGraph.getMainWindow(),
                        "None of the values you entered correspond to any row names.",
                        "No matches found", 0);
            } else {
            	JOptionPane.showMessageDialog(
                        MetaOmGraph.getMainWindow(),totalMatches+" values matched", totalMatches+" matches found", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
            return;
        }
    }
}
