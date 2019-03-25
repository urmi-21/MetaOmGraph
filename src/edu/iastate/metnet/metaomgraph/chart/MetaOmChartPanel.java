package edu.iastate.metnet.metaomgraph.chart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.DefaultFormatterFactory;

import org.dizitart.no2.Document;
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
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
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

import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.ui.MetadataFilter;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;
import edu.iastate.metnet.metaomgraph.utils.ExceptionHandler;
import edu.iastate.metnet.metaomgraph.utils.Utils;

/**
 * A panel containing a chart of the given data.
 *
 * @author Nick Ransom
 *
 */
public class MetaOmChartPanel extends JPanel implements ChartChangeListener, ChartMouseListener, ActionListener {

	/** The selected rows in the active project */
	private int[] selected;

	/**
	 * Specifies the order to display the columns. If sortOrder[0]==7, then data
	 * column 0 will be plotted at x-value 7.
	 */
	protected int[] sortOrder;

	/**
	 * Array that determines whether to display a given column. If isVisible[x] is
	 * false, column x won't be displayed in the chart.
	 */
	private boolean[] isVisible;

	/** This panel's chart */
	private JFreeChart myChart;

	/** The chart panel (contains chart, axes, title, legend) */
	private ChartPanel myChartPanel;

	/** The field used for creating custom annotations */
	private JTextField annotationField;

	/** The dataset used for the chart */
	private AbstractIntervalXYDataset myXYDataset;

	/** The number of visible columns in the chart */
	private int visibleColumns;

	protected int[] plottedColumns;

	/** Number formatter that converts column numbers to column names */
	private ExperimentFormat myFormatter;

	/** Labels for the chart */
	private String xaxisLabel, yaxisLabel, title;

	/** Colors used for the background gradient */
	private Color color1, color2;

	private ChartAnnotator myAnnotator;

	private Point2D selectedPoint;

	private int selectedSeries;

	private PointInfoPanel pip;

	private XYLineAndShapeRenderer myRenderer;

	private ChartToolBar myToolbar;

	private String normalizeMode;

	private JPanel bottomPanel;

	private ChartScrollBar hscroll;

	// private ChartScrollBar vscroll;

	private JPanel glassPane;

	private DataSorter sorter;

	// private double rangeMin, rangeMax;

	private RangeSelector rs;

	private boolean legendVisible;

	private LegendTitle myLegend;

	private JInternalFrame myParent;

	private MetaOmProject myProject;

	// private InfoPopupManager infoPopup;

	private ChartProperties props;

	private ArrayList<double[]> myValues;

	private ArrayList<double[]> stddev;

	private ArrayList<int[]> repCounts;

	/**
	 * @author urmi For replicate plots group name contains names fo the each
	 *         replicatted group sampleNames contains name of first datacolumns in
	 *         coressponding group
	 */
	private String[] sampleNames;
	private String[] groupNames;

	// urmi changing x axis label from metadata
	public String[] tickLabels;
	// column to use for displaying x axis label
	public String xaxisColumn;
	public boolean repFlag = false;

	public int[] getSortOrder() {
		return sortOrder;
	}

	public boolean repPlot = false;

	// chart colors
	private Color chartbg = MetaOmGraph.getChartBackgroundColor();
	private Color plotbg = MetaOmGraph.getPlotBackgroundColor();

	// private TreeMap<String, List<Integer>> repsMapUsed;

	// urmi for data transformation
	JMenu dataTransformMenu = new JMenu("Data transform");
	JCheckBoxMenuItem log2Item = new JCheckBoxMenuItem("<html>log<sub>2</sub></html>");
	JCheckBoxMenuItem log10Item = new JCheckBoxMenuItem("<html>log<sub>10</sub></html>");
	JCheckBoxMenuItem logeItem = new JCheckBoxMenuItem("<html>log<sub>e</sub></html>");
	JCheckBoxMenuItem sqrtItem = new JCheckBoxMenuItem("<html>sqrt</html>");
	JCheckBoxMenuItem noneItem = new JCheckBoxMenuItem("<html>None</html>");

	/**
	 * Constructs a chart panel using the given values.
	 *
	 * @param selected
	 *            The rows in the active project to be graphed.
	 * @param xaxisLabel
	 *            The label to use for the x-axis.
	 * @param yaxisLabel
	 *            The label to use for the y-axis.
	 * @param title
	 *            The chart's title.
	 * @param color1
	 *            The bottom-left color of the background gradient.
	 * @param color2
	 *            The top-right color of the background gradient.
	 * @param project
	 *            An instance of MetaOmProject for which ChartPanel is constructed.
	 */
	public MetaOmChartPanel(int[] selected, String xaxisLabel, String yaxisLabel, String title, Color color1,
			Color color2, MetaOmProject project) {
		this(selected, xaxisLabel, yaxisLabel, title, color1, color2, project, null, null, null, null, null, false,
				null);
	}

