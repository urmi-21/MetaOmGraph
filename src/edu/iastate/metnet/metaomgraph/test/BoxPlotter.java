package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

public class BoxPlotter {
    public BoxPlotter() {
    }

    public static javax.swing.JPanel getSampleBoxPlot(final MetaOmProject myProject, final int[] rows) {
        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Working", "Generating BoxPlot", 0L, rows.length, true);
        new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < rows.length && !progress.isCanceled(); i++) {
                        int row = rows[i];
                        double[] data = myProject.getIncludedData(row);
//				if (MetaOmGraph.getInstance().isLogging()) {
//					for (int j=0;j<data.length;j++) {
//						if (!Double.isNaN(data[j])) {
//							data[j]=Math.log(data[j])/Math.log(2);
//						} else {
//							data[j]=Double.NaN;
//						}
//					}
//				}
                        ArrayList<Double> list = new ArrayList<Double>(data.length);
                        for (double d : data) {
                            list.add(d);
                        }
                        dataset.add(list, 0, myProject.getRowName(row)[myProject
                                .getDefaultColumn()]
                                + "");

                    }
                } catch (IOException e) {


                    e.printStackTrace();
                }
                progress.dispose();
            }
        }.start();
        progress.setVisible(true);
        if (progress.isCanceled()) {
            return null;
        }
        
        
        JFreeChart myChart = ChartFactory.createBoxAndWhiskerChart("BoxPlot",
                "Sample", "Value", dataset, false);
        
        //urmi add chat options
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setMeanVisible(false);
        myChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(1.5707963267948966D));
        myChart.getCategoryPlot().setRenderer(renderer);
        myChart.getCategoryPlot().setBackgroundPaint(MetaOmGraph.getPlotBackgroundColor());
        myChart.setBackgroundPaint(MetaOmGraph.getChartBackgroundColor());
        
        ChartPanel cPanel = new ChartPanel(myChart, Toolkit.getDefaultToolkit()
                .getScreenSize().width, Toolkit.getDefaultToolkit()
                .getScreenSize().height, 0, 0, Toolkit.getDefaultToolkit()
                .getScreenSize().width, Toolkit.getDefaultToolkit()
                .getScreenSize().height, true, true, true, true, true, true);
        cPanel.setPreferredSize(new Dimension(800, 600));
        return cPanel;
    }

    public static void showSampleBoxPlot(final MetaOmProject myProject, final int[] rows) {
        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Working", "Generating BoxPlot", 0L, rows.length, true);
        new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < rows.length && !progress.isCanceled(); i++) {
                        int row = rows[i];
                        double[] data = myProject.getIncludedData(row);
                        ArrayList<Double> list = new ArrayList<Double>(data.length);
                        for (double d : data) {
                            list.add(d);
                        }
                        dataset.add(list, 0, myProject.getRowName(row)[myProject
                                .getDefaultColumn()]
                                + "");
                    }


                } catch (IOException e) {


                    e.printStackTrace();
                }
                progress.dispose();
            }
        }.start();
        progress.setVisible(true);
        if (progress.isCanceled()) {
            return;
        }
        JFreeChart myChart = ChartFactory.createBoxAndWhiskerChart("BoxPlot",
                "Sample", "Value", dataset, false);

        myChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(

                CategoryLabelPositions.createUpRotationLabelPositions(1.5707963267948966D));
        ChartPanel cPanel = new ChartPanel(myChart, Toolkit.getDefaultToolkit()
                .getScreenSize().width, Toolkit.getDefaultToolkit()
                .getScreenSize().height, 0, 0, Toolkit.getDefaultToolkit()
                .getScreenSize().width, Toolkit.getDefaultToolkit()
                .getScreenSize().height, true, true, true, true, true, true);
        JFrame f = new JFrame("BoxPlot");
        f.getContentPane().add(cPanel, "Center");
        f.setSize(MetaOmGraph.getMainWindow().getWidth() - 100,
                MetaOmGraph.getMainWindow().getHeight() - 100);
        cPanel.setPreferredSize(f.getContentPane().getSize());
        f.setLocationRelativeTo(MetaOmGraph.getMainWindow());
        f.setDefaultCloseOperation(2);
        f.setVisible(true);
    }

    public static javax.swing.JPanel getColumnBoxPlot(final MetaOmProject myProject) {
        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        final int rows = myProject.getRowCount();
        final int cols = myProject.getDataColumnCount();
        final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Working", "Generating BoxPlot", 0L, rows, true);
        new Thread() {
            public void run() {
                try {
                    ArrayList<ArrayList<Double>> allData = new ArrayList<ArrayList<Double>>();
                    for (int row = 0; row < rows && !progress.isCanceled(); row++) {
                        progress.setProgress(row);
                        double[] data = myProject.getAllData(row);
                        for (int col = 0; col < cols && !progress.isCanceled(); col++) {
                            ArrayList<Double> myData;
                            if (row != 0) {
                                myData = allData.get(col);
                            } else {
                                myData = new ArrayList<Double>(rows);
                            }
                            myData.add(data[col]);
                            if (row != 0) {
                                allData.remove(col);
                            }
                            allData.add(col, myData);
                        }
//				dataset.add(data, 0, myProject.getDataColumnHeader(col));
//				System.out.println("col "+col+" finished");
                    }
                    for (int i = 0; i < allData.size(); i++) {
                        dataset.add(allData.get(i), 0, myProject.getDataColumnHeader(i));
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                progress.dispose();
            }
        }.start();
        progress.setVisible(true);
        if (progress.isCanceled()) {
            return null;
        }
        JFreeChart myChart = ChartFactory.createBoxAndWhiskerChart("BoxPlot",
                "Sample", "Value", dataset, false);

        myChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(

                CategoryLabelPositions.createUpRotationLabelPositions(1.5707963267948966D));
        ChartPanel cPanel = new ChartPanel(myChart, Toolkit.getDefaultToolkit()
                .getScreenSize().width, Toolkit.getDefaultToolkit()
                .getScreenSize().height, 0, 0, Toolkit.getDefaultToolkit()
                .getScreenSize().width, Toolkit.getDefaultToolkit()
                .getScreenSize().height, true, true, true, true, true, true);
        return cPanel;
    }

    public static void showColumnBoxPlot(final MetaOmProject myProject) {
        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        final int rows = myProject.getRowCount();
        final int cols = myProject.getDataColumnCount();
        final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Working", "Generating BoxPlot", 0L, rows, true);
        new Thread() {
            public void run() {
                try {
                    ArrayList<ArrayList<Double>> allData = new ArrayList<ArrayList<Double>>();
                    for (int row = 0; row < rows && !progress.isCanceled(); row++) {
                        progress.setProgress(row);
                        double[] data = myProject.getAllData(row);
                        for (int col = 0; col < cols && !progress.isCanceled(); col++) {
                            ArrayList<Double> myData;
                            if (row != 0) {
                                myData = allData.get(col);
                            } else {
                                myData = new ArrayList<Double>(rows);
                            }
                            myData.add(data[col]);
                            if (row != 0) {
                                allData.remove(col);
                            }
                            allData.add(col, myData);
                        }
//				dataset.add(data, 0, myProject.getDataColumnHeader(col));
//				System.out.println("col "+col+" finished");
                    }
                    for (int i = 0; i < allData.size(); i++) {
                        dataset.add(allData.get(i), 0, myProject.getDataColumnHeader(i));
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                progress.dispose();
            }
        }.start();
        progress.setVisible(true);
        if (progress.isCanceled()) {
            return;
        }
        JFreeChart myChart = ChartFactory.createBoxAndWhiskerChart("BoxPlot",
                "Sample", "Value", dataset, false);

        myChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(

                CategoryLabelPositions.createUpRotationLabelPositions(1.5707963267948966D));
        ChartPanel cPanel = new ChartPanel(myChart, Toolkit.getDefaultToolkit()
                .getScreenSize().width, Toolkit.getDefaultToolkit()
                .getScreenSize().height, 0, 0, Toolkit.getDefaultToolkit()
                .getScreenSize().width, Toolkit.getDefaultToolkit()
                .getScreenSize().height, true, true, true, true, true, true);
        JFrame f = new JFrame("BoxPlot");
        f.getContentPane().add(cPanel, "Center");
        f.setSize(MetaOmGraph.getMainWindow().getWidth() - 100,
                MetaOmGraph.getMainWindow().getHeight() - 100);
        cPanel.setPreferredSize(f.getContentPane().getSize());
        f.setLocationRelativeTo(MetaOmGraph.getMainWindow());
        f.setDefaultCloseOperation(2);
        f.setVisible(true);
    }
}
