package edu.iastate.metnet.metaomgraph.chart;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;

/**
 * @author sumanth
 *	Copy of the ScatterPlotChart.java modified to plot PCA data.
 */
public class ScatterPlotPCA extends TaskbarInternalFrame implements ChartMouseListener, ActionListener {

	private int[] selected;
	// pivotIndex is the ith index in the selected rows which is the x axis for the
	// scatterplot
	private int pivotIndex;
	private String[] rowNames;
	private String[] selectedDataCols;
	private String xAxisname;
	private String yAxisname;
	private ChartPanel chartPanel;
	private JFreeChart myChart;
	private XYDataset dataset;
	
	private double[][] data;
	private MetadataCollection metaDataColl;

	private double pointSize = 7.0;
	// private XYLineAndShapeRenderer myRenderer;
	private XYItemRenderer myRenderer;
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
	// for hide and show legends
	private LegendTitle myLegend;
	private JToggleButton toggleLegend;
	private boolean legendFlag = true;
	// if number of items in legend is more than this then turn legend off
	private int maxLegend = 30;

	// bottom toolbar
	private JButton btnNewButton_1;

	public static final String ZOOM_IN_COMMAND = "zoomIn";
	public static final String ZOOM_OUT_COMMAND = "zoomOut";
	public static final String ZOOM_DEFAULT_COMMAND = "defaultZoom";

	private boolean[] excludedCopy;

	// chart colors
	private Color chartbg = MetaOmGraph.getChartBackgroundColor();
	private Color plotbg = MetaOmGraph.getPlotBackgroundColor();

	// keep track of user selected palette and retain this on updating the chart
	private Color[] currentlySetColors = null;

	private String splitCol;
	private Map<String, Collection<Integer>> splitIndex;
	private HashMap<String, String> dataColNameToolTipMap;
	private JSpinner spinner;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ScatterPlotChart frame = new ScatterPlotChart(null, 0, null, false);
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
	public ScatterPlotPCA(double[][] data, String[] rowNames, String[] selectedDataCols,
			String xLabel, String yLabel, boolean isPlayback) {
		super("Scatter Plot");
		
		this.data = data;
		this.xAxisname = xLabel;
		this.yAxisname = yLabel;
		this.selectedDataCols = selectedDataCols;
		this.rowNames = rowNames;
		this.metaDataColl = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataCollection();
		
		chartPanel = null;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		getContentPane().add(panel, BorderLayout.NORTH);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		btnNewButton_1 = new JButton("Change X axis");
		btnNewButton_1.setActionCommand("chooseX");
		btnNewButton_1.addActionListener(this);
		panel_1.add(btnNewButton_1);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// create sample plot

		try {
			chartPanel = makeScatterPlot();
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

		splitDataset = new JButton(theme.getSort());
		splitDataset.setToolTipText("Split by categories");
		splitDataset.setActionCommand("splitDataset");
		splitDataset.addActionListener(this);

		changePalette = new JButton(theme.getPalette());
		changePalette.setToolTipText("Color Palette");
		changePalette.setActionCommand("changePalette");
		changePalette.addActionListener(this);
		changePalette.setOpaque(false);
		changePalette.setContentAreaFilled(false);
		changePalette.setBorderPainted(true);

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

		spinner = new JSpinner();
		spinner.setToolTipText("Changes plot point size");
		spinner.setModel(new SpinnerNumberModel(pointSize, 1.0, 20.0, 1.0));
		// set uneditable
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
		panel.add(spinner);
		// add change listener
		ChangeListener listener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pointSize = (double) spinner.getValue();
				updateChart();
			}
		};

		spinner.addChangeListener(listener);
		/////////////////

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		String chartTitle = "Scatter Plot:" + String.join(",", rowNames);
		if (isPlayback) {
			chartTitle = "Playback - Scatter Plot:" + String.join(",", rowNames);
		}
		this.setTitle(chartTitle);

