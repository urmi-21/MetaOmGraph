package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.CorrelationCalc;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

public class WiesiaCorrelation {
    static String[] hitlist = {"At3g16950", "At3g25860", "At1g01090",
            "At2g34590", "At1g30120", "At1g34430", "At4g14070", "At3g05020",
            "At4g25050", "At5g27200", "At5g16390", "At5g15530", "At1g52670",
            "At3g15690", "At5g35360", "At2g38040", "At3g25110", "At5g10160",
            "At2g22230", "At5g46290", "At1g74960", "At1g62640", "At2g30200",
            "At2g05990", "At1g10070", "At1g55510", "At1g21400", "At3g06850",
            "At3g13450", "At3g45300", "At1g03090", "At4g34030", "At1g76130",
            "At1g69830", "At5g19220", "At5g48300", "At2g32290", "At4g00490",
            "At5g03650", "At2g36390", "At5g64860", "At2g40840", "At1g10760",
            "At5g26570", "At2g39930", "At1g03310", "At4g09020", "At5g51820",
            "At4g11570", "At1g70730", "At1g70820", "At3g29320", "At3g46970",
            "At5g04360", "At5g24300", "At4g18240", "At1g54350", "At4g36530",
            "At5g46800", "At5g08415", "At5g17520", "At5g46110"};

    public WiesiaCorrelation() {
    }

    public static void main(String[] args)
            throws IOException {
        File infile = Utils.chooseFileToOpen(Utils.createFileFilter("mog",
                "MetaOmGraph Project"), null);
        System.out.print("Loading project... ");
        MetaOmProject project = new MetaOmProject(infile);
        System.out.println("Done!");
        System.out.print("Finding row matches... ");
        int[] hitrows = new int[hitlist.length];
        for (int i = 0; i < hitrows.length; i++) {
            boolean found = false;
            for (int j = 0; (j < project.getRowCount()) && (!found); j++) {
                if ((project.getRowName(j)[0] + "").toLowerCase().contains(
                        hitlist[i].toLowerCase())) {
                    hitrows[i] = j;
                    found = true;
                }
            }
            if (!found) {
                System.err.println("No match found for " + hitlist[i]);
                hitrows[i] = -1;
            }
        }
        System.out.println("Done!");
        System.out.print("Calculating correlations...");
        ArrayList[] result = new ArrayList[hitrows.length];
        for (int i = 0; i < hitrows.length; i++) {
            result[i] = new ArrayList();
            if (hitrows[i] >= 0) {
                CorrelationCalc calcy = new CorrelationCalc(project
                        .getIncludedData(hitrows[i]));
                for (int j = 0; j < project.getRowCount(); j++) {
                    if (hitrows[i] != j) {
                        double corr = calcy.pearsonCorrelation(project.getIncludedData(j), false, new Double(0.0D));

                        if (Math.abs(corr) >= 0.7D) {
                            HitVal thisVal = new HitVal();
                            thisVal.corr = corr;
                            thisVal.id = (project.getRowName(j)[0] + "," + project.getRowName(j)[3]);
                            result[i].add(thisVal);
                        }
                    }
                }
                Collections.sort(result[i]);
            }
            System.out.println("Finished " + hitlist[i] + " [" + (i + 1) + "/" +
                    hitrows.length + "] - " + result[i].size() + " hits");
        }
        System.out.println("Done!");
        System.out.print("Writing results... ");
        File outfile = new File(infile.getParent(), "wiesia.csv");
        BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
        for (int i = 0; i < result.length; i++) {
            out.write(hitlist[i]);
            out.newLine();
            System.out.println("Writing results for " + hitlist[i] + " [" + (i + 1) + "/" + result.length + "]");
            for (int j = 0; j < result[i].size(); j++) {
                HitVal thisVal = (HitVal) result[i].get(j);
                out.write(thisVal.toString());
                out.newLine();
            }
            out.write("-----");
            out.newLine();
        }
        out.close();
        System.out.println("Done!");
    }

    static class HitVal implements Comparable {
        public double corr;
        public String id;

        HitVal() {
        }

        public int compareTo(Object o) {
            if (!(o instanceof HitVal)) {
                return -1;
            }
            return -Double.compare(corr, corr);
        }

        public String toString() {
            return id + "," + corr;
        }
    }
}
