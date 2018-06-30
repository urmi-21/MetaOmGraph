package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Paint;

public class DefaultColorScheme implements ChartColorScheme {
    private Paint[] defaultPaint;

    public DefaultColorScheme(Paint[] defaultPaint) {
        this.defaultPaint = defaultPaint;
    }

    public Paint getSelectionPaint(int series) {
        return null;
    }

    public int getSelectionWidth() {
        return 3;
    }

    public Paint getSeriesPaint(int series) {
        return defaultPaint[series];
    }

    public int getDefaultWidth() {
        return 1;
    }
}
