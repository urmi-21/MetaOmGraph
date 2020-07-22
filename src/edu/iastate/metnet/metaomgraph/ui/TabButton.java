package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class TabButton extends JButton implements ActionListener {
	
	JPanel panel;
	JTabbedPane pane;
    public TabButton(JPanel panel, JTabbedPane pane) {
//        int size = 17;
//        setPreferredSize(new Dimension(size, size));
    	this.pane = pane;
    	this.panel = panel;
        setToolTipText("close this tab");
        //Make the button looks the same for all Laf's
        setUI(new BasicButtonUI());
        //Make it transparent
        setContentAreaFilled(false);
        //No need to be focusable
        setFocusable(false);
        setBorder(BorderFactory.createEtchedBorder());
        setBorderPainted(false);
        //Making nice rollover effect
        //we use the same listener for all buttons
        //addMouseListener(buttonMouseListener);
        setRolloverEnabled(true);
        //Close the proper tab by clicking the button
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        int i = pane.indexOfTabComponent(panel);
        if (i != -1) {
            pane.remove(i);
        }
    }

    //we don't want to update UI for this button
    public void updateUI() {
    }

    //paint the cross
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        //shift the image for pressed buttons
        if (getModel().isPressed()) {
            g2.translate(1, 1);
        }
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        if (getModel().isRollover()) {
            g2.setColor(Color.MAGENTA);
        }
        int delta = 6;
        g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
        g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
        g2.dispose();
    }
}