package edu.iastate.metnet.metaomgraph.utils;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.plaf.ColorUIResource;

public class MOGColorThemes implements Serializable {
	private String themeName;
	private Color tableColor1 = null;
	private Color tableColor2 = null;
	private Color tableSelectionColor = null;
	private Color tableHighlightColor = null;
	private Color tableHyperlinkColor = null;
	private Color chartBackgroundColor = null;
	private Color plotBackgroundColor = null;

	public MOGColorThemes() {

	}

	public MOGColorThemes(String name) {
		this.themeName = name;
	}

	public MOGColorThemes(String name, Color tableCol1, Color tableCol2, Color tableSelColor, Color tableHlColor,
			Color tableHlinkColor, Color chartBgColor, Color plotBgColor) {
		this.themeName = name;
		this.tableColor1 = tableCol1;
		this.tableColor2 = tableCol2;
		this.tableHighlightColor = tableHlColor;
		this.tableHyperlinkColor = tableHlinkColor;
		this.tableSelectionColor = tableSelColor;
		this.chartBackgroundColor = chartBgColor;
		this.plotBackgroundColor = plotBgColor;
	}

	public String getThemeName() {
		return themeName;
	}

	public Color getTableColor1() {
		if (tableColor1 == null) {
			return Color.white;
		} else {
			return tableColor1;
		}
	}

	public Color getTableColor2() {
		if (tableColor2 == null) {
			return new ColorUIResource(216, 236, 213);
		} else {
			return tableColor2;
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

	public void setTableColor1(Color col) {
		tableColor1 = col;
	}

	public void setTableColor2(Color col) {
		tableColor2 = col;
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
