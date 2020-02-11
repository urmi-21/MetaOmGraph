package edu.iastate.metnet.metaomgraph.throbber;

import edu.iastate.metnet.metaomgraph.ui.MetNetLAF;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;


public class FlippingSquaresThrobber
        extends Throbber {
    private Polygon[] polys;
    private boolean flipping;
    private int size;
    private Color squareColor;
    private boolean front;
    private int offset;

    public FlippingSquaresThrobber() {
        size = 6;
        offset = (getThrobberWidth() / size);
        resetPolys();
    }

    @Override
	public int getDelay() {
        return 75;
    }

    private void resetPolys() {
        polys = new Polygon[4];
        int[] xpoints = {offset, offset + size, offset + size, offset};
        int[] ypoints = {offset, offset, offset + size, offset + size};
        int npoints = 4;
        polys[0] = new Polygon(xpoints, ypoints, npoints);
        xpoints = new int[]{offset + size + 1, offset + size + 1 + size, offset + 1 + size + size, offset + size + 1};
        polys[1] = new Polygon(xpoints, ypoints, npoints);
        ypoints = new int[]{offset + size + 1, offset + size + 1, offset + size + 1 + size, offset + size + 1 + size};
        polys[2] = new Polygon(xpoints, ypoints, npoints);
        xpoints = new int[]{offset, offset + size, offset + size, offset};
        polys[3] = new Polygon(xpoints, ypoints, npoints);
        flipping = true;
        squareColor = MetNetLAF.MNGreen;
        front = true;
    }

    private void movePolys() {
        for (int x = 0; x < 4; x++) {
            int change = flipping ? 1 : -1;

            int point = 0;
            if (point == 0) {
                polys[x].xpoints[point] += change;
                polys[x].ypoints[point] += change;
                polys[x].xpoints[(point + 2)] -= change;
                polys[x].ypoints[(point + 2)] -= change;
            } else {
                polys[x].xpoints[point] -= change;
                polys[x].ypoints[point] += change;
                polys[x].xpoints[(point + 2)] += change;
                polys[x].ypoints[(point + 2)] -= change;
            }
        }
        if (polys[0].xpoints[0] == polys[0].xpoints[2]) {
            flipping = false;
            front = (!front);
        } else if (polys[0].xpoints[0] == offset) {
            flipping = true;
        }
        squareColor = (front ? MetNetLAF.MNDarkGreen : MetNetLAF.MNBlue);
    }

    @Override
	public int getThrobberWidth() {
        return 24;
    }

    @Override
	public int getThrobberHeight() {
        return 24;
    }

    @Override
	protected void drawThrobber(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(MetNetLAF.MNYellow);
        g2d.fillRect(0, 0, getThrobberWidth(), getThrobberHeight());
        if (!isAnimating()) {
            resetPolys();
        } else {
            movePolys();
        }
        for (int x = 0; x < 4; x++) {
            g2d.setPaint(squareColor);
            g2d.fillPolygon(polys[x]);
            g2d.setPaint(Color.BLACK);
            g2d.drawPolygon(polys[x]);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("test");
        final FlippingSquaresThrobber throbber = new FlippingSquaresThrobber();
        f.getContentPane().add(throbber);
        f.pack();
        f.setDefaultCloseOperation(3);
        f.getContentPane().addMouseListener(new MouseListener() {


            @Override
			public void mouseClicked(MouseEvent e) {
                if (Throbber.isAnimating())
                    throbber.stop();
                else
                    throbber.start();
            }


            @Override
			public void mousePressed(MouseEvent e) {
            }


            @Override
			public void mouseReleased(MouseEvent e) {
            }


            @Override
			public void mouseEntered(MouseEvent e) {
            }


            @Override
			public void mouseExited(MouseEvent e) {
            }
        });
        f.setVisible(true);
    }
}
