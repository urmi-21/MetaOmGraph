package edu.iastate.metnet.metaomgraph.test;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import javax.swing.Timer;


public class UpdatingTextArea
        extends JTextArea {
    private Timer t;
    private BufferedImage[] animation;
    private int width;
    private int height;
    private int frame;
    private BufferedImage thisFrame;
    private boolean animating;
    private String cachedText;

    public UpdatingTextArea()
            throws IOException {
        int rows = 4;
        int cols = 8;
        BufferedImage source = ImageIO.read(getClass().getResourceAsStream(
                "/resource/tango/32x32/animations/process-working.png"));
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
            @Override
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

    @Override
	protected void paintComponent(Graphics g) {
        if (!animating) {
            super.paintComponent(g);
            return;
        }
        BufferedImage nextImage = new BufferedImage(getWidth(), getHeight(), 1);
        Graphics2D g2d = (Graphics2D) nextImage.getGraphics();

        g2d.setBackground(getBackground());
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.drawImage(thisFrame, null, (getWidth() - width) / 2,
                (getHeight() - height) / 2);
        g2d.dispose();
        g.drawImage(nextImage, 0, 0, null);
    }

    public void stop() {
        animating = false;
        t.stop();
        if (cachedText != null) {
            setText(cachedText);
        }
    }

    public void start() {
        animating = true;
        String oldText = getText();
        setText("");
        cachedText = oldText;
        t.start();
    }

    @Override
	public void setText(String t) {
        super.setText(t);
        cachedText = null;
    }
}
