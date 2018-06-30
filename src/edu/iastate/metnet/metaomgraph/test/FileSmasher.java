package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

public class FileSmasher {
    public FileSmasher() {
    }

    public static void smash(File file1, String delimiter1, int infoCols1, File file2, String delimiter2, int infoCols2, File destination, String destDelimiter) throws IOException {
        BufferedReader in1 = new BufferedReader(new FileReader(file1));
        BufferedReader in2 = new BufferedReader(new FileReader(file2));
        BufferedWriter out = new BufferedWriter(new FileWriter(destination));
        String readline1 = null;
        String readline2 = null;
        int read1 = 0;
        int read2 = 0;
        while (((readline1 = in1.readLine()) != null) && ((readline2 = in2.readLine()) != null)) {
            read1++;
            read2++;
            String[] line1 = readline1.split(delimiter1);
            String[] line2 = readline2.split(delimiter2);
            if ((line1.length != 0) && (line2.length != 0)) {

                out.write(line1[0]);
                for (int i = 1; i < infoCols1; i++) {
                    out.write(destDelimiter + line1[i]);
                }
                for (int i = 1; i < infoCols2; i++) {
                    out.write(destDelimiter + line2[i]);
                }
                for (int i = infoCols1; i < line1.length; i++) {
                    out.write(destDelimiter + line1[i]);
                }
                for (int i = infoCols1; i < line2.length; i++) {
                    out.write(destDelimiter + line2[i]);
                }
                out.newLine();
            }
        }
        if ((readline1 == null) && (readline2 != null)) {
            System.err.println("File 1 ended first!");
        } else if ((readline1 != null) && (readline2 == null)) {
            System.err.println("File 2 ended first!");
        }
    }


    public static void main(String[] args)
            throws Exception {
        Utils.setLastDir("c:\\documents and settings\\nick\\desktop\\compiled ae data\\6-5-08");

        System.out.println("Starting...");
        File f1 = Utils.chooseFileToOpen();
        if (f1 == null) {
            return;
        }
        File f2 = Utils.chooseFileToOpen();
        if (f2 == null) {
            return;
        }
        File dest = Utils.chooseFileToSave();
        if (dest == null) {
            return;
        }
        long startTime = Calendar.getInstance().getTimeInMillis();
        smash(f1, "\t", 2, f2, "\t", 1, dest, "\t");
        long endTime = Calendar.getInstance().getTimeInMillis();
        System.out.println(endTime - startTime + "ms");
    }
}
