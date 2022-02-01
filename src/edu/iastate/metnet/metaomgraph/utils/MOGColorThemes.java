package edu.iastate.metnet.metaomgraph.utils;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.Serializable;

public class MOGColorThemes implements Serializable {
    private String themeName;
    private Color tableRowEvenColor = null;
    private Color tableRowOddColor = null;
    private Color tableSelectionColor = null;
    private Color tableHighlightColor = null;
    private Color tableHyperlinkColor = null;
    private Color tableFgColor = null;
    private Color chartBackgroundColor = null;
    private Color plotBackgroundColor = null;

    public MOGColorThemes() {

    }

    public MOGColorThemes(String name) {
        this.themeName = name;
    }

    public MOGColorThemes(String name, Color evenRow, Color oddRow, Color tableSelColor, Color tableHlColor,
                          Color tableHlinkColor, Color chartBgColor, Color plotBgColor) {
        this.themeName = name;
        this.tableRowEvenColor = evenRow;
        this.tableRowOddColor = oddRow;
        this.tableHighlightColor = tableHlColor;
        this.tableHyperlinkColor = tableHlinkColor;
        this.tableSelectionColor = tableSelColor;
        this.chartBackgroundColor = chartBgColor;
        this.plotBackgroundColor = plotBgColor;
    }

    public MOGColorThemes(String name, Color evenRow, Color oddRow, Color tableSelColor, Color tableHlColor,
                          Color tableHlinkColor, Color chartBgColor, Color plotBgColor, Color tableFgColor) {
        this(name, evenRow, oddRow, tableSelColor, tableHlColor, tableHlinkColor, chartBgColor, plotBgColor);
        this.tableFgColor = tableFgColor;
    }

    public String getThemeName() {
        return themeName;
    }

    public Color getTableColorEven() {
        if (tableRowEvenColor == null) {
            return Color.white;
        } else {
            return tableRowEvenColor;
        }
    }

    public Color getTableColorOdd() {
        if (tableRowOddColor == null) {
            return new ColorUIResource(216, 236, 213);
        } else {
            return tableRowOddColor;
        }
    }

    public Color getTableSelectionColor() {
        if (tableSelectionColor == null) {
            return Color.black;
        } else {
            return tableSelectionColor;
        }
    }

    public Color getTableHighlightColor() {
        if (tableHighlightColor == null) {
            return Color.PINK;
        } else {
            return tableHighlightColor;
        }
    }

    public Color getTableHyperlinkColor() {
        if (tableHyperlinkColor == null) {
            return Color.green;
        } else {
            return tableHyperlinkColor;
        }
    }

    public Color getTableColorForeground() {
        return this.tableFgColor != null
                ? this.tableFgColor
                : UIManager.getColor("Table.foreground");
    }

    public Color getChartBackgroundColor() {
        if (chartBackgroundColor == null) {
            return Color.white;
        } else {
            return chartBackgroundColor;
        }
    }

    public Color getPlotBackgroundColor() {
        if (plotBackgroundColor == null) {
            return Color.white;
        } else {
            return plotBackgroundColor;
        }
    }

    public void setTableRowEvenColor(Color col) {
        tableRowEvenColor = col;
    }

    public void setTableRowOddColor(Color col) {
        tableRowOddColor = col;
    }

    public void setTableSelectionColor(Color col) {
        tableSelectionColor = col;
    }

    public void setTableHighlightColor(Color col) {
        tableHighlightColor = col;
    }

    public void setTableHyperlinkColor(Color col) {
        tableHyperlinkColor = col;
    }

    public void setChartBackgroundColor(Color col) {
        chartBackgroundColor = col;
    }

    public void setPlotBackgroundColor(Color col) {
        plotBackgroundColor = col;
    }

}
