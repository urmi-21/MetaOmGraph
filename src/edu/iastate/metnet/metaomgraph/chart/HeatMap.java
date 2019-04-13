package edu.iastate.metnet.metaomgraph.chart;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * WaferMapChartDemo.java
 * ----------------------
 * (C) Copyright 2003, 2004, by Robert Redburn and Contributors.
 *
 * Original Author:  Robert Redburn;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: WaferMapChartDemo.java,v 1.8 2004/04/26 19:12:04 taqua Exp $
 *
 * Changes
 * -------
 * 08-Nov-2003 : Version 1 (RR);
 * 04-Dec-2003 : Added standard header and Javadocs (DG);
 * 19-Jan-2004 : Moved waferdata() method to DemoDatasetFactory (RR);
 *
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.HeatMapUtils;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demo showing a wafer map chart.
 */
public class HeatMap extends ApplicationFrame {

    /**
     * Creates a new demo.
     * 
     * @param title  the frame title.
     */
    public HeatMap(final String title) {
        super(title);
        
        WaferMapDataset dataset = new WaferMapDataset(20,20, 1);
       // Random data for wafer dataset
        Random random = new Random();
        
        for (int i = 1; i < 10; i++) {
           for (int j = 1; j < 10; j++) {
              dataset.addValue(random.nextInt(3)+1, i, j);
           }
        }
        final JFreeChart chart = ChartFactory.createWaferMapChart(
            "Wafer Map Demo",         // title
            dataset,                  // wafermapdataset
            PlotOrientation.VERTICAL, // vertical = notchdown
            true,                     // legend           
            true,                    // tooltips
            false
        ); 
        
       // HeatMapUtils.createHeatMapImage(dataset, paintScale)
        
       
        
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 400));
        setContentPane(chartPanel);
    }

    /**
     * Starting point for the demo application.
     * 
     * @param args  command line arguments (ignored).
     */
    public static void main(final String[] args) {
        final HeatMap demo = new HeatMap("Wafer Map Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
    
} // end class wafermapchartdemo