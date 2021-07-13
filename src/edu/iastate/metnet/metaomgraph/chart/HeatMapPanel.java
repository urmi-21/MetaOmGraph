package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.ArrayUtils;
import org.jcolorbrewer.ColorBrewer;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.utils.Utils;

/**
 * 
 * @author sumanth
 * HeatMapPanel class
 * This class is responsible for all the rendering related to heatmap
 * Uses JTable to render the heatmap and many custom/overridden classes to generate heatmap from jtables
 * This also holds the legendpanel class
 */
public class HeatMapPanel extends JPanel{
	
	private double minValue;
	private double maxValue;
	private int numOfRowsOccupiedByLabels;
	private String[] rowNames;
	private String[] colNames;
	private int[] columnIndexColNamesMap;
	private int[] clusterLabelsInRowTillNow;
	private double[][] heatMapData;
	private boolean heatMapTableUpdating;
	private boolean sampleDataOnRow;
	private List<ColorBrewer> clusterLabelColors;
	private List<String> clusterRowNames;
	private Map<String, Collection<Integer>> columnClusterMap;
	private Map<String, Color> labelColorMap = new HashMap<String, Color>();
	private Map<String, ColorBrewer> clusterRowColorMap;
	private HashMap<String, ArrayList<Integer[]>> clusterLabelRangesMap;
	private TreeMap<Double[], Color> valueRangeColorMap;
	
	private JTable heatMapTable;
	private LegendPanel legendPanel;	
	private Font textFont;
	private Color textColor;
			
	/**
	 * Constructor
	 * @param data 2d heatmap data
	 * @param rowNames row labels to be displayed on the rows (left), length should be same as data.length
	 * @param columnNames column labels to be displayed on the columns (top), length should be same as data[0].length
	 */
	public HeatMapPanel(double[][] data, String[] rowNames, String[] colNames) {
		this.heatMapData = data;
		this.rowNames = rowNames;
		this.colNames = colNames;
		this.heatMapTableUpdating = false;
		this.sampleDataOnRow = false;
		this.textFont = new Font(null, Font.BOLD, 10);
		clusterRowNames = new ArrayList<String>();
		clusterRowNames.add("Data column");
		// Java 9 -> change it to new TreeMap<>(Arrays::compare);
		valueRangeColorMap = new TreeMap<>((o1, o2) -> {
	        for (int i = 0; i < o1.length; i++) {
	            if (o1[i] > o2[i]) {
	                return 1;
	            } else if (o1[i] < o2[i]) {
	                return -1;
	            }
	        }

	        return 0;
	    });
		
		this.setLayout(new GridLayout(2, 0));
		createTable();
	}
	
