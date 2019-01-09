package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ColorChooseButton
        extends JButton {
    private Color myColor;
    private Color highlight;
    private Color shadow;
    private String myTitle;
    private LinkedList changeListeners;

    public ColorChooseButton(Color color, String title) {
        myColor = color;
        myTitle = title;
        highlight = myColor.brighter();
        shadow = myColor.darker();
        setPreferredSize(new Dimension(16, 16));
        changeListeners = null;
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Color newColor = JColorChooser.showDialog(null, myTitle,
                        myColor);
                if (newColor != null) {

                    myColor = newColor;
                    highlight = myColor.brighter();
                    shadow = myColor.darker();
                    repaint(0, 0, getWidth(), getHeight());

                    if (changeListeners != null) {
                        ChangeEvent ce = new ChangeEvent(this);
                        for (int x = 0; x < changeListeners.size(); x++) {
                            ((ChangeListener) changeListeners.get(x)).stateChanged(ce);
                        }
                    }
                }
            }
        });
    }


    public Color getColor() {
        return myColor;
    }
    
    public void setColor(Color newColor) {
        myColor=newColor;
    }


    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setPaint(myColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());


        if (!getModel().isPressed()) {
            g2d.setPaint(highlight);
        } else
            g2d.setPaint(shadow);
        g2d.drawLine(0, 0, 0, getHeight());
        g2d.drawLine(0, 0, getWidth(), 0);


        if (!getModel().isPressed()) {
            g2d.setPaint(shadow);
        } else
            g2d.setPaint(highlight);
        g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);
        g2d.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
        g2d.dispose();
    }


    public void addChangeListener(ChangeListener myChangeListener) {
        if (changeListeners == null)
            changeListeners = new LinkedList();
        changeListeners.add(myChangeListener);
    }
}
