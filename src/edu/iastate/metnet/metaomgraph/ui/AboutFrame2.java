package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.test.MetaOmIconPanel;
import edu.iastate.metnet.metaomgraph.utils.ProgressiveLabel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class AboutFrame2
        extends MetaOmIconPanel {
    public AboutFrame2() {
        setLayout(new BoxLayout(this, 1));
        add(Box.createVerticalGlue());
        JPanel shadowPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(
                        10, 0.5F));
                super.paintComponent(g2d);
            }
        };
        shadowPanel.setBackground(Color.BLACK);
        JLabel myLabel = new ProgressiveLabel(
                "<html><p align=\"center\"><b>MetaOmGraph v1.1<br>This version built on June 12, 2008<br>Wurtele Lab, GDCB, Iowa State University<br>Send questions, comments, bug reports, and feature requests to:<br>mhhur@iastate.edu<br><br>For more information on the MetNet project, please visit<br>http://metnetdb.org/<br><br>This program uses code from the following projects:<br>BrowserLauncher2 (http://sourceforge.net/projects/browserlaunch2/)<br>Jakarta POI (http://jakarta.apache.org/poi/)<br>JDOM (http://www.jdom.org)<br>JFreeChart (http://www.jfree.org/jfreechart/)<br>L2FProd Common Components (http://common.l2fprod.com/)<br><br>Biologists: Eve Wurtele, Wieslawa Mentzen, Ling Li, Jianling Peng<br>Programmer: Nick Ransom</b></p></html>") {


            protected void paintComponent(Graphics g) {


                super.paintComponent(g);
            }
        };
        myLabel.setHorizontalTextPosition(0);
        myLabel.setForeground(Color.WHITE);
        shadowPanel.add(myLabel);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, 0));

        centerPanel.add(Box.createHorizontalGlue());
        centerPanel.add(shadowPanel);
        centerPanel.add(Box.createHorizontalGlue());
        centerPanel.setOpaque(false);
        add(centerPanel);

        add(Box.createVerticalGlue());
        setOpaque(true);
        setBackground(Color.red);
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("About test");
        f.getContentPane().add(new AboutFrame2(), "Center");
        f.pack();
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
