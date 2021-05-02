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
	private int clusterMidCol = -1;
	private String clusterMidColLabel = "";
	private TreeMap<Double[], Color> valueRangeColorMap;
	private Map<String, Collection<Integer>> columnClusterMap;
	private Map<String, Color> labelColorMap = new HashMap<String, Color>();
	private Map<String, Collection<Integer>> rowClusterMap;
	private int numOfRowsOccupiedByLabels;
	private List<ColorBrewer> clusterLabelColors;
	private int currColorIndex;
	private JTable heatMapTable;
	private LegendPanel legendPanel;
	private int[] columnIndexColNamesMap;
	private List<String> clusterRowNames;
	private Map<String, ColorBrewer> clusterRowColorMap;
	private int[] clusterLabelsInRowTillNow;
	private Font textFont;
	private Color textColor;
			
			
	public HeatMapPanel(double[][] data, String[] rowNames, String[] colNames) {
		this.heatMapData = data;
		this.rowNames = rowNames;
		this.colNames = colNames;
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
		text += "<td><font size=-2>" + Utils.wrapText("Value", 100, "<br>") + "</font></td>";
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
			
			@Override
			public boolean isCellEditable(int row, int column) {                
                return false;               
            }
		};
		
		MouseHandler mouseHandler = new MouseHandler(heatMapTable, (DefaultTableModel) heatMapTable.getModel());
		heatMapTable.addMouseListener(mouseHandler);
		heatMapTable.addMouseMotionListener(mouseHandler);

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
		//valueRangeColorMap.put(maxValue, Color.CYAN);
		//interval = calculateInterval();
		fillColorRangesMap();
		//heatMapTable.setPreferredScrollableViewportSize(heatMapTable.getPreferredSize());
		//heatMapTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		Dimension heatmapSize = new Dimension(800, 600);
//		heatMapTable.setPreferredSize(heatmapSize);
		//heatMapTable.getColumnModel().getColumn(0).setWidth(1);
		//heatMapTable.setAutoResizeMode(JTable.);
		heatMapTable.getColumnModel().getColumn(0).setMaxWidth(50);
		for(int i = 1; i <= colLen; i++) {
			heatMapTable.getColumnModel().getColumn(i).setPreferredWidth(1);
		}

		setVisible(true);
		add(heatMapTable);
		this.legendPanel = new LegendPanel();
		add(legendPanel);
		
		
		//JScrollPane scPane = new JScrollPane();
		//scPane.setViewportView(heatMapTable);
//		GridBagConstraints layoutConstraints = new GridBagConstraints();
//		layoutConstraints.insets = new Insets(0, 0, 0, 0);
//		layoutConstraints.gridx = 0;
//		layoutConstraints.gridy = 0;

		//LegendPanel legendPanel = new LegendPanel();
//		layoutConstraints.gridx = 0;
//		layoutConstraints.gridy = 1;
		
		
		//heatMapTable.
//		heatMapTable.addMouseMotionListener(new MouseMotionListener() {
//		    public void mouseDragged(MouseEvent e) {
//		        e.consume();
//		        JComponent c = (JComponent) e.getSource();
//		        TransferHandler handler = c.getTransferHandler();
//		        handler.exportAsDrag(c, e, TransferHandler.MOVE);
//		    }
//
//		    public void mouseMoved(MouseEvent e) {
//		    }
//		});
		
