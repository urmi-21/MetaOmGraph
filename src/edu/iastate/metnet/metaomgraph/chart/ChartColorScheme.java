package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Paint;

public interface ChartColorScheme {
    Paint getSeriesPaint(int paramInt);

    Paint getSelectionPaint(int paramInt);

    int getDefaultWidth();

    int getSelectionWidth();
}
