package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatterFactory;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class MyChartPanel extends ChartPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// specify chart type
	// 0 boxplot 1 barchart 2 hist 3scatterplot
	public static int BOXPLOT = 0;
	public static int BARCHART = 1;
	public static int HISTOGRAM = 2;
	public static int SCATTERPLOT = 3;
	public static int VOLCANOPLOT = 4;
	private static int chartType;

	public MyChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight,
			int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save,
			boolean print, boolean zoom, boolean tooltips, int ctype) {
		super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer,
				properties, save, print, zoom, tooltips);
		chartType = ctype;
		// TODO Auto-generated constructor stub
	}

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

	//tooltop function for a given type of chart
	@Override
	public String getToolTipText(MouseEvent event) {
		return "";
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
	
	
	
}
