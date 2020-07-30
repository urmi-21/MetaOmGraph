package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Paint;

import javax.swing.JInternalFrame;
import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;

public class JColorbrewerChooser extends TaskbarInternalFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		JButton btnNewButton_1 = new JButton("New button");
		panel.add(btnNewButton_1);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("New button");
		panel_1.add(btnNewButton);
		
		JPanel panel_2 = new JPanel();
		panel_2.add(dialog);
		getContentPane().add(panel_2, BorderLayout.CENTER);
		
		FrameModel colorBrewerFrameModel = new FrameModel("Color","Color Brewer",8);
		setModel(colorBrewerFrameModel);
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
