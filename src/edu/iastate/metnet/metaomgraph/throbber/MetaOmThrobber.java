package edu.iastate.metnet.metaomgraph.throbber;

import edu.iastate.metnet.metaomgraph.ui.MetNetLAF;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;


public class MetaOmThrobber
        extends Throbber {
    private double frame;
    private int frameCount;

    public MetaOmThrobber() {
        frame = 0.0D;
        frameCount = 4;
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("Throbber test");
        f.setDefaultCloseOperation(3);
        final MetaOmThrobber throbber = new MetaOmThrobber();
        f.getContentPane().add(throbber, "Before");

        JButton button = new JButton("Start/Stop");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isAnimating()) {
                    throbber.stop();
                } else {
                    throbber.start();
                }
            }
        });
        f.getContentPane().add(button, "After");
        f.pack();
        f.setVisible(true);
    }

    public int getFrameCount() {
        return 0;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getThrobberWidth() {
        return 20;
    }

    public int getThrobberHeight() {
        return 20;
    }

    protected void drawThrobber(Graphics2D g2d) {
        frame = ((frame + 0.5D) % frameCount);
        g2d.setBackground(getBackground());
        g2d.clearRect(0, 0, getThrobberWidth(), getThrobberHeight());
        for (int x = 0; x < 4; x++) {
            int xpos = 0;
            int ypos = 0;
            int xoffset = 0;
            int yoffset = 0;
            switch (x) {
                case 0:
                    xpos = 5;
                    ypos = 5;
                    if ((frame == 0.0D) && (isAnimating())) {
                        xoffset = -1;
                        yoffset = -1;
                    }
                    break;
                case 1:
                    xpos = 10;
                    ypos = 5;
                    if ((frame == 1.0D) && (isAnimating())) {
                        xoffset = 1;
                        yoffset = -1;
                    }
                    break;
                case 2:
                    xpos = 10;
                    ypos = 10;
                    if ((frame == 2.0D) && (isAnimating())) {
                        xoffset = 1;
                        yoffset = 1;
                    }
                    break;
                case 3:
                    xpos = 5;
                    ypos = 10;
                    if ((frame == 3.0D) && (isAnimating())) {
                        xoffset = -1;
                        yoffset = 1;
                    }
                    break;
            }
            g2d.setPaint(MetNetLAF.MNGreen);
            g2d.fillRect(xpos + xoffset, ypos + yoffset, 4, 4);
            g2d.setPaint(Color.BLACK);
            g2d.drawRect(xpos + xoffset, ypos + yoffset, 4, 4);
        }
    }
}
