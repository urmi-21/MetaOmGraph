package edu.iastate.metnet.metaomgraph.test;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;


public class UpdatingComponent
        extends JPanel {
    private Timer t;
    private BufferedImage[] animation;
    private int width;
    private int height;
    private int frame;
    private BufferedImage thisFrame;
    private boolean animating;
    private JComponent myComponent;

    public UpdatingComponent(JComponent wrapMe) {
        myComponent = wrapMe;
        add(myComponent);
        int rows = 4;
        int cols = 8;
        BufferedImage source;
        try {
            source = ImageIO.read(getClass().getResourceAsStream(
                    "/resource/tango/32x32/animations/process-working.png"));
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        frame = 0;
        animation = new BufferedImage[rows * cols];
        int frameIndex = 0;
        width = (source.getWidth() / cols);
        height = (source.getHeight() / rows);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                animation[(frameIndex++)] = source.getSubimage(col * width, row *
                        height, width, height);
            }
        }
        thisFrame = animation[0];
        t = new Timer(25, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!animating) {
                    t.stop();
                    return;
                }
                frame = ((frame + 1) % animation.length);
                if (frame == 0) {
                    frame = 1;
                }
                thisFrame = animation[frame];
                repaint();
            }
        });
        animating = false;
    }

    protected void paintComponent(Graphics g) {
        if (!animating) {
            super.paintComponent(g);
            return;
        }
        BufferedImage nextImage = new BufferedImage(getWidth(), getHeight(), 1);
        Graphics2D g2d = (Graphics2D) nextImage.getGraphics();

        Rectangle rect = getVisibleRect();
        g2d.setBackground(getBackground());

        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.drawImage(thisFrame, null, ((int) rect.getWidth() - width) / 2,
                ((int) rect.getHeight() - height) / 2);
        g2d.dispose();
        g.drawImage(nextImage, 0, 0, null);
    }

    public void stop() {
        animating = false;
        t.stop();
        add(myComponent);
        setPreferredSize(null);
        repaint();
        revalidate();
    }

    public void start() {
        animating = true;
        remove(myComponent);
        t.start();
        setPreferredSize(getVisibleRect().getSize());
        revalidate();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame f = new JFrame("Updating component test");


        UpdatingComponent updater = new UpdatingComponent(new JLabel("Done!"));
        f.getContentPane().add(updater);
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        updater.start();
        f.setVisible(true);
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updater.stop();
    }
}
