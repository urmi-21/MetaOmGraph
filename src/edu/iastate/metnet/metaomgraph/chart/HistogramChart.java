package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultFormatterFactory;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class HistogramChart extends JInternalFrame implements ChartMouseListener, ActionListener {

	private int[] selected;
	String[] rowNames;
	private MetaOmProject myProject;
	private String xAxisname;
	private ChartToolBar myToolbar;
	private ChartPanel chartPanel;
	private JFreeChart myChart;
	private HistogramDataset dataset;
	// private XYLineAndShapeRenderer myRenderer;
	private XYBarRenderer myRenderer;
	JScrollPane scrollPane;
	private int _bins;

	// toolbar buttons
	private JButton properties;
	private JButton save;
	private JButton print;
	private JButton zoomIn;
	private JButton zoomOut;
	private JButton defaultZoom;
	private JButton changePalette;

	public static final String ZOOM_IN_COMMAND = "zoomIn";
	public static final String ZOOM_OUT_COMMAND = "zoomOut";
	public static final String ZOOM_DEFAULT_COMMAND = "defaultZoom";

	// chart colors
	private Color chartbg = MetaOmGraph.getChartBackgroundColor();
	private Color plotbg = MetaOmGraph.getPlotBackgroundColor();
	Color[] colorArray = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HistogramChart frame = new HistogramChart(null, 1, null);
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
	public HistogramChart(int[] selected, int bins, MetaOmProject mp) {
		setBounds(100, 100, 450, 300);
		this.selected = selected;
		// init rownames
		rowNames = mp.getDefaultRowNames(selected);

		myProject = mp;
		_bins = bins;
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

		// add buttons to top panel
		IconTheme theme = MetaOmGraph.getIconTheme();
		properties = new JButton(theme.getProperties());
		properties.setToolTipText("Chart Properties");
		save = new JButton(theme.getSaveAs());
		save.setToolTipText("Save Chart as Image");
		print = new JButton(theme.getPrint());
		print.setToolTipText("Print Chart");
		zoomIn = new JButton(theme.getZoomIn());
		zoomIn.setToolTipText("Zoom In");
		zoomOut = new JButton(theme.getZoomOut());
		zoomOut.setToolTipText("Zoom Out");
		defaultZoom = new JButton(theme.getDefaultZoom());
		defaultZoom.setToolTipText("Default Zoom");
		properties.setActionCommand("PROPERTIES");
		properties.addActionListener(chartPanel);
		save.setActionCommand("SAVE");
		save.addActionListener(chartPanel);
		print.setActionCommand("PRINT");
		print.addActionListener(chartPanel);
		zoomIn.setActionCommand("zoomIn");
		zoomIn.addActionListener(this);
		zoomOut.setActionCommand("zoomOut");
		zoomOut.addActionListener(this);
		defaultZoom.setActionCommand("defaultZoom");
		defaultZoom.addActionListener(this);
		changePalette = new JButton(theme.getPalette());
		changePalette.setToolTipText("Color Palette");
		changePalette.setActionCommand("changePalette");
		changePalette.addActionListener(this);

		changePalette.setOpaque(false);
		changePalette.setContentAreaFilled(false);
		changePalette.setBorderPainted(true);

		panel.add(properties);
		panel.add(save);
		panel.add(print);
		panel.add(zoomIn);
		panel.add(zoomOut);
		panel.add(defaultZoom);
		panel.add(changePalette);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		String chartTitle = "Histogram Plot:" + String.join(",", rowNames);
		this.setTitle(chartTitle);

	}

	public ChartPanel makeHistogram() throws IOException {
		// Create dataset
		dataset = createHistDataset();
		// chart
		myChart = ChartFactory.createHistogram("Histogram", "Value", "Count", dataset, PlotOrientation.VERTICAL, true,
				true, false);
		XYPlot plot = (XYPlot) myChart.getPlot();
		// bg colors
		plot.setBackgroundPaint(plotbg);
		myChart.setBackgroundPaint(chartbg);
		plot.setForegroundAlpha(0.95F);

		myRenderer = (XYBarRenderer) plot.getRenderer();
		myRenderer.setBarPainter(new StandardXYBarPainter());
		if (colorArray != null) {
			plot.setDrawingSupplier((DrawingSupplier) new DefaultDrawingSupplier(colorArray,
					DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		}
		ChartPanel panel = new ChartPanel(myChart, 800, 600, 2, 2, 10000, 10000, true, true, true, true, true, true) {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(ChartPanel.SAVE_COMMAND)) {
					File destination = null;
					JFileChooser chooseDialog = new JFileChooser(Utils.getLastDir());
					chooseDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooseDialog.setFileFilter(new GraphFileFilter(GraphFileFilter.PNG));
					JPanel sizer = new JPanel();
					JFormattedTextField widthField, heightField;
					JFormattedTextField.AbstractFormatter af = new JFormattedTextField.AbstractFormatter() {

						public Object stringToValue(String text) throws ParseException {
							try {
								return new Integer(text);
							} catch (NumberFormatException nfe) {
								return getFormattedTextField().getValue();
							}
						}

						public String valueToString(Object value) throws ParseException {
							if (value instanceof Integer) {
								Integer intValue = (Integer) value;
								if (intValue.intValue() < 1)
									return "1";
								else
									return ((Integer) value).intValue() + "";
							}
							return null;
						}

					};
					widthField = new JFormattedTextField(new DefaultFormatterFactory(af), new Integer(getWidth()));
					heightField = new JFormattedTextField(new DefaultFormatterFactory(af), new Integer(getHeight()));
					widthField.setColumns(4);
					heightField.setColumns(4);
					sizer.setLayout(new GridBagLayout());
					GridBagConstraints c = new GridBagConstraints();
					c.gridwidth = 3;
					c.fill = GridBagConstraints.NONE;
					sizer.add(new JLabel("Image size:"), c);
					c.gridwidth = 1;
					c.gridy = 1;
					sizer.add(new JLabel("Width:"), c);
					c.gridx = 1;
					sizer.add(widthField, c);
					c.gridx = 2;
					sizer.add(new JLabel("pixels"), c);
					c.gridx = 0;
					c.gridy = 2;
					sizer.add(new JLabel("Height:"), c);
					c.gridx = 1;
					sizer.add(heightField, c);
					c.gridx = 2;
					sizer.add(new JLabel("pixels"), c);
					chooseDialog.setAccessory(sizer);
					int returnVal = JFileChooser.APPROVE_OPTION;
					/*
					 * Continually show a file chooser until user selects a valid location, or
					 * cancels.
					 */
					boolean ready = false;
					while (!ready) {
						while (((destination == null)) && (returnVal != JFileChooser.CANCEL_OPTION)) {
							returnVal = chooseDialog.showSaveDialog(MetaOmGraph.getMainWindow());
							destination = chooseDialog.getSelectedFile();
						}
						// Did user cancel? If so, don't do anything.
						if (returnVal == JFileChooser.CANCEL_OPTION)
							return;
						// Check if file exists, prompt to overwrite if it
						// does
						String filename = destination.getAbsolutePath();
						if (!filename.substring(filename.length() - 4).equals(".png")) {
							filename += ".png";
							destination = new File(filename);
						}
						if (destination.exists()) {
							int overwrite = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
									filename + " already exists.  Overwrite?", "Overwrite File",
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
							if ((overwrite == JOptionPane.CANCEL_OPTION) || (overwrite == JOptionPane.CLOSED_OPTION))
								return;
							else if (overwrite == JOptionPane.YES_OPTION)
								ready = true;
							else
								destination = null; // No option
						} else
							ready = true;
					}
					final int oldDrawWidth = getMaximumDrawWidth();
					final int oldDrawHeight = getMaximumDrawHeight();
					final int newWidth = Integer.parseInt(widthField.getText());
					final int newHeight = Integer.parseInt(heightField.getText());
					final File trueDest = new File(destination.getAbsolutePath());
					setMaximumDrawWidth(newWidth);
					setMaximumDrawHeight(newHeight);
					try {
						ComponentToImage.saveAsPNG(this, trueDest, newWidth, newHeight);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					setMaximumDrawWidth(oldDrawWidth);
					setMaximumDrawHeight(oldDrawHeight);
				} else
					super.actionPerformed(e);
			}
		};
		panel.setMouseWheelEnabled(true);
		return panel;
	}

	private HistogramDataset createHistDataset() throws IOException {

		HistogramDataset dataset = new HistogramDataset();
		String thisName = "";
		for (int i = 0; i < selected.length; i++) {

			double[] dataY = myProject.getIncludedData(selected[i]);
			thisName = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();
			dataset.addSeries(thisName, dataY, _bins);
		}
		return dataset;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if (ZOOM_IN_COMMAND.equals(e.getActionCommand())) {
			Point2D center = getCenterPoint();
			chartPanel.zoomInBoth(center.getX(), center.getY());
			return;
		}
		if (ZOOM_OUT_COMMAND.equals(e.getActionCommand())) {
			Point2D center = getCenterPoint();
			chartPanel.zoomOutBoth(center.getX(), center.getY());
			return;
		}
		if (ZOOM_DEFAULT_COMMAND.equals(e.getActionCommand())) {
			chartPanel.restoreAutoBounds();
			return;
		}

		if ("changePalette".equals(e.getActionCommand())) {

			/*
			 * JColorbrewerChooser frame = new JColorbrewerChooser();
			 * frame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
			 * MetaOmGraph.getMainWindow().getHeight() / 2); frame.pack();
			 * frame.setTitle("Change parameters"); MetaOmGraph.getDesktop().add(frame);
			 * frame.setVisible(true);
			 */

			ColorPaletteChooserDialog dialog = new ColorPaletteChooserDialog();
			ColorBrewer cb = null;
			dialog.setModal(true);
			dialog.show();
			if (dialog.wasOKPressed()) {
				cb = dialog.getColorPalette();
			}

			if (cb != null) {
				int numColors = myChart.getXYPlot().getSeriesCount();
				numColors = Math.min(numColors, 10);
				// get color array
				colorArray = cb.getColorPalette(numColors);
				setPalette(colorArray);
			} else {
				// reset was pressed and the OK. show default colors
				colorArray = null;
				updateChart();

			}

			return;
		}

	}

	private void setPalette(Color[] colors) {
		if (colors == null) {
			return;
		}
		// JOptionPane.showMessageDialog(null, "cols:"+Arrays.toString(colors));
		XYPlot plot = (XYPlot) myChart.getPlot();

		int seriesCount = plot.getSeriesCount();
		for (int i = 0; i < seriesCount; i++) {
			// call change series color
			changeSeriesColor(i, colors[i % colors.length]);

		}

	}

	private Point2D getCenterPoint() {
		JFreeChart myChart = this.myChart;
		ValueAxis domain = myChart.getXYPlot().getDomainAxis();
		ValueAxis range = myChart.getXYPlot().getRangeAxis();

		double minx = domain.getLowerBound();
		final double maxx = domain.getUpperBound();
		final double miny = range.getLowerBound();
		final double maxy = range.getUpperBound();
		Point2D result = new Point2D() {
			public double getX() {
				return (maxx - maxy) / 2.0D;
			}

			public double getY() {
				return (maxy - miny) / 2;
			}

			public void setLocation(double x, double y) {
			}
		};
		return result;
	}

	@Override
	public void chartMouseClicked(ChartMouseEvent event) {
		// JOptionPane.showMessageDialog(null, "CLICKED");
		// If the user has double-clicked the legend or a point on the chart,
		// change the color of the double-clicked series.
		if (event.getTrigger().getClickCount() == 2) {
			if (event.getEntity() instanceof LegendItemEntity) {
				Comparable seriesKey = ((LegendItemEntity) event.getEntity()).getSeriesKey();
				int index = myChart.getXYPlot().getDataset().indexOf(seriesKey);
				// JOptionPane.showMessageDialog(null, "CLICKED");
				changeSeriesColor(index);
				return;
			} else if (event.getEntity() instanceof XYItemEntity) {
				changeSeriesColor(((XYItemEntity) event.getEntity()).getSeriesIndex());
				// JOptionPane.showMessageDialog(null, "CLICKED2");
				return;
			}
		}

	}

	@Override
	public void chartMouseMoved(ChartMouseEvent arg0) {
		// TODO Auto-generated method stub
		// JOptionPane.showMessageDialog(null, "CLICKED2");

	}

	/**
	 * Changes the colour of the Selected series
	 * 
	 * @param series
	 *            Selected series
	 */
	public void changeSeriesColor(int series) {
		Color oldColor = (Color) myRenderer.getSeriesPaint(series);
		Color newColor = JColorChooser.showDialog(MetaOmGraph.getMainWindow(),
				myChart.getXYPlot().getDataset().getSeriesKey(series) + " color", oldColor);
		if (newColor != null) {
			myRenderer.setSeriesPaint(series, newColor);
		}
	}

	public void changeSeriesColor(int series, Color newColor) {
		if (newColor != null) {
			myRenderer.setSeriesPaint(series, newColor);
		}
	}

	/**
	 * call this after changing chart values
	 */
	private void updateChart() {
		// remove previous action listeners
		properties.removeActionListener(chartPanel);
		print.removeActionListener(chartPanel);
		save.removeActionListener(chartPanel);
		this.chartPanel = null;
		try {
			this.chartPanel = makeHistogram();
			scrollPane.setViewportView(chartPanel);
			properties.addActionListener(chartPanel);
			print.addActionListener(chartPanel);
			save.addActionListener(chartPanel);

			this.repaint();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "ERRRRR");
		}

		return;
	}

}
