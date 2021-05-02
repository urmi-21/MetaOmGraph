/**
 * 
 */
package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;

/**
 * @author sumanth
 *
 */
public class HeatMapChart extends TaskbarInternalFrame implements ActionListener{
	
	private double[][] originalData;
	private double[][] heatMapData;
	private String[] rowNames;
	private String[] columnNames;
	private String[] selectedDataCols;
	
	private JButton properties;
	private JButton save;
	private JButton print;
	private JButton zoomIn;
	private JButton zoomOut;
	private JButton defaultZoom;
	private JButton changePalette;
	private JButton splitDataset;
	private JToggleButton toggleLegend;
	private JScrollPane scrollPane;
	private JButton bottomPanelButton;
	
	private boolean legendFlag = true;
	private Color minValueColor = Color.GREEN;
	private Color maxValueColor = Color.RED;
	private double minValue;
	private double maxValue;
	private boolean transposeData;
	private boolean sampleDataOnRow;
	private String splitCol;
	private Map<String, Collection<Integer>> splitIndex;
	private HeatMapPanel heatMap;
	private Font textFont;
	private Color textColor;
	
	public HeatMapChart(double[][] data, String[] rowNames, String[] columnNames, boolean transposeData) {
		super();
		this.originalData = data;
		this.heatMapData = data;
		this.rowNames = rowNames;
		this.columnNames = columnNames;
		this.transposeData = transposeData;
		this.sampleDataOnRow = transposeData;
		this.selectedDataCols = columnNames;
		this.textFont = new JLabel().getFont();
		this.textColor = Color.BLACK;
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		createTopToolBar();
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		createHeatMapPanel();
		
		createBottomPanel();
	
		this.setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setTitle("Heat Map");
		
		FrameModel heatMapFrameModel = new FrameModel("Heat Map", "Heat Map", 3);
		setModel(heatMapFrameModel);
	}
	
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
		print = new JButton(theme.getPrint());
		print.setToolTipText("Print Chart");
		zoomIn = new JButton(theme.getZoomIn());
		zoomIn.setToolTipText("Zoom In");
		zoomOut = new JButton(theme.getZoomOut());
		zoomOut.setToolTipText("Zoom Out");
		defaultZoom = new JButton(theme.getDefaultZoom());
		defaultZoom.setToolTipText("Default Zoom");	
		save.setActionCommand("SaveImage");
		save.addActionListener(this);
		print.setActionCommand("PRINT");
		print.addActionListener(this);
		zoomIn.setActionCommand("ZoomIn");
		zoomIn.addActionListener(this);
		zoomOut.setActionCommand("ZoomOut");
		zoomOut.addActionListener(this);
		defaultZoom.setActionCommand("DefaultZoom");
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
		//chartButtonsPanel.add(zoomIn);
		//chartButtonsPanel.add(zoomOut);
		//chartButtonsPanel.add(defaultZoom);
		chartButtonsPanel.add(splitDataset);
		chartButtonsPanel.add(changePalette);

