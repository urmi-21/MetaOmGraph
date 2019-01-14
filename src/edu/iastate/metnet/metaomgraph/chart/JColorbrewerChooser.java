package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Paint;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;

public class JColorbrewerChooser extends JInternalFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JColorbrewerChooser frame = new JColorbrewerChooser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JColorbrewerChooser() {
		setBounds(100, 100, 450, 300);

		final ColorPaletteChooserDialog dialog = new ColorPaletteChooserDialog();
		add(dialog);
	}

	/**
	 * get color Palette using jcolorbrewer
	 * 
	 * @param n
	 * @return
	 */
	public Paint[] getpaintArray(int n) {

		/*
		 * final ColorPaletteChooserDialog dialog = new ColorPaletteChooserDialog();
		 * dialog.show(); if(dialog.wasOKPressed()) { Color c = dialog.getColor();
		 * String desc=dialog.getColorPalette().getPaletteDescription();
		 * JOptionPane.showMessageDialog(null, "desc:"+desc); }
		 */

		boolean colorBlindSave = false;
		// ColorBrewer[] sequentialPalettes =
		// ColorBrewer.getSequentialColorPalettes(colorBlindSave);
		ColorBrewer[] sequentialPalettes = ColorBrewer.getQualitativeColorPalettes(colorBlindSave);

		ColorBrewer myBrewer = sequentialPalettes[5];

		System.out.println("Name of this color brewer: " + myBrewer);

		// I want a gradient of 8 colors:
		Color[] myGradient = myBrewer.getColorPalette(5);

		// These are the color codes:
		for (Color color : myGradient) {
			// convert to hex for web display:
			String hex = Integer.toHexString(color.getRGB() & 0xffffff);
			System.out.println("#" + hex + ";");
		}

		return myGradient;

		// return null;
	}

}
