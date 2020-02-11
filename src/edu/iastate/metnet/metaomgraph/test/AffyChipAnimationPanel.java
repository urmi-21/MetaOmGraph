package edu.iastate.metnet.metaomgraph.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AffyChipAnimationPanel
        extends JPanel implements ActionListener {
    int row = 0;

    BufferedImage offscreen;

    public AffyChipAnimationPanel() {
    }

    @Override
	protected void paintComponent(Graphics g) {
        if ((offscreen == null) || (offscreen.getWidth() != getWidth()) ||
                (offscreen.getHeight() != getHeight())) {
            offscreen = new BufferedImage(getWidth(), getHeight(),
                    2);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        Graphics2D g2d = (Graphics2D) offscreen.getGraphics().create();
        for (int x = 0; x < getWidth(); x++) {
            boolean aberration = Math.random() > 0.95D;
            Color myColor;
            if (aberration) {
                myColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
            } else {
                int blueVal = (int) (Math.random() * 255.0D);
                myColor = new Color(0, 0, blueVal);
            }
            offscreen.setRGB(x, row, myColor.getRGB());
        }

        row += 1;
        if (row >= getHeight()) {
            row = 0;
        }
        Color myWhite = new Color(1.0F, 1.0F, 1.0F, 0.5F);
        g2d.setColor(myWhite);
        g2d.drawLine(0, row, getWidth(), row);
        myWhite = new Color(1.0F, 1.0F, 1.0F, 0.25F);

        g.drawImage(offscreen, 0, 0, null);
        g2d.dispose();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("AffyChip animation test");
        AffyChipAnimationPanel acap = new AffyChipAnimationPanel();
        f.getContentPane().add(acap, "Center");
        Timer t = new Timer(20, acap);
        t.setRepeats(true);
        f.setSize(300, 200);
        f.setDefaultCloseOperation(3);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        t.start();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
