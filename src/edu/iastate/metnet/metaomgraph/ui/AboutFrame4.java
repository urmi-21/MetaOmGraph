package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.test.MetaOmIconPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutFrame4
        extends MetaOmIconPanel {
    public AboutFrame4() {
        setLayout(new BoxLayout(this, 1));
        add(Box.createVerticalGlue());
        JPanel shadowPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(
                        10, 0.7F));
                super.paintComponent(g2d);
            }
        };
        shadowPanel.setBackground(Color.BLACK);
        shadowPanel.setFocusable(true);
        shadowPanel.addKeyListener(new KeyAdapter() {
            private StringBuffer buffer;

            private String password = "lessmagic";

            public void keyTyped(KeyEvent e) {
                if (buffer == null) {
                    buffer = new StringBuffer();
                }
                buffer.append(e.getKeyChar());
                if (buffer.toString().endsWith(password)) {
                    throw new NullPointerException("This is a test exception");
                }
                if (!password.contains(e.getKeyChar() + "")) {
                    buffer = null;
                } else if (buffer.length() > password.length()) {
                    buffer = new StringBuffer(buffer.substring(1));
                }

            }


        });
        JLabel myLabel = new JLabel(AboutFrame.getLabelText());
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

        this.setPreferredSize(new Dimension(
                shadowPanel.getPreferredSize().width + 100, shadowPanel
                .getPreferredSize().height + 100));
        shadowPanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("About test");
        f.getContentPane().add(new AboutFrame4(), "Center");
        f.pack();
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
