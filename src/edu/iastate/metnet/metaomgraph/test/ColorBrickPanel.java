package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.ui.StripedTable;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;

public class ColorBrickPanel extends JPanel implements MouseListener {
    private BufferedImage originalImage;
    private BufferedImage coloredImage;

    public ColorBrickPanel(InputStream instream) {
        try {
            originalImage = ImageIO.read(instream);
            addMouseListener(this);
            xpos = ((int) (Math.random() * getWidth()));
            ypos = ((int) (Math.random() * getHeight()));
            xvel = 1;
            yvel = 1;
            bounce = true;
            Thread r = new Thread() {


                public void run() {


                    for (; ; ) {


                        if (isVisible()) {
                            try {
                                Thread.sleep(25L);
                                if (bounce) {
                                    repaint();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };


            r.start();
            colorGoalPanel = new JPanel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.YELLOW};


    private int xpos;


    private int ypos;


    private int xvel;


    private int yvel;


    private boolean bounce;


    private int colorIndex;


    private Color goalColor;


    private Color currentColor;


    public static JPanel colorGoalPanel;


    private BufferedImage changeColor(Color newColor) {
        BufferedImage coloredImage = new BufferedImage(
                originalImage.getWidth(), originalImage.getHeight(),
                originalImage.getType());
        for (int x = 0; x < coloredImage.getWidth(); x++)
            for (int y = 0; y < coloredImage.getHeight(); y++)
                coloredImage.setRGB(x, y, originalImage.getRGB(x, y) &
                        newColor.getRGB());
        return coloredImage;
    }

    public Color randomColor() {
        return new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (currentColor == null) {
            currentColor = randomColor();
            coloredImage = changeColor(currentColor);
        }
        if (goalColor == null) {
            goalColor = randomColor();
            colorGoalPanel.setBackground(goalColor);
        }
        if (bounce) {
            int newRed = currentColor.getRed();
            int newGreen = currentColor.getGreen();
            int newBlue = currentColor.getBlue();
            if (newRed < goalColor.getRed()) {
                newRed++;
            } else if (newRed > goalColor.getRed()) {
                newRed--;
            }
            if (newGreen < goalColor.getGreen()) {
                newGreen++;
            } else if (newGreen > goalColor.getGreen()) {
                newGreen--;
            }
            if (newBlue < goalColor.getBlue()) {
                newBlue++;
            } else if (newBlue > goalColor.getBlue()) {
                newBlue--;
            }
            currentColor = new Color(newRed, newGreen, newBlue);
            coloredImage = changeColor(currentColor);
            if (currentColor.equals(goalColor)) {
                goalColor = null;
            }
            if (xpos + originalImage.getWidth() >= getWidth()) {
                xvel = -1;


            } else if (xpos == 0) {
                xvel = 1;
            }


            if (ypos + originalImage.getHeight() >= getHeight()) {
                yvel = -1;


            } else if (ypos == 0) {
                yvel = 1;
            }


            xpos += xvel;
            ypos += yvel;
        }

        g2d.drawImage(coloredImage, null, xpos, ypos);
        g2d.dispose();
    }

    protected void oldpaintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (bounce) {
            if (xpos + originalImage.getWidth() >= getWidth()) {
                xvel = -1;
                colorIndex = ((int) (Math.random() * 6.0D));
                coloredImage = changeColor(colors[colorIndex]);
            } else if (xpos == 0) {
                xvel = 1;
                colorIndex = ((int) (Math.random() * 6.0D));
                coloredImage = changeColor(colors[colorIndex]);
            }
            if (ypos + originalImage.getHeight() >= getHeight()) {
                yvel = -1;
                colorIndex = ((int) (Math.random() * 6.0D));
                coloredImage = changeColor(colors[colorIndex]);
            } else if (ypos == 0) {
                yvel = 1;
                colorIndex = ((int) (Math.random() * 6.0D));
                coloredImage = changeColor(colors[colorIndex]);
            }
            g2d.drawImage(coloredImage, null, xpos, ypos);
            xpos += xvel;
            ypos += yvel;
        } else {
            for (int y = 0; y < getHeight(); y += originalImage.getHeight())
                for (int x = 0; x < getWidth(); x += originalImage.getWidth()) {
                    colorIndex = ((int) (Math.random() * 6.0D));
                    g2d.drawImage(changeColor(colors[colorIndex]), null, x, y);
                }
        }
        g2d.dispose();
    }

    public static void main(String[] args) {
        System.out.println("Hello".indexOf(""));
        System.out.println(Integer.toHexString(StripedTable.alternateRowColor.getRGB()));
        JFrame f = new JFrame("test");
        f.setDefaultCloseOperation(3);
        f.setSize(800, 600);
        ColorBrickPanel cbp = new ColorBrickPanel(f.getClass()
                .getResourceAsStream("/resource/misc/metnet.gif"));
        f.getContentPane().add(cbp);
        f.setVisible(true);
        JFrame colorFrame = new JFrame("Goal color");
        colorFrame.setSize(100, 100);
        colorFrame.setLocation(800, 0);
        colorFrame.getContentPane().add(colorGoalPanel);
        colorFrame.setVisible(true);
    }

    public void mouseClicked(MouseEvent e) {
        bounce = (!bounce);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
