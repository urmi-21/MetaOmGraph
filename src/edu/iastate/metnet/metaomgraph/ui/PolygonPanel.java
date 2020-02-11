package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class PolygonPanel
        extends JPanel {
    BouncingPolygon[] myPolygons;
    Image bufferedImage;
    Graphics2D off;

    public PolygonPanel(int polygons, int sides) {
        myPolygons = new BouncingPolygon[polygons];
        for (int i = 0; i < polygons; i++) {
            myPolygons[i] = new BouncingPolygon(sides, 0, 0, 800, 600);
        }
        Timer myTimer = new Timer(20, new TimedAnimator());
        myTimer.setRepeats(true);
        myTimer.start();
    }

    public static class BouncingPolygon extends Polygon {
        private int[] xvels;
        private int[] yvels;
        private Color myColor;
        private int minX;
        private int minY;
        private int maxX;
        private int maxY;
        private int dr;
        private int dg;
        private int db;

        public BouncingPolygon(int sides, int minX, int minY, int maxX, int maxY) {
            xpoints = new int[sides];
            ypoints = new int[sides];
            npoints = sides;
            xvels = new int[sides];
            yvels = new int[sides];
            for (int i = 0; i < sides; i++) {
                xpoints[i] = ((int) (Math.random() * maxX) + minX);
                ypoints[i] = ((int) (Math.random() * maxY) + minY);
                double rand = Math.random() - 0.5D;
                if (rand < 0.0D) {
                    xvels[i] = -1;
                } else
                    xvels[i] = 1;
                rand = Math.random() - 0.5D;
                if (rand < 0.0D) {
                    yvels[i] = -1;
                } else
                    yvels[i] = 1;
            }
            myColor = new Color((int) (Math.random() * 255.0D), (int) (
                    Math.random() * 255.0D), (int) (Math.random() * 255.0D));
            double rand = Math.random() - 0.5D;
            if (rand < 0.0D) {
                dr = -1;
            } else
                dr = 1;
            rand = Math.random() - 0.5D;
            if (rand < 0.0D) {
                dg = -1;
            } else
                dg = 1;
            rand = Math.random() - 0.5D;
            if (rand < 0.0D) {
                db = -1;
            } else
                db = 1;
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public Color getColor() {
            return myColor;
        }

        public Point getPoint(int point) {
            return new Point(xpoints[point], ypoints[point]);
        }

        public Point getVel(int point) {
            return new Point(xvels[point], yvels[point]);
        }

        public void setXVel(int point, int newVel) {
            xvels[point] = newVel;
        }

        public void setYVel(int point, int newVel) {
            yvels[point] = newVel;
        }

        public void setBounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public void move() {
            for (int i = 0; i < npoints; i++) {
                int newX = xpoints[i] + xvels[i];
                int newY = ypoints[i] + yvels[i];
                if (newX < minX) {
                    xvels[i] = ((int) (Math.random() * 3.0D) + 1);
                    newX = xpoints[i] + xvels[i];
                }
                if (newY < minY) {
                    yvels[i] = ((int) (Math.random() * 3.0D) + 1);
                    newY = ypoints[i] + yvels[i];
                }
                if (newX > maxX) {
                    xvels[i] = (-(int) (Math.random() * 3.0D) - 1);
                    newX = xpoints[i] + xvels[i];
                }
                if (newY > maxY) {
                    yvels[i] = (-(int) (Math.random() * 3.0D) - 1);
                    newY = ypoints[i] + yvels[i];
                }
                xpoints[i] = newX;
                ypoints[i] = newY;
            }
            int r = getColor().getRed();
            int g = getColor().getGreen();
            int b = getColor().getBlue();
            if (r + dr > 254) {
                dr = (-(int) (Math.random() * 3.0D) - 1);
            } else if (r + dr < 0)
                dr = ((int) (Math.random() * 3.0D) + 1);
            if (g + dg > 254) {
                dg = (-(int) (Math.random() * 3.0D) - 1);
            } else if (g + dg < 0)
                dg = ((int) (Math.random() * 3.0D) + 1);
            if (b + db > 254) {
                db = (-(int) (Math.random() * 3.0D) - 1);
            } else if (b + db < 0)
                db = ((int) (Math.random() * 3.0D) + 1);
            r += dr;
            g += dg;
            b += db;
            myColor = new Color(r, g, b);
        }
    }

    private class TimedAnimator implements ActionListener {
        private TimedAnimator() {
        }

        @Override
		public void actionPerformed(ActionEvent e) {
            repaint();
            for (int i = 0; i < myPolygons.length; i++) {
                myPolygons[i].move();
                myPolygons[i].setBounds(0, 0, getWidth(), getHeight());
            }
        }
    }


    @Override
	protected void paintComponent(Graphics g) {
        off = ((Graphics2D) g.create());
        off.setStroke(new BasicStroke(2.0F));
        if (isOpaque()) {
            off.setPaint(Color.BLACK);
            off.fillRect(0, 0, getWidth(), getHeight());
        } else {
            off.setColor(Color.BLACK);
        }

        for (int j = 0; j < myPolygons.length; j++) {
            off.setPaint(myPolygons[j].getColor());
            off.fillPolygon(myPolygons[j]);
            off.setPaint(Color.BLACK);
            off.drawPolygon(myPolygons[j]);
        }
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("Polygon Demo");
        PolygonPanel pp = new PolygonPanel(3, 4);
        f.getContentPane().add(pp);
        f.setDefaultCloseOperation(3);
        f.setSize(800, 600);
        f.setVisible(true);
    }
}
