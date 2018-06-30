package edu.iastate.metnet.metaomgraph.metabolomics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

public class MPMRProjectMaker {

    public static void main(String[] args) throws Exception {
        String connString = "jdbc:mysql://localhost/new_mpmr";
        try {
            org.gjt.mm.mysql.Driver.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(connString,"guest", "");
        Statement statement = connection.createStatement();
        String sql = "select e.experiment_id, e.ordered, sp.name "
                + "from experiments e, species sp "
                + "where sp.species_id=e.species_id " + "order by sp.name";
        ResultSet rs = statement.executeQuery(sql);
        TreeMap<String, Integer> speciesExpMap = new TreeMap<String, Integer>();
        TreeMap<String, Boolean> speciesOrderedMap = new TreeMap<String, Boolean>();
        while (rs.next()) {
            String speciesName = rs.getString("name");
            Integer id = rs.getInt("experiment_id");
            if (!speciesExpMap.containsKey(speciesName)) {
                speciesExpMap.put(speciesName, id);
                speciesOrderedMap.put(speciesName, rs.getBoolean("ordered"));
            }
        }
        DefaultListModel listModel = new DefaultListModel();
        Set<String> keySet = speciesExpMap.keySet();
        for (String speciesName : keySet) {
            listModel.addElement(speciesName);
        }
        JList speciesList = new JList(listModel);
        int result = JOptionPane.showConfirmDialog(null, speciesList, "Create MPMR Project", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION || speciesList.getSelectedIndex() < 0) {
            System.exit(0);
        }
        String selectedSpecies = speciesList.getSelectedValue().toString();
        Integer selectedExp = speciesExpMap.get(selectedSpecies);
        boolean ordered = speciesOrderedMap.get(selectedSpecies);
        System.out.println("Ordered: " + ordered);
        System.out.println("Building experiment " + selectedExp + " (" + selectedSpecies + ")");

        // Build data file
        File dest = new File("/Users/mhhur/Desktop/mpmr-mog-data.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(dest));
        out.write("Metabolite ID\tMetabolite Name");
        sql = "select sample_id, name, reps from samples where experiment_id=" + selectedExp;
        if (!ordered) {
            sql += " order by name";
        }
        System.out.println(sql);
        rs = statement.executeQuery(sql);
        ArrayList<String> sampleList = new ArrayList<String>();
        while (rs.next()) {
            String sampleName = rs.getString("name");
            Integer reps = rs.getInt("reps");
            for (int i = 1; i <= reps; i++) {
                String thisSampleName = getFullSampleName(sampleName, i);
                out.write("\t" + thisSampleName);
                sampleList.add(thisSampleName);
            }
        }
        out.newLine();
        sql = "select md.sample_id, s.name as sampleName, md.metabolite_id, m.name as metabName, m2.name as predName, md.rep, md.value "
                + "from (metabolite_data_full md, metabolites m, samples s) "
                + "left join metabolites m2 on m2.metabolite_id=m.predicted_metabolite_id "
                + "where m.metabolite_id=md.metabolite_id and s.sample_id=md.sample_id and md.experiment_id="
                + selectedExp;
        if (!ordered) {
            sql += " order by metabolite_id, sampleName, rep";
        } else {
            sql += " order by metabolite_id, sample_id, rep";
        }
        System.out.println(sql);
        rs = statement.executeQuery(sql);
        rs.first();
        int currentMetabID;
        String currentMetabName, currentPredName;
        HashMap<String, Double> sampleValueMap;
        ArrayList<String> metabList = new ArrayList<String>();
        currentMetabID = rs.getInt("metabolite_id");
        currentMetabName = rs.getString("metabName");
        currentPredName = rs.getString("predName");
        sampleValueMap = new HashMap<String, Double>();
        rs.beforeFirst();
        while (rs.next()) {
            if (rs.getInt("metabolite_id") != currentMetabID) {
                // Finished with the current metabolite, output it and reset the
                // variables
                System.out.println("Finished " + currentMetabName);
                metabList.add(currentMetabName);
                out.write(currentMetabName + "\t");
                if (currentPredName != null) {
                    out.write(currentPredName);
                }
                for (String sampleName : sampleList) {
                    // Print the value for each sample in order, or a blank if
                    // no value was found
                    Double value = sampleValueMap.get(sampleName);
                    out.write("\t");
                    if (value != null) {
                        out.write(value.toString());
                    }
                }
                out.newLine();
                currentMetabID = rs.getInt("metabolite_id");
                currentMetabName = rs.getString("metabName");
                currentPredName = rs.getString("predName");
                sampleValueMap = new HashMap<String, Double>();
            }
            String thisSampleName = getFullSampleName(
                    rs.getString("sampleName"), rs.getInt("rep"));
            Double value = rs.getDouble("value");
            if (!rs.wasNull()) {
                if (sampleValueMap.containsKey(thisSampleName)) {
                    System.out.println("  WARNING: Duplicate value for: " + thisSampleName + " in " + currentMetabName);
                }
                sampleValueMap.put(thisSampleName, value);
            }
        }
        // Output the last metabolite
        System.out.println("Finished " + currentMetabName);
        out.write(currentMetabName + "\t");
        if (currentPredName != null) {
            out.write(currentPredName);
        }
        for (String sampleName : sampleList) {
            Double value = sampleValueMap.get(sampleName);
            out.write("\t");
            if (value != null) {
                out.write(value.toString());
            }
        }
        out.newLine();
        out.close();
        System.out.println("Done with data file!");
        SimpleXMLElement metadataRoot = new SimpleXMLElement("Experiments");
        // Future metadata format below

        SimpleXMLElement experimentElement = new SimpleXMLElement("group").setAttribute("name", "MPMR: " + selectedSpecies);

        // Get the experiment metadata
        sql = "select em.metadata_field, em.metadata_value, mc.name "
                + "from experiment_metadata em, metadata_categories mc "
                + "where em.experiment_id=" + selectedExp
                + " and mc.category_id=em.category_id"
                + " order by em.category_id";
        rs = statement.executeQuery(sql);

        while (rs.next()) {
            String field = rs.getString("metadata_field");
            String value = rs.getString("metadata_value");
            SimpleXMLElement thisElement = new SimpleXMLElement("md")
                    .setAttribute("field", field).setAttribute("value", value);
            String thisCategory = rs.getString("name");
            if (thisCategory != null) {
                thisElement.setAttribute("cat", thisCategory);
            }
            experimentElement.add(thisElement);
        }

		/*
         * SimpleXMLElement experimentElement = new
		 * SimpleXMLElement("Experiment") .setAttribute("name", "MPMR: " +
		 * selectedSpecies); sql =
		 * "select * from experiment_metadata where experiment_id=" +
		 * selectedExp; rs = statement.executeQuery(sql); while (rs.next()) {
		 * String field = JDomUtils.convertToValidElementName(rs
		 * .getString("metadata_field")); String value =
		 * rs.getString("metadata_value"); experimentElement.add(new
		 * SimpleXMLElement(field).setText(value)); }
		 */

        // Get the sample metadata
        sql = "select s.sample_id, s.name, sm.metadata_field, sm.metadata_value, sm.rep "
                + "from samples s, sample_metadata sm "
                + "where sm.sample_id=s.sample_id and s.experiment_id="
                + selectedExp + " order by sm.sample_id, sm.rep";
        System.out.println(sql);
        rs = statement.executeQuery(sql);
        int lastSample = -1;
        int lastRep = 0;
        SimpleXMLElement sampleElement = null, repElement = null;
        while (rs.next()) {
            String field = rs.getString("metadata_field");
            String value = rs.getString("metadata_value");
            int thisSample = rs.getInt("sample_id");
            int thisRep = rs.getInt("rep");
            String sampleName = rs.getString("name");
            if (thisSample != lastSample) {
                if (sampleElement != null) {
                    if (repElement != null) {
                        sampleElement.add(repElement);
                    }
                    repElement = null;
                    experimentElement.add(sampleElement);
                }
                sampleElement = new SimpleXMLElement("Sample").setAttribute(
                        "name", sampleName);
                lastSample = thisSample;
                lastRep = 0;
            }
            if (thisRep != lastRep) {
                if (repElement != null) {
                    sampleElement.add(repElement);
                }
                if (thisRep != 0) {
                    String fullName = getFullSampleName(sampleName, thisRep);
                    repElement = new SimpleXMLElement("Rep" + thisRep).add(new SimpleXMLElement("md").setAttribute(
                                    "field", "Sample Name").setAttribute(
                                    "value", fullName));
                    System.out.println("Doing metadata for " + fullName);
                    int col = sampleList.indexOf(fullName);
                    if (col >= 0) {
                        repElement.setAttribute("col", col + "");
                    }
                } else {
                    System.out.println(sampleName + " Rep 0");
                    repElement = null;
                }
                lastRep = thisRep;
            }
            // SimpleXMLElement addMe = new
            // SimpleXMLElement(field).setText(value);
            SimpleXMLElement addMe = new SimpleXMLElement("md").setAttribute(
                    "field", field).setAttribute("value", value);
            if (thisRep == 0) {
                sampleElement.add(addMe);
            } else {
                repElement.add(addMe);
            }
        }
        if (sampleElement != null) {
            if (repElement != null) {
                sampleElement.add(repElement);
            }
            experimentElement.add(sampleElement);
        }
        metadataRoot.add(experimentElement);
        dest = new File("/Users/mhhur/Desktop/mpmr-mog-metadata-newstyle.xml");
        out = new BufferedWriter(new FileWriter(dest));
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.newLine();
        out.write(metadataRoot.toFullString());
        out.close();
        System.out.println("Done with metadata!");
        System.exit(0);
    }

    public static void zipTest() {
        File testFile = new File("/Users/mhhur/Desktop/MyZipFile.zip");
        if (testFile.exists()) {
            testFile.delete();
        }
        // ZipOutputStream out=new ZipOutputStream()
    }

    public static String getFullSampleName(String sampleName, int rep) {
        return sampleName + " - Rep " + rep;
    }

    public static Statement connectToMPMRDB() throws SQLException {
        String connString = "jdbc:mysql://localhost/metnet2";
        try {
            org.gjt.mm.mysql.Driver.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(connString,
                "guest", "");
        return connection.createStatement();
    }
}
