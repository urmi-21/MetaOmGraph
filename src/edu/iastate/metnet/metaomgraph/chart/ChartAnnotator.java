package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;

import java.awt.BasicStroke;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class ChartAnnotator implements ChartMouseListener, ChartProgressListener {
	private JTable annotTable;
	private JScrollPane scrollPane;
	private ArrayList<XYTextAnnotation> annotationList;
	private MetaOmChartPanel myChartPanel;
	private Point2D lastSelectedPoint;

	public ChartAnnotator(MetaOmChartPanel myChartPanel) {
		this.myChartPanel = myChartPanel;
	}

	public void markSelected() {
		Point2D selectedPoint = myChartPanel.getSelectedPoint();
		if (selectedPoint != null) {
			XYTextAnnotation annoty = new XYTextAnnotation(
					"(" + myChartPanel.getFormatter().format(selectedPoint.getX()) + "," + selectedPoint.getY() + ")",
					selectedPoint.getX(), selectedPoint.getY());

			myChartPanel.getChart().getXYPlot().addAnnotation(annoty);
			if (annotationList == null)
				annotationList = new ArrayList();
			annotationList.add(annoty);
			myChartPanel.chartChanged(new ChartChangeEvent(annoty));
		}
	}

	public void manageAnnotations() {
		JInternalFrame f = new JInternalFrame("Annotation Manager");
		f.putClientProperty("JInternalFrame.frameType", "normal");
		
		JButton removeButton = new JButton("Remove selected");
		JButton clearButton = new JButton("Clear all");
		initAnnotationTable();
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int result = JOptionPane.showConfirmDialog(null,	"Are you sure you want to remove ALL annotations from this chart?", "Confirm", 0, 2);
				if (result == 0) {
					myChartPanel.getChart().getXYPlot().clearAnnotations();
					annotTable = new JTable(
							new NoneditableTableModel(null, new String[] { "Annotation", "X Pos", "Y Pos" }));
					annotationList = null;
					scrollPane.setViewportView(annotTable);
					myChartPanel.chartChanged(new ChartChangeEvent(myChartPanel.getChart()));
				}

			}
		});
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] removeUs = annotTable.getSelectedRows();
				if ((removeUs.length <= 0) || (annotationList == null))
					return;
				myChartPanel.getChart().getXYPlot().clearAnnotations();
				if (removeUs.length >= annotationList.size()) {
					annotationList = null;
				} else {
					for (int i = 0; i < removeUs.length; i++) {
						annotationList.remove(removeUs[i] - i);
					}
					for (int i = 0; i < annotationList.size(); i++) {
						myChartPanel.getChart().getXYPlot().addAnnotation(annotationList.get(i));
					}
				}
				ChartAnnotator.this.initAnnotationTable();
				scrollPane.setViewportView(annotTable);
				myChartPanel.chartChanged(new ChartChangeEvent(myChartPanel.getChart()));
			}

		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(removeButton);
		buttonPanel.add(clearButton);
		scrollPane = new JScrollPane(annotTable);
		f.getContentPane().add(scrollPane, "Center");
		f.getContentPane().add(buttonPanel, "Last");
		f.setDefaultCloseOperation(2);
		f.setClosable(true);
		f.setResizable(true);
		f.pack();
		//JOptionPane.showMessageDialog(null, "addd f");
		f.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
				MetaOmGraph.getMainWindow().getHeight() / 2);
		MetaOmGraph.getDesktop().add(f);
		f.setVisible(true);
		
		
	}

	private void initAnnotationTable() {
		String[][] annotations;
		// String[][] annotations;
		if (annotationList != null) {
			annotations = new String[annotationList.size()][3];
		} else
			annotations = new String[0][3];
		String[] headers = { "Annotation", "X Pos", "Y Pos" };
		for (int i = 0; i < annotations.length; i++) {
			XYTextAnnotation thisAnnotation = annotationList.get(i);
			annotations[i][0] = thisAnnotation.getText();
			annotations[i][1] = thisAnnotation.getX() + "";
			annotations[i][2] = thisAnnotation.getY() + "";
		}
		annotTable = new JTable(new NoneditableTableModel(annotations, headers));
	}

	public void chartMouseMoved(ChartMouseEvent arg0) {
		if (!myChartPanel.getChartPanel().getScreenDataArea().contains(arg0.getTrigger().getPoint())) {
			if (arg0.getEntity() == null) {
				return;
			}
		}

		if (arg0 != null) {
			arg0.getEntity();
		}
	}

	public void chartMouseClicked(ChartMouseEvent arg0) {
		if ((arg0.getEntity() instanceof LegendItemEntity)) {
			doLegendEntity((LegendItemEntity) arg0.getEntity());
		} else if (myChartPanel.getAnnotationText().equals("")) {
			if ((arg0.getEntity() instanceof XYItemEntity)) {
				doItemEntity((XYItemEntity) arg0.getEntity());
			}
			if (!myChartPanel.getChartPanel().getScreenDataArea().contains(arg0.getTrigger().getPoint())) {
				ChartColorScheme colorScheme = myChartPanel.getChartProperties().getColorScheme();
				if (myChartPanel.getSelectedSeries() >= 0) {
					myChartPanel.getRenderer().setSeriesStroke(myChartPanel.getSelectedSeries(),
							new BasicStroke(myChartPanel.getLineWidth()));
					myChartPanel.getRenderer().setSeriesPaint(myChartPanel.getSelectedSeries(),
							colorScheme.getSeriesPaint(myChartPanel.getSelectedSeries()));
				}
				myChartPanel.setSelectedPoint(null);
				myChartPanel.setSelectedSeries(-1);
				myChartPanel.getChart().getXYPlot().setDomainCrosshairVisible(false);
				myChartPanel.getChart().getXYPlot().setRangeCrosshairVisible(false);
				lastSelectedPoint = null;
				myChartPanel.getInfoPanel().refresh();
			} else {
				myChartPanel.getChart().getXYPlot().setDomainCrosshairVisible(true);
				myChartPanel.getChart().getXYPlot().setRangeCrosshairVisible(true);
			}
		} else {
			Point2D p = myChartPanel
					.screenToChart(myChartPanel.getChartPanel().translateScreenToJava2D(arg0.getTrigger().getPoint()));
			if (p != null) {
				XYTextAnnotation textAnnot = new XYTextAnnotation(myChartPanel.getAnnotationText(), p.getX(), p.getY());
				myChartPanel.getChart().getXYPlot().addAnnotation(textAnnot);
				if (annotationList == null)
					annotationList = new ArrayList();
				annotationList.add(textAnnot);
				myChartPanel.setAnnotationText("");
				myChartPanel.chartChanged(new ChartChangeEvent(textAnnot));
			}
		}
	}

	// to Change urmi
	private void doItemEntity(XYItemEntity myEntity) {
		int series = myEntity.getSeriesIndex();
		double x = myEntity.getDataset().getXValue(series, myEntity.getItem());
		double y = myEntity.getDataset().getYValue(series, myEntity.getItem());

		// this is where series is darkened upon mouse click on data point
		// urmi change here to display metadata
		// JOptionPane.showMessageDialog(null, "darjk");

		myChartPanel.setSelectedPoint(new Point2D.Double(x, y));
		darkenNewSeries(myEntity.getSeriesIndex());
		myChartPanel.setSelectedSeries(myEntity.getSeriesIndex());
		myChartPanel.getInfoPanel().refresh();
		MetaOmGraph.tableToFront();
		if (!myChartPanel.repFlag) {
			MetaOmGraph.getActiveTable().selectNode(myChartPanel.sortOrder[myChartPanel.plottedColumns[((int) x)]],false);
		}else {
			//get a sample under current selected group
			String thisSname=myChartPanel.getSampName(myChartPanel.sortOrder[myChartPanel.plottedColumns[((int) x)]]);
			int thisInd=MetaOmGraph.getActiveProject().getMetadataHybrid().getColIndexbyName(thisSname);
			//second argument select parent of the node
			MetaOmGraph.getActiveTable().selectNode(thisInd,true);
		}
		//myChartPanel.to
		MetaOmGraph.tableToFront();
	}

	private void doLegendEntity(LegendItemEntity myEntity) {
		Comparable seriesKey = myEntity.getSeriesKey();

		int index = myChartPanel.getChart().getXYPlot().getDataset().indexOf(seriesKey);
		darkenNewSeries(index);
		myChartPanel.setSelectedSeries(index);
		myChartPanel.setSelectedPoint(null);
		myChartPanel.getInfoPanel().refresh();
	}

	//highlight series
	private void darkenNewSeries(int darkenMe) {
		ChartColorScheme colorScheme = myChartPanel.getChartProperties().getColorScheme();
		int selectedSeries = myChartPanel.getSelectedSeries();
		XYLineAndShapeRenderer renderer = myChartPanel.getRenderer();
		if (selectedSeries >= 0) {
			renderer.setSeriesStroke(selectedSeries, new BasicStroke(myChartPanel.getLineWidth()));
			if (colorScheme.getSelectionPaint(selectedSeries) != null) {
				renderer.setSeriesPaint(selectedSeries, colorScheme.getSeriesPaint(selectedSeries));
			}
		}
		renderer.setSeriesStroke(darkenMe, new BasicStroke(myChartPanel.getHighlightedLineWidth()));
		if (colorScheme.getSelectionPaint(darkenMe) != null) {
			renderer.setSeriesPaint(darkenMe, colorScheme.getSelectionPaint(darkenMe));
		}
	}

	public void redrawAnnotations() {
		myChartPanel.getChart().getXYPlot().clearAnnotations();
		if (annotationList != null)
			for (int i = 0; i < annotationList.size(); i++)
				myChartPanel.getChart().getXYPlot().addAnnotation(annotationList.get(i));
	}

	public void chartProgress(ChartProgressEvent event) {
		if (event.getType() != 2) {
			return;
		}
		if (!myChartPanel.getChart().getXYPlot().isDomainCrosshairVisible()) {
			return;
		}
		Point2D screenPoint = myChartPanel.chartToScreen(myChartPanel.getCrosshairPoint());
		if ((screenPoint == null) || ((lastSelectedPoint != null) && (screenPoint.equals(lastSelectedPoint)))) {
			return;
		}
		lastSelectedPoint = screenPoint;
		ChartEntity entity = myChartPanel.getChartPanel().getEntityForPoint((int) screenPoint.getX(),
				(int) screenPoint.getY());
		if ((entity != null) && ((entity instanceof XYItemEntity))) {
			doItemEntity((XYItemEntity) entity);
		}
	}
}
