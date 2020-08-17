package edu.iastate.metnet.metaomgraph.chart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultFormatterFactory;

import org.jfree.chart.ChartPanel;

import edu.iastate.metnet.metaomgraph.ComponentToImage;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class ChartActions{
	
	// create the radiobutton panel to select save image as either png or svg file
	private static void createRadioButtonPanel(ChartPanel cpanel) {
		JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setTitle("Save as");
		dialog.setLocationRelativeTo(MetaOmGraph.getDesktop());
		
		JLabel label = new JLabel("Save as:");
		JRadioButton pngButton = new JRadioButton("PNG");
		JRadioButton svgButton = new JRadioButton("SVG");
		
		pngButton.setSelected(true);
		ButtonGroup group = new ButtonGroup();
		group.add(pngButton);
		group.add(svgButton);
		
		JButton okButton = new JButton("Ok");
		JButton cancelButton = new JButton("Cancel");
		
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(10, 10, 10, 10);
		
		dialog.add(label, constraints);
		constraints.gridy = 1;
		dialog.add(pngButton, constraints);
		constraints.gridx = 1;
		dialog.add(svgButton, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		dialog.add(okButton, constraints);
		constraints.gridx = 1;
		dialog.add(cancelButton, constraints);
		
		okButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				if(pngButton.isSelected()) {
					createFileSaveWindow("png", cpanel);
				}
				else if(svgButton.isSelected()) {
					createFileSaveWindow("svg", cpanel);
				}
				dialog.dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();				
			}
		});
		dialog.pack();
		dialog.setVisible(true);
	}

	private static void createFileSaveWindow(String fileType, ChartPanel cpanel) {
	    File destination = null;
	    JFormattedTextField widthField;
		JFormattedTextField heightField;
		JFileChooser chooseDialog = new JFileChooser(Utils.getLastDir());
		chooseDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooseDialog.setFileFilter(new FileNameExtensionFilter(fileType.toUpperCase(), "."+fileType));
		JPanel sizer = new JPanel();
		
		JFormattedTextField.AbstractFormatter af = new JFormattedTextField.AbstractFormatter() {
			@Override
			public Object stringToValue(String text) throws ParseException {
				try {
					return new Integer(text);
				} catch (NumberFormatException nfe) {
					return getFormattedTextField().getValue();
				}
			}

			@Override
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

		chooseDialog.setAccessory(sizer);
		int returnVal = JFileChooser.APPROVE_OPTION;
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
			filename += "." + fileType;
			destination = new File(filename);
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
			if (fileType == "svg") {
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
		
	public static void exportChart(ChartPanel cpanel) {
		createRadioButtonPanel(cpanel);
	}
}
