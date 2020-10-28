package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
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
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.statistics.HistogramDataset;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class HistogramChart extends TaskbarInternalFrame implements ChartMouseListener, ActionListener {

	// 1 for data rows, 2 for plotting a hist from double[]
	private int histType;
	private double[] plotData;
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
	private JButton splitDataset;
	private JButton normalizeButton;

	private LegendTitle myLegend;
	private JToggleButton toggleLegend;
	private boolean legendFlag = true;
	// if number of items in legend is more than this then turn legend off
	private int maxLegend = 30;

	private boolean[] excludedCopy;

	public static final String ZOOM_IN_COMMAND = "zoomIn";
	public static final String ZOOM_OUT_COMMAND = "zoomOut";
	public static final String ZOOM_DEFAULT_COMMAND = "defaultZoom";

	// chart colors
	private Color chartbg = MetaOmGraph.getChartBackgroundColor();
	private Color plotbg = MetaOmGraph.getPlotBackgroundColor();
	Color[] colorArray = null;

	// for slider to adjust transparency
	private float minAlpha = 0;
	private float maxAlpha = 10;
	private float initAlpha = 0.8F;
	JSlider alphaSlider;

	String splitCol;
	Map<String, Collection<Integer>> splitIndex;
	HashMap<String, String> seriesNameToKeyMap;

	private boolean isNormalized;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					HistogramChart frame = new HistogramChart(null, 1, null, 2, null,false);
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
	public HistogramChart(int[] selected, int bins, MetaOmProject mp, int htype, double[] data, boolean isPlayback) {
		this(selected, bins,  mp,htype, data, MetaOmAnalyzer.getExclude(), isPlayback);
	}
	public HistogramChart(int[] selected, int bins, MetaOmProject mp, int htype, double[] data, boolean[] excludedSamples, boolean isPlayback) {
		// create a copy of excluded
		
		if (excludedSamples != null) {
			excludedCopy = new boolean[excludedSamples.length];
			System.arraycopy(excludedSamples, 0, excludedCopy, 0, excludedSamples.length);
		}
		
		
		
		histType = htype;
		setBounds(100, 100, 450, 300);
		this.selected = selected;
		// init rownames
		if (htype == 1) {
			rowNames = mp.getDefaultRowNames(selected);
		}
		if (htype == 2) {
			rowNames = new String[] { "" };
			plotData = data;
		}

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

		JButton btnNewButton_1 = new JButton("Change bins");
		btnNewButton_1.setActionCommand("chooseBins");
		btnNewButton_1.addActionListener(this);
		panel_1.add(btnNewButton_1);

		normalizeButton = new JButton("Density Histogram");
		normalizeButton.setActionCommand("normalizeByCount");
		normalizeButton.addActionListener(this);
		panel_1.add(normalizeButton);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		alphaSlider = new JSlider();
		alphaSlider.setValue((int) (initAlpha * 10F));
		alphaSlider.setMaximum((int) maxAlpha);
		alphaSlider.setMinimum((int) minAlpha);
		alphaSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				float alphaVal = alphaSlider.getValue() / 10.0F;
				// JOptionPane.showMessageDialog(null, "val:"+alphaVal);
				updateChartAlpha(alphaVal);
			}
		});
		/*
		 * Hashtable labelTable = new Hashtable(); labelTable.put(new Integer(0), new
		 * JLabel("0.0")); labelTable.put(new Integer(5), new JLabel("0.5"));
		 * labelTable.put(new Integer(10), new JLabel("1.0"));
		 */

		// create sample plot

		try {
			// Create dataset
			if (histType == 1) {
				dataset = createHistDataset();
			} else if (histType == 2) {
				dataset = createHistDataset(plotData);
			}

			chartPanel = makeHistogram();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "ERRRRRRRRRRRRRR");
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

		splitDataset = new JButton(theme.getSort());
		splitDataset.setToolTipText("Split by categories");
		splitDataset.setActionCommand("splitDataset");
		splitDataset.addActionListener(this);

		if (histType == 2) {
			splitDataset.setEnabled(false);
		}

		toggleLegend = new JToggleButton(theme.getLegend(), legendFlag);
		toggleLegend.setToolTipText("Show/hide legend");
		toggleLegend.setActionCommand("legend");
		toggleLegend.addActionListener(this);

		panel.add(properties);
		panel.add(save);
		panel.add(print);
		panel.add(zoomIn);
		panel.add(zoomOut);
		panel.add(defaultZoom);
		panel.add(toggleLegend);
		panel.add(splitDataset);
		panel.add(changePalette);
		panel.add(alphaSlider);

		// alphaSlid

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		String chartTitle = "Histogram Plot:" + String.join(",", rowNames);
		if(isPlayback) {
			chartTitle = "Playback - Histogram Plot:" + String.join(",", rowNames);
		}
		this.setTitle(chartTitle);

		FrameModel histogramFrameModel = new FrameModel("Histogram",chartTitle,6);
		setModel(histogramFrameModel);

	}

	public ChartPanel makeHistogram() throws IOException {

		if (alphaSlider != null) {
			alphaSlider.setValue((int) (initAlpha * 10F));
		}
		// chart

		if(isNormalized) {
			myChart = ChartFactory.createHistogram("Density Histogram", MetaOmGraph.getActiveProject().getDefaultYAxis(), "Count", dataset, PlotOrientation.VERTICAL, true,
					true, false);
		}
		else {
			myChart = ChartFactory.createHistogram("Histogram", MetaOmGraph.getActiveProject().getDefaultYAxis(), "Count", dataset, PlotOrientation.VERTICAL, true,
					true, false);
		}

		XYPlot plot = (XYPlot) myChart.getPlot();

		// save legend
		myLegend = myChart.getLegend();
		// if legene flag is off remove legend
		if (!legendFlag) {
			myChart.removeLegend();
		}

		// bg colors
		plot.setBackgroundPaint(plotbg);
		myChart.setBackgroundPaint(chartbg);
		plot.setForegroundAlpha(initAlpha);

		myRenderer = (XYBarRenderer) plot.getRenderer();
		myRenderer.setBarPainter(new StandardXYBarPainter());
		if (colorArray != null) {
			plot.setDrawingSupplier(new DefaultDrawingSupplier(colorArray,
					DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		} else {
			Paint[] defaultPaint = DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE;
			Color[] defaultColor = Utils.paintArraytoColor(defaultPaint);
			plot.setDrawingSupplier(new DefaultDrawingSupplier(Utils.filterColors(defaultColor),
					DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		}
		chartPanel = new ChartPanel(myChart, 800, 600, 2, 2, 10000, 10000, true, true, true, true, true, true) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(ChartPanel.SAVE_COMMAND)) {
					ChartActions.exportChart(this);
				} else
					super.actionPerformed(e);
			}
		};
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.addChartMouseListener(this);

		return chartPanel;
	}

	private HistogramDataset createHistDatasetOld() throws IOException {

		HistogramDataset dataset = new HistogramDataset();
		String thisName = "";
		for (int i = 0; i < selected.length; i++) {

			double[] dataY = myProject.getIncludedData(selected[i]);
			thisName = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();
			dataset.addSeries(thisName, dataY, _bins);
		}
		return dataset;
	}

	private HistogramDataset createHistDataset() throws IOException {

		HistogramDataset dataset = new HistogramDataset();

		if(isNormalized) {
			dataset = new NormalizedHistogramDataset();
		}
		String thisName = "";
		if (splitIndex == null || splitCol == null || splitCol.length() < 1) {
			//get included data
			for (int i = 0; i < selected.length; i++) {
				double[] dataY = myProject.getIncludedData(selected[i], excludedCopy);
				thisName = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();

				dataset.addSeries(thisName, dataY, _bins);
			}

		} else {

			for (int i = 0; i < selected.length; i++) {
				double[] dataY = myProject.getAllData(selected[i]);
				thisName = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();
				for (String key : splitIndex.keySet()) {
					// seriesNames.add(key);
					Collection<Integer> thisInd = splitIndex.get(key);
					List<Double> temp = new ArrayList<>();
					// split dataY by thisInd
					if (excludedCopy == null) {
						for (Integer ind : thisInd) {
							temp.add(dataY[ind]);
						}
					} else {
						for (Integer ind : thisInd) {
							if (!excludedCopy[ind]) {
								temp.add(dataY[ind]);
							}
						}
					}
					dataset.addSeries(thisName + ";" + key, temp.stream().mapToDouble(d -> d).toArray(), _bins);
				}
			}

		}

		return dataset;
	}

	private HistogramDataset createHistDataset(double[] data) throws IOException {

		HistogramDataset dataset = new HistogramDataset();
		String thisName = "";
		dataset.addSeries(thisName, data, _bins);
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

		if ("chooseBins".equals(e.getActionCommand())) {
			int newBins = 0;
			boolean success = false;
			while (!success) {
				try {

					String input = (String) JOptionPane.showInputDialog(null, "Please Enter number of bins",
							"Input number of bins", JOptionPane.QUESTION_MESSAGE, null, null, String.valueOf(_bins));
					// cancel pressed
					if (input == null) {
						return;
					}
					newBins = Integer.parseInt(input);

					if (newBins > 0) {
						success = true;
					}

				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Invalid number entered. Please try again.", "Error",
							JOptionPane.ERROR_MESSAGE);

				}
			}

			this._bins = newBins;
			updateChart();

		}

		if ("normalizeByCount".equals(e.getActionCommand())) {

			if(isNormalized==false)
				isNormalized=true;
			else
				isNormalized=false;

			updateChart();

		}


		if ("legend".equals(e.getActionCommand())) {
			// TODO
			if (this.legendFlag) {
				this.legendFlag = false;
			} else {
				this.legendFlag = true;
			}

			setLegendVisible(legendFlag);
		}

		if ("splitDataset".equals(e.getActionCommand())) {
			// show metadata categories
			if (MetaOmGraph.getActiveProject().getMetadataHybrid() == null) {
				JOptionPane.showMessageDialog(this, "No metadata found.");
				return;
			}
			String[] options = {"Reset", "By Query", "By MetaData"};
			int selectedInd = splitCol == null? 0 : 2;			

			String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, options, options[selectedInd]);
			if (col_val == null) {
				return;
			}

			if (col_val.equals("Reset")) {
				splitCol = null;
				try {
					createHistDataset();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				updateChart();
				return;
			}

			List<String> selectedVals = new ArrayList<>();
			if (col_val.equals("By MetaData")) {
				// display jpanel with check box
				String[] fields =  MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataHeaders();
				JCheckBox[] cBoxes = new JCheckBox[fields.length];
				JPanel cbPanel = new JPanel();
				cbPanel.setLayout(new GridLayout(0, 3));
				for (int i = 0; i < fields.length; i++) {
					cBoxes[i] = new JCheckBox(fields[i]);
					cbPanel.add(cBoxes[i]);
				}
				int res = JOptionPane.showConfirmDialog(null, cbPanel, "Select categories",
						JOptionPane.OK_CANCEL_OPTION);
				if (res == JOptionPane.OK_OPTION) {
					for (int i = 0; i < fields.length; i++) {
						if (cBoxes[i].isSelected()) {
							selectedVals.add(fields[i]);
						}
					}
					splitCol = col_val;
				} else {
					return;
				}
				splitIndex = myProject.getMetadataHybrid().cluster(selectedVals);

			} else if (col_val.equals("By Query")) {
				splitCol = col_val;
				// display query panel
				final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(myProject, false);
				final MetadataQuery[] queries;
				queries = tsp.showSearchDialog();
				if (tsp.getQueryCount() <= 0) {
					System.out.println("Search dialog cancelled");
					// User didn't enter any queries
					return;
				}

				Collection<Integer> result = new ArrayList<>();
				List<Collection<Integer>> resList = new ArrayList<>();

				new AnimatedSwingWorker("Searching...", true) {
					@Override
					public Object construct() {
						ArrayList<Integer> toAdd = new ArrayList<Integer>(result.size());
						for (int i = 0; i < myProject.getDataColumnCount(); i++) {
							toAdd.add(i);
						}
						Integer[] hits = myProject.getMetadataHybrid().search(queries, tsp.matchAll());
						// remove excluded cols from list
						// urmi
						boolean[] excluded = excludedCopy;
						if (excluded != null) {
							List<Integer> temp = new ArrayList<>();
							for (Integer i : hits) {
								if (!excluded[i]) {
									temp.add(i);
								}
							}
							hits = new Integer[temp.size()];
							hits = temp.toArray(hits);
						}

						int index;
						for (index = 0; index < hits.length; index++) {
							result.add(hits[index]);
							toAdd.remove(hits[index]);
						}
						/*
						 * for (int i = 0; i < toAdd.size(); i++) { other.add(toAdd.get(i)); }
						 */
						resList.add(result);
						resList.add(toAdd);
						return null;
					}
				}.start();

				// create a split index with "hits" as one category and all others as second
				// category
				if (resList.get(0).size() < 1) {
					JOptionPane.showMessageDialog(null, "No hits found", "No hits", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				splitIndex = createSplitIndex(resList, Arrays.asList("Hits", "Other"));
			} 
			try {
				createHistDataset();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateChart();

			return;
		}

		if ("changePalette".equals(e.getActionCommand())) {

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
				// setPalette(colorArray);
				setPalette(Utils.filterColors(colorArray));

			} else {
				// reset was pressed and the OK. show default colors
				colorArray = null;
				updateChart();

			}

			return;
		}

	}

	/**
	 * create a map of name to indices
	 * 
	 * @param collList
	 * @param names
	 * @return
	 */
	private Map<String, Collection<Integer>> createSplitIndex(List<Collection<Integer>> collList, List<String> names) {
		Map<String, Collection<Integer>> res = new TreeMap();
		for (int i = 0; i < collList.size(); i++) {
			if (collList.get(i).size() > 0) {
				res.put(names.get(i), collList.get(i));
			}

		}
		return res;
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
			@Override
			public double getX() {
				return (maxx - maxy) / 2.0D;
			}

			@Override
			public double getY() {
				return (maxy - miny) / 2;
			}

			@Override
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

		if (event.getTrigger()
				.getClickCount() == 1) {/*
				 * if (event.getEntity() instanceof LegendItemEntity) { Comparable seriesKey =
				 * ((LegendItemEntity) event.getEntity()).getSeriesKey(); int index =
				 * myChart.getXYPlot().getDataset().indexOf(seriesKey);
				 * JOptionPane.showMessageDialog(null, "ToFront" +
				 * myChart.getXYPlot().getDatasetRenderingOrder().toString() + ":" +
				 * seriesKey.toString() + "," + dataset.getDomainOrder().toString());
				 * bringToFront(seriesKey.toString());
				 * 
				 * return; } else if (event.getEntity() instanceof XYItemEntity) {
				 * 
				 * return; }
				 */
		}

	}

	@Override
	public void chartMouseMoved(ChartMouseEvent arg0) {
		// TODO Auto-generated method stub

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
			// Create dataset
			if (histType == 1) {
				dataset = createHistDataset();

			} else if (histType == 2) {
				dataset = createHistDataset(plotData);

			}

			if(isNormalized) {
				if(normalizeButton != null) {
					normalizeButton.setText("Frequency Histogram");
				}
			}
			else {
				if(normalizeButton != null) {
					normalizeButton.setText("Density Histogram");
				}
			}

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

	private void updateChartAlpha(float alpha) {
		myChart.getXYPlot().setForegroundAlpha(alpha);

	}

	/**
	 * Sets the legend visible
	 * 
	 * @param legendVisible
	 *            <code>boolean</code> variable which adds the legend when true else
	 *            removes it
	 */
	public void setLegendVisible(boolean legendVisible) {
		if (legendVisible) {
			myChart.addLegend(myLegend);
		} else {
			myChart.removeLegend();
		}
	}

	/*
	 * private void bringToFront(String seriesKey) { HistogramDataset temp = new
	 * HistogramDataset(); for (int i = 0; i < dataset.getSeriesCount(); i++) {
	 * String thisKey = dataset.getSeriesKey(i).toString(); if
	 * (thisKey.equals(seriesKey)) { int icount = dataset.getItemCount(i); double[]
	 * data = new double[icount]; for (int j = 0; j < icount; j++) { data[j] =
	 * dataset.getYValue(i, j); } temp.addSeries(thisKey, data, _bins); //
	 * dataset.re // temp. } } dataset = temp; updateChart(); }
	 */

}
