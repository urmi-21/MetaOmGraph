package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Paint;
import java.util.ArrayList;

public class CustomColorScheme implements ChartColorScheme {
    private ArrayList<Paint> paint;
    private ArrayList<Paint> selectionPaint;
    private int defaultWidth;
    private int selectionWidth;

    public CustomColorScheme(ChartColorScheme base, int length) {
        paint = new ArrayList();
        selectionPaint = new ArrayList();
        for (int i = 0; i < length; i++) {
            paint.add(base.getSeriesPaint(i));
            selectionPaint.add(base.getSelectionPaint(i));
        }
        defaultWidth = base.getDefaultWidth();
        selectionWidth = base.getSelectionWidth();
    }

    @Override
	public Paint getSelectionPaint(int series) {
        return selectionPaint.get(series);
    }

    @Override
	public int getSelectionWidth() {
        return selectionWidth;
    }

    @Override
	public Paint getSeriesPaint(int series) {
        return paint.get(series);
    }

    @Override
	public int getDefaultWidth() {
        return defaultWidth;
    }

    public void setSelectionPaint(Paint selectionPaint) {
        for (int i = 0; i < this.selectionPaint.size(); i++) {
            this.selectionPaint.remove(i);
            this.selectionPaint.add(i, selectionPaint);
        }
    }

    public void setSelectionPaint(int index, Paint selectionPaint) {
        if ((index >= 0) && (index < this.selectionPaint.size())) {
            this.selectionPaint.remove(index);
        }
        this.selectionPaint.add(index, selectionPaint);
    }

    public void setDefaultWidth(int width) {
        defaultWidth = width;
    }

    public void setSelectionWidth(int width) {
        selectionWidth = width;
    }

    public void setSeriesPaint(int series, Paint paint) {
        this.paint.set(series, paint);
    }
}
