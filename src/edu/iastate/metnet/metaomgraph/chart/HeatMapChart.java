/**
 * 
 */
package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;
import edu.iastate.metnet.metaomgraph.utils.HierarchicalClusterData;
import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import smile.plot.swing.Canvas;
import smile.plot.swing.Heatmap;
import smile.plot.swing.PlotPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;

/**
 * @author sumanth
 * HeatMapChart class is frame which holds the heatmap panel and button panels and all
 * the user interaction related functions
 */
public class HeatMapChart extends TaskbarInternalFrame implements ActionListener{
	
	private double[][] originalData;
	private double[][] heatMapData;
	private String[] rowNames;
	private String[] originalRowNames;
	private String[] columnNames;
	private String[] selectedDataCols;
	private Canvas heatMapCanvas;
	private PlotPanel heatMapPanel;
	
	private JButton properties;
	private JButton save;
	private JButton changePalette;
	private JButton splitDataset;
	private JButton zoomIn;
	private JButton zoomOut;
	private JButton reset;
	JButton clusterDataBtn;

	private JButton bottomPanelButton;
	
	private boolean clusteredRows;
	private HierarchicalClusterData clusteredData;
	private boolean transposeData;
	private boolean sampleDataOnRow;
	private Map<String, Collection<Integer>> splitIndex;
	private Font textFont;
	private Color textColor;
	private Heatmap heatmap;
	private Color[] palette;
	
	/**
	 * Constructor
	 * @param data 2d heatmap data
	 * @param rowNames row labels to be displayed on the rows (left), length should be same as data.length
	 * @param columnNames column labels to be displayed on the columns (top), length should be same as data[0].length
	 * @param transposeData transpose rows and columns?
	 */
	public HeatMapChart(double[][] data, String[] rowNames, String[] columnNames, boolean transposeData) {
		super();
		this.originalData = data;
		this.heatMapData = data;
		this.rowNames = rowNames;
		this.originalRowNames = rowNames;
		this.columnNames = columnNames;
		this.transposeData = transposeData;
		this.sampleDataOnRow = transposeData;
		this.selectedDataCols = columnNames;
		this.textFont = new JLabel().getFont();
		this.textColor = Color.BLACK;
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		// Create the tool bar at the top which contains save, properties...
		createTopToolBar();

		// old method of generating heatmap
//		scrollPane = new JScrollPane();
//		getContentPane().add(scrollPane, BorderLayout.CENTER);
//		createHeatMapPanel();
		createHeatMap();
		
		// Create the bottom panel which contains the transpose button
		//createBottomPanel();
	
		this.setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setTitle("Heat Map");
		
		FrameModel heatMapFrameModel = new FrameModel("Heat Map", "Heat Map", 3);
		setModel(heatMapFrameModel);
	}
	
	// Create the tool bar panel of the chart
	private void createTopToolBar() {
		JPanel chartButtonsPanel = new JPanel();
		chartButtonsPanel.setLayout(new FlowLayout());

		IconTheme theme = MetaOmGraph.getIconTheme();
		properties = new JButton(theme.getProperties());
		properties.setToolTipText("Chart Properties");
		properties.setActionCommand("Properties");
		properties.addActionListener(this);
		
		save = new JButton(theme.getSaveAs());
		save.setToolTipText("Save Chart as Image");
		save.setActionCommand("SaveImage");
		save.addActionListener(this);

		splitDataset = new JButton(theme.getSort());
		splitDataset.setToolTipText("Split by meatadata");
		splitDataset.setActionCommand("splitDataset");
		splitDataset.addActionListener(this);
		
		clusterDataBtn = new JButton(theme.getClusterIcon());
		clusterDataBtn.setToolTipText("Sort/group rows");
		clusterDataBtn.setActionCommand("clusterDataSet");
		clusterDataBtn.addActionListener(this);

		changePalette = new JButton(theme.getPalette());
		changePalette.setToolTipText("Color Palette");
		changePalette.setActionCommand("changePalette");
		changePalette.addActionListener(this);
		changePalette.setOpaque(false);
		changePalette.setContentAreaFilled(false);
		changePalette.setBorderPainted(true);
		changePalette.setEnabled(false);

		zoomIn = new JButton(theme.getZoomIn());
		zoomIn.setToolTipText("Zoom In");
		zoomIn.setActionCommand("zoomIn");
		zoomIn.addActionListener(this);

		zoomOut = new JButton(theme.getZoomOut());
		zoomOut.setToolTipText("Zoom Out");
		zoomOut.setActionCommand("zoomOut");
		zoomOut.addActionListener(this);

		reset = new JButton(theme.getReset());
		reset.setToolTipText("Reset View");
		reset.setActionCommand("reset");
		reset.addActionListener(this);

		//chartButtonsPanel.add(properties);
		chartButtonsPanel.add(save);
		//chartButtonsPanel.add(splitDataset);
		chartButtonsPanel.add(changePalette);
		chartButtonsPanel.add(zoomIn);
		chartButtonsPanel.add(zoomOut);
		chartButtonsPanel.add(reset);

		getContentPane().add(chartButtonsPanel, BorderLayout.NORTH);
	}
	
