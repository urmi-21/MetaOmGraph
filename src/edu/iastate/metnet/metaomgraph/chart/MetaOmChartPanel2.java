package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.MetaOmProject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JPanel;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;


public class MetaOmChartPanel2
        extends JPanel
        implements ChartChangeListener, ChartMouseListener, ActionListener {
    private Collection<double[]> data;
    private Collection<double[]> stddev;
    private String[] sampleNames;
    private MetaOmProject myProject;

    public MetaOmChartPanel2(MetaOmProject myProject, Collection<double[]> data, Collection<double[]> stddev, String[] sampleNames) {
        this.data = data;
        this.sampleNames = sampleNames;
        this.myProject = myProject;
    }

    public void refresh() {
    }

    public void chartChanged(ChartChangeEvent arg0) {
    }

    public void chartMouseClicked(ChartMouseEvent arg0) {
    }

    public void chartMouseMoved(ChartMouseEvent arg0) {
    }

    public void actionPerformed(ActionEvent e) {
    }
}
