package edu.iastate.metnet.metaomgraph.chart;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.BorderLayout;
import java.awt.Color;

import java.io.IOException;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;

import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
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
import edu.iastate.metnet.metaomgraph.ui.SampleMetaDataListFrame;
import edu.iastate.metnet.metaomgraph.ui.SelectedSampleMetaDataDisplayTable;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.Utils;


/**
 * @author sumanth
 * Scatter plot char to plot the PCA
 * This charts implements the selection tool for the scatter plots.
 */
public class ScatterPlotDimReduction extends TaskbarInternalFrame implements ChartMouseListener, MouseMotionListener, ActionListener, MouseListener {

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

	private double pointSize = 5.0;
	private XYItemRenderer myRenderer;
	private JScrollPane scrollPane;

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
	
	private JToggleButton selectionButton;
	private JMenuItem singleSelectionMenu;
	private JMenuItem multiSelectionMenu;
	private JMenuItem zoomMenu;
	
	private JPanel chartButtonsPanel;

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
	
	private boolean singleSelection = false;
	private boolean multiSelection = false;
	private Point2D rectStartPos;
	private Point2D rectEndPos;
	
	private JPanel chartDisplayPanel;
	private JPanel tableDisplayPanel;
	private JTable selectedPointsTable;
	private JScrollPane scrollPanel;
	private JSplitPane splitPane;
	private SelectedSampleMetaDataDisplayTable selectedPointsDisplayTableObj;
	private ArrayList<Rectangle2D> selectedRectangles;
	/**
	 * Create the frame.
	 */
	public ScatterPlotDimReduction(double[][] data, String[] rowNames, String[] selectedDataCols,
			String xLabel, String yLabel, boolean isPlayback) {
		super("Scatter Plot");
		
		this.data = data;
		this.xAxisname = xLabel;
		this.yAxisname = yLabel;
		this.selectedDataCols = selectedDataCols;
		this.rowNames = rowNames;
		this.metaDataColl = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataCollection();		
		
		chartDisplayPanel = new JPanel();
		chartDisplayPanel.setLayout(new BorderLayout(0,0));
		tableDisplayPanel = new JPanel();
		tableDisplayPanel.setLayout(new BorderLayout(0,0));
		
		selectedPointsDisplayTableObj = new SelectedSampleMetaDataDisplayTable();
		initSelectedPointsTable();
		JMenuBar menuBar = initSelectedPointsTableMenuBar();
		tableDisplayPanel.add(menuBar, "First");
		scrollPanel = new JScrollPane(selectedPointsTable);
		tableDisplayPanel.add(scrollPanel, BorderLayout.CENTER);
		
		chartPanel = null;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));

		chartButtonsPanel = new JPanel();
		chartButtonsPanel.setLayout(new FlowLayout());

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

		chartButtonsPanel.add(properties);
		chartButtonsPanel.add(save);
		chartButtonsPanel.add(print);
		chartButtonsPanel.add(zoomIn);
		chartButtonsPanel.add(zoomOut);
		chartButtonsPanel.add(defaultZoom);
		chartButtonsPanel.add(toggleLegend);
		chartButtonsPanel.add(splitDataset);
		chartButtonsPanel.add(changePalette);
		
		spinner = new JSpinner();
		spinner.setToolTipText("Changes plot point size");
		spinner.setModel(new SpinnerNumberModel(pointSize, 1.0, 20.0, 1.0));
		// set uneditable
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
		chartButtonsPanel.add(spinner);
		// add change listener
		ChangeListener listener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pointSize = (double) spinner.getValue();
				updateChart();
			}
		};

		spinner.addChangeListener(listener);
		
		//Selection tool buttons
		selectionButton = new JToggleButton();
		selectionButton.setIcon(theme.getSelectIcon());
		
		JPopupMenu selectionPopUpMenu = new JPopupMenu();
		singleSelectionMenu = new JMenuItem("Single selection tool");
		singleSelectionMenu.setIcon(theme.getSelectIcon());
		singleSelectionMenu.setActionCommand("single selection");
		singleSelectionMenu.addActionListener(this);
		
		multiSelectionMenu = new JMenuItem("Multi selection tool");
		multiSelectionMenu.setIcon(theme.getSelectIcon());
		multiSelectionMenu.setActionCommand("multi selection");
		multiSelectionMenu.addActionListener(this);
		
		zoomMenu = new JMenuItem("Zoom tool");
		zoomMenu.setIcon(theme.getDefaultZoom());
		zoomMenu.setActionCommand("zoom tool");
		zoomMenu.addActionListener(this);
		
		selectionPopUpMenu.add(singleSelectionMenu);
		selectionPopUpMenu.add(multiSelectionMenu);
		selectionPopUpMenu.add(zoomMenu);
				
		
		selectionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectionButton.isSelected()) {
					selectionPopUpMenu.show(selectionButton, 0, selectionButton.getBounds().height);
				}
				else {
					selectionPopUpMenu.setVisible(false);
				}
			}
		});
		
		selectionPopUpMenu.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				selectionButton.setSelected(false);
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
		
		chartButtonsPanel.add(selectionButton);
		chartDisplayPanel.add(chartButtonsPanel, BorderLayout.NORTH);
		
		scrollPane = new JScrollPane();
		// create scatter plot
		try {
			chartPanel = makeScatterPlot();
		} catch (IOException e) {}
		
		scrollPane.setViewportView(chartPanel);
		
		chartDisplayPanel.add(scrollPane, BorderLayout.CENTER);
		
		// bottom panel
		JPanel panel_1 = new JPanel();
		btnNewButton_1 = new JButton("Change X axis");
		btnNewButton_1.setActionCommand("chooseX");
		btnNewButton_1.addActionListener(this);
		panel_1.add(btnNewButton_1);
		
		chartDisplayPanel.add(panel_1, BorderLayout.SOUTH);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, chartDisplayPanel, tableDisplayPanel);
		splitPane.setDividerSize(1);
		splitPane.remove(tableDisplayPanel);
		getContentPane().add(splitPane);
		
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
		selectedRectangles = new ArrayList<Rectangle2D>();
	}
		
	/**
	 * The selected points table that will be attached to the scatter plot upon selecting the 
	 * single or multi level selection tool.
	 */
	private void initSelectedPointsTable() {
		selectedPointsTable = new JTable();

		selectedPointsTable.setModel(new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int col) {
				Class<?> returnValue;
				returnValue = Object.class;
				return returnValue;
			}
		});

		selectedPointsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		selectedPointsTable.setPreferredScrollableViewportSize(selectedPointsTable.getPreferredSize());
		selectedPointsTable.setFillsViewportHeight(true);
		selectedPointsTable.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));
	}
	
	private JMenuBar initSelectedPointsTableMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu mnFile = new JMenu("File");

		JMenuItem exportToTextItem = new JMenuItem("Export to text file");
		exportToTextItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTabletofile(selectedPointsTable, "Metadata Table Panel");
			}
		});

		JMenuItem exportToExcelItem = new JMenuItem("Export to xlsx");
		exportToExcelItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTableToExcel(selectedPointsTable);
			}
		});
		
		JMenuItem createListItem = new JMenuItem("Create list");
		createListItem.setActionCommand("create list");
		createListItem.addActionListener(this);
		
		mnFile.add(exportToTextItem);
		mnFile.add(exportToExcelItem);
		mnFile.add(createListItem);
		
		menuBar.add(mnFile);
		
		return menuBar;
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
				int maxWidth = getWidth();
				// define horizontal space between tooltip and point
				int xMargin = 25;

				int y = thisPoint.y;
				int newy = 100;
				/*
				 * select appropriate y if(y-200<=0) { newy=10; }else { newy=y-200; }
				 */
				int x = thisPoint.x;
				// if point is far right of scree show tool tip to the left
				if (maxWidth - x <= 450) {
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
		String bgColor = "#" + Integer.toHexString(MetaOmGraph.getTableColor1().getRGB()).
				substring(2);
		String bgColorAlt = "#" + Integer.toHexString(MetaOmGraph.getTableColor2().getRGB()).
				substring(2);
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
	
	/**
	 * Sets the mouse zoom tool active
	 */
	private void setZoomToolActive() {
		chartPanel.removeMouseListener(this);
		chartPanel.removeMouseMotionListener(this);
		chartPanel.setMouseZoomable(true);
		splitPane.remove(tableDisplayPanel);
		selectedPointsDisplayTableObj.clearMetaDataCols();
	}
	
	/**
	 * Sets the selection tool (Single or multi level) active
	 */
	private void setSelectionToolActive() {
		chartPanel.setMouseZoomable(false);
		chartPanel.addMouseListener(this);
		chartPanel.addMouseMotionListener(this);
		if(splitPane.getComponentCount() == 2)
			splitPane.add(tableDisplayPanel);
		singleSelectionMenu.setSelected(true);
	}
	
	private void createSampleListFrame(String title, List<String> selectedRows,
			List<String> notSelectedRows) {
		SampleMetaDataListFrame sampleListFrame = null;
		String[] activeHeaderList = metaDataColl.getHeaders();
		sampleListFrame = new SampleMetaDataListFrame(metaDataColl, selectedRows, notSelectedRows, activeHeaderList);

		sampleListFrame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
				MetaOmGraph.getMainWindow().getHeight() / 2);
		sampleListFrame.setResizable(true);
		sampleListFrame.setMaximizable(true);
		sampleListFrame.setIconifiable(true);
		sampleListFrame.setClosable(true);
		sampleListFrame.setTitle(title);
		MetaOmGraph.getDesktop().add(sampleListFrame);
		sampleListFrame.setVisible(true);
		MetaOmGraph.getActiveProject().setChanged(true);
		MetaOmGraph.fixTitle();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if("single selection".equals(e.getActionCommand())) {
			selectionButton.setIcon(MetaOmGraph.getIconTheme().getSelectIcon());
			setSelectionToolActive();
			singleSelection = true;
			multiSelection = false;
			selectedRectangles.clear();
		}
		
		if("multi selection".equals(e.getActionCommand())) {
			selectionButton.setIcon(MetaOmGraph.getIconTheme().getSelectIcon());
			setSelectionToolActive();
			singleSelection = false;
			multiSelection = true;
		}
		
		if("zoom tool".equals(e.getActionCommand())) {
			selectionButton.setIcon(MetaOmGraph.getIconTheme().getDefaultZoom());
			setZoomToolActive();
			singleSelection = false;
			multiSelection = false;
		}
		
		if("create list".equals(e.getActionCommand())) {
			List<String> selectedRows = selectedPointsDisplayTableObj.getMetaDataColsInTable();
			createSampleListFrame("Selected samples list", new ArrayList<String>(), selectedRows);
		}

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
				// display querypanel
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
				e1.printStackTrace();
			}
			updateChart();

			return;
		}

		// show hide legend
		if ("legend".equals(e.getActionCommand())) {
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
				changeSeriesColor(index);
				return;
			} else if (event.getEntity() instanceof XYItemEntity) {
				changeSeriesColor(((XYItemEntity) event.getEntity()).getSeriesIndex());
				return;
			}
		}

	}

	@Override
	public void chartMouseMoved(ChartMouseEvent arg0) {}

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
			e1.printStackTrace();
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
	
	/**
	 * Find all the points in the scatter plot inside the drawn rectangle.
	 * @param start startpoint of the rectangle diagonal
	 * @param end endpoint of the rectangle diagonal
	 * @return ArrayList<String> list of selected points.
	 */
	private ArrayList<String> pointsInsideRect(Point2D start, Point2D end){
		ArrayList<String> points = new ArrayList<String>();
		Rectangle2D selectedRect = new Rectangle2D.Double();
		selectedRect.setFrameFromDiagonal(start, end);
		
		EntityCollection entities = chartPanel.getChartRenderingInfo().getEntityCollection();
		
		for(Iterator it = entities.iterator(); it.hasNext();) {
			ChartEntity entity = (ChartEntity)it.next();
			Rectangle2D entityRect = entity.getArea().getBounds2D();
			if(entityRect.intersects(selectedRect)) {
				if (!(entity instanceof XYItemEntity)) {
					continue;
				}
				XYItemEntity item = (XYItemEntity) entity;
				int thisXind = item.getItem();
				// get x and y points
				XYDataset thisDS = item.getDataset();
				double chartX = thisDS.getXValue(item.getSeriesIndex(), thisXind);
				double chartY = thisDS.getYValue(item.getSeriesIndex(), thisXind);
				if(splitIndex != null) {
					String seriesNameKey = String.valueOf(chartX) + " " +
							String.valueOf(chartY);
					points.add(dataColNameToolTipMap.get(seriesNameKey));
				}
				else {
					String toolTipKey = String.valueOf(chartX) + " " +
							String.valueOf(chartY);
					points.add(dataColNameToolTipMap.get(toolTipKey));
				}
			}
		}
		return points;
	}
		
	/**
	 * Use this event to record the mouse starting position.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		rectStartPos = chartPanel.translateScreenToJava2D(e.getPoint());
	}
	
	/**
	 * Draw the selected rectangle and add the points to the selected points table
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if(rectStartPos == null)
			return;
		
		rectEndPos = chartPanel.translateScreenToJava2D(e.getPoint());
		
		ArrayList<String> points = pointsInsideRect(rectStartPos, rectEndPos);
		
		HashSet<String> pointsSet = new HashSet<String>(points);
		if(multiSelection)
			pointsSet.addAll(selectedPointsDisplayTableObj.getMetaDataColsInTable());
		else
			selectedRectangles.clear();
		
		DefaultTableModel tablemodel = (DefaultTableModel) selectedPointsTable.getModel();
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		
		selectedPointsTable.setModel(selectedPointsDisplayTableObj.getTableModel(
				new ArrayList<String>(pointsSet)));
		selectedPointsTable.repaint();
		
		Point startPoint = new Point((int)rectStartPos.getX(), (int)rectStartPos.getY());
		Point endPoint = new Point((int)rectEndPos.getX(), (int)rectEndPos.getY());
		Rectangle2D selectedRect = getRectangleInChartPanel(startPoint, endPoint);
		selectedRectangles.add(selectedRect);
		
		Graphics2D graphics = (Graphics2D) getGraphics();
		graphics.setColor(Color.RED);
		drawRectangle(graphics);
		rectStartPos = null;
		rectEndPos = null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	/**
	 * Use this event to draw the rectangle when dragging the mouse.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if(rectStartPos != null && (singleSelection || multiSelection)) {
			Point startPoint = new Point((int)rectStartPos.getX(), (int)rectStartPos.getY());
			Graphics2D graphics = (Graphics2D) getGraphics();
			graphics.setColor(Color.RED);
			Rectangle2D selectedRect = getRectangleInChartPanel(startPoint, e.getPoint());
			graphics.drawRect((int)selectedRect.getX(), (int)selectedRect.getY(), 
					(int)selectedRect.getWidth(), (int)selectedRect.getHeight());
			graphics.dispose();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	
	/**
	 * Draw all the selected rectangles in case of multi selection tool.
	 * @param graphics
	 */
	private void drawRectangle(Graphics2D graphics) {
		for(Rectangle2D rect : selectedRectangles) {
			graphics.drawRect((int)rect.getX(), (int)rect.getY(), 
					(int)rect.getWidth(), (int)rect.getHeight());
		}
	}
	
	/** 
	 * Get the rectangle in chart plane based on start and end diagonal points.
	 * 
	 * @param startPoint
	 * @param endPoint
	 * @return Rectangle2D
	 */
	private Rectangle2D getRectangleInChartPanel(Point startPoint, Point endPoint) {
		int maxHeight = chartDisplayPanel.getHeight();
		int chartPanelHeight = scrollPane.getHeight();

		int yTranslation = 0;
		yTranslation = maxHeight - chartPanelHeight - 2;
		Point translationPnt = new Point(0, yTranslation);
		 
		startPoint.translate(translationPnt.x, translationPnt.y);
		
		if(endPoint.y <= chartPanelHeight) {
			endPoint.translate(translationPnt.x, translationPnt.y);
		}	
		else {
			endPoint.setLocation(endPoint.x, chartPanelHeight - 2);	
		}
		
		Rectangle2D rectInDrawPanel = new Rectangle2D.Double();
		rectInDrawPanel.setFrameFromDiagonal(startPoint, endPoint);
		
		return rectInDrawPanel;
	}
}
