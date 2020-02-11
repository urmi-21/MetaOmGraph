package edu.iastate.metnet.metaomgraph.throbber;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;


public abstract class Throbber extends JLabel implements ActionListener {
    private static boolean animating;
    private Timer t;
    private int startRequests;
    private BufferedImage image;
    private boolean drawn;
    private int delay;

    public abstract int getThrobberWidth();

    public abstract int getThrobberHeight();

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Throbber() {
        animating = false;
        delay = 50;
        if ((getThrobberWidth() > 0) && (getThrobberHeight() > 0)) {
            image = new BufferedImage(getThrobberWidth(), getThrobberHeight(),2);
            drawThrobber();
            setIcon(new ImageIcon(image));
            drawn = true;
        } else {
            drawn = false;
            image = new BufferedImage(16, 16, 2);
            setIcon(new ImageIcon(image));
        }
        startRequests = 0;
        t = new Timer(getDelay(), this);
        t.stop();
    }

    private void drawThrobber() {
        if (!drawn) {
            image = new BufferedImage(getThrobberWidth(), getThrobberHeight(),2);
            drawn = true;
        }
        drawThrobber((Graphics2D) image.getGraphics().create());
    }

    protected abstract void drawThrobber(Graphics2D paramGraphics2D);

    public synchronized void start() {
        startRequests += 1;

        if (!t.isRunning()) {
            animating = true;
            t.start();
        }
    }

    public synchronized void stop() {
        if (startRequests <= 0) return;
        startRequests -= 1;

        if (startRequests == 0) {
            animating = false;

            drawThrobber();
            setIcon(new ImageIcon(image));
            t.stop();
        }
    }

    public synchronized static boolean isAnimating() {
        return animating;
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        drawThrobber();
        setIcon(new ImageIcon(image));
    }

    @Override
	protected void paintComponent(Graphics g) {
        if (!drawn) actionPerformed(null);

        super.paintComponent(g);
    }
}
