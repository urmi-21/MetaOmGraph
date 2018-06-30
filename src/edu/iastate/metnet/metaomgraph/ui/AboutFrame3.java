package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.test.MetaOmIconPanel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class AboutFrame3
        extends JPanel {
    public AboutFrame3() {
        setBackground(Color.WHITE);
        MetaOmIconPanel icon = new MetaOmIconPanel();
        icon.setPreferredSize(new Dimension(200, 200));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 4;
        c.anchor = 13;
        c.fill = 0;
        add(icon, c);
        c.gridx = 0;
        c.gridy += 1;
        c.gridheight = 1;
        c.anchor = 17;
        JLabel name = new JLabel("MetaOmGraph");
        name.setFont(name.getFont().deriveFont(20.0F));
        add(name, c);
        JLabel version = new JLabel("Version 1.1.3");
        version.setFont(name.getFont().deriveFont(10.0F));
        version.setForeground(Color.DARK_GRAY);
        c.gridy += 1;
        add(version, c);
        JLabel date = new JLabel("August 7, 2008");
        date.setFont(version.getFont());
        date.setForeground(version.getForeground());
        c.gridy += 1;
        add(date, c);
        JLabel myLabel = new JLabel("<html><br>Wurtele Lab, GDCB, Iowa State University<br>Send questions, comments, bug reports, and feature requests to:<br>mhhur@iastate.edu<br><br>For more information on the MetNet project, please visit<br>http://metnetdb.org/<br><br>This program uses code from the following projects:<br>BrowserLauncher2 (http://sourceforge.net/projects/browserlaunch2/)<br>Jakarta POI (http://jakarta.apache.org/poi/)<br>JDOM (http://www.jdom.org)<br>JFreeChart (http://www.jfree.org/jfreechart/)<br>L2FProd Common Components (http://common.l2fprod.com/)<br><br>Biologists: Eve Wurtele, Wieslawa Mentzen, Ling Li, Jianling Peng<br>Programmer: Nick Ransom</html>");


        c.gridy += 1;
        add(myLabel, c);
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("About test");
        f.getContentPane().add(new AboutFrame3(), "Center");
        f.pack();
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