//		heatMapTable.setDragEnabled(true);
//		heatMapTable.setDropMode(DropMode.INSERT_ROWS);
//		heatMapTable.setTransferHandler(new TableRowTransferHandler(heatMapTable));
		
		//heatMapTable.setSize(800, 600);
	}
	
	// Fill the colors ranges map based on the interval returned by calculateInterval method.
	private void fillColorRangesMap() {
		double interval = calculateInterval();
		Color startRangeColor = Color.CYAN;
		Color endRangeColor = Color.BLUE;
		//valueRangeColorMap.put(new Double[] {minValue, minValue + interval}, startRangeColor);
		for(double i = minValue; i <= maxValue; i += interval) {
			double rangeVal = ((i - minValue) / (maxValue - minValue));
			double fraction = rangeVal % 1;
			if(rangeVal != 0 && rangeVal != 1) {
				//Color nextRangeColor = valueRangeColorMap.ceilingEntry(i).getValue();
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
	
	public boolean saveImage(File file, String fileExtension) {
		BufferedImage image = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		paint(g2);
		try{
			return ImageIO.write(image, "PNG", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setLabelColorAndFont(Font textFont, Color textColor) {
		this.textFont = textFont;
		this.textColor = textColor;
		if(columnClusterMap != null)
			heatMapTable.repaint();
	}
	
	public void setRangeColors(TreeMap<Double[], Color> valueColorRange) {
		this.valueRangeColorMap = valueColorRange;
	}
	
	public void addRangeColor(double rangeStart, double rangeEnd, Color color) {
		valueRangeColorMap.put(new Double[] {rangeStart, rangeEnd}, color);
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
		this.clusterMidCol = -1;
		this.clusterMidColLabel = "";
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
		heatMapTable.getColumnModel().getColumn(0).setMaxWidth(50);
		for(int i = 1; i <= colLen; i++) {
			heatMapTable.getColumnModel().getColumn(i).setPreferredWidth(1);
		}
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
						HeatMapRangeColorSelector rangeSelector = 
								new HeatMapRangeColorSelector(valueRangeColorMap);
						valueRangeColorMap = rangeSelector.getRangeColorMap();
						heatMapTable.repaint();
					}
				}
			});
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			int startX = 400;
			int startY = 7;
			int width = 300 / valueRangeColorMap.size();
			int height = 20;
			//double prevKey = 0;
			for(Map.Entry<Double[], Color> entry : valueRangeColorMap.entrySet()) {
//				if(entry.getKey() != 0 && entry.getKey() - prevKey < 1E-4) {
//					prevKey = entry.getKey();
//					continue;
//				}
				g.setColor(entry.getValue());
				g.fillRect(startX, startY, width, height);
				startX += width;
			}
			
			startX = 398;
			int labelY = startY + height + 5;
			Graphics2D g2 = (Graphics2D) g;
			Font font = new Font(null, Font.BOLD, 10);    
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(90), 0, 0);
			Font rotatedFont = font.deriveFont(affineTransform);
			g2.setFont(rotatedFont);
			g2.setColor(Color.BLACK);
			//prevKey = 0;
			for(Map.Entry<Double[], Color> entry : valueRangeColorMap.entrySet()) {
//				if(entry.getKey() != 0 && entry.getKey() - prevKey < 1E-4) {
//					prevKey = entry.getKey();
//					continue;
//				}
//				if(entry.getKey() == valueRangeColorMap.lastKey())
//					break;
				g2.drawString(String.valueOf(entry.getKey()[0].floatValue()), startX, labelY);
				startX += width;
				//prevKey = entry.getKey();
			}
			g2.drawString(String.valueOf(valueRangeColorMap.lastKey()[1].floatValue()), startX, labelY);
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
				boolean skipDrawString = false;
				if(column != 0) {
					String columnLabel = (String) value;
					if(!clusterMidColLabel.equals(columnLabel)) {
						int currColumn = column + 1;
						int columnClusterLen = 0;
						while(currColumn < table.getColumnCount() - 1) {
							String currColumnLabel = (String) table.getValueAt(row, currColumn++);
							if(currColumnLabel.equals(columnLabel))
								columnClusterLen++;
							else
								break;
						}
						clusterMidCol = column + columnClusterLen / 2;
						clusterMidColLabel = columnLabel;
					}

					if(column != clusterMidCol)
						skipDrawString = true;
				}
				stringRenderingObj.setStringLabel((String) value, row, column, skipDrawString);
				return stringRenderingObj;
			}
		}
	}
	
	private static Color getContrastColor(Color color) {
		  double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
		  return y >= 128 ? Color.black : Color.white;
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
		private boolean skipDrawString;
		
		public void setStringLabel(String label, int rowNum, int colNum, boolean skipDrawString) {
			this.stringLabel = label;
			this.rowNum = rowNum;
			this.colNum = colNum;
			this.skipDrawString = skipDrawString;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			if(colNum != 0 && (rowNum == 0 || 
					(columnClusterMap != null && rowNum < numOfRowsOccupiedByLabels))) {
				Color labelColor = textColor;
//				AffineTransform affineTransform = new AffineTransform();
//				affineTransform.rotate(Math.toRadians(90), 0, 0);
//				Font rotatedFont = font.deriveFont(affineTransform);
//				g2.setFont(rotatedFont);
				if(labelColorMap.containsKey(stringLabel + ":" + rowNum)) {
					Color cellColor = labelColorMap.get(stringLabel + ":" + rowNum);
					g.setColor(cellColor);
					labelColor = getContrastColor(cellColor);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
				}
				if(!skipDrawString) {
					g2.setColor(labelColor);
					g2.setFont(textFont);
					g2.drawString(stringLabel, 1, 10);
				}
			}
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
	
	private Color getColor(double value) {
		for(Entry<Double[], Color> entry : valueRangeColorMap.entrySet()) {
			if(value >= entry.getKey()[0] && value < entry.getKey()[1])
				return entry.getValue();
		}
		return valueRangeColorMap.lastEntry().getValue();
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
			//double key = valueRangeColorMap.floorKey(cellValue);
			this.color = getColor(cellValue);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(this.color);
			//g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 4, 4);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}

	/**
	 * 
	 * @author sumanth
	 * Mouse handler to handle the mouse drag events for column clusters.
	 */
	public class MouseHandler implements MouseListener, MouseMotionListener {

	    private Integer row = null;
	    private final WeakReference<JTable> table;
	    private final  WeakReference<DefaultTableModel> tableModel;

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
	        if(columnClusterMap == null)
	        	return;
	        int viewRowIndex = table.rowAtPoint(event.getPoint());
	        if(!(viewRowIndex == 0 || 
					(columnClusterMap != null)))
	        	return;
	        List<String> samplesToBeClustered = getClusterRowsInArrangedOrder();
	        Map<String, Collection<Integer>> splitIndex = 
	        		MetaOmGraph.getActiveProject().getMetadataHybrid().cluster(samplesToBeClustered, Arrays.asList(colNames));
	        clusterColumns(samplesToBeClustered, splitIndex);
	    }

	    @Override
	    public void mouseEntered(MouseEvent event) {}

	    @Override
	    public void mouseExited(MouseEvent event) {}

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
	        //MetaOmGraph.getActiveProject().getMetadataHybrid().cluster(, dataCols);
	    }

	    @Override
	    public void mouseMoved(MouseEvent event) {}
	    
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

