package edu.iastate.metnet.metaomgraph.chart;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import javax.swing.Popup;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;


public class InfoPopupManager
        implements ChartProgressListener, InternalFrameListener, ComponentListener {
    private transient Popup metadataPopup;
    private MetaOmChartPanel myChartPanel;
    private Point2D lastSelectedPoint;
    private boolean enabled;

    public InfoPopupManager(MetaOmChartPanel mcp) {
        myChartPanel = mcp;
        enabled = true;
    }

    @Override
	public void chartProgress(ChartProgressEvent event) {
        if (event.getType() != 2) {
            return;
        }
        if ((lastSelectedPoint != null) && (lastSelectedPoint.equals(myChartPanel.getSelectedPoint()))) return;
        lastSelectedPoint = myChartPanel.getSelectedPoint();
        if (metadataPopup != null) removePopup();

        showPopup();
    }


    private void showPopup() {
        throw new Error("Unresolved compilation problems: \n\tThe method getData(String) is undefined for the type Metadata\n\tThe method selectNode(int) in the type MetaOmTablePanel is not applicable for the arguments (String)\n");
    }

    private void removePopup() {
        if (metadataPopup != null) {
            metadataPopup.hide();
            metadataPopup = null;
        }
    }

    public Popup getPopup() {
        return metadataPopup;
    }


    @Override
	public void internalFrameOpened(InternalFrameEvent e) {
    }


    @Override
	public void internalFrameClosing(InternalFrameEvent e) {
        if (metadataPopup != null)
            removePopup();
    }

    @Override
	public void internalFrameClosed(InternalFrameEvent e) {
        if (metadataPopup != null)
            removePopup();
    }

    @Override
	public void internalFrameIconified(InternalFrameEvent e) {
        if (metadataPopup != null)
            removePopup();
    }

    @Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
        showPopup();
    }

    @Override
	public void internalFrameActivated(InternalFrameEvent e) {
        showPopup();
    }

    @Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
        if (metadataPopup != null)
            removePopup();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if ((!enabled) && (metadataPopup != null)) {
            removePopup();
        } else if (enabled)
            showPopup();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
	public void componentResized(ComponentEvent e) {
        if (metadataPopup != null) {
            removePopup();
            showPopup();
        }
    }

    @Override
	public void componentMoved(ComponentEvent e) {
        if (metadataPopup != null) {
            removePopup();
            showPopup();
        }
    }

    @Override
	public void componentShown(ComponentEvent e) {
    }

    @Override
	public void componentHidden(ComponentEvent e) {
    }
}
