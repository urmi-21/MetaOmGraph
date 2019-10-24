package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatterFactory;

import org.jfree.chart.ChartPanel;

import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class ChartActions {

	public static void exportChart(ChartPanel cpanel) {

		File destination = null;
		JFileChooser chooseDialog = new JFileChooser(Utils.getLastDir());
		final String[] fileExtensions = new String[] { "png", "svg" };
		chooseDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooseDialog.setFileFilter(new GraphFileFilter(GraphFileFilter.PNGSVG));
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
		widthField = new JFormattedTextField(new DefaultFormatterFactory(af), new Integer(cpanel.getWidth()));
		heightField = new JFormattedTextField(new DefaultFormatterFactory(af), new Integer(cpanel.getHeight()));
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
		c.gridy = 3;
		sizer.add(new JLabel("Please use extention .svg to save as svg"), c);
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
			// append extention .png by default if svg is not specified
			if (!filename.substring(filename.length() - 4).equals(".svg")) {
				filename += ".png";
				destination = new File(filename);
			}
			if (destination.exists()) {
				int overwrite = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
						filename + " already exists.  Overwrite?", "Overwrite File", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if ((overwrite == JOptionPane.CANCEL_OPTION) || (overwrite == JOptionPane.CLOSED_OPTION))
					return;
				else if (overwrite == JOptionPane.YES_OPTION)
					ready = true;
				else
					destination = null; // No option
			} else
				ready = true;
		}
		final int oldDrawWidth = cpanel.getMaximumDrawWidth();
		final int oldDrawHeight = cpanel.getMaximumDrawHeight();
		final int newWidth = Integer.parseInt(widthField.getText());
		final int newHeight = Integer.parseInt(heightField.getText());
		final File trueDest = new File(destination.getAbsolutePath());
		cpanel.setMaximumDrawWidth(newWidth);
		cpanel.setMaximumDrawHeight(newHeight);
		try {
			// check which extension was provided with file name
			if (trueDest.getName().substring(trueDest.getName().length() - 4).equals(".svg")) {
				ComponentToImage.saveAsSVG(cpanel.getChart(), newWidth, newHeight, trueDest);
			} else {

				ComponentToImage.saveAsPNG(cpanel, trueDest, newWidth, newHeight);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		cpanel.setMaximumDrawWidth(oldDrawWidth);
		cpanel.setMaximumDrawHeight(oldDrawHeight);

	}

}
