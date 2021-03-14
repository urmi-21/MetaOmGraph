package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.ArrayUtils;
import org.jcolorbrewer.ColorBrewer;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.utils.Utils;

/**
 * 
 * @author sumanth
 *
 */
public class HeatMapPanel extends JPanel{
	
	private double[][] heatMapData;
	private String[] rowNames;
	private String[] colNames;
	private double minValue;
	private double maxValue;
	private double interval;
	private TreeMap<Double, Color> valueRangeColorMap;
	private Map<String, Collection<Integer>> columnClusterMap;
	private Map<String, Color> labelColorMap = new HashMap<String, Color>();
	private Map<String, Collection<Integer>> rowClusterMap;
	private int numOfRowsOccupiedByLabels;
	private List<ColorBrewer> clusterLabelColors;
	private int currColorIndex;
	private JTable heatMapTable;
	private int[] columnIndexColNamesMap;
	private List<String> clusterRowNames;
	private Map<String, ColorBrewer> clusterRowColorMap;
	private int[] clusterLabelsInRowTillNow;
			
			
	public HeatMapPanel(double[][] data, String[] rowNames, String[] colNames) {
		this.heatMapData = data;
		this.rowNames = rowNames;
		this.colNames = colNames;
		clusterRowNames = new ArrayList<String>();
		clusterRowNames.add("Data column");
		valueRangeColorMap = new TreeMap<Double, Color>();
		this.setLayout(new GridLayout(2, 0));
		createTable();
	}
	
