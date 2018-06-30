package edu.iastate.metnet.metaomgraph.throbber;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ImageThrobber
        extends Throbber {
    private BufferedImage[] frames;
    private int frame;
    private int width;
    private int height;

    public ImageThrobber(File[] files) throws IOException {
        frame = 0;
        frames = new BufferedImage[files.length];
        width = 0;
        height = 0;
        for (int x = 0; x < files.length; x++) {
            frames[x] = ImageIO.read(files[x]);
            if (frames[x].getWidth() > width) {
                width = frames[x].getWidth();
            }
            if (frames[x].getHeight() > height) {
                height = frames[x].getHeight();
            }
        }
    }

    public int getThrobberWidth() {
        return width;
    }

    public int getThrobberHeight() {
        return height;
    }

    protected void drawThrobber(Graphics2D g2d) {
        g2d.drawImage(frames[frame], 0, 0, getThrobberWidth(), getThrobberHeight(), null);
        frame = ((frame + 1) % frames.length);
    }

    public static void main(String[] args) throws IOException {
        JFrame f = new JFrame("test");
        File[] frames = new File[4];
        frames[0] = new File("z:\\stickman\\1.GIF");
        frames[1] = new File("z:\\stickman\\2.GIF");
        frames[2] = new File("z:\\stickman\\3.GIF");
        frames[3] = new File("z:\\stickman\\4.GIF");
        final ImageThrobber throbber = new ImageThrobber(frames);
        f.getContentPane().add(throbber);
        f.pack();
        f.setDefaultCloseOperation(3);
        f.getContentPane().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (isAnimating()) {
                    throbber.stop();
                } else
                    throbber.start();

            }


            public void mousePressed(MouseEvent e) {
            }


            public void mouseReleased(MouseEvent e) {
            }


            public void mouseEntered(MouseEvent e) {
            }


            public void mouseExited(MouseEvent e) {
            }
        });
        f.setVisible(true);
    }
}
