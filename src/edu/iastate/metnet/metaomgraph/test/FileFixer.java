package edu.iastate.metnet.metaomgraph.test;

import java.io.File;
import java.io.PrintStream;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class FileFixer {
    public FileFixer() {
    }

    public static void main(String[] args)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(null);
        File infile = fc.getSelectedFile();
        String fileName = infile.getName();
        System.out.println("file name=[" + fileName + "]");
        System.out.println(infile.exists());
        fileName.trim();
        infile.renameTo(new File(fileName + ".fixed"));
    }
}