	// Tool tip text
	private String createToolTipTextString(String dataColName, String value) {
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
		text += "<td><font size=-2>" + Utils.wrapText("Value", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(value, 100, "<br>")
				+ "</font></td>";
		text += "</tr>";

		text += "<tr bgcolor=" + rowColors[0] + ">";
		text += "<td><font size=-2>" + Utils.wrapText("Sample", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(dataColName, 100, "<br>") + "</font></td>";
		text += "</tr>";

		if (MetaOmGraph.getActiveProject().getMetadataHybrid() == null) {
			return text;
		}
		
		HashMap<String, String> rowValsMap = MetaOmGraph.getActiveProject().getMetadataHybrid().
				getMetadataCollection().getDataColumnRowMap(dataColName);
		
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
	
	// Function which creates the JTable 
	private void createTable() {
		heatMapTableUpdating = true;
		// Create a JTable with 1 additional row and column to accomodate row and column labels
		heatMapTable = new JTable(heatMapData.length + 1, heatMapData[0].length + 1) {
			
			// Custom tool tip function
			@Override
			public String getToolTipText(MouseEvent e) {
				Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				if(rowIndex == -1 || colIndex == -1 || colIndex == 0)
					return "";
				if(sampleDataOnRow && rowIndex == 0) {
					return "";
				}
				DecimalFormat df = new DecimalFormat("####0.0000");
				// If clustering is enabled
				if(columnClusterMap != null) {
					String dataColName = colNames[columnIndexColNamesMap[colIndex]];
					String value;
					// key -> "clusterLabel : value" 
					if(rowIndex < numOfRowsOccupiedByLabels) {
						String clusterLabel = (String) this.getValueAt(rowIndex, 0);
						value = clusterLabel + " : " + (String) this.getValueAt(rowIndex, colIndex);
					}
					else {
						value = df.format((double) this.getValueAt(rowIndex, colIndex));
					}
					return createToolTipTextString(dataColName, value);
				}else {
					String dataColName;
					if(sampleDataOnRow) {
						dataColName = rowNames[rowIndex - 1];
					}
					else {
						dataColName = colNames[colIndex - 1];
					}
					String value;
					if (rowIndex == 0) {
						value = dataColName;
					}
					else {
						value = df.format((double) this.getValueAt(rowIndex, colIndex));
					}
					return createToolTipTextString(dataColName, value);
				}
			}
			
			// Custom function to specify the tool tip display location
			@Override
			public Point getToolTipLocation(MouseEvent event) {
				Point thisPoint = event.getPoint();
				int rowIndex = rowAtPoint(thisPoint);
				columnAtPoint(thisPoint);
				if(rowIndex == 0 || (columnClusterMap != null && rowIndex < numOfRowsOccupiedByLabels)) {
					return thisPoint;
				}
				int maxWidth = getWidth();
				int xMargin = 25;
				int newy = 100;
				int x = thisPoint.x;
				if (maxWidth - x <= 450) {
					return new Point(x - (400 + xMargin), newy);
				}
				return new Point(x + xMargin, newy);
			}
			
			// Do not support editing
			@Override
			public boolean isCellEditable(int row, int column) {                
                return false;               
            }
		};
		
		// Custom MouseHandler class to support sorting by dragging the rows
		MouseHandler mouseHandler = 
				new MouseHandler(heatMapTable, (DefaultTableModel) heatMapTable.getModel());
		heatMapTable.addMouseListener(mouseHandler);
		heatMapTable.addMouseMotionListener(mouseHandler);

		int rowLen = heatMapData.length;
		int colLen = heatMapData[0].length;
		heatMapTable.setGridColor(Color.BLACK);
		// Specify the default renderer which is our custom CellRenderer
		heatMapTable.setDefaultRenderer(Object.class, new CellRenderer());				
		minValue = Double.MAX_VALUE;
		maxValue = Double.MIN_VALUE;
		
		// Top left cell
		heatMapTable.setValueAt("", 0, 0);
		// Set all the row labels
		for(int i = 1; i < rowLen + 1; i++) {
			heatMapTable.setValueAt(rowNames[i - 1], i, 0);
		}
		// Set all the column labels
		for(int i = 1; i < colLen + 1; i++) {
			heatMapTable.setValueAt(colNames[i - 1], 0, i);
		}
	
		// Set the values in the cells now
		for(int i = 0; i < rowLen; i++) {
			for(int j = 0; j < colLen; j++) {
				minValue = Math.min(minValue, heatMapData[i][j]);
				maxValue = Math.max(maxValue, heatMapData[i][j]);
				heatMapTable.setValueAt(heatMapData[i][j], i+1, j+1);
			}
		}
		
		// Set the width of each cell.
		heatMapTable.getColumnModel().getColumn(0).setMaxWidth(50);
		for(int i = 1; i <= colLen; i++) {
			heatMapTable.getColumnModel().getColumn(i).setPreferredWidth(1);
		}
		
		// Fill the valueRangeColorMap based on the values in the cells. 
		fillColorRangesMap();

		setVisible(true);
		add(heatMapTable);
		heatMapTableUpdating = false;
		this.legendPanel = new LegendPanel();
		add(legendPanel);
	}
	
	// Fill the valueRangeColorMap based on the interval returned by calculateInterval method.
	private void fillColorRangesMap() {
		double interval = calculateInterval();
		Color startRangeColor = Color.CYAN;
		Color endRangeColor = Color.BLUE;
		valueRangeColorMap.put(new Double[] {minValue, minValue + interval}, startRangeColor);
		// Get increasing color for each interval
		for(double i = minValue; i <= maxValue; i += interval) {
			double rangeVal = ((i - minValue) / (maxValue - minValue));
			double fraction = rangeVal % 1;
			if(rangeVal != 0 && rangeVal != 1) {
				int minR = startRangeColor.getRed();
				int minG = startRangeColor.getGreen();
				int minB = startRangeColor.getBlue();
				int maxR = endRangeColor.getRed();
				int maxG = endRangeColor.getGreen();
				int maxB = endRangeColor.getBlue();
				int currR = (int) (minR + fraction * (maxR - minR));
				int currG = (int) (minG + fraction * (maxG - minG));
				int currB = (int) (minB + fraction * (maxB - minB));
				valueRangeColorMap.put(new Double[] {i, i + interval}, 
						new Color(currR, currG, currB));
			}
		}
	}
	
	// Calculate the interval based on min and max values
	private double calculateInterval() {
		double range = maxValue - minValue;
	    double x = Math.pow(10.0, Math.floor(Math.log10(range)));
	    if (range / x >= 5)
	        return x;
	    else if (range / (x / 2.0) >= 5)
	        return x / 2.0;
	    else
	        return x / 5.0;
	}
	
	/**
	 * saveImage to save the heatmap as image
	 * @param file File to save
	 * @param fileExtension extension of file
	 * @return boolean true if success, else false
	 */
	public boolean saveImage(File file, String fileExtension) {
		BufferedImage image = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_ARGB);
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
	
	/**
	 * Set the text font and color 
	 * @param textFont Font
	 * @param textColor Color
	 */
	public void setLabelColorAndFont(Font textFont, Color textColor) {
		this.textFont = textFont;
		this.textColor = textColor;
		if(columnClusterMap != null)
			heatMapTable.repaint();
	}
	
	/**
	 * Set the user selected colors for each range
	 * @param valueColorRange valueColorRangeMap
	 */
	public void setRangeColors(TreeMap<Double[], Color> valueColorRange) {
		this.valueRangeColorMap = valueColorRange;
	}
	
	/**
	 * Add a range and its color to the valueColorRangeMap
	 * @param rangeStart starting value of the range
	 * @param rangeEnd ending value of the range
	 * @param color color
	 */
	public void addRangeColor(double rangeStart, double rangeEnd, Color color) {
		valueRangeColorMap.put(new Double[] {rangeStart, rangeEnd}, color);
	}
	
	/**
	 * Set custom colors for the cluster rows
	 * @param clusterRowColorMap
	 */
	public void setClusterRowColorMap(Map<String, ColorBrewer> clusterRowColorMap) {
		this.clusterRowColorMap = clusterRowColorMap;
		// Clear the exisitng label colors 
		labelColorMap.clear();
		clusterLabelsInRowTillNow = new int[clusterRowNames.size()];
		// Update the heatmaptable with new colors
		updateHeatMapTableWithClusters();
	}
	
	/**
	 * Set the boolean whether the sample data is on row or columns. Used in tool tips
	 * @param sampleDataOnRow
	 */
	public void setSampleDataOnRow(boolean sampleDataOnRow) {
		this.sampleDataOnRow = sampleDataOnRow;
	}
	
	/**
	 * get the current cluster row colors
	 * @return clusterRowColorMap
	 */
	public Map<String, ColorBrewer> getClusterRowColorMap(){
		return this.clusterRowColorMap;
	}
	
	public void setHeatMapData(double[][] heatMapData) {
		this.heatMapData = heatMapData;
	}
	
	public void setRowNames(String[] rowNames) {
		this.rowNames = rowNames;
	}

	/**
	 * Do the clustering on the heatmap columns using columnClusterMap
	 * @param clusterRowNames cluster types
	 * @param columnClusterMap map with cluster as key and set of column indices for that cluster 
	 */
	public void clusterColumns(List<String> clusterRowNames,
			Map<String, Collection<Integer>> columnClusterMap) {
		clusterLabelRangesMap = new HashMap<String, ArrayList<Integer[]>>();
		clusterLabelsInRowTillNow = new int[clusterRowNames.size()];
		this.clusterRowNames = clusterRowNames;
		this.columnClusterMap = columnClusterMap;
		this.labelColorMap = new HashMap<String, Color>();
		columnIndexColNamesMap = new int[colNames.length + 1];
		// Set the possible colors of the ColorBrewer
		if(clusterLabelColors == null) {
			clusterLabelColors = new ArrayList<ColorBrewer>();
			clusterLabelColors.add(ColorBrewer.Greens);
			clusterLabelColors.add(ColorBrewer.Oranges);
			clusterLabelColors.add(ColorBrewer.Purples);
			clusterLabelColors.add(ColorBrewer.Reds);
			clusterLabelColors.add(ColorBrewer.hsvCyMg);
		}
		updateClusterLabelRowColors();
		updateHeatMapTableWithClusters();
	}
	
	// set the ColorBrewer for each row of the clusters
	private void updateClusterLabelRowColors() {
		int currColorIndex = 0;
		clusterRowColorMap = new LinkedHashMap<String, ColorBrewer>();
		for(String clusterName : clusterRowNames) {
			ColorBrewer colorBrewer = clusterLabelColors.
					get(currColorIndex++ % clusterLabelColors.size());
			clusterRowColorMap.put(clusterName, colorBrewer);
		}
	}
	
	// From the given columnClusterLabels, fill the labelColorMap
	private void addLabelsToColorMap(String[] columnClusterLabels) {
		for(int rowIndex = 0; rowIndex < columnClusterLabels.length; rowIndex++) {
			// if the color is already added to the cluster label, continue to next label
			if(labelColorMap.containsKey(columnClusterLabels[rowIndex] + ":" + rowIndex))
				continue;
			// get the ColorBrewer for the row
			ColorBrewer cb = clusterRowColorMap.get(clusterRowNames.get(rowIndex));
			Color[] colors = cb.getColorPalette(5);
			ArrayUtils.reverse(colors);
			// get the color from the ColorBrewer 
			int colorIndex = clusterLabelsInRowTillNow[rowIndex]++;
			Color color = colors[colorIndex % colors.length];
			// update the label color in map
			labelColorMap.put(columnClusterLabels[rowIndex] + ":" + rowIndex, color);
		}
	}
	
	// Updates the heatmap table with clusters
	public void updateHeatMapTableWithClusters() {
		heatMapTableUpdating = true;
		// reset the existing table first
		DefaultTableModel tablemodel = (DefaultTableModel) heatMapTable.getModel();
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		
		// get the set of cluster ex: abc;def;ghi
		Set<String> clusterColumnLabelsSet = columnClusterMap.keySet();
		String firstHeader = clusterColumnLabelsSet.stream().findFirst().get();
		// get all the cluster labels [abc, def, ghi]
		String[] clusterLabels = firstHeader.split(";");
		// the length denotes the number of rows occupied by columns
		numOfRowsOccupiedByLabels = clusterLabels.length;
		
		// set row and column count
		int colLen = heatMapData[0].length;
		tablemodel.setRowCount(heatMapData.length + numOfRowsOccupiedByLabels);
		tablemodel.setColumnCount(colLen + 1);
		
		// fill the cluster names first
		for(int i = 0; i < numOfRowsOccupiedByLabels; i++) {
			tablemodel.setValueAt(clusterRowNames.get(i), i, 0);
		}

		int rowCnt = tablemodel.getRowCount();
		int rowNameIndex = 0;
		//  now fill the row labels which occupy in the first column
		for(int i = numOfRowsOccupiedByLabels; i < rowCnt; i++) {
			tablemodel.setValueAt(rowNames[rowNameIndex++], i, 0);
		}
				
		int colIndex = 1;
		for(String key : clusterColumnLabelsSet) {
			String[] columnLabelClusters = key.split(";");
			// fill the labelColorMap with the column cluster labels
			addLabelsToColorMap(columnLabelClusters);
			for(int colStringIndex : columnClusterMap.get(key)) {
				// Set the cluster labels in cells
				for(int i = 0; i < numOfRowsOccupiedByLabels; i++) {
					tablemodel.setValueAt(columnLabelClusters[i], i, colIndex);
				}
				int mapDataRowIndex = 0;
				// Set the heat map data in the table
				for(int rowIndex = numOfRowsOccupiedByLabels; rowIndex < rowCnt; rowIndex++) {
					tablemodel.setValueAt(heatMapData[mapDataRowIndex++][colStringIndex], rowIndex, colIndex);
				}
				columnIndexColNamesMap[colIndex++] = colStringIndex;
			}
		}
		// Set the cell size in table
		heatMapTable.getColumnModel().getColumn(0).setMaxWidth(50);
		for(int i = 1; i <= colLen; i++) {
			heatMapTable.getColumnModel().getColumn(i).setPreferredWidth(1);
		}
		heatMapTableUpdating = false;
		heatMapTable.repaint();
	}
	
	/**
	 * 
	 * @author sumanth
	 * Class to draw the legend.
	 * Double click on the panel opens up a dialog to modify the ranges and colors
	 */
	private class LegendPanel extends JPanel {
		LegendPanel(){
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// double click
					if(e.getClickCount() == 2) {
						HeatMapRangeColorSelector rangeSelector = 
								new HeatMapRangeColorSelector(valueRangeColorMap);
						valueRangeColorMap = rangeSelector.getRangeColorMap();
						HeatMapPanel.this.repaint();
					}
				}
			});
		}
		
		// Draw the color legend and values below it
		@Override
		protected void paintComponent(Graphics g) {
			int startX = 400;
			int startY = 7;
			int width = 300 / valueRangeColorMap.size();
			int height = 20;
			// Paint/draw the colors first
			for(Map.Entry<Double[], Color> entry : valueRangeColorMap.entrySet()) {
				g.setColor(entry.getValue());
				g.fillRect(startX, startY, width, height);
				startX += width;
			}
			
			// Add the range values below the colors now
			startX = 398;
			int labelY = startY + height + 5;
			Graphics2D g2 = (Graphics2D) g;
			Font font = new Font(null, Font.BOLD, 10);    
			// draw the values in vertical orientation
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(90), 0, 0);
			Font rotatedFont = font.deriveFont(affineTransform);
			g2.setFont(rotatedFont);
			g2.setColor(Color.BLACK);
			for(Map.Entry<Double[], Color> entry : valueRangeColorMap.entrySet()) {
				g2.drawString(String.valueOf(entry.getKey()[0].floatValue()), startX, labelY);
				startX += width;
			}
			g2.drawString(String.valueOf(valueRangeColorMap.lastKey()[1].floatValue()), startX, labelY);
			g2.dispose();
		}
	}
	
	/**
	 * 
	 * @author sumanth
	 * Custom rendering class for the Jtable
	 * Based on the type of object (String, double) It will redirect it to StringRenderer and
	 * CellColorRenderer
	 */
	private class CellRenderer implements TableCellRenderer{
		private final CellColorRenderer cellColorRenderingObj = new CellColorRenderer();
		private final StringRenderer stringRenderingObj = new StringRenderer();
		
		// For the given colLable, get the start and end col index of the cluster
		private Integer[] getClusterRange(JTable table, String colLabel, int row, int col) {
			int currColumn = col;
			int lastCol = col;
			while(currColumn < table.getColumnCount() - 1) {
				String currColumnLabel = (String) table.getValueAt(row, ++currColumn);
				if(currColumnLabel.equals(colLabel))
					lastCol = currColumn;
				else
					break;
			}
			return new Integer[] {col, lastCol};
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if(heatMapTableUpdating) {
				return null;
			}
			if(value instanceof Double) {
				cellColorRenderingObj.setColorValue((double)value);
				return cellColorRenderingObj;
			}
			else {
				// draw the cluster label only in the middle column of cluster
				boolean skipDrawString = false;
				if(column != 0 && clusterLabelRangesMap != null) {
					String columnLabel = (String) value;
					// Fill the clusterLabelRangesMap only when the clusters are updated
					// In other cases use this map to determine the mid index of the cluster
					if(clusterLabelRangesMap.containsKey(columnLabel)) {
						ArrayList<Integer[]> rangesList = clusterLabelRangesMap.get(columnLabel);
						boolean rangeDetected = false;
						for(Integer[] range : rangesList) {
							if(column >= range[0] && column <= range[1]) {
								// get the middle index of the cluster
								int midVal = (range[0] + range[1]) / 2;
								// if current column is not the cluster middle column, skip drawing 
								// the string
								if(column != midVal)
									skipDrawString = true;
								rangeDetected = true;
								break;
							}
						}
						if(!rangeDetected) {
							Integer[] range = getClusterRange(table, columnLabel, row, column);
							rangesList.add(range);
							clusterLabelRangesMap.replace(columnLabel, rangesList);
							int midVal = (range[0] + range[1]) / 2;
							if(column != midVal)
								skipDrawString = true;
						}
					}
					else {
						ArrayList<Integer[]> rangesList = new ArrayList<Integer[]>();
						Integer[] range = getClusterRange(table, columnLabel, row, column);
						rangesList.add(range);
						clusterLabelRangesMap.put(columnLabel, rangesList);
						int midVal = (range[0] + range[1]) / 2;
						if(column != midVal)
							skipDrawString = true;
					}
				}
				stringRenderingObj.setStringLabel((String) value, row, column, skipDrawString);
				return stringRenderingObj;
			}
		}
	}
		
	/**
	 * 
	 * @author sumanth
	 * String renderer to render the strings (Column and row labels)
	 */
	private class StringRenderer extends JComponent{
		private String stringLabel = null; // label to render
		private int rowNum; // row index
		private int colNum; // col index
		private boolean skipDrawString; // whether to render or not
		
		// Constructor
		public void setStringLabel(String label, int rowNum, int colNum, boolean skipDrawString) {
			this.stringLabel = label;
			this.rowNum = rowNum;
			this.colNum = colNum;
			this.skipDrawString = skipDrawString;
		}
		
		// Get the contrast cell color to draw the label 
		private Color getContrastColor(Color color) {
			  double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
			  return y >= 128 ? Color.black : Color.white;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			// Check if clustering is enabled and current row is in the cluster rows limit
			if(colNum != 0 && (rowNum == 0 || 
					(columnClusterMap != null && rowNum < numOfRowsOccupiedByLabels))) {
				Color labelColor = textColor;
				if(labelColorMap.containsKey(stringLabel + ":" + rowNum)) {
					Color cellColor = labelColorMap.get(stringLabel + ":" + rowNum);
					g.setColor(cellColor);
					labelColor = getContrastColor(cellColor);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
				}
				if(!skipDrawString) {
					g2.setColor(labelColor);
					g2.setFont(textFont.deriveFont(Font.BOLD, 10));
					g2.drawString(stringLabel, 1, 10);
				}
			}
			// cluster types
			else if(colNum == 0 && rowNum < numOfRowsOccupiedByLabels) {
				g2.setFont(new Font(null, Font.BOLD, 12));
				g2.drawString(stringLabel, 1, 10);
			}
			else {
				g2.setFont(new Font(null, Font.PLAIN, 10));
				g2.drawString(stringLabel, 1, 10);
			}
			g2.dispose();
		}
	}
		
	/**
	 * 
	 * @author sumanth
	 * Cell render class to render the color based on the value of the cell.
	 */
	private class CellColorRenderer extends JComponent{
		private Color color;

		public void setColorValue(double cellValue) {
			this.color = getColor(cellValue);
		}
		
		// determine the cell color based on the value.
		private Color getColor(double value) {
			for(Entry<Double[], Color> entry : valueRangeColorMap.entrySet()) {
				if(value >= entry.getKey()[0] && value < entry.getKey()[1])
					return entry.getValue();
			}
			return valueRangeColorMap.lastEntry().getValue();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(this.color);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}

	/**
	 * 
	 * @author sumanth
	 * Mouse handler to handle the mouse drag events for column clusters.
	 * This class is responsible for the sorting operation done on the cluster by dragging rows
	 */
	public class MouseHandler implements MouseListener, MouseMotionListener {

	    private Integer row = null;
	    // Using weak reference so that, entire table won't be copied and maintained in the memory
	    private final WeakReference<JTable> table;
	    private final WeakReference<DefaultTableModel> tableModel;

	    // Constructor
	    public MouseHandler(JTable table, DefaultTableModel model) {
	        this.table = new WeakReference<>(table);
	        this.tableModel = new WeakReference<>(model);
	    }

	    @Override
	    public void mouseClicked(MouseEvent event) {}

	    @Override
	    public void mousePressed(MouseEvent event) {
	        JTable table;
	        if((table = this.table.get()) == null) {
	            return;
	        }
	        int viewRowIndex = table.rowAtPoint(event.getPoint());
	        // Check if the mouse press is in the proper location
	        // i.e. Clustering enabled and mouse is pressed only in the clustered label rows 
	        if(!(viewRowIndex == 0 || 
					(columnClusterMap != null && viewRowIndex < numOfRowsOccupiedByLabels)))
	        	return;
	        row = table.convertRowIndexToModel(viewRowIndex);
	    }

	    @Override
	    public void mouseReleased(MouseEvent event) {
	        row = null;
	        JTable table;
	        if((table = this.table.get()) == null) {
	            return;
	        }
	        // Check if the mouse is dragged to another proper row
	        // i.e not into the rows other than cluster label rows.
	        if(columnClusterMap == null)
	        	return;
	        int viewRowIndex = table.rowAtPoint(event.getPoint());
	        if(!(viewRowIndex == 0 || 
					(columnClusterMap != null)))
	        	return;
	        new AnimatedSwingWorker("Sorting by cluster...") {
				
				@Override
				public Object construct() {
			        List<String> samplesToBeClustered = getClusterRowsInArrangedOrder();
			        Map<String, Collection<Integer>> splitIndex = 
			        		MetaOmGraph.getActiveProject().getMetadataHybrid().cluster(samplesToBeClustered, Arrays.asList(colNames));
			        // Update the heatmap based on new clustering
			        clusterColumns(samplesToBeClustered, splitIndex);
					return null;
				}
			}.start();
	    }

	    @Override
	    public void mouseEntered(MouseEvent event) {}

	    @Override
	    public void mouseExited(MouseEvent event) {}

	    // Show the mouse dragging visually
	    @Override
	    public void mouseDragged(MouseEvent event) {
	        JTable table;
	        DefaultTableModel tableModel;
	        if((table = this.table.get()) == null || (tableModel = this.tableModel.get()) == null) {
	            return;
	        }

	        int viewRowIndex = table.rowAtPoint(event.getPoint());
	        if(!(viewRowIndex == 0 || 
					(columnClusterMap != null && viewRowIndex < numOfRowsOccupiedByLabels)))
	        	return;
	        
	        int currentRow = table.convertRowIndexToModel(viewRowIndex);

	        if(row == null || currentRow == row || currentRow < 0) {
	            return;
	        }

	        tableModel.moveRow(row, row, currentRow);
	        row = currentRow;
	        table.setRowSelectionInterval(viewRowIndex, viewRowIndex);        
	    }

	    @Override
	    public void mouseMoved(MouseEvent event) {}
	    
	    // Returns the cluster rows ordered from top to bottom in the table at the time of call to this method
	    // Note that user can drag a cluster row to other position as well.
	    private List<String> getClusterRowsInArrangedOrder(){
	    	List<String> rowsInArrangedOrder = new ArrayList<String>();
	    	DefaultTableModel tableModel = this.tableModel.get();
	    	for(int row = 0; row < numOfRowsOccupiedByLabels; ++row) {
	    		rowsInArrangedOrder.add((String) tableModel.getValueAt(row, 0));
	    	}
	    	
	    	return rowsInArrangedOrder;
	    }

	}

}

