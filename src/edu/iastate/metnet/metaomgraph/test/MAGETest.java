package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class MAGETest {
    static Hashtable<String, Person> people;
    static Hashtable<String, Organization> organizations;

    public MAGETest() {
    }

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
        f.getContentPane().add(new JScrollPane(resultArea), "Center");
        f.setDefaultCloseOperation(3);
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);
        String resultText = "";
        try {
            long startTime = Calendar.getInstance().getTimeInMillis();
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            Document myDoc = builder.build(new FileReader(infile));
            long endTime = Calendar.getInstance().getTimeInMillis();
            System.out.println("Building took " + (endTime - startTime) + "ms");
            Element root = myDoc.getRootElement();
            List peopleList = root.getChild("AuditAndSecurity_package").getChild("Contact_assnlist").getChildren("Person");
            List orgList = root.getChild("AuditAndSecurity_package").getChild("Contact_assnlist").getChildren("Organization");
            people = new Hashtable();
            organizations = new Hashtable();
            Iterator orgIter = orgList.iterator();
            while (orgIter.hasNext()) {
                Element thisOrgElem = (Element) orgIter.next();
                Organization thisOrg = new Organization(thisOrgElem.getAttributeValue("name"), thisOrgElem.getAttributeValue("address"));
                organizations.put(thisOrgElem.getAttributeValue("identifier"), thisOrg);
            }
            Iterator personIter = peopleList.iterator();
            while (personIter.hasNext()) {
                Element thisPersonElem = (Element) personIter.next();
                Person thisPerson = new Person(thisPersonElem.getAttributeValue("firstName"), thisPersonElem.getAttributeValue("lastName"), thisPersonElem.getAttributeValue("email"), thisPersonElem.getAttributeValue("address"));
                people.put(thisPersonElem.getAttributeValue("identifier"), thisPerson);
            }
            List experimentList = root.getChild("Experiment_package").getChild("Experiment_assnlist").getChildren("Experiment");
            Iterator experimentIter = experimentList.iterator();
            int index = 1;
            while (experimentIter.hasNext()) {
                resultText = resultText + "Experiment " + index + "\n";
                index++;
                Element thisExperiment = (Element) experimentIter.next();
                resultText = resultText + "  Name: " + thisExperiment.getAttributeValue("name") + "\n";
                String descrip = thisExperiment.getChild("Descriptions_assnlist").getChild("Description").getAttributeValue("text");
                resultText = resultText + "  Description: " + descrip + "\n";
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        resultArea.setText(resultText);
        f.setVisible(true);
    }

    private static class Person {
        public String fname;
        public String lname;

        public Person(String fname, String lname, String email, String address) {
            this.fname = fname;
            this.lname = lname;
            this.email = email;
            this.address = address;
        }

        public String email;
        public String address;
    }

    private static class Organization {
        public String name;

        public Organization(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public String address;
    }
}