		getContentPane().add(chartButtonsPanel, BorderLayout.NORTH);
	}
	
	private void createBottomPanel() {
		JPanel bottomPanel = new JPanel();

		bottomPanelButton = new JButton("Transpose axis");
		bottomPanelButton.setActionCommand("transposeAxis");
		bottomPanelButton.addActionListener(this);
		bottomPanel.add(bottomPanelButton);
		
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}
		
	private void createHeatMapPanel() {
		if(transposeData && splitIndex == null) {
			heatMapData = Utils.getTransposeMatrix(heatMapData);
			String[] temp = columnNames;
			columnNames = rowNames;
			rowNames = temp;
			//heatMap = new HeatMapPanel(transposedData, rowNames, columnNames);
		}
		heatMap = new HeatMapPanel(heatMapData, rowNames, columnNames);
//		JHeatMapModel heatMapModel = new JHeatMapModel(heatMapData, rowNames, columnNames);
//		heatMap = new JHeatMap(heatMapModel);
		//heatMap.setPreferredSize(new Dimension(800, 600));
//		maxValue = heatMap.getHighValue();
//		minValue = heatMap.getLowValue();
		//scrollPane.setPreferredSize(new Dimension(400, 600));
		scrollPane.setViewportView(heatMap);
		//this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if("transposeAxis".equals(e.getActionCommand())) {
			transposeData = true;
			sampleDataOnRow = !sampleDataOnRow;
			if(sampleDataOnRow) {
				splitDataset.setEnabled(false);
			}else {
				splitDataset.setEnabled(true);
			}
			createHeatMapPanel();
		}
		
		if("Properties".equals(e.getActionCommand())) {
			
			HeatMapChartProperties heatMapProperties = new HeatMapChartProperties(textFont, textColor);
			if(heatMapProperties.isValuesChanged()) {
				textFont = heatMapProperties.getSelectedFont();
				textColor = heatMapProperties.getSelectedFontColor();
				heatMap.setLabelColorAndFont(textFont, textColor);
			}
		}
		
		if("ZoomIn".equals(e.getActionCommand())) {
//			try {
//				heatMap.zoomIn(2.0);
//			}
//			catch(Exception e1) {
//				CustomMessagePane errorMessage = new CustomMessagePane("Heat Map zoom", "Reached the maximum zoom limit",
//						MessageBoxType.ERROR, MessageBoxButtons.OK);
//				errorMessage.displayMessageBox();
//			}

		}
		
		if("ZoomOut".equals(e.getActionCommand())) {
//			try {
//				heatMap.zoomOut(0.5);
//			}
//			catch(Exception e1) {
//				CustomMessagePane errorMessage = new CustomMessagePane("Heat Map zoom", "Reached the minimum zoom limit",
//						MessageBoxType.ERROR, MessageBoxButtons.OK);
//				errorMessage.displayMessageBox();
//			}
		}
		
		if("DefaultZoom".equals(e.getActionCommand())) {
			createHeatMapPanel();
		}
		
		if("SaveImage".equals(e.getActionCommand())){
			HashMap<String, String> fileTypes = new HashMap<String, String>();
			fileTypes.put("PNG", ".png");
			JFileChooser fileSave = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG files", "png");
			fileSave.addChoosableFileFilter(filter);
			
			if(fileSave.showSaveDialog(MetaOmGraph.getMainWindow()) == JFileChooser.APPROVE_OPTION){
				
				File fileToSave = fileSave.getSelectedFile();
				if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
					fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".png");
			      }
				if(heatMap.saveImage(fileToSave, ".png"))
					JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Image Saved");
				else
					JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), 
							"Image saved", "File save error", JOptionPane.ERROR_MESSAGE);
