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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
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
        
        WaferMapDataset dataset = new WaferMapDataset(200,200, 1);
       // Random data for wafer dataset
        Random random = new Random();
        
        for (int i = 1; i < 200; i++) {
           for (int j = 1; j < 200; j++) {
              dataset.addValue(random.nextInt(10)+1, i, j);
           }
        }
        final JFreeChart chart = ChartFactory.createWaferMapChart(
            "Wafer Map Demo",         // title
            dataset,                  // wafermapdataset
            PlotOrientation.VERTICAL, // vertical = notchdown
            true,                     // legend           
            true,                    // tooltips
            true
        ); 
        
       // HeatMapUtils.createHeatMapImage(dataset, paintScale)
        
        
        final ChartPanel chartPanel = new ChartPanel(chart) {
        	
        	@Override
			public String getToolTipText(MouseEvent event) {

				ChartEntity entity = getChartRenderingInfo().getEntityCollection().getEntity(event.getPoint().getX(),
						event.getPoint().getY());
				// JOptionPane.showMessageDialog(null, entity);
				if (!(entity instanceof  CategoryItemEntity)) {
					// JOptionPane.showMessageDialog(null, "null");
					return "null";
					//return null;
				}
				CategoryItemEntity item = (CategoryItemEntity) entity;
				String colKey = (String) item.getColumnKey();
				String rowKey = (String) item.getRowKey();
				String value = item.getToolTipText().split("=")[1];

				//JOptionPane.showMessageDialog(null,"rk:" + rowKey + " ck:" + colKey + " " + item.getToolTipText() + " val:" + value);
				// create tooltip
				return "toool";
			}

			// urmi display tooltip away from point
			@Override
			public Point getToolTipLocation(MouseEvent event) {
				Point thisPoint = event.getPoint();
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				// int maxWidth=(int) screenSize.getWidth();
				int maxWidth = getWidth();
				// define horizontal space between tooltip and point
				int xMargin = 25;

				int y = thisPoint.y;
				int newy = 100;
				/*
				 * select appropriate y if(y-200<=0) { newy=10; }else { newy=y-200; }
				 */
				int x = thisPoint.x;
				// JOptionPane.showMessageDialog(null, "mw:"+maxWidth+" x:"+x);
				// if point is far right of scree show tool tip to the left
				if (maxWidth - x <= 450) {
					// JOptionPane.showMessageDialog(null, "mw:"+maxWidth+" x:"+x);
					// return new Point(x-300, 5);
					// table width is 400
					return new Point(x - (400 + xMargin), newy);
				}
				return new Point(x + xMargin, newy);
			}
        	
        };
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