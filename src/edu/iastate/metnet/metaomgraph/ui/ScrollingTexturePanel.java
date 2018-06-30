package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.SwingWorker;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;


public class ScrollingTexturePanel
        extends JPanel {
    private BufferedImage originalImage;
    private BufferedImage coloredImage;
    private TexturePaint myPaint;
    private static final int N = 0;
    private static final int S = 1;
    private static final int NW = 2;
    private static final int NE = 3;
    private static final int SE = 4;
    private static final int SW = 5;
    private static final int E = 6;
    private static final int W = 7;
    private int dir = 7;


    private Color currentColor;


    private int timeUp;

    private int colorIndex;

    private Timer myTimer;

    private Color[] colors = {Color.RED, Color.GREEN, Color.BLUE,
            Color.MAGENTA, Color.CYAN, Color.YELLOW};

    public static void main(String[] args) throws IOException {
        JFrame f = new JFrame("Texture Window");
        ScrollingTexturePanel myself = new ScrollingTexturePanel(new File(
                "C:\\Documents and Settings\\STABS\\Desktop\\metnet.gif"), 100,
                5);
        JTextField myField = new JTextField(
                "Hello I am writing some text in this field!");
        myField.setBackground(new Color(285212672));
        myField.setForeground(new Color(-1));


        f.getContentPane().add(myself);
        f.setSize(800, 800);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }

    public TexturePaint getPaint() {
        return myPaint;
    }

    public void setPaint(TexturePaint newPaint) {
        myPaint = newPaint;
        repaint();
    }

    public BufferedImage getImage() {
        return coloredImage;
    }

    private class TimedAnimator implements ActionListener {
        int x = 0;

        int y = 0;

        int changes = 0;

        int oldColorIndex = 0;
        ScrollingTexturePanel.ColorFader myFader;

        private TimedAnimator() {
        }

        public void actionPerformed(ActionEvent e) {
            if ((dir == 6) || (dir == 3) || (dir == 4))
                x = ((x + 1) % getImage().getWidth());
            if ((dir == 7) || (dir == 2) || (dir == 5))
                x = ((x - 1) % getImage().getWidth());
            if ((dir == 0) || (dir == 3) || (dir == 2))
                y = ((y - 1) % getImage().getHeight());
            if ((dir == 1) || (dir == 4) || (dir == 5))
                y = ((y + 1) % getImage().getHeight());
            setPaint(new TexturePaint(getImage(), new Rectangle(x, y,
                    getImage().getWidth(), getImage().getHeight())));
            changes += 1;
            if (changes == timeUp) {
                oldColorIndex = colorIndex;
                while (colorIndex == oldColorIndex)
                    colorIndex = ((int) (Math.random() * colors.length));

                myFader = new ColorFader(colors[colorIndex]);
                myFader.start();
                changes = 0;
            }
        }
    }

    public ScrollingTexturePanel(InputStream myInputStream, int timeUp, int timerPeriod)
            throws IOException {
        originalImage = ImageIO.read(myInputStream);
        changeDir();
        colorIndex = ((int) (Math.random() * colors.length));
        changeColor(colors[colorIndex]);
        myPaint = new TexturePaint(coloredImage, new Rectangle(0, 0,
                originalImage.getWidth(), originalImage.getHeight()));
        setOpaque(true);
        this.timeUp = timeUp;


        myTimer = new Timer(timerPeriod, new TimedAnimator());
        myTimer.setRepeats(true);
        myTimer.start();
    }


    public ScrollingTexturePanel(File myImage, int timeUp, int timerPeriod)
            throws IOException {
        originalImage = ImageIO.read(myImage);
        changeDir();
        colorIndex = ((int) (Math.random() * colors.length));
        changeColor(colors[colorIndex]);
        myPaint = new TexturePaint(coloredImage, new Rectangle(0, 0,
                originalImage.getWidth(), originalImage.getHeight()));
        setOpaque(true);
        this.timeUp = timeUp;


        myTimer = new Timer(timerPeriod, new TimedAnimator());
        myTimer.setRepeats(true);
        myTimer.start();
    }

    public void stop() {
        myTimer.stop();
        changeColor(Color.WHITE);
        setPaint(new TexturePaint(getImage(), new Rectangle(0, 0, getImage()
                .getWidth(), getImage().getHeight())));
    }

    public void start() {
        myTimer.start();
        changeColor(colors[((int) (Math.random() * colors.length))]);
        changeDir();
        setPaint(new TexturePaint(getImage(), new Rectangle(0, 0, getImage()
                .getWidth(), getImage().getHeight())));
    }

    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setPaint(myPaint);
            Insets insets = getInsets();
            g2d.fillRect(insets.left, insets.top, getWidth() - insets.right
                    - insets.left, getHeight() - insets.bottom - insets.top);
            g2d.dispose();
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    private void changeDir() {
        int oldDir = dir;
        while (dir == oldDir)
            dir = ((int) (Math.random() * 7.0D));
    }

    private void changeColor(Color newColor) {
        currentColor = newColor;
        coloredImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), originalImage.getType());
        for (int x = 0; x < coloredImage.getWidth(); x++) {
            for (int y = 0; y < coloredImage.getHeight(); y++)
                coloredImage.setRGB(x, y, originalImage.getRGB(x, y) &
                        newColor.getRGB());
        }
    }

    class ColorFader extends SwingWorker {
        private Color fadeTo;

        public ColorFader(Color newColor) {
            fadeTo = newColor;
        }

        public Object construct() {
            int newR = currentColor.getRed();
            int newG = currentColor.getGreen();
            int newB = currentColor.getBlue();
            while (!currentColor.equals(Color.BLACK)) {
                if (newR > 0)
                    newR -= 5;
                if (newG > 0)
                    newG -= 5;
                if (newB > 0)
                    newB -= 5;
                ScrollingTexturePanel.this.changeColor(new Color(newR, newG, newB));
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ScrollingTexturePanel.this.changeDir();
            while (!currentColor.equals(fadeTo)) {
                if (fadeTo.getRed() > newR) {
                    newR += 5;
                } else if (fadeTo.getRed() < newR)
                    newR--;
                if (fadeTo.getGreen() > newG) {
                    newG += 5;
                } else if (fadeTo.getGreen() < newG)
                    newG--;
                if (fadeTo.getBlue() > newB) {
                    newB += 5;
                } else if (fadeTo.getBlue() < newB)
                    newB--;
                ScrollingTexturePanel.this.changeColor(new Color(newR, newG, newB));
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
