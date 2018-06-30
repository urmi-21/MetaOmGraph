package edu.iastate.metnet.metaomgraph.throbber;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class MultiFrameImageThrobber extends Throbber {
    private BufferedImage[] animation;
    private int width;
    private int height;
    private int frame;

    public MultiFrameImageThrobber(BufferedImage source, int rows, int cols) {
        animation = new BufferedImage[rows * cols];
        int frameIndex = 0;
        width = (source.getWidth() / cols);
        height = (source.getHeight() / rows);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                animation[(frameIndex++)] = source.getSubimage(col * width, row * height, width, height);
            }
        }
    }

    protected void drawThrobber(Graphics2D g2d) {
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getThrobberWidth(), getThrobberHeight());
        if (isAnimating()) {
            g2d.drawImage(animation[frame], 0, 0, getThrobberWidth(), getThrobberHeight(), null);
            frame = ((frame + 1) % animation.length);
            if (frame == 0) {
                frame = 1;
            }
        } else {
            frame = 1;
            g2d.drawImage(animation[0], 0, 0, getThrobberWidth(), getThrobberHeight(), null);
        }
    }

    public int getThrobberHeight() {
        return height;
    }

    public int getThrobberWidth() {
        return width;
    }

    public static void main(String[] args) throws IOException {
        JFrame f = new JFrame("test");
        BufferedImage source = ImageIO.read(f.getClass().getResourceAsStream("/resource/tango/16x16/animations/process-working.png"));
        final MultiFrameImageThrobber throbber = new MultiFrameImageThrobber(source, 4, 8);
        f.getContentPane().add(throbber);
        f.pack();
        f.setDefaultCloseOperation(3);
        f.getContentPane().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (isAnimating()) {
                    throbber.stop();
                } else {
                    throbber.start();
                }
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