	/**
	 * Constructs a chart panel using the given values.
	 *
	 * @param selected
	 *            The rows in the active project to be graphed.
	 * @param xaxisLabel
	 *            The label to use for the x-axis.
	 * @param yaxisLabel
	 *            The label to use for the y-axis.
	 * @param title
	 *            The chart's title.
	 * @param color1
	 *            The bottom-left color of the background gradient.
	 * @param color2
	 *            The top-right color of the background gradient.
	 */
	public MetaOmChartPanel(int[] selected, String xaxisLabel, String yaxisLabel, String title, Color color1,
			Color color2, MetaOmProject project, ArrayList<double[]> myValues, final ArrayList<double[]> stddev,
			ArrayList<int[]> repCounts, String[] groupNames, String[] sampleNames, boolean repFlag,
			TreeMap<String, List<Integer>> repsUsed) {

		this.repFlag = repFlag;
		// this.repsMapUsed=repsUsed;
		if ((selected == null) || (selected.length <= 0))
			return;
		if (selected.length >= 500) {
			int result = JOptionPane.showInternalConfirmDialog(MetaOmGraph.getDesktop(), "You are trying to plot "
					+ selected.length + " datasets.  Plotting too much data can be very slow, and\n"
					+ "can cause the program to run out of memory.  Are you sure " + "you want to plot this data?",
					"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result != JOptionPane.YES_OPTION)
				return;
		}
		// set the xaxis label to datacolumn urmi
		/*
		 * change default axis in metaomproject if
		 * (MetaOmGraph.getActiveProject().getMetadataHybrid() != null) {
		 * this.xaxisColumn =
		 * MetaOmGraph.getActiveProject().getMetadataHybrid().getDataColName(); }else {
		 * this.xaxisColumn = "Data column"; }
		 */

		// this.sampleNames=new String[sampleNames.length];
		/*
		 * if (repFlag) { for (int y = 0; y < sampleNames.length; y++) {
		 * JOptionPane.showMessageDialog(null, sampleNames[y]); } }
		 */
		// if(this.sampleNames==null) {
		// JOptionPane.showMessageDialog(null,"NULLL" );
		// }
		// for(int y=0;y<this.sampleNames.length;y++) {
		// JOptionPane.showMessageDialog(null,this.sampleNames[y] );
		// }
		// this.sampleNames[y]="7888";
		// }

		props = new ChartProperties();
		normalizeMode = "none";
		if (MetaOmGraph.getInstance() != null) {
			normalizeMode = MetaOmGraph.getInstance().getTransform();
		}
		selectedSeries = -1;
		sorter = new DataSorter(this);
		legendVisible = selected.length <= 50;
		// JOptionPane.showMessageDialog(null, "xlab:"+xaxisLabel);
		this.xaxisLabel = xaxisLabel;
		this.xaxisColumn = xaxisLabel;
		;
		this.yaxisLabel = yaxisLabel;
		this.title = title;
		this.selected = selected;
		this.color1 = color1;
		this.color2 = color2;
		this.setLayout(new BorderLayout());
		this.myProject = project;
		myChartPanel = null;
		isVisible = new boolean[myProject.getDataColumnCount()];

		if (sampleNames == null) {
			visibleColumns = myProject.getDataColumnCount() - MetaOmAnalyzer.getExcludeCount();
			this.myValues = null;
			this.stddev = null;
			this.repCounts = null;
			sortOrder = new int[myProject.getDataColumnCount()];
			for (int i = 0; i < sortOrder.length; i++)
				sortOrder[i] = i;
		} else {
			// sampnames contains a data col coressponding to a group
			// both samp names and group names have same size
			// sort order length is reduced to number of groups

			// JOptionPane.showConfirmDialog(null, "sname" + Arrays.toString(sampleNames));
			// JOptionPane.showConfirmDialog(null, "gname" + Arrays.toString(groupNames));
			// JOptionPane.showMessageDialog(null, "vals:" +
			// Arrays.toString(myValues.get(0)));
			// JOptionPane.showMessageDialog(null, "stddv:" + stddev.size());
			this.myValues = myValues;
			this.sampleNames = new String[sampleNames.length];
			System.arraycopy(sampleNames, 0, this.sampleNames, 0, sampleNames.length);
			this.groupNames = groupNames;
			this.stddev = stddev;
			this.repCounts = repCounts;
			repPlot = true;
			// urmi initialize sort order according to length of data plotted
			sortOrder = new int[sampleNames.length];
			for (int i = 0; i < sortOrder.length; i++)
				sortOrder[i] = i;
			visibleColumns = sampleNames.length;
			// set tick labels
			this.tickLabels = groupNames;

		}

		myFormatter = new ExperimentFormat();

		myAnnotator = new ChartAnnotator(this);
		pip = new PointInfoPanel(this);
		try {
			// Initialize and configure the chart panel
			initializeDataset();
			myChartPanel = new ChartPanel(myChart) {

				private Dimension oldSize = new Dimension(100, 100);

				public void paintComponent(Graphics g) {
					if (!oldSize.equals(getSize())) {
						oldSize = getSize();
						if (myChart.getBackgroundPaint() instanceof GradientPaint) {
							GradientPaint gp = (GradientPaint) myChart.getBackgroundPaint();
							// myChart.setBackgroundPaint(new GradientPaint(0, getHeight(), gp.getColor1(),
							// getWidth(), 0, gp.getColor2())); // mhhur
							// myChart.setBackgroundPaint(chartbg); // by mhhur
							// myChart.setpa
						}
					}

					super.paintComponent(g);

					// Custom drawing
					Graphics2D g2d = (Graphics2D) g.create();
					Color bg = (Color) myChart.getPlot().getBackgroundPaint();
					g2d.setColor(new Color(Color.WHITE.getRGB() - bg.getRGB()));
					// urmi
					if (getDataSorter().getRangeMarkers() != null && getDataSorter().getRangeMarkers().size() > 0) {
						// setup
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f); // mhhur
						g2d.setComposite(alpha);
						//
						drawRangeMarkers(g2d, getDataSorter().getRangeMarkers());
					}

					g2d.dispose();
				}

				private void drawRangeMarkers(Graphics2D g2d, Vector rangeMarkers) {
					if (rangeMarkers == null || rangeMarkers.size() == 0) {
						// JOptionPane.showMessageDialog(null, "Returning metaomchartpanel:347");
						// remove the chart
						g2d.dispose();
						return;
					}
					g2d.setClip(myChartPanel.getScreenDataArea());
					Paint oldPaint = g2d.getPaint();
					ValueAxis domain = myChart.getXYPlot().getDomainAxis();
					Color myColor = ((RangeMarker) rangeMarkers.get(0)).getColor();
					g2d.setColor(myColor);

					FontMetrics fontMetrics = g2d.getFontMetrics();
					FontRenderContext context = g2d.getFontRenderContext();
					Font font = g2d.getFont();

					double minx = 0;
					double maxx = 0;

					double anchorx = 0; // mhhur
					double anchory = 0;

					double theta = -Math.PI / 2.0;
					for (int index = 0; index < rangeMarkers.size(); index++) {
						RangeMarker thisMarker = (RangeMarker) rangeMarkers.get(index);
						if (thisMarker.getLabel() == "" || thisMarker.getLabel() == null)
							continue;

						minx = chartToNearestScreenX(thisMarker.getStart() - .5);
						maxx = chartToNearestScreenX(thisMarker.getEnd() + .5);

						if (thisMarker.getStyle() == RangeMarker.HORIZONTAL) {
							if ((thisMarker.getStart() - .5 < domain.getUpperBound())
									|| (thisMarker.getEnd() + .5 < domain.getLowerBound())) {
								g2d.drawLine((int) minx, 30, (int) maxx, 30);
								if (domain.getLowerBound() < thisMarker.getStart())
									g2d.drawLine((int) minx, 25, (int) minx,
											(int) myChartPanel.getScreenDataArea().getMaxY());
								if (domain.getUpperBound() > thisMarker.getEnd() - 1)
									g2d.drawLine((int) maxx, 25, (int) maxx,
											(int) myChartPanel.getScreenDataArea().getMaxY());
								if (domain.getLowerBound() < thisMarker.getEnd() - 1) {
									Font oldFont = g2d.getFont();
									g2d.setFont(g2d.getFont().deriveFont((float) 10));
									g2d.drawString(thisMarker.getLabel(), (int) minx + 3, 30);
									g2d.setFont(oldFont);
								}
								g2d.setColor(new Color(myColor.getRed(), myColor.getGreen(), myColor.getBlue(), 30));
								g2d.fillRect((int) minx, 0, (int) (maxx - minx),
										(int) myChartPanel.getScreenDataArea().getMaxY());
							}
						} else {
							if ((thisMarker.getEnd() + .5 < domain.getLowerBound())
									|| (thisMarker.getStart() - .5 > domain.getUpperBound()))
								continue;

							g2d.setColor(thisMarker.getColor());
							g2d.drawLine((int) minx, (int) myChartPanel.getScreenDataArea().getMinY(), (int) minx,
									(int) myChartPanel.getScreenDataArea().getMaxY());
							g2d.drawLine((int) maxx, (int) myChartPanel.getScreenDataArea().getMinY(), (int) maxx,
									(int) myChartPanel.getScreenDataArea().getMaxY());
							AffineTransform at = g2d.getTransform();

							TextLayout layout = new TextLayout(thisMarker.getLabel(), font, context);

							anchorx = chartToNearestScreenX(
									(thisMarker.getEnd() - thisMarker.getStart()) / 2 + thisMarker.getStart())
									+ layout.getDescent(); // mhhur
							anchory = SwingUtilities.computeStringWidth(fontMetrics, thisMarker.getLabel())
									+ getScreenDataArea().getMinY() + 7; // by mhhur

							g2d.translate(anchorx, anchory); // by mhhur

							g2d.rotate(theta);
							g2d.drawString(thisMarker.getLabel(), 0, 0);
							g2d.setTransform(at); // mhhur
						}
					}
					g2d.setPaint(oldPaint);
				}

				public void restoreAutoDomainBounds() {
					myChart.getXYPlot().getDomainAxis().setLowerBound(-1);
					myChart.getXYPlot().getDomainAxis().setUpperBound(visibleColumns);
					// myChart.getXYPlot().getDomainAxis().setUpperBound(
					// myProject.getDataColumnCount() + 1);
					hscroll.setEnabled(false);
				}

				/*
				 * public void restoreAutoRangeBounds() {
				 * myChart.getXYPlot().getRangeAxis().setUpperBound(rangeMax);
				 * myChart.getXYPlot().getRangeAxis().setLowerBound(rangeMin); //
				 * vscroll.setEnabled(false); }
				 */

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

						widthField = new JFormattedTextField(new DefaultFormatterFactory(af),
								new Integer(myChartPanel.getWidth()));
						heightField = new JFormattedTextField(new DefaultFormatterFactory(af),
								new Integer(myChartPanel.getHeight()));
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
								if ((overwrite == JOptionPane.CANCEL_OPTION)
										|| (overwrite == JOptionPane.CLOSED_OPTION))
									return;
								else if (overwrite == JOptionPane.YES_OPTION)
									ready = true;
								else
									destination = null; // No option
							} else
								ready = true;
						}
						final int oldDrawWidth = myChartPanel.getMaximumDrawWidth();
						final int oldDrawHeight = myChartPanel.getMaximumDrawHeight();
						final int newWidth = Integer.parseInt(widthField.getText());
						final int newHeight = Integer.parseInt(heightField.getText());
						final File trueDest = new File(destination.getAbsolutePath());
						myChartPanel.setMaximumDrawWidth(newWidth);
						myChartPanel.setMaximumDrawHeight(newHeight);
						try {
							ComponentToImage.saveAsPNG(myChartPanel, trueDest, newWidth, newHeight);
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
						myChartPanel.setMaximumDrawWidth(oldDrawWidth);
						myChartPanel.setMaximumDrawHeight(oldDrawHeight);
					} else
						super.actionPerformed(e);
				}

				@Override
				public String getToolTipText() {
					return "result of a plain request";
				}