//				try {
//					//heatMap.toPngImage(fileToSave);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
			}				
		}
		
		if("changePalette".equals(e.getActionCommand())) {
			Map<String, ColorBrewer> clusterRowColorMap = heatMap.getClusterRowColorMap();
			if(clusterRowColorMap == null)
				return;
			JButton[] cButtons = new JButton[clusterRowColorMap.size()];
			ColorBrewer[] buttonsCB = new ColorBrewer[clusterRowColorMap.size()];
			JPanel cbPanel = new JPanel();
			cbPanel.setLayout(new GridLayout(0, 3));
			//ColorBrewer colors = ColorBrewer.;
			ArrayList<String> clusterRowNames = new ArrayList<String>(clusterRowColorMap.keySet());
			int index = 0;
			for(Map.Entry<String, ColorBrewer> entry : clusterRowColorMap.entrySet()) {
				cButtons[index] = new JButton(entry.getKey());
				buttonsCB[index] = entry.getValue();
				cButtons[index].setBackground(entry.getValue().getColorPalette(5)[3]);
				//Color[] cArr = colors.getColorPalette(clusterRowNames.size());
				cButtons[index].addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JButton button = (JButton) e.getSource();
						ColorPaletteChooserDialog dialog = new ColorPaletteChooserDialog();
						ColorBrewer cb = null;
						dialog.setModal(true);
						dialog.show();
						if (dialog.wasOKPressed()) {
							cb = dialog.getColorPalette();
						}
						if (cb != null) {
							buttonsCB[clusterRowNames.indexOf(button.getText())] = cb;
							Color[] color = cb.getColorPalette(5);
							button.setBackground(color[3]);
						}
					}
				});
				cbPanel.add(cButtons[index++]);
			}
			int res = JOptionPane.showConfirmDialog(null, cbPanel, "Select colors for rows",
					JOptionPane.OK_CANCEL_OPTION);
			Map<String, ColorBrewer> setClusterRowColorMap = null;
			if (res == JOptionPane.OK_OPTION) {
				setClusterRowColorMap = new HashMap<String, ColorBrewer>();
				for(int i = 0; i < cButtons.length; i++) {
					setClusterRowColorMap.put(cButtons[i].getText(), buttonsCB[i]);
				}
			} else {
				return;
			}
			heatMap.setClusterRowColorMap(setClusterRowColorMap);
		}
		
		if("splitDataset".equals(e.getActionCommand())) {
			String[] options = {"By Metadata", "By Query", "Reset"};	

			String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (col_val == null) {
				return;
			}
			
			if (col_val.equals("Reset")) {
				splitCol = null;
				splitIndex = null;
				transposeData = false;
				this.heatMapData = originalData;
				bottomPanelButton.setEnabled(true);
				createHeatMapPanel();
				return;
			}
			List<String> selectedVals = new ArrayList<>();
			if (col_val.equals("By Metadata")) {
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
				heatMap.clusterColumns(selectedVals, splitIndex);
				bottomPanelButton.setEnabled(false);
			}
		}
	}
	
	private void updateHeatMap() {
		if(splitIndex != null) {
			if(sampleDataOnRow) {
				double[][] updatedData = new double[rowNames.length + splitIndex.size()][columnNames.length];
				String[] updatedRows = new String[rowNames.length + splitIndex.size()];
				int index = 0;
				for(Map.Entry<String, Collection<Integer>> entry : splitIndex.entrySet()) {
					updatedRows[index] = entry.getKey();
					double[] naArray = new double[columnNames.length];
					Arrays.fill(naArray, Double.NaN);
					naArray[naArray.length-1] = minValue;
					updatedData[index++] = naArray;
					ArrayList<Integer> clusterIndices = new ArrayList<Integer>(entry.getValue());
					for(int i : clusterIndices) {
						updatedRows[index] = rowNames[i];
						updatedData[index++] = heatMapData[i];
					}
				}
				
//				JHeatMapModel heatMapModel = new JHeatMapModel(updatedData, updatedRows, columnNames);
//				heatMap = new JHeatMap(heatMapModel);
//				heatMap.setNanColor(Color.WHITE);
//				heatMap.setPreferredSize(new Dimension(800, 600));
//				maxValue = heatMap.getHighValue();
//				minValue = heatMap.getLowValue();
//				scrollPane.setViewportView(heatMap);
			}
			else {
				double[][] updatedData = new double[rowNames.length][columnNames.length + splitIndex.size()];
				String[] updatedColumns = new String[columnNames.length + splitIndex.size()];
				int index = 0;
				for(Map.Entry<String, Collection<Integer>> entry : splitIndex.entrySet()) {
					updatedColumns[index] = entry.getKey();
					for(int i = 0; i < updatedData.length; i++) {
						updatedData[i][index] = Double.NaN;
					}
					//updatedData[0][index] = minValue;
					index++;
					ArrayList<Integer> clusterIndices = new ArrayList<Integer>(entry.getValue());
					for(int i : clusterIndices) {
						updatedColumns[index] = columnNames[i];
						for(int j = 0; j < updatedData.length; j++) {
							updatedData[j][index] = heatMapData[j][i];
						}
						index++;
					}
				}
//				JHeatMapModel heatMapModel = new JHeatMapModel(updatedData, rowNames, updatedColumns);
//				heatMap = new JHeatMap(heatMapModel);
//				heatMap.setNanColor(Color.WHITE);
//				heatMap.setPreferredSize(new Dimension(800, 600));
//				maxValue = heatMap.getHighValue();
//				minValue = heatMap.getLowValue();
//				scrollPane.setViewportView(heatMap);
			}
		}
	}
}
