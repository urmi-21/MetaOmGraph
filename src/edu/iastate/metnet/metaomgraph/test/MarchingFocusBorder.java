package edu.iastate.metnet.metaomgraph.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;


public class MarchingFocusBorder
        implements Border, ActionListener, FocusListener {
    private BasicStroke borderStroke;
    private Component myComponent;
    private Timer t;
    private int phase;
    public static final String MARCH_COMMAND = "march";

    public MarchingFocusBorder(Component c) {
        myComponent = c;
        myComponent.addFocusListener(this);
        phase = 0;
        borderStroke = new BasicStroke(1.0F, 0,
                2, 10.0F, new float[]{3.0F, 3.0F}, phase);
        t = new Timer(100, this);

        if (c.hasFocus()) {
            t.start();
            march();
        } else {
            halt();
        }
    }

    @Override
	public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
    }

    @Override
	public boolean isBorderOpaque() {
        return false;
    }

    @Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(borderStroke);
        if (myComponent.hasFocus()) {
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width - 1, height - 1);
        }
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if ("march".equals(e.getActionCommand())) {
            march();
        }
    }

    @Override
	public void focusGained(FocusEvent e) {
        t.start();
        march();
    }

    @Override
	public void focusLost(FocusEvent e) {
        halt();
    }

    private void march() {
        if (++phase % 6 == 0) {
            phase = 0;
        }
        borderStroke = new BasicStroke(1.0F, 0,
                2, 10.0F, new float[]{3.0F, 3.0F}, phase);
        myComponent.repaint();
    }

    private void halt() {
        t.stop();
        borderStroke = new BasicStroke(1.0F);
        phase = 0;
        myComponent.repaint();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Testing border");
        JTextField testfield = new JTextField("Testing");
        testfield.setBorder(new MarchingFocusBorder(testfield));
        testfield.setMaximumSize(new Dimension(70, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton("OK"));
        buttonPanel.add(new JButton("Cancel"));
        f.getContentPane().add(testfield, "Center");
        f.getContentPane().add(buttonPanel, "South");
        f.setSize(400, 400);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
