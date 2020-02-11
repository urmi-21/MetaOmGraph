package edu.iastate.metnet.metaomgraph.chart;

import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JScrollBar;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;

public class ChartScrollBar extends JScrollBar implements ChartChangeListener, java.awt.event.AdjustmentListener, java.awt.event.MouseWheelListener {
    private int min;
    private int max;
    private int value;
    private int range;
    private MetaOmChartPanel myChartPanel;

    public ChartScrollBar(MetaOmChartPanel myChartPanel, int orientation) {
        super(orientation);
        this.myChartPanel = myChartPanel;
        if (orientation == 0) horizInit();
        else vertInit();
    }

    private void horizInit() {
        min = -1;
        max = myChartPanel.getProject().getDataColumnCount();
        value = -1;
        range = (max - min);
        setMaximum(max);
        setMinimum(min);
        setValue(value);
        setVisibleAmount(range);
        setUnitIncrement(1);
        addAdjustmentListener(this);
    }

    public void setMax(int max) {
        this.max = max;
        range = (max - min);
        setMaximum(max);
        setVisibleAmount(range);
    }

    private void vertInit() {
        min = ((int) Math.floor(myChartPanel.getChart().getXYPlot().getRangeAxis().getLowerBound()));
        max = ((int) Math.ceil(myChartPanel.getChart().getXYPlot().getRangeAxis().getUpperBound()));
        range = (max - min);
        value = min;
        setMaximum(max);
        setMinimum(min);
        setValue(value);
        setVisibleAmount(range);
        setUnitIncrement(1);
        addAdjustmentListener(this);
    }

    private void horizChange() {
        ValueAxis domainAxis = myChartPanel.getChart().getXYPlot().getDomainAxis();
        if (domainAxis.getUpperBound() > max) {
            if (domainAxis.getLowerBound() > max) domainAxis.setLowerBound(max - 1);
            domainAxis.setUpperBound(max);
            return;
        }
        if (domainAxis.getLowerBound() < min) {
            if (domainAxis.getUpperBound() < min) domainAxis.setUpperBound(min + 1);
            domainAxis.setLowerBound(min);
            return;
        }
        value = ((int) domainAxis.getLowerBound());
        range = ((int) (domainAxis.getUpperBound() - value));
        if (range == max - min) {
            removeAdjustmentListener(this);
            setVisibleAmount(range);
            setEnabled(false);
            addAdjustmentListener(this);
        } else {
            removeAdjustmentListener(this);
            setEnabled(true);
            setValue(value);
            setVisibleAmount(range);
            setBlockIncrement(range);
            addAdjustmentListener(this);
        }
    }


    private void horizAdjustment() {
        ValueAxis domainAxis = myChartPanel.getChart().getXYPlot().getDomainAxis();
        myChartPanel.getChart().removeChangeListener(this);
        domainAxis.setLowerBound(getValue());
        domainAxis.setUpperBound(getValue() + getVisibleAmount());
        myChartPanel.getChart().addChangeListener(this);
    }


    private void vertChange() {
    }


    private void vertAdjustment() {
        ValueAxis rangeAxis = myChartPanel.getChart().getXYPlot().getRangeAxis();
        myChartPanel.getChart().removeChangeListener(this);
        rangeAxis.setLowerBound(max - getValue() - getVisibleAmount());
        rangeAxis.setUpperBound(max - getValue());
        myChartPanel.getChart().addChangeListener(this);
    }

    @Override
	public void chartChanged(ChartChangeEvent event) {
        if (getOrientation() == 0) horizChange();
        else vertChange();
    }

    @Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
        if (getOrientation() == 0) horizAdjustment();
        else vertAdjustment();
    }

    public void chartProgress(ChartProgressEvent event) {
        if (event.getType() != 2) return;

        if (getOrientation() == 0) horizChange();
        else vertChange();
    }

    @Override
	public void mouseWheelMoved(MouseWheelEvent e) {
        setValue(getValue() + e.getUnitsToScroll());
    }
}