				// show metadata on mouse hover
				@Override
				// changed by urmi
				// new function reads metadata from metadata hybrid class
				public String getToolTipText(MouseEvent event) {
					// if no metadata then return
					// change to gethybridmetada==null
					if (MetaOmGraph.getActiveProject().getMetadataHybrid() == null) {
						return null;
					}
					ChartEntity entity = myChartPanel.getChartRenderingInfo().getEntityCollection()
							.getEntity(event.getPoint().getX(), event.getPoint().getY());
					if (!(entity instanceof XYItemEntity)) {

						return null;
					}
					XYItemEntity item = (XYItemEntity) entity;
					// get item from data.
					// itemX is the nth item on X axis starting from zero
					// if plot is sorted the itemX is calculated according to new plot
					long itemX = myChartPanel.getChart().getXYPlot().getDataset()
							.getX(item.getSeriesIndex(), item.getItem()).longValue();
					// JOptionPane.showMessageDialog(null, "this item is:" + itemX);
					// plotting reps
					// change to display metadata of group
					if (stddev != null) {
						int maxrowsinMD = 40;
						int maxStringLen = 500;
						int maxGrpStringLen = 100;
						// find group name for a sample in default reps
						String thisSampname = getFormatter().format(itemX);
						String thisGname = getGroupName(thisSampname);
						// Get data cols used in this average plot
						String dataColsinGroup = String.join(",",
								getProject().getMetadataHybrid().getIncludedColumnNameRep(thisGname, repsUsed));
						// truncate repsused string
						if (dataColsinGroup.length() > maxGrpStringLen) {
							dataColsinGroup = dataColsinGroup.substring(0, maxGrpStringLen) + "...";
						}

						// get the index of a sample in group to find metadata of the group
						int thisSampIndex = getProject().getMetadataHybrid().getColIndexbyName(thisSampname);
						// get all metadata for this Gname
						String[][] tableData = getProject().getMetadataHybrid()
								.getNodeMetadata(getProject().getMetadataHybrid().getParentNodeForCol(thisSampIndex));
						//if nothing is returned. this should not happen.
						if(tableData==null) {
							return "Error. Metadata not found!!";
						}
						
						String text = "<html><table bgcolor=\"#FFFFFF\">" + " <tr>\n"
								+ "            <th>Attribute</th>\n" + "            <th>Value</th>\n" + "        </tr>";

						String bgColor = "#" + Integer.toHexString(MetaOmGraph.getTableColor1().getRGB()).substring(2);
						;
						String bgColorAlt = "#"
								+ Integer.toHexString(MetaOmGraph.getTableColor2().getRGB()).substring(2);
						text += "<tr bgcolor=" + bgColor + "><td><font size=-2>Group Name</font></td>";
						text += "<td><font size=-2>" + thisGname + "</font></td></tr>";
						text += "<tr bgcolor=" + bgColorAlt + "><td><font size=-2>Data columns used</font></td>";
						text += "<td><font size=-2>" + dataColsinGroup + "</font></td></tr>";
						String[] rowColors = { bgColor, bgColorAlt };
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
							text += "<td><font size=-2>" + Utils.wrapText(thisData.trim(), 100, "<br>")
									+ "</font></td>";
							text += "</tr>";
							colorIndex = (colorIndex + 1) % rowColors.length;
							// JOptionPane.showMessageDialog(null," message"); //urmi
						}
						return text.toString();
					}
					/*
					 * getMetadataForCol(int col, boolean includeParents) returns String[][] returns
					 * the value as a string [][] with colname-value pairs
					 * sortOrder[plottedColumns[(int) itemX]] gives col number of selected datapoint
					 * on x axis e.g. if original data file cols are R1,R2,R3,R4 then
					 * sortOrder[plottedColumns[(int) itemX]] will map to (for plot sorted in any
					 * order): R1 --> 0 R2-->1 R3 -->2 R4-->3
					 */

					String[][] tableData = getProject().getMetadataHybrid()
							.getNodeMetadata(sortOrder[plottedColumns[(int) itemX]]);
					
					//if nothing is returned. this should not happen.
					if(tableData==null) {
						return "Error. Metadata not found!!";
					}
					
					int maxrowsinMD = 40;
					int maxStringLen = 500;
					String text = "<html><head> " + "<style>" + ".scrollit {\n" + "    overflow:scroll;\n"
							+ "    height:100px;\n" + "}" + "</style></head><body>"

