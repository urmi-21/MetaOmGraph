package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.Utils;

/**
 * Class to plot barcharts. THe data should be categorical e.g., columns of
 * metadata table.
 * 
 * @author mrbai
 *
 */
public class BarChart extends TaskbarInternalFrame implements ChartMouseListener, ActionListener {

	// hasmap mapping feature num to expression data or datacol to sample depending
	// on the box plot
	// HashMap<Integer, double[]> plotData;
	// plot type 0 for feature 1 for sample
	private int plotType; // 1 for featuremetadata; 2 sample metadata
	private String[] rowNames;
	private MetaOmProject myProject;
	private String dataName;
	private List<String> dataList;

	// private String xAxisname;
	// private ChartToolBar myToolbar;
	private ChartPanel chartPanel;
	private JFreeChart myChart;
	private LegendTitle myLegend;
	// private XYLineAndShapeRenderer myRenderer;
	private BarRenderer myRenderer;
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
	private JButton barPlotOptions;
	private JToggleButton toggleLegend;
	private boolean legendFlag = true;
	// if number of items in legend is more than this then turn legend off
	private int maxLegend = 30;

	// bottom toolbar
	private JButton btnNewButton_1;

	public static final String ZOOM_IN_COMMAND = "zoomIn";
	public static final String ZOOM_OUT_COMMAND = "zoomOut";
	public static final String ZOOM_DEFAULT_COMMAND = "defaultZoom";

	// chart colors
	private Color chartbg = MetaOmGraph.getChartBackgroundColor();
	private Color plotbg = MetaOmGraph.getPlotBackgroundColor();

	Color[] colorArray = null;

