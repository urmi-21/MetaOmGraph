package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.jdom.input.SAXBuilder;


public class AEExperimentLister {
    public AEExperimentLister() {
    }

    public static void main(String[] args)
            throws Exception {
        int people = 6;
        String site = "http://www.ebi.ac.uk/microarray-as/aer/jsp/ae_expts.jsp?page-size=500&sort_by=releasedate&sort_order=descending&page-number=1&organism=&array=13851999&keyword=";
        File infile = Utils.chooseFileToOpen();
        new SAXBuilder().build(infile);
        BufferedWriter out = new BufferedWriter(new FileWriter(new File("test")));
    }
}