							+ "<div class=\"scrollit\"> <table bgcolor=\"#FFFFFF\" width=\"400\">" + " <tr>\n"
							+ "            <th>Attribute</th>\n" + "            <th >Value</th>\n" + "        </tr>";
					String bgColor = "#" + Integer.toHexString(MetaOmGraph.getTableColor1().getRGB()).substring(2);
					;
					String bgColorAlt = "#" + Integer.toHexString(MetaOmGraph.getTableColor2().getRGB()).substring(2);
					String[] rowColors = { bgColor, bgColorAlt };
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
						text += "<td><font size=-2>" + "There is no metadata on " + getFormatter().format(itemX)
								+ "<br>" + "</font></td>";
						text += "<td><font size=-2>" + "" + "<br>" + "</font></td>";
						text += "</tr>";
					}

					text += "</table> </div> </body></html>";
					// System.out.println(text);

					return text;
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

			// ToolTipManager.sharedInstance().setDismissDelay(0);
			if (myChartPanel.getChart().getPlot() != null) {
				myChartPanel.restoreAutoRangeBounds();
			}
			myChartPanel.setMouseZoomable(true, false);
			myChartPanel.setToolTipText("chart panel tooltip!");
			// myChartPanel.setHorizontalAxisTrace(true);
			// myChartPanel.setVerticalAxisTrace(true);
			myChartPanel.setDoubleBuffered(true);
			myChartPanel.setInitialDelay(0);
			int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
			myChartPanel.setMaximumDrawWidth(width);
			myChartPanel.setMaximumDrawHeight(height);
			myChartPanel.addChartMouseListener(myAnnotator);
			myChartPanel.addChartMouseListener(this);
			// Add options to the popup menu
			JMenu manageMenu = new JMenu("Manage");
			JMenuItem manageAnnotationsItem = new JMenuItem("Annotatons...");
			manageAnnotationsItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					myAnnotator.manageAnnotations();
					// s
				}

			});
			JMenuItem manageColumnsItem = new JMenuItem("Columns...");
			manageColumnsItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					// removed urmi
					// manageColumns();

					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								MetadataFilter frame = new MetadataFilter(
										MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataCollection(),
										false, getThisChartPanel());

								frame.setVisible(true);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				}
			});
			manageMenu.add(manageAnnotationsItem);
			manageMenu.add(manageColumnsItem);
			// urmi add manage range markers
			JMenuItem manageMarkersItem = new JMenuItem("Markers...");
			manageAnnotationsItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					myAnnotator.manageAnnotations();

				}

			});
			// manageMenu.add(manageMarkersItem);

			// transform data from chart right click menu

			log2Item.setSelected("log2".equals(normalizeMode));
			log2Item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					log2Item.setSelected(true);
					log10Item.setSelected(false);
					logeItem.setSelected(false);
					sqrtItem.setSelected(false);
					noneItem.setSelected(false);
					doTransformation();
					initializeDataset();
				}
			});

			log10Item.setSelected("log10".equals(normalizeMode));
			log10Item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					log2Item.setSelected(false);
					log10Item.setSelected(true);
					logeItem.setSelected(false);
					sqrtItem.setSelected(false);
					noneItem.setSelected(false);
					doTransformation();
					initializeDataset();
				}
			});

			logeItem.setSelected("loge".equals(normalizeMode));
			logeItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					log2Item.setSelected(false);
					log10Item.setSelected(false);
					logeItem.setSelected(true);
					sqrtItem.setSelected(false);
					noneItem.setSelected(false);
					doTransformation();
					initializeDataset();
				}
			});

			sqrtItem.setSelected("sqrt".equals(normalizeMode));
			sqrtItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					log2Item.setSelected(false);
					log10Item.setSelected(false);
					logeItem.setSelected(false);
					sqrtItem.setSelected(true);
					noneItem.setSelected(false);
					doTransformation();
					initializeDataset();
				}
			});

			noneItem.setSelected("NONE".equals(normalizeMode));
			noneItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					log2Item.setSelected(false);
					log10Item.setSelected(false);
					logeItem.setSelected(false);
					sqrtItem.setSelected(false);
					noneItem.setSelected(true);
					doTransformation();
					initializeDataset();
				}
			});

			dataTransformMenu.add(log2Item);
			dataTransformMenu.add(log10Item);
			dataTransformMenu.add(logeItem);
			dataTransformMenu.add(sqrtItem);
			dataTransformMenu.add(noneItem);

			myChartPanel.getPopupMenu().add(dataTransformMenu);
			myChartPanel.getPopupMenu().add(manageMenu);

			// Construct the chart annotation bar
			annotationField = new JTextField();
			// JPanel annotationPanel = new JPanel(new BorderLayout());
			JLabel annotationLabel = new JLabel("Annotation: ");
			this.add(myChartPanel, BorderLayout.CENTER);
			// this.add(vscroll, BorderLayout.LINE_START);
			bottomPanel = new JPanel(new BorderLayout());
			bottomPanel.add(hscroll, BorderLayout.PAGE_START);
			bottomPanel.add(pip, BorderLayout.PAGE_END);
			this.add(bottomPanel, BorderLayout.PAGE_END);
			myToolbar = new ChartToolBar(this);
			myToolbar.add(new JToolBar.Separator());
			myToolbar.add(annotationLabel);
			myToolbar.add(annotationField);
			this.add(myToolbar, BorderLayout.PAGE_START);
			this.setOpaque(true);
			this.addMouseWheelListener(hscroll);
			rs = new RangeSelector(this);
			rs.addActionListener(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs the chart.
	 *
	 */
	public void initializeDataset() {
		// final BlockingProgressDialog pm=new
		// BlockingProgressDialog(MetaOmGraph.getMainWindow(),"Creating
		// chart","Creating chart",0,selected.length);
		String seriesName;
		// XYSeries mySeries;
		// MetaOmProject myProject = myProject;
		if (stddev == null) {
			myXYDataset = new DefaultTableXYDataset();
			myRenderer = new XYLineAndShapeRenderer();
		} else {
			myXYDataset = new XYIntervalSeriesCollection();
			myRenderer = new XYErrorRenderer() {

				boolean paintAgain = false;

				@Override
				public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
						PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
						XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
					if (!paintAgain) {
						paintAgain = true;
						return;
					}
					int reps = repCounts.get(series)[item];
					int width = 1;
					if (series == getSelectedSeries())
						width = 2;

					if (reps < 3) {
						paintAgain = false;
						this.setErrorStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
								new float[] { 4f, 3f }, 0));
						paintAgain = true;
					} else {
						paintAgain = false;
						this.setErrorStroke(null);
						paintAgain = true;
					}
					super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item,
							crosshairState, pass);
				}

				@Override
				protected void fireChangeEvent() {
					if (paintAgain) {
						super.fireChangeEvent();
					}
				}
			};
		}
		// myRenderer.setShapesVisible(props.isShapePainted());
		// myRenderer.setLinesVisible(props.isLinePainted());
		myRenderer.setDefaultShapesVisible(props.isShapePainted());
		myRenderer.setDefaultLinesVisible(props.isLinePainted());

		ChartColorScheme colorScheme = props.getColorScheme();
		
		if (colorScheme != null) {
			for (int i = 0; i < selected.length; i++) {
				myRenderer.setSeriesPaint(i, colorScheme.getSeriesPaint(i));
				
				 
			}
		}
		
		//temporary set series width
		/*for (int i = 0; i < selected.length; i++) {
			myRenderer.setSeriesStroke(i, new BasicStroke(5)); 
		}*/
		
		myRenderer.setUseOutlinePaint(false);
		myRenderer.setUseFillPaint(true);
		try {
			double minValue = 0;
			double maxValue = 0;
			// new Thread() {
			// @Override
			// public void run() {
			// pm.setVisible(true);
			// }
			// }.start();
			for (int i = 0; i < selected.length; i++) {
				double[] thisData;
				if (myValues == null) {
					// thisData = myProject.getIncludedData(selected[i]);
					thisData = myProject.getAllData(selected[i]);
				} else {
					thisData = myValues.get(i);

				}
				if (i == 0) {
					minValue = thisData[0];
					maxValue = thisData[0];
				}
				for (int x = 0; x < thisData.length; x++) {
					if (thisData[x] < minValue) {
						minValue = thisData[x];
					}
					if (thisData[x] > maxValue) {
						maxValue = thisData[x];
					}
				}

				try {
					seriesName = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();
				} catch (NullPointerException npe) {
					// If the primary name entry is null, we'll just use the
					// index number as the series' name.
					seriesName = selected[i] + "";
				}

				if (stddev == null) {

					XYSeries mySeries = new XYSeries(seriesName, false, false);
					// if (normalizeMode.equals("ln"))
					// for (int x = 0; x < thisData.length; x++)
					// thisData[x] = Math.log(thisData[x]);
					// else if (normalizeMode.equals("log10"))
					// for (int x = 0; x < thisData.length; x++)
					// thisData[x] = (Math.log(thisData[x]) / Math.log(10));

					/*
					 * if (normalizeMode.equals("log2")) for (int x = 0; x < thisData.length; x++)
					 * if ((thisData[x] != 0) && (thisData[x] != Double.NaN)) thisData[x] =
					 * (Math.log(thisData[x]) / Math.log(2));
					 */
					int chartX = 0;
					plottedColumns = new int[visibleColumns];
					boolean[] exclude = MetaOmAnalyzer.getExclude();
					// JOptionPane.showMessageDialog(null, "exclude:" +Arrays.toString(exclude));
					// JOptionPane.showMessageDialog(null, "this data:" +
					// Arrays.toString(thisData));
					// JOptionPane.showMessageDialog(null, "this data:" +
					// Arrays.toString(sortOrder));
					for (int x = 0; x < thisData.length; x++) {
						// if (isVisible[sortOrder[x]]) {
						if (exclude == null || !exclude[sortOrder[x]]) {
							if (!Double.isNaN(thisData[sortOrder[x]])) {
								try {
									XYDataItem myItem = new XYDataItem(chartX, thisData[sortOrder[x]]);
									mySeries.add(myItem);
								} catch (java.lang.NumberFormatException nfe) {
									mySeries.add(x, 0);
								} catch (NullPointerException npe) {
									mySeries.add(x, 0);
								}
							} else {
								if (myProject.getBlankValue() != null) {
									XYDataItem myItem = new XYDataItem(chartX, myProject.getBlankValue().doubleValue());
									mySeries.add(myItem);
								}
							}
							plottedColumns[chartX] = x;
							chartX++;
						}
					}
					((DefaultTableXYDataset) myXYDataset).addSeries(mySeries);
				} else {

					XYIntervalSeries mySeries = new XYIntervalSeries(seriesName, false, false);
					/*
					 * if (normalizeMode.equals("log2")) for (int x = 0; x < thisData.length; x++)
					 * if ((thisData[x] != 0) && (thisData[x] != Double.NaN)) thisData[x] =
					 * (Math.log(thisData[x]) / Math.log(2));
					 */
					int chartX = 0;
					plottedColumns = new int[visibleColumns];
					boolean[] exclude = MetaOmAnalyzer.getExclude();
					// JOptionPane.showMessageDialog(null, "this data len:" + thisData.length);
					// JOptionPane.showMessageDialog(null, "exclude:" +Arrays.toString(exclude));
					// JOptionPane.showMessageDialog(null, "this data:" +
					// Arrays.toString(thisData));
					// JOptionPane.showMessageDialog(null, "sort ordr:" +
					// Arrays.toString(sortOrder));
					for (int x = 0; x < sortOrder.length; x++) {
						// skip unwanted cols
						/*
						 * if (thisData[sortOrder[x]] == -839.23183) { // plottedColumns[chartX] = x; //
						 * chartX++; continue; }
						 */
						// JOptionPane.showMessageDialog(null, "this x:" + x);
						// if (isVisible[sortOrder[x]]) {
						// JOptionPane.showMessageDialog(null, "thisdata[] x:" +
						// thisData[sortOrder[x]]);

						// replicates are already free of exluded data in new Replicate group class
						// remove if statement
						// urmi
						// if (exclude == null || !exclude[sortOrder[x]]) {

						if (!Double.isNaN(thisData[sortOrder[x]])) {
							try {
								XYDataItem myItem = new XYDataItem(chartX, thisData[sortOrder[x]]);

								mySeries.add(chartX, chartX, chartX, thisData[sortOrder[x]],
										thisData[sortOrder[x]] - stddev.get(i)[sortOrder[x]],
										thisData[sortOrder[x]] + stddev.get(i)[sortOrder[x]]);
							} catch (java.lang.NumberFormatException nfe) {
								mySeries.add(x, x, x, 0, 0, 0);
							} catch (NullPointerException npe) {
								mySeries.add(x, x, x, 0, 0, 0);
							}
						} else {
							if (myProject.getBlankValue() != null) {
								double val = myProject.getBlankValue().doubleValue();
								mySeries.add(chartX, chartX, chartX, val, val, val);
							}
						}
						plottedColumns[chartX] = x;
						// JOptionPane.showMessageDialog(null, "cx:" + chartX);
						// JOptionPane.showMessageDialog(null, "p[cx]:" + plottedColumns[chartX]);
						chartX++;
					}

					// JOptionPane.showMessageDialog(null, "this data lenDONE" + thisData.length);
					((XYIntervalSeriesCollection) myXYDataset).addSeries(mySeries);
					// System.out.println("Averaged reps, showing " + mySeries.getItemCount() + "
					// columns");
					// JOptionPane.showMessageDialog(null, "Averaged reps, showing " +
					// mySeries.getItemCount() + " columns");
				}
				// pm.setProgress(i);
			}
			// pm.dispose();

			// Construct and configure the chart
			myChart = ChartFactory.createXYLineChart(title, xaxisLabel, yaxisLabel, myXYDataset,
					PlotOrientation.VERTICAL, true, true, false);
			// urmi
			myChart.getPlot().setBackgroundPaint(plotbg);
			// change chart colors
			myChart.setBackgroundPaint(chartbg);
			
			// init colors
			//Color[] defaultColors= Utils.filterColors((Color[]) new DefaultDrawingSupplier().DEFAULT_PAINT_SEQUENCE);
			Paint[] defaultPaint=DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE;
			Color[] defaultColor=Utils.paintArraytoColor(defaultPaint);
			myChart.getPlot().setDrawingSupplier((DrawingSupplier) new DefaultDrawingSupplier(Utils.filterColors(defaultColor),
					DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));

			myAnnotator.redrawAnnotations(); // by mhhur

			((XYPlot) myChart.getPlot()).setRangeGridlinePaint(Color.LIGHT_GRAY);
			((XYPlot) myChart.getPlot()).setRangePannable(true);
			// ((XYPlot)myChart.getPlot()).setDomainTickBandPaint(Color.LIGHT_GRAY);
			((XYPlot) myChart.getPlot()).setDomainPannable(true);
			((XYPlot) myChart.getPlot()).setDomainGridlinePaint(Color.LIGHT_GRAY);
			myLegend = myChart.getLegend();
			if (!legendVisible)
				myChart.removeLegend();

			myChart.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			/**
			 * Change the formatter for reps plot plot correct rep name Change annotation of
			 * rep plots in chart
			 */
			// urmi set x axis labels
			((NumberAxis) (myChart.getXYPlot().getDomainAxis())).setNumberFormatOverride(myFormatter);
			myChart.getXYPlot().getDomainAxis().setVerticalTickLabels(true);
			// urmi
			// myChart.getXYPlot().getDomainAxis().setLabel("xxtz"); //only changes x axis
			// title
			// myChart.getXYPlot().setDomainAxis(null);

			// rangeMin = Math.floor(minValue);
			// rangeMax = Math.ceil(maxValue);
			// myChart.getXYPlot().getRangeAxis().setLowerBound(rangeMin);
			// myChart.getXYPlot().getRangeAxis().setUpperBound(rangeMax);
			// myChartPanel.restoreAutoBounds();

			// myChart.setBackgroundPaint(new GradientPaint(0, getHeight(), color1,
			// getWidth(), 0, color2)); // by mhhur
			myChart.getXYPlot().getDomainAxis().setLowerBound(-1);
			myChart.getXYPlot().getDomainAxis().setUpperBound(visibleColumns);
			myChart.getXYPlot().setRenderer(myRenderer);

			if (selectedSeries >= 0)
				myRenderer.setSeriesStroke(selectedSeries, new BasicStroke(2)); // by mhhur
			myChart.setBorderVisible(true); // by mhhur

			if (myChartPanel != null)
				myChartPanel.setChart(myChart);
			if (hscroll == null)
				hscroll = new ChartScrollBar(this, JScrollBar.HORIZONTAL); // by mhhur
			// if (vscroll == null)
			// vscroll = new ChartScrollBar(this, JScrollBar.VERTICAL);
			myChart.addChangeListener(hscroll); // mhhur
			// myChart.addChangeListener(vscroll);
			myChart.addChangeListener(this);
			/*
			 * XYPlot plot = myChart.getXYPlot(); plot.setDomainCrosshairLockedOnData(true);
			 * plot.setRangeCrosshairLockedOnData(true); if (myAnnotator != null)
			 * myChart.addProgressListener(myAnnotator); // if (infoPopup == null) { //
			 * infoPopup = new InfoPopupManager(this); // infoPopup.setEnabled(false); // }
			 * // if (props.getColorScheme() == null) { // Paint[] defaultColors = new
			 * Paint[selected.length]; // for (int i = 0; i < defaultColors.length; i++) {
			 * // defaultColors[i] = myRenderer.getSeriesPaint(i); // } //
			 * props.setColorScheme(new DefaultColorScheme(defaultColors)); // } //
			 * myChart.addProgressListener(infoPopup); ValueAxis domainAxis =
			 * plot.getDomainAxis(); domainAxis.setLowerBound(0);
			 * domainAxis.setUpperBound(visibleColumns); if (hscroll != null) {
			 * hscroll.setMax(visibleColumns); }
			 * 
			 */
			if (myChartPanel != null) {
				myChartPanel.restoreAutoBounds();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs and displays an internal frame on the MetaOmGraph desktop
	 * containing this chart panel.
	 */
	public void createInternalFrame() {
		if (myChartPanel == null)
			return;
		// System.out.println("Making it!");
		String frameTitle = xaxisLabel + " vs " + yaxisLabel + " Plot";

		myParent = new MetaOmFrame(frameTitle);
		myParent.putClientProperty("JInternalFrame.frameType", "normal");
		myParent.getContentPane().add(this, BorderLayout.CENTER);
		int width = MetaOmGraph.getMainWindow().getWidth();
		int height = MetaOmGraph.getMainWindow().getHeight();
		myParent.setSize(width - 200, height - 200);
		myParent.setLocation((width - myParent.getWidth()) / 2, (height - myParent.getHeight()) / 2);
		myParent.setClosable(true);
		myParent.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		myParent.setResizable(true);
		myParent.setIconifiable(true);
		myParent.setMaximizable(true);
		myParent.setFrameIcon(new ImageIcon(getClass().getResource("/resource/MetaOmicon16.png")));
		MetaOmGraph.getDesktop().add(myParent);
		glassPane = new JPanel(null);
		myParent.setGlassPane(glassPane);
		glassPane.setOpaque(false);
		// if (infoPopup != null) {
		// myParent.addInternalFrameListener(infoPopup);
		// myParent.addComponentListener(infoPopup);
		// }
		myParent.setName("plotwindow.php");
		myParent.show();

		// urmi
		// set exception listener
		if (this.myParent == null) {
			JOptionPane.showMessageDialog(null, "setting exception listener: NULL");
		}
		ExceptionHandler.getInstance(this.myParent).setUseBuffer(true);
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance(this.myParent));
	}

	/*
	 * Enables onscreen selection
	 */
	public void enableRangeSelection() {
		myChartPanel.setMouseZoomable(false, false);
		myChartPanel.addMouseListener(rs);
		myChartPanel.addMouseMotionListener(rs);
	}

	/**
	 * Returns selected range in the screen
	 *
	 * @return rs instance of <code>RangeSelector</code>
	 */
	public RangeSelector getRangeSelector() {
		return rs;
	}

	/**
	 * Saves the file in PNG format
	 * 
	 * @param dest
	 *            destination address where the file should be saved
	 * @throws IOException
	 *             if there is an error retrieving data from the project
	 */
	public void saveAsPNG(File dest) throws IOException {
		saveAsPNG(dest, getWidth(), getHeight());
	}

	/**
	 * Saves the file in PNG format
	 * 
	 * @param dest
	 *            destination address where the file should be saved
	 * @param width
	 *            width of the selected region
	 * @param height
	 *            height of the selected region
	 * @throws IOException
	 *             if there is an error retrieving data from the project
	 */
	public void saveAsPNG(File dest, int width, int height) throws IOException {
		ComponentToImage.saveAsPNG(myChartPanel, dest, width, height);
	}

	/**
	 * Displays a table of the chart's columns
	 */
	public void manageColumns() {
		MetaOmAnalyzer.showExcludeDialog(myProject, MetaOmGraph.getMainWindow());
		visibleColumns = myProject.getDataColumnCount() - MetaOmAnalyzer.getExcludeCount();
		initializeDataset();
		ValueAxis domainAxis = myChart.getXYPlot().getDomainAxis();
		ValueAxis rangeAxis = myChart.getXYPlot().getRangeAxis();
		domainAxis.setLowerBound(0);
		domainAxis.setUpperBound(visibleColumns);
		myChartPanel.restoreAutoBounds();
		if (hscroll != null) {
			hscroll.setMax(visibleColumns);
		}
		// rangeAxis.setLowerBound(lowY);
		// rangeAxis.setUpperBound(highY);
	}

	/**
	 * @author urmi Update the active chart after filtering metadata
	 */
	public void updateChartAfterFilter() {
		visibleColumns = myProject.getDataColumnCount() - MetaOmAnalyzer.getExcludeCount();
		initializeDataset();
		ValueAxis domainAxis = myChart.getXYPlot().getDomainAxis();
		ValueAxis rangeAxis = myChart.getXYPlot().getRangeAxis();
		domainAxis.setLowerBound(0);
		domainAxis.setUpperBound(visibleColumns);
		myChartPanel.restoreAutoBounds();
		if (hscroll != null) {
			hscroll.setMax(visibleColumns);
		}

		// clear rangemarkers
		getDataSorter().setRangeMarkers(null);

	}

	/**
	 * Get and set the visible columns after filtering data
	 * 
	 * @return
	 */
	public int getCurrentVisibleColumns() {
		return visibleColumns;

	}

	public void setCurrentVisibleColumns(int x) {
		visibleColumns = x;

	}

	/**
	 * Displays a table of the chart's columns and allows the user to manually
	 * hide/show them.
	 */
	public void oldmanageColumns2() {
		final Object[][] columnInfo = new Object[isVisible.length][2];
		// Construct the table
		for (int i = 0; i < isVisible.length; i++) {
			columnInfo[i][0] = myProject.getDataColumnHeader(i);
			columnInfo[i][1] = new Boolean(isVisible[i]);
		}
		String[] headers = { "Column", "Visible" };
		NoneditableTableModel model = new NoneditableTableModel(columnInfo, headers);
		model.setColumnEditable(1, true);
		JTable myTable = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(myTable);
		final JInternalFrame f = new JInternalFrame();
		f.putClientProperty("JInternalFrame.frameType", "normal");
		JPanel buttonPanel = new JPanel();
		final JButton okButton, cancelButton, applyButton;
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		applyButton = new JButton("Apply");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (applyButton.isEnabled())
					applyButton.getActionListeners()[0].actionPerformed(null);
				f.dispose();
			}

		});
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				f.dispose();
			}

		});
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ValueAxis domainAxis = myChart.getXYPlot().getDomainAxis();
				ValueAxis rangeAxis = myChart.getXYPlot().getRangeAxis();
				double lowX = domainAxis.getLowerBound();
				double highX = domainAxis.getUpperBound();
				double lowY = rangeAxis.getLowerBound();
				double highY = rangeAxis.getUpperBound();
				visibleColumns = 0;
				for (int i = 0; i < isVisible.length; i++) {
					isVisible[i] = ((Boolean) (columnInfo[i][1])).booleanValue();
					if (isVisible[i])
						visibleColumns++;
				}
				// Rebuild the chart
				initializeDataset();
				domainAxis = myChart.getXYPlot().getDomainAxis();
				rangeAxis = myChart.getXYPlot().getRangeAxis();
				domainAxis.setLowerBound(lowX);
				domainAxis.setUpperBound(highX);
				rangeAxis.setLowerBound(lowY);
				rangeAxis.setUpperBound(highY);
				applyButton.setEnabled(false);
			}
		});
		model.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent arg0) {
				applyButton.setEnabled(true);
			}

		});
		applyButton.setEnabled(false);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(applyButton);
		f.getContentPane().add(scrollPane, BorderLayout.CENTER);
		f.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		f.setClosable(true);
		f.setResizable(true);
		f.setIconifiable(true);
		f.setMaximizable(true);
		f.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		f.pack();
		MetaOmGraph.getDesktop().add(f);
		f.setVisible(true);
	}

	/**
	 * Replaces the domain axis tick mark labels with column names.
	 *
	 * @author Nick Ransom
	 *
	 */
	public class ExperimentFormat extends NumberFormat {

		public Number parse(String arg0, ParsePosition arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * This version of format will return either the full column name, or a
		 * shortened column name, based on the state of shortenItem
		 *
		 * @return column name
		 */
		public StringBuffer format(double arg0, StringBuffer arg1, FieldPosition arg2) {

			// urmi change x-axis labels
			boolean[] exclude = MetaOmAnalyzer.getExclude();
			// set the domain axis name in the chart
			myChart.getXYPlot().getDomainAxis().setLabel(xaxisColumn);
			// JOptionPane.showMessageDialog(null,"setxax:"+xaxisColumn);
			if (((int) arg0 < 0) || ((int) arg0 >= visibleColumns))
				return arg1;
			StringBuffer result = new StringBuffer("" + arg1);
			if (myToolbar != null && myToolbar.getNameLength() != 0) {
				String temp = null;
				if (tickLabels == null) {
					// temp = myProject.getDataColumnHeader(sortOrder[plottedColumns[(int) arg0]]);

					if (myValues != null) {
						// JOptionPane.showMessageDialog(null,"arg0"+(int) arg0);
						// JOptionPane.showMessageDialog(null,"plot[arg0"+plottedColumns[(int) arg0]);
						// temp="sss:"+String.valueOf(arg0)+":"+arg1;
						temp = sampleNames[sortOrder[(int) arg0]];
					} else {
						temp = myProject.getDataColumnHeader(sortOrder[plottedColumns[(int) arg0]]);
					}
					// JOptionPane.showMessageDialog(null,"NULLLLL");
					// urmi if plotting reps show rep names
					// temp = myProject.(sortOrder[plottedColumns[(int) arg0]]);
				} else {
					// JOptionPane.showMessageDialog(null,"2arg0"+(int) arg0);
					// JOptionPane.showMessageDialog(null, "sampname
					// now:"+Arrays.toString(sampleNames));
					// correctly find ticklabels for excluded data
					/**
					 * find nth non hidden value and get that tick label where n is the index of
					 * plotted column when plotting avg replicate plot, exclude columns are taken
					 * care of internally in ReplicateGroups class
					 */
					if (exclude != null && myValues == null) {
						// if (true) { // force exception
						boolean found = false;
						int numHid = 0;
						// find the nth non hidden index in sort order
						for (int i = 0; i < sortOrder.length; i++) {
							if (!exclude[sortOrder[i]]) {
								numHid++;
							}
							if (numHid - 1 == (int) arg0) {
								temp = tickLabels[sortOrder[i]];
								found = true;
								break;
							}
						}
						if (!found) {
							temp = "NA";
						}

					} else {
						temp = tickLabels[sortOrder[(int) arg0]];
					}
					// JOptionPane.showMessageDialog(null, "sampname now
					// 2:"+Arrays.toString(sampleNames));
				}
				if (temp.length() > myToolbar.getNameLength())
					temp = temp.substring(0, myToolbar.getNameLength()) + "...";
				// JOptionPane.showMessageDialog(null, "temp:"+temp);
				result.append(temp);
			}
			return result;
			// if (shortenItem.isSelected())
			// result.append(MetaOmGraph.getActiveProject()
			// .getDataColumnHeader(
			// sortOrder[plottedColumns[(int) arg0]], true));
			// else
			// result.append(MetaOmGraph.getActiveProject()
			// .getDataColumnHeader(
			// sortOrder[plottedColumns[(int) arg0]]));
			// return result;
		}

		/**
		 * This version of format will always return the full column name, regardless of
		 * the state of shortenItem. Both versions are included so that we can use this
		 * formatter to format annotations without shortening them.
		 */
		public StringBuffer format(long arg0, StringBuffer arg1, FieldPosition arg2) {
			if (((int) arg0 < 0) || ((int) arg0 >= visibleColumns))
				return arg1;
			StringBuffer result = new StringBuffer("" + arg1);
			if (sampleNames == null) {
				result.append(myProject.getDataColumnHeader(sortOrder[plottedColumns[(int) arg0]]));
			} else {
				try {
					result.append(sampleNames[sortOrder[(int) arg0]]);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			return result;
		}

		// public StringBuffer format(int arg0) {
		// return format((long)arg0,new StringBuffer(),null);
		// }
	}

	/**
	 * Converts a point on the screen to a point on the chart.
	 *
	 * @param p
	 *            The point on the screen.
	 * @return The corresponding x,y coordinates on the chart. Null if the given
	 *         screen point is not on the chart.
	 */
	public Point2D screenToChart(Point2D p) {
		Rectangle2D chartArea = myChartPanel.getScreenDataArea();
		if (!chartArea.contains(p)) {
			double x, y;
			if (p.getX() > chartArea.getMaxX())
				x = chartArea.getMaxX();
			else if (p.getX() < chartArea.getMinX())
				x = chartArea.getMinX();
			else
				x = p.getX();
			if (p.getY() > chartArea.getMaxY())
				y = chartArea.getMaxY();
			else if (p.getY() < chartArea.getMinY())
				y = chartArea.getMinY();
			else
				y = p.getY();

			return new Point2D.Double(x, y);
		}
		double xpos = p.getX() - chartArea.getMinX();
		double ypos = p.getY() - chartArea.getMinY();
		double width = chartArea.getWidth();
		double height = chartArea.getHeight();
		double domain = myChart.getXYPlot().getDomainAxis().getUpperBound()
				- myChart.getXYPlot().getDomainAxis().getLowerBound();
		double range = myChart.getXYPlot().getRangeAxis().getUpperBound()
				- myChart.getXYPlot().getRangeAxis().getLowerBound();
		double xratio = xpos / width;
		double yratio = ypos / height;
		double chartXPos = xratio * domain + myChart.getXYPlot().getDomainAxis().getLowerBound();
		double chartYPos = (1 - yratio) * range + myChart.getXYPlot().getRangeAxis().getLowerBound();
		Point2D result = new Point2D.Double(chartXPos, chartYPos);
		return result;
	}

	/**
	 * Converts a point on the screen to a point on the chart.
	 *
	 * @param p
	 *            The point on the screen.
	 * @return The corresponding x,y coordinates on the chart. Null if the given
	 *         screen point is not on the chart.
	 */
	public Point chartToScreen(Point2D p) {
		if (p == null)
			return null;
		if (p.getX() < myChart.getXYPlot().getDomainAxis().getLowerBound())
			return null;
		if (p.getX() > myChart.getXYPlot().getDomainAxis().getUpperBound())
			return null;
		if (p.getY() < myChart.getXYPlot().getRangeAxis().getLowerBound())
			return null;
		if (p.getY() > myChart.getXYPlot().getRangeAxis().getUpperBound())
			return null;

		double minx = myChart.getXYPlot().getDomainAxis().getLowerBound();
		double miny = myChart.getXYPlot().getRangeAxis().getLowerBound();
		double maxx = myChart.getXYPlot().getDomainAxis().getUpperBound();
		double maxy = myChart.getXYPlot().getRangeAxis().getUpperBound();
		double domain = maxx - minx;
		double range = maxy - miny;
		Rectangle2D chartArea = myChartPanel.getScreenDataArea();
		double xpos = chartArea.getMinX() + chartArea.getWidth() * ((p.getX() - minx) / domain);
		double ypos = chartArea.getMinY() + chartArea.getHeight() * ((maxy - p.getY()) / range);
		// double
		// xpos=(chartArea.getWidth()/(domain/p.getX()))+chartArea.getMinX();
		// double
		// ypos=(chartArea.getHeight()/(range/p.getY()))+chartArea.getMaxY();
		Point result = new Point((int) xpos, (int) ypos);
		return result;
	}

	/**
	 * Returns the selected range in the chart
	 * 
	 * @param chartX
	 *            selected region in the chart
	 * @return returns selected region as a chart
	 */
	private double chartToNearestScreenX(double chartX) {
		if (chartX < myChart.getXYPlot().getDomainAxis().getLowerBound())
			return myChartPanel.getScreenDataArea().getMinX();
		else if (chartX > myChart.getXYPlot().getDomainAxis().getUpperBound())
			return myChartPanel.getScreenDataArea().getWidth() + myChartPanel.getScreenDataArea().getMinX();
		else {
			double yval = myChart.getXYPlot().getRangeAxis().getUpperBound()
					- myChart.getXYPlot().getRangeAxis().getLowerBound();
			yval /= 2;
			yval += myChart.getXYPlot().getRangeAxis().getLowerBound();
			return chartToScreen(new Point2D.Double(chartX, yval)).getX();
		}
	}

	/**
	 * Enables or disables a menuitem
	 * 
	 * @param enabled
	 *            Boolean variable which enables the menu item if its true else
	 *            disables it
	 */
	public void setExtendedInfoEnabled(boolean enabled) {
		myToolbar.setExtendedInfoEnabled(enabled);
	}

	/**
	 * Refreshes the custom sort menu and cluster metadata menu
	 */
	public void refreshSortMenu() {
		myToolbar.refreshCustomSortMenu();
		myToolbar.refreshClusterMetadataMenu();
	}

	public class MetaOmFrame extends JInternalFrame {
		public MetaOmFrame(String title) {
			super(title);

		}

		public void enableExtendedInfo() {
			setExtendedInfoEnabled(true);
		}

		public void refreshSorts() {
			refreshSortMenu();
		}
	}

	/**
	 * Changes the orientation of the chart
	 */
	public void chartChanged(ChartChangeEvent event) {
		myChart.getXYPlot().getDomainAxis()
				.setVerticalTickLabels(myChart.getXYPlot().getOrientation() == PlotOrientation.VERTICAL);
	}

	/**
	 * Returns the currnet instance of <code>ChartPanel</code>
	 * 
	 * @return current instance of <code>ChartPanel</code>
	 */
	public ChartPanel getChartPanel() {
		return myChartPanel;
	}

	/**
	 * @author urmi Return the current char panel
	 * @return
	 */
	public MetaOmChartPanel getThisChartPanel() {
		return this;
	}

	/**
	 * Returns the current instance of <code>JFreeChart</code>
	 * 
	 * @return current instance of <code>JFreeChart</code>
	 */
	public JFreeChart getChart() {
		return myChart;
	}

	/**
	 * Returns the current instance of <code>DataSorter</code>
	 * 
	 * @return current instance of <code>DataSorter</code>
	 */
	public DataSorter getDataSorter() {
		return sorter;
	}

	/**
	 * Sets the new Sorting Order
	 * 
	 * @param newOrder
	 *            integer array which sets new sorting order
	 */
	public void setSortOrder(int[] newOrder) {
		/**
		 * @author urmi pad neworder array with arbitary order to correctly work with
		 *         excluded data this makes size of sortorder consistent
		 */
		if (newOrder.length > sortOrder.length) {
			JOptionPane.showConfirmDialog(null, "Error: Size of newOrder > Sortorder");
			return;
		} else if (newOrder.length < sortOrder.length) {
			java.util.List<Integer> temp = new ArrayList<>();
			for (int i : newOrder) {
				temp.add(i);
			}

			for (int i : sortOrder) {
				if (!temp.contains(i)) {
					temp.add(i);
				}
			}
			newOrder = new int[temp.size()];
			for (int i = 0; i < temp.size(); i++) {
				newOrder[i] = temp.get(i);
			}

		}
		sortOrder = newOrder;
		// JOptionPane.showMessageDialog(null, "set SO:" + Arrays.toString(sortOrder));
	}

	/**
	 * Returns the current instance of <code>PointInfoPanel</code>
	 * 
	 * @return current instance of <code>PointInfoPanel</code>
	 */
	public PointInfoPanel getInfoPanel() {
		return pip;
	}

	/**
	 * Returns the current instance of <code>Point2D</code>
	 * 
	 * @return current instance of <code>Point2D</code>
	 */
	public Point2D getSelectedPoint() {
		// final XYPlot plot=myChart.getXYPlot();
		// return new Point2D() {
		//
		// double x=plot.getDomainCrosshairValue();
		// double y=plot.getRangeCrosshairValue();
		//
		// public double getX() {
		// return x;
		// }
		//
		// public double getY() {
		// return y;
		// }
		//
		// public void setLocation(double x, double y) {
		//
		// }
		//
		// };
		return selectedPoint;
	}

	/**
	 * Sets the selected point as a new point
	 * 
	 * @param newPoint
	 *            instance of the Selected point
	 */
	public void setSelectedPoint(Point2D newPoint) {
		selectedPoint = newPoint;
		myToolbar.setMetadataEnabled(newPoint != null);
	}

	/**
	 * Returns the current instance of <code>XYLineAndShapeRenderer</code>
	 * 
	 * @return current instance of <code>XYLineAndShapeRenderer</code>
	 */
	public XYLineAndShapeRenderer getRenderer() {
		return myRenderer;
	}

	/**
	 * Returns the selected series
	 *
	 * @return integer value which denotes the selectedseries
	 */
	public int getSelectedSeries() {
		return selectedSeries;
	}

	/**
	 * Sets the selected series
	 * 
	 * @param newSeries
	 *            integer value which denotes the selected series
	 */
	public void setSelectedSeries(int newSeries) {
		selectedSeries = newSeries;
	}

	/**
	 * Returns the current instance of <code>ExperimentFormat</code>
	 * 
	 * @return current instance of <code>ExperimentFormat</code>
	 */
	public ExperimentFormat getFormatter() {
		return myFormatter;
	}

	/**
	 * Returns the selected rows
	 * 
	 * @return integer array which denotes the selected rows
	 */
	public int[] getSelectedRows() {
		return selected;
	}

	/**
	 * Returns the current instance of <code>ChartAnnotator</code>
	 * 
	 * @return current instance of <code>ChartAnnotator</code>
	 */
	public ChartAnnotator getAnnotator() {
		return myAnnotator;
	}

	/**
	 * Returns the Annotation text for the menu item
	 * 
	 * @return <code>String</code> element which denotes the annotation text
	 */
	public String getAnnotationText() {
		return annotationField.getText().trim();
	}

	/**
	 * Sets the Annotation text
	 * 
	 * @param newText
	 *            <code>String</code> element which denotes the annotation text.
	 */
	public void setAnnotationText(String newText) {
		annotationField.setText(newText);
	}

	/**
	 * Called just after the user clicks the listened-to component.
	 */
	public void chartMouseClicked(ChartMouseEvent event) {
		if (!myParent.hasFocus()) {
			// MetaOmGraph.getMainWindow().getDesktopManager()
			// .activateFrame(myParent);
			// myParent.requestFocus();

			try {
				myParent.setSelected(true);
				myParent.toFront();

			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.requestFocus(); // by mhhur
			// myParent.toFront();
			// myParent.requestFocusInWindow();
			// return;
		}
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

	/**
	 * Called just after the user moves the mouse over the listened-to component.
	 */
	public void chartMouseMoved(ChartMouseEvent event) {
		// if ((!myChartPanel.getScreenDataArea().contains(
		// event.getTrigger().getPoint()))
		// && (event.getEntity() == null)) {
		// myChartPanel.setCursor(Cursor
		// .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		// return;
		// }
		// System.out.println(event.getEntity());
		if ((event == null) || (event.getEntity() instanceof PlotEntity)) {
			myChartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			return;
		}
		if (event.getEntity() instanceof XYItemEntity) {
			myChartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			// JToolTip tip = new JToolTip();
			// tip.setTipText("hey hey");
			// tip.setVisible(true);
			return;
		}
		myChartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
			CustomColorScheme newColorScheme = new CustomColorScheme(props.getColorScheme(), selected.length);
			newColorScheme.setSeriesPaint(series, newColor);
			newColorScheme.setSelectionPaint(series, newColor);
			props.setColorScheme(newColorScheme);
		}
	}

	/**
	 * Set a color to series
	 * @param series series index
	 * @param newColor Color value
	 */
	public void changeSeriesColor(int series, Color newColor) {
		if (newColor != null) {
			myRenderer.setSeriesPaint(series, newColor);
		}
	}

	// urmi
	/**
	 * Function to set color from palette
	 * @param colors
	 */
	void setPalette(Color[] colors) {
		if (colors == null) {
			return;
		}
		// JOptionPane.showMessageDialog(null, "cols:"+Arrays.toString(colors));
		XYPlot plot = (XYPlot) getChart().getPlot();

		int seriesCount = plot.getSeriesCount();
		for (int i = 0; i < seriesCount; i++) {
			// call change series color
			changeSeriesColor(i, colors[i % colors.length]);

		}
	}

	/**
	 * Captures the action event triggered by the user
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(RangeSelector.ACTION_COMMAND)) {
			myChartPanel.setMouseZoomable(true, false);
			myChartPanel.removeMouseListener(rs);
			myChartPanel.removeMouseMotionListener(rs);
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
		if (this.legendVisible == legendVisible)
			return;
		this.legendVisible = legendVisible;
		if (legendVisible) {
			myChart.addLegend(myLegend);
		} else {
			myChart.removeLegend();
		}
	}

	public void setPopupEnabled(boolean enabled) {
		// if (infoPopup != null)
		// infoPopup.setEnabled(enabled);
	}

	/**
	 * Returns the value of legendVisible
	 * 
	 * @return boolean variable which has the value of legendvisible
	 */
	public boolean isLegendVisible() {
		return legendVisible;
	}

	public boolean isPopupEnabled() {
		// if (infoPopup == null)
		// return false;
		// return infoPopup.isEnabled();
		return false;
	}

	/**
	 * Returns the user selected point
	 * 
	 * @return Instance of <code>Point2D</code>
	 */
	public Point2D getCrosshairPoint() {
		Point2D result = new Point2D() {

			public double getX() {
				return myChart.getXYPlot().getDomainCrosshairValue();
			}

			public double getY() {
				return myChart.getXYPlot().getRangeCrosshairValue();
			}

			public void setLocation(double x, double y) {
			}

		};
		return result;
	}

	/**
	 * Returns the current instance of <code>MetaOmProject</code>
	 * 
	 * @return current instance of <code>MetaOmProject</code>
	 */
	public MetaOmProject getProject() {
		return myProject;
	}

	/**
	 * Returns the current selected column
	 *
	 * @return the current selected column
	 */
	public String getSelectedColumn() {
		return getFormatter().format((long) getSelectedPoint().getX());
	}

	public int getProjectRow(int chartSeries) {
		return selected[chartSeries];
	}

	/**
	 * Returns the current instance of <code>ChartProperties</code>
	 * 
	 * @return current instance of <code>ChartProperties</code>
	 */
	public ChartProperties getChartProperties() {
		if (props.getColorScheme() == null && myRenderer.getSeriesPaint(0) != null) {
			Paint[] defaultColors = new Paint[selected.length];
			for (int i = 0; i < defaultColors.length; i++) {
				defaultColors[i] = myRenderer.getSeriesPaint(i);
			}
			props.setColorScheme(new DefaultColorScheme(defaultColors));
		}
		return props;
	}

	/**
	 * Returns the sampleNames
	 * 
	 * @return String array which contains the sampleNames
	 */
	public String[] getSampleNames() {
		if (sampleNames != null) {
			// JOptionPane.showMessageDialog(null, "returning curr samps:" +
			// Arrays.toString(sampleNames));
			return sampleNames;
		}
		return myProject.getDataColumnHeaders();
	}

	/**
	 * @author urmi
	 * @param sName
	 *            sampname to get coressponding group name
	 * @return
	 */
	public String getGroupName(String sName) {

		if (this.sampleNames == null || this.groupNames == null) {
			return "NA";
		}
		// JOptionPane.showMessageDialog(null, "Tosearch:" + sName);
		// JOptionPane.showMessageDialog(null, "snames:"+Arrays.toString(sampleNames));
		// JOptionPane.showMessageDialog(null, "gnames:"+Arrays.toString(groupNames));
		for (int i = 0; i < sampleNames.length; i++) {
			try {
				if (sampleNames[i].equals(sName)) {
					return groupNames[i];
				}
			} catch (java.lang.NullPointerException e) {

			}

		}

		return "NA";
	}

	public String getSampName(int index) {

		if (this.sampleNames == null || this.groupNames == null) {
			return "NA";
		}
		return sampleNames[index];
	}

	/**
	 * Returns the data for the given series
	 * 
	 * @param series
	 *            integer value which denotes the series
	 * @return Double array which has the data for the given series
	 */
	public double[] getData(int series) {
		if (myValues != null) {
			return myValues.get(series);
		}
		try {
			// return myProject.getIncludedData(selected[series]);
			// urmi
			// get values of all the data to get consistent sort order
			return myProject.getAllData(selected[series]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// public ChartColorScheme getColorScheme() {
	// return props.getColorScheme();
	// }
	// public JToolTip createToolTip() {
	// System.out.println("ToolTip a-comin'");
	// JToolTip result=new JToolTip();
	// ToolTipManager.sharedInstance();
	// return result;
	// }
	/**
	 * Returns the current instance of <code>ChartToolBar</code>
	 * 
	 * @return current instance of <code>ChartToolBar</code>
	 */
	public ChartToolBar getToolbar() {
		return myToolbar;
	}

	public void doTransformation() {

		if (log2Item.isSelected()) {
			MetaOmGraph.setTransform("log2");
			log2Item.setSelected(true);
		} else if (log10Item.isSelected()) {
			MetaOmGraph.setTransform("log10");
			log10Item.setSelected(true);
		} else if (logeItem.isSelected()) {
			MetaOmGraph.setTransform("loge");
			logeItem.setSelected(true);
		} else if (sqrtItem.isSelected()) {
			MetaOmGraph.setTransform("sqrt");
			sqrtItem.setSelected(true);
		} else if (noneItem.isSelected()) {
			MetaOmGraph.setTransform("NONE");
			noneItem.setSelected(true);
		}
	}
}