package edu.iastate.metnet.metaomgraph.chart;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;

import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import javax.swing.JScrollPane;

public class ScatterPlotChart extends JInternalFrame implements ChartMouseListener, ActionListener {

	private int[] selected;
	// pivotIndex is the ith index in the selected rows which is the x axis for the
	// scatterplot
	private int pivotIndex;
	String[] rowNames;
	private MetaOmProject myProject;
	private String xAxisname;
	private ChartPanel chartPanel;
	private JFreeChart myChart;
	private XYDataset dataset;
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

	// bottom toolbar
	private JButton btnNewButton_1;

	public static final String ZOOM_IN_COMMAND = "zoomIn";
	public static final String ZOOM_OUT_COMMAND = "zoomOut";
	public static final String ZOOM_DEFAULT_COMMAND = "defaultZoom";

	private boolean[] excludedCopy;

	// chart colors
	private Color chartbg = MetaOmGraph.getChartBackgroundColor();
	private Color plotbg = MetaOmGraph.getPlotBackgroundColor();

	Color[] colorArray = null;

	String splitCol;
	Map<String, Collection<Integer>> splitIndex;
	HashMap<String, String> seriesNameToKeyMap;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					ScatterPlotChart frame = new ScatterPlotChart(null, 0, null);
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
	public ScatterPlotChart(int[] selected, int xind, MetaOmProject mp) {

		this.selected = selected;
		// init rownames
		rowNames = mp.getDefaultRowNames(selected);
		// JOptionPane.showMessageDialog(null, Arrays.toString(rowNames));
		pivotIndex = xind;
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

		panel.add(properties);
		panel.add(save);
		panel.add(print);
		panel.add(zoomIn);
		panel.add(zoomOut);
		panel.add(defaultZoom);
		panel.add(splitDataset);
		panel.add(changePalette);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		String chartTitle = "Scatter Plot:" + String.join(",", rowNames);
		this.setTitle(chartTitle);
	}

