/**
 * 
 */
package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;

/**
 * @author sumanth
 *
 */
public abstract class MogChartParentFrame extends JInternalFrame implements ChartMouseListener, ActionListener{
	
	private boolean legendFlag = true;
	private double pointSize = 7.0;
	
	private JButton propertiesBtn;
	private JButton saveBtn;
	private JButton printBtn;
	private ChartPanel chartDisplayPanel;
	private JFreeChart chart;
	
	protected JPanel toolBarPanel = new JPanel();
	protected JPanel bottomToolBarPanel = new JPanel();
	
	public MogChartParentFrame(String chartTitle) {
		createChartPanel();
		getContentPane().add(toolBarPanel, BorderLayout.NORTH);
		getContentPane().add(bottomToolBarPanel, BorderLayout.SOUTH);
		
		putClientProperty("JInternalFrame.frameType", "normal");
		MetaOmGraph.getDesktop().add(this);
		this.setDefaultCloseOperation(2);
		this.setClosable(true);
		this.setResizable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
		this.pack();
		this.setSize(1000, 700);
		this.setVisible(true);
		this.toFront();
		
		this.setTitle(chartTitle);
	}
	
	private void createChartPanel() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		toolBarPanel.setLayout(new FlowLayout());
		
		JButton changeXAxisBtn = new JButton("Change X axis");
		changeXAxisBtn.setActionCommand("chooseX");
		changeXAxisBtn.addActionListener(this);
		bottomToolBarPanel.add(changeXAxisBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		chartDisplayPanel = makePlot();
		chart = chartDisplayPanel.getChart();
		scrollPane.setViewportView(chartDisplayPanel);
		
		createTopToolBar();
	}
	
	// override to add buttons to top tool bar.
	public void addButtonsToTopToolBar() {
		
	}
	
	// override to add buttons to bottom tool bar.
	public void addButtonsToBottomToolBar() {
		
	}
	
	// override to customize top tool bar.
	public void createTopToolBar() {
		IconTheme theme = MetaOmGraph.getIconTheme();
		
		propertiesBtn = new JButton(theme.getProperties());
		propertiesBtn.setToolTipText("Chart Properties");
		propertiesBtn.setActionCommand("Chart Properties");
		propertiesBtn.addActionListener(chartDisplayPanel);
		
		saveBtn = new JButton(theme.getSaveAs());
		saveBtn.setToolTipText("Save Chart as Image");
		saveBtn.setActionCommand("Save Chart");
		saveBtn.addActionListener(chartDisplayPanel);
		
		printBtn = new JButton(theme.getPrint());
		printBtn.setToolTipText("Print Chart");
		printBtn.setActionCommand("Print Chart");
		printBtn.addActionListener(chartDisplayPanel);
		
		JButton zoomInBtn = new JButton(theme.getZoomIn());
		zoomInBtn.setToolTipText("Zoom In");
		zoomInBtn.setActionCommand("Zoom In");
		zoomInBtn.addActionListener(this);
		
		JButton zoomOutBtn = new JButton(theme.getZoomOut());
		zoomOutBtn.setToolTipText("Zoom Out");
		zoomOutBtn.setActionCommand("Zoom Out");
		zoomOutBtn.addActionListener(this);
		
		JButton defaultZoomBtn = new JButton(theme.getDefaultZoom());
		defaultZoomBtn.setToolTipText("Default Zoom"); 
		defaultZoomBtn.setActionCommand("Default Zoom");
		defaultZoomBtn.addActionListener(this);
		
		JToggleButton toggleLegendBtn = new JToggleButton(theme.getLegend(), legendFlag);
		toggleLegendBtn.setToolTipText("Show/hide legend");
		toggleLegendBtn.setActionCommand("legend");
		toggleLegendBtn.addActionListener(this);
		
		JButton splitDatasetBtn = new JButton(theme.getSort());
		splitDatasetBtn.setToolTipText("Split by categories");
		splitDatasetBtn.setActionCommand("splitDataset");
		splitDatasetBtn.addActionListener(this);
		
		JButton changePaletteBtn = new JButton(theme.getPalette());
		changePaletteBtn.setToolTipText("Color Palette");
		changePaletteBtn.setActionCommand("changePalette");
		changePaletteBtn.addActionListener(this);
		changePaletteBtn.setOpaque(false);
		changePaletteBtn.setContentAreaFilled(false);
		changePaletteBtn.setBorderPainted(true);
		
		JSpinner spinner = new JSpinner();
		spinner.setToolTipText("Changes plot point size");
		spinner.setModel(new SpinnerNumberModel(pointSize, 1.0, 20.0, 1.0));
		// set uneditable
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
		
		// add change listener
		ChangeListener listener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pointSize = (double) spinner.getValue();
				updateChart(pointSize);
				repaint();
			}
		};

		spinner.addChangeListener(listener);
		
		toolBarPanel.add(propertiesBtn);
		toolBarPanel.add(saveBtn);
		toolBarPanel.add(printBtn);
		toolBarPanel.add(zoomInBtn);
		toolBarPanel.add(zoomOutBtn);
		toolBarPanel.add(defaultZoomBtn);
		toolBarPanel.add(toggleLegendBtn);
		toolBarPanel.add(splitDatasetBtn);
		toolBarPanel.add(changePaletteBtn);
		toolBarPanel.add(spinner);
	}
	
	/**
	 * call this after changing chart values
	 */
	public abstract void updateChart(double pointSize);
	

	
	public abstract ChartPanel makePlot();
	
	
	private Point2D getCenterPoint() {
		JFreeChart myChart = chart;
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
	public void actionPerformed(ActionEvent e) {
		if ("Zoom In".equals(e.getActionCommand())) {
			Point2D center = getCenterPoint();
			chartDisplayPanel.zoomInBoth(center.getX(), center.getY());
			return;
		}
		if ("Zoom Out".equals(e.getActionCommand())) {
			Point2D center = getCenterPoint();
			chartDisplayPanel.zoomOutBoth(center.getX(), center.getY());
			return;
		}
		if ("Default Zoom".equals(e.getActionCommand())) {
			chartDisplayPanel.restoreAutoBounds();
			return;
		}
		
	}

	@Override
	public void chartMouseClicked(ChartMouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chartMouseMoved(ChartMouseEvent event) {
		// TODO Auto-generated method stub
		
	}
}
