package edu.iastate.metnet.metaomgraph.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * Class to plot runs as series
 * 
 * @author urmi
 *
 */
public class PlotRunsasSeries extends JPanel implements ChartChangeListener, ChartMouseListener, ActionListener {

	private String title;
	HashMap<Integer, double[]> databyCols;
	// DefaultCategoryDataset dataset = null;
	DefaultCategoryDataset dataset = null;
	XYDataset xyDataset = null;
	int opt = 0;

	public PlotRunsasSeries(String title, HashMap<Integer, double[]> databyCols, int opt) {
		this.title = title;
		this.databyCols = databyCols;
		this.opt = opt;
		if (opt == 1) {
			this.dataset = createDataset();
		} else if (opt == 2) {
			this.xyDataset = createXYDataset();
		}
	}

	public DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset res = new DefaultCategoryDataset();
		// add values to dataset
		Iterator it = databyCols.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry) it.next();
			String thisSeries = String.valueOf(pair.getKey());
			double[] thisData = (double[]) pair.getValue();
			for (int i = 0; i < thisData.length; i++) {
				res.addValue(thisData[i], thisSeries, String.valueOf(i));
			}
			// System.out.println(pair.getKey() + " = " + pair.getValue());

			it.remove(); // avoids a ConcurrentModificationException
		}
		return res;
	}

	private XYDataset createXYDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		Iterator it = databyCols.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry) it.next();
			XYSeries thisSeries = new XYSeries(
					MetaOmGraph.getActiveProject().getDataColumnHeader(Integer.valueOf(pair.getKey().toString())));
			double[] thisData = (double[]) pair.getValue();
			for (int i = 0; i < thisData.length; i++) {
				thisSeries.add(i, thisData[i]);
				// thisSeries.add
			}
			it.remove(); // avoids a ConcurrentModificationException
			dataset.addSeries(thisSeries);
		}
		return dataset;
	}

	public void createPlot() {
		JFreeChart chart = null;
		if (opt == 2) {
			chart = ChartFactory.createXYLineChart(title, "Data Series", "Expression", xyDataset);// (this.title,
		} else if (opt == 1) {
			chart = ChartFactory.createLineChart(this.title, "Data Series", "Expression", dataset);
		}
		
		if(chart==null) {
			return;
		}

		ChartPanel panel = new ChartPanel(chart);
		// setContentPane(panel);
		// displaychart in a frame
		SwingUtilities.invokeLater(() -> {
			JFrame newf = new JFrame();
			newf.setContentPane(panel);
			newf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			newf.pack();
			newf.setVisible(true);
		});

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void chartMouseClicked(ChartMouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void chartMouseMoved(ChartMouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void chartChanged(ChartChangeEvent arg0) {
		// TODO Auto-generated method stub

	}
}