	// Create the bottom panel which contains Transpose axis button
	private void createBottomPanel() {
		JPanel bottomPanel = new JPanel();

		bottomPanelButton = new JButton("Transpose axis");
		bottomPanelButton.setActionCommand("transposeAxis");
		bottomPanelButton.addActionListener(this);
		bottomPanel.add(bottomPanelButton);
		
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	private void createHeatMap() {
		heatmap = Heatmap.of(rowNames, columnNames, heatMapData);
		heatMapCanvas = heatmap.canvas();
		heatMapCanvas.setAxisLabel(0, "");
		heatMapCanvas.setAxisLabel(1, "");
		heatMapPanel = heatMapCanvas.panel();
		heatMapPanel.getToolbar().setVisible(false);
		getContentPane().add(heatMapPanel);
	}
		
	// The main heatmap panel
//	private void createHeatMapPanel() {
//		// If transpose data and clustering is not selected
//		if(transposeData && splitIndex == null) {
//			heatMapData = Utils.getTransposeMatrix(heatMapData);
//			String[] temp = columnNames;
//			columnNames = rowNames;
//			rowNames = temp;
//		}
//		heatMap = new HeatMapPanel(heatMapData, rowNames, columnNames);
//		heatMap.setSampleDataOnRow(sampleDataOnRow);
//		scrollPane.setViewportView(heatMap);
//	}

	public boolean saveImage( File file, String fileExtension) {
		BufferedImage image = heatmap.canvas().toBufferedImage(1000, 1000);
		Graphics2D g2 = image.createGraphics();
		paint(g2);
		try{
			ImageIO.write(image, fileExtension, file);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
					"Image not saved", "File save error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// transpose axis row->column and column->row
		if("transposeAxis".equals(e.getActionCommand())) {
			transposeData = true;
			sampleDataOnRow = !sampleDataOnRow;
			if(sampleDataOnRow) {
				splitDataset.setEnabled(false);
				changePalette.setEnabled(false);
				clusterDataBtn.setEnabled(false);
			}else {
				splitDataset.setEnabled(true);
				clusterDataBtn.setEnabled(true);
			}
			new AnimatedSwingWorker("Transposing rows and columns", true) {
				
				@Override
				public Object construct() {
					createHeatMap();
					return null;
				}
			}.start();
		}
		
		// Properties
		if("Properties".equals(e.getActionCommand())) {
			return;
		}
		
		// Save the heatmap as image
		if("SaveImage".equals(e.getActionCommand())){
			try {
				heatMapPanel.save();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
//			HashMap<String, String> fileTypes = new HashMap<String, String>();
//			fileTypes.put("PNG", ".png");
//			JFileChooser fileSave = new JFileChooser();
//			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG files", "png");
//			fileSave.addChoosableFileFilter(filter);
//
//			if(fileSave.showSaveDialog(MetaOmGraph.getMainWindow()) == JFileChooser.APPROVE_OPTION){
//
//				File fileToSave = fileSave.getSelectedFile();
//				if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
//					fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".png");
//			      }
//				if(saveImage(fileToSave, "png"))
//					JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Image Saved");
//			}
		}

		if ("zoomIn".equals(e.getActionCommand())) {
			heatMapPanel.zoom(true);
		}

		if ("zoomOut".equals(e.getActionCommand())) {
			heatMapPanel.zoom(false);
		}

		if ("reset".equals(e.getActionCommand())) {
			heatMapPanel.reset();
		}

		// Change color pallet
		if ("changePalette".equals(e.getActionCommand())){
			return;
		}
		
		// Change the cluster colors
//		if("changePalette".equals(e.getActionCommand())) {
//			Map<String, ColorBrewer> clusterRowColorMap = heatMapPanel.getClusterRowColorMap();
//			if(clusterRowColorMap == null)
//				return;
//			JButton[] cButtons = new JButton[clusterRowColorMap.size()];
//			ColorBrewer[] buttonsCB = new ColorBrewer[clusterRowColorMap.size()];
//			JPanel cbPanel = new JPanel();
//			cbPanel.setLayout(new GridLayout(0, 3));
//			ArrayList<String> clusterRowNames = new ArrayList<String>(clusterRowColorMap.keySet());
//			int index = 0;
//			// For each cluster row, add a button with the color of the cluster
//			for(Map.Entry<String, ColorBrewer> entry : clusterRowColorMap.entrySet()) {
//				cButtons[index] = new JButton(entry.getKey());
//				buttonsCB[index] = entry.getValue();
//				cButtons[index].setBackground(entry.getValue().getColorPalette(5)[3]);
//				cButtons[index].addActionListener(new ActionListener() {
//
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						JButton button = (JButton) e.getSource();
//						ColorPaletteChooserDialog dialog = new ColorPaletteChooserDialog();
//						ColorBrewer cb = null;
//						dialog.setModal(true);
//						dialog.show();
//						if (dialog.wasOKPressed()) {
//							cb = dialog.getColorPalette();
//						}
//						if (cb != null) {
//							buttonsCB[clusterRowNames.indexOf(button.getText())] = cb;
//							Color[] color = cb.getColorPalette(5);
//							button.setBackground(color[3]);
//						}
//					}
//				});
//				cbPanel.add(cButtons[index++]);
//			}
//			int res = JOptionPane.showConfirmDialog(null, cbPanel, "Select colors for rows",
//					JOptionPane.OK_CANCEL_OPTION);
//			Map<String, ColorBrewer> setClusterRowColorMap = null;
//			if (res == JOptionPane.OK_OPTION) {
//				setClusterRowColorMap = new HashMap<String, ColorBrewer>();
//				for(int i = 0; i < cButtons.length; i++) {
//					setClusterRowColorMap.put(cButtons[i].getText(), buttonsCB[i]);
//				}
//			} else {
//				return;
//			}
//			heatMapPanel.setClusterRowColorMap(setClusterRowColorMap);
//		}
		
		// Cluster the data
//		if("splitDataset".equals(e.getActionCommand())) {
//			String[] options = {"By Metadata", "Reset"};
//
//			String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
//					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
//			if (col_val == null) {
//				return;
//			}
//
//			if (col_val.equals("Reset")) {
//				splitIndex = null;
//				transposeData = false;
//				this.heatMapData = originalData;
//				bottomPanelButton.setEnabled(true);
//				new AnimatedSwingWorker("Resetting..") {
//
//					@Override
//					public Object construct() {
//						createHeatMapPanel();
//						return null;
//					}
//				}.start();
//				changePalette.setEnabled(false);
//				return;
//			}
//			List<String> selectedVals = new ArrayList<>();
//			if (col_val.equals("By Metadata")) {
//				String[] fields =  MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataHeaders();
//				JCheckBox[] cBoxes = new JCheckBox[fields.length];
//				JPanel cbPanel = new JPanel();
//				cbPanel.setLayout(new GridLayout(0, 3));
//				for (int i = 0; i < fields.length; i++) {
//					cBoxes[i] = new JCheckBox(fields[i]);
//					cbPanel.add(cBoxes[i]);
//				}
//				int res = JOptionPane.showConfirmDialog(null, cbPanel, "Select categories",
//						JOptionPane.OK_CANCEL_OPTION);
//				if (res == JOptionPane.OK_OPTION) {
//					for (int i = 0; i < fields.length; i++) {
//						if (cBoxes[i].isSelected()) {
//							selectedVals.add(fields[i]);
//						}
//					}
//				} else {
//					return;
//				}
//				List<String> dataCols = Arrays.asList(selectedDataCols);
//				new AnimatedSwingWorker("Clustering...") {
//
//					@Override
//					public Object construct() {
//						splitIndex = MetaOmGraph.getActiveProject().getMetadataHybrid().cluster(selectedVals, dataCols);
//						heatMapPanel.clusterColumns(selectedVals, splitIndex);
//						return null;
//					}
//				}.start();
//				changePalette.setEnabled(true);
//				bottomPanelButton.setEnabled(false);
//			}
//		}
	}
}
