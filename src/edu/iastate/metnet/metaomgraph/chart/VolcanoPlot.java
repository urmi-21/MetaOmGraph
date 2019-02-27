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
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultFormatterFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
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
	
	
	private double significanceCutOff=0.01;
	private double foldChangeCutOffUp=1;
	private double foldChangeCutOffDwn=-1;
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
		/*
		 * if (colorArray != null) { plot.setDrawingSupplier((DrawingSupplier) new
		 * DefaultDrawingSupplier(colorArray,
		 * DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
		 * DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
		 * DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
		 * DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
		 * DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE)); } else { Paint[]
		 * defaultPaint = DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE; Color[]
		 * defaultColor = Utils.paintArraytoColor(defaultPaint);
		 * plot.setDrawingSupplier((DrawingSupplier) new
		 * DefaultDrawingSupplier(Utils.filterColors(defaultColor),
		 * DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
		 * DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
		 * DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
		 * DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
		 * DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE)); }
		 */
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

				String thisFeature = getFeaturename(thisXind);

				// return "tooooool:" + String.valueOf(chartX) + "," + String.valueOf(chartY) +
				// thisFeature;
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

	private XYDataset createVolcanoDataset() throws IOException {

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

	private void formatInput() {
		
		/*split data into three categories
		 * 1: significantly upregulated
		 * 2: significantly dwnregulated
		 * 3: unregulated
		 */
		
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

	private String getFeaturename(int indexInPlot) {
		String res = "";
		// since data is ordered by x-axis just return the ith value
		return featureNames.get(indexInPlot);
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
		String[] infoCols=myProject.getInfoColumnNames();
		Object[] featureRow=myProject.getRowName(myProject.getRowIndexbyName(featureName, true));
		
		
		String[][] tableData = new String[infoCols.length][2];
		for(int i=0;i<infoCols.length;i++) {
			tableData[i][0]=infoCols[i];
			tableData[i][1]=String.valueOf(featureRow[i]);
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
	
	class VolcanoDataSet{
		List<String> featureNames;
		List<Double> foldChange;
		List<Double> pVals;
		
		public VolcanoDataSet(List<String> featureNames,List<Double> foldChange,List<Double> pVals) {
			this.featureNames=featureNames;
			this.foldChange=foldChange;
			this.pVals=pVals;
			
		}
		
	}

}
