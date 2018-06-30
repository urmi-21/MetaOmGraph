package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Color;

public class GrayColorScheme implements ChartColorScheme {
    public GrayColorScheme() {
    }

    public static final Color GRAY_COLOR = new Color(0, 0, 0, 100);

    public java.awt.Paint getSelectionPaint(int series) {
        return Color.RED;
    }

    public int getSelectionWidth() {
        return 2;
    }

    public java.awt.Paint getSeriesPaint(int series) {
        return GRAY_COLOR;
    }

    public int getDefaultWidth() {
        return 1;
    }
}