	private String createToolTipTextString(String dataColName, double value) {
		DecimalFormat df = new DecimalFormat("####0.0000");
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
		text += "<td><font size=-2>" + Utils.wrapText("Point", 100, "<br>") + "</font></td>";
		text += "<td><font size=-2>" + Utils.wrapText(df.format(value), 100, "<br>")
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
	
	private void createTable() {
		heatMapTable = new JTable(heatMapData.length + 1, heatMapData[0].length + 1) {
			
			public String getToolTipText(MouseEvent e) {
				Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				if(rowIndex == -1 || colIndex == -1 || colIndex == 0)
					return "";


				if(columnClusterMap != null) {
					String dataColName = colNames[columnIndexColNamesMap[colIndex]];
					if(rowIndex < numOfRowsOccupiedByLabels)
						return dataColName;

					double value = (double) this.getValueAt(rowIndex, colIndex);
					return createToolTipTextString(dataColName, value);
				}else {
					String dataColName = colNames[colIndex - 1];
					if (rowIndex == 0)
						return dataColName;

					double value = (double) this.getValueAt(rowIndex, colIndex);
					return createToolTipTextString(dataColName, value);
				}
			}
			
			@Override
			public Point getToolTipLocation(MouseEvent event) {
				Point thisPoint = event.getPoint();
				int rowIndex = rowAtPoint(thisPoint);
				int colIndex = columnAtPoint(thisPoint);
				if(rowIndex == 0 || (columnClusterMap != null && rowIndex < numOfRowsOccupiedByLabels)) {
					return thisPoint;
				}
				int maxWidth = getWidth();
				// define horizontal space between tooltip and point
				int xMargin = 25;

				int y = thisPoint.y;
				int newy = 100;
				/*
				 * select appropriate y if(y-200<=0) { newy=10; }else { newy=y-200; }
				 */
				int x = thisPoint.x;
				// if point is far right of screen show tool tip to the left
				if (maxWidth - x <= 450) {
					return new Point(x - (400 + xMargin), newy);
				}
				return new Point(x + xMargin, newy);
			}
		};
		//heatMapTable.setSize(800, 600);
		int panelHeight = 800;
		int panelWidth = 600;
		int rowLen = heatMapData.length;
		int colLen = heatMapData[0].length;
		
		//heatMapTable.setRowMargin(2);
//		Dimension cellSpacing = new Dimension(1, 1);
//		heatMapTable.setIntercellSpacing(cellSpacing);
		//heatMapTable.setColumnMargin(2);
		heatMapTable.setGridColor(Color.BLACK);
		//heatMapTable.setShowGrid(true);
		
		heatMapTable.setDefaultRenderer(Object.class, new CellRenderer());
				
		minValue = Double.MAX_VALUE;
		maxValue = Double.MIN_VALUE;
		
		heatMapTable.setValueAt("", 0, 0);
		for(int i = 1; i < rowLen + 1; i++) {
			heatMapTable.setValueAt(rowNames[i - 1], i, 0);
		}
		
		int columnLabelMaxSize = Integer.MIN_VALUE;
		for(int i = 1; i < colLen + 1; i++) {
			columnLabelMaxSize = Math.max(colNames[i-1].length(), columnLabelMaxSize);
			heatMapTable.setValueAt(colNames[i - 1], 0, i);
		}
		// set column label row height based on the maximum string length.
		//heatMapTable.setRowHeight(0, columnLabelMaxSize * 8);
		
		for(int i = 0; i < rowLen; i++) {
			for(int j = 0; j < colLen; j++) {
				minValue = Math.min(minValue, heatMapData[i][j]);
				maxValue = Math.max(maxValue, heatMapData[i][j]);
				heatMapTable.setValueAt(heatMapData[i][j], i+1, j+1);
			}
		}
		
		//double midValue = (minValue + maxValue) / 2;
		
		valueRangeColorMap.put(minValue, Color.BLUE);
		valueRangeColorMap.put(maxValue, Color.CYAN);
		interval = calculateInterval();
		fillColorRangesMap();
		//heatMapTable.setPreferredScrollableViewportSize(heatMapTable.getPreferredSize());
		//heatMapTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		Dimension heatmapSize = new Dimension(800, 600);
//		heatMapTable.setPreferredSize(heatmapSize);
		//heatMapTable.getColumnModel().getColumn(1).getWidth();
		for(int i = 1; i <= colLen; i++) {
			heatMapTable.getColumnModel().getColumn(i).setPreferredWidth(1);
		}
	
		setVisible(true);
		//JScrollPane scPane = new JScrollPane();
		//scPane.setViewportView(heatMapTable);
//		GridBagConstraints layoutConstraints = new GridBagConstraints();
//		layoutConstraints.insets = new Insets(0, 0, 0, 0);
//		layoutConstraints.gridx = 0;
//		layoutConstraints.gridy = 0;
		add(heatMapTable);
		//LegendPanel legendPanel = new LegendPanel();
//		layoutConstraints.gridx = 0;
//		layoutConstraints.gridy = 1;
		add(new LegendPanel());
	}
	
	// Fill the colors ranges map based on the interval returned by calculateInterval method.
	private void fillColorRangesMap() {
		for(double i = minValue; i <= maxValue; i += interval) {
			double rangeVal = ((i - minValue) / (maxValue - minValue));
			double fraction = rangeVal % 1;
			if(rangeVal != 0 && rangeVal != 1) {
				Color prevRangeColor = valueRangeColorMap.floorEntry(i).getValue();
				Color nextRangeColor = valueRangeColorMap.ceilingEntry(i).getValue();
				int minR = prevRangeColor.getRed();
				int minG = prevRangeColor.getGreen();
				int minB = prevRangeColor.getBlue();
				int maxR = nextRangeColor.getRed();
				int maxG = nextRangeColor.getGreen();
				int maxB = nextRangeColor.getBlue();
				int currR = (int) (minR + fraction * (maxR - minR));
				int currG = (int) (minG + fraction * (maxG - minG));
				int currB = (int) (minB + fraction * (maxB - minB));
				valueRangeColorMap.put(i, new Color(currR, currG, currB));
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
	
	public void setRangeColors(TreeMap<Double, Color> valueColorRange) {
		this.valueRangeColorMap = valueColorRange;
	}
	
	public void addRangeColor(double rangeStart, Color color) {
		valueRangeColorMap.put(rangeStart, color);
	}
	
	public void setClusterRowColorMap(Map<String, ColorBrewer> clusterRowColorMap) {
		this.clusterRowColorMap = clusterRowColorMap;
		labelColorMap.clear();
		clusterLabelsInRowTillNow = new int[clusterRowNames.size()];
		updateHeatMapTableWithClusters();
	}
	
	public Map<String, ColorBrewer> getClusterRowColorMap(){
		return this.clusterRowColorMap;
	}
		
	public void clusterColumns(List<String> clusterRowNames,
			Map<String, Collection<Integer>> columnClusterMap) {
		clusterLabelsInRowTillNow = new int[clusterRowNames.size()];
		this.clusterRowNames = clusterRowNames;
		this.columnClusterMap = columnClusterMap;
		this.labelColorMap = new HashMap<String, Color>();
		columnIndexColNamesMap = new int[colNames.length + 1];
//		int i = 0;
//		for(String label : columnClusterMap.keySet()) {
//			if(i++ % 2 == 0) {
//				labelColorMap.put(label, Color.YELLOW);
//			}
//			else {
//				labelColorMap.put(label, Color.ORANGE);
//			}
//		}
		currColorIndex = 0;
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
	
	private void updateClusterLabelRowColors() {
		int currColorIndex = 0;
		clusterRowColorMap = new LinkedHashMap<String, ColorBrewer>();
		for(String clusterName : clusterRowNames) {
			ColorBrewer colorBrewer = clusterLabelColors.
					get(currColorIndex++ % clusterLabelColors.size());
			clusterRowColorMap.put(clusterName, colorBrewer);
		}
	}
	
	private void addLabelsToColorMap(String[] columnClusterLabels) {
		for(int rowIndex = 0; rowIndex < columnClusterLabels.length; rowIndex++) {
			if(labelColorMap.containsKey(columnClusterLabels[rowIndex] + ":" + rowIndex))
				continue;
			ColorBrewer cb = clusterRowColorMap.get(clusterRowNames.get(rowIndex));
			Color[] colors = cb.getColorPalette(5);
			ArrayUtils.reverse(colors);
			int colorIndex = clusterLabelsInRowTillNow[rowIndex]++;
			Color color = colors[colorIndex % clusterLabelsInRowTillNow.length];
			labelColorMap.put(columnClusterLabels[rowIndex] + ":" + rowIndex, color);
		}
	}
	
	private void updateHeatMapTableWithClusters() {
		DefaultTableModel tablemodel = (DefaultTableModel) heatMapTable.getModel();
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		
		Set<String> clusterColumnLabelsSet = columnClusterMap.keySet();
		String firstHeader = clusterColumnLabelsSet.stream().findFirst().get();
		
		String[] clusterLabels = firstHeader.split(";");
		numOfRowsOccupiedByLabels = clusterLabels.length;
		
		//int rowLen = heatMapData.length;
		int colLen = heatMapData[0].length;
		tablemodel.setRowCount(heatMapData.length + numOfRowsOccupiedByLabels);
		tablemodel.setColumnCount(colLen + 1);
		
		for(int i = 0; i < numOfRowsOccupiedByLabels; i++) {
			tablemodel.setValueAt(clusterRowNames.get(i), i, 0);
		}

		int rowCnt = tablemodel.getRowCount();
		int rowNameIndex = 0;
		for(int i = numOfRowsOccupiedByLabels; i < rowCnt; i++) {
			tablemodel.setValueAt(rowNames[rowNameIndex++], i, 0);
		}
		
		//int row0LabelMaxSize = Integer.MIN_VALUE;
		//int row1LabelMaxSize = Integer.MIN_VALUE;
		
		int colIndex = 1;
		for(String key : clusterColumnLabelsSet) {
			String[] columnLabelClusters = key.split(";");
			addLabelsToColorMap(columnLabelClusters);
			for(int colStringIndex : columnClusterMap.get(key)) {
				for(int i = 0; i < numOfRowsOccupiedByLabels; i++) {
					tablemodel.setValueAt(columnLabelClusters[i], i, colIndex);
				}
				//row0LabelMaxSize = Math.max(entry.getKey().length(), row0LabelMaxSize);
				//tablemodel.setValueAt(key, 0, colIndex);
				
				//row1LabelMaxSize = Math.max(colNames[colStringIndex].length(), row1LabelMaxSize);
				//tablemodel.setValueAt(colNames[colStringIndex], 1, colIndex);
				int mapDataRowIndex = 0;
				for(int rowIndex = numOfRowsOccupiedByLabels; rowIndex < rowCnt; rowIndex++) {
					tablemodel.setValueAt(heatMapData[mapDataRowIndex++][colStringIndex], rowIndex, colIndex);
				}
				columnIndexColNamesMap[colIndex++] = colStringIndex;
			}
		}
		//heatMapTable.setRowHeight(0, row0LabelMaxSize * 8);
		//heatMapTable.setRowHeight(1, row1LabelMaxSize * 8);
		heatMapTable.repaint();
	}
	
	/**
	 * 
	 * @author sumanth
	 * Class to draw the legend.
	 */
	private class LegendPanel extends JPanel {
		LegendPanel(){
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2) {
						
					}
				}
				
			});
		}
		@Override
		protected void paintComponent(Graphics g) {
			
			int startX = 400;
			int startY = 7;
			int width = 30;
			int height = 20;
			for(Map.Entry<Double, Color> entry : valueRangeColorMap.entrySet()) {
				if(entry.getKey() == valueRangeColorMap.lastKey())
					break;
				g.setColor(entry.getValue());
				g.fillRect(startX, startY, width, height);
				startX += width;
			}
			
			startX = 398;
			int labelY = startY + 30;
			Graphics2D g2 = (Graphics2D) g;
			Font font = new Font(null, Font.BOLD, 10);    
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(90), 0, 0);
			Font rotatedFont = font.deriveFont(affineTransform);
			g2.setFont(rotatedFont);
			g2.setColor(Color.BLACK);
			for(Map.Entry<Double, Color> entry : valueRangeColorMap.entrySet()) {
				if(entry.getKey() == valueRangeColorMap.lastKey())
					break;
				g2.drawString(String.valueOf(entry.getKey()), startX, labelY);
				startX += width;
			}
			g2.drawString(String.valueOf(valueRangeColorMap.lastKey()), startX, labelY);
			g2.dispose();
		}
	}
	
