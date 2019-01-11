package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class GeneHistogram {
    public GeneHistogram() {
    }

    public static JPanel makeHistogram(MetaOmProject project, int[] rows, int bins) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int row : rows) {
            double[] data = project.getIncludedData(row);
            for (double val : data) {
                if (!Double.isNaN(val)) {

                    if (val < min) min = val;
                    if (val > max) max = val;
                }
            }
        }
        double range = max - min;
        double intervalSize = range / 50.0D;

        System.out.println("min: " + min);
        System.out.println("max: " + max);
        System.out.println("range: " + range);
        System.out.println("intervalSize: " + intervalSize);
        if (intervalSize == 0.0D) {
            System.err.println("Interval size is 0");
            return null;
        }
        for (int row = 0; row < rows.length; row++) {
            double[] data = project.getIncludedData(rows[row]);
            String title = project.getRowName(rows[row])[project.getDefaultColumn()] + "     ";
            dataset.addSeries(title, data, bins, min, max);
        }

        JFreeChart chart = ChartFactory.createXYBarChart(null, null, false, null, dataset, PlotOrientation.VERTICAL, true, true, true);
        ChartPanel panel = new ChartPanel(chart, 1024, 600, 1, 1, 10000, 10000, true, true, true, true, true, true);
        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(panel, "Center");

        final XYPlot plot = chart.getXYPlot();
        ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer();
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new XYBarPainter() {

            public void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) {
                Paint p = renderer.getSeriesPaint(row);
                g2.setPaint(p);
                g2.fill(bar);
            }

            public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
            }

			@Override
			public void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar,
					org.jfree.chart.ui.RectangleEdge base) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar,
					org.jfree.chart.ui.RectangleEdge base, boolean pegShadow) {
				// TODO Auto-generated method stub
				
			}
        });
        plot.setRenderer(renderer);
        panel.addChartMouseListener(new ChartMouseListener() {
            public void chartMouseClicked(ChartMouseEvent event) {
                if (event.getTrigger().getClickCount() == 2) {
                    int series = -1;
                    if ((event.getEntity() instanceof LegendItemEntity)) {
                        series =
                                ((XYItemEntity) event.getEntity()).getSeriesIndex();
                    } else if ((event.getEntity() instanceof XYItemEntity)) {
                        series =
                                ((XYItemEntity) event.getEntity()).getSeriesIndex();
                    }
                    if (series < 0) {
                        return;
                    }
                    Color oldColor = (Color) plot.getRenderer().getSeriesPaint(series);
                    Color newColor = JColorChooser.showDialog(MetaOmGraph.getMainWindow(),
                            plot.getDataset().getSeriesKey(series)
                                    + " color", oldColor);
                    if (newColor != null) {
                        plot.getRenderer().setSeriesPaint(series, newColor);

                    }
                }
            }

            public void chartMouseMoved(ChartMouseEvent event) {
            }
        });


        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            Paint p = plot.getRenderer().getSeriesPaint(i);
            if ((p instanceof Color)) {
                Color c = (Color) p;
                Color newColor = new Color(c.getRed(), c.getGreen(), c
                        .getBlue(), 128);
                plot.getRenderer().setSeriesPaint(i, newColor);
                System.out.println("Color set");
            }
        }


        plot.getDomainAxis().setLabel("Sample Value");
        plot.getRangeAxis().setLabel("# of Samples");
        plot.getRenderer().setSeriesItemLabelsVisible(0, true);

        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chartPanel;
    }

    public static JPanel makeStackedHistogram(MetaOmProject project, int[] rows, int bins) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int row : rows) {
            double[] data = project.getIncludedData(row);
            for (double val : data) {
                if (!Double.isNaN(val)) {

                    if (val < min) min = val;
                    if (val > max) max = val;
                }
            }
        }
        double range = max - min;
        double intervalSize = range / 50.0D;

        System.out.println("min: " + min);
        System.out.println("max: " + max);
        System.out.println("range: " + range);
        System.out.println("intervalSize: " + intervalSize);
        if (intervalSize == 0.0D) {
            System.err.println("Interval size is 0");
            return null;
        }
        for (int row = 0; row < rows.length; row++) {
            double[] data = project.getIncludedData(rows[row]);
            String title = project.getRowName(rows[row])[project.getDefaultColumn()] + "";

            int[] intervals = new int[bins];
            Arrays.fill(intervals, 0);
            for (double val : data)
                if (!Double.isNaN(val)) {

                    int slot = (int) ((val - min) / intervalSize);
                    if (slot >= bins) {
                        System.err.println("Trying to add to slot " + slot);
                        slot = bins - 1;
                    }
                    intervals[slot] += 1;
                }
            DecimalFormat formatter = new DecimalFormat("#");
            double thisMin = min;
            for (int i = 0; i < intervals.length; i++) {

                String label = formatter.format(thisMin) + "-" + formatter.format(thisMin + intervalSize);
                thisMin += intervalSize;
                dataset.addValue(intervals[i], "Series" + row, label);
            }
        }
        JFreeChart chart = ChartFactory.createStackedBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, true, true, true);


        ChartPanel panel = new ChartPanel(chart, 1024, 600, 1, 1, 10000, 10000, true, true, true, true, true, true);
        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(panel, "Center");

        CategoryPlot plot = chart.getCategoryPlot();

        plot.getDomainAxis().setLabel("Sample Value");
        plot.getRangeAxis().setLabel("# of Samples");

        plot.getRenderer().setSeriesItemLabelsVisible(0, true);

        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chartPanel;
    }


    public static JPanel makeHistogram(MetaOmProject project, int row) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double[] data = project.getIncludedData(row);

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double val : data) {
            if (!Double.isNaN(val)) {

                if (val < min) min = val;
                if (val > max) max = val;
            }
        }
        double range = max - min;
        double intervalSize = range / 50.0D;
        int[] intervals = new int[50];
        Arrays.fill(intervals, 0);
        System.out.println("Points: " + data.length);
        System.out.println("min: " + min);
        System.out.println("max: " + max);
        System.out.println("range: " + range);
        System.out.println("intervalSize: " + intervalSize);
        if (intervalSize == 0.0D) {
            System.err.println("Interval size is 0");
            return null;
        }
        for (double val : data)
            if (!Double.isNaN(val)) {

                int slot = (int) ((val - min) / intervalSize);
                if (slot >= 50) {
                    System.err.println("Trying to add to slot " + slot);
                    slot = 49;
                }
                intervals[slot] += 1;
            }
        DecimalFormat formatter = new DecimalFormat("#");
        double thisMin = min;
        for (int i = 0; i < intervals.length; i++) {

            String label = formatter.format(thisMin) + "-" + formatter.format(thisMin + intervalSize);
            thisMin += intervalSize;
            dataset.addValue(intervals[i], "Series", label);
        }
        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, true, true);
        chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        chart.getCategoryPlot().getRenderer().setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());
        chart.getCategoryPlot().getDomainAxis().setLabel("Sample Value");
        chart.getCategoryPlot().getRangeAxis().setLabel("# of Samples");
        chart.getCategoryPlot().getRenderer().setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition());

        chart.getCategoryPlot().getRenderer().setSeriesItemLabelsVisible(0, true);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartPanel panel = new ChartPanel(chart, 1024, 600, 1, 1, 10000, 10000, true, true, true, true, true, true);
        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(panel, "Center");
        return chartPanel;
    }

    public static void makeHistogram(double[] data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        long min = (long) Math.floor(data[0]);
        long max = (long) Math.ceil(data[0]);
        double[] arrayOfDouble1 = data;
        int j = data.length;
        for (int i = 0; i < j; i++) {
            double val = arrayOfDouble1[i];
            if (val < min) {
                min = (long) Math.floor(val);
            }
            if (val > max) {
                max = (long) Math.ceil(val);
            }
        }
        long range = max - min;
        long intervalSize = range / 50L;
        int[] intervals = new int[50];
        Arrays.fill(intervals, 0);
        System.out.println("Points: " + data.length);
        System.out.println("min: " + min);
        System.out.println("max: " + max);
        System.out.println("range: " + range);
        System.out.println("intervalSize: " + intervalSize);
        if (intervalSize == 0L) {
            System.err.println("Interval size is 0");
            return;
        }
        double[] arrayOfDouble2 = data;
        int m = data.length;
        for (int k = 0; k < m; k++) {
            double val = arrayOfDouble2[k];
            int slot = (int) ((val - min) / intervalSize);
            if (slot >= 50) {
                System.err.println("Trying to add to slot " + slot);
                slot = 49;
            }
            intervals[slot] += 1;
        }
        for (int i = 0; i < intervals.length; i++) {
            String label = min + i * intervalSize + "-" + (min + ((i + 1) * intervalSize - 1L));
            dataset.addValue(intervals[i], "Series", label);
        }
        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, true, true);
        chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        chart.getCategoryPlot().getRenderer().setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());
        chart.getCategoryPlot().getRenderer().setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition());


        chart.getCategoryPlot().getRenderer().setSeriesItemLabelsVisible(0, true);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartPanel panel = new ChartPanel(chart, 1024, 600, 1, 1, 10000, 10000, true, true, true, true, true, true);

        JFrame f = new JFrame("Histogram");
        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(panel, "Center");
        f.getContentPane().add(chartPanel, "Center");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(2);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
