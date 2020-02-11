package edu.iastate.metnet.metaomgraph.chart;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.Timer;


public class RangeSelector implements MouseListener, MouseMotionListener {
    private MetaOmChartPanel myChartPanel;
    private Rectangle2D area;
    private double startPoint;
    private double endPoint;
    private int screenStart;
    private int screenEnd;
    private int firstPoint;
    private boolean dragging;
    private Rectangle2D rangeRect;
    private int dashSize;
    private int gapSize;
    private int phase;
    private BasicStroke stroke;
    private Timer t;
    private int rangeStart;
    private int rangeEnd;
    private Vector<ActionListener> listeners;
    public static final String ACTION_COMMAND = "A range has been selected";

    public int getRangeEnd() {
        return rangeEnd;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public RangeSelector(MetaOmChartPanel mcp) {
        myChartPanel = mcp;
        dashSize = 3;
        gapSize = 3;
        phase = 0;
        stroke = new BasicStroke(1.0F, 0, 1, 1.0F, new float[]{dashSize, gapSize}, dashSize + gapSize - phase);
        t = new Timer(50, new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                updateStroke();
            }
        });
    }


    @Override
	public void mouseClicked(MouseEvent mouseevent) {
    }


    @Override
	public void mouseEntered(MouseEvent mouseevent) {
    }


    @Override
	public void mouseExited(MouseEvent mouseevent) {
    }

    @Override
	public void mousePressed(MouseEvent e) {
        if (e.getButton() != 1) return;
        area = myChartPanel.getChartPanel().getScreenDataArea();
        if (!area.contains(e.getPoint())) {
            return;
        }
        System.out.println("Chart panel's x is " + myChartPanel.getChartPanel().getX());
        screenStart = (e.getX() + myChartPanel.getChartPanel().getX());
        firstPoint = screenStart;
        screenEnd = (screenStart + 1);
        startPoint = myChartPanel.screenToChart(e.getPoint()).getX();
        System.out.println("Starting at: " + startPoint);
        dragging = true;
        t.start();
    }


    @Override
	public void mouseReleased(MouseEvent e) {
        System.out.println("released at " + endPoint);
        dragging = false;
        t.stop();
        if (rangeRect != null) {
            Graphics2D g2d = (Graphics2D) myChartPanel.getGraphics();
            g2d.setXORMode(Color.GRAY);
            g2d.setStroke(stroke);
            g2d.draw(rangeRect);
            rangeRect = null;
            g2d.dispose();
        }
        double chartStart = myChartPanel.screenToChart(new Point(screenStart, (int) (myChartPanel.getChartPanel().getY() + area.getY()))).getX();
        rangeStart = ((int) chartStart);
        if (chartStart > 0.0D) rangeStart += 1;
        if (rangeStart >= myChartPanel.getProject().getDataColumnCount()) {
            rangeStart = (myChartPanel.getProject().getDataColumnCount() - 1);
        }
        rangeEnd = ((int) myChartPanel.screenToChart(new Point(screenEnd, (int) (myChartPanel.getChartPanel().getY() + area.getY()))).getX());
        if (rangeEnd >= myChartPanel.getProject().getDataColumnCount()) {
            rangeEnd = (myChartPanel.getProject().getDataColumnCount() - 1);
        }
        if (rangeEnd < rangeStart)
            rangeEnd = rangeStart;
        actionPerformed();
    }

    @Override
	public void mouseDragged(MouseEvent e) {
        if (!dragging) return;
        if (!area.contains(e.getPoint())) return;
        if (e.getX() + myChartPanel.getChartPanel().getX() < screenStart) {
            screenEnd = firstPoint;
            screenStart = (e.getX() + myChartPanel.getChartPanel().getX());
        } else {
            screenStart = firstPoint;
            screenEnd = (e.getX() + myChartPanel.getChartPanel().getX());
        }
        endPoint = myChartPanel.screenToChart(e.getPoint()).getX();
        Graphics2D g2d = (Graphics2D) myChartPanel.getGraphics();
        g2d.setXORMode(Color.GRAY);
        g2d.setStroke(stroke);
        if (rangeRect != null) g2d.draw(rangeRect);
        rangeRect = new Rectangle2D.Double(screenStart, area.getY() + myChartPanel.getChartPanel().getY(), screenEnd - screenStart, area.getHeight());
        g2d.draw(rangeRect);
        g2d.dispose();
    }

    @Override
	public void mouseMoved(MouseEvent mouseevent) {
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(10, 0.25F));
        g2d.dispose();
    }

    public void updateStroke() {
        if (rangeRect == null) return;

        Graphics2D g2d = (Graphics2D) myChartPanel.getGraphics();
        g2d.setXORMode(Color.GRAY);
        g2d.setStroke(stroke);
        g2d.draw(rangeRect);
        phase = ((phase + 1) % (dashSize + gapSize));
        stroke = new BasicStroke(1.0F, 0, 1, 1.0F, new float[]{dashSize, gapSize}, dashSize + gapSize - phase);
        g2d.setStroke(stroke);
        g2d.draw(rangeRect);
        g2d.dispose();
    }


    public void addActionListener(ActionListener l) {
        if (listeners == null)listeners = new Vector();
        if (!listeners.contains(l)) listeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        if (listeners == null) return;

        listeners.remove(l);
    }


    private void actionPerformed() {
        if (listeners == null) return;
        ActionEvent event = new ActionEvent(this, 1001,"A range has been selected");
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).actionPerformed(event);
        }
    }

    public void clearActionListeners() {
        listeners = null;
    }
}
