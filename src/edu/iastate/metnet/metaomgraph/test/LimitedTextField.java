package edu.iastate.metnet.metaomgraph.test;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class LimitedTextField extends JTextField {
    public LimitedTextField() {
    }

    @Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(java.awt.AlphaComposite.getInstance(10,
                0.5F));
        g2d.setColor(java.awt.Color.RED);
        g2d.fillRect(getWidth() - 16, 0, 16, getHeight());
        g2d.dispose();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Text Field Test");
        LimitedTextField field = new LimitedTextField();
        f.getContentPane().add(field);
        f.pack();
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
