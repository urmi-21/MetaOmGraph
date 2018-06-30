package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Color;
import java.io.Serializable;
import java.security.InvalidParameterException;


public class RangeMarker implements Serializable {
    public static int HORIZONTAL = 0;

    public static int VERTICAL = 1;

    private String label;
    private int start;
    private int end;
    private int style;
    private Color myColor;

    public RangeMarker(int start, int end, String label, int style) {
        this(start, end, label, style, Color.BLACK);
    }

    public RangeMarker(int start, int end, String label, int style, Color myColor) {
        this.start = start;
        this.end = end;
        this.label = label;
        this.myColor = myColor;
        if ((style != HORIZONTAL) && (style != VERTICAL)) style = HORIZONTAL;
        this.style = style;
    }


    public final String getLabel() {
        if (label.length() <= 0) return " ";

        return label;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public int getStyle() {
        return style;
    }

    public Color getColor() {
        return myColor;
    }

    public void setColor(Color myColor) {
        this.myColor = myColor;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setStyle(int style) {
        if ((style != HORIZONTAL) && (style != VERTICAL)) throw new InvalidParameterException("Style must be either HORIZONTAL or VERTICAL");
        this.style = style;
    }
}
