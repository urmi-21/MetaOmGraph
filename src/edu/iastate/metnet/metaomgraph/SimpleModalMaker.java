package edu.iastate.metnet.metaomgraph;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JFrame;


public class SimpleModalMaker
        extends WindowAdapter {
    private Vector<Window> myDialogs;

    public SimpleModalMaker() {
        myDialogs = new Vector();
    }

    public void windowOpened(WindowEvent e) {
        System.out.println("open");
        MetaOmGraph.getMainWindow().setEnabled(false);
        myDialogs.add(e.getWindow());
    }

    public void windowClosed(WindowEvent e) {
        System.out.println("closed");
        myDialogs.remove(e.getWindow());
        if (myDialogs.size() == 0) {
            MetaOmGraph.getMainWindow().setEnabled(true);
            MetaOmGraph.getMainWindow().toFront();
        }
    }

    public void windowClosing(WindowEvent e) {
        System.out.println("closing");
        myDialogs.remove(e.getWindow());
        if (myDialogs.size() == 0) {
            MetaOmGraph.getMainWindow().setEnabled(true);
            MetaOmGraph.getMainWindow().toFront();
        }
    }
}
