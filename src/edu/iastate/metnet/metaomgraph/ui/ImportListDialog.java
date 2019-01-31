package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;


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
            List<String> notFound=new ArrayList<>();
            int totalMatches=0;
            while (st.hasMoreTokens()) {
                String thisToken = st.nextToken().trim().toLowerCase();
               
               int thisInd= myProject.getRowIndexbyName(thisToken,false);
               if(thisInd>=0) {
            	  // JOptionPane.showMessageDialog(null, "thisInd:"+thisInd);
            	   result.add(new Integer(thisInd));
            	   totalMatches++;
               }else {
            	   notFound.add(thisToken);
               }
               
           
            }
            
            
            

            if (result.size() <= 0) {
                JOptionPane.showMessageDialog(
                        MetaOmGraph.getMainWindow(),
                        "None of the values you entered correspond to any row names.",
                        "No matches found", 0);
            } else {
            	JOptionPane.showMessageDialog(
                        MetaOmGraph.getMainWindow(),totalMatches+" values matched", totalMatches+" matches found", JOptionPane.INFORMATION_MESSAGE);
            	
            	if(notFound.size()>0) {
            		JPanel listPanel=new JPanel();
            
            		DefaultListModel listmod = new DefaultListModel();
            		for(String s:notFound) {
            			listmod.addElement(s);
            		}
            		JList list= new JList<>(listmod);
            		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            		listPanel.add(new JScrollPane(list));
            		
            		JOptionPane.showConfirmDialog(null, listPanel, "Following ids were not matched",JOptionPane.PLAIN_MESSAGE);
            		
            	}
            	
            	
                dispose();
            }
            return;
        }
    }
}
