package edu.iastate.metnet.metaomgraph.utils.qdxml;

import java.io.FileReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;


public class Reporter
        implements DocHandler {
    static Reporter reporter = new Reporter();

    public Reporter() {
    }

    public void startDocument() {
        System.out.println("  start document");
    }


    public void endDocument() {
        System.out.println("  end document");
    }

    public void startElement(String elem, Hashtable h) {
        System.out.println("    start elem: " + elem);
        Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String val = (String) h.get(key);
            System.out.println("      " + key + " = " + val);
        }
    }

    public void endElement(String elem) {
        System.out.println("    end elem: " + elem);
    }

    public void text(String text) {
        System.out.println("        text: " + text);
    }

    public static void main(String[] args)
            throws Exception {
        for (int i = 0; i < args.length; i++)
            reportOnFile(args[0]);
    }

    public static void reportOnFile(String file) throws Exception {
        System.out.println("===============================");
        System.out.println("file: " + file);


        FileReader fr = new FileReader(file);
        QDParser.parse(reporter, fr);

        fr.close();
    }
}
