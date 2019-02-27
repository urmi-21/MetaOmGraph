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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;

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
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class VolcanoPlot extends JInternalFrame implements ChartMouseListener, ActionListener {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VolcanoPlot frame = new VolcanoPlot();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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

	List<String> featureNames;
	List<Double> foldChange;
	List<Double> pVals;

	private double significanceCutOff = 0.01;
	private double foldChangeCutOffUp = 1;
	private double foldChangeCutOffDwn = -1;
	private VolcanoDataSet upRegData;
	private VolcanoDataSet dwnRegData;
	private VolcanoDataSet unRegData;

	/**
	 * Create the frame.
	 */
	public VolcanoPlot() {

		this(null, null, null);

	}

	public VolcanoPlot(List<String> featureNames, List<Double> fc, List<Double> pv) {
		this.featureNames = featureNames;
		this.foldChange = fc;
		this.pVals = pv;
		// format the data; order data by foldchange values so order in lists and chart
		// is maintained
		formatInput();
		splitData();
		
		//initialize color array
		colorArray=new Color[] {new  Color(228, 26, 28, 180),new  Color(55, 126, 184, 180), new  Color(153, 153, 153, 121)};
		
		
		

		myProject = MetaOmGraph.getActiveProject();
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
			chartPanel = makeVolcanoPlot();
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
		splitDataset.setToolTipText("Select cutoffs");
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
		String chartTitle = "Volcano Plot:";
		this.setTitle(chartTitle);

	}

	public ChartPanel makeVolcanoPlot() throws IOException {

		// Create dataset
		dataset = createVolcanoDataset();
		myChart = ChartFactory.createScatterPlot("", "log2 fold change", "-log10 p-value", dataset);

		// Changes background color
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
				// JOptionPane.showMessageDialog(null, "TS:"+item.getSeriesIndex());
				String thisFeature = getFeaturename(thisXind, item.getSeriesIndex());
				return createTooltipTable(thisFeature, chartX, chartY);
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

	private XYDataset createVolcanoDatasetOld() throws IOException {

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries(xAxisname + " vs. ");
		for (int i = 0; i < foldChange.size(); i++) {
			double thisFC = foldChange.get(i);
			double thisPV = -1 * Math.log10(pVals.get(i));
			series1.add(thisFC, thisPV);
		}
		dataset.addSeries(series1);

		return dataset;
	}

	private XYDataset createVolcanoDataset() throws IOException {

		// create 3 series for up down and unregulated points

		XYSeriesCollection dataset = new XYSeriesCollection();
		// add upreg
		// List<String> thisName=upRegData.getNames();
		List<Double> thisFC = upRegData.getFC();
		List<Double> thisPV = upRegData.getPV();
		XYSeries seriesUp = createSeries(thisFC, thisPV, "Upregulated");
		dataset.addSeries(seriesUp);
		// add dwnReg
		thisFC = dwnRegData.getFC();
		thisPV = dwnRegData.getPV();
		XYSeries seriesDwn = createSeries(thisFC, thisPV, "Downregulated");
		dataset.addSeries(seriesDwn);
		// add unReg
		thisFC = unRegData.getFC();
		thisPV = unRegData.getPV();
		XYSeries seriesUn = createSeries(thisFC, thisPV, "Unregulated");
		dataset.addSeries(seriesUn);

		return dataset;
	}

	private XYSeries createSeries(List<Double> thisFC, List<Double> thisPV, String serName) {
		XYSeries series = new XYSeries(serName);
		for (int i = 0; i < thisFC.size(); i++) {
			double fc = thisFC.get(i);
			double pv = -1 * Math.log10(thisPV.get(i) + 1e-300);
			series.add(fc, pv);
		}

		return series;
	}

	private void formatInput() {
		// order featureName, pv by logfc values
		List<Integer> indList = new ArrayList<>();
		/*
		 * for (int i = 0; i < featureNames.size(); i++) { indList.add(i); }
		 */

		// sort logfc and indList together
		TreeMap<Double, Integer> indMap = new TreeMap<>();
		for (int i = 0; i < foldChange.size(); i++) {
			indMap.put(foldChange.get(i), i);
		}

		// get sorted order of index
		List<String> tempNames = new ArrayList<>();
		List<Double> tempfc = new ArrayList<>();
		List<Double> temppv = new ArrayList<>();
		Iterator itr = indMap.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry pairs = (Map.Entry) itr.next();
			Integer thisInd = (Integer) pairs.getValue();
			indList.add(thisInd);
			tempNames.add(featureNames.get(thisInd));
			tempfc.add(foldChange.get(thisInd));
			temppv.add(pVals.get(thisInd));
			itr.remove();
		}
		featureNames = tempNames;
		foldChange = tempfc;
		pVals = temppv;

	}

	private void splitData() {

		/*
		 * split data into three categories after performing ordering using
		 * formatInput() 1: significantly upregulated 2: significantly dwnregulated 3:
		 * unregulated
		 */
		upRegData = new VolcanoDataSet(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		dwnRegData = new VolcanoDataSet(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		unRegData = new VolcanoDataSet(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		for (int i = 0; i < foldChange.size(); i++) {
			double thisFC = foldChange.get(i);
			double thisPV = pVals.get(i);
			String thisName = featureNames.get(i);

			if (thisFC >= foldChangeCutOffUp && thisPV < significanceCutOff) {
				// add to upregulated series
				upRegData.addDataPoint(thisName, thisFC, thisPV);
			} else if (thisFC <= foldChangeCutOffDwn && thisPV < significanceCutOff) {
				// add to dwnregulated series
				dwnRegData.addDataPoint(thisName, thisFC, thisPV);
			} else {
				// add to unregulated series
				unRegData.addDataPoint(thisName, thisFC, thisPV);
			}

		}
	}

	private String getFeaturename(int indexInPlot, int series) {
		// series 0 Upreg, 1 Dwnreg, 2 Unreg
		String res = "";
		// since data is ordered by x-axis just return the ith value
		if (series == 0) {
			return upRegData.getNames().get(indexInPlot);
		} else if (series == 1) {
			return dwnRegData.getNames().get(indexInPlot);
		} else {
			return unRegData.getNames().get(indexInPlot);
		}

	}

	private String createTooltipTable(String featureName, double x, double y) {
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
		// get gene metadata in String [][] format
		String[] infoCols = myProject.getInfoColumnNames();
		Object[] featureRow = myProject.getRowName(myProject.getRowIndexbyName(featureName, true));

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

		if (tableData.length == 0 || tableData == null) {
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
			
			//display panel to choose cutoff
			JPanel optPanel=new JPanel(new GridLayout(3, 2));
			JLabel labUpreg=new JLabel("Upregulated cutoff");
			JLabel labDwnreg=new JLabel("Downregulated cutoff");
			JLabel labpv=new JLabel("P-value cutoff");
			JTextField upCutoff=new JTextField(String.valueOf(foldChangeCutOffUp));
			JTextField dwnCutoff=new JTextField(String.valueOf(foldChangeCutOffDwn));
			JTextField pvCutoff=new JTextField(String.valueOf(significanceCutOff));
			
			optPanel.add(labUpreg);
			optPanel.add(upCutoff);
			optPanel.add(labDwnreg);
			optPanel.add(dwnCutoff);
			optPanel.add(labpv);
			optPanel.add(pvCutoff);
			int res = JOptionPane.showConfirmDialog(null, optPanel, "Input values", JOptionPane.OK_CANCEL_OPTION);
			if (res == JOptionPane.OK_OPTION) {
				//validate input
				try {
					
					double upCutOffVal=Double.parseDouble(upCutoff.getText().trim());
					double dwnCutOffVal=Double.parseDouble(dwnCutoff.getText().trim());
					double pvCutOffVal=Double.parseDouble(pvCutoff.getText().trim());
					
					foldChangeCutOffUp=upCutOffVal;
					foldChangeCutOffDwn=dwnCutOffVal;
					significanceCutOff=pvCutOffVal;
					
					updateChart();
					
				}catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Invalid values. Please check input","Invalid input",JOptionPane.ERROR_MESSAGE);
					return;
				}
					
			}		
			
			
			return;
		}

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
			splitData();
			this.chartPanel = makeVolcanoPlot();
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

	class VolcanoDataSet {
		private List<String> featureNames;
		private List<Double> foldChange;
		private List<Double> pVals;

		public VolcanoDataSet(List<String> featureNames, List<Double> foldChange, List<Double> pVals) {
			this.featureNames = featureNames;
			this.foldChange = foldChange;
			this.pVals = pVals;

		}

		public void addDataPoint(String name, double fc, double pv) {
			featureNames.add(name);
			foldChange.add(fc);
			pVals.add(pv);
		}

		public List<String> getNames() {
			return this.featureNames;
		}

		public List<Double> getFC() {
			return this.foldChange;
		}

		public List<Double> getPV() {
			return this.pVals;
		}

	}

}