	/**
	 * 
	 * @author sumanth
	 * Custom rendering class for the Jtable
	 */
	private class CellRenderer implements TableCellRenderer{
		private final CellColorRenderer cellColorRenderingObj = new CellColorRenderer();
		private final StringRenderer stringRenderingObj = new StringRenderer();

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if(value instanceof Double) {
				cellColorRenderingObj.setColorValue((double)value);
				return cellColorRenderingObj;
			}
			else {
				stringRenderingObj.setStringLabel((String) value, row, column);
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
		private String stringLabel = null;
		private int rowNum;
		private int colNum;
		
		public void setStringLabel(String label, int rowNum, int colNum) {
			stringLabel = label;
			this.rowNum = rowNum;
			this.colNum = colNum;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Font font = new Font(null, Font.BOLD, 10); 
			if(colNum != 0 && (rowNum == 0 || 
					(columnClusterMap != null && rowNum < numOfRowsOccupiedByLabels))) {   
//				AffineTransform affineTransform = new AffineTransform();
//				affineTransform.rotate(Math.toRadians(90), 0, 0);
//				Font rotatedFont = font.deriveFont(affineTransform);
//				g2.setFont(rotatedFont);
				if(labelColorMap.containsKey(stringLabel + ":" + rowNum)) {
					g.setColor(labelColorMap.get(stringLabel + ":" + rowNum));
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
				}
				g2.setColor(Color.BLACK);
				g2.drawString(stringLabel, 1, 10);
			}
			else {
				g2.setFont(font);
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

		// determine the cell color based on the value.
		public void setColorValue(double cellValue) {
			double key = valueRangeColorMap.floorKey(cellValue);
			this.color = valueRangeColorMap.get(key);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(this.color);
			//g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 4, 4);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}

}

