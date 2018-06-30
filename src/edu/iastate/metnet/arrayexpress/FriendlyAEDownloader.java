package edu.iastate.metnet.arrayexpress;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FriendlyAEDownloader {
    public FriendlyAEDownloader() {
    }

    public static void main(String[] args) throws Exception {
        URL arrayUrl = new URL(
                "http://www.ebi.ac.uk/microarray-as/ae/arrays-list.html");
        BufferedReader in = new BufferedReader(new InputStreamReader(arrayUrl
                .openStream()));
        String line = in.readLine();
        in.close();
        JComboBox box = new JComboBox();
        DefaultListModel model = new DefaultListModel();


        String[] splitLine = line.split("<");
        System.out.println(splitLine.length);
        for (String thisTag : splitLine) {
            if (thisTag.startsWith("option")) {
                try {
                    Pattern regex = Pattern.compile(
                            "(?<=option value=\")\\d*(?=\">)", 194);


                    Matcher regexMatcher = regex.matcher(thisTag);
                    while (regexMatcher.find()) {
                        String thisValue = regexMatcher.group();
                        if (!thisValue.equals("")) {

                            Pattern regex2 = Pattern.compile("(?<=>).*$",
                                    194);

                            Matcher regexMatcher2 = regex2.matcher(thisTag);
                            if (regexMatcher2.find()) {
                                System.out.println("Found " + regexMatcher2.group());
                            } else {
                                System.out.println("Found nothing in " + thisTag);
                            }
                            String thisName = regexMatcher2.group();
                            Option thisOption = new Option(thisName,
                                    Integer.parseInt(thisValue));
                            model.addElement(thisOption);
                        }
                    }
                } catch (PatternSyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        }
        final JList list = new JList(model);
        list.setSelectionMode(0);
        final JFrame f = new JFrame("AE Analyzer");
        f.getContentPane().add(new javax.swing.JScrollPane(list), "Center");

        JButton okButton = new JButton(new AbstractAction("OK") {
            public void actionPerformed(ActionEvent e) {
                FriendlyAEDownloader.Option selected = (FriendlyAEDownloader.Option) list.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(f, "You must select an array!", "Error", 0);
                    return;
                }
                int id = selected.value;
                File dest = Utils.chooseDir(f);
                if (dest == null) {
                    return;
                }
                f.dispose();
                System.out.println("Saving to: " + dest);
                try {
                    AEDataDownloader.getItAll(id, dest);
                    AEDataDownloader.analyzeHeaders(dest);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                System.out.println(selected.value + ": " + selected);
                System.exit(0);
            }

        });
        JButton cancelButton = new JButton(new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                f.dispose();
                System.exit(0);
            }

        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        f.getContentPane().add(buttonPanel, "South");
        f.setSize(500, 500);
        f.setDefaultCloseOperation(3);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Option selected = (Option) list.getSelectedValue();
                System.out.println(selected.value + ": " + selected);

            }
        });
    }

    public static class Option {
        String name;
        int value;

        public Option(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String toString() {
            return name;
        }
    }
}
