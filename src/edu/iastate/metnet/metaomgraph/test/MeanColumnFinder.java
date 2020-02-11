package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.UIManager;

public class MeanColumnFinder {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        File source = Utils.chooseFileToOpen();
        if (source == null) return;
        BufferedReader in = new BufferedReader(new FileReader(source));
        String thisLine = in.readLine();
        String[] headers = thisLine.split("\t");
        int rows = 0;
        Double[] sums = new Double[headers.length - 1];
        for (int i = 0; i < sums.length; sums[i++] = 0.0) ;
        while ((thisLine = in.readLine()) != null) {
            rows++;
            String[] splitLine = thisLine.split("\t");
            for (int i = 1; i < headers.length; i++) {
                sums[i - 1] += Double.parseDouble(splitLine[i]);
            }
        }
        in.close();
        Double aveSum = 0.0;
        for (int i = 1; i < headers.length; i++) {
            Double ave = sums[i - 1] / rows;
            aveSum += ave;
            System.out.println(headers[i] + "=" + ave);
        }
        System.out.println("Average=" + (aveSum / sums.length));
    }

}
