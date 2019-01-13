package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;

public class HistogramChart extends JInternalFrame implements ChartMouseListener, ActionListener {

	private int[] selected;
	String[] rowNames;
	private MetaOmProject myProject;
	private String xAxisname;
	private ChartToolBar myToolbar;
	private ChartPanel chartPanel;
	private JFreeChart myChart;
	private XYDataset dataset;
	// private XYLineAndShapeRenderer myRenderer;
	private XYItemRenderer myRenderer;
	JScrollPane scrollPane;

	// chart colors
	private Color chartbg = MetaOmGraph.getChartBackgroundColor();
	private Color plotbg = MetaOmGraph.getPlotBackgroundColor();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HistogramChart frame = new HistogramChart(null, null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public HistogramChart(int[] selected, MetaOmProject mp) {
		setBounds(100, 100, 450, 300);
		this.selected = selected;
		// init rownames
		rowNames = mp.getDefaultRowNames(selected);
		// JOptionPane.showMessageDialog(null, Arrays.toString(rowNames));

		myProject = mp;
		chartPanel = null;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		getContentPane().add(panel, BorderLayout.NORTH);
		JButton btnNewButton = new JButton("New button");
		// panel.add(btnNewButton);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		JButton btnNewButton_1 = new JButton("Change X axis");
		btnNewButton_1.setActionCommand("chooseX");
		btnNewButton_1.addActionListener(this);
		panel_1.add(btnNewButton_1);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// create sample plot

		try {
			chartPanel = makeHistogram();
		} catch (IOException e) {
		}
		scrollPane.setViewportView(chartPanel);

	}

	public ChartPanel makeHistogram() throws IOException {
		// Create dataset
		dataset = createHistDataset();
		// Create chart
		myChart = ChartFactory.createScatterPlot("", "", "", dataset);
		// Changes background color
		Shape shape = ShapeUtilities.createRegularCross(2, 1);
		XYPlot plot = (XYPlot) myChart.getPlot();
		plot.setBackgroundPaint(plotbg);
		myChart.setBackgroundPaint(chartbg);
		// plot.setpaint
		// XYItemRenderer renderer = plot.getRenderer();
		myRenderer = plot.getRenderer();
		/// myRenderer.setBaseShape(shape);
		/*
		 * renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator() { public
		 * String generateToolTip(XYDataset dataset, int series, int item) { double y =
		 * dataset.getYValue(series, item); double x = dataset.getXValue(series, item);
		 * // JOptionPane.showMessageDialog(null, "item:"+item); return
		 * createTooltip(item, x, y); // return x+" m, "+y+" %"; } });
		 */
		// renderer.setSeriesShape(0, shape);
		// renderer.setSeriesPaint(0, Color.red);
		// Create Panel
		// use full constructor otherwise tooltips dont work
		ChartPanel chartPanel = new ChartPanel(myChart, 800, 600, 2, 2, 10000, 10000, true, true, true, true, true,
				true);
		return null;
	}

	private XYDataset createHistDataset() throws IOException {

		return null;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void chartMouseClicked(ChartMouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void chartMouseMoved(ChartMouseEvent event) {
		// TODO Auto-generated method stub

	}

}
