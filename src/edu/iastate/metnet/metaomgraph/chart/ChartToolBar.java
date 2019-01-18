package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.ui.MenuButton;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel;
import edu.iastate.metnet.metaomgraph.ui.ReadMetadata;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.filters.Filters;
import org.dizitart.no2.internals.NitriteService;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.store.NitriteMap;
import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import com.mysql.jdbc.Util;

//import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class ChartToolBar extends JToolBar implements ActionListener {
	public static final String ZOOM_IN_COMMAND = "zoomIn";
	public static final String ZOOM_OUT_COMMAND = "zoomOut";
	public static final String ZOOM_DEFAULT_COMMAND = "defaultZoom";
	public static final String SORT_DEFAULT_COMMAND = "defaultSort";
	public static final String SORT_NAME_COMMAND = "nameSort";
	// urmi
	public static final String SORT_XAXIS_COMMAND = "xaxisSort";
	public static final String SORT_YVAL_COMMAND = "yvalueSort";
	public static final String SORT_EXTINFO_COMMAND = "extInfoSort";
	public static final String SORT_PO_COMMAND = "poSort";
	public static final String SORT_CUSTOM_NEW_COMMAND = "newCustomSort";
	public static final String SORT_CUSTOM_LOAD_COMMAND = "loadCustomSort";
	public static final String SORT_CLUSTER_METADATA_COMMAND = "clusterMetadataSort";
	public static final String HIDE_COLUMNS_COMMAND = "hideColumns";
	public static final String EXPORT_COMMAND = "export";
	public static final String TOGGLE_LEGEND_COMMAND = "legend";
	public static final String TOGGLE_POPUP_COMMAND = "popup";
	public static final String TOGGLE_SHAPES_COMMAND = "shapes";
	public static final String TOGGLE_LINES_COMMAND = "lines";
	public static final String COLOR_SCHEME_DEFAULT_COMMAND = "default color scheme";
	public static final String COLOR_SCHEME_GRAYSCALE_COMMAND = "grayscale color scheme";
	public static final String METADATA_COMMAND = "get metadata";
	public static final String QUICK_SORT_PO_COMMAND = "Sort by POs without a dialog";
	JCheckBoxMenuItem showShapesItem;
	JCheckBoxMenuItem showLinesItem;
	JMenu colorSchemeMenu;
	JRadioButtonMenuItem defaultColorSchemeItem;
	JRadioButtonMenuItem grayColorSchemeItem;
	JRadioButtonMenuItem customColorSchemeItem;
	private ChartProperties chartProps;
	private JButton properties;
	private JButton save;
	private JButton print;
	private JButton zoomIn;
	private JButton zoomOut;
	private JButton defaultZoom;
	private JButton exportButton;
	private JButton metadataButton;
	private MenuButton sortMenuButton;
	private JToggleButton toggleLegend;
	// urmi
	private JButton removeRangeMarkers;
	private JToggleButton togglePopup;
	private JRadioButtonMenuItem defaultSortItem;
	private JRadioButtonMenuItem nameSortItem;
	// urmi
	private JRadioButtonMenuItem xaxisSortItem;
	private JRadioButtonMenuItem yvalueSortItem;
	private JRadioButtonMenuItem extInfoSortItem;
	private JRadioButtonMenuItem poSortItem;
	private JRadioButtonMenuItem newCustomSortItem;
	private JRadioButtonMenuItem metadataClusterSortItem;
	private JMenuItem hideColumnsItem;
	private JRadioButtonMenuItem lastSelected;
	private JMenu customSortMenu;
	private JMenu clusterMetadataMenu;
	private int[] newOrder;
	private MetaOmChartPanel myChartPanel;
	private String xaxisLabel;
	private String yaxisLabel;
	private JSlider nameLengthSlider;
	private JRadioButtonMenuItem[] customSorts;
	private ButtonGroup sortGroup;
	private JToggleButton toggleShapesButton;
	private JPopupMenu appearanceMenu;
	private DefaultColorScheme defaultColorScheme;
	// urmi
	private JButton changeAxislabelBtn = new JButton("Change X-Axis labels");
	Color[] colorArray = null;
	private JButton changePalette;

	public ChartToolBar(MetaOmChartPanel aChartPanel) {
		chartProps = aChartPanel.getChartProperties();
		myChartPanel = aChartPanel;
		JFreeChart myChart = myChartPanel.getChart();
		xaxisLabel = myChart.getXYPlot().getDomainAxis().getLabel();
		yaxisLabel = myChart.getXYPlot().getRangeAxis().getLabel();
		// nameLengthSlider = new JSlider(0,
		// myChartPanel.getProject().getMaxNameLength(), 0);
		// urmi change slider length
		nameLengthSlider = new JSlider(0, myChartPanel.getProject().getMaxNameLength(),
				myChartPanel.getProject().getMaxNameLength());
		nameLengthSlider.setMajorTickSpacing(10);
		nameLengthSlider.setMinorTickSpacing(2);
		nameLengthSlider.setPaintTicks(true);
		// nameLengthSlider.setSnapToTicks(true);

		nameLengthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				XYPlot plot = myChartPanel.getChart().getXYPlot();
				plot.axisChanged(new AxisChangeEvent(plot.getDomainAxis()));

			}

		});
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

		toggleShapesButton = new JToggleButton(theme.getShapes());
		toggleShapesButton.setActionCommand("shapes");
		toggleShapesButton.addActionListener(this);
		toggleShapesButton.setToolTipText("Toggle shapes");
		toggleLegend = new JToggleButton(theme.getLegend(), myChartPanel.isLegendVisible());
		toggleLegend.setToolTipText("Show/hide legend");
		// urmi
		removeRangeMarkers = new JButton(theme.getPopups());
		removeRangeMarkers.setToolTipText("Clear markers");
		removeRangeMarkers.setActionCommand("clearrange");
		removeRangeMarkers.addActionListener(this);
		// not used
		togglePopup = new JToggleButton(theme.getPopups(), myChartPanel.isPopupEnabled());
		togglePopup.setToolTipText("Show/hide Metadata Popups");
		metadataButton = new JButton("Show Metadata", theme.getMetadata());
		metadataButton.setToolTipText("Show Metadata");
		sortMenuButton = new MenuButton(theme.getSort(), null);
		sortMenuButton.setToolTipText("Sort data");
		exportButton = new JButton(theme.getExcel());
		exportButton.setToolTipText("Export to Excel");
		properties.setActionCommand("PROPERTIES");
		properties.addActionListener(myChartPanel.getChartPanel());
		save.setActionCommand("SAVE");
		save.addActionListener(myChartPanel.getChartPanel());
		print.setActionCommand("PRINT");
		print.addActionListener(myChartPanel.getChartPanel());
		zoomIn.setActionCommand("zoomIn");
		zoomIn.addActionListener(this);
		zoomOut.setActionCommand("zoomOut");
		zoomOut.addActionListener(this);
		defaultZoom.setActionCommand("defaultZoom");
		defaultZoom.addActionListener(this);

		toggleLegend.setActionCommand("legend");
		toggleLegend.addActionListener(this);
		togglePopup.setActionCommand("popup");
		togglePopup.addActionListener(this);
		metadataButton.setActionCommand("get metadata");
		metadataButton.addActionListener(this);
		metadataButton.setEnabled(false);
		exportButton.setActionCommand("export");
		exportButton.addActionListener(this);
		JPopupMenu sortMenu = new JPopupMenu();
		defaultSortItem = new JRadioButtonMenuItem("Default");
		// nameSortItem = new JRadioButtonMenuItem("By " + xaxisLabel);
		// urmi
		nameSortItem = new JRadioButtonMenuItem("By Data Column");
		xaxisSortItem = new JRadioButtonMenuItem("By X-axis labels");
		yvalueSortItem = new JRadioButtonMenuItem("By " + yaxisLabel);
		extInfoSortItem = new JRadioButtonMenuItem("Group by Query");
		// metadataClusterSortItem = new JRadioButtonMenuItem("More...");
		// changed to; urmi
		metadataClusterSortItem = new JRadioButtonMenuItem();
		poSortItem = new JRadioButtonMenuItem("By POs");
		newCustomSortItem = new JRadioButtonMenuItem("New custom sort...");
		sortGroup = new ButtonGroup();
		sortGroup.add(defaultSortItem);
		sortGroup.add(nameSortItem);
		sortGroup.add(xaxisSortItem);
		sortGroup.add(yvalueSortItem);
		sortGroup.add(extInfoSortItem);
		sortGroup.add(metadataClusterSortItem);
		sortGroup.add(poSortItem);
		sortGroup.add(newCustomSortItem);
		if (myChartPanel.getProject().getMetadataHybrid() == null) {
			extInfoSortItem.setEnabled(false);
			poSortItem.setEnabled(false);
			togglePopup.setEnabled(false);
		}
		poSortItem.setEnabled(true);
		defaultSortItem.setSelected(true);
		lastSelected = defaultSortItem;
		defaultSortItem.setActionCommand("defaultSort");
		defaultSortItem.addActionListener(this);
		nameSortItem.setActionCommand("nameSort");
		nameSortItem.addActionListener(this);
		xaxisSortItem.setActionCommand(SORT_XAXIS_COMMAND);
		xaxisSortItem.addActionListener(this);
		yvalueSortItem.setActionCommand("yvalueSort");
		yvalueSortItem.addActionListener(this);
		extInfoSortItem.setActionCommand("extInfoSort");
		extInfoSortItem.addActionListener(this);
		metadataClusterSortItem.setActionCommand("clusterMetadataSort");
		metadataClusterSortItem.addActionListener(this);
		poSortItem.setActionCommand("poSort");
		poSortItem.addActionListener(this);
		newCustomSortItem.setActionCommand("newCustomSort");
		newCustomSortItem.addActionListener(this);
		customSortMenu = new JMenu("Custom");
		customSortMenu.add(newCustomSortItem);
		refreshCustomSortMenu();
		clusterMetadataMenu = new JMenu("Group by Metadata");
		refreshClusterMetadataMenu();
		sortMenu.add(defaultSortItem);
		sortMenu.add(nameSortItem);
		sortMenu.add(xaxisSortItem);
		sortMenu.add(yvalueSortItem);

		/**
		 * urmi disable cluster in rep plot
		 */
		if (!myChartPanel.repFlag) {
			// JOptionPane.showMessageDialog(null, "OFF");
			sortMenu.add(clusterMetadataMenu);
			sortMenu.add(extInfoSortItem);
			sortMenu.add(customSortMenu);
		}
		// sortMenu.add(clusterMetadataMenu);

		hideColumnsItem = new JMenuItem("Hide/Show Columns...");
		hideColumnsItem.setActionCommand("hideColumns");
		hideColumnsItem.addActionListener(this);
		sortMenu.addSeparator();
		// sortMenu.add(hideColumnsItem);
		sortMenuButton.setMenu(sortMenu);
		sortMenuButton.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				int sortCount;

				if (myChartPanel.getProject().getSavedSorts() == null) {
					sortCount = 0;
				} else
					sortCount = myChartPanel.getProject().getSavedSorts().size();
				int customSortItems;

				if (customSorts == null) {
					customSortItems = 0;
				} else {
					customSortItems = customSorts.length;
				}
				if (customSortItems != sortCount) {
					refreshCustomSortMenu();
				}

			}
		});
		add(properties);
		add(save);
		add(print);
		addSeparator();
		add(zoomIn);
		add(zoomOut);
		add(defaultZoom);
		addSeparator();

		add(toggleShapesButton);
		add(toggleLegend);
		add(removeRangeMarkers);

		addSeparator();
		add(metadataButton);
		add(sortMenuButton);
		add(exportButton);
		add(new JToolBar.Separator());
		add(new JLabel("Adjust Name Length(X-axis): "));
		add(nameLengthSlider);
		setFloatable(false);
		add(new JToolBar.Separator());

		// urmi
		changeAxislabelBtn.setActionCommand("changelabels");
		changeAxislabelBtn.addActionListener(this);
		changeAxislabelBtn.setContentAreaFilled(true);
		changeAxislabelBtn.setBorder(BorderFactory.createEtchedBorder(0));
		add(changeAxislabelBtn);
		add(new JToolBar.Separator());
		changePalette = new JButton(theme.getPalette());
		changePalette.setToolTipText("Color Palette");
		changePalette.setActionCommand("changePalette");
		changePalette.addActionListener(this);
		changePalette.setOpaque(false);
		changePalette.setContentAreaFilled(false);
		changePalette.setBorderPainted(true);
		add(changePalette);
	}

	public void setExtendedInfoEnabled(boolean enabled) {
		extInfoSortItem.setEnabled(enabled);

		poSortItem.setEnabled(enabled);
		togglePopup.setEnabled(enabled);
	}

	public void setMetadataEnabled(boolean enabled) {
		metadataButton.setEnabled(enabled);
	}

	private Point2D getCenterPoint() {
		JFreeChart myChart = myChartPanel.getChart();
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

	public void actionPerformed(ActionEvent e) {

		if ("changelabels".equals(e.getActionCommand())) {

			// pass the ticklabels in the string array myChartPanel.tickLabels and the
			// col_name in myChartPanel.xaxisColumn
			// then use this data to change tick labels in the format function (StringBuffer
			// format(double arg0, StringBuffer arg1, FieldPosition arg2))
			// in the MetaOmChartPanel class

			// int totalFields = MetaOmGraph.getActiveProject().getMetadata().fields.size();
			// if null nothing to sort by
			if (MetaOmGraph.getActiveProject().getMetadataHybrid() == null) {
				JOptionPane.showMessageDialog(this, "No metadata found.");
				return;
			}
			String[] fields = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataHeaders();
			// JOptionPane.showMessageDialog(null, "8888flds:"+Arrays.toString(fields));
			String dataColName = MetaOmGraph.getActiveProject().getMetadataHybrid().getDataColName();

			String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, fields, fields[0]);
			if (col_val == null) {
				return;
			}

			new AnimatedSwingWorker("Working...", true) {

				@Override
				public Object construct() {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								// String[] curr_samp = myChartPanel.getSampleNames();
								// myChartPanel.sampnamesCopy=curr_samp;
								String[] curr_samp = myChartPanel.getSampleNames();
								String[] tempLabels = new String[curr_samp.length];
								System.arraycopy(curr_samp, 0, tempLabels, 0, curr_samp.length);
								// search in metadata if sample is present get coressponding value
								boolean missing_Flag = false;
								// adjust max length for slider
								int max = 0;
								// JOptionPane.showMessageDialog(null, "findig match");
								// long tStart = System.currentTimeMillis();

								// create a hashmap
								// use false to get names for excluded cols too in case they are added back
								// later. Otherwise results will show NA when excluded cols added back to chart
								HashMap<String, String> dcMap = MetaOmGraph.getActiveProject().getMetadataHybrid()
										.getDataColMap(col_val, false);

								for (int j = 0; j < tempLabels.length; j++) {

									// get the row containing datavalue or run and change templabels to
									// coreesponding values under col_val

									// faster
									// tempLabels[j] =
									// MetaOmGraph.getActiveProject().getMetadataHybrid().getColValueMatchingRow(tempLabels[j],
									// col_val);
									// tempLabels[j]=MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataCollection().getDatabyDataColumn(tempLabels[j],
									// col_val);
									// even faster
									tempLabels[j] = dcMap.get(tempLabels[j]);
									if (tempLabels[j] == null) {
										tempLabels[j] = "NA";
									}
									// keep record of max length to set slider value
									if (max < tempLabels[j].length()) {
										max = tempLabels[j].length();
									}

								}

								/*
								 * long tEnd = System.currentTimeMillis(); long tDelta = tEnd - tStart; double
								 * elapsedSeconds = tDelta / 1000.0; JOptionPane.showMessageDialog(null,
								 * "findig match Done:"+elapsedSeconds);
								 */

								// show message about missing runids
								if (missing_Flag) {
									JOptionPane.showMessageDialog(null,
											"Found missing or ambiguous values. These will not be changed on the X-axis.");
								}

								// JOptionPane.showMessageDialog(null, "findig match Done..");

								// copy values of templabels
								// set max val of slider, uperrlimit to 400
								max = ((max > 400) ? 400 : max);
								nameLengthSlider.setMaximum(max);
								myChartPanel.tickLabels = new String[tempLabels.length];
								System.arraycopy(tempLabels, 0, myChartPanel.tickLabels, 0, tempLabels.length);
								// set x-axis label
								myChartPanel.xaxisColumn = col_val;
								myChartPanel.getChartPanel().restoreAutoBounds();
								myChartPanel.getInfoPanel().reselect();
								myChartPanel.initializeDataset();
								myChartPanel.repaint();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					return null;
				}

			}.start();

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
				int numColors = myChartPanel.getChart().getXYPlot().getSeriesCount();
				numColors = Math.min(numColors, 30);
				// get color array
				colorArray = cb.getColorPalette(numColors);
				myChartPanel.setPalette(Utils.filterColors(colorArray));
			} else {
				// reset was pressed and the OK. show default colors
				colorArray = null;
				// myChartPanel.updateChart();

			}

			return;
		}
		if (ZOOM_IN_COMMAND.equals(e.getActionCommand())) {
			Point2D center = getCenterPoint();
			myChartPanel.getChartPanel().zoomInBoth(center.getX(), center.getY());
			return;
		}
		if (ZOOM_OUT_COMMAND.equals(e.getActionCommand())) {
			Point2D center = getCenterPoint();
			myChartPanel.getChartPanel().zoomOutBoth(center.getX(), center.getY());
			return;
		}
		if (ZOOM_DEFAULT_COMMAND.equals(e.getActionCommand())) {
			myChartPanel.getChartPanel().restoreAutoBounds();
			return;
		}

		if (SORT_DEFAULT_COMMAND.equals(e.getActionCommand())) {
			int[] oldsortOrder = myChartPanel.getSortOrder();
			// JOptionPane.showMessageDialog(null, "oldSO:" +
			// Arrays.toString(oldsortOrder));
			myChartPanel.setSortOrder(myChartPanel.getDataSorter().defaultOrder());
			myChartPanel.getInfoPanel().reselect();
			myChartPanel.initializeDataset();
			// update(myChartPanel.getGraphics());
			repaint();
			lastSelected = defaultSortItem;
			// update range markers
			Vector<RangeMarker> rangeMarkers = myChartPanel.getDataSorter().getRangeMarkers();
			if (rangeMarkers == null) {
				return;
			}
			myChartPanel.getDataSorter()
					.setRangeMarkers(updateRangeMarkers(oldsortOrder, myChartPanel.getSortOrder(), rangeMarkers));
			return;
		}
		if (SORT_NAME_COMMAND.equals(e.getActionCommand())) {
			int[] oldsortOrder = myChartPanel.getSortOrder();
			myChartPanel.setSortOrder(myChartPanel.getDataSorter().sortByColumnName());
			// myChartPanel.setSortOrder(myChartPanel.getDataSorter().sortByXaxisNames());
			myChartPanel.getInfoPanel().reselect();
			myChartPanel.initializeDataset();

			repaint();
			lastSelected = nameSortItem;
			// update range markers
			Vector<RangeMarker> rangeMarkers = myChartPanel.getDataSorter().getRangeMarkers();
			if (rangeMarkers == null) {
				return;
			}
			myChartPanel.getDataSorter()
					.setRangeMarkers(updateRangeMarkers(oldsortOrder, myChartPanel.getSortOrder(), rangeMarkers));
			return;
		}
		if (SORT_XAXIS_COMMAND.equals(e.getActionCommand())) {
			int[] oldsortOrder = myChartPanel.getSortOrder();
			myChartPanel.setSortOrder(myChartPanel.getDataSorter().sortByXaxisNames());
			myChartPanel.getInfoPanel().reselect();
			myChartPanel.initializeDataset();
			repaint();
			lastSelected = xaxisSortItem;
			// update range markers
			Vector<RangeMarker> rangeMarkers = myChartPanel.getDataSorter().getRangeMarkers();
			if (rangeMarkers == null) {
				return;
			}
			myChartPanel.getDataSorter()
					.setRangeMarkers(updateRangeMarkers(oldsortOrder, myChartPanel.getSortOrder(), rangeMarkers));
			return;
		}
		if (SORT_YVAL_COMMAND.equals(e.getActionCommand())) {
			int sortMe = 0;
			if (myChartPanel.getChart().getXYPlot().getDataset().getSeriesCount() > 1) {
				String[] seriesNames = new String[myChartPanel.getChart().getXYPlot().getDataset().getSeriesCount()];
				for (int x = 0; x < seriesNames.length; x++) {
					String thisName = myChartPanel.getChart().getXYPlot().getDataset().getSeriesKey(x).toString();
					int hits = 0;
					for (int y = 0; y < x; y++) {
						if (seriesNames[x] == thisName)
							hits++;
					}
					if (hits > 0)
						thisName = thisName + "(" + hits + ")";
					seriesNames[x] = thisName;
				}
				String result = (String) JOptionPane.showInternalInputDialog(myChartPanel, "Sort by which series?",
						"Y-Value Sort", JOptionPane.QUESTION_MESSAGE, null, seriesNames, seriesNames[0]);
				if (result == null) {
					lastSelected.setSelected(true);
					return;
				}
				for (int x = 0; x < seriesNames.length; x++)
					if (seriesNames[x] == result)
						sortMe = x;
			}
			try {
				double[] yvalues = myChartPanel.getData(sortMe);
				int[] oldsortOrder = myChartPanel.getSortOrder();
				myChartPanel.setSortOrder(myChartPanel.getDataSorter().sortByYValue(yvalues));
				myChartPanel.getInfoPanel().reselect();
				myChartPanel.initializeDataset();
				lastSelected = yvalueSortItem;
				// update(myChartPanel.getGraphics());
				repaint();
				// update range markers
				Vector<RangeMarker> rangeMarkers = myChartPanel.getDataSorter().getRangeMarkers();
				if (rangeMarkers == null) {
					return;
				}
				myChartPanel.getDataSorter()
						.setRangeMarkers(updateRangeMarkers(oldsortOrder, myChartPanel.getSortOrder(), rangeMarkers));

			} catch (Exception oops) {
				oops.printStackTrace();
			}
			return;
		}
		if (SORT_EXTINFO_COMMAND.equals(e.getActionCommand())) {
			newOrder = myChartPanel.getDataSorter().sortByMetadata();
			if (newOrder == null || newOrder[0] == -1) {
				JOptionPane.showMessageDialog(null, "No hits", "No hits", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			new AnimatedSwingWorker("Sorting...", true) {
				public Object construct() {
					if (newOrder == null) {
						lastSelected.setSelected(true);
						System.out.println("Cancelled searching");
						return null;
					}
					myChartPanel.setSortOrder(newOrder);
					myChartPanel.getInfoPanel().reselect();
					myChartPanel.initializeDataset();
					lastSelected = extInfoSortItem;
					repaint();
					// update(myChartPanel.getGraphics());
					System.out.println("Done sorting");
					return null;
				}
			}.start();
			return;
		}
		if ((e.getActionCommand() != null) && (e.getActionCommand().startsWith("clusterMetadataSort"))) {

			new AnimatedSwingWorker("Working...", false) {
				public Object construct() {
					String[] splitCommand = e.getActionCommand().split("::", 2);

					if (splitCommand.length == 2) {
						java.util.List<String> selectedVals = new ArrayList<>();

						if (splitCommand[1].equals("More...")) {

							String[] metadataHeaders = myChartPanel.getProject().getMetadataHybrid()
									.getMetadataHeaders();
							// display jpanel with check box
							JCheckBox[] cBoxes = new JCheckBox[metadataHeaders.length];
							JPanel cbPanel = new JPanel();
							cbPanel.setLayout(new GridLayout(0,3));
							for (int i = 0; i < metadataHeaders.length; i++) {
								cBoxes[i] = new JCheckBox(metadataHeaders[i]);
								cbPanel.add(cBoxes[i]);
							}

							int res = JOptionPane.showConfirmDialog(null, cbPanel, "Select categories",
									JOptionPane.OK_CANCEL_OPTION);
							if (res == JOptionPane.OK_OPTION) {

								for (int i = 0; i < metadataHeaders.length; i++) {
									if (cBoxes[i].isSelected()) {
										selectedVals.add(metadataHeaders[i]);
									}
								}
								newOrder = myChartPanel.getDataSorter().clusterByMetadata(selectedVals);
							} else {

								return null;
							}

						} else {
							selectedVals.add(splitCommand[1]);
							newOrder = myChartPanel.getDataSorter().clusterByMetadata(selectedVals);
						}
						if (newOrder.length == 0 || newOrder == null) {
							return null;
						}
					}
					if (newOrder == null) {
						lastSelected.setSelected(true);
						System.out.println("Cancelled searching");
						return null;
					}
					myChartPanel.setSortOrder(newOrder);
					myChartPanel.getInfoPanel().reselect();
					myChartPanel.initializeDataset();
					lastSelected = extInfoSortItem;

					repaint();

					return null;
				}
			}.start();

			return;
		}
		if (SORT_PO_COMMAND.equals(e.getActionCommand())) {
			newOrder = null;
			if (newOrder == null) {
				lastSelected.setSelected(true);
				return;
			}
			myChartPanel.setSortOrder(newOrder);
			myChartPanel.getInfoPanel().reselect();
			myChartPanel.initializeDataset();
			repaint();
			lastSelected = poSortItem;
			return;
		}
		if (QUICK_SORT_PO_COMMAND.equals(e.getActionCommand())) {
			newOrder = null;
			if (newOrder == null) {
				lastSelected.setSelected(true);
				return;
			}
			myChartPanel.setSortOrder(newOrder);
			myChartPanel.getInfoPanel().reselect();
			myChartPanel.initializeDataset();
			// update(myChartPanel.getGraphics());
			repaint();
			lastSelected = poSortItem;
			return;
		}
		if (SORT_CUSTOM_NEW_COMMAND.equals(e.getActionCommand())) {
			newOrder = myChartPanel.getDataSorter().sortCustom();
			if (newOrder == null) {
				lastSelected.setSelected(true);
				return;
			}
			myChartPanel.setSortOrder(newOrder);
			myChartPanel.getInfoPanel().reselect();
			myChartPanel.initializeDataset();
			lastSelected = newCustomSortItem;
			// update(myChartPanel.getGraphics());
			repaint();
			return;
		}
		if (SORT_CUSTOM_LOAD_COMMAND.equals(e.getActionCommand())) {
			NewCustomSortDialog.CustomSortObject sortObject = myChartPanel.getProject().getSavedSorts()
					.get(((Component) e.getSource()).getName());
			if ((sortObject == null) || (sortObject.getSortOrder() == null)) {
				lastSelected.setSelected(true);
				return;
			}
			newOrder = sortObject.getSortOrder();
			myChartPanel.getDataSorter().setRangeMarkers(sortObject.getRangeMarkers());
			myChartPanel.setSortOrder(newOrder);
			myChartPanel.getInfoPanel().reselect();
			myChartPanel.initializeDataset();
			lastSelected = ((JRadioButtonMenuItem) e.getSource());
			// update(myChartPanel.getGraphics());
			repaint();
			return;
		}
		if (ChartPanel.SAVE_COMMAND.equals(e.getActionCommand())) {
			try {
				ComponentToImage.saveAsPNG(myChartPanel, new File("z:\\newchart.jpg"), 2000, 2000);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		if (EXPORT_COMMAND.equals(e.getActionCommand())) {
			ChartDataExporter cde = new ChartDataExporter(myChartPanel);
			MetaOmGraph.getDesktop().add(cde);
			cde.setVisible(true);
			return;
		}
		if (TOGGLE_LEGEND_COMMAND.equals(e.getActionCommand())) {
			myChartPanel.setLegendVisible(toggleLegend.isSelected());
			return;
		}
		if (TOGGLE_POPUP_COMMAND.equals(e.getActionCommand())) {
			myChartPanel.setPopupEnabled(togglePopup.isSelected());
			return;
		}
		if (TOGGLE_SHAPES_COMMAND.equals(e.getActionCommand())) {
			chartProps.setShapePainted(!chartProps.isShapePainted());
			myChartPanel.initializeDataset();
			return;
		}

		if ("clearrange".equals(e.getActionCommand())) {
			myChartPanel.getDataSorter().setRangeMarkers(null);
			// myChartPanel.initializeDataset();
			return;
		}
		if (TOGGLE_LINES_COMMAND.equals(e.getActionCommand())) {
			chartProps.setLinePainted(showLinesItem.isSelected());
			myChartPanel.initializeDataset();
			return;
		}
		if (COLOR_SCHEME_DEFAULT_COMMAND.equals(e.getActionCommand())) {
			chartProps.setColorScheme(defaultColorScheme);
			myChartPanel.initializeDataset();
			return;
		}
		if (COLOR_SCHEME_GRAYSCALE_COMMAND.equals(e.getActionCommand())) {
			chartProps.setColorScheme(new GrayColorScheme());
			myChartPanel.initializeDataset();
			return;
		}
		if (METADATA_COMMAND.equals(e.getActionCommand())) {
			MetaOmGraph.tableToFront();
			/*
			 * MetaOmGraph.getActiveTable().selectNode(
			 * myChartPanel.sortOrder[myChartPanel.plottedColumns[((int)
			 * myChartPanel.getSelectedPoint().getX())]], false);
			 */
			// changed to //urmi
			if (!myChartPanel.repFlag) {
				MetaOmGraph.getActiveTable()
						.selectNode(myChartPanel.sortOrder[myChartPanel.plottedColumns[((int) myChartPanel
								.getSelectedPoint().getX())]], false);
			} else {
				// get a sample under current selected group
				String thisSname = myChartPanel
						.getSampName(myChartPanel.sortOrder[myChartPanel.plottedColumns[((int) myChartPanel
								.getSelectedPoint().getX())]]);
				int thisInd = MetaOmGraph.getActiveProject().getMetadataHybrid().getColIndexbyName(thisSname);
				MetaOmGraph.getActiveTable().selectNode(thisInd, true);
			}

			return;
		}
		if (HIDE_COLUMNS_COMMAND.equals(e.getActionCommand())) {
			myChartPanel.manageColumns();
			return;
		}
	}

	public int getNameLength() {
		return nameLengthSlider.getValue();
	}

	public void refreshCustomSortMenu() {
		if ((customSorts != null) && (customSorts.length > 0)) {
			for (int x = 0; x < customSorts.length; x++) {
				sortGroup.remove(customSorts[x]);
				customSortMenu.remove(customSorts[x]);
			}
		}
		if ((myChartPanel.getProject().getSavedSorts() != null)
				&& (myChartPanel.getProject().getSavedSorts().size() > 0)) {
			Hashtable<String, NewCustomSortDialog.CustomSortObject> savedSorts = myChartPanel.getProject()
					.getSavedSorts();
			Object[] keys = new Object[savedSorts.size()];
			Enumeration<String> keyEnum = savedSorts.keys();
			int index = 0;
			while (keyEnum.hasMoreElements()) {
				keys[(index++)] = keyEnum.nextElement();
			}
			Arrays.sort(keys);
			customSorts = new JRadioButtonMenuItem[keys.length];
			for (int x = 0; x < keys.length; x++) {
				customSorts[x] = new JRadioButtonMenuItem(keys[x] + "");
				customSorts[x].setActionCommand("loadCustomSort");
				customSorts[x].setName(keys[x] + "");
				customSorts[x].addActionListener(this);
				sortGroup.add(customSorts[x]);
				customSortMenu.add(customSorts[x]);
			}
		} else {
			customSorts = null;
		}
	}

	public void refreshClusterMetadataMenu() {
		/**
		 * @author urmi get metadata from new class and use its functions
		 */
		if (myChartPanel.getProject().getMetadataHybrid() == null) {
			if (clusterMetadataMenu != null) {
				clusterMetadataMenu.setEnabled(false);
			}
			return;

		}
		final String[] fields = myChartPanel.getProject().getMetadataHybrid().getMetadataHeaders();
		final String[] fields2 = new String[fields.length + 1];
		int k = 0;
		for (String f : fields) {
			fields2[k++] = f;
		}
		fields2[fields2.length - 1] = "More...";

		Component[] components = clusterMetadataMenu.getMenuComponents();
		for (Component c : components) {
			if (!(c instanceof JMenuItem))
				continue;
			JMenuItem item = (JMenuItem) c;

			if (item.equals(metadataClusterSortItem))
				continue;
			item.removeActionListener(this);
		}
		clusterMetadataMenu.removeAll();
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < fields2.length; i++) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(fields2[i]);
			item.setActionCommand(SORT_CLUSTER_METADATA_COMMAND + "::" + item.getText());
			item.addActionListener(this);
			group.add(item);
			clusterMetadataMenu.add(item);
		}
		// removed; urmi
		// clusterMetadataMenu.addSeparator();
		// clusterMetadataMenu.add(metadataClusterSortItem); removed urmi
		clusterMetadataMenu.setEnabled(true);
	}

	private void initAppearanceMenu() {
		showLinesItem = new JCheckBoxMenuItem("Show Lines");
		showLinesItem.setActionCommand(TOGGLE_LINES_COMMAND);
		showLinesItem.addActionListener(this);
		showLinesItem.setSelected(true);
		showShapesItem = new JCheckBoxMenuItem("Show Shapes");
		showShapesItem.setActionCommand(TOGGLE_SHAPES_COMMAND);
		showShapesItem.addActionListener(this);
		defaultColorSchemeItem = new JRadioButtonMenuItem("Default");
		defaultColorSchemeItem.setActionCommand(COLOR_SCHEME_DEFAULT_COMMAND);
		defaultColorSchemeItem.addActionListener(this);
		grayColorSchemeItem = new JRadioButtonMenuItem("Grayscale");
		grayColorSchemeItem.setActionCommand(COLOR_SCHEME_GRAYSCALE_COMMAND);
		grayColorSchemeItem.addActionListener(this);
		customColorSchemeItem = new JRadioButtonMenuItem("Custom");
		ButtonGroup colorSchemeGroup = new ButtonGroup();
		colorSchemeGroup.add(defaultColorSchemeItem);
		colorSchemeGroup.add(grayColorSchemeItem);
		colorSchemeGroup.add(customColorSchemeItem);
		defaultColorSchemeItem.setSelected(true);
		colorSchemeMenu = new JMenu("Color Scheme");
		appearanceMenu.add(showLinesItem);
		appearanceMenu.add(showShapesItem);
		colorSchemeMenu.add(defaultColorSchemeItem);
		colorSchemeMenu.add(grayColorSchemeItem);
		colorSchemeMenu.add(customColorSchemeItem);
		appearanceMenu.add(colorSchemeMenu);
		Paint[] defaultColors = new Paint[myChartPanel.getChart().getXYPlot().getSeriesCount()];
		for (int i = 0; i < defaultColors.length; i++) {
			defaultColors[i] = myChartPanel.getRenderer().getSeriesPaint(i);
		}
		defaultColorScheme = new DefaultColorScheme(defaultColors);
	}

	/**
	 * @author urmi Change range markers after data is sorted
	 * @param oldSortOrder
	 * @param newSortOrder
	 * @param oldRangeMarkers
	 * @return
	 */
	private Vector<RangeMarker> updateRangeMarkers(int[] oldSortOrder, int[] newSortOrder,
			Vector<RangeMarker> oldRangeMarkers) {
		if (Arrays.equals(oldSortOrder, newSortOrder)) {
			return oldRangeMarkers;
		}
		Vector<RangeMarker> temp = new Vector<>();

		// remove excluded cols
		boolean[] exclude = MetaOmAnalyzer.getExclude();
		if (exclude != null) {
			java.util.List<Integer> newSO = new ArrayList<>();
			java.util.List<Integer> oldSO = new ArrayList<>();
			for (int i = 0; i < oldSortOrder.length; i++) {
				if (!exclude[oldSortOrder[i]]) {
					oldSO.add(oldSortOrder[i]);
				}
				if (!exclude[newSortOrder[i]]) {
					newSO.add(newSortOrder[i]);
				}
			}
			oldSortOrder = oldSO.stream().mapToInt(i -> i).toArray();
			newSortOrder = newSO.stream().mapToInt(i -> i).toArray();
		}

		for (RangeMarker r : oldRangeMarkers) {
			int thisStrt = r.getStart();
			int thisEnd = r.getEnd();
			java.util.List<Integer> colsinRange = new ArrayList<>();
			java.util.List<Integer> colsinRangeNewInd = new ArrayList<>();
			for (int i = thisStrt; i <= thisEnd; i++) {
				colsinRange.add(oldSortOrder[i]);
			}
			// find indices of colsinRange in new sort order
			for (int c : colsinRange) {
				for (int i = 0; i < newSortOrder.length; i++) {
					if (newSortOrder[i] == c) {
						colsinRangeNewInd.add(i);
						break;
					}
				}

			}
			// sort new indices and break into continuous ranges
			Collections.sort(colsinRangeNewInd);
			// JOptionPane.showMessageDialog(null, "cols in thisR: " +
			// colsinRange.toString());
			// JOptionPane.showMessageDialog(null, "ind sorted: " +
			// colsinRangeNewInd.toString());
			int currStrt = colsinRangeNewInd.get(0);
			for (int j = 1; j < colsinRangeNewInd.size(); j++) {
				// if values are adjacent
				if (colsinRangeNewInd.get(j) == colsinRangeNewInd.get(j - 1) + 1) {

				} else {

					RangeMarker newRM = new RangeMarker(currStrt, colsinRangeNewInd.get(j - 1), r.getLabel(),
							r.getStyle());
					currStrt = colsinRangeNewInd.get(j);
					temp.add(newRM);
					// rangeSplit = true;
					// JOptionPane.showMessageDialog(null, "creatingRM: " + currStrt + "-" +
					// colsinRangeNewInd.get(j - 1));

				}

			}
			RangeMarker newRM = new RangeMarker(currStrt, colsinRangeNewInd.get(colsinRangeNewInd.size() - 1),
					r.getLabel(), r.getStyle());
			temp.add(newRM);
			// JOptionPane.showMessageDialog(null, "Last creatingRM: " + currStrt + "-" +
			// colsinRangeNewInd.get(colsinRangeNewInd.size() - 1));

		}

		/*
		 * for (RangeMarker r : temp) { JOptionPane.showMessageDialog(null, "thisR: " +
		 * r.getStart() + "-" + r.getEnd()+":"+r.getLabel()); }
		 */

		return mergeRangeMarkers(temp);
	}

	private Vector<RangeMarker> mergeRangeMarkers(Vector<RangeMarker> old) {

		// JOptionPane.showMessageDialog(null, "merging");
		Vector<RangeMarker> temp = new Vector<>();
		// sort the range markers
		Collections.sort(old);
		/*
		 * for (RangeMarker r : old) { JOptionPane.showMessageDialog(null, "thisR: " +
		 * r.getStart() + "-" + r.getEnd()+":"+r.getLabel()); }
		 */
		String currLabel = old.get(0).getLabel();
		int currStrt = old.get(0).getStart();
		int currEnd = old.get(0).getEnd();

		for (int i = 1; i < old.size(); i++) {

			int thisStrt = old.get(i).getStart();
			// int thisEnd = old.get(i).getEnd();
			String thisVal = old.get(i).getLabel();

			// int prevStrt = old.get(i - 1).getStart();
			int prevEnd = old.get(i - 1).getEnd();
			String prevVal = old.get(i - 1).getLabel();
			int prevStyle = old.get(i - 1).getStyle();

			if (thisStrt == prevEnd + 1 && thisVal == prevVal) {

			} else {

				RangeMarker newRM = new RangeMarker(currStrt, prevEnd, currLabel, prevStyle);
				// JOptionPane.showMessageDialog(null, "creatingRM: " + currStrt + "-" +
				// prevEnd);
				temp.add(newRM);
				currStrt = thisStrt;
				currLabel = thisVal;
			}
		}
		RangeMarker newRM = new RangeMarker(currStrt, old.get(old.size() - 1).getEnd(), currLabel,
				old.get(old.size() - 1).getStyle());
		// JOptionPane.showMessageDialog(null, "creatingRM: " + currStrt + "-" +
		// old.get(old.size() - 1).getEnd());
		temp.add(newRM);

		return temp;

	}
}