		FrameModel scatterPlotFrameModel = new FrameModel("Scatter Plot", chartTitle, 3);
		this.setModel(scatterPlotFrameModel);

	}

	public ChartPanel makeScatterPlot() throws IOException {
		
		// Create dataset
		dataset = createDataset();
		// Create chart
		myChart = ChartFactory.createScatterPlot("", this.xAxisname, this.yAxisname, dataset);

		// Changes background color
		XYPlot plot = (XYPlot) myChart.getPlot();
		plot.setBackgroundPaint(plotbg);
		myChart.setBackgroundPaint(chartbg);

		// save legend
		myLegend = myChart.getLegend();
		// if legene flag is off remove legend
		if (!legendFlag) {
			myChart.removeLegend();
		}

		myRenderer = plot.getRenderer();

		// use palette if available
		if (currentlySetColors != null) {
			plot.setDrawingSupplier(
					new DefaultDrawingSupplier(currentlySetColors, DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
							DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
							DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
							DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, getShapesSequence(pointSize)

					));
			// DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE

		} else {
			// redraw plot
			Paint[] defaultPaint = DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE;
			Color[] defaultColor = Utils.paintArraytoColor(defaultPaint);
			plot.setDrawingSupplier(new DefaultDrawingSupplier(Utils.filterColors(defaultColor),
					DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, getShapesSequence(pointSize)));
			// apply default palette
			setDefaultPalette();
		}
		// Create Panel
		// use full constructor otherwise tooltips dont work
		ChartPanel chartPanel = new ChartPanel(myChart, 800, 600, 2, 2, 10000, 10000, true, true, true, true, true,
				true) {
			private Dimension oldSize = new Dimension(100, 100);

			@Override
			public void paintComponent(Graphics g) {
				if (!oldSize.equals(getSize())) {
					oldSize = getSize();
					if (myChart.getBackgroundPaint() instanceof GradientPaint) {
						GradientPaint gp = (GradientPaint) myChart.getBackgroundPaint();
						myChart.setBackgroundPaint(Color.WHITE);
					}
				}

				super.paintComponent(g);
				// Custom drawing
				Graphics2D g2d = (Graphics2D) g.create();
				Color bg = (Color) myChart.getPlot().getBackgroundPaint();
				g2d.setColor(new Color(Color.WHITE.getRGB() - bg.getRGB()));
				g2d.dispose();
			}

			@Override
			public void actionPerformed(ActionEvent e) {

				if (e.getActionCommand().equals(ChartPanel.SAVE_COMMAND)) {
					ChartActions.exportChart(this);

				} else
					super.actionPerformed(e);
			}

			@Override
			public String getToolTipText(MouseEvent event) {
				if (metaDataColl == null) {
					return null;
				}
		
				ChartEntity entity = getChartRenderingInfo().getEntityCollection().getEntity(event.getPoint().getX(),
						event.getPoint().getY());
				if (!(entity instanceof XYItemEntity)) {
					return null;
				}
				XYItemEntity item = (XYItemEntity) entity;
				int thisXind = item.getItem();
				// get x and y points
				XYDataset thisDS = item.getDataset();
				double chartX = thisDS.getXValue(item.getSeriesIndex(), thisXind);
				double chartY = thisDS.getYValue(item.getSeriesIndex(), thisXind);
				
				String thisSeries = "";
				
				if(splitIndex != null) {
					String seriesNameKey = String.valueOf(chartX) + " " +
							String.valueOf(chartY);
					thisSeries = dataColNameToolTipMap.get(seriesNameKey);
				}
				else {
					String toolTipKey = String.valueOf(chartX) + " " +
							String.valueOf(chartY);
					thisSeries = dataColNameToolTipMap.get(toolTipKey);
				}
				return createTooltipTable(thisSeries, chartX, chartY);
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

		chartPanel.addChartMouseListener(this);

		return chartPanel;

	}

	private XYDataset createDataset() throws IOException {
		XYSeriesCollection dataset = new XYSeriesCollection();
		double[] dataX = data[0];
		double[] dataY = data[1];
		dataColNameToolTipMap = new HashMap<>();
		if(splitIndex != null) {
			for (String key : splitIndex.keySet()) {
				Collection<Integer> thisInd = splitIndex.get(key);
				
				XYSeries series = new XYSeries(rowNames[0] + " vs " + rowNames[1] + "(" + key + ")");
				for(int index : thisInd) {
					series.add(dataX[index], dataY[index]);
					String seriesNameKey = String.valueOf(dataX[index]) + " " +
							String.valueOf(dataY[index]);
					dataColNameToolTipMap.put(seriesNameKey, selectedDataCols[index]);
				}
				dataset.addSeries(series);
			}
		} else {
			for(int i = 1; i < data.length; i++) {
				XYSeries series = new XYSeries(rowNames[0] + " vs " + rowNames[1]);
				for(int j = 0; j < dataX.length; j++) {
					series.add(dataX[j], dataY[j]);
					String keyForToolTip = String.valueOf(dataX[j]) + " " +
							String.valueOf(dataY[j]);
					dataColNameToolTipMap.put(keyForToolTip, selectedDataCols[j]);
				}
				dataset.addSeries(series);
			}
		}

		return dataset;
	}

	private String createTooltipTable(String seriesName, double x, double y) {
		DecimalFormat df = new DecimalFormat("####0.0000");
		String bgColor = "#" + Integer.toHexString(MetaOmGraph.getTableColor1().getRGB()).substring(2);
		;
		String bgColorAlt = "#" + Integer.toHexString(MetaOmGraph.getTableColor2().getRGB()).substring(2);
		String[] rowColors = { bgColor, bgColorAlt };
		String text = "<html><head> " + "<style>" + ".scrollit {\n" + "    overflow:scroll;\n" + "    height:100px;\n"
				+ "}" + "</style></head><body>"

				+ "<div class=\"scrollit\"> <table bgcolor=\"#FFFFFF\" width=\"400\">" + " <tr>\n"
				+ "            <th>Attribute</th>\n" + "            <th >Value</th>\n" + "        </tr>";

		text += "<tr bgcolor=" + rowColors[1] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Point", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText("(" + df.format(x) + "," + df.format(y) + ")", 100, "<br>")
				+ "</font></td>";
		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[0] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Series", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(seriesName, 100, "<br>") + "</font></td>";
		text += "</tr>";

		if (MetaOmGraph.getActiveProject().getMetadataHybrid() == null) {
			return text;
		}
		
		HashMap<String, String> rowValsMap = metaDataColl.getDataColumnRowMap(seriesName);
		
		// if nothing is returned. this should not happen.
		if (rowValsMap.isEmpty()) {
			return "Error. Metadata not found!!";
		}

		int maxrowsinMD = 40;
		int maxStringLen = 500;

		int colorIndex = 0;
		int i = 0;
		for (Map.Entry<String, String> entry : rowValsMap.entrySet()) {
			if (i++ == maxrowsinMD) {
				text += "<tr bgcolor=" + rowColors[colorIndex] + ">";
				text += "<td><font size=-2>" + "..." + "</font></td>";
				text += "<td><font size=-2>" + "..." + "</font></td>";
				text += "</tr>";
				break;
			}
			
			String thisAtt = entry.getKey();
			String thisData = entry.getValue();
			if (thisData.length() > maxStringLen) {
				thisData = thisData.substring(0, maxStringLen) + "...";
			}

			text += "<tr bgcolor=" + rowColors[colorIndex] + ">";
			text += "<td><font size=-2>" + Utils.wrapText(thisAtt.trim(), 100, "<br>") + "</font></td>";
			text += "<td><font size=-2>" + Utils.wrapText(thisData.trim(), 100, "<br>") + "</font></td>";

			text += "</tr>";
			colorIndex = (colorIndex + 1) % rowColors.length;

		}

		if (rowValsMap.size() == 0 || rowValsMap == null) {
			text += "<tr bgcolor=" + rowColors[colorIndex] + ">";
			text += "<td><font size=-2>" + "There is no metadata" + "<br>" + "</font></td>";
			text += "<td><font size=-2>" + "" + "<br>" + "</font></td>";
			text += "</tr>";
		}

		text += "</table> </div> </body></html>";

		return text;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

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

		if ("chooseX".equals(e.getActionCommand())) {
			String[] options = rowNames;
			String selectedValue = (String) JOptionPane.showInputDialog(null, "Select a row:", "Rows...",
					JOptionPane.QUESTION_MESSAGE, null, options, options[pivotIndex]);
			if (selectedValue == null || selectedValue.length() < 1) {
				return;
			}

			for (int i = 0; i < options.length; i++) {
				if (selectedValue.equals(options[i])) {
					this.pivotIndex = i;
					break;
				}
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
				Color[] colorArray = cb.getColorPalette(numColors);
				setPalette(Utils.filterColors(colorArray));
				currentlySetColors = colorArray;
			} 

			return;
		}

		if ("splitDataset".equals(e.getActionCommand())) {
			// show metadata categories
			if (metaDataColl == null) {
				JOptionPane.showMessageDialog(this, "No metadata found.");
				return;
			}
			String[] options = {"By Metadata", "By Query", "Reset"};	

			String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (col_val == null) {
				return;
			}

			if (col_val.equals("Reset")) {
				splitCol = null;
				splitIndex = null;
				try {
					createDataset();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				updateChart();
				return;
			}

			List<String> selectedVals = new ArrayList<>();
			if (col_val.equals("By Metadata")) {
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
				List<String> dataCols = Arrays.asList(selectedDataCols);
				splitIndex = MetaOmGraph.getActiveProject().getMetadataHybrid().cluster(selectedVals, dataCols);

			} else if (col_val.equals("By Query")) {
				splitCol = col_val;
				// display query panel
				final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(MetaOmGraph.getActiveProject(), false);
				final MetadataQuery[] queries;
				queries = tsp.showSearchDialog();
				if (tsp.getQueryCount() <= 0) {
					System.out.println("Search dialog cancelled");
					// User didn't enter any queries
					return;
				}
				// final int[] result = new int[myProject.getDataColumnCount()];
				Collection<Integer> result = new ArrayList<>();
				List<Collection<Integer>> resList = new ArrayList<>();
				final boolean nohits;
				new AnimatedSwingWorker("Searching...", true) {
					@Override
					public Object construct() {
						ArrayList<Integer> toAdd = new ArrayList<Integer>(result.size());
						for (int i = 0; i < MetaOmGraph.getActiveProject().getDataColumnCount(); i++) {
							toAdd.add(i);
						}
						Integer[] hits = MetaOmGraph.getActiveProject().getMetadataHybrid().search(queries, tsp.matchAll());
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
				createDataset();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateChart();

			return;
		}

		// show hide legend
		if ("legend".equals(e.getActionCommand())) {
			// TODO
			if (this.legendFlag) {
				this.legendFlag = false;
			} else {
				this.legendFlag = true;
			}
			setLegendVisible(legendFlag);
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

	private void setDefaultPalette() {
		ColorBrewer[] qlPalettes = ColorBrewer.getQualitativeColorPalettes(false);
		// choose default
		ColorBrewer myBrewer = qlPalettes[1];
		XYPlot plot = (XYPlot) myChart.getPlot();
		Color[] myFills = myBrewer.getColorPalette(plot.getSeriesCount());
		setPalette(myFills);
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

	}

	@Override
	public void chartMouseMoved(ChartMouseEvent arg0) {

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
			this.chartPanel = makeScatterPlot();
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

	private int[] intArray(double a, double b, double c) {
		return new int[] { (int) a, (int) b, (int) c };
	}

	private static int[] intArray(double a, double b, double c, double d) {
		return new int[] { (int) a, (int) b, (int) c, (int) d };
	}

	// return a shape sequence for a give size
	// modified function from DefaultDrawingSupplier
	public Shape[] getShapesSequence(double size) {
		Shape[] result = new Shape[10];
		double delta = size / 2.0;
		int[] xpoints = null;
		int[] ypoints = null;

		// square
		result[0] = new Rectangle2D.Double(-delta, -delta, size, size);
		// circle
		result[1] = new Ellipse2D.Double(-delta, -delta, size, size);

		// up-pointing triangle
		xpoints = intArray(0.0, delta, -delta);
		ypoints = intArray(-delta, delta, delta);
		result[2] = new Polygon(xpoints, ypoints, 3);

		// diamond
		xpoints = intArray(0.0, delta, 0.0, -delta);
		ypoints = intArray(-delta, 0.0, delta, 0.0);
		result[3] = new Polygon(xpoints, ypoints, 4);

		// horizontal rectangle
		result[4] = new Rectangle2D.Double(-delta, -delta / 2, size, size / 2);

		// down-pointing triangle
		xpoints = intArray(-delta, +delta, 0.0);
		ypoints = intArray(-delta, -delta, delta);
		result[5] = new Polygon(xpoints, ypoints, 3);

		// horizontal ellipse
		result[6] = new Ellipse2D.Double(-delta, -delta / 2, size, size / 2);

		// right-pointing triangle
		xpoints = intArray(-delta, delta, -delta);
		ypoints = intArray(-delta, 0.0, delta);
		result[7] = new Polygon(xpoints, ypoints, 3);

		// vertical rectangle
		result[8] = new Rectangle2D.Double(-delta / 2, -delta, size / 2, size);

		// left-pointing triangle
		xpoints = intArray(-delta, delta, delta);
		ypoints = intArray(0.0, -delta, +delta);
		result[9] = new Polygon(xpoints, ypoints, 3);

		return result;

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

}
