package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultFormatterFactory;

import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.Outlier;
import org.jfree.chart.renderer.OutlierList;
import org.jfree.chart.renderer.OutlierListCollection;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class BoxPlot extends JInternalFrame implements ChartMouseListener, ActionListener {

	// hasmap mapping feature num to expression data or datacol to sample depending
	// on the box plot
	HashMap<Integer, double[]> plotData;
	// plot type 0 for feature 1 for sample
	int plotType;
	String[] rowNames;
	private MetaOmProject myProject;
	private String xAxisname;
	private ChartToolBar myToolbar;
	private ChartPanel chartPanel;
	private JFreeChart myChart;

	// private XYLineAndShapeRenderer myRenderer;
	private BoxAndWhiskerRenderer myRenderer;
	JScrollPane scrollPane;

	// toolbar buttons
	private JButton properties;
	private JButton save;
	private JButton print;
	private JButton zoomIn;
	private JButton zoomOut;
	private JButton defaultZoom;
	private JButton changePalette;
	private JButton splitDataset;

	// bottom toolbar
	private JButton btnNewButton_1;

	public static final String ZOOM_IN_COMMAND = "zoomIn";
	public static final String ZOOM_OUT_COMMAND = "zoomOut";
	public static final String ZOOM_DEFAULT_COMMAND = "defaultZoom";

	// chart colors
	private Color chartbg = MetaOmGraph.getChartBackgroundColor();
	private Color plotbg = MetaOmGraph.getPlotBackgroundColor();

	Color[] colorArray = null;

	DefaultBoxAndWhiskerCategoryDataset initdataset;
	String splitCol;
	List<String> seriesNames;
	Map<String, Collection<Integer>> splitIndex;

	private boolean[] excludedCopy;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	}

	/**
	 * Create the frame.
	 */
	public BoxPlot(HashMap<Integer, double[]> plotData, int pType, MetaOmProject mp) {

		this.plotData = plotData;
		this.plotType = pType;
		myProject = mp;
		if (plotType == 0) {
			// make copy of excluded
			boolean[] excluded = MetaOmAnalyzer.getExclude();
			if (excluded != null) {
				excludedCopy = new boolean[excluded.length];
				System.arraycopy(excluded, 0, excludedCopy, 0, excluded.length);
			}
		}
		chartPanel = null;
		// init rownames
		rowNames = initRowNames(plotData.keySet(), pType);
		// JOptionPane.showMessageDialog(null, Arrays.toString(rowNames));
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		getContentPane().add(panel, BorderLayout.NORTH);
		JButton btnNewButton = new JButton("New button");
		// panel.add(btnNewButton);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		btnNewButton_1 = new JButton("Change X axis");
		btnNewButton_1.setActionCommand("chooseX");
		btnNewButton_1.addActionListener(this);
		// panel_1.add(btnNewButton_1);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// create sample plot
		initdataset = createDataset();

		try {
			chartPanel = makeBoxPlot(initdataset);
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

		if (plotType == 0) {
			splitDataset = new JButton(theme.getSort());
			splitDataset.setToolTipText("Split by categories");
			splitDataset.setActionCommand("splitDataset");
			splitDataset.addActionListener(this);
		}

		panel.add(properties);
		panel.add(save);
		panel.add(print);
		// panel.add(zoomIn);
		// panel.add(zoomOut);
		panel.add(defaultZoom);
		if (plotType == 0) {
			panel.add(splitDataset);
		}
		panel.add(changePalette);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		String chartTitle = "Box Plot:" + String.join(",", rowNames);
		this.setTitle(chartTitle);
	}

	public ChartPanel makeBoxPlot(DefaultBoxAndWhiskerCategoryDataset dataset) throws IOException {

		JFreeChart myChart = ChartFactory.createBoxAndWhiskerChart("BoxPlot", "Sample", "Value", dataset, true);

		// urmi add chat options
		myRenderer = getBoxAndWhiskerRenderer();
		myRenderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		myRenderer.setFillBox(true);
		myRenderer.setMeanVisible(false);
		myChart.getCategoryPlot().getDomainAxis()
				.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(1.5707963267948966D));
		myChart.getCategoryPlot().setRenderer(myRenderer);
		myChart.getCategoryPlot().setBackgroundPaint(MetaOmGraph.getPlotBackgroundColor());
		myChart.setBackgroundPaint(MetaOmGraph.getChartBackgroundColor());

		myChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(

				CategoryLabelPositions.createUpRotationLabelPositions(1.5707963267948966D));
		ChartPanel chartPanel = new ChartPanel(myChart, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height, 0, 0,
				Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height,
				true, true, true, true, true, true);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		chartPanel.addChartMouseListener(this);
		setDefaultPalette();

		return chartPanel;

	}

	private DefaultBoxAndWhiskerCategoryDataset createDataset() {
		DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Working",
				"Generating BoxPlot", 0L, plotData.size(), true);
		new Thread() {
			public void run() {

				seriesNames = new ArrayList<>();
				if (splitIndex == null || splitCol == null || splitCol.length() < 1) {
					// no split
					int n = 0;
					for (int rKey : plotData.keySet()) {
						List<Double> list = new ArrayList();
						double[] thisData = plotData.get(rKey);
						for (int j = 0; j < thisData.length; j++) {
							if (excludedCopy == null) {
								list.add(thisData[j]);
							} else {
								if (!excludedCopy[j]) {
									list.add(thisData[j]);
								}
							}
						}
						dataset.add(list, "All", rowNames[n++]);
					}
					seriesNames.add("All");
				} else {

					for (String key : splitIndex.keySet()) {
						seriesNames.add(key);
						Collection<Integer> thisInd = splitIndex.get(key);
						int n = 0;
						for (int rKey : plotData.keySet()) {
							List list = new ArrayList();
							double[] thisData = plotData.get(rKey);
							for (int ind : thisInd) {
								if (excludedCopy == null) {
									list.add(thisData[ind]);
								} else {
									if (!excludedCopy[ind]) {
										list.add(thisData[ind]);
									}
								}
							}
							dataset.add(list, key, rowNames[n++]);

						}
					}
				}

				progress.dispose();
			}
		}.start();
		progress.setVisible(true);
		if (progress.isCanceled()) {
			return null;
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
			ColorPaletteChooserDialog dialog = new ColorPaletteChooserDialog();
			ColorBrewer cb = null;
			dialog.setModal(true);
			dialog.show();
			if (dialog.wasOKPressed()) {
				cb = dialog.getColorPalette();
			}

			if (cb != null) {
				int numColors = seriesNames.size();
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

		if ("splitDataset".equals(e.getActionCommand())) {

			// show metadata categories
			if (MetaOmGraph.getActiveProject().getMetadataHybrid() == null) {
				JOptionPane.showMessageDialog(this, "No metadata found.");
				return;
			}
			String[] fields = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataHeaders();
			String[] fields2 = new String[fields.length + 3];
			fields2[0] = "Reset";
			int selectedInd = 0;
			for (int i = 0; i < fields.length; i++) {
				fields2[i + 1] = fields[i];
				if (splitCol != null && splitCol.equals(fields2[i + 1])) {
					selectedInd = i + 1;
				}
			}
			fields2[fields2.length - 2] = "By Query";
			fields2[fields2.length - 1] = "More...";
			String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, fields2, fields2[selectedInd]);
			if (col_val == null) {
				return;
			}

			if (col_val.equals("Reset")) {
				splitCol = null;
				splitIndex = null;
				// createDataset();
				updateChart();
				return;
			}

			List<String> selectedVals = new ArrayList<>();
			if (col_val.equals("More...")) {
				// display jpanel with check box
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
				final int[] result = new int[myProject.getDataColumnCount()];
				final boolean nohits;
				new AnimatedSwingWorker("Searching...", true) {
					@Override
					public Object construct() {
						ArrayList<Integer> toAdd = new ArrayList<Integer>(result.length);
						for (int i = 0; i < result.length; i++) {
							toAdd.add(i);
						}
						Integer[] hits =myProject.getMetadataHybrid().search(queries, tsp.matchAll());
						// remove excluded cols from list to display corect range markers
						// urmi
						boolean[] excluded = excludedCopy;
						if (excluded != null) {
							java.util.List<Integer> temp = new ArrayList<>();
							for (Integer i : hits) {
								if (!excluded[i]) {
									temp.add(i);
								}
							}
							hits = new Integer[temp.size()];
							hits = temp.toArray(hits);
						}

						// return if no hits
						if (hits.length == 0) {
							// JOptionPane.showMessageDialog(null, "hits len:"+hits.length);
							// nohits=true;
							result[0] = -1;
							return null;
						}
						int index;
						for (index = 0; index < hits.length; index++) {
							result[index] = hits[index];
							toAdd.remove(hits[index]);
						}
						for (int i = 0; i < toAdd.size(); i++) {
							result[index++] = toAdd.get(i);
						}
						return null;
					}
				}.start();
				
				//create a split index with "hits" as one category and all others as second  category
				
			}

			else {
				// split data set by values of col_val
				selectedVals.add(col_val);
				splitCol = col_val;
			}

			splitIndex = myProject.getMetadataHybrid().cluster(selectedVals);

			// createDataset();
			updateChart();

			return;
		}

	}

	private void setPalette(Color[] colors) {
		if (colors == null) {
			return;
		}
		int seriesCount = seriesNames.size();
		for (int i = 0; i < seriesCount; i++) {
			// call change series color
			changeSeriesColor(i, colors[i % colors.length]);
		}
	}

	private void setDefaultPalette() {
		ColorBrewer[] qlPalettes = ColorBrewer.getQualitativeColorPalettes(false);
		// choose default
		ColorBrewer myBrewer = qlPalettes[6];
		Color[] myFills = myBrewer.getColorPalette(seriesNames.size());
		setPalette(myFills);
	}

	private Point2D getCenterPoint() {
		JFreeChart myChart = this.myChart;
		CategoryAxis domain = myChart.getCategoryPlot().getDomainAxis();
		ValueAxis range = myChart.getCategoryPlot().getRangeAxis();

		double minx = domain.getLowerMargin();
		final double maxx = domain.getUpperMargin();
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
				// JOptionPane.showMessageDialog(null, "indexcol:"+seriesKey.toString());
				// int index = myChart.getXYPlot().getDataset().indexOf(seriesKey);
				// int index=seriesNames.indexOf(seriesKey.toString())+1;
				// JOptionPane.showMessageDialog(null, "SR:"+seriesNames.toString());
				int index = seriesNames.indexOf(seriesKey.toString());
				// JOptionPane.showMessageDialog(null, "SR:"+seriesNames.toString()+"ind
				// of:"+index);
				changeSeriesColor(index);

				return;
			} else if (event.getEntity() instanceof CategoryItemEntity) {
				// changeSeriesColor(((CategoryItemEntity) event.getEntity()).get);
				//
				String ck = ((CategoryItemEntity) event.getEntity()).getColumnKey().toString();
				JOptionPane.showMessageDialog(null, "CLICKED2:" + ck);
				return;
			}
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

		Color newColor = JColorChooser.showDialog(MetaOmGraph.getMainWindow(), seriesNames.get(series) + " color",
				oldColor);
		if (newColor != null) {
			myRenderer.setSeriesPaint(series, newColor);
			// myRenderer.setpaint
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
			initdataset = createDataset();
			this.chartPanel = makeBoxPlot(initdataset);
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

	private String[] initRowNames(Set<Integer> keyset, int type) {
		int[] selectedInd = new int[keyset.size()];
		int k = 0;
		for (int i : keyset) {
			selectedInd[k++] = i;
		}

		if (type == 0) {
			return myProject.getDefaultRowNames(selectedInd);
		} else if (type == 1) {
			return myProject.getDataColumnHeaders(selectedInd);
		}

		return null;
	}

	///////////////////////// BoxPlot Renderer
	///////////////////////// functions*//////////////////////////////////////////////
	public static BoxAndWhiskerRenderer getBoxAndWhiskerRenderer() {
		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer() {
			@Override
			public void drawVerticalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea,
					CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row,
					int column) {

				BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset) dataset;

				double categoryEnd = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea,
						plot.getDomainAxisEdge());
				double categoryStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
						plot.getDomainAxisEdge());
				double categoryWidth = categoryEnd - categoryStart;

				double xx = categoryStart;
				int seriesCount = getRowCount();
				int categoryCount = getColumnCount();

				if (seriesCount > 1) {
					double seriesGap = dataArea.getWidth() * getItemMargin() / (categoryCount * (seriesCount - 1));
					double usedWidth = (state.getBarWidth() * seriesCount) + (seriesGap * (seriesCount - 1));
					// offset the start of the boxes if the total width used is smaller
					// than the category width
					double offset = (categoryWidth - usedWidth) / 2;
					xx = xx + offset + (row * (state.getBarWidth() + seriesGap));
				} else {
					// offset the start of the box if the box width is smaller than the
					// category width
					double offset = (categoryWidth - state.getBarWidth()) / 2;
					xx = xx + offset;
				}

				double yyAverage;
				double yyOutlier;

				Paint itemPaint = getItemPaint(row, column);
				g2.setPaint(itemPaint);
				Stroke s = getItemStroke(row, column);
				g2.setStroke(s);

				double aRadius = 0; // average radius

				org.jfree.chart.ui.RectangleEdge location = plot.getRangeAxisEdge();

				Number yQ1 = bawDataset.getQ1Value(row, column);
				Number yQ3 = bawDataset.getQ3Value(row, column);
				Number yMax = bawDataset.getMaxRegularValue(row, column);
				Number yMin = bawDataset.getMinRegularValue(row, column);
				Shape box = null;
				if (yQ1 != null && yQ3 != null && yMax != null && yMin != null) {

					double yyQ1 = rangeAxis.valueToJava2D(yQ1.doubleValue(), dataArea, location);
					double yyQ3 = rangeAxis.valueToJava2D(yQ3.doubleValue(), dataArea, location);
					double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
					double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
					double xxmid = xx + state.getBarWidth() / 2.0;
					double halfW = (state.getBarWidth() / 2.0) * getWhiskerWidth();

					// draw the body...
					box = new Rectangle2D.Double(xx, Math.min(yyQ1, yyQ3), state.getBarWidth(), Math.abs(yyQ1 - yyQ3));
					if (getFillBox()) {
						g2.fill(box);
					}

					Paint outlinePaint = getItemOutlinePaint(row, column);
					if (getUseOutlinePaintForWhiskers()) {
						g2.setPaint(outlinePaint);
					}
					// draw the upper shadow...
					g2.draw(new Line2D.Double(xxmid, yyMax, xxmid, yyQ3));
					g2.draw(new Line2D.Double(xxmid - halfW, yyMax, xxmid + halfW, yyMax));

					// draw the lower shadow...
					g2.draw(new Line2D.Double(xxmid, yyMin, xxmid, yyQ1));
					g2.draw(new Line2D.Double(xxmid - halfW, yyMin, xxmid + halfW, yyMin));

					g2.setStroke(getItemOutlineStroke(row, column));
					g2.setPaint(outlinePaint);
					g2.draw(box);
				}

				g2.setPaint(getArtifactPaint());

				// draw mean - SPECIAL AIMS REQUIREMENT...
				if (isMeanVisible()) {
					Number yMean = bawDataset.getMeanValue(row, column);
					if (yMean != null) {
						yyAverage = rangeAxis.valueToJava2D(yMean.doubleValue(), dataArea, location);
						aRadius = state.getBarWidth() / 4;
						// here we check that the average marker will in fact be
						// visible before drawing it...
						if ((yyAverage > (dataArea.getMinY() - aRadius))
								&& (yyAverage < (dataArea.getMaxY() + aRadius))) {
							Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx + aRadius, yyAverage - aRadius,
									aRadius * 2, aRadius * 2);
							g2.fill(avgEllipse);
							g2.draw(avgEllipse);
						}
					}
				}

				// draw median...
				if (isMedianVisible()) {
					Number yMedian = bawDataset.getMedianValue(row, column);
					if (yMedian != null) {
						double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
						g2.draw(new Line2D.Double(xx, yyMedian, xx + state.getBarWidth(), yyMedian));
					}
				}

				// draw yOutliers...
				double maxAxisValue = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, location) + aRadius;
				double minAxisValue = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, location) - aRadius;

				g2.setPaint(itemPaint);

				// draw outliers
				// double oRadius = 0 == null ? state.getBarWidth() / 3 : outlierRadius; //
				// outlier radius
				// display no outliers
				double oRadius = 0;
				List outliers = new ArrayList();
				OutlierListCollection outlierListCollection = new OutlierListCollection();

				// From outlier array sort out which are outliers and put these into a
				// list If there are any farouts, set the flag on the
				// OutlierListCollection
				List yOutliers = bawDataset.getOutliers(row, column);
				if (yOutliers != null) {
					for (int i = 0; i < yOutliers.size(); i++) {
						double outlier = ((Number) yOutliers.get(i)).doubleValue();
						Number minOutlier = bawDataset.getMinOutlier(row, column);
						Number maxOutlier = bawDataset.getMaxOutlier(row, column);
						Number minRegular = bawDataset.getMinRegularValue(row, column);
						Number maxRegular = bawDataset.getMaxRegularValue(row, column);
						if (outlier > maxOutlier.doubleValue()) {
							outlierListCollection.setHighFarOut(true);
						} else if (outlier < minOutlier.doubleValue()) {
							outlierListCollection.setLowFarOut(true);
						} else if (outlier > maxRegular.doubleValue()) {
							yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
							outliers.add(new Outlier(xx + state.getBarWidth() / 2.0, yyOutlier, oRadius));
						} else if (outlier < minRegular.doubleValue()) {
							yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
							outliers.add(new Outlier(xx + state.getBarWidth() / 2.0, yyOutlier, oRadius));
						}
						Collections.sort(outliers);
					}

					// Process outliers. Each outlier is either added to the
					// appropriate outlier list or a new outlier list is made
					for (Iterator iterator = outliers.iterator(); iterator.hasNext();) {
						Outlier outlier = (Outlier) iterator.next();
						outlierListCollection.add(outlier);
					}

					for (Iterator iterator = outlierListCollection.iterator(); iterator.hasNext();) {
						OutlierList list = (OutlierList) iterator.next();
						Outlier outlier = list.getAveragedOutlier();
						java.awt.geom.Point2D point = outlier.getPoint();

						if (list.isMultiple()) {
							drawMultipleEllipse(point, state.getBarWidth(), oRadius, g2);
						} else {
							drawEllipse(point, oRadius, g2);

						}
					}

					// draw farout indicators
					if (outlierListCollection.isHighFarOut()) {
						drawHighFarOut(aRadius / 2.0, g2, xx + state.getBarWidth() / 2.0, maxAxisValue);
					}

					if (outlierListCollection.isLowFarOut()) {
						drawLowFarOut(aRadius / 2.0, g2, xx + state.getBarWidth() / 2.0, minAxisValue);
					}
				}
				// collect entity and tool tip information...
				if (state.getInfo() != null && box != null) {
					EntityCollection entities = state.getEntityCollection();
					if (entities != null) {
						addItemEntity(entities, dataset, row, column, box);
					}
				}

			}

		};

		return renderer;
	}

	/**
	 * Draws two dots to represent the average value of more than one outlier.
	 *
	 * @param point
	 *            the location
	 * @param boxWidth
	 *            the box width.
	 * @param oRadius
	 *            the radius.
	 * @param g2
	 *            the graphics device.
	 */
	private static void drawMultipleEllipse(Point2D point, double boxWidth, double oRadius, Graphics2D g2) {

		Ellipse2D dot1 = new Ellipse2D.Double(point.getX() - (boxWidth / 2) + oRadius, point.getY(), oRadius, oRadius);
		Ellipse2D dot2 = new Ellipse2D.Double(point.getX() + (boxWidth / 2), point.getY(), oRadius, oRadius);
		g2.draw(dot1);
		g2.draw(dot2);
	}

	/**
	 * Draws a dot to represent an outlier.
	 *
	 * @param point
	 *            the location.
	 * @param oRadius
	 *            the radius.
	 * @param g2
	 *            the graphics device.
	 */
	private static void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
		Ellipse2D dot = new Ellipse2D.Double(point.getX() + oRadius / 2, point.getY(), oRadius, oRadius);
		g2.draw(dot);
	}

	/**
	 * Draws a triangle to indicate the presence of far-out values.
	 *
	 * @param aRadius
	 *            the radius.
	 * @param g2
	 *            the graphics device.
	 * @param xx
	 *            the x coordinate.
	 * @param m
	 *            the y coordinate.
	 */
	private static void drawHighFarOut(double aRadius, Graphics2D g2, double xx, double m) {
		double side = aRadius * 2;
		g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
		g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
		g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
	}

	/**
	 * Draws a triangle to indicate the presence of far-out values.
	 *
	 * @param aRadius
	 *            the radius.
	 * @param g2
	 *            the graphics device.
	 * @param xx
	 *            the x coordinate.
	 * @param m
	 *            the y coordinate.
	 */
	private static void drawLowFarOut(double aRadius, Graphics2D g2, double xx, double m) {
		double side = aRadius * 2;
		g2.draw(new Line2D.Double(xx - side, m - side, xx + side, m - side));
		g2.draw(new Line2D.Double(xx - side, m - side, xx, m));
		g2.draw(new Line2D.Double(xx + side, m - side, xx, m));
	}

}