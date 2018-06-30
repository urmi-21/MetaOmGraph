package edu.iastate.metnet.metaomgraph.chart;

public class ChartProperties {
    private boolean shapePainted;
    private boolean linePainted;
    private ChartColorScheme colorScheme;

    public ChartProperties() {
        shapePainted = false;
        linePainted = true;
        colorScheme = null;
    }

    public ChartColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(ChartColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public boolean isLinePainted() {
        return linePainted;
    }

    public void setLinePainted(boolean linePainted) {
        this.linePainted = linePainted;
    }

    public boolean isShapePainted() {
        return shapePainted;
    }

    public void setShapePainted(boolean shapePainted) {
        this.shapePainted = shapePainted;
    }
}
