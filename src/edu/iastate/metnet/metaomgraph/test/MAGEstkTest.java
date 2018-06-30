package edu.iastate.metnet.metaomgraph.test;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.biomage.BioAssay.BioAssay;
import org.biomage.BioAssayData.BioAssayData;
import org.biomage.BioAssayData.BioDataValues;
import org.biomage.Common.MAGEJava;
import org.biomage.Common.NameValueType;
import org.biomage.Description.Description;
import org.biomage.Experiment.Experiment;
import org.biomage.Experiment.Experiment_package.Experiment_list;
import org.biomage.Interface.HasBioAssays.BioAssays_list;
import org.biomage.tools.xmlutils.MAGEContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.iastate.metnet.metaomgraph.utils.Utils;

public class MAGEstkTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        System.out.println("UI set, loading file chooser");
        File infile = Utils.chooseFileToOpen();
        if (infile == null) {
            return;
        }
        JFrame f = new JFrame("MAGE-ML parsing results");
        JTextArea resultArea = new JTextArea();
        resultArea.setFont(new Font("ProFontWindows", Font.PLAIN, 9));
        f.getContentPane()
                .add(new JScrollPane(resultArea), BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);
        String resultText = "";

        try {
            // Create the parser.
            XMLReader parser = XMLReaderFactory
                    .createXMLReader("org.apache.xerces.parsers.SAXParser");

            // Create the content handler.
            MAGEContentHandler cHandler = new MAGEContentHandler();

            // Set the content handler.
            parser.setContentHandler(cHandler);

            long startTime = Calendar.getInstance().getTimeInMillis();
            // Parse the file.
            parser.parse(new InputSource(new FileInputStream(infile)));

            // Here is the MAGE-OM object.
            MAGEJava mageJava = cHandler.getMAGEJava();
            long endTime = Calendar.getInstance().getTimeInMillis();
            System.out.println("Parsing took " + (endTime - startTime) + "ms");

            Experiment_list expList = mageJava.getExperiment_package()
                    .getExperiment_list();
            Iterator experIter = expList.iterator();
            int index = 1;
            while (experIter.hasNext()) {
                resultText += "Experiment " + index + "\n";
                Experiment exp = (Experiment) experIter.next();
                resultText += "  Name: " + exp.getName() + "\n";
                Description desc = (Description) exp.getDescriptions().get(0);
                resultText += "  Description: " + desc.getText() + "\n";
                BioAssays_list baList = exp.getBioAssays();
                Iterator baIter = baList.iterator();
                resultText += "  Samples:\n";
                while (baIter.hasNext()) {
                    BioAssay ba = (BioAssay) baIter.next();
                    resultText += "    " + ba.getIdentifier() + "\n";
                    Iterator psIter = ba.getPropertySets().iterator();
                    while (psIter.hasNext()) {
                        NameValueType ps = (NameValueType) psIter.next();
                        resultText += "      " + ps.getName() + " - "
                                + ps.getType() + " - " + ps.getValue() + "\n";
                    }
                }
                Iterator badIter = exp.getBioAssayData().iterator();
                resultText += "  Quantitation Types:\n";
                ArrayList<File> files = new ArrayList<File>();
                while (badIter.hasNext()) {
                    BioAssayData bad = (BioAssayData) badIter.next();
                    BioDataValues bdv = bad.getBioDataValues();
                    Iterator psIter = bdv.getPropertySets().iterator();
                    while (psIter.hasNext()) {
                        NameValueType ps = (NameValueType) psIter.next();
                        resultText += "      " + ps.getName() + " - "
                                + ps.getType() + " - " + ps.getValue() + "\n";
                    }
                    // Iterator
                    // qtIter=bad.getQuantitationTypeDimension().getQuantitationTypes().iterator();
                    // while (qtIter.hasNext()) {
                    // QuantitationType qt=(QuantitationType) qtIter.next();
                    // resultText+=" "+qt.getIdentifier();
                    // OntologyEntry scale=qt.getScale();
                    // resultText+=" - "+scale.getValue()+"\n";
                    // }
                    StringWriter sw = new StringWriter();
                    bdv.writeAssociations(sw);
                    try {
                        Pattern regex = Pattern.compile(
                                "(?<=filenameURI=\").*(?=\">)",
                                Pattern.CANON_EQ | Pattern.MULTILINE);
                        Matcher regexMatcher = regex.matcher(sw.toString());
                        while (regexMatcher.find()) {
                            files.add(new File(infile.getParent(), regexMatcher
                                    .group()));
                        }
                    } catch (PatternSyntaxException ex) {
                        // Syntax error in the regular expression
                    }
                }
                BufferedReader[] ins = new BufferedReader[files.size()];
                for (int i = 0; i < ins.length; i++) {
                    try {
                        ins[i] = new BufferedReader(
                                new FileReader(files.get(i)));
                    } catch (FileNotFoundException nfe) {
                        System.err.println("Skipping " + files.get(i));
                        ins[i] = null;
                    }
                }
                BufferedWriter dataOut = new BufferedWriter(new FileWriter(new File(infile.getParent(), "mogified.csv")));
                String nextLine = "Data\n";
                int line = 0;
                while (!nextLine.equals("\n")) {
                    if (line < 100) {
                        resultText += nextLine;
                    }
                    dataOut.write(nextLine);
                    nextLine = "";
//					BufferedReader thisIn=ins[0];
                    for (BufferedReader thisIn : ins) {
                        if (thisIn != null && thisIn.ready()) {
                            String data = thisIn.readLine();
                            if (data != null) {
                                if (nextLine.equals("")) {
                                    nextLine = data.substring(data.lastIndexOf("\t") + 1);
                                } else {
                                    nextLine += "," + data.substring(data.lastIndexOf("\t") + 1);
                                }
                            } else {
                                System.out.println("Not ready!");
                                thisIn.close();
                            }
                        }
                    }
                    nextLine += "\n";
                    if (++line % 100 == 0) {
                        System.out.println(line);
                    }
                }
                dataOut.close();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        resultArea.setText(resultText);
        f.setVisible(true);
    }

}