	CategoryDataset initdataset;
	String splitCol;
	List<String> seriesNames;
	Map<String, Collection<Integer>> splitIndex;
	// to keep an order for the dataset
	List<String> orderedKeys;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	}

	/**
	 * Create the frame.
	 */
	public BarChart(MetaOmProject mp, String dataName, List<String> data, int type) {

		// this.plotData = plotData;
		super();
		myProject = mp;
		this.plotType = type;
		this.dataName = dataName;
		this.dataList = data;
		chartPanel = null;

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
		initdataset = createDataset(dataList);

		try {
			chartPanel = makeBarChart(initdataset);
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

		barPlotOptions = new JButton(theme.getOpts());
		barPlotOptions.setToolTipText("Options");
		barPlotOptions.setActionCommand("options");
		barPlotOptions.addActionListener(this);

		toggleLegend = new JToggleButton(theme.getLegend(), legendFlag);
		toggleLegend.setToolTipText("Show/hide legend");
		toggleLegend.setActionCommand("legend");
		toggleLegend.addActionListener(this);

		panel.add(properties);
		panel.add(save);
		panel.add(print);
		// panel.add(zoomIn);
		// panel.add(zoomOut);
		panel.add(defaultZoom);

		panel.add(toggleLegend);
		panel.add(splitDataset);
		// panel.add(barPlotOptions);
		panel.add(changePalette);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		//String chartTitle = "Bar Chart:" + String.join(",", rowNames);
		FrameModel barChartFrameModel = new FrameModel("Bar Chart","Bar Chart ["+dataName+"]",4);
		setModel(barChartFrameModel);
		// this.setTitle(chartTitle);
	}

	public ChartPanel makeBarChart(CategoryDataset dataset) throws IOException {

		myChart = ChartFactory.createBarChart("", // Chart Title
				"", // Category axis
				"Count", // Value axis
				dataset, PlotOrientation.VERTICAL, true, true, false);

		/*
		 * myChart = ChartFactory.createStackedBarChart("", // Chart Title "", //
		 * Category axis "Count", // Value axis dataset, PlotOrientation.VERTICAL, true,
		 * true, false);
		 */

		myChart.getCategoryPlot().setBackgroundPaint(MetaOmGraph.getPlotBackgroundColor());
		myChart.setBackgroundPaint(MetaOmGraph.getChartBackgroundColor());

		// save legend
		myLegend = myChart.getLegend();
		// if legene flag is off remove legend
		if (!legendFlag) {
			myChart.removeLegend();
		}
		CategoryPlot cplot = (CategoryPlot) myChart.getPlot();
		myRenderer = (BarRenderer) cplot.getRenderer();
		// remove shadows from bar chart
		myRenderer.setBarPainter(new StandardBarPainter());

		/*
		 * myRenderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator()
		 * {
		 * 
		 * @Override public String generateToolTip(CategoryDataset dataset, int row, int
		 * column) { String s = super.generateToolTip(dataset, row, column); int b =
		 * s.indexOf('(', 1) + 1; int e = s.indexOf(')'); return s; } });
		 */

		MyChartPanel chartPanel = new MyChartPanel(myChart, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height, 0, 0,
				Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height,
				true, true, true, true, true, true, MyChartPanel.BARCHART) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(ChartPanel.SAVE_COMMAND)) {
					ChartActions.exportChart(this);
				} else
					super.actionPerformed(e);
			}

			@Override
			public String getToolTipText(MouseEvent event) {

				ChartEntity entity = getChartRenderingInfo().getEntityCollection().getEntity(event.getPoint().getX(),
						event.getPoint().getY());
				// JOptionPane.showMessageDialog(null, entity);
				if (!(entity instanceof CategoryItemEntity)) {
					// JOptionPane.showMessageDialog(null, "null");
					// return "null";
					return null;
				}
				CategoryItemEntity item = (CategoryItemEntity) entity;
				String colKey = (String) item.getColumnKey();
				String rowKey = (String) item.getRowKey();
				String value = item.getToolTipText().split("=")[1];

				// JOptionPane.showMessageDialog(null,"rk:" + rowKey + " ck:" + colKey + " " +
				// item.getToolTipText() + " val:" + value);
				// create tooltip
				return createTooltipTable(rowKey, colKey, value);
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

		chartPanel.setPreferredSize(new Dimension(800, 600));
		chartPanel.addChartMouseListener(this);

		// setDefaultPalette();

		return chartPanel;

	}

	/**
	 * Function to prepare categorical data from a list.
	 * 
	 * @param data
	 * @return
	 */
	private CategoryDataset createDataset(List<String> data) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		seriesNames = new ArrayList<>();

		// hashmap to store the frequency of element
		Map<String, Integer> freqMap = new HashMap<String, Integer>();
		for (String i : data) {
			Integer j = freqMap.get(i);
			freqMap.put(i, (j == null) ? 1 : j + 1);
		}

		// order the hashmap by value
		freqMap = freqMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		if (freqMap.size() > maxLegend) {
			legendFlag = false;
			// setLegendVisible(legendFlag);
		}

		for (Map.Entry<String, Integer> val : freqMap.entrySet()) {

			dataset.addValue(val.getValue(), val.getKey(), dataName);
			seriesNames.add(val.getKey());
		}

		return dataset;
	}

	private String createTooltipTable(String row, String col, String val) {
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
		text += "<td><font size=-2>" + Utils.wrapText("Value", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(val, 100, "<br>") + "</font></td>";

		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[0] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Category", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(row, 100, "<br>") + "</font></td>";

		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[1] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Column", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(col, 100, "<br>") + "</font></td>";

		text += "</tr>";

		text += "</table> </div> </body></html>";
		// System.out.println(text);

		return text;

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

		if ("options".equals(e.getActionCommand())) {
			// TODO
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

			// show feature or sample metadata columns based on the plotType
			String[] fields = null;
			if (plotType == 1) {
				// show feature metadata columns
				fields = myProject.getInfoColumnNames();
			} else if (plotType == 2) {
				// show sample metadata columns
				fields = myProject.getMetadataHybrid().getMetadataHeaders();
			}

			fields = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataHeaders();
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
				// final int[] result = new int[myProject.getDataColumnCount()];
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

			else {
				// split data set by values of col_val
				selectedVals.add(col_val);
				splitCol = col_val;
				splitIndex = myProject.getMetadataHybrid().cluster(selectedVals);
			}

			// reset order
			orderedKeys = null;
			updateChart();

			return;
		}

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

				// seriesNames is a list containing names of each category
				int index = seriesNames.indexOf(seriesKey.toString());
				changeSeriesColor(index);

				return;
			} else if (event.getEntity() instanceof CategoryItemEntity) {
				// changeSeriesColor(((CategoryItemEntity) event.getEntity()).get);
				//
				String ck = ((CategoryItemEntity) event.getEntity()).getColumnKey().toString();
				String rk = ((CategoryItemEntity) event.getEntity()).getRowKey().toString();
				// JOptionPane.showMessageDialog(null, "CLICKED2:" + ck+" rk:"+rk);

				return;
			}
		}

		if (event.getTrigger().getClickCount() == 3) {
			reOrderGroups();
		}

	}

	@Override
	public void chartMouseMoved(ChartMouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * Changes the color of the Selected series
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
			initdataset = createDataset(this.dataList);
			this.chartPanel = makeBarChart(initdataset);
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

	private List<String> getSplitIndKeys() {
		if (splitIndex == null) {
			return null;
		}
		List<String> res = new ArrayList<>();
		if (orderedKeys == null) {
			res.addAll(splitIndex.keySet());
			return res;
		} else {
			return orderedKeys;
		}
	}

	private void reOrderGroups() {
		if (splitIndex == null) {
			return;
		}
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		DefaultListModel listmod = new DefaultListModel();
		List<String> thisOrder = getSplitIndKeys();
		for (String s : thisOrder) {
			listmod.addElement(s);
		}
		JList list = new JList<>(listmod);
		list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setTransferHandler(new ListItemTransferHandler());
		list.setDropMode(DropMode.INSERT);
		list.setDragEnabled(true);
		listPanel.add(new JScrollPane(list), BorderLayout.CENTER);

		int opt = JOptionPane.showConfirmDialog(null, listPanel, "Reorder using Drag-and-Drop",
				JOptionPane.PLAIN_MESSAGE);

		if (opt == JOptionPane.OK_OPTION) {
			Map<String, Collection<Integer>> temp = new HashMap<>();
			orderedKeys = new ArrayList<>();
			for (int i = 0; i < list.getModel().getSize(); i++) {
				// JOptionPane.showMessageDialog(null,list.getModel().getElementAt(i));
				// temp.put((String) list.getModel().getElementAt(i),
				// splitIndex.get(list.getModel().getElementAt(i).toString()));
				orderedKeys.add((String) list.getModel().getElementAt(i));
			}

			// splitIndex=temp;
			updateChart();

		}

	}

	public static BarRenderer getBoxAndWhiskerRenderer() {

		BarRenderer renderer = new BarRenderer() {
		};

		return renderer;
	}

}
