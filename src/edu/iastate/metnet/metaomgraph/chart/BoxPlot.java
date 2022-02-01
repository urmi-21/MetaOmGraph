package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
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
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.Outlier;
import org.jfree.chart.renderer.OutlierList;
import org.jfree.chart.renderer.OutlierListCollection;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.MenuButton;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class BoxPlot extends TaskbarInternalFrame implements ChartMouseListener, ActionListener {

	// hasmap mapping feature num to expression data or datacol to sample depending
	// on the box plot
	HashMap<Integer, double[]> plotData;
	// plot type 0 for features 1 for samples
	int plotType;
	private Map<String, HashMap<Integer, ArrayList<String>>> rowNamesIndexColNamesMap;
	private boolean splitIndicesFirstTime = false;
	private boolean reOrderGroups = false;
	private MetaOmProject myProject;
	// private String xAxisname;
	// private ChartToolBar myToolbar;
	private ChartPanel chartPanel;
	private JFreeChart myChart;
	private BoxPlotSorter boxPlotSorter;

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
	private MenuButton sortMenuButton;
	private JRadioButtonMenuItem defaultSortItem;
	private JRadioButtonMenuItem nameSortItem;
	private JRadioButtonMenuItem medianSortItem;
	private JRadioButtonMenuItem meanSortItem;
	private JRadioButtonMenuItem numOfSamplesSortItem;
	private JRadioButtonMenuItem tempSortItem;
	private JButton splitDataset;
	private JButton boxPlotOptions;

	private LegendTitle myLegend;
	private JToggleButton toggleLegend;
	private boolean legendFlag = true;
	// if number of items in legend is more than this then turn legend off
	private int maxLegend = 30;

	// keep track of user selected palette and retain this on updating the chart
	private Color[] currentlySetColors = null;

	/*
	 * TODO: Add option in boxplot show/hide/choose color for mean,median, outliers,
	 * far outliers add tool tips for outliers sort by mean or median values
	 */
	// default options
	private boolean showMean = false;
	private boolean showOutliers = false;
	private boolean showFarOutliers = false;
	private boolean showMedian = true;
	// colors
	Color medianColor = Color.black;
	Color meanColor = Color.red;
	Color outlierColor = Color.pink;
	Color faroutlierColor = Color.green;

	private int outlierSize = 4;
	private int faroutlierSize = 3;

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

	// seriesNames keeps all the series in the dataset
	List<String> seriesNames;
	Map<String, Collection<Integer>> splitIndex;

	private boolean[] excludedCopy;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	}

	public BoxPlot(HashMap<Integer, double[]> plotData, int pType, MetaOmProject mp, boolean isPlayback) {
		this(plotData, pType, mp, MetaOmAnalyzer.getExclude(), isPlayback);
	}

	/**
	 * Create the frame.
	 */
	// plot type 0 for features 1 for samples
	public BoxPlot(HashMap<Integer, double[]> plotData, int pType, MetaOmProject mp, boolean[] excludedSamples,
			boolean isPlayback) {

		this.plotData = plotData;
		this.plotType = pType;
		myProject = mp;
		if (plotType == 0) {
			// make copy of excluded
			// boolean[] excluded = MetaOmAnalyzer.getExclude();
			if (excludedSamples != null) {
				excludedCopy = new boolean[excludedSamples.length];
				System.arraycopy(excludedSamples, 0, excludedCopy, 0, excludedSamples.length);
			}
		}
		chartPanel = null;
		// init rownames
		rowNamesIndexColNamesMap = initRowNames(plotData.keySet(), pType);
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
		boxPlotSorter = new BoxPlotSorter();

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

		JPopupMenu sortMenu = new JPopupMenu();

		defaultSortItem = new JRadioButtonMenuItem("Default");
		defaultSortItem.setActionCommand("defaultSort");
		defaultSortItem.addActionListener(this);
		tempSortItem = defaultSortItem;
		tempSortItem.setSelected(true);

		nameSortItem = new JRadioButtonMenuItem("By Sample Name");
		nameSortItem.setActionCommand("nameSort");
		nameSortItem.addActionListener(this);

		medianSortItem = new JRadioButtonMenuItem("By Median");
		medianSortItem.setActionCommand("medianSort");
		medianSortItem.addActionListener(this);

		meanSortItem = new JRadioButtonMenuItem("By Mean");
		meanSortItem.setActionCommand("meanSort");
		meanSortItem.addActionListener(this);

		numOfSamplesSortItem = new JRadioButtonMenuItem("By Sample Count");
		numOfSamplesSortItem.setActionCommand("sampleCountSort");
		numOfSamplesSortItem.addActionListener(this);
		numOfSamplesSortItem.setEnabled(false);

		sortMenu.add(defaultSortItem);
		sortMenu.add(nameSortItem);
		sortMenu.add(medianSortItem);
		sortMenu.add(meanSortItem);
		sortMenu.add(numOfSamplesSortItem);

		sortMenuButton = new MenuButton(theme.getSort(), null);
		sortMenuButton.setToolTipText("Sort data");

		sortMenuButton.setMenu(sortMenu);

		if (plotType == 0) {
			splitDataset = new JButton(theme.getGroupBy());
			splitDataset.setToolTipText("Group by categories");
			splitDataset.setActionCommand("splitDataset");
			splitDataset.addActionListener(this);
		}

		boxPlotOptions = new JButton(theme.getOpts());
		boxPlotOptions.setToolTipText("Options");
		boxPlotOptions.setActionCommand("options");
		boxPlotOptions.addActionListener(this);

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
		panel.add(sortMenuButton);
		if (plotType == 0) {
			panel.add(splitDataset);
		}
		panel.add(boxPlotOptions);
		panel.add(changePalette);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		String chartTitle = "Box Plot:" + String.join(",", rowNamesIndexColNamesMap.keySet());
		if (isPlayback) {
			chartTitle = "Playback - Box Plot:" + String.join(",", rowNamesIndexColNamesMap.keySet());
		}
		this.setTitle(chartTitle);

		FrameModel boxPlotFrameModel = new FrameModel("Box Plot", chartTitle, 5);
		setModel(boxPlotFrameModel);

	}

	public ChartPanel makeBoxPlot(DefaultBoxAndWhiskerCategoryDataset dataset) throws IOException {
		return makeBoxPlot(dataset, showMean, showMedian, showOutliers, showFarOutliers, meanColor, medianColor,
				outlierColor, faroutlierColor, outlierSize, faroutlierSize);
	}

	public ChartPanel makeBoxPlot(DefaultBoxAndWhiskerCategoryDataset dataset, boolean showMean, boolean showMedian,
			boolean showOutliers, boolean showFaroutliers, Color meanColor, Color medianColor, Color outlierColor,
			Color farourlierColor, int outSize, int faroutSize) throws IOException {

		myChart = ChartFactory.createBoxAndWhiskerChart("BoxPlot", "Sample", "Value", dataset, true);

		// urmi add chart options
		myRenderer = getBoxAndWhiskerRenderer(meanColor, medianColor, outlierColor, farourlierColor, showOutliers,
				showFaroutliers, outSize, faroutSize);
		myRenderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		myRenderer.setFillBox(true);
		myRenderer.setMeanVisible(showMean);
		myRenderer.setMedianVisible(showMedian);

		myChart.getCategoryPlot().getDomainAxis()
				.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(1.5707963267948966D));
		myChart.getCategoryPlot().setRenderer(myRenderer);
		myChart.getCategoryPlot().setBackgroundPaint(MetaOmGraph.getPlotBackgroundColor());
		myChart.setBackgroundPaint(MetaOmGraph.getChartBackgroundColor());

		// save legend
		myLegend = myChart.getLegend();
		// if legene flag is off remove legend
		if (!legendFlag) {
			myChart.removeLegend();
		}

		myChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(

				CategoryLabelPositions.createUpRotationLabelPositions(1.5707963267948966D));
		ChartPanel chartPanel = new ChartPanel(myChart, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height, 0, 0,
				Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height,
				true, true, true, true, true, true) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(ChartPanel.SAVE_COMMAND)) {
					ChartActions.exportChart(this);
				} else
					super.actionPerformed(e);
			}

			@Override
			public String getToolTipText(MouseEvent event) {
				if (myProject.getMetadataHybrid() == null) {
					return null;
				}
				ChartEntity entity = getChartRenderingInfo().getEntityCollection().getEntity(event.getPoint().getX(),
						event.getPoint().getY());
				// JOptionPane.showMessageDialog(null, entity);
				if (!(entity instanceof CategoryItemEntity)) {
					// JOptionPane.showMessageDialog(null, "null");
					return null;
				}
				CategoryItemEntity item = (CategoryItemEntity) entity;
				String colKey = (String) item.getColumnKey();
				String rowKey = (String) item.getRowKey();
				String[] temp = item.getToolTipText().split(" ");
				// String mean=temp[1].split(":")[1].replaceAll("\\s+","");
				String mean = temp[3].replaceAll("\\s+", "");
				String median = temp[5].replaceAll("\\s+", "");
				String min = temp[7].replaceAll("\\s+", "");
				String max = temp[9].replaceAll("\\s+", "");
				String q1 = temp[11].replaceAll("\\s+", "");
				String q3 = temp[13].replaceAll("\\s+", "");

				// If box plot is sample display only data statistics in tooltip
				if (plotType == 1) {
					return createTooltipTable(null, rowKey, mean, median, min, max, q1, q3);
				}
				// create tooltip
				return createTooltipTable(colKey, rowKey, mean, median, min, max, q1, q3);
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
		// if a palette/colors is selected keep that otherwise use default
		if (currentlySetColors == null) {
			// set default colors
			setDefaultPalette();
		} else {
			setPalette(currentlySetColors);
		}

		return chartPanel;

	}

	/**
	 * create tooltip for boxplot
	 * 
	 * @param featureName
	 * @param x
	 * @param y
	 * @return
	 */
	private String createTooltipTable(String featureName, String series, String mean, String median, String min,
			String max, String q1, String q3) {
		DecimalFormat df = new DecimalFormat("####0.0000");
		String bgColor = "#" + Integer.toHexString(MetaOmGraph.getTableColorEven().getRGB()).substring(2);
		;
		String bgColorAlt = "#" + Integer.toHexString(MetaOmGraph.getTableColorOdd().getRGB()).substring(2);
		String[] rowColors = { bgColor, bgColorAlt };
		String text = "<html><head> " + "<style>" + ".scrollit {\n" + "    overflow:scroll;\n" + "    height:100px;\n"
				+ "}" + "</style></head><body>"

				+ "<div class=\"scrollit\"> <table bgcolor=\"#FFFFFF\" width=\"400\">" + " <tr>\n"
				+ "            <th>Attribute</th>\n" + "            <th >Value</th>\n" + "        </tr>";

		text += "<tr bgcolor=" + rowColors[1] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Median", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(median, 100, "<br>") + "</font></td>";
		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[0] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Mean", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(mean, 100, "<br>") + "</font></td>";
		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[1] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Min", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(min, 100, "<br>") + "</font></td>";
		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[0] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Max", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(max, 100, "<br>") + "</font></td>";
		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[1] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Q1", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(q1, 100, "<br>") + "</font></td>";
		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[0] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Q3", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(q3, 100, "<br>") + "</font></td>";
		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[1] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Series", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(series, 100, "<br>") + "</font></td>";
		text += "</tr>";

		// if data is from features
		if (featureName != null) {

			// get gene metadata in String [][] format
			String[] infoCols = myProject.getInfoColumnNames();
			Object[] featureRow = myProject.getRowName(myProject.getRowIndexbyName(featureName, true));

			// if feature metadata is found
			if (featureRow != null) {

				String[][] tableData = new String[infoCols.length][2];
				for (int i = 0; i < infoCols.length; i++) {
					tableData[i][0] = infoCols[i];
					tableData[i][1] = String.valueOf(featureRow[i]);
				}

				int maxrowsinMD = 40;
				int maxStringLen = 500;

				int colorIndex = 0;
				for (int i = 0; i < tableData.length; i++) {
					if (i == maxrowsinMD) {
						text += "<tr bgcolor=" + rowColors[colorIndex] + ">";
						text += "<td><font size=-2>" + "..." + "</font></td>";
						text += "<td><font size=-2>" + "..." + "</font></td>";
						text += "</tr>";
						break;
					}
					String thisAtt = tableData[i][0];
					String thisData = tableData[i][1];
					if (thisData.length() > maxStringLen) {
						thisData = thisData.substring(0, maxStringLen) + "...";
					}

					text += "<tr bgcolor=" + rowColors[colorIndex] + ">";
					text += "<td><font size=-2>" + Utils.wrapText(thisAtt.trim(), 100, "<br>") + "</font></td>";
					text += "<td><font size=-2>" + Utils.wrapText(thisData.trim(), 100, "<br>") + "</font></td>";

					text += "</tr>";
					colorIndex = (colorIndex + 1) % rowColors.length;

				}
			}

		}

		text += "</table> </div> </body></html>";

		return text;

	}

	private DefaultBoxAndWhiskerCategoryDataset createDataset() {
		DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Working",
				"Generating BoxPlot", 0L, plotData.size(), true);
		new Thread() {
			@Override
			public void run() {
				seriesNames = new ArrayList<>();
				if (splitIndex == null || splitCol == null || splitCol.length() < 1) {
					// no split
					int n = 0;
					for (Map.Entry<String, HashMap<Integer, ArrayList<String>>> entry : rowNamesIndexColNamesMap
							.entrySet()) {
						List<Double> list = new ArrayList();
						for (Map.Entry<Integer, ArrayList<String>> indexEntry : entry.getValue().entrySet()) {
							double[] thisData = plotData.get(indexEntry.getKey());
							for (int j = 0; j < thisData.length; j++) {
								if (excludedCopy == null) {
									list.add(thisData[j]);
								} else {
									if (!excludedCopy[j]) {
										list.add(thisData[j]);
									}
								}
							}
						}
						dataset.add(list, "All", entry.getKey());
					}
					seriesNames.add("All");
				} else {
					setColSplitIndKeysInRowIndexColMap();
					ArrayList<String> keyNames = new ArrayList<String>();
					boolean firstRow = true;
					for (String row : rowNamesIndexColNamesMap.keySet()) {
						HashMap<Integer, ArrayList<String>> rowIndexSplitIndKeys = rowNamesIndexColNamesMap.get(row);
						int rowIndex = rowIndexSplitIndKeys.keySet().iterator().next();
						List<String> splitIndKeys = rowIndexSplitIndKeys.get(rowIndex);
						double[] thisData = plotData.get(rowIndex);
						for (String key : splitIndKeys) {
							List list = new ArrayList();

							String thisKeyName = "";
							Collection<Integer> thisInd = splitIndex.get(key);
							for (int ind : thisInd) {
								if (excludedCopy == null) {
									list.add(thisData[ind]);
								} else {
									if (!excludedCopy[ind]) {
										list.add(thisData[ind]);
									}
								}
							}
							//urmi if list has no element then ignore
							if (list.size() > 0) {
								thisKeyName = key + "(n=" + String.valueOf(list.size()) + ")";
								dataset.add(list, thisKeyName, row);
							}
							if (firstRow) {
								if (list.size() > 0) {
									keyNames.add(thisKeyName);
								}
							}
						}
						firstRow = false;
					}
					seriesNames.addAll(keyNames);
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
				currentlySetColors = colorArray;
			} else {
				// reset was pressed and the OK. show default colors
				colorArray = null;
				updateChart();

			}

			return;
		}

		if ("options".equals(e.getActionCommand())) {

			// display option dialog
			BoxPlotOpts optPanel = new BoxPlotOpts(showMean, showMedian, showOutliers, showFarOutliers, meanColor,
					medianColor, outlierColor, faroutlierColor, outlierSize, faroutlierSize);
			/*
			 * dialog.setModal(true); dialog.pack(); dialog.setVisible(true);
			 */

			int res = JOptionPane.showConfirmDialog(null, optPanel, "Enter values", JOptionPane.OK_CANCEL_OPTION);
			if (res == JOptionPane.OK_OPTION) {
				// get data from optPanel's public methods
				// JOptionPane.showMessageDialog(null, "OK");
				showMean = optPanel.getShowMean();
				showMedian = optPanel.getShowMedian();
				showOutliers = optPanel.getShowOutliers();
				showFarOutliers = optPanel.getShowFarOutliers();
				meanColor = optPanel.getMeanColor();
				medianColor = optPanel.getMedianColor();
				outlierColor = optPanel.getOutColor();
				faroutlierColor = optPanel.getFarOutColor();
				outlierSize = optPanel.getOutlierSize();
				faroutlierSize = optPanel.getFarOutlierSize();
				// update the chart
				updateChart();
			} else {
				return;
			}

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
			String[] options = {"By Metadata", "By Query", "Reset"};		

			String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (col_val == null) {
				return;
			}

			if (col_val.equals("Reset")) {
				numOfSamplesSortItem.setEnabled(false);
				splitIndicesFirstTime = false;
				splitCol = null;
				splitIndex = null;
				reOrderGroups = false;
				// createDataset();
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
				splitIndex = myProject.getMetadataHybrid().cluster(selectedVals);
				if (splitIndex != null) {
					splitIndicesFirstTime = true;
					numOfSamplesSortItem.setEnabled(true);
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
				// final int[] result = new int[myProject.getDataColumnCount()];
				Collection<Integer> result = new ArrayList<>();
				List<Collection<Integer>> resList = new ArrayList<>();
				final boolean nohits;
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
				splitIndicesFirstTime = true;
				numOfSamplesSortItem.setEnabled(true);
			}

			reOrderGroups = false;
			updateChart();
			return;
		}

		if ("defaultSort".equals(e.getActionCommand())) {
			tempSortItem.setSelected(false);
			splitIndicesFirstTime = true;
			this.rowNamesIndexColNamesMap = new HashMap<String, HashMap<Integer, ArrayList<String>>>(
					this.rowNamesIndexColNamesMap);
			updateChart();
			tempSortItem = defaultSortItem;
		}

		if ("nameSort".equals(e.getActionCommand())) {
			tempSortItem.setSelected(false);
			splitIndicesFirstTime = false;
			this.rowNamesIndexColNamesMap = boxPlotSorter.sortByRowName(this.rowNamesIndexColNamesMap);
			updateChart();
			tempSortItem = nameSortItem;
		}

		if ("medianSort".equals(e.getActionCommand())) {
			tempSortItem.setSelected(false);
			splitIndicesFirstTime = false;
			this.rowNamesIndexColNamesMap = boxPlotSorter.sortByMedian(initdataset, rowNamesIndexColNamesMap,
					splitIndex == null);
			updateChart();
			tempSortItem = medianSortItem;
		}

		if ("meanSort".equals(e.getActionCommand())) {
			tempSortItem.setSelected(false);
			splitIndicesFirstTime = false;
			this.rowNamesIndexColNamesMap = boxPlotSorter.sortByMean(initdataset, rowNamesIndexColNamesMap,
					splitIndex == null);
			updateChart();
			tempSortItem = meanSortItem;
		}

		if ("sampleCountSort".equalsIgnoreCase(e.getActionCommand())) {
			tempSortItem.setSelected(false);
			splitIndicesFirstTime = false;
			this.rowNamesIndexColNamesMap = boxPlotSorter.sortBySampleCount(initdataset, rowNamesIndexColNamesMap);
			updateChart();
			tempSortItem = numOfSamplesSortItem;
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
				// JOptionPane.showMessageDialog(null, "indexcol:"+seriesKey.toString());
				// int index = myChart.getXYPlot().getDataset().indexOf(seriesKey);
				// int index=seriesNames.indexOf(seriesKey.toString())+1;
				// JOptionPane.showMessageDialog(null, "SR:"+seriesNames.toString()+"::
				// "+seriesKey.toString());

				int index = seriesNames.indexOf(seriesKey.toString());
				// JOptionPane.showMessageDialog(null, "SR:"+seriesNames.toString()+"ind
				// of:"+index);
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

	private Map<String, HashMap<Integer, ArrayList<String>>> initRowNames(Set<Integer> keyset, int type) {
		Map<String, HashMap<Integer, ArrayList<String>>> rowNamesIndexColNamesMap = new HashMap<String, HashMap<Integer, ArrayList<String>>>();

		// Map<String, Integer> rowNameIndexMap = new HashMap<String, Integer>();
		int[] selectedInd = new int[keyset.size()];
		int k = 0;
		for (int i : keyset) {
			selectedInd[k++] = i;
		}
		String[] rowNames = new String[keyset.size()];
		if (type == 0) {
			rowNames = myProject.getDefaultRowNames(selectedInd);
		} else if (type == 1) {
			rowNames = myProject.getDataColumnHeaders(selectedInd);
		}

		for (int i = 0; i < rowNames.length; i++) {
			HashMap<Integer, ArrayList<String>> rowIndexColNamesMap = new HashMap<Integer, ArrayList<String>>();
			ArrayList<String> colNames = new ArrayList<String>();
			rowIndexColNamesMap.put(selectedInd[i], colNames);
			rowNamesIndexColNamesMap.put(rowNames[i], rowIndexColNamesMap);
		}
		return rowNamesIndexColNamesMap;
	}

	private List<String> getSplitIndKeys() {
		if (splitIndex == null) {
			return null;
		}

		ArrayList<String> res = new ArrayList<>();
		res = rowNamesIndexColNamesMap.entrySet().iterator().next().getValue().entrySet().iterator().next().getValue();
		return res;
	}

	private void setColSplitIndKeysInRowIndexColMap() {
		if (splitIndex == null) {
			return;
		}
		if (!splitIndicesFirstTime) {
			return;
		}
		if (reOrderGroups) {
			return;
		}
		ArrayList<String> res = new ArrayList<>();
		res.addAll(splitIndex.keySet());
		for (String rowName : rowNamesIndexColNamesMap.keySet()) {
			rowNamesIndexColNamesMap.get(rowName).entrySet().iterator().next().setValue(res);
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
			ArrayList<String> reorderedKeys = new ArrayList<>();
			for (int i = 0; i < list.getModel().getSize(); i++) {
				reorderedKeys.add((String) list.getModel().getElementAt(i));
			}

			for (String rowName : rowNamesIndexColNamesMap.keySet()) {
				rowNamesIndexColNamesMap.get(rowName).entrySet().iterator().next().setValue(reorderedKeys);
			}
			reOrderGroups = true;
			updateChart();

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

	///////////////////////// BoxPlot Renderer
	///////////////////////// functions*//////////////////////////////////////////////
	public static BoxAndWhiskerRenderer getBoxAndWhiskerRenderer(Color meanColor, Color medianColor, Color outColor,
			Color faroutColor, boolean showOutliers, boolean showFaroutliers, int outSize, int faroutSize) {
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

				// aRadius controls the size of mean and triangles
				// urmi triangle indicates the presence of far out values.
				double aRadius = 0; // average radius

				org.jfree.chart.ui.RectangleEdge location = plot.getRangeAxisEdge();
				// plot box plots
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

				// draw mean line
				if (isMeanVisible()) {
					// if (true) {
					Number yMean = bawDataset.getMeanValue(row, column);
					if (yMean != null) {
						yyAverage = rangeAxis.valueToJava2D(yMean.doubleValue(), dataArea, location);
						aRadius = state.getBarWidth() / 10;
						// here we check that the average marker will in fact be
						// visible before drawing it...
						if ((yyAverage > (dataArea.getMinY() - aRadius))
								&& (yyAverage < (dataArea.getMaxY() + aRadius))) {
							// urmi don't draw ellipse
							/*
							 * Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx + aRadius, yyAverage -
							 * aRadius, aRadius * 2, aRadius * 2); g2.fill(avgEllipse); g2.draw(avgEllipse);
							 */

							double yyMean = rangeAxis.valueToJava2D(yMean.doubleValue(), dataArea, location);
							g2.setColor(meanColor);
							g2.draw(new Line2D.Double(xx, yyMean, xx + state.getBarWidth(), yyMean));
						}
					}
				}

				// draw median...
				if (isMedianVisible()) {
					Number yMedian = bawDataset.getMedianValue(row, column);
					if (yMedian != null) {
						double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
						g2.setColor(medianColor);
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
				double oRadius = outSize;
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

					if (showOutliers) {
						g2.setColor(outColor);
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
					}

					// draw farout indicators
					if (outlierListCollection.isHighFarOut() && showFaroutliers) {
						g2.setColor(faroutColor);
						aRadius = (state.getBarWidth() / 20) * faroutSize;
						drawHighFarOut(aRadius / 2.0, g2, xx + state.getBarWidth() / 2.0, maxAxisValue);
					}

					if (outlierListCollection.isLowFarOut() && showFaroutliers) {
						g2.setColor(faroutColor);
						aRadius = (state.getBarWidth() / 20) * faroutSize;
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

/**
 * TransferHandler for list to drag categories in the list and rearrange the
 * boxplot
 * 
 * @author mrbai
 *
 */
class ListItemTransferHandler extends TransferHandler {
	protected final DataFlavor localObjectFlavor;
	protected int[] indices;
	protected int addIndex = -1; // Location where items were added
	protected int addCount; // Number of items added.

	public ListItemTransferHandler() {
		super();
		// localObjectFlavor = new ActivationDataFlavor(
		// Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
		localObjectFlavor = new DataFlavor(Object[].class, "Array of items");
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		JList<?> source = (JList<?>) c;
		c.getRootPane().getGlassPane().setVisible(true);

		indices = source.getSelectedIndices();
		Object[] transferedObjects = source.getSelectedValuesList().toArray(new Object[0]);
		// return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
		return new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { localObjectFlavor };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return Objects.equals(localObjectFlavor, flavor);
			}

			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor)) {
					return transferedObjects;
				} else {
					throw new UnsupportedFlavorException(flavor);
				}
			}
		};
	}

	@Override
	public boolean canImport(TransferSupport info) {
		return info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
	}

	@Override
	public int getSourceActions(JComponent c) {
		Component glassPane = c.getRootPane().getGlassPane();
		glassPane.setCursor(DragSource.DefaultMoveDrop);
		return MOVE; // COPY_OR_MOVE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(TransferSupport info) {
		TransferHandler.DropLocation tdl = info.getDropLocation();
		if (!canImport(info) || !(tdl instanceof JList.DropLocation)) {
			return false;
		}

		JList.DropLocation dl = (JList.DropLocation) tdl;
		JList target = (JList) info.getComponent();
		DefaultListModel listModel = (DefaultListModel) target.getModel();
		int max = listModel.getSize();
		int index = dl.getIndex();
		index = index < 0 ? max : index; // If it is out of range, it is appended to the end
		index = Math.min(index, max);

		addIndex = index;

		try {
			Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
			for (int i = 0; i < values.length; i++) {
				int idx = index++;
				listModel.add(idx, values[i]);
				target.addSelectionInterval(idx, idx);
			}
			addCount = values.length;
			return true;
		} catch (UnsupportedFlavorException | IOException ex) {
			ex.printStackTrace();
		}

		return false;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		c.getRootPane().getGlassPane().setVisible(false);
		cleanup(c, action == MOVE);
	}

	private void cleanup(JComponent c, boolean remove) {
		if (remove && Objects.nonNull(indices)) {
			if (addCount > 0) {
				// https://github.com/aterai/java-swing-tips/blob/master/DragSelectDropReordering/src/java/example/MainPanel.java
				for (int i = 0; i < indices.length; i++) {
					if (indices[i] >= addIndex) {
						indices[i] += addCount;
					}
				}
			}
			JList source = (JList) c;
			DefaultListModel model = (DefaultListModel) source.getModel();
			for (int i = indices.length - 1; i >= 0; i--) {
				model.remove(indices[i]);
			}
		}

		indices = null;
		addCount = 0;
		addIndex = -1;
	}

}
