package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Paint;

public class DefaultColorScheme implements ChartColorScheme {
    private Paint[] defaultPaint;

    public DefaultColorScheme(Paint[] defaultPaint) {
        this.defaultPaint = defaultPaint;
    }

    @Override
	public Paint getSelectionPaint(int series) {
        return null;
    }

    @Override
	public int getSelectionWidth() {
        return 3;
    }

    @Override
	public Paint getSeriesPaint(int series) {
        return defaultPaint[series];
    }

    @Override
	public int getDefaultWidth() {
        return 1;
    }
}
