package edu.iastate.metnet.metaomgraph.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;


public class ProgressiveLabel
        extends JLabel
        implements ActionListener {
    private Timer t;
    private int revealed;
    private ProgressiveLabel myself;
    private int lineCount;
    private int lineHeight;
    private float alpha;

    public ProgressiveLabel(String text) {
        super(text);
        revealed = 0;
        addComponentListener(new VisibilityListener());
        myself = this;
        if (text.startsWith("<html>")) {
            lineCount = text.split("<br>").length;
        } else {
            lineCount = text.split("\n").length;
        }
        System.out.println(lineCount + " lines");
        lineHeight = getFontMetrics(getFont()).getHeight();
        alpha = 0.0F;
        setOpaque(false);
    }


    protected void paintComponent(Graphics g) {
        if (revealed < lineCount) {
            if (!t.isRunning()) {
                System.out.println("Premature stoppage!");
            }
            Graphics2D g2d = (Graphics2D) g.create();
            Rectangle clipRect = new Rectangle(0, 0, getWidth(), revealed * lineHeight);


            g2d.setClip(clipRect);
            super.paintComponent(g2d);
            g2d.setComposite(AlphaComposite.getInstance(
                    10, alpha));
            g2d.setClip(0, revealed * lineHeight, getWidth(), lineHeight);
            super.paintComponent(g2d);
            alpha = ((float) (alpha + 0.05D));
            if (alpha >= 1.0D) {
                alpha = 0.0F;
                revealed += 1;
                if (revealed == lineCount) {
                    t.stop();
                }
            }
            g2d.dispose();
        } else {
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        ProgressiveLabel label = new ProgressiveLabel(
                "<html>line<br>line2<br>line3<br>line4</html>");
        JFrame f = new JFrame();
        f.getContentPane().add(label, "Center");
        f.getContentPane().add(new TestLabel("Hey"), "South");
        f.pack();
        f.setDefaultCloseOperation(3);
        f.setBackground(Color.BLUE);
        f.setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {
        paintImmediately(0, 0, myself.getWidth(), getHeight());
    }

    private class VisibilityListener implements ComponentListener {
        private VisibilityListener() {
        }

        public void componentHidden(ComponentEvent e) {
            System.out.println("Hidden!");
            t.stop();
            t = null;
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentResized(ComponentEvent e) {
            if (t == null) {
                t = new Timer(50, myself);
                t.start();
            }
        }

        public void componentShown(ComponentEvent e) {
        }
    }

    private static class TestLabel extends JLabel {
        public TestLabel(String text) {
            super();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(
                    10, 0.5F));
            super.paintComponent(g2d);
        }
    }
}