	public ChartPanel makeScatterPlot() throws IOException {
		// create a copy of excluded
		boolean[] excluded = MetaOmAnalyzer.getExclude();
		if (excluded != null) {
			excludedCopy = new boolean[excluded.length];
			System.arraycopy(excluded, 0, excludedCopy, 0, excluded.length);
		}
		// Create dataset
		dataset = createDataset();
		// Create chart
		myChart = ChartFactory.createScatterPlot("", rowNames[pivotIndex], "", dataset);

		// Changes background color
		Shape shape = ShapeUtilities.createRegularCross(2, 1);
		XYPlot plot = (XYPlot) myChart.getPlot();
		plot.setBackgroundPaint(plotbg);
		myChart.setBackgroundPaint(chartbg);
		// plot.setpaint
		// XYItemRenderer renderer = plot.getRenderer();
		myRenderer = plot.getRenderer();

		// use palette if available
		if (colorArray != null) {
			plot.setDrawingSupplier((DrawingSupplier) new DefaultDrawingSupplier(colorArray,
					DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		} else {
			Paint[] defaultPaint = DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE;
			Color[] defaultColor = Utils.paintArraytoColor(defaultPaint);
			plot.setDrawingSupplier((DrawingSupplier) new DefaultDrawingSupplier(Utils.filterColors(defaultColor),
					DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
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

			@Override
			public String getToolTipText(MouseEvent event) {
				if (myProject.getMetadataHybrid() == null) {
					return null;
				}

				XYPlot plot = (XYPlot) myChart.getPlot(); // your plot
				// double chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea,
				// plot.getDomainAxisEdge());
				// double chartY = plot.getRangeAxis().java2DToValue(p.getY(), plotArea,
				// plot.getRangeAxisEdge());
				ChartEntity entity = getChartRenderingInfo().getEntityCollection().getEntity(event.getPoint().getX(),
						event.getPoint().getY());
				// JOptionPane.showMessageDialog(null, entity);
				if (!(entity instanceof XYItemEntity)) {
					// JOptionPane.showMessageDialog(null, "null");
					return null;
				}
				XYItemEntity item = (XYItemEntity) entity;
				int thisXind = item.getItem();
				// get x and y points

				XYDataset thisDS = item.getDataset();
				double chartX = thisDS.getXValue(item.getSeriesIndex(), thisXind);
				double chartY = thisDS.getYValue(item.getSeriesIndex(), thisXind);

				int correctColIndex = -1;
				try {
					// get correct colIndex
					if (splitIndex == null) {
						// correctColIndex =
						// myProject.getMetadataHybrid().getColIndexbyName(myProject.getDatainSortedOrder(selected[pivotIndex],
						// thisXind, excludedCopy));
						// create collection of 0 till #Samples-1

						Collection<Integer> thisIndices = new ArrayList<>(
								IntStream.rangeClosed(0, myProject.getDataColumnCount() - 1).boxed()
										.collect(Collectors.toList()));
						correctColIndex = myProject.getCorrectDataColumnForScatterPlot(selected[pivotIndex], thisXind,
								thisIndices, excludedCopy);

					} else {
						String splitIndexKey = seriesNameToKeyMap
								.get(thisDS.getSeriesKey(item.getSeriesIndex()).toString());

						correctColIndex = myProject.getCorrectDataColumnForScatterPlot(selected[pivotIndex], thisXind,
								splitIndex.get(splitIndexKey), excludedCopy);
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (correctColIndex == -1) {
					return null;
				}

				return createTooltip(correctColIndex, chartX, chartY);

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

	private XYDataset createDataset2() throws IOException {
		XYSeriesCollection dataset = new XYSeriesCollection();
		double[] dataX = myProject.getIncludedData(selected[pivotIndex]);
		xAxisname = myProject.getRowName(selected[pivotIndex])[myProject.getDefaultColumn()].toString();
		String yAxisname = "";
		for (int i = 0; i < selected.length; i++) {
			if (i == pivotIndex) {
				continue;
			}
			double[] dataY = myProject.getIncludedData(selected[i]);
			yAxisname = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();
			XYSeries series1 = new XYSeries(xAxisname + " vs. " + yAxisname);
			for (int j = 0; j < dataX.length; j++) {
				series1.add(dataX[j], dataY[j]);
			}
			dataset.addSeries(series1);
		}

		return dataset;
	}

	private XYDataset createDataset() throws IOException {

		// int serInd=0;
		XYSeriesCollection dataset = new XYSeriesCollection();
		double[] dataX = myProject.getAllData(selected[pivotIndex]);
		if (splitIndex != null) {
			seriesNameToKeyMap = new HashMap<>();
		}
		xAxisname = myProject.getRowName(selected[pivotIndex])[myProject.getDefaultColumn()].toString();
		String yAxisname = "";
		for (int i = 0; i < selected.length; i++) {
			if (i == pivotIndex) {
				continue;
			}
			double[] dataY = myProject.getAllData(selected[i]);

			if (splitIndex != null) {

				// split dataX
				for (String key : splitIndex.keySet()) {
					yAxisname = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString() + "(" + key
							+ ")";
					XYSeries series1 = new XYSeries(xAxisname + " vs. " + yAxisname);
					Collection<Integer> thisInd = splitIndex.get(key);

					for (int ind : thisInd) {
						if (excludedCopy == null) {
							series1.add(dataX[ind], dataY[ind]);
						} else {
							if (!excludedCopy[ind]) {
								series1.add(dataX[ind], dataY[ind]);
							}
						}

					}
					dataset.addSeries(series1);
					seriesNameToKeyMap.put(xAxisname + " vs. " + yAxisname, key);
				}
			} else {

				yAxisname = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();
				XYSeries series1 = new XYSeries(xAxisname + " vs. " + yAxisname);
				for (int j = 0; j < dataX.length; j++) {
					if (excludedCopy == null) {
						series1.add(dataX[j], dataY[j]);
					} else {
						if (!excludedCopy[j]) {
							series1.add(dataX[j], dataY[j]);
						}
					}

				}
				dataset.addSeries(series1);
			}
		}

		return dataset;
	}

	private String createTooltip(int colIndex, double x, double y) {
		DecimalFormat df = new DecimalFormat("####0.00");
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

		if (MetaOmGraph.getActiveProject().getMetadataHybrid() == null) {
			return text;
		}

		String[][] tableData = myProject.getMetadataHybrid().getMetadataForCol(colIndex);
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

		// added by mhhur
		if (tableData.length == 0 || tableData == null) {
			text += "<tr bgcolor=" + rowColors[colorIndex] + ">";
			text += "<td><font size=-2>" + "There is no metadata" + "<br>" + "</font></td>";
			text += "<td><font size=-2>" + "" + "<br>" + "</font></td>";
			text += "</tr>";
		}

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
				colorArray = cb.getColorPalette(numColors);
				setPalette(Utils.filterColors(colorArray));
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
			String[] fields2 = new String[fields.length + 2];
			fields2[0] = "Reset";
			int selectedInd = 0;
			for (int i = 0; i < fields.length; i++) {
				fields2[i + 1] = fields[i];
				if (splitCol != null && splitCol.equals(fields2[i + 1])) {
					selectedInd = i + 1;
				}
			}
			fields2[fields2.length - 1] = "More...";
			String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, fields2, fields2[selectedInd]);
			if (col_val == null) {
				return;
			}

			if (col_val.equals("Reset")) {
				splitCol = null;
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

			} else {
				// split data set by values of col_val
				selectedVals.add(col_val);
				splitCol = col_val;
			}
			
			splitIndex = myProject.getMetadataHybrid().cluster(selectedVals);

			try {
				createDataset();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateChart();

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

		// updateChart();

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

}
